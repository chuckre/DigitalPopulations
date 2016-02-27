package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;
import mil.army.usace.ehlschlaeger.rgik.util.Tree;
import mil.army.usace.ehlschlaeger.rgik.util.TreeObject;

/**
 * The AT watershed delineation algorithm (Ehlschlaeger, 1989).
 * 
 * @author Charles R. Ehlschlaeger
 */
public class WatershedDelineation {
    protected GISLattice lattice;

    public WatershedDelineation(GISLattice lattice) {
        this.lattice = lattice;
    }

    /** This method implements the AT watershed delineation algorithm (Ehlschlaeger, 1989).
     *  It returns GISClass of drainage flow.
     *  Drainage flow values indicate the angle of water flow (value * 45 degrees with 9
     *  indicating flow off the map.
     */
    public GISClass flowMap( BooleanGrid truePits) {
        boolean fourSideFlow = false;
        return( flowMap( truePits, fourSideFlow));
    }

//                         0   1   2   3   4   5  6  7  8  9
    private int[] dCol = { 1,  1,  0, -1, -1, -1, 0, 1, 1, 0 };
    private int[] dRow = { 0, -1, -1, -1,  0,  1, 1, 1, 0, 0 };
    private int[] sides = { 2, 4, 6, 8, 1, 3, 5, 7 };
    private int[] flowDir = { 6, 8, 2, 4, 5, 7, 1, 3 };

