package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.Boundable;



/**
 * Reference to a household in the database. Also keeps the bounding box of the
 * household so it can be used in a SpatialIndex.
 * <P>
 * Implementing Boundable makes it easy to use in Dp2Kml. Extending Envelope
 * directly allows it to be directly inserted into a JTS SpatialIndex.
 */
public class PointRef extends Envelope implements Boundable {
    private static final long serialVersionUID = 201101030001L;
    private String id;

    public PointRef(double lon, double lat, String id) {
        super(lon,lon, lat,lat);
        this.id = id;
    }
    
    public String getID() {
        return this.id;
    }

    public Object getBounds() {
        return this;
    }
}
