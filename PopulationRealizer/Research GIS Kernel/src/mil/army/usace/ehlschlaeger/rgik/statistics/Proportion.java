package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.TransformAttributes2double;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Compares the percentage of a population that have a certain trait with a
 * target percentage. Tries to match proportions on a tract-by-tract basis.
 * <P>
 * One instance must be constructed in goal mode and one in accumulation mode,
 * then the <code>spread</code> function can compare them. Both objects must be
 * given the same map and table so that the tract IDs match.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class Proportion extends RGIS implements PointSpatialStatistic, TractSpatialStatistic, Serializable {
    //
    // WRZ Historical Note:
    // This class used to hold an array of quad trees, intending to support
    // computing stats over multiple maps. The code was never completed, so the
    // array support was removed. The 'maps' member was also removed, as nothing
    // but the constructor needs access to the source quad tree.
    //
    /** Map of census tracts. */
    private GISClass                   regionMap;
    
    /** Goal mode: Number of objects with interesting trait in each region. */
    private double[]                   numInRegion;
    /** Goal mode: Total number of objects in each region. */
    private double[]                   totInRegion;
    /** Goal mode: Total number of objects in entire regionMap. */
    private double                     sumOfTotal;
    
    /** Accumulation mode: Field that gives number of objects with trait.  Unused; only useful to toString(). */
    private String                     numString;
    /** Accumulation mode: Field that gives total number of objects.  Unused; only useful to toString(). */
    private String                     totalString;
    /**
     * All modes: Lowest value found in regionMap. will be the class value at
     * numInRegion[0] and totalInRegion[0].
     */
    private int                        minClassValue;
    /** Accumulation mode: Extracts value of trait from a GISPoint. */
    private TransformAttributes2double traitGetter;
    /** Accumulation mode: Extracts number of objects from a GISPoint. */
    private TransformAttributes2double totalGetter;

    /** User's short description of this object. */
    private String label;

    
	/**
     * Same as "new Proportion(this)".
     */
	public SpatialStatistic createCopy() {
		Proportion rp = new Proportion( this);
		return( rp);
	}

    private Proportion() {
    }

    /**
     * Construct as copy of another instance. This instance will have the same
     * mode as the original. WARNING: The clone will not be a complete deep copy
     * of the original. The statistics arrays are deep copies, but all other
     * objects (especially the quad tree) are shared!
     * 
     * @param toCopy original object to copy
     */
	public Proportion( Proportion toCopy) {
		regionMap = toCopy.regionMap;
		numString = toCopy.numString;
		totalString = toCopy.totalString;
		minClassValue = toCopy.minClassValue;
		sumOfTotal = toCopy.sumOfTotal;
		traitGetter = toCopy.traitGetter;
		totalGetter = toCopy.totalGetter;
        numInRegion = toCopy.numInRegion.clone();
        totInRegion = toCopy.totInRegion.clone();
        label = toCopy.label;
	}

    /**
     * Set the label used by toString and print methods.
     * @param desc
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

	/**
	 * A point has been added to the map; update statistics accordingly.
	 * 
     * @param house archtype for household that was added
     * @param tractID code for tract into which this point has been placed
	 * @param mapNumber NOT USED, must be 0
	 */
	public void modifySS4NewPt( PumsHouseholdRealization newPoint, int mapNumber) {
        assert mapNumber == 0;
        PumsHousehold house = newPoint.getParentHousehold();
		int reg = regionMap.getCellValue( newPoint.getEasting(), newPoint.getNorthing());
		modifySS4NewPt(house, reg, mapNumber);
	}

    /**
     * A point has been added to the map; update statistics accordingly.
     * Shortcut for the other method with this name which computes tract number,
     * while this one takes it as a parameter.
     * 
     * @param house archtype for household that was added
     * @param tractID code for tract into which this point has been placed
     * @param mapNumber NOT USED, must be 0
     */
    public void modifySS4NewPt(PumsHousehold house, int tractID, int mapNumber) {
        assert mapNumber == 0;
        // Have tObject compute the "value" of this point (ie. number of
        // people with trait)
        double iValue = traitGetter.getDouble(house);
        numInRegion[tractID - minClassValue] += iValue;
        if( totalGetter == null) {
            // If totalGetter is null, denom will be 1.0 per region.
        } else {
            // Have members tell us the total number of things at this point.
            totInRegion[tractID - minClassValue] += totalGetter.getDouble(house);
            sumOfTotal += totalGetter.getDouble(house);
        }
    }

    /**
     * A point has been removed from the map; update statistics accordingly.
     * 
     * @param newPoint point object that was added to map
     * @param mapNumber NOT USED, must be 0
     */
	public void modifySS4RemovedPt( PumsHouseholdRealization removedPoint, int mapNumber) {
        assert mapNumber == 0;
        PumsHousehold house = removedPoint.getParentHousehold();
        int reg = regionMap.getCellValue( removedPoint.getEasting(), removedPoint.getNorthing());
        modifySS4RemovedPt(house, reg, mapNumber);
	}

    /**
     * A point has been removed from the map; update statistics accordingly.
     * Shortcut for the other method with this name which computes tract number,
     * while this one takes it as a parameter.
     * 
     * @param newPoint point object that was added to map
     * @param tractID code for tract into which this point has been placed
     * @param mapNumber NOT USED, must be 0
     */
    public void modifySS4RemovedPt(PumsHousehold house, int tractID, int mapNumber) {
        assert mapNumber == 0;
        double iValue = traitGetter.getDouble(house);
        numInRegion[tractID - minClassValue] -= iValue;
        if( totalGetter == null) {
            // If totalGetter is null, denom will be 1.0 per region.
        } else {
            totInRegion[tractID - minClassValue] -= totalGetter.getDouble(house);
            sumOfTotal -= totalGetter.getDouble(house);
        }
    }
	
    /**
     * Compute total number of people with trait (i.e. numerator) in all regions
     * in map.
     * 
     * @return total trait value for entire map
     */
	public double getSum() {
		double sum = 0.0;
		for( int i = numInRegion.length - 1; i >= 0; i--) {
			sum += numInRegion[ i];
		}
		return sum;
	}

    /**
     * Compute total number of candidates (i.e. denominator) in all regions in
     * map.
     * 
     * @return candidate value for entire map
     */
	public double getTotal() {
	    return sumOfTotal;
	}

	/**
	 * Accumulate statistics from all points in a tree.
	 * @param tree pile of points to add
	 */
	public void addAllPoints(GISPointQuadTree<? extends PumsHouseholdRealization> tree) {
        for(PumsHouseholdRealization pt : tree) {
            modifySS4NewPt(pt, 0);
        }
	}

    /**
     * Build statistics tables from a tract map.
     * 
     * @param regions
     *            map which specifies which tract covers each cell
     * @param attributeTable
     *            table that defines attributes for each tract
     * @param keyColumn
     *            unique ID for the tract
     * @param attributeColumn
     *            column which contains counts of people with particular trait
     *            in the tract
     * @param totalColumn
     *            column which contains total population in the tract, or fixed value
     */
	private void checkRun(GISClass regions, CSVTableNoSwing attributeTable,
            String keyColumn, String attributeColumn, String totalColumn) {
        assert regions != null;
        assert attributeTable != null;
        assert keyColumn != null;
        assert attributeColumn != null;
        assert totalColumn != null;
        
		regionMap = regions;
		
		// Scan tract map for lowest and highest tract IDs
		minClassValue = regions.getMinimumValue();
		int maxClassValue = regions.getMaximumValue();

		// Build arrays to hold statistics for regions.
		numInRegion = new double[ maxClassValue - minClassValue + 1];
		totInRegion = new double[ maxClassValue - minClassValue + 1];
		
		int key = attributeTable.findColumn( keyColumn);
		int attC = attributeTable.findColumn( attributeColumn);

		int totC = -1;
		try {
		    // if regionTotal specifies a constant, flood array
            double fixedTot = Double.parseDouble(totalColumn);
            Arrays.fill(totInRegion, fixedTot);
            sumOfTotal += fixedTot * totInRegion.length;
        } catch (NumberFormatException e) {
            // not a constant, verify it's a table column
            totC = attributeTable.findColumn( totalColumn);
        }
        
        // Accumulate errors so we can report them all.
        List<String> errors = new ArrayList<String>();
        
        int rows = attributeTable.getRowCount();
		for( int r = 0; r < rows; r++) {
		    // Get tract ID
			String s = attributeTable.getStringAt( r, key);
			int keyValue = Integer.parseInt(s.trim());
			if( keyValue < minClassValue || keyValue > maxClassValue) {
                errors.add(String.format("%s %d: key value must be between %d and %d",
                                         keyColumn, keyValue,
                                         minClassValue, maxClassValue));
			}
			
			// Get traited population of tract, save.
			s = attributeTable.getStringAt( r, attC);
			int attV = Integer.parseInt(s.trim());
			if( attV < 0) {
			    errors.add(String.format("%s %d: trait value ('%s'=%d) must be >= 0",
			                             keyColumn, keyValue,
			                             attributeColumn, attV));
			}
			numInRegion[ keyValue - minClassValue] = attV;
			
            // Get total population of tract, save.
			if(totC >= 0) {
    			s = attributeTable.getStringAt( r, totC);
    			int totValue = Integer.parseInt(s.trim());
    			// Update region total.
    			totInRegion[ keyValue - minClassValue] = totValue;
    			// Update map-wide total.
    			sumOfTotal += totValue;
			}
		}
		
		// Report all errors found.
		if(errors.size() > 0) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("Errors found in region table:\n");
		    for (String error : errors) {
                buf.append("  ").append(error).append("\n");
            }
		    throw new DataException(buf.toString());
		}
	}

	public String toString() {
        StringBuilder sb = new StringBuilder();
	    if(traitGetter == null) {
	        sb.append("Goal Proportion for ");
	        if(ObjectUtil.isBlank(label))
	            sb.append(numString).append("/").append(totalString);
	        else
	            sb.append(label);
	    }
	    else {
	        sb.append("Proportion for ");
            if(ObjectUtil.isBlank(label)) {
    	        sb.append('[').append(traitGetter.toString()).append("] / ");
    	        if(totalGetter != null)
    	            sb.append('[').append(totalGetter.toString()).append(']');
    	        else
    	            sb.append("1.0");
            }
            else
                sb.append(label);
	    }
	    return sb.toString();
	}

    /**
     * Compute difference between accumulated statistics in this object, and the
     * target percentages in another. Return value is the sum over all the
     * regions of the square of the number of people by which this object misses
     * the goal.
     * 
     * @param goalSpatialStatistic
     *            target percentages for each tract
     * @return sum of square of errors
     */
    public double spread(SpatialStatistic goalSpatialStatistic) {
        Proportion goal = (Proportion) goalSpatialStatistic;
		double value = 0.0;
		for( int i = numInRegion.length - 1; i >= 0; i--) {
            // Mathematically, 0/0 is ambiguous. But semantically, it means
            // this: if goal.totInRegion is zero, then numInRegion must be zero,
            // so the goal for this region is zero.
            double gProportion = (goal.totInRegion[i]==0 ? 0 : goal.numInRegion[i] / goal.totInRegion[i]);
            double gNum = gProportion * this.totInRegion[i];
			double gDif = (numInRegion[ i] - gNum);
			value += gDif * gDif;
		}
		return value;
	}

    /**
     * Compute absolute difference between number current count of people and
     * target count. Percentages are ignored; only actual quantities are
     * compared.
     * 
     * @param goal
     * @return
     */
    public double averageOff(TractSpatialStatistic statistic) {
        Proportion goal = (Proportion) statistic;
		double value = 0.0;
		for( int i = numInRegion.length - 1; i >= 0; i--) {
				double v = Math.abs( numInRegion[ i] - goal.numInRegion[ i]);
				value += v;
		}
		return( value / numInRegion.length);
	} 

	/**
	 * Display our contents in human-readable form.
	 */
	public void print(PrintStream out) {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%s:\n", toString());
        f.format("%5s %10s %10s %-16s %-16s\n",
                 "region",
                 numString, totalString,
                 "   proportion",
                 "global_proportion");
        for( int i = 0; i < numInRegion.length; i++) {
            if( totInRegion[ i] > 0) {
                f.format("%5d %10.1f %10.1f %16.9f %16.9f\n",
                         (i + minClassValue),
                         numInRegion[i], totInRegion[i],
                         numInRegion[i] / totInRegion[i],
                         numInRegion[i] / sumOfTotal);
            }
        }
        out.print(sb);
	}

    /**
     * Display our contents in human-readable form, and incorporate goal data
     * for comparison.
     */
    public void print(PrintStream out, SpatialStatistic goal) {
        Proportion pgoal = (Proportion) goal;
        
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%s:\n", toString());
        f.format("%5s %10s %10s %-16s %-16s\n",
                 "region",
                 numString, totalString,
                 "   proportion",
                 "goal_proportion");
        for( int i = 0; i < numInRegion.length; i++) {
            if( totInRegion[ i] > 0) {
                // Mathematically, 0/0 is ambiguous. But semantically, it means
                // this: if goal.totInRegion is zero, then numInRegion must be zero,
                // so the goal for this region is zero.
                double goalprop = (pgoal.totInRegion[i]==0 ? 0 : pgoal.numInRegion[i] / pgoal.totInRegion[i]);
                f.format("%5d %10.1f %10.1f %16.9f %16.9f\n",
                         (i + minClassValue),
                         numInRegion[i], totInRegion[i],
                         numInRegion[i] / totInRegion[i],
                         goalprop);
            }
        }
        out.print(sb);
    }

    /**
     * @return false, we don't support multiple maps
     */
    public boolean isMultiMap() {
        return false;
    }

    /**
     * @throws RuntimeException, we don't support multiple maps
     */
    public void printOneMapMeasure(int mapNumber, PrintStream printStream) {
        throw new RuntimeException("Not implemented.");
    }
    
    /**
     * Simplified constructor for accumulating instance. traitGetter/totalGetter
     * together provide the proportion of a household with a trait.
     * 
     * @param regions
     *            map which specifies which tract covers each cell
     * @param minRegionCode
     *            smallest value in region map
     * @param maxRegionCode
     *            largest value in region map
     * @param traitGetter
     *            object that can compute the 'value' of a household (i.e.
     *            number of people with trait, or whether house has roof)
     * @param totalGetter
     *            object that can compute the maximum 'value' of a household
     *            (i.e. total number of people), or null to count the household
     *            itself (= 1.0)
     */
    public static Proportion createStat(GISClass regions,
            TransformAttributes2double traitGetter,
            TransformAttributes2double totalGetter) {
        Proportion p = new Proportion();
        
        p.regionMap = regions;
        p.minClassValue = regions.getMinimumValue();
        int numClasses = regions.getMaximumValue() - p.minClassValue + 1;
        p.numInRegion = new double[ numClasses];
        p.totInRegion = new double[ numClasses];

        p.traitGetter = traitGetter;
        p.totalGetter = totalGetter;

        // If totalGetter is null, denom will be 1.0 per region.
        if(totalGetter == null) {
            Arrays.fill(p.totInRegion, 1.0);
            p.sumOfTotal = p.totInRegion.length * 1.0;
        }
        
        // Keep print() happy.
        p.numString = "numer";
        p.totalString = "denom";
        
        return p;
    }
    
    /**
     * Construct goal instance from a census tract map and its matching table.
     * Object can only be used for goal checking (via spread()); modifySS4NewPt
     * and modifySS4RemovedPt cannot be used.
     * 
     * @param regions
     *            map which specifies which tract covers each cell
     * @param attributeTable
     *            table that defines attributes for each tract
     * @param keyColumn
     *            unique ID for the tract
     * @param attributeColumn
     *            column which contains counts of people with particular trait
     *            in the tract
     * @param totalColumn
     *            column which contains total population in the tract
     */
    public static Proportion createGoal( GISClass regions, CSVTableNoSwing attributeTable,
            String keyColumn, String attributeColumn, String totalColumn) {
        Proportion p = new Proportion();
        
        p.numString = attributeColumn;
        p.totalString = totalColumn;
        p.checkRun( regions, attributeTable, keyColumn, attributeColumn, totalColumn);
        
        return p;
    }
}
