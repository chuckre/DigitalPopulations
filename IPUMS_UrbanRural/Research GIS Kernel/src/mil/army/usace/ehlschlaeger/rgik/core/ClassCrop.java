package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;



/**
 * Unset all cells that have values outside a range.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class ClassCrop {
    private int minValue, maxValue;

	public ClassCrop() {
		minValue = Integer.MIN_VALUE;
		maxValue = Integer.MAX_VALUE;
	}

	public ClassCrop( int minimumAttribute, int maximumAttribute) {
		minValue = minimumAttribute;
		maxValue = maximumAttribute;
	}

    /**
     * Process given map. Returns a new map that is only as big as the remaining
     * data. Original map is not modified.
     * 
     * @param originalMap
     *            map to process
     * @return new map with remaining data, or null if all cells were unset.
     */
    public GISClass crop(GISClass originalMap) {
        GISGrid cG = getBoundingGrid(originalMap);
        if( cG == null)
            return null;
        GISClass cc = new GISClass( cG, originalMap);

        // Unset cells outside range.
        for( int r = 0; r < cc.getNumberRows(); r++) {
            for( int c = 0; c < cc.getNumberColumns(); c++) {
                if( cc.isNoData( r, c) == false) {
                    int value = cc.getCellValue( r, c);
                    if( value < minValue || value > maxValue) {
                        cc.setNoData( r, c, true);
                    }
                }
            }
        }
        
        return cc;
    }
	
    /**
     * Determine the smallest rectangle that surrounds all of the cells within
     * our range. Returns a new GISGrid with matching resolution covering that
     * area, though with none of the no-data flags copied over.
     * 
     * @param map
     *            data to scan
     * @return a new GISGrid with matching resolution covering that area, though
     *         with none of the no-data flags copied over, or null if no cells
     *         are in range
     */
    public GISGrid getBoundingGrid(GISClass map) {
        int lowRow = -1;
        boolean done = false;
        while (done == false && lowRow < map.getNumberRows()) {
            lowRow++;
            for (int c = map.getNumberColumns() - 1; c >= 0; c--) {
                if (map.isNoData(lowRow, c) == false) {
                    int v = map.getCellValue(lowRow, c);
                    if(v >= minValue && v <= maxValue) {
                        done = true;
                        break;
                    }
                }
            }
        }
        if (lowRow == map.getNumberRows())
            return null;
        
        int hihRow = map.getNumberRows();
        done = false;
        while (done == false) {
            hihRow--;
            for (int c = map.getNumberColumns() - 1; c >= 0; c--) {
                if (map.isNoData(hihRow, c) == false) {
                    int v = map.getCellValue(hihRow, c);
                    if(v >= minValue && v <= maxValue) {
                        done = true;
                        break;
                    }
                }
            }
        }

        int lowCol = -1;
        done = false;
        while (done == false) {
            lowCol++;
            for (int r = hihRow; r >= lowRow; r--) {
                if (map.isNoData(r, lowCol) == false) {
                    int v = map.getCellValue(r, lowCol);
                    if(v >= minValue && v <= maxValue) {
                        done = true;
                        break;
                    }
                }
            }
        }
        
        int hihCol = map.getNumberColumns();
        done = false;
        while (done == false) {
            hihCol--;
            for (int r = hihRow; r >= lowRow; r--) {
                if (map.isNoData(r, hihCol) == false) {
                    int v = map.getCellValue(r, hihCol);
                    if(v >= minValue && v <= maxValue) {
                        done = true;
                        break;
                    }
                }
            }
        }
        
        double newWestEdge = map.getWestEdge() + lowCol * map.getEWResolution();
        double newNorthEdge = map.getNorthEdge() - lowRow * map.getNSResolution();
        int newRows = hihRow - lowRow + 1;
        int newCols = hihCol - lowCol + 1;
        
        GISGrid newGrid = new GISGrid(newWestEdge, newNorthEdge,
            map.getEWResolution(), map.getNSResolution(), newRows, newCols);
        return newGrid;
    }

    

    /**
     * Apply ClassCrop to a file via the command-line.
     * 
     * @param argv
     * @throws IOException
     */
	public static void main( String argv[]) throws IOException {
		if( argv.length < 2) {
			System.out.println( "ClassCrop main program requires at least two arguments:");
			System.out.println( "  java ClassCrop inMap outMap");
			System.out.println( "ClassCrop main program can also specify range of allowable attribute values:");
			System.out.println( "  java ClassCrop inMap outMap min_attribute max_attribute");
			System.exit( -1);
		} else {
			System.out.println( "RUNNING: ClassCrop " + argv[ 0] + " " + argv[ 1]);
		}
		String goalMapName = argv[ 1];
		String inMapName = argv[ 0];
		ClassCrop cm = null;
		if( argv.length == 2)
			cm = new ClassCrop();
		else {
			System.out.println( "RUNNING WITH CROPPING VALUES: " + (new Integer( argv[ 2])).intValue() +
				" and " + (new Integer( argv[ 3])).intValue());
			cm = new ClassCrop( (new Integer( argv[ 2])).intValue(), (new Integer( argv[ 3])).intValue());
		}
		GISClass cM = cm.crop( GISClass.loadEsriAscii( inMapName));
		if( cM == null) {
		    throw new DataException( "ClassCrop.setInMap ERROR: [" + inMapName + "] contained no data in given range");
		}
		cM.writeAsciiEsri( goalMapName);
	}
}
