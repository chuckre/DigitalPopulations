package mil.army.usace.ehlschlaeger.rgik.core;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

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
public class WaterSlope8Measure extends RGIS implements LatticeSpatialStatistic  {
	private int gNumSlope[], numSlope[][], gNumSum, numSum[];
	private double gSlopeSum[], slopeSum[][];
	private double slopeInc, slopeMax;
	private GISio gisIO;
    private static int ROW = 0; private static int COL = 1; 
    private static int DIA = 2; private static int NONE = -1;
	private static int UL = 3; private static int UC = 2; private static int UR = 1; 
	private static int CL = 4; private static int CC = 8; private static int CR = 0;
	private static int LL = 5; private static int LC = 6; private static int LR = 7;
	private static int[] oppSide = { CL, LL, LC, LR, CR, UR, UC, UL, CC };
	private static int[] adjRow = { 0, -1, -1, -1, 0, 1, 1, 1, 0 };
	private static int[] adjCol = { 1, 1, 0, -1, -1, -1, 0, 1, 0 };
	private static int[] adjDiag = { ROW, DIA, COL, DIA, ROW, DIA, COL, DIA };
	private double[] adjDist;
	static private String ssExt = ".wsp";

	/** in alpha testing */
	public WaterSlope8Measure() {
		// multiMeasure :true, oneMapMeasure: true
		slopeInc = .05;
		slopeMax = -.8;
		
		gNumSum = 0;
		gisIO = new GISio( 12);
		numSum = new int[1];
		numSum[0] = 0;
		gNumSlope = new int[ numSlopeCats()];
		numSlope = new int[ 1][ numSlopeCats()];
		gSlopeSum = new double[ numSlopeCats()];
		slopeSum = new double[ 1][ numSlopeCats()];
		for( int j = numSlopeCats() - 1; j >= 0; j--) {
			gNumSlope[ j] = 0;
			numSlope[ 0][ j] = 0;
			gSlopeSum[ j] = 0.0;
			slopeSum[ 0][ j] = 0.0;
		}
		
	}

	/** not implemented yet */
	public void graph( int xLocation, int yLocation, int xSize, int ySize) {
        throw new RuntimeException("Not implemented.");
	}

	/**
	 * initialize() is used by LatticeSDUM to initialize LatticeSpatialStatistic object.
	 * in alpha testing.
	 */
	public void initialize( GISData dataMaps[]) {
	    assert dataMaps != null;
	    
		GISLattice[] maps = (GISLattice[]) dataMaps;
		for( int i = maps.length - 1; i >= 0; i--) {
			if( maps[ i] == null) {
			    throw new NullPointerException("dataMaps["+i+"]");
			}
		}
		adjDist = new double[ 3];
		adjDist[ ROW] = maps[0].distance( 0, 0, 1, 0);
		adjDist[ COL] = maps[0].distance( 0, 0, 0, 1);
		adjDist[ DIA] = maps[0].distance( 0, 0, 1, 1);
		gNumSum = 0;
		gNumSlope = new int[ numSlopeCats()];
		numSlope = new int[ maps.length][ numSlopeCats()];
		gSlopeSum = new double[ numSlopeCats()];
		slopeSum = new double[ maps.length][ numSlopeCats()];
		numSum = new int[ maps.length];
		for( int i = maps.length - 1; i >= 0; i--) {
			numSum[ i] = 0;
			for( int j = numSlopeCats() - 1; j >= 0; j--) {
				numSlope[ i][ j] = 0;
				slopeSum[ i][ j] = 0.0;
			}
		}
		for( int j = numSlopeCats() - 1; j >= 0; j--) {
			gNumSlope[ j] = 0;
			gSlopeSum[ j] = 0.0;
		}
		GDouble eValue = new GDouble();
		for( int i = maps.length - 1; i >= 0; i--) {
			for( int r = maps[i].getNumberRows() - 2; r > 0; r--) {
				for( int c = maps[i].getNumberColumns() - 2; c > 0; c--) {
					if( maps[i].isNoData( r, c) == false) {
						eValue.setValue( maps[ i].getCellValue( r, c));
						incrementSlope( maps, i, r, c, eValue, CC);
					}
				}
			}
		}
	}

