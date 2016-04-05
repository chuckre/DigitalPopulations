package dpgui.swing;

import java.awt.Component;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor that makes multiple combo boxes available depending on the value
 * in another column.
 *
 * @author William R. Zwicky
 */
public class MultiOptionCellEditor extends AbstractCellEditor implements TableCellEditor {
    protected final int typeCol;
    protected Map<Object, JComboBox> editors;
    protected JComboBox currentEditor;

    /**
     * Simple constructor.
     *
     * @param typeColumn column that dictates which option list we'll present
     */
    public MultiOptionCellEditor(int typeColumn) {
        this.typeCol = typeColumn;
        this.editors = new HashMap<Object, JComboBox>();
    }

    /**
     * Complete constructor.
     *
     * @param typeColumn column that dictates which option list we'll present
     * @param editors collection of properly configured editors
     */
    public MultiOptionCellEditor(int typeColumn, Map<Object, JComboBox> editors) {
        this.typeCol = typeColumn;
        this.editors = editors;
    }

    /**
     * Replace the options associated with a key with a new set.
     *
     * @param editorMap
     * @param typeKey
     * @param editorValues
     */
    public void setOptions(Object typeKey, String[] editorValues) {
        JComboBox box = editors.get(typeKey);
        if (box == null) {
            box = new JComboBox();
            box.setEditable(true);
            box.setMaximumRowCount(30);
            editors.put(typeKey, box);
        }
        SwingUtils.replaceContents(box, editorValues);
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editor
     */
    @Override
    public Object getCellEditorValue() {
        if(currentEditor != null)
            return currentEditor.getSelectedItem();
        else
            return null;
    }

    /**
     * Returns true.  Cell is always editable.
     *
     * @param e  an event object
     * @return true
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Object type = table.getValueAt(row, typeCol);
        currentEditor = editors.get(type);
        if(currentEditor != null)
            currentEditor.setSelectedItem(value);
        return currentEditor;
    }
}
