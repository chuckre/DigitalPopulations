/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 *
 * @author mrivera
 */
public enum StepSevenInstructionNames {
        Name_Of_Run("Name of Run"),
        Fitting_File_Path("Fitting File path"),
        Phase1_Log_Results("Log the results of phase 1?"),
        Log_Quality_Evaluation("Log the quality evaluation reports between phases?"),
        Household_archetype("Does each Household record contain a full copy of the archetype record?"),
        Population_archetype("Does each Population record contain a full copy of the archetype record?"),
        First_Realization_Index("First Realization Index"),
        Final_Realization_Index("Final Realization Index"),
        Random_Number_Seed("Random Number seed"),
        First_Census_Tract("Use only the first census tract?"),
        Output_Directory("Output Directory"),
        Parallel_Threads("Number of parallel threads"),
        Phase1_Time_Limit("Phase 1 Time limit"),
        Phase2_Random_Placement("Phase 2 - Random Placement Percentage"),
        Phase2_Skip_Tracts("Phase 2 - Skip Tracts Probability"),
        Phase2_Skip_Tracts_Probability("Phase 2 - Skipped Tracts Probability Delta"),
        Phase34_Save_Interval("Phase 3 and 4 Intermediate Save Interval"),
        Phase3_Skip("Skip Phase 3?"),
        Phase3_Time_Limit("Phase 3 Time limit"),
        Phase4_Lags("Phase 4 - Number of Lags"),
        Phase4_Save("Phase 4 - Save both ends?"),
        Phase4_Skip("Skip Phase 4?"),
        Phase4_Time_Limit("Phase 4 Time Limit");
        
        private String instructionName = ""; 
    
        private StepSevenInstructionNames(String name)
        {
            this.instructionName = name;
        }

        @Override
        public String toString() {
            return instructionName;
        }    
}
