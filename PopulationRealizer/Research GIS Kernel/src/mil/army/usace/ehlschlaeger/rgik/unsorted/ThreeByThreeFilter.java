package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.gui.GISClassView;



/**
 * 3x3 homogeneity filter: copies only cells that match all 8 of their
 * neighbors.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class ThreeByThreeFilter {
    private boolean isValue;
    private String  inputMapName, outputMapName;
    private int     similarToCenter;

    /**
     * this method returns a new GISClass with cell values equal to the original
     * GISClass if and only if the grid cell has the same value as all of the
     * adjacent 3x3 grid cells. Otherwise, the grid cell will contain a no data
     * value.
     */
    public static GISClass threeByThreeHomogeneous(GISClass map) {
        GISClass homo = new GISClass( map);
        for( int r = homo.getNumberRows() - 2; r > 0; r--) {
            for( int c = homo.getNumberColumns() - 2; c > 0; c--) {
                if( map.isNoData( r-1, c-1) != true &&
                        map.isNoData( r-1, c) != true &&
                        map.isNoData( r-1, c+1) != true &&
                        map.isNoData( r, c-1) != true &&
                        map.isNoData( r, c) != true &&
                        map.isNoData( r, c+1) != true &&
                        map.isNoData( r+1, c-1) != true &&
                        map.isNoData( r+1, c) != true &&
                        map.isNoData( r+1, c+1) != true) {
                    int value = map.getCellValue( r, c);
                    if( map.getCellValue( r-1, c-1) == value &&
                        map.getCellValue( r-1, c) == value &&
                        map.getCellValue( r-1, c+1) == value &&
                        map.getCellValue( r, c-1) == value &&
                        map.getCellValue( r, c+1) == value &&
                        map.getCellValue( r+1, c-1) == value &&
                        map.getCellValue( r+1, c) == value &&
                        map.getCellValue( r+1, c+1) == value) {
                        homo.setCellValue( r, c, value);
                    }
                }
            }
        }
        return homo;
    }

    /** this method returns a new GISClass with cell values equal to the original
     *  GISClass if and only if the grid cell has the same value as similarToCenter cells 
     *  queen's case adjacent grid cells. Otherwise, the grid cell will contain a no data 
     *  value. For example, if parameter similarToCenter equals 5, then 0-4 adjacent cells
     *  having the same value as the center will result in NO DATA while 5-8 adjacent cells
     *  having the same value as the center cell will result in the center cell's class.
     */
    public static GISClass threeByThreeHomogeneous( GISClass map, int similarToCenter) {
        GISClass homo = new GISClass( map);
        for( int r = homo.getNumberRows() - 2; r > 0; r--) {
            for( int c = homo.getNumberColumns() - 2; c > 0; c--) {
                if( map.isNoData( r, c) != true) {
                    int value = map.getCellValue( r, c);
                    int count = 0;
                    if( map.isNoData( r-1, c-1) != true && map.getCellValue( r-1, c-1) == value)
                        count++;
                    if( map.isNoData( r-1, c) != true && map.getCellValue( r-1, c) == value)
                        count++;
                    if( map.isNoData( r-1, c+1) != true && map.getCellValue( r-1, c+1) == value)
                        count++;
                    if( map.isNoData( r, c-1) != true && map.getCellValue( r, c-1) == value)
                        count++;
                    if( map.isNoData( r, c+1) != true && map.getCellValue( r, c+1) == value)
                        count++;
                    if( map.isNoData( r+1, c-1) != true && map.getCellValue( r+1, c-1) == value)
                        count++;
                    if( map.isNoData( r+1, c) != true && map.getCellValue( r+1, c) == value)
                        count++;
                    if( map.isNoData( r+1, c+1) != true && map.getCellValue( r+1, c+1) == value)
                        count++;
                    if( count >= similarToCenter)
                        homo.setCellValue( r, c, value);
                }
            }
        }
        return homo;
    }

	public static void main( String argv[]) throws IOException {
		if( argv.length < 3) {
			System.out.println( "ThreeByThreeFilter main program requires three arguments:");
			System.out.println( "java ThreeByThreeFilter inMap outMap numberOfCellsSimilarToCenter");
			System.exit( -1);
		}
		Date runStartDate = new Date();
		System.out.println( "Starting ThreeByThreeFilter.main() at: " + runStartDate);
		String outputMapName = argv[ 1];
		System.out.println( "ThreeByThreeFilter.main() building object");
		ThreeByThreeFilter cm = new ThreeByThreeFilter();
		cm.setInputMap( argv[ 0]);
		cm.setOutputMap( outputMapName);
		cm.setSimilarValue( new Integer( argv[2].trim()).intValue());
		Date doneDate = new Date();
		System.out.println( "ThreeByThreeFilter.main() finished making maps at: " + doneDate);
		double minutes = (doneDate.getTime() - runStartDate.getTime()) / 1000.0 / 60.0;
		System.out.println( "ThreeByThreeFilter.main() took " + minutes + " minutes to run.");
	}

	public ThreeByThreeFilter() {
		inputMapName = null;
		outputMapName = null;
		similarToCenter = 0;
		isValue = false;
	}

	public void checkReady() throws IOException {
		if( inputMapName != null && outputMapName != null && isValue == true) {
			runFilter();
		}
	}

	public void setSimilarValue( int value) throws IOException {
		similarToCenter = value;
		isValue = true;
		checkReady();
	}
	
	public void setInputMap( String name) throws IOException {
		inputMapName = name;
		checkReady();
	}
	
	public void setOutputMap( String name) throws IOException {
		outputMapName = name;
		checkReady();
	}

	private void runFilter() throws IOException {
		GISClass map = GISClass.loadEsriAscii( inputMapName);
		System.out.println( "Pre Filter information:");
		map.printGrid();
		
		GISClassView prt = new GISClassView(map);
		prt.printCategoryCount( inputMapName);
		System.out.println( "no data value: " + map.getNoDataValue());
		prt.printCategoryCount();
		map = threeByThreeHomogeneous( map, similarToCenter);
		System.out.println( "Post Filter information with [" + similarToCenter + "] identical adjacent cells:");
		map.printGrid();
		prt.printCategoryCount();
		prt.printCategoryCount( outputMapName);
		map.writeAsciiEsri( outputMapName);
		prt.printSignatureCount( outputMapName);
	}
}
