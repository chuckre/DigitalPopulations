package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import javax.xml.bind.annotation.XmlAttribute;


/**
 * Part of FileRelationship.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class LanduseVacant {
    /** Description of classes to be treated as vacant. */
    @XmlAttribute public String desc = "Classes without households";
    
    /**
     * List of land-use category codes that are unpopulated (comma-separated
     * list of ints and ranges).
     */
    @XmlAttribute public String classes;
    
    /**
     * Determine if one number is within a our list of classes.
     * 
     * @param testClass
     *            integer to test
     * @return true if 'classes' includes the given number
     */
    public boolean contains(int testClass) {
        return LanduseCombination.contains(this.classes, testClass);
    }
}