	private boolean incrementSlope( GISLattice maps[], int mapNumber, int row, 
			int col, GDouble eValue, int location) {
		boolean isSlope = getSlope( maps[ mapNumber], row, col, eValue, location);
		if( isSlope ) {
			int slopeIndex = getSlopeIndex( eValue.getValue());
			gNumSum++;
			numSum[ mapNumber]++;
			gNumSlope[ slopeIndex]++;
			numSlope[ mapNumber][ slopeIndex]++;
			slopeSum[ mapNumber][ slopeIndex] += eValue.getValue();
			gSlopeSum[ slopeIndex] += eValue.getValue();
			return true;
		}
		return false;
	}

	private boolean decrementSlope( GISLattice maps[], int mapNumber, int row, 
			int col, GDouble eValue, int location) {
	    boolean isSlope = getSlope( maps[ mapNumber], row, col, eValue, location);
		if( isSlope) {
			int slopeIndex = getSlopeIndex( eValue.getValue());
			gNumSum--;
			numSum[ mapNumber]--;
			gNumSlope[ slopeIndex]--;
			numSlope[ mapNumber][ slopeIndex]--;
			slopeSum[ mapNumber][ slopeIndex] -= eValue.getValue();
			gSlopeSum[ slopeIndex] -= eValue.getValue();
			return true;
		}
		return false;
	}

	    
	/* eValue contains the cell value at location at the beginning of the
	 * method, and contains the slope when the method ends
	 */
	private boolean getSlope( GISLattice map, int row, int col, 
			GDouble eValue, int location) {
		if( row == 0 || col == 0 || row == map.getNumberRows() - 1 ||
				col == map.getNumberColumns() - 1)
			return false;
		for( int i = 0; i < 9; i++) {
		    if( map.isNoData( row + adjRow[ i], col + adjCol[ i]) == true) {
		        return false;
		    }
		}
		double[] values = new double[ 9];
		for( int i = 0; i < 9; i++) {
		    if( location == i) {
		        values[ i] = eValue.getValue();
		    } else {
		        values[ i] = map.getCellValue( row + adjRow[ i], col + adjCol[ i]);
		    }
		}
		double slope = 999999.9;
		double checkSlope = 999999.9;
		for( int i = 0; i < 8; i++) {
		    checkSlope = (values[ i] - values[ 8]) / adjDist[ adjDiag[ i]];
		    if( checkSlope < slope) {
		        slope = checkSlope;
		    }
		}
		eValue.setValue( slope);			
		return true;
	}

	/**
	 * in alpha testing.
	 * This method will create a copy of the spatialStatistic and return it.
	 */
	public SpatialStatistic createCopy() {
		WaterSlope8Measure pc = new WaterSlope8Measure();
		pc.slopeInc = slopeInc;
		pc.slopeMax = slopeMax;
        pc.adjDist = adjDist;
		pc.gNumSum = gNumSum;
		pc.gisIO = gisIO;
		pc.gNumSlope = new int[ gNumSlope.length];
		pc.gSlopeSum = new double[ gSlopeSum.length];
		for( int i = gNumSlope.length - 1; i >= 0; i--) {
			pc.gNumSlope[ i] = gNumSlope[ i];
			pc.gSlopeSum[ i] = gSlopeSum[ i];
		}
		pc.numSum = new int[ numSum.length];
		pc.slopeSum = new double[ slopeSum.length][ slopeSum[0].length];
		pc.numSlope = new int[ numSlope.length][ numSlope[0].length];
		for( int i = slopeSum.length - 1; i >= 0; i--) {
			pc.numSum[ i] = numSum[ i];
			for( int j = slopeSum[0].length - 1; j >= 0; j--) {
				pc.slopeSum[ i][ j] = slopeSum[ i][ j];
				pc.numSlope[ i][ j] = numSlope[ i][ j];
			}
		}
		return( (SpatialStatistic) pc);
	}

	private int numSlopeCats() {
		return( 1 + (int) Math.ceil( -1.0 * slopeMax / slopeInc));
	}

	private int getSlopeIndex( double slope) {
		if( slope >= 0.0) {
			return 0;
		}
		return( (int) ( Math.min( numSlopeCats() - 1, 
			Math.ceil( (-1.0 * slope) / slopeInc))));
	}

