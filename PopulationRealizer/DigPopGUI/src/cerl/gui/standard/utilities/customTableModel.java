/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author mrivera
 */
public class customTableModel  extends AbstractTableModel {
    private final ArrayList<String> columns;
    private final Object[][] tableCells;
    
    public customTableModel() {
        super();
        columns = new ArrayList<>();
        //default 2 x 2 table.
        tableCells = new Object[2][2];
    }
    
    public customTableModel(ArrayList<String> columnNames, Object[][] cellValues) {
        super();
        columns = columnNames;
        tableCells = cellValues;
    }

    @Override
    public int getRowCount() {
        return tableCells.length;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }
    
    /**
     * Gets the column header name for a specified column index.
     *
     * @param col The index of the column to retrieve the header name
     * @return
     */
    @Override
    public String getColumnName(int col) {
        return columns.get(col);
    }

    @Override
    public Object getValueAt(int row, int column) {
        try{
            return tableCells[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Caught ArrayIndexOutOfBoundsException" + e.getMessage());
            return null;
        }
    } 
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (tableCells[row][col] == null) {
            tableCells[row][col] = new customTableCell(value, true); 
        } else if (tableCells[row][col].getClass().equals(customTableCell.class)) {
            ((customTableCell) (tableCells[row][col])).setValue(value);
        } else {
            tableCells[row][col] = value;
        }
        
        this.fireTableCellUpdated(row, col);
    }
    
    /**
     * *
     * Allows users to only edit the interior cells Note: Will need logic for
     * editing computed values versus non-computed.
     *
     * @param row
     * @param col
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        if (tableCells[row][col] == null) {
            return true;
        } else if (tableCells[row][col].getClass().equals(customTableCell.class)) {
            return ((customTableCell) (tableCells[row][col])).isEditable();
        } else {
            return true; 
        }
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public Object[][] getTableCells() {
        return tableCells;
    }
}
