package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.util.BitSet;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.LanduseCombination;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.TransformAttributes2double;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Build object that can compute trait value (proportion numerator) from a
 * PumsHouseholdRealization.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class PumsTraitGetter implements TransformAttributes2double {
    protected Type    table;
    protected boolean fixedValue;       // false->traitCol; true->traitValue
    protected String  traitColName;     // (for display only)
    protected int     traitCol;         // which column in table to read
    protected int     traitValue;       // what value to always return
    protected BitSet  selection = null; // classification: return 1 if value is one of these
    protected boolean hasRange  = false; // true->min/max
    protected int     min, max;         // continuous, but only within range
    protected String  label;            // user's description of this object
    

    /**
     * Build empty getter. (Used by deserialization; useless otherwise.)
     */
    public PumsTraitGetter() {
    }

    /**
     * Build object that can compute trait value (proportion numerator) from a
     * PumsHouseholdRealization. Uses pumsTrait* fields from given trait:
     * pumsTraitTable, pumsTraitField, pumsTraitSelect, and pumsTraitContinuous.
     * 
     * @param trait
     *            trait record from which this getter will be built
     * @param householdSchema
     *            column names from PUMS household table
     * @param populationSchema
     *            column names from PUMS population table
     */
    public PumsTraitGetter(PumsTrait trait, CSVTableNoSwing householdSchema, CSVTableNoSwing populationSchema) {
        table = trait.pumsTraitTable;
        
        // Parse table and field
        try {
            traitValue = Integer.parseInt(trait.pumsTraitField);
            fixedValue = true;
        }
        catch(NumberFormatException e) {
            fixedValue = false;
            traitColName = trait.pumsTraitField;
            if(table == Type.HOUSEHOLDS)
                traitCol = householdSchema.findColumn(trait.pumsTraitField);
            else {
                if(populationSchema == null)
                    throw new DataException("Population file must be specified before traits can use it: "+trait);
                traitCol = populationSchema.findColumn(trait.pumsTraitField);
            }
        }

        // Parse selection set
        if (trait.pumsTraitSelect != null) {
            if(fixedValue)
                throw new IllegalArgumentException("Can't specify a selection on a fixed value.");
            if(trait.pumsTraitContinuous != null)
                throw new IllegalArgumentException("Can't specify select and continuous at the same time.");
            this.selection = LanduseCombination.makeBitSet(trait.pumsTraitSelect);
        }

        // Parse no-data specifier
        if (trait.pumsTraitContinuous != null) {
            if(fixedValue)
                throw new IllegalArgumentException("Can't specify a range on a fixed value.");
            hasRange = true;
            // skip index 0 in case 1st number is negative
            int p = trait.pumsTraitContinuous.indexOf('-', 1);
            if (p < 0) {
                min = Integer.parseInt(trait.pumsTraitContinuous);
                max = min;
            } else {
                min = Integer.parseInt(trait.pumsTraitContinuous.substring(0, p));
                max = Integer.parseInt(trait.pumsTraitContinuous.substring(p + 1));
            }
        }

        // Copy description.
        if(! ObjectUtil.isBlank(trait.desc))
            setLabel(trait.desc);
    }

    /**
     * Set a description for this object. toString() will use this if given.
     * 
     * @param label
     *            friendly description, or null to clear
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    /**
     * Compute the value for a household.
     * 
     * @param house
     *            object to evaluate
     * @return the value for given object
     */
    public double getDouble(PumsHousehold ps) {
        try {
            if(table == Type.HOUSEHOLDS) {
                if(fixedValue)
                    return traitValue;
                else {
                    int value = ps.getAttributeValue(traitCol);
                    if (selection != null) {
                        if (value >= 0 && selection.get(value) == true)
                            value = 1;
                        else
                            value = 0;
                    }
                    else if (hasRange) {
                        if (value < min || value > max)
                            value = 0;
                    }
                    return value;
                }
            }
            else {
                PumsPopulation[] members = ps.getMembersOfHousehold();
                if(fixedValue)
                    return traitValue * members.length;
                else {
                    int value = 0;
                    
                    if (members != null) {
                        if(selection != null) {
                            // If value is in selection, count person.
                            for (int i = 0; i < members.length; i++) {
                                int flag = members[i].getAttributeValue(traitCol);
                                if (flag >= 0 && selection.get(flag))
                                    value++;
                            }
                        }
                        else if (hasRange) {
                            // If a person has value in range, add the value.
                            for (int i = 0; i < members.length; i++) {
                                int count = members[i].getAttributeValue(traitCol);
                                if (count >= min && count <= max)
                                    value += count;
                            }
                        }
                        else {
                            for (int i = 0; i < members.length; i++) {
                                int count = members[i].getAttributeValue(traitCol);
                                value += count;
                            }
                        }
                    }
                    
                    return value;
                }
            }
        }
        catch(Exception e) {
            throw new RuntimeException("Error using trait ["+this+"] to access "+ps, e);
        }
    }

    @Override
    public String toString() {
        if(label != null)
            return label;
        else {
            String s = table.toString();
            
            if(fixedValue)
                s += " value "+traitValue;
            else
                s += " column "+traitColName;
    
            if(selection != null)
                s += " if " + selection.toString();
            
            if(hasRange)
                s += String.format(" between %d-%d", min, max);
            
            return s;
        }
    }
}
