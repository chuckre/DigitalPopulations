package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JFileChooser;

import mil.army.usace.ehlschlaeger.rgik.gui.FileExtensionChooser;
import mil.army.usace.ehlschlaeger.rgik.io.ESRI_ASCII;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Defines a rectangular area, subdivided by lines into smaller blocks.
 * Values are stored for the intersection of the lines, not for the blocks.
 * Values are double-precision floats.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISLattice extends GISGrid implements Serializable {
    protected static final int NORTH                = 0;
    protected static final int SOUTH                = 1;
    protected static final int EAST                 = 2;
    protected static final int WEST                 = 3;

    protected static final int NORTHEAST            = 0;
    protected static final int SOUTHEAST            = 1;
    protected static final int NORTHWEST            = 2;
    protected static final int SOUTHWEST            = 3;

    protected static final int CG1997               = 0;
    protected static final int LINEAR               = 1;
    protected static final int GIS2000              = 2;
    
    /**
     * Default indicator for cells that are considered undefined.
     */
    public static final int DEFAULT_NODATA = -999999;
    
    protected double       v[][];
    protected Double       noDataValue;
    protected boolean      minChange, maxChange;
    protected double       minValue, maxValue, minRealizationValue,
            maxRealizationValue;
    protected double  contourInterval = 10.0;
    protected double  contourBase = 0.0;
    protected int     contourIndexRate = 5;
    protected GISLattice[] mapRealizations;
    protected GISLattice   sumOfSquaresRealizations;
    protected int     interpolationStyle   = CG1997;

	/**
     * This constructor opens a JFileChooser object for the user to pick a ESRI
     * ASCII Grid file.
     * 
     * @throws IOException on any error
     */
    public GISLattice() {
        super(0.0, 1.0, 1.0, 1.0, 2, 2);
    }
    
	/**
	 * Create a new blank lattice.
	 * 
	 * @param westEdge
	 * @param northEdge
	 * @param EWResolution
	 * @param NSResolution
	 * @param numRows
	 * @param numCols
	 */
	public GISLattice( double westEdge, double northEdge, double EWResolution,
			double NSResolution, int numRows, int numCols) {
		super( westEdge, northEdge, EWResolution, NSResolution, numRows, numCols);
		constructorInitializer();
	}

    /**
     * Create a new blank lattice.
     * 
     * @param grid metadata for the new lattice
     */
	public GISLattice( GISGrid grid) {
		super( grid.getWestEdge(), grid.getNorthEdge(), grid.getEWResolution(), 
			grid.getNSResolution(),	grid.getNumberRows(), grid.getNumberColumns());
		constructorInitializer();
	}

    /**
     * Create and initialize a new lattice.
     * 
     * @param grid dimensions for the new lattice
     * @param cellValue initial value for all cells
     */
	public GISLattice( GISGrid grid, double cellValue) {
		super( grid.getWestEdge(), grid.getNorthEdge(), grid.getEWResolution(), 
			grid.getNSResolution(),	grid.getNumberRows(), grid.getNumberColumns());
		constructorInitializer();
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				setCellValue( r, c, cellValue);
			}
		}
	}

    /**
     * Create and initialize a new lattice by resampling another lattice.
     * 
     * @param grid
     *            metadata for the new lattice
     * @param data
     *            cell values to be resampled and copied
     * @deprecated Resampling doesn't work right. This method should be
     *             restricted to only extracting sub-grids.
     */
	public GISLattice( GISGrid grid, GISLattice data) {
		super( grid.getWestEdge(), grid.getNorthEdge(), grid.getEWResolution(), 
			grid.getNSResolution(), grid.getNumberRows(), grid.getNumberColumns());
		constructorInitializer();
		this.resampleDataToGrid( data);
	}

    private void constructorInitializer() {
		v = new double[ getNumberRows()][ getNumberColumns()];
		constructorInitializerNoAllocation();
	}

    public void setRealizationInterpolationStyle( int value) {
        interpolationStyle = value;
    }

    public int getRealizationInterpolationStyle() {
        return interpolationStyle;
    }

    public double getContourBase() {
        return contourBase;
    }

    public void setContourBase( double value) {
        contourBase = value;
        if( isRealizable() == true) {
            for( int i = mapRealizations.length - 1; i >= 0; i--) {
                mapRealizations[ i].setContourBase( value);
            }
        }
    }

    public int getContourIndexRate() {
        return contourIndexRate;
    }

    public void setContourIndexRate( int value) {
        contourIndexRate = value;
        if( isRealizable() == true) {
            for( int i = mapRealizations.length - 1; i >= 0; i--) {
                mapRealizations[ i].setContourIndexRate( value);
            }
        }
    }

    public double getContourInterval() {
        return contourInterval;
    }

    public void setContourInterval( double value) {
        contourInterval = value;
        if( isRealizable() == true) {
            for( int i = mapRealizations.length - 1; i >= 0; i--) {
                mapRealizations[ i].setContourInterval( value);
            }
        }
    }

    public double getMinRealizationValue() {
        return minRealizationValue;
    }

    public double getMaxRealizationValue() {
        return maxRealizationValue;
    }

	private void constructorInitializerNoAllocation() {
		minChange = maxChange = true;
		minRealizationValue = Double.POSITIVE_INFINITY;
		maxRealizationValue = Double.NEGATIVE_INFINITY;
	}

	public double distanceMap( BooleanGrid start) {
		double maxDistance = (double) 0.0;
		BooleanGrid done = new BooleanGrid( start.getRows(), start.getColumns(), false);
		FromToArray toDo = new FromToArray();
		int doneCount = 0;
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				if( start.getBoolean( r, c)) {
					setCellValue( r, c, (double) 0.0);
					doneCount++;
					done.setBoolean( r, c, true);
					if( r > 0 && ! start.getBoolean( r - 1, c)) {
						toDo.addFT( r, c, r - 1, c, 	distance( r, c, r - 1, c));
					}
					if( r < getNumberRows() - 1 && ! start.getBoolean( r + 1, c)) {
						toDo.addFT( r, c, r + 1, c, 	distance( r, c, r + 1, c));
					}
					if( c > 0 && ! start.getBoolean( r, c - 1)) {
						toDo.addFT( r, c, r, c - 1, 	distance( r, c, r, c - 1));
					}
					if( c < getNumberColumns() - 1 && ! start.getBoolean( r, c + 1)) {
						toDo.addFT( r, c, r, c + 1, 	distance( r, c, r, c + 1));
					}
				}
			}
		}
		FromTo doFT = toDo.getNextFT();
		int total = start.getRows() * start.getColumns();
		int donePercent = 0;
		System.out.println( "rows: " + start.getRows() + ", columns: " + start.getColumns());
		while( doFT != null) {
			int r = doFT.getToRow();
			int c = doFT.getToColumn();
			if( ! done.getBoolean( r, c) || getCellValue( r, c) > doFT.getDistance()) {
				setCellValue( r, c, doFT.getDistance());
				done.setBoolean( r, c, true);
				if( 100 * ++doneCount / total > donePercent) {
					donePercent = 100 * doneCount / total;
					System.out.print( "\b\b\b" + donePercent);
				}
				if( r > 0 && ! done.getBoolean( r - 1, c) && ! start.getBoolean( r - 1, c)) {
					toDo.addFT( doFT.getFromRow(), doFT.getFromColumn(), r - 1, c, 
						distance( doFT.getFromRow(), doFT.getFromColumn(), r - 1, c));
				}
				if( r < getNumberRows() - 1 && ! done.getBoolean( r + 1, c) && ! start.getBoolean( r + 1, c)) {
					toDo.addFT( doFT.getFromRow(), doFT.getFromColumn(), r + 1, c, 
						distance( doFT.getFromRow(), doFT.getFromColumn(), r + 1, c));
				}
				if( c > 0 && ! done.getBoolean( r, c - 1) && ! start.getBoolean( r, c - 1)) {
					toDo.addFT( doFT.getFromRow(), doFT.getFromColumn(), r, c - 1, 
						distance( doFT.getFromRow(), doFT.getFromColumn(), r, c - 1));
				}
				if( c < getNumberColumns() - 1 && ! done.getBoolean( r, c + 1) && ! start.getBoolean( r, c + 1)) {
					toDo.addFT( doFT.getFromRow(), doFT.getFromColumn(), r, c + 1, 
						distance( doFT.getFromRow(), doFT.getFromColumn(), r, c + 1));
				}
			}
			doFT = toDo.getNextFT();
		}
		System.out.println("");
		return( maxDistance);
	}

	/** This method makes a map of the average value of a square of size filtersize 
	 *  around each resulting cell. If anyNoDataIsNoData is true, all cells in the
	 *  square must have data values or the resulting cell is given a No Data result
	 */
	public GISLattice getAverageFilterMap( int filterSize, boolean anyNoDataIsNoData) {
		GISLattice result = new GISLattice( this);
		int rows = getNumberRows();
		int cols = getNumberColumns();
		for( int r = rows - 1; r >= 0; r--) {
			for( int c = cols - 1; c >= 0; c--) {
				int count = 0;
				double value = 0.0;
				for( int rr = Math.max( 0, r - (filterSize / 2)); 
						rr < Math.min( rows, r + (filterSize / 2)); rr++) {
					for( int cc = Math.max( 0, c - (filterSize / 2)); 
							cc < Math.min( cols, c + (filterSize / 2)); cc++) {
						if( isNoData( rr, cc) == false) {
							count++;
							value += getCellValue( rr, cc);
						} else if( anyNoDataIsNoData == true) {
							rr = rows;
							cc = cols;
							count = 0;
						}
					}
				}
				if( count > 0) {
					result.setCellValue( r, c, value / count);
				}
			}
		}
		return result;
	}

	public double getCellValue( int cellRowNum, int cellColumnNum) {
		return( v[ cellRowNum][ cellColumnNum]);
	}

	public double getCellValue( double cellEasting, double cellNorthing) {
		return( v[ (int) ((getNorthEdge() - cellNorthing) / getNSResolution())]
			[ (int) ((cellEasting - getWestEdge()) / getEWResolution())]);
	}

    public double getNoDataValue() {
        if(noDataValue == null)
            throw new DataException("No no-data value defined.");
        else
            return noDataValue;
    }

    public void setNoDataValue(Double value) {
        noDataValue = value;
    }
    
	@Override
	public boolean hasNoDataValue() {
	    return noDataValue != null;
	}
	
	public double getMaximumValue() {
		if( maxChange) {
			setMinMax();
			minChange = false;
			maxChange = false;
		}
		return maxValue;
	}

	public double getMinimumValue() {
		if( minChange) {
			setMinMax();
			minChange = false;
			maxChange = false;
		}
		return minValue;
	}

	public double getValue( int cellRowNum, int cellColumnNum) {
		return( v[ cellRowNum][ cellColumnNum]);
	}

	/**
	 * Estimate a value at precise coordinates by linear interpolation between
	 * surrounding lattice lines.
	 */
	public double getValue( double easting, double northing) {
		if( onMap( easting, northing) == false) {
		    throw new DataException("location (" + easting + ", " + northing + ") not on map");
		}
		double nsRes = getNSResolution();
		double ewRes = getEWResolution();
		double indexNS = ((getNorthEdge() - northing) / nsRes);
		double indexEW = ((easting - getWestEdge()) / ewRes);
		int hihNS = (int) Math.ceil( indexNS);
		int lowNS = (int) Math.floor( indexNS); 
		int hihEW = (int) Math.ceil( indexEW);
		int lowEW = (int) Math.floor( indexEW);
		int maxRow = getNumberRows() - 1;
		int maxCol = getNumberColumns() - 1;
		if( hihNS > maxRow)
			hihNS = maxRow;
		//if( hihNS < 0)
		//	hihNS = 0;
		//if( lowNS > maxRow)
		//	lowNS = maxRow;
		if( lowNS < 0)
			lowNS = 0;
		if( hihEW > maxCol)
			hihEW = maxCol;
		//if( hihEW < 0)
		//	hihEW = 0;
		//if( lowEW > maxCol)
		//	lowEW = maxCol;
		if( lowEW < 0)
			lowEW = 0;
		double lowNorthing = getCellCenterNorthing( lowNS, lowEW);
		double lowEasting = getCellCenterEasting( lowNS, lowEW);
		double pN = (northing - lowNorthing) / nsRes;
		double pW = (easting - lowEasting) / ewRes;
		return( getCellValue( lowNS, lowEW) * (1.0 - pN) * (1.0 - pW) +
			getCellValue( hihNS, lowEW) * pN * (1.0 - pW) +
			getCellValue( lowNS, hihEW) * (1.0 - pN) * pW +
			getCellValue( hihNS, hihEW) * pN * pW);
	}

	/**
	 * This method uses inverse distance interpolation to determine lattice value at
	 * easting, northing location. 
	 */
	public double getValueIDW( double cellEasting, double cellNorthing) {
		double indexNS = ((getNorthEdge() - cellNorthing) / getNSResolution());
		double indexEW = ((cellEasting - getWestEdge()) / getEWResolution());
		double sum = 0.0;
		double count = 0.0;
		int hihNS = (int) Math.ceil( indexNS);
		int lowNS = (int) Math.floor( indexNS);
		int hihEW = (int) Math.ceil( indexEW);
		int lowEW = (int) Math.floor( indexEW);
		if( ! isNoData( lowEW, lowNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( lowEW, lowNS), 
				getCellCenterNorthing( lowEW, lowNS));
			if( dSq <= 0.0000000001)
				return( getCellValue( lowNS, lowEW));
			count += 1 / dSq;
		}
		if( ! isNoData( hihEW, lowNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( hihEW, lowNS), 
				getCellCenterNorthing( hihEW, lowNS));
			if( dSq <= 0.0000000001)
				return( getCellValue( hihNS, lowEW));
			count += 1 / dSq;
		}
		if( ! isNoData( lowEW, hihNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( lowEW, hihNS), 
				getCellCenterNorthing( lowEW, hihNS));
			if( dSq <= 0.0000000001)
				return( getCellValue( lowNS, hihEW));
			count += 1 / dSq;
		}
		if( ! isNoData( hihEW, hihNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( hihEW, hihNS), 
				getCellCenterNorthing( hihEW, hihNS));
			if( dSq <= 0.0000000001)
				return( getCellValue( hihNS, hihEW));
			count += 1 / dSq;
		}
		if( ! isNoData( lowEW, lowNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( lowEW, lowNS), 
				getCellCenterNorthing( lowEW, lowNS));
			sum += (1 / dSq) * getCellValue( lowNS, lowEW);
		}
		if( ! isNoData( hihEW, lowNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( hihEW, lowNS), 
				getCellCenterNorthing( hihEW, lowNS));
			sum += (1 / dSq) * getCellValue( hihNS, lowEW);
		}
		if( ! isNoData( lowEW, hihNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( lowEW, hihNS), 
				getCellCenterNorthing( lowEW, hihNS));
			sum += (1 / dSq) * getCellValue( lowNS, hihEW);
		}
		if( ! isNoData( hihEW, hihNS)) {
			double dSq = distanceSquared( cellEasting, cellNorthing,
				getCellCenterEasting( hihEW, hihNS), 
				getCellCenterNorthing( hihEW, hihNS));
			sum += (1 / dSq) * getCellValue( hihNS, hihEW);
		}
		if( count > 0.0) {
			return( sum / count);
		}
		return( (double) 0.0);
	}

    /**
     * Determine if an interpolated location should be considered no-data.
     */
	public boolean isNoData4Corners(double cellEasting, double cellNorthing) {
        double indexNS = ((getNorthEdge() - cellNorthing) / getNSResolution());
        double indexEW = ((cellEasting - getWestEdge()) / getEWResolution());
        int hihNS = (int) Math.ceil(indexNS);
        int hihEW = (int) Math.ceil(indexEW);
        if (isNoData(hihNS, hihEW) == true)
            return true;
        int lowNS = (int) Math.floor(indexNS);
        if (isNoData(lowNS, hihEW) == true)
            return true;
        int lowEW = (int) Math.floor(indexEW);
        if (isNoData(hihNS, lowEW) == true)
            return true;
        if (isNoData(lowNS, lowEW) == true)
            return true;
        return false;
    }

	public boolean isRealizable() {
		if( mapRealizations != null && mapRealizations.length > 0)
			return true;
		else	return false;
	}

	/** this method makes a set of realizations.
	 *  not implemented yet.
	 */
	public void makeRealizations() {
		System.out.println( "GISLattice.makeNewRealization() not implemented yet");
	}

	public void printLattice(PrintStream out) {
		out.println( "n: " + getNorthEdge() + ", s: " + getSouthEdge() + ", e: " + 
			getEastEdge() + ", w: " + getWestEdge());
		out.println( "rows: " + getNumberRows() + ", cols: " + getNumberColumns());
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( isNoData( r, c)) {
					out.print( "NoData ");
				} else {
					out.print( getCellValue( r, c) + " ");
				}
			}
			out.println( "");
		}
	}

	/**
	 * Change the value of a single cell.
	 * @param cellRowNum
	 * @param cellColumnNum
	 * @param value
	 */
	public void setCellValue( int cellRowNum, int cellColumnNum, double value) {
		setNoData( cellRowNum, cellColumnNum, false);
		v[ cellRowNum][ cellColumnNum] = value;
	}

	/**
	 * Wipe the entire lattice, and replace every cell with one value.
	 * @param value
	 */
	public void setValueAll( double value) {
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				setCellValue( r, c, value);
			}
		}
	}

	/**
	 * Resamples data to the grid of the object calling the method.
	 * @deprecated Doesn't seem to work, plus I'm not really sure how this
	 *     is meant to work.  We need a new Resample class to wrap and document
	 *     this feature.
	 */
	public void resampleDataToGrid( GISLattice data) {
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				double easting = getCellCenterEasting( r, c);
				double northing = getCellCenterNorthing( r, c);
				if( data.isNoData4Corners( easting, northing)) {
					setNoData( r, c, true);
				} else {
					v[ r][ c] = data.getValue( easting, northing);
				}
			}
		}
	}

    /**
     * Create a new lattice containing only a subset of our data.
     * All grids must have same resolution.  'range' will be assumed to align
     * with the closest lattice lines to the northeast.
     * 
     * @param range
     *            specs for area new lattice should cover
     * @return new lattice that only covers given range
     */
	public GISLattice extract(GISGrid range) {
	    if(range.getEWResolution() != getEWResolution()
	            || range.getNSResolution() != getNSResolution())
	        throw new DataException("New lattice must have same resolution");
	    if(range.getWestEdge() < getWestEdge()
	            || range.getEastEdge() > getEastEdge()
	            || range.getSouthEdge() < getSouthEdge()
	            || range.getNorthEdge() > getNorthEdge())
	        throw new DataException("New lattice is outside our bounds.");
	    
        // Compute the location of the new grid within our grid.
        int r0 = getExactRowIndex(range.getNorthEdge());
        int c0 = getExactColumnIndex(range.getWestEdge());
        
        GISLattice newlat = new GISLattice(range);
        
        // Copy data into new grid.
        for(int r=0; r<range.getNumberRows(); r++) {
            for(int c=0; c<range.getNumberColumns(); c++) {
                if(! isNoData(r0+r, c0+c))
                    newlat.setCellValue(r, c, getCellValue(r0+r, c0+c));
            }
        }
        
	    return newlat;
	}

    /**
     * Create a new lattice containing only a subset of our data. 'range' will
     * be assumed to align with the closest lattice lines to the northeast.
     *
     * @param range
     *            geographic area new lattice should cover
     * @param where
     *            cells will not be copied wherever this grid is no-data
     *
     * @return new lattice that only covers given range
     */
	public GISLattice extractWhere(GISGrid range, GISGrid where) {
        if(range.getEWResolution() != getEWResolution()
                || range.getNSResolution() != getNSResolution())
            throw new DataException("New lattice must have same resolution.");
        if(range.getWestEdge() < getWestEdge()
                || range.getEastEdge() > getEastEdge()
                || range.getSouthEdge() < getSouthEdge()
                || range.getNorthEdge() > getNorthEdge())
            throw new DataException("New lattice is outside our bounds.");
        if(range.getWestEdge() < where.getWestEdge()
                || range.getEastEdge() > where.getEastEdge()
                || range.getSouthEdge() < where.getSouthEdge()
                || range.getNorthEdge() > where.getNorthEdge())
            throw new DataException("New lattice is outside 'where' bounds.");
        
        // Compute the location of the new grid within our grid.
        int r0 = getExactRowIndex(range.getNorthEdge());
        int c0 = getExactColumnIndex(range.getWestEdge());
        
        // Compute the location of the new grid within the 'where' grid.
        int Wr0 = where.getExactRowIndex(range.getNorthEdge());
        int Wc0 = where.getExactColumnIndex(range.getWestEdge());

        GISLattice newlat = new GISLattice(range);
        
        // Copy data into new grid.
        for(int r=0; r<range.getNumberRows(); r++) {
            for(int c=0; c<range.getNumberColumns(); c++) {
                boolean nodata = isNoData(r0+r, c0+c) || where.isNoData(Wr0+r, Wc0+c);
                newlat.setNoData(r, c, nodata);
                if(!nodata)
                    newlat.setCellValue(r, c, getCellValue(r0+r, c0+c));
            }
        }
        
        return newlat;
	}
	
	protected void setMinMax() {
		minValue = Double.POSITIVE_INFINITY;
		maxValue = Double.NEGATIVE_INFINITY;	
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				if( isNoData( r, c) == false) {
					minValue = Math.min( minValue, getCellValue( r, c));
					maxValue = Math.max( maxValue, getCellValue( r, c));
				}
			}
		}
	}

    public GISLattice[] getRealizations() {
        return mapRealizations;
    }

    public GISLattice getSumOfSquaresRealizations() {
        return sumOfSquaresRealizations;
    }
    
	public void setRealizations( GISLattice[] realizations) {
		int nR = realizations.length;
		setNumberDataRealizations( nR); 
		mapRealizations = realizations;
		sumOfSquaresRealizations = new GISLattice( this);
		for( int r = this.getNumberRows() - 1; r >= 0; r--) {
			for( int c = this.getNumberColumns() - 1; c >= 0; c--) {
				sumOfSquaresRealizations.setCellValue( r, c, 0);
			}
		}
		minRealizationValue = Double.POSITIVE_INFINITY;
		maxRealizationValue = Double.NEGATIVE_INFINITY;
		for( int i = 0; i < nR; i++) {
			for( int r = this.getNumberRows() - 1; r >= 0; r--) {
				for( int c = this.getNumberColumns() - 1; c >= 0; c--) {
					if( mapRealizations[ i].isNoData( r, c) == false) {
						double value = mapRealizations[ i].getCellValue( r, c);
						if( minRealizationValue > value)
							minRealizationValue = value;
						if( maxRealizationValue < value)
							maxRealizationValue = value;
						if( this.isNoData( r, c) == false) {
							sumOfSquaresRealizations.setCellValue( r, c, 
							  sumOfSquaresRealizations.getCellValue( r, c) +
							  Math.pow( value - this.getCellValue( r, c), 2.0));
						} else {
							this.setNoData( r, c, true);
						}
					} else {
						this.setNoData( r, c, true);
					}
				}
			}
		}
	}

    @Override
    public boolean equals(Object obj) {
        GISLattice g1 = this;
        GISLattice g2 = (GISLattice) obj;
        return 
            // GISData
            g1.equalsGrid(g2)
            && ObjectUtil.objEquals(g1.noData, g2.noData)
            && g1.projection == g2.projection
            && g1.projectionZone == g2.projectionZone
            // GISLattice
            && Arrays.deepEquals(g1.v, g2.v)
            && g1.contourBase == g2.contourBase
            && g1.contourIndexRate == g2.contourIndexRate
            && g1.contourInterval == g2.contourInterval
            && Arrays.deepEquals(g1.mapRealizations, g2.mapRealizations)
            && ObjectUtil.objEquals(g1.sumOfSquaresRealizations, g2.sumOfSquaresRealizations)
            && g1.interpolationStyle == g2.interpolationStyle;
    }
    
    public double interpolateValues( int first, int second, int row, int col, double i) {
        double value = 0.0;
        switch( interpolationStyle) {
            case CG1997:
                double sd = Math.pow( 
                    sumOfSquaresRealizations.getCellValue( row, col) / (mapRealizations.length - 1), 0.5);
                double meanValue = getCellValue( row, col);
                double firstValue = mapRealizations[ first].getCellValue( row, col);
                double secondValue = mapRealizations[ second].getCellValue( row, col);
                double firstSD = (firstValue - meanValue) / sd;
                double secondSD = (secondValue - meanValue) / sd;
                double sdBoth = firstSD * Math.cos( i * Math.PI / 2.0) + 
                    secondSD * Math.sin( i * Math.PI / 2.0);
                value = meanValue + sdBoth * sd;
                break;
            case LINEAR:
                value = mapRealizations[ first].getCellValue( row, col) * (1.0 - i) +
                    mapRealizations[ second].getCellValue( row, col) * i;
                break;
            case GIS2000:
                throw new DataException("GIS2000 interpolation method "+interpolationStyle+" not implemented yet.");
        }
        return value;
    }

    public double ratioEdge( double lowValue, double hihValue, int contour) {
        double cHeight = (contour * contourInterval) + contourBase;
        double ratio = (hihValue - cHeight) / (hihValue - lowValue);
        /*
        if( ratio > 1.0 || ratio < 0.0) {
            System.out.println( "LV: " + lowValue + ", HV: " + hihValue + 
                ", contour: " + contour + ", ratio: " + ratio);
        }
        */
        return ratio;
    }

    /**
     * Load an entire ESRI ASCII file into memory.
     * 
     * @param filename  path and name of file to load
     * @return a new GISLattice holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISLattice loadEsriAscii(String filename) throws IOException {
        File file = ESRI_ASCII.findFile(filename);
        return loadEsriAscii(file);
    }

    /**
     * Load an entire ESRI ASCII file into memory.
     * 
     * @param file  path and name of file to load
     * @return a new GISLattice holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISLattice loadEsriAscii(File file) throws IOException {
        
        MyReader fr = new MyReader(file);
        try {
            GISLattice lattice = loadEsriAscii(fr);
            lattice.setName(file.getName());
            return lattice;
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
     * @return a new GISLattice holding the contents of the file
     * @throws IOException on any file error
     */
    public static GISLattice loadEsriAscii(MyReader fr) throws IOException {
        // Read header
        GISGrid grid = GISGrid.loadEsriAscii(fr);
        GISLattice lattice = new GISLattice(grid);
        
        int endIndex = 0, startIndex = 0;
        String s = "", numberS = "";

        // Read NODATA line, if present
        s = fr.readLine().trim(); // NODATA_value  -9999 (opt).
        if(s.startsWith("NODATA_value")) {
            startIndex = RGISData.nextNumber( s, 11);
            s = s.substring( startIndex);
            lattice.setNoDataValue(new Double(s.trim()));
            s = fr.readLine().trim();
        } else {
            lattice.setNoDataValue(null);
        }

        // Read cell values
        startIndex = 0;
        for(int row=0; row<lattice.getNumberRows(); row++) {
            for(int col=0; col<lattice.getNumberColumns(); col++) {
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
 /*               String trimmedNumberString = numberS.trim();
                if(trimmedNumberString == null || trimmedNumberString.isEmpty())
                {
                	System.out.println("Row: " + row + "\tCol: " + col);
                	System.out.println("numberS: " + numberS);
                }*/
                double numberD = Double.parseDouble(numberS.trim());
                // save to lattice
                if( lattice.hasNoDataValue() == true && numberD == lattice.getNoDataValue())
                    lattice.setNoData( row, col, true);
                else {
                    lattice.setNoData( row, col, false);
                    lattice.setCellValue(row, col, numberD);
                }
                // advance to next number
                startIndex = RGISData.nextNumber( s, endIndex);
            }
        }
        
        return lattice;
    }
	

    /**
     * Write all data in this map to a file formatted as ESRI ASCII.
     * 
     * @param fileName path and name of file to create
     * @throws IOException on any file error
     */
	public void writeAsciiEsri( String fileName) throws IOException {
	    File file = new File(ESRI_ASCII.fixName(fileName));
        writeAsciiEsri(file);
	}

	/**
	 * Write all data in this map to a file formatted as ESRI ASCII.
	 * 
     * @param file path and name of file to create
     * @throws IOException on any file error
	 */
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
    
            boolean anyNoData = hasAnyNoData();
            double minCat = getMinimumValue();
    
            // If nodata was found, choose a nodata indicator smaller than the
            // smallest value found, and write it to header.
            double noDataValue = DEFAULT_NODATA;
            if( anyNoData) {
                if( minCat <= DEFAULT_NODATA)
                    noDataValue = Math.floor(minCat) - 1.0;
                out.println( "NODATA_value  " + noDataValue);
            }
    
            // Write contents.
            for( int r = 0; r < getNumberRows(); r++) {
                for( int c = 0; c < getNumberColumns(); c++) {
                    if(c > 0)
                        out.print(" ");
                    
                    if (isNoData(r, c))
                        out.print(noDataValue);
                    else
                        out.print((float)getCellValue(r, c));
                }
                out.println();
            }
        }
        finally {
            out.close();
        }
    }

    public static GISLattice loadFromChooser() throws IOException {
        GISLattice lattice = new GISLattice(0.0, 1.0, 1.0, 1.0, 2, 2);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("GISLattice File Chooser");
        chooser.setMultiSelectionEnabled(false);
        FileExtensionChooser filter = new FileExtensionChooser();
        filter.addExtension("asc");
        filter.setDescription("ASCII Grid File");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            lattice.setName(chooser.getSelectedFile().getName());
            System.out.println("You chose to open this file: "
                    + chooser.getSelectedFile().getName());
            
            lattice = loadEsriAscii(chooser.getSelectedFile());
            
            chooser.setDialogTitle("GISLattice Map Realizations File Chooser");
            chooser.setMultiSelectionEnabled(true);
            chooser.setSelectedFiles(null);
            returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                if (files != null) {
                    int numFiles = files.length;
                    lattice.setNumberDataRealizations(numFiles);
                    GISLattice[] mR = new GISLattice[numFiles];
                    for (int i = 0; i < numFiles; i++) {
                        mR[i] = GISLattice.loadEsriAscii(files[i]);
                    }
                    lattice.setRealizations(mR);
                }
            } else {
                lattice.setNumberDataRealizations(0);
            }
        } else {
            throw new IOException("GISLattice.GISLattice() ERROR: You must choose a file");
        }
        return lattice;
    }

