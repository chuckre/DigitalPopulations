package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.CleanGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.ColumnGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.FormatGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.VelocityGetter;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
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
import com.vividsolutions.jts.index.strtree.Boundable;
import com.vividsolutions.jts.index.strtree.STRtree;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileReader;



/**
 * Render the outputs of ConflatePumsQueryWithTracts as a KML file. Like
 * Csv2Kml, but only works with these files.
 * <P>
 * Note setHohFile (and setPopFile, if used) MUST be called before any other
 * sets. These methods examine the input files, and make the schema available to
 * other methods during setup.
 * <P>
 * The amount of input data can be significant, consequently this app copies the
 * input files into an SQL database before proceeding. Database will take 3x the
 * space of the input files, and operation will proceed much more slowly than an
 * all-memory version, but we should be able to handle much larger datasets
 * (around 5 million households per GB of RAM, regardless of number of attributes).
 * <P>
 * Our limit comes from the spatial index, which is held entirely in memory.
 * Amount of memory required depends only on the number of households being
 * processed; width of household table (number of fields) and size of population
 * table have no effect.
 * 
 * @author William R. Zwicky
 */
public class Dp2Kml {
    public static final String TEMPLATE_HOH_VAR = "point";
    public static final String TEMPLATE_POP_VAR = "members";
    public static final String DBNAME           = "DP2KML_WORK";

    protected Logger           log              = Logger.getLogger(Dp2Kml.class.getPackage().getName());

    public boolean             parallelSave     = true;
    /** Total pixel area for overview raster. */
    public int                 rasterSize       = 1000000;
    /** Maximum households per tile. Smaller tiles load faster. */
    public int                 maxPointsPerTile = 500;

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
    public int                 pixelsPerHoh     = -1;

    /** Path and name of households file. */
    private File               hohFile;
    /** Name of households table in database. */
    private String             hohTbl;

    /** Path and name of population file. */
    private File               popFile          = null;
    /** Name of population table in database. */
    private String             popTbl           = null;
    
    /** Column names from households table. */
    private List<String>       hohSchema;
    /** Column names from population table. */
    private List<String>       popSchema        = null;
    
    /** Parameterized SQL to fetch a row from hoh table. */
    private PreparedStatement  getHoh_stmt;
    /** Parameterized SQL to fetch a row from pop table. */
    private PreparedStatement  getPop_stmt;

    /** Helper to translate coordinate systems. */
    private ConvertToKml       trans            = null;
    /** Dir or zip file to receive results. */
    private File               outdir;
    /** Compute placemark name from one row of households table. */
    private ColumnGetter       nameGetter;
    /**
     * Compute description bubble for a household from its attributes and a
     * template file.
     */
    private VelocityGetter     descGetter       = null;
    /**
     * Compute icon and label appearance for a household from its attributes and
     * a template file.
     */
    private StyleMaker         styler           = null;
    
    /** Multi-thread manager. */
    private ExecutorService    exec;
    /** Connection to database. */
    private H2Wrapper          db;
    
    static {
        KmlWriter.configTrueZip();
    }

    
    /**
     * Construct blank instance.
     */
    public Dp2Kml() {
    }

    /**
     * Register the input households file.  MUST be called before other set methods will work.
     * Must be a file generated by Digital Populations.
     * 
     * @param csvFile file that provides households.  Each one will become a KML placemark.
     * 
     * @throws TransformException
     * @throws IOException on any error reading CSV file
     */
    public void setHohFile(File csvFile)
            throws TransformException, IOException {
        this.hohFile = csvFile;
        this.hohTbl  = VelocityGetter.makeSafeID(hohFile.getName());
        this.hohSchema = CSVTable.loadSchema(hohFile);

        // Set default output path.
        String n = FilenameUtils.getBaseName(csvFile.getName());
        setOutput(new File(csvFile.getParentFile(), n + ".kmz"));

        // Set default name.  Cryptic, but minimizes memory use.
        nameGetter = new FormatGetter("%{uid}", hohSchema);
    }

