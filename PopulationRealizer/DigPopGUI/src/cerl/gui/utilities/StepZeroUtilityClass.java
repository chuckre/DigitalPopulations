/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.standard.utilities.Result;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * A utility class to handle creating and verifying the initial DigPop save object
 * @author ajohnson
 */
public class StepZeroUtilityClass {
    /**
     * Creates a new empty save file for the user if they are starting a new run
     * @param file - the File to save the new DigPop object to
     * @return the result of the creation and saving of the file
     * @throws IOException if the file cannot be used
     */
    public static Result CreateNewEmptySaveFile(File file) throws IOException{
        
        Result result = new Result();
        
        if(file.createNewFile()){
            DigPopGUIInformation digPopGUIInformation = new DigPopGUIInformation();
            digPopGUIInformation.setFilePath(file.getPath());
            digPopGUIInformation.setCreatedDate(Calendar.getInstance().getTime().toString());
            
            result = DigPopGUIUtilityClass.saveDigPopGUIInformationSaveFile(
                    digPopGUIInformation, 
                    file.getPath());
        }else {
            result.setSuccess(false);
            result.setErrorMessage("File name is not valid please select a new file name/location.");
        }
        
        return result;
    }
    
    /**
     * Verifies the provided file to check if it is a valid XML Save file
     * @param file - the file to check
     * @return the result of the check, if the DigPop object could be read in
     */
    public static Result verifyXMLFile(File file){
        Result result = new Result();
        result = DigPopGUIUtilityClass.readInDigPopGUIInformationSaveFile(file.getPath());
        return result;
    }
    
}
