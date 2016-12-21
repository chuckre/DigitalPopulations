/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Used with the Markov Chain matrix for the Census Column information
 * Manages the values generated as output in the census enumerations .csv
 * @author ajohnson
 */
public class NewCensusColumnDetails {
    private String newColumnHeader;
    private Integer oldColumnNumber;
    private Double min;
    private Double max;
    private Double randomPercentage;

    /**
     * Creates a new census column detail item with:
     * Minimum = 0, Maximum = 0 and Random Percentage = 0
     */
    public NewCensusColumnDetails() {
        this.min = 0.0;
        this.max = 0.0;
        this.randomPercentage = 0.0;
    }

    /**
     * Creates a new census column object from the provided parameters
     * @param newColumnHeader - the new header name
     * @param oldColumnNumber - the old header number
     * @param min - the minimum value allowed
     * @param max - the maximum value allowed
     * @param randomPercentage - the calculated random percentage
     */
    public NewCensusColumnDetails(String newColumnHeader, Integer oldColumnNumber, Double min, Double max, Double randomPercentage) {
        this.newColumnHeader = newColumnHeader;
        this.oldColumnNumber = oldColumnNumber;
        this.min = min;
        this.max = max;
        this.randomPercentage = randomPercentage;
    }

    /**
     * Gets the minimum value for the census column
     * @return the Minimum value as a Double
     */
    public Double getMin() {
        return min;
    }

    /**
     * Sets the minimum value for the census column
     * @param min the new minimum value as a double
     */
    public void setMin(Double min) {
        this.min = min;
    }

    /**
     * Gets the maximum value for the census column
     * @return the Maximum value as a Double
     */
    public Double getMax() {
        return max;
    }

    /**
     * Sets the maximum value for the census column
     * @param max the Maximum value as a double
     */
    public void setMax(Double max) {
        this.max = max;
    }

    /**
     * Gets the text value of the new column header
     * @return the string value of the header
     */
    public String getNewColumnHeader() {
        return newColumnHeader;
    }

    /**
     * Sets the string value of the column header
     * @param newColumnHeader - the new text for the header
     */
    public void setNewColumnHeader(String newColumnHeader) {
        this.newColumnHeader = newColumnHeader;
    }

    /**
     * Used for calculating the percentage, multiplied by the random number for the new value
     * @return - the old column number as an integer
     */
    public Integer getOldColumnNumber() {
        return oldColumnNumber;
    }

    /**
     * Set the old column number
     * @param oldColumnNumber - the new value to set as an Integer
     */
    public void setOldColumnNumber(Integer oldColumnNumber) {
        this.oldColumnNumber = oldColumnNumber;
    }

    /**
     * Gets the random percentage that was calculated for this column
     * @return the random Percentage as a Double
     */
    public Double getRandomPercentage() {
        return randomPercentage;
    }
    
    /**
     * Calculates a new random percentage for the current census column
     * Calculated as the next random double between the minimum and maximum values
     * Rounded to a single decimal point
     */
    public void calculateNewRandomPercentage() {
        if(this.min.equals(this.max)){
            this.randomPercentage = this.max;
        }else {
            this.randomPercentage = ThreadLocalRandom.current().nextDouble(min, max);
            this.randomPercentage =Math.round(this.randomPercentage  * 100.0) / 100.0;
        }
    }
}
