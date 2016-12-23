/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Used with the Markov Chain matrix for the Census Column information
 * Manages the values generated as output in the census enumerations .csv
 * @author ajohnson
 */
public class NewCensusColumnDetails {
    private String newColumnHeader;
    
    private Double randomPercentage;
    private ArrayList<Integer> oldValueLookUpColumns;

    /**
     * Creates a new census column detail item with:
     * Minimum = 0, Maximum = 0 and Random Percentage = 0
     */
    public NewCensusColumnDetails() {
        this.randomPercentage = 0.0;
        this.oldValueLookUpColumns = new ArrayList<>();
    }

    

    /**
     * Creates a new census column object from the provided parameters
     * @param newColumnHeader - the new header name
     * @param randomPercentage - the calculated random percentage
     */
    public NewCensusColumnDetails(String newColumnHeader, Double randomPercentage, ArrayList<Integer> oldValueLookUpColumns) {
        this.newColumnHeader = newColumnHeader;
        this.randomPercentage = randomPercentage;
        this.oldValueLookUpColumns = oldValueLookUpColumns;
    }

    /**
     * Gets the text value of the new column header
     * @return the string value of the header
     */
    public String getNewColumnHeader() {
        return newColumnHeader;
    }

    public ArrayList<Integer> getOldValueLookUpColumns() {
        return oldValueLookUpColumns;
    }

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
     * Calculates a new random percentage for the current census column
     * Calculated as the next random double between the minimum and maximum values
     * Rounded to a single decimal point
     */
//    public void calculateNewRandomPercentage() {
//        if(this.min.equals(this.max)){
//            this.randomPercentage = this.max;
//        }else {
//            this.randomPercentage = ThreadLocalRandom.current().nextDouble(min, max);
//            this.randomPercentage =Math.round(this.randomPercentage  * 100.0) / 100.0;
//        }
//    }
}
