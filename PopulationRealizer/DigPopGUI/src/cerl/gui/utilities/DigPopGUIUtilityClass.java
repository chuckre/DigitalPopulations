/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.forms.HelpFileDisplay;
import cerl.gui.forms.MarkovChainMatrix;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * DigPopGUIUtilityClass is the Custom Utility class for the DigPopGUI application.
 * @author ajohnson
 */
public class DigPopGUIUtilityClass {

    /**
     * Path to the help file stored in the resource package.
     */
    private static final String HELP_FILE_PATH = "/cerl/gui/resources/HelpText.xml";

    private static final int FIRST_COLUMN_FOR_CENSUS_ENUMERATIONS_FILE = 9;
    private static final int FIRST_COLUMN_FOR_HOUSEHOLD_ENUMERATIONS_FILE = 8;
    private static final int FIRST_COLUMN_FOR_POPULATION_ENUMERATIONS_FILE = 6;

    /**
     * Calls FileUtility to read in the applications help file and stores into 
     * the HelpFile object. 
     * @return Loaded HelpFile Object 
     */
    private static HelpFile getDefaultHelpFile() {

        URL url = DigPopGUIUtilityClass.class.getResource(HELP_FILE_PATH);

        Result result = FileUtility.ParseXMLFileIntoSpecifiedObjectFromURL(url, HelpFile.class);
        return (HelpFile) result.getValue();

    }

    /**
     * Loads the default help file GUI by the specified screen name.
     * @param screenName - the screen to load the help for
     */
    public static void loadDefaultHelpGUIByScreenName(String screenName) {
        HelpFile newHelpFile = getDefaultHelpFile();
        new HelpFileDisplay(newHelpFile.getSelectedScreenByName(screenName), newHelpFile).setVisible(true);
    }

    /**
     * Loads the Help Screen for a specific item on a screen
     * @param screenName - the screen to view help for
     * @param instructionName - the specific item to view help for
     */
    public static void loadDefaultHelpGUIByScreenInstructionName(String screenName, String instructionName) {
        HelpFile newHelpFile = getDefaultHelpFile();

        Instruction selectedInstruction
                = newHelpFile.getSelectedScreenByName(screenName)
                .getSelectedInstructionByName(instructionName);

        new HelpFileDisplay(selectedInstruction, newHelpFile).setVisible(true);
    }

    /**
     * Saves the DigPop Object 
     * @param information - the current DigPop object
     * @param path - the FilePath where the file should be saved
     * @return
     */
    public static Result saveDigPopGUIInformationSaveFile(
            DigPopGUIInformation information,
            String path) {

        information.setLastSaveDate(Calendar.getInstance().getTime().toString());
        Result result = FileUtility.ParseObjectToXML(information, path, DigPopGUIInformation.class);
        result.setValue(information);

        return result;
    }

    /**
     * Reads the DigPop Object from the save file
     * @param path - the file path of the DigPop object to load
     * @return
     */
    public static Result readInDigPopGUIInformationSaveFile(String path) {
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(path, DigPopGUIInformation.class);

        return result;
    }

    /**
     * Gets the census and survey classes that were already loaded in a previous run
     * @param censusEnumerationsFilePath - the file path of the census enumerations .csv file
     * @param populationMicroDataFilePath - the file path of the population microdata .csv file
     * @param householdMicroDataFilePath - the file path of the household microdata .csv file
     * @return
     */
    public static Result getLoadedCensusSurveyClasses(
            String censusEnumerationsFilePath,
            String populationMicroDataFilePath,
            String householdMicroDataFilePath) {
        Result result = new Result();

        CensusSurveyClasses returnObject = new CensusSurveyClasses();

        if (censusEnumerationsFilePath != null && !censusEnumerationsFilePath.equals("")) {
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(censusEnumerationsFilePath, Census_Enumerations);

            if (result.isSuccess()) {
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setCensusClasses(results.getCensusClasses());
            }
        }

        if (populationMicroDataFilePath != null && !populationMicroDataFilePath.equals("")) {
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(populationMicroDataFilePath, Population_Micro_Data);

            if (result.isSuccess()) {
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setPopulationMicroDataClasses(results.getPopulationMicroDataClasses());
            }
        }

        if (householdMicroDataFilePath != null && !householdMicroDataFilePath.equals("")) {
            result = DigPopGUIUtilityClass.getClassNamesFromCSVFile(householdMicroDataFilePath, Household_Micro_Data);

            if (result.isSuccess()) {
                CensusSurveyClasses results = (CensusSurveyClasses) result.getValue();
                returnObject.setHouseholdMicroDataClasses(results.getHouseholdMicroDataClasses());
            }
        }

        result.setValue(returnObject);

        return result;
    }

