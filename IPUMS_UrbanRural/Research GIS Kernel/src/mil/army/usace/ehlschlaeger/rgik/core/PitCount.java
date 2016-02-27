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
public class PitCount extends RGIS implements LatticeSpatialStatistic  {
	private int gNumPits, numPits[];
	private double gPitWeight, pitWeights[];
	private GISLattice weight;
	private GISio gisIO;
	private static int UL = 1;
	private static int UC = 2;
	private static int UR = 3;
	private static int CL = 4;
	private static int CC = 5;
	private static int CR = 6;
	private static int LL = 7;
	private static int LC = 8;
	private static int LR = 9;

	/** in alpha testing */
	public PitCount() {
		// true for multiMeasure, true for oneMapMeasure
		gisIO = new GISio( 12);
		gNumPits = 0;
		gPitWeight = 0.0;
		numPits = new int[ 1];
		pitWeights = new double[ 1];
		numPits[ 0] = 0;
		pitWeights[ 0] = 0.0;
		weight = null;
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
		GISLattice[] maps = (GISLattice[]) dataMaps;
		for( int i = maps.length - 1; i >= 0; i--) {
			if( maps[ i] == null) {
			    throw new NullPointerException("maps["+i+"]");
			}
		}
		if( weight == null) {
			weight = new GISLattice( maps[ 0]);
			weight.setValueAll( 1.0);
		}
		gNumPits = 0;
		gPitWeight = 0.0;
		numPits = new int[ maps.length];
		pitWeights = new double[ maps.length];
		for( int i = maps.length - 1; i >= 0; i--) {
			numPits[ i] = 0;
			pitWeights[ i] = 0.0;
			for( int r = weight.getNumberRows() - 2; r > 0; r--) {
				for( int c = weight.getNumberColumns() - 2; c > 0; c--) {
					if( is8Pit( maps[ i], r, c) == true) {
						incrementPits( r, c, i);
					}
				}
			}
		}
	}

	private void incrementPits( int row, int col, int mapNumber) {
		gNumPits++;
		numPits[ mapNumber]++;
		gPitWeight += weight.getCellValue( row, col);
		pitWeights[ mapNumber] += weight.getCellValue( row, col);
	}

	private void decrementPits( int row, int col, int mapNumber) {
			gNumPits--;
			numPits[ mapNumber]--;
			gPitWeight -= weight.getCellValue( row, col);
			pitWeights[ mapNumber] -= weight.getCellValue( row, col);
	}

	public void setWeightMap( GISLattice weightMap) {
		weight = weightMap;
	}

	public GISLattice getWeightMap() {
		return weight;
	}

	private boolean is8Pit( GISLattice map, int row, int col) {
		if( map.isNoData( row, col) == true)
			return false;
		double value = map.getCellValue( row, col);
		return( is8Pit( map, row, col, value, CC));
	}

	private boolean is8Pit( GISLattice map, int row, int col, double value, int location) {
		if( row == 0 || col == 0 || row == map.getNumberRows() - 1 ||
				col == map.getNumberColumns() - 1)
			return false;
		if( map.isNoData( row - 1, col - 1) == true)
			return false;
		if( map.isNoData( row - 1, col) == true)
			return false;
		if( map.isNoData( row - 1, col + 1) == true)
			return false;
		if( map.isNoData( row, col - 1) == true)
			return false;
		if( map.isNoData( row, col) == true)
			return false;
		if( map.isNoData( row, col + 1) == true)
			return false;
		if( map.isNoData( row + 1, col - 1) == true)
			return false;
		if( map.isNoData( row + 1, col) == true)
			return false;
		if( map.isNoData( row + 1, col + 1) == true)
			return false;
		double centerValue = map.getCellValue( row, col);
		if( location == CC) {
			centerValue = value;
		} else if( value < centerValue) {
			return false;
		}
		if( location != UL) {
			if( map.getCellValue( row - 1, col - 1) < centerValue)
				return false;
		}
		if( location != UC) {
			if( map.getCellValue( row - 1, col) < centerValue)
				return false;
		}
		if( location != UR) {
			if( map.getCellValue( row - 1, col + 1) < centerValue)
				return false;
		}
		if( location != CL) {
			if( map.getCellValue( row, col - 1) < centerValue)
				return false;
		}
		if( location != CR) {
			if( map.getCellValue( row, col + 1) < centerValue)
				return false;
		}
		if( location != LL) {
			if( map.getCellValue( row + 1, col - 1) < centerValue)
				return false;
		}
		if( location != LC) {
			if( map.getCellValue( row + 1, col) < centerValue)
				return false;
		}
		if( location != LR) {
			if( map.getCellValue( row + 1, col + 1) < centerValue)
				return false;
		}
		return true;
	}

