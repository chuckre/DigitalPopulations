package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import mil.army.usace.ehlschlaeger.digitalpopulations.ConstrainedRealizer;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.digitalpopulations.Realizer;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.ClusterSpec;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.FittingCriteria;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.MatchSpec;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.TraitRefElement;
import mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml.ProgressToy;
import mil.army.usace.ehlschlaeger.digitalpopulations.io.HohRznWriter;
import mil.army.usace.ehlschlaeger.digitalpopulations.io.PumsLoader;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointGridIndex;
import mil.army.usace.ehlschlaeger.rgik.core.PointSemiVariogram_GridIndex;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.MyRandom;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;
import mil.army.usace.ehlschlaeger.rgik.util.TimeTracker;

import org.xml.sax.SAXException;



/**
 * Select precise easting/northing values for households based on clustering
 * criteria. Attempts to mimic reality where people tend to live closer to
 * people with similar attributes.
 * <P>
 * Is phase 4 of censusgen.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 * @author Yizhao Gao
 */
public class Phase_LocatePrecisely_GridIndex {
    /** Name of params key that switches on pre-cluster output. */
    public static final String PARAM_SAVEBOTHENDS = "phase4_save_both_ends";
    
    /** File name tag we use on result files saved before we begin. */
    public static final String PRECLUSTER_LABEL = "precluster";
    /** File name tag we use on result files saved after we're done. */
    public static final String POSTCLUSTER_LABEL = "postcluster";

    protected static Logger log = Logger.getLogger(Phase_LocatePrecisely_GridIndex.class.getPackage().getName());

    /** We always use this many lags. */
    protected static final int DEFAULT_NUM_LAGS = 5;
    
    
    // INPUT DATA //
    protected Params params = new Params();
    protected Realizer realizer;
    
    protected int realizationNum;
    protected List<PumsHousehold> households;
    protected GISClass regionMap;
    protected GISData bounds;
    protected GISLattice popDensityMap;
    protected Random random = new Random();

    protected CSVTableNoSwing householdSchema;
    protected CSVTableNoSwing populationSchema;

    
    // INTERMEDIATE DATA //
    /** Index of realizations by location. */
    protected GISPointGridIndex<PumsHouseholdRealization> rznIndex;
    /** Index of realizations by tract number. */
    protected RegionIndex regionIndex;

    protected LinkedHashMap<Trait,TraitRefElement> locationSpecs;
    protected List<? extends PointConstraint> constraints;

    /** Starting PSVs, for comparison to final PSVs. */
    ArrayList<PointSpatialStatistic> origPss;
    /** Quality statistics for rznIndex. */
    ArrayList<PointSpatialStatistic> stats;
    /** User's declaration of "good enough". */
    ArrayList<PointSpatialStatistic> goals;

    double GRIDSIZE = 1000.0;
    
    // OUTPUT DATA //
    //  -> none; results are written to disk

    /**
     * Build standard instance. You will generally want to call setParams() and
     * setRandomSource(), then call go() to run and generate output files.
     * 
     * @param realizationNum
     *            number of realization currently being generated. Copied into
     *            output file names.
     * @param households
     *            list of households that need to be placed
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param popDensityMap
     *            raster map of relative population density for each cell. Cells
     *            with value '0' will receive no households; value '2' will
     *            receive double the households as value '1'. If null, all cells
     *            receive equal population.
     * @param criteria
     *            criteria for placing households within tracts, in the order of
     *            importance
     */
    public Phase_LocatePrecisely_GridIndex(
            int realizationNum,
            PumsHousehold[] households,
            GISClass regionMap,
            GISLattice popDensityMap,
            List<? extends PointConstraint> constraints,
            LinkedHashMap<Trait,TraitRefElement> criteria) {
        this.realizationNum = realizationNum;
        this.households = Arrays.asList(households);
        this.regionMap = regionMap;
        this.bounds = new GISData(regionMap);
        this.popDensityMap = popDensityMap;
        this.constraints = constraints;
        this.locationSpecs = criteria;

        // Capture the archtype schemas.
        for(PumsHousehold house : households) {
            if(house != null) {
                // Grab first households schema
                if(householdSchema == null)
                    householdSchema = house.getSchema();
                
                PumsPopulation[] peops = house.getMembersOfHousehold();
                if(peops != null && peops.length > 0) {
                    // Grab first population schema
                    if(populationSchema == null)
                        populationSchema = peops[0].getSchema();
                }
                if(householdSchema != null && populationSchema != null)
                    break;
            }
        }
    }

