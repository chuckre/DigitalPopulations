package mil.army.usace.ehlschlaeger.digitalpopulations.tabletools;

import java.util.List;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;



/**
 * Extract a field from a table. This interface is useful when column access
 * might be more complex than simply reading a named column from a CSV file.
 * CleanGetter does just that, but FormatGetter can assemble multiple fields
 * into a single result.
 * <P>
 * Three kinds of tables need to be accessed (array, List, CSVTable). Any
 * processing can occur during extraction. <b>null</b> should be returned if
 * class wishes to indicate field has no value.
 * 
 * @author William R. Zwicky
 */
public interface ColumnGetter {
    /**
     * Compute "the value" from the fields in a row.
     * 
     * @param row one line of fields, as a simple array
     * @return the value
     */
    public String get(String[] row);

    /**
     * Compute "the value" from the fields in a row.
     * 
     * @param row one line of fields, as a List of String
     * @return the value
     */
    public String get(List<String> row);

    /**
     * Compute "the value" from the fields in a row.
     * 
     * @param table matrix of values
     * @param row index of row to access
     * @return the value
     */
    public String get(CSVTable table, int row);
}