	/**
	 * in alpha testing.
	 * This method will create a copy of the spatialStatistic and return it.
	 */
	public SpatialStatistic createCopy() {
		PitCount pc = new PitCount();
		pc.gNumPits = gNumPits;
		pc.numPits = new int[ numPits.length];
		pc.pitWeights = new double[ pitWeights.length];
		for( int i = numPits.length - 1; i >= 0; i--) {
			pc.numPits[ i] = numPits[ i];
			pc.pitWeights[ i] = pitWeights[ i];
		}
		pc.gPitWeight = gPitWeight;
		pc.weight = weight;
		return( (SpatialStatistic) pc);
	}

	/** Modifies spatial statistic based on change of one value in map.
	 * in alpha testing.
	 */
	public void modify( GISGrid gridMaps[], int mapA, int mapB, int row, int col) {
		GISLattice[] maps = (GISLattice[]) gridMaps;
		double valueA = maps[ mapA].getCellValue( row, col);
		double valueB = maps[ mapB].getCellValue( row, col);
		oneMapModify( maps, mapA, row, col, valueB);
		oneMapModify( maps, mapB, row, col, valueA);
	}

	/** Modifies spatial statistic based on change of one value in map.
	 * in alpha testing.
	 */
	private void oneMapModify( GISLattice[] maps, int mapNumber, int row, int col, double newValue) {
		if( is8Pit( maps[ mapNumber], row, col) == true) {
			decrementPits( row, col, mapNumber);
		}
		if( is8Pit( maps[ mapNumber], row, col, newValue, CC) == true) {
			incrementPits( row, col, mapNumber);
		}
		boolean isLeft = false;
		if( col > 0)
			isLeft = true;
		boolean isRight = false;
		if( col < maps[ mapNumber].getNumberColumns() - 1)
			isRight = true;
		if( row > 0) {
			if( maps[ mapNumber].isNoData( row - 1, col) == false) {
				if( is8Pit( maps[ mapNumber], row - 1, col) == true) {
					decrementPits( row - 1, col, mapNumber);
				}
				if( is8Pit( maps[ mapNumber], row - 1, col, newValue, LC) == true) {
					incrementPits( row - 1, col, mapNumber);
				}
			}
			if( isLeft) {
				if( maps[ mapNumber].isNoData( row - 1, col - 1) == false) {
					if( is8Pit( maps[ mapNumber], row - 1, col - 1) == true) {
						decrementPits( row - 1, col - 1, mapNumber);
					}
					if( is8Pit( maps[ mapNumber], row - 1, col - 1, newValue, LR) == true) {
						incrementPits( row - 1, col - 1, mapNumber);
					}
				}
			}
			if( isRight) {
				if( maps[ mapNumber].isNoData( row - 1, col + 1) == false) {
					if( is8Pit( maps[ mapNumber], row - 1, col + 1) == true) {
						decrementPits( row - 1, col + 1, mapNumber);
					}
					if( is8Pit( maps[ mapNumber], row - 1, col + 1, newValue, LL) == true) {
						incrementPits( row - 1, col + 1, mapNumber);
					}
				}
			}
		}
		if( row < maps[ mapNumber].getNumberRows() - 1) {
			if( maps[ mapNumber].isNoData( row + 1, col) == false) {
				if( is8Pit( maps[ mapNumber], row + 1, col) == true) {
					decrementPits( row + 1, col, mapNumber);
				}
				if( is8Pit( maps[ mapNumber], row + 1, col, newValue, UC) == true) {
					incrementPits( row + 1, col, mapNumber);
				}
			}
			if( isLeft) {
				if( maps[ mapNumber].isNoData( row + 1, col - 1) == false) {
					if( is8Pit( maps[ mapNumber], row + 1, col - 1) == true) {
						decrementPits( row + 1, col - 1, mapNumber);
					}
					if( is8Pit( maps[ mapNumber], row + 1, col - 1, newValue, UR) == true) {
						incrementPits( row + 1, col - 1, mapNumber);
					}
				}
			}
			if( isRight) {
				if( maps[ mapNumber].isNoData( row + 1, col + 1) == false) {
					if( is8Pit( maps[ mapNumber], row + 1, col + 1) == true) {
						decrementPits( row + 1, col + 1, mapNumber);
					}
					if( is8Pit( maps[ mapNumber], row + 1, col + 1, newValue, UL) == true) {
						incrementPits( row + 1, col + 1, mapNumber);
					}
				}
			}
		}
		if( isLeft) {
			if( maps[ mapNumber].isNoData( row, col - 1) == false) {
				if( is8Pit( maps[ mapNumber], row, col - 1) == true) {
					decrementPits( row, col - 1, mapNumber);
				}
				if( is8Pit( maps[ mapNumber], row, col - 1, newValue, CR) == true) {
					incrementPits( row, col - 1, mapNumber);
				}
			}
		}
		if( isRight) {
			if( maps[ mapNumber].isNoData( row, col + 1) == false) {
				if( is8Pit( maps[ mapNumber], row, col + 1) == true) {
					decrementPits( row, col + 1, mapNumber);
				}
				if( is8Pit( maps[ mapNumber], row, col + 1, newValue, CL) == true) {
					incrementPits( row, col + 1, mapNumber);
				}
			}
		}
	}

