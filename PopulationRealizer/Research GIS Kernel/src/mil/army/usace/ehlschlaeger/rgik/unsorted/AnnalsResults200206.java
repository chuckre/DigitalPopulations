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
public class AnnalsResults200206 {
	GISLattice	quality, general;
	double 	aveError;
	double 	aveAbsError, minError, maxError;
	double 	sd;
	int		count;
	int		grossCount;

	public double pointDistributionProportion( double precision, double mean, 
			double standardDeviation, double actualValue) {
		// 1/8 * (actualValue + precision)
		double result = pDPEquation( actualValue + precision, mean, standardDeviation) / 8.0;
		// 1/8 * (actualValue - precision)
		result += pDPEquation( actualValue - precision, mean, standardDeviation) / 8.0;
		// 1/4 * (actualValue + (precision/2))
		result += .25 * pDPEquation( actualValue + (precision / 2.0), mean, standardDeviation);
		// 1/4 * (actualValue - (precision/2))
		result += .25 * pDPEquation( actualValue - (precision / 2.0), mean, standardDeviation);
		// 1/4 * (actualValue)
		result += .25 * pDPEquation( actualValue, mean, standardDeviation);
		return( result);
	}

	private double pDPEquation( double j, double mean, double standardDeviation) {
		// j is actualValue +- precision
		double sdSquared = standardDeviation * standardDeviation;
		// (j - mean)^2 / (2 * sd^2)
		double result = (j - mean) * (j - mean) / (2.0 * sdSquared);
		// e^(-above)
		result = Math.pow( Math.E, - result);
		// above / (sqrt( 2 * PI * sd^2))
		result /= Math.sqrt( 2.0 * Math.PI * sdSquared);
		return( result);
	}

	public AnnalsResults200206( GISLattice quality, GISLattice general, 
			double maxDistance, String svName, 
			String proportionMapName, double precision,
			String adjustedMeanMap,
			String sdMap) throws IOException {
		this.quality = quality;
		this.general = general;
		if( quality.getNumberRows() != general.getNumberRows() ||
			quality.getNumberColumns() != general.getNumberColumns() ||
			quality.getEWResolution() != general.getEWResolution() ||
			quality.getEWResolution() != general.getEWResolution()) {
		    throw new DataException( "maps do not align");
		}
		VaryingStandardDeviationErrorModel vsdem = new VaryingStandardDeviationErrorModel();
		vsdem.setGeneralizedData( general);
		vsdem.setHigherQualityData( quality);
		double minV = vsdem.getMinimumDataValue();
		double maxV = vsdem.getMaximumDataValue();
		double mInt= vsdem.getMeanIntercept();
		double mSlope = vsdem.getMeanSlope();
		double sdInt = vsdem.getStandardDeviationIntercept();
		double sdSlope = vsdem.getStandardDeviationSlope();
		System.out.println( "For the maps you choose, the VaryingStandardDeviationErrorModel variables are:");
		System.out.println( "Minimum Data Value:           " + minV);
		System.out.println( "Maximum Data Value:           " + maxV);
		System.out.println( "Mean Intercept:               " + mInt);
		System.out.println( "Mean Slope:                   " + mSlope);
		System.out.println( "Standard Deviation Intercept: " + sdInt);
		System.out.println( "Standard Deviation Slope:     " + sdSlope);
		GISLattice meanAdjustedGen = new GISLattice( general);
		for( int r = general.getNumberRows() - 1; r >= 0; r--) {
			for( int c = general.getNumberColumns() - 1; c >= 0; c--) {
				if( general.isNoData( r, c) == false) {
					double genValue = general.getCellValue( r, c);
					double meanAdjGenValue = genValue + vsdem.getMeanAdjustmentAt( genValue);
					meanAdjustedGen.setCellValue( r, c, meanAdjGenValue);
				}
			}
		}
		meanAdjustedGen.writeAsciiEsri( adjustedMeanMap);
		meanAdjustedGen = null;
		GISLattice sdAdjustedGen = new GISLattice( general);
		double minSD = Double.POSITIVE_INFINITY;
		double maxSD = Double.NEGATIVE_INFINITY;
		for( int r = general.getNumberRows() - 1; r >= 0; r--) {
			for( int c = general.getNumberColumns() - 1; c >= 0; c--) {
				if( general.isNoData( r, c) == false) {
					double genValue = general.getCellValue( r, c);
					double meanAdjGenValue = genValue + vsdem.getMeanAdjustmentAt( genValue);
					double sdValue = vsdem.getStandardDeviationErrorAt( meanAdjGenValue);
					sdAdjustedGen.setCellValue( r, c, sdValue);
					if( sdValue < minSD)
						minSD = sdValue;
					if( sdValue > maxSD)
						maxSD = sdValue;					
				}
			}
		}
		System.out.println( "Minimum Standard Deviation Value: " + minSD);
		System.out.println( "Maximum Standard Deviation Value: " + maxSD);
		sdAdjustedGen.writeAsciiEsri( sdMap);
		sdAdjustedGen = null;
		GISLattice propMap = new GISLattice( general);
		double minP = Double.POSITIVE_INFINITY;
		double maxP = Double.NEGATIVE_INFINITY;
		for( int r = general.getNumberRows() - 1; r >= 0; r--) {
			for( int c = general.getNumberColumns() - 1; c >= 0; c--) {
				if( general.isNoData( r, c) == false && quality.isNoData( r, c) == false) {
					double genValue = general.getCellValue( r, c);
					double meanAdjGenValue = genValue + vsdem.getMeanAdjustmentAt( genValue);
					double pValue = pointDistributionProportion( precision, meanAdjGenValue, 
							vsdem.getStandardDeviationErrorAt( genValue), quality.getCellValue( r, c));
					propMap.setCellValue( r, c, pValue);
					if( pValue < minP)
						minP = pValue;
					if( pValue > maxP)
						maxP = pValue;
				}
			}
		}
		System.out.println( "Minimum Proportion Value: " + minP);
		System.out.println( "Maximum Proportion Value: " + maxP);
		propMap.writeAsciiEsri( proportionMapName);
		propMap = null;
		count = 0;
		grossCount = 0;
		double oriSum = 0.0;
		double oriAbsSum = 0.0;
		minError = Double.POSITIVE_INFINITY;
		maxError = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && general.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c) - general.getCellValue( r, c);
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
				if( quality.isNoData( r, c) == false && general.isNoData( r, c) == false) {
					double value = quality.getCellValue( r, c) - general.getCellValue( r, c);
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
	}