    /**
     * Register the input population file.  This provides members for each of the households.
     * This file will only be used if a bubble template is provided.  Note this method must
     * be called BEFORE setBubbleTemplate is called.
     * 
     * @param csvFile file that provides members of the households
     * 
     * @throws IOException on any error reading CSV file
     */
    private void setPopFile(File csvFile) throws IOException {
        this.popFile = csvFile;
        this.popTbl  = VelocityGetter.makeSafeID(popFile.getName());
        this.popSchema = CSVTable.loadSchema(popFile);
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
            nameGetter = new FormatGetter(nameFmt, hohSchema);
// Stick with FormatGetter for now; VG requires a more verbose syntax "$point.householdID".
//            nameGetter = new VelocityGetter(hohSchema, nameFmt, TEMPLATE_HOH_VAR);
        else if (!ObjectUtil.isBlank(nameFmt)) {
            int idx = hohSchema.indexOf(nameFmt);
            if(idx < 0)
                throw new DataException(String.format("Column \"%s\" not found in table.", nameFmt));
            else
                nameGetter = new CleanGetter(idx);
        }
    }

    /**
     * Load bubble template from given file.
     * 
     * @param bubbleTemplateFile
     *            path and name of HTML file to be use to generate bubbles
     * 
     * @throws Exception
     *             on any error accessing template file
     */
    public void setBubbleTemplate(File bubbleTemplateFile) throws Exception {
        descGetter = new VelocityGetter(hohSchema, bubbleTemplateFile, TEMPLATE_HOH_VAR);
        if(popFile != null)
            descGetter.addMembersVar(TEMPLATE_POP_VAR, popSchema);
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
        this.styler = new StyleMaker(hohSchema, styleFile, TEMPLATE_HOH_VAR);
    }
    
    /**
     * Run the program now. Data will be loaded and processed, and KML output
     * will be generated.  Progress will be displayed as it goes.
     * 
     * @throws SQLException
     *             on any error fetching records from database
     * @throws TransformException
     *             on any error transforming coordinates
     * @throws IOException on any error reading CSV or writing KML
     */
    public void go() throws SQLException, IOException, TransformException {
        // Test outdir here, not in setOutput. setInput calls setOutput, but we
        // don't want to crash there in case user calls setOutput with a
        // different path.
        if (outdir.exists() && !outdir.isDirectory())
            throw new IllegalArgumentException("Output \"" + outdir
                    + "\" exists as a non-zip file, and cannot be overwritten.");

        TimeTracker.start("Digital Populations to KML");

        File dbDir = (File) hohFile.getParentFile();
        File dbfile = new File(dbDir, DBNAME);
        this.db = new H2Wrapper(dbfile);
        TimeTracker.finished("Opening database file");


        // --- Load Main Tables Into Database --- //
        loadMainTables();
        
        
        // --- Build Spatial Index --- //
        STRtree index = loadAllPoints(hohTbl);
        TimeTracker.finished("Building spatial index");

        
        // --- Cut Index Into Tiles --- //
        // 7 out of 10 dentists prefer square tiles.
        Envelope bounds = (Envelope) index.getRoot().getBounds();
        JTSUtil.square(bounds);
        // Add safety margin.
        bounds.expandBy(1.0);
        
        FastQuadTree creat = new FastQuadTree(maxPointsPerTile);
        Tile<Boundable> _tbTiles = creat.createTiles(index, bounds);
        // Cheap Hack - I know this is true, just need to convince Java.
        @SuppressWarnings("unchecked")
        Tile<PointRef> tiles = (Tile<PointRef>)(Tile<?>)_tbTiles;
        
        if (index.size() > 0)
            System.err.format("Not all points were tiled!  %d points remain:\n  %s",
                index.size(), index);
        TimeTracker.finished("Building tiles");

        
        // --- Create KML File --- //
        createKml(FilenameUtils.getBaseName(hohFile.getName()), tiles);
        TimeTracker.finished("Writing KML file");
        
        
        // --- Close Database --- //
        db.shutdown();
        TimeTracker.finished("Closing database file");
        TimeTracker.total();
    }

