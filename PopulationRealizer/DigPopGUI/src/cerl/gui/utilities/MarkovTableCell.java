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
    private boolean error;
    private boolean editable;

    public MarkovTableCell(int row, int column, Object value, boolean calculated, boolean error, boolean editable) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.calculated = calculated;
        this.error = error;
        this.editable = editable;
    }
    
    public MarkovTableCell(int row, int column, double max, double min, Object value, boolean calculated, boolean error, boolean editable) {
        this.row = row;
        this.column = column;
        this.max = max;
        this.min = min;
        this.value = value;
        this.calculated = calculated;
        this.error = error;
        this.editable = editable;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public Object getValue() {
        return value;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public boolean isError() {
        return error;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return value.toString();
    }    
}
