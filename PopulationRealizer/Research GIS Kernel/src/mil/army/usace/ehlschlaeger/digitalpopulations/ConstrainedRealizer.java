package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.AttMapHelper;
import mil.army.usace.ehlschlaeger.rgik.core.ClassCrop;
import mil.army.usace.ehlschlaeger.rgik.core.CumulativeDistributionFunction;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;
import mil.army.usace.ehlschlaeger.rgik.util.IntsMap;



/**
 * Produce a sequence of realizations from a sequence of archtypes. Constructor
 * receives the criteria for realizing, then iterate() constructs an object that
 * produces a stream of realizations from a stream of archtypes.
 * <P>
 * Given criteria are used to generate locations, but are not modified by the
 * process. Thus this object can be re-used for any number of streams.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class ConstrainedRealizer extends Realizer {
    private static final boolean DUMP_PDFS = false;

    protected static Logger log = Logger.getLogger(ConstrainedRealizer.class.getPackage().getName());
    
    // INPUT DATA //
    protected GISClass regionMap;
    protected GISLattice popDensityMap;
    
    // INTERMEDIATE DATA //
    protected List<? extends PointConstraint> pcons;
    // Note: This is preserved between calls to iterate(), which is just fine.
    protected IntsMap<CumulativeDistributionFunction> cdfCache;

    /**
     * Construct instance.
     * 
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param popDensityMap
     *            raster map of desired relative population density. If null,
     *            all cells with have equal population (allowing for random
     *            variation.)
     * @param attributeMaps
     *            raster maps used by 'criteria'. Ignored if criteria == null.
     * @param criteria
     *            specs on how to use attributeMaps (i.e.
     *            FittingCriteria.traitCluster). If null, only regionMap and
     *            popDensityMap will be used. Criteria will be used in given
     *            order.
     */
    public ConstrainedRealizer(
            GISClass regionMap,
            GISLattice popDensityMap,
            List<? extends PointConstraint> constraints) {
        this.regionMap = regionMap;
        this.popDensityMap = popDensityMap;
        this.pcons = constraints;
        
        this.cdfCache = new IntsMap<CumulativeDistributionFunction>();
    }

    /**
     * Construct realization of an archtype. A "realization" is a household with
     * easting and northing, which inherits its attributes from an archtype.
     * This method constructs a new realization instance, and gives it a
     * location based on descriptors held by the archtype.
     * 
     * Create realizations for all archtypes constrained by locationSpecs. Each
     * rzn will be in the appropriate tract, but the map cell it appears in will
     * only be one of those permitted by locationSpecs.
     * 
     * @param arch
     *            archtype to realize
     * @param which
     *            index into archtype's array of descriptors. Also serves as a
     *            unique ID for this archtype's realizations.
     * 
     * @return new realization
     */
    protected PumsHouseholdRealization realize(PumsHousehold arch, int which) {
        int tract = arch.getRealizationTract(which);

        // Fetch pre-built CDF.
        int[] key = getCDFCkey(arch, tract);
        CumulativeDistributionFunction cdf = cdfCache.get(key);

        // Not found; build new CDF.
        if(cdf == null) {
            cdf = buildSpecificCDF(regionMap, tract, popDensityMap, pcons, arch, null);
            if(cdf == null) {
                // Build failed:  Re-run with diagnostics.  We don't
                // want to always run with diagnostic, as that runs much slower.
                Object[] diagnostic = new Object[1];
                cdf = buildSpecificCDF(regionMap, tract, popDensityMap, pcons, arch, diagnostic);
                if(cdf == null) {
                    // Internal error: cdf is null here only if PDF is zero
                    // everywhere in this tract. But we should not have tried to
                    // place a hoh here.
                    throw new IllegalStateException(String.format(
                        "Population density map is zero everywhere in region %d.",
                        tract));
                }
                String msg = String.format(
                    "Can't place %s in region %d:  %s eliminated all cells from consideration.",
                    arch, tract, diagnostic[0]);
                log.warning(msg);
                
                AttMapHelper amh = new AttMapHelper(regionMap, null, pcons);
                amh.diagnose(arch, log);
            }
            cdfCache.put(cdf, key);
        }

        // Create and locate realization.
        PumsHouseholdRealization hohRzn = new PumsHouseholdRealization(
            arch, which,
            0,0);
        cdf.locateRandomly(hohRzn, random);
        
        return hohRzn;
    }

    /**
     * Generate key for use with cdfCache.
     * 
     * @param house
     *            archtype of household for which key should be generated
     * @param regionID
     *            region code that realization will be placed into
     */
    protected int[] getCDFCkey(PumsHousehold house, int regionID) {
        int len = (pcons == null ? 0 : pcons.size());
        int[] key = new int[len+1];
        
        int pos = 0;
        key[pos] = regionID;
        pos += 1;
        for(PointConstraint cons : pcons) {
            key[pos] = cons.getKeyValue(house);
            pos += 1;
        }
        
        return key;
    }

    /**
     * Build CDF compatible with given archtype. CDF will only place households
     * in given region where population density map and attribute maps permit.
     * 
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param regionID
     *            ID of region of interest
     * @param pdf
     *            population density map. Probability a household will be placed
     *            in a cell is governed by the value in the cell. Any raster
     *            size is allowed; only that portion overlaying 'region' will be
     *            used. If null, all cells will have equal probability.
     * @param stats
     *            attribute map managers. If null, only regionMap and pdf will
     *            be used.
     * @param archType
     *            prototype household to test against attribute maps. Ignored if
     *            'stats' is null.
     * @param diagnostic
     *            (first element is output parameter) first constraint that
     *            caused CDF to become blank. If null is passed in, then all
     *            constraints are evaluated, and if constraints invalidate all
     *            regions, then returned CDF will be null. If an array is passed
     *            in, then evaluation will stop as soon as CDF becomes blank:
     *            method's return value will be the last non-blank CDF before it
     *            became blank, and first element of array will receive the
     *            object that caused the list to become empty. Can be displayed
     *            to user to help diagnose trouble. If not null, method runs
     *            slower.
     * 
     * @return new CDF as described, or null if no maps cells are valid or pdf
     *         is zero at every valid map cell
     */
    protected CumulativeDistributionFunction buildSpecificCDF(
            GISClass regionMap, int regionID,
            GISLattice pdf,
            List<? extends PointConstraint> stats,
            PumsHousehold archType,
            Object[] diagnostic) {
        
        GISClass region = new ClassCrop(regionID,regionID).crop(regionMap);
        
        if(region.getBoundingGrid() == null)
            throw new DataException(String.format(
                "Region map %s contains no cells for region %d",
                regionMap.getName(),
                regionID));
        
        //
        // DEV NOTE:
        // Here's the trick: 'forbid' is higher priority than the region, but
        // 'allow' is higher priority than 'forbid'. Basically, the stats are
        // ORed together, and if they're all FORBID, then the cell is forbidden.
        // If any stat returns ALLOW, then the cell is legel regardless of what
        // the other stats say.
        //
        // We two separate grids because if 'allow' just switches region bits
        // back on, then the system becomes order-dependent (i.e. ALLOW follwed
        // by FORBID will disable the bit, even though it should be left
        // enabled.)
        //
        GISGrid allow = new GISGrid(region);
        GISGrid forbid = new GISGrid(region);
        GISGrid goodForbid = null;
        
        // Clear error.
        if(diagnostic != null) {
            diagnostic[0] = null;
            goodForbid = forbid.clone();
        }
        
        // Scan region map, check 'stats' for approval of each cell.
        if(stats != null && stats.size() > 0) {
            PumsHouseholdRealization probe = new PumsHouseholdRealization(archType, 0, 0, 0);
            for(PointConstraint constraint : stats) {
                // Erase cells where 'stat' forbids 'archType'.
                for(int r = 0; r<region.getNumberRows(); r++) {
                    for(int c = 0; c<region.getNumberColumns(); c++) {
                        if(!region.isNoData(r, c)) {
                            double e = region.getCellCenterEasting(r, c);
                            double n = region.getCellCenterNorthing(r, c);
                            probe.setEasting(e);
                            probe.setNorthing(n);
                            switch(constraint.allows(probe)) {
                                case PointConstraint.FORBID:
                                    forbid.setNoData(r, c, false);
                                    break;
                                case PointConstraint.ALLOW:
                                    allow.setNoData(r, c, false);
                                    break;
                                case PointConstraint.DONTCARE:
                                    // do nothing if DONTCARE
                                    break;
                            }
                        }
                    }
                }
                
                if(diagnostic != null) {
                    GISClass test = region.clone();
                    merge(test, forbid, allow);
                    
                    if(test.getBoundingGrid() == null) {
                        // forbid has marked every cell as invalid, and allow is
                        // currently blank. allow may pick up some bits from
                        // later stats, but for now we don't know.
                        
                        // record the constraint that wiped regions
                        diagnostic[0] = constraint;
                    }
                    else {
                        // All is well.
                        diagnostic[0] = null;
                        goodForbid = forbid.clone();
                    }
                }
            }
        }

        if(diagnostic != null)
            merge(region, goodForbid, allow);
        else
            merge(region, forbid, allow);
        
        // If all cells have been eliminated, return null.
        GISGrid grid = region.getBoundingGrid();
        if(grid == null)
            return null;
        
        // Create density map from the cells that remain.
        GISLattice lattice = new GISLattice(grid);
        for(int r = 0; r<lattice.getNumberRows(); r++) {
            for(int c = 0; c<lattice.getNumberColumns(); c++) {
                double e = lattice.getCellCenterEasting(r, c);
                double n = lattice.getCellCenterNorthing(r, c);
                // if region bit remains set or allow bit was set, then copy density
                if(! region.isNoData(e, n)) {
                    double density = (pdf == null ? 1.0 : pdf.getCellValue(e, n));
                    lattice.setCellValue(r, c, density);
                }
            }
        }

        // Write every generated lattice to disk.
        if(DUMP_PDFS) {
            // file name is built same way getCDFCkey builds key
            StringBuilder buf = new StringBuilder();
            buf.append(regionID);
            for(PointConstraint cons : stats) {
                buf.append('-');
                buf.append(cons.getKeyValue(archType));
            }
            
            try {
                lattice.writeAsciiEsri(buf.toString());
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't write CDF to file "+buf.toString(), e);
            }
        }
        
        // Create CDF from density map.
        CumulativeDistributionFunction cdf = CumulativeDistributionFunction.createNormalized(lattice);
        return cdf;
    }

    /**
     * Erase cells from region based on no-data bits in the constraint grids.
     * 
     * @param region
     *            region map
     * @param forbid
     *            is data (no-data == false) wherever households MUST NOT be
     *            placed. Cell values, if present, are ignored.
     * @param allow
     *            is data (no-data == false) wherever households MAY be placed.
     *            Cell values, if present, are ignored.
     */
    private void merge(GISGrid region, GISGrid forbid, GISGrid allow) {
        if(!forbid.equalsGrid(region))
            throw new DataException("forbid isn't on same grid as region");
        if(!allow.equalsGrid(region))
            throw new DataException("allow isn't on same grid as region");
        
        for(int r = 0; r<region.getNumberRows(); r++) {
            for(int c = 0; c<region.getNumberColumns(); c++) {
                if(!region.isNoData(r, c)) {
                    boolean erase = false;
                    if(!forbid.isNoData(r, c))
                        erase = true;
                    if(!allow.isNoData(r,c))
                        erase = false;
                    region.setNoData(r, c, erase);
                }
            }
        }
    }
}
