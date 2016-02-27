package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.ExpansionFactor;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.MyRandom;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Compute the "expansion factor" for household archtypes. "Expansion factor" is
 * defined as the number of clones we will create of the records in the
 * households table.
 * 
 * @see {@link Phase_ComputeMultiplier}
 */
public class Phase_ExpansionFactor {
    //
    // DEV NOTE:
    //
    // Here be dragons! We want to ensure only one thread runs
    // computeMultiplier(), and all other threads, processes, and remote systems
    // wait for the result. There's no way to do this, so we just make a modest
    // effort:
    //
    //  * We use lock() to ensures different VMs on the same machine cooperate
    //    nicely, but it may or may not work when the file system is remote.
    //  * We use synchronized(cacheFileLock) to ensure threads in the same VM
    //    cooperate, as lock() is documented to NOT work with threads (it only
    //    holds a VM-wide lock.)
    //
    
    protected static Logger log = Logger.getLogger(Phase_InitialPlacement.class.getPackage().getName());

    // Lock for threads in this VM.
    // 
    // The problem with static members is that if custom classloaders cause two
    // instance of this class to be loaded, there will be two independent locks
    // in the same VM. Using an interned string should fix that.
    private static Object cacheFileLock = Phase_ExpansionFactor.class.getName().intern();

    protected Solution soln;
    protected ExpansionFactor phaseData;
    protected RegionData primaryRegion;
    protected int peopleInArchTypes;
    protected Date configTime;

    protected Date householdArchTypesTime;
    protected ArrayList<Integer> regionList;
    protected int naiveExpansion = -1;
    
    protected int MINIMUM_HOUSEHOLD_EXPANSION = 2;
    
    protected Params params = new Params();
    
    protected long initialSeed;
    protected Random statFactorRnd;
    protected Random userFactorRnd;

    
    /**
     * Build standard instance. You will generally want to call setParams() and
     * setRandomSource(), then go() to run process.
     * 
     * @param soln
     *            set of archtypes for which we will create realizations
     * @param phaseData
     *            user's preferences for this phase
     * @param primaryRegion
     *            main region map and its table
     * @param peopleInArchTypes
     *            total number of people in all the households in
     *            householdArchTypes. This will be a percentage of the actual
     *            number of people in the region.
     * @param configTime
     *            time at which config files were created. If there are several
     *            input files, then this should be the most recent (latest)
     *            time.
     */
    public Phase_ExpansionFactor(
            Solution soln,
            ExpansionFactor phaseData,
            RegionData primaryRegion,
            int peopleInArchTypes,
            Date configTime) {
        this.soln = soln;
        this.phaseData = phaseData;
        this.primaryRegion = primaryRegion;
        this.peopleInArchTypes = peopleInArchTypes;
        this.configTime = configTime;

        this.regionList = new ArrayList<Integer>(primaryRegion.map.makeInventory());
        Collections.sort(regionList);
        
        // Compute data file time stamp.
        long htime = soln.householdSchema.getFile().lastModified();
        long ptime = 0;
        if(soln.populationSchema != null)
            ptime = soln.populationSchema.getFile().lastModified();
        householdArchTypesTime = new Date(htime>ptime ? htime : ptime);
        
        setRandomSeed(new Random().nextLong());
    }
    
    /**
     * Install run-time configuration.
     * 
     * @param params current set of run-time parameters
     */
    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * Change our source of random numbers. Note we can't accept a custom Random
     * here as we need access to the original seed to determine whether our
     * cache file is valid.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSeed(long seed) {
        initialSeed = seed;
        statFactorRnd = new Random(seed);
        // Peel off a RNG so makeStatFactors() doesn't change based on whether
        // we run makeUserFactors().
        userFactorRnd = new Random(statFactorRnd.nextLong());
    }
    
//    public void setRandomSource(Random source) {
//        random = source;
//        // Peel off a RNG so makeStatFactors() doesn't change based on whether
//        // we run makeUserFactors().
//        userFactorRnd = new Random(random.nextLong());
//    }

