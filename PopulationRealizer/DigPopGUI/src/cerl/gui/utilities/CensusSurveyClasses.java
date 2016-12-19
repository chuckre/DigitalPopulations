/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The Census and Survey information for all Markov Chains
 * @author ajohnson
 */
public class CensusSurveyClasses {
    private ArrayList<Class> censusClasses;
    private ArrayList<Class> populationMicroDataClasses;
    private ArrayList<Class> householdMicroDataClasses;
    private long censusTotal;
    private long surveyTotal;
    private ArrayList<MarkovChain> markovChains;

    /**
     * Creates a new, empty, CensusSurveyClass object
     */
    public CensusSurveyClasses() {
        this.censusClasses = new ArrayList<Class>();
        this.populationMicroDataClasses = new ArrayList<Class>();
        this.householdMicroDataClasses = new ArrayList<Class>();
        this.markovChains = new ArrayList<MarkovChain>();
    }

    /**
     * Creates a new Census Survey Class object
     * @param censusClasses - the ArrayList of census values
     * @param populationMicroDataClasses - the ArrayList of population Microdata
     * @param householdMicroDataClasses - the ArrayList of household Microdata
     * @param censusTotal - the long value calculating the total census values
     * @param surveyTotal - the long value calculating the total survey values
     * @param markovChains - the ArrayList of MarkovChains
     */
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

    /**
     * Gets the list of MarkovChains
     * @return ArrayList of MarkovChains
     */
    public ArrayList<MarkovChain> getMarkovChains() {
        return markovChains;
    }
    
    /**
     * Gets a specific Markov Chain by unique ID
     * @param id - the unique ID of the MarkovChain
     * @return MarkovChain if found, or null if not
     */
    public MarkovChain getMarkovChainByID(int id) {
        Optional<MarkovChain> foundFromStream = markovChains.stream().filter(m-> m.getId() == id).findFirst();
                    
        if(foundFromStream.isPresent()){
            return foundFromStream.get();
        }
        else {
            return null;
        }
    }

    /**
     * Sets the ArrayList of MarkovChain
     * @param markovChains - the new list
     */
    public void setMarkovChains(ArrayList<MarkovChain> markovChains) {
        this.markovChains = markovChains;
    }

    /**
     * Gets the ArrayList of Classes for all censusClasses
     * @return ArrayList of Class objects
     */
    public ArrayList<Class> getCensusClasses() {
        return censusClasses;
    }

    /**
     * Sets the Census Classes ArrayList
     * @param censusClasses - an ArrayList of Class objects
     */
    public void setCensusClasses(ArrayList<Class> censusClasses) {
        this.censusClasses = censusClasses;
    }

    /**
     * Gets the Population Microdata
     * @return an ArrayList of Class objects
     */
    public ArrayList<Class> getPopulationMicroDataClasses() {
        return populationMicroDataClasses;
    }

    /**
     * Sets the Population Microdata
     * @param populationMicroDataClasses - An ArrayList of Class objects
     */
    public void setPopulationMicroDataClasses(ArrayList<Class> populationMicroDataClasses) {
        this.populationMicroDataClasses = populationMicroDataClasses;
    }

    /**
     * Gets the Household Microdata
     * @return - An ArrayList of Class objects
     */
    public ArrayList<Class> getHouseholdMicroDataClasses() {
        return householdMicroDataClasses;
    }

    /**
     * Sets the Household Microdata
     * @param householdMicroDataClasses - An ArrayList of Class objects
     */
    public void setHouseholdMicroDataClasses(ArrayList<Class> householdMicroDataClasses) {
        this.householdMicroDataClasses = householdMicroDataClasses;
    }

    /**
     * Gets the current census total
     * @return a Long value of the census total
     */
    public long getCensusTotal() {
        return censusTotal;
    }

    /**
     * Sets the census total
     * @param censusTotal - the new long value of the new census total
     */
    public void setCensusTotal(long censusTotal) {
        this.censusTotal = censusTotal;
    }

    /**
     * Gets the survey total
     * @return - the long value of the surveyTotal
     */
    public long getSurveyTotal() {
        return surveyTotal;
    }

    /**
     * Sets the survey total
     * @param surveyTotal - the new long value of the surveyTotal
     */
    public void setSurveyTotal(long surveyTotal) {
        this.surveyTotal = surveyTotal;
    }
    
    /**
     * Adds the provided value to the surveyTotal
     * @param addValue - the long value to add to the current total
     */
    public void addToSurveyTotal(long addValue){
        this.surveyTotal = this.surveyTotal + addValue;
    }
    
    /**
     * Adds the provided value to the census Total
     * @param addValue - the long value to add to the current total
     */
    public void addToCensusTotal(long addValue){
        this.censusTotal = this.censusTotal + addValue;
    }
    
    /**
     * Adds a new census Class to the ArrayList of census Classes
     * @param censusClass - the new census class to add
     */
    public void addCensusClass(Class censusClass) {
        this.censusClasses.add(censusClass);
    }
    
    /**
     * Adds a new Household Microdata class to the ArrayList of household microdata
     * @param householdMicroDataClass - the new class to add
     */
    public void addhouseholdMicroDataClass(Class householdMicroDataClass) {
        this.householdMicroDataClasses.add(householdMicroDataClass);
    }
    
    /**
     * Adds a new Population Microdata class to the ArrayList of population microdata
     * @param populationMicroDataClass - the new class to add
     */
    public void addpopulationMicroDataClass(Class populationMicroDataClass) {
        this.populationMicroDataClasses.add(populationMicroDataClass);
    }
    
    /**
     * Adds a new Markov Chain to the ArrayList of Markov Chains
     * @param markovChain - the new MarkovChain to add
     */
    public void addMarkovChains(MarkovChain markovChain) {
        this.markovChains.add(markovChain);
    }
}
