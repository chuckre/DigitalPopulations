package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;

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
public class ClassMorph extends RGIS implements RGISFunction  {
	private GISClass startMap, endMap;
	private GISLattice doMap;
	private int Rs, Cs, numSwaps;
	private DensoStat dStart, dEnd, dNow, dCheck, bestD;
	private BooleanGrid bgDo;

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		GISClass sM = GISClass.loadEsriAscii("w_il_lc");
		GISClass eM = new GISClass( sM);
		Random rng = new Random( );
		int oldCat = sM.getCellValue( 0, 0);
		for( int r = 0; r < sM.getNumberRows(); r++) {
			for( int c = 0; c < sM.getNumberColumns(); c++) {
				int value = sM.getCellValue( r, c);
				if( value != oldCat) {
					if( rng.nextDouble() < .001) {
						eM.setCellValue( r, c, oldCat);
						oldCat = value;
					} else {
						eM.setCellValue( r, c, value);
					}
				} else {
					eM.setCellValue( r, c, value);
				}
			}
		}
		ClassMorph cM = new ClassMorph( sM, eM, 1.01f * sM.getEWResolution(), 
			3.0f * sM.getEWResolution());
		GISLattice timeMap = cM.getTimeSwitch();
		timeMap.writeAsciiEsri( "w_il_swapTime");
		GISClass mapAtHalf = cM.getMapAtTime( 0.5);
		mapAtHalf.writeAsciiEsri( "w_il_halfTime");
		int startCell = sM.getCellValue( 9, 8);
		int halfCell = cM.getCellAtTime( 9, 8, 0.5);
		int endCell = eM.getCellValue( 9, 8);
		System.out.println( "start: " + startCell + ", halfCell: " + halfCell + ", endCell: " + endCell);
	}

	public GISLattice getTimeSwitch() {
		return doMap;
	}

	public GISClass getMapAtTime( double timeRatio) {
		GISClass map = new GISClass( startMap, startMap);
		for( int r = 0; r < map.getNumberRows(); r++) {
			for( int c = 0; c < map.getNumberColumns(); c++) {
				if( startMap.isNoData( r, c) != true) {
					if( timeRatio <= doMap.getCellValue( r, c)) {
						map.setCellValue( r, c, endMap.getCellValue( r, c));
					}
				}
			}
		}
		return map;
	}

	public int getCellAtTime( int row, int col, double timeRatio) {
		if( startMap.isNoData( row, col) != true && endMap.isNoData( row, col) != true) {
			if( timeRatio <= doMap.getCellValue( row, col)) {
				return( endMap.getCellValue( row, col));
			} else {
				return( startMap.getCellValue( row, col));
			}
		}
		throw new DataException( "Asking for cell with no data value");
	}

	public ClassMorph( GISClass sMap, GISClass eMap, double lagDist, double maxDist) {
		super();
		doMap = new GISLattice( sMap, 0.0); 
		startMap = sMap;
		endMap = eMap;
		int minCat = (int) Math.min( startMap.getMinimumValue(), endMap.getMinimumValue());
		int maxCat = (int) Math.max( startMap.getMaximumValue(), endMap.getMaximumValue());
		dStart = new DensoStat( minCat, maxCat, lagDist, maxDist, startMap);
		dEnd = new DensoStat( minCat, maxCat, lagDist, maxDist, eMap);
		dNow = new DensoStat( minCat, maxCat, lagDist, maxDist);
		dCheck = new DensoStat( minCat, maxCat, lagDist, maxDist);
		bestD = new DensoStat( minCat, maxCat, lagDist, maxDist);
		Rs = startMap.getNumberRows();
		Cs = startMap.getNumberColumns();
		bgDo = new BooleanGrid( Rs, Cs, false);
		numSwaps = 0;
		for( int r = Rs - 1; r >= 0; r--) {
			for( int c = Cs - 1; c >= 0; c--) {
				if( ! startMap.isNoData( r, c) && ! endMap.isNoData( r, c) &&
						startMap.getCellValue( r, c) != endMap.getCellValue( r, c)) {
					numSwaps++;
					bgDo.setBoolean( r, c, true);
				}
			}
		}
System.out.println( "swaps to make: " + numSwaps);
		makeTimeSwitch();
	}

	private void makeTimeSwitch() {
		dNow.copy( dStart);
		int doNum = 1;
		while( doNum <= numSwaps) {
			double best = dEnd.spread( dNow) * 1000.0;
			int bestR = -1;
			int bestC = -1;
			for( int r = Rs - 1; r >= 0; r--) {
				for( int c = Cs - 1; c >= 0; c--) {
					if( bgDo.getBoolean( r, c) == true) {
						double checkScore = swapScore( r, c, startMap.getCellValue( r, c), 
							endMap.getCellValue( r, c));
						if( checkScore < best) {
							best = checkScore;
							bestR = r;
							bestC = c;
							bestD.copy( dCheck);
						}							
					}
				}
			}
			if( bestR == -1) {
			    throw new DataException( "error in makeMorph");
			}
			bgDo.setBoolean( bestR, bestC, false);
			dNow.copy( bestD);
			//System.out.println( "swapping " + startMap.getCellValue( bestR, bestC) + " with " + endMap.getCellValue( bestR, bestC) + " at (" + bestR + "," + bestC + ")");
			//doMap.printClass();
			doMap.setCellValue( bestR, bestC, (1.0 * doNum / numSwaps));
			//doMap.printClass();
			//doMap.writeAsciiEsri( morphName + doNum);
			//dNow.print( morphName + doNum);
			doNum++;
		}
	}

	/** in alpha testing */
	private double swapScore( int row, int col, int classNow, int classEnd) {
		dCheck.copy( dNow);
		int maxRows = (int) (dEnd.getMaximumDistance() / endMap.getNSResolution());
		int minRow = Math.max( row - maxRows, 0);
		int maxRow = Math.min( row + maxRows, Rs - 1);
		int maxCols = (int) (dEnd.getMaximumDistance() / endMap.getEWResolution());
		int minCol = Math.max( col - maxCols, 0);
		int maxCol = Math.min( col + maxCols, Cs - 1);
		double dist = 0.0f;
		for( int r = minRow; r <= maxRow; r++) {
			for( int c = minCol; c <= maxCol; c++) {
				if( (r != row || c != col) && ! endMap.isNoData( r, c) &&
						dEnd.getMaximumDistance() >= (dist = endMap.distance( r, c, row, col))) {
					int lag = dEnd.lag( dist);
					//modify( int catOutCell, int catInCell, int compareCat, int lag)
					dCheck.modify( classNow, classEnd, startMap.getCellValue( r, c), lag);
				}
			}
		}
		return( dEnd.spread( dCheck));
	}
}
