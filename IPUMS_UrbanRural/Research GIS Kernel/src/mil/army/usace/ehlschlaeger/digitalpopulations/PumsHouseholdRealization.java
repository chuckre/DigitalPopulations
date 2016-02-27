package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.PrintStream;
import java.io.Serializable;

import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;



/**
 * A "realization" of one household. PumsHouseholdRealization "inherits" from
 * PumsHousehold, which means the attributes are stored there rather than here.
 * One PH is actually a record from a PUMS (sample census) table, so we don't
 * need to clone all the data here. Each PHR keeps its own list of
 * PumsPopulationRealizations.
 * <p>
 * PumsHouseholdRealization is a GISPoint where the attribute array contains
 * three objects: [0] is the PumsHousehold parent, [1] is an Integer containing
 * the realization number, [2] is an array of integers containing realized
 * variables, and [3] is null but would contain information regarding the
 * realized members of the household.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class PumsHouseholdRealization extends GISPoint implements Serializable {

    public PumsHouseholdRealization(PumsHousehold parent,
            int realizationNumber, double easting, double northing) {
		super( easting, northing);
		Object[] atts = new Object[3];
		atts[ 0] = parent;
		atts[ 1] = new Integer( realizationNumber);
		atts[ 2] = new int[ 1];
		this.setAttributes( atts);
	}

    /**
     * Get number of solution to which we belong.
     * 
     * @return int system realization number
     */
	public int getRealizationNumber() {
		Object[] atts = getAttributes();
		Integer i = (Integer) atts[ 1];
		return( i.intValue());
	}

	/**
	 * Get archtype.
	 * 
	 * @return object that defines our attributes
	 */
	public PumsHousehold getParentHousehold() {
		Object[] atts = getAttributes();
		PumsHousehold i = (PumsHousehold) atts[ 0];
		return( i);
	}
	
	protected void setParentHousehold(PumsHousehold newParent) {
	    // Not public cuz I'm paranoid.  It might be safe, but think carefully.
        Object[] atts = getAttributes();
	    atts[0] = newParent;
	    setAttributes(atts);
	}

	/**
	 * Get number of people residing in this household.
	 * 
	 * @return int number of residents
	 */
	public int getNumberMembers() {
	    // There's no PumsPopRzn class, so just delegate.
		return getParentHousehold().getNumberMembers();
	}

    /**
     * Replace this object's parent with a clone unique to this rzn so attributes
     * can be changed. Realizations share a common archtype, so any changes to
     * the archtype will normally affect all realizations. But after this method
     * is called this realization will have its own archtype, which can be
     * altered at whim.
     * <P>
     * Note that the ID numbers (archtype ID and our realizationNumber) are NOT
     * changed. This is not generally a problem, as we don't need archtype IDs
     * to be unique on export.
     */
    public void uniquify() {
        PumsHousehold neuParent = getParentHousehold().cloneValues();
        setParentHousehold(neuParent);
    }
	
	/**
	 * Builds brief string to help user identify this household.
	 */
	public String toString() { 
		String s = String.format("%s \"%s/%d\"",
		    getClass().getSimpleName(),
		    getParentHousehold().getID(),
		    getRealizationNumber());
		s += " at (" + Double.toString( getEasting()) + ", " + Double.toString( getNorthing()) + ")";
		return s;
	}

	/**
	 * Like toString(), but also includes all attribute values.
	 * @return
	 */
	public String toStringLong() {
	    StringBuffer buf = new StringBuffer();
	    buf.append(toString()).append(" values [");
	    for(int a=0; a<getParentHousehold().getNumberAttributes(); a++) {
            if(a > 0)
                buf.append(", ");
	        buf.append(getParentHousehold().getAttributeValue(a));
	    }
	    buf.append(']');
	    return buf.toString();
	}
	
	/**
	 * Print out full contents of this realization.
	 * @param out stream to receive output
	 */
	public void dump(PrintStream out) {
	    out.println(toString());
	    
		PumsHousehold parent = getParentHousehold();
		for( int v = 0; v < parent.getNumberAttributes(); v++) {
            int value = parent.getAttributeValue( v);
		    out.format("  %s: %s\n", 
		               parent.getAttributeName( v),
		               value);
		}
	}
}