    /**
     * @return "naive" multiplier, as if every archtype would receive the same
     *         number of clones
     */
    public int getNaiveFactor() {
        return naiveExpansion;
    }
    
    /**
     * Perform the process as currently configured.
     * 
     * @return expansion factors as an array of ints
     * 
     * @throws IOException on any error writing output files
     */
    public int[] go() {
        int[] numRealizations2Make;
        
        LogUtil.cr(log);
        LogUtil.progress(log, "Starting phase 1: Computing expansion factors for household archtypes.");

        LogUtil.cr(log);
        LogUtil.result(log, "Ratio of households Agg / Pums: %.2f    Ratio of people Agg / Pums: %.2f",
            primaryRegion.aggregateHouseholds / (float)soln.householdArchTypes.length,
            primaryRegion.aggregatePopulation / (float)peopleInArchTypes);
        
        // Make naive assumption that we can just clone everything to bring the
        // population up to snuff.  The code below will find better numbers.
        naiveExpansion  = (int) Math.round(primaryRegion.aggregatePopulation / (float)peopleInArchTypes);
        if(naiveExpansion < 1)
            naiveExpansion = 1;

        if(params.getDumpNumberArchtypes())
            LogUtil.result(log, "Initial assumption: %d realizations per archtype", naiveExpansion);

        // Init default phase 1 specs.
        double trust = 100.0;

        // Fetch user's phase 1 specs.
        if(phaseData != null) {
            // Collect and validate inputs.
            trust = phaseData.trust;
        }

        // trust is only [0,100], so <=0 and >=100 is just being paranoid.
        if(trust <= 0) {
            // Use only user factors.
            double[] userFactors = makeUserFactors();
            numRealizations2Make = new int[soln.householdArchTypes.length];
            for(int i=0; i < soln.householdArchTypes.length; i++)
                numRealizations2Make[i] = (int) Math.round(userFactors[i]);
        }
        else if(trust >= 100) {
            // Use only computed factors.
            numRealizations2Make = makeStatFactors();
        }
        else {
            // Combine the two sets of values.
            int[] statFactors = makeStatFactors();
            double[] userFactors = makeUserFactors();
            numRealizations2Make = new int[soln.householdArchTypes.length];
            
            for(int i=0; i < soln.householdArchTypes.length; i++) {
                numRealizations2Make[i] = (int) Math.round(trust/100.0 * statFactors[i]
                                                     + (1-trust/100.0) * userFactors[i]);
            }
        }
        
        // Dump final result.
        if(params.getDumpNumberArchtypes()) {
            LogUtil.cr(log);
            LogUtil.result(log, dumpNumberArchtypes(numRealizations2Make));
        }
        
        return numRealizations2Make;
    }


