package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;
import java.io.PrintStream;

import mil.army.usace.ehlschlaeger.rgik.statistics.LatticeSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;



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
public class Correlogram extends RGIS implements LatticeSpatialStatistic {
    private int     numLags, countArray[];
    private double  lagDist, maxDist;
    private double  oWeight, uWeight, decayExp;
    private double  mean[], covar[], sdi[];
    private GISio   gisIO;
    private Filter  filter;
    private boolean calculated;
    private boolean multiMap;

	public Correlogram( double lagDistance, double maximumDistance, GISLattice map) {
		// true for multiMeasure, false for oneMapMeasure
		initVars( lagDistance, maximumDistance);
		filter = new Filter( map, maximumDistance);
		addMean( map);
		multiMap = false;
		calcMean();
		calcCov( map);
	}

	public Correlogram( double lagDistance, double maximumDistance, GISLattice maps[]) {
		initVars( lagDistance, maximumDistance);
		initialize( maps);
	}

	/**
	 * initialize() is used by SDUM to initialize correlogram for all maps.
	 * in alphs testing.
	 */
	public void initialize( GISData[] dataMaps) {
		GISLattice[] maps = (GISLattice[]) dataMaps;
		filter = new Filter( maps[0], maxDist);
		countArray = new int[ numLags];
		mean = new double[ numLags];
		covar = new double[ numLags];
		sdi = new double[ numLags];
		for( int lag = 0; lag < numLags; lag++) {
			countArray[ lag] = 0;
			mean[ lag] = covar[ lag] = sdi[ lag] = 0.0;
		}
		calculated = false;
		for( int i = 0; i < maps.length; i++) {
			addMean( maps[ i]);
		}
		multiMap = true;
		calcMean();
		for( int i = 0; i < maps.length; i++) {
			calcCov( maps[ i]);
		}
	}

