package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;

/**
 * Base class for elements that refer to a trait defined elsewhere. Trait can be
 * identified by its id, regionTrait, or description attributes. Only one of
 * these can be specified.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class TraitRefElement {

    /** ID string of relevant trait. */
    @XmlAttribute(required = false)
    public String id;
    /** regionTrait attribute of relevant trait. */
    @XmlAttribute(required = false)
    public String regionTrait;
    /** Description attribute of relevant trait. */
    @XmlAttribute(required = false)
    public String desc;

    /** Construct blank object. */
    public TraitRefElement() {
    }

    /** Construct populated object. */
    public TraitRefElement(String id, String regionTrait, String desc) {
        this.id = id;
        this.regionTrait = regionTrait;
        this.desc = desc;
    }

    /**
     * Perform some simple validation.
     */
    public void validate() {
        int specs = 0;
        if(id != null)
            specs++;
        if(regionTrait != null)
            specs++;
        if(desc != null)
            specs++;
            
        if (specs == 0)
            throw new DataException("One of id, regionTrait, or desc must be provided: "+toString());
        if(specs > 1)
            throw new DataException("Only one of id, regionTrait, and desc can be provided: "+toString());
    }

    /**
     * Find this trait in a list of traits. All of id,rtrait,desc will be used,
     * if provided. If none are specified, the first element of Trait will be
     * returned, though this is technically an error and should have been caught
     * by validate().
     * 
     * @param traits
     *            list of Trait objects to search
     * @return specified trait, or null if not found or list is empty
     */
    public Trait find(List<Trait> traits) {
        for (Trait trait : traits) {
            boolean eq = true;
            if (eq && id != null)
                eq = id.equals(trait.id);
            if (eq && desc != null)
                eq = desc.equals(trait.desc);
            if(trait instanceof PumsTrait) {
                PumsTrait pt = (PumsTrait)trait;
                if (eq && regionTrait != null)
                    eq = regionTrait.equals(pt.regionTrait);
            }
            if (eq)
                return trait;
        }
        return null;
    }

    /**
     * Construct start of XML element.  Sub-class will finish and close element.
     * @return StringBuffer containing start of XML element
     */
    protected StringBuffer startToString(String elementName) {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(elementName);
        if(id != null)
            buf.append(" id=\"").append(id).append("\"");
        if(regionTrait != null)
            buf.append(" regionTrait=\"").append(regionTrait).append("\"");
        if(desc != null)
            buf.append(" desc=\"").append(desc).append("\"");
        return buf;
    }
    
    /**
     * Contents of the element, formatted as XML.
     */
    public String toString() {
        StringBuffer buf = startToString("trait");
        buf.append("/>");
        return buf.toString();
    }
}
