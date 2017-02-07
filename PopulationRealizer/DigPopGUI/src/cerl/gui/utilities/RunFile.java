/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.Date;

/**
 * Handles all the variables used in the last-run.properties file
 * Provided by the user in the last step of the DigPop GUI
 * Additional information can be found at http://digitalpopulations.pbworks.com/w/page/30614056/schema%3A%20parameters%20file
 * @author ajohnson
 */
public class RunFile {
    private String runName;
    private Date dateOfRun;
    private String criteria_file;
    private Boolean do_dump_number_archtypes;
    private Boolean do_dump_statistics;
    private Boolean do_write_all_hoh_fields;
    private Boolean do_write_all_pop_fields;
    private Integer final_rzn_num;
    private Integer first_rzn_num;
    private Long initial_seed;
    private Boolean only_one_region;
    private String output_dir;
    private Integer parallel_threads;
    private Double phase1_time_limit;
    private Double phase2_random_tract_prob;
    private Double phase2_tract_skip_prob_delta;
    private Double phase2_tract_skip_prob_init;
    private Double phase3_save_intermediate;
    private Boolean phase3_skip;
    private Double phase3_time_limit;
    private Integer phase4_num_lags;
    private Boolean phase4_save_both_ends;
    private Boolean phase4_skip;
    private Double phase4_time_limit;

    /**
     * Creates a new, blank run file object, with the default values
     * All Skip Options default to "False": phase3_skip, phase4_skip
     * All other true/false values default to "True": do_dump_number_archtypes, do_dump_statistics, do_write_all_hoh_fields ,do_write_all_pop_fields, only_one_region, phase4_save_both_ends
     */
    public RunFile() {
        this.do_dump_number_archtypes = true;
        this.do_dump_statistics = true;
        this.do_write_all_hoh_fields = true;
        this.do_write_all_pop_fields = true;
        this.only_one_region = false;
        this.phase3_skip = false;
        this.phase4_save_both_ends = true;
        this.phase4_skip = false;
    }

