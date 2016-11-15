/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 *
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
    
    private HelpFileScreenNames(String name)
    {
        this.screenName = name;
    }

    @Override
    public String toString() {
        return screenName;
    }
}
