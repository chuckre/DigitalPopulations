package mil.army.usace.ehlschlaeger.rgik.unsorted;

import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeErrorDistributionModel;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomField;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class LinearTrendGaussianErrorModel extends LatticeErrorDistributionModel {
	private double errIntercept;
	private double errSlope;
	private double stdDev;

	public LinearTrendGaussianErrorModel() {
		super();
	}

	public void setErrorIntercept( double errorIntercept) {
		errIntercept = errorIntercept;
	}

	public void setErrorSlope( double errorSlope) {
		errSlope = errorSlope;
	}

	public void setStandardDeviationError( double standardDeviationError) {
		stdDev = standardDeviationError;
	}

	public double getErrorIntercept() {
		return errIntercept;
	}

	public double getErrorSlope() {
		return errSlope;
	}

	public double getStandardDeviationError() {
		return stdDev;
	}

	public GISData makeInitialMap() {
		LatticeRandomField randomField = new LatticeRandomField( getRandomFieldTemplate());
		GISLattice map = new GISLattice( getApplicationGrid());
		GISLattice genMap = getGeneralizedMap();
		for( int r = map.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = map.getCellCenterNorthing( r, 0);
			for( int c = map.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = map.getCellCenterEasting( r, c);
				if( randomField.isNoData( cellEasting, cellNorthing) == false) {
					if( genMap.isNoData( cellEasting, cellNorthing) == false) {
						double originalValue = genMap.getValue( cellEasting, cellNorthing); 
						double rfValue = randomField.getValue( cellEasting, cellNorthing);
						double newValue = originalValue + 
							errSlope * originalValue + errIntercept + stdDev * rfValue;
						map.setCellValue( r, c, newValue);
					}
				}
			}
		}
		return( (GISData) map);
	}
}