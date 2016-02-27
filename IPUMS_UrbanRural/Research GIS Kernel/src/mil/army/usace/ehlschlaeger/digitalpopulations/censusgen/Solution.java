package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.util.ArrayList;
import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.statistics.Count;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.Proportion;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.TractSpatialStatistic;

import org.apache.commons.collections.primitives.ArrayDoubleList;



/**
 * Wrapper for all data representing one solution: archtypes, tract numbers of
 * realizations, and statistical quality of same. Only supports tract-level
 * analysis (i.e. TractSpatialStatistic).
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Solution {
    //
    // DEV NOTE:
    // This class has createCopy(); update it if you change fields.
    //
    
    /**
     * Current arrangement of households.  Contains a copy of the
     * archtypes from the households table, and each archtype tracks
     * its own realizations.
     */
    public PumsHousehold[] householdArchTypes;

    /**
     * Names of attributes in each object in householdArchTypes.
     */
    public CSVTableNoSwing householdSchema;
    
    /**
     * Names of attributes in each object in householdArchTypes.membersOfHousehold.
     */
    public CSVTableNoSwing populationSchema;

    /**
     * Name of uid column in households table.
     */
    String hohKeyCol;
    
    /**
     * Name of column in population table that identifies containing household.
     */
    String popHohCol;

    /**
     * List of trait values for the arrangement of households contained above.
     * Each object tracks values for every region in the region map.
     */
    public List<TractSpatialStatistic> stats;
    /**
     * Each element is a goal for the corresponding element in pstats.
     */
    public List<TractSpatialStatistic> goals;
    /**
     * Relative weights for each of the statistic objects in stats. Used by
     * getFit(). Default value is 1.0; adjust higher for more important traits,
     * and lower for less important.
     */
    public ArrayDoubleList goalWeights;

    /**
     * List of location-based statistic evaluators. Solution is primarily
     * intended for tract-based analysis, so this is not used by the methods
     * here.
     */
    public List<PointSpatialStatistic> pstats;
    /**
     * Each element is a goal for the corresponding element in pstats.
     */
    public List<PointSpatialStatistic> pgoals;

    /**
     * List of location-based constraints.
     */
    public List<PointConstraint> pcons;

    /**
     * Construct an empty object.
     */
    public Solution() {
        stats = new ArrayList<TractSpatialStatistic>();
        goals = new ArrayList<TractSpatialStatistic>();
        goalWeights = new ArrayDoubleList();
        
        pstats = new ArrayList<PointSpatialStatistic>();
        pgoals = new ArrayList<PointSpatialStatistic>();
        
        pcons = new ArrayList<PointConstraint>();
    }

    /**
     * Add a tract-based statistic evaluator, its goal, and its weight.
     * 
     * @param stat
     * @param goal
     * @param weight
     */
    public void addStat(TractSpatialStatistic stat, TractSpatialStatistic goal,
            double weight) {
        stats.add(stat);
        goals.add(goal);
        goalWeights.add(weight);
    }

    /**
     * Add a location-based statistic evaluator and its goal.
     * 
     * @param stat
     * @param goal
     */
    public void addStat(PointSpatialStatistic stat, PointSpatialStatistic goal) {
        pstats.add(stat);
        pgoals.add(goal);
    }
    
    public void addConstraints(PointConstraint cons) {
        pcons.add(cons);
    }
    
    /**
     * Compute total number of household realizations we have.
     * @return
     */
    public long getTotalRealizations() {
        int hohs = 0;
        for (PumsHousehold arch : householdArchTypes)
            hohs += arch.getNumberRealizations();
        return hohs;
    }
    
    /**
     * Cartesian difference: compute sum of difference between stats and goals,
     * ignoring tracts and weights.  Use to indicate total number of units that are in
     * error.
     * 
     * @return sum of abs of each stat's error
     */
    public double getDist() {
        double diff = 0;
        for( int s = 0; s < stats.size(); s++) {
            double d = Math.abs(getSum(goals.get(s)) - getSum(stats.get(s)));
            diff += d;
        }
        return diff;
    }
    
    /**
     * Simple difference: compute overall delta-squared difference between stats and goals,
     * ignoring tracts.
     * 
     * @return sum of square of each stat's error
     */
    public double getFit() {
        double fit = 0;
        for (int i = 0; i < stats.size(); i++) {
            double goal = getSum(goals.get(i));
            double sum = getSum(stats.get(i));
            fit += (goal-sum)*(goal-sum) * goalWeights.get(i);
        }
        return fit;
    }

    /**
     * Full difference: compute delta-squared difference between stats and goals, going
     * tract by tract.
     * 
     * @return sum of square of errors of each stat in each tract
     */
    public double getSpread() {
        double spread = 0;
        for (int i = 0; i < stats.size(); i++) {
            double s = stats.get(i).spread(goals.get(i));
            spread += s*s * goalWeights.get(i);
        }
        return spread;
    }
    
    /**
     * Add new realization to an archtype, and update statistics.
     * @param hohIdx index of archtype to update
     * @param newTract tract in which to place realization
     */
    public void addRealization(int hohIdx, int newTract) {
        PumsHousehold hoh = householdArchTypes[hohIdx];
        
        hoh.addRealization(newTract);
        for (TractSpatialStatistic stat : stats) {
            stat.modifySS4NewPt(hoh, newTract, 0);
        }
    }

    /**
     * Delete last realization in list, update statistics, and return the tract it was in.
     * @return tract from which we removed a realization
     */
    public int removeRealization(int hohIdx) {
        PumsHousehold hoh = householdArchTypes[hohIdx];
        
        int tract = hoh.removeRealization();
        for (TractSpatialStatistic stat : stats)
            stat.modifySS4RemovedPt(hoh, tract, 0);
        return tract;
    }

    /**
     * Delete a realization of an archtype from a specific tract.
     * @return true if done, false if archtype has no realizations in that tract
     */
    public boolean removeRealization(int hohIdx, int tract) {
        PumsHousehold hoh = householdArchTypes[hohIdx];
        
        if(hoh.removeRealization(tract)) {
            for (TractSpatialStatistic stat : stats)
                stat.modifySS4RemovedPt(hoh, tract, 0);
            return true;
        }
        else
            return false;
    }

    /**
     * Update stats as if the given household was placed into the given tract,
     * but don't record its existence anywhere.
     * 
     * @param tract
     *            region in which household lies
     * @param hoh
     *            household to measure
     */
    public void addPhantom(int tract, PumsHousehold hoh) {
        for (TractSpatialStatistic stat : stats)
            stat.modifySS4NewPt(hoh, tract, 0);
    }

    /**
     * Update stats as if the given household was removed from the given tract,
     * but don't record its existence anywhere.
     * 
     * @param tract
     *            region in which household lies
     * @param hoh
     *            household to measure
     */
    public void removePhantom(int tract, PumsHousehold hoh) {
        for (TractSpatialStatistic stat : stats)
            stat.modifySS4RemovedPt(hoh, tract, 0);
    }

    /**
     * Move a realization out of one tract into another.
     * To undo the effect and reset the stats, simply move() back to old tract.
     * 
     * @param hohIdx index of household archtype to modify
     * @param rznIdx index of archtype's realization to move
     * @param newTract index of tract to move realization into
     * 
     * @return tract the realization was in before being moved
     */
    public int move(int hohIdx, int rznIdx, int newTract) {
        PumsHousehold hoh = householdArchTypes[hohIdx];
        int oldTract = hoh.getRealizationTract(rznIdx);
        
        if(oldTract != newTract) {
            for (TractSpatialStatistic stat : stats)
                stat.modifySS4RemovedPt(hoh, oldTract, 0);
            hoh.moveRealization(rznIdx, newTract);
            for (TractSpatialStatistic stat : stats)
                stat.modifySS4NewPt(hoh, newTract, 0);
        }
        
        return oldTract;
    }

    /**
     * Produce a "functional" copy of this instance.  Statistics and realizations are cloned,
     * but weights and archtypes are shared.
     */
    public Solution createCopy() {
        Solution s = new Solution();
        s.stats = copy(this.stats);
        s.goals = copy(this.goals);
        s.goalWeights = this.goalWeights;

        // PSS are for reference, and don't need to be cloned.
        s.pstats = this.pstats;
        s.pgoals = this.pgoals;
        s.pcons  = this.pcons;
        
        s.householdArchTypes = cloneRzn(this.householdArchTypes);
        s.householdSchema = this.householdSchema;
        s.populationSchema = this.populationSchema;

        s.hohKeyCol = this.hohKeyCol;
        s.popHohCol = this.popHohCol;
        
        return s;
    }

    /**
     * Helper to copy this.stats or this.goals.
     */
    @SuppressWarnings("unchecked")
    public static <SS extends SpatialStatistic> List<SS> copy(List<SS> stats) {
        List<SS> newStats = new ArrayList<SS>();
        for (SS pss : stats)
            newStats.add((SS) pss.createCopy());
        return newStats;
    }

    /**
     * Helper to copy this.householdArchTypes.
     */
    public static PumsHousehold[] cloneRzn(PumsHousehold[] hohs) {
        PumsHousehold[] newHohs = new PumsHousehold[hohs.length];
        for (int i = 0; i < hohs.length; i++) {
            newHohs[i] = hohs[i].cloneRzn();
        }
        return newHohs;
    }
    
    /**
     * Compute some sort of numerator for the given statistic object.
     * 
     * @param stat
     * @return
     */
    public static double getSum(SpatialStatistic stat) {
        if(stat instanceof Proportion)
            return ((Proportion)stat).getSum();
        else if(stat instanceof Count)
            return ((Count)stat).getSum();
        else
            return 0;
    }
}
