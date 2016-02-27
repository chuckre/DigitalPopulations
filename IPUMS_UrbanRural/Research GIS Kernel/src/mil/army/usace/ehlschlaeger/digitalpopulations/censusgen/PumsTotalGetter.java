package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.TransformAttributes2double;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

/**
 * Build getter for total values from a PumsHouseholdRealization.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PumsTotalGetter {
    /**
     * Construct a suitable object that can compute the total candidates
     * (proportion denominator) from a PumsHouseholdRealization. Directly uses
     * the pumsTotal* fields of the given trait, but also uses pumsTrait* to
     * determine how to sum totals.
     * 
     * @param trait
     *            trait record from which this getter will be built
     * @param households
     *            raw PUMS household table. Used as the schema for records
     *            attached to PumsHousehold.
     * @param population
     *            raw PUMS population table. Used as the schema for records
     *            attached to PumsPopulation.
     * 
     * @return custom object that fetches the value of the requested field
     */
    public static TransformAttributes2double make(PumsTrait trait, CSVTableNoSwing households, CSVTableNoSwing population) {
        // Check for errors
        if(trait.pumsTotalTable == null) {
            if(ObjectUtil.isBlank(trait.pumsTotalField))
                return null;
            else
                throw new DataException("trait.pumsTotalTable is missing.");
        }
        else if(ObjectUtil.isBlank(trait.pumsTotalField))
            throw new DataException("trait.pumsTotalField is missing.");
        
        // Parse denominator
        Type    totalTable = trait.pumsTotalTable;
        boolean fixedValue;
        int     totalCol = -1;
        int     totalValue = -1;
        
        try {
            totalValue = Integer.parseInt(trait.pumsTotalField);
            fixedValue = true;
        }
        catch(NumberFormatException e) {
            fixedValue = false;
            if(totalTable == Type.HOUSEHOLDS)
                totalCol = households.findColumn(trait.pumsTotalField);
            else
                totalCol = population.findColumn(trait.pumsTotalField);
        }
        
        // Make Java happy
        final Type f_totalTable = totalTable;
        final int f_totalCol = totalCol;
        final String f_totalColName = trait.pumsTotalField;
        final int f_totalValue = totalValue;

        // Let PTG parse numerator
        final PumsTraitGetter ptg = new PumsTraitGetter(trait, households, population);
        if(ptg.fixedValue)
            ptg.hasRange = false;

        // 16 states to deal with:
        //   traitHasRange(Y/N) x traitTable(H/P) x totalTable(H/P) x fixedValue(Y/N)
        //     traitHasRange = whether pumsTraitContinuous was specified
        //     *Table = Households or Population
        //     fixedValue = Y if pumsTraitField specifies a constant, N if field name
        // Each state contains an anonymous inner class customized to that state for speed.
        // Note: if ptg.hasRange==true, then ptg.fixedValue must be false (else PumsTraitGetter will crash.)

        if(ptg.hasRange) {
            // Y-*-*
            if(ptg.table == Type.HOUSEHOLDS) {
                // Y-H-*
                if(totalTable == Type.HOUSEHOLDS) {
                    // Y-H-H-*
                    if(fixedValue) {
                        // Y-H-H-Y: return total only if trait is not no-data
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int test = ps.getAttributeValue(ptg.traitCol);
                                if(test >= ptg.min && test <= ptg.max)
                                    return f_totalValue;
                                else
                                    return 0;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s value %d", f_totalTable, f_totalValue);
                            }
                        };
                    }
                    else {
                        // Y-H-H-N: return total only if trait is not no-data
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int test = ps.getAttributeValue(ptg.traitCol);
                                if(test >= ptg.min && test <= ptg.max)
                                    return ps.getAttributeValue(f_totalCol);
                                else
                                    return 0;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s column %s", f_totalTable, f_totalColName);
                            }
                        };
                    }
                }
                else {
                    // Y-H-P-*
                    if(fixedValue) {
                        // Y-H-P-Y: if trait is-data, return total value * number of people
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int test = ps.getAttributeValue(ptg.traitCol);
                                int total = 0;
                                if(test >= ptg.min && test <= ptg.max) {
                                    PumsPopulation[] members = ps.getMembersOfHousehold();
                                    if (members != null) {
                                        total = members.length * f_totalValue;
                                    }
                                }
                                return total;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s value %d", f_totalTable, f_totalValue);
                            }
                        };
                    }
                    else {
                        // Y-H-P-N: if trait is-data, return sum of POP total fields
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int test = ps.getAttributeValue(ptg.traitCol);
                                int total = 0;
                                if(test >= ptg.min && test <= ptg.max) {
                                    PumsPopulation[] members = ps.getMembersOfHousehold();
                                    if (members != null) {
                                        for (int i = 0; i < members.length; i++) {
                                            int count = members[i].getAttributeValue(f_totalCol);
                                            total += count;
                                        }
                                    }
                                }
                                return total;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s column %s", f_totalTable, f_totalColName);
                            }
                        };
                    }
                }
            }
            else {
                // Y-P-*
                if(totalTable == Type.HOUSEHOLDS) {
                    // Y-P-H-*
                    throw new RuntimeException("LOGIC FAILURE: We have no good solution for when trait table is POP and has pumsTraitContinuous, but total table is HOH.  What do we return if only some of the people are no-data?");
                }
                else {
                    // Y-P-P-*
                    if(fixedValue) {
                        // Y-P-P-Y: sum totalValue for each person whose trait is-data
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int total = 0;
                                PumsPopulation[] members = ps.getMembersOfHousehold();
                                if (members != null) {
                                    for (int i = 0; i < members.length; i++) {
                                        int test = members[i].getAttributeValue(ptg.traitCol);
                                        if(test >= ptg.min && test <= ptg.max) {
                                            total += f_totalValue;
                                        }
                                    }
                                }
                                return total;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s value %d", f_totalTable, f_totalValue);
                            }
                        };
                    }
                    else {
                        // Y-P-P-N: sum total field for each person whose trait is-data
                        return new TransformAttributes2double() {
                            public double getDouble(PumsHousehold ps) {
                                int total = 0;
                                PumsPopulation[] members = ps.getMembersOfHousehold();
                                if (members != null) {
                                    for (int i = 0; i < members.length; i++) {
                                        int test = members[i].getAttributeValue(ptg.traitCol);
                                        if(test >= ptg.min && test <= ptg.max) {
                                            int count = members[i].getAttributeValue(f_totalCol);
                                            total += count;
                                        }
                                    }
                                }
                                return total;
                            }
                            @Override
                            public String toString() {
                                return String.format("%s column %s", f_totalTable, f_totalColName);
                            }
                        };
                        
                    }
                }
            }
        }
        else {
            // N-*-*
            //  -> If trait doesn't have range, then we don't need to consider whether it's no-data.
            if(totalTable == Type.HOUSEHOLDS) {
                // N-*-H: simply return household field
                if(fixedValue) {
                    // N-*-H-Y
                    return new TransformAttributes2double() {
                        public double getDouble(PumsHousehold ps) {
                            return f_totalValue;
                        }
                        @Override
                        public String toString() {
                            return String.format("%s value %d", f_totalTable, f_totalValue);
                        }
                    };
                }
                else {
                    // N-*-H-N
                    return new TransformAttributes2double() {
                        public double getDouble(PumsHousehold ps) {
                            return ps.getAttributeValue(f_totalCol);
                        }
                        @Override
                        public String toString() {
                            return String.format("%s column %s", f_totalTable, f_totalColName);
                        }
                    };
                }
            }
            else {
                // N-*-P: return sum of population fields
                if(fixedValue) {
                    // N-*-P-Y
                    return new TransformAttributes2double() {
                        public double getDouble(PumsHousehold ps) {
                            PumsPopulation[] members = ps.getMembersOfHousehold();
                            if(members == null)
                                return 0;
                            else
                                return f_totalValue * members.length;
                        }
                        @Override
                        public String toString() {
                            return String.format("%s value %d", f_totalTable, f_totalValue);
                        }
                    };
                }
                else {
                    // N-*-P-N
                    return new TransformAttributes2double() {
                        public double getDouble(PumsHousehold ps) {
                            PumsPopulation[] members = ps.getMembersOfHousehold();
                            int value = 0;
                            if (members != null) {
                                for (int i = 0; i < members.length; i++) {
                                    int count = members[i].getAttributeValue(f_totalCol);
                                    value += count;
                                }
                            }
                            return value;
                        }
                        @Override
                        public String toString() {
                            return String.format("%s column %s", f_totalTable, f_totalColName);
                        }
                    };
                }
            }
        }
    }
}
