/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 * The object used to group survey columns in Step 3
 * Represents a single value in a column of the survey data
 * For groupings, Markov only displays each value once, with the number of times it has been used in the column of the survey data file
 * @author ajohnson
 */
public class SurveyColumnValue  implements Cloneable {
    private int rowId;
    private int value; /* the pumsTraitField */
    private boolean used;
    private int numberOfTimesUsed; 
    private String sourceFile; /* the pumsTraitTable */

    /**
     * Creates a new, empty survey column value object
     */
    public SurveyColumnValue() {
        this.used = false;
        this.numberOfTimesUsed = 0;
        this.value = 0;
    }

    /**
     * Creates a new survey column value object from the specified parameters
     * @param rowId - the index of the first row the value appears in the survey data
     * @param value - the column index from the survey data file
     * @param used - true if the survey column is used
     * @param sourceFile - "Household" or "Population" based on where it originated
     * @param numberOfTimesUsed - the counter tracking the number of times it has been used in the survey data column
     */
    public SurveyColumnValue(int rowId, int value, boolean used, int numberOfTimesUsed, String sourceFile) {
        this.rowId = rowId;
        this.value = value;
        this.used = used;
        this.numberOfTimesUsed = numberOfTimesUsed;
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the value of the counter for the number of times the value has been used in the survey data column
     * @return the number of times the value has been used in the survey data column
     */
    public int getNumberOfTimesUsed() {
        return numberOfTimesUsed;
    }

    /**
     * Sets the number of times the value has been used in the survey data column 
     * @param numberOfTimesUsed - the new value for the usage counter
     */
    public void setNumberOfTimesUsed(int numberOfTimesUsed) {
        this.numberOfTimesUsed = numberOfTimesUsed;
    }

    /**
     * Gets the index of the first row the value appears in
     * @return the row index for the first time the value appears in the survey data
     */
    public int getRowId() {
        return rowId;
    }

    /**
     * Sets the index of the first Row the value appears in
     * @param rowId - the first row index the value appears in the survey data
     */
    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    /**
     * Gets the value, pumsTraitField, from the survey column
     * @return the integer value from the survey column data
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value, pumsTraitField, for the survey column data item
     * @param value - the new value from the survey data
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * A true/false value tracking if the survey column value has been used
     * @return true if it has been used, false if not
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Sets the value of the isUsed true/false flag
     * @param used - true if it has been used, false if not
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Gets the source file, pumsTraitTable, associated with this column
     * @return 
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the source file, pumsTraitTable, associated with this column. 
     * @param sourceFile 
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    /**
     * Returns the string value with the first row the value was used in, the number of times used in the column, and the value
     * @return the string representation of the survey column value data
     */
    @Override
    public String toString() {
        return "First Row ID: " + this.rowId + "   -   Used: " +  this.numberOfTimesUsed + " times   -   Value: " + this.value + " - Source: " + this.sourceFile;
    }
    
    /**
     * Clones the survey column value object
     * @return a new survey column value object as a clone of another
     * @throws CloneNotSupportedException 
     */
    public SurveyColumnValue clone() throws CloneNotSupportedException 
    {
        SurveyColumnValue clonedSurveyColumnValue = (SurveyColumnValue) super.clone();
 
        return clonedSurveyColumnValue;
    }
    
    /**
     * Increments the usage counter for the specific value
     */
    public void addOneToNumberOfTimesUsed() {
        this.numberOfTimesUsed++;
    }
}
