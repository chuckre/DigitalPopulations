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
 * The DigPop GUI Information for all 
 * information needed to be saved for every run.
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

    /**
     * Creates a new, empty, DigPopGUIInformation object
     */
    public DigPopGUIInformation() {
        this.censusSurveyClasses = new CensusSurveyClasses();
        this.landUseMapInformation = new LandUseMapInformation();
        this.constraintMaps = new ArrayList<ConstraintMap>();
    }

    /**
     * Creates a new DigPopGUIInformation object
     * @param filePath - the file path for the save file.
     * @param fileDirectory - the output file directory
     * @param createdDate - date the save file is created
     * @param lastSaveDate - last date the save file was saved
     * @param landUseMapFilePath - file path for the land use map
     * @param validLandUseMapFilePath - file path for the land use map
     * @param landUseMapInformation - the LandUseMapInformation object that has 
     *                                all information for the land use map
     * @param householdDensityMapFilePath - file path for the household density map
     * @param validHouseholdDensityMapFilePath - boolean value for a valid household density map
     * @param regionMapFilePath - file path for the region map
     * @param validRegionMapFilePath - boolean value for a valid region map
     * @param censusEnumerationsFilePath - file path for the census enumerations file
     * @param validCensusEnumerationsFilePath - boolean value for a valid census enumerations file
     * @param constraintMaps - Array List of Constraint Maps
     * @param validConstraintMapsFilePaths - boolean value for a valid Constraint Maps
     * @param populationMicroDataFilePath - file path for the population Micro Data file
     * @param validPopulationMicroDataFilePath - boolean value for a valid population Micro Data file
     * @param householdMicroDataFilePath - file path for the household Micro Data file
     * @param validHouseholdMicroDataFilePath - boolean value for a valid household Micro Data file
     * @param censusSurveyClasses - Array List of CensusSurveyClasses
     * @param runFile 
     */
    public DigPopGUIInformation(
            String filePath, 
            String fileDirectory, 
            String createdDate, 
            String lastSaveDate, 
            String landUseMapFilePath, 
            Boolean validLandUseMapFilePath, 
            LandUseMapInformation landUseMapInformation, 
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
            CensusSurveyClasses censusSurveyClasses, 
            RunFile runFile) {
        this.filePath = filePath;
        this.fileDirectory = fileDirectory;
        this.createdDate = createdDate;
        this.lastSaveDate = lastSaveDate;
        this.landUseMapFilePath = landUseMapFilePath;
        this.validLandUseMapFilePath = validLandUseMapFilePath;
        this.landUseMapInformation = landUseMapInformation;
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
        this.runFile = runFile;
    }

    /**
     * Gets the LandUseMapInformation Object
     * @return LandUseMapInformation Object
     */
    @XmlElement(name = "landuse")
    public LandUseMapInformation getLandUseMapInformation() {
        return landUseMapInformation;
    }

    /**
     * Sets the LandUseMapInformation Object
     * @param landUseMapInformation LandUseMapInformation Object
     */
    public void setLandUseMapInformation(LandUseMapInformation landUseMapInformation) {
        this.landUseMapInformation = landUseMapInformation;
    }

    /**
     * Gets the CensusSurveyClasses Object
     * @return CensusSurveyClasses Object
     */
    public CensusSurveyClasses getCensusSurveyClasses() {
        return censusSurveyClasses;
    }

    /**
     * Sets the CensusSurveyClasses Object
     * @param censusSurveyClasses CensusSurveyClasses Object
     */
    public void setCensusSurveyClasses(CensusSurveyClasses censusSurveyClasses) {
        this.censusSurveyClasses = censusSurveyClasses;
    }

    /**
     * Gets the LandUse Map File Path as a string
     * @return String Land Use Map File Path 
     */
    public String getLandUseMapFilePath() {
        return landUseMapFilePath;
    }

    /**
     * Sets the LandUse Map File Path as a string
     * @param landUseMapFilePath String Land Use Map File Path 
     */
    public void setLandUseMapFilePath(String landUseMapFilePath) {
        this.landUseMapFilePath = landUseMapFilePath;

        if (landUseMapFilePath == null || landUseMapFilePath.equals("")) {
            this.landUseMapInformation.setMap("");
        } else {
            File file = new File(landUseMapFilePath);
            this.landUseMapInformation.setMap(file.getName());
        }
    }

    /**
     * Gets the Household Density Map File Path as a string 
     * @return String Household Density Map File Path
     */
    public String getHouseholdDensityMapFilePath() {
        return householdDensityMapFilePath;
    }

    /**
     * Sets the Household Density Map File Path as a string 
     * @param householdDensityMapFilePath String Household Density Map File Path
     */
    public void setHouseholdDensityMapFilePath(String householdDensityMapFilePath) {
        this.householdDensityMapFilePath = householdDensityMapFilePath;
    }

    /**
     * Gets the Region Map File Path as a string 
     * @return String Region Map File Path
     */
    public String getRegionMapFilePath() {
        return regionMapFilePath;
    }
    
    /**
     * Sets the Region Map File Path as a string 
     * @param regionMapFilePath String Region Map File Path
     */
    public void setRegionMapFilePath(String regionMapFilePath) {
        this.regionMapFilePath = regionMapFilePath;
    }

    /**
     * Gets the Census Enumerations File Path as a string 
     * @return String Census Enumerations File Path
     */
    public String getCensusEnumerationsFilePath() {
        return censusEnumerationsFilePath;
    }

    /**
     * Sets the Census Enumerations File Path as a string 
     * @param censusEnumerationsFilePath String Census Enumerations File Path
     */
    public void setCensusEnumerationsFilePath(String censusEnumerationsFilePath) {
        this.censusEnumerationsFilePath = censusEnumerationsFilePath;
    }

    /**
     * Gets the ConstraintMap ArrayList
     * @return ConstraintMap ArrayList
     */
    public ArrayList<ConstraintMap> getConstraintMaps() {
        return constraintMaps;
    }

    /**
     * Sets the ConstraintMap ArrayList
     * @param constraintMaps ConstraintMap ArrayList
     */
    public void setConstraintMaps(ArrayList<ConstraintMap> constraintMaps) {
        this.constraintMaps = constraintMaps;
    }

    /**
     * Gets the Population MicroData File Path as a string 
     * @return String Population MicroData File Path
     */
    public String getPopulationMicroDataFilePath() {
        return populationMicroDataFilePath;
    }

    /**
     * Sets the Population MicroData File Path as a string 
     * @param populationMicroDataFilePath String Population MicroData File Path
     */
    public void setPopulationMicroDataFilePath(String populationMicroDataFilePath) {
        this.populationMicroDataFilePath = populationMicroDataFilePath;
    }

    /**
     * Gets the Household MicroData File Path as a string 
     * @return String Household MicroData File Path
     */
    public String getHouseholdMicroDataFilePath() {
        return householdMicroDataFilePath;
    }

    /**
     * Sets the Household MicroData File Path as a string 
     * @param householdMicroDataFilePath String Household MicroData File Path
     */
    public void setHouseholdMicroDataFilePath(String householdMicroDataFilePath) {
        this.householdMicroDataFilePath = householdMicroDataFilePath;
    }

    /**
     * Adds new ConstraintMap to the ConstraintMaps ArrayList
     * @param path New ConstraintMap
     */
    public void addConstraintMap(ConstraintMap path) {
        this.constraintMaps.add(path);
    }

    /**
     * Gets the Boolean value of validLandUseMapFilePath
     * @return Boolean validLandUseMapFilePath
     */
    public Boolean getValidLandUseMapFilePath() {
        return validLandUseMapFilePath;
    }

    /**
     * Sets the Boolean value of validLandUseMapFilePath
     * @param validLandUseMapFilePath Boolean validLandUseMapFilePath
     */
    public void setValidLandUseMapFilePath(Boolean validLandUseMapFilePath) {
        this.validLandUseMapFilePath = validLandUseMapFilePath;
    }

    /**
     * Gets the Boolean value of validHouseholdDensityMapFilePath
     * @return Boolean validHouseholdDensityMapFilePath
     */
    public Boolean getValidHouseholdDensityMapFilePath() {
        return validHouseholdDensityMapFilePath;
    }

    /**
     * Sets the Boolean value of validHouseholdDensityMapFilePath
     * @param validHouseholdDensityMapFilePath Boolean validHouseholdDensityMapFilePath
     */
    public void setValidHouseholdDensityMapFilePath(Boolean validHouseholdDensityMapFilePath) {
        this.validHouseholdDensityMapFilePath = validHouseholdDensityMapFilePath;
    }

    /**
     * Gets the Boolean value of validRegionMapFilePath
     * @return Boolean validRegionMapFilePath
     */
    public Boolean getValidRegionMapFilePath() {
        return validRegionMapFilePath;
    }

    /**
     * Sets the Boolean value of validRegionMapFilePath
     * @param validRegionMapFilePath Boolean validRegionMapFilePath
     */
    public void setValidRegionMapFilePath(Boolean validRegionMapFilePath) {
        this.validRegionMapFilePath = validRegionMapFilePath;
    }

    /**
     * Gets the Boolean value of validCensusEnumerationsFilePath
     * @return Boolean validCensusEnumerationsFilePath
     */
    public Boolean getValidCensusEnumerationsFilePath() {
        return validCensusEnumerationsFilePath;
    }

    /**
     * Sets the Boolean value of validCensusEnumerationsFilePath
     * @param validCensusEnumerationsFilePath Boolean validCensusEnumerationsFilePath
     */
    public void setValidCensusEnumerationsFilePath(Boolean validCensusEnumerationsFilePath) {
        this.validCensusEnumerationsFilePath = validCensusEnumerationsFilePath;
    }

    /**
     * Gets the Boolean value of validConstraintMapsFilePaths
     * @return Boolean validConstraintMapsFilePaths
     */
    public Boolean getValidConstraintMapsFilePaths() {
        return validConstraintMapsFilePaths;
    }

    /**
     * Sets the Boolean value of validConstraintMapsFilePaths
     * @param validConstraintMapsFilePaths Boolean validConstraintMapsFilePaths
     */
    public void setValidConstraintMapsFilePaths(Boolean validConstraintMapsFilePaths) {
        this.validConstraintMapsFilePaths = validConstraintMapsFilePaths;
    }

    /**
     * Gets the Boolean value of validPopulationMicroDataFilePath
     * @return Boolean validPopulationMicroDataFilePath
     */
    public Boolean getValidPopulationMicroDataFilePath() {
        return validPopulationMicroDataFilePath;
    }

    /**
     * Sets the Boolean value of validPopulationMicroDataFilePath
     * @param validPopulationMicroDataFilePath Boolean validPopulationMicroDataFilePath
     */
    public void setValidPopulationMicroDataFilePath(Boolean validPopulationMicroDataFilePath) {
        this.validPopulationMicroDataFilePath = validPopulationMicroDataFilePath;
    }

    /**
     * Gets the Boolean value of validHouseholdMicroDataFilePath
     * @return Boolean validHouseholdMicroDataFilePath
     */
    public Boolean getValidHouseholdMicroDataFilePath() {
        return validHouseholdMicroDataFilePath;
    }

    /**
     * Sets the Boolean value of validHouseholdMicroDataFilePath
     * @param validHouseholdMicroDataFilePath Boolean validHouseholdMicroDataFilePath
     */
    public void setValidHouseholdMicroDataFilePath(Boolean validHouseholdMicroDataFilePath) {
        this.validHouseholdMicroDataFilePath = validHouseholdMicroDataFilePath;
    }

    /**
     * Get the Created Date String
     * @return String Created Date 
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * Set the Created Date String
     * @param createdDate String Created Date 
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Get the LastSaveDate String
     * @return String LastSaveDate 
     */
    public String getLastSaveDate() {
        return lastSaveDate;
    }

    /**
     * Set the LastSaveDate String
     * @param lastSaveDate String LastSaveDate 
     */
    public void setLastSaveDate(String lastSaveDate) {
        this.lastSaveDate = lastSaveDate;
    }

    /**
     * Get the FilePath String
     * @return String FilePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set the FilePath String
     * @param filePath String FilePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get the RunFile Object
     * @return RunFile Object
     */
    public RunFile getRunFile() {
        return runFile;
    }

    /**
     * Set the RunFile Object
     * @param runFile RunFile Object
     */
    public void setRunFile(RunFile runFile) {
        this.runFile = runFile;
    }

    /**
     * Get the FileDirectory String
     * @return String FileDirectory
     */
    public String getFileDirectory() {
        return fileDirectory;
    }

    /**
     * Set the FileDirectory String
     * @param fileDirectory String FileDirectory
     */
    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }
}
