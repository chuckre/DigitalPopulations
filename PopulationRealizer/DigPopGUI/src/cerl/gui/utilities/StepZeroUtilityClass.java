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
 *
 * @author ajohnson
 */
public class StepZeroUtilityClass {
    
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
    
    public static Result verifyXMLFile(File file){
        Result result = new Result();
        result = DigPopGUIUtilityClass.readInDigPopGUIInformationSaveFile(file.getPath());
        return result;
    }
    
}
