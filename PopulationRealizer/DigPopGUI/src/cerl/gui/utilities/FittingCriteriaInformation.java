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

/**
 *
 * @author mrivera
 */
@XmlRootElement(name="FittingCriteriaFile")
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

    public FittingCriteriaInformation(){
    }
    
    public FittingCriteriaInformation(String relationshipFile, ArrayList<Traits> traits, double location, ArrayList<Weights> weights, ArrayList<Cluster> positionRules) {
        this.relationshipFile = relationshipFile;
        this.traits = traits;
        this.location = location;
        this.weights = weights;
        this.positionRules = positionRules;
    }

    @XmlAttribute(name="relationshipFile")
    public String getRelationshipFile() {
        return relationshipFile;
    }

    @XmlElementWrapper(name="traits")
    @XmlElement(name="trait")
    public ArrayList<Traits> getTraits() {
        return traits;
    }

    public double getLocation() {
        return location;
    }

    @XmlElementWrapper(name="weights")
    @XmlElement(name="trait")
    public ArrayList<Weights> getWeights() {
        return weights;
    }

    @XmlElementWrapper(name="position-rules")
    @XmlElement(name="cluster")
    public ArrayList<Cluster> getPositionRules() {
        return positionRules;
    }

    public void setRelationshipFile(String relationshipFile) {
        this.relationshipFile = relationshipFile;
    }

    public void setTraits(ArrayList<Traits> traits) {
        this.traits = traits;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public void setWeights(ArrayList<Weights> weights) {
        this.weights = weights;
    }

    public void setPositionRules(ArrayList<Cluster> positionRules) {
        this.positionRules = positionRules;
    }

    @Override
    public String toString() {
        return "<FittingCriteria " + "relationshipFile=" + relationshipFile + "><traits=" + traits + "><weights location=" + location + ">" + weights + "<positionRules>" + positionRules + "</position-rules>";
    }
}
