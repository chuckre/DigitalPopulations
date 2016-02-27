package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;


/** Index of household realizations by region code. */
class RegionIndex {
    // can be a Map<>, but I'll take a chance that region codes aren't too large.
    protected Vector<List<PumsHouseholdRealization>> index;

    public RegionIndex() {
        this.index = new Vector<List<PumsHouseholdRealization>>();
    }

    /**
     * @return number of slots used by index
     */
    public int getNumRegions() {
        return index.size();
    }

    /**
     * @return list of all region codes for which we have households
     */
    public Set<Integer> getIDs() {
        HashSet<Integer> ids = new HashSet<Integer>();
        for(int i=0; i<index.size(); i++) {
            List<PumsHouseholdRealization> el = index.get(i);
            if(el != null && ! el.isEmpty())
                ids.add(i);
        }
        return ids;
    }
    
    /**
     * @param regionID region code
     * @return container for all households in given region
     */
    public List<PumsHouseholdRealization> getRzns(int regionID) {
        return index.get(regionID);
    }

    /**
     * Add a household to the appropriate container.
     * 
     * @param regionID region code
     * @param hohRzn household object to add
     */
    public void add(int regionID, PumsHouseholdRealization hohRzn) {
        if (regionID >= index.size())
            index.setSize(regionID + 1);
        List<PumsHouseholdRealization> list = index.get(regionID);
        if (list == null) {
            list = new ArrayList<PumsHouseholdRealization>();
            index.set(regionID, list);
        }
        list.add(hohRzn);
    }
}
