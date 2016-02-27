package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.CleanGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.ColumnGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.FormatGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.MapList;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.StaticGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.VelocityGetter;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.CSVMetadata;
import mil.army.usace.ehlschlaeger.rgik.util.JTSUtil;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.NullExecutorService;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;
import mil.army.usace.ehlschlaeger.rgik.util.QueuePutPolicy;
import mil.army.usace.ehlschlaeger.rgik.util.TimeTracker;

import org.apache.commons.io.FilenameUtils;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.Boundable;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileReader;



/**
 * Convert a CSV file into a KML file.
 * <UL>
 *   <LI>Overview bitmap is created for distant views.
 *   <LI>Points are divided into tiles to prevent Google Earth from running out of
 *     memory.
 *   <LI>Tiles are given limited view distances to keep Google Earth performant.
 *   <LI>Input data is constructed entirely in RAM, limiting the size of projects
 *     that can be run.
 *   <LI>Pop-up bubbles are created by Apache Velocity using a template file.
 * </UL>
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Csv2Kml {
    protected static Logger log = Logger.getLogger(Csv2Kml.class.getPackage().getName());
    protected static final String VAR_NAME              = "point";
    protected static final String MD_TITLE_NAME         = "csvTitle";
    protected static final String MD_FIELD_NAME         = "csvField";

    /** Whether createKml will use multiple threads. */
    protected static boolean      PARALLEL_SAVE         = true;

    /** How often creatKml will print progress to the screen. */
    protected static float        CREATEKML_REPORT_TIME = 5.0f;                                                  // secs

    /** loadCsv will print progress every this many lines. */
    protected static final int    LOADCSV_REPORT_LINES  = 50000;
    
    // --- Params ---//
    public int                    rasterSize            = 1000000;
    public int                    maxPointsPerTile      = 500;
    /**
     * Minimum pixel area per household before tile becomes visible. This value
     * times number of points in a tile yields the total screen area a tile must
     * occupy before Google Earth will be allowed to display it. Larger values
     * reduce the number of tiles visible on screen, and reduce memory
     * requirements.
     * <P>
     * Zero or less will have us compute a value based on the presence of the
     * bubble template.
     */
    public int                    pixelsPerHoh          = -1;

    // --- Input Specs ---/
    protected File                csvFile;
    protected String              xCol;
    protected String              yCol;

    // --- Output Specs ---//
    protected File                outdir;

    // --- Temps ---//
    /** Translate GIS points to WGS 84. */
    protected ConvertToKml        trans                 = null;
    /** Construct value to use as point name. */
    protected ColumnGetter        nameGetter            = new StaticGetter(null);
    /** Construct value to use as description (bubble). */
    protected ColumnGetter        descGetter            = new StaticGetter(null);
    /**
     * Compute icon and label appearance for a household from its attributes and
     * a template file.
     */
    protected StyleMaker            styler                = null;
    /**
     * Provide table metadata to VelocityGetter.
     */
    protected MapList<String, Map<String, Object>> metadata;    

    // jts.Quadtree prefers large ints. So meters work nicely, but degrees do
    // not. (very slow)
    // jts.STRtree works much better with degrees.
    protected STRtree             tree;
    protected Envelope            tree_bounds;
    protected List<String>        schema;
    /** Manage concurrent operations. */
    protected ExecutorService     exec;
    protected int                 originalTreeSize;

    static {
        KmlWriter.configTrueZip();
    }

    
    /**
     * Default constructor. You must at least call setInput() before go().
     */
    public Csv2Kml() {
    }

    /**
     * Set the projection used by the input files. Can be "-" for no conversion,
     * or an EPSG:#### spec, or path and name of a projection file.
     * 
     * @param crs name or file of coordinate reference system, or "-" to disable
     * 
     * @throws TransformException on any error building conversion objects
     * @throws FactoryException on any error building conversion objects
     * @throws IOException if crs is a file, but it's not valid 
     */
    public void setCRS(String crs) throws TransformException, FactoryException, IOException {
        if (crs == null || "-".equals(crs))
            this.trans = null;
        else {
            File probe = new File(crs);
            if (probe.exists() && probe.isFile())
                this.trans = new ConvertToKml(probe);
            else
                this.trans = new ConvertToKml(crs);
        }
        
    }

    /**
     * Config input file, and set a default output directory.
     * 
     * @param csvFile
     *            path and name of file to convert
     * @param crs
     *            name or file of coordinate reference system. Null or "-" will
     *            disable (use if file is already in WGS84).
     * @param xCol
     *            name of column that provides easting or longitude
     * @param yCol
     *            name of column that provides northing or latitude
     * 
     * @throws TransformException
     *             if transform from source CRS to WGS 84 cannot be built
     * @throws FactoryException
     *             if transform from source CRS to WGS 84 cannot be built
     * @throws IOException
     *             if crs is a file name but it couldn't be accessed
     */
    public void setInput(File csvFile, String xCol, String yCol)
            throws TransformException, FactoryException, IOException {
        this.csvFile = csvFile;
        this.xCol = xCol;
        this.yCol = yCol;

        // Load header line with column names.
        ICsvListReader reader = new CsvListReader(new FileReader(csvFile),
            CsvPreference.STANDARD_PREFERENCE);
        try {
            this.schema = Arrays.asList(reader.getCSVHeader(true));
        }
        finally {
            reader.close();
        }

        String n = FilenameUtils.getBaseName(csvFile.getName());
        setOutput(new File(csvFile.getParentFile(), n+".kmz"));
    }

    /**
     * Set the format string to be used to generate the name of each placemark.
     * Name will be generated from household data. Format string can either be
     * the name of a field, or a string containing "%{name}" markers, where each
     * marker will be replaced with the value from the corresponding field.
     * 
     * @param nameFmt format string for placemark names
     * 
     * @throws Exception on any error building template from nameFmt
     */
    public void setNameTemplate(String nameFmt) throws Exception {
        if (FormatGetter.hasVars(nameFmt))
            nameGetter = new FormatGetter(nameFmt, schema);
// Stick with FormatGetter for now; VG requires a more verbose syntax "$point.householdID".
//            nameGetter = new VelocityGetter(hohSchema, nameFmt, TEMPLATE_HOH_VAR);
        else if (!ObjectUtil.isBlank(nameFmt)) {
            int idx = schema.indexOf(nameFmt);
            if(idx < 0)
                throw new DataException(String.format("Column \"%s\" not found in table.", nameFmt));
            else
                nameGetter = new CleanGetter(idx);
        }
    }

    /**
     * Load bubble template from given file. Must contain a template named
     * "description" that takes a single argument "point". WARNING: setInput()
     * must be called BEFORE this.
     * 
     * @param bubbleTemplateFile
     * @throws Exception
     *             on any error access template file
     */
    public void setBubbleTemplate(File bubbleTemplateFile) throws Exception {
        if(bubbleTemplateFile == null)
            descGetter = new StaticGetter(null);
        else {
            VelocityGetter g = new VelocityGetter(schema, bubbleTemplateFile, VAR_NAME);
            metadata = new MapList<String, Map<String,Object>>();
            g.setScriptVar("metadata", metadata);
            descGetter = g;
        }
    }

    /**
     * Load style template from given file.
     * 
     * @param styleFile
     *            path and name of file to be use to adjust placemark styles
     *            
     * @throws Exception
     *             on any error accessing template file
     */
    private void setStyleTemplate(File styleFile) throws Exception {
        this.styler = new StyleMaker(schema, styleFile, VAR_NAME);
    }

    /**
     * Set the output directory or file. Will not be created until we start
     * generating files. If name ends with ".zip" or ".kmz", a compressed zip
     * file will be created. (Google Earth uses *.kmz, but the file is just a
     * zip file.)  Otherwise, a directory will be created.
     * 
     * @param outdir
     *            dir or zip file to receive all output
     * 
     * @throws IllegalArgumentException
     *             if file currently exists
     */
    public void setOutput(File outdir) {
        this.outdir = outdir;
    }

    /**
     * Perform task as currently configured. You must at least call setInput()
     * before go().
     * 
     * @throws NumberFormatException
     * @throws IOException
     * @throws TransformException
     * @throws FactoryException
     */
    public void go() throws NumberFormatException, IOException, TransformException,
            FactoryException {
        // Test outdir here, not in setOutput. setInput calls setOutput, but we
        // don't want to crash there in case user calls setOutput with a
        // different path.
        if (outdir.exists() && !outdir.isDirectory())
            throw new IllegalArgumentException("Output \"" + outdir
                    + "\" exists as a non-zip file, and cannot be overwritten.");
        
        TimeTracker.start(getClass().getName());

        loadCSV();
        TimeTracker.finished("Input");

        createKml(FilenameUtils.getBaseName(csvFile.getName()));
        TimeTracker.finished("Output");
        TimeTracker.total();
    }

    /**
     * Load, parse, and transform input file.
     * 
     * @throws IOException
     * @throws NumberFormatException
     * @throws TransformException
     */
    protected void loadCSV() throws IOException {
        LogUtil.progress(log, "Loading and indexing "+csvFile.getName());
        
        ICsvListReader reader = new CsvListReader(new FileReader(csvFile),
            CsvPreference.STANDARD_PREFERENCE);

        try {
            // Eat header line.
            List<String> h = Arrays.asList(reader.getCSVHeader(true));
            for(int c=0; c<h.size(); c++)
                h.set(c, h.get(c).trim());

            // Detect and save ##keyword metadata.
            CSVMetadata metaScanner = new CSVMetadata(metadata);
            if(metadata != null) {
                metaScanner.addNames(h, MD_TITLE_NAME);
                metaScanner.addNames(VelocityGetter.makeSafeID(h), MD_FIELD_NAME);
            }
            
            int iEasting = schema.indexOf(xCol);
            int iNorthing = schema.indexOf(yCol);

            tree = new STRtree();
            tree_bounds = new Envelope();

            // Load all fields from all rows.
            List<String> line;
            while ((line = reader.read()) != null) {
                // Parse and store metadata.
                if(metaScanner.readRow(line))
                    continue;
                
                // Skip rows that have no coordinates.
                // BOTH must be blank; one blank is an error.
                String sx = line.get(iEasting);
                String sy = line.get(iNorthing);
                if(ObjectUtil.isBlank(sx) && ObjectUtil.isBlank(sy))
                    continue;
                
                double x = Double.parseDouble(sx);
                double y = Double.parseDouble(sy);

                if(trans != null) {
                    DirectPosition kmlXY = trans.toKml(x, y);
                    x = trans.getKmlX(kmlXY);
                    y = trans.getKmlY(kmlXY);
                }

                String name = nameGetter.get(line);
                String desc = descGetter.get(line);

                Point p;
                if(styler == null) {
                    // Minimize memory consumption.
                    p = Point.create(x, y, name, desc);
                }
                else {
                    // Maximize feature support.
                    p = Point.createFull(x, y, name, desc);

                    styler.setScriptVar("name", name);
                    styler.setScriptVar("desc", desc);
                    styler.get(line);
                    
                    p.setName((String) styler.getScriptVar("name"));
                    p.setDesc((String) styler.getScriptVar("desc"));
                    p.setIconStyle(styler.getCurrentIconStyle());
                    p.setNameStyle(styler.getCurrentLabelStyle());
                }

                // Insert point into spatial index.
                tree.insert(p.getBounds(), p);
                tree_bounds.expandToInclude(x, y);

                if (reader.getLineNumber() % LOADCSV_REPORT_LINES == 0)
                    LogUtil.progress(log, "  Line " + reader.getLineNumber());
            }
        }
        catch(Exception e) {
            // Flatten exception stack
            String msg = String.format("Error reading line %d from file %s\n  due to %s:",
                reader.getLineNumber(), csvFile, ObjectUtil.getMessage(e));
            IOException e2 = new IOException(msg);
            e2.setStackTrace(e.getStackTrace());
            throw e2;
        }
        finally {
            reader.close();
        }
        
        this.originalTreeSize = tree.size();
    }

    /**
     * Create all output files.
     * 
     * @param name
     *            main file "doc.kml" will receive this as it's internal
     *            document name
     * 
     * @throws TransformException
     * @throws FactoryException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected void createKml(String name) throws TransformException, FactoryException, IOException {
        if(PARALLEL_SAVE)
            // Use exactly one thread per core, and no job queue. TrueZip is a
            // major bottleneck, limiting CPU use to below 50%.
            exec = QueuePutPolicy.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        else
            exec = new NullExecutorService();
        
        File tiledir = new File(outdir, "tiles");
        File imagedir = new File(outdir, "rasters");

        outdir.mkdirs();
        imagedir.mkdirs();
        tiledir.mkdirs();

        log.info("Outputs will be saved to "+outdir.getAbsolutePath());
        
        // Wipe the old file; we don't want to merge new contents with old.
        outdir.deleteAll();
        
        KmlWriter masterKml = new KmlWriter(name, outdir);
        masterKml.writeStyle("tiles/sty.kml");

        // Google Earth is not smart about memory, and will load tiles til it
        // chokes and hangs. So we need to limit the number of points visible on
        // screen at once. Pixels-per-household is the best way to do that, but
        // the only way to find the best number is trial-and-error.
        if(pixelsPerHoh > 0) {
            masterKml.setPixelsPerPoint(pixelsPerHoh);
        }
        else {
            if(descGetter != null) {
                // bubbles are large
                masterKml.setPixelsPerPoint(250);
            }
            else
                // no bubble means we can display lots on screen at once.
                masterKml.setPixelsPerPoint(50);
        }

        // Write overview raster for distant viewing.
        //  - tree.query(bounds) is used cuz STRtree doesn't have a queryAll
        //  - our JTSUtil.query is not needed cuz we want all points
        LogUtil.progress(log, "Building raster overview ...");
        masterKml.writeRaster("rasters/overview.png", tree_bounds, tree.query(tree_bounds),
            rasterSize, Color.RED);
        LogUtil.progress(log, "  Done.");

        // 7 out of 10 dentists prefer square tiles.
        Envelope bounds = new Envelope(tree_bounds);
        JTSUtil.square(bounds);
        // Add safety margin.
        bounds.expandBy(1.0);

        // Write out all the tiles.
        try {
            Tile<Boundable> root_tile = new FastQuadTree(maxPointsPerTile).createTiles(tree, tree_bounds);
            // Evil hack! I know root_tile contains Points, but the above line
            // does not, and Java won't allow a cast from Tile<Boundable> to
            // Tile<Point> because they don't want to have to walk the list to
            // verify that every element is indeed a Point. This dodges that
            // whole mess by abusing the way Java implements generics:
            Tile<Point> hack = (Tile<Point>)(Tile<?>)root_tile;
            createKml(hack, masterKml, "1");
        }
        finally {
            // Ensure Java exits cleanly even if error.
            exec.shutdown();
        }
        
        try {
            // Wait for all the tiles to finish.
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Trouble while waiting for output", e);
        }

        // Write the master document.
        masterKml.save();

        if (tree.size() > 0)
            System.err.format("Not all points were written!  %d points remain:\n  %s",
                tree.size(), tree);
    }

    /**
     * Create KML file from a collection of tiles. Each tile will be written as
     * a seperate .kml file into the output folder. The file name will be
     * representative of the tile's location in the original tile tree.
     * <P>
     * This method uses multi-threading to build the tiles, and can use as many
     * cores as are available, although TrueZip greatly interferes with that.
     * <P>
     * NOTE: tile.contents is nulled as each tile processed to be nice to user.
     * 
     * @param tile
     *            tree of tiles to write out.  All objects must extend csv2kml.Point.
     * @param masterKml
     *            helper for creating output files
     * @param tileName
     *            base name of file if written, or prefix of sub-tiles
     */
    protected void createKml(final Tile<? extends Point> tile, final KmlWriter masterKml, final String tileName) {
        if(tile == null)
            return;

        // Process point in this node, if any.
        if(tile.contents != null && tile.contents.size() > 0) {
            exec.submit(new Runnable() {
                public void run() {
                    try {
                        // something wrong with java .. can't cast contents to (Collection<Point>)
                        masterKml.writeTile("tiles/" + tileName + ".kml", "tile-" + tileName,
                            tile.bounds, tile.contents);
                        printProgress(tileName);
                        
                        // CONSERVE MEMORY
                        // This app is extremely wasteful with memory, so we'll
                        // be nice and release it back to the user as we go.
                        // -> In practice, this doesn't seem to work .. the
                        // decreased memory pressure doesn't allow Java to run
                        // any faster, and Java isn't releasing the memory back
                        // to the user.  But I'm going to leave this in anyway,
                        // and hope.
                        tile.contents = null;
                    }
                    catch (Exception e) {
                        // Can't do much here; throwing won't stop the program.
                        log.severe(e.getMessage());
                    }
                }
            });
        }

        // Process child nodes.
        createKml(tile.nw, masterKml, tileName+"1");
        createKml(tile.ne, masterKml, tileName+"2");
        createKml(tile.sw, masterKml, tileName+"3");
        createKml(tile.se, masterKml, tileName+"4");
    }

    
    
    private long p_outputStarted = -1;
    private long p_nextReport;
    private int p_lastPercent;


    /**
     * Display the progress of createKml in friendly terms.
     */
    private synchronized void printProgress(String tileName) {
        if(p_outputStarted < 0) {
            p_outputStarted = System.currentTimeMillis();
            p_nextReport = p_outputStarted + (long)(CREATEKML_REPORT_TIME*1000);
            LogUtil.progress(log, "Starting tile construction");
        }
        else {
            long now = System.currentTimeMillis();
            // Report only once every N seconds.
            if(now > p_nextReport) {
                p_nextReport += (long)(CREATEKML_REPORT_TIME*1000);

                if(p_lastPercent >= 100) {
                    // We reported 100% last time, but we're still rolling.
                    LogUtil.progress(log, "  Finishing output tasks ...");
                    p_nextReport = Long.MAX_VALUE;
                }
                else {
                    // Compute progress based on tile name.
                    float val = 100;
                    float sum = 0;
                    for(int i = 0; i<tileName.length(); i++) {
                        // If there's another char after, then we haven't completed this quadrant.
                        if(i+1 < tileName.length())
                            sum += val*(tileName.charAt(i)-'1');
                        else
                            sum += val*(tileName.charAt(i)-'0');
                        val /= 4;
                    }
                    
                    // Don't report if no progress made.
                    if(sum > p_lastPercent) {
                        p_lastPercent = (int) Math.ceil(sum);
                        LogUtil.progress(log, "  %3d%% of tiles complete\n", p_lastPercent);
                    }
                }
            }
        }
    }
    
    
    
    /**
     * Create a simple Apache Velocity template for the the user to start with.
     * 
     * @param csv
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void generateTemplate(File csv) throws FileNotFoundException, IOException {
        ICsvListReader reader = new CsvListReader(new FileReader(csv),
            CsvPreference.STANDARD_PREFERENCE);

        try {
            // Load header line with column names.
            List<String> head = Arrays.asList(reader.getCSVHeader(true));
            System.out.println("<table border=\"1\">");
            for (int i = 0; i < head.size(); i++) {
                String name = head.get(i);
                String field;
                
                if(ObjectUtil.isBlank(name)) {
                    name = String.format("(Column %d)", i);
                    field = String.format("$%s[%d]", VAR_NAME, i);
                }
                else {
                    field = String.format("$%s.%s", VAR_NAME, VelocityGetter.makeSafeID(name));
                }
                System.out.format("  <tr><td>%s</td><td>%s</td></tr>\n", name, field);
            }
            System.out.println("</table>");
        }
        finally {
            reader.close();
        }
    }

    /**
     * Test the name and bubble templates with a random selectiion of
     * households. Prints simple HTML to stdout containing generated names and
     * bubbles.
     * 
     * @throws IOException
     *             on any file error
     */
    public void testTemplate() throws IOException {
        int numTestRecs = 10;
        
        ICsvListReader reader = new CsvListReader(new FileReader(csvFile),
            CsvPreference.STANDARD_PREFERENCE);

        try {
            List<String> h = Arrays.asList(reader.getCSVHeader(true));
            for(int c=0; c<h.size(); c++)
                h.set(c, h.get(c).trim());

            // Detect and save ##keyword metadata.
            CSVMetadata metaScanner = new CSVMetadata(metadata);
            if(metadata != null) {
                metaScanner.addNames(h, MD_TITLE_NAME);
                metaScanner.addNames(VelocityGetter.makeSafeID(h), MD_FIELD_NAME);
            }
            
            System.out.println("<HTML><BODY>");
            
            // Load all fields from some rows.
            List<String> line;
            while ((line = reader.read()) != null) {
                // Parse and store metadata.
                if(metaScanner.readRow(line))
                    continue;
                
                String name = nameGetter.get(line);
                String desc = descGetter.get(line);
                
                // Test mode:  Print first 10 records.
                if(name != null)
                    System.out.format("<H3>%s</H3>\n", name);
                
                if(desc != null) {
                    System.out.println(desc);
                    System.out.println("<BR/><HR/>\n");
                }
                
                numTestRecs--;
                if(numTestRecs <= 0)
                    break;
            }
            
            System.out.println("</BODY></HTML>");
        }
        catch(Exception e) {
            // Flatten exception stack
            String msg = String.format("Error reading line %d from file %s\n  due to %s:",
                reader.getLineNumber(), csvFile, ObjectUtil.getMessage(e));
            IOException e2 = new IOException(msg);
            e2.setStackTrace(e.getStackTrace());
            throw e2;
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * Helper to remove multiple objects from a SpatialIndex.
     * 
     * @param tree a JTS spatial index
     * @param points any collection of csv2kml.Point objects
     */
    protected static void removeAll(SpatialIndex tree, Collection<?> points) {
        for (Object object : points) {
            Point node = (Point) object;
            tree.remove(node.getBounds(), node);
        }
    }

    public static void main(String[] args) throws Exception {
        Csv2Kml kg = new Csv2Kml();

        OptionParser parser = new OptionParser();
        
        parser.accepts("gentemplate", "create default bubble template and exit");
        parser.accepts("testtemplate", "generate HTML from the first 10 records then exit");

        parser.accepts("bubble", "create bubbles using this template file (default: no bubbles)")
            .withRequiredArg().ofType(File.class);
        parser.accepts("style", "adjust placemark styles using this template file")
            .withRequiredArg().ofType(File.class);
        parser.accepts("pix", "minimum pixels per household for visibility; tiles with less will not be displayed")
            .withRequiredArg().ofType(Integer.class);
        parser.accepts("crs", "coord ref system used in file (default: no conversion)")
            .withRequiredArg().ofType(String.class);
        parser.accepts("name", "format string for name of each point (default: no name)")
            .withRequiredArg().ofType(String.class);
        parser.accepts("output", "dir or file name to receive output (default: household file name, with .kmz extension)")
            .withRequiredArg().ofType(File.class);
        
        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");

        OptionSet opts = parser.parse(args);
        int reqdArgs;
        if (opts.has("gentemplate"))
            reqdArgs = 1;
        else
            reqdArgs = 3;

        if(opts.has("h") || opts.has("help") || args.length == 0) {
            System.err.println("Usage: " + kg.getClass().getSimpleName()
                    + " [options] <file> <x> <y>");
            System.err.println("  <file> is csv file to convert.");
            System.err.println("  <x> is name of col containing horizontal pos of each point");
            System.err.println("  <y> is name of col containing vertical pos of each point");
            System.err.println();
            parser.printHelpOn(System.err);
            System.exit(5);
        }

        if(opts.nonOptionArguments().size() != reqdArgs) {
            System.err.println("Required args are missing; there should be "+reqdArgs+".");
            System.exit(5);
        }
        
        LogUtil.getRootLogger().setLevel(Level.INFO);
        LogUtil.cleanFormat();
        
        File csv = new File(opts.nonOptionArguments().get(0));

        if (opts.has("gentemplate")) {
            generateTemplate(csv);
        } else {
            String xCol = opts.nonOptionArguments().get(1).trim();
            String yCol = opts.nonOptionArguments().get(2).trim();
            
            kg.setInput(csv, xCol, yCol);

            if(opts.has("crs"))
                kg.setCRS((String) opts.valueOf("crs"));
            if(opts.has("name"))
                kg.setNameTemplate((String) opts.valueOf("name"));
            if (opts.has("bubble"))
                kg.setBubbleTemplate((File) opts.valueOf("bubble"));
            if (opts.has("style"))
                kg.setStyleTemplate((File) opts.valueOf("style"));
            if (opts.has("pix"))
                kg.pixelsPerHoh = (Integer) opts.valueOf("pix");
            if(opts.has("output"))
                kg.setOutput((File) opts.valueOf("output"));
            
            if (opts.has("testtemplate")) {
                // Generate html by applying template to first 10 rows of csv
                kg.testTemplate();
            }
            else {
                kg.go();
            }
        }
    }
}
