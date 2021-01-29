/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * The class to manage file types and file manipulation within the DigPop GUI
 * @author ajohnson
 */
public class FileUtility {

    /**
     * Default path separator.
     */
    private static final char DEFAULT_PATH_SEPARATOR = '.';

    /**
     * Verifies that the sent in file matches the sent in File Type.
     *
     * @param expectedFileType
     * @param file
     * @return
     */
    public static Result VerifyFileType(
            FileType expectedFileType,
            File file) {

        Result result = new Result();
        
        boolean validFileType = false;
        
        result = getFileExtension(file.getName(), DEFAULT_PATH_SEPARATOR);

        if (result.isSuccess()) {
            String foundFileType = (String) result.getValue();
            if (foundFileType != null
                    && expectedFileType.toString().equals(foundFileType.toUpperCase())) {
                validFileType = true;
            }

            result.setSuccess(true);
        }

        result.setValue(validFileType);
        return result;
    }

    /**
     * Verifies that another file with the same name/directory exists with the new expected type
     * Used to check if the matching .prj file exists for an .asc file in the same folder
     * 
     * @param orginalFile - The file who's match needs to exist in the same folder
     * @param secondaryExpectedFileType - The matching type to look for
     * @return Result - with success/failure of test
     */
    public static Result VerifySecondaryFileExists(
            File orginalFile,
            FileType secondaryExpectedFileType) {
        Result result = new Result();
        boolean valid = false;

        result = getFileNameNoExtension(
                orginalFile.getName(),
                DEFAULT_PATH_SEPARATOR);

        if (result.isSuccess()) {
            String fileNameWithoutExtension;
            fileNameWithoutExtension = (String) result.getValue();
            String fileLocation = orginalFile.getParent();

            String toVerifyPath
                    = String.format("%s\\%s%s%s",
                            fileLocation,
                            fileNameWithoutExtension,
                            DEFAULT_PATH_SEPARATOR,
                            secondaryExpectedFileType.toString());

            File newToVerifyFile = new File(toVerifyPath);
            valid = newToVerifyFile.exists();
        }

        result.setValue(valid);
        return result;
    }
    
    /**
     * Checks that a file name exists, not just a blank extension
     * @param fileNameWithExtension - The full name of the file with extension
     * @param pathSeparator - The separator used for the paths
     * @return 
     */
    private static Result getFileNameNoExtension(
            String fileNameWithExtension,
            char pathSeparator){
        
        Result result = new Result();
        
        String foundFileNameNoExtension = "";
        
        int seperatorIndex = fileNameWithExtension.lastIndexOf(pathSeparator);
        
        if (seperatorIndex > -1) {
            foundFileNameNoExtension = fileNameWithExtension.substring(0, seperatorIndex);
            result.setSuccess(true);
        } else {
            result.setErrorMessage(
                    "getFileNameNoExtension",
                    "No File Name was found");
            result.setSuccess(false);
        }

        result.setValue(foundFileNameNoExtension);
        
        return result;
    }

    /**
     * Gets the file name of a current file name with extension
     * @param fileNameWithExtension - The full file name including the extension
     * @param pathSeparator - The character used to separate paths
     * @return Result - Result of the check
     */
    public static Result getFileExtension(
            String fileNameWithExtension,
            char pathSeparator) {
        Result result = new Result();
        String finalFoundFileExtension = "";

        int seperatorIndex = fileNameWithExtension.lastIndexOf(pathSeparator);

        if (seperatorIndex > -1) {
            seperatorIndex++;
            finalFoundFileExtension = fileNameWithExtension.substring(seperatorIndex);
            result.setSuccess(true);
        } else {
            result.setErrorMessage(
                    "getFileExtension",
                    "No FileExtension was found");
            result.setSuccess(false);
        }

        result.setValue(finalFoundFileExtension);
        return result;
    }

    /**
     * Creates a new text file for the given file path. The new file will
     * contain the sent in string.
     *
     * @param newFilePath The new file path of the newly created file.
     * @param newFileText Text for the new file.
     * @return
     */
    public static Result WriteNewTextFile(
            String newFilePath,
            String newFileText) {
        Result result = new Result();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFilePath);
            byte[] output = newFileText.getBytes();
            out.write(output);

