package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.Boundable;

import de.micromata.opengis.kml.v_2_2_0.Container;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.IconStyle;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LabelStyle;
import de.micromata.opengis.kml.v_2_2_0.LatLonAltBox;
import de.micromata.opengis.kml.v_2_2_0.LatLonBox;
import de.micromata.opengis.kml.v_2_2_0.Lod;
import de.micromata.opengis.kml.v_2_2_0.NetworkLink;
import de.micromata.opengis.kml.v_2_2_0.Pair;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;
import de.micromata.opengis.kml.v_2_2_0.StyleMap;
import de.micromata.opengis.kml.v_2_2_0.StyleState;
import de.schlichtherle.io.DefaultArchiveDetector;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.archive.zip.Zip32Driver;


/**
 * Helper for the *2Kml tools.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class KmlWriter {
    /**
     * Whether to draw a faint outline around each generated tile.
     */
    public static final boolean SHOW_TILE_BORDERS = false;
    
    /**
     * Name of main control file in the .kmz archive. 
     */
    public static final String  MAIN_KML          = "doc.kml";

    /** Default icon scale normally. */
    double DEFAULT_ICON_SCALE_NORMAL = 0.4;
    /** Default icon scale when mouse is on top. */
    double DEFAULT_ICON_SCALE_HOVER = 0.8;
    
    protected Logger            log               = Logger.getLogger(Dp2Kml.class.getPackage().getName());
    
    /**
     * Area on screen each placemark requires to become visible. Each tile will
     * be set to be invisible until it occupies an area on screen equal to this
     * value times the number of points in the tile.
     */
    private double              pixelsPerPoint    = 50;
    
    protected String            docName;
    protected File              outdir;
    protected Kml               topKml;
    protected Document          topDoc;
    protected Container         placemarkFolder;
    protected Container         borderFolder;
    protected String            styleFile         = null;
    protected String            hohIconUrl        = "http://maps.google.com/mapfiles/kml/shapes/homegardenbusiness.png";    

    /**
     * Init new writer to generate files into a location.
     * 
     * @param docName
     *            document name to write into main doc.kml
     * @param outdir
     *            dir or zip file to receive all output. If name ends with zip
     *            or kmz, a zip file will be created.
     * @throws IOException
     */
    public KmlWriter(String docName, File outdir) throws IOException {
        this.docName = docName;
        this.outdir = outdir;
        
        if(outdir.exists() && !outdir.isDirectory())
            throw new IllegalArgumentException("Output \"" + outdir
                    + "\" exists as a non-zip file, and cannot be overwritten.");
        
        topKml = new Kml();
        
        topDoc = topKml
            .createAndSetDocument()
                .withName(docName);
//Note: ArcGIS can't load a KMZ that contains <open>.
//                .withOpen(false);
        
        placemarkFolder = topDoc;
        borderFolder = topDoc;
        
        addKmlLineStyle(topDoc, "box");
        
        // If creating a zip file, we MUST save once before ANY other KML files
        // are written to zip file. Google Earth opens the first KML it sees,
        // regardless of which directory it's in. Doing this now effectively
        // reserves the first zip entry for the main file.
        save();
    }
    
    public String getDocName() {
        return docName;
    }

    /**
     * Create sub-folders in the master document to hold placemarks. If not
     * called, all objects will be dumped into root document.
     * 
     * @param borderName
     *            name of folder that will hold tile outlines (works only if
     *            borders are enabled)
     * @param placemarkName
     *            name of folder that will hold placemark tiles
     */
    public void setFolders(String borderName, String placemarkName) {
        if(placemarkFolder != topDoc || borderFolder != topDoc)
            throw new IllegalStateException("Folders have already been created.");
        else {
            if(SHOW_TILE_BORDERS)
                borderFolder = topDoc.createAndAddFolder()
                    .withName(borderName);
            placemarkFolder = topDoc.createAndAddFolder()
                .withName(placemarkName);
        }
    }
    
    public void setPixelsPerPoint(double pixelsPerPoint) {
        this.pixelsPerPoint = pixelsPerPoint;
    }

    public double getPixelsPerPoint() {
        return pixelsPerPoint;
    }

    /**
     * Set LookAt element so the given box is visible on screen when project is
     * opened. This is just an estimate; KML doesn't let you specify a
     * geographic box, so we play some games to try to achieve the same result.
     * <P>
     * This is optional; Google Earth will default to displaying the project's
     * bounds.
     * 
     * @param range geographic box to display when project is opened
     */
    public void setLookAt(Envelope range) {
        // Can't say "fit box to screen", can only specify center and range to camera.
        double look[] = getLookAt(range);
        topDoc.createAndSetLookAt()
            .withLongitude(look[0]).withLatitude(look[1])
            .withRange(Math.ceil(look[2])).withTilt(0);
    }

    /**
     * Call writeRaster using a java.util.Collection.
     */
    public void writeRaster(
            String path,
            Envelope bounds, Collection<Point> points,
            int rasterSize, Color color)
            throws IOException, TransformException {
        writeRaster(path, bounds, points.iterator(), rasterSize, color);
    }

    /**
     * Call writeRaster using a tree of csv2kml.Tile objects.
     */
    public void writeRaster(
            String path,
            Tile<? extends Boundable> tiles,
            int rasterSize, Color color)
    throws IOException, TransformException {
        writeRaster(path, tiles.bounds, tiles.contentIterator(), rasterSize, color);
    }
    
    /**
     * Create PNG image from points, and attach to KML as a GroundOverlay. File
     * path written to KML will be final two components of 'file' only (i.e. the
     * file name and the name of its directory, e.g. "rasters/overview.png").
     * <P>
     * CHEAT: We only plot a dot at the lower left corner of each object's
     * bounding box. We *should* draw a rectangle, but csv2kml only generates
     * dots.
     * 
     * @param path
     *            relative path to create inside outdir
     * @param bounds
     *            region that image will cover. Object partially or entirely
     *            outside this region will not be drawn.
     * @param points
     *            stream of objects to draw
     * @param rasterSize
     *            total area of image, in pixels
     * @param color
     *            color of points in raster
     * 
     * @throws IOException
     * @throws TransformException
     */
    public void writeRaster(
            String path,
            Envelope bounds, Iterator<? extends Boundable> pointz,
            int rasterSize, Color color)
            throws IOException, TransformException {
        // Compute width and height to cover map with rasterSize pixels
        // while keeping the pixels square.
        double factor = Math.sqrt(bounds.getWidth()*bounds.getHeight() / rasterSize);
        int numCols = (int) Math.ceil(bounds.getWidth() / factor);
        int numRows = (int) Math.ceil(bounds.getHeight() / factor);
        
        // RGIK does the hard work.
        GISClass raster = new GISClass(
            bounds.getMinX(), bounds.getMaxY(), 
            bounds.getWidth() / (numCols), bounds.getHeight() / (numRows),
            numRows, numCols);

        // GISClass stores a values at the intersection of horizontal and
        // vertical lines. Google Earth, however, treats each pixel as filling
        // the cell between lines. Fortunately, the way Index is calculated is
        // exactly countered by the way GE displays the image.
        
        // Add 1 to each cell for every point it contains.
        while(pointz.hasNext()) {
            Boundable point = pointz.next();
            Envelope pbound = (Envelope) point.getBounds();
            if(bounds.contains(pbound)) {
                double x = pbound.getMinX();
                double y = pbound.getMinY();
                
                int r = raster.getRowIndex(x, y);
                int c = raster.getColumnIndex(x, y);
                raster.setCellValue(r, c, 1 + raster.getCellValue(r, c) );
            }
        }

        // Find largest value written.
        int max = raster.getMaximumValue();

        // Generate image from raster.
        BufferedImage img = new BufferedImage(raster.getNumberColumns(), raster.getNumberRows(), BufferedImage.TYPE_INT_ARGB);
        for(int r=0; r<=raster.getNumberRows(); r++)
            for(int c=0; c<=raster.getNumberColumns(); c++) {
                if(! raster.isNoData(r, c)) {
                    int val = raster.getCellValue(r, c);
                    if(val > 0) {
                        // Pixels is always opaque, but intensity varies with density.
                        float grey = val / (float)max;
                        int alf = 0xff; //alpha = opaque
                        int red = (int) (color.getRed()*grey);
                        int grn = (int) (color.getGreen()*grey);
                        int blu = (int) (color.getBlue()*grey);
                        img.setRGB(c, r, ((alf<<8|red)<<8|grn)<<8|blu);
                    }
                }
                // else do nothing; pixel defaults to transparent black.
            }
        
        File outfile = new File(outdir, path);
        zipWrite(img, "PNG", outfile);

        topDoc.createAndAddGroundOverlay()
            .withName("Overview")
            .withLatLonBox(toBox(raster))
            .createAndSetIcon()
                .withHref(path);
    }

    int stylenum = 1;
    
    /**
     * Write households to new KML file, and add it as tile to current KML.
     * 
     * @param path relative path and name of file to create inside outdir
     * @param docName user-visible name to place inside kml file
     * @param bounds nominal bounding box for points
     * @param points Collection<csv2kml.Point> of households
     * 
     * @throws TransformException
     * @throws IOException 
     */
    public void writeTile(String path, String docName, Envelope bounds, Collection<? extends Point> points)
            throws TransformException, IOException {
        Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(docName);
        
        //Don't need this; we're using writeStyle() instead.
        //addKmlStyles(doc);
        
        for(Point point : points) {
            Placemark p = doc.createAndAddPlacemark();
            
            if(point.getName() != null)
                p.setName(point.getName());
            if(point.getDesc() != null)
                p.setDescription(point.getDesc());

            IconStyle is = null;
            if(point.getIconStyle() != null) {
                // Placemark has a custom icon style.
                is = toKml(point.getIconStyle());
            }
            
            LabelStyle ls = null;
            if(point.getNameStyle() != null) {
                // Placemark has a custom label style.
                ls = toKml(point.getNameStyle());
            }

            // If user specifies custom icon or label (or both), then we need to
            // build a custom per-placemark style element. If user specifies one
            // but not the other, then we'll provide defaults as in
            // addKmlStyles().
            if(is != null || ls != null) {
                // Build suitable icon styles.
                IconStyle isN;
                IconStyle isH;
                if(is == null) {
                    // Default: as in addKmlStyles()
                    isN = new IconStyle()
                        .withScale(DEFAULT_ICON_SCALE_NORMAL)
                        .withIcon(new Icon()
                            .withHref(hohIconUrl));
                    isH = new IconStyle()
                        .withScale(DEFAULT_ICON_SCALE_HOVER)
                        .withIcon(new Icon()
                            .withHref(hohIconUrl));
                }
                else {
                    // Normal: as defined by user
                    isN = is;
                    // Hightlight (mouse-over): 2x larger
                    isH = is.clone();
                    isH.setScale(isH.getScale() * 2);
                }
                
                // Build suitable label styles.
                LabelStyle lsN;
                LabelStyle lsH;
                if(ls == null) {
                    // Default: as in addKmlStyles()
                    lsH = new LabelStyle().withScale(1);
                    lsN = new LabelStyle().withScale(0);
                }
                else {
                    // Hightlight (mouse-over): as defined by user
                    lsH = ls;
                    // Normal: hidden
                    lsN = lsH.clone();
                    lsN.setScale(0);
                }

                Style normal = new Style()
                    .withIconStyle(isN)
                    .withLabelStyle(lsN);
                Style hilite = new Style()
                    .withIconStyle(isH)
                    .withLabelStyle(lsH);
                StyleMap style = new StyleMap()
                    .withPair(Arrays.asList(
                        new Pair().withKey(StyleState.NORMAL).withStyleSelector(normal),
                        new Pair().withKey(StyleState.HIGHLIGHT).withStyleSelector(hilite)));
                p.addToStyleSelector(style);
            }
            
            else {
                // Placemark gets global style.
                if(styleFile != null)
                    p.setStyleUrl(String.format("%s#%s", styleFile, "hoh"));
            }
            
            p.createAndSetPoint().addToCoordinates(point.getX(), point.getY());
        }
        
        // Generally, building the structure above takes very little time, while
        // marshal() takes quite a while.
        File outfile = new File(outdir, path);
        zipWrite(kml, outfile);

        
        // Compute LOD range.
        //  - houses.size() / maxPointsOnScreen gives us scale factor for current tile
        //  - typicalScreen * (that^) gives us pixels we want this tile to cover before it becomes visible
        //  - sqrt(that^) gives use the KML "LodPixels" number
        //  - ceil(that^) is just to give nice round numbers
//        double typicalScreen = 1920*1080;
//        double maxPointsOnScreen = 10000;
//        int lodPixels = (int) Math.ceil(Math.sqrt(typicalScreen * (points.size() / maxPointsOnScreen)));
        int lodPixels = (int) Math.ceil(Math.sqrt(points.size() * getPixelsPerPoint()));

        // Add link to main file.
        addLink(path, docName, bounds, lodPixels);
    }

    /**
     * Create stand-alone style file, and create appropriate URL for use by all
     * placemarks.  File is assumed to be in same dir as tiles; URL will contain only
     * file name and given id.
     * 
     * @param path relative path to file to create inside outdir
     * @param id element within file that placemarks will use
     * 
     * @throws IOException
     *             on any file error
     */
    public void writeStyle(String path) throws IOException {
        File outfile = new File(outdir, path);
        
        // Create our custom icon first.
        File iconfile = new File(outfile.getParentFile(), "household.png");
        URL iconrsc = getClass().getResource("household.png");
        iconfile.copyFrom(iconrsc.openStream());
        this.hohIconUrl = "household.png";

        // Now create the placemark styles using the above icon.
        Kml kml = new Kml();
        Document doc = kml.createAndSetDocument()
            .withName("DP Tile Style");
        addKmlStyles(doc, "hoh");
        
        zipWrite(kml, outfile);
        
        // Save name so writeTile knows where to find styles.
        this.styleFile = outfile.getName();
    }

    /**
     * @param doc object to receive styles
     * @param id name that placemarks will use to refer to this style
     */
    public void addKmlStyles(Document doc, String id) {
        // Create a style for every household.
        //  - normal style for all households
        doc.createAndAddStyle()
            .withId("hohNorm")
            .withIconStyle(new IconStyle()
                .withScale(DEFAULT_ICON_SCALE_NORMAL)
                .withIcon(new Icon()
                    .withHref(hohIconUrl)))
            .withLabelStyle(new LabelStyle()
                .withScale(0));
        //  - highlight (i.e. mouse-hover) style for all households
        doc.createAndAddStyle()
            .withId("hohHover")
            .withIconStyle(new IconStyle()
                .withScale(DEFAULT_ICON_SCALE_HOVER)
                .withIcon(new Icon()
                    .withHref(hohIconUrl)))
            .withLabelStyle(new LabelStyle()
                .withScale(1.0));
        //  - tell when to use each
        doc.createAndAddStyleMap()
            .withId(id)
            .addToPair(new Pair()
                .withKey(StyleState.NORMAL)
                .withStyleUrl("#hohNorm"))
            .addToPair(new Pair()
                .withKey(StyleState.HIGHLIGHT)
                .withStyleUrl("#hohHover"));
    }
    
    /**
     * Create a style for tile outlines.
     * 
     * @param doc
     * @param id
     */
    public static void addKmlLineStyle(Document doc, String id) {
        doc.createAndAddStyle()
            .withId(id)
            .createAndSetLineStyle()
                .withColor("26ffffff")
                .withWidth(0.1);
    }
    
    public void addLink(String relPath, String docName, Envelope bounds, int minLodPixels) {
        // Add link to main file.
        NetworkLink link = createAndAddNetworkLink(placemarkFolder)
            .withName(docName);
        link.createAndSetLink()
            .withHref(relPath);
        link.createAndSetRegion()
            .withLatLonAltBox(toBox2(bounds));
        if(minLodPixels > 0)
            link.getRegion()
                .setLod(new Lod()
                    .withMinLodPixels(minLodPixels)
                    .withMaxLodPixels(-1));
        
        // Add outline for debugging
        if(SHOW_TILE_BORDERS) {
            createAndAddPlacemark(borderFolder)
                .withName(docName+"-border")
                .withStyleUrl("#box")
                .createAndSetLineString()
                    .withCoordinates(outline(bounds));
        }
    }

    /**
     * Save main KML file that links all the rasters and tiles together.
     * Be sure to call this after all rasters and tiles have been written.
     * It can be called at any time for a snapshot of the files written so far.
     * 
     * @param path relative path and name of file inside outdir
     * @throws IOException
     */
    public void save() throws IOException {
        File outfile = new File(outdir, MAIN_KML);
        zipWrite(topKml, outfile);
    }

    public LatLonBox toBox(Envelope range) {
        LatLonBox box = new LatLonBox()
            .withWest(range.getMinX()).withSouth(range.getMinY())
            .withEast(range.getMaxX()).withNorth(range.getMaxY());
        return box;
    }
    
    public LatLonBox toBox(GISGrid grid) {
        LatLonBox box = new LatLonBox()
            .withWest(grid.getWestEdge()).withSouth(grid.getSouthEdge())
            .withEast(grid.getEastEdge()).withNorth(grid.getNorthEdge());
        return box;
    }
    
    public LatLonAltBox toBox2(Envelope range) {
        LatLonAltBox box = new LatLonAltBox()
            .withWest(range.getMinX()).withSouth(range.getMinY())
            .withEast(range.getMaxX()).withNorth(range.getMaxY());
        return box;
    }
    
    public static List<Coordinate> outline(Envelope range) {
        List<Coordinate> coords = new ArrayList<Coordinate>();
        coords.add(new Coordinate(range.getMinX(), range.getMinY()));
        coords.add(new Coordinate(range.getMaxX(), range.getMinY()));
        coords.add(new Coordinate(range.getMaxX(), range.getMaxY()));
        coords.add(new Coordinate(range.getMinX(), range.getMaxY()));
        coords.add(new Coordinate(range.getMinX(), range.getMinY()));
        return coords;
    }

    /**
     * Compute LookAt numbers for a geographic area.
     * 
     * @param e bounds of area to look at, in WGS84 degrees
     * @return {longitude, latitude, range} as array
     * 
     * @see Shamelessly stolen from <a href="http://jira.codehaus.org/browse/GEOS-1240">a patch to GeoServer</a>.
     */
    public static double[] getLookAt(Envelope e) {
        double lon1 = e.getMinX();
        double lat1 = e.getMinY();
        double lon2 = e.getMaxX();
        double lat2 = e.getMaxY();

        double R_EARTH = 6.371 * 1000000; // meters
        double VIEWER_WIDTH = 22 * Math.PI / 180; // The field of view of the
        // google maps camera, in radians
        double[] p1 = getRect(lon1, lat1, R_EARTH);
        double[] p2 = getRect(lon2, lat2, R_EARTH);
        double[] midpoint = new double[] {
                (p1[0] + p2[0]) / 2, (p1[1] + p2[1]) / 2, (p1[2] + p2[2]) / 2
        };

        midpoint = getGeographic(midpoint[0], midpoint[1], midpoint[2]);

        double distance = distance(p1, p2);

        double height = distance / (2 * Math.tan(VIEWER_WIDTH));

        return new double[] {
                midpoint[0], midpoint[1], height
        };
    }

    private static double[] getRect(double lat, double lon, double radius) {
        double theta = (90 - lat) * Math.PI / 180;
        double phi = (90 - lon) * Math.PI / 180;

        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        return new double[] {
                x, y, z
        };
    }

    private static double[] getGeographic(double x, double y, double z) {
        double theta, phi, radius;
        radius = distance(new double[] {
                x, y, z
        }, new double[] {
                0, 0, 0
        });
        theta = Math.atan2(Math.sqrt(x * x + y * y), z);
        phi = Math.atan2(y, x);

        double lat = 90 - (theta * 180 / Math.PI);
        double lon = 90 - (phi * 180 / Math.PI);

        return new double[] {
                lon, (lat > 180 ? lat - 360 : lat), radius
        };
    }

    private static double distance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        double dz = p1[2] - p2[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Write KML via the special TrueZip output stream.
     * 
     * @param kml
     * @param file
     * 
     * @throws IOException
     */
    public void zipWrite(Kml kml, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            kml.marshal(out);
        }
        finally {
            out.close();
        }
    }

    /**
     * Write image via the special TrueZip output stream.
     * 
     * @param img
     * @param string
     * @param file
     * 
     * @throws IOException
     */
    public void zipWrite(RenderedImage img, String string, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            ImageIO.write(img, "PNG", out);
        }
        finally {
            out.close();
        }
    }

    /**
     * Strangely, Container does not support adding things, despite the fact
     * that both its sub-types (Document and Folder) do. This is a bug in the
     * KML schema, not just in our KML library.
     * 
     * @param container
     * @return
     */
    public static Placemark createAndAddPlacemark(Container container) {
        if(container instanceof Document)
            return ((Document) container).createAndAddPlacemark();
        else if(container instanceof Folder)
            return ((Folder) container).createAndAddPlacemark();
        else
            throw new IllegalArgumentException("createAndAddPlacemark not supported by "+container);
    }

    /**
     * Strangely, Container does not support adding things, despite the fact
     * that both its sub-types (Document and Folder) do. This is a bug in the
     * KML schema, not just in our KML library.
     * 
     * @param container
     * @return
     */
    public static NetworkLink createAndAddNetworkLink(Container container) {
        if(container instanceof Document)
            return ((Document) container).createAndAddNetworkLink();
        else if(container instanceof Folder)
            return ((Folder) container).createAndAddNetworkLink();
        else
            throw new IllegalArgumentException("createAndAddNetworkLink not supported by "+container);
    }

    /**
     * Convert a Color object into the notation expected by KML.
     * 
     * @param color
     * @return
     */
    public static String toKml(Color color) {
        return String.format("%02x%02x%02x%02x",
            color.getAlpha(), color.getBlue(), color.getGreen(), color.getRed());
    }

    /**
     * Generate KML IconStyle element from our own IconStyle descriptor. Fields
     * that haven't been set will receive defaults.
     * 
     * @param icon our IconStyle descriptor
     * @return new KML IconStyle object
     */
    protected de.micromata.opengis.kml.v_2_2_0.IconStyle toKml(mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml.IconStyle icon) {
        de.micromata.opengis.kml.v_2_2_0.IconStyle s = new de.micromata.opengis.kml.v_2_2_0.IconStyle();
        
        if(ObjectUtil.isBlank(icon.getImg()))
            s.setIcon(new Icon().withHref("household.png"));
        else
            s.setIcon(new Icon().withHref(icon.getImg()));

        // icon.scale==0 means "use default"
        if(icon.getScale() != 0)
            s.setScale(icon.getScale());
        else
            // .. unfortunately, JAK's default is also zero!
            s.setScale(DEFAULT_ICON_SCALE_NORMAL);
        
        if(!ObjectUtil.isBlank(icon.getColor())) {
            try {
                String k = toKml(icon.getColorObj());
                s.setColor(k);
            }
            catch(IllegalArgumentException e) {
                log.warning(e.getMessage());
            }
        }
        return s;
    }

    protected LabelStyle toKml(NameStyle nameStyle) {
        LabelStyle s = new LabelStyle();
        
        if(nameStyle.getScale() != 0)
            s.setScale(nameStyle.getScale());
        if(nameStyle.getColorObj() != null) {
            try {
                String k = toKml(nameStyle.getColorObj());
                s.setColor(k);
            }
            catch(IllegalArgumentException e) {
                log.warning(e.getMessage());
            }
        }
        return s;
    }

    /**
     * Configure TrueZip to recognize kmz files. MUST BE CALLED before any .kmz
     * files can be read or written.
     */
    public static void configTrueZip() {
        // TrueZip supports *.zip; this adds *.kmz.
        File.setDefaultArchiveDetector(
            new DefaultArchiveDetector(
                DefaultArchiveDetector.DEFAULT,
                new Object[] {
                    "kmz", new Zip32Driver()
                }));
    }
}
