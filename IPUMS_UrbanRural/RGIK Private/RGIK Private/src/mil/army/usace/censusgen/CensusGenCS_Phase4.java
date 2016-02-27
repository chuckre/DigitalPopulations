package mil.army.usace.censusgen;

import java.util.LinkedHashMap;
import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.Phase_LocatePrecisely;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.TraitRefElement;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;



/**
 * Extends Phase_LocatePrecisely with support for conditional simulation.
 * Constructed and called by {@link CensusGenCS}.
 * 
 * @author William R. Zwicky
 */
public class CensusGenCS_Phase4 extends Phase_LocatePrecisely {
    private List<PumsHouseholdRealizationCS> csRzns;

    
    public CensusGenCS_Phase4(int realizationNum,
            PumsHousehold[] households,
            GISClass regionMap,
            GISLattice popDensityMap,
            List<? extends PointConstraint> constraints,
            LinkedHashMap<Trait,TraitRefElement> criteria,
            List<PumsHouseholdRealizationCS> csRzns) {
        super(realizationNum, households, regionMap, popDensityMap, constraints, criteria);
        this.csRzns = csRzns;
    }
    
    @Override
    protected void realizeHouseholds() {
        super.realizeHouseholds();
        
        // Add all concrete rzns to indices.
        if(csRzns != null) {
            for(PumsHouseholdRealizationCS rzn : csRzns) {
                // WARNING: We're abusing the way Phase_LocatePrecisely works to
                // make this work: Statistics are initialized from rznIndex (the
                // quad-trees), but candidates for movement are selected from
                // tractIndex. Thus adding concrete rzns to rznIndex ONLY will
                // make them appear in the stats, but prevent them from moving
                // around.  writeFileSet() also uses rznIndex.
                rznIndex.addPoint(rzn);
            }
        }
    }
}
