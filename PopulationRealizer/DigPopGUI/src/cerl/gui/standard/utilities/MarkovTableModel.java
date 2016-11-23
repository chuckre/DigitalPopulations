/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import cerl.gui.utilities.MarkovTableCell;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.table.AbstractTableModel;
import java.util.*;
import javax.swing.JButton;

/**
 * The table model for the Markov Chain Matrix tables.
 *
 * @author mrivera
 */
public class MarkovTableModel extends AbstractTableModel {

    private ArrayList<String> columns;
    private Object[][] markovTable;
    private final int PROPORTION_COLUMN = 1;
    private final int PROPORTION_ROW = 0;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");
    
    /**
     * *
     * Creates a new, blank Markov Table Model
     */
    public MarkovTableModel() {
        super();
        columns = new ArrayList<>();
        //columns must be rows+1 because the header row is the -1th row.
        markovTable = new Object[PROPORTION_ROW+1][PROPORTION_COLUMN+1];
    }

    /**
     * Creates a Markov Matrix with an existing set of columns and cells
     *
     * @param columnNames The names of the columns
     * @param cellValues The 2D matrix of cell values
     */
    public MarkovTableModel(ArrayList<String> columnNames, Object[][] cellValues) {
        super();
        columns = columnNames;
        markovTable = cellValues;
    }

