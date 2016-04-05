package mil.army.usace.ehlschlaeger.rgik.test;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  @version 0.3
 */

	/** in alpha testing */
public class ElevationNoise {

	public ElevationNoise( GISLattice quality, String outputName) throws IOException {
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c);	
					double noise = .5 * Math.random() - 0.25;
					quality.setCellValue( r, c, value + noise);
				}
			}
		}
		quality.writeAsciiEsri( outputName);
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		System.out.println( "running ElevationNoise");
		// The next two maps should have the same extents and resolutions!!!!!!!!
		GISLattice g = GISLattice.loadEsriAscii("countydem");
		//ElevationNoise dtedResults = 
		new ElevationNoise( g, "countydem2");
	}
}