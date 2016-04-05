package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISio;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;

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
public class DensoStat extends RGIS implements RGISFunction  {
	private int	numLags, numCats, minCat, countArray[][];
	private double lagDist, maxDist, oWeight, uWeight, decayExp, matchArray[][];
	private boolean calculated;
	private GISio gisIO;

	public int getNumberLags() {
		return numLags;
	}

	public double getLagDistance() {
		return lagDist;
	}

	public double getMaximumDistance() {
		return maxDist;
	}

	public static void main( String argv[]) throws IOException {
		DensoStat dA, dB;
		GISClass aGrid, bGrid;
		int minCat, maxCat;

		if( argv.length < 1 || argv.length > 7 || argv.length == 2 ||
				argv.length == 4 || argv.length == 6) {
			System.out.println( "Application requires one to seven arguments.");
			System.out.println( "First form, prints densogram:");
			System.out.println( "      java DensoStat densogram01");
			System.out.println( "Second form, calculates spread of two densograms:");
			System.out.println( "      java DensoStat densogram01 densogram02 underWeight overWeight decayExponent");
			System.out.println( "Third form, calculates densogram of ESRI ASCII map:");
			System.out.println( "      java DensoStat map01 lagDistance maxDistance");
			System.out.println( "Fourth form, calculates spread of two ESRI ASCII maps:");
			System.out.println( "      java DensoStat map01 map02 lagDistance underWeight overWeight decayExponent maxDistance");
			System.out.println( "");
			System.out.println( "densogram01: DensoStat, if only densogram, prints densogram");
			System.out.println( "densogram02: DensoStat, if listed, application compares two maps");
			System.out.println( "lagDistance: distance of lag");
			System.out.println( "maxDistance: maximum distance to calculate");
			System.out.println( "decayExponent: determines how important lags are based on distance");
			System.out.println( "map01: Grid map, if only map, application prints densogram");
			System.out.println( "map02: Grid map, if listed, application compares two maps");
			System.exit(-1);
		} 
		switch( argv.length) {
			case 1:
				dA = new DensoStat( argv[0]);
				dA.print( );
				break;
			case 5:
				dA = new DensoStat( argv[0]);
				dB = new DensoStat( argv[1]);
				dA.setUnderWeight( new Double( argv[2].trim()).doubleValue());
				dA.setOverWeight( new Double( argv[3].trim()).doubleValue());
				dA.setDecayExponent( new Double( argv[4].trim()).doubleValue());
				System.out.println( dA.spread( dB));
				break;
			case 3:
				aGrid = GISClass.loadEsriAscii(argv[0]);
				minCat = aGrid.getMinimumValue();
				maxCat = aGrid.getMaximumValue();
				dA = new DensoStat( minCat, maxCat, new Double( argv[1].trim()).doubleValue(),
					new Double( argv[2].trim()).doubleValue(), aGrid);
				dA.print( );
				dA.print( argv[0]);
				break;
			case 7:
				aGrid = GISClass.loadEsriAscii(argv[0]);
				minCat = aGrid.getMinimumValue();
				maxCat = aGrid.getMaximumValue();
				dA = new DensoStat( minCat, maxCat, new Double( argv[2].trim()).doubleValue(),
					new Double( argv[6].trim()).doubleValue(), aGrid);
				dA.print( argv[0]);				
				dA.setUnderWeight( new Double( argv[3].trim()).doubleValue());
				dA.setOverWeight( new Double( argv[4].trim()).doubleValue());
				dA.setDecayExponent( new Double( argv[5].trim()).doubleValue());
				bGrid = GISClass.loadEsriAscii(argv[ 1]);
				minCat = bGrid.getMinimumValue();
				maxCat = bGrid.getMaximumValue();
				dB = new DensoStat( minCat, maxCat, new Double( argv[2].trim()).doubleValue(),
					new Double( argv[6].trim()).doubleValue(), bGrid);
				dB.print( argv[ 1]);
				System.out.println( dA.spread( dB));
				break;
		}
	}

