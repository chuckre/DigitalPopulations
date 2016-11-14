/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.HelpFile;
import cerl.gui.standard.utilities.Result;
import java.io.File;

/**
 *
 * @author ajohnson
 */
public class DigPopGUIUtilityClass {
    
    private static final String HELP_FILE_PATH = "./docs/HelpText.xml";
    
    public static HelpFile getDefaultHelpFile(){
        
        File helpFileLocation = new File(HELP_FILE_PATH);
        Result result = FileUtility.ParseXMLFileIntoSpecifiedObject(helpFileLocation.getPath(), HelpFile.class);
        return (HelpFile)result.getValue();
        
    }
}
