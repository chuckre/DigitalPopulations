/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 * Used for the names of each of the instruction names, to be pulled into the help files of Step 1
 * @author ajohnson
 */
public enum StepOneInstructionNames {
        Land_Use_Household_Map("Land Use or Household Density Map"),
        Region_Map("Region Map"),
        Census_Enumerations("Census Enumerations"),
        Constraint_Map("Constraint Map"),
        Population_Micro_Data("Population Micro-Data"),
        Household_Micro_Data("Household Micro-Data");
        
        private String instructionName = ""; 
    
        /**
         * Sets the name of the instruction
         * @param name - the new Instruction Name
         */
        private StepOneInstructionNames(String name)
        {
            this.instructionName = name;
        }

        /**
         * Gets the string value of the instruction name
         * @return 
         */
        @Override
        public String toString() {
            return instructionName;
        }
    }

