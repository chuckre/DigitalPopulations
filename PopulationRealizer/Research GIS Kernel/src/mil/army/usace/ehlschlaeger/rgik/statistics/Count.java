package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
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
 * Compares the absolute quantities of object in each region with a target
 * quantity. Doesn't actually compare absolute quantities; rather, we try to
 * ensure that the proportion of objects in each region vs the entire map are
 * close to the target.
 * <P>
 * One object does track both the population counts and the goal; one instance
 * must be constructed in goal mode and one in accumulation mode, then the
 * <code>spread</code> function can compare them. Both objects must be given the
 * same map and table so that the tract IDs match.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Count extends RGIS implements PointSpatialStatistic, TractSpatialStatistic, Serializable {
    /** Map of census tracts. */
    private GISClass                   regionMap;

    /** Goal mode: Number of objects with interesting trait in each region. */
    private int[]                      numInRegion;
    /** Goal mode: Total number of objects in entire regionMap. */
    private int                        sumOfNum;
    
    /** Accumulation mode: Field that gives number of objects with trait.  Unused; only useful to toString(). */
    private String                     numString;
    /**
     * All modes: Lowest value found in regionMap. will be the class value at
     * numInRegion[0].
     */
    private int                        minClassValue;
    /** Accumulation mode: Extracts value of trait from a GISPoint. */
    private TransformAttributes2double countGetter;

    /** User's short description of this object. */
    private String label;

    
    /**
     * Build clone of this instance.  Same as "new Count(this)".
     */
    public SpatialStatistic createCopy() {
        return new Count(this);
    }

    private Count() {
    }
    
    /**
     * Construct as copy of another instance. This instance will have the same
     * mode as the original. WARNING: The clone will not be a complete deep copy
     * of the original. The statistics containers are deep copies, but all other
     * objects (especially the quad tree) are shared!
     * 
     * @param toCopy original object to copy
     */
    public Count( Count toCopy) {
        regionMap = toCopy.regionMap;
        numString = toCopy.numString;
        minClassValue = toCopy.minClassValue;
        sumOfNum = toCopy.sumOfNum;
        countGetter = toCopy.countGetter;
        numInRegion = toCopy.numInRegion.clone();
        label = toCopy.label;
    }

    /**
     * A point has been added to the map; update statistics accordingly.
     * 
     * @param newPoint point object that was added to map
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
     * @param newPoint point object that was added to map
     * @param tractID code for tract into which this point has been placed
     * @param mapNumber NOT USED, must be 0
     */
    public void modifySS4NewPt(PumsHousehold house, int tractID, int mapNumber) {
        assert mapNumber == 0;
        
        double iValue = countGetter.getDouble(house);
        numInRegion[tractID - minClassValue] += iValue;
        sumOfNum += iValue;
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
        
        double iValue = countGetter.getDouble(house);
        numInRegion[tractID - minClassValue] -= iValue;
        sumOfNum -= iValue;
    }
    
    /**
     * Compute total number of people in map.
     * 
     * @return total for entire map
     */
    public double getSum() {
        return sumOfNum;
    }

    /**
     * Compute trait value for a single object.
     */
    public double getCount(PumsHousehold house) {
        return( countGetter.getDouble(house));
    }

    public int getFirstRegion() {
        return minClassValue;
    }
    
    public int getLastRegion() {
        return minClassValue + numInRegion.length-1;
    }
    
    /**
     * @param regionNum region number, from map
     * @return number of objects currently in that region
     */
    public int getNumInRegion(int regionNum) {
        return numInRegion[regionNum - minClassValue];
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
            String keyColumn, String attributeColumn) {
        assert regions != null;
        assert attributeTable != null;
        assert keyColumn != null;
        assert attributeColumn != null;
        
        regionMap = regions;
        
        // Scan tract map for lowest and highest tract IDs
        minClassValue = regions.getMinimumValue();
        int maxClassValue = regions.getMaximumValue();

        // Build arrays to hold statistics for regions.
        numInRegion = new int[maxClassValue - minClassValue + 1];
        sumOfNum = 0;
        
        int key = attributeTable.findColumn( keyColumn);
        int attC = attributeTable.findColumn( attributeColumn);

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
            numInRegion[keyValue - minClassValue] = attV;
            
            // Update map-wide total.
            sumOfNum += attV;
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
        if(countGetter == null) {
            sb.append("Goal Count for ");
            if(ObjectUtil.isBlank(label))
                sb.append(numString);
            else
                sb.append(label);
        }
        else {
            sb.append("Count for ");
            if(ObjectUtil.isBlank(label))
                sb.append('[').append(countGetter.toString()).append(']');
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
     * @param goal
     *            target counts for each tract.  Must be of type Count.
     * @return sum of square of errors
     */
    public double spread(SpatialStatistic goal) {
        Count cgoal = (Count) goal;
        double value = 0.0;
        for( int i = numInRegion.length - 1; i >= 0; i--) {
            double gProportion = cgoal.numInRegion[i] / (double)cgoal.sumOfNum;
            double gNum = gProportion * this.sumOfNum;
            double gDif = (this.numInRegion[ i] - gNum);
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
     *            target counts for each tract.  Must be of type Count.
     * @return
     */
    public double averageOff(TractSpatialStatistic goal) {
        Count cgoal = (Count) goal;
        double value = 0.0;
        for( int i = numInRegion.length - 1; i >= 0; i--) {
                double v = Math.abs(numInRegion[ i] - cgoal.numInRegion[ i]);
                value += v;
        }
        return(value / numInRegion.length);
    } 


    /**
     * Dump everything interesting to System.out.
     */
    public void print(PrintStream out) {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%s:\n", toString());
        f.format("%5s %10s %-16s\n",
                 "region",
                 numString,
                 "global_proportion");
        for( int i = 0; i < numInRegion.length; i++) {
            f.format("%5d %10d %16.9f \n",
                     (i + minClassValue),
                     numInRegion[i],
                     numInRegion[i] / (double)sumOfNum);
        }
        out.print(sb);
    }

    public void print(PrintStream out, SpatialStatistic goal) {
        Count cgoal = (Count) goal;
        
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%s:\n", toString());
        f.format("%5s %10s %-16s %-16s\n",
                 "region",
                 numString,
                 "global_proportion",
                 "goal_proportion");
        for( int i = 0; i < numInRegion.length; i++) {
            f.format("%5d %10d %16.9f %16.9f\n",
                     (i + minClassValue),
                     numInRegion[i],
                     numInRegion[i] / (double)sumOfNum,
                     cgoal.numInRegion[i] / (double)cgoal.sumOfNum);
        }
        out.print(sb);
    }
    
    public boolean isMultiMap() {
        return false;
    }

    public void printOneMapMeasure(int mapNumber, PrintStream printStream) {
        throw new RuntimeException("Not implemented.");
    }
    
    /**
     * Simplified constructor for accumulating instance. traitGetter/totalGetter
     * together provide the proportion of a household with a trait.
     * 
     * @param regions
     *            map which specifies which tract covers each cell
     * @param traitGetter
     *            object that can compute the 'value' of a household (i.e.
     *            number of people with trait, or whether house has roof)
     */
    public static Count createStat(GISClass regions,
            TransformAttributes2double traitGetter) {
        Count c = new Count();
        
        c.regionMap = regions;
        c.minClassValue = regions.getMinimumValue();
        int numClasses = regions.getMaximumValue() - c.minClassValue + 1;
        c.numInRegion = new int[numClasses];

        c.countGetter = traitGetter;

        // Keep print() happy.
        c.numString = "count";
        
        return c;
    }
    
    /**
     * Construct goal instance from a census tract map and its matching table.
     * Object can only be used for goal checking (via spread()); modifySS4NewPt
     * and modifySS4RemovedPt cannot be used.
     * 
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param attributeTable
     *            table that defines attributes for each tract
     * @param keyColumn
     *            unique ID for the tract
     * @param attributeColumn
     *            column which contains counts of people with particular trait
     *            in the tract
     */
    public static Count createGoal( GISClass regionMap, CSVTableNoSwing attributeTable,
            String keyColumn, String attributeColumn) {
        Count c = new Count();
        c.numString = attributeColumn;
        c.checkRun( regionMap, attributeTable, keyColumn, attributeColumn);
        return c;
    }
}
