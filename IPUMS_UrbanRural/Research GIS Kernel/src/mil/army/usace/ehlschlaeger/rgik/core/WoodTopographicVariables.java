package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.util.LeastSquaresSolver;
import mil.army.usace.ehlschlaeger.rgik.util.Matrix;

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
public class WoodTopographicVariables extends RGIS implements RGISFunction {
	GISLattice input, slope, aspect, profileC, planC;
	double radius;
	Filter filter;

	public WoodTopographicVariables( GISLattice inputMap, double radius, double weightExponent) {
		super();
		this.radius = radius;
		slope = new GISLattice( inputMap);
		aspect = new GISLattice( inputMap);
		profileC = new GISLattice( inputMap);
		planC = new GISLattice( inputMap);
		filter = new Filter( inputMap, radius);
		double res = Math.min( inputMap.getNSResolution(), inputMap.getEWResolution());
		int maxCount = filter.getMaxCells();
		double[] x = new double[ maxCount];
		double[] y = new double[ maxCount];
		double[] z = new double[ maxCount];
		double[] w = new double[ maxCount];
		double[][] wValues = new double[ 6][ 6];
		double[][] dvValues = new double[ 6][ 1];
		for( int row = inputMap.getNumberRows() - 1 - filter.getMaxRows(); row >= filter.getMaxRows(); row--) {
			for( int col = inputMap.getNumberColumns() - 1 - filter.getMaxCols(); col >= filter.getMaxCols(); col--) {
				if( inputMap.isNoData( row, col) == false) {
					int count = 0;
					double centerX = inputMap.getCellCenterEasting( row, col);
					double centerY = inputMap.getCellCenterNorthing( row, col);
					boolean anyNoData = false;
					for( int r = filter.getMinRow( row); r <= filter.getMaxRow( row); r++) {
						for( int c = filter.getMinCol( col); c <= filter.getMaxCol( col); c++) {
							if( inputMap.isNoData( r, c) == false) {
								double dist = inputMap.distance( row, col, r, c);
								if( dist <= radius) {
									x[ count] = inputMap.getCellCenterEasting( r, c) - centerX;
									y[ count] = inputMap.getCellCenterNorthing( r, c) - centerY;
									z[ count] = inputMap.getCellValue( r, c);
									w[ count] = 1.0 / Math.pow( dist + res, weightExponent);
									count++;
								}
							} else if( inputMap.distance( row, col, r, c) <= radius) {
								anyNoData = true;
							}
						}
					}
					if( count >= 6 && anyNoData == false) {
					//if( count > 6) {
						Matrix weightMatrix = new Matrix();
						Matrix dependentVector = new Matrix();
						for( int i = 0; i < 6; i++ ) {
							dvValues[ i][ 0] = 0.0;
							for( int j = 0; j < 6; j++) {
								wValues[ i][ j] = 0.0;
							}
						}
						for( int i = 0; i < count; i++) {
							double xx = x[ i] * x[ i];
							double xxx = xx * x[ i];
							double yy = y[ i] * y[ i];
							double yyy = yy * y[ i];

							dvValues[ 0][ 0] += z[ i] * xx * w[ i];
							dvValues[ 1][ 0] += z[ i] * yy * w[ i];
							dvValues[ 2][ 0] += z[ i] * x[ i] * y[ i] * w[ i];
							dvValues[ 3][ 0] += z[ i] * x[ i] * w[ i];
							dvValues[ 4][ 0] += z[ i] * y[ i] * w[ i];
							dvValues[ 5][ 0] += z[ i] * w[ i];

							wValues[ 0][ 0] += xx * xx * w[ i];				// xxxxw
							wValues[ 0][ 1] += xx * yy * w[ i];				// xxyyw
							wValues[ 0][ 2] += xxx * y[ i] * w[ i];			// xxxyw
							wValues[ 0][ 3] += xxx * w[ i];				// xxxw
							wValues[ 0][ 4] += xx * y[ i] * w[ i];			// xxyw
							wValues[ 0][ 5] += xx * w[ i];				// xxw

							//wValues[ 1][ 0] += xx * yy * w[ i];
							wValues[ 1][ 1] += yy * yy * w[ i];				// yyyyw
							wValues[ 1][ 2] += x[ i] * yyy * w[ i];			// xyyyw
							wValues[ 1][ 3] += x[ i] * yy * w[ i];			// xyyw
							wValues[ 1][ 4] += yyy * w[ i];				// yyyw
							wValues[ 1][ 5] += yy * w[ i];				// yyw

							//wValues[ 2][ 0] += xxx * y[ i] * w[ i];
							//wValues[ 2][ 1] += x[ i] * yyy * w[ i];
							//wValues[ 2][ 2] += xx * yy * w[ i];
							//wValues[ 2][ 3] += xx * y[ i] * w[ i];
							//wValues[ 2][ 4] += x[ i] * yy * w[ i];
							wValues[ 2][ 5] += x[ i] * y[ i] * w[ i];			// xyw

							// 3 0 xxxw	// 3 1 xyyw	// 3 2 xxyw	// 3 3 xxw	// 3 4 xyw
							wValues[ 3][ 5] += x[ i] * w[ i];				// xw 

							// 4 0 xxyw	// 4 1 yyyw	// 4 2 xyyw	// 4 3 xyw	// 4 4 yyw
							wValues[ 3][ 5] += y[ i] * w[ i];				// yw

							// 5 0 xxw	// 5 1 yyw	// 5 2 xyw	// 5 3 xw	// 5 4 yw
							wValues[ 5][ 5] += w[ i];					// w

						}
						wValues[ 1][ 0] = wValues[ 0][ 1];
						wValues[ 2][ 0] = wValues[ 0][ 2];
						wValues[ 2][ 1] = wValues[ 1][ 2];
						wValues[ 2][ 2] = wValues[ 0][ 1];
						wValues[ 2][ 3] = wValues[ 0][ 4];
						wValues[ 2][ 4] = wValues[ 1][ 3];
						wValues[ 3][ 0] = wValues[ 0][ 3];
						wValues[ 3][ 1] = wValues[ 1][ 3];
						wValues[ 3][ 2] = wValues[ 2][ 3];
						wValues[ 3][ 3] = wValues[ 0][ 5];
						wValues[ 3][ 4] = wValues[ 2][ 5];
						wValues[ 4][ 0] = wValues[ 0][ 4];
						wValues[ 4][ 1] = wValues[ 1][ 4];
						wValues[ 4][ 2] = wValues[ 2][ 4];
						wValues[ 4][ 3] = wValues[ 3][ 4];
						wValues[ 4][ 4] = wValues[ 1][ 5];
						wValues[ 5][ 0] = wValues[ 0][ 5];
						wValues[ 5][ 1] = wValues[ 1][ 5];
						wValues[ 5][ 2] = wValues[ 2][ 5];
						wValues[ 5][ 3] = wValues[ 3][ 5];
						wValues[ 5][ 4] = wValues[ 4][ 5];

						weightMatrix.setMatrix( wValues);
						dependentVector.setMatrix( dvValues);
						Matrix parameters = LeastSquaresSolver.getBeta( dependentVector, weightMatrix);
						double pA = parameters.getCell( 0, 0);
						double pB = parameters.getCell( 1, 0);
						double pC = parameters.getCell( 2, 0);
						double pD = parameters.getCell( 3, 0);
						double pE = parameters.getCell( 4, 0);
//						double pF = parameters.getCell( 5, 0);
						slope.setCellValue( row, col, Math.atan( Math.sqrt( pD * pD + pE * pE)));
						aspect.setCellValue( row, col, Math.atan( pE / pD));
						profileC.setCellValue( row, col, -200. * (pA * pD * pD + pB * pE * pE + pC * pD * pE) /
									((pE * pE + pD * pD) * Math.pow( 1. + pD * pD + pE * pE, 1.5)));
						planC.setCellValue( row, col, -200. * (pA * pE * pE + pB * pD * pD - pC * pD * pE) /
									(pE * pE + pD * pD));
					}
				}
			}
		}
	}