    /**
     * Build an instance for data that is partially processed. Intended for use
     * by main(), which expects data that has been run through the prior phases
     * of Digital Populations.
     * 
     * @param realizationNum
     *            number of realization currently being generated. Copied into
     *            output file names.
     * @param households
     *            list of households that need to be placed, and their initial
     *            location. Households will stay in their tract, but their
     *            precise location may move to satisfy 'criteria'.
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param criteria
     *            criteria for placing households within tracts, in the order of
     *            importance
     */
    public Phase_LocatePrecisely_GridIndex(
            int realizationNum,
            List<PumsHouseholdRealization> households,
            GISClass regionMap,
            List<? extends PointConstraint> constraints,
            LinkedHashMap<Trait, TraitRefElement> criteria) {
        this.realizationNum = realizationNum;
        this.regionMap = regionMap;
        this.bounds = new GISData(regionMap);
        this.constraints = constraints;
        this.locationSpecs = criteria;

        // Capture the attribute schemas.
        for(PumsHouseholdRealization house : households) {
            if(house != null) {
                // Grab first households schema
                if(householdSchema == null)
                    householdSchema = house.getParentHousehold().getSchema();
                
                PumsPopulation[] peops = house.getParentHousehold().getMembersOfHousehold();
                if(peops != null && peops.length > 0) {
                    // Grab first population schema
                    if(populationSchema == null)
                        populationSchema = peops[0].getSchema();
                }
                if(householdSchema != null && populationSchema != null)
                    break;
            }
        }
        
        // Add realizations to indices.
        this.rznIndex = new GISPointGridIndex<PumsHouseholdRealization>(bounds, GRIDSIZE, 10);
        this.regionIndex = new RegionIndex();
        for(PumsHouseholdRealization house : households) {
            rznIndex.addPoint(house);
            regionIndex.add(regionMap.getCellValue(house.getEasting(), house.getNorthing()), house);
        }
    }

    /**
     * @return collection of realized and adjusted households. Not valid until
     *         go() has been called.
     */
 //   public Iterable<PumsHouseholdRealization> getResults() {
   //     return rznIndex;
    //}
    
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
     * Install run-time configuration.
     * 
     * @param params current set of run-time parameters
     */
    public void setParams(Params params) {
        this.params = params;

        if(!params.getWriteAllHohFields()) {
            log.warning("Paramter '"+Params.WRITE_ALL_HOH_FIELDS+"' must be enabled for phase 4.  Enabling now.");
            params.setWriteAllHohFields(true);
        }
        if(!params.getWriteAllPopFields()) {
            log.warning("Paramter '"+Params.WRITE_ALL_POP_FIELDS+"' must be enabled for phase 4.  Enabling now.");
            params.setWriteAllPopFields(true);
        }
    }
    
    /**
     * Install a custom realizer. In this class, this is only used to generate
     * easting/northing values when writing output files. If none is provided
     * here, we will generate one from our inputs.
     * 
     * @param rzr
     *            new realizer
     */
    public void setRealizer(Realizer rzr) {
        this.realizer = rzr;
    }