    /**
     * Compute multipliers from user-specified random criteria.
     * 
     * @return array of random multipliers
     */
    protected double[] makeUserFactors() {
        double[] userExpansionFactors = new double[soln.householdArchTypes.length];

        String factorCol = null;
        int    factorIdx = -1;
        double factorVal = -1;
        String stdDevCol = null;
        int    stdDevIdx = -1;
        double stdDevVal = -1;

        // Fetch user's phase 1 specs.
        if(phaseData != null) {
            factorCol = phaseData.factorCol;
            if(ObjectUtil.isBlank(factorCol))
                factorCol = null;
            stdDevCol = phaseData.stdDevCol;
            if(ObjectUtil.isBlank(stdDevCol))
                stdDevCol = null;

            // Parse factorCol.
            try {
                if(factorCol != null)
                    factorVal = Double.parseDouble(factorCol);
            } catch (NumberFormatException e) {
                factorIdx = soln.householdSchema.findColumn(factorCol);
            }

            // Parse stdDevCol.
            try {
                if(stdDevCol != null)
                    stdDevVal = Double.parseDouble(stdDevCol);
            } catch (NumberFormatException e) {
                stdDevIdx = soln.householdSchema.findColumn(stdDevCol);
            }
        }
        
        // If no factorCol, use naiveMult.
        if(factorCol == null)
            Arrays.fill(userExpansionFactors, naiveExpansion);
        else {
            // If factorCol is a number, use that.
            if(factorIdx < 0)
                Arrays.fill(userExpansionFactors, factorVal);
            else {
                // Else parse column from archtypes.
                for(int i=0; i < soln.householdArchTypes.length; i++) {
                    userExpansionFactors[i] = soln.householdArchTypes[i].getAttributeValue(factorIdx);
                    
                    if(userExpansionFactors[i] < 0)
                        throw new DataException(
                            String.format("Expansion factor cannot be less than zero:"
                                +" Household archtypes file \"%s\", row %d, column %s.",
                                soln.householdSchema.getFile(), i, stdDevCol));
                }
            }
        }
        
        // If stdDevCol provided, randomize userExpansionFactors.
        if(stdDevCol != null) {
            double[] stddev = new double[userExpansionFactors.length];
            
            // If stdDevCol is a number, use that.
            if(stdDevIdx < 0)
                Arrays.fill(stddev, stdDevVal);
            else
                // Else parse column from archtypes.
                for(int i=0; i < soln.householdArchTypes.length; i++) {
                    stddev[i] = soln.householdArchTypes[i].getAttributeValue(stdDevIdx);
                    
                    if(stddev[i] < 0)
                        throw new DataException(
                            String.format("Standard deviation cannot be less than zero:"
                                +" Household archtypes file \"%s\", row %d, column %s.",
                                soln.householdSchema.getFile(), i, stdDevCol));
                }

            // Randomize.
            for(int i=0; i < soln.householdArchTypes.length; i++) {
                userExpansionFactors[i] = MyRandom.nextGaussian(userFactorRnd,
                    userExpansionFactors[i], stddev[i]);
                
                if(userExpansionFactors[i] < 0)
                    userExpansionFactors[i] = 0;
            }
        }
        
        return userExpansionFactors;
    }

