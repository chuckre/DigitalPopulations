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
public class DigPopGUIFiles {
    
    private String landUseHouseholdMapFilePath;
    private String regionMapFilePath;
    private String censusEnumerationsFilePath;
    private String populationMicroDataFilePath;
    private String householdMicroData;
    
    private ArrayList<String> constraintMapFilePaths;

    public DigPopGUIFiles() {
        constraintMapFilePaths = new ArrayList<String>();
    }

    public String getLandUseHouseholdMapFilePath() {
        return landUseHouseholdMapFilePath;
    }

    public void setLandUseHouseholdMapFilePath(String landUseHouseholdMapFilePath) {
        this.landUseHouseholdMapFilePath = landUseHouseholdMapFilePath;
    }

    public String getRegionMapFilePath() {
        return regionMapFilePath;
    }

    public void setRegionMapFilePath(String regionMapFilePath) {
        this.regionMapFilePath = regionMapFilePath;
    }

    public String getCensusEnumerationsFilePath() {
        return censusEnumerationsFilePath;
    }

    public void setCensusEnumerationsFilePath(String censusEnumerationsFilePath) {
        this.censusEnumerationsFilePath = censusEnumerationsFilePath;
    }

    public String getPopulationMicroDataFilePath() {
        return populationMicroDataFilePath;
    }

    public void setPopulationMicroDataFilePath(String populationMicroDataFilePath) {
        this.populationMicroDataFilePath = populationMicroDataFilePath;
    }

    public String getHouseholdMicroData() {
        return householdMicroData;
    }

    public void setHouseholdMicroData(String householdMicroData) {
        this.householdMicroData = householdMicroData;
    }

    public ArrayList<String> getConstraintMapFilePaths() {
        return constraintMapFilePaths;
    }

    public void setConstraintMapFilePaths(ArrayList<String> constraintMapFilePaths) {
        this.constraintMapFilePaths = constraintMapFilePaths;
    }
    
    public void addConstraintMapFilePath(String constraintMapFilePath) {
        this.constraintMapFilePaths.add(constraintMapFilePath);
    }
    
}
