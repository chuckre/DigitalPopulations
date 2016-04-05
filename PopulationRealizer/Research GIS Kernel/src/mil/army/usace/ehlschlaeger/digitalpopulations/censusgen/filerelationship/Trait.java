package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import mil.army.usace.ehlschlaeger.rgik.util.HashCodeUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

/**
 * Parent class for elements that can appear inside of &lt;traits&gt;.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
@XmlTransient
public class Trait {
    /*
     * DEV NOTE:
     * XmlTransient marks this class as not part of domain model (i.e. the
     * XML file cannot have Trait elements), and also makes proporder work
     * in subclasses.
     */
    
    /** GENERAL: Unique ID (in this file) for this record. */
    @XmlAttribute(required=false) public String id;
    
    /** GENERAL: Human-readable description of this trait. */
    @XmlAttribute(required=false) public String desc;


    /**
     * Indicates whether some other object is "equal to" this one. 
     * @see Object.equals()
     */
    @Override
    public boolean equals(Object obj) {
        Trait t1 = this;
        Trait t2 = (Trait)obj;
        boolean eq = 
               ObjectUtil.objEquals(t1.id, t2.id)
            && ObjectUtil.objEquals(t1.desc, t2.desc)
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
        
        return h;
    }
    
    /**
     * Generate a brief identifying string for this object.
     */
    public String toString() {
        if(ObjectUtil.isBlank(desc))
            return String.format("%s[id=\"%s\"]",
                getClass().getSimpleName(),
                id);
        else
            return String.format("%s[id=\"%s\" desc=\"%s\"]",
                getClass().getSimpleName(),
                id,
                desc);
    }

    /**
     * Called before XML is saved to disk.  Can be used to clean or reformat data.
     */
    public void preSave() {
    }
}
