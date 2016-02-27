package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.File;
import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.Filter;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.io.ESRI_ASCII;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class ClassWobbly {
	GISLattice density;
	double radius;
	Filter filter;

	public ClassWobbly( GISClass classMap, int classValue, double radius) {
		this.radius = radius;
		density = new GISLattice( classMap);
		filter = new Filter( density, radius);
		for( int row = density.getNumberRows() - 1; row >= 0; row--) {
			for( int col = density.getNumberColumns() - 1; col >= 0; col--) {
				if( classMap.isNoData( row, col) == false) {
					int count = 0;
					int classCount = 0;
					for( int r = filter.getMinRow( row); r <= filter.getMaxRow( row); r++) {
						for( int c = filter.getMinCol( col); c <= filter.getMaxCol( col); c++) {
							if( classMap.isNoData( r, c) == false &&
							    classMap.distance( row, col, r, c) <= radius) {
								count++;
								if( classMap.getCellValue( r, c) == classValue)
									classCount++;
							}
						}
					}
					if( count > 0) {
						density.setCellValue( row, col, 1.0 * classCount / count);
					}
				}
			}
		}
	}

	public GISLattice getDensityMap() {
		return density;
	}

	public GISLattice getDifferenceMap() {
		GISLattice result = new GISLattice( density);
		for( int row = density.getNumberRows() - 1; row >= 0; row--) {
			for( int col = density.getNumberColumns() - 1; col >= 0; col--) {
				if( density.isNoData( row, col) == false) {
					double minValue = Double.POSITIVE_INFINITY;
					double maxValue = Double.NEGATIVE_INFINITY;
					for( int r = filter.getMinRow( row); r <= filter.getMaxRow( row); r++) {
						for( int c = filter.getMinCol( col); c <= filter.getMaxCol( col); c++) {
							if( density.isNoData( r, c) == false &&
							    density.distance( row, col, r, c) <= radius) {
								double densityValue = density.getCellValue( r, c);
								if( densityValue > maxValue)
									maxValue = densityValue;
								if( densityValue < minValue)
									minValue = densityValue;
							}
						}
					}
					if( minValue < Double.POSITIVE_INFINITY && maxValue > Double.NEGATIVE_INFINITY) {
						result.setCellValue( row, col, maxValue - minValue);
					}
				}
			}
		}
		return result;
	}

	public GISLattice getMagnetMap( GISClass classMap, int classValue, double magnetRadius) {
		GISLattice result = new GISLattice( density);
		Filter magnetFilter = new Filter( result, magnetRadius);
		for( int row = classMap.getNumberRows() - 1; row >= 0; row--) {
			for( int col = classMap.getNumberColumns() - 1; col >= 0; col--) {
				if( classMap.isNoData( row, col) == false && density.isNoData( row, col) == false) {
					int count = 0;
					int classCount = 0;
					for( int r = magnetFilter.getMinRow( row); r <= magnetFilter.getMaxRow( row); r++) {
						for( int c = magnetFilter.getMinCol( col); c <= magnetFilter.getMaxCol( col); c++) {
							if( classMap.isNoData( r, c) == false &&
							    classMap.distance( row, col, r, c) <= magnetRadius) {
								count++;
								if( classMap.getCellValue( r, c) == classValue)
									classCount++;
							}
						}
					}
					if( count > 0) {
						double densityValue = density.getCellValue( row, col);
						result.setCellValue( row, col, densityValue - (1.0 * classCount / count));
					}
				}
			}
		}
		return result;
	}

	public static void main( String argv[]) throws IOException {
		if( argv.length != 5 && argv.length != 6) {
			System.out.println( "ClassWobbly main program requires either five or six arguments:");
			System.out.println( "java -mx####m ClassWobbly classMap category boundaryRadius densityMap boundaryMap");
			System.out.println( "         or");
			System.out.println( "java -mx####m ClassWobbly classMap category magnetRadius compareRadius densityMap magnetMap");
			System.exit( -1);
		}
		String categoryString = argv[ 1];
		String radiusString = argv[ 2];
		String cMap = argv[ 0];
		GISClass on_map = GISClass.loadEsriAscii(cMap);
		int category = new Integer( categoryString.trim()).intValue();
		double radius = new Double( radiusString.trim()).doubleValue();
		ClassWobbly cd = new ClassWobbly( on_map, category, radius);
		GISLattice density = cd.getDensityMap();
		if( argv.length == 5) {
			File dMap = ESRI_ASCII.findFile(argv[ 3]);
			density.writeAsciiEsri(dMap);
			File bMap = ESRI_ASCII.findFile(argv[ 4]);
			GISLattice boundary = cd.getDifferenceMap();
			boundary.writeAsciiEsri(bMap);
		} else {
            File dMap = ESRI_ASCII.findFile(argv[ 4]);
            density.writeAsciiEsri(dMap);
			radiusString = argv[ 3];
			double compareRadius = new Double( radiusString.trim()).doubleValue();
			GISLattice magnet = cd.getMagnetMap( on_map, category, compareRadius);
            File mMap = ESRI_ASCII.findFile(argv[ 5]);
            magnet.writeAsciiEsri(mMap);
		}
	}
}