/*
	public static void main( String[] args) {
		// beginning of code to set up RGISAnimatedView object 
		//GISLattice t = new GISLattice( "TestAllFour0");
		GISLattice t = new GISLattice();
		System.out.println( "");
		System.out.println( "rows: " + t.getNumberRows() + ", columns: " + t.getNumberColumns());
		t.setContourInterval( 10.);
		t.setContourIndexRate( 5);
		//t.setContourBase( 0.01);
		t.setDrawAsContours( true);
		GISData gisObject[] = new GISData[ 1];
		gisObject[ 0] = (GISData) t; 
		gisObject[ 0].setStartTime( new Date());
		RGISAnimatedView view = new RGISAnimatedView( 1000, 1000, 2, 30, 1.0);
		view.setData( gisObject);
		//view.setSize( 1000, 1000);
		Frame f = new AnimationFrame( view, 1000, 1000, false);
		f.setVisible( true);
		// end of code to set up RGISAnimatedView object
	}
*/
}


		/* this demonstrates problem with ?raster? location 
		GISLattice t = new GISLattice( "dem_small");
		System.out.println( "");
		System.out.println( "rows: " + t.getNumberRows() + ", columns: " + t.getNumberColumns());
		t.setContourInterval( 10.0);
		//t.setDrawAsContours( true);
		GISData gisObject[] = new GISData[ 2];
		gisObject[ 1] = (GISData) t; 
		gisObject[ 1].setStartTime( new Date());
		GISLattice t2 = new GISLattice( "dem_small");
		t2.setContourInterval( 10.0);
		t.setDrawAsContours( true);
		gisObject[ 0] = (GISData) t2;
		*/