    /** This method implements the AT watershed delineation algorithm (Ehlschlaeger, 1989).
     *  It returns GISClass of drainage flow.
     *  Drainage flow values indicate the angle of water flow (value * 45 degrees with 9
     *  indicating flow off the map. If downhill gradient is greater than maximumDownhillSlope,
     *  the GISLattice's elevation values will be adjusted to make downhill gradient = 
     *  maximumDownhillSlope.
     */
    public GISClass flowMap( BooleanGrid truePits, boolean fourSideFlow) {
        int totalCells = lattice.getNumberRows() * lattice.getNumberColumns();
        int numInTree = 0;
        //System.out.println( "GISLattice.flowMap() pre null removal totalCells = " + totalCells);
        BooleanGrid done = new BooleanGrid( lattice.getNumberRows(), lattice.getNumberColumns(), false);
        Tree toDo = new Tree();
        for( int r = lattice.getNumberRows() - 1; r >= 0; r--) {
            if( lattice.isNoData( r, 0) == false) {
                toDo.insertNode( new CellObj( r, 0, lattice.getCellValue( r, 0), Double.POSITIVE_INFINITY, 9));
                numInTree++;
            } else {
                totalCells--;
                done.setBoolean( r, 0, true);
            }
            if( lattice.isNoData( r, lattice.getNumberColumns() - 1) == false) {
                toDo.insertNode( new CellObj( r, lattice.getNumberColumns() - 1, 
                    lattice.getCellValue( r, lattice.getNumberColumns() - 1), Double.POSITIVE_INFINITY, 9));
                numInTree++;
            } else {
                totalCells--;
                done.setBoolean( r, lattice.getNumberColumns() - 1, true);
            }
        }
        for( int c = lattice.getNumberColumns() - 2; c >= 1; c--) {
            if( lattice.isNoData( 0, c) == false) {
                toDo.insertNode( new CellObj( 0, c, lattice.getCellValue( 0, c), Double.POSITIVE_INFINITY, 9));
                numInTree++;
            } else {
                totalCells--;
                done.setBoolean( 0, c, true);
            }
            if( lattice.isNoData( lattice.getNumberRows() - 1, c) == false) {
                toDo.insertNode( new CellObj( lattice.getNumberRows() - 1, c, 
                    lattice.getCellValue( lattice.getNumberRows() - 1, c), Double.POSITIVE_INFINITY, 9));
                numInTree++;
            } else {
                totalCells--;
                done.setBoolean( lattice.getNumberRows() - 1, c, true);
            }
        }
        //System.out.println( "GISLattice.flowMap() post null removal outside totalCells = " + totalCells);
        //Date ddd = new Date();
        //System.out.println( "GISLattice.flowMap() pre truePits check at " + ddd.toString());
        if( truePits != null) {
            for( int r = lattice.getNumberRows() - 2; r >= 1; r--) {
                for( int c = lattice.getNumberColumns() - 2; c >= 1; c--) {
                    if( lattice.isNoData( 0, c) == false) {
                        if( truePits.getBoolean( r, c) == true) {
                            if( lattice.isNoData( 0, c) == false) {
                                toDo.insertNode( new CellObj( r, c, lattice.getCellValue( r, c), Double.POSITIVE_INFINITY, 9));
                                numInTree++;
                            }
                        } else {
                            for( int i = 0; i < 8; i++) {
                                int checkRow = r + dRow[i];
                                int checkCol = c + dCol[i];
                                if( lattice.isNoData( checkRow, checkCol) == true) {
                                    toDo.insertNode( new CellObj( r, c, lattice.getCellValue( r, c), Double.POSITIVE_INFINITY, 9));
                                    numInTree++;
                                    i = 10;
                                }
                            }
                        }
                    } else {
                        totalCells--;
                        done.setBoolean( r, c, true);
                    }
                }
            }
        } else {
            for( int r = lattice.getNumberRows() - 2; r >= 1; r--) {
                for( int c = lattice.getNumberColumns() - 2; c >= 1; c--) {
                    if( lattice.isNoData( r, c) == false) {
                        for( int i = 0; i < 8; i++) {
                            int checkRow = r + dRow[i];
                            int checkCol = c + dCol[i];
                            if( lattice.isNoData( checkRow, checkCol) == true) {
                                toDo.insertNode( new CellObj( r, c, lattice.getCellValue( r, c), Double.POSITIVE_INFINITY, 9));
                                numInTree++;
                                i = 10;
                            }
                        }
                    } else {
                        totalCells--;
                        done.setBoolean( r, c, true);
                    }
                }
            }
        }
        //System.out.println( "GISLattice.flowMap() post null removal inside totalCells = " + totalCells);
        Date startFindFlow = new Date();
        //System.out.println( "GISLattice.flowMap() post truePits check at " + startFindFlow.toString());
        //System.out.println( "GISLattice.flowMap() original # cells: " + (lattice.getNumberRows() * lattice.getNumberColumns()));
        System.out.println( "GISLattice.flowMap() cells to do: " + totalCells);
        int cells2doTotal = totalCells;
        double oldPercent2Do = 100.0;
        GISClass flow = new GISClass(lattice);
        CellObj cell = (CellObj) toDo.inorderFirst();
        double horzRes = lattice.getEWResolution();
        double vertRes = lattice.getNSResolution();
        double diagRes = Math.sqrt( horzRes * horzRes + vertRes * vertRes);
        double[] resArray = new double[ 9];
        resArray[ 0] = vertRes;
        resArray[ 1] = horzRes;
        resArray[ 2] = vertRes;
        resArray[ 3] = horzRes;
        for( int i = 4; i < 8; i++) {
            resArray[ i] = diagRes;
        }
        while( cell != null) {
            toDo.removeFirstInOrderNode();
            numInTree--;
            double percent2Do = (100.0 * totalCells) / cells2doTotal;
            if( percent2Do < (oldPercent2Do - 5.0)) {
                Date nowTime = new Date();
                //System.out.println( "numInTree:     " + numInTree);
                //System.out.println( "totalCells:    " + totalCells);
                //System.out.println( "cells2doTotal: " + cells2doTotal);
                //System.out.println( "nowTime:       " + nowTime.toString());
                //System.out.println( "millisecs:     " + (nowTime.getTime() - startFindFlow.getTime()));
                double ratio = (percent2Do / (100.0 - percent2Do));
                //System.out.println( "ratio:         " + ratio);
                Date estTime = new Date( (long) (nowTime.getTime() + 
                    (nowTime.getTime() - startFindFlow.getTime()) * ratio));
                if( percent2Do < 40) {
                    System.out.println( nowTime.toString() + ": " + Math.ceil( percent2Do) + 
                        "% to do, min. time of finish: " + estTime.toString());
                } else {
                    System.out.println( nowTime.toString() + ": " + Math.ceil( percent2Do) + 
                        "% to do, est. time of finish: " + estTime.toString());
                }
                oldPercent2Do = Math.ceil( percent2Do);
            }
            int cellRow = cell.getR();
            int cellCol = cell.getC();
            if( done.getBoolean( cellRow, cellCol) == false) {
                totalCells--;
                done.setBoolean( cellRow, cellCol, true);
                int flowDirection = cell.getFlow();
                if( flowDirection < 8) {
                    flow.setCellValue( cellRow, cellCol, flowDirection * 45);
                } else { 
                    flow.setCellValue( cellRow, cellCol, 8 - flowDirection);
                } 
                int doSides = 8;
                if( fourSideFlow == true)
                    doSides = 4;
                for( int ff = 0; ff < doSides; ff++) {
                    int f = sides[ ff];
                    int checkRow = cellRow + dRow[ f];
                    int checkCol = cellCol + dCol[ f];
                    if( lattice.isNoData( checkRow, checkCol) == false) {
                        if( done.getBoolean( checkRow, checkCol) == false) {
                            double z = lattice.getCellValue( checkRow, checkCol);
                            double downZ = lattice.getCellValue( cellRow, cellCol);
                            double thisRes = resArray[ ff];
                            // lower is better
                            double gradient = (downZ - z) / thisRes;
                            toDo.insertNode( new CellObj( 
                                checkRow, checkCol, z, gradient, flowDir[ ff]));
                            numInTree++;
                        }
                    }
                }
            }
            cell = (CellObj) toDo.inorderFirst();
        }
        return flow;
    }

