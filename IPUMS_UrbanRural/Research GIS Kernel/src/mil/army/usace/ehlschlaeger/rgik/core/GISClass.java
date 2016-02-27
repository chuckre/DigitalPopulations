package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mil.army.usace.ehlschlaeger.rgik.io.ESRI_ASCII;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;



/**
 * Defines a rectangular area, subdivided by lines into smaller blocks.
 * Values are stored for the intersection of the lines, not for the blocks.
 * Values are integers.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GISClass extends GISGrid implements Serializable {
    /**
     * Default indicator for cells that are considered undefined.
     */
    public static final int DEFAULT_NODATA = -999999;
    
    private int v[][];
    private Integer noDataValue;
    private boolean minChange, maxChange;
    private boolean hasMinMax = false;
    private int     minValue, maxValue;

    /**
     * Default constructor is forbidden due to superclass missing one.
     */
    private GISClass() {
        super(0,0,0,0,0,0);
    }
    
// -- Disabled by WRZ due to lack of GRASS files to test with.
//    When restored, this should become "static loadAsciiGrass()".
//
//    /** Currently, the GRASS 5.0 ASCII files are not supported.
//     *  @param fileName ASCII file containing grid metadata without the extension. 
//     *  @param fileType if GISGrid.ESRI is passed as the argument, then the file 
//     *  is an ESRI ASCII (*.asc) file. If GISGrid.GRASS4 is passed as the argument,
//     *  then the file is the ASCII output from GRASS (or Idrisi's GRASSIDR). The 
//     *  GRASS ASCII file must have a .gra extension. 
//     * @throws IOException 
//     */
//    public GISClass( String fileName, int fileType) throws IOException {
//        super( 0.0, 0.0, 1.0, 1.0, 2, 2);
//        int endIndex = 0, startIndex = 0;
//        String s = "", numberS = "";
//        String totalFileName = esriFileName( fileName);
//        if( fileType == GISGrid.GRASS4) {
//            totalFileName = grassFileName( fileName);
//        }
//        MyReader fr = new MyReader( totalFileName);
//        if( fileType == GISGrid.ESRI) {
//            -->see ESRI_ASCII
//        } else if( fileType == GISGrid.GRASS4) {
//            s = fr.readLine(); // north: 4299000.00
//            startIndex = nextNumber( s, 4);
//            s = s.substring( startIndex);
//            setNorthEdge( new Double( s.trim()).doubleValue());
//            s = fr.readLine(); // south:  4247000.00
//            startIndex = nextNumber( s, 4);
//            s = s.substring( startIndex);
//            setSouthEdge( new Double( s.trim()).doubleValue());
//            s = fr.readLine(); // east:    528000.00
//            startIndex = nextNumber( s, 8);
//            s = s.substring( startIndex);
//            double east = new Double( s.trim()).doubleValue();
//            setEastEdge( east);
//            s = fr.readLine(); // west:    500000.00
//            startIndex = nextNumber( s, 8);
//            s = s.substring( startIndex);
//            double west = new Double( s.trim()).doubleValue();
//            setWestEdge( west);
//            s = fr.readLine(); // rows:   10
//            startIndex = nextNumber( s, 7);
//            s = s.substring( startIndex);
//            int rows = new Integer( s.trim()).intValue();
//            setNumberRows( rows);
//            s = fr.readLine(); // cols:  15
//            startIndex = nextNumber( s, 7);
//            s = s.substring( startIndex);
//            int columns = new Integer( s.trim()).intValue();
//            setNumberColumns( columns);
//            setNSResolution( (getNorthEdge() - getSouthEdge()) / rows);
//            setEWResolution( (getEastEdge() - getWestEdge()) / columns);
//            setIsEsriNoDataValue( false);
//            s = fr.readLine();
//        } else {
//            // The GRASS5 header now can have entries: multiplier, null, and type. If type is not set in the header, the new
//            // map is created integer if all of the values in the input file are integer, and float otherwise.
//            throw new IllegalArgumentException("fileType does not match supported types.");
//        }
//        constructorInitializer();
//        int row = 0;
//        int col = 0;
//        startIndex = 0;
//        while( row < getNumberRows()) {
//            if( startIndex >= s.length())  {
//                s = fr.readLine();
//                startIndex = 0;
//            }
//            char ch = s.charAt( startIndex);
//            while( Character.isWhitespace( ch) == true) {
//                startIndex++;
//                ch = s.charAt( startIndex);
//            }
//            endIndex = endNumber( s, startIndex);
//            if( endIndex == s.length() - 1) {
//                endIndex++;
//            }
//            numberS = s.substring( startIndex, endIndex);
//            int fileValue = new Integer( numberS.trim()).intValue();
//            //v[ row][ col] = (int) fileValue;
//            if( getIsEsriNoDataValue() == true && fileValue == getNoDataValue()) {
//                setNoData( row, col, true);
//            } else {
//                setCellValue( row, col, fileValue);
//            }
//            startIndex = nextNumber( s, endIndex);
//            col++;
//            if( col == getNumberColumns()) {
//                col = 0;
//                row++;
//            }
//        }
//        fr.close();
//    }

    /**
     * Create new empty grid with given size. "Nodata" is true for all cells.
     * 
     * @param westEdge
     *            location of western edge of bounds
     * @param northEdge
     *            location of northern edge of bounds
     * @param EWResolution
     *            width of each cell
     * @param NSResolution
     *            height of each cell
     * @param numRows
     *            number of cells (NOT lines) tall
     * @param numCols
     *            number of cells (NOT lines) wide
     */
    public GISClass( double westEdge, double northEdge, double EWResolution,
            double NSResolution, int numRows, int numCols) {
        super( westEdge, northEdge, EWResolution, NSResolution, numRows, numCols);
        constructorInitializer();
    }

    /**
     * Create new empty grid with given size. "Nodata" is true for all cells.
     * 
     * @param grid
     *            metadata for new GISClass.
     */
    public GISClass( GISGrid grid) {
        super( grid.getWestEdge(), grid.getNorthEdge(), 
            grid.getEWResolution(), grid.getNSResolution(),
            grid.getNumberRows(), grid.getNumberColumns());
        constructorInitializer();
    }

    /**
     * Construct a deep copy of this object.
     */
    public GISClass clone() {
        GISClass newClass = new GISClass(this);
        newClass.copyData(this);
        return newClass;
    }
    
    /**
     * This constructor resamples data from data map to grid metadata.
     * Currently, the aggregation method (when the grid resolution is greater
     * than data resolution) will result in a no data value unless all data in
     * the data cells instead a grid cell are identical. If the data cells are
     * either no data or a single class value, the grid cell will be given the
     * class value.
     * 
     * @param grid
     *            metadata for new GISClass,
     * @param data
     *            data to be copied into new GISClass.
     */
    public GISClass( GISGrid grid, GISClass data) {
        this(grid);
        
        double gridRes = Math.min( grid.getEWResolution(), grid.getNSResolution());
        double dataRes = Math.min( data.getEWResolution(), data.getNSResolution());
        int failRow = -1;
        boolean[] failedCell = null;
        if( gridRes > dataRes) {
            for( int dr = 0; dr <= data.getNumberRows() - 1; dr++) {
                for( int dc = 0; dc <= data.getNumberColumns() - 1; dc++) {
                    double easting = data.getCellCenterEasting( dr, dc);
                    double northing = data.getCellCenterNorthing( dr, dc);
                    if( data.isNoData( dr, dc) == false &&
                        grid.onMap( easting, northing) == true) {
                        int gr = grid.getRowIndex( easting, northing);
                        if( gr != failRow) {
                            failedCell = new boolean[ grid.getNumberColumns()];
                            failRow = gr;
                            for( int i = 0; i < grid.getNumberColumns(); i++)
                                failedCell[ i] = false;
                        }
                        int gc = grid.getColumnIndex( easting, northing);
                        if( failedCell[ gc] == false) {
                            if( this.isNoData( gr, gc) == true) {
                                this.setCellValue( gr, gc, data.getCellValue( dr, dc));
                            } else if( this.getCellValue( gr, gc) != data.getCellValue( dr, dc)) {
                                failedCell[ gc] = true;
                                setNoData( gr, gc, true);
                            } 
                        } 
                    } 
                }
            }
        } else {
            for( int r = 0; r <= getNumberRows() - 1; r++) {
                for( int c = 0; c <= getNumberColumns() - 1; c++) {
                    double newE = getCellCenterEasting( r, c);
                    double newN = getCellCenterNorthing( r, c);
                    if( data.isNoData( newE, newN)) {
                        setNoData(r, c, true);
                    } else {
                        setCellValue( r, c, data.getCellValue( newE, newN));
                    }
                }
            }
        }
    }

	private void constructorInitializer() {
		v = new int[ getNumberRows()+1][ getNumberColumns()+1];
		minChange = maxChange = true;
	}

	/**
	 * Compare contents of this class to another.
	 * 
	 * @param otherClass the other GISClass to compare against
	 * @return true if both are the same (bounds, grid, and data),
	 *     or false if there are any differences
	 */
	public boolean equalsClass(GISClass otherClass) {
	    boolean equ = equalsGrid(otherClass);
	    // above tested #rows and #cols
	    // now we test all the cells
	    if(equ) {
            outer: for( int r = 0; r < getNumberRows(); r++) {
                for( int c = 0; c < getNumberColumns(); c++) {
                    // verify no-data state
                    if(isNoData(r, c) != otherClass.isNoData(r, c)) {
                        equ = false;
                        break outer;
                    }
                    // if is-data, then verify the data
                    if(!isNoData(r,c)) {
                        int mine = getCellValue(r, c);
                        int other = otherClass.getCellValue(r, c);
                        if(mine != other) {
                            equ = false;
                            break outer;
                        }
                    }
                }
            }
	    }
	    return equ;
	}
	
	public int getNoDataValue() {
	    if(noDataValue == null)
	        throw new DataException("No no-data value defined.");
	    else
	        return noDataValue;
	}

	public void setNoDataValue(Integer value) {
		noDataValue = value;
	}
	
	@Override
	public boolean hasNoDataValue() {
	    return noDataValue != null;
	}

	public void setMinimumChange( boolean value) {
		minChange = value;
	}

	public void setMaximumChange( boolean value) {
		maxChange = value;
	}

	/**
	 * Unset all cells that have the given value.
	 * @param value  value to remove from this class
	 */
	public void setCellValueToNoData( int value) {
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( isNoData( r, c) == false) {
					if( getCellValue( r, c) == value) {
						setNoData( r, c, true);
					}
				}
			}
		}
		minChange = maxChange = true;
	}

	/**
	 * Unset one cell if it has a certain value.
	 * 
	 * @param row  y-index of cell to change
	 * @param column  x-index of cell to change
	 * @param value  cell value to test for
	 */
	public void setCellValueToNoData( int row, int column, int value) {
		if( isNoData( row, column) == false) {
			if( getCellValue( row, column) == value) {
				setNoData( row, column, true);
			}
		}
		minChange = maxChange = true;
	}

	protected void setMainArray( int[][] newArray) {
		v = newArray;
		minChange = maxChange = true;
	}

	public boolean isReady() {
		if( v != null)
			return true;
		return false;
	}

	/** this method makes a set of realizations.
	 *  not implemented yet.
	 */
	public void makeRealizations() {
	    throw new UnsupportedOperationException("GISClass.makeNewRealization() not implemented yet");
	}

	public boolean isRealizable() {
	    throw new UnsupportedOperationException("GISClass.isRealizable() not implemented yet");
	}

	public void setMinimumValue( int value) {
		minValue = value;
		minChange = false;
	}

	public void setMaximumValue( int value) {
		maxValue = value;
		maxChange = false;
	}

	protected void findMinMax() {
		hasMinMax = false;
		minValue = Integer.MAX_VALUE;
		maxValue = Integer.MIN_VALUE;	
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( isNoData( r, c) == false) {
					if( hasMinMax == false) {
						minValue = maxValue = getCellValue( r, c);
						hasMinMax = true;
					} else {
						minValue = Math.min( minValue, getCellValue( r, c));
						maxValue = Math.max( maxValue, getCellValue( r, c));
					}
				}
			}
		}
		minChange = false;
		maxChange = false;
	}

    public int getMinimumValue() {
        if( minChange) {
            findMinMax();
        }
        if(hasMinMax)
            return minValue;
        else
            throw new DataException("GISClass contains no values.");
    }

	public int getMaximumValue() {
		if( maxChange) {
			findMinMax();
		}
		if(hasMinMax)
		    return maxValue;
		else
            throw new DataException("GISClass contains no values.");
	}

	/** in alpha testing */
	public BooleanGrid catBooleanMap( int catNum) {
		BooleanGrid cat = new BooleanGrid( getNumberRows(), getNumberColumns(), false);
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				if( ! isNoData( r, c) && getCellValue( r, c) == catNum)
					cat.setBoolean( r, c, true);
			}
		}
		return( cat);
	}

	/** 
	 * Fetch the value from a cell.
	 * 
	 * @param cellRowNum row (vertical ~ north/south) index
	 * @param cellColumnNum column (horizontal ~ east/west) index
	 * @return value in indicated cell, regardless of no-data status
	 */
	public int getCellValue( int cellRowNum, int cellColumnNum) {
		return( v[ cellRowNum][ cellColumnNum]);
	}

	/**
     * Fetch the value from a cell.
	 * 
	 * @param cellEasting east/west position
	 * @param cellNorthing north/south position
     * @return value in indicated cell, regardless of no-data status
	 */
	public int getCellValue( double cellEasting, double cellNorthing) {
		int row = getRowIndex( cellEasting, cellNorthing);
		int col = getColumnIndex( cellEasting, cellNorthing);
		return( getCellValue( row, col));
	}

	/**
	 * Place a value into a cell.
	 * 
     * @param cellRowNum row (vertical ~ north/south) index
     * @param cellColumnNum column (horizontal ~ east/west) index
	 * @param value new data value for cell
	 */
	public void setCellValue( int cellRowNum, int cellColumnNum, int value) {
		setNoData( cellRowNum, cellColumnNum, false);
		v[ cellRowNum][ cellColumnNum] = (int) value;
		minChange = maxChange = true;
	}

    /**
     * Copy the contents of another object into this one, replacing our
     * contents.
     * 
     * @param grid object from which to get contents
     */
	public void clone( GISClass grid) {
		copyMetaData( grid);
		copyData( grid);
	}

	/**
	 * Copy the contents from another class.
	 * Resolution (number of rows and columns) must match.
	 * 
	 * @param grid  carrier of contents to copy.
	 */
	public void copyData( GISClass grid) {
        assert grid.getNumberColumns()==this.getNumberColumns();
        assert grid.getNumberRows()==this.getNumberRows();

        // Clone value array.
        this.v = Arrays.copyOf(grid.v, grid.v.length);
        for( int r = grid.getNumberRows() - 1; r >= 0; r--) {
            this.v[r] = Arrays.copyOf(this.v[r], this.v[r].length);
        }
        
        // Clone no-data array.
        this.noData = new BooleanGrid(grid.noData);

        // Copy other stuff.
        this.minChange = grid.minChange;
        this.minValue = grid.minValue;
        this.maxChange = grid.maxChange;
        this.maxValue = grid.maxValue;
	}

    /**
     * Copy the contents from a lattice.
     * Resolution (number of rows and columns) must match.
     * This object will receive the floor values (integer
     * portion only) of the doubles in the lattice.
     * 
     * @param lattice  carrier of contents to copy.
     */
    public void copyData( GISLattice lattice) {
        assert lattice.getNumberColumns()==this.getNumberColumns();
        assert lattice.getNumberRows()==this.getNumberRows();
        
        for( int r = lattice.getNumberRows() - 1; r >= 0; r--) {
            for( int c = lattice.getNumberColumns() - 1; c >= 0; c--) {
                setCellValue( r, c, (int)lattice.getCellValue( r, c));
                setNoData( r, c, lattice.isNoData( r, c));
            }
        }
        minChange = maxChange = true;
    }

    /**
     * Load an entire ESRI ASCII file into memory.
     * 
     * @param filename  path and name of file to load
     * @return a new GISClass holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISClass loadEsriAscii(String filename) throws IOException {
        File file = ESRI_ASCII.findFile(filename);
        return loadEsriAscii(file);
    }

    /**
     * Load an entire ESRI ASCII file into memory.
     * 
     * @param file  path and name of file to load
     * @return a new GISClass holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISClass loadEsriAscii(File file) throws IOException {
        MyReader fr = new MyReader(file);
        try {
            GISClass cls = loadEsriAscii(fr);
            cls.setName(file.getName());
            return cls;
        }
        finally {
            fr.close();
        }
    }

    /**
     * Load an entire ESRI ASCII file into memory from an opened reader.
     * Reader will be left at the line following all of the cell data.
     * 
     * @param fr  reader, already open and positioned at the header
     * @return a new GISClass holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISClass loadEsriAscii(MyReader fr) throws IOException {
        // Read header
        GISGrid grid = GISGrid.loadEsriAscii(fr);
        GISClass cls = new GISClass(grid);
        
        int endIndex = 0, startIndex = 0;
        String s = "", numberS = "";

        // Read NODATA line, if present
        s = fr.readLine().trim(); // NODATA_value  -9999 (opt).
        if(s.startsWith("NODATA_value")) {
            startIndex = RGISData.nextNumber( s, 11);
            s = s.substring( startIndex);
            cls.setNoDataValue(new Integer(s.trim()));
            s = fr.readLine().trim();
        }
        else {
            cls.setNoDataValue(null);
        }

        // Read cell values
        startIndex = 0;
        for(int row=0; row<cls.getNumberRows(); row++) {
            for(int col=0; col<cls.getNumberColumns(); col++) {
                if(startIndex >= s.length()) {
                    // out of numbers, advance to next line
                    // must test at top of loop; will crash at end of file
                    // if at bottom
                    s = fr.readLine().trim();
                    startIndex = 0;
                    endIndex = RGISData.endNumber( s, startIndex);
                }
                // find end of number that starts at 'startIndex'
                endIndex = RGISData.endNumber( s, startIndex);
                if( endIndex == s.length() - 1)
                    endIndex++;
                // extract number
                numberS = s.substring( startIndex, endIndex);
                int numberI = Integer.parseInt(numberS.trim());
                // save to class
                if( cls.hasNoDataValue() == true && numberI == cls.getNoDataValue())
                    cls.setNoData( row, col, true);
                else
                    cls.setCellValue(row, col, numberI);
                // advance to next number
                startIndex = RGISData.nextNumber( s, endIndex);
            }
        }
        fr.close();
        
        return cls;
    }
    
	public void writeAsciiGrass( String fileName) throws IOException {
		PrintWriter out = new PrintWriter(
				new BufferedWriter( new FileWriter( grassFileName( fileName))));
		printGrassHeader( out);
		boolean anyNoData = hasAnyNoData();
		int minCat = getMinimumValue();
		int maxCat = getMaximumValue();
		
		int noDataValue;
		if( anyNoData) {
			if( minCat > 0) {
			    noDataValue = 0;
			} else if( minCat > Integer.MIN_VALUE) {
				noDataValue = minCat - 1;
			} else if( maxCat < Integer.MAX_VALUE) {
			    noDataValue = maxCat + 1;
			} else {
			    // Data spans int.min to int.max, so scan for any unused value.
                noDataValue = 0;
				while( noDataValue == 0) {
				    if(minCat == Integer.MAX_VALUE)
				        break;
					minCat++;
					noDataValue = 1;
 					for( int r = getNumberRows() - 1; r >= 0; r--) {
						for( int c = getNumberColumns() - 1; c >= 0; c--) {
							if( ! isNoData( r, c)) {
								if( getCellValue( r, c) == minCat) {
									r = c = -1;
									noDataValue = 0;
								}
							}
						}
					}
				}
				if(minCat == Integer.MAX_VALUE)
				    throw new DataException("Array contains no-data cells, but no positive values can be found to represent no-data in the output file.");
				noDataValue = minCat;
			}
		}
		
		for( int r = 0; r < getNumberRows(); r++) {
			if( isNoData( r, 0)) {
				out.print( getNoDataValue());
			} else {
				out.print( getCellValue( r, 0));
			}
			for( int c = 1; c < getNumberColumns(); c++) {
				if( isNoData( r, c)) {
					out.print( " " + getNoDataValue());
				} else {
					out.print( " " + getCellValue( r, c));
				}
			}
			out.println( "");
		}
		out.close();
	}

	public void writeAsciiEsri( String fileName) throws IOException {
	    File file = new File(ESRI_ASCII.fixName(fileName));
	    writeAsciiEsri(file);
	}

    public void writeAsciiEsri(File file) throws IOException {
        if( getEWResolution() != getNSResolution()) {
            throw new DataException("ESRI ASCII files cannot handle unequal resolutions");
        }

        PrintWriter out = new PrintWriter(
                new BufferedWriter( new FileWriter(file)));

        try {
            // Write header
            out.println( "ncols         " + getNumberColumns());
            out.println( "nrows         " + getNumberRows());
            out.println( "xllcorner     " + (double) getWestEdge());
            out.println( "yllcorner     " + (double) getSouthEdge());
            out.println( "cellsize      " + (double) getEWResolution());
    
            // Scan for smallest value, and the presence of nodata.
            boolean anyNoData = hasAnyNoData();
    
            // If nodata was found, choose a nodata indicator smaller than the
            // smallest value found, and write it to header.
            // Changed by Yizhao Gao (ygao29@illinois.edu)
            /*
            int minCat = getMinimumValue();
            int noDataValue = DEFAULT_NODATA;
            if( anyNoData) {
                if( minCat <= DEFAULT_NODATA)
                    noDataValue = minCat - 1;
                out.println( "NODATA_value  " + noDataValue);
            }
            */
            int noDataValue = DEFAULT_NODATA;
            if(this.noDataValue != null)
            {
            	noDataValue = this.noDataValue;
            }
            if(anyNoData)
            {
            	out.println( "NODATA_value  " + noDataValue);
            }
    
            // Write contents.
            for( int r = 0; r < getNumberRows(); r++) {
                if( isNoData( r, 0)) {
                    out.print( noDataValue);
                } else {
                    out.print( getCellValue( r, 0));
                }
                for( int c = 1; c < getNumberColumns(); c++) {
                    if( isNoData( r, c)) {
                        out.print( " " + noDataValue);
                    } else {
                        out.print( " " + getCellValue( r, c));
                    }
                }
                out.println();
            }
        }
        finally {
            out.close();
        }
    }
	
	/** in alpha testing */
	public void printClass() {
		printGrid();
		if( hasNoDataValue() == true)
			System.out.println( "No data value: " + getNoDataValue());
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( isNoData( r, c)) {
					//System.out.print( "NoData ");
					System.out.print( "N ");
				} else {
					System.out.print( getCellValue( r, c) + " ");
				}
			}
			System.out.println( "");
		}
	}

	/**
	 * Count the number of cells that use each value.
	 * categoryCount[ 0] == the number of cells of category GISClass.getMinimumValue().
	 * 
	 * @return array of integers
	 */
	public int[] makeHistogram() {
		int minValue = getMinimumValue();
		int maxValue = getMaximumValue();
		int[] categoryCount = new int[ maxValue - minValue + 1];
		
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( ! isNoData( r, c)) {
					int cellValue = getCellValue( r, c);
					categoryCount[ cellValue - minValue] += 1;
				}
			}
		}
		return categoryCount;
	}

    /**
     * Return a listing of every value that appears in at least one cell.
     * 
     * @return set of ints that are used in the map
     */
	public Set<Integer> makeInventory() {
	    HashSet<Integer> cats = new HashSet<Integer>();
	    int[] catcnt = makeHistogram();
	    for(int i=0; i<catcnt.length; i++) {
	        if(catcnt[i] > 0)
	            cats.add(i+minValue);
	    }
	    return cats;
	}

	/** the extension for class files informing idrisi which categories should be
	 *  turned into signature files is .cls
	 */
	public String categoryCountFileName( String fileName) {
		return( fileName + ".cat");
	}
}
