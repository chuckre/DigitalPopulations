package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.Serializable;
import java.util.Arrays;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntList;



/**
 * Manages one household archtype plus its realizations. An "archtype" is one
 * record from the households table.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PumsHousehold implements Serializable {
    public static final int NODATA_VALUE = -999999;

    /** Unique ID for this archetype. */
    protected String                         id;
    /** Names for the values in array 'values'. */
    protected CSVTableNoSwing                       schema;
    /** Values corresponding to the names in 'schema'. */
    protected int[]                          values;
    /** List of tracts in which our realizations (clones of this archetype) must be placed. */
    protected IntList                        rznTracts;
    /** Data for the people who live in this house. */
    protected PumsPopulation[]               membersOfHousehold;

    /**
     * Construct empty instance.
     */
    protected PumsHousehold() {
    }
    
    /**
     * Construct instance from given data.
     * 
     * @param schema names of attributes
     * @param attributeValues value for every attribute given in 'schema'
     * @param uniqueID ID code for this object to distinguish it from all other instances
     */
    public PumsHousehold(CSVTableNoSwing schema, int[] attributeValues, String uniqueID) {
        this.schema = schema;
        this.values = attributeValues;
        this.rznTracts = new ArrayIntList();
        this.id = uniqueID;
    }
    
    /** Unique ID for this archetype. */
    public String getID() {
        return id;
    }
    
    /**
     * Change this object's unique ID.
     * 
     * @param uid new UID
     */
    public void setID(String uid) {
        id = uid;
    }
    
    public CSVTableNoSwing getSchema() {
        return schema;
    }

    public PumsPopulation[] getMembersOfHousehold() {
		return membersOfHousehold;
	}

    public int getNumberMembers() {
        if(membersOfHousehold == null)
            return 0;
        else
            return membersOfHousehold.length;
    }
    
    /**
     * Replace array of people with an entirely new array.
     * 
     * @param newMembers
     *            array of the people within this house
     */
	public void setMembersOfHousehold(PumsPopulation[] newMembers) {
	    if(newMembers == null || newMembers.length == 0)
	        membersOfHousehold = null;
	    else
	        membersOfHousehold = Arrays.copyOf(newMembers, newMembers.length);
	}

	/**
	 * Erase all our realizations.
	 */
    public void clearRealizations() {
        rznTracts.clear();
    }

    /**
     * Create a realization in a specific tract.
     * @param tractID
     */
	public void addRealization(int tractID) {
	    rznTracts.add(tractID);
	}

	/**
	 * Delete last realization in list, and return the tract it was in.
	 * @return tract from which we removed a realization
	 */
	public int removeRealization() {
	    return rznTracts.removeElementAt(rznTracts.size()-1);
	}

    /**
     * Remove occurance of given tract number. Will reduce by 1 the number of
     * realizations created in this tract.
     * 
     * @param tractID
     *            tract number to remove
     * @return true if number was actually found and removed; false if this
     *         archtype had no realizations in that tract
     */
    public boolean removeRealization(int tractID) {
        return rznTracts.removeElement(tractID);
    }
	
    /**
     * Report current number of realizations.
     * @param rzn
     * @return
     */
	public int getNumberRealizations() {
	    return rznTracts.size();
	}

	/**
	 * Report the tract number that the given realization resides within.
	 * 
	 * @param rzn
	 * @return
	 */
    public int getRealizationTract(int rzn) {
        return rznTracts.get(rzn);
    }

    /**
     * @param tractId
     *            tract number to look for
     * @return true if this archtype is directed to create at least one
     *         realization in the given tract
     */
    public boolean hasRealizationTract(int tractId) {
        return rznTracts.contains(tractId);
    }

    /**
     * Move a realization to a different tract.
     * 
     * @param rzn
     * @param newTract
     */
    public void moveRealization(int rzn, int newTract) {
        rznTracts.set(rzn, newTract);
    }

    /**
     * Report number of attributes held by this archetype.
     * @return
     */
    public int getNumberAttributes() {
	    return values.length;
	}

    /**
     * Fetch the name of an attribute.
     * 
     * @param attribute
     * @return
     */
	public String getAttributeName( int attribute) {
		return(schema.getColumnName(attribute));
	}

	/**
     * Find index of named column.
     * @param attributeName name of attribute to find
     * @return index of named column
     * @throws DataException if column isn't found
	 */
	public int getAttributeIndex(String attributeName) {
	    return schema.findColumn(attributeName);
	}
	
	/**
	 * Fetch the value of an attribute.
	 * 
	 * @param attribute
	 * @return value of attribute at given index
	 */
	public int getAttributeValue( int attribute) {
		return( values[ attribute]);
	}

	/**
     * Fetch the value of an attribute.
	 * 
     * @param attributeName name of attribute to find
     * @return value of attribute
     * @throws DataException if column isn't found
	 */
	public int getAttributeValue(String attributeName) {
	    return values[getAttributeIndex(attributeName)];
	}
	
	public void setAttributeValue(int index, int value) {
	    values[index] = value;
	}
	
	public void setAttributeValue(String name, int value) {
	    values[getAttributeIndex(name)] = value;
	}
	
    /**
     * Builds brief string to help user identify this archtype.
     */
    public String toString() {
        return String.format("%s \"%s\"", getClass().getSimpleName(), id);
    }

    /**
     * Like toString(), but also includes all attribute values.
     * @return
     */
    public String toStringLong() {
        StringBuffer buf = new StringBuffer();
        buf.append(toString()).append(" values [");
        for(int a=0; a<getNumberAttributes(); a++) {
            if(a > 0)
                buf.append(", ");
            buf.append(getAttributeValue(a));
        }
        buf.append(']');
        return buf.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        PumsHousehold o1 = this;
        PumsHousehold o2 = (PumsHousehold)obj;
        boolean eq = o1.id.equals(o2.id)
            && o1.schema == o2.schema
            && Arrays.equals(o1.values, o2.values)
            && o1.rznTracts.equals(o2.rznTracts)
            && Arrays.equals(o1.membersOfHousehold, o2.membersOfHousehold);
        return eq;
    }

    /**
     * Create a copy that shares attribute arrays, but contains a distinct copy
     * of the realization arrays. Use this to probe the value of moving
     * realizations to different tracts.
     * 
     * @return copy of this object
     */
    public PumsHousehold cloneRzn() {
        PumsHousehold newHoh = new PumsHousehold();
        // Copy pointer for these fields.
        newHoh.id = this.id;
        newHoh.schema = this.schema;
        newHoh.values = this.values;
        newHoh.membersOfHousehold = this.membersOfHousehold;
        // Deep-clone rzns so they can be changed.
        newHoh.rznTracts = new ArrayIntList(this.rznTracts);
        return newHoh;
    }

    /**
     * Create copy that shares schema, but copies values and realizations. Use
     * this to create a new archtype that starts with the values in another
     * archtype.
     * 
     * @return copy of this object
     */
    public PumsHousehold cloneValues() {
        PumsHousehold newHoh = new PumsHousehold();
        // Copy pointer for these fields.
        newHoh.id = this.id;
        newHoh.schema = this.schema;
        
        // Deep-clone these fields.
        newHoh.values = Arrays.copyOf(this.values, this.values.length);
        newHoh.rznTracts = new ArrayIntList(this.rznTracts);
        
        if(this.membersOfHousehold != null) {
            newHoh.membersOfHousehold = new PumsPopulation[this.membersOfHousehold.length];
            for (int i = 0; i < this.membersOfHousehold.length; i++) {
                newHoh.membersOfHousehold[i] = this.membersOfHousehold[i].cloneValues();
            }
        }
            
        return newHoh;
    }
}
