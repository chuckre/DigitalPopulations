package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;


import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;




/**
 * Part of FittingCriteria.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class TraitWeight extends TraitRefElement {
    /** Weighting value to apply to trait. */
    @XmlAttribute(required = true)
    public double weight;

    
    /** Construct blank object. */
    public TraitWeight() {
    }

    /** Construct populated object. */
    public TraitWeight(String id, String regionTrait, String desc, double weight) {
        super(id, regionTrait, desc);
        this.weight = weight;
    }
    
    /**
     * Perform some simple validation.
     */
    @Override
    public void validate() {
        super.validate();
        if (weight <= 0) // || weight > 1)
            throw new DataException("Weight must be greater than 0: "+toString());
    }
    
    /**
     * Contents of the element, formatted as XML.
     */
    @Override
    public String toString() {
        StringBuffer buf = startToString("trait");
        buf.append(" weight=\"").append(weight).append("\"");
        buf.append("/>");
        return buf.toString();
    }
}
