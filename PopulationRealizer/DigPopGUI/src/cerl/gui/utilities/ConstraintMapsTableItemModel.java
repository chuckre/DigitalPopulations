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
public class ConstraintMapsTableItemModel extends AbstractTableModel {

    private String[] columnNames = new String[]{"File Path", "Description", "mapSelect", "pumsTraitTable", "pumsTraitField", "pumsTraitSelect"};
    
    private ArrayList<ConstraintMap> constraintMaps;
    
    private int tableRowCounter = 0;
    
    public ConstraintMapsTableItemModel(ArrayList<ConstraintMap> constraintMaps) {
        this.constraintMaps = constraintMaps;
    }

    public ArrayList<ConstraintMap> getConstraintMaps() {
        return constraintMaps;
    }
    
    public ConstraintMap getConstraintMapAt(int rowIndex, int columnIndex) {
        ConstraintMap selectedConstraintMap = constraintMaps.get(rowIndex);
        return selectedConstraintMap;
    }
    
    @Override
    public int getRowCount() {
        return constraintMaps.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object value = "??";
        ConstraintMap constraintMap = constraintMaps.get(rowIndex);
        switch (columnIndex) {
            case 0:
                value = constraintMap.getFilePath();
                break;
            case 1:
                value = constraintMap.getForbid().getDesc();
                break;
            case 2:
                value = constraintMap.getForbid().getMapSelect();
                break;
            case 3:
                value = constraintMap.getForbid().getPumsTraitTable();
                break;
            case 4:
                value = constraintMap.getForbid().getPumsTraitField();
                break;
            case 5:
                value = constraintMap.getForbid().getPumsTraitSelect();
                break;
        }
        return value;
    }
    
    @Override
    public boolean isCellEditable(int rowId, int columnId) {
        return false;//All are un editable 
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            ConstraintMap constraintMap = constraintMaps.get(row);
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
