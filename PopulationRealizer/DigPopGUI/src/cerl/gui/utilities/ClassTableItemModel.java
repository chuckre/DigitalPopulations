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
public class ClassTableItemModel extends AbstractTableModel {

    private String[] columnNames = new String[]{"CSV Column Number", "Class ID", "User Defined Description"};
    
    private ArrayList<cerl.gui.utilities.Class> classes;
    
    private int tableRowCounter = 0;
    
    public ClassTableItemModel(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public ArrayList<Class> getClasses() {
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
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            cerl.gui.utilities.Class editedClass = classes.get(row);
            
            editedClass.setUserDefinedDescription((String) value);
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
