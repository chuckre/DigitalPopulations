/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author ajohnson
 */
public class MarkovChain {
    private ArrayList<String> FittingCriteriaColumnNames;
    private ArrayList<ArrayList<Object>> FittingCriteriaCellValues;
    private ArrayList<Traits> FittingTraits;
    private ArrayList<Weights> TraitWeights;
    private GoalRelationshipFile goalRelationshipFile;
    private Double traitWeightLocation;
    
    private String markovName;
    private ArrayList<cerl.gui.utilities.Class> censusClasses;
    private cerl.gui.utilities.Class selectSurveyClass;
    private int id;
    private long allCensusTotal = 0;

    public MarkovChain() {
        this.censusClasses = censusClasses = new ArrayList<cerl.gui.utilities.Class>();
        this.selectSurveyClass = new cerl.gui.utilities.Class();
        this.goalRelationshipFile = new GoalRelationshipFile();
    }
    
    public MarkovChain(String markovName, ArrayList<Class> censusClasses, Class selectSurveyClass, int id) {
        this.markovName = markovName;
        this.censusClasses = censusClasses;
        this.selectSurveyClass = selectSurveyClass;
        this.id = id;
        this.goalRelationshipFile = new GoalRelationshipFile();
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
    public ArrayList<ArrayList<Object>> getFittingCriteriaCellValues() {
        return FittingCriteriaCellValues;
    }

    public void setFittingCriteriaCellValues(ArrayList<ArrayList<Object>> FittingCriteriaCellValues) {
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
}
