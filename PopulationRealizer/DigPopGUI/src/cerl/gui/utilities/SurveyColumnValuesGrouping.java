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
public class SurveyColumnValuesGrouping {
    
    private String userDefinedDescription;
    private ArrayList<SurveyColumnValue> surveyColumnValues;

    public SurveyColumnValuesGrouping() {
    }

    public SurveyColumnValuesGrouping(String userDefinedDescription, ArrayList<SurveyColumnValue> surveyColumnValues) {
        this.userDefinedDescription = userDefinedDescription;
        this.surveyColumnValues = surveyColumnValues;
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
    
    
    public String getAllRowIdsAsString(){
        return surveyColumnValues.toString();
    }
    
}
