package mil.army.usace.censusgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import mil.army.usace.ehlschlaeger.digitalpopulations.ConstrainedRealizer;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.digitalpopulations.Realizer;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.FittingCriteria;
import mil.army.usace.ehlschlaeger.digitalpopulations.io.HohRznWriter;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.statistics.Count;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.apache.commons.collections.MapUtils;


/**
 * This is ConflatePumsQueryWithTracts, customized to support conditional
 * simulation.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class CensusGenCS extends mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.ConflatePumsQueryWithTracts {
    private static final long serialVersionUID = 1;
    
    /** CS specs from csv file. */
    protected CSTableUtil csUtil;
    /** Realizations constructed to match csTable. */
    protected List<PumsHouseholdRealizationCS> csRzns;
    
    protected transient Realizer realizer = null;

    
    public CensusGenCS() {
    }
    
    /**
     * Create a clone of this object sufficient for running runOne() in parallel.
     * Mutable data is cloned; reference data is shared.
     * 
     * @return copy of this instance
     */
    @Override
    public CensusGenCS createCopy() {
        CensusGenCS obj2 = new CensusGenCS();
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

        obj2.csUtil = this.csUtil;
        obj2.csRzns = this.csRzns;
        
        return obj2;
    }
    
    /**
     * Load the conditional simulation table before we start running.
     */
    @Override
    public void prepareData(FittingCriteria crit) throws IOException {
        super.prepareData(crit);

        // Load CS specs table directly.
        File file = getCSFile();
        if(file == null)
            this.csUtil = null;
        else {
            try {
                // If phase 2 places hoh randomly, CS will select random hohs.
                if(params.getPhase2RandomTractProb() > 0 || params.getPhase2TractSkipProbInit() > 0)
                    log.warning("Conditional simulation works best when phase 2 skip params are zero.");

                // Load CS file.
                CSVTable csTable = new CSVTable(file.getAbsolutePath());
                this.csUtil = new CSTableUtil(csTable, primaryRegion.map);
                
                LogUtil.cr(log);
                LogUtil.progress(log, "Conditional simulation activated using %s", file);
                diagnoseCS();
            } catch (IOException e) {
                throw new DataException("Can't load condsim_file "+params.get("condsim_file"), e);
            }
        }
    }
    
    /**
     * Same as super.phase2, allowing for our hidden rzns.
     */
    @Override
    protected void phase2_makeInitialPlacement(long seed) {
        // Pull seeds now.
        long csrSeed = random.nextLong();
        
        // Sort rzns into tracts, as usual.
        super.phase2_makeInitialPlacement(seed);

        // Realize concrete households.
        //  - Grab rzns sorted above, and move and rewrite them as needed.
        //  - Can't do this at the start of phase3, it might be disabled.
        LogUtil.cr(log);
        if(csUtil == null) {
            csRzns = null;
            LogUtil.progress(log, "Phase 2 extension (conditional simulation) skipped - no CS table provided.");
        }
        else {
            CSRealizer csr = new CSRealizer(csUtil, new Random(csrSeed), soln);
            csRzns = csr.realizeHouseholdsCS();
            LogUtil.progress(log, "Phase 2 extension (conditional simulation) complete.");
        }
        LogUtil.cr(log);
    }
    
    /**
     * Verify that the quantities loaded into trait 0 (absolute households)
     * match where the archtypes think they have rzns.
     * <P>
     * Same as super.validateTracts(), but allows for our hidden rzns.
     */
    @Override
    protected void validateTracts() {
        // DEV NOTE:
        // This is mostly a copy of super.validateTracts, except for the
        // if(csRzns) block below.
        
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

        // Update counts with our hiddent concrete rzns.
        // super.validateTracts will fail because we've hidden these rzns, so
        // we clone that function, then patch counts here.
        if(csRzns != null) {
            for(PumsHouseholdRealizationCS rzn : csRzns) {
                int tract = primaryRegion.map.getCellValue(rzn.getEasting(), rzn.getNorthing());
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

    //
    // phase3_findBestPlacement:
    //
    // Phase 3 needs no tweaking; concrete rzns have been moved out of the
    // Solution, and will not be visible to that phase. However, we overloaded
    // writeRealizations, so the concrete rzns *will* appear in the output file.
    //

    /**
     * Same as super.phase4, allowing for our hidden rzns.
     */
    @Override
    protected void phase4_locatePrecisely(int realizationNum, long seed) throws IOException {
        // Run phase 4.
        CensusGenCS_Phase4 p4 = new CensusGenCS_Phase4(
            realizationNum,
            soln.householdArchTypes,
            primaryRegion.map,
            popDensityMap,
            soln.pcons,
            fitCrit.traitCluster,
            csRzns);
        p4.setParams(params);
        p4.setRandomSource(new Random(seed));
        p4.go();
    }
    
    // Copied from Phase_OptimizeRegion
    @Override
    public void writeFileSet(int runNumber, String nameNote,
            PumsQuery pumsQuery) throws IOException {
        // Realize archtypes and assign locations.
        if(realizer == null) {
            // Preserve realizer for every call to writeFileSet.
            realizer = new ConstrainedRealizer(primaryRegion.map, popDensityMap, soln.pcons);
            realizer.setRandomSource(random);
        }

        String hohKeyCol = fitCrit.relationship.households.key;
        String popHohCol = null;
        if(fitCrit.relationship.population != null && !ObjectUtil.isBlank(fitCrit.relationship.population.table))
            popHohCol  = fitCrit.relationship.population.household;
        
        // I built that nifty iterator system so I wouldn't have to do this ..
        // but it turns out the best way to preserve the generated easting and
        // northing values is to generate and capture the whole list.
        // If we have enough memory for phase 4, we have enough memory for this.
        ArrayList<PumsHouseholdRealization> hohs = ObjectUtil.list(realizer.iterate(
            Arrays.asList(soln.householdArchTypes).iterator()));
        
        // Write filtered set first.
        if(pumsQuery != null) {
            String newNote = "(filtered)" + ObjectUtil.nz(nameNote);

            CSWriter writer = new CSWriter(RGIS.getOutputFolder(), csRzns, pumsQuery);
            writer.writeFileSet(
                runNumber, newNote,
                pumsQuery.iterateRzn(hohs.iterator()),
                true,
                false,
                params.getWriteAllHohFields(), params.getWriteAllPopFields(),
                hohKeyCol, popHohCol);
        }
        
        // Now write complete set.
        CSWriter writer = new CSWriter(RGIS.getOutputFolder(), csRzns, null);
        writer.writeFileSet(
            runNumber, nameNote,
            hohs.iterator(),
            true,
            true,
            params.getWriteAllHohFields(), params.getWriteAllPopFields(),
            hohKeyCol, popHohCol);
    }
    
    public File getCSFile() throws FileNotFoundException {
        File inputsDir = params.getOutputDir();
        String csPath = params.get("condsim_file");
        File csFile = null;
        
        if(! ObjectUtil.isBlank(csPath)) {
            // Find best file for given path.
            csPath = FileUtil.resolve(inputsDir, csPath).getAbsolutePath();
            csFile = CSVTable.findFile(csPath);
        }
        return csFile;
    }

    /**
     * Prints counts of CS records per tract. User can examine this when CS runs
     * out of households to assign to CS records.
     */
    protected void diagnoseCS() {
        Map<Integer, Integer> csCounts = csUtil.count();
        int keyCol = primaryRegion.table.findColumn(primaryRegion.spec.key);
        int hohCol = primaryRegion.table.findColumn(primaryRegion.spec.households);

        StringBuffer buf = new StringBuffer();
        buf.append("\nHousehold counts:\n");
        buf.append("Tract  Region_Table CS_Table\n");
        for(int row=0; row < primaryRegion.table.getRowCount(); row++) {
            String s = primaryRegion.table.getStringAt(row, keyCol);
            int tract = Integer.parseInt(s);
            s = primaryRegion.table.getStringAt(row, hohCol);
            int count = Integer.parseInt(s);
            
            buf.append(String.format("%6d    %6d     %6d\n", tract, count, MapUtils.getInteger(csCounts, tract, 0)));
        }

        LogUtil.result(log, buf);
    }
    

    
    /**
     * Run census generator from the command line.
     * 
     * @param argv command-line args
     * @throws IOException on any file error
     * @throws JAXBException on any problem parsing XML control files
     */
    public static void main( String argv[]) throws IOException, JAXBException {
        CensusGenCS cg = new CensusGenCS();
        
        // Load defaults from last run.
        try {
            cg.params.merge(LASTRUN_FILE);
        } catch (Exception e) {
            //ignore; file doesn't exist, we don't care
        }
        // Update per user's request.
        if(! mergeArgs(argv, cg.params))
            return;
        cg.params.validate();

        
        // =========================== //
        // Configure master logger.    //

        // Build name for log file
        int firstReal = cg.params.getFirstRzn();
        int lastReal = cg.params.getLastRzn();
        String logname;
        if(firstReal == lastReal)
            logname = String.format("%s.rzn%03d.log", 
                    cg.getClass().getSimpleName(),
                    firstReal);
        else
            logname = String.format("%s.rzn%03d-%03d.log", 
                    cg.getClass().getSimpleName(),
                    firstReal, lastReal);
        
        // Limit to INFO, as Java logs things down at the FINE levels.
        LogUtil.getRootLogger().setLevel(Level.INFO);
        LogUtil.quietConsole();
        LogUtil.cleanFormat();
        LogUtil.setOutput(new File(RGIS.getOutputFolder(), logname).getCanonicalPath());

        LogUtil.progress(log,
            "Starting "+cg.getClass().getSimpleName()+" [CERL private version]"
                +"\n  Version "+SVN_REV+" dated "+SVN_DATE
                +"\n  in directory "+new File(".").getCanonicalPath()
                +"\n  with arguements "+Arrays.toString(argv));
        LogUtil.cr(log);

        
        // =========================== //
        // Start running the program. //

        cg.run();
    }
}



/**
 * Tweaked to write our hidden "concrete" realizations. Phase 3 proper can use
 * the normal Phase_OptimizeRegions; this class is only needed for writing
 * results.
 * 
 * @author William R. Zwicky
 */
class CSWriter extends HohRznWriter {
    protected List<PumsHouseholdRealizationCS> csRzns;
    protected PumsQuery pumsQuery;

    /**
     * Construct instance.
     * 
     * @param csRzns
     * @param pumsQuery
     *            need pumsQuery here. write*() is normally passed a
     *            pre-filtered list of households, but we need the pumsQuery so
     *            we can filter our concrete realizations.
     */
    public CSWriter(File outputDir, List<PumsHouseholdRealizationCS> csRzns, PumsQuery pumsQuery) {
        super(outputDir);
        this.csRzns = csRzns;
        this.pumsQuery = pumsQuery;
    }
    
    /**
     * Tweaked to write our hidden "concrete" realizations. Note this will only
     * be called if phase 3 is enabled.
     */
    @Override
    public void writeRealizations(File hohFile, File popFile,
            Iterator<PumsHouseholdRealization> houses,
            boolean allHohFields, boolean allPopFields,
            String hohKeyField, String popHohField) throws IOException {
        // Generate random rzns for current configuration of archtypes.
        super.writeRealizations(
            hohFile, popFile,
            houses,
            allHohFields, allPopFields,
            hohKeyField, popHohField);

        //
        // The gimmick: We re-open the output files, and append our hidden
        // realizations to the end. Since our custom rzns have exactly the same
        // schema as all the others, we can do this without problem.
        //
        
        // Short circuit:  If no hidden rzns, just bail.
        if(csRzns == null || csRzns.isEmpty())
            return;
        
        PrintWriter hout = null, pout = null;
        
        if(hohFile != null) {
            hout = new PrintWriter(
                new BufferedWriter(new FileWriter(hohFile, true)));
        }

        //Pop table is optional
        if(popFile != null) {
            pout = new PrintWriter(
                new BufferedWriter(new FileWriter(popFile, true)));
            pout.println();
        }
        
        for(PumsHouseholdRealization house : csRzns) {
            boolean dumpThis = true;
            if(pumsQuery != null) {
                int numTrue = pumsQuery.numberTrue(house.getParentHousehold());
                dumpThis = ( numTrue > 0 );
            }
            
            if(dumpThis) {
                String serID = house.getParentHousehold().getID();
                int rznID = house.getRealizationNumber();
    
                // Output household if requested
                if(hout != null) {
                    hout.print( house.getEasting() + "," + house.getNorthing() + "," + serID + "," + rznID);
        
                    if(allHohFields) {
                        for(int v = 0; v < house.getParentHousehold().getNumberAttributes(); v++) {
                            hout.print(",");
                            hout.print(house.getParentHousehold().getAttributeValue(v));
                        }
                    }
                    hout.println();
                }
                
                // Output people if requested
                if(pout != null) {
                    PumsPopulation[] peops = house.getParentHousehold().getMembersOfHousehold();

                    if(peops != null) {
                        for (PumsPopulation person : peops) {
                            pout.print( house.getEasting() + "," + house.getNorthing() + "," + serID + "," + rznID);
                            if(allPopFields) {
                                for(int v = 0; v < person.getNumberAttributes(); v++) {
                                    pout.print(",");
                                    pout.print(person.getAttributeValue(v));
                                }
                            }
                            pout.println();
                        }
                    }
                }
            }
        }
            
        if(hout != null)
            hout.close();
        if(pout != null)
            pout.close();
    }

}
