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
 *
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

    public MarkovChain() {
        this.censusClasses = censusClasses = new ArrayList<cerl.gui.utilities.Class>();
        this.selectSurveyClass = new cerl.gui.utilities.Class();
        this.goalRelationshipFile = new GoalRelationshipFile();
        this.markovChainTableCells = new ArrayList<MarkovChainRows>();
        this.newCensusColumnDetails  = new ArrayList<NewCensusColumnDetails>();
    }
    
    public MarkovChain(String markovName, ArrayList<Class> censusClasses, Class selectSurveyClass, int id) {
        this.markovName = markovName;
        this.censusClasses = censusClasses;
        this.selectSurveyClass = selectSurveyClass;
        this.id = id;
        this.goalRelationshipFile = new GoalRelationshipFile();
        this.markovChainTableCells = new ArrayList<MarkovChainRows>();
        this.newCensusColumnDetails  = new ArrayList<NewCensusColumnDetails>();
    }

    public ArrayList<NewCensusColumnDetails> getNewCensusColumnDetails() {
        return newCensusColumnDetails;
    }

    public void setNewCensusColumnDetails(ArrayList<NewCensusColumnDetails> newCensusColumnDetails) {
        this.newCensusColumnDetails = newCensusColumnDetails;
    }
    
    public void addNewCensusColumnDetails(NewCensusColumnDetails newCensusColumnDetail) {
        this.newCensusColumnDetails.add(newCensusColumnDetail);
    }
    
    public ArrayList<Traits> getFittingTraits() {
        return FittingTraits;
    }

    public void setFittingTraits(ArrayList<Traits> FittingTraits) {
        this.FittingTraits = FittingTraits;
    }
    
     public ArrayList<Weights> getTraitWeights() {
        return TraitWeights;
    }

    public void setTraitWeights(ArrayList<Weights> TraitWeights) {
        this.TraitWeights = TraitWeights;
    }
    
     public ArrayList<String> getFittingCriteriaColumnNames() {
        return FittingCriteriaColumnNames;
    }

    public void setFittingCriteriaColumnNames(ArrayList<String> FittingCriteriaColumnNames) {
        this.FittingCriteriaColumnNames = FittingCriteriaColumnNames;
    }

    @XmlElement(name="fittingCriteriaCellValues")
    public ArrayList<ArrayList<customTableCell>> getFittingCriteriaCellValues() {
        return FittingCriteriaCellValues;
    }

    public void setFittingCriteriaCellValues(ArrayList<ArrayList<customTableCell>> FittingCriteriaCellValues) {
        this.FittingCriteriaCellValues = FittingCriteriaCellValues;
    }

    public long getAllCensusTotal() {
        this.censusClasses.stream().forEach((c) -> {
            this.allCensusTotal += c.getClassTotal();
        });
        
        return allCensusTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getMarkovName() {
        return markovName;
    }

    public void setMarkovName(String markovName) {
        this.markovName = markovName;
    }

    public ArrayList<Class> getCensusClasses() {
        return censusClasses;
    }

    public void setCensusClasses(ArrayList<Class> censusClasses) {
        this.censusClasses = censusClasses;
    }

    @XmlElement
    public cerl.gui.utilities.Class  getSelectSurveyClass() {
        return selectSurveyClass;
    }

    public void setSelectSurveyClass(Class selectSurveyClass) {
        this.selectSurveyClass = selectSurveyClass;
    }
    
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
    
     public Double getTraitWeightLocation() {
        return traitWeightLocation;
    }

    public void setTraitWeightLocation(Double traitWeightLocation) {
        this.traitWeightLocation = traitWeightLocation;
    }
    
    public void addConstraintMapFilePath(ArrayList<String> constraintMapsFilePaths) {
        constraintMapsFilePaths.stream().forEach((c) -> {
            Forbid f = new Forbid();
            f.setMap(c);
            this.goalRelationshipFile.setForbid(f);
        });
    }
    
    public GoalRelationshipFile getGoalRelationshipFile() {
        return goalRelationshipFile;
    }

    public void setGoalRelationshipFile(GoalRelationshipFile goalRelationshipFile) {
        this.goalRelationshipFile = goalRelationshipFile;
    }
    
    public ArrayList<ArrayList<Object>> getTraitClusters() {
        return traitClusters;
    }

    public void setTraitClusters(ArrayList<ArrayList<Object>> traitClusters) {
        this.traitClusters = traitClusters;
    }

    public ArrayList<String> getTraitList() {
        return traitList;
    }

    public void setTraitList(ArrayList<String> traitList) {
        this.traitList = traitList;
    }
    
    public ArrayList<Cluster> getTraitPositionClusters() {
        return traitPositionClusters;
    }

    public void setTraitPositionClusters(ArrayList<Cluster> traitPositionClusters) {
        this.traitPositionClusters = traitPositionClusters;
    }

    public ArrayList<MarkovChainRows> getMarkovChainTableCells() {
        return markovChainTableCells;
    }
    
    @XmlElement(name="genericMarkovTableCells")
    public ArrayList<ArrayList<Object>> getGenericTableCells(){
        ArrayList<ArrayList<Object>> genericTableCells = new ArrayList<>();
        
        this.markovChainTableCells.stream().forEach((c)-> {genericTableCells.add(c.getGenericTableCells());});
        
        return genericTableCells;
    }
    
    public void setMarkovTableCellsFromGeneric(ArrayList<ArrayList<Object>> genericTableCells){
        ArrayList<MarkovChainRows> newTableCells = new ArrayList<>();
        
        genericTableCells.stream().forEach((c)-> {
            MarkovChainRows mc = new MarkovChainRows();
            mc.setTableCellsFromGeneric(c);
            newTableCells.add(mc);});
        
        this.markovChainTableCells = newTableCells;
    }

    public void setMarkovChainTableCells(ArrayList<MarkovChainRows> markovChainTableCells) {
        this.markovChainTableCells = markovChainTableCells;
    }

    public int[][] getEmptyCells() {
        return emptyCells;
    }

    public void setEmptyCells(int[][] emptyCells) {
        this.emptyCells = emptyCells;
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(ArrayList<String> columnNames) {
        this.columnNames = columnNames;
    }
}