	/** Modifies spatial statistic based on change of one value in map.
	 */
	public void modify( GISGrid gridMaps[], int mapA, int mapB, int row, int col) {
		GISLattice[] maps = (GISLattice[]) gridMaps;
		double valueA = maps[ mapA].getCellValue( row, col);
		double valueB = maps[ mapB].getCellValue( row, col);
		oneMapModify( maps, mapA, row, col, valueB);
		oneMapModify( maps, mapB, row, col, valueA);
	}

	/** Modifies spatial statistic based on change of one value in map.
	 */
	private void oneMapModify( GISLattice[] maps, int mapNumber, 
			int row, int col, double newValue) {
		GDouble eValue = new GDouble();
		for( int i = 0; i < 9; i++) {
		    int doRow = row + adjRow[ i];
		    int doCol = col + adjCol[ i];
		    if( doRow > 0 && doCol > 0 && doRow < maps[0].getNumberRows() -1 &&
		            doCol < maps[0].getNumberColumns() -1) {
			    decrementSlope( maps, mapNumber, doRow, doCol, eValue, NONE);
		        eValue.setValue( newValue);
			    incrementSlope( maps, mapNumber, doRow, doCol, eValue, oppSide[i]);
		    }
		}
	}

    public double spread(SpatialStatistic goalSpatialStatistic) {
        return spread((WaterSlope8Measure)goalSpatialStatistic);
    }

	/** Returns spread between two WaterSlope8Measure objects. 0.0 is a perfect fit.
	 */
	public double spread(WaterSlope8Measure w) {
		double dif = 0.0;
		int iThis = gNumSlope.length - 1;
		int iW = w.gNumSlope.length - 1;
		if( iThis < 0 || iW < 0) {
		    throw new DataException("Empty WaterSlope8Measure iW: " +
		                            iW + ", iThis: " + iThis);
		}
		while( gNumSlope[ iThis] == 0) {
			iThis--;
			if( iThis < 0) {
			    throw new DataException( "Empty iThis in WaterSlope8Measure");
			}
		}
		while( w.gNumSlope[ iW] == 0) {
			iW--;
			if( iW < 0) {
			    throw new DataException( "Empty iW in WaterSlope8Measure");
			}
		}
		double thisLeft = 1.0 * gNumSlope[ iThis] / gNumSum;
		double wLeft = 1.0 * w.gNumSlope[ iW] / w.gNumSum;
		double meanThis = gSlopeSum[ iThis] / gNumSlope[ iThis];
		double meanW = w.gSlopeSum[ iW] / w.gNumSlope[ iW];
		while( iThis >= 0) {
			if( wLeft > 0) {
				if( wLeft > thisLeft) {
					dif += thisLeft * (meanThis - meanW) * (meanThis - meanW); 
					wLeft -= thisLeft;
					iThis--;
					if( iThis < 0) {
						return Math.sqrt( dif); 
					}
					while( gNumSlope[ iThis] == 0) {
						iThis--;
						if( iThis < 0) {
							return Math.sqrt( dif); 
						}
					}
					thisLeft = 1.0 * gNumSlope[ iThis] / gNumSum;
					meanThis = gSlopeSum[ iThis] / gNumSlope[ iThis];
				} else if( thisLeft > wLeft) {
					dif += wLeft * (meanThis - meanW) * (meanThis - meanW); 
					thisLeft -= wLeft;
					iW--;
					if( iW < 0) {
						return Math.sqrt( dif); 
					}
					while( w.gNumSlope[ iW] == 0) {
						iW--;
						if( iW < 0) {
							return Math.sqrt( dif); 
						}
					}
					wLeft = 1.0 * w.gNumSlope[ iW] / w.gNumSum;
					meanW = w.gSlopeSum[ iW] / w.gNumSlope[ iW];
				} else { // thisLeft == wLeft
					dif += wLeft * (meanThis - meanW) * (meanThis - meanW); 
					iThis--;
					if( iThis < 0) {
						return Math.sqrt( dif); 
					}
					while( gNumSlope[ iThis] == 0) {
						iThis--;
						if( iThis < 0) {
							return Math.sqrt( dif); 
						}
					}
					thisLeft = 1.0 * gNumSlope[ iThis] / gNumSum;
					meanThis = gSlopeSum[ iThis] / gNumSlope[ iThis];
					iW--;
					if( iW < 0) {
						return Math.sqrt( dif); 
					}
					while( w.gNumSlope[ iW] == 0) {
						iW--;
						if( iW < 0) {
							return Math.sqrt( dif); 
						}
					}
					wLeft = 1.0 * w.gNumSlope[ iW] / w.gNumSum;
					meanW = w.gSlopeSum[ iW] / w.gNumSlope[ iW];
				}
			}
		}
		dif = Math.sqrt( dif);
		return( dif);
	}

