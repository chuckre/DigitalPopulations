/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.Date;

/**
 *
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

    public RunFile() {
    }

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

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public Date getDateOfRun() {
        return dateOfRun;
    }

    public void setDateOfRun(Date dateOfRun) {
        this.dateOfRun = dateOfRun;
    }

    public String getCriteria_file() {
        return criteria_file;
    }

    public void setCriteria_file(String criteria_file) {
        this.criteria_file = criteria_file;
    }

    public Boolean getDo_dump_number_archtypes() {
        return do_dump_number_archtypes;
    }

    public void setDo_dump_number_archtypes(Boolean do_dump_number_archtypes) {
        this.do_dump_number_archtypes = do_dump_number_archtypes;
    }

    public Boolean getDo_dump_statistics() {
        return do_dump_statistics;
    }

    public void setDo_dump_statistics(Boolean do_dump_statistics) {
        this.do_dump_statistics = do_dump_statistics;
    }

    public Boolean getDo_write_all_hoh_fields() {
        return do_write_all_hoh_fields;
    }

    public void setDo_write_all_hoh_fields(Boolean do_write_all_hoh_fields) {
        this.do_write_all_hoh_fields = do_write_all_hoh_fields;
    }

    public Boolean getDo_write_all_pop_fields() {
        return do_write_all_pop_fields;
    }

    public void setDo_write_all_pop_fields(Boolean do_write_all_pop_fields) {
        this.do_write_all_pop_fields = do_write_all_pop_fields;
    }

    public Integer getFinal_rzn_num() {
        return final_rzn_num;
    }

    public void setFinal_rzn_num(Integer final_rzn_num) {
        this.final_rzn_num = final_rzn_num;
    }

    public Integer getFirst_rzn_num() {
        return first_rzn_num;
    }

    public void setFirst_rzn_num(Integer first_rzn_num) {
        this.first_rzn_num = first_rzn_num;
    }

    public Long getInitial_seed() {
        return initial_seed;
    }

    public void setInitial_seed(Long initial_seed) {
        this.initial_seed = initial_seed;
    }

    public Boolean getOnly_one_region() {
        return only_one_region;
    }

    public void setOnly_one_region(Boolean only_one_region) {
        this.only_one_region = only_one_region;
    }

    public String getOutput_dir() {
        return output_dir;
    }

    public void setOutput_dir(String output_dir) {
        this.output_dir = output_dir;
    }

    public Integer getParallel_threads() {
        return parallel_threads;
    }

    public void setParallel_threads(Integer parallel_threads) {
        this.parallel_threads = parallel_threads;
    }

    public Double getPhase1_time_limit() {
        return phase1_time_limit;
    }

    public void setPhase1_time_limit(Double phase1_time_limit) {
        this.phase1_time_limit = phase1_time_limit;
    }

    public Double getPhase2_random_tract_prob() {
        return phase2_random_tract_prob;
    }

    public void setPhase2_random_tract_prob(Double phase2_random_tract_prob) {
        this.phase2_random_tract_prob = phase2_random_tract_prob;
    }

    public Double getPhase2_tract_skip_prob_delta() {
        return phase2_tract_skip_prob_delta;
    }

    public void setPhase2_tract_skip_prob_delta(Double phase2_tract_skip_prob_delta) {
        this.phase2_tract_skip_prob_delta = phase2_tract_skip_prob_delta;
    }

    public Double getPhase2_tract_skip_prob_init() {
        return phase2_tract_skip_prob_init;
    }

    public void setPhase2_tract_skip_prob_init(Double phase2_tract_skip_prob_init) {
        this.phase2_tract_skip_prob_init = phase2_tract_skip_prob_init;
    }

    public Double getPhase3_save_intermediate() {
        return phase3_save_intermediate;
    }

    public void setPhase3_save_intermediate(Double phase3_save_intermediate) {
        this.phase3_save_intermediate = phase3_save_intermediate;
    }

    public Boolean getPhase3_skip() {
        return phase3_skip;
    }

    public void setPhase3_skip(Boolean phase3_skip) {
        this.phase3_skip = phase3_skip;
    }

    public Double getPhase3_time_limit() {
        return phase3_time_limit;
    }

    public void setPhase3_time_limit(Double phase3_time_limit) {
        this.phase3_time_limit = phase3_time_limit;
    }

    public Integer getPhase4_num_lags() {
        return phase4_num_lags;
    }

    public void setPhase4_num_lags(Integer phase4_num_lags) {
        this.phase4_num_lags = phase4_num_lags;
    }

    public Boolean getPhase4_save_both_ends() {
        return phase4_save_both_ends;
    }

    public void setPhase4_save_both_ends(Boolean phase4_save_both_ends) {
        this.phase4_save_both_ends = phase4_save_both_ends;
    }

    public Boolean getPhase4_skip() {
        return phase4_skip;
    }

    public void setPhase4_skip(Boolean phase4_skip) {
        this.phase4_skip = phase4_skip;
    }

    public Double getPhase4_time_limit() {
        return phase4_time_limit;
    }

    public void setPhase4_time_limit(Double phase4_time_limit) {
        this.phase4_time_limit = phase4_time_limit;
    }

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
