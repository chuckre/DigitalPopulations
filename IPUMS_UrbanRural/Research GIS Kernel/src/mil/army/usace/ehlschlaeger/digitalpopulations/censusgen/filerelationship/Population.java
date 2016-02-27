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
public class Population {
    /**
     * Table containing partial sample of population data (i.e. US census PUMS).
     */ 
    @XmlAttribute public String table;
    
    /** Column in table which indicates which household each person is in. */
    @XmlAttribute public String household;

    public Population() {
    }
    
    public Population(String table, String household) {
        super();
        this.table = table;
        this.household = household;
    }

    public void validate() {
        if(ObjectUtil.isBlank(table))
            throw new DataException("population 'table' attribute is mandatory");
        if(ObjectUtil.isBlank(household))
            throw new DataException("population 'household' attribute is mandatory");
    }

    /**
     * @return true if all fields are blank or null
     */
    public boolean isBlank() {
        return ObjectUtil.isBlank(table) && ObjectUtil.isBlank(household);
    }
}
