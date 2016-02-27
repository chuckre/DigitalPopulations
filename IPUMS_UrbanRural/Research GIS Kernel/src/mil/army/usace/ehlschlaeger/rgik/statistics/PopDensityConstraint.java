package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.util.HashSet;
import java.util.Set;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;



/**
 * Find regions where the population density is non-zero. This class will
 * compare a region map and a population density map, and return a list of all
 * codes for regions where the total population density is greater than zero.
 * The actual scan is performed in the constructor, so validRegions() takes no
 * time.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 * @deprecated EXPERIMENTAL -- not used anywhere yet.
 */
public class PopDensityConstraint implements PointConstraint {
    private GISLattice popDensityMap;
    private GISClass theRegionMap;
    /** precomputed; result is same for all hoh */
    private Set<Integer> validRegions;
    private String label;

    /**
     * Create standard instance.
     * 
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param popDensityMap
     *            raster map of relative population density for each cell. Only
     *            used to guide generation of random locations for houses that
     *            are written to disk.
     */
    public PopDensityConstraint(GISClass regionMap, GISLattice popDensityMap) {
        //
        // DEV NOTE:
        // Implemented this way cuz this is all we really need now. If we need
        // something more comprehensive, then we might want to implement a
        // generalized validRegions() somewhere, then have our validRegions
        // cache the result based on the regionMap passed in.
        //
        this.popDensityMap = popDensityMap;
        this.theRegionMap = regionMap;
        
        int minRegions = regionMap.getMinimumValue();
        int maxRegions = regionMap.getMaximumValue();
        double[] density = new double[maxRegions-minRegions+1];
        
        // Total up pop density for each region.
        for(int r=0; r<regionMap.getNumberRows(); r++) {
            for(int c=0; c<regionMap.getNumberColumns(); c++) {
                int region = regionMap.getCellValue(r, c);
                double e = regionMap.getCellCenterEasting(r, c);
                double n = regionMap.getCellCenterNorthing(r, c);
                
                density[region-minRegions] += popDensityMap.getCellValue(e, n);
            }
        }

        validRegions = new HashSet<Integer>();
        for(int i=0; i<density.length; i++) {
            if(density[i] > 0)
                validRegions.add(i+minRegions);
        }
        
        label = String.format("%s: household can only be placed where %s is non-zero",
            getClass().getSimpleName(),
            popDensityMap.getName());
    }

    /**
     * @return 'true'; we are applicable to all households
     */
    public boolean appliesTo(PumsHousehold archHoh) {
        return true;
    }

    public int getKeyValue(PumsHousehold archHoh) {
        // we only have the one validRegions
        return 0;
    }
    
    /**
     * Determine if a household can be placed at a location.
     */
    public int allows(PumsHouseholdRealization hoh) {
        return popDensityMap.getCellValue(hoh.getEasting(), hoh.getNorthing()) > 0 ? ALLOW : FORBID;
    }
    
    /**
     * Find all regions that contain at least one cell where a household is
     * allowed to be placed.
     * 
     * @param archHoh
     *            household to test
     * @param regionMap
     *            grid to scan. WARNING: Must be same grid as was passed into
     *            constructor!
     * 
     * @return regions with non-zero population density. WARNING: This is our
     *         container! Clone if you need to modify!
     */
    public Set<Integer> validRegions(PumsHousehold archHoh, GISClass regionMap) {
        assert regionMap == theRegionMap;
        return validRegions;
    }

    /**
     * @return long descriptive string
     */
    public String describe() {
        return label;
    }
    
    public String explain(PumsHousehold archHoh) {
        return popDensityMap.getName();
    }
}