            result.setSuccess(true);
        } catch (FileNotFoundException ex) {
            result.setErrorMessage("WriteNewTextFile", ex.getMessage());
            result.setSuccess(false);
        } catch (IOException ex) {
            result.setErrorMessage("WriteNewTextFile", ex.getMessage());
            result.setSuccess(false);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                result.setErrorMessage("WriteNewTextFile", ex.getMessage());
                result.setSuccess(false);
            }
        }

        return result;
    }
    
    /**
     * Creates the new file based on the newFilePath. The Array List of strings
     * is added to the new file line by. 
     * @param newFilePath New file path to be created.
     * @param outputLines Array List of Strings to be added to the new file. 
     * @return 
     */
    public static Result WriteNewTextFileFromArrayOfLines(
            String newFilePath,
            ArrayList<String> outputLines) {
        Result result = new Result();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFilePath);
            
            for(String line : outputLines){
                line = line + "\n";//adds a line break at the end of every line
                byte[] output = line.getBytes();
                out.write(output);
            }
            result.setValue(newFilePath);
            result.setSuccess(true);
        } catch (FileNotFoundException ex) {
            result.setErrorMessage("WriteNewTextFileFromArrayOfLines", ex.getMessage());
            result.setSuccess(false);
        } catch (IOException ex) {
            result.setErrorMessage("WriteNewTextFileFromArrayOfLines", ex.getMessage());
            result.setSuccess(false);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                result.setErrorMessage("WriteNewTextFileFromArrayOfLines", ex.getMessage());
                result.setSuccess(false);
            }
        }

        return result;
    }
    
    /**
     * Reads a text file
     * @param filePath - the file to read
     * @return Result - holds the value with the file contents if successful
     */
    public static Result ReadTextFile(
            String filePath) {
        Result result = new Result();
        
        //build result string
        String fileContent = null;
        String line = null;
                
        try{
            //Read the file
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            //read line by line
            while((line = bufferedReader.readLine()) != null){
                fileContent += line + ",";
            }
            //close file
            bufferedReader.close();
            result.setValue(fileContent);
        } catch(FileNotFoundException ex){
            result.setErrorMessage("FileNotFoundException" + ex.getMessage());
        } catch (IOException io){
            result.setErrorMessage("IOException" + io.getMessage());
        }
        
        return result;
    }

    /**
     * File Utility class that will parse a given XML file into a specified
     * object.
     *
     * Example: HelpFile.xml will be parsed into the HelpFile object.
     *
     * @param filePath The file path of the parsed XML file.
     * @param classType The Object type that the XML file will be parsed into.
     * Object loaded with the data from the given XML file.
     */
    public static Result ParseXMLFileIntoSpecifiedObject(
            String filePath,
            Class classType) {
        Result result = new Result();

        try {
            File file = new File(filePath);

            JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            result.setValue(jaxbUnmarshaller.unmarshal(file));

            result.setSuccess(true);
        } catch (JAXBException ex) {
            result.setErrorMessage(
                    "ParseXMLFileIntoSpecifiedObject",
                    ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }
    
    /**
     * File Utility class that will parse a given XML file into a specified
     * object.
     *
     * Example: HelpFile.xml will be parsed into the HelpFile object.
     *
     * @param url The url path of the parsed XML file.
     * @param classType The Object type that the XML file will be parsed into.
     * Object loaded with the data from the given XML file.
     * @return 
     */
    public static Result ParseXMLFileIntoSpecifiedObjectFromURL(
            URL url,
            Class classType) {
        Result result = new Result();

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            result.setValue(jaxbUnmarshaller.unmarshal(url));

            result.setSuccess(true);
        } catch (JAXBException ex) {
            result.setErrorMessage(
                    "ParseXMLFileIntoSpecifiedObject",
                    ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }
    
    /**
     * Parses an object into an XML File
     * @param objectToParseIntoXML - The object to save as XML
     * @param filePath - The path to save the file into
     * @param classType - The class type of object provided
     * @return Result - if successful or failed parsing into XML
     */
    public static Result ParseObjectToXML(
            Object objectToParseIntoXML, 
            String filePath,
            Class classType){
        Result result = new Result();
        
        try {
            File file = new File(filePath);
            
            JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            jaxbMarshaller.marshal(objectToParseIntoXML, file);
            
            result.setSuccess(true);
            result.setValue(filePath);
        } catch (JAXBException ex) {
            result.setErrorMessage(
                    "ParseObjectToXML",
                    ex.getMessage());
            result.setSuccess(false);
        }
        
        return result;
    }
    
    /**
     * Create a String name for a file based on the type of file and timestamp
     * @param addCurrentDateTime - Current timestamp
     * @param starterName - The beginning of the file name to use
     * @param type - The DigPop File Type to create
     * @return String as date_starterName.type
     */
    public static String createNewFileName(Boolean addCurrentDateTime, String starterName, FileType type){
        String result = "";
        
        if(addCurrentDateTime){
            String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            result = String.format(
                    "%s_%s.%s", 
                    dateString,
                    starterName.replaceAll("[^a-zA-Z0-9-_\\.]", "_"),
                    type.toString());
        } else{
            result = starterName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
            result = result.concat(type.toString());
        }
        
        return result;
    }
    
    /**
     * Creates a copy of the provided file
     * @param file - the File to copy
     * @return String of the new file path
     */
    public static String createNewValidCopyOfFileName(File file){
        String newPath = null;
        
        boolean newValidFound = false;
        
        int counter = 0;
        
        while(!newValidFound){
            newPath = String.format("%s\\%s", file.getParent(), file.getName());
            File newTestFile = new File(newPath);
            if(newTestFile.exists()){
                
            }
        }
        
        return newPath;
    }
    
}
