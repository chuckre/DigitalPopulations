package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.awt.Color;

import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Carrier for info that describes the appearance of a placemark's name text
 * (what Google Earth calls "label"). Methods and fields are designed for the
 * requirements of Dp2Kml, but are generally inspired by the KML LabelStyle
 * element.
 * <P>
 * Only color and scale are supported. KML has no support for font face, bold,
 * italic, etc.
 * 
 * @author William R. Zwicky
 */
public class NameStyle {
    private double scale;
    /** Original string descriptor for color. */
    private String colorStr;
    /** Our interpretation of the string descriptor. */
    private Color colorObj;

    public NameStyle() {
        // These values imply no-data; caller should use their defaults.
        scale = 0;
        colorStr = null;
        colorObj = null;
    }

    /**
     * Size of icon, relative to whatever Google Earth considers "normal".
     */
    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public String getColor() {
        return colorStr;
    }

    public void setColor(String color) {
        this.colorStr = color;
        if(ObjectUtil.isBlank(color))
            this.colorObj = null;
        else {
            // decode now so template engine will report where the bad value is located in the template
            this.colorObj = IconStyle.interpretColor(color);
        }
    }

    /**
     * Return an instance of {@link Color} corresponding to the string given to
     * setColor().  Color is determined by our interpretColor().
     * 
     * @return
     */
    public Color getColorObj() {
        return colorObj;
    }
    
    /**
     * @return true if no member has been set
     */
    public boolean isDefault() {
        return scale == 0 && ObjectUtil.isBlank(colorStr);
    }
    
    @Override
    public String toString() {
        return String.format("%s[scale=%f, color=%s]", getClass().getSimpleName(), scale, colorStr);
    }
}
