package mil.army.usace.ehlschlaeger.rgik.test;

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
public class DEMData {

	public DEMData() {
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	  GISLattice g = GISLattice.loadEsriAscii("study_dem");
	  GISLattice d = GISLattice.loadEsriAscii("study_dted");
	  System.out.println( "x,y,dem,dted");
	  for( int r = 0; r < g.getNumberRows(); r++) {
		for( int c = 0; c < g.getNumberColumns(); c++) {
			if( g.isNoData( r, c) == false && d.isNoData( r, c) == false) {
				System.out.println(
					g.getCellCenterEasting( r, c) + "," +
					g.getCellCenterNorthing( r, c) + "," +
					g.getCellValue( r, c) + "," +
					d.getCellValue( r, c));
			}				
		}
	  }
      }
}