    /**
     * Gets the census or survey class names from the .csv file
     * @param filePath - the file path of the .csv file to read
     * @param digPopFileType - the DigPop type, either Census_Enumerations, Population_Micro_Data or Household_Micro_Data
     * @return 
     */
    public static Result getClassNamesFromCSVFile(
            String filePath,
            DigPopFileTypeEnum digPopFileType) {

        Result result = new Result();

        CensusSurveyClasses returnObject = new CensusSurveyClasses();

        switch (digPopFileType) {
            case Census_Enumerations:
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_CENSUS_ENUMERATIONS_FILE, Census_Enumerations);

                if (result.isSuccess()) {
                    returnObject.setCensusClasses((ArrayList<Class>) result.getValue());
                }

                break;
            case Population_Micro_Data:
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_POPULATION_ENUMERATIONS_FILE, Population_Micro_Data);

                if (result.isSuccess()) {
                    returnObject.setPopulationMicroDataClasses((ArrayList<Class>) result.getValue());
                }

                break;
            case Household_Micro_Data:
                result = readClassNamesFromFirstLine(filePath, FIRST_COLUMN_FOR_HOUSEHOLD_ENUMERATIONS_FILE, Household_Micro_Data);

                if (result.isSuccess()) {
                    returnObject.setHouseholdMicroDataClasses((ArrayList<Class>) result.getValue());
                }

