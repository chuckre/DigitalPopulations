/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * A custom table model used for Census Class User Definitions
 * @author ajohnson
 */
public class ClassTableItemModel extends AbstractTableModel {

    private final String[] columnNames = new String[]{"CSV Column Number", "Class ID", "User Defined Description"};
    private final ArrayList<cerl.gui.utilities.Class> classes;
    private final int tableRowCounter = 0;
    
    /**
     * Creates a new ClassTableItemModel from an ArrayList of Classes
     * @param classes 
     */
    public ClassTableItemModel(ArrayList<Class> classes) {
        this.classes = classes;
    }

    /**
     * Gets the current array list of classes
     * @return all classes in the current table model
     */
    public ArrayList<Class> getClasses() {
        return classes;
    }
    
    /**
     * Gets the number of rows, as the size of the classes ArrayList
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return classes.size();
    }
    
    /**
     * Gets the current number of columns, as the size of the ColumnNames array
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets the cell value at the specified row/column indices
     * @param rowIndex - the index of the row the cell is located
     * @param columnIndex - the index of the column the cell is located
     * @return - the cell value
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object value = "??";
        cerl.gui.utilities.Class selectedClass = classes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                //value = selectedClass.isSelected();
                if(selectedClass.isSelected()){
                    value = selectedClass.getColumnNumber();
                }else{
                    value = null;
                }
                break;
            case 1:
                value = selectedClass.getClassName();
                break;
            case 2:
                value = selectedClass.getUserDefinedDescription();
                break;
        }
        return value;
    }
    
    /**
     * Checks if a specific cell is editable
     * @param rowId - the Row the cell is located
     * @param columnId - the Column the cell is located
     * @return - true if the cell is editable, false if not
     */
    @Override
    public boolean isCellEditable(int rowId, int columnId) {
        
        boolean value = false;
        
        switch (columnId) {
            case 0:
                value = false;
                break;
            case 1:
                value = false;
                break;
            case 2:
                value = true;
                break;
        }
        
        return value;
    }
    
    /**
     * Sets the value at the current row/column as the provided object value
     * @param value - the new cell value
     * @param row - the row the cell is located
     * @param col - the column the cell is located
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            cerl.gui.utilities.Class editedClass = classes.get(row);
            
            editedClass.setUserDefinedDescription((String) value);
        }
    }
    
    /**
     * Gets the current column name at the specified column index
     * @param columnIndex - the column ID to get the name of
     * @return - the string value of the column name at the specified index
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Gets the table model listeners
     * @return 
     */
    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