	public GISLattice getSlopeMap() {
		return slope;
	}

	public GISLattice getAspectMap() {
		return aspect;
	}

	public GISLattice getProfileCurvatureMap() {
		return profileC;
	}

	public GISLattice getPlanCurvatureMap() {
		return planC;
	}

	public static void main( String argv[]) throws IOException {
		if( argv.length != 7) {
			System.out.println( "WoodTopographicVariables main program requires seven arguments:");
			System.out.println( "java -mx####m WoodTopographicVariables inputMap scaleDistance weightExponent slopeMap aspectMap profileCurveMap planCurveMap");
			System.exit( -1);
		}
		String cMap = argv[ 0];
		String scaleString = argv[ 1];
		String weightString = argv[ 2];
		GISLattice inputMap = GISLattice.loadEsriAscii(cMap);
		double scale = new Double( scaleString.trim()).doubleValue();
		double exponent = new Double( weightString.trim()).doubleValue();
		WoodTopographicVariables wtv = new WoodTopographicVariables( inputMap, scale, exponent);
		GISLattice s = wtv.getSlopeMap();
		String sMap = argv[ 3];
		s.writeAsciiEsri( sMap);
		String aMap = argv[ 4];
		GISLattice a = wtv.getAspectMap();
		a.writeAsciiEsri( aMap);
		String prMap = argv[ 5];
		GISLattice pr = wtv.getProfileCurvatureMap();
		pr.writeAsciiEsri( prMap);
		String pcMap = argv[ 6];
		GISLattice pc = wtv.getPlanCurvatureMap();
		pc.writeAsciiEsri( pcMap);
	}
}