	/** in alpha testing */
	public DensoStat( int minCat, int maxCat, double lagDistance, double maxDistance, GISClass map) {
		super();
		int endR = map.getNumberRows() - 1;
		int endC = map.getNumberColumns() - 1;
		int doCell = 0;
		double distance = (double) 0.0;
		calculated = false;
		initVars( minCat, maxCat, lagDistance, maxDistance);
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

	/** in alpha testing */
	public DensoStat( int minimumCat, int maximumCat, double lagDistance, double maxDistance) {
		super();
		initVars( minimumCat, maximumCat, lagDistance, maxDistance);
		calculated = false;
	}

	/** in alpha testing */
	private void initVars( int minimumCat, int maximumCat, double lagDistance, double maxDistance) {
		gisIO = new GISio( 12);
		numLags = (int) Math.ceil( (double) maxDistance / lagDistance);
		numCats = maximumCat - minimumCat + 1;
		minCat = minimumCat;
		lagDist = lagDistance;
		maxDist = maxDistance;
		oWeight = uWeight = (double) 1.0;
		decayExp = (double) 1.0;
		countArray = new int[ numCats][ numLags];
		matchArray = new double[ numCats][ numLags];
		for( int cat = 0; cat < numCats; cat++) {
			for( int lag = 0; lag < numLags; lag++) {
				countArray[ cat][ lag] = 0;
				matchArray[ cat][ lag] = (double) 0.0;
			}
		}
	}

	/** in alpha testing 
	 * @throws IOException */
	public DensoStat( String fName) throws IOException {
		super();
		gisIO = new GISio( 12);
		calculated = true;
		String longFN = fileName( fName);
		FileReader fr = gisIO.openFile( longFN);
		BufferedReader br = new BufferedReader( fr);
		String ss = "";
		StringBuffer s = new StringBuffer( gisIO.readLineNoNull( br, fileName( fName)));
		ss = GISio.dropCommas( GISio.nextNumber( s));
		int numLags = 0;
		lagDist = new Double( ss).doubleValue();
		while( s.length() > 0) {
			numLags++;
			maxDist = new Double( ss.trim()).doubleValue();
			ss = GISio.nextNumber( s);
			ss = GISio.dropCommas( ss);
		} 
		s = new StringBuffer( gisIO.readLineNoNull( br, fileName( fName)));
		// System.out.println(s);
		ss = GISio.dropCommas( GISio.thisNumber( s));
		// System.out.println(ss);
		minCat = new Integer( ss).intValue();
		// System.out.println(minCat);
		DensoStat oldDG = new DensoStat( minCat, minCat, lagDist, maxDist);
		while( s != null) {
			ss = GISio.dropCommas( GISio.thisNumber( s));
			int catValue = new Integer( ss).intValue();
			this.initVars( minCat, catValue, lagDist, maxDist);
			numCats = catValue - minCat + 1;
			this.copy( oldDG);
			for( int lag = 0; lag < numLags; lag++) {
				ss = GISio.dropCommas( GISio.nextNumber( s));
				double v = new Double( ss).doubleValue();
				setMatchValue( catValue, lag, v);
			}
			oldDG = new DensoStat( minCat, catValue, lagDist, maxDist);
			oldDG.copy( this);

			String tmp = br.readLine();
			if( tmp != null)
				s = new StringBuffer( tmp);
			else s = null;
		}
		br.close();
	}

	/** in alpha testing */
	private void setMatchValue( int catValue, int lag, double value) {
		matchArray[ catValue - minCat][ lag] = value;
		countArray[ catValue - minCat][ lag] = 1;
	}

	/** in alpha testing */
	public void setUnderWeight( double uW) {
		uWeight = uW;
	}

	/** in alpha testing */
	public void setOverWeight( double oW) {
		oWeight = oW;
	}

	/** in alpha testing */
	public void setDecayExponent( double dE) {
		decayExp = dE;
	}

	/** in alpha testing */
	public void copy( DensoStat iLag) {
		calculated = iLag.calculated;
		for( int cat = 0; cat < iLag.numCats; cat++) {
			for( int lag = 0; lag < iLag.numLags; lag++) {
				matchArray[ cat][ lag] = iLag.matchArray[ cat][ lag];
				countArray[ cat][ lag] = iLag.countArray[ cat][ lag];
			} 
		}
	}

	/** in alpha testing */
	public int lag( double distance) {
		return( (int) Math.min( (int) (distance / lagDist), numLags - 1));
	}

	/** in alpha testing */
	public void add( int iValue, int jValue, double dist) {
		if(calculated) {
	        throw new IllegalStateException("Can't add to calculated densogram");
		}
		int lag = lag( dist);
		countArray[ iValue - minCat][ lag] ++;
		countArray[ jValue - minCat][ lag] ++;
		if( iValue == jValue) {
			matchArray[ iValue - minCat][ lag] += 2.0;
		}
	}

	/** in alpha testing */
	private double matchValue( int cat, int lag) {
		if(calculated)
			return( matchArray[ cat][ lag]);
		if( countArray[ cat][ lag] > 0)
			return( matchArray[ cat][ lag] / countArray[ cat][ lag]);
		return( (double) 0.0);
	}

	/** in alpha testing */
	public void calc() {
		if(!calculated) {
			calculated = true;
			for( int cat = 0; cat < numCats; cat++) {
				for( int lag = 0; lag < numLags; lag++) {
					matchArray[ cat][ lag] = matchValue( cat, lag);
				}
			} 
		}
	}

	/** in alpha testing */
	public void modify( int catOutCell, int catInCell, int compareCat, int lag) {
		//int lag = Math.min( (int) (dist / lagDist), numLags - 1);
		if(calculated) {
		    throw new IllegalStateException("Can't modify calculated densogram");
		}
		if( catOutCell == compareCat) {
			matchArray[ catOutCell - minCat][ lag] -= (double) 2.0;
			countArray[ catOutCell - minCat][ lag] -= (double) 1.0;
			countArray[ catInCell - minCat][ lag] += (double) 1.0;
		} else if( catInCell == compareCat) {
			matchArray[ catInCell - minCat][ lag] += (double) 2.0;
			countArray[ catOutCell - minCat][ lag] -= (double) 1.0;
			countArray[ catInCell - minCat][ lag] += (double) 1.0;
		} else {
			countArray[ catOutCell - minCat][ lag] -= (double) 1.0;
			countArray[ catInCell - minCat][ lag] += (double) 1.0;
		}
	}

	/** Returns positive value if aLag is closer to this than bLag, negative if b closer to a.
	 * in alpha testing */
	public double closer( DensoStat aLag, DensoStat bLag) {
		return( spread( bLag) - spread( aLag));
	}

	/** Returns spread between two densograms.
	 * in alpha testing */
	public double spread( DensoStat aLag) {
		int lag;
		double dist;

		double sumLSA = 0.0;
		for( lag = 0, dist = lagDist; lag < numLags; lag++, dist += lagDist) {
			if( dist > maxDist) {
				lag = numLags;
			} else {
				double distCalc = dist + lagDist / (double) 2.0;
				for( int cat = 0; cat < numCats; cat++) {
					double varE = 1000.0 * this.matchValue( cat, lag);
					double varA = 1000.0 * aLag.matchValue( cat, lag);
					if( varA < varE) {
						sumLSA += (uWeight * Math.pow( (varE - varA), 2.0) *
							(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
					} else {
						sumLSA += (oWeight * Math.pow( (varA - varE), 2.0) *
							(1.0 / Math.pow( (double) distCalc, (double) decayExp)));
					}
				}
			}
		}
		return( sumLSA);
	}

	/** in alpha testing */
	public void print() {
		gisIO.printBuffered( "Cat/Lag");
		for( int i = 1; i <= numLags; i++) {
			System.out.print( " ");
			gisIO.printBuffered( gisIO.getIntegerForm().format( (i * lagDist)));
		}
		System.out.println( "");
		for( int cat = 0; cat < numCats; cat++) {
			int isCat = 0;
			for( int lag = 0; lag < numLags; lag++) {
				if( countArray[ cat][ lag] > 0) {
					isCat = 1;
					break;
				}
			}
			if( isCat > 0) {
				gisIO.printBuffered( gisIO.getIntegerForm().format( (cat + minCat)));
				for( int lag = 0; lag < numLags; lag++) {
					System.out.print( " ");
					gisIO.printBuffered( gisIO.getDecimalForm().format( matchValue( cat, lag)));
				}
				System.out.println();
/*test stuff 
				for( int lag = 0; lag < numLags; lag++) {
					System.out.print( " ");
					System.out.print( matchArray[ cat][ lag]);
				}
				System.out.println();
				for( int lag = 0; lag < numLags; lag++) {
					System.out.print( " ");
					System.out.print( countArray[ cat][ lag]);
				}
				System.out.println();
 end test stuff */
			}
		}
	}

	/** This method functionally identical to print(), except first arg to printBuffered is PrintWriter.
	 * in alpha testing 
	 * @throws IOException */
	public void print( String fName) throws IOException {
		PrintWriter out = null;

		out = new PrintWriter(
					new BufferedWriter( new FileWriter( fileName( fName))));
		gisIO.printBuffered( out, "Cat/Lag");
		for( int i = 1; i <= numLags; i++) {
			out.print( " ");
			gisIO.printBuffered( out, gisIO.getIntegerForm().format( (i * lagDist)));
		}
		out.println( "");
		for( int cat = 0; cat < numCats; cat++) {
			int isCat = 0;
			for( int lag = 0; lag < numLags; lag++) {
				if( countArray[ cat][ lag] > 0) {
					isCat = 1;
					break;
				}
			}
			if( isCat > 0) {
				gisIO.printBuffered( out, gisIO.getIntegerForm().format( (cat + minCat)));
				for( int lag = 0; lag < numLags; lag++) {
					out.print( " ");
					gisIO.printBuffered( out, gisIO.getDecimalForm().format( matchValue( cat, lag)));
				}
				out.println();
			}
		}
		out.close();
		//System.out.println("end DensoStat.print");
	}

	/** in alpha testing */
	private String fileName( String fN) {
		return( fN + ".den");
	}

}