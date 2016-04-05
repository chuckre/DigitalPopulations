package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.SortedProperties;

// Dev Notes:
// Note all values must be stored as Strings, since that's all that Properties.load can return.
// If a getX() helper returns a primitive, you can use C.parseC() to convert the value,
// but be sure there's a default installed in setDefaults().
// If a getX() returns an object, then create a new getC() to parse the value and work
// around nulls.

/**
 * Wrap and manage all the configs, settings, and options required by
 * {@link ConflatePumsQueryWithTracts}. Save this to a file creates a record of
 * precisely how the most recent run was configured.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class Params {
    public static final String WRITE_ALL_HOH_FIELDS = "do_write_all_hoh_fields";
    public static final String WRITE_ALL_POP_FIELDS = "do_write_all_pop_fields";
       
    protected SortedProperties params;

    public Params() {
        params = new SortedProperties();
        mergeDefaults();
    }

    /**
     * Save a key/value pair to the params file. Note that although we accept
     * any object, only strings will be written to file. So the corresponding
     * get() will need to perform the appropriate conversion.
     * 
     * @param name key to update in file
     * @param value.toString() will be written to key
     */
    public void set(String name, Object value) {
        if(value == null)
            params.remove(name);
        else
            params.put(name, value.toString());
    }

    /**
     * Get the value associated with a key.
     * 
     * @param name key to retrieve
     * @return value stored for given key, or null if undefined
     * 
     * @throws ClassCastException
     *             someone snuck a non-String into our Properties object. Bad
     *             news! Use set(name, null) to take it out.
     */
    public String get(String name) {
        return (String) params.get(name);
    }

    /**
     * Get the value associated with a key.
     * 
     * @param name key to retrieve
     * @param defalt value to insert if key is undefined
     * 
     * @return current value of key, or defalt if undefined
     * 
     * @throws ClassCastException
     *             someone snuck a non-String into our Properties object. Bad
     *             news! Use set(name, null) to take it out.
     */
    public String get(String name, String defalt) {
        String s = (String) params.get(name);
        if(s == null) {
            s = defalt;
            params.put(name, defalt);
        }
        return s;
    }

    /**
     * Get value, converted to Boolean.
     * 
     * @param name key to retrieve
     * @return value converted to Boolean, or null if undefined
     */
    public Boolean getBoolean(String name) {
        String s = get(name);
        if(s == null)
            return null;
        else
            return new Boolean(s);
    }
    
    /**
     * Get value, converted to Boolean.
     * 
     * @param name key to retrieve
     * @param defalt value to insert if key is undefined
     * 
     * @return current value of key as a boolean, or defalt if undefined
     */
    public boolean getBoolean(String name, boolean defalt) {
        String s = get(name);
        if(s == null)
            set(name, defalt);
        else
            defalt = Boolean.parseBoolean(s);
        return defalt;
    }
    
    /**
     * Get value, converted to Long.
     * 
     * @param name key to retrieve
     * @return value converted to Long, or null if undefined
     */
    public Long getLong(String name) {
        String s = get(name);
        if(s == null)
            return null;
        else
            return new Long(s);
    }

    /**
     * Get value, converted to long.
     * 
     * @param name key to retrieve
     * @param defalt value to insert if key is undefined
     * 
     * @return current value of key as a long, or defalt if undefined
     */
    public long getLong(String name, long defalt) {
        String s = get(name);
        if(s == null)
            set(name, defalt);
        else
            defalt = Long.parseLong(s);
        return defalt;
    }
    
    /**
     * Get value, converted to Double.
     * 
     * @param name key to retrieve
     * @return value converted to Double, or null if undefined
     */
    public Double getDouble(String name) {
        String s = get(name);
        if(s == null)
            return null;
        else
            return Double.parseDouble(s);
    }

    /**
     * Get value, converted to double.
     * 
     * @param name key to retrieve
     * @param defalt value to insert if key is undefined
     * 
     * @return current value of key as a double, or defalt if undefined
     */
    public double getDouble(String name, double defalt) {
        String s = get(name);
        if(s == null)
            set(name, defalt);
        else
            defalt = Double.parseDouble(s);
        return defalt;
    }

    /**
     * Get value, converted to File.
     * 
     * @param name key to retrieve
     * @return value converted to File, or null if undefined
     */
    public File getFile(String name) {
        String s = get(name);
        if(s == null)
            return null;
        else
            return new File(s);
    }

    
    
    /**
     * Copy all values from given object into this one. If any key is already
     * defined, it will be overwritten.
     * 
     * @param otherProps values to copy
     */
    public void merge(Properties otherProps) {
        params.putAll(otherProps);
    }

    /**
     * Copy all values from given object into this one. If any key is already
     * defined, it will be overwritten.
     * 
     * @param otherParams values to copy
     */
    public void merge(Params otherParams) {
        params.putAll(otherParams.params);
    }



    /**
     * Register the main fitting criteria file that the application will "run".
     * 
     * @param mainFile
     */
    public void setCriteriaFile(File mainFile) {
        set("criteria_file", mainFile);
    }

    public File getCriteriaFile() {
        return getFile("criteria_file");
    }

    /**
     * If true, phase 1 will print the computed quantities of archtypes when it
     * finishes.
     */
    public void setDumpNumberArchtypes(boolean doDump) {
        set("do_dump_number_archtypes", doDump);
    }
    
    public boolean getDumpNumberArchtypes() {
        return Boolean.parseBoolean(get("do_dump_number_archtypes"));
    }

    /**
     * If true, prints a full dump of the statistic objects' current values for every region.
     */
    public void setDumpStatistics(boolean doDump) {
        set("do_dump_statistics", doDump);
    }
    
    public boolean getDumpStatistics() {
        return Boolean.parseBoolean(get("do_dump_statistics"));
    }

    /**
     * (Speed hack) Command algorithm to use only the first census tract.
     * This is a hack to only be used for exercising the code; calculations may
     * or may not be correct.
     */
    public void setOnlyOneRegion(boolean oneOnly) {
        set("only_one_region", oneOnly);
    }
    
    public boolean getOnlyOneRegion() {
        return Boolean.parseBoolean(get("only_one_region"));
    }
    
    /**
     * Register the directory that will receive all output files.
     * 
     * @param outputDir
     */
    public void setOutputDir(File outputDir) {
        set("output_dir", outputDir);
    }
    
    public File getOutputDir() {
        return getFile("output_dir");
    }
    
    /**
     * (Speed hack) Phase 1 will be aborted if it runs for more than this
     * many minutes.  Set to zero to disable.
     */
    public void setPhase1TimeLimit(double minutes) {
        set("phase1_time_limit", minutes);
    }
    
    public double getPhase1TimeLimit() {
        return Double.parseDouble(get("phase1_time_limit"));
    }

    /**
     * (Speed hack) Probability with which phase 2 will pick a tract at random
     * rather than evaluating quality statistics for all tracts. If chance is
     * met, a new household will be placed into a random tract. If not, phase 2
     * will hunt for the best tract for the household.
     * <P>
     * Set <=0 to disable (i.e. always hunt for best tract).
     */
    public void setPhase2RandomTractProb(double skip) {
        set("phase2_random_tract_prob", skip);
    }
    
    public double getPhase2RandomTractProb() {
        return Double.parseDouble(get("phase2_random_tract_prob"));
    }

    /**
     * (Speed hack) Probability with which phase 2 will skip tracts while
     * evaluating quality statistics. If P2_RANDOM_TRACT_PROB chance is met,
     * this variable is irrelevent. If phase 2 hunts for tracts, it will
     * randomly skip tracts with this probability, and only evaluate the ones
     * that remain.
     * <P>
     * Set <=0 to disable (i.e. always evaluate all tracts).
     */
    public void setPhase2TractSkipProbInit(double skip) {
        set("phase2_tract_skip_prob_init", skip);
    }

    public double getPhase2TractSkipProbInit() {
        return Double.parseDouble(get("phase2_tract_skip_prob_init"));
    }

    /**
     * (Speed hack) If phase 2 decided to scan tracts for a best fit, but
     * P2_TRACT_SKIP_PROB_INIT caused it to skip every tract available, then the
     * probability will be reduced by this amount before trying again. Phase 2
     * will repeat reducing probability and rescanning until at least one tract
     * has been tested.
     * <P>
     * Ignored if P2_TRACT_SKIP_PROB_INIT <= 0.
     */
    public void setPhase2TractSkipProbDelta(double skipFactor) {
        if(skipFactor < 0.01)
            throw new DataException("skip factor must be at least 0.01 (1%)");
        set("phase2_tract_skip_prob_delta", skipFactor);
    }
    
    public double getPhase2TractSkipProbDelta() {
        return Double.parseDouble(get("phase2_tract_skip_prob_delta"));
    }

    /**
     * How often to save intermediate results, in minutes. A complete set of
     * output files will be created, though the households will not be fully
     * optimized.
     */
    public void setPhase3SaveIntermediate(double minutes) {
        set("phase3_save_intermediate", minutes);
    }
    
    public double getPhase3SaveIntermediate() {
        return Double.parseDouble(get("phase3_save_intermediate"));
    }

    /**
     * Enable this to completely skip phase 3.
     * 
     * @param doSkip whether to skip phase 3
     */
    public void setPhase3Skip(boolean doSkip) {
        set("phase3_skip", doSkip);
    }
    
    public boolean getPhase3Skip() {
        return Boolean.parseBoolean(get("phase3_skip"));
    }

    /**
     * (Speed hack) Phase 3 will be aborted if it runs for more than this
     * many minutes.  Set <= 0 to disable.
     */
    public void setPhase3TimeLimit(double minutes) {
        set("phase3_time_limit", minutes);
    }
    
    public double getPhase3TimeLimit() {
        return Double.parseDouble(get("phase3_time_limit"));
    }

    /**
     * Enable this to completely skip phase 4.
     * 
     * @param doSkip whether to skip phase 4
     */
    public void setPhase4Skip(boolean doSkip) {
        set("phase4_skip", doSkip);
    }
    
    public boolean getPhase4Skip() {
        return Boolean.parseBoolean(get("phase4_skip"));
    }

    /**
     * (Speed hack) Phase 4 will be aborted if it runs for more than this
     * many minutes.  Set to null to disable.
     */
    public void setPhase4TimeLimit(double minutes) {
        set("phase4_time_limit", minutes);
    }
    
    public double getPhase4TimeLimit() {
        return Double.parseDouble(get("phase4_time_limit"));
    }

    /**
     * If true, writeHouseholdRealizations() will append a full copy of the
     * household data to each output record.
     */
    public void setWriteAllHohFields(boolean doWrite) {
        set(WRITE_ALL_HOH_FIELDS, doWrite);
    }
    
    public boolean getWriteAllHohFields() {
        return Boolean.parseBoolean(get(WRITE_ALL_HOH_FIELDS));
    }
    
    /**
     * If true, writePopulationRealizations() will append a full copy of the
     * population data to each output record.
     */
    public void setWriteAllPopFields(boolean doWrite) {
        set(WRITE_ALL_POP_FIELDS, doWrite);
    }

    public boolean getWriteAllPopFields() {
        return Boolean.parseBoolean(get(WRITE_ALL_POP_FIELDS));
    }

    /**
     * Random seed that will guide the creation of all random numbers. Running
     * the program twice with the same seed will result in the exact same output.
     * <P>
     * Set to null to make program generate a random seed every run.
     * Output files will be different every time.
     * 
     * @param seed
     */
    public void setRandomSeed(Long seed) {
        set("initial_seed", seed);
    }
    
    public Long getRandomSeed() {
        return getLong("initial_seed");
    }

    /**
     * Index of first realization to generate. All realizations between 'first'
     * and 'last' will be run and saved. In other words,
     * {@link ConflatePumsQueryWithTracts} will run repeatedly, once for each
     * realization, but with different random seeds each time.
     * 
     * @param firstRzn
     */
    public void setFirstRzn(int firstRzn) {
        set("first_rzn_num", firstRzn);
    }

    public int getFirstRzn() {
        return Integer.parseInt(get("first_rzn_num"));
    }

    /**
     * Index of final realization to generate. All realizations between 'first'
     * and 'last' will be run and saved. In other words,
     * {@link ConflatePumsQueryWithTracts} will run repeatedly, once for each
     * realization, but with different random seeds each time.
     * 
     * @param lastRzn
     */
    public void setLastRzn(int lastRzn) {
        set("final_rzn_num", lastRzn);
    }

    public int getLastRzn() {
        return Integer.parseInt(get("final_rzn_num"));
    }

    /**
     * Number of threads of processing to run simultaneously if more than one
     * realization is requested. Only useful if computer has multiple
     * processors.
     * 
     * @param threads absolute number of threads to use
     */
    public void setThreads(int threads) {
        if(threads < 1)
            throw new IllegalArgumentException("threads must be >= 1");
        set("parallel_threads", threads);
    }
    
    public int getThreads() {
        return (int) getLong("parallel_threads", 1);
    }
    
    
    /**
     * Overwrite properties with basic defaults. Properties we don't have
     * defaults for will not be touched. The result will be a <i>fast</i> run,
     * though not necessarily a <i>good</i> run.
     */
    public void mergeDefaults() {
        setOnlyOneRegion(false);
        setDumpNumberArchtypes(true);
        setDumpStatistics(true);

        setWriteAllHohFields(true);
        setWriteAllPopFields(true);

        setOutputDir(new File("."));
        
        setFirstRzn(1);
        setLastRzn(1);
        
        setPhase1TimeLimit(7.0);
        
        setPhase2RandomTractProb(0.995);
        setPhase2TractSkipProbInit(0.995);
        setPhase2TractSkipProbDelta(0.05);
        
        setPhase3SaveIntermediate(60.0);
        setPhase3Skip(false);
        setPhase3TimeLimit(10.0);
        
        setPhase4Skip(true);
        setPhase4TimeLimit(5.0);
        
        setThreads(1);
    }

    /**
     * Load properties from file, and merge into current set. Properties that
     * are already defined will be overwritten.
     * 
     * @param propertiesFile
     *            path and name of file, relative to configured output dir
     */
    public void merge(String propertiesFile) throws IOException {
        File d = getOutputDir();
        merge(FileUtil.resolve(d, propertiesFile));
    }

    /**
     * Load properties from file, and merge into current set. Properties that
     * are already defined will be overwritten.
     * 
     * @param propertiesFile
     *            path and name of file
     */
    public void merge(File propertiesFile) throws IOException {
        Reader in = new FileReader(propertiesFile);
        try {
            params.load(in);
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Save properties to file.
     * <P>
     * File will be saved to configured output dir.
     * 
     * @param propertiesFile
     */
    public void save(String propertiesFile) throws IOException {
        File d = getOutputDir();
        save(FileUtil.resolve(d, propertiesFile));
    }
    
    /**
     * Save properties to file.
     * 
     * @param propertiesFile
     */
    public void save(File propertiesFile) throws IOException {
        Writer out = new FileWriter(propertiesFile);
        try {
            String comments = String.format("Last run of %s",
                ConflatePumsQueryWithTracts.class.getSimpleName());
            params.store(out, comments);
        }
        finally {
            out.close();
        }
    }

    /**
     * Ensure current options are reasonable.
     * 
     * @throws IOException if the file params have problems
     * @throws DataException if other params have problems
     */
    public void validate() throws IOException {
        File mainFile = getCriteriaFile();
        if(mainFile == null)
            throw new IOException("No fitting criteria file was specified.");
        if(!mainFile.exists())
            throw new IOException(mainFile+" does not exist");
        if(!mainFile.isFile())
            throw new IOException(mainFile+" is not a file");
        
        File outputDir = getOutputDir();
//No need for this; we'll create it when needed.
//        if(!outputDir.exists())
//            throw new IOException("Cannot find or create "+outputDir);
        if(outputDir.isFile())
            throw new IOException("Output dir \""+outputDir+"\" exists, but is not a directory.");
        
        int firstReal = getFirstRzn();
        if(firstReal < 1)
            throw new DataException("First realization number must be >= 1.");
        
        int lastReal = getLastRzn();
        if(lastReal  < firstReal)
            throw new DataException("Final realization number must be >= first.");
    }
}
