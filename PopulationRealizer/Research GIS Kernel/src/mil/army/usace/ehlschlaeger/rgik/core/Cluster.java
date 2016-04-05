package mil.army.usace.ehlschlaeger.rgik.core;

import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;




/**
 * ??
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger 
 */
public class Cluster extends RGIS {
    private int         numCells;
    private double[]    mean, sd;
    private GISLattice  maps[];
    private BooleanGrid doCell;

    public Cluster(GISLattice[] maps) {
        super();
        this.maps = maps;
        mean = new double[maps.length];
        sd = new double[maps.length];
        for (int m = maps.length - 1; m >= 0; m--) {
            if (maps[m] == null) {
                throw new NullPointerException("maps[" + m + "]");
            }
            if (maps[m].equalsGrid(maps[0]) == false) {
                throw new DataException("maps[" + m
                        + "] has dissimilar metadata from maps[0]");
            }
            mean[m] = 0.0;
            sd[m] = 0.0;
        }
        numCells = 0;
        doCell = new BooleanGrid(maps[0].getNumberRows(),
                                 maps[0].getNumberColumns(), true);
        for (int r = maps[0].getNumberRows() - 1; r >= 0; r--) {
            for (int c = maps[0].getNumberColumns() - 1; c >= 0; c--) {
                for (int m = maps.length - 1; m >= 0; m--) {
                    if (maps[m].isNoData(r, c) == true) {
                        doCell.setBoolean(r, c, false);
                        m = -1;
                    }
                }
                if (doCell.getBoolean(r, c) == true) {
                    numCells++;
                    for (int m = maps.length - 1; m >= 0; m--) {
                        double value = maps[m].getCellValue(r, c);
                        mean[m] += value;
                        sd[m] += value * value;
                    }
                }
            }
        }
        if (numCells == 0) {
            throw new DataException("there are no valid cells in maps.");
        }
        for (int m = maps.length - 1; m >= 0; m--) {
            sd[m] = Math
                        .sqrt((sd[m] - mean[m] * mean[m] / numCells) / numCells);
            mean[m] /= numCells;
        }
//        System.out.println("Object created:");
//        for (int m = maps.length - 1; m >= 0; m--) {
//            System.out.println(" map[" + m + "]: mean: " + mean[m] + ", sd: "
//                    + sd[m]);
//        }
    }

