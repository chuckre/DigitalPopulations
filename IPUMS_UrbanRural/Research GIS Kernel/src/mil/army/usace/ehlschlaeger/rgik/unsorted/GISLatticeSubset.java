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
public class GISLatticeSubset {
	private GISLattice map;

	public GISLatticeSubset( GISLattice inMap) {
		map = inMap;
		//map.printGrid();
	}

	public GISLattice getSubset( double n, double s, double e, double w) {
		int northRow = map.getRowIndex( e, n);
		int southRow = map.getRowIndex( e, s);
		int numRows = southRow - northRow + 1;
		int eastColumn = map.getColumnIndex( e, n);
		int westColumn = map.getColumnIndex( w, n); 
		//System.out.println( "northRow: " + northRow + ", southRow: " + southRow );
		//System.out.println( "eastColumn: " + eastColumn + ", westColumn: " + westColumn );

		int numCols = eastColumn - westColumn + 1;
		double EWResolution = map.getEWResolution();
		double NSResolution = map.getNSResolution();
		double northEdge = NSResolution * 0.5 + map.getCellCenterNorthing( northRow, 0);
		double westEdge = map.getCellCenterEasting( 0, westColumn) - EWResolution * 0.5;

		GISLattice newMap = new GISLattice( westEdge, northEdge, EWResolution, NSResolution, numRows, numCols);

		//System.out.println( "n: " + n + ", s: " + s + ", e: " + e + ", w: " + w);
		//newMap.printGrid();
		int dR = map.getRowIndex( e, newMap.getCellCenterNorthing( 0, 0));
		int dC = map.getColumnIndex( newMap.getCellCenterEasting( 0, 0), n);
/*
System.out.println( "dR: " + dR + ", dC: " + dC);
System.out.println( "Old Map:");
System.out.println( "e: " + map.getCellCenterEast( dR, dC) + ", n: " + map.getCellCenterNorth( dR, dC));
System.out.println( "New Map:");
System.out.println( "e: " + newMap.getCellCenterEast( 0,0) + ", n: " + newMap.getCellCenterNorth( 0,0));
*/

		for( int r = 0; r < newMap.getNumberRows(); r++) {
			for( int c = 0; c < newMap.getNumberColumns(); c++) {
				if( map.isNoData( r + dR, c + dC) == false) {
					newMap.setCellValue( r, c, map.getCellValue( r + dR, c + dC));
				}				
			}
		}
		return newMap;
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		if( argv.length != 6) {
			System.out.println( "GISLatticeSubset main program requires five arguments:");
			System.out.println( 
			"java -mx####m GISLatticeSubset inMap outMap northLine southLine eastLine westLine");
			System.exit( -1);
		}
		String inString = argv[ 0];
		String outString = argv[ 1];
		String numString = argv[ 2];
		double n = new Double( numString.trim()).doubleValue();
		numString = argv[ 3];
		double s = new Double( numString.trim()).doubleValue();
		numString = argv[ 4];
		double e = new Double( numString.trim()).doubleValue();
		numString = argv[ 5];
		double w = new Double( numString.trim()).doubleValue();

		GISLattice in = GISLattice.loadEsriAscii(inString);
		GISLatticeSubset cd = new GISLatticeSubset( in);
		GISLattice out = cd.getSubset( n, s, e, w);
		out.writeAsciiEsri( outString);
      }
}