    /**
     * Perform the process as currently configured.
     * 
     * @throws IOException on any error writing output files
     */
    public void go() throws IOException {
        // true to save incoming hoh as well as final hoh arrangement for
        // comparison. false to purge older when saving final.
        boolean saveBothEnds = params.getBoolean(PARAM_SAVEBOTHENDS, true);
        
        // Create an initial layout if needed.
        if(rznIndex == null || regionIndex == null) {
            LogUtil.progress(log, "ConflatePumsQueryWithTracts: realizing households");
            realizeHouseholds();
            TimeTracker.finished("Realizing households");
            
            LogUtil.cr(log);
            LogUtil.progress(log, "ConflatePumsQueryWithTracts: saving intermediate household locations");
            if(saveBothEnds) {
                // Label incoming hoh; we'll preserve this.
                writeFileSet(PRECLUSTER_LABEL, rznIndex, null, true);
            }
            else {
                // Use generic label; we'll delete when saving final.
 //               writeFileSet("intermediate", rznIndex, null, true);
            }
            TimeTracker.finished("Writing files");
        }

        LogUtil.cr(log);
        LogUtil.progress(log, "Building goal statistics.");
        prepareStats();
        
        LogUtil.cr(log);
        LogUtil.progress(log, "Optimizing clustering of households within regions.");
        doCluster();
        
        TimeTracker.finished("Phase 4");

        // Save final file(s).
        LogUtil.cr(log);
        LogUtil.progress(log, "ConflatePumsQueryWithTracts: saving final household locations ...");
        if(saveBothEnds) {
            // Save results, preserve precluster.
            writeFileSet(POSTCLUSTER_LABEL, rznIndex, null, false);
        }
        else {
            // Save final results.
            writeFileSet(null, rznIndex, null, true);
        }
        TimeTracker.finished("Writing files");
    }

    /**
     * Construct initial arrangement of households. This is like
     * {@link Phase_InitialPlacement}, only it assigns precise easting/northing
     * values within regions.
     */
    protected void realizeHouseholds() {
        this.rznIndex = new GISPointGridIndex<PumsHouseholdRealization>(bounds, GRIDSIZE, 10);
        this.regionIndex = new RegionIndex();
        
        if(realizer == null) {
            realizer = new ConstrainedRealizer(
                regionMap,
                popDensityMap,
                constraints);
            realizer.setRandomSource(random);
        }
        
        Iterator<PumsHouseholdRealization> ri = realizer.iterate(households.iterator());
        while(ri.hasNext()) {
            PumsHouseholdRealization rzn = ri.next();
            int tract = regionMap.getCellValue(rzn.getEasting(), rzn.getNorthing());
            rznIndex.addPoint(rzn);
            regionIndex.add(tract, rzn);
        }
    }
    
    
    /**
     * Build the statistic objects required by doCluster().
     */
    protected void prepareStats() {
        long numLags = params.getLong("phase4_num_lags", DEFAULT_NUM_LAGS);

        // Prepare stats containers.
        origPss = new ArrayList<PointSpatialStatistic>();
        stats = new ArrayList<PointSpatialStatistic>();
        goals = new ArrayList<PointSpatialStatistic>();
        
        // Build all our PSV and goals.
        for (Trait trait : locationSpecs.keySet()) {
            // 'trait' says how to compute stats; 'ref' says how to use trait.
            TraitRefElement ref = locationSpecs.get(trait);
            
            if(ref instanceof ClusterSpec) {
                // Generate clustering stats from a trait.
                log.info("  Building "+ref);
                
                ClusterSpec spec = (ClusterSpec)ref;
                PumsTraitGetter valueGetter = new PumsTraitGetter((PumsTrait) trait, householdSchema, populationSchema);
                PointSemiVariogram_GridIndex psv = PointSemiVariogram_GridIndex.createStat(
                    spec.distance/numLags, spec.distance,
                    rznIndex,
                    valueGetter);
                
                origPss.add(psv);
                stats.add(psv.createCopy());
                goals.add(reduce(psv, spec.reduction));
            }
            else if(ref.getClass() == MatchSpec.class){
                // MatchSpec is only use for attribute maps, which are
                // considered constraints. Since we were passed a list of
                // constraints to the constructor, we don't need to process
                // these elements.
            }
            else {
                throw new DataException("Unknown position-rules element: "+ref);
            }
        }
    }

