package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.SemiVariogram;

/**
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class AnnalsResults200205 {
	GISLattice	quality, testing;
	double 	aveError;
	double 	aveAbsError, minError, maxError;
	double 	sd;
	int		count;
	int		grossCount;

	public AnnalsResults200205( GISLattice quality, GISLattice testing, double maxDistance, String svName) throws IOException {
		this.quality = quality;
		this.testing = testing;
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
		System.out.println( "Average Error: " + aveError);
		System.out.println( "Average Absolute Error: " + aveAbsError);
		System.out.println( "Range of Error: " + minError + " to " + maxError);
		System.out.println( "Gross errors: " + grossCount + " of " + count + " cells: " + 
			(1.0 * grossCount / count));
		sd = 0.0;
		GISLattice errorMap = new GISLattice( quality);
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && testing.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c) - testing.getCellValue( r, c);
					errorMap.setCellValue( r, c, value);
					sd += (value - aveError) * (value - aveError);
				}
			}
		}
		sd /= count - 1;
		sd = Math.sqrt( sd);
		System.out.println( "The standard deviation of data's error is: " + sd);
		SemiVariogram sv = new SemiVariogram( 0.1 * maxDistance, maxDistance, errorMap);
		sv.print(System.out);
		/*
		VaryingStandardDeviationErrorModel vsdem = new VaryingStandardDeviationErrorModel();
		vsdem.setGeneralizedData( gen);
		vsdem.setHigherQualityData( qual);
		*/
	}

	public GISLattice getRawSDMap() {
		GISLattice sdMap = new GISLattice( quality);
		double minSD = Double.POSITIVE_INFINITY;
		double maxSD = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && testing.isNoData( r, c) == false) {
					double testingValue = testing.getCellValue( r, c);
					double qualityValue = quality.getCellValue( r, c);
					double dif = qualityValue - testingValue;
					double sdValue = dif / sd;
					if( minSD > sdValue)
						minSD = sdValue;
					if( maxSD < sdValue)
						maxSD = sdValue;
					sdMap.setCellValue( r, c, sdValue);
				}
			}
		}
		System.out.println( "AnnalsResults200205.getRawSDMap() minimum SD: " + minSD);
		System.out.println( "AnnalsResults200205.getRawSDMap() maximum SD: " + maxSD);
		return sdMap;
	}

	public GISLattice getSDMapAveErrorAdjusted() {
		GISLattice sdMap = new GISLattice( quality);
		double minSD = Double.POSITIVE_INFINITY;
		double maxSD = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && testing.isNoData( r, c) == false) {
					double testingValue = testing.getCellValue( r, c);
					double qualityValue = quality.getCellValue( r, c);
					double dif = qualityValue - (testingValue + aveError);
					double sdValue = dif / sd;
					if( minSD > sdValue)
						minSD = sdValue;
					if( maxSD < sdValue)
						maxSD = sdValue;
					sdMap.setCellValue( r, c, sdValue);
				}
			}
		}
		System.out.println( "AnnalsResults200205.getSDMapAveErrorAdjusted() minimum SD: " + minSD);
		System.out.println( "AnnalsResults200205.getSDMapAveErrorAdjusted() maximum SD: " + maxSD);
		return sdMap;
	}

	public double getAverageError() {
		return aveError;
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		System.out.println( "running AnnalsResults200205");
		double maxDist = 1800.0;
		GISLattice qual = GISLattice.loadEsriAscii("study_dem");
		GISLattice genTrendBest = GISLattice.loadEsriAscii("Trend27");
		System.out.println( "");
		System.out.println( "Working on best trend map (trend27).");
		//AnnalsResults200205 trendResults = 
		new AnnalsResults200205( 
			qual, genTrendBest, maxDist, "trend27error");
		for( int i = 0; i < 30; i++) {
			System.out.println( "");
			System.out.println( "Working on realization [" + i + "]");
			GISLattice gen = GISLattice.loadEsriAscii("RubberSheetTest" + i);
			//AnnalsResults200205 dtedResults = 
			new AnnalsResults200205( 
				qual, gen, maxDist, "errorMap" + i);
			//GISLattice sd = dtedResults.getRawSDMap();
			//sd.writeAsciiEsri( "sdMap" + i);
		}
		/*
		GISLattice sd = new GISLattice( "sdMap0");
		GISLattice low = new GISLattice( sd);
		sd = null;
		GISLattice hih = new GISLattice( low);
		for( int r = low.getNumberRows() - 1; r >= 0; r--) {
			for( int c = low.getNumberColumns() - 1; c >= 0; c--) {
				low.setCellValue( r, c, Double.POSITIVE_INFINITY);
				hih.setCellValue( r, c, Double.NEGATIVE_INFINITY);
			}
		}
		for( int i = 0; i < 30; i++) {
			GISLattice sample = new GISLattice( "sdMap" + i);
			for( int r = low.getNumberRows() - 1; r >= 0; r--) {
				for( int c = low.getNumberColumns() - 1; c >= 0; c--) {
					if( sample.isNoData( r, c) == false) {
						double value = sample.getCellValue( r, c);
						if( value < low.getCellValue( r, c)) {
							low.setCellValue( r, c, value);
						}
						if( value > hih.getCellValue( r, c)) {
							hih.setCellValue( r, c, value);
						}
					}
				}
			}
		}
		for( int r = low.getNumberRows() - 1; r >= 0; r--) {
			for( int c = low.getNumberColumns() - 1; c >= 0; c--) {
				if( low.getCellValue( r, c) == Double.POSITIVE_INFINITY) {
					low.setNoData( r, c, true);
				}
				if( hih.getCellValue( r, c) == Double.NEGATIVE_INFINITY) {
					hih.setNoData( r, c, true);
				}
			}
		}
		low.writeAsciiEsri( "sdLowMap");
		hih.writeAsciiEsri( "sdHihMap");
		*/
		System.exit( 0);
		/*
		System.out.println( "Pick map of generalized surface data");
		GISLattice gen = new GISLattice();
		System.out.println( "Pick map of application quality surface data");
		GISLattice qual = new GISLattice();
		AnnalsResults200205 dtedResults = new AnnalsResults200205( qual, gen);
		GISLattice sd = dtedResults.getRawSDMap();
		sd.writeAsciiEsri( "sdMap");
		System.exit( 0);

		System.out.println( "running AnnalsResults200205");
		// The next two maps should have the same extents and resolutions!!!!!!!!
		GISLattice g = new GISLattice( "study_dem");
		GISLattice d = new GISLattice( "study_dted");
		System.out.println( "dted results");
		AnnalsResults200205 dtedResults = new AnnalsResults200205( g, d);
		double aveError = dtedResults.getAverageError();
		GISLattice sd = dtedResults.getRawSDMap();
		sd.writeAsciiEsri( "dtedRawSD");
		GISLattice sdAveError = dtedResults.getSDMapAveErrorAdjusted();
		sdAveError.writeAsciiEsri( "dtedErrorAdjustedSD");
		System.out.println( "Mean-difference results");
		AnnalsResults200205 meanResults = new AnnalsResults200205( g, dPlusError);
		*/
	}
}