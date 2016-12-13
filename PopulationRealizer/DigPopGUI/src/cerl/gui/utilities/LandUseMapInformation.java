/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;

/**
 *
 * @author ajohnson
 */
public class LandUseMapInformation {
    private String vacentClasses;
    private String vacentClassDescription;
    private String comment;
    private ArrayList<String> allClasses;
    private ArrayList<LandUseMapClassCombination> landUseMapClassCombinations;

    public LandUseMapInformation() {
        this.allClasses = new ArrayList<String>();
        this.landUseMapClassCombinations = new ArrayList<LandUseMapClassCombination>();
    }

    public LandUseMapInformation(String vacentClasses, String vacentClassDescription, String comment, ArrayList<String> allClasses, ArrayList<LandUseMapClassCombination> landUseMapClassCombinations) {
        this.vacentClasses = vacentClasses;
        this.vacentClassDescription = vacentClassDescription;
        this.comment = comment;
        this.allClasses = allClasses;
        this.landUseMapClassCombinations = landUseMapClassCombinations;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVacentClasses() {
        return vacentClasses;
    }

    public void setVacentClasses(String vacentClasses) {
        this.vacentClasses = vacentClasses;
    }

    public String getVacentClassDescription() {
        return vacentClassDescription;
    }

    public void setVacentClassDescription(String vacentClassDescription) {
        this.vacentClassDescription = vacentClassDescription;
    }

    public ArrayList<String> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(ArrayList<String> allClasses) {
        this.allClasses = allClasses;
    }

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
