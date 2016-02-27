package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.awt.Color;

import mil.army.usace.ehlschlaeger.rgik.util.HTMLColors;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Carrier for info that describes the appearance of a placemark. Methods and
 * fields are designed for the requirements of Dp2Kml, but are generally
 * inspired by the KML IconStyle element.
 * 
 * @author William R. Zwicky
 */
public class IconStyle {
    private String img;
    private double scale;
    /** Original string descriptor for color. */
    private String colorStr;
    /** Our interpretation of the string descriptor. */
    private Color colorObj;


    /**
     * Create blank instance (with nulls and zeros).
     */
    public IconStyle() {
        // These values imply no-data; caller should use their defaults.
        img = null;
        scale = 0;
        colorStr = null;
        colorObj = null;
    }

    /**
     * Create filled instance.
     * 
     * @param image icon URL
     * @param scale relative size
     * @param color color code
     */
    public IconStyle(String image, double scale, String color) {
        setImg(image);
        setScale(scale);
        setColor(color);
    }
    
    public String getImg() {
        return img;
    }

    /**
     * URL for icon file. Generally should be relative to the contents of the
     * KMZ file.
     * 
     * @return
     */
    public void setImg(String img) {
        this.img = img;
    }

    public double getScale() {
        return scale;
    }

    /**
     * Size of icon, relative to whatever Google Earth considers "normal".
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    public String getColor() {
        return colorStr;
    }

    /**
     * Color of icon.
     * 
     * @param color any code string that is accepted by interpretColor().
     */
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
        return ObjectUtil.isBlank(img) && scale == 0 && ObjectUtil.isBlank(colorStr);
    }
    
    public void installIcons(KmlWriter kmlWriter) {
        // copy all the font files into the kmz
//TODO setImg(File) here or elsewhere, maybe KmlWriter, generates relative url for use here, and queues file to be added to KMZ
// Map<String,File> iconFiles;  //map<url,user_path>
    }
    
    @Override
    public String toString() {
        return String.format("%s[img=%s, scale=%f, color=%s]", getClass().getSimpleName(), img, scale, colorStr);
    }

    /**
     * Convert a description of a color into a Color object. Supports:
     * <OL>
     * <LI>KML color code as eight hex digits: aabbggrr
     * <LI>HTML CSS short form: #rgb
     * <LI>HTML form: #rrggbb
     * <LI>HTML color name
     * </OL>
     * Note the options are tried in the order above, so this method cannot
     * decode an color whose name is 8 characters long and composed entirely of
     * the letters [ABCDEF].
     * 
     * @param text
     *            string to decode
     * @return corresponding color
     * @throws IllegalArgumentException
     * 
     * @see http://en.wikipedia.org/wiki/Web_colors#X11_color_names
     */
    public static Color interpretColor(String text) {
        Color c = null;
        text = text.trim().toLowerCase();
        
        // Bare number.
        if(text.length() == 8) {
            try {
                // KML form: aabbggrr
                int v = Integer.parseInt(text, 16);
                int r = v & 0xFF;
                int g = v & 0xFF00;
                int b = v & 0xFF0000;
                int a = v & 0xFF000000;
                c = new Color(r, g, b, a);
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }

        // HTML forms.
        if(c == null) {
            if(text.startsWith("#")) {
                if(text.length() == 4) {
                    // CSS short form: #rgb
                    int r = Character.digit(text.charAt(1), 16);
                    r = r<<4 + r;
                    int g = Character.digit(text.charAt(2), 16);
                    g = g<<4 + g;
                    int b = Character.digit(text.charAt(3), 16);
                    b = g<<4 + g;
                    int a = 0xFF;
                    return new Color(r, g, b, a);
                }
                else if(text.length() == 7) {
                    // HTML form: #rrggbb
                    int r = Integer.parseInt(text.substring(1, 3), 16);
                    int g = Integer.parseInt(text.substring(3, 5), 16);
                    int b = Integer.parseInt(text.substring(5, 7), 16);
                    int a = 0xFF;
                    return new Color(r, g, b, a);
                }
                else
                    throw new IllegalArgumentException("Invalid HTML color code - must have precisely 3 or 6 digits: "+text);
            }
            else
                c = HTMLColors.getColor(text);
        }

        // Nothing worked.
        if(c == null)
            throw new IllegalArgumentException("Could not interpret as a color: "+text);
        
        return c;
    }
}
