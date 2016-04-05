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
public class DEMData2 {

	public DEMData2() {
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	  double xyInt = -1145.3042;
	  double xCoef = -0.0004714747;
	  double yCoef = 0.0003273871;
	  GISLattice d = GISLattice.loadEsriAscii("study_dted");
	  for( int r = 0; r < d.getNumberRows(); r++) {
		for( int c = 0; c < d.getNumberColumns(); c++) {
			if( d.isNoData( r, c) == false) {
				d.setCellValue( r, c,  
					(xyInt + xCoef * d.getCellCenterEasting( r, c) + 
					 yCoef * d.getCellCenterNorthing( r, c)));
			}				
		}
	  }
	  d.writeAsciiEsri( "planarTrend");
      }
}
