/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The Markov Chain object - as a matrix of census and survey columns selected
 * Used for creating the Fitting Criteria file and Goal Relationship file
 * @author ajohnson
 */
public class MarkovChain {
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
