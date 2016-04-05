package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import de.micromata.opengis.kml.v_2_2_0.LabelStyle;

public class FontStyle {
// can't find a way to do fonts
//    public String name;
//    public String style;
    public double scale;
    public String color;
    
    public LabelStyle toKml() {
        LabelStyle f = new LabelStyle();
        f.setScale(scale);
        f.setColor(color);
        return f;
    }
}
