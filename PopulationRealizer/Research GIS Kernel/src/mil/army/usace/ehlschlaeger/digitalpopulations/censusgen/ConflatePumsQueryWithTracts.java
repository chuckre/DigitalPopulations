package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.digitalpopulations.ConstrainedRealizer;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.FileRelationship;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.FittingCriteria;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.TraitRefElement;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.statistics.*;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.NullExecutorService;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;
import mil.army.usace.ehlschlaeger.rgik.util.QueuePutPolicy;
import mil.army.usace.ehlschlaeger.rgik.util.TimeTracker;

import org.xml.sax.SAXException;



/**
 * Create synthetic census data using a sampling of actual census data and
 * land-use maps to guide the placement of households.  Process runs in
 * several phases:
 * <OL>
 *   <LI> Load and pre-process data ({@link DataPreparer}).
 *   <LI> Compute expansion factor ({@link Phase_ExpansionFactor}).
 *   <LI> Build clones of archtypes, sort into regions based on statistics
 *        ({@link Phase_InitialPlacement}).
 *   <LI> Move clones between regions to optimize layout
 *        ({@link Phase_OptimizeRegions}).
 *   <LI> Compute precise locations for clones based on statistics
 *        ({@link Phase_LocatePrecisely}).
 * </OL>
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 * @author William R. Zwicky
 * @author Yizhao Gao
 */
public class ConflatePumsQueryWithTracts implements Serializable {
    // Capture SVN metadata as strings so we can embed in log files.
    // The regex removes the SVN cruft and leaves the bare data.
    /** Version number of last change. */
    public static final String SVN_REV = ObjectUtil.extract("$Revision: 536 $", "\\$\\w+:\\s(.*)\\s\\$", 1);
    /** Last date this file was changed. */
    public static final String SVN_DATE = ObjectUtil.extract("$Date: 2012-10-30 03:57:11 -0700 (Tue, 30 Oct 2012) $", "\\$\\w+:.*\\((.*)\\)\\s\\$", 1);

    
    /**
     * Name of file where we will save all configured settings from our run.
     */
    public static final String LASTRUN_FILE = "last-run.properties";
    

    protected static Logger log = Logger.getLogger(ConflatePumsQueryWithTracts.class.getPackage().getName());
    
    /** Our run-time configuration. */
    protected Params params;

    /** Our source of random numbers. */
    protected Random random;
    
    /**
     * Date and time that configuration data (fitting and criteria files) were
     * last modified. Used to determine validity of phase 1 cache.
     */
    protected Date configTime;
    
    /** User-supplied fitting criteria and relationship spec. */
    protected FittingCriteria fitCrit;

    /**
     * Wrapper for all data representing one solution:
     * archtypes, locations of realizations, and statistical quality of same.
     */
    protected Solution soln;

//    /** Location-based stats (i.e. Match objects) for phase 2. */
//    protected ArrayList<PointSpatialStatistic> p2_pstats;
//    /** Goals objects for p2_pstats. */
//    protected ArrayList<PointSpatialStatistic> p2_pgoals;
    
    /**
     * Total number of people in all the households in householdArchTypes.
     * This will be a percentage of the actual number of people in the region.
     */
    protected int peopleInArchTypes;

    /** Main useful region map and its table. */
    protected RegionData primaryRegion;
    /** Raster map of desired relative population density. */
    protected GISLattice popDensityMap;
    /** Each map contains a single goal attribute. */
    protected HashMap<String, RegionData> attributeMaps;
    
    /** Generates easting/northing values. */
    protected ConstrainedRealizer realizer;

    // -----
    // results of one realization
    //  - optimized expansion factors
    protected int[] numRealizations2Make;
    //  - one arrangement ("realization") of households
    //    ->stored inside soln.householdArchTypes
    // -----

    
    
    /**
     * Create an unconfigured program.
     */
    public ConflatePumsQueryWithTracts() {
        params = new Params();
    }

