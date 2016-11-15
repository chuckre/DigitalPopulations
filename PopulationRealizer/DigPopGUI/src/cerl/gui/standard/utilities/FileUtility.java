/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author ajohnson
 */
public class FileUtility {

    private static final char DEFAULT_PATH_SEPERATOR = '.';

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
        
        result = getFileExtension(file.getName(), DEFAULT_PATH_SEPERATOR);

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

    public static Result VeirfySecondaryFileExists(
            File orginalFile,
            FileType secondaryExpectedFileType) {
        Result result = new Result();
        boolean valid = false;

        result = getFileNameNoExtension(
                orginalFile.getName(),
                DEFAULT_PATH_SEPERATOR);

        if (result.isSuccess()) {
            String fileNameWithoutExtension;
            fileNameWithoutExtension = (String) result.getValue();
            String fileLoaction = orginalFile.getParent();

            String toVerifyPath
                    = String.format(
                            "%s\\%s%s%s",
                            fileLoaction,
                            fileNameWithoutExtension,
                            DEFAULT_PATH_SEPERATOR,
                            secondaryExpectedFileType.toString());

            File newToVerifyFile = new File(toVerifyPath);
            valid = newToVerifyFile.exists();
        }

        result.setValue(valid);
        return result;
    }
    
    private static Result getFileNameNoExtension(
            String fileNameWithExtension,
            char pathSeperator){
        
        Result result = new Result();
        
        String foundFileNameNoExtension = "";
        
        int seperatorIndex = fileNameWithExtension.lastIndexOf(pathSeperator);
        
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

    public static Result getFileExtension(
            String fileNameWithExtension,
            char pathSeperator) {
        Result result = new Result();
        String finalFoundFileExtension = "";

        int seperatorIndex = fileNameWithExtension.lastIndexOf(pathSeperator);

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
            jaxbMarshaller.marshal(objectToParseIntoXML, System.out);
            
            result.setSuccess(true);
            
        } catch (JAXBException ex) {
            result.setErrorMessage(
                    "ParseObjectToXML",
                    ex.getMessage());
            result.setSuccess(false);
        }
        
        return result;
    }
}
