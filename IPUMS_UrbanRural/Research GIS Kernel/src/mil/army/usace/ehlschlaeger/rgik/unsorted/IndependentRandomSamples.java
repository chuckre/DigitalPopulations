package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.io.IOException;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class IndependentRandomSamples {
	private GISLattice	original;
	private int[]		cellsInRow;
	private int			cellsInOriginal, lastSamples;
	private Random		rng;

	public IndependentRandomSamples( GISLattice originalMap) {
		original = originalMap;
		cellsInRow = new int[ originalMap.getNumberRows()];
		rng = new Random();
		lastSamples = Integer.MIN_VALUE;
	}

	public IndependentRandomSamples( GISLattice originalMap, long seed) {
		original = originalMap;
		cellsInRow = new int[ originalMap.getNumberRows()];
		rng = new Random( seed);
		lastSamples = Integer.MIN_VALUE;
	}

	public GISLattice getSample( int maxSamples, double distanceBetweenSamples) {
		int rows = original.getNumberRows();
		int cols = original.getNumberColumns();
		BooleanGrid possibleSamples = new BooleanGrid( rows, cols, false);
		cellsInOriginal = 0;
		for( int r = 0; r < rows; r++) {
			cellsInRow[ r] = 0;
			for( int c = 0; c < cols; c++) {
				if( original.isNoData( r, c) == false) {
					cellsInOriginal++;
					cellsInRow[ r]++;
					possibleSamples.setBoolean( r, c, true);
				}
			}
		}
		GISLattice result = new GISLattice( original);
		double ewRes = original.getEWResolution();
		double nsRes = original.getNSResolution();
		int rowBuffer = (int) Math.floor( distanceBetweenSamples / nsRes);
		int colBuffer = (int) Math.floor( distanceBetweenSamples / ewRes);
		lastSamples = 0;
		while( cellsInOriginal > 0 && maxSamples > 0) {
			int doCell = rng.nextInt( cellsInOriginal);
			int row = 0;
			while( doCell - cellsInRow[ row] >= 0) {
				doCell -= cellsInRow[ row];
				row++;
			}
			int col = 0;
			while( doCell >= 0) {
				boolean ps = possibleSamples.getBoolean( row, col);
				if( ps == true) {
					doCell--;
				} 
				if( doCell >= 0) {
					col++;
				}
			}
			result.setCellValue( row, col, original.getCellValue( row, col));
			lastSamples++;
			maxSamples--;
			int minRow = Math.max( 0, row - rowBuffer);
			int maxRow = Math.min( rows - 1, row + rowBuffer);
			int minCol = Math.max( 0, col - colBuffer);
			int maxCol = Math.min( cols - 1, col + colBuffer);
			for( int r = minRow; r <= maxRow; r++) {
				for( int c = minCol; c <= maxCol; c++) {
					if( result.distance( row, col, r, c) <= distanceBetweenSamples) {
						if( possibleSamples.getBoolean( r, c) == true) {
							possibleSamples.setBoolean( r, c, false);
							cellsInRow[ r]--;
							cellsInOriginal--;
						}
					}
				}
			}
		}
		//System.out.println( "IndependentRandomSamples.getSample() finishing");
		return( result);
	}

	public int getNumberSamples() {
		return lastSamples;
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		System.out.println( "running IndependentRandomSamples");
		System.out.println( "Choose quality map to make samples from");
		IndependentRandomSamples ars = new IndependentRandomSamples( GISLattice.loadEsriAscii("study_dem"));
		GISLattice measuringSample = ars.getSample( Integer.MAX_VALUE, 300.0);
		measuringSample.writeAsciiEsri( "sample");
	}
}