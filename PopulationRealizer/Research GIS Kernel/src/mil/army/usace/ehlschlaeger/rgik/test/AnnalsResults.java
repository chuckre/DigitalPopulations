package mil.army.usace.ehlschlaeger.rgik.test;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  @version 0.3
 */

	/** in alpha testing */
public class AnnalsResults {
	double 	aveError;
	double 	aveAbsError, minError, maxError;
	double 	sd;
	int		count;
	int		grossCount;
    double  stdDev;

	public AnnalsResults( GISLattice quality, GISLattice testing) {
		if( quality.getNumberRows() != testing.getNumberRows() ||
			quality.getNumberColumns() != testing.getNumberColumns() ||
			quality.getEWResolution() != testing.getEWResolution() ||
			quality.getEWResolution() != testing.getEWResolution()) {
			throw new DataException( "maps do not align");
		}
		count = 0;
		grossCount = 0;
		double oriSum = 0.0;
		double oriAbsSum = 0.0;
		minError = Double.POSITIVE_INFINITY;
		maxError = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && testing.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c) - testing.getCellValue( r, c);
					count++;
					oriSum += value;
					if( value >= 0.0) {
						oriAbsSum += value;
						if( value >= 50.0)
							grossCount++;
					} else	{
						oriAbsSum -= value;
						if( value <= -50.0)
							grossCount++;
					}
					if( minError > value)
						minError = value;
					if( maxError < value)
						maxError = value;
				}
			}
		}
		if( count < 2) {
		    throw new DataException( "Data has no overlap or not enough data [" + count + "]");
		}
		aveError = oriSum / count;
		aveAbsError = oriAbsSum / count;

		double sd = 0.0;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && testing.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c) - testing.getCellValue( r, c);
					sd += (value - aveError) * (value - aveError);
				}
			}
		}
		sd /= count - 1;
		sd = Math.sqrt( sd);
		stdDev = sd;
	}

	public void print(String title) {
	    System.out.println(title);
        System.out.println( "Average Error: " + aveError);
        System.out.println( "Average Absolute Error: " + aveAbsError);
        System.out.println( "Range of Error: " + minError + " to " + maxError);
        System.out.println( "Gross errors: " + grossCount + " of " + count + " cells: " + 
            (1.0 * grossCount / count));
        System.out.println( "The standard deviation of data's error is: " + stdDev);
        System.out.println();
	}
	
	public double getAverageError() {
		return aveError;
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		System.out.println( "running AnnalsResults");
		// The next two maps should have the same extents and resolutions!!!!!!!!
		GISLattice g = GISLattice.loadEsriAscii("study_dem");
		GISLattice d = GISLattice.loadEsriAscii("study_dted");
		if(!g.equalsGrid(d))
		    throw new DataException("Lattices don't have identical grids.");

		AnnalsResults dtedResults = new AnnalsResults( g, d);
		dtedResults.print("dted results");
		
		double aveError = dtedResults.getAverageError();
		GISLattice dPlusError = GISLattice.loadEsriAscii("study_dted");
		for( int r = 0; r < d.getNumberRows(); r++) {
			for( int c = 0; c < dPlusError.getNumberColumns(); c++) {
				if( dPlusError.isNoData( r, c) == false) {
					dPlusError.setCellValue( r, c, 
						dPlusError.getCellValue( r, c) + aveError);
				}
			}
		}

		AnnalsResults meanResults = new AnnalsResults( g, dPlusError);
		meanResults.print("Mean-difference results");
		
		// trend27 was the best trend-differenced map of previous analysis.
		GISLattice trend27 = GISLattice.loadEsriAscii("trend27");

		AnnalsResults trendResults = new AnnalsResults( g, trend27);
		trendResults.print("Trend-difference results");
		GISLattice rs18 = GISLattice.loadEsriAscii("RubberSheetTest18");

		AnnalsResults rsResults = new AnnalsResults( g, rs18);
		rsResults.print("3D RubberSheet results");
		
		for( int i = 0; i < 2; i++) { //30
			String name = "RubberSheetTest" + i;
			GISLattice rs = GISLattice.loadEsriAscii(name);
			AnnalsResults results = new AnnalsResults( g, rs);
			results.print("3D RubberSheet results map " + i);
		}
	}
}