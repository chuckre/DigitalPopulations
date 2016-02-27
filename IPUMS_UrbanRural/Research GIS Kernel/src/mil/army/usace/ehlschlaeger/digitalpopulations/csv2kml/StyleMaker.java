package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.VelocityGetter;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;

/**
 * Generates style information using an Apache Velocity template. Note the text
 * produced by the template is useless; only our specific style variables are
 * used.
 * 
 * @author William R. Zwicky
 */
public class StyleMaker extends VelocityGetter {
    protected IconStyle icon;
    protected NameStyle label;
    
    public StyleMaker(List<String> schema, java.io.File templateFile, String varName)
            throws Exception {
        super(schema, templateFile, varName);
    }

    public IconStyle getCurrentIconStyle() {
        return icon;
    }
    
    public NameStyle getCurrentLabelStyle() {
        return label;
    }
    
    /**
     * Evaluate template relative to a row of data. Computed text is returned;
     * getCurrentIconStyle() returns the style data.
     */
    @Override
    public String get(CSVTable table, int row) {
        icon = new IconStyle();
        label = new NameStyle();
        setScriptVar("icon", icon);
        setScriptVar("label", label);
        String s = super.get(table, row);
        if(icon.isDefault())
            icon = null;
        if(label.isDefault())
            label = null;
        return s;
    }
    
    /**
     * Evaluate template relative to a row of data. Computed text is returned;
     * getCurrentIconStyle() returns the style data.
     */
    @Override
    public String get(String[] row) {
        icon = new IconStyle();
        label = new NameStyle();
        setScriptVar("icon", icon);
        setScriptVar("label", label);
        String s = super.get(row);
        if(icon.isDefault())
            icon = null;
        if(label.isDefault())
            label = null;
        return s;
    }
    
    /**
     * Evaluate template relative to a row of data. Computed text is returned;
     * getCurrentIconStyle() returns the style data.
     */
    @Override
    public String get(List<String> row) {
        icon = new IconStyle();
        label = new NameStyle();
        setScriptVar("icon", icon);
        setScriptVar("label", label);
        String s = super.get(row);
        if(icon.isDefault())
            icon = null;
        if(label.isDefault())
            label = null;
        return s;
    }
}