	private void addMean( GISLattice map) {
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
									add( doCell, map.getCellValue( r, c), distance);
								}
							}
						}
						if( r != doR) {
							for( int c = doC; c <= endC; c++) {
								if( (distance = map.distance( doR, doC, r, c)) > maxDist) {
									c = endC + 1;
								} else {
									if( map.isNoData( r, c) == false) {
										add( doCell, map.getCellValue( r, c), distance);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void add( double iValue, double jValue, double dist) {
		if(calculated) {
		    throw new IllegalStateException("Can't add() on calculated Correlogram");
		}
		int lag = getLag( dist);
		countArray[ lag] += 2;
		mean[ lag] += iValue + jValue;
	}

	private void calcMean() {
		for( int i = 0; i < numLags; i++) {
			if( countArray[ i] > 0) {
				mean[ i] = mean[ i] / countArray[ i];
			}
		}
	}

	/* public double getWeightModifier() {
		double newWM = 0.0;
		double dist = 0.0;
		int lag = 0;
		for( lag = 0, dist = 0.0; lag < numLags; lag++, dist += lagDist) {
			double distCalc = dist + lagDist / (double) 2.0;
			newWM += Math.max( uWeight, oWeight) / Math.pow( (double) distCalc, (double) decayExp);
		}
		return( 1.0 / newWM);
	}
	*/

	public double getLagDistance() {
		return lagDist;
	}

	public double getMaximumDistance() {
		return maxDist;
	}

	private void calcCov( GISLattice map) {
		double doCell = 0;
		double distance = (double) 0.0;
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
									addCov( doCell, map.getCellValue( r, c), distance);
								}
							}
						}
						if( r != doR) {
							for( int c = doC; c <= endC; c++) {
								if( (distance = map.distance( doR, doC, r, c)) > maxDist) {
									c = endC + 1;
								} else {
									if( map.isNoData( r, c) == false) {
										addCov( doCell, map.getCellValue( r, c), distance);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void addCov( double iValue, double jValue, double dist) {
		int i = getLag( dist);
		covar[ i] += iValue * jValue - mean[ i] * mean[ i];
		sdi[ i] += iValue * iValue - mean[ i] * mean[ i];
		sdi[ i] += jValue * jValue - mean[ i] * mean[ i];
	}

	/**
	 * This constructor should be used when creating a LatticeSDUM.
	 */
	public Correlogram( double lagDistance, double maxDistance) {
		initVars( lagDistance, maxDistance);
	}

	private void initVars( double lagDistance, double maxDistance) {
		gisIO = new GISio( 12);
		numLags = (int) Math.ceil( (double) maxDistance / lagDistance);
		lagDist = lagDistance;
		maxDist = maxDistance;
		oWeight = uWeight = 1.0f;
		decayExp = 0.0f;
		countArray = new int[ numLags];
		mean = new double[ numLags];
		covar = new double[ numLags];
		sdi = new double[ numLags];
		for( int lag = 0; lag < numLags; lag++) {
			countArray[ lag] = 0;
			mean[ lag] = covar[ lag] = sdi[ lag] = 0.0;
		}
		calculated = false;
	}

	public boolean getCalculated() {
		return calculated;
	}

	public void setUnderWeight( double underWeight) {
		uWeight = underWeight;
	}

	public double getUnderWeight() {
		return uWeight;
	}

	public void setOverWeight( double overWeight) {
		oWeight = overWeight;
	}

	public double getOverWeight() {
		return oWeight;
	}

	public void setDecayExponent( double decayExponent) {
		decayExp = decayExponent;
	}

	public double getDecayExponent() {
		return decayExp;
	}

	/**
     *  This method copies the information from one correlogram to a new
     *  correlogram. 
	 */
	public SpatialStatistic createCopy() {
        Correlogram newC = new Correlogram( this.getLagDistance(), 
            this.getMaximumDistance());
        newC.copy( this);
        return newC;
	}

    /**
     * This method copies the information from one correlogram into this one.
     * Both correlograms must have the same meta information. Use newCopy() to
     * construct correlogram copy from scratch.
     */
	public void copy(Correlogram c) {
		calculated = c.getCalculated();
		oWeight = c.oWeight;
		uWeight = c.uWeight;
		decayExp = c.decayExp;
		gisIO = c.gisIO;
		filter = c.filter;
		for( int lag = 0; lag < c.numLags; lag++) {
			countArray[ lag] = c.countArray[ lag];
			mean[ lag] = c.mean[ lag];
			covar[ lag] = c.covar[ lag];
			sdi[ lag] = c.sdi[ lag];
		}
	}

	public int getLag( double distance) {
		return( (int) Math.min( (int) (distance / lagDist), numLags - 1));
	}

	private double CorrelValue( int lag) {
		if(calculated)
			return( covar[ lag]);
		if( countArray[ lag] > 0) {
			double fcovar = covar[ lag] / countArray[ lag];
			double fsdi = (double) Math.sqrt( sdi[ lag] / countArray[ lag]);
			if( fsdi > 0.0f)
				return( fcovar / (fsdi * fsdi));
		}
		return( (double) 0.0);
	}

	public void calculate() {
		if(!calculated) {
			calculated = true;
			for( int lag = 0; lag < numLags; lag++) {
				covar[ lag] = CorrelValue( lag);
			} 
		}
	}

	/**
	 * This method should be used when modifying Correlogram within a LatticeSDUM. Multimodify
	 * must be used because cell swapping would require querying every map cell for every modify.
	 */
	public void modify( GISGrid gridMaps[], int mapA, int mapB, int row, int col) {
		GISLattice[] maps = (GISLattice[]) gridMaps;
		double valueA = maps[ mapA].getCellValue( row, col);
		double valueB = maps[ mapB].getCellValue( row, col);
		modifyMap( maps[ mapA], row, col, valueB);
		modifyMap( maps[ mapB], row, col, valueA);
	}

	private void modifyMap( GISLattice map, int row, int col, double newValue) {
		if(calculated) {
		    throw new IllegalStateException("Can't modify calculated Correlogram");
		}
		if( filter == null) {
			filter = new Filter( map, getMaximumDistance());
		}
		for( int r = filter.getMinRow( row); r < row; r++) {
			for( int c = col; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, row, col, r, c, map, true) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
			}
			for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, row, col, r, c, map, true) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
			}
		}
		for( int c = col + 1; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( row, c) == false) {
					//if( modifyCell( newValue, row, col, row, c, map, true) == false) {
					if( modifyCell( newValue, row, c, row, col, map, false) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
		}
		for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( row, c) == false) {
					if( modifyCell( newValue, row, col, row, c, map, true) == false) {
					//if( modifyCell( newValue, row, col, row, c, map, false) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
		}
		for( int r = row + 1; r <= filter.getMaxRow( row); r++) {
			for( int c = col; c <= filter.getMaxCol( col); c++) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, r, c, row, col, map, false) == false) {
					//if( modifyCell( newValue, row, col, r, c, map, false) == false) {
						c = filter.getMaxCol( col) + 1;
					}
				}
			}
			for( int c = col - 1; c >= filter.getMinCol( col); c--) {
				if( map.isNoData( r, c) == false) {
					if( modifyCell( newValue, r, c, row, col, map, false) == false) {
					//if( modifyCell( newValue, row, col, r, c, map, false) == false) {
						c = filter.getMinCol( col) - 1;
					}
				}
			}
		}
	}

	/* returns true if within distance of analysis, false otherwise */
	private boolean modifyCell( double newValue, int iRow, int iCol, 
			int jRow, int jCol, GISLattice map, boolean isI) {
		double distance = map.distance( iRow, iCol, jRow, jCol);
		if( distance > maxDist)
			return false;
		if( isI == true) {
			double jValue = map.getCellValue( jRow, jCol);
			double oldValue = map.getCellValue( iRow, iCol);
			int lag = getLag( distance);
			covar[ lag] -= oldValue * jValue - mean[ lag] * mean[ lag];
			sdi[ lag] -= oldValue * oldValue - mean[ lag] * mean[ lag];
			sdi[ lag] += newValue * newValue - mean[ lag] * mean[ lag];
			covar[ lag] += newValue * jValue - mean[ lag] * mean[ lag];
		} else {
			double iValue = map.getCellValue( iRow, iCol);
			double oldValue = map.getCellValue( jRow, jCol);
			int lag = getLag( distance);
			covar[ lag] -= oldValue * iValue - mean[ lag] * mean[ lag];
			sdi[ lag] -= oldValue * oldValue - mean[ lag] * mean[ lag];
			sdi[ lag] += newValue * newValue - mean[ lag] * mean[ lag];
			covar[ lag] += newValue * iValue - mean[ lag] * mean[ lag];
		}
		return true;
	}

	/**
	 * Returns spread between two Correlograms.
	 */
    public double spread(SpatialStatistic goalSpatialStatistic) {
        Correlogram aLag = (Correlogram) goalSpatialStatistic;
		int lag;
		double dist;

//		if( spatialStatistic instanceof Correlogram == false) {
//			System.out.println( "Correlogram.spread() must have Correlogram object as parameter");
//			System.exit( -1);
//		}
//		Correlogram aLag = (Correlogram) spatialStatistic;
		double sumLSA = 0.0;
		for( lag = 0, dist = 0.0f; lag < numLags; lag++, dist += lagDist) {
			if( dist > maxDist) {
				lag = numLags;
			} else {
				double distCalc = dist + lagDist / (double) 2.0;
				double varE = this.CorrelValue( lag);
				double varA = aLag.CorrelValue( lag);
				if( varA < varE) {
					sumLSA += (uWeight * Math.pow( (varE - varA), 2.0) *
						(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
				} else {
					sumLSA += (oWeight * Math.pow( (varA - varE), 2.0) *
						(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
				}
			}
		}
		return( sumLSA);
	}

	private void setValue( int lag, double value) {
		calculated = true;
		covar[ lag] = value;
		mean[ lag] = 1.0f;
		sdi[ lag] = 1.0f;
		countArray[ lag] = 1;
	}

	public void print(PrintStream out) {
	    out.println("Lag          Correlation");
		for( int i = 1; i <= numLags; i++) {
		    out.format("%12d %d\n", i*lagDist, CorrelValue(i-1));
		}
	}

    public void print(PrintStream printStream, SpatialStatistic goal) throws IOException {
        throw new RuntimeException("Not implemented.");
    }

	public Correlogram( String fName) throws IOException {
		gisIO = new GISio( 12);
		calculated = true;
		String longFN = fName+".cor";
		MyReader br = new MyReader( longFN);
		String ss = "";
		StringBuffer s = new StringBuffer( br.readLine()); // reads first line
		s = new StringBuffer( br.readLine()); // reads second line
		ss = GISio.thisNumber( s);
		ss = GISio.dropCommas( ss);
		lagDist = maxDist = new Double( ss).doubleValue();
		Correlogram oldDG = new Correlogram( lagDist, maxDist);
		this.initVars( lagDist, maxDist);
		this.multiMap = false;
		int numLags = 0;
		while( s != null && s.length() > 0) {
			ss = GISio.thisNumber( s);
			ss = GISio.dropCommas( ss);
			maxDist = new Double( ss).doubleValue();
			oldDG = new Correlogram( lagDist, maxDist);
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

    public boolean isMultiMap() {
        return multiMap;
    }

    public void printOneMapMeasure(int mapNumber, PrintStream printStream) {
        throw new RuntimeException(getClass().getName()+" doesn't track per-map statistics.");
    }
}
