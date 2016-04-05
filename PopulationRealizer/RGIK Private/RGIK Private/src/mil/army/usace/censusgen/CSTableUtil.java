package mil.army.usace.censusgen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.MapUtils;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

/**
 * Helpers to work with the conditional simulation data table.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 *
 * @author William R. Zwicky
 */
public class CSTableUtil {
    private CSVTable table;
    private GISClass regionMap;
    /** index (0-based) of easting col in csTable */
    private int xidx = -1;
    /** index (0-based) of northing col in csTable */
    private int yidx = -1;

    /**
     * Initialize.
     * 
     * @param csTable
     *            CSV table that contains CS points. Easting column must be
     *            named "x", and northing column must be named "y".
     * @param regionMap
     *            raster map of region codes
     */
    public CSTableUtil(CSVTable csTable, GISClass regionMap) {
        this.table = csTable;
        this.regionMap = regionMap;
        this.xidx = csTable.findColumn("x");
        this.yidx = csTable.findColumn("y");
    }

    /**
     * @return the CS table that was passed to the constructor
     */
    public CSVTable getCSVTable() {
        return table;
    }

    /**
     * @param row index of row in CS table to examine
     * @return tract number that the row wants to appear in
     */
    public int getTract(int row) {
        String sval = table.getStringAt(row, xidx);
        double e = Double.parseDouble(sval);
        sval = table.getStringAt(row, yidx);
        double n = Double.parseDouble(sval);
        if(regionMap.isNoData(e, n))
            throw new DataException(String.format("Region map is NODATA where CS file line %d wants to locate (at %s,%s)",
                row+2, e, n));  // what we call "row 0", spreadsheet says "line 2" 
        return regionMap.getCellValue(e, n);
    }

    /**
     * Return a square-root-of-sum-of-squares of difference between attributes
     * in a CS row and those in a concrete household.
     * 
     * @param row
     *            index of row in CS table to examine
     * @param arch
     *            reference household
     * @return value indicating how different the two are. Zero is identical,
     *         larger means more different.
     */
    public double spread(int row, PumsHousehold arch) {
        double sum = 0;
        for(int col=0; col<table.getColumnCount(); col++) {
            // Skip coords.
            if(col == xidx || col == yidx)
                continue;
            // Skip blank fields.
            String sval = table.getStringAt(row, col);
            if(!ObjectUtil.isBlank(sval)) {
                String name = table.getColumnName(col);
                int csVal = Integer.parseInt(sval);
                int archVal = arch.getAttributeValue(name);
                double d = (csVal-archVal);
                sum += d*d;
            }
        }
        return Math.sqrt(sum);
    }

    /**
     * Copy all non-coordinate and non-blank attribs from table row into archtype.
     * 
     * @param row number of row to copy from
     * @param archtype object to receive values
     */
    public void copyAttribs(int row, PumsHousehold archtype) {
        for(int c=0; c < table.getColumnCount(); c++) {
            // Skip coords.
            if(c == xidx || c == yidx)
                continue;
            // Skip blank fields.
            String sval = table.getStringAt(row, c);
            if(! ObjectUtil.isBlank(sval)) {
                String name = table.getColumnName(c);
                int ival = Integer.parseInt(sval);
                archtype.setAttributeValue(name, ival);
            }
        }
    }

    /**
     * Copy coordinates from table row into realization.
     * 
     * @param row number of row to copy from
     * @param rzn object to receive values
     */
    public void copyLocation(int row, PumsHouseholdRealizationCS rzn) {
        String sval = table.getStringAt(row, xidx);
        double e = Double.parseDouble(sval);
        sval = table.getStringAt(row, yidx);
        double n = Double.parseDouble(sval);
        rzn.fixLocation(e, n);
    }
    
    /**
     * Count the number of CS records in each region.
     * 
     * @return map from tract number to count
     */
    public Map<Integer,Integer> count() {
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
        
        for(int row=0; row<table.getRowCount(); row++) {
            int t = getTract(row);

            // increment count for tract
            int c = MapUtils.getIntValue(result, t, 0);
            c += 1;
            result.put(t, c);
        }
        
        return result;
    }

    /**
     * Probabilistic loader: Load a CSV file, and only keep a random subset of
     * the rows.
     * 
     * @param csFile
     *            CSV file to load
     * @param probCol
     *            name of column that contains the probility that this row
     *            should be dropped.  Probabilities are decimal [0.0, 1.0].
     *            If cell is empty, row will never be dropped.
     * @param rnd
     *            random-number generator
     * 
     * @return named CSV file with containing a subset of rows
     * 
     * @throws IOException
     *             on any file error
     */
    public static CSVTable load(File csFile, String probCol, Random rnd) throws IOException {
        CSVTable csTable = new CSVTable(csFile.getAbsolutePath());
        if(!ObjectUtil.isBlank(probCol)) {
            int probIdx = csTable.findColumn(probCol);
            // Go backwards so .remove() doesn't break things.
            for(int r=csTable.getRowCount(); r>=0; r--) {
                String cell = csTable.getStringAt(r, probIdx);
                double prob;
                if(ObjectUtil.isBlank(cell))
                    prob = 0.0;
                else
                    prob = Double.parseDouble(cell);
                
                if(rnd.nextDouble() < prob) {
                    csTable.removeRow(r);
                }
                    
            }
        }
        return csTable;
    }
}
