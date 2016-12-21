/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ajohnson
 */
@XmlRootElement(name = "DigPopGUISaveFile")
public class DigPopGUIInformation {

    private String filePath;
    private String fileDirectory;

    private String createdDate;
    private String lastSaveDate;

    private String landUseMapFilePath;
    private Boolean validLandUseMapFilePath;
    private LandUseMapInformation landUseMapInformation;

    private String householdDensityMapFilePath;
    private Boolean validHouseholdDensityMapFilePath;

    private String regionMapFilePath;
    private Boolean validRegionMapFilePath;

    private String censusEnumerationsFilePath;
    private Boolean validCensusEnumerationsFilePath;

    private ArrayList<ConstraintMap> constraintMaps;
    private Boolean validConstraintMapsFilePaths;

    private String populationMicroDataFilePath;
    private Boolean validPopulationMicroDataFilePath;

    private String householdMicroDataFilePath;
    private Boolean validHouseholdMicroDataFilePath;

    private CensusSurveyClasses censusSurveyClasses;

    private RunFile runFile;

    public DigPopGUIInformation() {
        this.censusSurveyClasses = new CensusSurveyClasses();
        this.landUseMapInformation = new LandUseMapInformation();
        this.constraintMaps = new ArrayList<ConstraintMap>();
    }

    public DigPopGUIInformation(
            String filePath,
            String createdDate,
            String lastSaveDate,
            String landUseMapFilePath,
            Boolean validLandUseMapFilePath,
            String householdDensityMapFilePath,
            Boolean validHouseholdDensityMapFilePath,
            String regionMapFilePath,
            Boolean validRegionMapFilePath,
            String censusEnumerationsFilePath,
            Boolean validCensusEnumerationsFilePath,
            ArrayList<ConstraintMap> constraintMaps,
            Boolean validConstraintMapsFilePaths,
            String populationMicroDataFilePath,
            Boolean validPopulationMicroDataFilePath,
            String householdMicroDataFilePath,
            Boolean validHouseholdMicroDataFilePath,
            ArrayList<String> FittingCriteriaColumnNames,
            ArrayList<ArrayList<Object>> FittingCriteriaCellValues,
            ArrayList<ArrayList<Object>> traitClusters,
            ArrayList<String> traitList,
            CensusSurveyClasses censusSurveyClasses,
            LandUseMapInformation landUseMapInformation) {
        this.filePath = filePath;
        this.createdDate = createdDate;
        this.lastSaveDate = lastSaveDate;
        this.landUseMapFilePath = landUseMapFilePath;
        this.validLandUseMapFilePath = validLandUseMapFilePath;
        this.householdDensityMapFilePath = householdDensityMapFilePath;
        this.validHouseholdDensityMapFilePath = validHouseholdDensityMapFilePath;
        this.regionMapFilePath = regionMapFilePath;
        this.validRegionMapFilePath = validRegionMapFilePath;
        this.censusEnumerationsFilePath = censusEnumerationsFilePath;
        this.validCensusEnumerationsFilePath = validCensusEnumerationsFilePath;
        this.constraintMaps = constraintMaps;
        this.validConstraintMapsFilePaths = validConstraintMapsFilePaths;
        this.populationMicroDataFilePath = populationMicroDataFilePath;
        this.validPopulationMicroDataFilePath = validPopulationMicroDataFilePath;
        this.householdMicroDataFilePath = householdMicroDataFilePath;
        this.validHouseholdMicroDataFilePath = validHouseholdMicroDataFilePath;
        this.censusSurveyClasses = censusSurveyClasses;
        this.landUseMapInformation = landUseMapInformation;
    }

    @XmlElement(name = "landuse")
    public LandUseMapInformation getLandUseMapInformation() {
        return landUseMapInformation;
    }

    public void setLandUseMapInformation(LandUseMapInformation landUseMapInformation) {
        this.landUseMapInformation = landUseMapInformation;
    }

    public CensusSurveyClasses getCensusSurveyClasses() {
        return censusSurveyClasses;
    }

    public void setCensusSurveyClasses(CensusSurveyClasses censusSurveyClasses) {
        this.censusSurveyClasses = censusSurveyClasses;
    }

    public String getLandUseMapFilePath() {
        return landUseMapFilePath;
    }

    public void setLandUseMapFilePath(String landUseMapFilePath) {
        this.landUseMapFilePath = landUseMapFilePath;

        if (landUseMapFilePath == null || landUseMapFilePath.equals("")) {
            this.landUseMapInformation.setMap("");
        } else {
            File file = new File(landUseMapFilePath);
            this.landUseMapInformation.setMap(file.getName());
        }
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

    public ArrayList<ConstraintMap> getConstraintMaps() {
        return constraintMaps;
    }

    public void setConstraintMaps(ArrayList<ConstraintMap> constraintMaps) {
        this.constraintMaps = constraintMaps;
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

    public void addConstraintMap(ConstraintMap path) {
        this.constraintMaps.add(path);
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

    public RunFile getRunFile() {
        return runFile;
    }

    public void setRunFile(RunFile runFile) {
        this.runFile = runFile;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }
}
