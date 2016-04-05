package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.util.Stack;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/**
 * Extracted from GISClass; NOT TESTED.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class Rivers {
    private GISClass map;

    public Rivers(GISClass map) {
        this.map = map;
    }
    
    /** Creates a river map from a flowMap, from GISLattice.flowMap(). minRiverFlow
     *  is the amount of water that must flow through a cell before it is considered
     *  a river cell. Note: This function must run accumulateDownhill() to create
     *  an accumulation map, but will not save the results. If you intend to have
     *  an accumulation map, run GISLattice accumulationMap = flowMap.accumulateDownhill()
     *  followed by rivers( accumulationMap, minRiverFlow). If you have map representing
     *  surface water originating at each grid cell, you must run 
     *  flowMap.accumulateDownhill( GISLattice waterInUnitArea) first.
    **/
    public GISClass rivers( double minRiverFlow) {
        GISLattice accumulationMap = accumulateDownhill();
        return rivers( accumulationMap, minRiverFlow);
    }

    /** Creates a river map from a flowMap, from GISLattice.flowMap(). minRiverFlow
     *  is the amount of water that must flow through a cell before it is considered
     *  a river cell. The accumulation map is created by 
     *  GISLattice accumulationMap = flowMap.accumulateDownhill().
     *  If you have map representing surface water originating at each grid cell, 
     *  you must run flowMap.accumulateDownhill( GISLattice waterInUnitArea) first.
    **/
    public GISClass rivers( GISLattice accumulationMap, double minRiverFlow) {
        GISClass riversMap = new GISClass( map);
        for( int r = 0; r < map.getNumberRows(); r++) {
            for( int c = 0; c < map.getNumberColumns(); c++) {
                if( map.isNoData( r, c) == false) {
                    riversMap.setCellValue( r, c, 0);
                }
            }
        }
        for( int r = 0; r < map.getNumberRows(); r++) {
            for( int c = 0; c < map.getNumberColumns(); c++) {
                if( map.isNoData( r, c) == false) {
                    if( map.getCellValue( r, c) == -1) {
                        rivers( minRiverFlow, riversMap, accumulationMap, r, c);
                    }
                }
            }
        }
        return riversMap;
    }

    private void rivers( double minRiverFlow, GISClass riversMap, 
            GISLattice accumulationMap, int startR, int startC) { 
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        Stack<BasinUpStackObject> accum2doStack = new Stack<BasinUpStackObject>();
        BasinUpStackObject firstBSO = new BasinUpStackObject( startR, startC, 0.0);
        accum2doStack.push( firstBSO);
        while( accum2doStack.isEmpty() == false) {
            BasinUpStackObject bso = null;
            bso = accum2doStack.pop();
            if( accumulationMap.getCellValue( bso.r, bso.c) >= minRiverFlow) {
                riversMap.setCellValue( bso.r, bso.c, 1);
                for( int i = 0; i < 8; i++) {
                    int checkR = -1 * dRow[ i] + bso.r;
                    int checkC = -1 * dCol[ i] + bso.c;
                    if( map.isNoData( checkR, checkC) == false) {
                        int flow = map.getCellValue( checkR, checkC);
                        if( flow / 45 == i && flow != -1) {
                            BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, 0.0);
                            accum2doStack.push( newBUSO);
                        }
                    }
                }
            }
        }
    }

    /** Creates an accumulation map from a flowMap, from flowMap().
     **/
     public GISLattice accumulateDownhill() {
         return accumulateDownhill( null);
     }

     /** Creates an accumulation map from a flowMap, from flowMap().
     **/
     public GISLattice accumulateDownhill( GISLattice waterInUnitArea) {
         GISLattice accumMap = new GISLattice( map);
         for( int r = 0; r < map.getNumberRows(); r++) {
             for( int c = 0; c < map.getNumberColumns(); c++) {
                 if( map.isNoData( r, c) == false) {
                     if( map.getCellValue( r, c) == -1) {
                         accumulateDownhill( waterInUnitArea, accumMap, r, c);
                     }
                 }
             }
         }
         return accumMap;
     }

