/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.forms.HelpFileDisplay;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.HelpFile;
import cerl.gui.standard.utilities.Instruction;
import cerl.gui.standard.utilities.Result;
import static cerl.gui.utilities.DigPopFileTypeEnum.Census_Enumerations;
import static cerl.gui.utilities.DigPopFileTypeEnum.Household_Micro_Data;
import static cerl.gui.utilities.DigPopFileTypeEnum.Population_Micro_Data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author ajohnson
 */
public class DigPopGUIUtilityClass {

    private static final String HELP_FILE_PATH = "./docs/HelpText.xml";

    private static HelpFile getDefaultHelpFile() {

        File helpFileLocation = new File(HELP_FILE_PATH);
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(helpFileLocation.getPath(), HelpFile.class);
        return (HelpFile) result.getValue();

    }

    public static void loadDefaultHelpGUIByScreenName(String screenName) {
        HelpFile newHelpFile = getDefaultHelpFile();
        new HelpFileDisplay(newHelpFile.getSelectedScreenByName(screenName), newHelpFile).setVisible(true);
    }

    public static void loadDefaultHelpGUIByScreenInstructionName(String screenName, String instructionName) {
        HelpFile newHelpFile = getDefaultHelpFile();

        Instruction selectedInstruction
                = newHelpFile.getSelectedScreenByName(screenName)
                .getSelectedInstructionByName(instructionName);

        new HelpFileDisplay(selectedInstruction, newHelpFile).setVisible(true);
    }

    public static Result saveDigPopGUIInformationSaveFile(
            DigPopGUIInformation information,
            String path) {

        information.setLastSaveDate(Calendar.getInstance().getTime().toString());
        Result result = FileUtility.ParseObjectToXML(information, path, DigPopGUIInformation.class);
        result.setValue(information);

        return result;
    }

    public static Result readInDigPopGUIInformationSaveFile(String path) {
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(path, DigPopGUIInformation.class);

        return result;
    }
    
     public static Result getLoadedCensusSurveyClasses(
             String censusEnumerationsFilePath,
             String populationMicroDataFilePath,
             String householdMicroDataFilePath){
        Result result = new Result();
        
        CensusSurveyClasses returnObject = new CensusSurveyClasses();
        
        if(censusEnumerationsFilePath != null && !censusEnumerationsFilePath.equals("")){
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(censusEnumerationsFilePath, Census_Enumerations);
            
            if(result.isSuccess()){
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setCensusClasses(results.getCensusClasses());
            }
        }
        
        if(populationMicroDataFilePath != null && !populationMicroDataFilePath.equals("")){
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(populationMicroDataFilePath, Population_Micro_Data);
            
            if(result.isSuccess()){
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setPopulationMicroDataClasses(results.getPopulationMicroDataClasses());
            }
        }
        
        if(householdMicroDataFilePath != null && !householdMicroDataFilePath.equals("")){
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(householdMicroDataFilePath, Household_Micro_Data);
            
            if(result.isSuccess()){
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setHouseholdMicroDataClasses(results.getHouseholdMicroDataClasses());
            }
        }
        
        result.setValue(returnObject);
         
        return result;
     }

