package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.util.Set;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;



/**
 * Determine if an object is forbidden from occupying a point.
 * <p>
 * NOT COMPLETE; will probably want valid() and a generalized validRegions().
 * But this is all we need for now.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public interface PointConstraint {
    /** We approve of the household's current location. */
    public static final int ALLOW = 1;
    /**
     * We disapprove of the household's current location - please move
     * it somewhere else.
     */
    public static final int FORBID = 0;
    /**
     * We have no opinion of the household's current location - our parameters
     * do not apply to the given situation.
     */
    public static final int DONTCARE = -1;
    
    
    /**
     * Determine if we have any opinion of an archtype. If this returns 'false',
     * there's no need to call any other method, as validRegions() will return
     * an empty set.
     * <P>
     * This should only be implemented if it's much faster than the other
     * methods. If it takes to long to determine, just return 'true'.
     * 
     * @param archHoh
     *            archtype to examine
     * 
     * @return 'false' if we don't care where in the map the given household
     *         winds up; 'true' if another method should be called to determine
     *         where household can go
     */
    public boolean appliesTo(PumsHousehold archHoh);
    
    /**
     * Extract the "key" value from a household's attributes. Key values must
     * must be the same for households where validRegions() would return the
     * identical results. This allows the caller to cache results of
     * validRegions based on the output of this function.
     * <P>
     * DEV NOTE: This method was designed for Match and Forbid, which only
     * examine a single attribute. If your class examines multiple attributes, I
     * don't know the best solution, but a hash is probably fine.
     * 
     * @param archHoh
     *            archtype to examine
     * 
     * @return value of attribute(s) we examine when computing results
     */
    public int getKeyValue(PumsHousehold archHoh);
    
    /**
     * Determine the validity of a household's location. Returns DONTCARE if we
     * have no opinion of the current location, i.e. our data does not apply to
     * the given household. Returns ALLOW for "true" if we're applicable and
     * household matches our goal, or FORBID for "false" if it does not.
     * <P>
     * Generally, DONTCARE can be considered the same as ALLOW. i.e. If we're
     * not applicable, than the given household is fine where it is.
     * 
     * @param house
     *            household to examine
     * @return ALLOW, FORBID, or DONTCARE
     */
    public int allows(PumsHouseholdRealization hoh);

    /**
     * Find all regions that contain at least one cell where a household is
     * allowed to be placed. Regions with no valid cells are not returned,
     * allowing those regions to be ruled out at the beginning of an analysis.
     * 
     * @param archHoh
     *            archtype household with attributes to test
     * @param regions
     *            region map (raster map that contains a region code in each
     *            cell). Since this constraint is location-sensitive, this map's
     *            grid will be used to generate locations to probe.
     * 
     * @return list of region numbers where household is allowed
     */
    public Set<Integer> validRegions(PumsHousehold archHoh, GISClass regions);

    /**
     * Long version of toString(), contains all our fields.
     * 
     * @return long descriptive string
     */
    public String describe();
    
    /**
     * Briefly explain to the user how we examine a household to arrive at an
     * answer. This generally consists of getKeyValue, and a description of
     * where getKeyValue came from.
     * 
     * @param archHoh
     * @return
     */
    public String explain(PumsHousehold archHoh);
}
