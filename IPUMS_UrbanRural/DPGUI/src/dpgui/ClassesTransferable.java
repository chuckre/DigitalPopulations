package dpgui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JTable;

/**
 * Drag-and-drop behavior for a list of land-use classes.
 * This is distinct from ClassesTransferData in case we want to be able to
 * convert to other "flavors" of data (i.e. a string that can be pasted into
 * a text editor.)
 *
 * @author William R. Zwicky
 */
public class ClassesTransferable implements Transferable {
    /** Collection of stuff being dragged. */
    protected ClassesTransferData data;

    /**
     * Create a new transferable by copying all of the selected rows from
     * a JTable.
     *
     * @param table component to copy from
     * @param group group number from which these classes came, or
     *     NO_GROUP or VACANT_GROUP.
     * @return new Transferable with the values
     */
    public static ClassesTransferable getSelected(JTable table, int group) {
        return new ClassesTransferable(ClassesTransferData.getSelected(table, group));
    }

    public ClassesTransferable(ClassesTransferData data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { ClassesTransferData.FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return ClassesTransferData.FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(!isDataFlavorSupported(flavor))
            throw new UnsupportedFlavorException(flavor);
        return data;
    }
}
