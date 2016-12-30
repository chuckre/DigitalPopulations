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
 * The land use combination table model for the table used in Step 2
 * @author ajohnson
 */
public class LandUseCombinationTableItemModel extends AbstractTableModel {

    private final String[] columnNames = new String[]{"Class", "Target", "Description"};
    private final ArrayList<LandUseMapClassCombination> classes;
    private final int tableRowCounter = 0;
    
    /**
     * Creates a new Land Use Combination Table Model
     * @param classes - the new list of LandUseMapClassCombinations
     */
    public LandUseCombinationTableItemModel(ArrayList<LandUseMapClassCombination> classes) {
        this.classes = classes;
    }

    /**
     * Gets the list of land use map class combinations
     * @return - the list of classes
     */
    public ArrayList<LandUseMapClassCombination> getClasses() {
        return classes;
    }
    
    /**
     * Gets the number of rows as the size of the classes array
     * @return 
     */
    @Override
    public int getRowCount() {
        return classes.size();
    }
    
    /**
     * Gets the number of columns, as the length of the columnNames array
     * @return 
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets the value in the table for a specific cell
     * @param rowIndex - the row of the cell to get the value for
     * @param columnIndex - the column of the cell to get the value for
     * @return the object value of the cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object value = "??";
        LandUseMapClassCombination selectedClass = classes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                value = selectedClass.getClasses().toString();
                break;
            case 1:
                value = selectedClass.getTarget();
                break;
            case 2:
                value = selectedClass.getClassCombinationDescription();
                break;
        }
        return value;
    }
    
    /**
     * Gets the status of the editable flag for a specific cell
     * @param rowId - the row the cell to check is in
     * @param columnId - the column the cell to check is in
     * @return the value of the editable flag, true if editable, false if not
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
                value = false;
                break;
        }
        
        return value;
    }
    
    /**
     * Sets the value at a specific cell to the provided value
     * @param value - the new value of the cell
     * @param row - the row the cell is located in
     * @param col - the column the cell is located in
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            LandUseMapClassCombination landUseMapClassCombination = classes.get(row);
            
            
        }
    }
    
    /**
     * Gets the column name for a specific column found by index
     * @param columnIndex - the ID of the column to find the name for
     * @return - the column name of the specified column index
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Gets the listeners associated with the current table model
     * @return 
     */
    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
