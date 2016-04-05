package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;

/**
 * @author Chuck Ehlschlaeger in alpha testing. Copyright Charles R.
 *         Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 *         <http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 * @version 0.3
 */
public class AnnalsRandomSamples {
    private GISLattice original;
    private int        cellsInOriginal;

    public AnnalsRandomSamples(GISLattice originalMap) {
        original = originalMap;
        cellsInOriginal = 0;
        for (int r = 0; r < originalMap.getNumberRows(); r++) {
            for (int c = 0; c < originalMap.getNumberColumns(); c++) {
                if (originalMap.isNoData(r, c) == false) {
                    cellsInOriginal++;
                }
            }
        }
        System.out
                .println("AnnalsRandomSamples.AnnalsRandomSamples() complete");
    }

    public GISLattice getSample(double mapletWidth, double proportionMap) {
        System.out.println("AnnalsRandomSamples.getSample() starting");
        int cells2Do = (int) Math.min(cellsInOriginal, Math
                .ceil(cellsInOriginal * proportionMap));
        double ewRes = original.getEWResolution();
        double nsRes = original.getNSResolution();
//        double north = original.getNorthEdge();
//        double south = original.getSouthEdge();
//        double east = original.getEastEdge();
//        double west = original.getWestEdge();
//        double ew = east - west;
//        double ns = north - south;
        int rows = original.getNumberRows();
        int cols = original.getNumberColumns();
        int lessRow = (int) Math.round(mapletWidth / nsRes);
        int lessCol = (int) Math.round(mapletWidth / ewRes);
        GISLattice result = new GISLattice(original);
        while (cells2Do > 0) {
            int colCenter = (int) Math.floor(Math.random() * cols);
            int rowCenter = (int) Math.floor(Math.random() * rows);
            int minRow = Math.max(0, rowCenter - lessRow);
            int maxRow = Math.min(rows - 1, rowCenter + lessRow);
            int minCol = Math.max(0, colCenter - lessCol);
            int maxCol = Math.min(cols - 1, colCenter + lessCol);
            for (int r = minRow; r <= maxRow; r++) {
                for (int c = minCol; c <= maxCol; c++) {
                    if (original.isNoData(r, c) == false) {
                        if (result.isNoData(r, c) == true) {
                            result.setCellValue(r, c, original.getCellValue(r,
                                    c));
                            cells2Do--;
                        }
                    }
                }
            }
        }
        System.out.println("AnnalsRandomSamples.getSample() finishing");
        return (result);
    }

    public GISLattice getTestingSample(GISLattice measuringSample,
            int numberCells, double minDistancFromMeasuringSample) {
        System.out.println("AnnalsRandomSamples.getTestingSample() starting");
        GISLattice result = new GISLattice(measuringSample);
        int cells2Do = numberCells;
        int noDataCellsInMeasuringSample = 0;
        for (int r = 0; r < measuringSample.getNumberRows(); r++) {
            for (int c = 0; c < measuringSample.getNumberColumns(); c++) {
                if (measuringSample.isNoData(r, c) == true) {
                    noDataCellsInMeasuringSample++;
                }
            }
        }
        System.out
                .println("AnnalsRandomSamples.getTestingSample() starting distance calculation");
        if (noDataCellsInMeasuringSample < cells2Do) {
            String msg = "Not enouth no-data cells in measuringSample"
                + " [" + noDataCellsInMeasuringSample
                + "] for proper testing sample if [" + numberCells
                + "] are needed.";
            throw new DataException(msg);
        }
        GISLattice distanceMap = new GISLattice(measuringSample);
        BooleanGrid samples = new BooleanGrid(measuringSample.getNumberRows(),
                measuringSample.getNumberColumns(), false);
        for (int r = 0; r < measuringSample.getNumberRows(); r++) {
            for (int c = 0; c < measuringSample.getNumberColumns(); c++) {
                if (measuringSample.isNoData(r, c) == false) {
                    samples.setBoolean(r, c, true);
                }
            }
        }
        distanceMap.distanceMap(samples);
        System.out
                .println("AnnalsRandomSamples.getTestingSample() ending distance calculation");
        samples = null;
        int possibleCells = 0;
        for (int r = 0; r < measuringSample.getNumberRows(); r++) {
            for (int c = 0; c < measuringSample.getNumberColumns(); c++) {
                if (measuringSample.isNoData(r, c) == true) {
                    if (original.isNoData(r, c) == false) {
                        if (distanceMap.getCellValue(r, c) >= minDistancFromMeasuringSample) {
                            possibleCells++;
                            result.setCellValue(r, c, original.getCellValue(r,
                                    c));
                        }
                    }
                }
            }
        }
        if (possibleCells < numberCells) {
            System.out
                    .println("AnnalsRandomSamples.getTestingSample() WARNING: not enouth cells in measuringSample");
            System.out.println("  greater than ["
                    + minDistancFromMeasuringSample + "] distance away from");
            System.out.println("  measuringSample cells to get [" + numberCells
                    + "] testing sample cells.");
            System.out.println("  Returning all possible cells.");
            return result;
        }
        for (int r = 0; r < measuringSample.getNumberRows(); r++) {
            for (int c = 0; c < measuringSample.getNumberColumns(); c++) {
                if (result.isNoData(r, c) == false) {
                    double likelihood = ((double) numberCells) / possibleCells;
                    // System.out.print( "nC:" + numberCells + ",pC:" +
                    // possibleCells);
                    if (Math.random() > likelihood) {
                        result.setNoData(r, c, true);
                        // System.out.print( ",NO    ");
                    } else {
                        numberCells--;
                        // System.out.print( ",YES   ");
                    }
                    possibleCells--;
                }
            }
        }
        System.out.println("AnnalsRandomSamples.getTestingSample() finishing");
        return result;
    }

    /** This main tests the sharing function. 
     * @throws IOException */
    public static void main(String argv[]) throws IOException {
        System.out.println("running AnnalsRandomSamples");
        System.out.println("Choose quality map to make samples from");
        AnnalsRandomSamples ars = new AnnalsRandomSamples(GISLattice.loadEsriAscii("study_dem"));
        GISLattice measuringSample = ars.getSample(250., .05);
        measuringSample.writeAsciiEsri("measuringSample");
        GISLattice testSample = ars.getTestingSample(measuringSample, 200,
                1200.);
        testSample.writeAsciiEsri("testingSample");
    }
}
