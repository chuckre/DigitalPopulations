package mil.army.usace.ehlschlaeger.digitalpopulations.tabletools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.generic.ConversionTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.MathTool;



/**
 * Compute a value from a template using Apache Velocity. Row will be accessible
 * through a variable that has the given name and acts like a list, but with an
 * extra get(key) method. Thus these will all be valid ways to access a field:
 * <code>
 *   point.get(5)
 *   point.get("quantity")
 *   point.quantity
 * </code>
 * <P>
 * NOTE: Velocity adheres to Java's security and visibility policies. So if an
 * object is added to a context whose class is NOT public, then methods in that
 * class will NOT be accessible from templates. But if the method appears in a
 * public ancestor class or interface, then the method WILL be accessible.
 * 
 * @author William R. Zwicky
 */
public class VelocityGetter implements ColumnGetter {
    /** Original list of column names passed into constructor. */
    protected List<String> origSchema;
    /** List of column names, made safe for Velocity. */
    protected List<String> safeSchema;

    /** Our custom-configured engine. */
    protected VelocityEngine engine;
    /** The template, loaded from the given file. */
    protected Template template;
    /** Container for variables the template can access. */
    protected VelocityContext context;
    /** Name through which the template will access point attributes. */
    protected String varName;
    /** Adds get(name) to a List<String>. */
    protected MapList<String, String> rowWrapper;
    
    /** "Var 2" name for use by template. */
    protected String var2Name;
    /** "Var 2" field names. */
    protected List<String> origSchema2;
    protected List<String> safeSchema2;
    /** "Var 2" content wrapper. */
    protected List<MapList<String, String>> membersList;
    protected MapList<String,String> memberMaster;

    
    
    /**
     * Construct new getter.
     * 
     * @param schema
     *            names for every column to be accessible
     * @param template
     *            content of template
     * @param varName
     *            name that template will use to access values
     * 
     * @throws Exception
     *             on any error building template
     */
    public VelocityGetter(List<String> schema, String template, String varName) throws Exception {
        this.origSchema = schema;
        this.safeSchema = makeSafeID(schema);
        
        this.engine = newVelocity(new File(".").getCanonicalFile());
        this.template = createTemplate(template);
        
        this.varName = varName;
        this.rowWrapper = new MapList<String, String>(safeSchema);
        rowWrapper.addAliases(origSchema);
        
        context = new VelocityContext();
        context.put(varName, rowWrapper);
    }

    /**
     * Construct new getter.
     * 
     * @param schema
     *            names for every column to be accessible
     * @param templateFile
     *            path and name of file which contains template contents. If
     *            file contains references to other templates, they will be
     *            loaded from the same dir.
     * @param varName
     *            name that template will use to access values
     * 
     * @throws Exception
     *             on any error loading or building template
     */
    public VelocityGetter(List<String> schema, File templateFile, String varName) throws Exception {
        if(!templateFile.exists())
            throw new FileNotFoundException(templateFile.toString());
        
        this.origSchema = schema;
        this.safeSchema = makeSafeID(schema);
        
        this.engine = newVelocity(templateFile.getAbsoluteFile().getParentFile());
        this.template = engine.getTemplate(templateFile.getName());
        
        this.varName = varName;
        this.rowWrapper = new MapList<String, String>(safeSchema);
        rowWrapper.addAliases(origSchema);
        
        context = new VelocityContext();
        context.put("ConversionTool", new ConversionTool() {
            //
            // DEV NOTE:  Original toDouble throws NullPointerException on any problem,
            // which isn't helpful.  This override is more informative.
            //
            @Override
            public Double toDouble(Object value) {
                return new Double((String)value);
            }
        });
        context.put("DisplayTool", new DisplayTool());
        context.put("MathTool", new MathTool());
        context.put(varName, rowWrapper);
    }

    /**
     * Provide a custom object to template script.
     * 
     * @param name
     *            name that script will use to access this object
     * @param value
     *            object that will be accessed by 'name'
     */
    public void setScriptVar(String name, Object value) {
        context.put(name, value);
    }

    /**
     * Retrieve an object from script environment. Can be use to fetch modified
     * data from a script.
     * 
     * @param name
     *            name that script used to access this object
     * @return current value of object.
     */
    public Object getScriptVar(String name) {
        return context.get(name);
    }
    
