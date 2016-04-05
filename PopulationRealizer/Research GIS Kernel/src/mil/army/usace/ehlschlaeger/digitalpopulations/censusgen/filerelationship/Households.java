package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;


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
public class Households {
    /**
     * Table containing partial sample of household data (i.e. US census PUMS).
     */ 
    @XmlAttribute public String table;
    
    /** Column in table which gives the unique ID for each row. (Optional) */
    @XmlAttribute public String key;
    
    /** Column in table which gives number of members. (Optional) */
    @XmlAttribute public String members;

    /** Construct new blank instance. */
    public Households() {
    }
    
    public Households(String table, String key, String members) {
        this.table = table;
        this.key = key;
        this.members = members;
    }
    
    public void validate() {
        if(ObjectUtil.isBlank(table))
            throw new DataException("households 'table' attribute is mandatory");
    }
}
