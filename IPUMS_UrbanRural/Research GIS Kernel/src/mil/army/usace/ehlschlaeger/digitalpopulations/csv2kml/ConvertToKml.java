package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;



/**
 * Convert coordinates to WGS 84 (required by KML). Conversion is performed by
 * GeoTools, and the main way to specify coordinate systems is from the EPSG
 * database. Projection files are also supported.
 * <P>
 * WARNING: Difference CRS systems store their ordinates in different orders.
 * You can't assume [0] points in any particular direction. Use getKmlX() and
 * getKmlY() to get ordinates.
 * 
 * @see <A HREF="http://spatialreference.org/">http://spatialreference.org/</A>
 * @see <A HREF="http://www.epsg-registry.org/">http://www.epsg-registry.org/</A>
 * 
 * @author William R. Zwicky
 */
public class ConvertToKml {
    /** String that was used to create crs_src. */
    public String crs_src_name;
    /** Source CRS for the input data. */
    public CoordinateReferenceSystem crs_src;
    
    /** String that was used to create crs_kml. */
    public String crs_kml_name;
    /** Target CRS for the output data. */
    public CoordinateReferenceSystem crs_kml;
    private int crs_kml_x;
    private int crs_kml_y;
    
    private MathTransform trans;
    private MathTransform inverse;

    
    /**
     * Create a translator that assumes all input points are WGS84, so does
     * nothing.
     * 
     * @throws TransformException
     * @throws FactoryException
     */
    public ConvertToKml() throws TransformException, FactoryException {
        this("EPSG:4326");
        this.trans = null;
        this.inverse = null;
    }
    
    /**
     * Create a translator, give the spec for a coordinate system.
     * 
     * @param sourceCRS name or spec for a coordinate system, as expected
     *     by GeoTools CRS.decode()
     * 
     * @throws TransformException
     * @throws FactoryException
     * 
     * @see http://docs.codehaus.org/display/GEOTDOC/01+CRS+Helper+Class
     * @see http://javadoc.geotools.fr/snapshot/org/geotools/referencing/CRS.html#decode(java.lang.String)
     */
    public ConvertToKml(String sourceCRS) throws TransformException, FactoryException {
        this.crs_src_name = sourceCRS;
        this.crs_src = CRS.decode(crs_src_name);
        postInit();
    }
    
    /**
     * Create a translator, give a file containing a spec for a coordinate system.
     * ESRI .prj files are one such source.
     * 
     * @param projFile
     * @throws IOException 
     * @throws FactoryException 
     * @throws TransformException 
     */
    public ConvertToKml(File projFile) throws IOException, FactoryException, TransformException {
        String spec = FileUtils.readFileToString(projFile);
        this.crs_src = CRS.parseWKT(spec);
        
        Set<ReferenceIdentifier> ids = crs_src.getIdentifiers();
        if(ids.size() > 0)
            this.crs_src_name = ids.iterator().next().toString();
        else
            this.crs_src_name = "(File "+projFile.getName()+")";
        
        postInit();
    }
    
    private void postInit() throws FactoryException, TransformException {
        // Official CRS of kml files is WGS84
        // http://www.gdal.org/ogr/drv_kml.html
        // http://spatialreference.org/ref/epsg/4326/
        this.crs_kml_name = "EPSG:4326";
        this.crs_kml = CRS.decode(crs_kml_name);
        
        this.crs_kml_x = GTRefUtil.indexOfPosX(crs_kml);
        this.crs_kml_y = GTRefUtil.indexOfPosY(crs_kml);
        
        // FYI, Google Maps uses a different CRS.
        // http://docs.codehaus.org/display/GEOTDOC/08+Google+Maps+Projection
        
        this.trans = CRS.findMathTransform(crs_src, crs_kml);
        this.inverse = trans.inverse();
    }

    /**
     * Create coordinate object in source coordinate system.
     * @return new object
     */
    public DirectPosition newSourcePoint() {
        return new DirectPosition2D(crs_src);
    }

    /**
     * Create coordinate object in KML coordinate system.
     * @return new object
     */
    public DirectPosition newKmlPoint() {
        return new DirectPosition2D(crs_kml);
    }

    /** Translate KML without allocating new objects. */
    public void toKml(DirectPosition srcPoint, DirectPosition kmlPoint) throws TransformException {
        if(trans == null)
            for(int i=0; i<srcPoint.getDimension(); i++)
                kmlPoint.setOrdinate(i, srcPoint.getOrdinate(i));
        else
            trans.transform(srcPoint, kmlPoint);
    }

    public DirectPosition toKml(DirectPosition srcPoint) throws TransformException {
        return trans.transform(srcPoint, null);
    }

    public DirectPosition toKml(double easting, double northing) throws TransformException {
        return trans.transform(new DirectPosition2D(crs_src, easting, northing), null);
    }
    
    /** Translate KML, and return as KML Coordinate object. */
    public Coordinate toKmlCoord(double easting, double northing) throws TransformException {
        if(trans == null)
            return new Coordinate(easting, northing);
        else {
            DirectPosition p = trans.transform(new DirectPosition2D(crs_src, easting, northing), null);
            return new Coordinate(p.getOrdinate(crs_kml_x), p.getOrdinate(crs_kml_y));
        }
    }

    public ReferencedEnvelope toKml(Envelope range) throws TransformException {
        if(trans == null)
            // Create new one; caller may be surprised if they get original back.
            return new ReferencedEnvelope(
                range.getMinX(), range.getMaxX(),
                range.getMinY(), range.getMaxY(),
                crs_kml);
        else {
            DirectPosition sw = toKml(range.getMinX(), range.getMinY());
            DirectPosition ne = toKml(range.getMaxX(), range.getMaxY());
            return new ReferencedEnvelope(
                sw.getOrdinate(crs_kml_x), ne.getOrdinate(crs_kml_x),
                sw.getOrdinate(crs_kml_y), ne.getOrdinate(crs_kml_y),
                crs_kml);
//            double[] sw = toKml(range.getMinX(), range.getMinY());
//            double[] ne = toKml(range.getMaxX(), range.getMaxY());
//            return new ReferencedEnvelope(sw[0], ne[0], sw[1], ne[1], crs_kml);
        }
    }
    
    /** Translate from KML back to source system without allocating new objects. */
    public void fromKml(DirectPosition kmlPoint, DirectPosition srcPoint) throws TransformException {
        if(inverse == null)
            for(int i=0; i<kmlPoint.getDimension(); i++)
                srcPoint.setOrdinate(i, kmlPoint.getOrdinate(i));
        else
            inverse.transform(kmlPoint, srcPoint);
    }
    
    /**
     * Get the value of whichever ordinate represents X (east-west).
     * 
     * @param kmlPoint
     * @return
     */
    public double getKmlX(DirectPosition kmlPoint) {
        return kmlPoint.getOrdinate(crs_kml_x);
    }
    
    /**
     * Get the value of whichever ordinate represents Y (north-south).
     * 
     * @param kmlPoint
     * @return
     */
    public double getKmlY(DirectPosition kmlPoint) {
        return kmlPoint.getOrdinate(crs_kml_y);
    }
}
