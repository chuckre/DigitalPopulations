package dpgui;

import java.awt.datatransfer.DataFlavor;
import javax.swing.JTable;

/**
 * Drag-and-drop container for a list of land-use classes.
 *
 * @author William R. Zwicky
 */
public class ClassesTransferData{
    /** Custom data flavor: an array of rows, each row is array of string. */
    public static final DataFlavor FLAVOR = new DataFlavor(ClassesTransferData.class, "Land-Use Class List");

    /** Marker for 'group' indicating classes are not in any group. */
    public static final int NO_GROUP = -2;
    /** Marker for 'group' indicating classes came from 'vacant classes' list. */
    public static final int VACANT_GROUP = -1;

    /** GUI component this data was dragged from.  (Not avaiable to TransferHandler for some reason.) */
    public Object source;
    /** Class group these classes were dragged from. */
    public int group;
    /** Copy of values being dragged. cells[row][0] is Integer class, and cells[row][1] is String name. */
    public Object[][] cells;


    public ClassesTransferData() {
    }

    /**
     * Copy all of the selected rows from a JTable.
     *
     * @param table component to copy from
     * @param group group number from which these classes came, or
     *     NO_GROUP or VACANT_GROUP.
     * @return new Transferable with the values
     */
    public static ClassesTransferData getSelected(JTable table, int group) {
        ClassesTransferData data = new ClassesTransferData();
        data.source = table;
        data.group = group;
        data.cells = new Object[table.getSelectedRowCount()][table.getColumnCount()];

        for(int r=0; r<table.getSelectedRowCount(); r++)
            for(int c=0; c<data.cells[0].length; c++)
                data.cells[r][c] = table.getValueAt(table.getSelectedRows()[r], c);

        return data;
    }
}