    /**
     * Compute multipliers with values determined by statistics.
     * <P>
     * Loads numRealizations2Make from cache file if possible, else computes it
     * from input data then saves to cache file when done. Note the cache file
     * contains only the optimized values; user's adjusted values (via the
     * <&lt;phase1&gt;> element are computed separately.
     * 
     * @return array of optimal multipliers
     * @throws IOException 
     */
    @SuppressWarnings({
            "unchecked", "unused"
    })
    protected int[] makeStatFactors() {
        // Cache file must be seed specific; each realization will have a different seed.
        // Constants are hidden here as the cache file is not meant for public consumption.
        // If that changes, then a new class should be made to wrap all this stuff.
        File cacheFile = FileUtil.resolve(RGIS.getOutputFolder(),
            String.format("phase1-cache-%d.bin", initialSeed));
        // Phase 1 Properties.
        final String P1P_RESULT = "numRealizations2Make";
        final String P1P_LAST_MODIFIED = "last-modified";
        final String P1P_RANDOM_SEED = "random-seed";
        
        // Compute time stamp for our input files
        Date inputTime = householdArchTypesTime.after(configTime) ? householdArchTypesTime : configTime;
        
        int[] numRealizations2Make = null;

        //
        // Notes:
        //  * Only one thread needs to load or compute expansion factors, so
        //    sync() causes all other threads in this VM to wait.
        //  * Since we can't lock files but only channels, there's a bit of a
        //    race condition between the reading and writing chucks of code. But we
        //    use tryLock() in the writer, so it shouldn't be a problem.
        //
        
        synchronized (cacheFileLock) {
            while(numRealizations2Make == null) {
                // Skip this quietly if file doesn't exist. We only want to print
                // warnings if file exists but is corrupt.  If it's missing, we'll
                // just build a new one below.
                if(cacheFile.exists()) {
                    HashMap<String, Object> props = null;
                    ObjectInputStream in = null;
                    FileLock lock = null;
                    try {
                        // Load file. If another thread or process is busy
                        // generating this file, lock() will wait for them to
                        // finish.
                        FileInputStream fin = new FileInputStream(cacheFile);
                        lock = fin.getChannel().lock(0, Long.MAX_VALUE, true);
                        in = new ObjectInputStream(fin);
                        props = (HashMap<String, Object>) in.readObject();
                        in.close();
                    } catch (IOException e) {
                        log.warning("Unable to read phase-1 cache file: "+ObjectUtil.getMessage(e));
                    } catch (ClassNotFoundException e) {
                        LogUtil.cr(log);
                        log.warning("Unable to read phase-1 cache file: "+ObjectUtil.getMessage(e));
                    }
                    finally {
                        if(lock != null)
                            try {
                                // in.close() doesn't release lock.
                                lock.release();
                            } catch (IOException e) {
                                // don't care
                            }
                        if(in != null)
                            try {
                                in.close();
                            } catch (IOException e) {
                                // don't care
                            }
                    }

                    // props will be null if file doesn't exist, or exists but
                    // hasn't been written yet, or exists and is corrupt.
                    
                    props = null; // ZZZ changed to ensure nothing is duplicated
                    
                    if(props != null) {
                        // Extract contents.
                        int[] cacheData = (int[]) props.get(P1P_RESULT);
                        Date cacheTime = (Date) props.get(P1P_LAST_MODIFIED);
                        Long cacheSeed = (Long) props.get(P1P_RANDOM_SEED);
        
                        // Verify contents.
                        // If input files are newer than cache, then bail and regenerate.
                        // If cached random seed is null, seed changes every run, so bail and regenerate.
                        // If random seed has changed, bail and regenerate.
                        // We use !before because there's no sameOrAfter.
                        // We use new Long().equals() in case an old cache file is missing random-seed.
                        if(!cacheTime.before(inputTime) && 
                                cacheSeed != null && cacheSeed.equals(params.getRandomSeed())) {
                            numRealizations2Make = cacheData;
                            householdArchTypesTime = cacheTime;
                            LogUtil.progress(log, "Loaded results from cache file "+cacheFile.getName());
                        }
                    }
                }

                // If anything above failed, then we will need to generate a new
                // multiplier array and save it to the cache file. Since other
                // threads or processes may be attempting the same thing, we
                // will try to create and lock the cache file now as a warning
                // to other processes that they should not waste time computing
                // multipliers.

                if(numRealizations2Make == null) {
                    // Create file and lock it now. "new FOS" will not fail if
                    // the file already exists, so we need a FileLock to detect
                    // if anyone else is computing multipliers.  We only want a
                    // tryLock() so if this fails, we'll re-run the above code
                    // to load the cache once it gets created.
                    
                    // If any of this crap fails, we'll just do the computation
                    // but not write the file.
                    
                    FileOutputStream fout = null;
                    FileLock lock = null;
                    boolean locked = false;
                    try {
                        fout = new FileOutputStream(cacheFile);
                        lock = fout.getChannel().tryLock();
                        locked = (lock != null);
                    } catch (IOException e) {
                        // Failed: just warn, then do the computation.
                        LogUtil.cr(log);
                        log.warning("Unable to write phase-1 cache file: "+ObjectUtil.getMessage(e));
                        
                        if(lock != null)
                            //@SuppressWarnings({"unused"}) due to this code.
                            //Java thinks it's dead code, but it's not: tryLock() can throw.
                            try {
                                // in.close() doesn't release lock.
                                lock.release();
                            } catch (IOException e1) {
                                // don't care
                            }
                        if(fout != null) {
                            try {
                                fout.close();
                                locked = true;
                            } catch (IOException e1) {
                                // don't care
                            }
                        }
                    }

                    try {
                        if(locked) {
                            numRealizations2Make = computeMultiplier();
        
                            if(fout != null) {
                                // Build cache.
                                HashMap<String, Object> props = new HashMap<String, Object>();
                                props.put(P1P_RESULT, numRealizations2Make);
                                props.put(P1P_LAST_MODIFIED, inputTime);
                                props.put(P1P_RANDOM_SEED, params.getRandomSeed());
        
                                // Write new cache file.
                                ObjectOutputStream out = null;
                                try {
                                    out = new ObjectOutputStream(fout);
                                    out.writeObject(props);
                                } catch (Exception e) {
                                    // Just warn; we've done the computation, so can keep running.
                                    LogUtil.cr(log);
                                    log.warning("Unable to write phase-1 cache file: "+ObjectUtil.getMessage(e));
                                }
                            }
                        }
                    }
                    finally {
                        if(fout != null) {
                            try {
                                fout.close();
                            } catch (IOException e1) {
                                // don't care
                            }
                        }
                    }
                }
                
                if(numRealizations2Make == null) {
                    // Looks like someone else is computing multipliers in another
                    // process. We don't have a good way to wait for them, so we'll
                    // just retry periodically.
                    try {
                        cacheFileLock.wait(2000);
                    } catch (InterruptedException e) {
                        // don't care
                    }
                }
            }
        }
        
        return numRealizations2Make;
    }
    
    
    /**
     * Compute numRealizations2Make, which holds the number of times to clone each household
     * archtype to best fit the required statistics.
     */
    protected int[] computeMultiplier() {
        //  - Note we're not *placing* the rzns for real, so the spread() method is
        //    a waste of time.  Instead, we'll use getSum and getTotal to examine
        //    overall map-wide stats.

        Solution probe = soln.createCopy();

        // Find a valid tract we can use to probe stats.
        int sampleTract = regionList.get(0);
        
        // Prime the stats with naive assumption.
        for( int j = 0; j < probe.householdArchTypes.length; j++) {
            for(int i=0; i<naiveExpansion; i++)
                probe.addRealization(j, sampleTract);
        }        

        // Now create helper with init'd stats.
        Phase_ComputeMultiplier help = new Phase_ComputeMultiplier(probe);
        
        // We will not take away a rzn from any archtype that has too few.
        // This *seems* to help, though we don't have any hard data to prove.
        //help.minimumRealizations = (int) Math.floor(0.17 * naiveExpansion);
        // The archtypes are real households, so ensure at least 1 copy.
        //if(help.minimumRealizations < 1)
            help.minimumRealizations = MINIMUM_HOUSEHOLD_EXPANSION;

        // Finish init of helper.
        help.bestfit = probe.getFit();

        Date startArchCount = new Date();
        Date progressTime = startArchCount;
        boolean checkedAll = false;

        LogUtil.cr(log);
        LogUtil.progress(log, "Starting the process of optimizing numbers of archtypes at " + startArchCount);

        help.printProgressHeader(log);
        help.printProgress(log, 0);

        // CRE: not sure adjusting counts is a good idea: reverting code to increment by 1 only
        // Start by adjusting counts by the largest increment we can.
        // Use power of two cuz we're gonna divide by two progressively
        // til we're nudging counts by one.
        int delta = 1;
        /* CRE: returning code to increment by one
        while(delta < naiveExpansion)
            delta *= 2;
        // delta is now > naiveMult, so pull it back one step.
        if(delta > 1)
            delta /= 2;
        // pull it back one more; this seems to produce a higher quality result.
        if(delta > 1)
            delta /= 2;
         end of CRE adjustment.   */

        // Determine time limit.
        double limit = -1;
        if(params.getPhase1TimeLimit() > 0)
            limit = 60 * params.getPhase1TimeLimit();
        
        // Nudge counts of archtypes up and down to hunt for the best fit we
        // can get here.  This can take a while, and the result will never be
        // perfect since our sampleTract is always wrong, so we'll give up
        // when either:
        //  - time limit PHASE1_TIME_LIMIT is exceeded, or
        //  - we've tested every archtype but didn't change any of them.
        while( checkedAll == false) {
            Date nowTime = new Date();
            double elapsed = (nowTime.getTime()-startArchCount.getTime()) / 1000.0;
            
            // Time limit up?  Bail.
            if(limit > 0 && elapsed > limit)
                break;
            
            // Display progress every minute.
            if( nowTime.getTime() - progressTime.getTime() > 60000) {
                progressTime = nowTime;

                help.printProgress(log, elapsed);
            }

            // Pick an archtype at random and adjust it.  If stats improve, keep the change, and
            // pick another at random.  If not, cycle through all the other archtypes in order
            // until we find one that helps.  If none help, decrease delta factor, and try again.
            // If delta is already at one, we can't do any more, so quit.
            int archtype = statFactorRnd.nextInt(soln.householdArchTypes.length);
            int lastArchtype = archtype;
            do {
                // Check if one more of this archtype improves fit.
                if(help.tryMore(archtype, delta, sampleTract))
                    break;
                else if(help.tryLess(archtype, delta, sampleTract))
                    break;

                // Step to previous archtype.
                archtype--;
                if (archtype < 0)
                    archtype = probe.householdArchTypes.length - 1;
                if (lastArchtype == archtype)
                    checkedAll = true;
            } while(archtype != lastArchtype);
            
            if(checkedAll) {
                if(delta > 1) {
                    delta /= 2;
                    checkedAll = false;
                }
            }
        }

        // Extract results from helper.
        int[] numRealizations2Make = help.getNumberRealizations();
        
        Date endArchCount = new Date();
        
        // Dump per-stat fit.
        double elapsed = (endArchCount.getTime()-startArchCount.getTime()) / 1000.0;
        help.printProgress(log, elapsed);
        
        if(!checkedAll)
            LogUtil.progress(log, "Aborting process: time budget exceeded");
        else
            LogUtil.progress(log, "Ending process: no more moves can be found");
        
        // Print final quality report.
        LogUtil.cr(log);
        LogUtil.result(log, help.dumpFit("Quality of Adjusted Quantities"));
        
        return numRealizations2Make;
    }

