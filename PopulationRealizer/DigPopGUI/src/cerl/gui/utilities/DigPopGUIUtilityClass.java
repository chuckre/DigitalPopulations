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
import java.io.File;
import java.util.Calendar;

/**
 *
 * @author ajohnson
 */
public class DigPopGUIUtilityClass {
    
    private static final String HELP_FILE_PATH = "./docs/HelpText.xml";
    
    private static HelpFile getDefaultHelpFile(){
        
        File helpFileLocation = new File(HELP_FILE_PATH);
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(helpFileLocation.getPath(), HelpFile.class);
        return (HelpFile)result.getValue();
        
    }
    
    public static void loadDefaultHelpGUIByScreenName(String screenName){
        HelpFile newHelpFile = getDefaultHelpFile();
        new HelpFileDisplay(newHelpFile.getSelectedScreenByName(screenName), newHelpFile).setVisible(true);
    }
    
    public static void loadDefaultHelpGUIByScreenInstructionName(String screenName, String instructionName){
        HelpFile newHelpFile = getDefaultHelpFile();
        
        Instruction selectedInstruction = 
                newHelpFile.getSelectedScreenByName(screenName)
                        .getSelectedInstructionByName(instructionName);
        
        new HelpFileDisplay(selectedInstruction, newHelpFile).setVisible(true);
    }
    
    public static Result saveDigPopGUIInformationSaveFile(
            DigPopGUIInformation information, 
            String path){
        
        information.setLastSaveDate(Calendar.getInstance().getTime().toString());
        Result result = FileUtility.ParseObjectToXML(information, path, DigPopGUIInformation.class);
        result.setValue(information);
        
        return result;
    }
    
    public static Result readInDigPopGUIInformationSaveFile(String path){
         Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(path, DigPopGUIInformation.class);
         
         return result;
    }
}
