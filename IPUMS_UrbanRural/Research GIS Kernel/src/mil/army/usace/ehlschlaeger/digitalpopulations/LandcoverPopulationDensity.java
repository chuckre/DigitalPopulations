package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.CumulativeDistributionFunction;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.Reclass;
import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;
import mil.army.usace.ehlschlaeger.rgik.util.LeastSquaresSolver;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.Matrix;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.lang.ArrayUtils;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;


/**
 * Compute population density for each class in a land cover map.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class LandcoverPopulationDensity implements Serializable {
    public static final String TABLE_LANDUSE_DENSITY = "density";
    public static final String TABLE_LANDUSE_DESC    = "desc";
    public static final String TABLE_LANDUSE_CLASS   = "class";

    /**
     * If true, we'll print details on internal decisions made plus reasons for them.
     * (Basically a trace log.)
     */
	public static boolean PRINT_REASONS = true;
	
    protected static Logger log = Logger.getLogger(LandcoverPopulationDensity.class.getPackage().getName());

    // ------ Source Data ----- //
    protected GISClass landcover;
    protected Map<Integer, String> classes;
    protected GISClass regionMap;
    protected CSVTableNoSwing regionTable;
    protected String regionColumnName;
    protected String yColumnName;
    
    // ----- Intermediate Data ----- //
    /**
     * Identifies the region code at each row in classCellsInRegions and Y. Note
     * that DataPreparer hides user's region code, and replaces it with
     * row_number.
     */
    protected ArrayIntList regionAtRow;
    /** Count of cells covered by each land-use class within each region. */
    protected int[][] classCellsInRegions;
    /**
     * Condensed form of classCellsInRegions. X[row][col] is for region
     * regionAtRow[row], land-use class classArray[col].
     */
    protected Matrix X;
    /**
     * Contains value from 'yColumnName' for each region.
     */
    protected Matrix y;

    // ----- Results ----- //
    /**
     * Identifies the class code for each element in b and e arrays, and for
     * each column in the X matrix.
     */
    protected ArrayIntList classArray;
    /**
     * "beta": the results. b[class] is relative density for land-use class code
     * classArray[class]. "Relative density" means a cell with b=6 gets 2x the
     * hohs as b=3 (if yColumnName points to household counts.)
     */
    protected double[] b;
    /** "error": quality of each result */
    protected double[] e;

    
    /**
     * Construct new instance.  NOTE: landcover and regionMap must cover exact same
     * area at same resolution.
     * 
     * @param landcover land cover map
     * @param landcoverClasses class-&gt;name mapping for land cover map
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param regionTable attributes for tracts defined in regionMap
     * @param regionColumnName name of column that links regionMap to regionTable
     * @param yColumnName name of column that contains the y-values
     */
    public LandcoverPopulationDensity( GISClass landcover, Map<Integer, String> landcoverClasses,
            GISClass regionMap, CSVTableNoSwing regionTable, 
            String regionColumnName, String yColumnName) {
        this.landcover = landcover;
        this.classes = landcoverClasses;
        this.regionMap = regionMap;
        this.regionTable = regionTable;
        this.regionColumnName = regionColumnName;
        this.yColumnName = yColumnName;

        int minClass = landcover.getMinimumValue();
        int maxClass = landcover.getMaximumValue();

        int numRegions = regionTable.getRowCount();
        
        LogUtil.cr(log);
        LogUtil.detail(log, "Solving land cover population density:");
        
        // Extract region number from each row in regionTable.
        // Build "Y" vector for solver, containing value from 'yColumnName' for each region.
        regionAtRow = new ArrayIntList();
        int regionColumn = regionTable.findColumn(regionColumnName);
        int yColumn = regionTable.findColumn(yColumnName);
        y = new Matrix(numRegions, 1);
        for (int row = 0; row < numRegions; row++) {
            String regionNumberS = regionTable.getStringAt(row, regionColumn);
            int regionNumber = Integer.parseInt(regionNumberS.trim());
            String yNumberS = regionTable.getStringAt(row, yColumn);
            double yNumber = Double.parseDouble(yNumberS.trim());
            // save region numbers
            regionAtRow.add(regionNumber);
            // save y values
            y.setCell(row, 0, yNumber);
        }

        // Build usage matrix.
        //   each row is a region from regionTable
        //   each column is a landcover number
        //   each cell contains a count of number cells with that landcover code in that region
        classCellsInRegions = new int[numRegions][maxClass - minClass + 1];
        for (int r = landcover.getNumberRows() - 1; r >= 0; r--) {
            for (int c = landcover.getNumberColumns() - 1; c >= 0; c--) {
                if (landcover.isNoData(r, c) == false
                        && regionMap.isNoData(r, c) == false) {
                    int regionValue = regionMap.getCellValue(r, c);
                    int lcValue = landcover.getCellValue(r, c);
                    int row = regionAtRow.indexOf(regionValue);
                    classCellsInRegions[row][lcValue - minClass] += 1;
                }
            }
        }

        // Delete rows (regions) not useful to the solver.
        // We don't zero the data, we actually delete the region from the solver so its
        // data has no effect on the solution.
        for(int r = 0; r < classCellsInRegions.length; r++) {
            int total=0;    // doesn't really mean anything, just a quick way to scan row
            int classes=0;  // number of different land use classes in this region
            for (int c = 0; c < classCellsInRegions[r].length; c++) {
                int cells = classCellsInRegions[r][c];
                total += cells;
                if(cells > 0)
                    classes++;
            }
            
            // ** MUST USE ELSE IF BELOW DUE TO DIRTY HACK WITH "r--" ** //
            if(total == 0) {
                // I'm not entirely sure how to get here; I think this means:
                //  - region is in table, but not in map; or
                //  - land use map does not cover this region.
                if(PRINT_REASONS)
                    LogUtil.detail(log, "  Ignoring region "+regionAtRow.get(r)+": no land use data");
                // Delete region from all three places, in this order.
                y.removeRow(r);
                regionAtRow.removeElementAt(r);
                classCellsInRegions = (int[][]) ArrayUtils.remove(classCellsInRegions, r);
                // DIRTY HACK: Since we deleted this row, the subsequent row will have moved
                // into our slot.  So we must re-do this index.
                r--;
            }
            else if(classes < 2) {
                // Region must use two or more different land use codes.
                // Having only one code seems to skew the solution badly.
                if(PRINT_REASONS)
                    LogUtil.detail(log, "  Ignoring region "+regionAtRow.get(r)+": too few land use types");
                y.removeRow(r);
                regionAtRow.removeElementAt(r);
                classCellsInRegions = (int[][]) ArrayUtils.remove(classCellsInRegions, r);
                r--;
            }
            else if(y.getCell(r, 0) < 1) {
                // Y-column (assumed to contain number of households in region) must be greater
                // than zero.  Here too, zero seems to skew the solution badly.
                if(PRINT_REASONS)
                    LogUtil.detail(log, "  Ignoring region "+regionAtRow.get(r)+": "+yColumnName+" is zero");
                y.removeRow(r);
                regionAtRow.removeElementAt(r);
                classCellsInRegions = (int[][]) ArrayUtils.remove(classCellsInRegions, r);
                r--;
            }
            /*
             *  TODO: `masking layer for phase one'
             *       Explicitly exclude regions based on a column in CSVTable data. For
             *       this to happen, a new parameter `String exclusionColumnName' should
             *       be included. If value exclusionColumnName not "", non-zeros in that
             *       column indicate regions not to be included in LeastSquaresSolver.
             */
        }
        
        numRegions = classCellsInRegions.length;
        
        // Extract list of classes that are in use.
        classArray = new ArrayIntList();
        for(int c=minClass; c<=maxClass; c++) {
            int d = c-minClass;
            for (int r = numRegions - 1; r >= 0; r--) {
                if (classCellsInRegions[r][d] > 0) {
                    classArray.add(c);
                    break;
                }
            }
        }

        // Now delete columns (land-use class) that are not used.
        // We don't actually modify cCIR, we build a new "X" matrix
        // containing only the columns we want to keep.
        X = new Matrix(numRegions, classArray.size());
        for (int r = 0; r < numRegions; r++) {
            for (int d = 0; d < classArray.size(); d++) {
                X.setCell(r, d, classCellsInRegions[r][classArray.get(d) - minClass]);
            }
        }
        
        // Solve repeatedly til we get a "good" result (all results are > 0).
        boolean done = false;
        do {
            // Solve.
            LeastSquaresSolver svls = new LeastSquaresSolver();
            svls.setX(X);
            svls.sety(y);
            b = svls.getBetas();
            e = svls.getErrors();

            // Find lowest result.
            double worstValue = Double.MAX_VALUE;
            int worstIndex = -1;
            for (int i = 0; i < b.length; i++) {
                if (b[i] < worstValue) {
                    worstValue = b[i];
                    worstIndex = i;
                }
            }

            int worstClass = classArray.get(worstIndex);
            String worstName = classes.get(worstClass);
            String msg;
            if(ObjectUtil.isBlank(worstName))
                msg = String.format("  Worst class is %d, beta is %f",
                    worstClass, worstValue);
            else
                msg = String.format("  Worst class is %d \"%s\", beta is %f",
                    worstClass, worstName, worstValue);
            
            // If worst value < 0, remove that class from solver, and try again.
            if (worstValue >= 0.0) {
                LogUtil.detail(log, msg+": solution accepted");
                done = true;
            } else {
                LogUtil.detail(log, msg+": solution rejected");
                // Remove class from classArray, and corresponding column from X.
                X.removeColumn(classArray.indexOf(worstClass));
                classArray.removeElementAt(worstIndex);
            }
        } while(!done);
        
        // Log results.
        LogUtil.cr(log);
        StringOutputStream sos = new StringOutputStream();
        print(sos);
        LogUtil.result(log, sos.toString());
    }

    /**
     * Display our contents in human-readable form.
     */
    public void print(PrintStream out) {
        out.format("%5s %-16s %-16s  %s\n", "Class","        Beta","       Error", "    Label");
        for (int i = 0; i < b.length; i++) {
            int c = classArray.get(i);
            out.format("%5d %16.9f %16.9f  %s\n", c, b[i], e[i], classes.get(c));
        }
    }

    /**
     * Resample a population density map to match the range and resolution of a
     * region map.
     * 
     * @param regionMap
     *            map which specifies the region that covers each cell
     * @param populationDensityMap
     *            density map to resample
     */
    public static GISLattice createPDF( GISClass regionMap, GISLattice populationDensityMap) {
        GISLattice pdf = new GISLattice((GISGrid)regionMap);
        for (int r = 0; r < pdf.getNumberRows(); r++) {
            double regionNorthing = pdf.getCellCenterNorthing(r, 0);
            for (int c = 0; c < pdf.getNumberColumns(); c++) {
                double regionEasting = pdf.getCellCenterEasting(0, c);
                if (regionMap.isNoData(r, c) == false) {
                    if( populationDensityMap.isNoData(regionEasting, regionNorthing))
                        throw new DataException("population density map must have values whereever region values exist");
                    double pdmValue = populationDensityMap.getCellValue(regionEasting, regionNorthing);
                    if( pdmValue < 0.0)
                        pdmValue = 0.0;
                    pdf.setCellValue(r, c, pdmValue);
                } else {
                    pdf.setCellValue(r, c, 0.0);
                }
            }
        }
        return pdf;
    }

    /**
     * Create PDF from landcover map and density-per-class table.
     * 
     * @param landcover
     * @param valueMap
     * @return
     */
    public static GISLattice createPDF(GISClass landcover, Map<Integer, Double> valueMap) {
        return Reclass.reclass(landcover, valueMap, 0.0);
    }

	/**
	 * Construct a population density function from computed solution.
	 * 
	 * @return result map with population density in each cell
	 */
	public GISLattice createPDF() {
        GISLattice pdf = new GISLattice(landcover);
        for (int r = 0; r < landcover.getNumberRows(); r++) {
            for (int c = 0; c < landcover.getNumberColumns(); c++) {
                if (landcover.isNoData(r, c) == false
                        && regionMap.isNoData(r, c) == false) {
                    int lc = landcover.getCellValue(r, c);
                    int index = classArray.indexOf(lc);
                    if(index < 0)
                        pdf.setCellValue(r, c, 0.0);
                    else
                        pdf.setCellValue(r, c, b[index]);
                } else {
                    pdf.setCellValue(r, c, 0.0);
                }
            }
        }
		return pdf;
	}

	/**
	 * Write a CSV file containing the raw data used to create the PDF.
	 * Rows that are all zero are not written.
	 * 
	 * @param file path and name of file to create
	 * @throws IOException on any file error
	 */
	public void writeSourceTable(File file) throws IOException {
        int minClass = landcover.getMinimumValue();

        // Scan for zeroes (i.e. unused classes).
        BitSet colHasData = new BitSet();
        for(int r = 0;  r < y.getNumberRows();  r++)
            for(int c = 0;  c < classCellsInRegions[r].length; c++)
                if(classCellsInRegions[r][c] != 0)
                    colHasData.set(c);
        
        CsvListWriter out = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

        try {
            String[] row = new String[2+colHasData.cardinality()];
            
            // Write header row.
            row[0] = regionColumnName;
            row[1] = yColumnName;
            // 'c' is input column, 'wc' is write column
            for(int c = 0, wc = 0;  c < classCellsInRegions[0].length; c++)
                if(colHasData.get(c)) {
                    row[2+wc] = String.format("class-%d", minClass+c); //"class-"+Integer.toString(minClass+c);
                    wc++;
                }
            out.writeHeader(row);
            
            // Write all data rows.
            for(int r = 0;  r < y.getNumberRows();  r++) {
                row[0] = Integer.toString(regionAtRow.get(r));
                row[1] = Double.toString(y.getCell(r, 0));
                for(int c = 0, wc = 0;  c < classCellsInRegions[r].length; c++) {
                    // Only write columns that aren't all zero.
                    if(colHasData.get(c)) {
                        row[2+wc] = Integer.toString(classCellsInRegions[r][c]);
                        wc++;
                    }
                }
                out.write(row);
            }
            
            // Write instructions at bottom.
            out.write("");
            out.write("# This is the final table used by Digital Populations to compute object density for each land-use class.");
            out.write("# Columm '"+yColumnName+"' contains number of object in each region.");
            out.write("# 'class-xx' columns contain the number of map cells of each land-use class.");
            out.write("# This table can be fed back into Digital Populations to bypass automatic calculation.");
        }            
        finally {
            out.close();
        }
	}

	/**
	 * Write beta values into a simple CSV file.  All classes in relationship
	 * file are written out.  If they're not present in solution, they get the 
	 * value zero.
	 * 
     * @param file path and name of file to create
     * @throws IOException on any file error
	 */
	public void writeSolutionTable(File file) throws IOException {
        String[] row = new String[3];

        // Construct map from class to value for ALL classes in rel file.
        // Classes missing from our 'b' array get zero.
        Map<Integer,Double> values = new HashMap<Integer, Double>();
        for (Integer cls : classes.keySet())
            values.put(cls, 0.0);
        for(int r = 0;  r < b.length;  r++)
            values.put(classArray.get(r), b[r]);
        
        CsvListWriter out = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

        try {
            // Write header row.
            row[0] = TABLE_LANDUSE_CLASS;
            row[1] = TABLE_LANDUSE_DESC;
            row[2] = TABLE_LANDUSE_DENSITY;
            out.writeHeader(row);

            // Write all data rows, in order.
            ArrayList<Integer> keys = new ArrayList<Integer>(values.keySet());
            Collections.sort(keys);
            for (Integer cls : keys) {
                row[0] = cls.toString();
                row[1] = "";
                row[2] = values.get(cls).toString();
                
                if(classes != null) {
                    row[1] = classes.get(cls);
                    if(row[1] == null)
                        row[1] = "--";
                }
                
                out.write(row);
            }
            
            // Write instructions at bottom.
            out.write("");
            out.write("# This table contains the final land-use class density solution computed by Digital Populations.");
            out.write("# Column 'density' give relative densities for each land-use class.");
            out.write("# 'Relative density' means a class with value 6 will receive twice as many objects as a class with 3.");
            out.write("# This table can be fed back into Digital Populations to bypass automatic calculation.");
        }            
        finally {
            out.close();
        }
	}

    /**
     * Create CSV file containing goal quantities of yColumnName (i.e. number of
     * households) for each land-use class in each region. Values are based on
     * results of solver, so regions and class codes the solver didn't like will
     * be dropped, and can be assumed to be zero.
     * <P>
     * To understand this method, think of the final PDF generated by
     * createPDF(): Each cell contains a relative weight for the number of
     * objects that should be placed in that cell. To compute the total objects
     * per land-use class, we need to compare the PDF to the land-use map to
     * compute total weights per class. The percentage of objects to be placed
     * in a class is the total of all weight values for cells in that class,
     * divided by the sum of all weight values in the region. The final value is
     * the number of objects to be placed in the region (i.e. number of
     * households as reported by region table) multiplied by that percentage.
     * 
     * @param file path and name of file to create
     * @throws IOException on any file error
     */
	public void writePopulationTable(File file) throws IOException {
        String[] row = new String[2+classArray.size()];
        double[] totals = new double[classArray.size()];

        CsvListWriter out = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

        try {
            // Write header row.
            row[0] = regionColumnName;
            for(int c=0; c<classArray.size(); c++) {
                int klass = classArray.get(c);
                // classes.get(klass) is name of class
                row[1+c] = String.format("class-%d", klass);
            }
            row[1+classArray.size()] = "TOTAL";
            out.writeHeader(row);

            // Write rows of data.
            for(int r = 0;  r < regionAtRow.size();  r++) {
                // Total "weight" for this region is the sum of class
                // weights, and each class weight is the beta for each
                // class multiplied by the number of cells in this region
                // that are in that class.
                double totWeight = 0;            
                for(int c = 0; c < classArray.size(); c++) {
                    double numCells = X.getCell(r, c);
                    totWeight += numCells * b[c];
                }

                // Fill first column with region ID.
                int region = regionAtRow.get(r);
                row[0] = Integer.toString(region);
                
                // Fetch total objects in entire region.
                double quantity = y.getCell(r, 0);

                double regionTot = 0;
                for(int c = 0; c < classArray.size(); c++) {
                    // Number of objects in class is proportion of "weight"
                    // belonging to class.
                    double numCells = X.getCell(r, c);
                    double weight = numCells * b[c];
                    double num = quantity * weight / totWeight;

                    // Fill in columns, update totals.
                    row[1+c] = Double.toString(num);
                    totals[c] += num;
                    regionTot += num;
                }

                // Put row total in final column.
                row[1+classArray.size()] = Double.toString(regionTot);
                out.write(row);
            }

            // Write column totals.
            row[0] = "TOTAL";
            double regionTot = 0;
            for(int c = 0; c < classArray.size(); c++) {
                row[1+c] = Double.toString(totals[c]);
                regionTot += totals[c];
            }
            row[1+classArray.size()] = Double.toString(regionTot);
            out.write(row);

            // Write instructions at bottom.
            out.write("");
            out.write("# Values represent goal number of "+yColumnName+" for each land-use class in each region.");
            out.write("# Actual numbers of households placed may differ.");
            out.write("# Totals should match column '"+yColumnName+"' in region table.");
            out.write("# NOTE: Regions and classes with no people do not appear in table.");
        }            
        finally {
            out.close();
        }
	}

    /**
     * Create CSV table that specifies the percentage of each region covered by
     * each land-use class.
     * 
     * @param file path and name of file to create
     * @throws IOException on any file error
     */
    public void writeLandPercentTable(File file) throws IOException {
        int minRgn = regionMap.getMinimumValue();
        int maxRgn = regionMap.getMaximumValue();
        int numRegions = maxRgn - minRgn + 1;
        int minClass = landcover.getMinimumValue();
        int maxClass = landcover.getMaximumValue();
        int numClasses = maxClass - minClass + 1;

        //**Clone of code above in constructor. We need to rebuild this matrix
        //**since the constructor may have deleted some of it.
        // Build usage matrix.
        //   each row is a region from regionTable
        //   each column is a landcover number
        //   each cell contains a count of number cells with that landcover code in that region
        int[][] ccir = new int[numRegions][numClasses];
        for (int r = landcover.getNumberRows() - 1; r >= 0; r--) {
            for (int c = landcover.getNumberColumns() - 1; c >= 0; c--) {
                if (landcover.isNoData(r, c) == false
                        && regionMap.isNoData(r, c) == false) {
                    int regionValue = regionMap.getCellValue(r, c);
                    int lcValue = landcover.getCellValue(r, c);
                    
                    ccir[regionValue - minRgn][lcValue - minClass] += 1;
                }
            }
        }

        //**Clone of code above in writeSourceTable().
        // Scan for zeroes (i.e. unused classes).
        BitSet colHasData = new BitSet();
        for(int r = 0;  r < ccir.length;  r++)
            for(int c = 0;  c < ccir[r].length; c++)
                if(ccir[r][c] != 0)
                    colHasData.set(c);
        
        CsvListWriter out = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
        String[] row = new String[1+colHasData.cardinality()];

        try {
            // Write header row.
            int row_c = 0;
            row[row_c++] = regionColumnName;
            for(int c = 0; c < ccir[0].length; c++) {
                if(colHasData.get(c)) {
                    int klass = minClass+c;
                    // classes.get(klass) is name of class
                    row[row_c++] = String.format("class-%d", klass);
                }
            }
            out.writeHeader(row);

            // Write regions.
            int tt = 0;
            for(int r = 0; r < numRegions; r++) {
                // Total up row.
                int t = 0;
                for(int c = 0; c < ccir[r].length; c++)
                    t += ccir[r][c];
                tt += t;
                
                // Write row if not empty.
                if(t > 0) {
                    row_c=0;
                    row[row_c++] = Integer.toString(minRgn + r);
                    for(int c = 0; c < ccir[r].length; c++) {
                        if(colHasData.get(c)) {
                            row[row_c++] = Double.toString(100.0 * ccir[r][c] / t);
                        }
                    }
                    out.write(row);
                }
            }
    
            // Write overall percents.
            row_c=0;
            row[row_c++] = "OVERALL";
            for(int c = 0; c < ccir[0].length; c++) {
                if(colHasData.get(c)) {
                    int t = 0;
                    for(int r = 0; r < numRegions; r++)
                        t += ccir[r][c];
                    row[row_c++] = Double.toString(100.0 * t / tt);
                }
            }                
            out.write(row);
            
            // Write instructions at bottom.
            out.write("");
            out.write("# Values represent percentage of region covered by land-use class.");
            out.write("# Each row must sum to 100%.");
            out.write("# OVERALL values represent percentage of entire map covered by land-use class.");
        }            
        finally {
            out.close();
        }
    }	
	
	/**
	 * Run solver on a specific map file.
	 * 
	 * @param argv
	 * @throws IOException
	 */
	public static void main( String argv[]) throws IOException {
		if( argv.length != 7) {
			System.out.println( "LandcoverPopulationDensity main program requires seven arguments:");
			System.out.println( "java -mx####m LandcoverPopulationDensity NLCDMap RegionClassMap RegionAttributes.csv RegionColumn SolveColumn PDFmapName CDFmapName");
			System.exit( -1);
		}
		String lcMap = argv[ 0];
		GISClass lc_map = GISClass.loadEsriAscii( lcMap);
		for( int r = lc_map.getNumberRows() - 1; r >= 0; r--) {
			for( int c = lc_map.getNumberColumns() - 1; c >= 0; c--) {
				if( lc_map.isNoData( r, c) == false) {
					int value = lc_map.getCellValue( r, c);
					if( value > 90) {
						lc_map.setCellValue( r, c, 9);
					} else if( value > 80) {
						lc_map.setCellValue( r, c, 8);
					} else if( value > 70) {
						lc_map.setCellValue( r, c, 4);
					} else if( value > 60) {
						lc_map.setCellValue( r, c, 6);
					} else if( value > 40) {
						lc_map.setCellValue( r, c, 4);
					} else if( value > 30) {
						lc_map.setCellValue( r, c, 3);
					}
				}
			}
		}
		String rMap = argv[ 1];
		GISClass r_map = GISClass.loadEsriAscii( rMap);
		String rTable = argv[ 2];
		CSVTableNoSwing regionTable = new CSVTableNoSwing( rTable);
		String rColumn = argv[ 3];
		String yColumn = argv[ 4];
		LandcoverPopulationDensity lpd = new LandcoverPopulationDensity( 
			lc_map, null, r_map, regionTable, rColumn, yColumn);
		lc_map = null;
		r_map = null;
		regionTable = null;
		rColumn = null;
		yColumn = null;
		GISLattice pdf = lpd.createPDF();
		pdf.writeAsciiEsri( argv[ 5]);
		GISLattice cdf = CumulativeDistributionFunction.createNormalized(pdf);
		cdf.writeAsciiEsri( argv[ 6]);
	}
}
