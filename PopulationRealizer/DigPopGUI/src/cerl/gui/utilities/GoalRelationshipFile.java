/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Used for writing out the Goal Relationship .dprxml file
 * @author mrivera
 */
@XmlRootElement(name="FileRelationship")
//@XmlType(propOrder={"landuse","popdensity","traits", "forbid"})
//@XmlType(propOrder={"landuse","popdensity","regions", "households", "population", "traits", "forbid"})
public class GoalRelationshipFile {
    private LandUseMapInformation landUseMapInformation;
    private String landUseMap;
    private String populationDensity; //the landuse.csv or map.asc
    private String populationDensityType; //map or landuse, only one allowed
    //private Regions regionMap; 
    //private Households households;
    //private Population population;
    private ArrayList<Traits> traits; 
    private Forbid forbid;

    public GoalRelationshipFile(){}
    
    public GoalRelationshipFile(LandUseMapInformation landUseMapInformation, String landUseMap, String populationDensity, String populationDensityType, ArrayList<Traits> traits, Forbid forbid) {
        this.landUseMapInformation = landUseMapInformation;
        this.landUseMap = landUseMap;
        this.populationDensity = populationDensity;
        this.populationDensityType = populationDensityType;
        this.traits = traits;
        this.forbid = forbid;
    }

    @XmlElement(name="combination")
    public LandUseMapInformation getLandUseMapInformation() {
        return landUseMapInformation;
    }

    //@XmlAccessorType(XmlAccessType.PROPERTY)
    //@XmlType(name="map")
    public String getLandUseMap() {
        return landUseMap;
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
    public Forbid getForbid() {
        return forbid;
    }

    public void setLandUseMapInformation(LandUseMapInformation landUseMapInformation) {
        this.landUseMapInformation = landUseMapInformation;
    }

    public void setLandUseMap(String landUseMap) {
        this.landUseMap = landUseMap;
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

    public void setForbid(Forbid forbid) {
        this.forbid = forbid;
    }
}
