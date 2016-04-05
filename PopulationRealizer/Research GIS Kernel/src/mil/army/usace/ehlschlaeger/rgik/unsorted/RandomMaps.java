package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;



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
public class RandomMaps extends RGIS implements RGISFunction  {
	GISClass maps[], copyMap;
	int numMaps;
	DensoStat dGoal, dBest[], dNewA, dNewB;
	BooleanGrid bgDo;
	double fitValue[];
	int doCells, fitOrder[];
	BubbleSort bubble;
	private Runtime rt;

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		double lagDistance = (double) 0.0;
		double maximumDistance = (double) 0.0;
		int numMaps = 0;
		int startRow = 0;
		int startColumn = 0;
		RandomMaps rM;

		if( argv.length < 6 || argv.length > 11) {
			System.out.println( "Application requires six arguments. Form:");
			System.out.println( "java RandomMaps lag_distance max_distance map inPrefix outPrefix numMaps");
			System.out.println( "  [underWeight] [overWeight] [decayExponent] [startRow] [startColumn]");
			System.out.println( "");
			System.out.println( "lag_distance: distance of lag");
			System.out.println( "max_distance: maximum distance to calculate");
			System.out.println( "map: Goal grid map, map statistics to copy");
			System.out.println( "inPrefix: Prefix, maps containing initial noise");
			System.out.println( "outPrefix: Prefix, output maps");
			System.out.println( "numMaps: Number of maps");
			System.out.println( "optional [underWeight]: Impact of under-dense statistics (default: 1.0)");
			System.out.println( "optional [overWeight]: Impact of over-dense statistics (default: 1.0)");
			System.out.println( "optional [decayExponent]: Impact of lag distance on fitting function (default: 1.0)");
			System.out.println( "optional [startRow]: Row swapping algorithm starts (default: 0)");
			System.out.println( "optional [startColumn]: Column swapping algorithm starts (default: 0)");
		} else {
			lagDistance = new Double( argv[0].trim()).doubleValue();
			//System.out.println( "lagDistance: " + lagDistance);
			maximumDistance = new Double( argv[1].trim()).doubleValue();
			//System.out.println( "maximumDistance: " + maximumDistance);
			numMaps = new Integer( argv[5].trim()).intValue();
			//System.out.println( "numMaps: " + numMaps);
			rM = new RandomMaps( argv[2], argv[3], lagDistance, maximumDistance, numMaps);
			if( argv.length > 6)
				rM.dGoal.setUnderWeight( new Double( argv[6].trim()).doubleValue());
			else
				rM.dGoal.setUnderWeight( (double) 1.0);
			if( argv.length > 7)
				rM.dGoal.setOverWeight( new Double( argv[7].trim()).doubleValue());
			else
				rM.dGoal.setOverWeight( (double) 1.0);
			if( argv.length > 8)
				rM.dGoal.setDecayExponent( new Double( argv[8].trim()).doubleValue());
			else
				rM.dGoal.setDecayExponent( (double) 1.0);
			if( argv.length > 9)
				startRow = new Integer( argv[9].trim()).intValue();
			if( argv.length > 10)
				startColumn = new Integer( argv[10].trim()).intValue();
			rM.swapCells(  startRow, startColumn, argv[4]);
		}
	}

	/** in alpha testing 
	 * @throws IOException */
	public RandomMaps( String goalMapPrefix, String noiseMapPrefix, double lagDistance, 
			double maximumDistance, int nMaps) throws IOException {
		super();
		Date nowDate = new Date();
		System.out.println("Starting construction of Random Map object at: " + nowDate);
		rt = Runtime.getRuntime();
		maps = new GISClass[ nMaps];
		fitValue = new double[ nMaps];
		fitOrder = new int[ nMaps];
		copyMap = GISClass.loadEsriAscii(goalMapPrefix);
		int minCat = copyMap.getMinimumValue();
		int maxCat = copyMap.getMaximumValue();
		dGoal = new DensoStat( minCat, maxCat, lagDistance, maximumDistance, copyMap);
		System.out.println( "Goal DensoStat:");
		dGoal.print();
		dGoal.print( goalMapPrefix);
		dNewA = new DensoStat( minCat, maxCat, lagDistance, maximumDistance);
		dNewA.copy( dGoal);
		dNewB = new DensoStat( minCat, maxCat, lagDistance, maximumDistance);
		dNewB.copy( dGoal);
		numMaps = nMaps;
		dBest = new DensoStat[ numMaps];
		for( int m = 0; m < nMaps; m++) {
			maps[ m] = GISClass.loadEsriAscii(noiseMapPrefix + m);
			dBest[ m] = new DensoStat( minCat, maxCat, lagDistance, maximumDistance, maps[m]);
			dBest[ m].print( noiseMapPrefix + m);
			System.out.println( "save noise map: " + m + ", RAM: " + rt.freeMemory());
			rt.gc();
		}
		bgDo = new BooleanGrid( maps[ 0].getNumberRows(), maps[ 0].getNumberColumns(), true);
		int m, value = 0;
		doCells = 0;
		for( int r = maps[ 0].getNumberRows() - 1; r >= 0; r--) {
			for( int c = maps[ 0].getNumberColumns() - 1; c >= 0; c--) {
				if( maps[ numMaps - 1].isNoData( r, c))  {
					bgDo.setBoolean( r, c, false);
				} else {
					value = maps[ numMaps - 1].getCellValue( r, c);
					for( m = numMaps - 2;  m >= 0; m--) {
						if( maps[ m].getCellValue( r, c) != value) {
							m = -10;
							doCells++;
						}
					}
					if( m > -10) {
						bgDo.setBoolean( r, c, false);
					}
				}
			}
		}
		System.out.println( "doCells: " + doCells);
		nowDate = new Date();
		System.out.println("Finished construction of Random Map object at: " + nowDate);	
	}

	/** in alpha testing 
	 * @throws IOException */
	public void swapCells( int startRow, int startCol, String prefixMapName) throws IOException {
		Date nowDate = new Date();
		System.out.println( "Starting swapCells at: " + nowDate);
//		boolean swapped;
		int maxPercentDone = 0;
		int percentDone = 0;
		int maxRow = maps[0].getNumberRows() - 1;
		int maxCol = maps[0].getNumberColumns() - 1;
//		int numCells = (maxRow + 1) * (maxCol + 1);
		int fails = 0;
		int totalSwaps = 0;
		int totalFails = 0;
		int passSwaps = 0;
		for( int i = 0; i < numMaps; i++) {
			fitValue[ i] = dGoal.spread( dBest[i]);
			fitOrder[ i] = i;
		}
		bubble = new BubbleSort( true);
		bubble.sort( fitOrder, fitValue);
		System.out.println( "Initial Fit:");
		for( int i = numMaps - 1; i >= 0 ; i--) {
			System.out.println( fitOrder[i] + ": " + fitValue[ fitOrder[ i]]);
		}
		Date startDate = new Date();
		saveData( prefixMapName, prefixMapName);
		Date endDate = new Date();
		long timeSave = endDate.getTime() - startDate.getTime();
		long timeBetweenSaves = Math.max( timeSave * 24, 15 * 60 * 1000);
		Date incDate = new Date( endDate.getTime() + timeBetweenSaves);
		System.out.println( "Starting row by row at: " + endDate + ", next map save at: " + incDate);
		BooleanGrid pass1 = new BooleanGrid( bgDo);
		boolean doMore = true;
		int firstSwaps = 6;
		//for( int passTimes = 0; passTimes < 10; passTimes++) {
		while( firstSwaps > 5) {
			firstSwaps = -1;
			int passLeft = doCells;
			doMore = false;
			pass1.copy( bgDo);
			while( passLeft > 0) {
				System.out.println( "cells left in row by row fitting: " + passLeft);
				int thisCount = 0;
				for( int r = 0; r <= maxRow; r++) {
					for( int c = 0; c <= maxCol; c++) {
						if( incDate.before( new Date())) {
							saveData( prefixMapName, prefixMapName);
							rt.gc();
							System.out.println( "available memory: " + rt.freeMemory());
							System.out.println( "Total swaps: " + totalSwaps + ", swaps in pass: " + passSwaps);
							System.out.println( "Total fails: " + totalFails + ", fails in pass: " + fails);
							passSwaps = 0; fails = 0;
							Date now = new Date();
							incDate = new Date( now.getTime() + timeBetweenSaves);
							System.out.println("now: " + now + ", next Save: " + incDate);	
							System.out.println( "");
						}
						if( pass1.getBoolean( r, c)) {
							if( swapCell( r, c)) {
								totalSwaps++; passSwaps++; doMore = true; thisCount++;
							} else {
								fails++; totalFails++;
								passLeft--;
								pass1.setBoolean( r, c, false);
							}
						}
					}
				}
				if( firstSwaps == -1)
					firstSwaps = thisCount;
			}
		}
		saveData( prefixMapName, prefixMapName);
		rt.gc();
		System.out.println( "available memory: " + rt.freeMemory());
		System.out.println( "Total swaps: " + totalSwaps + ", swaps in pass: " + passSwaps);
		System.out.println( "Total fails: " + totalFails + ", fails in pass: " + fails);
		passSwaps = 0; fails = 0;
		Date now = new Date();
		incDate = new Date( now.getTime() + timeBetweenSaves);
		System.out.println( "Starting flood fill passes at: " + now + ", next Save: " + incDate);	
		System.out.println( "");
		while( doMore) {
			percentDone = 0;
			if( incDate.before( new Date())) {
				saveData( prefixMapName, prefixMapName);
				rt.gc();
				System.out.println( "");
				System.out.println( "Present Fit:");
				for( int i = numMaps - 1; i >= 0 ; i--) {
					System.out.println( fitOrder[i] + ": " + fitValue[ fitOrder[ i]]);
				}
				System.out.println( "Total swaps: " + totalSwaps + ", swaps in pass: " + passSwaps);
				System.out.println( "Total fails: " + totalFails + ", fails in pass: " + 
					Math.min( fails, totalFails));
				passSwaps = 0;
				fails = 0;
				now = new Date();
				incDate = new Date( now.getTime() + timeBetweenSaves);
				System.out.println("now: " + now + ", next Save: " + incDate);
			}
			doMore = false; 
			int cellsFailed = 0;
			// swapCell() returns boolean indicating if cell is swapped.
			if( swapCell( startRow, startCol)) {
				totalSwaps++; passSwaps++;
				doMore = true;
			} else {
				cellsFailed++; fails++; totalFails++;
				int westCol = startCol - 1;
				int eastCol = startCol + 1;
				int northRow = startRow - 1;
				int southRow = startRow + 1;
				do {
					if( northRow >= 0) {
						for( int c = Math.max( 0, westCol); c <= Math.min( maxCol, eastCol); c++) {
							if( bgDo.getBoolean( northRow, c)) {
								if( swapCell( northRow, c)) {
									totalSwaps++; passSwaps++;
									doMore = true;
									startRow = northRow;
									startCol = c;
									c = maxCol + 1;
								} else {
									cellsFailed++; fails++; totalFails++;
								}
							}
						}
					}
					if( doMore == false && southRow <= maxRow) {
						for( int c = Math.max( 0, westCol); c <= Math.min( maxCol, eastCol); c++) {
							if( bgDo.getBoolean( southRow, c)) {
								if( true == swapCell( southRow, c)) {
									totalSwaps++; passSwaps++;
									doMore = true;
									startRow = southRow;
									startCol = c;
									c = maxCol + 1;
								} else {
									cellsFailed++; fails++; totalFails++;
								}
							}
						}
					}
					if( doMore == false && eastCol <= maxCol) {
						for( int r = Math.max( 0, northRow + 1); r <= Math.min( maxRow, southRow - 1); r++) {
							if( bgDo.getBoolean( r, eastCol)) {
								if( true == swapCell( r, eastCol)) {
									totalSwaps++; passSwaps++;
									doMore = true;
									startRow = r;
									startCol = eastCol;
									r = maxRow + 1;
								} else {
									cellsFailed++; fails++; totalFails++;
								}
							}
						}
					}
					if( doMore == false && westCol >= 0) {
						for( int r = Math.max( 0, northRow + 1); r <= Math.min( maxRow, southRow - 1); r++) {
							if( bgDo.getBoolean( r, westCol)) {
								if( true == swapCell( r, westCol)) {
									totalSwaps++; passSwaps++;
									doMore = true;
									startRow = r;
									startCol = westCol;
									r = maxRow + 1;
								} else {
									cellsFailed++; fails++; totalFails++;
								}
							}
						}
					}
					if( percentDone < 20 * cellsFailed / doCells) {
						percentDone = 20 * cellsFailed / doCells;
						if( percentDone > maxPercentDone)
							maxPercentDone = percentDone;
						System.out.println( "%Done: " + (5 * percentDone) + ", Max %Done: " + (5 * maxPercentDone));
					}
				} while( doMore == false && 
					((--westCol) >= 0 | (--northRow) >= 0 |
					(++eastCol) <= maxCol | (++southRow) <= maxRow));
			} 
		}
		saveData( prefixMapName, prefixMapName);
		System.out.println("Order of maps from best fitting to worst fitting:");
		for( int i = numMaps - 1; i >= 0; i--)
			System.out.println( fitOrder[i] + ": " + fitValue[ fitOrder[i]]);
		System.out.println("");
		nowDate = new Date();
		System.out.println("Finished swapCells at: " + nowDate);
		System.out.println("Total swaps: " + totalSwaps + ", Total fails: " + totalFails + ", Ratio Swaps: " + ((1.0 * totalSwaps) / (totalSwaps + totalFails)));
	}

	/** in alpha testing */
	private boolean swapCell( int row, int col) {
		//if( bgDo.getBoolean( row, col) == false)
		//	return( false);
		for( int i = 0; i < numMaps - 1; i++) {
			int mapA = fitOrder[i];
			int vA = maps[ mapA].getCellValue( row, col);
			for( int j = i + 1; j < numMaps; j++) {
				int mapB = fitOrder[j];
				int vB = maps[ mapB].getCellValue( row, col);
				if( vA != vB) {
					if( better( row, col, mapA, mapB, vA, vB)) {
						return( true);
					}
				}
			}
		}
		return( false);
	}

	/** in alpha testing */
	private boolean better( int row, int col, int mapA, int mapB, int swapAv, int swapBv) {
		int maxRows = (int) (dBest[0].getMaximumDistance() / maps[0].getNSResolution());
		int minRow = Math.max( row - maxRows, 0);
		int maxRow = Math.min( row + maxRows, maps[0].getNumberRows() - 1);
		int maxCols = (int) (dBest[0].getMaximumDistance() / maps[0].getEWResolution());
		int minCol = Math.max( col - maxCols, 0);
		int maxCol = Math.min( col + maxCols, maps[0].getNumberColumns() - 1);
		double dist;
		double valA, valB;

		dNewA.copy( dBest[ mapA]);
		dNewB.copy( dBest[ mapB]);
		for( int r = minRow; r <= maxRow; r++) {
			for( int c = minCol; c <= maxCol; c++) {
				if( (r != row || c != col) && ! maps[ 0].isNoData( r, c) &&
						dBest[0].getMaximumDistance() >= (dist = maps[0].distance( r, c, row, col))) {
					int lag = Math.min( (int) (dist / dNewA.getLagDistance()), dNewA.getNumberLags() - 1);
					//modify( int catOutCell, int catInCell, int compareCat, int lag)
					dNewA.modify( swapAv, swapBv, maps[ mapA].getCellValue( r, c), lag);
					dNewB.modify( swapBv, swapAv, maps[ mapB].getCellValue( r, c), lag);
				}
			}
		}
		valA = dGoal.spread( dNewA);
		valB = dGoal.spread( dNewB);
/*
System.out.println("Goal:");
dGoal.print();
System.out.println( "a:");
dNewA.print();
System.out.println( "b:");
dNewB.print();
System.out.println("valA: " + valA + ", oriA: " + fitValue[ mapA] + ", valB: " + valB +
", oriB: " + fitValue[ mapB]);
*/
		if( valA - fitValue[ mapA] + valB - fitValue[ mapB]  < -0.000001) {
			//System.out.println( "swapping");
			maps[ mapA].setCellValue( row, col, swapBv);
			maps[ mapB].setCellValue( row, col, swapAv);
			dBest[ mapA].copy( dNewA);
			dBest[ mapB].copy( dNewB);
			fitValue[ mapA] = valA;
			fitValue[ mapB] = valB;
			bubble.sort( fitOrder, fitValue);
			return( true);
		}
		return( false);
	}

	/** in alpha testing 
	 * @throws IOException */
	public void saveData( String prefixMapName, String prefixDenso) throws IOException {
		for( int m = numMaps - 1; m >= 0; m--) {
			maps[ m].writeAsciiEsri( prefixMapName + m);
			dBest[ m].print( prefixDenso + m);
		}
	}
}