    /**
     * Test the name and bubble templates with a random selectiion of
     * households. Prints simple HTML to stdout containing generated names and
     * bubbles.
     * 
     * @throws SQLException
     *             on any error fetching records from database
     * @throws TransformException
     *             on any error transforming coordinates
     * @throws IOException on any error reading CSV files
     */
    public void testTemplate() throws SQLException, IOException, TransformException {
        TimeTracker.start("Digital Populations to HTML");

        File dbDir = (File) hohFile.getParentFile();
        File dbfile = new File(dbDir, DBNAME);
        this.db = new H2Wrapper(dbfile);
        TimeTracker.finished("Opening database file");


        // --- Load Main Tables Into Database --- //
        loadMainTables();


        // --- Select Random Points --- //
        List<PointRef> refs = loadRandomPoints(hohTbl, 10);
        List<Point> points = load(refs);
        TimeTracker.finished("Selecting random households");

        
        // --- Generate HTML --- //
        System.out.println("<HTML><BODY>");
        for(Point p : points) {
            System.out.format("<H3>%s</H3>\n", p.getName());
            if(descGetter != null) {
                System.out.println(p.getDesc());
                System.out.println("<BR/><HR/>\n");
            }
        }
        System.out.println("</BODY></HTML>");
        
        
        // --- Close Database --- //
        db.shutdown();
        TimeTracker.finished("Closing database file");
        TimeTracker.total();
    }
    
    /**
     * Ensure our tables are in the database. If they're missing or the database
     * is out of date, then they're loaded from the CSV file(s).
     * 
     * @throws SQLException
     * @throws IOException on any error reading CSV files
     */
    private void loadMainTables() throws SQLException, IOException {
        if(hohFile.lastModified() > db.lastModified()) {
            db.loadTable(hohFile, hohTbl, false);
            TimeTracker.finished("Loading households table");
        }
        getHoh_stmt = db.prepareStatement("SELECT * FROM "+hohTbl+" WHERE uid=?");
        
        // Pop table only needed if making bubbles.
        if(popFile != null && descGetter != null) {
            if(popFile.lastModified() > db.lastModified()) {
                db.loadTable(popFile, popTbl, true);
                TimeTracker.finished("Loading population table");
            }
            getPop_stmt = db.prepareStatement("SELECT * FROM "+popTbl+" WHERE household=?");
        }
    }

    /**
     * Scan households table, compute geographic points, and load them into a
     * suitable spatial index.
     * 
     * @param table
     *            name of SQL table to scan
     * @return spatial index which maps locations to PointRef objects
     * 
     * @throws SQLException
     *             on any error fetching records from database
     * @throws TransformException
     *             on any error transforming coordinates
     */
    protected STRtree loadAllPoints(String table) throws SQLException, TransformException {
        STRtree tree = new STRtree();
        
        // WARNING: Harcoded field names!  This is what makes us Dp2Kml instead of Csv2Kml.
        String sql = "SELECT x,y,uid FROM "+table;
        ResultSet rs = db.executeQuery(sql);
        while(rs.next()) {
            // Skip rows that have no coordinates.
            // BOTH must be blank; one blank is an error.
            String sx = rs.getString(1);
            String sy = rs.getString(2);
            if(ObjectUtil.isBlank(sx) && ObjectUtil.isBlank(sy))
                continue;

            double x = Double.parseDouble(sx);
            double y = Double.parseDouble(sy);
            String uid = rs.getString(3);

            if(trans != null) {
                DirectPosition kmlXY = trans.toKml(x, y);
                x = trans.getKmlX(kmlXY);
                y = trans.getKmlY(kmlXY);
            }
            
            PointRef env = new PointRef(x, y, uid);
            tree.insert(env, env);
        }
        
        return tree;
    }

    /**
     * Select some random records from the households table.
     * Quantity returned may be reduced if any records selected have blank coordinates.
     * 
     * @param table name of table to scan
     * @param numPoints number of points to build
     * @return new list of points as PointRef objects
     * 
     * @throws SQLException
     * @throws TransformException
     */
    protected List<PointRef> loadRandomPoints(String table, int numPoints) throws SQLException, TransformException {
        ArrayList<PointRef> list = new ArrayList<PointRef>();
        
        // WARNING: Harcoded field names!
        String sql = String.format("SELECT x,y,uid FROM %s ORDER BY RAND() LIMIT %d",
            table, numPoints);
        ResultSet rs = db.executeQuery(sql);
        while(rs.next()) {
            // Skip rows that have no coordinates.
            // BOTH must be blank; one blank is an error.
            String sx = rs.getString(1);
            String sy = rs.getString(2);
            if(ObjectUtil.isBlank(sx) && ObjectUtil.isBlank(sy))
                continue;
            
            double x = Double.parseDouble(sx);
            double y = Double.parseDouble(sy);
            String uid = rs.getString(3);

            if(trans != null) {
                DirectPosition kmlXY = trans.toKml(x, y);
                x = trans.getKmlX(kmlXY);
                y = trans.getKmlY(kmlXY);
            }
            
            PointRef env = new PointRef(x, y, uid);
            list.add(env);
        }
        
        return list;
    }
    
