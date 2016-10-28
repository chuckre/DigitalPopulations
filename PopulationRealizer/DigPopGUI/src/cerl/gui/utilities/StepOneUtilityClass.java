/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import cerl.gui.standard.utilities.FileType;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.ImageUtility;
import cerl.gui.standard.utilities.Result;
import java.io.File;
import javax.swing.ImageIcon;

/**
 *
 * @author ajohnson
 */
public class StepOneUtilityClass {
    
    private final static FileType LAND_USE_MAP_HOUSEHOLD_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType REGION_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType REGION_MAP_SECONDARY_FILE_TYPE = FileType.PRJ;
    private final static FileType CENSUS_ENUMERATIONS_FILE_TYPE = FileType.CVS;
    private final static FileType CONSTRAINT_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType POPULATION_MICRO_DATA_FILE_TYPE = FileType.CVS;
    private final static FileType HOUSEHOLD_MICRO_DATA_FILE_TYPE = FileType.CVS;
    
    private final static String VALID_IMAGE_ICON_FILEPATH = "src/cerl/gui/resources/check.png";
    private final static int VALID_IMAGE_ICON_WIDTH = 25;
    private final static int VALID_IMAGE_ICON_HEIGHT = 25;
    
    private final static String INVALID_IMAGE_ICON_FILEPATH = "src/cerl/gui/resources/stop.png";
    private final static int INVALID_IMAGE_ICON_WIDTH = 25;
    private final static int INVALID_IMAGE_ICON_HEIGHT = 25;
    
    public enum DigPopFileTypeEnum {
        Land_Use_Household_Map,
        Region_Map,
        Census_Enumerations,
        Constraint_Map,
        Population_Micro_Data,
        Household_Micro_Data
    }
    
    public static ImageIcon GetValidImageIcon(){
        ImageIcon newImageIcon = ImageUtility.CreateSizedImageIconScaledSmooth(
                VALID_IMAGE_ICON_FILEPATH,
                VALID_IMAGE_ICON_WIDTH,
                VALID_IMAGE_ICON_HEIGHT);
        
        return newImageIcon;
    }
    
    public static ImageIcon GetInValidImageIcon(){
        ImageIcon newImageIcon = ImageUtility.CreateSizedImageIconScaledSmooth(
                INVALID_IMAGE_ICON_FILEPATH,
                INVALID_IMAGE_ICON_WIDTH,
                INVALID_IMAGE_ICON_HEIGHT);
        
        return newImageIcon;
    }
    
    public static Result verifyFile(File file, DigPopFileTypeEnum fileType){
        
        Result result = new Result();
        
        switch(fileType){
            case Land_Use_Household_Map:
                result = FileUtility.VerifyFileType(LAND_USE_MAP_HOUSEHOLD_MAP_FILE_TYPE, file);
                break;
            case Region_Map:
                result = FileUtility.VerifyFileType(REGION_MAP_FILE_TYPE, file);
                
                if(result.isSuccess())
                {
                    result = FileUtility.VeirfySecondaryFileExists(file, REGION_MAP_SECONDARY_FILE_TYPE);
                }
                break;
            case Census_Enumerations:
                result = FileUtility.VerifyFileType(CENSUS_ENUMERATIONS_FILE_TYPE, file);
                break;
            case Constraint_Map:
                result = FileUtility.VerifyFileType(CONSTRAINT_MAP_FILE_TYPE, file);
                break;
            case Population_Micro_Data:
                result = FileUtility.VerifyFileType(POPULATION_MICRO_DATA_FILE_TYPE, file);
                break;
            case Household_Micro_Data:
                result = FileUtility.VerifyFileType(HOUSEHOLD_MICRO_DATA_FILE_TYPE, file);
                break;
            default:
                result.setErrorMessage(
                        "verifyFile", 
                        "Selected DigPopFileTypeEnum value not a valid switch option.");
                result.setSuccess(false);
                break;
        }
        
        return result;
    }
}