    /**
     * Main method of the algorithm.
     * 
     * @throws IOException
     *             on any error logging status
     */
    protected void doCluster() throws IOException {
        assert regionIndex.getNumRegions() > 0;

        // Collect sorted list of index codes.
        List<Integer> regionIDs = new ArrayList<Integer>(regionIndex.getIDs());
        Collections.sort(regionIDs);
        
        // Calculate quality of current arrangement.
        double oldSpread = 0;
        for(int i=0; i<goals.size(); i++) {
            double s = stats.get(i).spread(goals.get(i));
            oldSpread += s*s;
        }
        oldSpread = Math.sqrt(oldSpread);

        // Log initial statistics.
        LogUtil.cr(log);
        LogUtil.result(log, "INITIAL COVARIANCE OF LOCATIONS:");
        LogUtil.cr(log);
        for(int i=0; i<stats.size(); i++) {
            PointSpatialStatistic pss = stats.get(i);
            StringOutputStream sos = new StringOutputStream();
            sos.format("clus[%d]: ", i);
            pss.print(sos);
            LogUtil.result(log, sos.toString());
            LogUtil.cr(log);
        }
        
        LogUtil.cr(log);
        LogUtil.result(log, "GOAL COVARIANCE:");
        LogUtil.cr(log);
        for(int i=0; i<goals.size(); i++) {
            PointSpatialStatistic pss = goals.get(i);
            StringOutputStream sos = new StringOutputStream();
            sos.format("clus[%d]: ", i);
            pss.print(sos);
            LogUtil.result(log, sos.toString());
            LogUtil.cr(log);
        }

        // Determine amount of time we can spend.
        long tMainStart = System.nanoTime();
        long tMainAbort = -1;
        if(params.getPhase4TimeLimit() > 0)
            tMainAbort = tMainStart + (long)(params.getPhase4TimeLimit() * 60 * 1e9);

        // Determine max number of consecutive failures allowed.
        long maxFails = rznIndex.getNumberPoints();

        // Determine when to save intermediate results.  (Use same timer as for phase 3.)
        long tNextSave = tMainStart + (long)(params.getPhase3SaveIntermediate() * 60 * 1e9);
     
        // while not good enough:
        //   pick region at random
        //   pick two points at random
        //   swap them, update pss
        //   if pss reduced, keep
        //   else swap back

        printRunHeader(goals);
        printRunProgress(oldSpread, 0, 0, 0);
        
        ProgressToy prog = new ProgressToy(log, 60, 0, "Moved");
        long attempts=0, fails = 0;
        long moves=0, movesAtLastSave = 0;
        main_loop: for(;;) {
            // Pick region at random.
            int tractID = MyRandom.pick(random, regionIDs);
            List<PumsHouseholdRealization> tractRzns = regionIndex.getRzns(tractID);
            // From region, pick two households at random.
            if(random == null)
                System.out.println("random");
            if(tractRzns==null)
                System.out.println("tractRzns");
            int p1 = random.nextInt(tractRzns.size());
            PumsHouseholdRealization h1 = tractRzns.get(p1);
            int p2 = random.nextInt(tractRzns.size());
            PumsHouseholdRealization h2 = tractRzns.get(p2);

            // Verify that new locations satisfy all point constraints. If ANY
            // constraint rejects either house, we cannot swap.
            boolean forbid = false;
            //  - allows() requires hoh to have a location, so we swap here, then swap back when done
            swapLocation(h1, h2);
            for(PointConstraint c : constraints) {
                if(c.allows(h1) == PointConstraint.FORBID || c.allows(h2) == PointConstraint.FORBID) {
                    forbid = true;
                    break;
                }
            }
            swapLocation(h1, h2);

            //  - check result only after hoh are swapped back
            if(forbid) {
                fails += 1;
                continue;
            }
            
            // Swap locations of households, and update statistics.
            ArrayList<PointSpatialStatistic> swapd = new ArrayList<PointSpatialStatistic>();
            for(PointSpatialStatistic pss : stats) {
                PointSpatialStatistic neu = (PointSpatialStatistic) pss.createCopy();
                neu.modifySS4RemovedPt(h1, 0);
                neu.modifySS4RemovedPt(h2, 0);
                swapd.add(neu);
            }
            this.rznIndex.swapPoints(h1, h2);
            for(PointSpatialStatistic neu : swapd) {
                neu.modifySS4NewPt(h1, 0);
                neu.modifySS4NewPt(h2, 0);
            }

            // Calculate quality of new arrangement.
            double neuSpread = 0;
            for(int i=0; i<goals.size(); i++) {
                double s = swapd.get(i).spread(goals.get(i));
                neuSpread += s*s;
            }
            neuSpread = Math.sqrt(neuSpread);

            // If quality improved, keep.
            if(neuSpread < oldSpread) {
                stats = swapd;
                oldSpread = neuSpread;
                fails = 0;
                moves += 1;
            }
            //   else swap back
            else {
            	this.rznIndex.swapPoints(h1, h2);
                fails += 1;
            }
            attempts += 1;
            
            long tNow = System.nanoTime();
            double totalSeconds = (tNow-tMainStart) / 1e9;
            boolean printed = false;
            if(prog.updateProgress(moves)) {
                printRunProgress(oldSpread, totalSeconds, attempts, moves);
                // Prevent this line from being duplicated below.
                printed = true;
            }
            
            // Too many failures, assume no reducements are available.
            if(fails > maxFails) {
                if(!printed)
                    printRunProgress(oldSpread, totalSeconds, attempts, moves);
                LogUtil.progress(log, "Giving up:  Too many failures.");
                break main_loop;
            }
            // Abort run after time limit if requested.
            if(tMainAbort > 0 && tNow > tMainAbort) {
                if(!printed)
                    printRunProgress(oldSpread, totalSeconds, attempts, moves);
                LogUtil.progress(log, "Giving up:  Time limit has been reached.");
                break main_loop;
            }
            // Save intermediate results periodically.
            if(tNow > tNextSave) {
                // Save only if something has changed.
                if(moves != movesAtLastSave) {
                    movesAtLastSave = moves;

                    LogUtil.progress(log, "Long run, saving intermediate data set.");
                    try {
                        // If saveBothEnds, then we're going to leave
                        // "intermediate" lying around too. We're not smart
                        // enough to delete "intermediate" but preserve
                        // "precluster".
                        boolean saveBothEnds = params.getBoolean(PARAM_SAVEBOTHENDS, true);
                        writeFileSet("intermediate", rznIndex, null, !saveBothEnds);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Unable to save intermediate data, continuing anyway.", e);
                    }
                    LogUtil.cr(log);
                    LogUtil.result(log, "INTERMEDIATE COVARIANCE OF LOCATIONS:");
                    LogUtil.cr(log);
                    for(int i=0; i<stats.size(); i++) {
                        PointSpatialStatistic pss = stats.get(i);
                        PointSpatialStatistic orig = origPss.get(i);
                        PointSpatialStatistic goal= goals.get(i);
                        
                        StringOutputStream sos = new StringOutputStream();
                        sos.format("clus[%d]: ", i);
                        if(pss instanceof PointSemiVariogram_GridIndex)
                            ((PointSemiVariogram_GridIndex) pss).printComparison(sos, (PointSemiVariogram_GridIndex)orig, (PointSemiVariogram_GridIndex)goal);
                        else
                            pss.print(sos);
                        LogUtil.result(log, sos.toString());
                        LogUtil.cr(log);
                    }
                }
                // Save again precisely one increment from now. Ignore
                // however late we are performing this save, and also
                // ignore however long this save took.
                tNextSave = System.nanoTime() + (long)(params.getPhase3SaveIntermediate() * 60 * 1e9);
            }
        }
        
        // Log final statistics.
        LogUtil.cr(log);
        LogUtil.result(log, "FINAL QUALITY OF LOCATIONS:");
        LogUtil.cr(log);
        for(int i=0; i<stats.size(); i++) {
            PointSpatialStatistic pss = stats.get(i);
            PointSpatialStatistic orig = origPss.get(i);
            PointSpatialStatistic goal= goals.get(i);
            
            StringOutputStream sos = new StringOutputStream();
            sos.format("clus[%d]: ", i);
            if(pss instanceof PointSemiVariogram_GridIndex)
                ((PointSemiVariogram_GridIndex) pss).printComparison(sos, (PointSemiVariogram_GridIndex)orig, (PointSemiVariogram_GridIndex)goal);
            else
                pss.print(sos);
            LogUtil.result(log, sos.toString());
            LogUtil.cr(log);
        }
    }