    /**
     * Generate KML file from H2 database.  One query is executed per household.
     * 
     * @param docName
     * @param tiles objects to write into KML
     * 
     * @throws TransformException
     * @throws IOException on any error writing KML content
     * @throws SQLException 
     */
    protected void createKml(String docName, Tile<PointRef> tiles) throws TransformException, IOException, SQLException {
        if(parallelSave)
            // Use exactly one thread per core, and no job queue. TrueZip is a
            // major bottleneck, so this doesn't help much.
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
        
        KmlWriter masterKml = new KmlWriter(docName, outdir);
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
                if(popFile != null)
                    // bubble+population makes for huge files.
                    masterKml.setPixelsPerPoint(500);
                else
                    // bubble without population isn't quite so bad.
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
        masterKml.writeRaster("rasters/overview.png", tiles, rasterSize, Color.RED);
        LogUtil.progress(log, "  Done.");

        // Write out all the tiles.
        try {
            masterKml.setFolders("Tile Borders", "Households");
            createKml(tiles, masterKml);
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
    }

    /**
     * Create KML file from a collection of tiles. Each tile will be written as
     * a seperate .kml file into the output folder. The file name will be
     * representative of the tile's location in the original tile tree.
     * <P>
     * This method uses multi-threading to build the tiles, and can use as many
     * cores as are available, although TrueZip greatly interferes with that.
     * 
     * @param tile
     *            tree of tiles to write out. Must contains PointRef objects.
     * @param masterKml
     *            helper for creating output files
     * @param tileName
     *            base name of file if written, or prefix of sub-tiles
     * 
     * @throws SQLException
     *             on any error fetching records from database
     * @throws TransformException
     *             on any error transforming coordinates
     */
    protected void createKml(Tile<PointRef> rootTile, final KmlWriter masterKml) throws SQLException, TransformException {
        // Collect all writable tiles into flat list so we can report progress %.
        ArrayList<Tile<PointRef>> tilesList = new ArrayList<Tile<PointRef>>();
        int count = 0;
        for(Tile<PointRef> node : rootTile)
            if(node.contents != null && node.contents.size() > 0) {
                tilesList.add(node);
                count += node.contents.size();
            }

        LogUtil.progress(log, "Started writing tiles");
        final ProgressToy prog = new ProgressToy(log, 5, count);
        
        int complete = 0;
        for(int n=0; n<tilesList.size(); n++) {
            Tile<PointRef> curTile = tilesList.get(n);
            final Tile<Point> tile = load(curTile);
            final String tileName = String.format("%d", n);
            complete += tile.contents.size();
            final int f_complete = complete;
            
            exec.submit(new Runnable() {
                public void run() {
                    try {
                        // something wrong with java .. can't cast contents to (Collection<Point>)
                        masterKml.writeTile(
                            "tiles/" + tileName + ".kml",
                            masterKml.getDocName() + "-" + tileName,
                            tile.bounds, tile.contents);
                        prog.printProgress(f_complete);
                    }
                    catch (Exception e) {
                        // Can't do much here; throwing won't stop the program.
                        log.severe(ObjectUtil.getMessage(e));
                    }
                }
            });
        }
    }

    /**
     * Convert a tile of PointRef into a tile of Point by loading the referenced
     * point data from the database.
     * 
     * @param ref tile containing point IDs
     * @return tile containing actual points, with all coordinates translated
     * 
     * @throws SQLException
     *             on any error fetching records from database
     * @throws TransformException
     *             on any error transforming coordinates
     */
    protected Tile<Point> load(Tile<PointRef> ref) throws SQLException, TransformException {
        Tile<Point> tile = new Tile<Point>();
        tile.bounds = ref.bounds;
        tile.contents = load(ref.contents);
        return tile;
    }
    
    /**
     * Fetch the household and population records referenced by the given list
     * of IDs, and build a Point object for each.
     * 
     * @param contents
     *            list of household/realization IDs
     * @return Point objects ready to convert into KML placemarks
     * 
     * @throws SQLException on any error fetching records from database
     * @throws TransformException on any error transforming coordinates
     */
    protected ArrayList<Point> load(List<PointRef> contents) throws SQLException, TransformException {
        ArrayList<Point> points = new ArrayList<Point>();
        for(PointRef ref : contents) {
            // Get household record.
            List<List<String>> hohs = db.getRecords(getHoh_stmt, ref.getID());
            if(hohs.size() != 1)
                throw new IllegalStateException(String.format(
                    "Expected 1 household, got %d for ID \"%s\"",
                    hohs.size(), ref.getID()));
            List<String> hoh = hohs.get(0);

            // Build placemark from previously-translated coords and loaded records.
            double x = ref.getMinX();
            double y = ref.getMinY();
            String name = nameGetter.get(hoh);
            
            String desc = null;
            if(descGetter != null) {
                if(popFile != null) {
                    // Get all its peoples.
                    List<List<String>> pops = db.getRecords(getPop_stmt, ref.getID());
                    descGetter.setMembersValue(pops);
                }
                
                desc = descGetter.get(hoh);
            }

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
                styler.get(hoh);
                
                p.setName((String) styler.getScriptVar("name"));
                p.setDesc((String) styler.getScriptVar("desc"));
                p.setIconStyle(styler.getCurrentIconStyle());
                p.setNameStyle(styler.getCurrentLabelStyle());
            }
            
            points.add(p);
        }
        return points;
    }

