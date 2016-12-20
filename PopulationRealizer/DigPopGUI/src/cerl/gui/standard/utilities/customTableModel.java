/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * A new custom table model, as an extension of the Abstract Table Model
 * Tracks the ArrayList of column names and a 2D ArrayList of objects for cells
 * @author mrivera
 */
public class customTableModel  extends AbstractTableModel {
    private final ArrayList<String> columns;
    private final ArrayList<ArrayList<Object>> tableCells;
    
    /**
     * Creates a new, empty, customTableModel
     */
    public customTableModel() {
        super();
        columns = new ArrayList<>();
        tableCells = new ArrayList<>();
    }
    
    /**
     * Creates a new customTableModel with the provided values
     * @param columnNames - The list of column Names as a string array
     * @param cellValues - The 2D ArrayList of cell values
     */
    public customTableModel(ArrayList<String> columnNames, ArrayList<ArrayList<Object>> cellValues) {
        super();
        columns = columnNames;
        tableCells = cellValues;
    }

    /**
     * Gets the current size of the 2D arrayList of tableCells
     * @return 
     */
    @Override
    public int getRowCount() {
        return tableCells.size();
    }

    /**
     * Gets the current size of the columns ArrayList
     * @return 
     */
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

    /**
     * Gets the Object value for the provided row/column
     * If the row/column are out of bounds, catches and logs as an error
     * 
     * @param row - the row to find the value for
     * @param column - the column to find the value for
     * @return Object of the current value of the cell
     */
    @Override
    public Object getValueAt(int row, int column) {
        try{
            return tableCells.get(row).get(column);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Caught ArrayIndexOutOfBoundsException" + e.getMessage());
            return null;
        } catch(IndexOutOfBoundsException e){
            System.err.println("Caught IndexOutOfBoundsException" + e.getMessage());
            return null;
        }
    } 
    
    /**
     * Adds a new row to the 2D array of TableCells
     * @param values - an ArrayList of Objects to add to the table
     */
    public void addRow(ArrayList<Object> values){
        tableCells.add(values);
        this.fireTableDataChanged();
    }
    
    /**
     * Sets the value at the provided row/column
     * @param value - the object to set as the new value for the row/column
     * @param row - the row the cell to set is in
     * @param col - the column the cell to set is in
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        boolean updateWholeTable = false;
        while(tableCells.size() <= row) { //|| (tableCells.get(row)==null))
            tableCells.add(new ArrayList<>());
            updateWholeTable = true;
        }
        
        while (tableCells.get(row).size() <= col) { // ||  (tableCells.get(row).get(col) == null)) {
            tableCells.get(row).add(new customTableCell(value, true, "java.lang.String", false));
            updateWholeTable = true;
        } 
            
        if (tableCells.get(row).get(col).getClass().equals(customTableCell.class)) {
            ((customTableCell)tableCells.get(row).get(col)).setValue(value);
        } else {
            tableCells.get(row).set(col, value); 
        }
        
        if(updateWholeTable){
            this.fireTableDataChanged();
        } else{
            this.fireTableCellUpdated(row, col);
        }
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
        try{
            if(tableCells.get(row) == null){
                return false;
            } else if(tableCells.get(row).get(col) == null){
                return true;
            } else if (tableCells.get(row).get(col).getClass().equals(customTableCell.class)) {
                return ((customTableCell) (tableCells.get(row).get(col))).isEditable();
            } else {
                return true; 
            }
        } catch(IndexOutOfBoundsException e){
            System.err.println("Caught IndexOutOfBoundsException" + e.getMessage());
            return false;
        }
    }
    
    /**
     * Specific to the custom table Cell, Checks if a cell has an error
     * @param row
     * @param col
     * @return
     */
    public boolean isErrorInCell(int row, int col) {
        try{
            if(tableCells.get(row) == null){
                return false;
            } else if(tableCells.get(row).get(col) == null){
                return false;
            } else if (tableCells.get(row).get(col).getClass().equals(customTableCell.class)) {
                return ((customTableCell) (tableCells.get(row).get(col))).isError();
            } else {
                return false;
            }
        } catch(IndexOutOfBoundsException e){
            System.err.println("Caught IndexOutOfBoundsException" + e.getMessage());
            return false;
        }
    }

    /**
     * Gets an ArrayList of strings for all the column names
     * @return list of column names
     */
    public ArrayList<String> getColumns() {
        return columns;
    }

    /**
     * Gets the full 2D ArrayList of table cells
     * @return all table cell objects in a 2D ArrayList
     */
    public ArrayList<ArrayList<Object>> getTableCells() {
        return tableCells;
    }
    
    /**
     * Gets the full 2D ArrayList of table cells, cast as customTableCells
     * @return all table cell objects in a 2D ArrayList cast as customTableCells
     */
    public ArrayList<ArrayList<customTableCell>> getCustomTableCells() {
        ArrayList<ArrayList<customTableCell>> val = new ArrayList<>();
        
        for(int r=0;r<tableCells.size(); r++){
            val.add(new ArrayList<>());
            for(int c=0;c<tableCells.get(r).size(); c++){
                val.get(r).add((customTableCell)tableCells.get(r).get(c));
            }
        }
        return val;
        //return tableCells;
    }

    /**
     * Called from the customTableModelListener when the table is changed
     * @param row - The first row that was changed
     * @param col - The column that changed
     */
    public void handleTableChange(int row, int col) {
        //System.out.println("Handling table change for row:" + row + "col: " + col + " size: " + tableCells.size());
        
        //validate data types
        if((tableCells.size() <= row) || (row < 0)){
            System.out.println("Row is too big:" + row);
            return;
        } else if((tableCells.get(row).size() <= col) || (col < 0)){
            System.out.println("Col is too big:" + col);
            return;
        }
        if (tableCells.get(row).get(col).getClass().equals(customTableCell.class)) {
            Object value  = ((customTableCell) (tableCells.get(row).get(col))).getValue();
            String allowedType =  ((customTableCell) (tableCells.get(row).get(col))).getAllowedDataType();
            
            if((value.getClass().toString() != null) && (allowedType != null) && (!value.getClass().toString().equals(allowedType))){
                if(allowedType.contains("Double")){
                    try{
                        double d = Double.parseDouble(value.toString());
                        ((customTableCell) (tableCells.get(row).get(col))).setError(false);
                    } catch(NumberFormatException e){
                        ((customTableCell) (tableCells.get(row).get(col))).setError(true);
                    }
                }
                else if(allowedType.contains("Integer")){
                    try{
                        int i = Integer.parseInt(value.toString());
                        ((customTableCell) (tableCells.get(row).get(col))).setError(false);
                    } catch(NumberFormatException e){
                        ((customTableCell) (tableCells.get(row).get(col))).setError(true);
                    }
                }
            }   
        }            
    }
}
