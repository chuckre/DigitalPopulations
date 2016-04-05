package mil.army.usace.ehlschlaeger.rgik.core;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GaussianErrorModel extends LatticeErrorDistributionModel {
	private double aveError;
	private double stdDev;
	private GISLattice gen, qual, dx, dy, dz;
	private double[] sampleEasting, sampleNorthing, sampleZ;

	public GaussianErrorModel() {
		super();
	}

	public GaussianErrorModel( boolean averageErrorFlag) {
		super();
	}

	public GaussianErrorModel( GISLattice generalizedMap, GISLattice qualityMap) {
		super();
		gen = generalizedMap;
		qual = qualityMap;
		if( qual != null && gen != null) {
			calc();
		}
	}

	public GaussianErrorModel( GISLattice generalizedMap, GISLattice dx, GISLattice dy, 
			GISLattice dz, double[] sampleEasting, double[] sampleNorthing, double[] sampleZ) {
		super();
		gen = generalizedMap;
		this.sampleEasting = sampleEasting;
		this.sampleNorthing = sampleNorthing;
		this.sampleZ = sampleZ;
		if( qual != null && sampleEasting != null && sampleNorthing != null && sampleZ != null) {
			calc();
		}
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
		double oriSum = 0.0;
		aveError = 0.0;
		if( qual != null) {
			for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
				double cellNorthing = qual.getCellCenterNorthing( r, 0);
				for( int c = qual.getNumberColumns() - 1; c >=0; c--) { 
					double cellEasting = qual.getCellCenterEasting( r, c);
					if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
						count++;
						double genValue = gen.getValue( cellEasting, cellNorthing);
						double qualValue = qual.getCellValue( r, c);
						double value = qualValue - genValue;
						oriSum += value;
					}
				}
			}
		} else {
			for( int i = sampleZ.length - 1; i >= 0; i--) {
				double cellNorthing = sampleNorthing[ i]; 
				double cellEasting = sampleEasting[ i]; 
				double newX = cellEasting + dx.getValue( cellEasting, cellNorthing);
				double newY = cellNorthing + dy.getValue( cellEasting, cellNorthing);
				if( gen.isNoData4Corners( newX, newY) == false) {
					double value = sampleZ[ i] - 
						(dz.getValue( cellEasting, cellNorthing) + gen.getValue( newX, newY));
					oriSum += value;
					count++;
				}
			}
		}
		if( count < 2) {
		    throw new DataException("Data has no overlap or not enough data [" + count + "]");
		}
		aveError = oriSum / count;
		stdDev = 0.0;
		if( qual != null) {
			for( int r = qual.getNumberRows() - 1; r >= 0; r--) {
				double cellNorthing = qual.getCellCenterNorthing( r, 0);
				for( int c = qual.getNumberColumns() - 1; c >=0; c--) { 
					double cellEasting = qual.getCellCenterEasting( r, c);
					if( qual.isNoData( r, c) == false && gen.isNoData4Corners( cellEasting, cellNorthing) == false) {
						double genValue = gen.getValue( cellEasting, cellNorthing);
						double qualValue = qual.getCellValue( r, c);
						double value = qualValue - genValue;
						stdDev += (value - aveError) * (value - aveError);
					}
				}
			}
		} else {
			for( int i = sampleZ.length - 1; i >= 0; i--) {
				double cellNorthing = sampleNorthing[ i]; 
				double cellEasting = sampleEasting[ i]; 
				double newX = cellEasting + dx.getValue( cellEasting, cellNorthing);
				double newY = cellNorthing + dy.getValue( cellEasting, cellNorthing);
				if( gen.isNoData4Corners( newX, newY) == false) {
					double value = sampleZ[ i] - 
						(dz.getValue( cellEasting, cellNorthing) + gen.getValue( newX, newY));
					stdDev += (value - aveError) * (value - aveError);
				}
			}
		}
		stdDev /= count - 1;
		stdDev = Math.sqrt( stdDev);	
	}

	public void setAverageError( double averageError) {
		aveError = averageError;
	}

	public void setStandardDeviationError( double standardDeviationError) {
		stdDev = standardDeviationError;
	}

	public double getAverageError() {
		return aveError;
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
						map.setCellValue( r, c, originalValue + aveError + 
							stdDev * randomField.getValue( cellEasting, cellNorthing));
					}
				}
			}
		}
		return( (GISData) map);
	}
}