                break;
        }

        result.setValue(returnObject);

        return result;
    }

    /**
     * Reads the census classes from the .csv file
     * @param filePath - the file path of the file to read in
     * @param columnIndent - the number of columns to ignore at the front of the file
     * @param digPopFileType - the type of DigPop File Type provided
     * @return the found classes
     */
    private static Result readClassNamesFromFirstLine(
            String filePath,
            int columnIndent,
            DigPopFileTypeEnum digPopFileType) {
        Result result = new Result();

        ArrayList<Class> foundClasses = new ArrayList<Class>();

        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            int classIDCounter = 1;

            if ((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for (int count = columnIndent; count < lineInfo.length; count++) {
                    Class newClass = new Class(lineInfo[count], count, false, classIDCounter, digPopFileType);
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

    /**
     * Gets the survey data column values from the .csv file
     * @param filePath - the path of the file to read
     * @param columnNumber - the number of columns to ignore at the start of the file
     * @param tableType - the type of file, either household or population data
     * @return the found column values
     */
    public static Result getSurveyDataColumnValues(String filePath, int columnNumber, String tableType) {
        Result result = new Result();
        List<SurveyColumnValue> columnValues = new ArrayList<SurveyColumnValue>();

        String line = "";
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if (lineCounter >= 1) {
                    // use comma as separator, but allow for commas inside a string
                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    int value = Integer.parseInt(lineInfo[columnNumber]);

                    //boolean alreadyFound = columnValues.stream().anyMatch(c -> c.getValue() == value);
                    Optional<SurveyColumnValue> foundFromStream = columnValues.stream().filter(c -> c.getValue() == value).findFirst();

                    if (!foundFromStream.isPresent()) {
                        columnValues.add(new SurveyColumnValue(lineCounter, Integer.parseInt(lineInfo[columnNumber]), false, 1, tableType));
                    } else {
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

    /**
     * Gets the values of the selected census classes and adds them to the column total
     * @param filePath - the file path for the census enumerations .csv file
     * @param censusClasses - the selected classes
     * @return the selected values from the selected census columns
     */
    public static Result getSelectedCensusColumnValues(String filePath, List<Class> censusClasses) {
        Result result = new Result();

        String line = "";
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if (lineCounter >= 1) {
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

    /**
     * Gets the classes from the land use .asc file
     * @param filePath - the file path of the land use .asc file
     * @return - the classes in the land use file
     * @throws IOException if the file is not found, or classes are not found
     */
    public static Result getClassesFromLandUseASCFile(
            String filePath) throws IOException {

        Result result = new Result(true);

        List<String> classes = null;

        int counter = 0;

        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (counter >= 6) {
                    String[] lineInfo = line.split(" ");

                    if (classes == null) {
                        classes = Arrays.asList(lineInfo).stream().distinct().collect(Collectors.toList());
                    } else {
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

    /**
     * Create a new census enumeration file
     * @param oldFilePath - the original census enumeration file's path
     * @param newFilePath - the path for the new census enumeration file
     * @param newDetailsToAdd - the ArrayList for each of the new columns and related values to add
     * @return success or failure
     * @throws IOException If can't find file or can't write to file
     */
    public static Result outputNewCensusFile(
            String oldFilePath,
            String newFilePath,
            ArrayList<NewCensusColumnDetails> newDetailsToAdd) throws IOException {

        Result result = new Result(true);

        ArrayList<String> outputLines = new ArrayList<>();

        int counter = 1;

        FileInputStream inputStream = null;

        Scanner sc = null;
        try {
            inputStream = new FileInputStream(oldFilePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (counter == 1) {
                    for (NewCensusColumnDetails newInfo : newDetailsToAdd) {
                        line = line + ", " + newInfo.getNewColumnHeader(); // + "_" + newInfo.getRandomPercentage() + "%";
                    }
                } else {

                    String[] lineInfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    for (NewCensusColumnDetails newInfo : newDetailsToAdd) {
                        int oldValue = 0;
                        for(int oldColumnNumber : newInfo.getOldValueLookUpColumns()){
                            oldValue = Integer.parseInt(lineInfo[oldColumnNumber]);
                        }
                        
                        int newValue = (int) (oldValue * newInfo.getRandomPercentage());

                        line = line + ", " + newValue;
                    }
                }

                outputLines.add(line);

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

        if (result.isSuccess()) {
            result = FileUtility.WriteNewTextFileFromArrayOfLines(newFilePath, outputLines);
        }

        return result;
    }

    /**
     * Creates a new census .csv file
     * @param markovChains - the list of all Markov Chains created
     * @param numberOfRuns - the number of runs from the Run File step
     * @param censusEnumerationFullPath - the filepath of the census enumeration file
     * @param fileDirectory - the directory where all files for this run are saved
     * @return the new census enumeration .csv output file
     */
    public static Result CreateNewCensusCSVFiles(
            ArrayList<MarkovChain> markovChains,
            int numberOfRuns,
            String runName,
            String censusEnumerationFullPath,
            String fileDirectory) {

        Result result = new Result(true);
        int counter = 1;

        String onlyFilename = (new File(censusEnumerationFullPath)).getName();

        while (counter <= numberOfRuns && result.isSuccess()) {
            String newFileName = String.format(
                    "%s\\%s_Run_%s_%s", 
                    fileDirectory,
                    runName,
                    counter, onlyFilename);
            ArrayList<NewCensusColumnDetails> newColumns = new ArrayList<>();
            for (MarkovChain markovChain : markovChains) {
                newColumns.addAll(markovChain.getNewCensusColumnDetails());
            }
            try {
                result = DigPopGUIUtilityClass.outputNewCensusFile(
                        censusEnumerationFullPath,
                        newFileName,
                        newColumns);
            } catch (IOException ex) {
                Logger.getLogger(MarkovChainMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
            counter++;
        }

        return result;

    }

}
