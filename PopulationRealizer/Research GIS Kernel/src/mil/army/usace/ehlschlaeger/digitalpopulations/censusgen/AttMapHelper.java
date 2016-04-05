package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;
import mil.army.usace.ehlschlaeger.rgik.util.IntsMap;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Helper to work with attribute maps in phases 2 and 3
 * ({@link Phase_InitialPlacement} and {@link Phase_OptimizeRegions}).
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class AttMapHelper {
    /** list of point and region constraints */
    protected List<? extends PointConstraint> pcons;
    /** map which specifies the region that covers each cell */
    protected GISClass regionMap;
    /** default list of region codes */
    protected ArrayList<Integer> regionList;
    
    /** Cache for answers from validRegions(). */
    protected IntsMap<List<Integer>> attMapCache = new IntsMap<List<Integer>>();
    /** Cache for diagnose() so we don't repeat the same messages. */
    protected IntsMap<Boolean> errorCache = new IntsMap<Boolean>();

    /**
     * Default constructor.
     * 
     * @param pstats
     *            list of statistic objects to be treated as constraints
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param regionList
     *            default list of region codes. validRegions will only return
     *            these or a subset of them. null to use all regions in
     *            regionMap.
     */
    public AttMapHelper(GISClass regionMap, ArrayList<Integer> regionList,
            List<? extends PointConstraint> pstats) {
        this.pcons = pstats;
        this.regionMap = regionMap;
        
        if(regionList == null) {
            regionList = new ArrayList<Integer>(regionMap.makeInventory());
            Collections.sort(regionList);
        }
        this.regionList = regionList;
    }

    /**
     * Compute list of valid regions a household can be placed into. Attribute
     * maps are considered constraints -- households *must* obey. So here we
     * compute which regions a household is allowed to be placed into.
     * <P>
     * Method can run in either of two modes, and 'diagnostic' controls the
     * mode:
     * <UL>
     * <LI>If null, then method runs in normal mode: all constraints are
     * evaluated, and if constraints end up invalidating all of the regions,
     * then the returned list will be empty.
     * <LI>If an array is passed in, then method runs in diagnostic mode:
     * evaluation will stop as soon as list becomes empty, and the return value
     * will be the last non-empty list before it became empty. The first element
     * of this array will receive the object that caused the list to become
     * empty.
     * </UL>
     * <P>
     * If 'diagnostic' is used, the result can be displayed to the user to help
     * diagnose trouble, although diagnose() is better suited for that.
     * 'diagnostic' is primarily intended to inform the caller whether the
     * return value is the result of an incomplete run.
     * <P>
     * Note that you will only get this warning once: we cache results, so
     * diagnostic[0] will be non-null the first time we encounter the problem.
     * For all subsequent calls with the same combination of attributes,
     * diagnostic[0] will be null.
     * 
     * @param house
     *            households attribute set
     * @param diagnostic
     *            (first element is output parameter) first constraint that
     *            caused region list to become empty. If null is passed in, then
     *            all constraints are evaluated, and if constraints invalidate
     *            all regions, then returned list will be empty. If an array is
     *            passed in, then evaluation will stop as soon as list becomes
     *            empty: method's return value will be the last non-empty list
     *            before it became empty, and first element of array will
     *            receive the object that caused the list to become empty. Can
     *            be displayed to user to help diagnose trouble, though
     *            diagnose() is better suited for that. This is primarily
     *            intended to inform the caller whether the return value is
     *            valid.
     * 
     * @return list of region codes household can be placed into. Do not modify
     *         without cloning; result may just be regionList.
     */
    public List<Integer> validRegions(PumsHousehold house, Object[] diagnostic) {
        //
        // DEV NOTE:
        // 'diagnostic' is not a boolean, as user needs to inform us whether to
        // perform diagnostic at all (is slightly slower), and we need to inform
        // user whether the returned list is valid (i.e. all stats were
        // evaluated).
        //

        // Reset diagnostic.
        if(diagnostic != null)
            diagnostic[0] = null;
        
        if(pcons == null || pcons.isEmpty()) {
            // no point stats; all regions are valid
            return regionList;
        }
        else {
            // check the cache first
            int[] key = getAMCkey(house);
            List<Integer> goodRegions = attMapCache.get(key);
            
            // not found, compute
            if(goodRegions == null) {
                goodRegions = new ArrayList<Integer>(regionList);

                // filter out invalid regions
                for(PointConstraint cons : pcons) {
                    if(cons.appliesTo(house)) {
                        Set<Integer> rgns = cons.validRegions(house, regionMap);
                        
                        if(diagnostic == null) {
                            // Process all maps normally.
                            goodRegions.retainAll(rgns);
                            if(goodRegions.isEmpty())
                                // All regions ruled out; no need to continue.
                                break;
                        }
                        else {
                            // Diagnostic is requested: return last good list.
                            ArrayList<Integer> test = new ArrayList<Integer>(goodRegions);
                            test.retainAll(rgns);
                            if(test.isEmpty()) {
                                // All regions ruled out; no need to continue.
                                // Record troublemaker, and return previous goodRegions.
                                diagnostic[0] = cons;
                                break;
                            }
                            else {
                                goodRegions = test;
                            }
                        }
                    }
                }

                // update cache
                attMapCache.put(goodRegions, key);
            }

            // Useful for debugging. However, best way to debug is Dp2Kml with
            // point colors based on region code.
            //LogUtil.detail(log, "%s restricted to regions %s", house, goodRegions);
            
            return goodRegions;
        }
    }

    /**
     * Compute a cache key for a household archtype. The "key" is simply an
     * array containing the values that the constraints will examine. For every
     * household with identical attributes, validRegions() will return the same
     * answer, so we can cache the answer using the attributes as a key.
     * 
     * @param house household that can be passed to validRegions.
     * 
     * @return int[] key for cache, or null if not found
     */
    protected int[] getAMCkey(PumsHousehold house) {
        if(pcons != null) {
            int[] key = new int[pcons.size()];
            int pos = 0;
            for(PointConstraint cons : pcons) {
                key[pos] = cons.getKeyValue(house);
                pos += 1;
            }
            
            return key;
        }
        else
            return null;
    }

    /**
     * Explain how validRegions produced a result. This is essentially a
     * duplicate of validRegions except for the logging.
     * 
     * @param house
     *            household that was passed to validRegions.
     * @param log
     *            target for our explanation text
     */
    public void diagnose(PumsHousehold house, Logger log) {
        // Only explain each attribute set once.
        int[] key = getAMCkey(house);
        
        if(errorCache.get(key) == null) {
            errorCache.put(Boolean.TRUE, key);

            StringBuffer buf = new StringBuffer();
            buf.append(String.format("Diagnosing validRegions(%s)", house));
            
            if(pcons == null) {
                buf.append("\n  No attribute maps defined; all regions are valid.");
            }
            else {
                ArrayList<Integer> goodRegions = ObjectUtil.clone(regionList);
                buf.append(String.format("\n  Initial region list: %s", goodRegions));
                for(PointConstraint cons : pcons) {
                    String msg = null;
                    if(cons.appliesTo(house)) {
                        Set<Integer> rgns = cons.validRegions(house, regionMap);
                        goodRegions.retainAll(rgns);
                        
                        if(rgns.isEmpty())
                            msg = "NO MATCHING CELLS FOUND";
                        else
                            msg = rgns.toString();
                    }
                    else
                        msg = "Not Applicable";
                    
                    // Printing house ID here is redundant, but should make
                    // it easier for users to follow what's going on.
                    buf.append("\n  ").append(cons.describe());
                    buf.append(String.format("\n    %s -> %s",
                        cons.explain(house),
                        msg));
                }
                buf.append(String.format("\n  Final region list: %s", goodRegions));
                
                LogUtil.detail(log, buf);
            }
        }
    }
}