	public GISLattice getRawSDMap() {
		GISLattice sdMap = new GISLattice( quality);
		double minSD = Double.POSITIVE_INFINITY;
		double maxSD = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && general.isNoData( r, c) == false) {
					double testingValue = general.getCellValue( r, c);
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
		System.out.println( "AnnalsResults200206.getRawSDMap() minimum SD: " + minSD);
		System.out.println( "AnnalsResults200206.getRawSDMap() maximum SD: " + maxSD);
		return sdMap;
	}

	public GISLattice getSDMapAveErrorAdjusted() {
		GISLattice sdMap = new GISLattice( quality);
		double minSD = Double.POSITIVE_INFINITY;
		double maxSD = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < quality.getNumberRows(); r++) {
			for( int c = 0; c < quality.getNumberColumns(); c++) {
				if( quality.isNoData( r, c) == false && general.isNoData( r, c) == false) {
					double testingValue = general.getCellValue( r, c);
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
		System.out.println( "AnnalsResults200206.getSDMapAveErrorAdjusted() minimum SD: " + minSD);
		System.out.println( "AnnalsResults200206.getSDMapAveErrorAdjusted() maximum SD: " + maxSD);
		return sdMap;
	}

	public double getAverageError() {
		return aveError;
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		System.out.println( "running AnnalsResults200206");
		double maxDist = 1800.0;
		GISLattice qual;
		GISLattice gen;
		qual = GISLattice.loadEsriAscii("study_dem");
/*
		gen = new GISLattice( "Trend27");
		System.out.println( "");
		//System.out.println( "Working on best trend map (trend27).");
		trendResults = new AnnalsResults200206( 
			qual, gen, maxDist, "trend27error", "trend27proportion", 15.0,
			"trend27mean", "trend27sd");
		trendResults = null;
		for( int i = 0; i < 30; i++) {
*/
		for( int i = 11; i < 30; i++) {
			System.out.println( "");
			System.out.println( "Working on realization [" + i + "]");
			gen = GISLattice.loadEsriAscii("RubberSheetTest" + i);
			//AnnalsResults200206 dtedResults = 
			new AnnalsResults200206( 
				qual, gen, maxDist, "errorMap" + i, 
				"RubberSheetTest" + i + "proportion", 15.0, 
				"RubberSheetTest" + i + "mean", 
				"RubberSheetTest" + i + "sd");
		}
	}
}