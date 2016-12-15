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
 *
 * @author ajohnson
 */
public class LandUseCombinationTableItemModel extends AbstractTableModel {

    private String[] columnNames = new String[]{"Class", "Target", "Description"};
    
    private ArrayList<LandUseMapClassCombination> classes;
    
    private int tableRowCounter = 0;
    
    public LandUseCombinationTableItemModel(ArrayList<LandUseMapClassCombination> classes) {
        this.classes = classes;
    }

    public ArrayList<LandUseMapClassCombination> getClasses() {
        return classes;
    }
    
    @Override
    public int getRowCount() {
        return classes.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
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
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            LandUseMapClassCombination landUseMapClassCombination = classes.get(row);
            
            
        }
    }
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
