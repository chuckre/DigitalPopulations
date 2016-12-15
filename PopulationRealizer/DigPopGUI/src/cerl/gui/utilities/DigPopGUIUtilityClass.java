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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JProgressBar;

/**
 *
 * @author ajohnson
 */
public class DigPopGUIUtilityClass {

    private static final String HELP_FILE_PATH = "/cerl/gui/resources/HelpText.xml";
    
    private static final int FIRST_COLUMN_FOR_CENSUS_ENUMERATIONS_FILE = 9;
    private static final int FIRST_COLUMN_FOR_HOUSEHOLD_ENUMERATIONS_FILE = 8;
    private static final int FIRST_COLUMN_FOR_POPULATION_ENUMERATIONS_FILE = 6;

    private static HelpFile getDefaultHelpFile() {

        URL url = DigPopGUIUtilityClass.class.getResource(HELP_FILE_PATH);
        
//        String filePath = "";
//        try {
//           // filePath = Paths.get(url.toURI()).toFile().getPath();
//            
//           filePath = url.toURI().getPath();
//            
//           // int b = 0;
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(StepOneUtilityClass.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //File helpFileLocation = new File(filePath);
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObjectFromURL(url, HelpFile.class);
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
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_CENSUS_ENUMERATIONS_FILE, Census_Enumerations);
                
                if(result.isSuccess()){
                    returnObject.setCensusClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
            case Population_Micro_Data:
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_POPULATION_ENUMERATIONS_FILE, Population_Micro_Data); 
                
                if(result.isSuccess()){
                    returnObject.setPopulationMicroDataClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
            case Household_Micro_Data:
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_HOUSEHOLD_ENUMERATIONS_FILE, Household_Micro_Data);
                
                if(result.isSuccess()){
                    returnObject.setHouseholdMicroDataClasses((ArrayList<Class>)result.getValue());
                }
                
                break;
        }
        
        result.setValue(returnObject);

        return result;
    }
    
    private static Result readClassNamesFromFirstLine(
            String filePath, 
            int columnIndent,
            DigPopFileTypeEnum digPopFileType){
        Result result = new Result();
        
        ArrayList<Class> foundClasses = new ArrayList<Class>();
        
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            int classIDCounter = 1;

            if((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    for (int count = columnIndent; count < lineInfo.length; count++) {
                        Class newClass = new Class(lineInfo[count],count,false, classIDCounter, digPopFileType);
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
    
//    public static Result readInClassTotals(String filePath, ArrayList<Class> classes){
//        Result result = new Result();
//        
//        String line = "";
//        
//        long total = 0;
//        int lineCounter = 0;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            while((line = br.readLine()) != null) {
//                if(lineCounter > 1){
//                    // use comma as separator, but allow for commas inside a string
//                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//
//                    for(int counter = 0; counter < classes.size(); counter++){
//                        if(classes.get(counter).isSelected()){
//                            try{
//                                Class selectClass = classes.get(counter);
//                                long value = Long.parseLong(lineInfo[selectClass.getColumnNumber()]);
//                                selectClass.addToClassTotal(value);
//                                classes.set(counter, selectClass);
//
//                                total += value;
//                            } catch(NumberFormatException e){
//                                //nothing just move on
//                                //We can add this to the log if they are expecting different results
//                            }
//                        }
//                    }
//                }
//                
//                lineCounter++;
//            }
//
//            br.close();
//            result.setSuccess(true);
//        } catch (IOException ex) {
//            result.setErrorMessage(
//                    "getCensusClassesFromCSVFile",
//                    ex.getMessage());
//            result.setSuccess(false);
//        }
//        
//        Object[] resultValues = new Object[2];
//        resultValues[0] = total;
//        resultValues[1] = classes;
//        result.setValue(resultValues);
//        return result;
//    }
    
//    public static Result getSurveyDataColumnValues(String filePath, int columnNumber){
//        Result result = new Result();
//        ArrayList<SurveyColumnValue> columnValues = new ArrayList<>();
//        
//        String line = "";
//        int lineCounter = 0;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            while((line = br.readLine()) != null) {
//                if(lineCounter >= 1){
//                    // use comma as separator, but allow for commas inside a string
//                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//                    
//                    columnValues.add(new SurveyColumnValue(lineCounter,Integer.parseInt(lineInfo[columnNumber]), false, 0));//TODO FIX THISD
//                }
//                
//                lineCounter++;
//            }
//
//            br.close();
//            result.setSuccess(true);
//        } catch (IOException ex) {
//            result.setErrorMessage(
//                    "getCensusClassesFromCSVFile",
//                    ex.getMessage());
//            result.setSuccess(false);
//        }
//        
//        result.setValue(columnValues);
//        return result;
//    }

    public static Result getSurveyDataColumnValues(String filePath, int columnNumber){
        Result result = new Result();
        List<SurveyColumnValue> columnValues = new ArrayList<SurveyColumnValue>();
        
        String line = "";
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                if(lineCounter >= 1){
                    // use comma as separator, but allow for commas inside a string
                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    
                    int value =Integer.parseInt(lineInfo[columnNumber]);
                    
                    //boolean alreadyFound = columnValues.stream().anyMatch(c -> c.getValue() == value);
                    
                    Optional<SurveyColumnValue> foundFromStream = columnValues.stream().filter(c-> c.getValue() == value).findFirst();
                    
                    if(!foundFromStream.isPresent()){
                        columnValues.add(new SurveyColumnValue(lineCounter,Integer.parseInt(lineInfo[columnNumber]), false, 1));
                    }
                    else{
                        SurveyColumnValue found = foundFromStream.get();
                        found.addOneToNumberOfTimesUsed();
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
        
        result.setValue(columnValues);
        return result;
    }
    
    public static Result getSelectedCensusColumnValues(String filePath, List<Class> censusClasses){
        Result result = new Result();
        
        String line = "";
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                if(lineCounter >= 1){
                    // use comma as separator, but allow for commas inside a string
                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    
                    censusClasses.stream().forEach((c) -> {
                        c.addToClassTotal(Long.parseLong(lineInfo[c.getColumnNumber()]));
                    });
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
        
        result.setValue(censusClasses);
        return result;
    }
    
    public static Result getClassesFromLandUseASCFile(
        String filePath) throws IOException {
        
        Result result = new Result(true);
        
         List<String> classes = null;
        
        int counter =0;
                
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(counter >= 6){
                    String[] lineInfo = line.split(" ");

                    if(classes == null){
                        classes = Arrays.asList(lineInfo).stream().distinct().collect(Collectors.toList());
                    }
                    else{
                        classes.addAll(Arrays.asList(lineInfo));
                        classes = classes.stream().distinct().collect(Collectors.toList());
                    }
                }
                counter++;
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (FileNotFoundException ex) {
            result.setErrorMessage(
                    "getClassesFromLandUseASCFile",
                    ex.getMessage());
            result.setSuccess(false);
        } catch (IOException ex) {
            result.setErrorMessage(
                    "getClassesFromLandUseASCFile",
                    ex.getMessage());
            result.setSuccess(false);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        
        result.setValue(classes);
        return result;
    }
}
