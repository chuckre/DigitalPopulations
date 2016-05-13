package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.HashCodeUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * One of the <code>&lt;trait&gt;</code> rows from the relationship file.
 * <p>
 * <b>regionTrait &divide; regionTotal</b> forms a proportion that specifies the
 * percentage of 'things' (people or households) within a region that should
 * have a certain trait. <b>pumsTraitField &divide; pumsTotalField</b> specifies
 * how to calculate the corresponding proportion from a collection of household
 * objects. ConflatePumsQueryWithTracts will create and place households so that
 * the calculated 'pums' proportion is as close as possible to the target
 * 'region' proportion.
 * <p>
 * Traits can be continuous or classification. Continuous traits, like income or
 * population counts, can have any value, and this value will be summed or
 * averaged as desired. Traits are continuous by default. Specifying the
 * <b>pumsTraitContinuous</b> attribute makes it possible to ignore certain
 * records (i.e. values outside the given range will be considered "no-data",
 * and will not be tallied at all.)
 * <p>
 * Classification traits are created by specifying <b>pumsTraitSelect</b>.
 * Things are simply considered to be "in class" or "not in class".
 * pumsTraitSelect specifies which values are considered in-class. The
 * underlying column can be coded (i.e. 1 for males, 2 for females) or
 * continuous (i.e. income), and pumsTraitSelect can specify discrete values,
 * ranges, or both.
 * <p>
 * Alternatively, traits can describe an "attribute map" consisting of a raster
 * map of goal values, and a description of which household or population
 * attribute should match the goal. 'attribute' specifies the raster map ID (for
 * a file described elsewhere in the relationship file), and
 * pumsTraitTable/pumsTraitField names the column in the data that should match.
 * <p>
 * <b>Note that</b> 'region' properties and 'attribute' properties cannot be
 * used simultaneously. One one set can be used to describe goal values for a
 * trait.
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
                      "regionTable", "regionTrait", "regionTotal",
                      "attribute", "attributeSelect",
                      "pumsTraitTable", "pumsTraitField", "pumsTraitSelect", "pumsTraitContinuous",
                      "pumsTotalTable", "pumsTotalField"})
public class PumsTrait extends Trait implements Serializable{
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

    /** Legal values for table-type fields. */
    public enum Type { HOUSEHOLDS, POPULATION }
    
    /**
     * REGION GOAL: ID of region table to use as source of goal data.
     * Optional if only one region table was provided.
     * Use this or the attribute* fields, not both.
     */
    @XmlAttribute public String regionTable;
    
    /**
     * REGION GOAL: Column in region table which provides the number of items with a
     * specific trait in each region.  Used as numerator for target proportion.
     * Use this or the attribute* fields, not both.
     */
    @XmlAttribute public String regionTrait;

    /**
     * REGION GOAL: Column in region table which provides the total number of people.
     * Used as denominator for target proportion.  Specify "1" if regionTrait
     * is already the target proportion.
     * Use this or the attribute* fields, not both.
     */
    @XmlAttribute public String regionTotal;
    
    /**
     * ATTRIBUTE GOAL: ID of attribute map to use as source of goal data.
     * Use this or the region* fields, not both.
     */
    @XmlAttribute public String attribute;

    /**
     * ATTRIBUTE GOAL: Subset of attribute map values considered acceptable.
     * Use this or the region* fields, not both.
     * Format is a comma-separated list of numbers and ranges, i.e. "1,4,7-9".
     */
    @XmlAttribute public String attributeSelect;
    
    /**
     * STATS NUMERATOR: Whether <code>pumsTraitField</code> refers to a column
     * from the population table or the households table.
     */
    @XmlAttribute(required=true) public Type pumsTraitTable;

    /**
     * STATS NUMERATOR: Data column or fixed value that provides the trait.
     * Field is considered continuous by default, and will be summed over all
     * appropriate objects.
     */
    @XmlAttribute(required=true) public String pumsTraitField;

    /**
     * STATS NUMERATOR: If regionTrait only describes a subset of the trait
     * covered by pumsTraitField, then this specifies which values are included
     * in that subset.  If specified, trait becomes a classification.
     * Format is a comma-separated list of numbers and ranges, i.e. "1,4,7-9".
     */
    @XmlAttribute(required=false) public String pumsTraitSelect;
    
    /** STATS NUMERATOR: Range of values to be considered */
    @XmlAttribute(required=false) public String pumsTraitContinuous;
    
    /**
     * STATS DENOMINATOR: Whether <code>pumsTotalField</code> refers to a column
     * from the population table or the households table.
     */
    @XmlAttribute public Type pumsTotalTable;

    /**
     * STATS DENOMINATOR: Data column or fixed value that provides the
     * denominator. Field is considered continuous by default, and will be
     * summed over all appropriate objects.
     */
    @XmlAttribute public String pumsTotalField;
    
    
    /** Construct a new blank instance. */
    public PumsTrait() {
    }