    public static Result getClassNamesFromCSVFile(
            String filePath,
            DigPopFileTypeEnum digPopFileType) {
        
        Result result = new Result();
        
        CensusSurveyClasses returnObject = new CensusSurveyClasses();

        switch(digPopFileType){
            case Census_Enumerations:
                result = readClassNamesFromFirstLine(filePath,9);
                
                if(result.isSuccess()){
                    returnObject.setCensusClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
            case Population_Micro_Data:
                result = readClassNamesFromFirstLine(filePath,6); 
                
                if(result.isSuccess()){
                    returnObject.setPopulationMicroDataClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
            case Household_Micro_Data:
                result = readClassNamesFromFirstLine(filePath,8);
                
                if(result.isSuccess()){
                    returnObject.setHouseholdMicroDataClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
        }
        
        result.setValue(returnObject);

        return result;
    }
    
    private static Result readClassNamesFromFirstLine(String filePath, int columnIndent){
        Result result = new Result();
        
        ArrayList<Class> foundClasses = new ArrayList<Class>();
        
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            int classIDCounter = 1;

            if((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    for (int count = columnIndent; count < lineInfo.length; count++) {
                        Class newClass = new Class(lineInfo[count],count,false, classIDCounter);
                        foundClasses.add(newClass);
                        classIDCounter++;
                    }
            }

            br.close();
            result.setSuccess(true);
        } catch (IOException ex) {
            result.setErrorMessage(
                    "getCensusClassesFromCSVFile",
                    ex.getMessage());
            result.setSuccess(false);
        }
        
        result.setValue(foundClasses);
        
        return result;
    }
    
    public static Result readInClassTotals(String filePath, ArrayList<Class> classes){
        Result result = new Result();
        
        String line = "";
        
        long total = 0;
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                if(lineCounter > 1){
                    // use comma as separator, but allow for commas inside a string
                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    for(int counter = 0; counter < classes.size(); counter++){
                        if(classes.get(counter).isSelected()){
                            try{
                                Class selectClass = classes.get(counter);
                                long value = Long.parseLong(lineInfo[selectClass.getColumnNumber()]);
                                selectClass.addToClassTotal(value);
                                classes.set(counter, selectClass);

                                total += value;
                            } catch(NumberFormatException e){
                                //nothing just move on
                                //We can add this to the log if they are expecting different results
                            }
                        }
                    }
                }
                
                lineCounter++;
            }

            br.close();
            result.setSuccess(true);
        } catch (IOException ex) {
            result.setErrorMessage(
                    "getCensusClassesFromCSVFile",
                    ex.getMessage());
            result.setSuccess(false);
        }
        
        Object[] resultValues = new Object[2];
        resultValues[0] = total;
        resultValues[1] = classes;
        result.setValue(resultValues);
        return result;
    }
            
//    public static CensusEnumerations convertCSVFileToCensusEnumerationsObject(
//            String filePath) {
//
//        CensusEnumerations returnObject = new CensusEnumerations();
//
//        String line = "";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//
//            int lineCount = 1;
//            String[] classes = null;
//
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//
//                if (lineCount == 1) {
//                    /**
//                     * Need to read in the first row to get the class names.
//                     */
//
//                    classes = new String[lineInfo.length];
//
//                    for (int count = 9; count < lineInfo.length; count++) {
//                        classes[count] = lineInfo[count];
//                    }
//
//                } else {
//
//                    CensusEnumeration newLine = new CensusEnumeration();
//                    newLine.setGISJOIN(lineInfo[0]);
//                    newLine.setUNIQUE_ID(lineInfo[1]);
//                    newLine.setYEAR(lineInfo[2]);
//                    newLine.setSTATEA(lineInfo[3]);
//                    newLine.setCOUNTY(lineInfo[4]);
//                    newLine.setCOUNTYA(lineInfo[5]);
//                    newLine.setTRACTA(lineInfo[6]);
//                    newLine.setBLKGRPA(lineInfo[7]);
//                    newLine.setNAME_E(lineInfo[8]);
//
//                    for (int count = 9; count < lineInfo.length; count++) {
//                        CensusEnumerationClass censusEnumerationClass = new CensusEnumerationClass();
//                        censusEnumerationClass.setClassName(classes[count]);
//                        censusEnumerationClass.setValue(lineInfo[count]);
//
//                        newLine.addCensusEnumerationClass(censusEnumerationClass);
//                    }
//
//                    returnObject.addCensusEnumerations(newLine);
//                }
//
//                lineCount++;
//
//            }
//
//            br.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return returnObject;
//    }
//
//    public static SurveyMicroDataHouseHolds convertCSVFileToSurveyMicroDataHouseHoldsObject(
//            String filePath) {
//
//        SurveyMicroDataHouseHolds returnObject = new SurveyMicroDataHouseHolds();
//
//        String line = "";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//
//            int lineCount = 1;
//            String[] classes = null;
//
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//
//                if (lineCount == 1) {
//                    /**
//                     * Need to read in the first row to get the class names.
//                     */
//
//                    classes = new String[lineInfo.length];
//
//                    for (int count = 8; count < lineInfo.length; count++) {
//                        classes[count] = lineInfo[count];
//                    }
//
//                } else {
//
//                    SurveyMicroDataHouseHold newLine = new SurveyMicroDataHouseHold();
//                    newLine.setInsp(lineInfo[0]);
//                    newLine.setRT(lineInfo[1]);
//                    newLine.setSERIALNO(lineInfo[2]);
//                    newLine.setDIVISION(lineInfo[3]);
//                    newLine.setPUMA00(lineInfo[4]);
//                    newLine.setPUMA10(lineInfo[5]);
//                    newLine.setREGION(lineInfo[6]);
//                    newLine.setST(lineInfo[7]);
//
//                    for (int count = 8; count < lineInfo.length; count++) {
//                        SurveyMicroDataHouseHoldClass surveyMicroDataHouseHoldClass = new SurveyMicroDataHouseHoldClass();
//                        surveyMicroDataHouseHoldClass.setClassName(classes[count]);
//                        surveyMicroDataHouseHoldClass.setValue(lineInfo[count]);
//
//                        newLine.addSurveyMicroDataHouseHoldClass(surveyMicroDataHouseHoldClass);
//                    }
//
//                    returnObject.addSurveyMicroDataHouseHolds(newLine);
//                }
//
//                lineCount++;
//
//            }
//
//            br.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return returnObject;
//    }
//
//    public static SurveyMicroDataPeoples convertCSVFileToSurveyMicroDataPeoplesObject(
//            String filePath) {
//
//        SurveyMicroDataPeoples returnObject = new SurveyMicroDataPeoples();
//        
//        List<Object> list = new ArrayList<>();
//
//        String line = "";
//        String encoding = "UTF-8";
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encoding))) {
//
//            int lineCount = 1;
//            String[] classes = null;
//
//            List<Object> testList = new ArrayList<Object>();
//
//            int testerCounter = 0;
//
//            while ((line = br.readLine()) != null) {
//
////                // use comma as separator
//                String[] lineInfo = line.split(",");//(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
////                
//                if (lineCount == 1) {
//                    /**
//                     * Need to read in the first row to get the class names.
//                     */
//
//                    classes = Arrays.copyOfRange(lineInfo, 6, lineInfo.length);
//
//                } else {
//
//                    ArrayList<SurveyMicroDataPeopleClass> arrayList = new ArrayList<SurveyMicroDataPeopleClass>();
//
//                    for (int count = 6; count < lineInfo.length; count++) {
//                        arrayList.add(new SurveyMicroDataPeopleClass(
//                                classes[count - 6],
//                                lineInfo[count]));
//                    }
//
//                    list.add(new SurveyMicroDataPeople(
//                            lineInfo[0],
//                            lineInfo[1],
//                            lineInfo[2],
//                            lineInfo[3],
//                            lineInfo[4],
//                            lineInfo[5],
//                            arrayList));
//
//                    //    testArrayListAdd.add(newLine);
//                    //test.add(newLine);
//                    // returnObject.addSurveyMicroDataPeople(newLine);
//                }
//
//                lineCount++;
//
//            }
//
//            System.out.println(lineCount);
//
//            br.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return returnObject;
//    }

//    private static SurveyMicroDataPeople dump(String crunchifyCSV, String[] classes) {
//        SurveyMicroDataPeople returnvalue = null;
//
//        if (crunchifyCSV != null) {
//            String[] splitData = crunchifyCSV.split("\\s*,\\s*");
//
//            ArrayList<SurveyMicroDataPeopleClass> arrayList = new ArrayList<SurveyMicroDataPeopleClass>();
//
//            for (int count = 6; count < splitData.length; count++) {
//                SurveyMicroDataPeopleClass surveyMicroDataPeopleClass = new SurveyMicroDataPeopleClass();
//                surveyMicroDataPeopleClass.setClassName(classes[count - 6]);//classes needs to start at zero
//                surveyMicroDataPeopleClass.setValue(splitData[count]);
//
//                arrayList.add(surveyMicroDataPeopleClass);
//            }
//
//            returnvalue = new SurveyMicroDataPeople(
//                    splitData[0],
//                    splitData[1],
//                    splitData[2],
//                    splitData[3],
//                    splitData[4],
//                    splitData[5],
//                    arrayList);
//        }
//
//        return returnvalue;
//    }

}
