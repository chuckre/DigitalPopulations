package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.io.ESRI_ASCII;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;
import mil.army.usace.ehlschlaeger.rgik.util.RowRLE;



/**
 * Like GISClass, but performs run-length compression to reduce memory
 * consumption.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GISClassRLE extends GISClass {
    private RowRLE[] rows;
    private Runtime  rt;

	/** parameters define metadata for new GISClassRLE. 
	 */
	public GISClassRLE( double westEdge, double northEdge, double EWResolution,
			double NSResolution, int numRows, int numCols) {
		super( westEdge, northEdge, EWResolution, NSResolution, 1, 1);
		initRLE( numRows, numCols);
	}

	/** grid metadata for new GISClassRLE. 
	 */
	public GISClassRLE( GISGrid grid) {
		super( grid.getWestEdge(), grid.getNorthEdge(), 
			grid.getEWResolution(), grid.getNSResolution(), 1, 1);
		initRLE( grid.getNumberRows(), grid.getNumberColumns());
	}

	private void initRLE( int numRows, int numColumns) {
		//System.out.println( "GISClassRLE.initRLE() starting");
		rt = Runtime.getRuntime();
		setMainArray( null);
		setNumberRows( numRows);
		setSouthEdge( getNorthEdge() - numRows * getNSResolution());
		setEastEdge( getWestEdge() + numColumns * getEWResolution());
		setNumberColumns( numColumns);
		rows = new RowRLE[ getNumberRows()];
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			rows[ r] = new RowRLE( getNumberColumns());
			rows[ r].optimizeRow();
		}
		setMinimumChange( true);
		setMaximumChange( true);
		rt.gc();
		//System.out.println( "GISClassRLE.initRLE() finished");
	}

	/** This constructor resamples data from data map to grid metadata.
	 *  Currently, the aggregation method isn't implemented
	 */
	/** @param grid metadata for new GISClassRLE,
	 *  @param data class data to be copied into new GISClassRLE.
	 */
	public GISClassRLE( GISGrid grid, GISClass data) {
		super( grid.getWestEdge(), grid.getNorthEdge(), 
			grid.getEWResolution(), grid.getNSResolution(), 1, 1);
		initRLE( grid.getNumberRows(), grid.getNumberColumns());
		double gridRes = Math.min( grid.getEWResolution(), grid.getNSResolution());
		double dataRes = Math.min( data.getEWResolution(), data.getNSResolution());
		if( gridRes > dataRes) {
	        throw new RuntimeException("GISClassRLE constructor not finished yet");
		} else {
			for( int r = 0; r <= getNumberRows() - 1; r++) {
				for( int c = 0; c <= getNumberColumns() - 1; c++) {
					double newE = getCellCenterEasting( r, c);
					double newN = getCellCenterNorthing( r, c);
					if( ! data.onMap( newE, newN) || data.isNoData( newE, newN)) {
						setNoData( r, c, true);
					} else {
						setCellValue( r, c, data.getCellValue( newE, newN));
					}
				}
				rows[ r].optimizeRow();
			}
		}
	}

	/** to be deprecated in favor of "static GISClass loadEsriAscii(MyReader fr)" */
	/** @param fileName esri .ASC file without the ".ASC" extension. 
	 * @throws IOException */
	public GISClassRLE( String fileName) throws IOException {
		super( 0.0, 0.0, 1.0, 1.0, 1, 1);
		int endIndex = 0, startIndex = 0;
		int numberRows, numberColumns;
		//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") starting");
		String s = "", numberS = "";
		MyReader fr = new MyReader( ESRI_ASCII.findFile(fileName));
		//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") a");
		s = fr.readLine(); // ncols         10
		startIndex = nextNumber( s, 4);
		s = s.substring( startIndex);
		numberColumns = new Integer( s.trim()).intValue();
		s = fr.readLine(); // nrows         15
		startIndex = nextNumber( s, 4);
		s = s.substring( startIndex);
		numberRows =  new Integer( s.trim()).intValue();
		s = fr.readLine(); // xllcorner     100000
		startIndex = nextNumber( s, 8);
		s = s.substring( startIndex);
		double west = new Double( s.trim()).doubleValue();
		setWestEdge( west);
		s = fr.readLine(); // yllcorner     300000
		startIndex = nextNumber( s, 8);
		s = s.substring( startIndex);
		double sEdge = new Double( s.trim()).doubleValue();
		setSouthEdge( sEdge);
		s = fr.readLine(); // cellsize      100
		startIndex = nextNumber( s, 7);
		s = s.substring( startIndex);
		double Res = new Double( s.trim()).doubleValue();
		s = fr.readLine(); // NODATA_value  -9999 (opt).
		char ch = s.charAt( 0);
		if( ch == 'N' || ch == 'n') {
			startIndex = nextNumber( s, 11);
			s = s.substring( startIndex);
			setNoDataValue( new Integer( s.trim()));	
			s = fr.readLine();
		}
		double north = sEdge + Res * numberRows;
		setNorthEdge( north);
		setEastEdge( west + Res * numberColumns);
		setNSResolution( Res);
		setEWResolution( Res);
		initRLE( numberRows, numberColumns);
		int row = 0;
		int col = 0;
		startIndex = 0;
		int allFlatCells = 0;
		int allRLECells = 0;
		//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") d");
		while( row < getNumberRows()) {
			//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") row: " + row);
			if( startIndex >= s.length())  {
				s = fr.readLine();
//System.out.println( s);
				startIndex = 0;
				while( ch == ' ') {
					startIndex++;
					ch = s.charAt( startIndex);
				}
			}
			endIndex = endNumber( s, startIndex);
			if( endIndex == s.length() - 1)
				endIndex++;
			numberS = s.substring( startIndex, endIndex);
System.out.println( "row:" + row + ", col:" + col + ", sIndex:" + startIndex + ", eIndex:" + endIndex + ", numberS: " + numberS);
			int newValue = new Integer( numberS.trim()).intValue();
			if( hasNoDataValue() == true && newValue == getNoDataValue()) {
				setNoData( row, col, true);
			} else {
				setNoData( row, col, false);
				setCellValue( row, col, newValue);
			}
			startIndex = nextNumber( s, endIndex);
			col++;
			if( col == getNumberColumns()) {
				col = 0;
				if( rows[ row].optimizeRow() == true) {
					allRLECells += rows[ row].getLengthRow();
				} else allFlatCells += rows[ row].getLengthRow();
				row++;
			}
		}
		fr.close();
		//* debugging prints
		System.out.println("");
		System.out.println( "If flat: [" + (getNumberRows() * getNumberColumns()) + "] cells, this");
		System.out.println( "map has: [" + (allFlatCells + allRLECells) + "] cells for a size ratio of: " + 
			((allRLECells * 3.0 + allFlatCells) / (getNumberRows() * getNumberColumns())));
		//*/
	}

	/** Currently, the GRASS 5.0 ASCII files are not supported.
	 *  @param fileName ASCII file containing grid metadata without the extension. 
	 *  @param fileType if GISGrid.ESRI is passed as the argument, then the file 
	 *  is an ESRI ASCII (*.asc) file. If GISGrid.GRASS4 is passed as the argument,
	 *  then the file is the ASCII output from GRASS (or Idrisi's GRASSIDR). The 
	 *  GRASS ASCII file must have a .gra extension. 
	 * @throws IOException 
	 */
	public GISClassRLE( String fileName, int fileType) throws IOException {
		super( 0.0, 0.0, 1.0, 1.0, 2, 2);
		int endIndex = 0, startIndex = 0;
		int numberRows = 1, numberColumns = 1;
		String s = "", numberS = "";
		String totalFileName = ESRI_ASCII.findFile(fileName).getAbsolutePath();
		if( fileType == GISGrid.GRASS4) {
			totalFileName = grassFileName( fileName);
		}
		MyReader fr = new MyReader( totalFileName);
		if( fileType == GISGrid.ESRI) {
			s = fr.readLine(); // ncols         10
			startIndex = nextNumber( s, 4);
			s = s.substring( startIndex);
			numberColumns = new Integer( s.trim()).intValue();
			s = fr.readLine(); // nrows         15
			startIndex = nextNumber( s, 4);
			s = s.substring( startIndex);
			numberRows =  new Integer( s.trim()).intValue();
			s = fr.readLine(); // xllcorner     100000
			startIndex = nextNumber( s, 8);
			s = s.substring( startIndex);
			double west = new Double( s.trim()).doubleValue();
			setWestEdge( west);
			s = fr.readLine(); // yllcorner     300000
			startIndex = nextNumber( s, 8);
			s = s.substring( startIndex);
			double sEdge = new Double( s.trim()).doubleValue();
			s = fr.readLine(); // cellsize      100
			startIndex = nextNumber( s, 7);
			s = s.substring( startIndex);
			double Res = new Double( s.trim()).doubleValue();
			double north = sEdge + Res * numberRows;
			setNorthEdge( north);
			setEastEdge( west + Res * getNumberColumns());
			setNSResolution( Res);
			setEWResolution( Res);
			initRLE( numberRows, numberColumns);
			s = fr.readLine(); // NODATA_value  -9999 (opt).
			char ch = s.charAt( 0);
			if( ch == 'N' || ch == 'n') {
				startIndex = nextNumber( s, 11);
				s = s.substring( startIndex);
				setNoDataValue( new Integer( s.trim()).intValue());	
				s = fr.readLine();
			}
		} else if( fileType == GISGrid.GRASS4) {
// The GRASS5 header now can have entries: multiplier, null, and type. If type is not set in the header, the new
// map is created integer if all of the values in the input file are integer, and float otherwise.
			s = fr.readLine(); // north: 4299000.00
			startIndex = nextNumber( s, 4);
			s = s.substring( startIndex);
			setNorthEdge( new Double( s.trim()).doubleValue());
			s = fr.readLine(); // south:  4247000.00
			startIndex = nextNumber( s, 4);
			s = s.substring( startIndex);
			setSouthEdge( new Double( s.trim()).doubleValue());
			s = fr.readLine(); // east:    528000.00
			startIndex = nextNumber( s, 8);
			s = s.substring( startIndex);
			double east = new Double( s.trim()).doubleValue();
			setEastEdge( east);
			s = fr.readLine(); // west:    500000.00
			startIndex = nextNumber( s, 8);
			s = s.substring( startIndex);
			double west = new Double( s.trim()).doubleValue();
			setWestEdge( west);
			s = fr.readLine(); // rows:   10
			startIndex = nextNumber( s, 7);
			s = s.substring( startIndex);
			numberRows = new Integer( s.trim()).intValue();
			//setNumberRows( rows);
			s = fr.readLine(); // cols:  15
			startIndex = nextNumber( s, 7);
			s = s.substring( startIndex);
			numberColumns = new Integer( s.trim()).intValue();
			//setNumberColumns( columns);
			setNSResolution( (getNorthEdge() - getSouthEdge()) / numberRows);
			setEWResolution( (getEastEdge() - getWestEdge()) / numberColumns);
			setNoDataValue(null);
			s = fr.readLine();
        } else {
            throw new IllegalArgumentException("fileType does not match supported types.");
		}
		initRLE( numberRows, numberColumns);
		int row = 0;
		int col = 0;
		startIndex = 0;
		int allFlatCells = 0;
		int allRLECells = 0;
		//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") d");
		while( row < getNumberRows()) {
			//System.out.println( "GISClassRLS.GISClassRLE( " + fileName + ") row: " + row);
			if( startIndex >= s.length())  {
				s = fr.readLine();
				startIndex = 0;
			}
			char ch = s.charAt( startIndex);
			while( Character.isWhitespace( ch) == true) {
				startIndex++;
				ch = s.charAt( startIndex);
			}
			endIndex = endNumber( s, startIndex);
			if( endIndex == s.length() - 1)
				endIndex++;
			numberS = s.substring( startIndex, endIndex);
			int newValue = new Integer( numberS.trim()).intValue();
			if( hasNoDataValue() == true && newValue == getNoDataValue()) {
				setNoData( row, col, true);
			} else {
				//setNoData( row, col, false);
				setCellValue( row, col, newValue);
			}
			startIndex = nextNumber( s, endIndex);
			col++;
			if( col == getNumberColumns()) {
				col = 0;
				if( rows[ row].optimizeRow() == true) {
					allRLECells += rows[ row].getLengthRow();
				} else allFlatCells += rows[ row].getLengthRow();
				row++;
			}
		}
		//* debugging prints
		System.out.println("");
		System.out.println( "If flat: [" + (getNumberRows() * getNumberColumns()) + "] cells, this");
		System.out.println( "map has: [" + (allFlatCells + allRLECells) + "] cells for a size ratio of: " + 
			((allRLECells * 3.0 + allFlatCells) / (getNumberRows() * getNumberColumns())));
		//*/

		fr.close();
	}

	/** in alpha testing */
	public int getCellValue( int cellRowNum, int cellColumnNum) {
		return( rows[ cellRowNum].getCellValue( cellColumnNum));
	}

	/** in alpha testing */
	public void setCellValue( int cellRowNum, int cellColumnNum, int value) {
		setNoData( cellRowNum, cellColumnNum, false);
		rows[ cellRowNum].setCellValue( cellColumnNum, value);
		//TODO: optimizeRow() as needed
	}

	public boolean optimizeRow( int row) {
		return rows[ row].optimizeRow();
	}

	public boolean isRunLengthEncoded( int row) {
		return rows[ row].isRunLengthEncoded();
	}

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		GISClassRLE t = new GISClassRLE( "testNoData");
		System.out.println( "testNoData map from orginal read");
		t.printClass();
		t.writeAsciiGrass( "testNoData");
		System.out.println( "testNoData map from new ESRI read");
		t = new GISClassRLE( "testNoData", GISGrid.ESRI);
		t.printClass();
		t = new GISClassRLE( "testNoData", GISGrid.GRASS4);
		System.out.println( "testNoData map from new GRASS4 read");
		t.printClass();
		System.out.println( "testNoDataSpace map from new ESRI read");
		t = new GISClassRLE( "testNoDataSpace", GISGrid.ESRI);
		t.printClass();
