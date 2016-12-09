/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cerl.gui.standard.utilities.Screen;
import cerl.gui.standard.utilities.HelpFile;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.Result;
import cerl.gui.utilities.CensusSurveyClasses;
import static cerl.gui.utilities.DigPopFileTypeEnum.Census_Enumerations;
import static cerl.gui.utilities.DigPopFileTypeEnum.Household_Micro_Data;
import static cerl.gui.utilities.DigPopFileTypeEnum.Population_Micro_Data;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import junit.framework.Assert;

/**
 *
 * @author ajohnson
 */
public class FileUtilityJUnitTest {

    public FileUtilityJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void readInHelpFileTest() {
        String base = "C:\\Projects\\CERL GIT FOLDER\\DigitalPopulations\\PopulationRealizer\\DigPopGUI\\test";
        String path = "C:\\Projects\\CERL GIT FOLDER\\DigitalPopulations\\PopulationRealizer\\DigPopGUI\\docs\\HelpText.xml";

        //   System.out.print(relative);
        File test = new File("./docs/HelpText.xml");

        System.out.println(test.exists());

//        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject("\\..\\..\\..\\..\\docs\\HelpFile.xml", HelpFile.class);
//        HelpFile helpFile = (HelpFile)result.getValue();
//         ArrayList<Screen> screens = helpFile.getScreen();
//        System.out.println(screens.size());
//        assertEquals(2, screens.size());
        //  Result result = FileUtility.getFileExtension("Testerhjhjkh.hjkhk", '.');
        //   System.out.println(result.getValue());
    }
    
    @Test
    public void getClassNamesFromCSVFileCensus_EnumerationsTest(){
        
        long tStart = System.currentTimeMillis();
        System.out.println(tStart);
        
        Result result = DigPopGUIUtilityClass.getClassNamesFromCSVFile("P:\\CERL\\md_sample-data\\md_census_enumerations.csv", Census_Enumerations);
        
        long tEnd = System.currentTimeMillis();
        System.out.println(tEnd);
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println(elapsedSeconds);
    }
    
    @Test
    public void getClassNamesFromCSVFileHousehold_Micro_DataTest(){
        
        long tStart = System.currentTimeMillis();
        System.out.println(tStart);
        
        Result result = DigPopGUIUtilityClass.getClassNamesFromCSVFile("P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv", Household_Micro_Data);
        
        long tEnd = System.currentTimeMillis();
        System.out.println(tEnd);
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println(elapsedSeconds);
    }
    
    @Test
    public void getClassNamesFromCSVFilePopulation_Micro_DataTest(){
        
        long tStart = System.currentTimeMillis();
        System.out.println(tStart);
        
        Result result = DigPopGUIUtilityClass.getClassNamesFromCSVFile("P:\\CERL\\md_sample-data\\md_survey_microdata_people.csv", Population_Micro_Data);
        
        long tEnd = System.currentTimeMillis();
        System.out.println(tEnd);
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println(elapsedSeconds);
    }
    
    @Test
    public void readInClassTotalsHousehold_Micro_DataTest(){
        
        CensusSurveyClasses returnObject = new CensusSurveyClasses();
        
        Result result = DigPopGUIUtilityClass.getClassNamesFromCSVFile("P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv", Household_Micro_Data);
        returnObject = (CensusSurveyClasses)result.getValue();
        
        returnObject.getHouseholdMicroDataClasses().get(0).setSelected(true);
        returnObject.getHouseholdMicroDataClasses().get(4).setSelected(true);
        returnObject.getHouseholdMicroDataClasses().get(10).setSelected(true);
        
        result = DigPopGUIUtilityClass.readInClassTotals("P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv",returnObject.getHouseholdMicroDataClasses());
        
        System.out.print(result.getValue());
    }
    
//    @Test 
 //   public void testgetSurveyDataColumnValues(){
  //      DigPopGUIUtilityClass.getSurveyDataColumnValues("P:\\CERL\\md_sample-data\\md_survey_microdata_people.csv", 11);
 //   }
    
    @Test 
    public void getSurveyDataColumnValuesNEWWWWWWWWWWWWWWWWWWWWWWWWW1(){
        DigPopGUIUtilityClass.getSurveyDataColumnValues("P:\\CERL\\md_sample-data\\md_survey_microdata_people.csv", 13);
    }
    @Test 
    public void getSurveyDataColumnValuesNEWWWWWWWWWWWWWWWWWWWWWWWWW2(){
        DigPopGUIUtilityClass.getSurveyDataColumnValues("P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv", 13);
    }
    
 
    @Test
    public void createTextFileFromString() throws IOException {
        File temp = File.createTempFile("temp-file-name", ".txt");
        String newFilePath = temp.getPath();
        System.out.println(newFilePath);

        System.out.println(temp.getPath());
        System.out.println(temp.getAbsoluteFile());
        System.out.println(temp.getParent());
        System.out.println(temp.getName());
        String newText = "Test Document";

        FileUtility.WriteNewTextFile(newFilePath, newText);
    }

//    @Test
//    public void exportToXML() {
//
//        String landUseMapFilePath = "Tester 11";
//        String householdDensityMapFilePath = "Tester 12";
//        String regionMapFilePath = "Tester 13";
//        String censusEnumerationsFilePath = "Tester 14";
//
//        ArrayList<String> constraintMapsFilePaths = new ArrayList<String>();
//        constraintMapsFilePaths.add("Tester");
//        constraintMapsFilePaths.add("Tester1");
//        constraintMapsFilePaths.add("Tester2");
//        constraintMapsFilePaths.add("Tester3");
//
//        String populationMicroDataFilePath = "Tester 15";
//        String householdMicroDataFilePath = "Tester 16";
//
//        DigPopGUIInformation test = new DigPopGUIInformation(
//                landUseMapFilePath,
//                householdDensityMapFilePath,
//                regionMapFilePath,
//                censusEnumerationsFilePath,
//                constraintMapsFilePaths,
//                populationMicroDataFilePath,
//                householdMicroDataFilePath);
//
//        Result result = FileUtility.ParseObjectToXML(test, "file.xml", DigPopGUIInformation.class);
//        Assert.assertEquals(true, result.isSuccess());
//    }

    @Test
    public void ReadInSaveFile() {
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject("file.xml", DigPopGUIInformation.class);
        
        Assert.assertEquals(true, result.isSuccess());
        
        String landUseMapFilePath = "Tester 11";
        String householdDensityMapFilePath = "Tester 12";
        String regionMapFilePath = "Tester 13";
        String censusEnumerationsFilePath = "Tester 14";

        ArrayList<String> constraintMapsFilePaths = new ArrayList<String>();
        constraintMapsFilePaths.add("Tester");
        constraintMapsFilePaths.add("Tester1");
        constraintMapsFilePaths.add("Tester2");
        constraintMapsFilePaths.add("Tester3");

        String populationMicroDataFilePath = "Tester 15";
        String householdMicroDataFilePath = "Tester 16";
        
        DigPopGUIInformation testSentBack = (DigPopGUIInformation)result.getValue();
        
        assertEquals(landUseMapFilePath, testSentBack.getLandUseMapFilePath());
        assertEquals(householdDensityMapFilePath, testSentBack.getHouseholdDensityMapFilePath());
        assertEquals(regionMapFilePath, testSentBack.getRegionMapFilePath());
        //(constraintMapsFilePaths, testSentBack.getConstraintMapsFilePaths());
        assertEquals(populationMicroDataFilePath, testSentBack.getPopulationMicroDataFilePath());
        assertEquals(householdMicroDataFilePath, testSentBack.getHouseholdMicroDataFilePath());
    }

}
