package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Graphics;
import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.io.ESRI_ASCII;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
import mil.army.usace.ehlschlaeger.rgik.util.MyReader;



/**
 * Like GISClass, but only keeps one row in memory. Use only for very large
 * files. Can only read in forward direction.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISClassReadRowByRow extends GISClass {
    private int            rowArray[], fileTypeOpened;
    private int            rowNumber;
    private MyReader       fr;
    private String         fName, s;

	/** parameters define metadata for new GISClassReadRowByRow. */
//should use GISClassReadRowByRow.GISClassReadRowByRow( String fileName) as constructor
//	public GISClassReadRowByRow(double westEdge, double northEdge,
//			double EWResolution, double NSResolution, int numRows, int numCols) {
//		super(westEdge, northEdge, EWResolution, NSResolution, 1, 1);
//		initRbR(numRows, numCols);
//	}

	/** grid metadata for new GISClassReadRowByRow. */
//should use GISClassReadRowByRow.GISClassReadRowByRow( String fileName) as constructor
//	public GISClassReadRowByRow(GISGrid grid) {
//		super(grid.getWestEdge(), grid.getNorthEdge(), grid.getEWResolution(),
//				grid.getNSResolution(), 1, 1);
//		initRbR(grid.getNumberRows(), grid.getNumberColumns());
//	}

	/**
	 * This constructor resamples data from data map to grid metadata.
	 * Currently, the aggregation method isn't implemented
	 */
	public GISClassReadRowByRow(GISGrid grid, GISClass data) {
		super(grid.getWestEdge(), grid.getNorthEdge(), grid.getEWResolution(),
				grid.getNSResolution(), 1, 1);
		initRbR(grid.getNumberRows(), grid.getNumberColumns());
		double gridRes = Math.min(grid.getEWResolution(), grid
				.getNSResolution());
		double dataRes = Math.min(data.getEWResolution(), data
				.getNSResolution());
		if (gridRes > dataRes) {
	        throw new RuntimeException("GISClassReadRowByRow constructor not finished yet");
		}
	}

	/**
	 * to be deprecated in favor of GISClassReadRowByRow.GISClassReadRowByRow(
	 * String fileName, int fileType)
	 */
	/** fileName esri .ASC file without the ".ASC" extension. 
	 * @throws IOException */
	public GISClassReadRowByRow(String fileName) throws IOException {
		super(0.0, 0.0, 1.0, 1.0, 1, 1);
		int startIndex;
		fName = fileName;
		// System.out.println(
		// "GISClassReadRowByRow.GISClassReadRowByRow(String fileName) shouldn't be here.");
		fr = new MyReader(ESRI_ASCII.findFile(fName));
		// System.out.println( "GISClassRLS.GISClassReadRowByRow( " + fName +
		// ") a");
		s = fr.readLine();      // ncols 10
		startIndex = nextNumber(s, 4);
		s = s.substring(startIndex);
		int numberColumns = new Integer(s.trim()).intValue();
		s = fr.readLine();      // nrows 15
		startIndex = nextNumber(s, 4);
		s = s.substring(startIndex);
		int numberRows = new Integer(s.trim()).intValue();
		s = fr.readLine();      // xllcorner 100000
		startIndex = nextNumber(s, 8);
		s = s.substring(startIndex);
		double west = new Double(s.trim()).doubleValue();
		setWestEdge(west);
		s = fr.readLine();      // yllcorner 300000
		startIndex = nextNumber(s, 8);
		s = s.substring(startIndex);
		double sEdge = new Double(s.trim()).doubleValue();
		setSouthEdge(sEdge);
		s = fr.readLine();      // cellsize 100
		startIndex = nextNumber(s, 7);
		s = s.substring(startIndex);
		double Res = new Double(s.trim()).doubleValue();
		double north = sEdge + Res * numberRows;
		setNorthEdge(north);
		setNSResolution(Res);
		setEWResolution(Res);
		initRbR(numberRows, numberColumns);
		s = fr.readLine();      // NODATA_value -9999 (opt).
		char ch = s.charAt(0);
		if (ch == 'N' || ch == 'n') {
			// System.out.println("no data == true");
			startIndex = nextNumber(s, 11);
			s = s.substring(startIndex);
			setNoDataValue(new Integer(s.trim()));
			getAnotherRow();
		} else {
			// System.out.println("no data == false, cols:" + getNumberColumns()
			// + ", rowArray.length: " + rowArray.length);
		    setNoDataValue(null);
			startIndex = 0;
			rowNumber = 0;
			processColumns();
		}
		// System.out.println("GISClassReadRowByRow.GISClassReadRowByRow() post no data check");
		setEastEdge(west + Res * getNumberColumns());
		setNSResolution(Res);
		setEWResolution(Res);
		// System.out.println( "GISClassRLS.GISClassReadRowByRow( " + fName +
		// ") b");
		setMainArray(null);
	}

	private void initRbR(int numberRows, int numberColumns) {
		// System.out.println( "GISClassReadRowByRow.initRbR() starting");
		setMainArray(null);
		// System.out.println( "n:" + getNorthEdge() + ", rows:" + numberRows +
		// ", res:" + getNSResolution());
		setNumberRows(numberRows);
		setNumberColumns(numberColumns);
		setSouthEdge(getNorthEdge() - numberRows * getNSResolution());
		setEastEdge(getWestEdge() + numberColumns * getEWResolution());
		rowArray = new int[getNumberColumns()];
		rowNumber = -1;
		setMinimumChange(true);
		setMaximumChange(true);
		// System.out.println( "GISClassReadRowByRow.initRbR() finished");
	}

	/**
	 * Currently, the GRASS 5.0 ASCII files are not supported.
	 * 
	 * @param fileName
	 *            ASCII file containing grid metadata without the extension.
	 * @param fileType
	 *            if GISGrid.ESRI is passed as the argument, then the file is an
	 *            ESRI ASCII (*.asc) file. If GISGrid.GRASS4 is passed as the
	 *            argument, then the file is the ASCII output from GRASS (or
	 *            Idrisi's GRASSIDR). The GRASS ASCII file must have a .gra
	 *            extension.
	 * @throws IOException 
	 */
	public GISClassReadRowByRow(String fileName, int fileType) throws IOException {
		super(0.0, 0.0, 1.0, 1.0, 2, 2);
		int startIndex = 0;
		int numberRows = 1, numberColumns = 1;
		fileTypeOpened = fileType;
		fName = fileName;
		s = "";
		String totalFileName = ESRI_ASCII.findFile(fileName).getAbsolutePath();
		if (fileType == GISGrid.GRASS4) {
			totalFileName = grassFileName(fileName);
		}
		// System.out.println(
		// "GISClassReadRowByRow.GISClassReadRowByRow(String fileName, int fileType) attempting to open ["
		// + totalFileName + "]");
		// System.out.println( "fileType:" + fileType);
		MyReader fr = new MyReader(totalFileName);
		if (fileType == GISGrid.ESRI) {
		    s = fr.readLine();      // ncols 10
			startIndex = nextNumber(s, 4);
			s = s.substring(startIndex);
			numberColumns = new Integer(s.trim()).intValue();
			s = fr.readLine();      // nrows 15
			startIndex = nextNumber(s, 4);
			s = s.substring(startIndex);
			numberRows = new Integer(s.trim()).intValue();
			s = fr.readLine();      // xllcorner 100000
			startIndex = nextNumber(s, 8);
			s = s.substring(startIndex);
			double west = new Double(s.trim()).doubleValue();
			setWestEdge(west);
			s = fr.readLine();      // yllcorner 300000
			startIndex = nextNumber(s, 8);
			s = s.substring(startIndex);
			double sEdge = new Double(s.trim()).doubleValue();
			setSouthEdge(sEdge);
			s = fr.readLine();      // cellsize 100
			startIndex = nextNumber(s, 7);
			s = s.substring(startIndex);
			double Res = new Double(s.trim()).doubleValue();
			double north = sEdge + Res * numberRows;
			setNorthEdge(north);
			setEastEdge(west + Res * numberColumns);
			setNSResolution(Res);
			setEWResolution(Res);
			initRbR(numberRows, numberColumns);
			// System.out.println(
			// "GISClassReadRowByRow.GISClassReadRowByRow( String, int): line to check for no data");
			s = fr.readLine();      // NODATA_value -9999 (opt).
			// System.out.println( s);
			char ch = s.charAt(0);
			if (ch == 'N' || ch == 'n') {
				startIndex = nextNumber(s, 11);
				s = s.substring(startIndex);
				setNoDataValue(new Integer(s.trim()));
				// System.out.println( "reading next line with row number at: "
				// + rowNumber);
				// s = readLineNoNull( br, totalFileName);
				getAnotherRow();
				// System.out.println( s);
			} else {
	            setNoDataValue(null);
				rowNumber++;
				processColumns();
			}
		} else if (fileType == GISGrid.GRASS4) {
			// The GRASS5 header now can have entries: multiplier, null, and
			// type. If type is not set in the header, the new
			// map is created integer if all of the values in the input file are
			// integer, and float otherwise.
		    s = fr.readLine();      // north: 4299000.00
			startIndex = nextNumber(s, 4);
			s = s.substring(startIndex);
			setNorthEdge(new Double(s.trim()).doubleValue());
			s = fr.readLine();      // south: 4247000.00
			startIndex = nextNumber(s, 4);
			s = s.substring(startIndex);
			setSouthEdge(new Double(s.trim()).doubleValue());
			s = fr.readLine();      // east: 528000.00
			startIndex = nextNumber(s, 8);
			s = s.substring(startIndex);
			double east = new Double(s.trim()).doubleValue();
			setEastEdge(east);
			s = fr.readLine();      // west: 500000.00
			startIndex = nextNumber(s, 8);
			s = s.substring(startIndex);
			double west = new Double(s.trim()).doubleValue();
			setWestEdge(west);
			s = fr.readLine();      // rows: 10
			startIndex = nextNumber(s, 7);
			s = s.substring(startIndex);
			numberRows = new Integer(s.trim()).intValue();
			// setNumberRows( rows);
			s = fr.readLine();      // cols: 15
			startIndex = nextNumber(s, 7);
			s = s.substring(startIndex);
			numberColumns = new Integer(s.trim()).intValue();
			// setNumberColumns( columns);
			setNSResolution((getNorthEdge() - getSouthEdge()) / numberRows);
			setEWResolution((getEastEdge() - getWestEdge()) / numberColumns);
            setNoDataValue(null);
			s = fr.readLine();
			initRbR(numberRows, numberColumns);
        } else {
            throw new IllegalArgumentException("fileType does not match supported types.");
		}
		setMainArray(null);
		System.out
				.println("GISClassReadRowByRow.GISClassReadRowByRow(String fileName, int fileType) header complete.");
	}

	private void getAnotherRow() {
		// System.out.println( "GISClassReadRowByRow.getAnotherRow() row:" +
		// (rowNumber + 1));
		rowNumber++;
		try {
		    s = fr.readLine();
            processColumns();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	private void processColumns() throws IOException {
		// System.out.println( "map: " + totalFileName + ", row: " + rowNumber);
		int col = 0;
		int startIndex = 0;
		char ch = s.charAt(startIndex);
		while (Character.isWhitespace(ch) == true) {
			startIndex++;
			ch = s.charAt(startIndex);
		}
		while (col < getNumberColumns()) {
			int endIndex = endNumber(s, startIndex);
			if (startIndex >= s.length()) {
			    s = fr.readLine();
				startIndex = 0;
				ch = s.charAt(startIndex);
				while (Character.isWhitespace(ch) == true) {
					startIndex++;
					ch = s.charAt(startIndex);
				}
				endIndex = endNumber(s, startIndex);
			}
			if (endIndex == s.length() - 1)
				endIndex++;
			// System.out.println( "sI:" + startIndex + ", eI:" + endIndex);
			String numberS = s.substring(startIndex, endIndex);
			rowArray[col] = new Integer(numberS.trim()).intValue();
			if (hasNoDataValue() == true
					&& rowArray[col] == getNoDataValue())
				setNoData(rowNumber, col, true);
			else
				setNoData(rowNumber, col, false);
			startIndex = nextNumber(s, endIndex);
			col++;
		}
		if (rowNumber == getNumberRows() - 1) {
			fr.close();
			// System.out.println(
			// "GISClassReadRowByRow.processColumns() closing " +
			// totalFileName);
			rowNumber = getNumberRows();
		}
	}

	/**
     * in alpha testing
     * 
     * @throws IOException
     */
    public boolean isNoData(int row, int col) {
        if (onMap(row, col) == false)
            return false;
        while (rowNumber < row) {
            getAnotherRow();
        }
        return (super.isNoData(row, col));
    }

	protected void findMinMax() {
		System.out.println("GISClassReadRowByRow.setMinMax() starting");
		/*
		 * GISClassReadRowByRow newMap = new GISClassReadRowByRow( fName,
		 * fileTypeOpened); int minValue = 10; int maxValue = 5; for( int r = 0;
		 * r < newMap.getNumberRows(); r++) { for( int c = 0; c <
		 * newMap.getNumberColumns(); c++) { if( ! newMap.isNoData( r, c)) { if(
		 * minValue > maxValue) { minValue = maxValue = newMap.getCellValue( r,
		 * c); } else { minValue = Math.min( minValue, newMap.getCellValue( r,
		 * c)); maxValue = Math.max( maxValue, newMap.getCellValue( r, c)); } }
		 * } } newMap = null; //System.out.println( "min: " + minValue +
		 * ", max: " + maxValue); setMinimumValue( minValue); setMaximumValue(
		 * maxValue);
		 */
		GISClassReadRowByRow newMap;
		try {
            newMap = new GISClassReadRowByRow(fName, fileTypeOpened);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		newMap.printGrid();
		boolean someValues = false;
		int minValue = getNoDataValue();
		int maxValue = getNoDataValue();
		for (int r = 0; r < newMap.getNumberRows(); r++) {
			for (int c = 0; c < newMap.getNumberColumns(); c++) {
				if (newMap.isNoData(r, c) == false) {
					if (someValues == false) {
						minValue = maxValue = newMap.getCellValue(r, c);
						someValues = true;
					} else {
						minValue = Math
								.min(minValue, newMap.getCellValue(r, c));
						maxValue = Math
								.max(maxValue, newMap.getCellValue(r, c));
					}
					// System.out.print( newMap.getCellValue( r, c) + " ");
				} else {
					// System.out.print( "ND ");
				}
			}
			// System.out.println("");
		}
		setMinimumValue(minValue);
		setMaximumValue(maxValue);
		System.out.println("GISClassReadRowByRow.setMinMax() finishing");
	}

	/**
     * not implemented
	 */
	public BooleanGrid catBooleanMap(int catNum) {
        throw new RuntimeException("Not implemented.");
//		BooleanGrid cat = new BooleanGrid(getNumberRows(), getNumberColumns(),
//				false);
//		for (int r = getNumberRows() - 1; r >= 0; r--) {
//			for (int c = getNumberColumns() - 1; c >= 0; c--) {
//				if (!isNoData(r, c) && getCellValue(r, c) == catNum)
//					cat.setBoolean(r, c, true);
//			}
//		}
//		return (cat);
	}

	/**
     * not implemented
	 */
	public void paint(Graphics g) {
        throw new RuntimeException("Not implemented.");
	}

	/**
	 * Fetch the value in a cell.
	 * Will read forward to required row.
	 * 
	 * @throws IllegalStateException on an attempt to read a row already passed.
	 */
	public int getCellValue(int cellRowNum, int cellColumnNum) {
        if(cellRowNum < 0 || cellRowNum >= getNumberRows())
            throw new IndexOutOfBoundsException("cellRowNum="+cellRowNum+", must be in [0,"+getNumberRows()+")");
        if(rowNumber > cellRowNum)
            throw new IllegalStateException("Row "+cellRowNum+" requested but we're at "+rowNumber+" and cannot back up.");
		while (rowNumber < cellRowNum) {
			getAnotherRow();
		}
		return (rowArray[cellColumnNum]);
	}

	/**
     * not implemented
	 */
	public void setCellValue(int cellRowNum, int cellColumnNum, int value) {
	    throw new IllegalStateException("GISClassReadRowByRow is not writable");
	}

	public static void main(String argv[]) throws IOException {
		System.out.println("testNoData2");
		GISClassReadRowByRow tt = new GISClassReadRowByRow("testNoData2",
				GISGrid.ESRI);
		System.out.println("testNoData2 post constructor");
		tt.print();
		for (int r = 0; r < tt.getNumberRows(); r++) {
			for (int c = 0; c < tt.getNumberColumns(); c++) {
				if (tt.isNoData(r, c) == true)
					System.out.print("ND ");
				else {
					int v = tt.getCellValue(r, c);
					System.out.print(v + " ");
				}
			}
			System.out.println("");
		}
		System.out.println("testAllData2");
		tt = new GISClassReadRowByRow("testAllData2", GISGrid.ESRI);
		System.out.println("testAllData2 post constructor");
		tt.print();
		for (int r = 0; r < tt.getNumberRows(); r++) {
			for (int c = 0; c < tt.getNumberColumns(); c++) {
				if (tt.isNoData(r, c) == true)
					System.out.print("ND ");
				else {
					int v = tt.getCellValue(r, c);
					System.out.print(v + " ");
				}
			}
			System.out.println("");
		}
		System.out.println("Copy70clust");
		GISClassReadRowByRow t = new GISClassReadRowByRow("Copy70clust",
				GISGrid.ESRI);
		GISClassReadRowByRow t2 = new GISClassReadRowByRow("70clust",
				GISGrid.ESRI);
		System.out.println("Copy70clust post constructor");
		t.print();
		System.out.println("min: " + t.getMinimumValue());
		System.out.println("max: " + t.getMaximumValue());
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				if (t.isNoData(r, c) == false) {
					int v = t.getCellValue(r, c);
					if (t2.isNoData(r, c) == true && v != 1) {
					    throw new DataException("t should == t2, but t2 cell is no-data");
					}
					int v2 = t2.getCellValue(r, c);
					if (v != v2 && v != 1 && v2 != 1) {
                        throw new DataException("t should == t2. t=" + v + ", t2=" + v2);
					}
				}
				// System.out.print( v + " ");
			}
			// System.out.println("");
		}
		System.out.println("70clust");
		t = new GISClassReadRowByRow("70clust", GISGrid.ESRI);
		System.out.println("70clust post constructor");
		t.print();
//		for (int r = 0; r < t.getNumberRows(); r++) {
//			for (int c = 0; c < t.getNumberColumns(); c++) {
//				int v = t.getCellValue(r, c);
//				 System.out.print( v + " ");
//			}
//			 System.out.println("");
//		}
		System.out.println("testNoData");
		t = new GISClassReadRowByRow("testNoData", GISGrid.ESRI);
		System.out.println("testNoData post constructor");
		t.print();
		System.out.println("min: " + t.getMinimumValue());
		System.out.println("max: " + t.getMaximumValue());
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				int v = t.getCellValue(r, c);
				System.out.print(v + " ");
			}
			System.out.println("");
		}
		System.out.println("testNoData2");
		t = new GISClassReadRowByRow("testNoData2", GISGrid.ESRI);
		System.out.println("testNoData2 post constructor");
		t.print();
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				int v = t.getCellValue(r, c);
				System.out.print(v + " ");
			}
			System.out.println("");
		}
		System.out.println("testWithoutNoData");
		t = new GISClassReadRowByRow("testWithoutNoData");
		System.out.println("testWithoutNoData post constructor");
		t.print();
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				int v = t.getCellValue(r, c);
				System.out.print(v + " ");
			}
			System.out.println("");
		}
		System.out.println("testWithoutNoData2");
		t = new GISClassReadRowByRow("testWithoutNoData2");
		System.out.println("testWithoutNoData2 post constructor");
		t.print();
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				int v = t.getCellValue(r, c);
				System.out.print(v + " ");
			}
			System.out.println("");
		}
		System.out.println("testNoDataSpace map from new ESRI read");
		t = new GISClassReadRowByRow("testNoDataSpace", GISGrid.ESRI);
		t.print();
		for (int r = 0; r < t.getNumberRows(); r++) {
			for (int c = 0; c < t.getNumberColumns(); c++) {
				int v = t.getCellValue(r, c);
				System.out.print(v + " ");
			}
			System.out.println("");
		}
	}
}
