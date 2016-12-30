/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.standard.utilities.customTableCell;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The Markov Chain object - as a matrix of census and survey columns selected
 * Used for creating the Fitting Criteria file and Goal Relationship file
 * @author ajohnson
 */
public class MarkovChain {
    private ArrayList<String> FittingCriteriaColumnNames;
    private ArrayList<ArrayList<customTableCell>> FittingCriteriaCellValues;
    private ArrayList<Traits> FittingTraits;
    private ArrayList<Weights> TraitWeights;
    private GoalRelationshipFile goalRelationshipFile;
    private Double traitWeightLocation;
    
    //Used in Step 6 - Generate Trait Clusters
    private ArrayList<ArrayList<Object>> traitClusters;
    private ArrayList<String> traitList;
    private ArrayList<Cluster> traitPositionClusters;
    
    private String markovName;
    private ArrayList<MarkovChainRows> markovChainTableCells;
    private int[][] emptyCells;
    private ArrayList<String> columnNames;
    private ArrayList<cerl.gui.utilities.Class> censusClasses;
    private cerl.gui.utilities.Class selectSurveyClass;
    private int id;
    private long allCensusTotal = 0;
    
    private ArrayList<NewCensusColumnDetails> newCensusColumnDetails;

    /**
     * Creates a new, empty Markov Chain matrix
     */
    public MarkovChain() {
        this.censusClasses = censusClasses = new ArrayList<cerl.gui.utilities.Class>();
        this.selectSurveyClass = new cerl.gui.utilities.Class();
        this.goalRelationshipFile = new GoalRelationshipFile();
        this.markovChainTableCells = new ArrayList<MarkovChainRows>();
        this.newCensusColumnDetails  = new ArrayList<NewCensusColumnDetails>();
    }
    
    /**
     * Creates a new Markov Chain matrix from the provided parameters
     * @param markovName - the name of the Markov Chain matrix
     * @param censusClasses - the census data selected to be used in the analysis in Step 3
     * @param selectSurveyClass - the survey values used in the analysis, already grouped together from Step 3
     * @param id - the unique identifier of the current Markov Chain matrix
     */
    public MarkovChain(String markovName, ArrayList<Class> censusClasses, Class selectSurveyClass, int id) {
        this.markovName = markovName;
        this.censusClasses = censusClasses;
        this.selectSurveyClass = selectSurveyClass;
        this.id = id;
        this.goalRelationshipFile = new GoalRelationshipFile();
        this.markovChainTableCells = new ArrayList<MarkovChainRows>();
        this.newCensusColumnDetails  = new ArrayList<NewCensusColumnDetails>();
    }

    /**
     * Gets the list of census column details used in the Markov Chain
     * @return the list of new census column details
     */
    public ArrayList<NewCensusColumnDetails> getNewCensusColumnDetails() {
        return newCensusColumnDetails;
    }

    /**
     * Sets the census column details to be used in the Markov Chain
     * @param newCensusColumnDetails - the new set of census columns to use
     */
    public void setNewCensusColumnDetails(ArrayList<NewCensusColumnDetails> newCensusColumnDetails) {
        this.newCensusColumnDetails = newCensusColumnDetails;
    }
    
    /**
     * Adds a new census column to the existing list in the Markov Chain matrix
     * @param newCensusColumnDetail - the new census column to add to the list
     */
    public void addNewCensusColumnDetails(NewCensusColumnDetails newCensusColumnDetail) {
        this.newCensusColumnDetails.add(newCensusColumnDetail);
    }
    
    /**
     * Gets the list of Fitting Criteria traits associated with the Markov Chain matrix
     * @return - the list of Traits
     */
    public ArrayList<Traits> getFittingTraits() {
        return FittingTraits;
    }

    /**
     * Sets the list of Fitting Criteria Traits associated with the Markov Chain
     * @param FittingTraits - the new list of Traits
     */
    public void setFittingTraits(ArrayList<Traits> FittingTraits) {
        this.FittingTraits = FittingTraits;
    }
    
    /**
     * Gets the list of weights associated with the Fitting Criteria traits
     * @return - the list of weights
     */
     public ArrayList<Weights> getTraitWeights() {
        return TraitWeights;
    }

     /**
      * Sets the list of weights for the Fitting Criteria traits
      * @param TraitWeights - the new list of weights
      */
    public void setTraitWeights(ArrayList<Weights> TraitWeights) {
        this.TraitWeights = TraitWeights;
    }
    
    /**
     * Gets the list of all Fitting Criteria columns
     * @return - the list of the column names
     */
     public ArrayList<String> getFittingCriteriaColumnNames() {
        return FittingCriteriaColumnNames;
    }

