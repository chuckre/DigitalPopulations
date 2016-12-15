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
    private final ArrayList<ArrayList<Object>> tableCells;
    
    public customTableModel() {
        super();
        columns = new ArrayList<>();
        tableCells = new ArrayList<>();
    }
    
    public customTableModel(ArrayList<String> columnNames, ArrayList<ArrayList<Object>> cellValues) {
        super();
        columns = columnNames;
        tableCells = cellValues;
    }

    @Override
    public int getRowCount() {
        return tableCells.size();
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
            return tableCells.get(row).get(column);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Caught ArrayIndexOutOfBoundsException" + e.getMessage());
            return null;
        } catch(IndexOutOfBoundsException e){
            System.err.println("Caught IndexOutOfBoundsException" + e.getMessage());
            return null;
        }
    } 
    
    public void addRow(ArrayList<Object> values){
        tableCells.add(values);
        this.fireTableDataChanged();
    }
    
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

    public ArrayList<String> getColumns() {
        return columns;
    }

    public ArrayList<ArrayList<Object>> getTableCells() {
        return tableCells;
    }
    
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