    /**
     * Print to the log a header line for our progress reports.
     * 
     * @param goals
     */
    protected void printRunHeader(ArrayList<PointSpatialStatistic> goals) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("%8s %11s %6s ", "Moves", "Attempts", "Minute"));
        for (int i = 0; i < goals.size(); i++)
            buf.append(String.format("%10s ", "clus[" + i + "]"));
        buf.append(String.format("%10s", "= Spread  "));
        String st = buf.toString();

        LogUtil.cr(log);
        LogUtil.progress(log, st);
        st = st.replaceAll(".", "-");
        LogUtil.progress(log, st);
    }

    /**
     * Print to the log our current progress.
     * 
     * @param spread
     * @param totalSeconds
     * @param attempts
     * @param moves
     */
    protected void printRunProgress(double spread, double totalSeconds,
            long attempts, long moves) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("%8d %11d %6.1f ", moves, attempts, totalSeconds/60.0));
        
        for(int i=0; i<goals.size(); i++) {
            double s = stats.get(i).spread(goals.get(i));
            buf.append(String.format("%10.8f ", s));
        }
        buf.append(String.format("%10.8f", spread));
        LogUtil.progress(log, buf.toString());
    }

    /**
     * Write all required output files.
     * 
     * @param nameNote
     *            which version of file, as a note to the user. Will be embedded
     *            in file names. Suggestions: "preliminary", "intermediate",
     *            "phase3", etc. Use null if no note is desired (i.e. is final
     *            version of file.)
     * @param houses
     *            list of households and attached members (location and
     *            metadata) to write
     * @param filter
     *            selects a subset of households to write. null will write all
     *            given households.
     * @param flushOlder
     *            'true' to delete older versions of these files, or 'false' to
     *            leave them. This should normally be 'true'; only use 'false'
     *            for testing and debugging.
     * 
     * @throws IOException
     *             on any error creating files. If there's a problem renaming
     *             files, it will only appear in the log.
     */
    protected void writeFileSet(
            String nameNote,
            GISPointGridIndex<PumsHouseholdRealization> houses,
            PumsQuery filter, boolean flushOlder)
    throws IOException {
        HohRznWriter writer = new HohRznWriter(RGIS.getOutputFolder());
        boolean writePop = (populationSchema != null);
        
        if(filter != null) {
            String newNote = "(filtered)" + ObjectUtil.nz(nameNote);
            
            writer.writeFileSet(
                realizationNum, newNote,
                filter.iterateRzn(houses.iterator()),
                flushOlder, writePop,
                params.getWriteAllHohFields(), params.getWriteAllPopFields(),
                null, null);
        }

        // Now we can write the full set of households.
        writer.writeFileSet(
            realizationNum, nameNote,
            houses.iterator(),
            flushOlder, writePop,
            params.getWriteAllHohFields(), params.getWriteAllPopFields(),
            null, null);
    }
    
    /**
     * <P>
     * Build a goal PSV that's reduced from an existing PSV. Inertia values will
     * be scaled by given amount progressively: the first lag is reduced by the
     * given amount, while points past the final lag are not reduced a all. Lags
     * in between are reduced by a proportional ammount. Note that since lags
     * cover a range of distances, they will actually be reduced by the average
     * value appropriate for their range.
     * <P>
     * For example, if a reduction of 20% is requested on a PSV with 5 lags, the
     * actual reductions will be:
     * 
     * <PRE>
     *   lag[0]  18%
     *   lag[1]  14%
     *   lag[2]  10%
     *   lag[3]   6%
     *   lag[4]   2%
     * </PRE>
     * 
     * lag[0] needs to be reduced by 20% at distance zero, and 16% at its outer
     * limit. Since a lag only contains a single set of statistics, it is simply
     * reduced by the average amount of 18%.
     * 
     * @param psv
     *            PSV to reduce
     * @param reduction
     *            percentage reduction to apply at psv.getMaximumDistance()
     * 
     * @return new PSV with modified inertia values
     */
    protected static PointSemiVariogram_GridIndex reduce(PointSemiVariogram_GridIndex psv, double reduction) {
    	PointSemiVariogram_GridIndex goal = PointSemiVariogram_GridIndex.create(psv.getLagDistance(), psv.getMaximumDistance(), null);
        
        reduction /= 100.0;
        double delta = reduction / psv.getNumberLags();
        double first = reduction - delta/2;
        
        for (int i = 0; i < psv.getNumberLags(); i++) {
            double scale = first - (i*delta);
            goal.setInertiaValue(i, (1-scale) * psv.getInertiaValue(i));
        }
        
        return goal;
    }

    /**
     * Swap the geographic locations (easting and northing) of two households.
     * 
     * @param h1
     * @param h2
     */
    protected static void swapLocation(PumsHouseholdRealization h1, PumsHouseholdRealization h2) {
        double e1 = h1.getEasting();
        double n1 = h1.getNorthing();
        h1.setEasting(h2.getEasting());
        h1.setNorthing(h2.getNorthing());
        h2.setEasting(e1);
        h2.setNorthing(n1);
    }

    /**
     * Run this phase on the saved results of the prior phase (class
     * {@link Phase_OptimizeRegions}). WARNING: Do not use our
     * "rzn###-households.csv" file naming scheme; these may be deleted when the
     * new results are saved.
     * 
     * @param args
     * 
     * @throws JAXBException
     * @throws IOException
     * @throws SAXException
     */
    public static void main(String[] args) throws JAXBException, IOException, SAXException {
        // args: -c config -r random --xCol n --yCol n --uidCol n --hohPopCol n fcFile hohFile popFile
        //       -o output-base
        if(args.length > 0)
            throw new DataException("This is just a test app; all args are hard-coded.");
        
        //TODO: command-line args
        // Should try to parse hohFile for rzn number so output file has correct name.
        // Also need to tweak name so we don't overwrite input.
        
        // We only do one rzn at a time, so rznNum can be zero.
        int rznNum = 0;
        File projDir = new File("C:/Users/Bill/Documents/Projects/DigitalPopulations/workspace/Honduras_021111");
        File hohFile = new File(projDir, String.format("run/phase4-rzn%03d-households(precluster).csv", rznNum));
        File popFile = new File(projDir, String.format("run/phase4-rzn%03d-population(precluster).csv", rznNum));
        File fcFile  = new File(projDir, "data/goal_relationship_map.dprxml");

        FittingCriteria fc = FittingCriteria.loadFile(fcFile, null);
        DataPreparer gen = new DataPreparer(fc);
        
        // DP can load household archtypes, but we need realizations.
        PumsLoader pl = new PumsLoader();
        ArrayList<PumsHouseholdRealization> hohs = pl.loadPumsHouseholdRzns(hohFile, rznNum, "x", "y", "uid");
        List<PumsPopulation> pops = pl.loadPumsPopulation(popFile, "household");
        pl.populateHouseholdRzns(hohs, pops, null);

        Params params = new Params();
        params.set(PARAM_SAVEBOTHENDS, true);  // force true; always preserve user's files
        params.setWriteAllHohFields(true);  // disable these if we load user's params file
        params.setWriteAllPopFields(true);
        
        Phase_LocatePrecisely_GridIndex phase = new Phase_LocatePrecisely_GridIndex(
                rznNum,
                hohs,
                gen.getPrimaryRegion().map,
                gen.makeConstraints(),
                fc.traitCluster);
        phase.setParams(params);
//        phase.setRandomSource(random);
        
        phase.go();
    }
}
