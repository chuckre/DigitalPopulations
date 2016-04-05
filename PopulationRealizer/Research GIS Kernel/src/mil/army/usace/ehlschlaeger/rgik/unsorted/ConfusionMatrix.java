package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISio;
/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class ConfusionMatrix {
	private GISClass onMap, goalMap;
	private GISGrid testGrid;
	private boolean isMade;
	private double[][] matrix;
	private int[] rowSum, colSum;
	private int minOnClass, numOnClasses, minGoalClass, numGoalClasses; 
	private GISio gisIO, gisIOneg;
	private int sumTestCells, typeMatrix;
	static final public int CONFUSIONMATRIX = 0;
	static final public int PERCELLKAPPA = 1;

	public ConfusionMatrix() {
		onMap = goalMap = null;
		testGrid = null;
		isMade = false; 
		gisIO = new GISio( 7);
		gisIOneg = new GISio( 6);
		matrix = null;
		rowSum = colSum = null;
		typeMatrix = CONFUSIONMATRIX;
	}

	public void setTypeMatrix( int type) {
		typeMatrix = type;
	}

	public int getTypeMatrix() {
		return typeMatrix;
	}

	public void checkReady() {
		if( onMap != null && goalMap != null && testGrid != null) {
			if( matrix == null) { 
				setMatrix();
			}
		}
	}

	public void setOnMap( GISClass oMap) {
		onMap = oMap;
		checkReady();
	}
	
	public void setGoalMap( GISClass gMap) {
		goalMap = gMap;
		checkReady();
	}

	public void setTestArea( GISGrid tGrid) {
		testGrid = tGrid;
		checkReady();
	}

	public void setMatrix() {
		//System.out.println( "ConfusionMatrix.setMatrix() making matrix");
		minOnClass = onMap.getMinimumValue();
		numOnClasses = onMap.getMaximumValue() - minOnClass + 1; 
		minGoalClass = goalMap.getMinimumValue();
		numGoalClasses = goalMap.getMaximumValue() - minGoalClass + 1;
		matrix = new double[ numOnClasses][ numGoalClasses];
		rowSum = new int[ numOnClasses];
		colSum = new int[ numGoalClasses];
		for( int r = 0; r < numOnClasses; r++) {
			rowSum[ r] = 0;
			for( int c = 0; c < numGoalClasses; c++) {
				matrix[ r][ c] = 0.0;
			}
		}
		for( int c = numGoalClasses - 1; c >= 0; c--) {
			colSum[ c] = 0;
		}
		sumTestCells = 0;
		for( int r = 0; r < testGrid.getNumberRows(); r++) {
			for( int c = 0; c < testGrid.getNumberColumns(); c++) {
				double easting = testGrid.getCellCenterEasting( r, c);
				double northing = testGrid.getCellCenterNorthing( r, c);
				if( onMap.onMap( easting, northing) && goalMap.onMap(easting, northing)) {
					int onMapRowIndex = onMap.getRowIndex( easting, northing);
					int onMapColumnIndex = onMap.getColumnIndex( easting, northing);
					if( onMap.isNoData( onMapRowIndex, onMapColumnIndex) == false &&
						goalMap.isNoData( easting, northing) == false) {
						int onClass = onMap.getCellValue( onMapRowIndex, onMapColumnIndex);
						int goalClass = goalMap.getCellValue( easting, northing);
/* if( onClass < minOnClass || onClass >= minOnClass + numOnClasses) {
	System.out.println("r:" + onMapRowIndex + " c:" + onMapColumnIndex + 
		", onClass:" + onClass + ", easting:" + easting + ", northing:" + northing);
}
if( goalClass < minGoalClass || goalClass >= minGoalClass + numGoalClasses) {
	System.out.println("r:" + onMapRowIndex + " c:" + onMapColumnIndex + 
		", goalClass:" + goalClass + ", easting:" + easting + ", northing:" + northing);
} */
						rowSum[ onClass - minOnClass]++;
						colSum[ goalClass - minGoalClass]++;
						matrix[onClass - minOnClass][ goalClass - minGoalClass] += 1.0;
						sumTestCells++;
					} 
				}
			}
		}
		isMade = true;
	}

	public double getConfusionValue( int matrixRow, int matrixCol) {
		double value = Double.NaN;
		if( rowSum[ matrixRow] > 0) {
			value = matrix[ matrixRow][ matrixCol] / rowSum[ matrixRow];
		}
		return value;
	}

	public int getNumberMatrixRows() {
		return rowSum.length;
	}

	public int getNumberMatrixColumns() {
		return colSum.length;
	}

	public int getMatrixRowValue( int index) {
		return( index + minOnClass);
	}

	public int getMatrixColumnValue( int index) {
		return( index + minGoalClass);
	}

	public int getMinimumColumnClass() {
		return minGoalClass;
	}

	public int getMinimumRowClass() {
		return minOnClass;
	}

	public double getPerCellKappa( int matrixRow, int matrixCol) {
		double value = Double.NaN;
		if( rowSum[ matrixRow] > 0) {
			value = ((matrix[ matrixRow][ matrixCol] / sumTestCells) - 
				(1.0 * rowSum[ matrixRow] / sumTestCells) * (1.0 * colSum[ matrixCol] / sumTestCells)) / 
				((1.0 * rowSum[ matrixRow] / sumTestCells) - 
				(1.0 * rowSum[ matrixRow] / sumTestCells) * (1.0 * colSum[ matrixCol] / sumTestCells));
		}
		return value;
	}

	/** in alpha testing */
	public void print() {
		if(!isMade == false) {
		    throw new DataException("ConfusionMatrix ERROR: matrix hasn't been set yet");
		}
		if( typeMatrix == PERCELLKAPPA)
			gisIO.printBuffered( "kappa");
		else	gisIO.printBuffered( "cnfus");
		for( int i = 0; i < numGoalClasses; i++) {
			if( colSum[ i] > 0) {
				System.out.print( " ");
				gisIO.printBuffered( gisIO.getIntegerForm().format( (i + minGoalClass)));
			}
		}
		System.out.print( " ");
		gisIO.printBuffered( "OnRatio");
		System.out.println( "");
		for( int cat = 0; cat < numOnClasses; cat++) {
			if( rowSum[ cat] > 0) {
				gisIO.printBuffered( gisIO.getIntegerForm().format( (cat + minOnClass)));
				for( int i = 0; i < numGoalClasses; i++) {
					if( colSum[ i] > 0) {
						System.out.print( " ");
						//double value = calcCell( cat, i);
						double value = Double.NaN;
						if( typeMatrix == PERCELLKAPPA)
							value = getPerCellKappa( cat, i);
						else	value = getConfusionValue( cat, i);
						if( value >= 0.0) {
							gisIO.printBuffered( gisIO.getDecimalForm().format( value));
						} else {
							gisIO.printBuffered( gisIOneg.getDecimalForm().format( value));
						}
					}
				}
				System.out.print( " ");
				gisIO.printBuffered( gisIO.getDecimalForm().format( (1.0 * rowSum[ cat]) / sumTestCells));
				System.out.println();
			}
		}
		gisIO.printBuffered( "GlRatio");
		for( int i = 0; i < numGoalClasses; i++) {
			if( colSum[ i] > 0) {
				System.out.print( " ");
				gisIO.printBuffered( gisIO.getDecimalForm().format( (1.0 * colSum[ i]) / sumTestCells));
			}
		}
		System.out.println();
	}

	/** This method functionally identical to print(), except first arg to printBuffered is PrintWriter. Doesn't work yet.
	 * in alpha testing 
	 * @throws IOException */
	public void print( String fName) throws IOException {
		if(!isMade) {
		    throw new DataException("ConfusionMatrix ERROR: matrix hasn't been set yet");
		}
		PrintWriter out = new PrintWriter(
					new BufferedWriter( new FileWriter( fileName( fName))));
		if( typeMatrix == PERCELLKAPPA)
			gisIO.printBuffered( out, "kappa");
		else	gisIO.printBuffered( out, "cnfus");
		for( int i = 0; i < numGoalClasses; i++) {
			if( colSum[ i] > 0) {
				out.print( " ");
				gisIO.printBuffered(out, gisIO.getIntegerForm().format( (i + minGoalClass)));
			}
		}
		out.print( " ");
		gisIO.printBuffered( out, "OnRatio");
		out.println( "");
		for( int cat = 0; cat < numOnClasses; cat++) {
			if( rowSum[ cat] > 0) {
				gisIO.printBuffered( out, gisIO.getIntegerForm().format( (cat + minOnClass)));
				for( int i = 0; i < numGoalClasses; i++) {
					if( colSum[ i] > 0) {
						out.print( " ");
						double value = Double.NaN;
						if( typeMatrix == PERCELLKAPPA)
							value = getPerCellKappa( cat, i);
						else	value = getConfusionValue( cat, i);
						if( value >= 0.0) {
							gisIO.printBuffered( out, gisIO.getDecimalForm().format( value));
						} else {
							gisIO.printBuffered( out, gisIOneg.getDecimalForm().format( value));
						}
					}
				}
				out.print( " ");
				gisIO.printBuffered( out, gisIO.getDecimalForm().format( (1.0 * rowSum[ cat]) / sumTestCells));
				out.println();
			}
		}
		gisIO.printBuffered( out, "GlRatio");
		for( int i = 0; i < numGoalClasses; i++) {
			if( colSum[ i] > 0) {
				out.print( " ");
				gisIO.printBuffered( out, gisIO.getDecimalForm().format( (1.0 * colSum[ i]) / sumTestCells));
			}
		}
		out.println();
		out.close();
	}

	/** in alpha testing */
	private String fileName( String fN) {
		if( typeMatrix == PERCELLKAPPA)
			return( fN + ".pck");
		return( fN + ".con");
	}

	public static void main( String argv[]) throws IOException {
		if( argv.length < 3) {
			System.out.println( "ConfusionMatrix main program requires three arguments:");
			System.out.println( "java ConfusionMatrix dependentMap IndependentMap testGrid");
			System.exit( -1);
		}
		System.out.println( "ConfusionMatrix WARNING: this version will treat 0 values as no data");
		String dMap = argv[ 0];
		String iMap = argv[ 1];
		String tMap = argv[ 2];
		GISClass on_map = GISClass.loadEsriAscii( dMap);
		on_map.setCellValueToNoData( 0);
		GISClass goal_map = GISClass.loadEsriAscii( iMap);
		goal_map.setCellValueToNoData( 0);
		GISGrid testArea = GISClass.loadEsriAscii( tMap);
		ConfusionMatrix cm = new ConfusionMatrix();
		cm.setOnMap( on_map);
		cm.setGoalMap( goal_map);
		cm.setTestArea( testArea);
		System.out.println( "CONFUSIONMATRIX Dependent Map (rows): " + dMap + iMap + ", Independent Map (columns): ");
		cm.print( );
		cm.print( dMap);
		cm.setTypeMatrix( ConfusionMatrix.PERCELLKAPPA);
		System.out.println( "PERCELLKAPPA Dependent Map (rows): " + dMap + iMap + ", Independent Map (columns): ");
		cm.print( );
		cm.print( dMap);
	}
}