    /**
     * Indicates whether some other object is "equal to" this one. 
     * @see Object.equals()
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PumsTrait))
            return false;
        
        PumsTrait t1 = this;
        PumsTrait t2 = (PumsTrait)obj;
        boolean eq = 
               ObjectUtil.objEquals(t1.id, t2.id)
            && ObjectUtil.objEquals(t1.desc, t2.desc)
            
            && ObjectUtil.objEquals(t1.regionTable, t2.regionTable)
            && ObjectUtil.objEquals(t1.regionTrait, t2.regionTrait)
            && ObjectUtil.objEquals(t1.regionTotal, t2.regionTotal)
            
            && ObjectUtil.objEquals(t1.attribute,       t2.attribute)
            && ObjectUtil.objEquals(t1.attributeSelect, t2.attributeSelect)
            
            &&                      t1.pumsTraitTable ==    t2.pumsTraitTable
            && ObjectUtil.objEquals(t1.pumsTraitField,      t2.pumsTraitField)
            && ObjectUtil.objEquals(t1.pumsTraitSelect,     t2.pumsTraitSelect)
            && ObjectUtil.objEquals(t1.pumsTraitContinuous, t2.pumsTraitContinuous)
            
            && ObjectUtil.objEquals(t1.pumsTotalTable, t2.pumsTotalTable)
            && ObjectUtil.objEquals(t1.pumsTotalField, t2.pumsTotalField)
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
        int h = HashCodeUtil.start();
        // Don't need to hash everything,
        // just the major values.
        h = HashCodeUtil.hash(h, id);
        h = HashCodeUtil.hash(h, desc);
        
        h = HashCodeUtil.hash(h, regionTable);
        h = HashCodeUtil.hash(h, regionTrait);
        h = HashCodeUtil.hash(h, regionTotal);

        h = HashCodeUtil.hash(h, attribute);
        
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
    protected void afterUnmarshal(Unmarshaller um, Object parent) {
        //Don't validate; it prevents the GUI from loading the file.
        //validate();
    }

    /**
     * Create default desc if missing.
     * Not automatic; must be called manually before saving.
     */
    public void preSave() {
        // Generate default desc, if missing.
        if(ObjectUtil.isBlank(desc)) {
            String when = "";
            if(pumsTraitSelect != null)
                when = " when " + pumsTraitSelect;
            else if(pumsTraitContinuous != null)
                when = " when " + pumsTraitContinuous;
            desc = String.format("%s.%s%s",
                                 pumsTraitTable,
                                 pumsTraitField,
                                 when);
        }
    }

    /**
     * Perform some simple validation.
     */
    public void validate() {
        ArrayList<String> errs = new ArrayList<String>();

        if(id == null)
            errs.add("id is missing.");
        
        // Validate goal (mandatory)
        if(ObjectUtil.isBlank(regionTrait)) {
            if(ObjectUtil.isBlank(attribute))
                errs.add("Goal is missing - either regionTrait or attribute must be specified.");
        }
        else {
            if(! ObjectUtil.isBlank(attribute))
                errs.add("Goal conflict - only one of regionTrait or attribute can be specified.");
            if(ObjectUtil.isBlank(regionTotal))
                errs.add("regionTotal is missing.");
        }
            
        // Validate numerator (mandatory)
        if(pumsTraitTable == null)
            errs.add("pumsTraitTable is missing or invalid.");
        if(ObjectUtil.isBlank(pumsTraitField))
            errs.add("pumsTraitField is missing.");
        if(! ObjectUtil.isBlank(pumsTraitSelect) && ! ObjectUtil.isBlank(pumsTraitContinuous))
            errs.add("pumsTraitSelect and pumsTraitContinuous cannot both be specified.");

        // Validate denominator (optional)
        if(pumsTotalTable == null && ! ObjectUtil.isBlank(pumsTotalField))
            errs.add("pumsTotalTable is missing or invalid.");
        if(pumsTotalTable != null && ObjectUtil.isBlank(pumsTotalField))
            errs.add("pumsTotalField is missing.");

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
        if(! ObjectUtil.isBlank(regionTrait)) {
            return String.format("Trait[id=\"%s\" desc=\"%s\" for %s%s/%s]",
                id,
                desc,
                (ObjectUtil.isBlank(regionTable) ? "" : regionTable + ":"),
                regionTrait, regionTotal);
        }
        else if(! ObjectUtil.isBlank(attribute)) {
            return String.format("Trait[id=\"%s\" desc=\"%s\" for map \"%s\"]",
                id,
                desc,
                attribute);
        }
        else {
            return String.format("Trait[id=\"%s\" desc=\"%s\"]",
                id,
                desc);
        }
    }
}
