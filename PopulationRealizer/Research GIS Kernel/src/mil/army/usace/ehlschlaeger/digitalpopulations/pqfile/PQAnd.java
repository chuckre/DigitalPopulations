package mil.army.usace.ehlschlaeger.digitalpopulations.pqfile;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.apache.commons.lang.ArrayUtils;


/**
 * List of conditions, ANDed together.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 *
 * @author William R. Zwicky
 */
public class PQAnd implements PQNode {
    /** Values for PQCondition.table that indicate household table. */
    protected static final String[] HOH_KEYS = {"households", "household", "hoh", "h"};
    /** Values for PQCondition.table that indicate population table. */
    protected static final String[] POP_KEYS = {"population", "pop", "p"};
    
    @XmlElement(name = "condition")
    public List<PQCondition> conditions = new ArrayList<PQCondition>();

    
    /**
     * Create blank instance.
     */
    public PQAnd() {
    }

    /**
     * Add the nodes in this object to a query.
     * 
     * @param hohSchema schema for households table
     * @param popSchema schema for population table
     * @param query object to receive query terms
     */
    public void addTo(PumsQuery query, List<String> hohSchema, List<String> popSchema) {
        boolean[] isPopulations = new boolean[conditions.size()];
        int[] variables = new int[conditions.size()];
        int[] minValues = new int[conditions.size()];
        int[] maxValues = new int[conditions.size()];
        
        for(int i=0; i<conditions.size(); i++) {
            PQCondition cond = conditions.get(i);
            isPopulations[i] = isTablePop(cond.table);
            
            if(isPopulations[i])
                variables[i] = popSchema.indexOf(cond.field);
            else
                variables[i] = hohSchema.indexOf(cond.field);
            if(variables[i] < 0)
                throw new DataException(String.format("Can't find column %s in table %s",
                    cond.field, cond.table));
            
            if(!ObjectUtil.isBlank(cond.equals)) {
                if(!ObjectUtil.isBlank(cond.min))
                    throw new DataException("'equals' is present; min cannot also be specified");
                if(!ObjectUtil.isBlank(cond.max))
                    throw new DataException("'equals' is present; max cannot also be specified");
                
                int v = Integer.parseInt(cond.equals);
                minValues[i] = v;
                maxValues[i] = v;
            }
            else {
                if(ObjectUtil.isBlank(cond.min)) {
                    // if min and max are both blank, crash
                    if(ObjectUtil.isBlank(cond.max))
                        throw new DataException("One of 'equals', 'min', or 'max' must be specified");
                    // else default min
                    minValues[i] = Integer.MIN_VALUE;
                }
                else
                    // min is not blank; parse it
                    minValues[i] = Integer.parseInt(cond.min);
                
                if(ObjectUtil.isBlank(cond.max))
                    // default max
                    maxValues[i] = Integer.MAX_VALUE;
                else
                    // parse max
                    maxValues[i] = Integer.parseInt(cond.max);
                
            }
        }
        
        query.addAndQuery(isPopulations, variables, minValues, maxValues);
    }

    /**
     * Parse PQCondition.table and return 'true' if households is indicated, or
     * 'false' if not.
     * 
     * @param value string to parse
     * @return boolean
     * @throws DataException if value is not legal
     */
    public static boolean isTablePop(String value) {
        value = value.toLowerCase();
        if(ArrayUtils.indexOf(HOH_KEYS, value) >= 0)
            return false;
        else if(ArrayUtils.indexOf(POP_KEYS, value) >= 0)
            return true;
        else
            throw new DataException("Can't parse 'table' attribute: "+value);
    }
}