    public void setRandomRandom() {
        // Perform a little dance to construct a fresh generator and capture the seed.
        random = new Random();
        long initialSeed = random.nextLong();
        random = new Random(initialSeed);
    }
    
    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        random = source;
    }

    /**
     * Return our source of random numbers.
     * 
     * @return current random number generator
     */
    public Random getRandomSource() {
        return random;
    }
    
    /**
     * Create a clone of this object sufficent for running runOne() in parallel.
     * Mutable data is cloned; reference data is shared.
     * 
     * @return copy of this instance
     */
    public ConflatePumsQueryWithTracts createCopy() {
        ConflatePumsQueryWithTracts obj2 = new ConflatePumsQueryWithTracts();
        obj2.params = this.params;
        obj2.random = this.random;
        obj2.configTime = this.configTime;
        obj2.fitCrit = this.fitCrit;
        obj2.peopleInArchTypes = this.peopleInArchTypes;
        obj2.primaryRegion = this.primaryRegion;
        obj2.popDensityMap = this.popDensityMap;
        obj2.attributeMaps = this.attributeMaps;

        obj2.soln = this.soln.createCopy();
        obj2.numRealizations2Make = (int[]) ObjectUtil.cloneArray(this.numRealizations2Make, true);
        
        return obj2;
    }

    /**
     * Load data and initialize structures.
     * 
     * @param crit
     *            complete FittingCriteria object (with FileRelationship) that
     *            will control this run
     * @throws IOException
     *             on any file error
     * @throws JAXBException
     *             on any problem parsing XML control files
     */
    public void prepareData(FittingCriteria crit) throws IOException {
        Date startTime = new Date();
        TimeTracker.start(this.getClass().getSimpleName());

        this.fitCrit = crit;
        
        // Load file.
        FileRelationship relat = crit.relationship;

        // Update config time stamp.
        Date ctime = crit.sourcFileTime;
        Date rtime = relat.sourceFileTime;
        configTime = ctime.after(rtime) ? ctime : rtime;

        // Start up our helper.
        DataPreparer gen = new DataPreparer(crit);
        Random myRnd = new Random(random.nextLong());
        gen.setRandomSource(myRnd);
        this.primaryRegion = gen.getPrimaryRegion();
        
        if( crit.locationWeight < 0.0) {
            throw new IllegalArgumentException( "negative value for location weight");
        }

        LogUtil.cr(log);
        LogUtil.result(log, "Census tracts report a total of %d households and %d people.",
            gen.getPrimaryRegion().aggregateHouseholds,
            gen.getPrimaryRegion().aggregatePopulation);
        LogUtil.cr(log);
        
        this.attributeMaps = gen.getAttributeMaps();
        
        // Build household and population structures
        //  - load PUMS files
        List<PumsHousehold> pumsHouses = gen.loadHouseholds();

        //  - init vars
        CSVTableNoSwing householdSchema = gen.getHouseholdSchema();
//        CSVTable populationSchema = gen.getPopulationSchema();
        int hohRows = pumsHouses.size();

        //  - count people in PUMS
        int hohPopCol = householdSchema.findColumn(relat.households.members);
        peopleInArchTypes = 0;
        for (PumsHousehold house : pumsHouses) {
            peopleInArchTypes += house.getAttributeValue(hohPopCol);
        }
        TimeTracker.finished("Parse PUMS tables");
        
        LogUtil.cr(log);
        LogUtil.result(log, "PUMS data has " + hohRows + " types of households.\n"
                    + "Each will be cloned " + (((double) gen.getPrimaryRegion().aggregateHouseholds) / hohRows) + " times on average.");
        LogUtil.cr(log);
        

        this.soln = prepareSolution(gen, pumsHouses);
        
        // Dump goals for every region.
        if(params.getDumpStatistics()) {
            for(int i=0; i<soln.goals.size(); i++) {
                LogUtil.cr(log);
                StringOutputStream sos = new StringOutputStream();
                sos.format("goal[%d]: ", i);
                soln.goals.get(i).print(sos);
                LogUtil.result(log, sos.toString());
            }
            LogUtil.cr(log);
        }
        
        TimeTracker.finished("Build statistic objects");
        
        popDensityMap = gen.makePDF(primaryRegion);
        validatePDF(popDensityMap, primaryRegion.map);

        this.realizer = new ConstrainedRealizer(primaryRegion.map, popDensityMap, soln.pcons, primaryRegion.idReverseMap);
        
        // Log time of completion.
        Date dataPrep = new Date();
        LogUtil.cr(log);
        LogUtil.progress(log, "Data preparation took %s.", TimeTracker.format(startTime, dataPrep));
    }

    /**
     * Build a Solution object as needed by all the phases.
     * 
     * @param gen helper to load and build pieces
     * @param pumsHouses household archtypes loaded by gen.loadHouseholds().
     * 
     * @return
     */
    public static Solution prepareSolution(DataPreparer gen, List<PumsHousehold> pumsHouses) {
        // --- Assemble solution manager --- //
        Solution soln = new Solution();

        // Build statistic evaluators for automatic traits
        PumsTrait[] autoTraits = gen.makeAutomaticTraits(gen.getPrimaryRegion());
        for(int i=0; i<autoTraits.length; i++) {
            SpatialStatistic stat = gen.makeAccumStat(autoTraits[i]);
            if(stat instanceof TractSpatialStatistic) {
                soln.addStat(
                    (TractSpatialStatistic) stat,
                    (TractSpatialStatistic) gen.makeGoalStat(autoTraits[i]),
                    gen.getFittingCriteria().locationWeight);
            }
        }
        
        // Build statistic evaluators for user-specified traits.
        Set<PumsTrait> keyTraits = gen.getFittingCriteria().traitWeights.keySet();
        for (PumsTrait trait : keyTraits) {
            SpatialStatistic stat = gen.makeAccumStat(trait);
            // Test for TSS first! If a stat implements both TSS and PSS, we
            // want to use it as a TSS. PSS are saved seperately as Solution
            // can't really use the, though individual phases might.
            if(stat instanceof TractSpatialStatistic) {
                soln.addStat(
                    (TractSpatialStatistic) stat,
                    (TractSpatialStatistic) gen.makeGoalStat(trait),
                    gen.getFittingCriteria().traitWeights.get(trait));
            }
            else if(stat instanceof PointSpatialStatistic) {
                // weight is useless here
                soln.addStat(
                    (PointSpatialStatistic) stat,
                    (PointSpatialStatistic) gen.makeGoalStat(trait));
            }
        }
        
        // Collect point constraints.
        try {
            soln.pcons = gen.makeConstraints();
        } catch (IOException e) {
            throw new DataException(e);
        }
        
        soln.householdArchTypes = pumsHouses.toArray(new PumsHousehold[pumsHouses.size()]);
        soln.householdSchema = gen.getHouseholdSchema();
        soln.populationSchema = gen.getPopulationSchema();
        soln.hohKeyCol = gen.getFittingCriteria().relationship.households.key;
        if(gen.getFittingCriteria().relationship.population != null
                && !ObjectUtil.isBlank(gen.getFittingCriteria().relationship.population.table))
            soln.popHohCol = gen.getFittingCriteria().relationship.population.household;
        
        return soln;
    }

    /**
     * Verify that PDF totals over each region are greater than zero. Logs a
     * warning for each region == 0, as that region will get no households.
     * 
     * @param pdf
     *            population density map
     * @param regionMap
     *            region map
     */
    protected void validatePDF(GISLattice pdf, GISClass regionMap) {
        // for each cell in region
        //   tot[region.get(r,c)] += pdm.get(e,n)
        // for each region
        //   if tot[region] == 0
        //     crash(region gets no houses)

        // Sum up PDF values for each region.
        double[] totals = new double[1000];
        for(int r=0; r<regionMap.getNumberRows(); r++) {
            for(int c=0; c<regionMap.getNumberColumns(); c++) {
                double n = regionMap.getCellCenterNorthing(r, c);
                double e = regionMap.getCellCenterEasting(r, c);
                
                int region = regionMap.getCellValue(r, c);
                double pd = pdf.getCellValue(e, n);
                
                while(totals.length <= region)
                    totals = Arrays.copyOf(totals, totals.length*2);
                totals[region] += pd;
            }
        }
        
        Set<Integer> regions = regionMap.makeInventory();
        for(int region : regions) {
            if(totals[region] <= 0)
                log.warning(String.format("Population density map is zero for all of region %d", this.primaryRegion.idReverseMap.get(region)));
        }
    }
    
    /**
     * Run program as currently configured.
     * 
     * @throws IOException 
     */
    public void run() throws Exception {
        Date start = new Date();

        if(params.getRandomSeed() == null)
            setRandomRandom();
        else
            setRandomSource(new Random(params.getRandomSeed()));
        
        // Save now to preserve config in case of crash.
        //params.save(LASTRUN_FILE);
        
        // "Hard open": crash if problems.
        File mainFile = params.getCriteriaFile();
        try {
            FittingCriteria crit = FittingCriteria.loadFile(mainFile, null);
            prepareData(crit);
        } catch (JAXBException e) {
            throw new IOException(ObjectUtil.getMessage(e));
        } catch (SAXException e) {
            throw new IOException(ObjectUtil.getMessage(e));
        }
        
        int firstReal = params.getFirstRzn();
        int lastReal = params.getLastRzn();
        if(params.getThreads() == 1 || lastReal-firstReal == 0)
            runAll(firstReal, lastReal);
        else
            runAllParallel(firstReal, lastReal);
        
        Date end = new Date();
        LogUtil.cr(log);
        LogUtil.progress(log, "Digital Populations completed in %s.", TimeTracker.format(start,end));
        
        // Save one more time in case someone changed something.
        //params.save(LASTRUN_FILE);
    }

    /**
     * Compute and save all realizations.
     * 
     * @param firstReal number of first realization to run
     * @param lastReal number of final realization to run
     * 
     * @throws IOException
     */
    public void runAll(int firstReal, int lastReal) throws Exception {
        // Pull out a set of seeds before we start processing.
        // This allows different computers to work on different realizations
        // from the same set of random numbers.
        long[] realizationSeeds = new long[lastReal-firstReal+1];
        for( int realNum = firstReal; realNum <= lastReal; realNum++) {
            realizationSeeds[realNum-firstReal] = random.nextLong();
        }

        Solution masterSoln = soln;
        for( int realNum = firstReal; realNum <= lastReal; realNum++) {
            this.random.setSeed(realizationSeeds[realNum-firstReal]);
            // Stats are mangled during each run, so we need to reset to a clean state.
            this.soln = masterSoln.createCopy();
            runOne(realNum);
        }
    }

    /**
     * Experimental version of runAll(). Seems to work fine, though oddities:
     * all threads write to same log simultaneously, interleaving lines. Also,
     * first batch of threads will run phase 1 and save the cache file, if it
     * doesn't exist when they start.
     * 
     * @param firstReal number of first realization to run
     * @param lastReal number of final realization to run
     */
    public void runAllParallel(int firstReal, int lastReal) {
        int threads = params.getThreads();
        
        // Build parallel runner.
        ExecutorService exec;
        if(threads > 1) {
            // Windows multithreading sucks, as usual. If we consume all
            // available processors, entire system becomes unresponsive. So we
            // drop the entire app's priority to compensate.
            int p = Thread.currentThread().getPriority() - 4;
            p = Math.max(p, Thread.MIN_PRIORITY);
            Thread.currentThread().setPriority(p);
            
            // Use exactly one thread per core, and no job queue.
            exec = QueuePutPolicy.newFixedThreadPool(threads);
        }
        else
            exec = new NullExecutorService();
        
        // Pull out a set of seeds before we start processing.
        // This allows different computers to work on different realizations
        // from the same set of random numbers.
        long[] realizationSeeds = new long[lastReal-firstReal+1];
        for( int realNum = firstReal; realNum <= lastReal; realNum++) {
            realizationSeeds[realNum-firstReal] = random.nextLong();
        }
        
        for(int realNum = firstReal; realNum <= lastReal; realNum++) {
            final int f_num = realNum;
            final Random f_rnd = new Random(realizationSeeds[realNum-firstReal]);
            
            exec.submit(new Runnable() {
                public void run() {
                    try {
                        // Stats are mangled during each run, so we need to reset to a clean state.
                        // Not to mention, the system is not thread-safe, so we need a unique
                        // instance for each thread.
                        ConflatePumsQueryWithTracts runner = createCopy();
                        runner.setRandomSource(f_rnd);
                        runner.runOne(f_num);
                    }
                    catch (Throwable e) {
                        // Can't do much here; throwing won't stop the program.
                        log.log(Level.SEVERE, String.format("Processing realization %d crashed", f_num), e);
                    }
                }
            });
        }
        
        // Ensure Java exits cleanly even if error.
        exec.shutdown();
    
        // Wait for all the threads to finish.
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Trouble while waiting for runs to complete", e);
        }
    }

    /**
     * Compute and save a single realization.
     * 
     * @param realizationNum
     * @param randomSeed 
     * @throws IOException
     */
    public void runOne(int realizationNum) throws Exception {

        // Dandong Yin
        //
        // The following block of code will resume an old Phase4 from file, run it and exit the program
        // Therefore everything else (Phase1-3, and normal Phase4) is skipped
        // To enable a normal run from Phase1, comment out this block and re-compile
        //if (true == true) {
        //    phase4_fromFile("phase4_save_"+realizationNum+".se");
        //    return;
        //}

        Date realizationStartTime = new Date();

        // Pull random seeds ahead of time for repeatability.
        long rndP1 = random.nextLong();
        long rndP2 = random.nextLong();
        long rndP3 = random.nextLong();
        long rndP4 = random.nextLong();

        LogUtil.cr(log);
        LogUtil.progress(log, "ConflatePumsQueryWithTracts: realization [" + realizationNum + "] started at " + realizationStartTime.toString());
        LogUtil.cr(log);
        
      //Yizhao Gao --- The summary statistics of archtype can go here
        /*
        for(int i = 0; i < this.soln.householdArchTypes.length; i++)
        {
        	this.soln.addRealization(i, 0);
        }
        
        StringOutputStream sos = new StringOutputStream();
        sos.format("\n%s:\n", "Summaries of archtypes");
        sos.format("%4s  %-15s  %-15s  %-15s  %s\n",
            "stat", "archtypeProportion", "GoalProprotion", "Trait");
        
        
        SpatialStatistic stat;
        SpatialStatistic goal;
        
        for (int i = 0; i < this.soln.stats.size(); i++)
        {
        	stat = this.soln.stats.get(i);
        	goal = this.soln.goals.get(i);
        	
        	if(goal instanceof Proportion)
        	{
        		Proportion pS = (Proportion)stat;
        		Proportion pG = (Proportion)goal;
        		
        		sos.format("%4s  %8.1f   %8.1f  %s\n",
        	                String.format("[%d]",i), 
        	                pS.getSum() / pS.getTotal(),
        	                pG.getSum() / pG.getTotal(),
        	                soln.stats.get(i));
        	}
        }
        
        LogUtil.result(log, sos.toString());
        
        for(int i = 0; i < this.soln.householdArchTypes.length; i++)
        {
        	this.soln.removeRealization(i, 0);
        }
        */

        
        // --- Phase 1 --- //
        printSection("START PHASE 1");
        
        phase1_computeExpansionFactor(rndP1);
        
        LogUtil.cr(log);
        TimeTracker.finished("Phase 1");
        printSection("END PHASE 1");
        
        
        // --- Phase 2 --- //
        printSection("START PHASE 2");
        
        phase2_makeInitialPlacement(rndP2);
        
        TimeTracker.finished("Phase 2");
        printSection("END PHASE 2");

        // Intermission //
        
        printSolutionStats();
        validateTracts();        
        
        Date nowTime = new Date();
        LogUtil.cr(log);
        LogUtil.progress(log, "Initial construction required %s.", TimeTracker.format(realizationStartTime, nowTime));
        LogUtil.cr(log);

        
        // --- Phase 3 --- //
        boolean doPhase3 = ! params.getPhase3Skip();
//cdfMaps are gone; we may want to add a field regionList to contains the subset of regions to focus on.
//        if(doPhase3) {
//            if(cdfMaps.size() > 1)
//                doPhase3 = true;
//            else {
//                // Override; nothing to do.
//                doPhase3 = false;
//                LogUtil.cr(log);
//                LogUtil.progress(log, "Skipping phase 3, it has no effect with only one region.");
//            }
//        }
        if(doPhase3) {
            LogUtil.cr(log);
            LogUtil.progress(log, "ConflatePumsQueryWithTracts: saving preliminary household locations ...");
            writeFileSet(realizationNum, "initial", null);
            TimeTracker.finished("Writing files");   //Reset timer so this isn't charged to Phase 3
              
            printSection("START PHASE 3");

            phase3_findBestPlacement(realizationNum, rndP3);

            TimeTracker.finished("Phase 3");
            printSection("END PHASE 3");

            printSolutionStats();
        }
        
        // Only applies to phases 1-3.
        printInterpretation();
        
        
        // --- Phase 4 --- //
        boolean doPhase4 = ! params.getPhase4Skip();
        if(doPhase4) {
            if(fitCrit.hasCluster())
                doPhase4 = true;
            else {
                // Override; no input data provided.
                doPhase4 = false;
                LogUtil.cr(log);
                LogUtil.progress(log, "Skipping phase 4: fitting criteria file doesn't doesn't contain any instructions.");
            }
        }
        if(doPhase4) {
            LogUtil.cr(log);
            printSection("START PHASE 4");
            
            // Phase4 has its own writeFileSet, and will write intermediate and final versions.
            phase4_locatePrecisely(realizationNum, rndP4);
            
            printSection("END PHASE 4");
        }
        else {
            // Skipped phase 4, so write final result.
            LogUtil.cr(log);
            LogUtil.progress(log, "ConflatePumsQueryWithTracts: saving final household locations ...");
            writeFileSet(realizationNum, null, null);
            TimeTracker.finished("Writing files");
        }
    }
    
    /**
     * Compute the "expansion factor" for household archtypes.
     * "Expansion factor" is defined as the number of clones we will create of
     * the records in the households table.
     * <P>
     * Results are left in member numRealizations2Make.
     */
    protected void phase1_computeExpansionFactor(long seed) {
        Phase_ExpansionFactor p1 = new Phase_ExpansionFactor(soln, fitCrit.phase1,
            primaryRegion, peopleInArchTypes,
            configTime);
        p1.setRandomSeed(seed);
        p1.setParams(params);
        numRealizations2Make = p1.go();
    }

    /**
     * Generate a complete set of households, giving them a plausible initial
     * placement.
     */
    protected void phase2_makeInitialPlacement(long seed) {
        LogUtil.cr(log);
        LogUtil.progress(log, "Starting phase 2: Computing inital placement of households");

        Phase_InitialPlacement p2 = new Phase_InitialPlacement(
            soln, primaryRegion.map, numRealizations2Make);
        p2.setParams(params);
        p2.setRandomSource(new Random(seed));
        p2.go();
        // Result is tract numbers attached to archtypes in this.soln.
        
        LogUtil.cr(log);
        LogUtil.progress(log, "Phase 2 complete.");
    }

    /**
     * Verify that the quantities loaded into trait 0 (number households per
     * tract) match where the archtypes think they have rzns.
     * <p>
     * Assumes trait 0 is a 'Count' object, and that it counts households. If
     * these change, then this method will need to be updated.
     * 
     * @throws DataException
     *             if any count doesn't match
     */
    protected void validateTracts() {
        // = The Plan =
        // trait 0 is #hoh in each tract
        // new ary[#tracts]
        // for each hoh
        //   for each rzn
        //     ary[hoh.getTract(rzn)]++;
        // compare to trait 0
        
        // We cheat by using our auto-generated trait to provide the numbers.
        // This is of course a big hack, and will break if trait[0] ever changes.
        Count hohPerTract = (Count) soln.stats.get(0);
        assert hohPerTract.toString().equals("Count for Absolute Households (AUTO)");

        // total houses we intend to place in each tract
        int[] counts = new int[hohPerTract.getLastRegion()+1];
        
        // Count planned distribution of archtypes.
        for(int hoh = 0; hoh < soln.householdArchTypes.length; hoh++) {
            PumsHousehold house = soln.householdArchTypes[hoh];
            for (int rzn = 0; rzn < house.getNumberRealizations(); rzn++) {
                int tract = house.getRealizationTract(rzn);
                counts[tract] += 1;
            }
        }
        
        // Compare against statistics.
        StringBuffer buf = new StringBuffer();
        for(int i=hohPerTract.getFirstRegion(); i<=hohPerTract.getLastRegion(); i++) {
            if(counts[i] != hohPerTract.getNumInRegion(i)) {
                if(buf.length() > 0)
                    buf.append("\n");
                buf.append(String.format("Region %d: trait0 requires %d households, but we've only placed %d there",
                    i,
                    hohPerTract.getNumInRegion(i),
                    counts[i]));
            }
        }
        
        if(buf.length() > 0)
            throw new DataException(buf.toString());
    }

    /**
     * Move households around to find the arrangement that best fits the
     * statistic evaluators.
     * 
     * @param realizationNum
     *            ID number for current realization. Used to create distinct
     *            output files.
     * @throws IOException
     *             on any file error
     */
    protected void phase3_findBestPlacement(int realizationNum, long seed) {
        LogUtil.cr(log);
        LogUtil.progress(log, "Starting phase 3: Optimizing the sorting of households into regions");
        
        Phase_OptimizeRegions p3 = new Phase_OptimizeRegions(soln, primaryRegion.map, popDensityMap, primaryRegion.idReverseMap);
        p3.setParams(params);
        p3.setRandomSource(new Random(seed));
        p3.setRealizer(realizer);
        p3.go(realizationNum);
        
        LogUtil.cr(log);
        LogUtil.progress(log, "Phase 3 complete.");
    }
    
    /**
     * Compute precise easting/northing locations for all household
     * realizations. Process is guided by statistic objects that try to enhance
     * clustering of attributes (i.e. different tribes are clustered in their
     * own locations.)
     * 
     * @param realizationNum
     * @param seed 
     * 
     * @throws IOException on any error writing output files
     */
    protected void phase4_locatePrecisely(int realizationNum, long seed) throws IOException {
        // Run phase 4.
//        Phase_LocatePrecisely p4 = new Phase_LocatePrecisely( //Edited by Yizhao
        // Serialization

    	Phase_LocatePrecisely_GridIndex p4 = new Phase_LocatePrecisely_GridIndex(
            realizationNum,
            soln.householdArchTypes,
            primaryRegion.map,
            popDensityMap,
            soln.pcons,
            fitCrit.traitCluster,
            primaryRegion.idReverseMap);
        p4.setParams(params);
        p4.setRandomSource(new Random(seed));
        p4.setRealizer(realizer);
        p4.go();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("phase4_save_"+realizationNum+".se"));
        out.writeObject(p4);
