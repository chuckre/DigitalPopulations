package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GaussianErrorModel;
import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  @version 0.3
 */
public class AnnalsResults200207 {
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

	public AnnalsResults200207( GISLattice quality, GISLattice general, 
			GISLattice samples, double precision, double localExponent) {
		gem = new GaussianErrorModel( general, quality);
		double aveError = gem.getAverageError();
		double sd = gem.getStandardDeviationError();
		System.out.print( "StDev," + sd + ",chanceRealized,");
		mapProportion = 1.0;
		for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = samples.getCellCenterNorthing( r, 0);
			for( int c = samples.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = samples.getCellCenterEasting( r, c);
				if( samples.isNoData( r, c) == false) {
					double sampleValue = samples.getCellValue( r, c);
					double genValue = general.getValue( cellEasting, cellNorthing);
					double ptProportion = pointDistributionProportion( 
						precision, genValue + aveError, sd, sampleValue);
					mapProportion *= ptProportion;							
				}
			}
		}
		System.out.println( mapProportion);
	}

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	   System.out.println( "running AnnalsResults200207");
	   int numRealizations = 30;
	   int maxSeeds = 20;
	   double maxDist = 2000.0;
	   double precision = 25.0;
	   int maxSamplePoints = 60;
	   double[] sd = new double[ numRealizations];
	   double[] ae = new double[ numRealizations];
	   GISLattice[] ga = new GISLattice[ numRealizations];
	   double[] mapP = new double[ numRealizations];
	   GISLattice qual;
	   GISLattice gen;
	   qual = GISLattice.loadEsriAscii("study_dem");
	   GISLattice allMapsData = new GISLattice( qual);
	   for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
		for( int c = qual.getNumberColumns() - 1; c >=0; c--) {
			if( qual.isNoData( r, c) == false) {
				allMapsData.setCellValue( r, c, qual.getCellValue( r, c));
			}
		}
	   }
	   for( int i = 0; i < numRealizations; i++) {
		ga[ i] = GISLattice.loadEsriAscii("RubberSheetTest" + i);
		//System.out.print( i + " ");
		for( int r = allMapsData.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = allMapsData.getCellCenterNorthing( r, 0);
			for( int c = allMapsData.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = allMapsData.getCellCenterEasting( r, c);
				if( allMapsData.isNoData( r, c) == false && ga[ i].isNoData4Corners( cellEasting, cellNorthing) == true) {
					allMapsData.setNoData( r, c, true);
				}
			}
		}
	   }
	   for( long seed = 1; seed <= maxSeeds; seed++) { 
		//System.out.println( "********************************************************");
		System.out.println( "SEED VALUE," + seed);
		IndependentRandomSamples irs = new IndependentRandomSamples( allMapsData, seed);
		GISLattice samples = irs.getSample( maxSamplePoints, maxDist);
		//System.out.println( "AnnalsResults200207.main() preparing sample map, finishing samples");
		//System.out.println( "AnnalsResults200207.main(): saving sample map as samples" + seed + ".asc");
		samples.writeAsciiEsri( "samples" + seed);
		//System.out.println( "");
		System.out.print( "OriginalMapNoAdjustment,");
		gen = GISLattice.loadEsriAscii("study_dted");
		AnnalsResults200207 originalDted = new AnnalsResults200207( qual, gen, samples, precision, 1.0);
		double aveError = originalDted.getAverageError();
		for( int r = gen.getNumberRows() - 1; r >= 0; r--) {
			for( int c = gen.getNumberColumns() - 1; c >= 0; c--) {
				if( gen.isNoData( r, c) == false) {
					gen.setCellValue( r, c, gen.getCellValue( r, c) + aveError);
				}
			}
		}
		System.out.print( "OriginalMapAdjusted,");
		originalDted = new AnnalsResults200207( qual, gen, samples, precision, 1.0);
		originalDted = null;
		gen = GISLattice.loadEsriAscii("trend27");
		System.out.print( "BestTrendMap(trend27),");
		new AnnalsResults200207( qual, gen, samples, precision, 1.0);
		AnnalsResults200207 dtedResults = null;
		for( int i = 0; i < numRealizations; i++) {
			System.out.print( "Realization," + i + ",");
			dtedResults = new AnnalsResults200207( qual, ga[ i], samples, precision, 1.0);
			sd[ i] = dtedResults.getStandardDeviationError();
			ae[ i] = dtedResults.getAverageError();
			double bd = dtedResults.getMapProportion();
			mapP[ i] = bd;
		}
		System.out.print( "Sum,");
		double mapProportion = 1.0;
		int sampleCount = 0;
		for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = samples.getCellCenterNorthing( r, 0);
			for( int c = samples.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = samples.getCellCenterEasting( r, c);
				if( samples.isNoData( r, c) == false) {
					sampleCount++;
					double sampleValue = samples.getCellValue( r, c);
					double ptProportion = 0.0;
					for( int i = 0; i < numRealizations; i++) {
						double genValue = ga[ i].getValue( cellEasting, cellNorthing);
						double ptP = dtedResults.pointDistributionProportion( 
							precision, genValue + ae[i], sd[ i], sampleValue);
						ptProportion += ptP;
					}	
					mapProportion *= ptProportion / numRealizations;
				}
			}
		}
		System.out.println( "Likelihood sample is realized," + mapProportion);
		BubbleSort bs = new BubbleSort( true);
		int[] index = new int[ numRealizations];
		/* unnecessary after 3/10/04
		for( int i = 0; i < numRealizations; i++) {
			index[ i] = i;
		}
		*/
		bs.sort( index, mapP);
		for( int i = 0; i < numRealizations; i++) {
			System.out.println( "Rank: " + (i + 1) + ", Realization: " + 
				index[ i] + ", SD: " + sd[ index[i]] + ", proportion: " + mapP[ index[i]]);
		}
		for( int j = 1; j <= numRealizations; j++) {
			double mP = 1.0;
			for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
				double cellNorthing = samples.getCellCenterNorthing( r, 0);
				for( int c = samples.getNumberColumns() - 1; c >= 0; c--) { 
					double cellEasting = samples.getCellCenterEasting( r, c);
					if( samples.isNoData( r, c) == false) {
						double sampleValue = samples.getCellValue( r, c);
						double ptProportion = 0.0;
						for( int i = 0; i < j; i++) {
							double genValue = ga[ index[i]].getValue( cellEasting, cellNorthing);
							double ptP = dtedResults.pointDistributionProportion( 
								precision, genValue + ae[ index[i]], sd[ index[i]], sampleValue);
							ptProportion += ptP;
							//System.out.println( "ptP: " + ptP + ", prop: " + ptProportion);
						}
						//System.out.print( "prop: " + ptProportion);	
						if( j > 1) {
							ptProportion /= 1.0 * j;
						}
						mP *= ptProportion;	
						//System.out.println( ", pt prop: " + ptProportion + "new Prop: " + mP);			
					}
				}
			}
			System.out.print( "Likelihood sample is realized for best maps," + j + "," + mP + ", maps:");
			for( int i = 0; i < j; i++) {
				System.out.print( "," + index[i]);
			}
			System.out.println("");
		}
		System.out.println( "Total samples in this run: " + sampleCount);
	   }
	}
}

		/*
		String propValue = mapProportion.toString();
		if( propValue.charAt( 0) == '1') {
			System.out.println( "1.0 x 10^0");
		} else {
			int stringLength = propValue.length();
			int i = 2;
			int count = 0;
			while( stringLength > i && propValue.charAt( i) == '0') {
				i++;
				count++;
			}
			if( stringLength == i) {
				System.out.println( "0.0");
			} else {
				System.out.print( "0." + propValue.charAt( i));
				int digitsToPrint = Math.min( 10, 1+ stringLength - i);
				for( int j = i + 1; j <= i + digitsToPrint; j++) {
					System.out.print( propValue.charAt( j));
				}
				System.out.println( ",x10^,-" + count);
			}
		}
		*/

		/* BigDecimal to string
		String propValue = mapProportion.toString();
		System.out.print( "Likelihood sample is realized,");
		if( propValue.charAt( 0) == '1') {
			System.out.print( "1.0 x 10^0");
		} else {
			int stringLength = propValue.length();
			int i = 2;
			int count = 0;
			while( stringLength > i && propValue.charAt( i) == '0') {
				i++;
				count++;
			}
			if( stringLength == i) {
				System.out.print( "0.0");
			} else {
				System.out.print( "0." + propValue.charAt( i));
				int digitsToPrint = Math.min( 10, 1+ stringLength - i);
				for( int j = i + 1; j <= i + digitsToPrint; j++) {
					System.out.print( propValue.charAt( j));
				}
				System.out.print( ",x10^,-" + count);
			}
		}
		System.out.println( "");
		*/


		/* code to determine spatial dependence of "do nothing" results
		gen = new GISLattice( "study_dted");
		GISLattice genError = new GISLattice( qual);
		for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = qual.getCellCenterNorth( r, 0);
			for( int c = qual.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = qual.getCellCenterEast( r, c);
				if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
					genError.setCellValue( r, c, qual.getCellValue( r, c) - gen.getValue( cellEasting, cellNorthing));
				}
			}
		}
		Correlogram errorCor = new Correlogram( genError.getEWResolution() * 1.01, 3000.0, genError);
		System.out.println( "AnnalsResults200207.main() The correlogram of error is:");
		errorCor.print();
		*/

		/* buggy code for future exploration
		double pp = 0.0;
		for( int r = samples.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = samples.getCellCenterNorth( r, 0);
			for( int c = samples.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = samples.getCellCenterEast( r, c);
				if( samples.isNoData( r, c) == false) {
					double sampleValue = samples.getCellValue( r, c);
					double ptProportion = 0.0;
					for( int i = 0; i < numRealizations; i++) {
						//gen = new GISLattice( "RubberSheetTest" + i);
						double genValue = ga[ i].getValue( cellEasting, cellNorthing);
						double ptP = dtedResults.pointDistributionProportion( 
							precision, genValue, sd[ i], sampleValue);
						ptProportion += ptP;
					}
					ptProportion = numRealizations / ptProportion;
					pp += ptProportion * ptProportion;	
				}
			}
		}
		System.out.println( ",leastSquaresInverseProportion:," + Math.sqrt( pp));
		*/