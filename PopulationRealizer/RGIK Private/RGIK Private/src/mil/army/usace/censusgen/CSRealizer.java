package mil.army.usace.censusgen;

import java.util.ArrayList;
import java.util.Random;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.Solution;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;



/**
 * The heart of conditional simulation: Takes a table of coordinates with
 * specific attributes, finds a matching household realization for each row, and
 * moves it to the given coords. Note this only realizes households to satisfy
 * the CS table; all other potential realizations (i.e. the tract number lists
 * in each archtype) are left unfulfilled.
 * <P>
 * Rzns that are rewritten will be removed from the given Solution object, but
 * the new concrete rzns will be loaded into the Solution's statistics.
 * 
 * @author William R. Zwicky
 */
public class CSRealizer {
    protected CSTableUtil csUtil;
    protected Random random;
    protected Solution soln;

    /** Helps generate UIDs for customized archtypes (see realize()). */
    protected int archAutoNum = 1;
    

    public CSRealizer(CSTableUtil csUtil, Random random, Solution soln) {
        this.csUtil = csUtil;
        this.random = random;
        this.soln = soln;
    }

    /**
     * Perform configured job.
     * <P>
     * Each realized household has its own private archtype, copied from the
     * master list(i.e. our in-memory copy of the PUMS table), but unique to the
     * realization.
     * 
     * @return list of realized households
     */
    public ArrayList<PumsHouseholdRealizationCS> realizeHouseholdsCS() {
        assert soln.householdArchTypes != null && soln.householdArchTypes.length > 0;

        // for each record in csTable
        //   determine tract number for record
        //   h = random archtype
        //   for each archtype from h to h-1 (wrap around)
        //     if arch wants a rzn in this tract (i.e. arch.rznTracts contains tractNum)
        //       check spread(arch, record)
        //       keep arch as "best" if spread is better
        //   create rzn from best, customize from record
        //   delete tract number from best.rznTracts

        ArrayList<PumsHouseholdRealizationCS> csRzns;
        csRzns = new ArrayList<PumsHouseholdRealizationCS>();
        
        // for each record in csTable
        CSVTable csvTable = csUtil.getCSVTable();
        for(int row=0; row<csvTable.getRowCount(); row++) {
            // determine tract number for record
            int csTract = csUtil.getTract(row);

            // init result
            double bestd = Double.POSITIVE_INFINITY;
            int bestArch = -1;
            
            // h = random archtype
            int startHoh = random.nextInt(soln.householdArchTypes.length);
            // for each archtype from h to h-1 (wrap around)
            for(int hoh=startHoh; ;) {
                PumsHousehold house = soln.householdArchTypes[hoh];
                // scan arch.rznTracts for record's tract
                if(house.hasRealizationTract(csTract)) {
                    // if found,
                    //   check distance(arch, record)
                    //   keep arch as "best" if distance is better
//TODO use criteria weights, if possible
                    double d = csUtil.spread(row, house);
                    if(d < bestd) {
                        bestd = d;
                        bestArch = hoh;
                    }
                }

                // iterate, wrap, terminate
                hoh += 1;
                if(hoh >= soln.householdArchTypes.length)
                    hoh = 0;
                if(hoh == startHoh)
                    break;
            }

            if(bestArch < 0)
                // can only happen if we tested zero archtypes
                throw new DataException(
                    String.format("Ran out of realizations while trying to fit CS records in tract %d.",
                        csTract));
            else {
                // create rzn from best, customize from record
                PumsHousehold house = soln.householdArchTypes[bestArch];
                PumsHouseholdRealizationCS rzn = realize(house, row);

                // remove floating realization
                soln.removeRealization(bestArch, csTract);
                
                // add hidden CS realization
                csRzns.add(rzn);
                soln.addPhantom(csTract, rzn.getParentHousehold());
            }
        }
        
        return csRzns;
    }

    /**
     * Create a new realization by copying an archtype, and customizing with
     * attribs from a CS record.
     * 
     * @param archtype
     * @param row
     * @return
     */
    protected PumsHouseholdRealizationCS realize(PumsHousehold archtype, int row) {
        PumsHousehold newArch = archtype.cloneValues();
        String rid = String.format("%dC%s", archAutoNum++, archtype.getID());  //C for "concrete"
        newArch.setID(rid);
        csUtil.copyAttribs(row, newArch);
        
        PumsHouseholdRealizationCS newRzn = new PumsHouseholdRealizationCS(newArch, 0, -1, -1);
        csUtil.copyLocation(row, newRzn);
        return newRzn;
    }
}
