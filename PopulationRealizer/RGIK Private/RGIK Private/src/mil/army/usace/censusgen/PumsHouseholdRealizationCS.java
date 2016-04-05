package mil.army.usace.censusgen;

import java.io.Serializable;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;



/**
 * Extension of PumsHouseholdRealization that adds 'isMovable', which is false
 * for rzns created from concrete data (i.e. conditional simulation).
 * 
 * @author William R. Zwicky
 */
public class PumsHouseholdRealizationCS extends PumsHouseholdRealization implements Serializable {
    private static final long serialVersionUID = 1;
    
    private boolean movable = true;

    public PumsHouseholdRealizationCS(PumsHousehold parent, int realizationNumber, double easting,
            double northing) {
        super(parent, realizationNumber, easting, northing);
    }

    /**
     * If false, then this rzn holds a household derived from concrete data, and
     * should not be moved or modified any further.
     * 
     * @return true if generic, false if specific
     */
    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }
    
    /**
     * Set easting and northing, and set isMovable to false.
     * 
     * @param easting
     * @param northing
     */
    public void fixLocation(double easting, double northing) {
        setEasting(easting);
        setNorthing(northing);
        movable = false;
    }

    /**
     * Helper to cope with mixed set of PHR and PHR_CS. PHR is always movable;
     * PHR_CS has a flag.
     * 
     * @param rzn
     *            realization to test
     * @return false if realization must not be moved from its current
     *         easting/northing; true if phases 2, 3, and 4 are free to relocate
     *         it
     */
    public static boolean isMovable(PumsHouseholdRealization rzn) {
        if(rzn instanceof PumsHouseholdRealizationCS)
            return ((PumsHouseholdRealizationCS) rzn).isMovable();
        else
            return true;
    }
}
