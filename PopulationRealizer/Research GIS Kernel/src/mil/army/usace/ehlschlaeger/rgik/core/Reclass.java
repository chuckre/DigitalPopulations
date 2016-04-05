package mil.army.usace.ehlschlaeger.rgik.core;

import java.util.HashMap;
import java.util.Map;



/**
 * Reclass reclassifies one GISClass map into another based on two columns in a
 * CSVTable. Reclass scans all the values in the map, and if any value appears
 * in the table column named by 'keyColumn', it will be replaced by the value
 * from that row of the table, in the column named by 'newColumn'.
 * <p>
 * A copy of the original map will be produced; the original map will not be
 * changed.
 * <p>
 * This class can be used in two ways: The static <code>reclass</code> method
 * will perform the job on the spot, or an instance of this class can be used as
 * a JavaBean which will automatically call <code>reclass</code> when all
 * required parameters have been set.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class Reclass extends RGIS implements RGISFunction {
    private GISClass oldMap, newMap;
    private CSVTable table;
    private String   keyColumn, newColumn;

    public Reclass() {
        super();
        oldMap = null;
        newMap = null;
        table = null;
        keyColumn = null;
        newColumn = null;
    }

    /**
     * @param originalMap
     *            the map that will be processed.
     */
    public void setOriginalMap(GISClass originalMap) {
        oldMap = originalMap;
        checkRun();
    }

    public GISClass getOriginalMap() {
        return oldMap;
    }

    /**
     * @param controlTable
     *            the table which specifies the values to match, and the new
     *            values to substitute.
     */
    public void setReclassTable(CSVTable controlTable) {
        this.table = controlTable;
        checkRun();
    }

    public CSVTable getReclassTable() {
        return table;
    }

    /**
     * @param columnName
     *            the name of the column in the control table that contains the
     *            values to match.
     */
    public void setKeyColumn(String columnName) {
        keyColumn = columnName;
        checkRun();
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    /**
     * @param columnName
     *            the name of the column in the control table that contains the
     *            new values to substitute.
     */
    public void setNewColumn(String columnName) {
        newColumn = columnName;
        checkRun();
    }

    public String getNewColumn() {
        return newColumn;
    }

    /**
     * Perform the replacement if all parameters have been set.
     * Call <code>getReclassMap()</code> to fetch the result.
     */
    private void checkRun() {
        if (oldMap != null && table != null && keyColumn != null
                && newColumn != null) {
            newMap = reclass(oldMap, table, keyColumn, newColumn);
        }
    }

    /**
     * @return a new map containing the results of the last run, or null if
     *         Reclass hasn't been run yet.
     */
    public GISClass getReclassMap() {
        return newMap;
    }
    
    /**
     * Reclassify one GISClass map into another based on two columns
     * in a CSVTable. Reclass scans all the values in the map, and if any value
     * appears in the table column named by 'keyColumn', it will be replaced by
     * the value from that row of the table, in the column named by 'newColumn'.
     * 
     * @param oldMap
     *            the map that will be processed
     * @param controlTable
     *            the table which specifies the values to match, and the new
     *            values to substitute
     * @param keyColumn
     *            the name of the column in the control table that contains the
     *            values to match
     * @param newColumn
     *            the name of the column in the control table that contains the
     *            new values to substitute
     * @return a new map containing a copy of oldMap, but with the key values
     *         replaced
     */
    public static GISClass reclass(GISClass oldMap, CSVTable controlTable,
            String keyColumn, String newColumn) {
        assert oldMap != null;
        assert controlTable != null;
        assert keyColumn != null;
        assert newColumn != null;

        int keyC = controlTable.findColumn(keyColumn);
        int newC = controlTable.findColumn(newColumn);

        // Build a map from old values to new values.
        // 'keyColumn' contains the old values caller wants to change;
        // 'newColumn' contains the corresponding new values.
        Map<Integer, Integer> valueMap = new HashMap<Integer, Integer>();
        for (int r = 0; r < controlTable.getRowCount(); r++) {
            // get key
            String s = controlTable.getStringAt(r, keyC);
            int keyValue = Integer.parseInt(s.trim());
            // get corresponding new value
            s = controlTable.getStringAt(r, newC);
            int reclassValue = Integer.parseInt(s.trim());
            // Java autoboxes ints into Integer objects
            valueMap.put(keyValue, reclassValue);
        }

        GISClass newMap = reclass(oldMap, valueMap);
        
        return newMap;
    }

    /**
     * Reclassify one GISClass map into another based on a number mapping
     * structure.  Reclass scans all the values in the map, and if any value
     * appears in the mapping's set of keys, it will be replaced by that
     * key's value.
     * 
     * @param oldMap
     *            the map that will be processed
     * @param valueMap
     *            mapping from old values to new values
     * @return a new map containing a copy of oldMap, but with the key values
     *         replaced
     */
    public static GISClass reclass(GISClass oldMap, Map<Integer, Integer> valueMap) {
        // Construct *blank* map, same size as source.
        GISClass newMap = new GISClass((GISGrid)oldMap);
        for (int r = oldMap.getNumberRows() - 1; r >= 0; r--) {
            for (int c = oldMap.getNumberColumns() - 1; c >= 0; c--) {
                if (oldMap.isNoData(r, c) == false) {
                    int oldval = oldMap.getCellValue(r, c);
                    Integer newval = valueMap.get(oldval);
                    if (newval != null)
                        newMap.setCellValue(r, c, newval.intValue());
                }
            }
        }
        return newMap;
    }

    /**
     * "Reclassify" a GISClass map into a GISLattice based on a number mapping
     * structure.  GISClass only supports ints, so the returned object is a GISLattice.
     * 
     * @param oldMap
     *            the map that will be processed
     * @param valueMap
     *            mapping from old values to new values
     * @param defalt
     *            if not null and a cell's value is not found in the map, it
     *            will be replace with this value.  if null, the cell is not
     *            modified.
     * @return a new map containing a copy of oldMap, but with the key values
     *         replaced
     */
    public static GISLattice reclass(GISClass oldMap, Map<Integer, Double> valueMap, Double defalt) {
        // Construct *blank* map, same size as source.
        GISLattice newMap = new GISLattice((GISGrid)oldMap);
        for (int r = oldMap.getNumberRows() - 1; r >= 0; r--) {
            for (int c = oldMap.getNumberColumns() - 1; c >= 0; c--) {
                if (oldMap.isNoData(r, c) == false) {
                    int oldval = oldMap.getCellValue(r, c);
                    Double newval = valueMap.get(oldval);
                    if (newval != null)
                        newMap.setCellValue(r, c, newval.doubleValue());
                    else if(defalt != null)
                        newMap.setCellValue(r, c, defalt.doubleValue());
                }
            }
        }
        return newMap;
    }
}