    /**
     * Create a simple Apache Velocity template for the the user to start with.
     * Result is printed to stdout. Includes fields for household and population
     * file, if provided.
     * 
     * @throws FileNotFoundException
     * @throws IOException on any error reading CSV files
     */
    public void generateTemplate() throws FileNotFoundException, IOException {
        // Hide DP fields that are useless or redundant.
        HashSet<String> hohFieldsHidden = new HashSet<String>();
        hohFieldsHidden.add("x");
        hohFieldsHidden.add("y");
        hohFieldsHidden.add("uid");
        hohFieldsHidden.add("household");
        hohFieldsHidden.add("householdID");
        hohFieldsHidden.add("realizationID");
        
        ICsvListReader reader = new CsvListReader(new FileReader(hohFile),
            CsvPreference.STANDARD_PREFERENCE);

        try {
            System.out.println("<h3>Household</h3>");
            System.out.println("<table border=\"1\">");
            
            // Load header line with column names.
            List<String> head = Arrays.asList(reader.getCSVHeader(true));
            
            // Present household as a 1D table; one row per value; title on left, value on right.
            for (int i = 0; i < head.size(); i++) {
                String name = head.get(i);
                String field;
                
                if(ObjectUtil.isBlank(name)) {
                    name = String.format("(Column %d)", i);
                    field = String.format("$%s[%d]", TEMPLATE_HOH_VAR, i);
                }
                else {
                    if(hohFieldsHidden.contains(name))
                        System.out.print("##");
                    field = String.format("$%s.%s", TEMPLATE_HOH_VAR, VelocityGetter.makeSafeID(name));
                }
                System.out.format("  <tr><td>%s</td><td>%s</td></tr>\n", name, field);
            }
            
            System.out.println("</table>");
        }
        finally {
            reader.close();
        }


        // Present population as a 2D table, one row per member.
        if(popFile != null) {
            reader = new CsvListReader(new FileReader(popFile),
                CsvPreference.STANDARD_PREFERENCE);

            try {
                List<String> head = Arrays.asList(reader.getCSVHeader(true));

                System.out.println("\n<h3>Members</h3>");
                System.out.println("<table border=\"1\">");

                // Generate header line with column titles.
                System.out.println("  <tr>");
                for (int i = 0; i < head.size(); i++) {
                    String name = head.get(i);
                    String title = name;
                    if(ObjectUtil.isBlank(name))
                        title = String.format("(Column %d)", i);
                    else if(hohFieldsHidden.contains(name))
                        System.out.print("##");
                    System.out.format("    <th>%s</th>\n", title);
                }
                System.out.println("  </tr>");

                // Generate loop statement for rows of people.
                System.out.format("#foreach( $pop in $%s )\n", TEMPLATE_POP_VAR);
                System.out.println("  <tr>");
                for (int i = 0; i < head.size(); i++) {
                    String name = head.get(i);
                    String field;
                    
                    if(ObjectUtil.isBlank(name))
                        field = String.format("$pop[%d]", i);
                    else {
                        field = String.format("$pop.%s", name);
                        if(hohFieldsHidden.contains(name))
                            System.out.print("##");
                    }
                    System.out.format("    <td>%s</td>\n", field);
                }
                System.out.println("  </tr>");
                System.out.println("#end");
                
                System.out.println("</table>");
            }
            finally {
                reader.close();
            }
        }
    }

    
    
