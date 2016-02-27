package mil.army.usace.ehlschlaeger.digitalpopulations.tabletools;

import java.util.List;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;

/**
 * Always returns a constant string.
 *
 * @author William R. Zwicky
 */
public class StaticGetter implements ColumnGetter {
    String value;

    /**
     * @param value that get() methods will always return
     */
    public StaticGetter(String value) {
        this.value = value;
    }
    
    public String get(String[] row) {
        return value;
    }

    public String get(List<String> row) {
        return value;
    }

    public String get(CSVTable table, int row) {
        return value;
    }
}
