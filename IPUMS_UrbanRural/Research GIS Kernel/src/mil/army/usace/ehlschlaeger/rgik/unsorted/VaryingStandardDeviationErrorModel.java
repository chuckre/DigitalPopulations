package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeErrorDistributionModel;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomField;
import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;
import mil.army.usace.ehlschlaeger.rgik.util.Matrix;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class VaryingStandardDeviationErrorModel extends LatticeErrorDistributionModel {
	private double 	minValue, maxValue;
	//private double	minMeanAdjustment, maxMeanAdjustment;
	private double	meanIntercept, meanSlope;
	private double	sdIntercept, sdSlope;
	private double	minSDError;
	private GISLattice gen, qual;

	public VaryingStandardDeviationErrorModel() {
		super();
	}

	public void setGeneralizedData( GISLattice map) {
		gen = map;
		if( qual != null) {
			calc();
		}
	}

	public void setHigherQualityData( GISLattice map) {
		qual = map;
		if( gen != null) {
			calc();
		}
	}

	private void calc() {
		int count = 0;
		minValue = Double.POSITIVE_INFINITY;
		maxValue = Double.NEGATIVE_INFINITY;
		for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = qual.getCellCenterNorthing( r, 0);
			for( int c = qual.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = qual.getCellCenterEasting( r, c);
				if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
					count++;
					double genValue = gen.getValue( cellEasting, cellNorthing);
					if( genValue < minValue)
						minValue = genValue;
					if( genValue > maxValue)
						maxValue = genValue;
				}
			}
		}
		Matrix aMatrix = new Matrix();
		double[][] aData = new double[ count][ 2];
		Matrix zVector = new Matrix();
		double zData[][] = new double[ count][ 1];
		// calculating mean adjustment range
		count = 0;
		for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = qual.getCellCenterNorthing( r, 0);
			for( int c = qual.getNumberColumns() - 1; c >=0; c--) { 
				double cellEasting = qual.getCellCenterEasting( r, c);
				if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
					double genValue = gen.getValue( cellEasting, cellNorthing);
					double qualValue = qual.getValue( r, c);
					aData[ count][ 0] = 1.0;
					aData[ count][ 1] = genValue;
					zData[ count++][ 0] = qualValue - genValue;
				}
			}
		}
		zVector.setMatrix( zData);
		aMatrix.setMatrix( aData); 
		Matrix aTranspose = aMatrix.transpose();
		Matrix aaMatrix = new Matrix();
		aaMatrix.multiply( aTranspose, aMatrix); 
		Matrix aaInverse = aaMatrix.inverse(); 
		aaMatrix = null;
		Matrix aaI_aT = new Matrix();
		aaI_aT.multiply( aaInverse, aTranspose);
		aaInverse = null;
		aTranspose = null;
		Matrix coefVector = new Matrix();
		coefVector.multiply( aaI_aT, zVector);
		aaI_aT = null;
		meanIntercept  = coefVector.getCell( 0, 0); // a0
		meanSlope = coefVector.getCell( 1, 0); // a1
		// calculating standard deviation range
		// 	sort aData and zData from lowest to highest elevation
		boolean desendingValues = false;
		BubbleSort bs = new BubbleSort( desendingValues);
		int[] order = new int[ count];
		double[] values = new double[ count];
		for( int i = 0; i < count; i++) {
			order[ i] = i;
			values[ i] = aData[ i][ 1];
			zData[ i][ 0] = 0.0;
		} 
		bs.sort( order, values);
		values = null;
		// determine SD for every elevation value to place zData with a moving window.
		// When data existing in both maps, order[ count] will indicate the location within aData and zData
		int movingWindowSize = 31; // should be odd
		int movingWindowSpread = movingWindowSize / 2;
		// SD = sqrt( (sum(x^2) - sum(x)^2 / N) / (N - 1))
		// Part one: sum(x)
		count = 0;
		for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = qual.getCellCenterNorthing( r, 0);
			for( int c = qual.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = qual.getCellCenterEasting( r, c);
				if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
					double genValue = gen.getValue( cellEasting, cellNorthing);
					genValue += getMeanAdjustmentAt( genValue);
					double qualValue = qual.getValue( r, c);
					int minIndex = Math.max( 0, order[ count] - movingWindowSpread);
					int maxIndex = Math.min( zData.length - 1, order[ count] + movingWindowSpread);
					for( int i = zData.length - 1; i >= 0; i--) {
						if( order[ i] >= minIndex && order[ i] <= maxIndex) {
							zData[ i][0] += qualValue - genValue;
						}
					}
					count++;
				}
			}
		}
		// Part two: - sum(x)^2 / N
		for( int i = zData.length - 1; i >= 0; i--) {
			zData[ i][0] *= zData[ i][0] / 
				numberNforMovingWindow( zData.length, movingWindowSpread, movingWindowSize, order[ i]);
			zData[ i][0] *= -1.0;
		}
		// Part three: (sum(x^2) - sum(x)^2 / N)
		count = 0;
		for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
			double cellNorthing = qual.getCellCenterNorthing( r, 0);
			for( int c = qual.getNumberColumns() - 1; c >= 0; c--) { 
				double cellEasting = qual.getCellCenterEasting( r, c);
				if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
					double genValue = gen.getValue( cellEasting, cellNorthing);
					genValue += getMeanAdjustmentAt( genValue);
					double qualValue = qual.getValue( r, c);
					int minIndex = Math.max( 0, order[ count] - movingWindowSpread);
					int maxIndex = Math.min( zData.length - 1, order[ count] + movingWindowSpread);
					for( int i = zData.length - 1; i >= 0; i--) {
						if( order[ i] >= minIndex && order[ i] <= maxIndex) {
							zData[ i][0] += (qualValue - genValue) * (qualValue - genValue);
						}
					}
					count++;
				}
			}
		}
		// Part four: sqrt( (sum(x^2) - sum(x)^2 / N) / (N - 1))
		minSDError = Double.POSITIVE_INFINITY;
		for( int i = zData.length - 1; i >= 0; i--) {
			zData[ i][0] /= numberNforMovingWindow( zData.length, 
				movingWindowSpread, movingWindowSize, order[ i]) - 1;
			zData[ i][0] = Math.sqrt( zData[ i][0]);
			if( zData[ i][0] < minSDError) {
				minSDError = zData[ i][0];
			}
		}
		aMatrix.setMatrix( aData); 
		aTranspose = aMatrix.transpose();  
		aaMatrix = new Matrix();
		aaMatrix.multiply( aTranspose, aMatrix);  
		aMatrix = null;
		aaInverse = aaMatrix.inverse(); 
		aaMatrix = null;
		aaI_aT = new Matrix(); //needed
		aaI_aT.multiply( aaInverse, aTranspose); 
		aaInverse = null;
		aTranspose = null;
		coefVector = new Matrix();
		coefVector.multiply( aaI_aT, zVector); 
		aaI_aT = null;
		zVector = null;
		sdIntercept = coefVector.getCell( 0, 0); // a0
		sdSlope = coefVector.getCell( 1, 0); // a1
	}

	private int numberNforMovingWindow( int sizeArray, int movingWindowSpread, int movingWindowSize, int index) {
		if( index >= movingWindowSpread && index < sizeArray - movingWindowSpread) {
			return movingWindowSize;
		}
        if( sizeArray <= movingWindowSize)
            throw new IllegalArgumentException("Moving Window Size too large for number of samples");
            
		if( index < movingWindowSpread) {
            return( 1 + movingWindowSpread + index);
		} else {
    		// index >= sizeArray - movingWindowSpread
            return( movingWindowSpread + sizeArray - index);
		}
	}

	public double getMinimumDataValue() {
		return minValue;
	}

	public double getMaximumDataValue() {
		return maxValue;
	}

	public double getMeanIntercept() {
		return meanIntercept;
	}

	public double getMeanSlope() {
		return meanSlope;
	}

	public double getMeanAdjustmentAt( double value) { 
		return( meanIntercept + meanSlope * value);
	}

	public double getStandardDeviationErrorAt( double value) {
		double sd = sdIntercept + sdSlope * value;
		if( sd < minSDError)
			return minSDError;
		return sd;
	}

	public double getStandardDeviationIntercept() {
		return sdIntercept;
	}

	public double getStandardDeviationSlope() {
		return sdSlope;
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
						double meanAdjuster = getMeanAdjustmentAt( originalValue);
						double rfValue = randomField.getValue( cellEasting, cellNorthing);
						double rfMultiplier = getStandardDeviationErrorAt( originalValue);
						double newValue = originalValue + meanAdjuster + rfMultiplier * rfValue;
						map.setCellValue( r, c, newValue);
					}
				}
			}
		}
		return( (GISData) map);
	}

	public static void main( String[] args) throws IOException {
		VaryingStandardDeviationErrorModel vsdem = new VaryingStandardDeviationErrorModel();
		System.out.println( "");
		System.out.println( "VaryingStandardDeviationErrorModel.main starting");
		System.out.println( "Choose a map for the generalized data.");
		GISLattice gen = new GISLattice( );
		System.out.println( "Choose a map for the higher quality data.");
		GISLattice qual = new GISLattice( );
		if( gen != null && qual != null) {
			vsdem.setGeneralizedData( gen);
			vsdem.setHigherQualityData( qual);
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
			System.out.println( "");
			for( double v = minV; v <= maxV; v += (maxV - minV) * .09999999999) {
				System.out.println( "v: " + v + ", mean error: " + vsdem.getMeanAdjustmentAt( v) +
					", SD error: " + vsdem.getStandardDeviationErrorAt( v));
			}
		}
		for( int r = gen.getNumberRows() - 1; r >= 0; r--) {
			for( int c = gen.getNumberColumns() - 1; c >= 0; c--) {
				if( gen.isNoData( r, c) == false) {
					double genValue = gen.getCellValue( r, c);
					genValue += vsdem.getMeanAdjustmentAt( genValue);
					gen.setCellValue( r, c, genValue);
				}
			}
		}
		gen.writeAsciiEsri( "adj");
		System.out.println( "VaryingStandardDeviationErrorModel.main ending");
		System.exit( 0);
	}
}