package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.util.List;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.TractSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;



/**
 * This class helps make the phase 1 (compute multipliers) method cleaner. You
 * can think of these as "nested" methods within phase1(), not to be called from
 * any other method.
 * <P>
 * This class doesn't contain all of the phase; some of the code is too tightly
 * integrated into censusgen to pull out.
 */
public class Phase_ComputeMultiplier {
    /** Stores and helps update current solution. */
    public Solution soln;
    public double bestfit;
    public int minimumRealizations;
    /** Number of objects tested. */
    public long tests = 0;
    /** Number of adjustments made. */
    public long changes = 0;
    
    /**
     * Copy of stats as they were at beginning of process (i.e. when constructor
     * was called.)
     */
    private List<TractSpatialStatistic> initStats;

    
    /**
     * Create helper to work with given solution.
     * @param solution
     */
    public Phase_ComputeMultiplier(Solution solution) {
        this.soln = solution;
        this.initStats = Solution.copy(soln.stats);
    }

    /**
     * Extract numbers of realizations of each archtype as an array that can be used
     * for numRealizations2Make.
     * @return int array of counts of realizations
     */
    public int[] getNumberRealizations() {
        int[] nr = new int[soln.householdArchTypes.length];
        for(int h=0; h<nr.length; h++)
            nr[h] = soln.householdArchTypes[h].getNumberRealizations();
        return nr;
    }
    
    /** Check if more of this archtype improves fit. */
    public boolean tryMore(int archtype, int quantity, int tract) {
        tests++;
        
        // Increase by given amount.
        for(int n=0; n<quantity; n++)
            soln.addRealization(archtype, tract);

        double testfit = soln.getFit();
        
        if( testfit < bestfit) {
            // Fit improved, so keep.
            bestfit = testfit;
            changes++;
            return true;
        }               
        else {
            // Fit worsened, undo the NewPt above.
            for(int n=0; n<quantity; n++)
                soln.removeRealization(archtype);
            return false;
        }
    }
    
    /** Check if less of this archtype improves fit. */
    public boolean tryLess(int archtype, int quantity, int tract) {
        // If too few rzns, bail.
        if(soln.householdArchTypes[archtype].getNumberRealizations()-quantity < minimumRealizations)
            return false;
        
        tests++;
        
        // Reduce by given amount.
        for(int n=0; n<quantity; n++)
            soln.removeRealization(archtype);
        
        double testfit = soln.getFit();

        if( testfit < bestfit) {
            // Fit improved, so keep.
            bestfit = testfit;
            changes++;
            return true;
        }               
        else {
            // Fit worsened, undo the RemovedPt above.
            for(int n=0; n<quantity; n++)
                soln.addRealization(archtype, tract);
            return false;
        }
    }
    
    /** Print detailed listing of stats. */
    public String dumpFit(String title) {
        StringOutputStream sos = new StringOutputStream();
        
        sos.format("\n%s:\n", title);
        sos.format("%4s  %-8s  %-8s  %-8s  %-8s  %-8s  %s\n",
            "stat", "  Goal",
            " Inital",  "Goal/Ini",
            "Adjusted", "Goal/Adj",
            "Trait");
        
        double fit = 0;
        for (int i = 0; i < soln.stats.size(); i++) {
            double goal = Solution.getSum(soln.goals.get(i));
            double sum = Solution.getSum(soln.stats.get(i));
            double initSum = Solution.getSum(initStats.get(i));
            
            fit += (goal-sum)*(goal-sum) * soln.goalWeights.get(i);

            sos.format("%4s  %8.1f  %8.1f  %8.6f  %8.1f  %8.6f  %s\n",
                String.format("[%d]",i), goal,
                initSum, goal/initSum,
                sum, goal/sum,
                soln.stats.get(i));
        }
        sos.format("Fitness: ", fit);
        return sos.toString();
    }

    /** Print a header to go above the output of printProgress(). */
    public void printProgressHeader(Logger log) {
        StringOutputStream sos = new StringOutputStream();
        sos.format("  %9s %9s %5s %8s", "Moves", "Fails", "Mins", "Test/M");
        for (int i = 0; i < soln.stats.size(); i++)
            sos.format( " %10s", "stat[" + i + "]");
        sos.format( " %s", "Spread");
        LogUtil.progress(log, sos.toString());
    }

    /** Print a line indicating status of phase 1. */
    public void printProgress(Logger log, double elapsed) {
        StringOutputStream sos = new StringOutputStream();
        sos.format("  %9d %9s %5.1f %8.0f",
                    changes, tests-changes,
                    elapsed/60, 60*tests/elapsed);
        
        // Print sum-of-squares.
//        for (int i = 0; i < soln.stats.size(); i++)
//            sos.format(" %10.5g", Math.sqrt( soln.stats.get(i).spread( soln.goals.get(i))));
//        sos.format(" %f", Math.sqrt(soln.getFit()));
        
        // Print cartesian distance.
        for (int i = 0; i < soln.stats.size(); i++) {
            SpatialStatistic stat = soln.stats.get(i);
            SpatialStatistic goal = soln.goals.get(i);
            if(stat instanceof TractSpatialStatistic)
                sos.format(" %10.5g", Math.sqrt(((TractSpatialStatistic) stat).averageOff((TractSpatialStatistic) goal)));
            else
                sos.format(" %10s", "");
        }
        sos.format(" %.0f", soln.getDist());
        
        LogUtil.progress(log, sos.toString());
    }
}
