/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;

/**
 *
 * @author ajohnson
 */
public class SurveyColumnValuesGrouping implements Cloneable {
    
    private String userDefinedDescription;
    private ArrayList<SurveyColumnValue> surveyColumnValues;
    private long groupingTotal = 0;

    public SurveyColumnValuesGrouping() {
        this.surveyColumnValues = new ArrayList<SurveyColumnValue>();
    }

    public SurveyColumnValuesGrouping(String userDefinedDescription, ArrayList<SurveyColumnValue> surveyColumnValues) {
        this.userDefinedDescription = userDefinedDescription;
        this.surveyColumnValues = surveyColumnValues;
    }

    public long getGroupingTotal() {
        return groupingTotal;
    }

    public long calculateGroupingTotal() {
        this.surveyColumnValues.stream().forEach((s) -> {
           this.groupingTotal += s.getSurveyColumnValueTotal();
        });
        
        return groupingTotal;
    }

    public String getUserDefinedDescription() {
        return userDefinedDescription;
    }

    public void setUserDefinedDescription(String userDefinedDescription) {
        this.userDefinedDescription = userDefinedDescription;
    }

    public ArrayList<SurveyColumnValue> getSurveyColumnValues() {
        return surveyColumnValues;
    }

    public void setSurveyColumnValues(ArrayList<SurveyColumnValue> surveyColumnValues) {
        this.surveyColumnValues = surveyColumnValues;
    }
    
    public void addToSurveyColumnValues(SurveyColumnValue surveyColumnValue) {
        this.surveyColumnValues.add(surveyColumnValue);
    }
    
    
    public String getAllRowIdsAsString(){
        return surveyColumnValues.toString();
    }
    
    public SurveyColumnValuesGrouping clone() throws CloneNotSupportedException 
    {
        SurveyColumnValuesGrouping clonedSurveyColumnValuesGrouping = (SurveyColumnValuesGrouping) super.clone();
 
        return clonedSurveyColumnValuesGrouping;
    }

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
