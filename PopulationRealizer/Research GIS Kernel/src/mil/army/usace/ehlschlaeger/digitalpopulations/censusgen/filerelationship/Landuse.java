package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
@XmlType(propOrder = {"map", "comment", "vacant", "combos"})
public class Landuse {
    /** Land-use map file. */
    @XmlAttribute(required=false)
    public String map;

    // Must be 'protected' else XML system gets confused between this and getComment.
    protected String comment = null;
    
    /** Classes that are vacant. */
    @XmlElement(required=true)
    public LanduseVacant vacant = new LanduseVacant();
    
    /** List of combination specs. */
    @XmlElement(name="combination", required=true)
    public List<LanduseCombination> combos = new ArrayList<LanduseCombination>();

    
    
    /**
     * Default constructor.
     */
    public Landuse() {
    }

    /**
     * Notes on class combos, ie. the classification system used by the file,
     * and the one these combos convert it into.
     */
    @XmlElement(required=false)
    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        if(ObjectUtil.isBlank(comment))
            this.comment = null;
        else
            this.comment = comment;
    }

    /**
     * Check for logic errors.  Rules:
     * <UL>
     *   <LI> &lt;combination target&gt; cannot appear in &lt;vacant classes&gt;
     *   <LI> 'classes' cannot spec a value that appears in another 'classes'
     * </UL>
     * 
     */
    public void validate() {
        ArrayList<String> msgs = new ArrayList<String>();

        DataException.verifyNotBlank(map, "land-use map file is mandatory");
        
        // If a map is not provided, then this element is useless, and anything goes.
        BitSet used = new BitSet();
        BitSet vac = LanduseCombination.makeBitSet(vacant.classes);
        
        for(LanduseCombination combo : combos) {
            if(vac.get(combo.target))
                msgs.add(String.format("target must not appear in vacant list: %s", combo));
            
            BitSet neu = LanduseCombination.makeBitSet(combo.classes);
            if(vac.intersects(neu))
                msgs.add(String.format("classes must not overlap vacant classes: %s", combo));
            if(used.intersects(neu))
                msgs.add(String.format("classes must not overlap any other 'classes' attribute: %s", combo));
            
            used.or(neu);
        }
        
        if(! msgs.isEmpty())
            throw new DataException(ObjectUtil.join(msgs, "\n"));
    }
    
    /**
     * Find a combination spec with the given target class.
     * 
     * @param target 'target' property of the combo to find
     * @return requested combo spec, or null if not found
     */
    public LanduseCombination findComboByTarget(int target) {
        for (LanduseCombination combo : combos) {
            if(combo.target == target)
                return combo;
        }
        return null;
    }
    
    /**
     * Find a combination spec that covers the given class value.
     * 
     * @param member value to look for within 'classes' property
     * @return requested combo spec, or null if not found
     */
    public LanduseCombination findComboByMember(int member) {
        for (LanduseCombination combo : combos) {
            if(combo.contains(member))
                return combo;
        }
        return null;
    }

    /**
     * Sort list of combos in place according to target number.
     */
    public void sortCombos() {
        Collections.sort(combos, new Comparator<LanduseCombination>() {
            public int compare(LanduseCombination c1, LanduseCombination c2) {
                if(c1.target < c2.target)
                    return -1;
                else if(c1.target > c2.target)
                    return +1;
                else
                    return 0;
            }
        });
    }
}
