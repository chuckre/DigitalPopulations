package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.LanduseCombination;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Simple single-column attribute-resemblance statistic. The map contains
 * goal values which are examined where households are placed. If the named
 * household attribute doesn't match the value in the corresponding cell, then
 * the error spread is increased by the square of their difference.
 * <P>
 * Comparison rules are a little complex.  Basically, attribSelect and goalSelect
 * control whether this instance applies to a situation.  If so, a comparison is
 * made and error values are accumulated.  If not, house is ignored, and error
 * value doesn't change.
 * 
 * <UL>
 *   <LI> If map value is NODATA, then all houses in that cell are ignored.
 *   <LI> If attribSelect alone is provided, a house will only be examined if its attribute
 *        is in the given set of values. Houses with values outside that set are ignored.
 *   <LI> If goalSelect alone is provided, house will only be examined if map has a value in
 *        the set.  If the map value is outside the set, then the house is ignored.
 *   <UL> If both are provided, then only houses matching attribSelect are examined; all
 *        others are ignored.  Map is then examined:  If map cell fails to match goalSelect,
 *        then error is increased by one.  The house's exact
 *        attribute is <b>not</b> compared to the map value; only set membership is checked.
 * </UL>
 * <P>
 * Note that unlike most SpatialStatistic objects, we don't need a goal
 * instance. A single instance of this class manages both the goal values and
 * the household values, so getSpread() can be passed null.
 * <P>
 * Note also that this statistic is NOT used by most of censusgen; it requires
 * each household realization have specific coordinates, while most phase only
 * track the tract number. Only phase 4 works with specific coordinates.
 * 
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Match extends RGIS implements PointSpatialStatistic, PointConstraint, Serializable {
    private static Match GOAL_INSTANCE = new Match();

    private String       label;

    private GISClass     goalMap;
    private String       goalSelect;
    private BitSet       goalSet;

    private String       attribName;
    private String       attribSelect;
    private BitSet       attribSet;

    private double       spread;

    
    /**
     * Use factory methods!
     */
    private Match() {
    }

    /**
     * Set the label used by toString and print methods.
     * Does nothing if this is the common goal instance.
     * 
     * @param desc
     */
    public void setLabel(String label) {
        if(this != GOAL_INSTANCE)
            this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public GISClass getGoalMap() {
        return goalMap;
    }

    public String getGoalSelect() {
        return goalSelect;
    }

    public BitSet getGoalSet() {
        return goalSet;
    }

    public String getAttribName() {
        return attribName;
    }

    public String getAttribSelect() {
        return attribSelect;
    }

    public BitSet getAttribSet() {
        return attribSet;
    }

    /**
     * A point has been added to the map; update statistics accordingly.
     * 
     * @param newPoint
     *            household that was added to map
     * @param mapNumber
     *            NOT USED, must be zero
     */
    public void modifySS4NewPt(PumsHouseholdRealization newPoint, int mapNumber) {
        assert mapNumber == 0;
        spread += compute(newPoint);
    }

    /**
     * A point has been removed from the map; update statistics accordingly.
     * 
     * @param removedPoint
     *            household that was removed from map
     * @param mapNumber
     *            NOT USED, must be zero
     */
    public void modifySS4RemovedPt(PumsHouseholdRealization removedPoint, int mapNumber) {
        assert mapNumber == 0;
        spread -= compute(removedPoint);
    }

    /**
     * Determine if we have any opinion of an archtype. If this returns 'false',
     * there's no need to call any other method, as compute() will return 0,
     * match() will return DONTCARE, and validRegions() will return the
     * default set.
     * 
     * @param archHoh
     *            archtype to examine
     * 
     * @return 'false' if we don't care where in the map the given household
     *         winds up; 'true' if another method should be called to determine
     *         where household can go
     */
    public boolean appliesTo(PumsHousehold archHoh) {
        if(attribSet == null) {
            // pums set not defined; all households need to be examined.
            return true;
        }
        else if(goalSet != null) {
            // pums set and map set both defined; we will always have an opinion
            return true;
        }
        else {
            // pums set defined but goal set not; we can short-circuit if hoh not in set.
            int attVal = archHoh.getAttributeValue(attribName);
            return attribSet.get(attVal);
        }
    }

    public int getKeyValue(PumsHousehold archHoh) {
        return archHoh.getAttributeValue(attribName);
    }
    
    /**
     * Compute an error value for the point and it's corresponding cell in the
     * map. Return value is an <i>error</i> value, so anything other than zero
     * means the house is in a bad location. The larger the value, the worse the
     * location. Zero is returned if the location is acceptable, and that means
     * either that our criteria agrees with the house, or our criteria is not
     * relevant to this particular house and we don't care where it goes.
     * 
     * @param house 
     * @return 0 if house's location is acceptable, >0 if not
     */
    public int compute(PumsHouseholdRealization house) {
        int attVal = house.getParentHousehold().getAttributeValue(attribName);
        
        int goalVal = 0;
        boolean goalIsData = ! goalMap.isNoData(house.getEasting(), house.getNorthing());
        if(goalIsData)
            goalVal = goalMap.getCellValue(house.getEasting(), house.getNorthing());

        // "Allowed" is 0; "not allowed" is 1.
        // However, if either selection set is null, then allowed() compares the
        // actual values in the map to the household. So we can do the same, and
        // return the difference as an error value.
        int error;
        switch(match(attVal, goalIsData, goalVal)) {
            case ALLOW:
                error = 0;
                break;
            case FORBID:
                error = 1;
                if(goalIsData)
                    if(attribSet == null || goalSet == null)
                        error = (goalVal-attVal) * (goalVal-attVal);
                break;
            case DONTCARE:
                error = 0;
                break;
            default:
                // logic error: allowed() can only return above values
                error = 1;
        }
        
        return error;
    }

    /**
     * Determine if a household 'matches' us. Returns DONTCARE if we're
     * "not applicable", i.e. attribSelect or goalSelect are violated. Returns
     * ALLOW for "true" if we're applicable and household matches our goal,
     * or FORBID for "false" if it does not.
     * <P>
     * Generally, DONTCARE can be considered the same as ALLOW. i.e.
     * If we're not applicable, than the given household is fine where it is.
     * 
     * @param house
     *            household to examine
     * @return -1 if not applicable, 0 if applicable but no match, +1 if
     *         applicable and match
     */
    public int allows(PumsHouseholdRealization house) {
        int attVal = house.getParentHousehold().getAttributeValue(attribName);
        int goalVal = 0;
        boolean goalIsData = ! goalMap.isNoData(house.getEasting(), house.getNorthing());
        if(goalIsData)
            goalVal = goalMap.getCellValue(house.getEasting(), house.getNorthing());

        return match(attVal, goalIsData, goalVal);
    }

    // special-purpose method that can be used in compute() as well as validRegions().
    private int match(int attVal, boolean goalIsData, int goalVal) {
        if(attribSet == null) {
            if(goalSet == null) {
                // CASE 1: no goal set; no pums set
                //   -> house must be placed in cell with same value
                if(goalIsData)
                    // compare, allow only if match
                    return attVal == goalVal ? ALLOW : FORBID;
                else
                    // fail; hoh can never match NODATA
                    return FORBID;
            }
            else {
                // CASE 2: goal set, no pums set
                //   -> cells with goal value must receive matching houses
                //      other cells can get anything
                if(goalIsData) {
                    if(goalSet.get(goalVal))
                        // compare, allow only if match
                        return attVal == goalVal ? ALLOW : FORBID;
                    else
                        // allow; don't care what goes elsewhere
                        return DONTCARE;
                }
                else
                    // allow; don't care what happens outside map
                    return DONTCARE;
            }
        }
        else {
            if(goalSet == null) {
                // CASE 3: no goal set; pums set
                //   -> hoh in set must match cell value
                //      other hoh can go anywhere
                if(attribSet.get(attVal)) {
                    if(goalIsData)
                        // compare, allow only if match
                        return attVal == goalVal ? ALLOW : FORBID;
                    else
                        // fail; hoh can never match NODATA
                        return FORBID;
                }
                else {
                    // allow; other hoh can go anywhere
                    return DONTCARE;
                }
            }
            else {
                // CASE 4: goal set; pums set
                //   -> allow only if hoh and cell are both members, or both non-memmbers
                if(attribSet.get(attVal)) {
                    // hoh is member: it must go into member cells
                    if(goalIsData) {
                        if(goalSet.get(goalVal))
                            // hoh is member, goal is member: allow
                            return ALLOW;
                        else
                            // hoh is member, goal is not member: forbid
                            return FORBID;
                    }
                    else
                        // hoh is member, goal is NODATA: forbid
                        return FORBID;
                }
                else {
                    // hoh is not member: it can go anywhere BUT member cells
                    if(goalIsData) {
                        if(goalSet.get(goalVal))
                            // hoh is not member, goal is member: forbid
                            return FORBID;
                        else
                            // hoh is not member, goal is not member: allow
                            return ALLOW;
                    }
                    else
                        // hoh is not member, goal is NODATA: allow
                        return ALLOW;
                }
            }
        }
    }
    
    /**
     * Returns the current error spread value. This instance manages all goals
     * and statistics, so the parameter is ignored.
     * 
     * @param goal
     *            can only be null or the result of createGoal().
     * @return the current error spread value
     */
    public double spread(SpatialStatistic goal) {
        assert goal == null || goal == GOAL_INSTANCE;
        return spread;
    }

    /**
     * Find all regions that contain at least one cell where given household can
     * be placed.
     * 
     * @param archHoh
     *            archtype household with attributes to test
     * @param regions
     *            region map (raster map that contains a region code in each cell)
     * 
     * @return list of region numbers where household is allowed
     */
    public Set<Integer> validRegions(PumsHousehold archHoh, GISClass regions) {
        // By default, no regions are allowed.
        BitSet allow = new BitSet();

        PumsHouseholdRealization probe = new PumsHouseholdRealization(archHoh, 0, 0, 0);
        
        GISClass grid = regions;  //Use region map as main grid.
        for(int r=0; r<grid.getNumberRows(); r++) {
            for(int c=0; c<grid.getNumberColumns(); c++) {
                double e = grid.getCellCenterEasting(r, c);
                double n = grid.getCellCenterNorthing(r, c);
                probe.setEasting(e);
                probe.setNorthing(n);

                // shortcut: if rgn already approved, don't analyze it any more
                //  - matches() is pretty quick, but this is much faster.
                int rgn = regions.getCellValue(e, n);
                if(allow.get(rgn))
                    continue;

                if(allows(probe) != FORBID)
                    allow.set(rgn);
            }
        }

        // Convert BitSet to HashSet.
        HashSet<Integer> allowSet = new HashSet<Integer>();
        for (int i = allow.nextSetBit(0); i >= 0; i = allow.nextSetBit(i+1))
            allowSet.add(i);
        return allowSet;
    }
    
    /**
     * Build clone of this instance.  All members are shared with original.
     */
    public SpatialStatistic createCopy() {
        Match m = new Match();
        m.label = label;
        m.spread = spread;
        
        m.goalMap = goalMap;
        m.goalSelect = goalSelect;
        m.goalSet = goalSet;
        
        m.attribName = attribName;
        m.attribSelect = attribSelect;
        m.attribSet = attribSet;
        
        return m;
    }

    public boolean isMultiMap() {
        return false;
    }

    public void print(PrintStream out) throws IOException {
        out.println(toString());
        if(goalMap == null && attribName == null)
            ; //goal is blank, print nothing
        else {
            out.print("  Current spread: ");
            out.print(spread);
            out.println();
        }
    }

    public void print(PrintStream out, SpatialStatistic goal) throws IOException {
        assert goal == null || goal == GOAL_INSTANCE;
        print(out);
    }

    public void printOneMapMeasure(int mapNumber, PrintStream out) {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * @return short descriptive string
     */
    public String toString() {
        if(goalMap == null && attribName == null) {
            if(label != null)
                return "Match goal for "+label;
            else
                return "Match goal";
        }
        else {
            if(label != null) {
                // Short form if we have a label.
                return String.format("%s stat for %s",
                    getClass().getSimpleName(), label);
            }
            else {
                return describe();
            }
        }
    }
    
    /**
     * Long version of toString(), contains all our fields.
     * 
     * @return long descriptive string
     */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" stat");
        
        if(label != null)
            sb.append(String.format(" \"%s\"", label));
        
        sb.append(":");

        if(attribSelect == null) {
            if(goalSelect == null) {
                sb.append(String.format("household must be placed in %s where %s matchess cell",
                    goalMap.getName(),
                    attribName));
            }
            else {
                sb.append(String.format("%s where cell is in [%s] may only receive households where %s matches",
                    goalMap.getName(),
                    goalSelect,
                    attribName));
            }
        }
        else {
            if(goalSelect == null) {
                sb.append(String.format("households with %s in [%s] must be placed in %s where %s matchess cell",
                    attribName,
                    attribSelect,
                    goalMap.getName(),
                    attribName));
            }
            else {
                sb.append(String.format("households with %s in [%s] must be placed in %s where cell is in [%s]",
                    attribName,
                    attribSelect,
                    goalMap.getName(),
                    goalSelect));
            }
        }
        
        return sb.toString();
    }
    
    public String explain(PumsHousehold archHoh) {
        return String.format("%s = %d",
            attribName,
            getKeyValue(archHoh));
    }
    
    /**
     * Construct instance. This instance will manage both goal and accumulating
     * stats; spread() can be passed a null.
     * 
     * @param goalMap
     *            map contains values for every location where households can be
     *            placed (i.e. the same area as the regions map)
     * @param goalSelect
     *            which map values we will consider; all others will be ignored.
     *            Pass null or "" to consider all values.
     * @param usePopTable
     *            false if attribName refers to household data, or true if
     *            population
     * @param attribName
     *            name of attribute to examine in household or population data
     * @param attribSelect
     *            which attribute values we will consider; all others will be
     *            ignored. Pass null to consider all values.
     */
    public static Match createStat(GISClass goalMap, String goalSelect,
            boolean usePopTable, String attribName, String attribSelect) {
        assert ! ObjectUtil.isBlank(attribName);
        assert usePopTable == false;
        //TODO if usePopTable==true, then for(p in house.getPeops()); spread += compute(p);
        //TODO overhaul to create subclasses with custom matches(), maybe optimize with ClassCrop

        Match m = new Match();
        
        m.goalMap = goalMap;
        m.attribName = attribName;
        
        if(! ObjectUtil.isBlank(goalSelect)) {
            m.goalSelect = goalSelect;
            m.goalSet = LanduseCombination.makeBitSet(goalSelect);
        }
        
        if(! ObjectUtil.isBlank(attribSelect)) {
            m.attribSelect = attribSelect;
            m.attribSet = LanduseCombination.makeBitSet(attribSelect);
        }
        
        m.spread = 0;
        return m;
    }

    /**
     * @return a dummy instance that can be passed to spread()
     */
    public static Match createGoal() {
        return GOAL_INSTANCE;
    }
}
