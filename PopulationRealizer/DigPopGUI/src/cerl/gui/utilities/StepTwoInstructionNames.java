/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 * The object to store the help instruction names for all fields in Step 2
 * @author ajohnson
 */
public enum StepTwoInstructionNames {
        Census_File("Census File"),
        Population_File("Population File"),
        Household_File("Household File"),
        Household_Density_Map_File("Household Density Map File"),
        Land_Use_Map_File("Land Use Map Information"),
        Vacant_Classes("Vacant Classes"),
        Vacant_Class_Description("Vacant Class Description"),
        Combination_Classes("Combination Classes"),
        Classes("Classes"),
        Target("Target"),
        Description("Description"),
        Region_Map("Region Map"),
        Constraint_Map("Constraint Map");
                              
        private String instructionName = ""; 
    
        /**
         * Sets the instruction name
         * @param name - the new instruction name
         */
        private StepTwoInstructionNames(String name)
        {
            this.instructionName = name;
        }

        /**
         * Gets the instruction name as a string
         * @return the instruction name
         */
        @Override
        public String toString() {
            return instructionName;
        }
    }