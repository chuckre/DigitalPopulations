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
import java.util.*;
import javax.swing.JButton;

/**
 * The table model for the Markov Chain Matrix tables.
 *
 * @author mrivera
 */
public class MarkovTableModel extends customTableModel {

    private final ArrayList<String> columns;
    private final int[][] emptyCells;
    private Object[][] markovTable;
    private final int PROPORTION_COLUMN = 1;
    private final int PROPORTION_ROW = 0;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    /**
     * *
     * Creates a new, blank Markov Table Model
     */
    public MarkovTableModel() {
        super();
        columns = new ArrayList<>();
        emptyCells = new int[2][Math.max(PROPORTION_ROW, PROPORTION_COLUMN)+1];
        //columns must be rows+1 because the header row is the -1th row.
        markovTable = new Object[PROPORTION_ROW+1][PROPORTION_COLUMN+1];
    }

    /**
     * Creates a Markov Matrix with an existing set of columns and cells
     *
     * @param columnNames The names of the columns
     * @param cellValues The 2D matrix of cell values
     * @param cells The array for tracking the number of empty cells in each row/column
     */
    public MarkovTableModel(ArrayList<String> columnNames, Object[][] cellValues, int[][] cells) {
        super(columnNames, cellValues);
        columns = columnNames;
        markovTable = cellValues;
        emptyCells = cells;
        populateEmptyCells();
    }

    /**
     * Populates the empty cell array, to be used for calculating remaining cells
     * emptyCells[0] tracks the empty cells per row 
     *      (e.g. emptyCells[0][5] is the number of empty cells in row 5)
     * emptyCells[1] tracks the empty cells per column 
     *      (e.g. emptyCells[1][7] is the number of empty cells in the 7th column)
     */
    private void populateEmptyCells(){
        int numRows = emptyCells[0].length;
        int numCols = emptyCells[1].length;
        
        for(int r=0; r<numRows; r++){
            emptyCells[0][r] = numCols; //in a row, there are numCols cells
        }
        for(int c=0; c<numCols; c++){
            emptyCells[1][c] = numRows; //in a column, there are numRows cells
        }
    }
    
