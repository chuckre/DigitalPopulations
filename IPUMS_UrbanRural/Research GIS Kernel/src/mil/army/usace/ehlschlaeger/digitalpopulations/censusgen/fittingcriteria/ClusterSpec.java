package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;



/**
 * Adds clustering specs to one of the relationship traits. Part of the
 * FittingCriteria file.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class ClusterSpec extends TraitRefElement {
    /**
     * Percentage reduction in "inertia," which is a measure of the randomness
     * of an arrangement of households. A reduction of inertia is an increase of
     * clustering, and is achieved by moving households closer together. Goal
     * will be this much lower than the "inertia" values calculated when phase 4
     * begins.
     */
    @XmlAttribute(required = true)
    public double reduction;

    /**
     * Maximum size of clusters to build. Only houses within this radius will be
     * considered for clustering. 
     * This values is in the same units as the input maps.
     */
    @XmlAttribute(required = true)
    public double distance;
    
    
    /** Construct blank object. */
    public ClusterSpec() {
    }

    /** Construct populated object. */
    public ClusterSpec(String id, String regionTrait, String desc, double reduction, double distance) {
        super(id, regionTrait, desc);
        this.reduction = reduction;
        this.distance = distance;
        
        validate();
    }

    /**
     * Perform some simple validation.
     */
    public void validate() {
        super.validate();
        if (reduction < 0.0 || reduction > 100.0)
            throw new DataException("Reduction must be between 0 and 100: "+toString());
        if (distance < 0.0)
            throw new DataException("Distance cannot be negative: "+toString());
    }

    /**
     * Contents of the element, formatted as XML.
     */
    public String toString() {
        StringBuffer buf = startToString("trait");
        buf.append(" reduction=\"").append(reduction).append("\"");
        buf.append(" distance=\"").append(distance).append("\"");
        buf.append("/>");
        return buf.toString();
    }
}
