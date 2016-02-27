package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;


/**
 * Creates a new class that copies its data from a rectangular area
 * within another class.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISClassSubset {
	private GISClass map;

	public GISClassSubset( GISClass inMap) {
		map = inMap;
		//map.printGrid();
	}

	public GISClass getSubset( double n, double s, double e, double w) {
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

		GISClass newMap = new GISClass( westEdge, northEdge, EWResolution, NSResolution, numRows, numCols);

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
			System.out.println( "GISClassSubset main program requires five arguments:");
			System.out.println( 
			"java -mx####m GISClassSubset inMap outMap northLine southLine eastLine westLine");
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

		GISClass in = GISClass.loadEsriAscii( inString);
		GISClassSubset cd = new GISClassSubset( in);
		GISClass out = cd.getSubset( n, s, e, w);
		out.writeAsciiEsri( outString);
      }
}