    /*
        int flowCount = 0;
        int[] rowToDo = null;
        int[] colToDo = null;
        while( rowToDo == null || colToDo == null) {
            rowToDo = null;
            colToDo = null;
            for( int r = 0; r < flowMap.lattice.getNumberRows(); r++) {
                for( int c = 0; c < flowMap.lattice.getNumberColumns(); c++) {
                    if( flowMap.lattice.isNoData( r, c) == false) {
                        double h2oCell = cellArea;
                        if( waterInUnitArea != null) {
                            double easting = flowMap.getCellCenterEast( r, c);
                            double northing = flowMap.getCellCenterNorth( r, c);
                            if( waterInUnitArea.isNoData4Corners( easting, northing) == false) {
                                h2oCell = waterInUnitArea.getValue( easting, northing) * cellArea;
                            }
                        }
                        accumMap.setCellValue( r, c, h2oCell) + accumMap.lattice.getCellValue( r, c);
                        int flowValue = flowMap.getValue( r, c);
                        if( flowValue >= 0) {
                            // ZZZ equation below only works for D4 or D8.
                            flowValue = flowValue / 45;
                            int downRow = dRow[ flowValue] + r;
                            int downCol = dCol[ flowValue] + c;
                            if( flowMap.onMap( downRow, downCol)) {
                                if( downFlow.getBoolean( downRow, downCol == false) {
                                    flowCount++;
                                    downFlow.setBoolean( downRow, downCol, true);
                                }
                            }
                        }
                    }
                }
            }
            rt.gc();
        }
    */

