/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ajohnson
 */
@XmlRootElement(name="DigPopGUISaveFile")
public class DigPopGUIInformation {
    
    private String filePath;
    
    private String createdDate;
    private String lastSaveDate;

    private String landUseMapFilePath;
    private Boolean validLandUseMapFilePath;
    
    private String householdDensityMapFilePath;
    private Boolean validHouseholdDensityMapFilePath;
    
    private String regionMapFilePath;
    private Boolean validRegionMapFilePath;
    
    private String censusEnumerationsFilePath;
    private Boolean validCensusEnumerationsFilePath;
    
    private ArrayList<String> constraintMapsFilePaths;
    private Boolean validConstraintMapsFilePaths;
    
    private String populationMicroDataFilePath;
    private Boolean validPopulationMicroDataFilePath;
    
    private String householdMicroDataFilePath;
    private Boolean validHouseholdMicroDataFilePath;
    

    public DigPopGUIInformation() {
    }

    public DigPopGUIInformation(
            String landUseMapFilePath,
            String householdDensityMapFilePath,
            String regionMapFilePath,
            String censusEnumerationsFilePath,
            ArrayList<String> constraintMapsFilePaths,
            String populationMicroDataFilePath,
            String householdMicroDataFilePath) {
        this.landUseMapFilePath = landUseMapFilePath;
        this.householdDensityMapFilePath = householdDensityMapFilePath;
        this.regionMapFilePath = regionMapFilePath;
        this.censusEnumerationsFilePath = censusEnumerationsFilePath;
        this.constraintMapsFilePaths = constraintMapsFilePaths;
        this.populationMicroDataFilePath = populationMicroDataFilePath;
        this.householdMicroDataFilePath = householdMicroDataFilePath;
    }

    public String getLandUseMapFilePath() {
        return landUseMapFilePath;
    }

    public void setLandUseMapFilePath(String landUseMapFilePath) {
        this.landUseMapFilePath = landUseMapFilePath;
    }

    public String getHouseholdDensityMapFilePath() {
        return householdDensityMapFilePath;
    }

    public void setHouseholdDensityMapFilePath(String householdDensityMapFilePath) {
        this.householdDensityMapFilePath = householdDensityMapFilePath;
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

    public ArrayList<String> getConstraintMapsFilePaths() {
        return constraintMapsFilePaths;
    }

    public void setConstraintMapsFilePaths(ArrayList<String> constraintMapsFilePaths) {
        this.constraintMapsFilePaths = constraintMapsFilePaths;
    }

    public String getPopulationMicroDataFilePath() {
        return populationMicroDataFilePath;
    }

    public void setPopulationMicroDataFilePath(String populationMicroDataFilePath) {
        this.populationMicroDataFilePath = populationMicroDataFilePath;
    }

    public String getHouseholdMicroDataFilePath() {
        return householdMicroDataFilePath;
    }

    public void setHouseholdMicroDataFilePath(String householdMicroDataFilePath) {
        this.householdMicroDataFilePath = householdMicroDataFilePath;
    }

    public void addConstraintMapFilePath(String path) {
        this.constraintMapsFilePaths.add(path);
    }

    public Boolean getValidLandUseMapFilePath() {
        return validLandUseMapFilePath;
    }

    public void setValidLandUseMapFilePath(Boolean validLandUseMapFilePath) {
        this.validLandUseMapFilePath = validLandUseMapFilePath;
    }

    public Boolean getValidHouseholdDensityMapFilePath() {
        return validHouseholdDensityMapFilePath;
    }

    public void setValidHouseholdDensityMapFilePath(Boolean validHouseholdDensityMapFilePath) {
        this.validHouseholdDensityMapFilePath = validHouseholdDensityMapFilePath;
    }

    public Boolean getValidRegionMapFilePath() {
        return validRegionMapFilePath;
    }

    public void setValidRegionMapFilePath(Boolean validRegionMapFilePath) {
        this.validRegionMapFilePath = validRegionMapFilePath;
    }

    public Boolean getValidCensusEnumerationsFilePath() {
        return validCensusEnumerationsFilePath;
    }

    public void setValidCensusEnumerationsFilePath(Boolean validCensusEnumerationsFilePath) {
        this.validCensusEnumerationsFilePath = validCensusEnumerationsFilePath;
    }

    public Boolean getValidConstraintMapsFilePaths() {
        return validConstraintMapsFilePaths;
    }

    public void setValidConstraintMapsFilePaths(Boolean validConstraintMapsFilePaths) {
        this.validConstraintMapsFilePaths = validConstraintMapsFilePaths;
    }

    public Boolean getValidPopulationMicroDataFilePath() {
        return validPopulationMicroDataFilePath;
    }

    public void setValidPopulationMicroDataFilePath(Boolean validPopulationMicroDataFilePath) {
        this.validPopulationMicroDataFilePath = validPopulationMicroDataFilePath;
    }

    public Boolean getValidHouseholdMicroDataFilePath() {
        return validHouseholdMicroDataFilePath;
    }

    public void setValidHouseholdMicroDataFilePath(Boolean validHouseholdMicroDataFilePath) {
        this.validHouseholdMicroDataFilePath = validHouseholdMicroDataFilePath;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastSaveDate() {
        return lastSaveDate;
    }

    public void setLastSaveDate(String lastSaveDate) {
        this.lastSaveDate = lastSaveDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