    public double spread( SpatialStatistic goalSpatialStatistic) {
        return spread((PitCount)goalSpatialStatistic);
    }

    /** Returns spread between two spatial statistics.
	 * in alpha testing.
	 */
	public double spread(PitCount pc) {
		double dif = (gPitWeight - pc.gPitWeight) * (gPitWeight - pc.gPitWeight);
		if( pc.pitWeights.length == pitWeights.length) {
			for( int i = pitWeights.length - 1; i >= 0; i--) {
				dif += (pitWeights[ i] - pc.pitWeights[ i]) * (pitWeights[ i] - pc.pitWeights[ i]);
			}
		} else {
			for( int i = pitWeights.length - 1; i >= 0; i--) {
				dif += pitWeights[ i] * pitWeights[ i];
			}
		}
		dif = Math.sqrt( dif);
		return( dif);
	}

	/** in alpha testing */
	public void print(PrintStream out) {
		gisIO.printBuffered(out, "Pits         PitWeight");
		gisIO.printBuffered(out, gisIO.getIntegerForm().format( gNumPits));
		out.print(" ");
		out.println(gPitWeight);
	}

    public void print(PrintStream printStream, SpatialStatistic goal) throws IOException {
        throw new RuntimeException("Not implemented.");
    }

	/** in alpha testing */
	public void printOneMapMeasure( int mapNumber) {
		if( numPits.length == 1) {
			gisIO.printBuffered( "Pits         PitWeight");
			System.out.println( "");
			gisIO.printBuffered( gisIO.getIntegerForm().format( numPits[ 0]));
			System.out.print( " ");
			//gisIO.printBuffered( gisIO.getDecimalForm().format( pitWeights[ 0]));
			System.out.println( pitWeights[ 0]);
		} else {
			gisIO.printBuffered( "Pits         PitWeight");
			System.out.println( "");
			gisIO.printBuffered( gisIO.getIntegerForm().format( numPits[ mapNumber]));
			System.out.print( " ");
			//gisIO.printBuffered( gisIO.getDecimalForm().format( pitWeights[ mapNumber]));
			System.out.println( pitWeights[ mapNumber]);
		}
	}

    public void printOneMapMeasure(int mapNumber, PrintStream out) {
        if( numPits.length == 1) {
            gisIO.printBuffered( out, "Pits         PitWeight");
            out.println( "");
            gisIO.printBuffered( out, gisIO.getIntegerForm().format( numPits[ 0]));
            out.print( " ");
            //gisIO.printBuffered( out, gisIO.getDecimalForm().format( pitWeights[ 0]));
            out.println( pitWeights[ mapNumber]);
        } else {
            gisIO.printBuffered( out, "Pits         PitWeight");
            out.println( "");
            gisIO.printBuffered( out, gisIO.getIntegerForm().format( numPits[ mapNumber]));
            out.print( " ");
            //gisIO.printBuffered( out, gisIO.getDecimalForm().format( pitWeights[ mapNumber]));
            out.println( pitWeights[ mapNumber]);
        }
    }

	/** This method functionally identical to printMultiMeasure(), except output printed to (fName + ext).
	 * in alpha testing.
	 * @throws IOException 
	 */
	public void printOneMapMeasure( int mapNumber, String fName) throws IOException {
	    PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(fName+".pit")));
		printOneMapMeasure(mapNumber, out);
		out.close();
	}

    public boolean isMultiMap() {
        return numPits.length > 1;
    }
}
