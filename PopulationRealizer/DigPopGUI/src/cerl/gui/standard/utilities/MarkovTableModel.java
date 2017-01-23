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
    private ArrayList<ArrayList<Object>> markovTable;
    private final int PROPORTION_COLUMN = 1;
    private final int PROPORTION_ROW = 0;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private final int numberOfRows;
    private final int numberOfColumns;
    
    /**
     * *
     * Creates a new, blank Markov Table Model
     */
    public MarkovTableModel() {
        super();
        columns = new ArrayList<>();
        numberOfRows = PROPORTION_ROW + 1;
        numberOfColumns = PROPORTION_COLUMN + 1;
        emptyCells = new int[2][Math.max(PROPORTION_ROW, PROPORTION_COLUMN)+1];
        //columns must be rows+1 because the header row is the -1th row.
        markovTable = new ArrayList<>();
    }

    /**
     * Creates a Markov Matrix with an existing set of columns and cells
     *
     * @param columnNames The names of the columns
     * @param cellValues The 2D matrix of cell values
     * @param cells The array for tracking the number of empty cells in each row/column
     * @param numberOfRows The number of editable rows in the markovTable - used for tracking empty cells
     * @param numberOfColumns The number of editable columns in the table - used for tracking empty cells
     */
    public MarkovTableModel(ArrayList<String> columnNames, ArrayList<ArrayList<Object>> cellValues, int[][] cells, int numberOfRows, int numberOfColumns) {
        super(columnNames, cellValues);
        columns = columnNames;
        markovTable = cellValues;
        emptyCells = cells;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
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
        for(int r=0; r<numberOfRows; r++){
            emptyCells[0][r] = numberOfColumns; //in a row, there are numCols cells
        }
        for(int c=0; c<numberOfColumns; c++){
            emptyCells[1][c] = numberOfRows; //in a column, there are numRows cells
        }
    }
    
    /**
     * Clears a single cell at the provided row/column
     * Resets the "calculated" flag to false
     * Resets the "user entered" flag to false
     * Updates the Empty Cell array
     * @param row - the row to clear
     * @param col - the column to clear
     */
    private void clearCell(int row, int col){
        this.setValueAt("", row, col);
        
        if((row < numberOfRows) && (emptyCells[0][row-PROPORTION_ROW-1] < numberOfColumns)){
            emptyCells[0][row-PROPORTION_ROW-1] = emptyCells[0][row-PROPORTION_ROW-1] +1;
        }
        if((col < numberOfColumns) && (emptyCells[1][col-PROPORTION_COLUMN-1] < numberOfRows)){
            emptyCells[1][col-PROPORTION_COLUMN-1] = emptyCells[1][col-PROPORTION_COLUMN-1] +1;
        }
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
        //populateEmptyCells();
    }

    /**
     * Clears the data from all editable cells in the specified row
     *
     * @param row The offset of which column to start clearing
     */
    public void clearRow(int row) {
        //Removes all data entered into the specified row
        for (int c = PROPORTION_COLUMN+1; c < PROPORTION_COLUMN+1+numberOfColumns; c++) {
            if (isCellEditable(row, c)) {
                clearCell(row, c);
            }
        }
                
        /*for(int r=0; r<numberOfRows; r++){
            emptyCells[0][r] = numberOfColumns; //in a row, there are numCols cells
        }*/
    }

    /**
     * Clears the data from all editable cells in the specified column
     *
     * @param column The column to clear
     */
    public void clearColumn(int column) {
        //Removes all data entered into the grid so far
        for (int r = PROPORTION_ROW+1; r < PROPORTION_ROW+1+numberOfRows; r++) {
            if (isCellEditable(r, column)) {
                clearCell(r, column);
            }
        }
                
        /*for(int c=0; c<numberOfColumns; c++){
            emptyCells[1][c] = numberOfRows; //in a column, there are numRows cells
        }*/
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
     * Finds the empty cell, in the provided row
     * @param rowToCheck - the ID of the row to check for an empty cell
     * @return the ID of the column if it has only one cell left, or -1 if none are found
     */
    private int findEmptyCellInRow(int rowToCheck){
        //if this row has only one cell left to fill
        if((emptyCells[0].length > (rowToCheck-PROPORTION_ROW-1)) && (emptyCells[0][rowToCheck-PROPORTION_ROW-1] == 1)){
            return rowToCheck;
        }
                        
        return 0;
    }
    
    /**
     * Finds the empty cell, in the provided column
     * @param colToCheck - the ID of the column to check for an empty cell
     * @return the ID of the column if it has only one cell left, or -1 if none are found
     */
    private int findEmptyCellInColumn(int colToCheck){
        //if this column has only one cell left to fill
        if((emptyCells[1].length > (colToCheck-PROPORTION_COLUMN-1)) && (emptyCells[1][colToCheck-PROPORTION_COLUMN-1] == 1)){
            return colToCheck;
        }
                        
        return 0;
    }
    
    /**
     * Finds the empty cell, in the provided row or column.
     * @param rowToCheck - the ID of the row to check for an empty cell
     * @param colToCheck - the ID of the column to check for an empty cell
     * @return an array of value[0] as the row ID of the empty cell, and val[1] of the empty column
     */
    private int[] findEmptyCell(int rowToCheck, int colToCheck){
        int[] rowColArray = new int[2]; //Row is val[0], col is val[1]
        rowColArray[0] = findEmptyCellInColumn(colToCheck);
        rowColArray[1] = findEmptyCellInRow(rowToCheck);
                 
        System.out.println("Found Empty Cells: " + rowColArray[0] + ", " + rowColArray[1]);
        return rowColArray;
    }
    
    /**
     * Sums all the values in a row for a given table
     * @param row - the row to calculate
     * @param startCol - the initial column to start the calculations
     * @param sumRow - the row for the summation to be stored
     * @param sumCol - the column for the summation to be stored
     * @return total amount left, as 1 - sum(all min/max values in row starting at start column)
     */
    private String sumRow(int row, int startCol, int sumRow, int sumCol){
        int rowWithEmptyCell = 0;
        int colWithEmptyCell = 0;
        
        //get Proportion values for this row, by min and max to start
        double rowMinTotal = (double)((MarkovTableCell)markovTable.get(row).get(PROPORTION_COLUMN)).getMin();
        double rowMaxTotal = (double)((MarkovTableCell)markovTable.get(row).get(PROPORTION_COLUMN)).getMax();
                    
        //if this row has only one cell left to fill
        rowWithEmptyCell = findEmptyCellInRow(row);
        
        //for each column in this row
        for(int c=startCol;c<sumCol;c++){ 
            if(markovTable.get(row).get(c) != null){//if not null
                //update the current total for the row/column
                rowMinTotal = getTotalByClass((MarkovTableCell)markovTable.get(row).get(c), rowMinTotal, "Min");
                rowMaxTotal = getTotalByClass((MarkovTableCell)markovTable.get(row).get(c), rowMaxTotal, "Max");
            }
            
            //check columns in this row for the empty cell
            if(!((MarkovTableCell)(markovTable.get(rowWithEmptyCell).get(c))).isUserEntered() && (rowWithEmptyCell > PROPORTION_ROW)){
                ((MarkovTableCell) (markovTable.get(rowWithEmptyCell).get(c))).setCalculated(true);
                colWithEmptyCell = c;
            }
        }

        //Set values for the empty cell
        double newMinTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Min", rowMaxTotal);
        double newMaxTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Max", rowMinTotal);
        filledInEmptyCell(rowWithEmptyCell, colWithEmptyCell);
        
        //update sum column for this row
        String newRowValue = "0 - " + DECIMAL_FORMAT.format(newMinTotal) + "   0 - " + DECIMAL_FORMAT.format(newMaxTotal);
        
        markovTable = setNewTotalValue(row, sumCol, newMaxTotal, newMinTotal, newRowValue);
                
        return newRowValue;
    }
    
    /**
     * Sums all the values in a column for a given table
     * @param col - the column to calculate
     * @param startRow - the initial row to start the calculations
     * @param sumRow - the row for the summation to be stored
     * @param sumCol - the column for the summation to be stored
     * @return total amount left, total-sum(all values in column starting at startrow)
     */
    private String sumColumn(int col, int startRow, int sumRow, int sumCol){
        int rowWithEmptyCell = 0;
        int colWithEmptyCell = 0;
        
        //get Proportion values for the column, by min and max
        double colMinTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(col)).getMin();
        double colMaxTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(col)).getMax();            
        
        //if this column has only one cell left to fill
        colWithEmptyCell = findEmptyCellInColumn(col);
        
        //for each row in this column
        for(int r=startRow;r<sumRow;r++){
            if(markovTable.get(r).get(col) != null){
                //update the curent total for the row/column
                colMinTotal = getTotalByClass((MarkovTableCell)(markovTable.get(r).get(col)), colMinTotal, "Min");
                colMaxTotal = getTotalByClass((MarkovTableCell)(markovTable.get(r).get(col)), colMaxTotal, "Max");
            }
            
            //check rows in this column for the empty cell
            if(!((MarkovTableCell)(markovTable.get(r).get(colWithEmptyCell))).isUserEntered() && (colWithEmptyCell > PROPORTION_COLUMN)){
                ((MarkovTableCell) (markovTable.get(r).get(colWithEmptyCell))).setCalculated(true);
                rowWithEmptyCell = r;
            }
        }

        //Set values for the empty cell
        double newMinTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Min", colMaxTotal);
        double newMaxTotal = setCalculatedField(rowWithEmptyCell, colWithEmptyCell, "Max", colMinTotal);
        filledInEmptyCell(rowWithEmptyCell, colWithEmptyCell);    
                
        String newColValue = "0 - " + DECIMAL_FORMAT.format(newMinTotal) + "   0 - " + DECIMAL_FORMAT.format(newMaxTotal);
            
        //set column total
        markovTable = setNewTotalValue(sumRow, col, newMaxTotal, newMinTotal, newColValue);
                        
        return newColValue;
    }
    
    /**
     * Checks the totals based on the provided row/column being the last updated
     * @param row - the row last updated
     * @param col - the column last updated
     * @return 
     */
    private ArrayList<ArrayList<Object>> checkTotals(int row, int col){
        int sumRow = PROPORTION_ROW+numberOfRows+1;
        int sumCol = PROPORTION_COLUMN+numberOfColumns+1;

        //get Proportion values for the row, by min and max to start
        double colMinTotal = (double)((MarkovTableCell)markovTable.get(row).get(PROPORTION_COLUMN)).getMin();
        double colMaxTotal = (double)((MarkovTableCell)markovTable.get(row).get(PROPORTION_COLUMN)).getMax();
        
        //check the row totals, by summing up each column
        for(int c=PROPORTION_COLUMN+1;c<sumCol;c++){
            if(markovTable.get(row).get(c) != null){ //if a cell is a cell
                //update the curent running totals for the column
                colMinTotal = getTotalByClass((MarkovTableCell)(markovTable.get(row).get(c)), colMinTotal, "Min");
                colMaxTotal = getTotalByClass((MarkovTableCell)(markovTable.get(row).get(c)), colMaxTotal, "Max");
            }                
        }
        if((colMinTotal != ((MarkovTableCell)(markovTable.get(row).get(sumCol))).getMin())
            || (colMaxTotal != ((MarkovTableCell)(markovTable.get(row).get(sumCol))).getMax())){
            //update the column total
            String newColValue = "0 - " + DECIMAL_FORMAT.format(colMinTotal) + "   0 - " + DECIMAL_FORMAT.format(colMaxTotal);
            markovTable = setNewTotalValue(row, sumCol, colMaxTotal, colMinTotal, newColValue);
            setOrClearErrors(row,col);
        }

        //get Proportion values for the column, by min and max to start
        double rowMinTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(col)).getMin();
        double rowMaxTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(col)).getMax();            
        
        //check the column totals, by summing up each row in the column
        for(int r=PROPORTION_ROW+1;r<sumRow;r++){
            //if a cell is a cell
            if(markovTable.get(r).get(col) != null){
                //update the current running totals for the row
                rowMinTotal = getTotalByClass((MarkovTableCell)markovTable.get(r).get(col), rowMinTotal, "Min");
                rowMaxTotal = getTotalByClass((MarkovTableCell)markovTable.get(r).get(col), rowMaxTotal, "Max");
            }                
        }
        if((rowMinTotal != ((MarkovTableCell)(markovTable.get(sumRow).get(col))).getMin())
                || (rowMaxTotal != ((MarkovTableCell)(markovTable.get(sumRow).get(col))).getMax())){
            //update the row total
            String newRowValue = "0 - " + DECIMAL_FORMAT.format(rowMinTotal) + "   0 - " + DECIMAL_FORMAT.format(rowMaxTotal);
            markovTable = setNewTotalValue(sumRow, col, rowMaxTotal, rowMinTotal, newRowValue);
            setOrClearErrors(row,col);
        }

        return markovTable;
    }
    
    private ArrayList<ArrayList<Object>> setNewTotalValue(int row, int col, double newMaxTotal, double newMinTotal, String newValue){
        System.out.println("Set new total value: " + row + ", " + col + ", min: " + newMinTotal + ", max: " + newMaxTotal);
        
        if(markovTable.get(row).get(col) == null){
            markovTable.get(row).set(col, new MarkovTableCell(row, col, newMaxTotal, newMinTotal, newValue, true, false, false, false));
        } else{
            ((MarkovTableCell)markovTable.get(row).get(col)).setMin(newMinTotal);
            ((MarkovTableCell)markovTable.get(row).get(col)).setMax(newMaxTotal);
            ((MarkovTableCell)markovTable.get(row).get(col)).setValue(newValue);
            ((MarkovTableCell)markovTable.get(row).get(col)).setEditable(false);
            ((MarkovTableCell)markovTable.get(row).get(col)).setCalculated(true);
        }
        
        return markovTable;
    }
    
    private boolean emptyCellsAreFilled(){
        boolean allEmptyCellsAreFilled = true;
        
        for(int r=0;r<emptyCells[0].length;r++){
            if(emptyCells[0][r] > 0){
                allEmptyCellsAreFilled = false;
                break;
            }
        }
        if(allEmptyCellsAreFilled){ //don't check if already an issue
            for(int c=0;c<emptyCells[1].length;c++){
                if(emptyCells[1][c]>0){
                    allEmptyCellsAreFilled = false;
                    break;
                }
            }
        }
        
        return allEmptyCellsAreFilled;
    }
    
    /*private ArrayList<ArrayList<Object>> sumAllRowsAndColumns(int sumRow, int sumCol){
        //for each row
        for(int r=PROPORTION_ROW+1;r<sumRow;r++){
            //get Proportion values for this row, by min and max to start
            double rowMinTotal = (double)((MarkovTableCell)markovTable.get(r).get(PROPORTION_COLUMN)).getMin();
            double rowMaxTotal = (double)((MarkovTableCell)markovTable.get(r).get(PROPORTION_COLUMN)).getMax();

            //for each column
            for(int c=PROPORTION_COLUMN+1;c<sumCol;c++){ 
                //get Proportion values for the column, by min and max
                double colMinTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(c)).getMin();
                double colMaxTotal = (double)((MarkovTableCell)markovTable.get(PROPORTION_ROW).get(c)).getMax();            

                if(markovTable.get(r).get(c) != null){
                    //update the curent total for the row/column
                    colMinTotal = getTotalByClass((MarkovTableCell)(markovTable.get(r).get(c)), colMinTotal, "Min");
                    colMaxTotal = getTotalByClass((MarkovTableCell)(markovTable.get(r).get(c)), colMaxTotal, "Max");
                }

                String newColValue = "0 - " + DECIMAL_FORMAT.format(colMinTotal) + "   0 - " + DECIMAL_FORMAT.format(colMaxTotal);
                ((MarkovTableCell)markovTable.get(sumRow).get(c)).setMin(colMinTotal);
                ((MarkovTableCell)markovTable.get(sumRow).get(c)).setMax(colMaxTotal);
                ((MarkovTableCell)markovTable.get(sumRow).get(c)).setValue(newColValue);
                ((MarkovTableCell)markovTable.get(sumRow).get(c)).setEditable(false);
                ((MarkovTableCell)markovTable.get(sumRow).get(c)).setCalculated(true);
                
                if(markovTable.get(r).get(c) != null){//if not null
                    //update the current total for the row/column
                    rowMinTotal = getTotalByClass((MarkovTableCell)markovTable.get(r).get(c), rowMinTotal, "Min");
                    rowMaxTotal = getTotalByClass((MarkovTableCell)markovTable.get(r).get(c), rowMaxTotal, "Max");
                }
            }
            
            String newRowValue = "0 - " + DECIMAL_FORMAT.format(rowMinTotal) + "   0 - " + DECIMAL_FORMAT.format(rowMaxTotal);
            ((MarkovTableCell)markovTable.get(r).get(sumCol)).setMin(rowMinTotal);
            ((MarkovTableCell)markovTable.get(r).get(sumCol)).setMax(rowMaxTotal);
            ((MarkovTableCell)markovTable.get(r).get(sumCol)).setValue(newRowValue);
            ((MarkovTableCell)markovTable.get(r).get(sumCol)).setEditable(false);
            ((MarkovTableCell)markovTable.get(r).get(sumCol)).setCalculated(true);
        }
        
        return markovTable;
    }*/
    
    /**
     * Sets the min/max or total value of an empty cell to the new calculated value
     * @param rowWithEmptyCell - the row of the cell to set
     * @param colWithEmptyCell - the column of the cell to set
     * @param minOrMax - "Min" if the minimum value is being set, "Max" if the maximum value is being set, otherwise, sets the Value
     * @param total - the value to set in the provided minOrMax attribute
     * @return 0 if the value was set, or the total provided if no cell was changed
     */
    private double setCalculatedField(int rowWithEmptyCell, int colWithEmptyCell, String minOrMax, double total){
        System.out.println("Set Calculated Field: " + rowWithEmptyCell + ", " + colWithEmptyCell + "," + total);
        
        if((colWithEmptyCell > 0) && (rowWithEmptyCell > 0) && (minOrMax != null)){
            ((MarkovTableCell) (markovTable.get(rowWithEmptyCell).get(colWithEmptyCell))).setCalculated(true);
              
            if(minOrMax == "Min"){
                ((MarkovTableCell) (markovTable.get(rowWithEmptyCell).get(colWithEmptyCell))).setMin(total);
            } else if(minOrMax == "Max"){
                ((MarkovTableCell) (markovTable.get(rowWithEmptyCell).get(colWithEmptyCell))).setMax(total);
            } else{
                ((MarkovTableCell) (markovTable.get(rowWithEmptyCell).get(colWithEmptyCell))).setValue(total);
            }
            this.setOrClearErrors(rowWithEmptyCell,colWithEmptyCell);
            total = 0;
        }
        
        return total;
    }
    
    /**
     * Called from the customTableModelListener when the table is changed
     * Runs the calculate function, to update the total rows
     * @param row - The row that was updated
     * @param column - The column that was updated
     */
    @Override
    public void handleTableChange(int row, int column){
        //calculated "Amount Left" columns are the 2nd to last row and column
        markovTable = calculateAmountLeft(this.getRowCount()-2,columns.size()-2);
        
        if(emptyCellsAreFilled()){
            System.out.println("all Empty Cells are filled");
            System.out.println("new field is" + ((MarkovTableCell)markovTable.get(row).get(column)).getValue());
            markovTable = checkTotals(row, column);
        }

    }
    
    public ArrayList<ArrayList<Object>> updateAmountLeft(int row, int column){
        int sumRow = PROPORTION_ROW+numberOfRows+1;
        int sumCol = PROPORTION_COLUMN+numberOfColumns+1;
        
        MarkovTableCell currentRowTotal = (MarkovTableCell)markovTable.get(sumRow).get(column);
        MarkovTableCell currentColumnTotal = (MarkovTableCell)markovTable.get(row).get(sumCol);
                        
        System.out.println("update total from: " + currentRowTotal.getValue().toString());
        
        double newRowMinTotal = this.getTotalByClass((MarkovTableCell)markovTable.get(row).get(column), currentRowTotal.getMin(), "Min");
        double newRowMaxTotal = this.getTotalByClass((MarkovTableCell)markovTable.get(row).get(column), currentRowTotal.getMax(), "Max");
        String newRowValue = "0 - " + DECIMAL_FORMAT.format(newRowMinTotal) + "   0 - " + DECIMAL_FORMAT.format(newRowMaxTotal);
        
        this.setNewTotalValue(row, sumCol, newRowMaxTotal, newRowMinTotal, newRowValue);
        
        double newColMinTotal = this.getTotalByClass((MarkovTableCell)markovTable.get(row).get(column), currentColumnTotal.getMin(), "Min");
        double newColMaxTotal = this.getTotalByClass((MarkovTableCell)markovTable.get(row).get(column), currentColumnTotal.getMax(), "Max");
        String newColValue = "0 - " + DECIMAL_FORMAT.format(newColMinTotal) + "   0 - " + DECIMAL_FORMAT.format(newColMaxTotal);
        this.setNewTotalValue(sumRow, column, newColMaxTotal, newColMinTotal, newColValue);
                
        System.out.println("to: " + newRowMinTotal + " - " + newRowMaxTotal);
        
        return markovTable;
    }
    
    /**
     * Recalculates all summary values for the entire grid, for all rows/columns.
     * @param sumRow - the row containing the final summations for it's column
     * @param sumCol - the column containing the final summations for it's row
     * @return 
     */
    public ArrayList<ArrayList<Object>> calculateAmountLeft(int sumRow, int sumCol){
        //calculate all columns
        for(int c=numberOfColumns; c<sumCol; c++){
            if(markovTable.get(PROPORTION_ROW).get(c)==null){
                markovTable.get(PROPORTION_ROW).set(c, new MarkovTableCell(PROPORTION_ROW, c, 0.0, true, false, false, false));
            }
            
            //set column total
            sumColumn(c, PROPORTION_ROW+1, sumRow, sumCol);
        }
        
        //calculate all rows
        for(int r=PROPORTION_ROW+1; r<sumRow; r++){
            if(markovTable.get(r).get(PROPORTION_COLUMN) == null){
                markovTable.get(r).set(PROPORTION_COLUMN, new MarkovTableCell(r, PROPORTION_COLUMN, 0.0, true, false, false, false));
            }
            
            //calculate the amount left for the row, by min and max
            sumRow(r, PROPORTION_COLUMN+1, sumRow, sumCol);
        }
        
        return markovTable;
    }
        
    /**
     * Returns the value for a specific row/column index of the Markov table
     *
     * @param row
     * @param column
     * @return Object Value for the cell in the table.
     */
    @Override
    public Object getValueAt(int row, int column) {
        try {
            //if the cell does not exist yet, create it as a new editable cell
            if (markovTable.get(row).get(column) == null) {
                markovTable.get(row).set(column, new MarkovTableCell(row, column, "", false, false, false, true));
            }

            //Add a button to the last row/column of the grid to allow clearing
            //with the exception of the non-editable rows/columns
            if ((row == markovTable.size() - 1) && (column > 1) && (column < columns.size() - 2)) {
                //clear the column that button is in
                JButton colClear = createColumnClearButton(column);
                return colClear;
            } else if ((column == columns.size() - 1) && (row > 0) && (row < markovTable.size() - 2)) {
                //clear the row that button is in
                JButton rowClear = createRowClearButton(row);
                return rowClear;
            } else if (markovTable.get(row).get(column).getClass().equals(MarkovTableCell.class)) {
                MarkovTableCell thisCell = (MarkovTableCell)markovTable.get(row).get(column);
                
                if ((thisCell.getValue() != "") && 
                        (thisCell.isEditable() && thisCell.isUserEntered()) 
                        || (thisCell.isEditable() && thisCell.isCalculated())){
                    return DECIMAL_FORMAT.format(thisCell.getMin()) + " - " + DECIMAL_FORMAT.format(thisCell.getMax());
                }else{
                    return thisCell.getValue();
                }
            }
            return markovTable.get(row).get(column);
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
        if (markovTable.get(row).get(col) == null) {
            return true;
        } else if (markovTable.get(row).get(col).getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable.get(row).get(col))).isEditable();
        } else {
            return true; 
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
        if (markovTable.get(row).get(col) == null) {
            return false;
        } else if (markovTable.get(row).get(col).getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable.get(row).get(col))).isCalculated();
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
    @Override
    public boolean isErrorInCell(int row, int col) {
        if (markovTable.get(row).get(col) == null) {
            return false;
        } else if (markovTable.get(row).get(col).getClass().equals(MarkovTableCell.class)) {
            return ((MarkovTableCell) (markovTable.get(row).get(col))).isError();
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
        } 
        
        if (markovTable.get(row).get(col) == null) {
            filledInEmptyCell(row,col); 
            markovTable.get(row).set(col, new MarkovTableCell(row, col, maxVal, minVal, value, false, false, false, true)); 
        } else if (markovTable.get(row).get(col).getClass().equals(MarkovTableCell.class)) {
            if(value == ""){
                ((MarkovTableCell) (markovTable.get(row).get(col))).setEditable(true);
                ((MarkovTableCell) (markovTable.get(row).get(col))).setError(false);
                ((MarkovTableCell) (markovTable.get(row).get(col))).setUserEntered(false);
            } else{
                filledInEmptyCell(row,col); 
                ((MarkovTableCell) (markovTable.get(row).get(col))).setUserEntered(true);
            }
            ((MarkovTableCell) (markovTable.get(row).get(col))).setMin(minVal);
            ((MarkovTableCell) (markovTable.get(row).get(col))).setMax(maxVal);            
            ((MarkovTableCell) (markovTable.get(row).get(col))).setValue(value);
            ((MarkovTableCell) (markovTable.get(row).get(col))).setCalculated(false);
        } else {
            markovTable.get(row).set(col, value);
        }
        
        MarkovTableCell Cell = (MarkovTableCell)(markovTable.get(row).get(col));
        System.out.println("Row: " + row + ", Col: " + col 
                + ", Min: " + Cell.getMin() + ", Max: " + Cell.getMax() 
                + ", Val: " + Cell.getValue()
                + ", Editable: " + Cell.isEditable() 
                + ", Calc: " + Cell.isCalculated()
                + ", Error: " + Cell.isError()
                + ", User: " + Cell.isUserEntered()
                );
        
        this.setOrClearErrors(row,col);
        //this.calculateMarkov(row, col);
        this.fireTableCellUpdated(row, col);
    }
    
    /**
     * Handles tracking that a cell was filled in that was previously empty
     * @param row - the row filled in
     * @param col - the column filled in
     */
    private void filledInEmptyCell(int row, int col){
        if((emptyCells[0].length > (row-PROPORTION_ROW-1)) && (row > 0) 
                && (emptyCells[0][row-PROPORTION_ROW-1] > 0)
            && (emptyCells[1].length > (col-PROPORTION_COLUMN-1)) && (col > 0) 
                && (emptyCells[1][col-PROPORTION_COLUMN-1] > 0))
        {
            emptyCells[0][row-PROPORTION_ROW-1] = emptyCells[0][row-PROPORTION_ROW-1] - 1;
            emptyCells[1][col-PROPORTION_COLUMN-1] = emptyCells[1][col-PROPORTION_COLUMN-1] - 1;
        }
    }
    
    /**
     * Sets or clears the errors for a given cell
     * A cell is erroneous if:
     *   1. The min/max values are negative, 
     *   2. The min/max values are greater than the current row's proportion
     *   3. The min/max values are greater than the current column's proportion
     *   4. The cell values cause the sum of the row/column to be greater than the total allowed
     * @param row - The row of the cell to check for errors
     * @param col - The column of the cell to check for errors
     */
    private void setOrClearErrors(int row, int col){
        //Set or clear errors
        System.out.println("Set or Clear Errors: " + row + ", " + col);
        if((((MarkovTableCell) (markovTable.get(row).get(col))).getMin() < 0) 
                || (((MarkovTableCell) (markovTable.get(row).get(col))).getMax() <0) 
                || (((MarkovTableCell) (markovTable.get(row).get(col))).getMin() > ((MarkovTableCell) (markovTable.get(PROPORTION_ROW).get(col))).getMin())
                || (((MarkovTableCell) (markovTable.get(row).get(col))).getMin() > ((MarkovTableCell) (markovTable.get(row).get(PROPORTION_COLUMN))).getMin())
                || (((MarkovTableCell) (markovTable.get(row).get(col))).getMax() > ((MarkovTableCell) (markovTable.get(PROPORTION_ROW).get(col))).getMax())
                || (((MarkovTableCell) (markovTable.get(row).get(col))).getMax() > ((MarkovTableCell) (markovTable.get(row).get(PROPORTION_COLUMN))).getMax())
                ){
            ((MarkovTableCell) (markovTable.get(row).get(col))).setError(true);
        } else if(((row-PROPORTION_ROW-1 < emptyCells[0].length) && (emptyCells[0][row-PROPORTION_ROW-1] == 0) 
                    && (((MarkovTableCell) (markovTable.get(PROPORTION_ROW).get(col))).getMax() > 0))
                || ((col-PROPORTION_COLUMN-1 < emptyCells[1].length) && (emptyCells[1][col-PROPORTION_COLUMN-1] == 0) 
                    && (((MarkovTableCell) (markovTable.get(row).get(PROPORTION_COLUMN))).getMax() > 0))){
            ((MarkovTableCell) (markovTable.get(row).get(col))).setError(true);
        }
        else{
            ((MarkovTableCell) (markovTable.get(row).get(col))).setError(false);
        }
    }
    
    /**
     * Finds the Min and Max values at a given Row/Column combination.
     * @param row The given row index
     * @param column The given column index
     * @return 
     */
    public double[] getMinMaxObject(int row, int column){
        double min = ((MarkovTableCell)markovTable.get(row).get(column)).getMin();
        double max = ((MarkovTableCell)markovTable.get(row).get(column)).getMax();
        double[] values = new double[]{min, max};
        
        return values;
    }
}