    /**
     * Creates a new run file object with the provided parameters
     * @param runName - the text value for the run name
     * @param dateOfRun - the date the user ran it - defaults to the current timestamp
     * @param criteria_file - path and name of main fitting criteria file, relative to current directory
     * @param do_dump_number_archtypes - If true, the log file will receive the results of phase 1.
     * @param do_dump_statistics - If true, the log file will receive detailed reports from  the quality evaluation objects between phases.
     * @param do_write_all_hoh_fields - If false, each record in households.csv will contain coordinates plus a reference to one of the archtypes.  If true, each record will also contain a full copy of the archtype record.
     * @param do_write_all_pop_fields - If false, each record in populations.csv will contain coordinates plus a reference to one of the archtypes.  If true, each record will also contain a full copy of the archtype record.
     * @param final_rzn_num - Index of first realization to generate. 
     * @param first_rzn_num -  Index of final realization to generate.
     * @param initial_seed - Random seed that will guide the creation of all random numbers. 
     * @param only_one_region - This is a speed hack that causes algorithm to use only the first census tract.  
     * @param output_dir - Target directory for all output files, relative to current directory.
     * @param parallel_threads - The number of threads being run at the same time in parallel. 
     * @param phase1_time_limit - Limit phase 1 to this many minutes.
     * @param phase2_random_tract_prob - This fraction of households will be placed randomly, regardless of whether the placement is a good fit or not.  Setting to zero will greatly improve the output of phase 2, but also take a great deal longer.
     * @param phase2_tract_skip_prob_delta - If phase2_tract_skip_prob_init caused phase 2 to skip every tract, then the fraction will be reduced by this amount, and placement will be tried again.
     * @param phase2_tract_skip_prob_init - This fraction of tracts will be ignored when phase 2 tries to place a household into a tract.  If phase2_random_tract_prob is triggered, then this setting is not used.  Setting this to zero will make phase 2 properly evaluate all tracts, but take a great deal longer.
     * @param phase3_save_intermediate - Phase 3 will write a set of files every this many minutes.  Phase 4 also uses this timer for the same purpose.
     * @param phase3_skip - If true, phase 3 will be skipped altogether.
     * @param phase3_time_limit - Limit phase 3 to this many minutes.
     * @param phase4_num_lags - As a rule of thumb, 5 - 10 lags is appropriate.
     * @param phase4_save_both_ends - Determine if the user should save at both ends of Phase 4.
     * @param phase4_skip - If true, phase 4 will be skipped altogether
     * @param phase4_time_limit - Limit phase 4 to this many minutes
     */
    public RunFile(
            String runName, 
            Date dateOfRun, 
            String criteria_file, 
            Boolean do_dump_number_archtypes, 
            Boolean do_dump_statistics, 
            Boolean do_write_all_hoh_fields, 
            Boolean do_write_all_pop_fields, 
            Integer final_rzn_num, 
            Integer first_rzn_num, 
            Long initial_seed, 
            Boolean only_one_region, 
            String output_dir, 
            Integer parallel_threads, 
            Double phase1_time_limit, 
            Double phase2_random_tract_prob, 
            Double phase2_tract_skip_prob_delta, 
            Double phase2_tract_skip_prob_init, 
            Double phase3_save_intermediate, 
            Boolean phase3_skip, 
            Double phase3_time_limit, 
            Integer phase4_num_lags, 
            Boolean phase4_save_both_ends, 
            Boolean phase4_skip, 
            Double phase4_time_limit) {
        this.runName = runName;
        this.dateOfRun = dateOfRun;
        this.criteria_file = criteria_file;
        this.do_dump_number_archtypes = do_dump_number_archtypes;
        this.do_dump_statistics = do_dump_statistics;
        this.do_write_all_hoh_fields = do_write_all_hoh_fields;
        this.do_write_all_pop_fields = do_write_all_pop_fields;
        this.final_rzn_num = final_rzn_num;
        this.first_rzn_num = first_rzn_num;
        this.initial_seed = initial_seed;
        this.only_one_region = only_one_region;
        this.output_dir = output_dir;
        this.parallel_threads = parallel_threads;
        this.phase1_time_limit = phase1_time_limit;
        this.phase2_random_tract_prob = phase2_random_tract_prob;
        this.phase2_tract_skip_prob_delta = phase2_tract_skip_prob_delta;
        this.phase2_tract_skip_prob_init = phase2_tract_skip_prob_init;
        this.phase3_save_intermediate = phase3_save_intermediate;
        this.phase3_skip = phase3_skip;
        this.phase3_time_limit = phase3_time_limit;
        this.phase4_num_lags = phase4_num_lags;
        this.phase4_save_both_ends = phase4_save_both_ends;
        this.phase4_skip = phase4_skip;
        this.phase4_time_limit = phase4_time_limit;
    }
    
