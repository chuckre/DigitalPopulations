/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Land Use Map used in the Goal Relationship .dprxml File
 * @author ajohnson
 */
@XmlRootElement(name="landuse")
public class LandUseMapInformation {
    private VacantClass vacantClasses;
    private String comment;
    private ArrayList<String> allClasses;
    private ArrayList<LandUseMapClassCombination> landUseMapClassCombinations;
    private String map;

    /**
     * Creates a new, empty land use map object
     */
    public LandUseMapInformation() {
        this.allClasses = new ArrayList<String>();
        this.landUseMapClassCombinations = new ArrayList<LandUseMapClassCombination>();
    }

    /**
     * Creates a new land use map object from the provided parameters
     * @param map - the file name of the land use map
     * @param vacantClasses - the vacant class object
     * @param comment - a comment about the land use map
     * @param allClasses - the list of all classes included
     * @param landUseMapClassCombinations - the list of combination classes
     */
    public LandUseMapInformation(String map, VacantClass vacantClasses, String comment, ArrayList<String> allClasses, ArrayList<LandUseMapClassCombination> landUseMapClassCombinations) {
        this.vacantClasses = vacantClasses;
        //this.vacantClassDescription = vacentClassDescription;
        this.comment = comment;
        this.allClasses = allClasses;
        this.landUseMapClassCombinations = landUseMapClassCombinations;
        this.map = map;
    }

    /**
     * Gets the file name of the land use map without the file path
     * @return - the file name of the land use map
     */
    @XmlAttribute
    public String getMap() {
        return map;
    }

    /**
     * Sets the land use map file name
     * @param map - the new land use file name without the full path
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Gets the comment for the land use map information
     * @return - the text description about the land use map
     */
    @XmlElement(name="comment")
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment for the land use map information
     * @param comment - a new text description fo the land use map
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the VacantClass object for the current land use map
     * @return - the VacantClass object
     */
    @XmlElement(name="vacant")
    public VacantClass getVacantClasses() {
        return vacantClasses;
    }

    /**
     * Sets the vacant classes for the current land use map
     * @param vacantClasses - the new vacant class object
     */
    public void setVacantClasses(VacantClass vacantClasses) {
        this.vacantClasses = vacantClasses;
    }

    /**
     * Gets the list of classes in the land use map
     * @return - the list of classes
     */
    @XmlTransient
    public ArrayList<String> getAllClasses() {
        return allClasses;
    }

    /**
     * Sets the list of classes for the land use map
     * @param allClasses - the new list of classes
     */
    public void setAllClasses(ArrayList<String> allClasses) {
        this.allClasses = allClasses;
    }

    /**
     * Gets the list of combination classes for the current land use map
     * @return the list of combination classes
     */
    @XmlElement(name="combination")
    public ArrayList<LandUseMapClassCombination> getLandUseMapClassCombinations() {
        return landUseMapClassCombinations;
    }

    /**
     * Sets the list of combination classes for the current land use map
     * @param landUseMapClassCombinations - the new list of combo classes
     */
    public void setLandUseMapClassCombinations(ArrayList<LandUseMapClassCombination> landUseMapClassCombinations) {
        this.landUseMapClassCombinations = landUseMapClassCombinations;
    }
    
    /**
     * Adds a new combination class to the list of combo classes on the current land use map
     * @param landUseMapClassCombination - the new combo class to add to the list
     */
    public void addLandUseMapClassCombinations(LandUseMapClassCombination landUseMapClassCombination) {
        this.landUseMapClassCombinations.add(landUseMapClassCombination);
    }
}
