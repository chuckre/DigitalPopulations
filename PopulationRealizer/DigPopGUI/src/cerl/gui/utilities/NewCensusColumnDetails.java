/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;

/**
 * Used with the Markov Chain matrix for the Census Column information
 * Manages the values generated as output in the census enumerations .csv
 * @author ajohnson
 */
public class NewCensusColumnDetails {
    private String newColumnHeader;
    
    private Double randomPercentage;
    private ArrayList<Integer> oldValueLookUpColumns;
    private Integer numberOfColumnsPriorToSum;

    /**
     * Creates a new census column detail item with:
     * Minimum = 0, Maximum = 0 and Random Percentage = 0
     */
    public NewCensusColumnDetails() {
        this.randomPercentage = 0.0;
        this.oldValueLookUpColumns = new ArrayList<>();
        this.numberOfColumnsPriorToSum = -1;
    }

    /**
     * Creates a new census column object from the provided parameters
     * @param newColumnHeader - the new header name
     * @param randomPercentage - the calculated random percentage
     */
    public NewCensusColumnDetails(String newColumnHeader, Double randomPercentage, ArrayList<Integer> oldValueLookUpColumns, Integer numberOfColumnsPriorToSum) {
        this.newColumnHeader = newColumnHeader;
        this.randomPercentage = randomPercentage;
        this.oldValueLookUpColumns = oldValueLookUpColumns;
        this.numberOfColumnsPriorToSum = numberOfColumnsPriorToSum;
    }

    /**
     * Gets the text value of the new column header
     * @return the string value of the header
     */
    public String getNewColumnHeader() {
        return newColumnHeader;
    }

    /**
     * Gets the list of column numbers for census values to calculate the new value
     * @return ArrayList of column numbers
     */
    public ArrayList<Integer> getOldValueLookUpColumns() {
        return oldValueLookUpColumns;
    }

    /**
     * Sets the list of columns used for calculating the new value
     * @param oldValueLookUpColumns - the arrayList of column numbers
     */
    public void setOldValueLookUpColumns(ArrayList<Integer> oldValueLookUpColumns) {
        this.oldValueLookUpColumns = oldValueLookUpColumns;
    }
    
    /**
     * Sets the string value of the column header
     * @param newColumnHeader - the new text for the header
     */
    public void setNewColumnHeader(String newColumnHeader) {
        this.newColumnHeader = newColumnHeader;
    }

    /**
     * Sets the new random percentage used for calculating the new column
     * @param randomPercentage - the percentage as a double (i.e. 70% = 0.75)
     */
    public void setRandomPercentage(Double randomPercentage) {
        this.randomPercentage = randomPercentage;
    }

    /**
     * Gets the random percentage that was calculated for this column
     * @return the random Percentage as a Double
     */
    public Double getRandomPercentage() {
        return randomPercentage;
    }

    /**
     * Gets value for the number of columns prior used for summing a new total column
     * @return the integer value of the number of columns prior to sum
     */
    public Integer getNumberOfColumnsPriorToSum() {
        return numberOfColumnsPriorToSum;
    }

    /**
     * Sets the value for the number of columns prior to the current column
     *  to sum for new total columns
     * @param numberOfColumnsPriorToSum - the number of columns prior needed for sum
     */
    public void setNumberOfColumnsPriorToSum(Integer numberOfColumnsPriorToSum) {
        this.numberOfColumnsPriorToSum = numberOfColumnsPriorToSum;
    }
}
