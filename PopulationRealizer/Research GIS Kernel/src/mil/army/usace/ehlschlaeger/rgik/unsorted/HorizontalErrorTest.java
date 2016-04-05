package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class HorizontalErrorTest {
	public static void main( String argv[]) throws IOException {
		GISLattice g = GISLattice.loadEsriAscii("study_dem");
		System.out.println( "lag,AbsAverage,StandardDev");
		for( int lag = 1; lag <= 200; lag++) {
			int count = 0;
			double ave = 0.0;
			double absError = 0.0;
			for( int r = lag; r < g.getNumberRows(); r++) {
				for( int c = 0; c < g.getNumberColumns(); c++) {
					if( g.isNoData( r, c) == false) {
						int lagUp = r - lag;
						if( g.isNoData( lagUp, c) == false) {
							double value = g.getCellValue( r, c) - g.getCellValue( lagUp, c);
							count++;
							ave += value;
							if( value > 0.0) {
								absError += value;
							} else {
								absError -= value;
							}
						}
					}
				}
			}
			for( int c = lag; c < g.getNumberColumns(); c++) {
				for( int r = 0; r < g.getNumberRows(); r++) {
					if( g.isNoData( r, c) == false) {
						int lagUp = c - lag;
						if( g.isNoData( r, lagUp) == false) {
							double value = g.getCellValue( r, c) - g.getCellValue( r, lagUp);
							count++;
							ave += value;
							if( value > 0.0) {
								absError += value;
							} else {
								absError -= value;
							}
						}
					}
				}
			}
			ave /= count;
			absError /= count;
			double sd = 0.0;
			for( int r = lag; r < g.getNumberRows(); r++) {
				for( int c = 0; c < g.getNumberColumns(); c++) {
					if( g.isNoData( r, c) == false) {
						int lagUp = r - lag;
						if( g.isNoData( lagUp, c) == false) {
							double value = g.getCellValue( r, c) - g.getCellValue( lagUp, c);
							sd += (value - ave) * (value - ave);
						}
					}
				}
			}
			for( int c = lag; c < g.getNumberColumns(); c++) {
				for( int r = 0; r < g.getNumberRows(); r++) {
					if( g.isNoData( r, c) == false) {
						int lagUp = c - lag;
						if( g.isNoData( r, lagUp) == false) {
							double value = g.getCellValue( r, c) - g.getCellValue( r, lagUp);
							sd += (value - ave) * (value - ave);
						}
					}
				}
			}
			sd = Math.sqrt( sd / (count - 1));	
			System.out.println( (lag * 30.0) + "," + absError + ","	+ sd);
		}
	}
}