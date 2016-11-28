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
public enum StepOneInstructionNames {
        Land_Use_Household_Map("Land Use or Household Density Map"),
        Region_Map("Region Map"),
        Census_Enumerations("Census Enumerations"),
        Constraint_Map("Constraint Map"),
        Population_Micro_Data("Population Micro-Data"),
        Household_Micro_Data("Household Micro-Data");
        
        private String instructionName = ""; 
    
        private StepOneInstructionNames(String name)
        {
            this.instructionName = name;
        }

        @Override
        public String toString() {
            return instructionName;
        }
    }
