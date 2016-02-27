package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.HashCodeUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * <forbid> explicitly disallows the placement of certain households into
 * certain map cells. Households are selected based on their attributes, and map
 * cells are described by a custom map file.
 * <P>
 * Households with attributes matching the household selector will not be placed
 * in any cells described by map cell selector. Households that do not match
 * household selector will be ignored by this constraint, allowing them to be
 * placed anywhere. Cells that do not match the map selector will be allowed to
 * receive any kind of household. Only selected households will be constrained,
 * and they will be constrained only from the selected cells.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
@XmlType(propOrder = {"id", "desc",
                      "pumsTraitTable", "pumsTraitField", "pumsTraitSelect",
                      "map", "mapSelect"})
public class Forbid extends Trait {
    /*
     ** *****
     **
     ** WARNING!
     ** This method has custom equals() and hashCode(). If any
     ** changes are made to this class's fields, those methods
     ** must be updated.
     **
     ** *****
     */

    /**
     * MAP CELL SELECTOR: Raster map that provides cell attributes. Value is a
     * path and name of an ESRI ASCII file, relative to this fitting criteria
     * file.
     */
    @XmlAttribute(required=true) public String map;

    /**
     * MAP CELL SELECTOR: Map cell values to match. Selected households will NOT
     * be placed in any cells where given map has this value. Format is a
     * comma-separated list of numbers and ranges, i.e. "1,4,7-9".
     */
    @XmlAttribute(required=true) public String mapSelect;
    
    /**
     * HOUSEHOLD SELECTOR: Whether <code>pumsTraitField</code> refers to a
     * column from the population table or the households table.
     */
    @XmlAttribute(required=true) public Type pumsTraitTable;

    /**
     * HOUSEHOLD SELECTOR: Data column that provides the trait.
     */
    @XmlAttribute(required=true) public String pumsTraitField;

    /**
     * HOUSEHOLD SELECTOR: Only households where pumsTraitField has these values
     * will be constrained. Format is a comma-separated list of numbers and
     * ranges, i.e. "1,4,7-9".
     */
    @XmlAttribute(required=true) public String pumsTraitSelect;
    
    
    
    /** Construct a new blank instance. */
    public Forbid() {
    }

    /**
     * Indicates whether some other object is "equal to" this one. 
     * @see Object.equals()
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Forbid))
            return false;

        Forbid t1 = this;
        Forbid t2 = (Forbid)obj;
        boolean eq = 
               super.equals(obj)
            
            && ObjectUtil.objEquals(t1.map,       t2.map)
            && ObjectUtil.objEquals(t1.mapSelect, t2.mapSelect)
            
            &&                      t1.pumsTraitTable ==    t2.pumsTraitTable
            && ObjectUtil.objEquals(t1.pumsTraitField,      t2.pumsTraitField)
            && ObjectUtil.objEquals(t1.pumsTraitSelect,     t2.pumsTraitSelect)
            ;
        return eq;
    }

    /**
     * Returns a hash code value for the object. This method is supported for
     * the benefit of hashtables such as those provided by java.util.Hashtable.
     * @see Object.hashCode()
     */
    @Override
    public int hashCode() {
        int h = super.hashCode();
        
        // Don't need to hash everything,
        // just the major values.
        h = HashCodeUtil.hash(h, map);
        
        h = HashCodeUtil.hash(h, pumsTraitTable);
        h = HashCodeUtil.hash(h, pumsTraitField);
        
        return h;
    }

    /**
     * Perform validation after loading one element from XML.
     * 
     * @param um
     *            Unmarshaller in charge of the process
     * @param parent
     *            object that contains this instance (ie. FileRelationship)
     */
//    protected void afterUnmarshal(Unmarshaller um, Object parent) {
//        //Don't validate; it prevents the GUI from loading the file.
//        //validate();
//    }

    /**
     * Perform some simple validation.
     */
    public void validate() {
        ArrayList<String> errs = new ArrayList<String>();

        if(ObjectUtil.isBlank(id))
            errs.add("id is missing.");
        if(ObjectUtil.isBlank(map))
            errs.add("Map file is missing.");
        if(ObjectUtil.isBlank(mapSelect))
            errs.add("mapSelect is missing.");
        
        // Validate numerator (mandatory)
        if(pumsTraitTable == null)
            errs.add("pumsTraitTable is missing or invalid.");
        if(ObjectUtil.isBlank(pumsTraitField))
            errs.add("pumsTraitField is missing.");
        if(ObjectUtil.isBlank(pumsTraitSelect))
            errs.add("pumsTraitSelect is missing.");

        if(!errs.isEmpty()) {
            String msg = "Errors in " + toString() + ":\n  ";
            msg += ObjectUtil.join(errs, "\n  ");
            throw new DataException(msg);
        }
    }
    
    /**
     * Generate a brief identifying string for this object.
     */
    public String toString() {
        if(! ObjectUtil.isBlank(desc))
            return String.format("%s[id=\"%s\" desc=\"%s\"]",
                getClass().getSimpleName(),
                id,
                desc);
        else {
            return String.format("%s[id=\"%s\" for %s.%s]",
                getClass().getSimpleName(),
                id,
                pumsTraitTable, pumsTraitField);
        }
    }
}
