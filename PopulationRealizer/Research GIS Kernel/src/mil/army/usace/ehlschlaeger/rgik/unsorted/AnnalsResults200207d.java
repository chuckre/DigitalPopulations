package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GaussianErrorModel;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  @version 0.3
 */
public class AnnalsResults200207d {
	GaussianErrorModel gem;
	double mapProportion;

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

	public double getAverageError() {
		return( gem.getAverageError());
	}

	public double getStandardDeviationError() {
		return( gem.getStandardDeviationError());
	}

	public double getMapProportion() {
		return( mapProportion);
	}

	public AnnalsResults200207d( GISLattice quality, GISLattice general, 
			GISLattice samples, double precision, double localExponent) {
		//gem = new GaussianErrorModel( general, quality);
		gem = new GaussianErrorModel( samples, quality);
		double aveError = gem.getAverageError();
		double sd = gem.getStandardDeviationError();
		System.out.print( "AveError," + aveError + ",StDev," + sd + ",chanceRealized,");
		mapProportion = 1.0;
		for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = samples.getCellCenterNorthing( r, 0);
			for( int c = samples.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = samples.getCellCenterEasting( r, c);
				if( samples.isNoData( r, c) == false) {
					double qualValue = quality.getValue( cellEasting, cellNorthing);
					double genValue = general.getValue( cellEasting, cellNorthing);
					double ptProportion = pointDistributionProportion( 
						precision, genValue + aveError, sd, qualValue);
					mapProportion *= ptProportion;							
				}
			}
		}
		System.out.println( mapProportion);
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	   System.out.println( "running AnnalsResults200207d");
	   int maxSeeds = 20;
	   double maxDist = 2000.0;
	   double precision = 25.0;
	   int maxSamplePoints = 60;
	   GISLattice qual;
	   GISLattice gen;
	   qual = GISLattice.loadEsriAscii("study_dem");
	   GISLattice forSamples = qual.getAverageFilterMap( 17, true);
	   for( int f = 3; f <= 15; f++) {
		System.out.println( "filter size," + f);
		gen = qual.getAverageFilterMap( f, true);
		for( long seed = 1; seed <= maxSeeds; seed++) {
			System.out.println( "SEED VALUE," + seed);
			IndependentRandomSamples irs = new IndependentRandomSamples( forSamples, seed);
			GISLattice samples = irs.getSample( maxSamplePoints, maxDist);
			int sampleCount = 0;
			for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
				for( int c = samples.getNumberColumns() - 1; c >= 0; c--) {
					if( samples.isNoData( r, c) == false) {
						sampleCount++;
						samples.setCellValue( r, c, qual.getCellValue( r, c));
					}
				}
			}
			new AnnalsResults200207d( qual, gen, samples, precision, 1.0);
			System.out.println( "Total samples in this run: " + sampleCount);
		}
	   }
	}
}