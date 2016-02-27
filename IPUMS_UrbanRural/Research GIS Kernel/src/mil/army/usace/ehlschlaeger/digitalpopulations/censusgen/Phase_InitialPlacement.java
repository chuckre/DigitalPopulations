package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml.ProgressToy;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;


/**
 * Generate a complete set of households, giving them a plausible initial
 * placement.
 * <P>
 * Is phase 2 of censusgen.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Phase_InitialPlacement {
    protected static Logger log = Logger.getLogger(Phase_InitialPlacement.class.getPackage().getName());
    
    protected Solution soln;
    protected ArrayList<Integer> regionList;
    protected int[] numRealizations2Make;
    
    protected Params params = new Params();
    protected Random random = new Random();

    protected GISClass regionMap;

    /**
     * Build standard instance. You will generally want to call setParams() and
     * setRandomSource(), then go() to run process. Result will be in
     * <tt>soln</tt>.
     * 
     * @param soln
     *            set of archtypes for which we will create realizations
     * @param regionMap
     *            raster map with region ID in each cell. Only needed for
     *            processing attribute maps (i.e. if soln.pstats is not null).
     * @param popDensityMap
     *            raster map of relative population density for each cell.
     *            Regions that are all zero will not receive households.
     * @param numRealizations2Make
     *            number of clones of each archtype to create (i.e. output of
     *            phase 1)
     */
    public Phase_InitialPlacement(Solution soln,
            GISClass regionMap,
            int[] numRealizations2Make) {
        this.soln = soln;
        this.numRealizations2Make = numRealizations2Make;
        this.regionMap = regionMap;
        
        // Extract keys for easy random access.
        regionList = new ArrayList<Integer>(regionMap.makeInventory());
        Collections.sort(regionList);
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
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        random = source;
    }

    /**
     * Perform the process as currently configured.
     * 
     * @throws IOException on any error writing output files
     */
    public void go() {
        int numHouseholdArchtypes = soln.householdArchTypes.length;

        // erase old realization
        for(PumsHousehold house : soln.householdArchTypes)
            house.clearRealizations();

        // tally up goal
        long households2do = 0;
        for( int j = 0; j < numHouseholdArchtypes; j++)
            households2do += numRealizations2Make[ j];
        long householdsDone = soln.getTotalRealizations();

        AttMapHelper attMapHelper = new AttMapHelper(regionMap, regionList, soln.pcons);
        
        // Build header for progress reporting.
        StringOutputStream sos = new StringOutputStream();
        sos.println("Progress; Time");
        //  - capture second line before printing
        StringBuffer buf = new StringBuffer();
        buf.append(" ");
        for( int i = 0; i < soln.stats.size(); i++)
            buf.append(String.format(" %9s", "stat[" + i + "]"));
        buf.append(" = Spread");
        //  - print second line
        String s = buf.toString();
        sos.println(s);
        //  - print "----" exactly the same length
        sos.println(s.replaceAll(".", "-"));
        LogUtil.cr(log);
        LogUtil.progress(log, sos.toString());

        
//        int peopleDone = 0;
        Date startRealizing = new Date();
        ProgressToy prog = new ProgressToy(log, 60, households2do);
        Object[] diagnostic = new Object[1];
        
        while( householdsDone < households2do) {
            int h = -1;
            double bestRatio = Double.MAX_VALUE;
            
            // Find a household "most" in need of another realization
            for( int j = 0; j < 4; j++) {
                // Pick an archtype at random
                int thisH = (int) (Math.floor( numHouseholdArchtypes * random.nextDouble()));
                int firstH = thisH;
                int numRzns, reqdRzns;
                
                // Find an archtype that needs more rzns.
                // (WRZ's patented 'center-termination' loop)
                for(;;) {
                    // 1. Prepare data
                    numRzns = soln.householdArchTypes[ thisH].getNumberRealizations();
                    reqdRzns = numRealizations2Make[ thisH];
                    // 2. Test for completion
                    if(numRzns < reqdRzns)
                        break;
                    // 3. Process data (not required)
                    // 4. Move to next item
                    thisH--;
                    if( thisH < 0)
                        thisH = numHouseholdArchtypes - 1;
                    if(thisH == firstH)
                        // woops, households2do > sum(reqdRzns)
                        throw new RuntimeException("INTERNAL ERROR: No archtypes need more realizations.");
                }
                
                // Keep the one with the lowest % of completed rzns.
                double thisRatio = ((double) numRzns) / reqdRzns;
                if( thisRatio < bestRatio) {
                    h = thisH;
                    bestRatio = thisRatio;
                }
            }
            
            // Find best location for new realization
            double bestFit = Double.MAX_VALUE;
            int bestTract = -1;
            double chanceTractIgnored = params.getPhase2TractSkipProbInit();

            // Only compute if needed, and only compute once per household.
            List<Integer> goodRegions = null;

            // Re-try until a good tract is found.  This will iterate when:
            //  * chanceTractIgnored caused all tracts to be skipped.
            while( bestTract < 0) {
                // Probabilistically choose a random tract, ignore stats.
                if( random.nextDouble() < params.getPhase2RandomTractProb()) {
                    int z = (int) Math.floor(random.nextDouble() * regionList.size());
                    bestTract = regionList.get(z);
                    break;
                }
                
                // Scan all tracts, find the best one.
                else {
                    PumsHousehold archHoh = soln.householdArchTypes[h];
                    
                    // Aye, here's the rub:  createCopy() increases safety, and prevents the
                    // repeated addRzn/removeRzn calls from accumulating error.  But it's
                    // extremely expensive.  With it, 41% of Afghan takes 13 hours, and the
                    // speed continually decreases.  Without it, 100% is done in one hour.
                    //
                    // Rhode Island produces the exact same output both ways, and no-copy was
                    // 2.3 times faster.
                    Solution testSoln = soln;
//                    Solution testSoln = soln.createCopy();   //TODO test again (watch for memory leak); should not be that slow - only called once per hoh

                    // We defer calculating goodRegions until we need it; the
                    // random-skip trick above may obviate the need.
                    if(goodRegions == null) {
                        goodRegions = attMapHelper.validRegions(archHoh, diagnostic);
                        
                        if (goodRegions.isEmpty() || diagnostic[0] != null) {
                            String msg = String.format(
                                "%s eliminated all regions from consideration for %s.",
                                diagnostic[0], archHoh);
                            log.warning(msg);
                            attMapHelper.diagnose(archHoh, log);

                            // Paranoid check: validRegions() must at least
                            // return the list of all regions; if we get an empty
                            // list, something is horribly wrong.
                            if(goodRegions.isEmpty())
                                throw new IllegalStateException("goodRegions is empty due to internal logic bug.");
                        }
                    }

                    // Scan regions for best fit based on the other traits.
                    for(int tract : goodRegions) {
                        
                        // Randomly skip tracts (sacrifice quality for speed)
                        if( random.nextDouble() < chanceTractIgnored)
                            continue;
                        
                        // Probe this tract.
                        testSoln.addRealization(h, tract);
                        double potSpread = testSoln.getSpread();
                        testSoln.removeRealization(h);
                        
                        // Keep best tract.
                        if(bestTract < 0 || potSpread < bestFit) {
                            bestFit = potSpread;
                            bestTract = tract;
                        }
                    }
                }

                // If we must repeat, this will decrease the number of tracts skipped.
                chanceTractIgnored -= params.getPhase2TractSkipProbDelta();
            }

            // Save best household
            soln.addRealization(h, bestTract);
            
            // Update our 'best' stats.
            bestFit = soln.getSpread();
            
            householdsDone++; 
//            peopleDone += soln.householdArchTypes[h].getNumberMembers();


            // Dump stats periodically.
            if(prog.updateProgress(householdsDone)) {
                double pDone = (1.0 * householdsDone) / households2do;
                double secondsSoFar = prog.elapsed();
                double secondsToDo = secondsSoFar * ((1.0 * households2do - householdsDone) / householdsDone);

                String head = String.format("%d%% done: %d of %d households; %.2f min, %.2f min remaining, %.5f random household placement.",
                                  (int) (100 * pDone),
                                  householdsDone, households2do,
                                  secondsSoFar / 60.0, secondsToDo / 60.0,
                                  params.getPhase2RandomTractProb());
                
                // Dump quality stats
                sos = new StringOutputStream();
                sos.println(head);
                sos.print(" ");
                for (int i = 0; i < soln.stats.size(); i++) {
                    sos.format(" %9.2f", Math.sqrt(soln.stats.get(i).spread(soln.goals.get(i))));
                }
                sos.format(" = %.2f", Math.sqrt(bestFit));
                LogUtil.progress(log, sos.toString());
            }
        }

        // Dump final stats.
        Date nTime = new Date();
        double secondsSoFar = (nTime.getTime() - startRealizing.getTime()) / (1000.0);
        LogUtil.progress(log, "All done: %d of %d households; %.2f min.",
                          householdsDone, households2do,
                          secondsSoFar / 60.0);
        
        sos = new StringOutputStream();
        sos.print(" ");
        for (int i = 0; i < soln.stats.size(); i++)
            sos.format(" %9.2f", Math.sqrt(soln.stats.get(i).spread(soln.goals.get(i))));
        sos.format(" = %.2f", Math.sqrt(soln.getSpread()));
        LogUtil.progress(log, sos.toString());
    }
}