    protected String dumpNumberArchtypes(int[] numRealizations2Make) {
        StringOutputStream sos = new StringOutputStream();
        ArrayList<String> msgs = new ArrayList<String>();

        sos.println("Final expansion factors:");

        // Compute maximum printed width of 'i'.
        // - Java doesn't support the "%*d" like printf, so we build the format
        //   string here, and slip the computed width into it.
        int idxWid = 1;
        if(numRealizations2Make.length > 9)
            idxWid = 1 + (int) Math.floor(Math.log10(numRealizations2Make.length));
        String idxFmt = "%"+idxWid+"d:";

        // Compute maximum printed width of values.
        // - Same deal as above.
        // - Track min and max separately, as negative numbers print 1 char wider.
        double max = 0, min = 0;
        for(int i=0; i<numRealizations2Make.length; i++) {
            max = Math.max(max, numRealizations2Make[i]);
            min = Math.min(min, numRealizations2Make[i]);
        }        
        int valWid = 1;
        if(max > 9)
            valWid = 1 + (int) Math.floor(Math.log10(max));
        if(min < 0)
            valWid = Math.max(valWid, 2 + (int) Math.floor(Math.log10(-min)));
        String valFmt = " %"+valWid+"d";
        
        // Print out the list.
        for(int i=0; i<numRealizations2Make.length; i++) {
            if(i>0) {
                // 20 nums per line
                if(i % 20 == 0)
                    sos.println();
            }
            // Start each line with its index.
            if(i % 20 == 0)
                sos.format(idxFmt, i);
            
            // Append value.
            sos.format(valFmt, numRealizations2Make[i]);
            
            // Generate warning for every zero.
            if(numRealizations2Make[i] == 0)
                msgs.add(String.format("WARNING: Archtype %d has ZERO realizations.", i));
        }

        // Append warnings.
        if(msgs.size() > 0) {
            sos.println();  // end last line above
            sos.println();  // blank line
            sos.print(ObjectUtil.join(msgs, "\n"));  // not 'println'; logging will take care of it
        }
        
        return sos.toString();
    }
}