    public GISClass getCluster(int numberClasses) {
        assert numberClasses >= 1;
        GISClass classes = new GISClass(maps[0]);
        int[] cellsInClass = new int[numberClasses];
        double[][] meanClass = new double[numberClasses][maps.length];
        for (int cl = numberClasses - 1; cl >= 1; cl--) {
            for (int m = maps.length - 1; m >= 0; m--) {
                meanClass[cl][m] = 0.0;
                cellsInClass[cl] = 0;
            }
        }
        for (int m = maps.length - 1; m >= 0; m--) {
            meanClass[0][m] = mean[m];
        }
        cellsInClass[0] = numCells;
        int maxOutRow = -1;
        int maxOutCol = -1;
        double maxOutDistance = -1.0;
        for (int r = classes.getNumberRows() - 1; r >= 0; r--) {
            for (int c = classes.getNumberColumns() - 1; c >= 0; c--) {
                if (doCell.getBoolean(r, c) == true) {
                    double outDistance = 0.0;
                    for (int m = maps.length - 1; m >= 0; m--) {
                        double value = maps[m].getCellValue(r, c);
                        double thisDistance = (value - mean[m]) / sd[m];
                        outDistance += thisDistance * thisDistance;
                    }
                    classes.setCellValue(r, c, 0);
                    if (outDistance > maxOutDistance) {
                        maxOutRow = r;
                        maxOutCol = c;
                        maxOutDistance = outDistance;
                    }
                }
            }
        }
        for (int cl = 1; cl < numberClasses; cl++) {
            // change outlier cell to new class and adjust statistics.
            int oldClass = classes.getCellValue(maxOutRow, maxOutCol);
            classes.setCellValue(maxOutRow, maxOutCol, cl);
            for (int m = maps.length - 1; m >= 0; m--) {
                double value = maps[m].getCellValue(maxOutRow, maxOutCol);
                meanClass[oldClass][m] = (meanClass[oldClass][m]
                        * cellsInClass[oldClass] - value)
                        / (cellsInClass[oldClass] - 1);
                meanClass[cl][m] = (meanClass[cl][m] * cellsInClass[cl] + value)
                        / (cellsInClass[cl] + 1);
            }
            cellsInClass[oldClass]--;
            cellsInClass[cl]++;

            // move cells to new class if those cells are closer to new class
            for (int r = classes.getNumberRows() - 1; r >= 0; r--) {
                for (int c = classes.getNumberColumns() - 1; c >= 0; c--) {
                    if (doCell.getBoolean(r, c) == true) {
                        double distance2existingClass = 0.0;
                        double distance2newClass = 0.0;
                        int thisClass = classes.getCellValue(r, c);
                        if (thisClass != cl) {
                            for (int m = maps.length - 1; m >= 0; m--) {
                                double value = maps[m].getCellValue(r, c);
                                double thisDistance = (value - meanClass[thisClass][m])
                                        / sd[m];
                                distance2existingClass += thisDistance
                                        * thisDistance;
                                thisDistance = (value - meanClass[cl][m])
                                        / sd[m];
                                distance2newClass += thisDistance
                                        * thisDistance;
                            }
                            if (distance2existingClass > distance2newClass) {
                                for (int m = maps.length - 1; m >= 0; m--) {
                                    double value = maps[m].getCellValue(r, c);
                                    meanClass[thisClass][m] = (meanClass[thisClass][m]
                                            * cellsInClass[thisClass] - value)
                                            / (cellsInClass[thisClass] - 1);
                                    meanClass[cl][m] = (meanClass[cl][m]
                                            * cellsInClass[cl] + value)
                                            / (cellsInClass[cl] + 1);
                                }
                                cellsInClass[thisClass]--;
                                cellsInClass[cl]++;
                                classes.setCellValue(r, c, cl);
                            }
                        }
                    }
                }
            }

            // find next cell that is biggest outlier
            maxOutRow = -1;
            maxOutCol = -1;
            maxOutDistance = -1.0;
            for (int r = classes.getNumberRows() - 1; r >= 0; r--) {
                for (int c = classes.getNumberColumns() - 1; c >= 0; c--) {
                    if (doCell.getBoolean(r, c) == true) {
                        double outDistance = 0.0;
                        int thisClass = classes.getCellValue(r, c);
                        for (int m = maps.length - 1; m >= 0; m--) {
                            double value = maps[m].getCellValue(r, c);
                            double thisDistance = (value - meanClass[thisClass][m])
                                    / sd[m];
                            outDistance += thisDistance * thisDistance;
                        }
                        if (outDistance > maxOutDistance) {
                            maxOutRow = r;
                            maxOutCol = c;
                            maxOutDistance = outDistance;
                        }
                    }
                }
            }
            /*
             * System.out.println( "end for creating class [" + cl + "]"); for(
             * int cc = 0; cc < numberClasses; cc++) { System.out.println(
             * "class[" + cc + "]:"); for( int m = maps.length - 1; m >= 0; m--)
             * { System.out.println( " map[" + m + "]: mean: " + meanClass[ cc][
             * m] + ", cells: " + cellsInClass[ cc]); } }
             */
        }

        // swap cells between classes if they are closer to other class
        int swappedCells = -1;
        while (swappedCells != 0) {
            swappedCells = 0;
            for (int r = classes.getNumberRows() - 1; r >= 0; r--) {
                for (int c = classes.getNumberColumns() - 1; c >= 0; c--) {
                    if (doCell.getBoolean(r, c) == true) {
                        double distance2existingClass = 0.0;
                        int thisClass = classes.getCellValue(r, c);
                        for (int m = maps.length - 1; m >= 0; m--) {
                            double value = maps[m].getCellValue(r, c);
                            double thisDistance = (value - meanClass[thisClass][m])
                                    / sd[m];
                            distance2existingClass += thisDistance
                                    * thisDistance;
                        }
                        for (int oc = 0; oc < numberClasses; oc++) {
                            if (thisClass != oc) {
                                double distance2otherClass = 0.0;
                                for (int m = maps.length - 1; m >= 0; m--) {
                                    double value = maps[m].getCellValue(r, c);
                                    double thisDistance = (value - meanClass[oc][m])
                                            / sd[m];
                                    distance2otherClass += thisDistance
                                            * thisDistance;
                                }
                                if (distance2existingClass > distance2otherClass) {
                                    for (int m = maps.length - 1; m >= 0; m--) {
                                        double value = maps[m].getCellValue(r,
                                                                            c);
                                        meanClass[thisClass][m] = (meanClass[thisClass][m]
                                                * cellsInClass[thisClass] - value)
                                                / (cellsInClass[thisClass] - 1);
                                        meanClass[oc][m] = (meanClass[oc][m]
                                                * cellsInClass[oc] + value)
                                                / (cellsInClass[oc] + 1);
                                    }
                                    cellsInClass[thisClass]--;
                                    cellsInClass[oc]++;
                                    classes.setCellValue(r, c, oc);
                                    oc = numberClasses;
                                    swappedCells++;
                                }
                            }
                        }
                    }
                }
            }
            //System.out.println("cells swapped this round: " + swappedCells);
            /*
             * for( int cc = 0; cc < numberClasses; cc++) { System.out.println(
             * "class[" + cc + "]:"); for( int m = maps.length - 1; m >= 0; m--)
             * { System.out.println( " map[" + m + "]: mean: " + meanClass[ cc][
             * m] + ", cells: " + cellsInClass[ cc]); } }
             */
        }
        // classes.printClass();
        return classes;
    }
}
