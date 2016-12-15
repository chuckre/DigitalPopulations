/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.Optional;

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
    private ArrayList<MarkovChain> markovChains;

    public CensusSurveyClasses() {
        this.censusClasses = new ArrayList<Class>();
        this.populationMicroDataClasses = new ArrayList<Class>();
        this.householdMicroDataClasses = new ArrayList<Class>();
        this.markovChains = new ArrayList<MarkovChain>();
    }

    public CensusSurveyClasses(
            ArrayList<Class> censusClasses,
            ArrayList<Class> populationMicroDataClasses,
            ArrayList<Class> householdMicroDataClasses, 
            long censusTotal, 
            long surveyTotal,
            ArrayList<MarkovChain> markovChains) {
        this.censusClasses = censusClasses;
        this.populationMicroDataClasses = populationMicroDataClasses;
        this.householdMicroDataClasses = householdMicroDataClasses;
        this.censusTotal = censusTotal;
        this.surveyTotal = surveyTotal;
        this.markovChains = markovChains;
    }

    public ArrayList<MarkovChain> getMarkovChains() {
        return markovChains;
    }
    
    public MarkovChain getMarkovChainByID(int id) {
        Optional<MarkovChain> foundFromStream = markovChains.stream().filter(m-> m.getId() == id).findFirst();
                    
        if(foundFromStream.isPresent()){
            return foundFromStream.get();
        }
        else {
            return null;
        }
    }

    public void setMarkovChains(ArrayList<MarkovChain> markovChains) {
        this.markovChains = markovChains;
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
    
    public void addMarkovChains(MarkovChain markovChain) {
        this.markovChains.add(markovChain);
    }
}
