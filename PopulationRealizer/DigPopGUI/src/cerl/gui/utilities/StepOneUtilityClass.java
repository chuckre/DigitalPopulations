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
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * The utility class for handling valid file types and errors in Step 1
 * @author ajohnson
 */
public class StepOneUtilityClass {
    
    private final static FileType LAND_USE_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType LAND_USE_MAP_SECONDARY_FILE_TYPE = FileType.PRJ;
    public final static String LAND_USE_MAP_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING LAND USE MAP: NEED ASC FILE AND MATCHING PRJ FILE.";
    
    private final static FileType HOUSEHOLD_DENSITY_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType HOUSEHOLD_DENSITY_MAP_SECONDARY_FILE_TYPE = FileType.PRJ;
    public final static String HOUSEHOLD_DENSITY_MAP_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING HOUSEHOLD DENSITY MAP: NEED ASC FILE AND MATCHING PRJ FILE.";
    
    private final static FileType REGION_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType REGION_MAP_SECONDARY_FILE_TYPE = FileType.PRJ;
    public final static String REGION_MAP_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING REGION MAP: NEED ASC FILE AND MATCHING PRJ FILE.";
    
    private final static FileType CENSUS_ENUMERATIONS_FILE_TYPE = FileType.CVS;
    public final static String CENSUS_ENUMERATIONS_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING CENSUS ENUMERATIONS: NEED CSV FILE.";
    
    private final static FileType CONSTRAINT_MAP_FILE_TYPE = FileType.ASC;
    private final static FileType CONSTRAINT_MAP_SECONDARY_FILE_TYPE = FileType.PRJ;
    public final static String CONSTRAINT_MAPS_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING CONSTRAINT MAPS: NEED ASC FILE AND MATCHING PRJ FILE.";
    
    private final static FileType POPULATION_MICRO_DATA_FILE_TYPE = FileType.CVS;
    public final static String POPULATION_MICRO_DATA_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING POPULATION MICRO DATA: NEED CSV FILE.";
    
    private final static FileType HOUSEHOLD_MICRO_DATA_FILE_TYPE = FileType.CVS;
    public final static String HOUSEHOLD_MICRO_DATA_FILE_PATH_ERROR_MESSAGE = 
            "*WARNING HOUSEHOLD MICRO DATA: NEED CSV FILE.";
    
    public final static String VALID_IMAGE_ICON_FILEPATH = "/cerl/gui/resources/check.png";
    private final static int VALID_IMAGE_ICON_WIDTH = 25;
    private final static int VALID_IMAGE_ICON_HEIGHT = 25;
    
    public final static String INVALID_IMAGE_ICON_FILEPATH = "/cerl/gui/resources/stop.png";
    private final static int INVALID_IMAGE_ICON_WIDTH = 25;
    private final static int INVALID_IMAGE_ICON_HEIGHT = 25;
    
    /**
     * Gets the icon used to delineate a valid item provided by the user
     * @return the ImageIcon of the icon, scaled to size
     */
    public static ImageIcon GetValidImageIcon(){

        URL url = StepOneUtilityClass.class.getResource(StepOneUtilityClass.VALID_IMAGE_ICON_FILEPATH);
        
        ImageIcon newImageIcon = ImageUtility.CreateSizedImageIconScaledSmooth(
                url,
                VALID_IMAGE_ICON_WIDTH,
                VALID_IMAGE_ICON_HEIGHT);
        
        return newImageIcon;
    }
    
    /**
     * Gets the icon used to delineate invalid, missing, or erroneous items provided by the user
     * @return the ImageIcon of the icon, scaled to size
     */
    public static ImageIcon GetInValidImageIcon(){
        
        URL url = StepOneUtilityClass.class.getResource(StepOneUtilityClass.INVALID_IMAGE_ICON_FILEPATH);
        
        ImageIcon newImageIcon = ImageUtility.CreateSizedImageIconScaledSmooth(
                url,
                INVALID_IMAGE_ICON_WIDTH,
                INVALID_IMAGE_ICON_HEIGHT);
        
        return newImageIcon;
    }
    
    /**
     * Checks the provided file if it matches the required File Type
     * If applicable, checks that the matching .asc/.prj file exists as well
     * @param file - the File to check
     * @param fileType - the type the file should be
     * @return a Result if the file type exists, and is of the correct file type
     */
    public static Result verifyFile(File file, DigPopFileTypeEnum fileType){
        
        Result result = new Result();
        
        switch(fileType){
            case Land_Use_Map:
                result = FileUtility.VerifyFileType(LAND_USE_MAP_FILE_TYPE, file);
                
                if(result.isSuccess())
                {
                    result = FileUtility.VerifySecondaryFileExists(file, LAND_USE_MAP_SECONDARY_FILE_TYPE);
                }
                break;
            case Household_Density_Map:
                result = FileUtility.VerifyFileType(HOUSEHOLD_DENSITY_MAP_FILE_TYPE, file);
                
                if(result.isSuccess())
                {
                    result = FileUtility.VerifySecondaryFileExists(file, HOUSEHOLD_DENSITY_MAP_SECONDARY_FILE_TYPE);
                }
                break;
            case Region_Map:
                result = FileUtility.VerifyFileType(REGION_MAP_FILE_TYPE, file);
                
                if(result.isSuccess())
                {
                    result = FileUtility.VerifySecondaryFileExists(file, REGION_MAP_SECONDARY_FILE_TYPE);
                }
                break;
            case Census_Enumerations:
                result = FileUtility.VerifyFileType(CENSUS_ENUMERATIONS_FILE_TYPE, file);
                break;
            case Constraint_Map:
                result = FileUtility.VerifyFileType(CONSTRAINT_MAP_FILE_TYPE, file);
                
                if(result.isSuccess())
                {
                    result = FileUtility.VerifySecondaryFileExists(file, HOUSEHOLD_DENSITY_MAP_SECONDARY_FILE_TYPE);
                }
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