/*
		System.out.println( "GISClass file:");
		GISClass grid = new GISClass( "testNoData");
		grid.printClass();
		grid.print();
		System.out.println( "");
		System.out.println( "GISClassRLE file:");
		GISClassRLE gridRLE = new GISClassRLE( "testNoData");
		gridRLE.printClass();
		gridRLE.print();
		gridRLE.writeAsciiGrass( "testNoDataGRASSRLE");
		int min = gridRLE.getMinimumValue();
		int max = gridRLE.getMaximumValue();
		System.out.println( "min: " + min + ", max: " + max);
		grid.setCellValue( 0, 3, 4);
		if( grid.optimizeRow( 0) == true) {
			System.out.println( "r");
		} else System.out.println( "f");
		grid.printClass();
		grid.setCellValue( 0, 2, 4);
		if( grid.optimizeRow( 0) == true) {
			System.out.println( "r");
		} else System.out.println( "f");
		grid.printClass();
		grid.setCellValue( 0, 1, 4);
		if( grid.optimizeRow( 0) == true) {
			System.out.println( "r");
		} else System.out.println( "f");
		grid.printClass();
		grid.setCellValue( 0, 2, 5);
		grid.optimizeRow( 0);
		if( grid.optimizeRow( 0) == true) {
			System.out.println( "r");
		} else System.out.println( "f");
		grid.printClass();
		if( grid.optimizeRow( 0) == true) {
			System.out.println( "r");
		} else System.out.println( "f");
		grid.printClass();
*/
	}
}
