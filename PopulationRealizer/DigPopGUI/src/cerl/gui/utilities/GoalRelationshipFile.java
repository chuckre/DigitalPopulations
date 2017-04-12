/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Used for writing out the Goal Relationship .dprxml file
 * @author mrivera
 */
@XmlRootElement(name="FileRelationship")
@XmlType(propOrder={"populationDensity","populationDensityType","landUseMapInformation","traits","forbids"})
public class GoalRelationshipFile {
    private LandUseMapInformation landUseMapInformation;
    private String populationDensity; //the landuse.csv or map.asc
    private String populationDensityType; //map or landuse, only one allowed
    private ArrayList<Traits> traits; 
    private ArrayList<Forbid> forbids;
    
    /**
     * Creates a new, empty, goal relationship file
     */
    public GoalRelationshipFile(){
        this.forbids = new ArrayList<Forbid>();
    }
    
    /**
     * Creates a new goal relationship file from the provided values
     * @param landUseMapInformation - the land use .asc map
     * @param populationDensity - the population density, landuse or household density map
     * @param populationDensityType - the type of population density (landuse or household density)
     * @param traits - the list of traits
     * @param forbid - the list of constraints for placing households
     */
     public GoalRelationshipFile(LandUseMapInformation landUseMapInformation, String populationDensity, String populationDensityType, ArrayList<Traits> traits, ArrayList<Forbid> forbids) {
        this.landUseMapInformation = landUseMapInformation;
        this.populationDensity = populationDensity;
        this.populationDensityType = populationDensityType;
        this.traits = traits;
        this.forbids = forbids;
    }

    /**
     * Gets the landuse map .asc file
     * @return 
     */
    @XmlElement(name="landuse")
    public LandUseMapInformation getLandUseMapInformation() {
        return landUseMapInformation;
    }

    /**
     * Gets the population density landuse or household density map
     * @return 
     */
    @XmlElement(name="popdensity")
    public String getPopulationDensity() {
        return populationDensity;
    }

    /**
     * Gets the type of population density (landuse or household density)
     * @return 
     */
    public String getPopulationDensityType() {
        return populationDensityType;
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
     * Gets the list of constraints
     * @return 
     */
    @XmlElement(name="forbid")
    public ArrayList<Forbid> getForbids() {
        return forbids;
    }

    /**
     * Sets the landuse .asc map
     * @param landUseMapInformation - the new map
     */
    public void setLandUseMapInformation(LandUseMapInformation landUseMapInformation) {
        this.landUseMapInformation = landUseMapInformation;
    }

    /**
     * Sets the population density, landuse or household density map
     * @param populationDensity - the new map
     */
    public void setPopulationDensity(String populationDensity) {
        this.populationDensity = populationDensity;
    }

    /**
     * Sets the population density type as landuse or household density
     * @param populationDensityType - the new type
     */
    public void setPopulationDensityType(String populationDensityType) {
        this.populationDensityType = populationDensityType;
    }

    /**
     * Sets the the list of traits
     * @param traits - the new list of traits
     */
    public void setTraits(ArrayList<Traits> traits) {
        this.traits = traits;
    }


    /**
     * Sets the list of constraint forbids
     * @param forbid - the new list of constraints
     */
    public void setForbid(ArrayList<Forbid> forbids) {
        this.forbids = forbids;
    }
    
    /**
     * Adds to the list of constraint forbids
     * @param forbid  - the new forbid
     */
    public void addForbid(Forbid forbid) {
        this.forbids.add(forbid);
    }
}