	/** This method functionally identical to print(), except output printed to (fName + ext).
	 * @throws IOException 
	 */
	public void print(PrintStream out) {
		gisIO.printBuffered( out, "WaterSlope8   Proportion");
		out.println( "");
		for( int i = 0; i < gNumSlope.length; i++) {
			if( gNumSlope[ i] > 0) {
				if( gSlopeSum[ i] / gNumSlope[ i] >= 0.0)
					out.print( " ");
				gisIO.printBuffered( out, 
					gisIO.getDecimalForm().format( gSlopeSum[ i] / gNumSlope[ i]));
				out.print( " ");
				gisIO.printBuffered( out, 
					gisIO.getDecimalForm().format( 1.0 * gNumSlope[ i] / gNumSum));
				out.println( "");
			}
		}
	}

	/** This method functionally identical to printMultiMeasure(), except output printed to (fName + ext).
	 * in alpha testing.
	 * @throws IOException 
	 */
	public void printOneMapMeasure( int mapNumber, String fName) throws IOException {
	    PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(fName+ssExt)));
	    printOneMapMeasure(mapNumber, out);
		out.close();
	}
	
	public void printOneMapMeasure(int mapNumber, PrintStream out) {
        if( numSum.length == 1) {
            gisIO.printBuffered( out, "WaterSlope8   Proportion");
            out.println( "");
            for( int i = 0; i < numSlope[0].length; i++) {
                if( numSlope[0][ i] > 0) {
                    if( slopeSum[0][ i] / numSlope[0][ i] >= 0.0)
                        out.print( " ");
                    gisIO.printBuffered( out, 
                        gisIO.getDecimalForm().format( 
                            slopeSum[0][ i] / numSlope[0][ i]));
                    out.print( " ");
                    gisIO.printBuffered( out, 
                        gisIO.getDecimalForm().format( 
                            1.0 * numSlope[0][ i] / numSum[0]));
                    out.println( "");
                }
            }
        } else {
            gisIO.printBuffered( out, "WaterSlope8   Proportion");
            out.println( "");
            for( int i = 0; i < numSlope[mapNumber].length; i++) {
                if( numSlope[mapNumber][ i] > 0) {
                    if( slopeSum[mapNumber][ i] / numSlope[mapNumber][ i] >= 0.0)
                        out.print( " ");
                    gisIO.printBuffered( out, 
                        gisIO.getDecimalForm().format( 
                            slopeSum[mapNumber][ i] / numSlope[mapNumber][ i]));
                    out.print( " ");
                    gisIO.printBuffered( out, 
                        gisIO.getDecimalForm().format( 
                            1.0 * numSlope[mapNumber][ i] / numSum[mapNumber]));
                    out.println( "");
                }
            }
        }
	}

	public double getSlopeIncrement() {
		return slopeInc;
	}

	public double getSlopeMaximum() {
		return slopeMax;
	}

	public void setSlopeIncrement( double increment) {
		slopeInc = increment;
	}

	public void setSlopeMaximum( double maximum) {
		slopeInc = maximum;
	}

    public void print(PrintStream printStream, SpatialStatistic goal) throws IOException {
        throw new RuntimeException("Not implemented.");
    }

    public boolean isMultiMap() {
        return numSum != null && numSum.length > 1;
    }
}

class GDouble {
	private boolean isV;
	private double v;

	public GDouble() {
		isV = false;
		v = 0.0;
	}

	public void setValue( double value) {
		v = value;
		isV = true;
	}

	public void setNoValue() {
		isV = false;
	}

	public boolean isValue() {
		return isV;
	}

	public double getValue() {
		return v;
	}
}
	