    /**
     * Sets the list of column names for the Fitting Criteria
     * @param FittingCriteriaColumnNames - the new list of column names
     */
    public void setFittingCriteriaColumnNames(ArrayList<String> FittingCriteriaColumnNames) {
        this.FittingCriteriaColumnNames = FittingCriteriaColumnNames;
    }

    /**
     * Gets the 2D list as a row/column list of values for all fitting criteria cells
     * @return - the 2D arraylist of all the customTableCells
     */
    @XmlElement(name="fittingCriteriaCellValues")
    public ArrayList<ArrayList<customTableCell>> getFittingCriteriaCellValues() {
        return FittingCriteriaCellValues;
    }

    /**
     * Sets the full list of cells based on a 2D arraylist as row/column combos
     * @param FittingCriteriaCellValues - the new 2D list of cells
     */
    public void setFittingCriteriaCellValues(ArrayList<ArrayList<customTableCell>> FittingCriteriaCellValues) {
        this.FittingCriteriaCellValues = FittingCriteriaCellValues;
    }

    /**
     * Recalculates the total for all census classes
     * @return the new census total 
     */
    public long getAllCensusTotal() {
        this.censusClasses.stream().forEach((c) -> {
            this.allCensusTotal += c.getClassTotal();
        });
        
        return allCensusTotal;
    }

    /**
     * Gets the unique identifier of the current Markov Chain matrix
     * @return - the unique ID of the matrix
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the current Markov Chain matrix
     * @param id - the new unique identifier for this Markov Chain matrix
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the user entered name of the Markov Chain matrix
     * @return - the text value of the user entered name for the Markov Chain
     */
    public String getMarkovName() {
        return markovName;
    }

    /**
     * Sets the name for the Markov Chain based on the user entered value
     * @param markovName - the new name for the Markov Chain
     */
    public void setMarkovName(String markovName) {
        this.markovName = markovName;
    }

    /**
     * Gets the list of census classes used in the Markov Chain
     * @return - the list of census classes
     */
    public ArrayList<Class> getCensusClasses() {
        return censusClasses;
    }

    /**
     * Sets the list of census classes used in the Markov Chain
     * @param censusClasses - the new list of census Classes
     */
    public void setCensusClasses(ArrayList<Class> censusClasses) {
        this.censusClasses = censusClasses;
    }

    /**
     * Gets the selected survey class
     * @return - the selected survey class
     */
    @XmlElement
    public cerl.gui.utilities.Class getSelectSurveyClass() {
        return selectSurveyClass;
    }

    /**
     * Sets the selected survey class for the current Markov chain
     * @param selectSurveyClass - the new survey class
     */
    public void setSelectSurveyClass(Class selectSurveyClass) {
        this.selectSurveyClass = selectSurveyClass;
    }
    
    /**
     * Gets all the selected census survey classes with the user defined names
     * @return - the list of all census class names
     */
    public ArrayList<String> getAllSelectedCensusClassesUserDefinedNames() {
        ArrayList<String> userDefinedNames =  new ArrayList<>();
        
        censusClasses.stream().forEach((c) -> {
            if(c.getUserDefinedDescription() == null || c.getUserDefinedDescription().equals("")){
                userDefinedNames.add(c.getClassName());
            }
            else{
                userDefinedNames.add(c.getUserDefinedDescription());
            }
        });
        
        return userDefinedNames;
    }
    
    /**
     * Gets the trait weight location field
     * @return the value of the trait weight location
     */
    public Double getTraitWeightLocation() {
        return traitWeightLocation;
    }

    /**
     * Sets the trait weight location field
     * @param traitWeightLocation - the new trait weight location
     */
    public void setTraitWeightLocation(Double traitWeightLocation) {
        this.traitWeightLocation = traitWeightLocation;
    }
    

    /**
     * Adds the list of constraint maps to the list for the Goal Relationship File
     * @param constraintMapsFilePaths - the new list of constraint map file names
     */
    public void addConstraintMaps(ArrayList<ConstraintMap> constraintMaps) {
        this.goalRelationshipFile.getForbids().clear();
        constraintMaps.stream().forEach((c) -> {
            this.goalRelationshipFile.addForbid(c.getForbid());
        });
    }
    
    /**
     * Gets the Goal Relationship File object
     * @return the Goal Relationship File object
     */
    public GoalRelationshipFile getGoalRelationshipFile() {
        return goalRelationshipFile;
    }

