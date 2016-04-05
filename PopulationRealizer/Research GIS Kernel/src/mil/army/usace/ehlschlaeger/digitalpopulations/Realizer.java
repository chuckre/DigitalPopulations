package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.util.Iterator;
import java.util.Random;



/**
 * Produce a sequence of realizations from a sequence of archtypes. An iterator
 * is fed to the constructor, and next() will produce realizations in every
 * region requested by each archtype.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public abstract class Realizer {
    protected Random random = new Random();
    
    
    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        this.random = source;
    }

    /**
     * Construct realization of an archtype. A "realization" is a household with
     * easting and northing, which inherits its attributes from an archtype.
     * This method constructs a new realization instance, and gives it a
     * location based on descriptors held by the archtype.
     * 
     * @param arch
     *            archtype to realize
     * @param which
     *            index into archtype's array of descriptors. Also serves as a
     *            unique ID for this archtype's realizations.
     * 
     * @return new realization
     */
    protected abstract PumsHouseholdRealization realize(PumsHousehold arch, int which);

    /**
     * Construct an iterator that produces realizations as it consumes
     * archtypes.
     * 
     * @param archTypes
     *            archtypes to consume
     * 
     * @return an iterator that produces realizations
     */
    public Iterator<PumsHouseholdRealization> iterate(final Iterator<PumsHousehold> archTypes) {
        return new Iterator<PumsHouseholdRealization>() {
            protected Iterator<PumsHousehold> archIter;
            protected PumsHousehold curArch;
            protected int nextRzn;
            
            /*constructor*/ {
                this.archIter = archTypes;
                this.curArch = null;
                this.nextRzn = -1;
                
                findNextt();
            }
            
            /**
             * Update pointers to point to the objects from which next() will construct
             * a realization. If there is no data left, markers are left for hasNext().
             */
            protected void findNextt() {
                for(;;) {
                    // Move to next rzn of current archtype, validate.
                    nextRzn += 1;
                    if(curArch != null && nextRzn < curArch.getNumberRealizations())
                        break;

                    // Ran out of rzns, move to next archtype.
                    if(archIter.hasNext()) {
                        curArch = archIter.next();
                        nextRzn = -1;
                        // will validate this on next loop
                    }
                    else {
                        // Ran out of archtypes.
                        curArch = null;
                        nextRzn = -1;
                        break;
                    }
                }
            }
            
            public boolean hasNext() {
                return curArch != null;
            }

            public PumsHouseholdRealization next() {
                PumsHouseholdRealization rzn = realize(curArch, nextRzn);
                findNextt();
                return rzn;
            }

            public void remove() {
                // Not needed, but shouldn't be too hard.
                throw new UnsupportedOperationException();
            }
        };
    }
}
