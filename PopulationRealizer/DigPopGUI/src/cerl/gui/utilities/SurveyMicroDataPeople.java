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
public class SurveyMicroDataPeople {
    private Object RT;
    private Object SERIALNO;
    private Object SPORDER;
    private Object PUMA00;
    private Object PUMA10;
    private Object ST;
    private ArrayList<SurveyMicroDataPeopleClass> surveyMicroDataPeopleClasses;
//    private SurveyMicroDataPeopleClass[] surveyMicroDataPeopleClasses;

    public SurveyMicroDataPeople() {
        surveyMicroDataPeopleClasses = new ArrayList<SurveyMicroDataPeopleClass>();
    }

    public SurveyMicroDataPeople(Object RT, Object SERIALNO, Object SPORDER, Object PUMA00, Object PUMA10, Object ST) {
        this.RT = RT;
        this.SERIALNO = SERIALNO;
        this.SPORDER = SPORDER;
        this.PUMA00 = PUMA00;
        this.PUMA10 = PUMA10;
        this.ST = ST;
    }

    public SurveyMicroDataPeople(Object RT, Object SERIALNO, Object SPORDER, Object PUMA00, Object PUMA10, Object ST, ArrayList<SurveyMicroDataPeopleClass> surveyMicroDataPeopleClasses) {
        this.RT = RT;
        this.SERIALNO = SERIALNO;
        this.SPORDER = SPORDER;
        this.PUMA00 = PUMA00;
        this.PUMA10 = PUMA10;
        this.ST = ST;
        this.surveyMicroDataPeopleClasses = surveyMicroDataPeopleClasses;
    }

    

    public Object getRT() {
        return RT;
    }

    public void setRT(Object RT) {
        this.RT = RT;
    }

    public Object getSERIALNO() {
        return SERIALNO;
    }

    public void setSERIALNO(Object SERIALNO) {
        this.SERIALNO = SERIALNO;
    }

    public Object getSPORDER() {
        return SPORDER;
    }

    public void setSPORDER(Object SPORDER) {
        this.SPORDER = SPORDER;
    }

    public Object getPUMA00() {
        return PUMA00;
    }

    public void setPUMA00(Object PUMA00) {
        this.PUMA00 = PUMA00;
    }

    public Object getPUMA10() {
        return PUMA10;
    }

    public void setPUMA10(Object PUMA10) {
        this.PUMA10 = PUMA10;
    }

    public Object getST() {
        return ST;
    }

    public void setST(Object ST) {
        this.ST = ST;
    }

    public ArrayList<SurveyMicroDataPeopleClass> getSurveyMicroDataPeopleClasses() {
        return surveyMicroDataPeopleClasses;
    }

    public void setSurveyMicroDataPeopleClasses(ArrayList<SurveyMicroDataPeopleClass> surveyMicroDataPeopleClasses) {
        this.surveyMicroDataPeopleClasses = surveyMicroDataPeopleClasses;
    }

    
    
    
    
}
