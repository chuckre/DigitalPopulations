package dpgui.model;

import javax.swing.table.AbstractTableModel;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Landuse;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.LanduseCombination;


/**
 * Present as a table the land-use group names.
 *
 * @author William R. Zwicky
 */
public class LanduseGroupsAdaptor extends AbstractTableModel {
    public static String[] COLUMNS = {
        "Class", "Name"
    };

    /** Current class groups in project. */
    protected Landuse landuse;
    /** Maps land-use codes to names. */
    protected LanduseDictionary dicto = new LanduseDictionary();

    public LanduseGroupsAdaptor() {
        this.landuse = new Landuse();
    }

    public LanduseGroupsAdaptor(Landuse landuse) {
        this.landuse = landuse;
        landuse.sortCombos();
    }

    /**
     * Change object that we're viewing.
     * @param newLanduse
     */
    public void setData(Landuse newLanduse) {
        this.landuse = newLanduse;
        landuse.sortCombos();
        fireTableDataChanged();
    }

    /**
     * Change our class number/name mapping object.  All future use updates
     * will be stored to this object.
     *
     * @param classNames
     */
    public void setDictionary(LanduseDictionary classNames) {
        this.dicto = classNames;
        fireTableDataChanged();
    }

    public int getRowCount() {
        return 1 + landuse.combos.size();
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMNS[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex==0 ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 'vacant' class number cannot be edited
        return !(rowIndex==0 && columnIndex==0);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex == 0)
            return columnIndex==0 ? null : landuse.vacant.desc;
        else {
            LanduseCombination c = landuse.combos.get(rowIndex - 1);
            return columnIndex==0 ? c.target : c.desc;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if(rowIndex == 0 && columnIndex == 1)
            landuse.vacant.desc = (String) value;
        else {
            LanduseCombination c = landuse.combos.get(rowIndex - 1);
            if(columnIndex == 0)
                c.target = (Integer)value;
            else
                c.desc = (String) value;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