    /**
     * Add a var to the context that will contain a table of "members" of the
     * next record to be passed in to get().
     * 
     * @param var2Name
     *            name of variable to expose to template
     * @param schema
     *            names of columns in table
     */
    public void addMembersVar(String var2Name, List<String> schema2) {
        this.var2Name = var2Name;
        this.origSchema2 = schema2;
        this.safeSchema2 = makeSafeID(schema2);
        this.membersList = new ArrayList<MapList<String,String>>();
        
        context.put(var2Name, membersList);
        memberMaster = new MapList<String, String>(safeSchema2);
        memberMaster.addAliases(origSchema2);
    }
    
    /**
     * Set values for the "members" of the next record to be passed in to get().
     * This will NOT be cleared between calls; use setMembersValue(null) to clear it.
     * 
     * @param table list of records, one per "member"
     */
    public void setMembersValue(List<List<String>> table) {
        if(membersList != null) {
            membersList.clear();
            if(table != null) {
                for(List<String> row : table) {
                    membersList.add(memberMaster.shallowCopy(row));
                }
            }
        }
    }
    
    public String get(String[] row) {
        rowWrapper.setValues(Arrays.asList(row));
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        return sw.toString();
    }

    public String get(List<String> row) {
        rowWrapper.setValues(row);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        return sw.toString();
    }

    public String get(CSVTable table, int row) {
        rowWrapper.setValues(table.getRow(row));
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        return sw.toString();
    }



    /**
     * Create Velocity engine that supports loading from absolute file paths.
     * Velocity normally requires a template directory, and will auto-find
     * templates within it. We don't work that way; we're given an absolute path,
     * and load that one file only.
     * 
     * @return new engine
     * 
     * @throws Exception on any error building engine
     * 
     * @see <a
     *      href="http://softwaredevscott.spaces.live.com/blog/cns!1A9E939F7373F3B7!639.entry"
     *      >Loading Velocity Templates from Arbitrary Absolute Paths</a>
     */
    public static VelocityEngine newVelocity() throws Exception {
        Properties props = new Properties();
        props.put("resource.loader", "file");
        props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        props.put("file.resource.loader.path", "");  // !!!
        props.put("file.resource.loader.cache", "true");
        
        props.put("runtime.references.strict", "true");
        
        VelocityEngine ve = new VelocityEngine();
        ve.init(props);
        return ve;
    }

    /**
     * Init Velocity to support auto-loading from a directory.
     *
     * @param dir directory from which templates will be loaded
     * @return new engine
     * @throws Exception on any error building engine
     */
    public static VelocityEngine newVelocity(File dir) throws Exception {
        Properties props = new Properties();
        props.put("resource.loader", "file");
        props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        props.put("file.resource.loader.path", dir.getAbsolutePath());
        props.put("file.resource.loader.cache", "true");
        
        props.put("runtime.references.strict", "true");

        VelocityEngine ve = new VelocityEngine();
        ve.init(props);
        return ve;
    }

    /**
     * Create a velocity template from a plain string.
     * 
     * @param template content to convert into template
     * @return new Velocity template
     * 
     * @throws ParseException if bad content
     */
    public static Template createTemplate(String template) throws ParseException {
        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();            
        SimpleNode node = runtimeServices.parse(template, String.format("Anonymous Template %d",template.hashCode()));
        
        Template t = new Template();
        t.setRuntimeServices(runtimeServices);
        t.setData(node);
        t.initDocument();
        
        return t;
    }

    /**
     * Convert column name into an identifier that is safe for use in a Velocity
     * template. For the sake of simplicity, this method imposes more
     * restrictions than Velocity:
     * <UL>
     * <LI>Only letters, numbers, and underscore are allowed.
     * <LI>All illegal characters (spaces and punctuation) are converted to
     * underscores.
     * <LI>Consecutive underscores are collapsed to a single underscore.
     * </UL>
     * <P>
     * For example, $data$_$column$ would become _data_column_.
     *    
     * @param name
     * @return
     */
    public static String makeSafeID(String name) {
        return name.trim().replaceAll("[^A-Za-z0-9]+", "_");
    }
    
    /**
     * Apply makeSafeID to every member of a list.
     * 
     * @param schema list of names
     * @return list of cleaned names
     */
    public static List<String> makeSafeID(List<String> schema) {
        ArrayList<String> newSchema = new ArrayList<String>();
        for(String name : schema)
            newSchema.add(makeSafeID(name));
        return newSchema;
    }
}
