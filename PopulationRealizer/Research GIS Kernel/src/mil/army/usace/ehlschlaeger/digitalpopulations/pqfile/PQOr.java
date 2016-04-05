package mil.army.usace.ehlschlaeger.digitalpopulations.pqfile;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;



/**
 * List of conditions, ORed together. This is effectively the true top level of
 * the structure, as this and its children can describe one entire PumsQuery
 * object. Officially defined as containing a list of PQAnd object, but for
 * convenience lone PQCondition objects can be present, and they will be treated
 * as a PQAnd with the one condition inside.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PQOr implements PQNode {
    @XmlElements({
        @XmlElement(name="condition", type=PQCondition.class),
        @XmlElement(name="and", type=PQAnd.class)})
    public List<PQNode> conditions;
    

    /**
     * Create blank instance.
     */
    public PQOr() {
    }
    
    /**
     * Add the nodes in this object to a query.
     * 
     * @param hohSchema schema for households table
     * @param popSchema schema for population table
     * @param query object to receive query terms
     */
    public void addTo(PumsQuery query, List<String> hohSchema, List<String> popSchema) {
        for(PQNode node : conditions) {
            if(node instanceof PQCondition) {
                PQCondition cond = (PQCondition) node;
                PQAnd and = new PQAnd();
                and.conditions.add(cond);
                and.addTo(query, hohSchema, popSchema);
            }
            else if(node instanceof PQAnd) {
                PQAnd and = (PQAnd) node;
                and.addTo(query, hohSchema, popSchema);
            }
            else
                throw new DataException("PQOr cannot contain objects of type "+node.getClass().getName());
        }
    }
}
