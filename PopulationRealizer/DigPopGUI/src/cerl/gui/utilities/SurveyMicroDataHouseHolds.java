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
public class SurveyMicroDataHouseHolds {
    private ArrayList<SurveyMicroDataHouseHold> surveyMicroDataHouseHolds;

    public SurveyMicroDataHouseHolds() {
        this.surveyMicroDataHouseHolds = new ArrayList<SurveyMicroDataHouseHold>();
    }

    public ArrayList<SurveyMicroDataHouseHold> getSurveyMicroDataHouseHolds() {
        return surveyMicroDataHouseHolds;
    }

    public void setSurveyMicroDataHouseHolds(ArrayList<SurveyMicroDataHouseHold> surveyMicroDataHouseHolds) {
        this.surveyMicroDataHouseHolds = surveyMicroDataHouseHolds;
    }
    
    public void addSurveyMicroDataHouseHolds(SurveyMicroDataHouseHold surveyMicroDataHouseHold) {
        this.surveyMicroDataHouseHolds.add(surveyMicroDataHouseHold);
    }
}
