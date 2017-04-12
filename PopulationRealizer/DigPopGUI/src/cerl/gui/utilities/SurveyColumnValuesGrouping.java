/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;

/**
 * The object for grouping survey column values into a new item for use in the Markov Chain analysis
 * @author ajohnson
 */
public class SurveyColumnValuesGrouping implements Cloneable {
    
    private String userDefinedDescription;
    private ArrayList<SurveyColumnValue> surveyColumnValues;
    private long groupingTotal = 0;

    /**
     * Creates a new, empty survey column value grouping
     */
    public SurveyColumnValuesGrouping() {
        this.surveyColumnValues = new ArrayList<SurveyColumnValue>();
    }

    /**
     * Creates a new survey column value grouping from the provided parameters
     * @param userDefinedDescription - the new name of the group provided by the user
     * @param surveyColumnValues - the survey column values to be grouped together
     */
    public SurveyColumnValuesGrouping(String userDefinedDescription, ArrayList<SurveyColumnValue> surveyColumnValues) {
        this.userDefinedDescription = userDefinedDescription;
        this.surveyColumnValues = surveyColumnValues;
    }

    /**
     * The total value, summing all the times each grouped value has been used in the column of the survey data
     * @return the count of usage for all values in the group
     */
    public long getGroupingTotal() {
        return groupingTotal;
    }

    /**
     * Calculates the new total for all values in the group
     * @return the count of usage for all values in the group
     */
    public long calculateGroupingTotal() {
        this.surveyColumnValues.stream().forEach((s) -> {
           this.groupingTotal += s.getNumberOfTimesUsed();
        });
        
        return groupingTotal;
    }

    /**
     * Gets the new user provided name for the group
     * @return the user provided name of the group
     */
    public String getUserDefinedDescription() {
        return userDefinedDescription;
    }

    /**
     * Sets the name of the group, as provided by the user
     * @param userDefinedDescription - the new name for the group
     */
    public void setUserDefinedDescription(String userDefinedDescription) {
        this.userDefinedDescription = userDefinedDescription;
    }

    /**
     * Gets the list of survey column values grouped together for the Markov Chain Analysis
     * @return the ArrayList of survey column values in the group
     */
    public ArrayList<SurveyColumnValue> getSurveyColumnValues() {
        return surveyColumnValues;
    }

    /**
     * Sets the group of survey column values 
     * @param surveyColumnValues - the new grouping of survey column values
     */
    public void setSurveyColumnValues(ArrayList<SurveyColumnValue> surveyColumnValues) {
        this.surveyColumnValues = surveyColumnValues;
    }
    
    /**
     * Adds a single survey column value to an existing group
     * @param surveyColumnValue - the survey column value to add to the group
     */
    public void addToSurveyColumnValues(SurveyColumnValue surveyColumnValue) {
        this.surveyColumnValues.add(surveyColumnValue);
    }
    
    /**
     * Gets all the row Ids as a single string for the group
     * @return 
     */
    public String getAllRowIdsAsString(){
        return surveyColumnValues.toString();
    }
    
    /**
     * Gets all the values as a comma delimited string for the group
     * @return 
     */
    public String getAllValuesAsString(){
        String returnValue = "";
        
        for(SurveyColumnValue s : surveyColumnValues){
            returnValue = returnValue + s.getValue() + ",";
        }
        returnValue = returnValue.substring(0, returnValue.lastIndexOf(","));
        return returnValue;
    }
    
    /**
     * Creates a clone of the survey column value grouping
     * @return a new cloned survey column value grouping
     * @throws CloneNotSupportedException 
     */
    public SurveyColumnValuesGrouping clone() throws CloneNotSupportedException 
    {
        SurveyColumnValuesGrouping clonedSurveyColumnValuesGrouping = (SurveyColumnValuesGrouping) super.clone();
 
        return clonedSurveyColumnValuesGrouping;
    }

    /**
     * Provides the user defined description if it exists, otherwise the list of all row Ids for the group
     * @return 
     */
    @Override
    public String toString() {
        if(userDefinedDescription == null || userDefinedDescription.equals("")){
            return getAllRowIdsAsString();
        }
        else{
            return userDefinedDescription;
        }
    }
}