        /* time for accum: replace stack overflow problem with long arrays of cells to do so loop in one method.
        Stack accum2doStack = new Stack();
        GISClass accum = new GISClass( flow);
        for( int r = 0; r < flow.lattice.getNumberRows(); r++) {
            for( int c = 1; c < flow.lattice.getNumberColumns(); c++) {
                if( flow.lattice.isNoData( r, c) == false) {
                    accum.setCellValue( r, c, 1);
                }
            }
        }
        // fill stack with every cell with value of 9
        for( int r = 0; r < flow.getNumberRows(); r++) {
            for( int c = 1; c < flow.lattice.getNumberColumns(); c++) {
                if( flow.isNoData( r, c) == false) {
                    if( flow.lattice.getCellValue( r, c) == 9) {
                        addBasin2Stack( flow, accum2doStack, fourSideFlow, r, c, 9);
                    }
                    CellAccumObj popped = null;
                    try {
                        popped = (CellAccumObj) accum2doStack.pop();
                    //while( popped != null) {
                        int cRow = popped.getR();
                        int cCol = popped.getC();
                        int v = popped.getValue();
                        if( v < 9) {
                            int dR = cRow + dRow[ v];
                            int dC = cCol + dCol[ v];
                            int dCellAccum = accum.lattice.getCellValue( dR, dC);
                            int cellAccum = accum.lattice.getCellValue( cRow, cCol);
                            accum.setCellValue( dR, dC, dCellAccum + cellAccum);
                        }
                        popped = (CellAccumObj) accum2doStack.pop();
                    //}
                    } catch ( EmptyListException e ) {
                    }
                }
            }
        }
        g[ 1] = accum;
        */

    /*
    private void addBasin2Stack( GISClass flow, Stack accum2doStack, boolean fourSideFlow, int r, int c, int f) {
        accum2doStack.push( new CellAccumObj( r, c, f));
        // for each cell, check adjacent to see whether adj cells flow into goingIntoStackCell
        int doSides = 8;
        if( fourSideFlow == true)
            doSides = 4;
        for( int ff = 0; ff < doSides; ff++) {
            f = sides[ ff];
            int checkRow = r + dRow[ f];
            int checkCol = c + dCol[ f];
            if( checkRow >= 0 && checkCol >= 0 && checkRow < getNumberRows() &&
                checkCol < getNumberColumns() && flow.isNoData( checkRow, checkCol) == false) {
                f = flow.getCellValue( checkRow, checkCol);
                if( r == checkRow + dRow[ f] && c == checkCol + dCol[ f]) {
                    addBasin2Stack( flow, accum2doStack, fourSideFlow, r, c, f);
                }
            }
        }
    }
*/



    class CellObj implements TreeObject {
        private int r, c, f;
        private double value, downValue;

        public CellObj( int r, int c, double value, double downValue, int flow) {
            this.value = value;
            this.downValue = downValue;
            this.r = r;
            this.c = c;
            f = flow;
        }

        /** compare should return a negative number if t is less than this, 0 if 
        *  t == this, and a positive number if t is greater than this.
        */
        public int compare( TreeObject t) {
            CellObj o = (CellObj) t;
            if( o.value < value)
                return -1;
            else if( o.value > value)
                return 1;
            else if( o.downValue < downValue)
                return -1;
            else if( o.downValue > downValue)
                return 1;
            return 0;
        }

        public TreeObject minimumObject() {
            CellObj o = new CellObj( -1, -1, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, -1);
            return( (TreeObject) o);
        }

        public TreeObject maximumObject() {
            CellObj o = new CellObj( -1, -1, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, -1);
            return( (TreeObject) o);
        }

        public void setR( int value) {
            r = value;
        }
        public void setC( int value) {
            c = value;
        }
        public int getR() {
            return r;
        }
        public int getC() {
            return c;
        }
        public void setValue( double value) {
            this.value = value;
        }
        public double getValue() {
            return value;
        }
        public void setDownValue( double value) {
            this.downValue = value;
        }
        public double getDownValue() {
            return downValue;
        }
        public void setFlow( int value) {
            f = value;
        }
        public int getFlow() {
            return f;
        }
    }
}
