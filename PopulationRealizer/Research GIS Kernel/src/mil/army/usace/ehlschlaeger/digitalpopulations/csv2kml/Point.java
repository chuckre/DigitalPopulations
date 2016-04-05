package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.Boundable;



/**
 * One element to be stored in our spatial index. Carefully crafted to hold down
 * memory use; some projects contain north of 3 million points.
 * <P>
 * Use of create() methods ensures returned object contains only the given
 * fields, and doesn't waste space on nulls. Note that this means if an object
 * is created without a field, that field cannot be set later.
 */
public class Point implements Boundable {
    private double x, y;

    public Point() {
    }

    public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    /**
     * Short name, visible with mouse hovers over point.
     * @return
     */
    public String getName() {
        return null;
    }

    public void setName(String name) {
        throw new IllegalStateException("Point was created without a name field.");
    }

    /**
     * Long HTML text, visible in pop-up bubble when user clicks on point.
     * @return
     */
    public String getDesc() {
        return null;
    }

    public void setDesc(String desc) {
        throw new IllegalStateException("Point was created without a desc field.");
    }

    /**
     * Set the appearance of the point's icon when displayed in Google Earth.
     */
    public void setIconStyle(IconStyle iconStyle) {
        throw new IllegalStateException("Point was created without style support.");
    }

    public IconStyle getIconStyle() {
        return null;
    }

    /**
     * Set the appearance of the point's name (label) when displayed in Google
     * Earth.
     */
    public void setNameStyle(NameStyle nameStyle) {
        throw new IllegalStateException("Point was created without style support.");
    }

    public NameStyle getNameStyle() {
        return null;
    }

    /**
     * Create a point object that only supports given fields. To conserve
     * memory, no other fields are available.
     * 
     * @param x
     *            position of point's easting or longitude
     * @param y
     *            position of point's northing or latitude
     * @return new Point object
     */
    public static Point create(double x, double y) {
        return new Point(x, y);
    }

    /**
     * Create a point object that only supports given fields. To conserve
     * memory, no other fields are available. If name is null, resulting Point
     * will NOT contain a name field.
     * 
     * @param x
     *            position of point's easting or longitude
     * @param y
     *            position of point's northing or latitude
     * @param name
     *            point's name string. In Google Earth, this is displayed
     *            directly on the map.
     * @return new Point object
     */
    public static Point create(double x, double y, String name) {
        if (name == null)
            return new Point(x, y);
        else
            return new Point3(x, y, name);
    }

    /**
     * Create a point object that only supports given fields. To conserve
     * memory, no other fields are available. If desc is null, resulting Point
     * will NOT contain a desc field. If name is also null, result will NOT
     * contain either name or desc.
     * <P>
     * If desc is not null, result will support all fields.
     * 
     * @param x
     *            position of point's easting or longitude
     * @param y
     *            position of point's northing or latitude
     * @param name
     *            point's name string. In Google Earth, this is displayed
     *            directly on the map.
     * @param desc
     *            point's description text. In Google Earth, this will be
     *            displayed in a pop-up bubble when point is clicked.
     * @return new Point object
     */
    public static Point create(double x, double y, String name, String desc) {
        if (desc == null)
            return create(x, y, name);
        else
            return new Point4(x, y, name, desc);
    }
    
    /**
     * Create a Point object that supports all fields.
     * 
     * @param x
     *            position of point's easting or longitude
     * @param y
     *            position of point's northing or latitude
     * @param name
     *            point's name string. In Google Earth, this is displayed
     *            directly on the map.
     * @param desc
     *            point's description text. In Google Earth, this will be
     *            displayed in a pop-up bubble when point is clicked.
     * @return new Point object
     */
    public static Point createFull(double x, double y, String name, String desc) {
        return new Point4(x, y, name, desc);
    }

    public Envelope getBounds() {
        return new Envelope(getX(), getX(), getY(), getY());
    }

    @Override
    public String toString() {
        return String.format("Point[at (%f,%f)]", getX(), getY());
    }
}



/**
 * Point object with only x, y, and name fields.
 * <P>
 * Not public; should be created via Point.create(). Not nested inside Point so
 * we don't waste memory on a pointer to a Point.
 * 
 * @author William R. Zwicky
 */
class Point3 extends Point {
    private String name;

    public Point3() {
    }

    public Point3(double x, double y, String name) {
        super(x, y);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Point[\"%s\" at (%f,%f)]", name, getX(), getY());
    }
}



/**
 * Point object with all the fields we can use. Supports x, y, name, desc, and
 * styles.
 * <P>
 * Not public; should be created via Point.create(). Not nested inside Point so
 * we don't waste memory on a pointer to a Point.
 * 
 * @author William R. Zwicky
 */
class Point4 extends Point3 {
    private String desc;
    private IconStyle iconStyle;
    private NameStyle nameStyle;

    public Point4() {
    }

    public Point4(double x, double y, String name, String desc) {
        super(x, y, name);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setIconStyle(IconStyle iconStyle) {
        this.iconStyle = iconStyle;
    }

    public IconStyle getIconStyle() {
        return iconStyle;
    }

    public void setNameStyle(NameStyle nameStyle) {
        this.nameStyle = nameStyle;
    }

    public NameStyle getNameStyle() {
        return nameStyle;
    }
}
