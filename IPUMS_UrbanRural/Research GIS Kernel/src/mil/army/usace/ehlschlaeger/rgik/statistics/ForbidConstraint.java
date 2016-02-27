package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.LanduseCombination;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Flatly forbid a household to be placed in certain map cells. Parameters
 * define which households and map cells to consider. When a household is to be
 * placed in a cell where both the household and cell match our parameters, then
 * FORBID is returned. In all other cases, DONTCARE is returned.
 * <P>
 * Note that PumsHousehold has a value for NODATA -- If a cell is blank,
 * PumsHousehold will store this value instead. And whenever we see that value,
 * we consider that household inapplicable and return DONTCARE. Be careful that
 * the households table doesn't not contain that value.
 * 
 * @author William R. Zwicky
 */
public class ForbidConstraint implements PointConstraint {
    private String       label;

    private GISClass     goalMap;
    private String       goalSelect;
    private BitSet       goalSet;

    private String       attribName;
    private String       attribSelect;
    private BitSet       attribSet;

    
    /**
     * Use factory methods!
     */
    private ForbidConstraint() {
    }

    public void setLabel(String desc) {
        this.label = desc;
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
        int attVal = archHoh.getAttributeValue(attribName);
        if(attVal == PumsHousehold.NODATA_VALUE)
            // we never apply to NODATA
            return false;
        else if(attribSet == null) {
            // pums set not defined; all households need to be examined.
            return true;
        }
        else {
            // pums set defined; we only care if hoh attrib is in set
            return attribSet.get(attVal);
        }
    }

    /**
     * @return appropriate attribute from given household, or
     *         {@link PumsHousehold#NODATA_VALUE} if field is empty
     */
    public int getKeyValue(PumsHousehold archHoh) {
        int attVal = archHoh.getAttributeValue(attribName);
        if(attribSet == null) {
            // if no attribSet, then every val is distinct
            return attVal;            
        }
        else if(attVal == PumsHousehold.NODATA_VALUE) {
            return PumsHousehold.NODATA_VALUE;
        }
        else {
            // if attribSet, then we only care about in/out
            return attribSet.get(attVal) ? 1 : 0;
        }
    }
    
    /**
     * Determine if household is allowed to be in its current location.
     */
    public int allows(PumsHouseholdRealization house) {
        if(attribSet != null) {
            int attVal = house.getParentHousehold().getAttributeValue(attribName);
            if(attVal == PumsHousehold.NODATA_VALUE)
                // we never apply to NODATA
                return DONTCARE;
            else if(!attribSet.get(attVal))
                // hoh does not match:  don't care
                return DONTCARE;
        }
        // hoh has matching attrib, or attribSet is null (we must examine all hoh)
        int goalVal = goalMap.getCellValue(house.getEasting(), house.getNorthing());
        if(goalSet.get(goalVal))
            // hoh matches, cell matches:  FORBID!
            return FORBID;
        else
            // hoh matches but cell does not:  don't care.
            return DONTCARE;
    }

    /**
     * Find all regions that contain at least one cell where given household can
     * be placed.
     * 
     * @param archHoh
     *            archtype household with attributes to test
     * @param regions
     *            region map (raster map that contains a region code in each
     *            cell)
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
     * Long version of toString(), contains all our fields.
     * 
     * @return long descriptive string
     */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        
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

    /**
     * @return short descriptive string
     */
    public String toString() {
        if(label != null) {
            // Short form if we have a label.
            return String.format("%s for %s",
                getClass().getSimpleName(), label);
        }
        else {
            return describe();
        }
    }
    
    public String explain(PumsHousehold archHoh) {
        if(attribSet == null) {
            return String.format("%s = %d",
                attribName,
                getKeyValue(archHoh));
        }
        else {
            int attVal = archHoh.getAttributeValue(attribName);
            return String.format("(%s in %s) = %s",
                attribName,
                attribSet.toString(),
                attribSet.get(attVal));
        }
    }
    
    /**
     * Construct standard instance.
     * 
     * @param usePopTable
     *            false if attribName refers to household data, or true if
     *            population. MUST BE FALSE; true is not supported.
     * @param attribName
     *            name of attribute to examine in household or population data
     * @param attribSelect
     *            which attribute values we will consider; all others will be
     *            ignored. Pass null to consider all values (i.e. NO households
     *            will be placed in specified cells.)
     * @param goalMap
     *            map contains values for every location where households can be
     *            placed (i.e. the same area as the regions map)
     * @param goalSelect
     *            which map values we will consider; all others will be ignored.
     *            Cannot be null or "".
     * 
     * @return new instance, properly configured
     */
    public static ForbidConstraint createStat(boolean usePopTable, String attribName, String attribSelect,
            GISClass goalMap, String goalSelect) {
        assert usePopTable == false;
        assert ! ObjectUtil.isBlank(attribName);
        assert goalMap != null;
        assert ! ObjectUtil.isBlank(goalSelect);
        
        ForbidConstraint m = new ForbidConstraint();
        
        m.goalMap = goalMap;
        m.attribName = attribName;
        
        if(! ObjectUtil.isBlank(goalSelect)) {
            m.goalSelect = goalSelect;
            m.goalSet = LanduseCombination.makeBitSet(goalSelect);
        }

        m.attribSelect = attribSelect;
        m.attribSet = LanduseCombination.makeBitSet(attribSelect);
        
        return m;
    }
}
