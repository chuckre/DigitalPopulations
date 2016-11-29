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
public class CensusSurveyClasses {
    private ArrayList<Class> censusClasses;
    private ArrayList<Class> populationMicroDataClasses;
    private ArrayList<Class> householdMicroDataClasses;
    private long censusTotal;
    private long surveyTotal;

    public CensusSurveyClasses() {
        this.censusClasses = new ArrayList<Class>();
        this.populationMicroDataClasses = new ArrayList<Class>();
        this.householdMicroDataClasses = new ArrayList<Class>();
    }

    public CensusSurveyClasses(
            ArrayList<Class> censusClasses,
            ArrayList<Class> populationMicroDataClasses,
            ArrayList<Class> householdMicroDataClasses, 
            long censusTotal, 
            long surveyTotal) {
        this.censusClasses = censusClasses;
        this.populationMicroDataClasses = populationMicroDataClasses;
        this.householdMicroDataClasses = householdMicroDataClasses;
        this.censusTotal = censusTotal;
        this.surveyTotal = surveyTotal;
    }

    public ArrayList<Class> getCensusClasses() {
        return censusClasses;
    }

    public void setCensusClasses(ArrayList<Class> censusClasses) {
        this.censusClasses = censusClasses;
    }

    public ArrayList<Class> getPopulationMicroDataClasses() {
        return populationMicroDataClasses;
    }

    public void setPopulationMicroDataClasses(ArrayList<Class> populationMicroDataClasses) {
        this.populationMicroDataClasses = populationMicroDataClasses;
    }

    public ArrayList<Class> getHouseholdMicroDataClasses() {
        return householdMicroDataClasses;
    }

    public void setHouseholdMicroDataClasses(ArrayList<Class> householdMicroDataClasses) {
        this.householdMicroDataClasses = householdMicroDataClasses;
    }

    public long getCensusTotal() {
        return censusTotal;
    }

    public void setCensusTotal(long censusTotal) {
        this.censusTotal = censusTotal;
    }

    public long getSurveyTotal() {
        return surveyTotal;
    }

    public void setSurveyTotal(long surveyTotal) {
        this.surveyTotal = surveyTotal;
    }
    
    public void addToSurveyTotal(long addValue){
        this.surveyTotal = this.surveyTotal + addValue;
    }
    
    public void addToCensusTotal(long addValue){
        this.censusTotal = this.censusTotal + addValue;
    }
    
    public void addCensusClass(Class censusClass) {
        this.censusClasses.add(censusClass);
    }
    
    public void addhouseholdMicroDataClass(Class householdMicroDataClass) {
        this.householdMicroDataClasses.add(householdMicroDataClass);
    }
    
    public void addpopulationMicroDataClass(Class populationMicroDataClass) {
        this.populationMicroDataClasses.add(populationMicroDataClass);
    }
}
