package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;



/**
 * "Expansion factor" is the number of clones we will create of the records in
 * the households table. Phase 1 is dedicated to computing this value, but this
 * element can be used to adjust the result before it's passed to the later
 * phases.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class ExpansionFactor {
    /**
     * Percentage trust in the result of phase 1, in percent. "100" will use the
     * result of phase 1; "0" will use 'factor' and 'std-dev'; and a value
     * between will use a weighted average of the two. (i.e. "43" is interpreted
     * as 0.43 * phase_1 + 0.57 * fixed_factor, where phase_1 is the result of
     * Digital Population's phase 1, and fixed_factor is the value derived from
     * the 'factor' and 'std-dev' attributes.)
     */
    @XmlAttribute(name = "trust", required = true)
    public Double trust;

    /**
     * Quantity or name of column to use as expansion factor. If absent, the
     * naive multiplier will be use (e.g. number of households in map divided by
     * number of archtypes in households table.) If a number is provided, that
     * will be the factor for every household. If a name is provided, that
     * column in the households table will provide the factor for each
     * household.
     */
    @XmlAttribute(name = "factor", required = false)
    public String factorCol;

    /**
     * Standard deviation for a random factor. If absent, the default is zero
     * (i.e. the 'factor' attribute will be used as-is.) If present, a random
     * value will be selected whose mean is 'factor' and whose standard
     * deviation is given here.
     */
    @XmlAttribute(name = "std-dev", required = false)
    public String stdDevCol;

    /**
     * Perform simple validation.
     */
    public void validate() {
        // DEV NOTE:
        // "required=true" does nothing; it's only used if you ask JAXB to generate a schema.
        // So instead, we need to use an object (instead of a primitive) so we can detect
        // null, then validate the attribute ourself.
        DataException.verifyNotNull(trust, "'trust' attribute is required in "+toString());
        if(trust < 0 || trust > 100.0)
            throw new DataException("'trust' attribute must be >= 0 and <= 100.");

        if(factorCol != null) {
            try {
                double d = Double.parseDouble(factorCol);
                // <0 is useless
                // <1 is weird, but usable if stdDev is provided
                if(d < 0)
                    throw new DataException("'factorCol' value must be >= 0");
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        
        if(stdDevCol != null) {
            try {
                double d = Double.parseDouble(stdDevCol);
                // <0 is useless
                // ==0 ok, it's the default
                if(d < 0)
                    throw new DataException("'stdDevCol' value must be >= 0");
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        // Unfortunately, element name is determined by parent. I'll just assume
        // it's "phase1".
        buf.append(String.format("<phase1"));
        if(trust != null)
            buf.append(String.format(" trust=\"%s\"", trust));
        if(factorCol != null)
            buf.append(String.format(" factor=\"%s\"", factorCol));
        if(stdDevCol != null)
            buf.append(String.format(" std-dev=\"%s\"", stdDevCol));
        buf.append("/>");
        return buf.toString();
    }
}
