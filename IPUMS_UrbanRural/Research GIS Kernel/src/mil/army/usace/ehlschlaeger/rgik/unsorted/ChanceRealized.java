package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.math.BigDecimal;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GaussianErrorModel;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class ChanceRealized {
	GaussianErrorModel gem;
	double mapProportion;
	BigDecimal mapProportionBD;

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

/*
	public double getMapProportion() {
		return( mapProportion);
	}
*/

	public BigDecimal getMapProportionBD() {
		return( mapProportionBD);
	}

/*
	public ChanceRealized( GISLattice general, double[] sampleEasting, double[] sampleNorthing, double[] sampleZ, double precision) {
		gem = new GaussianErrorModel( general, sampleEasting, sampleNorthing, sampleZ);
		double aveError = gem.getAverageError();
		double sd = gem.getStandardDeviationError();
		mapProportionBD = new CreBigDecimal( 1.0);
		//mapProportion = 1.0;
		for( int i = sampleEasting.length - 1; i >= 0; i--) {
			double cellNorthing = sampleNorthing[ i]; 
			double cellEasting = sampleEasting[ i];
			double qualValue = sampleZ[ i];
			double genValue = general.getValue( cellEasting, cellNorthing);
			double ptProportion = pointDistributionProportion( 
				precision, genValue + aveError, sd, qualValue);
			//mapProportion *= ptProportion;
			mapProportionBD.multiply( ptProportion, 0);							
		}
	}
*/
 
	public ChanceRealized( GISLattice general, GISLattice dx, GISLattice dy, 
			GISLattice dz, double[] sampleEasting, double[] sampleNorthing, 
			double[] sampleZ, double precision) {
		gem = new GaussianErrorModel( general, dx, dy, dz, sampleEasting, sampleNorthing, sampleZ);
		double aveError = gem.getAverageError();
		double sd = gem.getStandardDeviationError();
		mapProportionBD = new BigDecimal( 1.0);
		//mapProportion = 1.0;
		for( int i = sampleEasting.length - 1; i >= 0; i--) {
			double cellNorthing = sampleNorthing[ i]; 
			double cellEasting = sampleEasting[ i];
			double newX = cellEasting + dx.getValue( cellEasting, cellNorthing);
			double newY = cellNorthing + dy.getValue( cellEasting, cellNorthing);
			double qualValue = sampleZ[ i];
			if( general.isNoData4Corners( newX, newY) == true) {
				System.out.println( "ChanceRealized Warning: general map has location without data");
			} else {
				double genValue = dz.getValue( cellEasting, cellNorthing) + 
					general.getValue( newX, newY);
				double ptProportion = pointDistributionProportion( 
					precision, genValue + aveError, sd, qualValue);
				//mapProportion *= ptProportion;
				mapProportionBD.multiply( new BigDecimal(ptProportion));
			}
		}
	}

	public ChanceRealized( GISLattice general, GISLattice samples, double precision) {
		gem = new GaussianErrorModel( general, samples);
		double aveError = gem.getAverageError();
		double sd = gem.getStandardDeviationError();
		mapProportionBD = new BigDecimal( 1.0);
		//mapProportion = 1.0;
		for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = samples.getCellCenterNorthing( r, 0);
			for( int c = samples.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = samples.getCellCenterEasting( r, c);
				if( samples.isNoData( r, c) == false) {
					double qualValue = samples.getValue( r, c);
					double genValue = general.getValue( cellEasting, cellNorthing);
					double ptProportion = pointDistributionProportion( 
						precision, genValue + aveError, sd, qualValue);
					//mapProportion *= ptProportion;
					mapProportionBD.multiply( new BigDecimal(ptProportion));							
				}
			}
		}
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	   /*
	   System.out.println( "testing CreBigDecimal");
		CreBigDecimal bd = new CreBigDecimal( 1000000000.0);
		for( int i = 1; i < 1000; i++) {
			double smallNumber = Math.random() * .99 + 1.0;
			CreBigDecimal sN = new CreBigDecimal( smallNumber);
			bd.divideBy( sN);
			System.out.println( "bd: " + bd.toString() + ", sN: " + smallNumber + ", sNBD: " + sN.toString());
		}
	   */
	   System.out.println( "running ChanceRealized test");
	   int maxSeeds = 20;
	   double maxDist = 200.0;
	   double precision = 1.0;
	   int maxSamplePoints = 2000;
	   GISLattice qual;
	   GISLattice gen;
	   qual = GISLattice.loadEsriAscii( "study_dem");
	   gen = GISLattice.loadEsriAscii( "study_dted");
		for( long seed = 1; seed <= maxSeeds; seed++) {
			IndependentRandomSamples irs = new IndependentRandomSamples( qual, seed);
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
			System.out.print( "samplePoints," + sampleCount);
			ChanceRealized cR = new ChanceRealized( gen, samples, precision);
			//ChanceRealized cR = new ChanceRealized( gen, qual, precision);
			System.out.print( ",seed," + seed);
			System.out.print( ",ave," + cR.getAverageError());
			System.out.print( ",sd," + cR.getStandardDeviationError());
			System.out.println( ",ChanceRealized," + (cR.getMapProportionBD()).toString());
			//System.out.println( ",ChanceRealized," + cR.getMapProportion());
		}
	}
}