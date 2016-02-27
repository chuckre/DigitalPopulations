package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;


/**
 * Estimate distance-based correlation between "nearby" GIS points.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class PointSemiVariogram extends RGIS implements PointSpatialStatistic {
    private GISPointQuadTree<PumsHouseholdRealization>[] maps;
    private TransformAttributes2double transformObject;
    private int                        numLags;
    private long                       countArray[];
    private double                     lagDist, maxDist;
    private double                     oWeight, uWeight, iWeight, decayExp;
    private double                     covar[];
    private boolean                    calculated;
    private PointSemiVariogram         mapSVs[];

    
    /**
     * Construct blank instance.
     */
    protected PointSemiVariogram() {
    }

    /**
     * Construct a blank PSV. To convert to goal instance, use setInertiaValue()
     * to load goal values. To convert to accumulating instance, use
     * initialize() to load maps.
     * 
     * @param lagDistance
     * @param maxDistance
     * @param transformObject
     *            helper to process points that are added or removed, or null
     *            for a pure, unmodifiable goal instance
     */
    public static PointSemiVariogram create(double lagDistance, double maxDistance,
            TransformAttributes2double transformObject) {
        PointSemiVariogram psv = new PointSemiVariogram();
        psv.initVars(lagDistance, maxDistance, transformObject);
        psv.initialize((GISPointQuadTree<PumsHouseholdRealization>[]) null);
        return psv;
    }

    /**
     * Construct accumulating instance, and initialize with points from a map.
     * 
     * @param lagDistance
     * @param maximumDistance
     * @param map
     * @param transformObject
     * @return
     */
    public static PointSemiVariogram createStat(double lagDistance, double maximumDistance,
            GISPointQuadTree<PumsHouseholdRealization> map,
            TransformAttributes2double transformObject) {
        PointSemiVariogram psv = new PointSemiVariogram();
        psv.initVars(lagDistance, maximumDistance, transformObject);
        psv.initialize(map);
        return psv;
    }

    /**
     * Construct accumulating instance, and initialize with points from multiple maps.
     * 
     * @param lagDistance
     * @param maximumDistance
     * @param maps
     * @param transformObject
     * @return
     */
    public static PointSemiVariogram createStat(double lagDistance, double maximumDistance,
            GISPointQuadTree<PumsHouseholdRealization>[] maps,
            TransformAttributes2double transformObject) {
        PointSemiVariogram psv = new PointSemiVariogram();
        psv.initVars(lagDistance, maximumDistance, transformObject);
        psv.initialize(maps);
        return psv;
    }

    public int getNumberLags() {
        return numLags;
    }

    public double averageOff(PointSpatialStatistic goalSpatialStatistic) {
        throw new RuntimeException("Not implemented.");
    }

    public double getSum() {
        throw new RuntimeException("Not implemented.");
    }

    public double getTotal() {
        throw new RuntimeException("Not implemented.");
    }

    public double getCount(GISPoint aPoint) {
        throw new RuntimeException("Not implemented.");
    }

    public boolean isMultiMap() {
        return maps != null && maps.length > 1;
    }

    /** not implemented yet */
    public void graph(int xLocation, int yLocation, int xSize, int ySize) {
        throw new RuntimeException("Not implemented.");
    }

    private void initVars(double lagDistance, double maxDistance,
            TransformAttributes2double transformObject) {
        this.transformObject = transformObject;
        numLags = (int) Math.ceil((double) maxDistance / lagDistance);
        lagDist = lagDistance;
        maxDist = maxDistance;
        oWeight = uWeight = 1.0f;
        decayExp = 0.0f;
        countArray = new long[numLags];
        covar = new double[numLags];
        for (int lag = 0; lag < numLags; lag++) {
            countArray[lag] = 0;
            covar[lag] = 0.0;
        }
        calculated = false;
    }

    /**
     * Register a map, and update statistics accordingly.
     */
    @SuppressWarnings("unchecked")
    public void initialize(GISPointQuadTree<PumsHouseholdRealization> dataMap) {
        initialize(new GISPointQuadTree[] { dataMap });
    }    
    
    /**
     * Register a set of maps, and update statistics accordingly.
     */
    public void initialize(GISPointQuadTree<PumsHouseholdRealization>[] dataMaps) {
        this.maps = dataMaps;
        this.mapSVs = null;
        this.countArray = new long[numLags];
        this.covar = new double[numLags];
        this.calculated = false;
        
        for (int lag = 0; lag < numLags; lag++) {
            countArray[lag] = 0;
            covar[lag] = 0.0;
        }

        if(maps != null) {
            this.mapSVs = new PointSemiVariogram[maps.length];
            for (int m = 0; m < maps.length; m++) {
                mapSVs[m] = PointSemiVariogram.create(lagDist, maxDist, transformObject);
                calcCov(maps[m], m);
            }
        }
    }

    /**
     * Update statistics for all points in the map.
     * 
     * @param qt
     * @param mapNumber
     */
    private void calcCov(GISPointQuadTree<PumsHouseholdRealization> qt, int mapNumber) {
        //printf("mapNumber: " + mapNumber);
        for(PumsHouseholdRealization iRzn : qt) {
            double iValue = transformObject.getDouble(iRzn.getParentHousehold());
            LinkedList<PumsHouseholdRealization> nearbyPoints = qt.getPoints(iRzn, maxDist);
            for(PumsHouseholdRealization jRzn : nearbyPoints) {
                double jValue = transformObject.getDouble(jRzn.getParentHousehold());
                double dist = jRzn.distance(iRzn);
                addCov(iValue, jValue, dist, mapNumber);
            }
        }
    }

    /**
     * Update a single lag.
     */
    private void addCov(double iValue, double jValue, double dist, int mapNumber) {
        int lag = getLag(dist);
        covar[lag] += (iValue - jValue) * (iValue - jValue);
        countArray[lag]++;
        mapSVs[mapNumber].covar[lag] += (iValue - jValue) * (iValue - jValue);
        mapSVs[mapNumber].countArray[lag]++;
    }

    /**
     * Update a single lag.
     */
    private void subCov(double iValue, double jValue, double dist, int mapNumber) {
        int lag = getLag(dist);
        covar[lag] -= (iValue - jValue) * (iValue - jValue);
        countArray[lag]--;
        mapSVs[mapNumber].covar[lag] -= (iValue - jValue) * (iValue - jValue);
        mapSVs[mapNumber].countArray[lag]--;
    }

    /**
     * This method copies the information from one PointSemiVariogram to a new
     * PointSemiVariogram.
     */
    public PointSemiVariogram createCopy() {
        PointSemiVariogram newC = new PointSemiVariogram();
        newC.copy(this);
        return newC;
    }

    /**
     * This method copies the information from one PointSemiVariogram to
     * another. Both PointSemiVariograms must have the same meta information.
     * Use createCopy() to construct PointSemiVariogram copy from scratch.
     */
    public void copy(PointSemiVariogram c) {
        maps = c.maps;
        transformObject = c.transformObject;
        numLags = c.numLags;
        this.countArray = Arrays.copyOf(c.countArray, c.countArray.length);
        lagDist = c.lagDist;
        maxDist = c.maxDist;
        oWeight = c.oWeight;
        uWeight = c.uWeight;
        iWeight = c.iWeight;
        decayExp = c.decayExp;
        this.covar = Arrays.copyOf(c.covar, c.covar.length);
        calculated = c.getCalculated();
        
        if(c.mapSVs == null)
            this.mapSVs = null;
        else {
            this.mapSVs = new PointSemiVariogram[c.mapSVs.length];
            for (int m = 0; m < c.mapSVs.length; m++) {
                mapSVs[m] = c.mapSVs[m].createCopy();
            }
        }
    }

    public int getLag(double distance) {
        return ((int) Math.min((int) (distance / lagDist), numLags - 1));
    }

    public double getInertiaValue(int lag) {
        if (calculated) {
            return (covar[lag]);
        }
        if (countArray[lag] > 0) {
            // System.out.print( "lag: " + lag + ", covar: " + covar[ lag] +
            // ", count: " + countArray[ lag] + "	  ");
            return (covar[lag] / countArray[lag]);
        }
        return ((double) 0.0);
    }

    public void setInertiaValue(int lag, double value) {
        calculated = true;
        covar[lag] = value;
    }

    public void calculate() {
        if (!calculated) {
            for (int lag = 0; lag < numLags; lag++) {
                covar[lag] = getInertiaValue(lag);
            }
            calculated = true;
        }
    }

    /** Modifies spatial statistic based on creating a point at a new location. */
    public void modifySS4NewPt(PumsHouseholdRealization newPoint, int mapNumber) {
        double iValue = transformObject.getDouble(newPoint.getParentHousehold());
        
        GISPointQuadTree<PumsHouseholdRealization> map = maps[mapNumber];
        LinkedList<PumsHouseholdRealization> nearbyPoints = map.getPoints(newPoint, maxDist);
        for(PumsHouseholdRealization jRzn : nearbyPoints) {
            if (jRzn != newPoint) {
                double jValue = transformObject.getDouble(jRzn.getParentHousehold());
                double dist = jRzn.distance(newPoint);
                addCov(iValue, jValue, dist, mapNumber);
            }
        }
    }

    /** Modifies spatial statistic based on removing a point. */
    public void modifySS4RemovedPt(PumsHouseholdRealization removedPoint, int mapNumber) {
        double iValue = transformObject.getDouble(removedPoint.getParentHousehold());
        
        GISPointQuadTree<PumsHouseholdRealization> map = maps[mapNumber];
        LinkedList<PumsHouseholdRealization> nearbyPoints = map.getPoints(removedPoint, maxDist);
        for(PumsHouseholdRealization jRzn : nearbyPoints) {
            if (jRzn != removedPoint) {
                double jValue = transformObject.getDouble(jRzn.getParentHousehold());
                double dist = jRzn.distance(removedPoint);
                subCov(iValue, jValue, dist, mapNumber);
            }
        }
        
    }

    public double spread(SpatialStatistic goalSpatialStatistic) {
        return spread((PointSemiVariogram) goalSpatialStatistic);
    }

    /**
     * Returns spread between two PointSemiVariograms.
     */
    public double spread(PointSemiVariogram goal) {
        int lag;
        double dist;
        double sumLSA = 0.0;
        for (lag = 0, dist = 0.0f; lag < numLags; lag++, dist += lagDist) {
            if (dist > maxDist) {
                lag = numLags;
            } else {
                double distCalc = dist + lagDist / (double) 2.0;
                double varE = this.getInertiaValue(lag);
                double varA = goal.getInertiaValue(lag);
                if (varA < varE) {
                    sumLSA += (uWeight * Math.pow((varE - varA), 2.0) * (1.0 / Math.pow(distCalc, decayExp)));
                } else {
                    sumLSA += (oWeight * Math.pow((varA - varE), 2.0) * (1.0 / Math.pow(distCalc, decayExp)));
                }
                if (this.mapSVs != null && this.mapSVs.length > 0) {
                    for (int m = 0; m < this.mapSVs.length; m++) {
                        if (goal.mapSVs != null && goal.mapSVs.length > 0) {
                            varA = goal.mapSVs[m].getInertiaValue(lag);
                        }
                        varE = this.mapSVs[m].getInertiaValue(lag);
                        if (varA < varE) {
                            sumLSA += iWeight
                                    * (uWeight * Math.pow((varE - varA), 2.0) * (1.0 / Math.pow(
                                        (double) distCalc, (double) decayExp)));
                        } else {
                            sumLSA += iWeight
                                    * (oWeight * Math.pow((varA - varE), 2.0) * (1.0 / Math.pow(
                                        (double) distCalc, (double) decayExp)));
                        }
                    }
                }
            }
        }
        return (sumLSA);
    }

    private void setValue(int lag, double value) {
        calculated = true;
        covar[lag] = value;
        countArray[lag] = 1;
    }

    @Override
    public String toString() {
        String d = getClass().getSimpleName();
        if(transformObject == null)
            d += " goal";
        else
            d += " for " + transformObject.toString();
        // transformObject provides the label, so no need to explain.
        // formatting a float as %s generates shortest string
        //        d = String.format("%s: %s per lag out to distance %s", d, lagDist, maxDist);
        return d;
    }

    public void printOneMapMeasure(int mapNumber, PrintStream printStream) {
        mapSVs[mapNumber].print(printStream);
    }

    public void printOneMapMeasure(int mapNumber, String fName) throws IOException {
        mapSVs[mapNumber].print(fName);
    }

    public void printMultiMeasure() {
        print(System.out);
    }

    public void printMultiMeasure(String fName) throws IOException {
        print(fName);
    }

    /**
     * This method functionally identical to print(), except first arg to
     * printBuffered is PrintWriter.
     * 
     * @throws IOException
     */
    public void print(String fName) throws IOException {
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(fName)));
        print(out);
        out.close();
    }

    public void print(PrintStream out) {
        out.format("%s:\n", toString());
        out.format("%-8s %-17s\n", "   Lag", "Moment of Inertia");
        for (int i = 1; i <= numLags; i++) {
            // ori out.format("%8.2f %17.8f\n", i*lagDist, getInertiaValue(i-1));
            out.format("%8.2f %17.8f %d\n", i*lagDist, getInertiaValue(i-1), countArray[i-1]);
        }
    }

    public void print(PrintStream out, SpatialStatistic goal) {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Dump inertia values like print(), along with % reduction relative to another PSV.
     * 
     * @param out place to write data
     * @param origPsv baseline PSVs (i.e. PSVs statistics from start of process)
     */
    public void printReduction(PrintStream out, PointSemiVariogram origPsv) {
        PointSemiVariogram curPsv = this;
        out.format("%s:\n", curPsv.toString());
        out.format("%-8s %17s %9s\n", "Lag", "Moment of Inertia", "Reduction");
        for (int i = 1; i <= curPsv.getNumberLags(); i++) {
            double imp = 1.0-(curPsv.getInertiaValue(i-1) / origPsv.getInertiaValue(i-1));
            out.format("%8.2f %17.8f %8.1f%%\n", i*curPsv.getLagDistance(), curPsv.getInertiaValue(i-1), imp*100.0);
        }
    }

    /**
     * Dump reduction table like printReduction() along side goal values.
     * 
     * @param out place to write data
     * @param origPsv baseline PSVs (i.e. PSVs statistics from start of process)
     * @param goalPsv target PSV (i.e. when process will quit)
     */
    public void printComparison(PrintStream out, PointSemiVariogram origPsv, PointSemiVariogram goalPsv) {
        PointSemiVariogram curPsv = this;
        out.format("%s:\n", curPsv.toString());
        out.format("%-8s %16s %16s %16s %9s %10s\n",
            "Lag",
            "Init Inertia",
            "Cur Inertia",
            "Goal Inertia",
            "Cur Reduc",
            "Goal Reduc");
        
        for (int i = 1; i <= curPsv.getNumberLags(); i++) {
            double currR = 1.0-(curPsv.getInertiaValue(i-1) / origPsv.getInertiaValue(i-1));
            double goalR = 1.0-(goalPsv.getInertiaValue(i-1) / origPsv.getInertiaValue(i-1));
            
            out.format("%8.2f %16.8f %16.8f %16.8f %8.1f%% %9.1f%%\n",
                i*curPsv.getLagDistance(),
                origPsv.getInertiaValue(i-1),
                curPsv.getInertiaValue(i-1),
                goalPsv.getInertiaValue(i-1),
                currR*100.0,
                goalR*100.0);
        }
    }
    
    /**
     * @throws IOException
     */
    public PointSemiVariogram(String fName) throws IOException {
        calculated = true;
        String longFN = fName;
        MyReader fr = new MyReader(longFN);
        String ss = "";
        fr.readLine(); // reads first line
        StringBuffer s = new StringBuffer(fr.readLine()); // reads second line
        ss = GISio.thisNumber(s);
        ss = GISio.dropCommas(ss);
        lagDist = maxDist = new Double(ss).doubleValue();
        PointSemiVariogram oldDG = PointSemiVariogram.create(lagDist, maxDist, null);
        this.initVars(lagDist, maxDist, null);
        int numLags = 0;
        while (s != null && s.length() > 0) {
            ss = GISio.thisNumber(s);
            ss = GISio.dropCommas(ss);
            maxDist = new Double(ss).doubleValue();
            oldDG = PointSemiVariogram.create(lagDist, maxDist, null);
            oldDG.copy(this);
            ss = GISio.nextNumber(s);
            ss = GISio.nextNumber(s);
            ss = GISio.dropCommas(ss);
            double firstV = new Double(ss).doubleValue();
            oldDG.setValue(numLags, firstV);
            this.initVars(lagDist, maxDist, null);
            this.copy(oldDG);
            numLags++;

            String tmp = fr.readLine();
            if (tmp != null)
                s = new StringBuffer(tmp);
            else
                s = null;
        }
        fr.close();
    }

    public double getLagDistance() {
        return lagDist;
    }

    public double getMaximumDistance() {
        return maxDist;
    }

    public boolean getCalculated() {
        return calculated;
    }

    public void setUnderWeight(double underWeight) {
        uWeight = underWeight;
    }

    public double getUnderWeight() {
        return uWeight;
    }

    public void setOverWeight(double overWeight) {
        oWeight = overWeight;
    }

    public double getOverWeight() {
        return oWeight;
    }

    public void setOneMapWeight(double oneMapWeight) {
        iWeight = oneMapWeight;
    }

    public double getOneMapWeight() {
        return iWeight;
    }

    public void setDecayExponent(double decayExponent) {
        decayExp = decayExponent;
    }

    public double getDecayExponent() {
        return decayExp;
    }
}
