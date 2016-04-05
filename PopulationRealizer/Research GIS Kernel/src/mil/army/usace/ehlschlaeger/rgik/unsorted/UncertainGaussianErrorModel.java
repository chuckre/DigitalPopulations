package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.util.Date;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeErrorDistributionModel;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomField;
/** Not in final form. UGEM object conceptualized in Ehlschlaeger, C. R., A. M. Shortridge, M. F. Goodchild
 *  (1997). Visualizing Spatial Data Uncertainty using Animation, Computers & Geosciences, 23(4):387-395.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class UncertainGaussianErrorModel extends LatticeErrorDistributionModel {
	private double aveError;
	private double stdDev;
	private double uncertStdDev;
	private long seed;
	private static long previousSeed = 0;
	private Random ran;
	private static boolean referencedUGEM = false;

	public UncertainGaussianErrorModel() {
		super();
		Date seedDate = new Date();
		while( seedDate.getTime() == previousSeed) {
			seedDate = new Date();
		}
		long randomSeed = seedDate.getTime();
		previousSeed = randomSeed;
		make( randomSeed);
	}

	public UncertainGaussianErrorModel( long randomSeed) {
		super();
		make( randomSeed);
	}

	private void make( long randomSeed) {
		seed = randomSeed;
		ran = new Random( seed);
		if( referencedUGEM == false) {
			referencedUGEM = true;
			System.out.println( "");
			System.out.println( 
"UncertainGaussianErrorModel models uncertainty in a gaussian error model. It is");
			System.out.println( 
"described in: Ehlschlaeger, C. R., A. M. Shortridge, M. F. Goodchild, 1997,");
			System.out.println( 
"Visualizing Spatial Data Uncertainty using Animation: Computers & Geosciences ");
			System.out.println( 
"23(4): 387-395. WWW: <http://www.geo.hunter.cuny.edu/~chuck/CGFinal/paper.htm>");
			System.out.println( "");
		}
	}

	public void setSeed( long randomSeed) {
		seed = randomSeed;
	}

	public long getSeed() {
		return seed;
	}

	public void setAverageError( double averageError) {
		aveError = averageError;
	}

	public double getAverageError() {
		return aveError;
	}

	public void setStandardDeviationError( double standardDeviationError) {
		stdDev = standardDeviationError;
	}

	public double getStandardDeviationError() {
		return stdDev;
	}

	public void setUncertaintyStandardDeviation( double uncertaintyStandardDeviation) {
		uncertStdDev = uncertaintyStandardDeviation;
	}

	public double getUncertaintyStandardDeviation() {
		return uncertStdDev;
	}

	public GISData makeInitialMap() {
		LatticeRandomField randomField = new LatticeRandomField( getRandomFieldTemplate());
		GISLattice map = new GISLattice( getApplicationGrid());
		GISLattice genMap = getGeneralizedMap();
		double allMapUncertainty = (double) ran.nextGaussian();
		for( int r = map.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = map.getCellCenterNorthing( r, 0);
			for( int c = map.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = map.getCellCenterEasting( r, c);
				if( randomField.isNoData( cellEasting, cellNorthing) == false) {
					if( genMap.isNoData( cellEasting, cellNorthing) == false) {
						double originalValue = genMap.getValue( cellEasting, cellNorthing); 
						double rfValue = randomField.getValue( cellEasting, cellNorthing);
						double newValue = originalValue + aveError +
							allMapUncertainty * uncertStdDev + stdDev * rfValue;
						map.setCellValue( r, c, newValue);
					}
				}
			}
		}
		return( (GISData) map);
	}
}