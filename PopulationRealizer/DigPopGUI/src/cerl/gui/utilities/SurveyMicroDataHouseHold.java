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
public class SurveyMicroDataHouseHold {
    private Object insp;
    private Object RT;
    private Object SERIALNO;
    private Object DIVISION;
    private Object PUMA00;
    private Object PUMA10;
    private Object REGION;
    private Object ST;
    private ArrayList<SurveyMicroDataHouseHoldClass> surveyMicroDataHouseHoldClasses;

    public SurveyMicroDataHouseHold() {
        this.surveyMicroDataHouseHoldClasses = new ArrayList<SurveyMicroDataHouseHoldClass>();
    }

    public SurveyMicroDataHouseHold(Object insp, Object RT, Object SERIALNO, Object DIVISION, Object PUMA00, Object PUMA10, Object REGION, Object ST, ArrayList<SurveyMicroDataHouseHoldClass> surveyMicroDataHouseHoldClasses) {
        this.insp = insp;
        this.RT = RT;
        this.SERIALNO = SERIALNO;
        this.DIVISION = DIVISION;
        this.PUMA00 = PUMA00;
        this.PUMA10 = PUMA10;
        this.REGION = REGION;
        this.ST = ST;
        this.surveyMicroDataHouseHoldClasses = surveyMicroDataHouseHoldClasses;
    }

    public Object getInsp() {
        return insp;
    }

    public void setInsp(Object insp) {
        this.insp = insp;
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

    public Object getDIVISION() {
        return DIVISION;
    }

    public void setDIVISION(Object DIVISION) {
        this.DIVISION = DIVISION;
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

    public Object getREGION() {
        return REGION;
    }

    public void setREGION(Object REGION) {
        this.REGION = REGION;
    }

    public Object getST() {
        return ST;
    }

    public void setST(Object ST) {
        this.ST = ST;
    }

    public ArrayList<SurveyMicroDataHouseHoldClass> getSurveyMicroDataHouseHoldClasses() {
        return surveyMicroDataHouseHoldClasses;
    }

    public void setSurveyMicroDataHouseHoldClasses(ArrayList<SurveyMicroDataHouseHoldClass> surveyMicroDataHouseHoldClasses) {
        this.surveyMicroDataHouseHoldClasses = surveyMicroDataHouseHoldClasses;
    }
    
    public void addSurveyMicroDataHouseHoldClass(SurveyMicroDataHouseHoldClass surveyMicroDataHouseHoldClass){
        this.surveyMicroDataHouseHoldClasses.add(surveyMicroDataHouseHoldClass);
    }
    
}
