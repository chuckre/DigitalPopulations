package mil.army.usace.ehlschlaeger.digitalpopulations.pqfile;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * One PumsQuery condition. Requirements on this object are very loose; PQAnd
 * does the parsing and validation.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PQCondition implements PQNode {
    /**
     * Table containing value to test. Value must be 'households', 'household',
     * 'hoh', or 'h' for households; or 'population', 'pop', or 'p' for people.
     * Not case sensitive.
     */
    @XmlAttribute(name="table", required=true)
    public String table;
    
    /**
     * Name of column to test.
     */
    @XmlAttribute(name="field", required=true)
    public String field;

    /**
     * Spec this if field must precisely match a value. Value must be a number.
     * If present, min and max must be absent.
     */
    @XmlAttribute(name="equals", required=false)
    public String equals;

    /**
     * Spec this if value must be greater than or equal to a value. Value must
     * be a number. If present, equals must be absent, and max is optional.
     */
    @XmlAttribute(name="min", required=false)
    public String min;

    /**
     * Spec this if value must be less than or equal to a value. Value must be a
     * number. If present, equals must be absent, and min is optional.
     */
    @XmlAttribute(name="max", required=false)
    public String max;

    
    /**
     * Create blank instance.
     */
    public PQCondition() {
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<condition");
        buf.append(" table=\"").append(table).append('"');
        buf.append(" field=\"").append(field).append('"');
        if(ObjectUtil.isBlank(equals)) {
            buf.append(" min=\"").append(min).append('"');
            buf.append(" max=\"").append(max).append('"');
        }
        else
            buf.append(" equals=\"").append(equals).append('"');
        buf.append("/>");
        return buf.toString();
    }
}