//        out.writeObject(realizationNum);
//        out.writeObject(soln.householdArchTypes);
//        out.writeObject(primaryRegion.map);
//        out.writeObject(popDensityMap);
//        out.writeObject(soln.pcons);
//        out.writeObject(fitCrit.traitCluster);
//        out.writeObject(params);
//        out.writeObject(seed);
//        out.writeObject(realizer);
        out.close();
    }

    protected void phase4_fromFile(String filename) throws Exception{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
//        int realizationNum = (int)in.readObject();
//        PumsHousehold[] householdArchTypes = (PumsHousehold[]) in.readObject();
//        GISClass map = (GISClass) in.readObject();
//        GISLattice densityMap = (GISLattice) in.readObject();
//        List<PointConstraint> pcons = (List<PointConstraint>) in.readObject();
//        LinkedHashMap<Trait,TraitRefElement> traitCluster = (LinkedHashMap<Trait,TraitRefElement>) in.readObject();
//        Params params = (Params) in.readObject();
//        long seed = (long) in.readObject();
//        ConstrainedRealizer realizer = (ConstrainedRealizer) in.readObject();
        Phase_LocatePrecisely_GridIndex p4 = (Phase_LocatePrecisely_GridIndex)in.readObject();
        in.close();

//        Phase_LocatePrecisely_GridIndex p4 = new Phase_LocatePrecisely_GridIndex(
//                realizationNum,
//                householdArchTypes,
//                map,
//                densityMap,
//                pcons,
//                traitCluster);
//        p4.setParams(params);
//        p4.setRandomSource(new Random(seed));
//        p4.setRealizer(realizer);
        p4.resume();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(p4);
        out.close();
    }
    /**
     * Print all stats from our Solution object. 
     * @return getSpread()
     * @throws IOException on any error constructing message
     */
    protected double printSolutionStats() {
        for (int i = 0; i < soln.stats.size(); i++) {
            SpatialStatistic stat = soln.stats.get(i);
            SpatialStatistic goal = soln.goals.get(i);
            StringOutputStream sos = new StringOutputStream();
            sos.format("stat[%d]: ", i);
            try {
                stat.print(sos, goal);
            } catch (IOException e) {
                throw new RuntimeException("Internal error", e);
            }
            LogUtil.cr(log);
            LogUtil.result(log, sos.toString());
        }
        
        double fit = soln.getSpread();
        LogUtil.cr(log);
        LogUtil.result(log, "spread: %f", Math.sqrt(fit));
        LogUtil.cr(log);
        
        return fit;
    }

    /**
     * Log a bold and obvious section separator.
     * @param label text to place inside separator block.
     */
    protected void printSection(String label) {
        final String SEP = "******************************************************************************";
        LogUtil.cr(log);
        LogUtil.progress(log, String.format("%s\n**** %-68s ****\n%s", SEP, label, SEP));
        LogUtil.cr(log);
    }
    
    /**
     * Print descriptive listing of stat values and what they mean.
     */
    protected void printInterpretation() {
        LogUtil.cr(log);
        LogUtil.result(log, "Map-Wide Statistic Summary:");
        LogUtil.result(log, "      Object: %10s %10s  %10s %10s  %11s  %s",
            "Placed-Sum", "Placed-Tot", "Goal-Sum", "Goal-Tot", "Average-Off", "Description");
        
        for (int i = 0; i < soln.stats.size(); i++) {
            SpatialStatistic stat = soln.stats.get(i);
            SpatialStatistic goal = soln.goals.get(i);
            
            StringBuffer buf = new StringBuffer();
            buf.append(String.format("    stat[%2d]: ", i));
            
            if(stat instanceof Proportion) {
                Proportion s = (Proportion)stat;
                buf.append(String.format("%10.0f %10.0f  ",
                    s.getSum(), s.getTotal()));
            }
            else if(stat instanceof Count) {
                Count s = (Count)stat;
                buf.append(String.format("%10s %10.0f  ",
                    "", s.getSum()));
            }
            else {
                buf.append(String.format("%10s %10s  ",
                    "", ""));
            }
                
            if(goal instanceof Proportion) {
                Proportion s = (Proportion)goal;
                buf.append(String.format("%10.0f %10.0f  ",
                    s.getSum(), s.getTotal()));
            }
            else if(goal instanceof Count) {
                Count s = (Count)goal ;
                buf.append(String.format("%10s %10.0f  ",
                    "", s.getSum()));
            }
            else {
                buf.append(String.format("%10s %10s  ",
                    "", ""));
            }

            if(stat instanceof TractSpatialStatistic) {
                buf.append(String.format("%11.2f", ((TractSpatialStatistic) stat).averageOff((TractSpatialStatistic) goal)));
            }
            else {
                buf.append(String.format("%11s", ""));
            }
            
            buf.append(String.format("  %s", stat.toString()));
            
            LogUtil.result(log, buf.toString());
        }
    }

    /**
     * Write current solution to disk.
     * 
     * @param runNumber
     *            ID number for the current run. Will be placed in file names.
     * @param nameNote
     *            extra string to append to file names
     * @param pumsQuery
     *            filter to select which records to write
     * 
     * @throws IOException
     *             on any file error
     */
    protected void writeFileSet(int runNumber, String nameNote, PumsQuery pumsQuery)
        throws IOException {
        // Abuse Phase 3 to write our files.
        Phase_OptimizeRegions helper = new Phase_OptimizeRegions(soln, primaryRegion.map, popDensityMap, primaryRegion.idReverseMap);
        helper.setParams(params);
        helper.setRandomSource(random);
        helper.writeFileSet(runNumber, nameNote, pumsQuery);
    }

    /**
     * Write this object and all its data to a binary file. This file can be
     * used to reconstruct this object and resume processing.
     * 
     * @param outputFile
     *            path and name of file to create. No extension is added, but
     *            ".state" is recommended.
     * @throws IOException on any file error
     */
    public void saveState(String filename) throws IOException {
        File outputFile = FileUtil.resolve(RGIS.getOutputFolder(), filename);
        ObjectOutputStream out = 
            new ObjectOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream(outputFile)));
        try {
            out.writeObject(this);
        }
        finally {
            out.close();
        }
    }

    
    
    /**
     * Reconstruct an instance from a binary file created by saveState.
     * 
     * @param file
     *            path and name of file to load
     * @return new instance containing all the data that was in the originally
     *         saved instance
     * @throws IOException
     *             on any file error
     * @throws ClassNotFoundException
     *             if a class cannot be found for any of the instances in the
     *             file. (Only class contents are saved; the classes themselves
     *             must be on the classpath.)
     */
    public static ConflatePumsQueryWithTracts loadState(File file)
            throws IOException, ClassNotFoundException {
        ObjectInputStream in = 
            new ObjectInputStream(
                new BufferedInputStream(
                    new FileInputStream(file)));
        try {
            return (ConflatePumsQueryWithTracts) in.readObject();
        }
        finally {
            in.close();
        }
    }

    

    /**
     * Parse command-line args and update our properties.
     * 
     * @param argv list of arguments provided to main
     * @return false if program should not continue
     * 
     * @throws IOException
     */
    protected static boolean mergeArgs(String argv[], Params params) throws IOException {
        OptionParser parser = new OptionParser();
        parser.accepts("c", "configuration (properties file) to load before parsing command line (default=last-run.properties)").withRequiredArg().ofType(File.class);
        parser.accepts("o", "output directory (default=current dir)").withRequiredArg().ofType(File.class);
        parser.accepts("f", "first realization number (default=1)").withRequiredArg().ofType(Integer.class);
        parser.accepts("l", "last realization number (default=value of -f)").withRequiredArg().ofType(Integer.class);
        parser.accepts("r", "random seed (default=time-dependent random)").withRequiredArg().ofType(Long.class);
        parser.accepts("s", "phase 2 skip factor (default=99.5%; zero is best but slowest)")
            .withRequiredArg().ofType(Double.class);
        parser.acceptsAll(Arrays.asList(
            "p", "phase"),
            "final phase to execute:  system will save and exit when this phase completes (default=3, which skips phase 4)")
            .withRequiredArg().ofType(Integer.class);
        parser.accepts(
            "parallel",
            "number of realizations to build simultaneously (default=1)")
            .withRequiredArg().ofType(Integer.class);

        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");
        
        OptionSet opts = parser.parse(argv);
        if(opts.has("h") || opts.has("help") || argv.length == 0) {
            System.out.println("Usage:\n  "+ConflatePumsQueryWithTracts.class.getSimpleName()+" [options] fitting-criteria-file\n");
            parser.printHelpOn(System.out);
            return false;
        }

        // Load config before parsing rest of command line.
        if(opts.has("c")) {
            File f = (File) opts.valueOf("c");
            if(f.getPath().equals("."))
                ; //skip, caller should have loaded their default file already.
            else
                params.merge(f);
        }
        
        if(opts.nonOptionArguments().size() > 0) {
            File mainFile = new File(opts.nonOptionArguments().get(0));
            params.setCriteriaFile(mainFile);
        }
        
        if(opts.has("o")) {
            File outputDir = (File) opts.valueOf("o");
            params.setOutputDir(outputDir);
        }
        
        if(opts.has("r")) {
            long seed = ((Long)opts.valueOf("r")).longValue();
            params.setRandomSeed(seed);
        }
        
        if(opts.has("s")) {
            double skip = ((Double)opts.valueOf("s")).doubleValue();
            if(skip < 0)
                skip = 0;
            else
                skip /= 100.0;
            params.setPhase2RandomTractProb(skip);
            params.setPhase2TractSkipProbInit(skip);
        }
        
        if(opts.has("f")) {
            int firstReal = (Integer)opts.valueOf("f");
            params.setFirstRzn(firstReal);
            params.setLastRzn(firstReal);
        }
        if(opts.has("l")) {
            int lastReal = (Integer)opts.valueOf("l");
            params.setLastRzn(lastReal);
        }

        if(opts.has("p")) {
            int phase = (Integer)opts.valueOf("p");
            if(phase < 2)
                throw new DataException("Error: phases 1 and 2 are mandatory.");
            params.setPhase3Skip(phase < 3);
            params.setPhase4Skip(phase < 4);
        }

        if(opts.has("parallel")) {
            // >0 means "use this many"
            // <0 means "reserve this many"
            // =0 mean 1
            // capped at Java's report of number of processors.
            int maxThreads = Runtime.getRuntime().availableProcessors();
            int threads = maxThreads;
            
            Integer obj = (Integer) opts.valueOf("parallel");
            if(obj != null) {
                threads = obj.intValue();
                if(threads <= 0)
                    threads = maxThreads + threads;
                threads = Math.max(1, threads);
                threads = Math.min(threads, maxThreads);
            }
            params.setThreads(threads);
        }
        
        return true;
    }
    
    /**
     * Run census generator from the command line.
     * 
     * @param argv command-line args
     * @throws IOException on any file error
     * @throws JAXBException on any problem parsing XML control files
     */
    public static void main( String argv[]) throws IOException, JAXBException {
        ConflatePumsQueryWithTracts cpt = new ConflatePumsQueryWithTracts();
        
        // Load defaults from last run.
        try {
            cpt.params.merge(LASTRUN_FILE);
        } catch (Exception e) {
            //ignore; file doesn't exist, we don't care
        }
        // Update per user's request.
        if(! mergeArgs(argv, cpt.params))
            return;
        cpt.params.validate();

        
        // =========================== //
        // Configure output location.  //

        File dir = cpt.params.getOutputDir().getCanonicalFile();
        if(dir.exists()) {
            if(dir.isDirectory() == false)
                throw new IOException("Output dir \""+dir+"\" exists but is not a directory.");
        }
        else {
            if(dir.mkdirs() == false)
                throw new IOException("Output dir \""+dir+"\" cannot be created.");
        }
        RGIS.setOutputFolder(cpt.params.getOutputDir());

        
        // =========================== //
        // Configure master logger.    //

        // Build name for log file
        int firstReal = cpt.params.getFirstRzn();
        int lastReal = cpt.params.getLastRzn();
        String logname;
        if(firstReal == lastReal)
            logname = String.format("%s.rzn%03d.log", 
                    ConflatePumsQueryWithTracts.class.getSimpleName(),
                    firstReal);
        else
            logname = String.format("%s.rzn%03d-%03d.log", 
                    ConflatePumsQueryWithTracts.class.getSimpleName(),
                    firstReal, lastReal);
        
        // Log PROGRESS and above to the console.
        // Log same plus DETAIL, RESULT, and INFO to a file.
        // Limit to INFO, as Java logs things down at the FINE levels.
        LogUtil.getRootLogger().setLevel(Level.INFO);
        LogUtil.quietConsole();
        LogUtil.cleanFormat();
        LogUtil.setOutput(new File(RGIS.getOutputFolder(), logname).getCanonicalPath());

        LogUtil.progress(log,
            "Starting "+ConflatePumsQueryWithTracts.class.getSimpleName()
                +"\n  Version "+SVN_REV+" dated "+SVN_DATE
                +"\n  in directory "+new File(".").getCanonicalPath()
                +"\n  with arguements "+Arrays.toString(argv));
        LogUtil.cr(log);

        
        // =========================== //
        // Start running the program. //

        try {
            cpt.run();
        }
        catch(Throwable t) {
            LogUtil.cr(log);
            if(t instanceof AssertionError) {
                log.log(
                    Level.SEVERE,
                    "INTERNAL ERROR!  Our apologies, there's nothing you can do about this.  Please report this error, and include this log file.",
                    t);
            }
            else {
                log.log(
                    Level.SEVERE,
                    "FATAL ERROR!  Our apologies, please include this log file if you report this error.",
                    t);
            }
            System.exit(99);
        }
    }
}
