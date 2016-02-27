package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import mil.army.usace.ehlschlaeger.rgik.gui.GraphLine2D;
import mil.army.usace.ehlschlaeger.rgik.statistics.LatticeSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;

/**
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class SemiVariogram extends RGIS implements LatticeSpatialStatistic {
	private int numLags, numMaps, countArray[];
	private double lagDist, maxDist;
	private double oWeight, uWeight, iWeight, decayExp;
	private double covar[];
	private GISio gisIO;
	private Filter filter;
	private boolean calculated;
	private SemiVariogram mapSVs[];

	public int getNumberLags() {
		return numLags;
	}

	public SemiVariogram( double lagDistance, double maximumDistance, GISLattice map) {
		// true for multiMeasure, true for oneMapMeasure
		initVars( lagDistance, maximumDistance);
		calcCov( map, -1);
	}

	/** in alpha testing */
	public SemiVariogram( double lagDistance, double maximumDistance, GISLattice maps[]) {
		initVars( lagDistance, maximumDistance);
		initialize( maps);
	}

	/**
	 * This constructor should be used when creating a LatticeSDUM.
	 * in alpha testing.
	 */
	public SemiVariogram( double lagDistance, double maxDistance) {
		initVars( lagDistance, maxDistance);
	}

	/** in alpha testing */
	private void initVars( double lagDistance, double maxDistance) {
		gisIO = new GISio( 12);
		numLags = (int) Math.ceil( (double) maxDistance / lagDistance);
		lagDist = lagDistance;
		maxDist = maxDistance;
		oWeight = uWeight = 1.0f;
		decayExp = 0.0f;
		countArray = new int[ numLags];
		covar = new double[ numLags];
		numMaps = -1;
		for( int lag = 0; lag < numLags; lag++) {
			countArray[ lag] = 0;
			covar[ lag] = 0.0;
		}
		calculated = false;
	}

	/**
	 * initialize() is used by LatticeSDUM to initialize semivariogram for all maps.
	 * in alphs testing.
	 */
	public void initialize( GISData dataMaps[]) {
		GISLattice[] maps = (GISLattice[]) dataMaps;
		filter = new Filter( maps[0], maxDist);
		countArray = new int[ numLags];
		covar = new double[ numLags];
		numMaps = maps.length;
		mapSVs = new SemiVariogram[ maps.length];
		for( int m = 0; m < maps.length; m++) {
			mapSVs[ m] = new SemiVariogram( this.lagDist, this.maxDist);
		}
		for( int lag = 0; lag < numLags; lag++) {
			countArray[ lag] = 0;
			covar[ lag] = 0.0;
		}
		calculated = false;
		for( int m = 0; m < maps.length; m++) {
			calcCov( maps[ m], m);
		}
	}


	/** in alpha testing */
	private void calcCov( GISLattice map, int mapNumber) {
		double doCell = 0;
		double distance = 0.0;
		int endR = map.getNumberRows() - 1;
		int endC = map.getNumberColumns() - 1;
		for( int doR = endR; doR >= 0; doR--) {
			for( int doC = endC; doC >= 0; doC--) {
				if( map.isNoData( doR, doC) == false) {
					doCell = map.getCellValue( doR, doC);
					for( int r = doR; r >= 0; r--) {
						for( int c = doC - 1; c >= 0; c--) {
							if( (distance = map.distance( doR, doC, r, c)) > maxDist) {
								c = -1;
							} else {
								if( map.isNoData( r, c) == false) {
									addCov( doCell, map.getCellValue( r, c), distance, mapNumber);
								}
							}
						}
						if( r != doR) {
							for( int c = doC; c <= endC; c++) {
								if( (distance = map.distance( doR, doC, r, c)) > maxDist) {
									c = endC + 1;
								} else {
									if( map.isNoData( r, c) == false) {
										addCov( doCell, map.getCellValue( r, c), distance, mapNumber);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/** in alpha testing */
	private void addCov( double iValue, double jValue, double dist, int mapNumber) {
		int i = getLag( dist);
		covar[ i] += (iValue - jValue) * (iValue - jValue);
		countArray[ i]++;
		if( mapNumber >= 0) {
			mapSVs[ mapNumber].covar[ i] += (iValue - jValue) * (iValue - jValue);
			mapSVs[ mapNumber].countArray[ i]++;
		}
	}

	/**
	 * This constructor should be used when creating SemiVariogram copies within a LatticeSDUM.
	 * in alpha testing.
	 */
	public SpatialStatistic createCopy() {
		return( (SpatialStatistic) this.newCopy());
	}

	/**
	 *	This method copies the information from one semivariogram to a new
	 *	semivariogram. 
	 *	in alpha testing.
	 */
	public SemiVariogram newCopy() {
		SemiVariogram newC = new SemiVariogram( this.getLagDistance(), 
			this.getMaximumDistance());
		newC.copy( this);
		return newC;
	}

	/**
	 *	This method copies the information from one semivariogram to another. Both
	 * semivariograms must have the same meta information. Use newCopy() to
	 * construct semivariogram copy from scratch.
	 *	in alpha testing.
	 */
	public void copy(SemiVariogram c) {
		// ZZZ needed?		maps = c.maps;
		calculated = c.getCalculated();
		oWeight = c.oWeight;
		uWeight = c.uWeight;
		decayExp = c.decayExp;
		gisIO = c.gisIO;
		filter = c.filter;
		numMaps = c.numMaps;
		for( int lag = 0; lag < c.numLags; lag++) {
			countArray[ lag] = c.countArray[ lag];
			covar[ lag] = c.covar[ lag];
		}
		if( numMaps > 0) {
			mapSVs = new SemiVariogram[ numMaps];
			for( int m = 0; m < numMaps; m++) {
				mapSVs[ m] = c.mapSVs[ m].newCopy();
			}
		}
	}

	/** in alpha testing */
	public int getLag( double distance) {
		return( (int) Math.min( (int) (distance / lagDist), numLags - 1));
	}

	/** in alpha testing */
	public double getInertiaValue( int lag) {
		if(calculated) {
			return( covar[ lag]);
		}
		if( countArray[ lag] > 0) {
			//System.out.print( "lag: " + lag + ", covar: " + covar[ lag] + ", count: " + countArray[ lag] + "	  ");
			return( covar[ lag] / countArray[ lag]);
		}
		return( (double) 0.0);
	}

	public void setInertiaValue( int lag, double value) {
		calculated = true;
		covar[ lag] = value;
	}

	/** in alpha testing */
	public void calculate() {
		if(!calculated) {
			for( int lag = 0; lag < numLags; lag++) {
				covar[ lag] = getInertiaValue( lag);
			} 
			calculated = true;
		}
	}

	/**
	 * This method should be used when modifying SemiVariogram within a LatticeSDUM. Multimodify
	 * must be used because cell swapping would require querying every map cell for every modify.
	 * in alpha testing.
	 */
	public void modify( GISGrid gridMaps[], int mapA, int mapB, int row, int col) {
		GISLattice[] maps = (GISLattice[]) gridMaps;
		double valueA = maps[ mapA].getCellValue( row, col);
		double valueB = maps[ mapB].getCellValue( row, col);
		modifyMap( maps[ mapA], row, col, valueB, mapA);
		modifyMap( maps[ mapB], row, col, valueA, mapB);
	}

	private void modifyMap( GISLattice map, int row, int col, 
	    double newValue, int mapNumber) {
        if(calculated) {
            throw new IllegalStateException("Can't modify calculated SemiVariogram");
		}
		/* stuff shouldn't be needed
		if( filter == null) {
			filter = new Filter( map, getMaximumDistance());
			System.out.println( "making new filter in Semivariogram.modifyMap()");
		}
		*/
		for( int r = filter.getMinRow( row); r < row; r++) {
			for( int c = col; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, row, col, r, c, map, mapNumber, true) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
			}
			for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, row, col, r, c, map, mapNumber, true) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
			}
		}
		for( int c = col + 1; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( row, c) == false) {
					//if( modifyCell( newValue, row, col, row, c, map, mapNumber, true) == false) {
					if( modifyCell( newValue, row, c, row, col, map, mapNumber, false) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
		}
		for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( row, c) == false) {
					if( modifyCell( newValue, row, col, row, c, map, mapNumber, true) == false) {
					//if( modifyCell( newValue, row, col, row, c, map, mapNumber, false) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
		}
		for( int r = row + 1; r <= filter.getMaxRow( row); r++) {
			for( int c = col; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, r, c, row, col, map, mapNumber, false) == false) {
					//if( modifyCell( newValue, row, col, r, c, map, mapNumber, false) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
			}
			for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, r, c, row, col, map, mapNumber, false) == false) {
					//if( modifyCell( newValue, row, col, r, c, map, mapNumber, false) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
			}
		}
	}

	/* in alpha testing, returns true if within distance of analysis, false otherwise */
	private boolean modifyCell( double newValue, int iRow, int iCol, 
			int jRow, int jCol, GISLattice map, int mapNumber, boolean isI) {
		double distance = map.distance( iRow, iCol, jRow, jCol);
		if( distance > maxDist)
			return false;
		if( isI == true) {
			double jValue = map.getCellValue( jRow, jCol);
			double oldValue = map.getCellValue( iRow, iCol);
			double oldLessJSquared = (oldValue - jValue) * (oldValue - jValue);
			double newLessJSquared = (newValue - jValue) * (newValue - jValue);
			int lag = getLag( distance);
			covar[ lag] -= oldLessJSquared;
			covar[ lag] += newLessJSquared;
			if( numMaps > 0) {
				mapSVs[ mapNumber].covar[ lag] -= oldLessJSquared;
				mapSVs[ mapNumber].covar[ lag] += newLessJSquared;
			}
		} else {
			double iValue = map.getCellValue( iRow, iCol);
			double oldValue = map.getCellValue( jRow, jCol);
			double oldLessISquared = (oldValue - iValue) * (oldValue - iValue);
			double newLessISquared = (newValue - iValue) * (newValue - iValue);
			int lag = getLag( distance);
			covar[ lag] -= oldLessISquared;
			covar[ lag] += newLessISquared;
			if( numMaps > 0) {
				mapSVs[ mapNumber].covar[ lag] -= oldLessISquared;
				mapSVs[ mapNumber].covar[ lag] += newLessISquared;
			}
		}
		return true;
	}

    public double spread(SpatialStatistic goalSpatialStatistic) {
        return spread((SemiVariogram)goalSpatialStatistic);
    }

    /** Returns spread between two SemiVariograms. */
	public double spread(SemiVariogram goal) {
		int lag;
		double dist;
		double sumLSA = 0.0;
		for( lag = 0, dist = 0.0f; lag < numLags; lag++, dist += lagDist) {
			if( dist > maxDist) {
				lag = numLags;
			} else {
				double distCalc = dist + lagDist / (double) 2.0;
				double varE = this.getInertiaValue( lag);
				double varA = goal.getInertiaValue( lag);
				if( varA < varE) {
					sumLSA += (uWeight * Math.pow( (varE - varA), 2.0) *
						(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
				} else {
					sumLSA += (oWeight * Math.pow( (varA - varE), 2.0) *
						(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
				}
				if( this.numMaps > 0) {
					for( int m = 0; m < this.numMaps; m++) {
						if( goal.numMaps > 0) {
							varA = goal.mapSVs[ m].getInertiaValue( lag);
						}
						varE = this.mapSVs[ m].getInertiaValue( lag);
						if( varA < varE) {
							sumLSA += iWeight * (uWeight * Math.pow( (varE - varA), 2.0) *
								(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
						} else {
							sumLSA += iWeight * (oWeight * Math.pow( (varA - varE), 2.0) *
								(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
						}
					}
				}
			}
		}
		return( sumLSA);
	}

	/** in alpha testing */
	private void setValue( int lag, double value) {
		calculated = true;
		covar[ lag] = value;
		countArray[ lag] = 1;
	}

	/**
	 * This method functionally identical to print(), except first arg to 
	 * printBuffered is PrintWriter.
	 * in alpha testing.
	 * @throws IOException 
	 */
	public void print(PrintStream out) {
		gisIO.printBuffered( out, "Lag          Moment of Inertia");
		out.println( "");
		for( int i = 1; i <= numLags; i++) {
			gisIO.printBuffered( out, 
				gisIO.getIntegerForm().format( (i * lagDist)));
			out.print( " ");
			gisIO.printBuffered( out, 
				gisIO.getDecimalForm().format( getInertiaValue( i - 1)));
			out.println( "");
		}
	}


	/** in alpha testing 
	 * @throws IOException */
	public SemiVariogram( String fName) throws IOException {
		gisIO = new GISio( 12);
		calculated = true;
		String longFN = fName+".var";
		FileReader fr = gisIO.openFile( longFN);
		BufferedReader br = new BufferedReader( fr);
		String ss = "";
		StringBuffer s = new StringBuffer( 
			gisIO.readLineNoNull( br, longFN)); // reads first line
		s = new StringBuffer( 
			gisIO.readLineNoNull( br, longFN)); // reads second line
		ss = GISio.thisNumber( s);
		ss = GISio.dropCommas( ss);
		lagDist = maxDist = new Double( ss).doubleValue();
		SemiVariogram oldDG = new SemiVariogram( lagDist, maxDist);
		this.initVars( lagDist, maxDist);
		int numLags = 0;
		while( s != null && s.length() > 0) {
			ss = GISio.thisNumber( s);
			ss = GISio.dropCommas( ss);
			maxDist = new Double( ss).doubleValue();
			oldDG = new SemiVariogram( lagDist, maxDist);
			oldDG.copy( this);
			ss = GISio.nextNumber( s);
			ss = GISio.nextNumber( s);
			ss = GISio.dropCommas( ss);
			double firstV = new Double( ss).doubleValue();
			oldDG.setValue( numLags, firstV);
			this.initVars( lagDist, maxDist);
			this.copy( oldDG);
			numLags++;

			String tmp = br.readLine();
			if( tmp != null)
				s = new StringBuffer( tmp);
			else s = null;
		}
		br.close();
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

	/** in alpha testing */
	public void setUnderWeight( double underWeight) {
		uWeight = underWeight;
	}

	/** in alpha testing */
	public double getUnderWeight() {
		return uWeight;
	}

	/** in alpha testing */
	public void setOverWeight( double overWeight) {
		oWeight = overWeight;
	}

	/** in alpha testing */
	public double getOverWeight() {
		return oWeight;
	}

	/** in alpha testing */
	public void setOneMapWeight( double oneMapWeight) {
		iWeight = oneMapWeight;
	}

	/** in alpha testing */
	public double getOneMapWeight() {
		return iWeight;
	}

	/** in alpha testing */
	public void setDecayExponent( double decayExponent) {
		decayExp = decayExponent;
	}

	/** in alpha testing */
	public double getDecayExponent() {
		return decayExp;
	}

	/** not implemented yet */
	public void graph( int xLocation, int yLocation, int xSize, int ySize) {
		//gisIO.printBuffered( "Lag          Moment of Inertia");
		double[] x = new double[ numLags];
		double[][] y = new double[ numLags][ 3];
		for( int i = 0; i < numLags; i++) {
			x[ i] = (i + 1) * lagDist;
			y[ i][ 0] = getInertiaValue( i);
			y[ i][ 1] = y[ i][ 0] * 1.1;
			y[ i][ 2] = y[ i][ 0] * 0.9;
		}
		Color[] color = new Color[ 3];
		color[ 0] = new Color( 255, 0, 0);
		color[ 1] = new Color( 200, 0, 0);
		color[ 2] = new Color( 200, 0, 0);
		GraphLine2D gl2d = new GraphLine2D( "SemiVariogram of x", x, y, color, xSize, ySize);
		gl2d.setLocation( xLocation, yLocation);
		gl2d.setVisible( true);
	}

	public static void main( String[] args) throws IOException {
		GISLattice m = GISLattice.loadEsriAscii("newElevation");
		SemiVariogram s = new SemiVariogram( 30.1, 900.0, m);
		s.print(System.out);
		s.graph( 50, 50, 400, 200);
	}

    public boolean isMultiMap() {
        return mapSVs != null && mapSVs.length > 1;
    }

    public void print(PrintStream printStream, SpatialStatistic goal) throws IOException {
        throw new RuntimeException("Not implemented.");
    }

    public void printOneMapMeasure(int mapNumber, PrintStream printStream) {
        throw new RuntimeException(getClass().getName()+" doesn't track per-map statistics.");
    }
}
