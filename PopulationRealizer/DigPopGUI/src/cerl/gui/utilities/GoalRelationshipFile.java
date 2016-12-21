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

/**
 * Used for writing out the Goal Relationship .dprxml file
 * @author mrivera
 */
@XmlRootElement(name="FileRelationship")
public class GoalRelationshipFile {
    private LandUseMapInformation landUseMapInformation;
    private String populationDensity; //the landuse.csv or map.asc
    private String populationDensityType; //map or landuse, only one allowed
    private ArrayList<Traits> traits; 
    private ArrayList<Forbid> forbids;

    public GoalRelationshipFile(){
        this.forbids = new ArrayList<Forbid>();
    }
    
    public GoalRelationshipFile(LandUseMapInformation landUseMapInformation, String populationDensity, String populationDensityType, ArrayList<Traits> traits, ArrayList<Forbid> forbids) {
        this.landUseMapInformation = landUseMapInformation;
        this.populationDensity = populationDensity;
        this.populationDensityType = populationDensityType;
        this.traits = traits;
        this.forbids = forbids;
    }

    @XmlElement(name="landuse")
    public LandUseMapInformation getLandUseMapInformation() {
        return landUseMapInformation;
    }

    @XmlElement(name="popdensity")
    public String getPopulationDensity() {
        return populationDensity;
    }

    public String getPopulationDensityType() {
        return populationDensityType;
    }

    @XmlElementWrapper(name="traits")
    @XmlElement(name="trait")
    public ArrayList<Traits> getTraits() {
        return traits;
    }

    @XmlElement(name="forbid")
    public ArrayList<Forbid> getForbids() {
        return forbids;
    }

    public void setLandUseMapInformation(LandUseMapInformation landUseMapInformation) {
        this.landUseMapInformation = landUseMapInformation;
    }

    public void setPopulationDensity(String populationDensity) {
        this.populationDensity = populationDensity;
    }

    public void setPopulationDensityType(String populationDensityType) {
        this.populationDensityType = populationDensityType;
    }

    public void setTraits(ArrayList<Traits> traits) {
        this.traits = traits;
    }

    public void setForbid(ArrayList<Forbid> forbids) {
        this.forbids = forbids;
    }
    
    public void addForbid(Forbid forbid) {
        this.forbids.add(forbid);
    }
}