    /**
     * Sets the Goal Relationship File object for the specific Markov Chain
     * @param goalRelationshipFile - the new Goal Relationship File
     */
    public void setGoalRelationshipFile(GoalRelationshipFile goalRelationshipFile) {
        this.goalRelationshipFile = goalRelationshipFile;
    }
    
    /**
     * Gets the list of Trait Clusters as a 2D ArrayList
     * @return - the 2D ArrayList of trait clusters
     */
    public ArrayList<ArrayList<Object>> getTraitClusters() {
        return traitClusters;
    }

    /**
     * Sets the 2D ArrayList of trait clusters
     * @param traitClusters - the new ArrayList of trait clusters
     */
    public void setTraitClusters(ArrayList<ArrayList<Object>> traitClusters) {
        this.traitClusters = traitClusters;
    }

    /**
     * Gets the list of Traits
     * @return - the ArrayList of traits
     */
    public ArrayList<String> getTraitList() {
        return traitList;
    }

    /**
     * Sets the list of traits
     * @param traitList - the new ArrayList of traits
     */
    public void setTraitList(ArrayList<String> traitList) {
        this.traitList = traitList;
    }
    
    /**
     * Gets the ArrayList of Trait Position Clusters
     * @return - an ArrayList of Clusters
     */
    public ArrayList<Cluster> getTraitPositionClusters() {
        return traitPositionClusters;
    }

    /**
     * Sets the ArrayList of trait position clusters
     * @param traitPositionClusters - the new ArrayList of clusters
     */
    public void setTraitPositionClusters(ArrayList<Cluster> traitPositionClusters) {
        this.traitPositionClusters = traitPositionClusters;
    }

    /**
     * Gets the full ArrayList (by rows) of Markov Chain Table cells
     * @return - the ArrayList of all rows of cells in the Markov Chain
     */
    public ArrayList<MarkovChainRows> getMarkovChainTableCells() {
        return markovChainTableCells;
    }
    
    /**
     * Gets the full 2D row/column ArrayList of table cells, cast to generic objects
     * Needed to output properly to XML for the DigPop save log
     * @return the 2D ArrayList of generic objects
     */
    @XmlElement(name="genericMarkovTableCells")
    public ArrayList<ArrayList<Object>> getGenericTableCells(){
        ArrayList<ArrayList<Object>> genericTableCells = new ArrayList<>();
        
        this.markovChainTableCells.stream().forEach((c)-> {genericTableCells.add(c.getGenericTableCells());});
        
        return genericTableCells;
    }
    
    /**
     * Sets the Markov Table cells from a 2D ArrayList of generic Objects.
     * Casts back to MarkovTableCells and resets the values in the Markov Chain Table
     * @param genericTableCells - the 2D ArrayList of generic objects
     */
    public void setMarkovTableCellsFromGeneric(ArrayList<ArrayList<Object>> genericTableCells){
        ArrayList<MarkovChainRows> newTableCells = new ArrayList<>();
        
        genericTableCells.stream().forEach((c)-> {
            MarkovChainRows mc = new MarkovChainRows();
            mc.setTableCellsFromGeneric(c);
            newTableCells.add(mc);});
        
        this.markovChainTableCells = newTableCells;
    }

    /**
     * Sets the full list of cells in the Markov Chain table from an ArrayList of all Markov Chain Rows
     * @param markovChainTableCells - the new ArrayList of Markov Chain Rows 
     */
    public void setMarkovChainTableCells(ArrayList<MarkovChainRows> markovChainTableCells) {
        this.markovChainTableCells = markovChainTableCells;
    }

    /**
     * Gets the list of cells that have not yet been modified by a user
     * Used to calculate the remaining cells
     * Stored as array[0][x] is the number of remaining cells in row x, 
     * and array[1][y] is the number of remaining cells in column y
     * @return the 2D integer array of the number of empty cells
     */
    public int[][] getEmptyCells() {
        return emptyCells;
    }

    /**
     * Sets the 2D array of empty cells, not yet modified by the user for calculating the remaining cells
     * Stored as array[0][x] is the number of remaining cells in row x, 
     * and array[1][y] is the number of remaining cells in column y
     * @param emptyCells - the new 2D array of empty cells
     */
    public void setEmptyCells(int[][] emptyCells) {
        this.emptyCells = emptyCells;
    }

    /**
     * Gets the ArrayList of all column names as strings
     * @return - the ArrayList of column names
     */
    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Sets the column names for the Markov Chain
     * @param columnNames - the ArrayList of strings for all column names
     */
    public void setColumnNames(ArrayList<String> columnNames) {
        this.columnNames = columnNames;
    }
}
