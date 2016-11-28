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
public class SurveyMicroDataPeoples {
    private ArrayList<SurveyMicroDataPeople> surveyMicroDataPeoples;
    
  //  private SurveyMicroDataPeople[] surveyMicroDataPeoples;

    public SurveyMicroDataPeoples() {
        surveyMicroDataPeoples = new ArrayList<SurveyMicroDataPeople>();
    }
    
    

    public ArrayList<SurveyMicroDataPeople> getSurveyMicroDataPeoples() {
        return surveyMicroDataPeoples;
    }

    public void setSurveyMicroDataPeoples(ArrayList<SurveyMicroDataPeople> surveyMicroDataPeoples) {
        this.surveyMicroDataPeoples = surveyMicroDataPeoples;
    }
    
    public void addSurveyMicroDataPeople(SurveyMicroDataPeople surveyMicroDataPeople) {
        this.surveyMicroDataPeoples.add(surveyMicroDataPeople);
    }

     public void addSurveyMicroDataPeople(Object RT, Object SERIALNO, Object SPORDER, Object PUMA00, Object PUMA10, Object ST, ArrayList<SurveyMicroDataPeopleClass> surveyMicroDataPeopleClasses) {

         this.surveyMicroDataPeoples.add( new SurveyMicroDataPeople(
                    RT,
                    SERIALNO,
                    SPORDER,
                    PUMA00,
                    PUMA10,
                    ST,
                    surveyMicroDataPeopleClasses));
    }
}
