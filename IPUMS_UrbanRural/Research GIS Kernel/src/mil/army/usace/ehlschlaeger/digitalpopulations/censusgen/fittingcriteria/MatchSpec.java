package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import javax.xml.bind.annotation.XmlAttribute;



/**
 * Refers to a trait (generally an attribute-map trait) and specifies a
 * priority. Part of the FittingCriteria file.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class MatchSpec extends TraitRefElement {
    /**
     * Elements will be used in rank order. Elements missing 'rank' will be used
     * last, in the order given in the FittingCriteria file.
     */
    @XmlAttribute(required = false)
    public Integer rank;

    /** Construct blank object. */
    public MatchSpec() {
    }

    /** Construct populated object. */
    public MatchSpec(String id, String regionTrait, String desc, int rank) {
        super(id, regionTrait, desc);
        this.rank = rank;
        validate();
    }

    /**
     * Contents of the element, formatted as XML.
     */
    public String toString() {
        StringBuffer buf = startToString("match");
        if(rank != null)
            buf.append(" rank=\"").append(rank).append("\"");
        buf.append("/>");
        return buf.toString();
    }
}