//                          0   1   2   3   4   5  6  7  8  9
     private int[] dCol = { 1,  1,  0, -1, -1, -1, 0, 1, 1, 0 };
     private int[] dRow = { 0, -1, -1, -1,  0,  1, 1, 1, 0, 0 };

 //  private int[] sides = { 2, 4, 6, 8, 1, 3, 5, 7 };
 //  private int[] flowDir = { 6, 8, 2, 4, 5, 7, 1, 3 };
     private static double NO_UP_FLOW = -2.0;
     private static double YES_UP_FLOW = -1.0;

     private void accumulateDownhill( GISLattice waterInUnitArea, 
             GISLattice accumMap, int startR, int startC) {
         if( waterInUnitArea != null) {
             for( int r = 0; r < waterInUnitArea.getNumberRows(); r++) {
                 for( int c = 0; c < waterInUnitArea.getNumberColumns(); c++) {
                     if( waterInUnitArea.isNoData( r, c) == true) {
                             waterInUnitArea.setCellValue( r, c, 0.0);
                     }
                 }
             }
         }
         double cellArea = map.getEWResolution() * map.getNSResolution();
         Runtime rt = Runtime.getRuntime();
         rt.gc();
         double[] fs = new double[ 8];
         boolean upflows = getUpFlows( startR, startC, fs);
         if( upflows == true) {
             Stack<BasinDownStackObject> accum2doStack = new Stack<BasinDownStackObject>();
             BasinDownStackObject firstBSO = new BasinDownStackObject( startR, startC, fs);
             accum2doStack.push( firstBSO);
             while( accum2doStack.isEmpty() == false) {
                 BasinDownStackObject bso = null;
                 bso = accum2doStack.pop();
                 int upflow = -1;
                 double sumFlows = 0.0;
                 for( int i = 0; i < 8; i++) {
                     if( bso.upflows[ i] == YES_UP_FLOW) {
                         upflow = i;
                         //bso.upflows[ i] = NO_UP_FLOW;
                         i = 8;
                     } else if( bso.upflows[ i] != NO_UP_FLOW) {
                         sumFlows += bso.upflows[ i];
                     }
                 }
                 if( upflow == -1) {
                     double waterInCell = cellArea;
                     if( waterInUnitArea != null) {
                         double e = map.getCellCenterEasting( startR, startC);
                         double n = map.getCellCenterNorthing( startR, startC);
                         if( waterInUnitArea.onMap( e, n) && 
                                 waterInUnitArea.isNoData4Corners( e, n) == false) {
                             waterInCell = cellArea * waterInUnitArea.getValue( e, n);
                         } else {
                             waterInCell = 0.0;
                         }
                     }
                     waterInCell += sumFlows;
                     accumMap.setCellValue( bso.r, bso.c, waterInCell);
                     if( accum2doStack.isEmpty() == false) {
                         BasinDownStackObject bso2 = null;
                         bso2 = accum2doStack.pop();
                         boolean isUpFlow = false;
                         for( int i = 0; i < 8; i++) {
                             if( bso2.upflows[ i] == YES_UP_FLOW) {
                                 bso2.upflows[ i] = waterInCell;
                                 isUpFlow = true;
                                 i = 10;
                             }
                         }
                         if(!isUpFlow) {
                             throw new DataException("isUpFlow == false: should never happen");
                         }
                         accum2doStack.push( bso2);
                     }
                 } else {
                     int upR = -1 * dRow[ upflow] + bso.r;
                     int upC = -1 * dCol[ upflow] + bso.c; 
                     accum2doStack.push( bso);
                     double[] fs2 = new double[ 8];
                     getUpFlows( upR, upC, fs2);
                     BasinDownStackObject bso3 = new BasinDownStackObject( upR, upC, fs2);
                     accum2doStack.push( bso3);
                 }
             }
         } else {
             double waterInCell = cellArea;
             if( waterInUnitArea != null) {
                 double e = map.getCellCenterEasting( startR, startC);
                 double n = map.getCellCenterNorthing( startR, startC);
                 if( waterInUnitArea.onMap( e, n) && 
                         waterInUnitArea.isNoData4Corners( e, n) == false) {
                     waterInCell = waterInUnitArea.getValue( e, n);
                 } else {
                     waterInCell = 0.0;
                 }
             }
             accumMap.setCellValue( startR, startC, waterInCell);
         }
     }

    private boolean getUpFlows(int r, int c, double[] flows) {
        // System.out.println( "upflows for r: " + r + ", c: " + c);
        boolean isUpFlows = false;
        for (int i = 0; i < 8; i++) {
            flows[i] = NO_UP_FLOW;
            int checkR = -1 * dRow[i] + r;
            int checkC = -1 * dCol[i] + c;
            if (map.isNoData(checkR, checkC) == false) {
                int flow = map.getCellValue(checkR, checkC);
                if (flow / 45 == i && flow != -1) {
                    // System.out.println( "        i: " + i + ", checkR: " +
                    // checkR + ", checkC: " + checkC);
                    flows[i] = YES_UP_FLOW;
                    isUpFlows = true;
                }
            }
        }
        return isUpFlows;
    }

    /** Creates an accumulation map going uphill from a flowMap, from GISLattice.flowMap().
     **/
     public GISLattice accumulateUphill( GISClass riverMap) {
         return accumulateUphill( null, riverMap);
     }

     /** Creates an accumulation map going uphill from a flowMap, from GISLattice.flowMap().
     **/
     public GISLattice accumulateUphill( GISLattice waterInUnitArea, GISClass riverMap) {
         GISLattice accumMap = new GISLattice( map);
         for( int r = 0; r < map.getNumberRows(); r++) {
             for( int c = 0; c < map.getNumberColumns(); c++) {
                 if( map.isNoData( r, c) == false) {
                     if( map.getCellValue( r, c) == -1 && riverMap.getCellValue( r, c) > 0) {
                         accumulateUphill( waterInUnitArea, riverMap, accumMap, r, c);
                     }
                 }
             }
         }
         return accumMap;
     }

     private void accumulateUphill( GISLattice waterInUnitArea, GISClass riverMap,
                 GISLattice accumMap, int startR, int startC) {
         if( waterInUnitArea != null) {
             for( int r = 0; r < waterInUnitArea.getNumberRows(); r++) {
                 for( int c = 0; c < waterInUnitArea.getNumberColumns(); c++) {
                     if( waterInUnitArea.isNoData( r, c) == true) {
                             waterInUnitArea.setCellValue( r, c, 0.0);
                     }
                 }
             }
         }
         double cellArea = map.getEWResolution() * map.getNSResolution();
         Runtime rt = Runtime.getRuntime();
         rt.gc();
         Stack<BasinUpStackObject> accum2doStack = new Stack<BasinUpStackObject>();
         BasinUpStackObject firstBSO = new BasinUpStackObject( startR, startC, 0.0);
         accum2doStack.push( firstBSO);
         while( accum2doStack.isEmpty() == false) {
             BasinUpStackObject bso = null;
             bso = accum2doStack.pop();
             double waterInCell = cellArea;
             if( waterInUnitArea != null) {
                 double e = map.getCellCenterEasting( bso.r, bso.c);
                 double n = map.getCellCenterNorthing( bso.r, bso.c);
                 if( waterInUnitArea.onMap( e, n) && 
                         waterInUnitArea.isNoData4Corners( e, n) == false) {
                     waterInCell = waterInUnitArea.getValue( e, n) * cellArea;
                 } else {
                     waterInCell = 0.0;
                 }
             }
             double cellFlow = bso.downFlow + waterInCell;
             if( riverMap.getCellValue( bso.r, bso.c) > 0) {
                 cellFlow = 0.0;
             }
             accumMap.setCellValue( bso.r, bso.c, cellFlow);
             int upflows = 0;
             for( int i = 0; i < 8; i++) {
                 int checkR = -1 * dRow[ i] + bso.r;
                 int checkC = -1 * dCol[ i] + bso.c;
                 if( map.isNoData( checkR, checkC) == false) {
                     int flow = map.getCellValue( checkR, checkC);
                     if( flow / 45 == i && flow != -1) {
                         upflows++;
                     }
                 }
             }
             for( int i = 0; i < 8; i++) {
                 int checkR = -1 * dRow[ i] + bso.r;
                 int checkC = -1 * dCol[ i] + bso.c;
                 if( map.isNoData( checkR, checkC) == false) {
                     int flow = map.getCellValue( checkR, checkC);
                     if( flow / 45 == i && flow != -1) {
                         // old BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, cellFlow / upflows);
                         BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, cellFlow);
                         accum2doStack.push( newBUSO);
                     }
                 }
             }
         }
     }

     /** Creates a basin map from a flowMap, from GISLattice.flowMap() with a
      *  binary map of rivers as a parameter.
     **/
     public GISClass basins( GISClass rivers) {
         return basins( rivers, null);
     }

     /** Creates a basin map from a flowMap, from GISLattice.flowMap() with a
      *  binary map of rivers as a parameter. GISClass terminalBasins should have
      *  the same resolution and extents as the flowMap and rivers. terminalBasins
      *  will be overwritten with 0 or basins values if those basins are terminal.
      *  "Terminal basins" do not have any basins which flow into them.
     **/
     public GISClass basins( GISClass rivers, GISClass terminalBasins) {
         GISClass basinsMap = new GISClass( rivers);
         for( int r = 0; r < map.getNumberRows(); r++) {
             for( int c = 0; c < map.getNumberColumns(); c++) {
                 if( map.isNoData( r, c) == false) {
                     basinsMap.setCellValue( r, c, 0);
                     if( terminalBasins != null) {
                         terminalBasins.setCellValue( r, c, 0);
                     }
                 }
             }
         }
         Stack<BasinUpStackObject> riverBasin2doStack = new Stack<BasinUpStackObject>();
         int basinNumber = 0;
         for( int r = 0; r < map.getNumberRows(); r++) {
             for( int c = 0; c < map.getNumberColumns(); c++) {
                 if( map.isNoData( r, c) == false) {
                     if( map.getCellValue( r, c) == -1 && rivers.getCellValue( r, c) == 1) {
                         BasinUpStackObject bso = new BasinUpStackObject( r, c, 0.0);
                         riverBasin2doStack.push( bso);
                     }
                 }
             }
         }
         while( riverBasin2doStack.isEmpty() == false) {
             BasinUpStackObject bso2 = null;
             bso2 = riverBasin2doStack.pop();
             basinNumber += 2;
             boolean terminal = doBasin( riverBasin2doStack, rivers, basinsMap, bso2.r, bso2.c, basinNumber);
             if( terminal == true && terminalBasins != null) {
                 doTerminalBasin( rivers, terminalBasins, bso2.r, bso2.c, basinNumber);
             }
         }
         return basinsMap;
     }

     private void doTerminalBasin( GISClass riversMap, GISClass terminalBasins, 
             int startR, int startC, int basinNumber) { 
         Runtime rt = Runtime.getRuntime();
         rt.gc();
         Stack<BasinUpStackObject> accum2doStack = new Stack<BasinUpStackObject>();
         BasinUpStackObject firstBSO = new BasinUpStackObject( startR, startC, 0.0);
         accum2doStack.push( firstBSO);
         while( accum2doStack.isEmpty() == false) {
             BasinUpStackObject bso = null;
             bso = accum2doStack.pop();
             terminalBasins.setCellValue( bso.r, bso.c, basinNumber);
             for( int i = 0; i < 8; i++) {
                 int checkR = -1 * dRow[ i] + bso.r;
                 int checkC = -1 * dCol[ i] + bso.c;
                 if( map.isNoData( checkR, checkC) == false) {
                     int flow = map.getCellValue( checkR, checkC);
                     if( flow / 45 == i && flow != -1) {
                         BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, 0.0);
                         accum2doStack.push( newBUSO);
                     }
                 }
             }
         }
     }

     private boolean doBasin( Stack<BasinUpStackObject> riverBasin2doStack, GISClass riversMap, GISClass basinsMap, 
             int startR, int startC, int basinNumber) { 
         Runtime rt = Runtime.getRuntime();
         rt.gc();
         Stack<BasinUpStackObject> accum2doStack = new Stack<BasinUpStackObject>();
         BasinUpStackObject firstBSO = new BasinUpStackObject( startR, startC, 0.0);
         accum2doStack.push( firstBSO);
         boolean terminal = true;
         while( accum2doStack.isEmpty() == false) {
             BasinUpStackObject bso = null;
             bso = accum2doStack.pop();
             int numberUpRivers = 0;
             basinsMap.setCellValue( bso.r, bso.c, basinNumber);
             // finds up rivers w/ flows
             for( int i = 0; i < 8; i++) {
                 int checkR = -1 * dRow[ i] + bso.r;
                 int checkC = -1 * dCol[ i] + bso.c;
                 if( map.isNoData( checkR, checkC) == false) {
                     int flow = map.getCellValue( checkR, checkC);
                     if( flow / 45 == i && flow != -1) {
                         if( riversMap.getCellValue( checkR, checkC) > 0) {
                             numberUpRivers++;
                         }
                     }
                 }
             }
             // if upRivers <= 1, continue
             if( numberUpRivers <= 1) {
                 for( int i = 0; i < 8; i++) {
                     int checkR = -1 * dRow[ i] + bso.r;
                     int checkC = -1 * dCol[ i] + bso.c;
                     if( map.isNoData( checkR, checkC) == false) {
                         int flow = map.getCellValue( checkR, checkC);
                         if( flow / 45 == i && flow != -1) {
                             BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, 0.0);
                             accum2doStack.push( newBUSO);
                         }
                     }
                 }
             } else { // else, save upRiver start locations with riverBasin2doStack.push() 
                 terminal = false;
                 for( int i = 0; i < 8; i++) {
                     int checkR = -1 * dRow[ i] + bso.r;
                     int checkC = -1 * dCol[ i] + bso.c;
                     if( map.isNoData( checkR, checkC) == false) {
                         int flow = map.getCellValue( checkR, checkC);
                         if( flow / 45 == i && flow != -1) {
                             BasinUpStackObject newBUSO = new BasinUpStackObject( checkR, checkC, 0.0);
                             if( riversMap.getCellValue( checkR, checkC) > 0) {
                                 riverBasin2doStack.push( newBUSO);
                             } else {
                                 accum2doStack.push( newBUSO);
                             }
                         }
                     }
                 }
             }
         }
         return terminal;
     }

}



class BasinDownStackObject {
    int      r;
    int      c;
    double[] upflows;

    public BasinDownStackObject(int row, int col, double[] upFlows) {
        r = row;
        c = col;
        upflows = upFlows;
    }
}



class BasinUpStackObject {
    int    r;
    int    c;
    double downFlow;

    public BasinUpStackObject(int row, int col, double downFlowSum) {
        r = row;
        c = col;
        downFlow = downFlowSum;
    }
}
