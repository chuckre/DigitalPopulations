/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Object for creating the Fitting Criteria .dprxml file
 * @author mrivera
 */
@XmlRootElement(name="FittingCriteria")
@XmlType(propOrder={"traits","weights","location","positionRules"})
public class FittingCriteriaInformation {
    private String relationshipFile;
    
    //Traits
    private ArrayList<Traits> traits; 
    //Weights
    private ArrayList<Weights> weights;
    
    //@XmlElementRef(name="location")
    private double location;
    
    //Position rules
    private ArrayList<Cluster> positionRules;

    /**
     * Creates a new, empty Fitting Criteria Information object
     */
    public FittingCriteriaInformation(){
    }
    
    /**
     * Creates a new Fitting Criteria object from the provided parameters
     * @param relationshipFile - the file name for the .dprxml file
     * @param traits - the census/survey region traits from the goal fitting file
     * @param location - the weight to be used with the generated traits
     * @param weights - the weight values for each trait
     * @param positionRules - the list of clusters for moving households closer to each other
     */
    public FittingCriteriaInformation(String relationshipFile, ArrayList<Traits> traits, double location, ArrayList<Weights> weights, ArrayList<Cluster> positionRules) {
        this.relationshipFile = relationshipFile;
        this.traits = traits;
        this.location = location;
        this.weights = weights;
        this.positionRules = positionRules;
    }

    /**
     * Get the file path for the relationship file .dprxml file
     * @return 
     */
    @XmlAttribute(name="relationshipFile")
    public String getRelationshipFile() {
        return relationshipFile;
    }

    /**
     * Gets the list of traits
     * @return 
     */
    @XmlElementWrapper(name="traits")
    @XmlElement(name="trait")
    public ArrayList<Traits> getTraits() {
        return traits;
    }

    /**
     * Gets the location for weights
     * @return 
     */
    public double getLocation() {
        return location;
    }

    /**
     * Gets the list of weights associated with traits
     * @return 
     */
    @XmlElementWrapper(name="weights")
    @XmlElement(name="trait")
    public ArrayList<Weights> getWeights() {
        return weights;
    }

    /**
     * Gets the list of clusters for positioning households
     * @return 
     */
    @XmlElementWrapper(name="position-rules")
    @XmlElement(name="cluster")
    public ArrayList<Cluster> getPositionRules() {
        return positionRules;
    }

    /**
     * Sets the relationship file location to the provided file
     * @param relationshipFile - the new location
     */
    public void setRelationshipFile(String relationshipFile) {
        this.relationshipFile = relationshipFile;
    }
    
    /**
     * Sets the list of traits
     * @param traits - the new list of traits
     */
    public void setTraits(ArrayList<Traits> traits) {
        this.traits = traits;
    }

    /**
     * Sets the location weight to be used with generated traits
     * @param location - the new location
     */
    public void setLocation(double location) {
        this.location = location;
    }

    /**
     * Sets the list of trait weights 
     * @param weights - the new set of weights
     */
    public void setWeights(ArrayList<Weights> weights) {
        this.weights = weights;
    }

    /**
     * Sets the list of clusters for position Rules
     * @param positionRules - the new list of clusters
     */
    public void setPositionRules(ArrayList<Cluster> positionRules) {
        this.positionRules = positionRules;
    }

    /**
     * Provides an "XML" like string with the Fitting Criteria information
     * @return 
     */
    @Override
    public String toString() {
        return "<FittingCriteria " + "relationshipFile=" + relationshipFile + "><traits=" + traits + "><weights location=" + location + ">" + weights + "<positionRules>" + positionRules + "</position-rules>";
    }
}
