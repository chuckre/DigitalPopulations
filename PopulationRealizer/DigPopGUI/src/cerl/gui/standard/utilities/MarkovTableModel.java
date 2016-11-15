/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 *
 * @author mrivera
 */
public class MarkovTableModel extends AbstractTableModel{
    private ArrayList<String> columns = new ArrayList<>();
    private Object[][] markovTable;
    
    /***
     * Creates a default Markov Table Model, of size 1x1
     */
    public MarkovTableModel(){
        super();
        columns.addAll(Arrays.asList("","Value","","Amount Left"));
        //columns must be rows+1 because the header row is the -1th row.
        markovTable = new Object[5][6];
        markovTable[0][0] = "Value";
        markovTable[2][0] = "Amount Left";
        markovTable[0][1] = "Proportion";
        boolean proportionsSet = setProportions();
        if(!proportionsSet){
            System.out.print("Error setting proportions");
        }
    }

    /**
     * Creates a Markov Matrix with an existing set of columns and cells
     * @param columnNames The names of the columns
     * @param cellValues The 2D matrix of cell values
     */
    public MarkovTableModel(ArrayList<String> columnNames, Object[][] cellValues){
        super();
        columns = columnNames;
        markovTable = cellValues;
    }
    
    /***
     * Sets the proportions for the Markov matrix.
     * @return true if successful - false on error
     */
    public boolean setProportions(){
        //current test example
        markovTable[0][2] = 0.25;
        markovTable[1][1] = 0.99;
        return true;
    }
    
    /**
     * Calculate the Markov values
     * NOTE: NEEDS WORK!
     * @return 
     */
    public Object[][] calculateMarkov(){
        int numStates = columns.size();
        int currentState = numStates - 1;
        
        while (currentState > 0){
            double r = Math.random();
            double sum = 0.0;
            
            for (int j = 0; j < numStates; j++){
                sum += new Double(markovTable[currentState][j].toString());
                if(r <= sum){
                    currentState = j;
                    break;
                }
            }
        }
        return markovTable;
    }
    
    /***
     * Gets the number of rows in the Markov matrix
     * @return 
     */
    @Override
    public int getRowCount() {
        return markovTable.length;
    }

    /***
     * gets the number of columns in the Markov matrix
     * @return 
     */
    @Override
    public int getColumnCount() {
        return columns.size();
    }
    
    /**
     * Gets the column header name for a specified column index.
     * @param col The index of the column to retrieve the header name
     * @return 
     */
    @Override
    public String getColumnName(int col) {
        return columns.get(col);
    }
       
    /**
     * Determines the values for the Markov Chain
     * If enough values were provided, auto calculates the rest of the row/col
     * @param row
     * @param column
     * @return Object Value for the cell in the table.
     */
    @Override
    public Object getValueAt(int row, int column){
        return markovTable[row][column];
    }
    
    /***
     * Allows users to only edit the interior cells
     * Note: Will need logic for editing computed values versus non-computed.
     * @param row
     * @param col
     * @return 
     */
    @Override
    public boolean isCellEditable(int row, int col){
        return (!(col==0)) && (!(col==1)) && (!(row==0));
    }
    
    /**
     * Sets the value for a specific cell in the Markov table
     * @param value The cell value to set
     * @param row The row index of the cell to set
     * @param col The column index of the cell to set
     */
    @Override
    public void setValueAt(Object value, int row, int col){
        markovTable[row][col] = value;
    }
}
