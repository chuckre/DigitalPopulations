package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Provides specs for computing precise coordinates (easting/northing) for
 * households. Part of the FittingCriteria file. Used by phase 4.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PreciseLocation {
    /**
     * List of specs that control how we will compute precise locations for each
     * household. Can contain 'cluster' and 'match' elements in any order.
     * <UL>
     *   <LI>&lt;cluster&gt; says
     *     "derive clustering statistics from the given trait", and can only be used
     *     with normal tract-based traits (with 'regionTrait' and 'regionTotal').
     *   <LI>&lt;match&gt; says "just do whatever the trait says to do", and must
     *     identify one of the new attribute-map traits (with 'attribute').
     * </UL>
     * Note that when loading from an XML file, JAXB will leave this null if there
     * are no suitable child elements.
     */
    @XmlElements({
        @XmlElement(name="cluster", type=ClusterSpec.class),
        @XmlElement(name="match", type=MatchSpec.class)})
    public List<TraitRefElement> traits = null;

    
    public PreciseLocation() {
    }

    /**
     * Append element, creating container if necessary.
     * @param spec
     */
    public void add(TraitRefElement spec) {
        assert spec instanceof ClusterSpec || spec instanceof MatchSpec;
        if(traits == null)
            traits = new ArrayList<TraitRefElement>();
        traits.add(spec);
    }
    
    /**
     * Remove element from container, deleting it if it is now empty.
     * @param spec
     */
    public void remove(ClusterSpec spec) {
        if(traits != null) {
            traits.remove(spec);
            if(traits.isEmpty())
                traits = null;
        }
    }
    
    /**
     * Perform some simple validation.
     */
    public void validate() {
        ArrayList<String> errs = new ArrayList<String>();
        
        if(traits != null) {
            for(TraitRefElement spec : traits) {
                try {
                    spec.validate();
                } catch (Exception e) {
                    errs.add(e.getMessage());
                }
            }
        }
        
        if(!errs.isEmpty())
            throw new DataException(ObjectUtil.join(errs, "\n  "));
    }
    
    @Override
    public String toString() {
        return traits.toString();
    }
}
