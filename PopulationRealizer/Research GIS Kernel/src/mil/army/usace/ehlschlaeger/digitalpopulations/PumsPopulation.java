package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.Serializable;
import java.util.Arrays;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;



/**
 * One person from the PUMS population table. Household ID is handled separately
 * as it's a string, while the attributes are all ints.
 * <P>
 * Note there is no PumsPopulationRealization class, as people are assumed to be
 * in the same location as their household. PumsHouseholdRealization holds the
 * location on behalf of households and people.
 * <P>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class PumsPopulation implements Serializable {
    public static final int NODATA_VALUE = -999999;

    protected String   hid;
    protected CSVTableNoSwing schema;
    protected int[]    values;

	public PumsPopulation(String hohID, CSVTableNoSwing schema, int[] attributeValues) {
	    this.hid = hohID;
	    this.schema = schema;
		values = attributeValues;
	}

    /**
     * Return the ID of the household that houses this person.
     * @return
     */
    public String getHohID() {
        return hid;
    }
    
    public int getNumberAttributes() {
        return values.length;
    }

	public String getAttributeName( int attribute) {
		return(schema.getColumnName(attribute));
	}

	public int getAttributeValue( int attribute) {
		return(values[ attribute]);
	}
    
    public CSVTableNoSwing getSchema() {
        return schema;
    }

    /**
     * create copy that shares schema but clones values
     * @return
     */
    public PumsPopulation cloneValues() {
        PumsPopulation neu = new PumsPopulation(this.hid, this.schema, Arrays.copyOf(this.values, this.values.length));
        return neu;
    }

    /**
     * Test if content of another object matches this object.
     */
	@Override
	public boolean equals(Object obj) {
	    PumsPopulation o1 = this;
	    PumsPopulation o2 = (PumsPopulation)obj;
	    boolean eq = o1.hid.equals(o2.hid)
	        && o1.schema == o2.schema
	        && Arrays.equals(o1.values, o2.values);
	    return eq;
	}
}
