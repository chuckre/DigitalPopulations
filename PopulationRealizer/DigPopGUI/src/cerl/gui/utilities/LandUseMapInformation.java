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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ajohnson
 */
@XmlRootElement(name="landuse")
public class LandUseMapInformation {
    private VacantClass vacantClasses;
    //private String vacantClasses;
    //private String vacantClassDescription;
    private String comment;
    private ArrayList<String> allClasses;
    private ArrayList<LandUseMapClassCombination> landUseMapClassCombinations;
    private String map;

    public LandUseMapInformation() {
        this.allClasses = new ArrayList<String>();
        this.landUseMapClassCombinations = new ArrayList<LandUseMapClassCombination>();
    }

    public LandUseMapInformation(String map, VacantClass vacantClasses, String comment, ArrayList<String> allClasses, ArrayList<LandUseMapClassCombination> landUseMapClassCombinations) {
        this.vacantClasses = vacantClasses;
        //this.vacantClassDescription = vacentClassDescription;
        this.comment = comment;
        this.allClasses = allClasses;
        this.landUseMapClassCombinations = landUseMapClassCombinations;
        this.map = map;
    }

    @XmlAttribute
    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    @XmlElement(name="comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlElement(name="vacant")
    public VacantClass getVacantClasses() {
        return vacantClasses;
    }

    public void setVacantClasses(VacantClass vacantClasses) {
        this.vacantClasses = vacantClasses;
    }

    /*@XmlAttribute(name="desc")
    public String getVacantClassDescription() {
        return vacantClassDescription;
    }

    public void setVacantClassDescription(String vacantClassDescription) {
        this.vacantClassDescription = vacantClassDescription;
    }*/

    @XmlTransient
    public ArrayList<String> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(ArrayList<String> allClasses) {
        this.allClasses = allClasses;
    }

    @XmlElement(name="combination")
    public ArrayList<LandUseMapClassCombination> getLandUseMapClassCombinations() {
        return landUseMapClassCombinations;
    }

    public void setLandUseMapClassCombinations(ArrayList<LandUseMapClassCombination> landUseMapClassCombinations) {
        this.landUseMapClassCombinations = landUseMapClassCombinations;
    }
    
    public void addLandUseMapClassCombinations(LandUseMapClassCombination landUseMapClassCombination) {
        this.landUseMapClassCombinations.add(landUseMapClassCombination);
    }
}