    /**
     * Parse command line and perform requested operation.
     * 
     * @param args list of command-line arguments
     * 
     * @throws Exception on any error accessing required data
     */
    public static void main(String[] args) throws Exception {
        Dp2Kml dk = new Dp2Kml();

        OptionParser parser = new OptionParser();
        
        parser.accepts("gentemplate", "output simple bubble template from households file and exit");
        parser.accepts("testtemplate", "generate HTML from the first 10 records then exit");
        
        parser.accepts("bubble", "create bubbles using this template file (default: no bubbles)")
            .withRequiredArg().ofType(File.class);
        parser.accepts("style", "adjust placemark styles using this template file")
            .withRequiredArg().ofType(File.class);
        parser.accepts("pix", "minimum pixels per household for visibility; tiles with less will not be displayed")
            .withRequiredArg().ofType(Integer.class);
        parser.accepts("crs", "coord ref system used in file (default: no conversion)")
            .withRequiredArg().ofType(String.class);
        parser.accepts("name", "format string for name of each point (default: \"%{uid}\"")
            .withRequiredArg().ofType(String.class);
        parser.accepts("output", "dir or file name to receive output (default: household file name, with .kmz extension)")
            .withRequiredArg().ofType(File.class);
        
        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");

        OptionSet opts = parser.parse(args);
        int reqdArgs = 1;

        if(opts.has("h") || opts.has("help") || args.length == 0) {
            System.err.println("Usage: " + dk.getClass().getSimpleName()
                + " [options] <household-file> [<population-file>]");
            System.err.println("  <household-file> is the table of households generated by Digital Populations");
            System.err.println("  <population-file> is an optional table of members of the above households");
            System.err.println();
            parser.printHelpOn(System.err);
            System.exit(5);
        }

        if (opts.nonOptionArguments().size() < reqdArgs || opts.nonOptionArguments().size() > 2) {
            System.err.println("Required args are missing; there should be "+reqdArgs+".");
            System.exit(5);
        }

        LogUtil.getRootLogger().setLevel(Level.INFO);
        LogUtil.cleanFormat();
        
        dk.setHohFile(new File(opts.nonOptionArguments().get(0)));
        if(opts.nonOptionArguments().size() > 1)
            dk.setPopFile(new File(opts.nonOptionArguments().get(1)));

        if(opts.has("crs"))
            dk.setCRS((String) opts.valueOf("crs"));
        if(opts.has("name"))
            dk.setNameTemplate((String) opts.valueOf("name"));
        if (opts.has("bubble"))
            dk.setBubbleTemplate((File) opts.valueOf("bubble"));
        if (opts.has("style"))
            dk.setStyleTemplate((File) opts.valueOf("style"));
        if (opts.has("pix"))
            dk.pixelsPerHoh = (Integer) opts.valueOf("pix");
        if(opts.has("output"))
            dk.setOutput((File) opts.valueOf("output"));
        
        if (opts.has("gentemplate")) {
            dk.generateTemplate();
        } else if (opts.has("testtemplate")) {
            dk.testTemplate();
        } else {
            dk.go();
        }
    }
}