    /**
     * Clears the data from cells in the grid
     *
     * @param startRow The first row to start clearing
     * @param endRow The last row to clear
     * @param startCol The first column to start clearing
     * @param endCol The last column to clear
     */
    public void clear(int startRow, int endRow, int startCol, int endCol) {
        //Removes all data entered into the grid so far
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                this.setValueAt("", r, c);
                this.fireTableCellUpdated(r, c);
            }
        }
    }

    /**
     * Clears the data from all editable cells in the specified row
     *
     * @param row The offset of which column to start clearing
     */
    public void clearRow(int row) {
        //Removes all data entered into the specified row
        for (int c = 0; c < markovTable[row].length; c++) {
            if (isCellEditable(row, c)) {
                this.setValueAt("", row, c);
                this.fireTableCellUpdated(row, c);
            }
        }
    }

    /**
     * Clears the data from all editable cells in the specified column
     *
     * @param column The column to clear
     */
    public void clearColumn(int column) {
        //Removes all data entered into the grid so far
        for (int r = 0; r < markovTable.length; r++) {
            if (isCellEditable(r, column)) {
                this.setValueAt("", r, column);
                this.fireTableCellUpdated(r, column);
            }
        }
    }

    /**
     * Calculate the Markov values NOTE: NEEDS WORK!
     *
     * @return
     */
    public Object[][] calculateMarkov() {
        int numStates = columns.size();
        int currentState = numStates - 1;

        while (currentState > 0) {
            double r = Math.random();
            double sum = 0.0;

            for (int j = 0; j < numStates; j++) {
                sum += new Double(markovTable[currentState][j].toString());
                if (r <= sum) {
                    currentState = j;
                    break;
                }
            }
        }
        return markovTable;
    }
    
    /**
     * Function to calculate the total values based on the type of data in the cell
     * @param currentCell - The current cell to use for updating the total
     * @param total - the current total
     * @param minOrMax - "Min" if updating the minimum value, "Max" if updating the Maximum value
     * @return the updated total
     */
    private double getTotalByClass(MarkovTableCell currentCell, double total, String minOrMax){
        switch(currentCell.getClassOfValue()){
            case 1: //Double
                total -= (double)currentCell.getValue();
                break;
            case 2: //String
                //must handle strings here, because can't override getColumnClass if the columns must be dynamic
                //if(String.valueOf(value).length() > 0){
                  // total -= currentCell.getMin();
                   //total -= Double.parseDouble(String.valueOf(value));
                //}       
                
                if((String.valueOf(currentCell.getValue()).length() > 0) && (null != minOrMax))switch (minOrMax) {
                    case "Min":{
                        total -= currentCell.getMin();
                        break;
                        }
                    case "Max":{
                        total -= currentCell.getMax();
                            break;
                        }
                    default:{
                        total -= Double.parseDouble((String) currentCell.getValue());
                            break;
                        }
                }
                
                break;
            default: //something else, do nothing
                break;
        }
        return total;
    }
    
    /**
     * Sums all the values in a row for a given table
     * @param total - the starting value used as the base to subtract from to create the new sum
     * @param row - the row to calculate
     * @param startCol - the initial column to start the calculations
     * @param thisTable - the table to calculate
     * @param minOrMax - "Min" if updating the minimum value, "Max" if updating the Maximum value
     * @return total amount left, total-sum(all values in row starting at start column)
     */
    private double sumRow(double total, int row, int startCol, int sumCol, Object[][] thisTable, String minOrMax){
        for(int c=startCol;c<sumCol;c++){
            if(thisTable[row][c] != null){
                MarkovTableCell currentCell = (MarkovTableCell)thisTable[row][c];
                
                total = getTotalByClass(currentCell, total, minOrMax);
            }
        }
        
        return total;
    }
    
    /**
     * Sums all the values in a column for a given table
     * @param total - the starting value used as the base to subtract from to create the new sum
     * @param col - the column to calculate
     * @param startRow - the initial row to start the calculations
     * @param thisTable - the table to calculate
     * @param minOrMax - "Min" if updating the minimum value, "Max" if updating the Maximum value
     * @return total amount left, total-sum(all values in column starting at startrow)
     */
    private double sumColumn(double total, int col, int startRow, int sumRow, Object[][] thisTable, String minOrMax){
        for(int r=startRow;r<sumRow;r++){
            if(thisTable[r][col] != null){
                MarkovTableCell currentCell = (MarkovTableCell)thisTable[r][col];
                
                total = getTotalByClass(currentCell, total, minOrMax);
            }
        }
        return total;
    }
        
    /**
     * Runs the calculate function, to update the total rows
     */
    public void calculateAmountLeft(){
        //uses model to recalculate
        //calculated "Amount Left" columns are the 2nd to last row and column
        markovTable = calculateAmountLeft(this.getRowCount()-2,columns.size()-2);
    }
    
    /**
     * Recalculates all summary values for the entire grid, for all rows/columns.
     * @param sumRow - the row containing the final summations for it's column
     * @param sumCol - the column containing the final summations for it's row
     * @return 
     */
    public Object[][] calculateAmountLeft(int sumRow, int sumCol){
        //calculate all columns
        for(int c=PROPORTION_COLUMN+1; c<sumCol; c++){
            if(markovTable[PROPORTION_ROW][c]==null){
                markovTable[PROPORTION_ROW][c] = new MarkovTableCell(PROPORTION_ROW, c, 0.0, true, false, false);
            }
            double colMinProportion = (double)((MarkovTableCell)markovTable[PROPORTION_ROW][c]).getMin();
            double colMaxProportion = (double)((MarkovTableCell)markovTable[PROPORTION_ROW][c]).getMax();
            
            double amountLeftColMin = sumColumn(colMinProportion, c, PROPORTION_ROW+1, sumRow, markovTable, "Min");
            double amountLeftColMax = sumColumn(colMaxProportion, c, PROPORTION_ROW+1, sumRow, markovTable, "Max");
            
            String newColValue = "0 - " + DECIMAL_FORMAT.format(amountLeftColMin) + "   0 - " + DECIMAL_FORMAT.format(amountLeftColMax);
            
            //set column total
            if(markovTable[sumRow][c] == null){
                markovTable[sumRow][c] = new MarkovTableCell(sumRow, c, amountLeftColMax, amountLeftColMin, newColValue, true, false, false);
                //markovTable[sumRow][c] = new MarkovTableCell(sumRow, c, amountLeftCol, true, false, false);
            } else{
                ((MarkovTableCell)markovTable[sumRow][c]).setMin(amountLeftColMin);
                ((MarkovTableCell)markovTable[sumRow][c]).setMax(amountLeftColMax);
                ((MarkovTableCell)markovTable[sumRow][c]).setValue(newColValue);
            }
        }
        
        //calculate all rows
        for(int r=PROPORTION_ROW+1; r<sumRow; r++){
            if(markovTable[r][PROPORTION_COLUMN] == null){
                markovTable[r][PROPORTION_COLUMN] = new MarkovTableCell(r, PROPORTION_COLUMN, 0.0, true, false, false);
            }
            //double rowProportion = (double)((MarkovTableCell)markovTable[r][PROPORTION_COLUMN]).getValue();
            double rowMinProportion = (double)((MarkovTableCell)markovTable[r][PROPORTION_COLUMN]).getMin();
            double rowMaxProportion = (double)((MarkovTableCell)markovTable[r][PROPORTION_COLUMN]).getMax();
            
            //double amountLeftRow = sumRow(rowProportion, r, PROPORTION_COLUMN+1, sumCol, markovTable);
            double amountLeftRowMin = sumRow(rowMinProportion, r, PROPORTION_COLUMN+1, sumCol, markovTable, "Min");
            double amountLeftRowMax = sumRow(rowMaxProportion, r, PROPORTION_COLUMN+1, sumCol, markovTable, "Max");
                                    
            String newRowValue = DECIMAL_FORMAT.format(amountLeftRowMin) + " - " + DECIMAL_FORMAT.format(amountLeftRowMax);
            
            if(markovTable[r][sumCol] == null){
                //markovTable[r][sumCol] = new MarkovTableCell(r, sumCol, amountLeftRow, true, false, false);
                markovTable[r][sumCol] = new MarkovTableCell(r, sumCol, amountLeftRowMax, amountLeftRowMin, newRowValue, true, false, false);
            } else{
                //((MarkovTableCell)markovTable[r][sumCol]).setValue(amountLeftRow);
                ((MarkovTableCell)markovTable[r][sumCol]).setMin(amountLeftRowMin);
                ((MarkovTableCell)markovTable[r][sumCol]).setMax(amountLeftRowMax);
                ((MarkovTableCell)markovTable[r][sumCol]).setValue(newRowValue);
            }
        }
        
        return markovTable;
    }


    /**
     * *
     * Gets the number of rows in the Markov matrix
     *
     * @return
     */
    @Override
    public int getRowCount() {
        return markovTable.length;
    }

    /**
     * *
     * gets the number of columns in the Markov matrix
     *
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
     * Determines the values for the Markov Chain If enough values were
     * provided, auto calculates the rest of the row/col
     *
     * @param row
     * @param column
     * @return Object Value for the cell in the table.
     */
    @Override
    public Object getValueAt(int row, int column) {
        //if the cell does not exist yet, create it as a new editable cell
        try {
            if (markovTable[row][column] == null) {
                markovTable[row][column] = new MarkovTableCell(row, column, "", false, false, true);
            }

            //Add a button to the last row/column of the grid to allow clearing
            //with the exception of the non-editable rows/columns
            if ((row == markovTable.length - 1) && (column > 1) && (column != columns.size() - 1)) {
                //clear the column that button is in
                JButton colClear = createColumnClearButton(column);
                return colClear;
            } else if ((column == columns.size() - 1) && (row > 0) && (row != markovTable.length - 1)) {
                //clear the row that button is in
                JButton rowClear = createRowClearButton(row);
                return rowClear;
            } else if (markovTable[row][column].getClass().equals(MarkovTableCell.class)) {
                MarkovTableCell thisCell = (MarkovTableCell)markovTable[row][column];
                
                if(thisCell.isEditable()){
                    return thisCell.getMin() + " - " + thisCell.getMax();
                }else{
                    return thisCell.getValue();
                }
            }
            return markovTable[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Caught ArrayIndexOutOfBoundsException" + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new button for clearing rows.
     *
     * @param row - the row for which the button shall be created
     * @return
     */
    public JButton createRowClearButton(int row) {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearRow(row);
            }
        });
        return clearButton;
    }

    /**
     * Creates a new button for clearing columns
     *
     * @param col - the column for which the bottom shall be created
     * @return
     */
    public JButton createColumnClearButton(int col) {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearColumn(col);
            }
        });
        return clearButton;
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
        if (markovTable[row][col] == null) {
            return true;
        } else if (markovTable[row][col].getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable[row][col])).isEditable();
        } else {
            return true; //(!(col==0)) && (!(col==1)) && (!(row==0));
        }
    }

    /**
     * *
     * Specific to the Markov Table Cell, checks if a cell is calculated
     *
     * @param row
     * @param col
     * @return
     */
    public boolean isCellCalculated(int row, int col) {
        if (markovTable[row][col] == null) {
            return false;
        } else if (markovTable[row][col].getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable[row][col])).isCalculated();
        } else {
            return false;  //if not the Markov calculated cells - leave alone
        }
    }

    /**
     * *
     * Specific to the Markov table Cell, checks if a cell has an error
     *
     * @param row
     * @param col
     * @return
     */
    public boolean isErrorInCell(int row, int col) {
        if (markovTable[row][col] == null) {
            return false;
        } else if (markovTable[row][col].getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable[row][col])).isError();
        } else {
            return false;  //if not the Markov cells - leave alone
        }
    }

    /**
     * Sets the value for a specific cell in the Markov table
     *
     * @param value The cell value to set
     * @param row The row index of the cell to set
     * @param col The column index of the cell to set
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        double minVal = 0.0;
        double maxVal = 0.0;
        
        if(value.getClass() == String.class ){
            String thisCell = (String)value;
            int minValLocation = thisCell.indexOf(" - ");
            if(minValLocation > 0){
                minVal = Double.parseDouble(thisCell.substring(0, minValLocation));
                maxVal = Double.parseDouble(thisCell.substring(minValLocation+3));

                System.out.println("minVal: " + minVal + ", " + DECIMAL_FORMAT.format(minVal));
                System.out.println("maxVal: " + maxVal + ", " + DECIMAL_FORMAT.format(maxVal));
            }
        }
        
        if (markovTable[row][col] == null) {
            markovTable[row][col] = new MarkovTableCell(row, col, maxVal, minVal, value, false, false, true); 
            //markovTable[row][col] = new MarkovTableCell(row, col, value, false, false, true);
        } else if (markovTable[row][col].getClass().equals(MarkovTableCell.class)) {
            ((MarkovTableCell) (markovTable[row][col])).setMin(minVal);
            ((MarkovTableCell) (markovTable[row][col])).setMax(maxVal);
            ((MarkovTableCell) (markovTable[row][col])).setValue(value);
        } else {
            markovTable[row][col] = value;
        }
        this.fireTableCellUpdated(row, col);
    }
}