    /**
     * Creates a new RunFile object from an existing file string
     * @param fullRunFile 
     */
    public RunFile(String fullRunFile) {
        String delims = "[,]+";
        String[] tokens = fullRunFile.split(delims);
        
        for (int i=0; i<tokens.length; i++){
            String innerDelims = "[=]+";
            String values[] = tokens[i].split(innerDelims);
            
            if((values.length == 2) && (values[1] != null) && !(values[1].equals("null"))){
                switch(values[0]){
                    case "#":
                        if(i == 0){
                            this.runName = values[1];
                        } else if(i == 1){
                            this.dateOfRun = new Date(); //today
                        }
                        break;
                    case "criteria_file":
                        this.criteria_file = values[1];
                        break;      
                    case "do_dump_number_archtypes":
                        this.do_dump_number_archtypes = Boolean.parseBoolean(values[1]);
                        break;
                    case "do_dump_statistics":
                        this.do_dump_statistics = Boolean.parseBoolean(values[1]);
                        break;
                    case "do_write_all_hoh_fields":
                        this.do_write_all_hoh_fields = Boolean.parseBoolean(values[1]);
                        break;
                    case "do_write_all_pop_fields":
                        this.do_write_all_pop_fields = Boolean.parseBoolean(values[1]);
                        break;
                    case "final_rzn_num":
                        this.final_rzn_num = Integer.parseInt(values[1]);
                        break;
                    case "first_rzn_num":
                        this.first_rzn_num = Integer.parseInt(values[1]);
                        break;
                    case "initial_seed":
                        this.initial_seed = Long.parseLong(values[1]);
                        break;
                    case "only_one_region":
                        this.only_one_region = Boolean.parseBoolean(values[1]);
                        break;
                    case "output_dir":
                        this.output_dir = values[1];
                        break;
                    case "parallel_threads":
                        this.parallel_threads = Integer.parseInt(values[1]);
                        break;
                    case "phase1_time_limit":
                        this.phase1_time_limit = Double.parseDouble(values[1]);
                        break;
                    case "phase2_random_tract_prob":
                        this.phase2_random_tract_prob = Double.parseDouble(values[1]);
                        break;
                    case "phase2_tract_skip_prob_delta":
                        this.phase2_tract_skip_prob_delta = Double.parseDouble(values[1]);
                        break;
                    case "phase2_tract_skip_prob_init":
                        this.phase2_tract_skip_prob_init = Double.parseDouble(values[1]);
                        break;
                    case "phase3_save_intermediate":
                        this.phase3_save_intermediate = Double.parseDouble(values[1]);
                        break;
                    case "phase3_skip":
                        this.phase3_skip = Boolean.parseBoolean(values[1]);
                        break;
                    case "phase3_time_limit":
                        this.phase3_time_limit = Double.parseDouble(values[1]);
                        break;
                    case "phase4_num_lags":        
                        this.phase4_num_lags = Integer.parseInt(values[1]);
                        break;
                    case "phase4_save_both_ends":
                        this.phase4_save_both_ends = Boolean.parseBoolean(values[1]);
                        break;
                    case "phase4_skip":
                        this.phase4_skip = Boolean.parseBoolean(values[1]);
                        break;
                    case "phase4_time_limit":
                        this.phase4_time_limit = Double.parseDouble(values[1]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Gets the name of the run
     * @return the text value provided by the user
     */
    public String getRunName() {
        return runName;
    }

    /**
     * Sets the name of the run to the value provided by the user
     * @param runName the text value of the run name
     */
    public void setRunName(String runName) {
        this.runName = runName;
    }

    /**
     * Gets the date of the run
     * @return the date and timestamp for the run
     */
    public Date getDateOfRun() {
        return dateOfRun;
    }

    /**
     * Sets the date of the run
     * @param dateOfRun the date/timestamp for the run
     */
    public void setDateOfRun(Date dateOfRun) {
        this.dateOfRun = dateOfRun;
    }

    /**
     * Gets the criteria file
     * @return the path and name of the main fitting criteria file
     */
    public String getCriteria_file() {
        return criteria_file;
    }

    /**
     * Sets the criteria file path, relative to the current directory
     * @param criteria_file the new file path, relative to the current directory
     */
    public void setCriteria_file(String criteria_file) {
        this.criteria_file = criteria_file;
    }

    /**
     * Get the true/false value if the log file will receive the results of phase 1
     * @return true if the log will receive the results of phase 1
     */
    public Boolean getDo_dump_number_archtypes() {
        return do_dump_number_archtypes;
    }

    /**
     * Sets the true/false value if the log file will receive the results of phase 1
     * @param do_dump_number_archtypes set to true if the log will receive the results of phase 1
     */
    public void setDo_dump_number_archtypes(Boolean do_dump_number_archtypes) {
        this.do_dump_number_archtypes = do_dump_number_archtypes;
    }

    /**
     * Gets the true/false value if the log file will receive detailed reports from the quality evaluation objects between phases
     * @return true if the log will receive detailed reports
     */
    public Boolean getDo_dump_statistics() {
        return do_dump_statistics;
    }

    /**
     * Sets the true/false value if the log file will receive detailed reports from the quality evaluation objects between phases
     * @param do_dump_statistics true if the log will receive detailed reports
     */
    public void setDo_dump_statistics(Boolean do_dump_statistics) {
        this.do_dump_statistics = do_dump_statistics;
    }

    /**
     * Gets the true/false value if each record in households.csv will contain a full copy of the archtype record
     * @return true if each record will contain a full copy, false if it will contain coordinates plus a reference to one of the archtypes
     */
    public Boolean getDo_write_all_hoh_fields() {
        return do_write_all_hoh_fields;
    }

    /**
     * Sets the true/false value if each record in households.csv will contain a full copy of the archtype record
     * @param do_write_all_hoh_fields true if each record will contain a full copy, false if it will contain coordinates plus a reference to one of the archtypes
     */
    public void setDo_write_all_hoh_fields(Boolean do_write_all_hoh_fields) {
        this.do_write_all_hoh_fields = do_write_all_hoh_fields;
    }

    /**
     * Gets the true/false value if each record in populations.csv will contain a full copy of the archtype record
     * @return true if each record will contain a full copy, false if it will contain coordinates plus a reference to one of the archtypes
     */
    public Boolean getDo_write_all_pop_fields() {
        return do_write_all_pop_fields;
    }

    /**
     * Sets the true/false value if each record in populations.csv will contain a full copy of the archtype record
     * @param do_write_all_pop_fields - true if each record will contain a full copy, false if it will contain coordinates plus a reference to one of the archtypes
     */
    public void setDo_write_all_pop_fields(Boolean do_write_all_pop_fields) {
        this.do_write_all_pop_fields = do_write_all_pop_fields;
    }

    /**
     * Get the final realization index to generate
     * @return the final realization index
     */
    public Integer getFinal_rzn_num() {
        return final_rzn_num;
    }

    /**
     * Sets the index of the final realization to generate
     * @param final_rzn_num - the index of the final realization
     */
    public void setFinal_rzn_num(Integer final_rzn_num) {
        this.final_rzn_num = final_rzn_num;
    }

    /**
     * Gets the index of the first realization to generate
     * @return the index of the first realization
     */
    public Integer getFirst_rzn_num() {
        return first_rzn_num;
    }

    /**
     * Sets the index of the first realization to generate
     * @param first_rzn_num the index of the first realization
     */
    public void setFirst_rzn_num(Integer first_rzn_num) {
        this.first_rzn_num = first_rzn_num;
    }

    /**
     * Gets the random seed that will guide the creation of all random numbers
     * @return the random seed to guide all random number creation
     */
    public Long getInitial_seed() {
        return initial_seed;
    }

    /**
     * Sets the random seed that will guide the creation of all random numbers
     * @param initial_seed the random seed to guide all random number creation
     */
    public void setInitial_seed(Long initial_seed) {
        this.initial_seed = initial_seed;
    }

    /**
     * Gets the value speed hack for debugging the criteria file
     * @return ture if only the first census tract should be used
     */
    public Boolean getOnly_one_region() {
        return only_one_region;
    }

    /**
     * Sets the value speed hack for debugging the criteria file
     * @param only_one_region true if only the first census tract should be used
     */
    public void setOnly_one_region(Boolean only_one_region) {
        this.only_one_region = only_one_region;
    }

    /**
     * Gets the target directory for all output files
     * @return the target directory
     */
    public String getOutput_dir() {
        return output_dir;
    }

    /**
     * Sets the target directory for all output files
     * @param output_dir - the target directory for all output files, relative to the current directory
     */
    public void setOutput_dir(String output_dir) {
        this.output_dir = output_dir;
    }

    /**
     * Gets the number of parallel threads to be run at once
     * @return the number of parallel threads
     */
    public Integer getParallel_threads() {
        return parallel_threads;
    }

    /**
     * Sets the number of parallel threads to be run at once
     * @param parallel_threads the number of parallel threads
     */
    public void setParallel_threads(Integer parallel_threads) {
        this.parallel_threads = parallel_threads;
    }

    /**
     * Gets the time limit for phase 1 in minutes
     * @return the number of minutes for completing phase 1
     */
    public Double getPhase1_time_limit() {
        return phase1_time_limit;
    }

    /**
     * Sets the time limit for pHase 1 to this many minutes
     * @param phase1_time_limit the number of minutes phase 1 is limited to
     */
    public void setPhase1_time_limit(Double phase1_time_limit) {
        this.phase1_time_limit = phase1_time_limit;
    }

    /**
     * Gets the fraction of households that will be placed randomly in phase 2
     * @return the fraction of households as a double
     */
    public Double getPhase2_random_tract_prob() {
        return phase2_random_tract_prob;
    }

    /**
     * Sets the fraction of households that will be placed randomly
     * @param phase2_random_tract_prob - Setting to 0 will greatly improve the output of phase 2, but will take a great deal longer
     */
    public void setPhase2_random_tract_prob(Double phase2_random_tract_prob) {
        this.phase2_random_tract_prob = phase2_random_tract_prob;
    }

    /**
     * Gets the reduction amount that the phase 2 skip prob init tract fraction will be reduced by
     * @return the reduction amount
     */
    public Double getPhase2_tract_skip_prob_delta() {
        return phase2_tract_skip_prob_delta;
    }

    /**
     * Sets the reduction amount that the phase 2 skip prob init tract fraction will be reduced by
     * @param phase2_tract_skip_prob_delta the reduction amount as a double
     */
    public void setPhase2_tract_skip_prob_delta(Double phase2_tract_skip_prob_delta) {
        this.phase2_tract_skip_prob_delta = phase2_tract_skip_prob_delta;
    }

    /**
     * Gets the fraction of tracts that will be ignored when phase 2 tries to place a household into a tract
     * @return the fraction of tracts that will be ignored as a double
     */
    public Double getPhase2_tract_skip_prob_init() {
        return phase2_tract_skip_prob_init;
    }

    /**
     * Sets the fraction of tracts that will be ignored when phase 2 tries to place a household into a tract
     * @param phase2_tract_skip_prob_init the fraction of tracts that will be ignored
     */
    public void setPhase2_tract_skip_prob_init(Double phase2_tract_skip_prob_init) {
        this.phase2_tract_skip_prob_init = phase2_tract_skip_prob_init;
    }

    /**
     * Gets the number of minutes of how often Phase 3 and 4 will write a set of files
     * @return the number of minutes between saves
     */
    public Double getPhase3_save_intermediate() {
        return phase3_save_intermediate;
    }

    /**
     * Sets the number of minutes of how often Phase 3 and 4 will write a set of files
     * @param phase3_save_intermediate the number of minutes between saves
     */
    public void setPhase3_save_intermediate(Double phase3_save_intermediate) {
        this.phase3_save_intermediate = phase3_save_intermediate;
    }

    /**
     * Gets the true/false value indicating if phase 3 will be skipped altogether
     * @return true if phase 3 should be skipped
     */
    public Boolean getPhase3_skip() {
        return phase3_skip;
    }

    /**
     * Sets the true/false value indicating if phase 3 will be skipped altogether
     * @param phase3_skip true if phase 3 should be skipped
     */
    public void setPhase3_skip(Boolean phase3_skip) {
        this.phase3_skip = phase3_skip;
    }

    /**
     * Gets the number of minutes allowed for phase 3
     * @return the number of minutes allowed for phase 3 as a double
     */
    public Double getPhase3_time_limit() {
        return phase3_time_limit;
    }

    /**
     * Sets the number of minutes allowed for phase 3
     * @param phase3_time_limit - the number of minutes allowed for phase 3
     */
    public void setPhase3_time_limit(Double phase3_time_limit) {
        this.phase3_time_limit = phase3_time_limit;
    }

    /**
     * Gets the number of lags in Phase 4
     * @return the number of lags
     */
    public Integer getPhase4_num_lags() {
        return phase4_num_lags;
    }

    /**
     * Sets the number of lags in Phase 4
     * @param phase4_num_lags - As a rule of thumb, 5 - 10 lags is appropriate.
     */
    public void setPhase4_num_lags(Integer phase4_num_lags) {
        this.phase4_num_lags = phase4_num_lags;
    }

    /**
     * Gets the true/false value that determines if the user should save at both ends of Phase 4.
     * @return true if phase 4 should be saved at both ends
     */
    public Boolean getPhase4_save_both_ends() {
        return phase4_save_both_ends;
    }

    /**
     * Sets the true/false value that determines if the user should save at both ends of Phase 4.
     * @param phase4_save_both_ends - true if phase 4 should be saved at both ends
     */
    public void setPhase4_save_both_ends(Boolean phase4_save_both_ends) {
        this.phase4_save_both_ends = phase4_save_both_ends;
    }

    /**
     * Gets the true/false value if phase 4 will be skipped altogether
     * @return - true if phase 4 will be skipped
     */
    public Boolean getPhase4_skip() {
        return phase4_skip;
    }

    /**
     * Sets the true/false value if phase 4 will be skipped altogether
     * @param phase4_skip - true if phase 4 will be skipped
     */
    public void setPhase4_skip(Boolean phase4_skip) {
        this.phase4_skip = phase4_skip;
    }

    /**
     * Gets the number of minutes phase 4 is limited to
     * @return - the number of minutes as a double
     */
    public Double getPhase4_time_limit() {
        return phase4_time_limit;
    }

    /**
     * Sets the number of minutes phase 4 is limited to
     * @param phase4_time_limit the number of minutes as a double
     */
    public void setPhase4_time_limit(Double phase4_time_limit) {
        this.phase4_time_limit = phase4_time_limit;
    }

    /**
     * Returns the text string formatted for use in the last-run.properties file
     * @return A string of all values
     */
    @Override
    public String toString() {
        String output = "";
        
        output = String.format(
                                "#%s\n" +
                                "#%s\n" +
                                "criteria_file=%s\n" +
                                "do_dump_number_archtypes=%s\n" +
                                "do_dump_statistics=%s\n" +
                                "do_write_all_hoh_fields=%s\n" +
                                "do_write_all_pop_fields=%s\n" +
                                "final_rzn_num=%s\n" +
                                "first_rzn_num=%s\n" +
                                "initial_seed=%s\n" +
                                "only_one_region=%s\n" +
                                "output_dir=%s\n" +
                                "parallel_threads=%s\n" +
                                "phase1_time_limit=%s\n" +
                                "phase2_random_tract_prob=%s\n" +
                                "phase2_tract_skip_prob_delta=%s\n" +
                                "phase2_tract_skip_prob_init=%s\n" +
                                "phase3_save_intermediate=%s\n" +
                                "phase3_skip=%s\n" +
                                "phase3_time_limit=%s\n" +
                                "phase4_num_lags=%s\n" +
                                "phase4_save_both_ends=%s\n" +
                                "phase4_skip=%s\n" +
                                "phase4_time_limit=%s",
                                this.runName,
                                this.dateOfRun,
                                this.criteria_file,
                                this.do_dump_number_archtypes,
                                this.do_dump_statistics,
                                this.do_write_all_hoh_fields,
                                this.do_write_all_pop_fields,
                                this.final_rzn_num,
                                this.first_rzn_num,
                                this.initial_seed,
                                this.only_one_region,
                                this.output_dir,
                                this.parallel_threads,
                                this.phase1_time_limit,
                                this.phase2_random_tract_prob,
                                this.phase2_tract_skip_prob_delta,
                                this.phase2_tract_skip_prob_init,
                                this.phase3_save_intermediate,
                                this.phase3_skip,
                                this.phase3_time_limit,
                                this.phase4_num_lags,
                                this.phase4_save_both_ends,
                                this.phase4_skip,
                                this.phase4_time_limit
        );
        return output;
    }
}
