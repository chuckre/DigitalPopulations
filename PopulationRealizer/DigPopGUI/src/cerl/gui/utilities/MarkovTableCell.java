/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 * A new Markov Table cell. Used to track the row and column of the cell.
 * Also keeps track of the min and max values entered for that cell
 * If the cell has only one value, the "value" field is used.
 * If the cell has been populated from a system calculation, set "calculated" = true.
 * If the cell may be modified by a user, set "editable" = true.
 * @author mrivera
 */
public class MarkovTableCell {
    private int row;
    private int column;
    private double max;
    private double min;
    private Object value;
    private boolean calculated;
    private boolean userEntered;
    private boolean error;
    private boolean editable;

    public MarkovTableCell(){
    }
    
    /**
     * Creates a new, basic Markov Table Cell
     * @param row - the index of the row the cell will reside in the table
     * @param column - the index of the column the cell will reside in the table
     * @param value - the value of the cell displayed to the user
     * @param calculated - true if the cell is calculated by the system
     * @param userEntered - true if the cell was entered by a user
     * @param error - true if this cell causes an error in the Markov logic
     * @param editable - true if the cell can be edited by the user
     */
    public MarkovTableCell(int row, int column, Object value, boolean calculated, boolean userEntered, boolean error, boolean editable) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.calculated = calculated;
        this.userEntered = userEntered;
        this.error = error;
        this.editable = editable;
    }
    
    /**
     * Creates a new, full Markov table cell with all attributes
     * @param row - the index of the row the cell will reside in the table
     * @param column - the index of the column the cell will reside in the table
     * @param max - the maximum value entered for the cell
     * @param min - the minimum value entered for the cell
     * @param value - the value of the cell displayed to the user
     * @param calculated - true if the cell is calculated by the system
     * @param userEntered - true if the cell was entered by a user
     * @param error - true if this cell causes an error in the Markov logic
     * @param editable - true if the cell can be edited by the user
     */
    public MarkovTableCell(int row, int column, double max, double min, Object value, boolean calculated, boolean userEntered, boolean error, boolean editable) {
        this.row = row;
        this.column = column;
        this.max = max;
        this.min = min;
        this.value = value;
        this.calculated = calculated;
        this.userEntered = userEntered;
        this.error = error;
        this.editable = editable;
    }

    /**
     * Gets the index of the row for the current cell
     * @return 
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the index of the column for the current cell
     * @return 
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the maximum value for the current cell
     * @return 
     */
    public double getMax() {
        return max;
    }

    /**
     * Gets the minimum value for the current cell
     * @return 
     */
    public double getMin() {
        return min;
    }

    /**
     * Gets the value of the current cell
     * @return 
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns true if the cell is calculated by the system
     * @return 
     */
    public boolean isCalculated() {
        return calculated;
    }

    /**
     * Returns true if the cell value was entered by a user
     * @return 
     */
    public boolean isUserEntered() {
        return userEntered;
    }

    /**
     * Returns true if the cell breaks the Markov logic
     * @return 
     */
    public boolean isError() {
        return error;
    }

    /**
     * Returns true if the user can edit the cell
     * @return 
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Gets the class type of the value.
     * @return 1 if Double, 2 if string, -1 otherwise
     */
    public int getClassOfValue(){
        if(value.getClass() == Double.class){
            return 1;
        } else if(value.getClass() == String.class){
            return 2;
        }
        return -1;
    }
    
    /**
     * Sets the row index of the current cell.
     * @param row 
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the column index of the current cell.
     * @param column 
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Sets the maximum value of the current cell.
     * @param max 
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Sets the minimum value of the current cell.
     * @param min 
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Sets the value displayed to the user for the current cell.
     * @param value 
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Updates the flag if the current cell is calculated by the system.
     * @param calculated 
     */
    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    /**
     * Updates the flag if the current cell was entered by a user
     * @param userEntered 
     */
    public void setUserEntered(boolean userEntered) {
        this.userEntered = userEntered;
    }
    
    /**
     * Updates the flag if the current cell does not abide by the Markov logic
     * @param error 
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Updates the flag for the current cell to be editable.
     * @param editable 
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Returns the value of the cell displayed to the user
     * @return 
     */
    @Override
    public String toString() {
        return value.toString();
    }    
}
