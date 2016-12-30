/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 * Maintains the list of screen names used for pulling Help Information
 * @author ajohnson
 */
public enum HelpFileScreenNames {
    STEP_ONE_HELP_FILE_NAME ("Step 1 - Maps"),
    STEP_TWO_HELP_FILE_NAME ("Step 2 - Goal Relationship File"),
    STEP_THREE_HELP_FILE_NAME ("Step 3 - Census and Survey Data"),
    STEP_FOUR_HELP_FILE_NAME ("Step 4 - Markov Chain"),
    STEP_FIVE_HELP_FILE_NAME ("Step 5 - Generate Fitting Criteria File"),
    STEP_SIX_HELP_FILE_NAME ("Step 6 - Generate Trait Clusters"),
    STEP_SEVEN_HELP_FILE_NAME ( "Step 7 - Generate Run Parameter File");
    
    private String screenName = ""; 
    
    /**
     * Sets the help file's screen name
     * @param name - the new screen name
     */
    private HelpFileScreenNames(String name)
    {
        this.screenName = name;
    }

    /**
     * Gets the screen name of the current help file
     * @return - the screen name
     */
    @Override
    public String toString() {
        return screenName;
    }
}