    private void clearCell(int row, int col){
        this.setValueAt("", row, col);
        ((MarkovTableCell) (markovTable[row][col])).setCalculated(false);
        ((MarkovTableCell) (markovTable[row][col])).setUserEntered(false);
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
                clearCell(r, c);
            }
        }
        populateEmptyCells();
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
                clearCell(row, c);
            }
        }
        
        int numRows = emptyCells[0].length;
        int numCols = emptyCells[1].length;
        
        for(int r=0; r<numRows; r++){
            emptyCells[0][r] = numCols; //in a row, there are numCols cells
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
                clearCell(r, column);
            }
        }
        
        int numRows = emptyCells[0].length;
        int numCols = emptyCells[1].length;
        
        for(int c=0; c<numCols; c++){
            emptyCells[1][c] = numRows; //in a column, there are numRows cells
        }
    }

    /**
     * Calculate the Markov values NOTE: NEEDS WORK!
     *
     * @param row - The row of the cell that was just edited
     * @param col - The column of the cell that was just edited
     * @return
     */
    public Object[][] calculateMarkov(int row, int col) {
        System.out.println("Calculate Markov Called for row:" + row + ", col: " + col);
        if((row <= PROPORTION_ROW) || (col <= PROPORTION_COLUMN)){
            return markovTable; //invalid cells for this purpose
        }
        
        /*//EVENTUALLY USE TO GENERATE THE CSV FILE
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
        }*/
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
     * @param row - the row to calculate
     * @param startCol - the initial column to start the calculations
     * @param sumCol - the column for the summation to be stored
     * @return total amount left, total-sum(all values in row starting at start column)
     */
    private String sumRow(int row, int startCol, int sumCol){
        int rowWithEmptyCell = 0;
        int colWithEmptyCell = 0;
        
        //get Proportion values for this row, by min and max to start
        double rowMinTotal = (double)((MarkovTableCell)markovTable[row][PROPORTION_COLUMN]).getMin();
        double rowMaxTotal = (double)((MarkovTableCell)markovTable[row][PROPORTION_COLUMN]).getMax();
                    
        //if this row has only one cell left to fill
        if((emptyCells[0].length > (row-PROPORTION_ROW-1)) && (emptyCells[0][row-PROPORTION_ROW-1] == 1)){
            //can be calculated - save cell for use once total is obtained
            rowWithEmptyCell = row; 
        }
        
        //for each column in this row
        for(int c=startCol;c<sumCol;c++){ 
            if(markovTable[row][c] != null){//if not null
                //update the current total for the row/column
                rowMinTotal = getTotalByClass((MarkovTableCell)markovTable[row][c], rowMinTotal, "Min");
                rowMaxTotal = getTotalByClass((MarkovTableCell)markovTable[row][c], rowMaxTotal, "Max");
            }
            
            //check columns in this row for the empty cell
            if(!((MarkovTableCell)(markovTable[rowWithEmptyCell][c])).isUserEntered() && (rowWithEmptyCell > PROPORTION_ROW)){
                ((MarkovTableCell) (markovTable[rowWithEmptyCell][c])).setCalculated(true);
                emptyCells[0][rowWithEmptyCell-1-PROPORTION_ROW] = emptyCells[0][rowWithEmptyCell-1-PROPORTION_ROW] - 1;
                //remember the empty column - to set after have updated total
                colWithEmptyCell = c;
            }
        }

        //Set values for the empty cell
        rowMinTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Min", rowMinTotal);
        rowMaxTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Max", rowMaxTotal);
        
        //update sum column for this row
        String newRowValue = "0 - " + DECIMAL_FORMAT.format(rowMinTotal) + "   0 - " + DECIMAL_FORMAT.format(rowMaxTotal);
        
        if(markovTable[row][sumCol] == null){
            markovTable[row][sumCol] = new MarkovTableCell(row, sumCol, rowMaxTotal, rowMinTotal, newRowValue, true, false, false, false);
        } else{
            ((MarkovTableCell)markovTable[row][sumCol]).setMin(rowMinTotal);
            ((MarkovTableCell)markovTable[row][sumCol]).setMax(rowMaxTotal);
            ((MarkovTableCell)markovTable[row][sumCol]).setValue(newRowValue);
        }

        return newRowValue;
    }
    
    /**
     * Sums all the values in a column for a given table
     * @param col - the column to calculate
     * @param startRow - the initial row to start the calculations
     * @param sumRow - the row for the summation to be stored
     * @return total amount left, total-sum(all values in column starting at startrow)
     */
    private String sumColumn(int col, int startRow, int sumRow){
        int rowWithEmptyCell = 0;
        int colWithEmptyCell = 0;
        
        //get Proportion values for the column, by min and max
        double colMinTotal = (double)((MarkovTableCell)markovTable[PROPORTION_ROW][col]).getMin();
        double colMaxTotal = (double)((MarkovTableCell)markovTable[PROPORTION_ROW][col]).getMax();            
        
        //if this column has only one cell left to fill
        if((emptyCells[1].length > (col-PROPORTION_COLUMN-1)) && (emptyCells[1][col-PROPORTION_COLUMN-1] == 1)){
            //can be calculated - save cell fo ruse once total is obtained
            colWithEmptyCell = col;
        }
        
        //for each row in this column
        for(int r=startRow;r<sumRow;r++){
            if(markovTable[r][col] != null){
                //update the curent total for the row/column
                colMinTotal = getTotalByClass((MarkovTableCell)markovTable[r][col], colMinTotal, "Min");
                colMaxTotal = getTotalByClass((MarkovTableCell)markovTable[r][col], colMaxTotal, "Max");
            }
            
            //check rows in this column for the empty cell
            if(!((MarkovTableCell)(markovTable[r][colWithEmptyCell])).isUserEntered() && (colWithEmptyCell > PROPORTION_COLUMN)){
                ((MarkovTableCell) (markovTable[r][colWithEmptyCell])).setCalculated(true);
                emptyCells[1][colWithEmptyCell-1-PROPORTION_COLUMN] = emptyCells[1][colWithEmptyCell-1-PROPORTION_COLUMN] - 1;
                rowWithEmptyCell = r;
            }
        }

        //Set values for the empty cell
        colMinTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Min", colMinTotal);
        colMaxTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Max", colMaxTotal);

        String newColValue = "0 - " + DECIMAL_FORMAT.format(colMinTotal) + "   0 - " + DECIMAL_FORMAT.format(colMaxTotal);
            
        //set column total
        if(markovTable[sumRow][col] == null){
            markovTable[sumRow][col] = new MarkovTableCell(sumRow, col, colMaxTotal, colMinTotal, newColValue, true, false, false, false);
        } else{
            ((MarkovTableCell)markovTable[sumRow][col]).setMin(colMinTotal);
            ((MarkovTableCell)markovTable[sumRow][col]).setMax(colMaxTotal);
            ((MarkovTableCell)markovTable[sumRow][col]).setValue(newColValue);
        }
        
        return newColValue;
    }
    
    private double setCalculatedField(int rowWithEmptyCell, int colWithEmptyCell, String minOrMax, double total){
        //synchronized(markovTable){

        if((colWithEmptyCell > 0) && (rowWithEmptyCell > 0) && (minOrMax != null)){
            if(minOrMax == "Min"){
                ((MarkovTableCell) (markovTable[rowWithEmptyCell][colWithEmptyCell])).setMin(total);
            } else if(minOrMax == "Max"){
                ((MarkovTableCell) (markovTable[rowWithEmptyCell][colWithEmptyCell])).setMax(total);
            } else{
                ((MarkovTableCell) (markovTable[rowWithEmptyCell][colWithEmptyCell])).setValue(total);
            }
            this.setOrClearErrors(rowWithEmptyCell,colWithEmptyCell);
            total = 0;
        }
        
        return total;
        //}
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
                markovTable[PROPORTION_ROW][c] = new MarkovTableCell(PROPORTION_ROW, c, 0.0, true, false, false, false);
            }
            
            //set column total
            String newColValue = sumColumn(c, PROPORTION_ROW+1, sumRow);
        }
        
        //calculate all rows
        for(int r=PROPORTION_ROW+1; r<sumRow; r++){
            if(markovTable[r][PROPORTION_COLUMN] == null){
                markovTable[r][PROPORTION_COLUMN] = new MarkovTableCell(r, PROPORTION_COLUMN, 0.0, true, false, false, false);
            }
            
            //calculate the amount left for the row, by min and max
            String newRowValue = sumRow(r, PROPORTION_COLUMN+1, sumCol);
        }
        
        return markovTable;
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
        try {
            //if the cell does not exist yet, create it as a new editable cell
            if (markovTable[row][column] == null) {
                markovTable[row][column] = new MarkovTableCell(row, column, "", false, false, false, true);
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
                if(((MarkovTableCell)markovTable[row][column]).isEditable()){
                    return DECIMAL_FORMAT.format(((MarkovTableCell)markovTable[row][column]).getMin()) + " - " + DECIMAL_FORMAT.format(((MarkovTableCell)markovTable[row][column]).getMax());
                }else{
                    return ((MarkovTableCell)markovTable[row][column]).getValue();
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
            }
            
            if(value != ""){
                System.out.println("Updated empty cells");
                emptyCells[0][row-PROPORTION_ROW-1] = emptyCells[0][row-PROPORTION_ROW-1] - 1;
                emptyCells[1][col-PROPORTION_COLUMN-1] = emptyCells[1][col-PROPORTION_COLUMN-1] - 1;
            }
        } 
        
        
        if (markovTable[row][col] == null) {
            markovTable[row][col] = new MarkovTableCell(row, col, maxVal, minVal, value, false, false, false, true); 
        } else if (markovTable[row][col].getClass().equals(MarkovTableCell.class)) {
            ((MarkovTableCell) (markovTable[row][col])).setMin(minVal);
            ((MarkovTableCell) (markovTable[row][col])).setMax(maxVal);
            ((MarkovTableCell) (markovTable[row][col])).setValue(value);
            ((MarkovTableCell) (markovTable[row][col])).setUserEntered(true);
            ((MarkovTableCell) (markovTable[row][col])).setCalculated(false);
        } else {
            markovTable[row][col] = value;
        }
        
        this.setOrClearErrors(row,col);
        //this.calculateMarkov(row, col);
        this.fireTableCellUpdated(row, col);
    }
    
    /**
     * Sets or clears the errors for a given cell
     * A cell is erroneous if:
     *   1. The min/max values are negative, 
     *   2. The min/max values are greater than the current row's proportion
     *   3. The min/max values are greater than the current column's proportion
     * @param row - The row of the cell to check for errors
     * @param col - The column of the cell to check for errors
     */
    private void setOrClearErrors(int row, int col){
        //Set or clear errors
        if((((MarkovTableCell) (markovTable[row][col])).getMin() < 0) 
                || (((MarkovTableCell) (markovTable[row][col])).getMax() <0) 
                || (((MarkovTableCell) (markovTable[row][col])).getMin() > ((MarkovTableCell) (markovTable[PROPORTION_ROW][col])).getMin())
                || (((MarkovTableCell) (markovTable[row][col])).getMin() > ((MarkovTableCell) (markovTable[row][PROPORTION_COLUMN])).getMin())
                || (((MarkovTableCell) (markovTable[row][col])).getMax() > ((MarkovTableCell) (markovTable[PROPORTION_ROW][col])).getMax())
                || (((MarkovTableCell) (markovTable[row][col])).getMax() > ((MarkovTableCell) (markovTable[row][PROPORTION_COLUMN])).getMax())){
            ((MarkovTableCell) (markovTable[row][col])).setError(true);
        } else{
            ((MarkovTableCell) (markovTable[row][col])).setError(false);
        }
    }
}
