package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.ConflatePumsQueryWithTracts;
import mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml.ProgressToy;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointSymbol;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointSymbolSquare;
import mil.army.usace.ehlschlaeger.rgik.core.RGISData;
import mil.army.usace.ehlschlaeger.rgik.util.DistanceDecay;
import mil.army.usace.ehlschlaeger.rgik.util.LinearDecay;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;
import mil.army.usace.ehlschlaeger.rgik.util.TimeTracker;
import mil.army.usace.ehlschlaeger.rgik.util.TransformDouble;



/**
 * In the realized maps, if the point is not an event, it has value of 1
 * (negative) In the realized maps, if the point is an event, has value of 2
 * (positive) In the realized maps, if the point is an event but realized as not
 * an event, has value of -2 (false negative) In the realized maps, if the point
 * is not an event but realized as an event, has value of -1 (false positive)
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class ConditionalPointClusterDetector {
    // Capture SVN metadata as strings so we can embed in log files.
    // The regex removes the SVN cruft and leaves the bare data.
    /** Version number of last change. */
    public static final String SVN_REV = ObjectUtil.extract("$Revision: 1 $", "\\$\\w+:\\s(.*)\\s\\$", 1);
    /** Last date this file was changed. */
    public static final String SVN_DATE = ObjectUtil.extract("$Date: 2011-04-01 23:09:26 -0700 (Fri, 01 Apr 2011) $", "\\$\\w+:.*\\((.*)\\)\\s\\$", 1);

    protected static Logger log = Logger.getLogger(ConflatePumsQueryWithTracts.class.getPackage().getName());

    // -- Inputs -- //
    private File eventTable;
    private double maximumClusterDistance;
    private double incrementDistance;
    private GISGrid grid2check;
    private double distanceDecayExponent;
    private boolean searchHighRate;
    private boolean saveRealizations;
    private List<File> populationFiles;
    private Random random = new Random();

    // -- Workspace -- //
    private GISPointQuadTree<GISPoint>[] simulatedPopulations;
    private GISPointQuadTree<GISPoint>   events;
    private GISLattice         eventClusterDensityMean, eventClusterDensitySD,
            pScore;
    private GISLattice         eventClusterDistance;
    private GISLattice         maximumLikelihood;
    
    /** point is not an event, has value of 1 */
    private GISPointSymbol     negPointSymbol;
    /** point is an event, has value of 2 */
    private GISPointSymbol     posPointSymbol;
    /** point is an event but realized as not an event, has value of -2 */
    private GISPointSymbol     falseNegPointSymbol;
    /** point is not an event but realized as an event, has value of -1 */
    private GISPointSymbol     falsePosPointSymbol;

    /**
     * Create fully configured instance.
     * 
     * @param eventTable
     * @param maximumClusterDistance
     * @param incrementDistance
     * @param grid2check
     * @param distanceDecayExponent
     * @param searchHighRate
     *            true to search for high-rate clusters; false to search for
     *            low-rate clusters (i.e. voids)
     * @param saveRealizations
     * @param populationFiles
     */
    public ConditionalPointClusterDetector( File eventTable,
            double maximumClusterDistance, double incrementDistance, GISGrid grid2check,
            double distanceDecayExponent, boolean searchHighRate, boolean saveRealizations,
            List<File> populationFiles) {
        this.eventTable = eventTable;
        this.maximumClusterDistance = maximumClusterDistance;
        this.incrementDistance = incrementDistance;
        this.grid2check = grid2check;
        this.distanceDecayExponent = distanceDecayExponent;
        this.searchHighRate = searchHighRate;
        this.saveRealizations = saveRealizations;
        this.populationFiles = populationFiles;
    }

    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        random = source;
    }
    
	/**
	 * Run process as currently configured.
	 * @throws IOException 
	 */
    public void go() throws IOException {
	    int numberOfFiles = populationFiles.size();

		TimeTracker.start(getClass().getSimpleName());
		
//		if( numberPopulationFiles < 20) {
//		    throw new DataException("numberPopulationFiles < 20."
//		                            + "  The number of populations should be REALLY LARGE for an accurate assessment."
//		                            + "  If number is below 20, we cannot assume a normal distribution for the z score.");
//		}
		if( eventTable == null) {
		    throw new DataException("eventTable == null.");
		}
		if( maximumClusterDistance < 0.0) {
		    throw new DataException("maximumClusterDistance < 0.0.");
		}
		if( incrementDistance <= 0.0) {
		    throw new DataException("incrementDistance <= 0.0.");
		}
		if( grid2check == null) {
		    throw new DataException("grid2check == null.");
		}
		TransformDouble td = null;
		if( distanceDecayExponent > .99999 && distanceDecayExponent < 1.00001) {
			td = new LinearDecay();
		} else {
			td = new DistanceDecay( distanceDecayExponent);
		}

		//TODO pScore.clone()
		pScore = new GISLattice( grid2check);
		eventClusterDensityMean = new GISLattice( grid2check);
		eventClusterDensitySD = new GISLattice( grid2check);
		eventClusterDistance = new GISLattice( grid2check);
		maximumLikelihood = new GISLattice( grid2check);

		// Compute reasonable size for symbols.
		double ew = Math.abs(grid2check.getEastEdge() - grid2check.getWestEdge());
		double ns = Math.abs(grid2check.getNorthEdge() - grid2check.getSouthEdge());
		double symSize = Math.max(ew, ns);
		negPointSymbol = (GISPointSymbol) (new GISPointSymbolSquare( symSize / 100.0, null, Color.black)) ;
		posPointSymbol = (GISPointSymbol) (new GISPointSymbolSquare( symSize / 50.0, null, Color.black)) ;
		falseNegPointSymbol = (GISPointSymbol) (new GISPointSymbolSquare( symSize / 50.0, null, Color.red)) ;
		falsePosPointSymbol = (GISPointSymbol) (new GISPointSymbolSquare( symSize / 100.0, null, Color.red)) ;

		// Load event table.
        {
            CSVTable test = new CSVTable(eventTable.getAbsolutePath());
            events = GISPointQuadTree.load( test, "x", "y", null, null, 10);
            events.setRandomSource(new Random(random.nextLong()));
            test = null;
        }

        // Load and condition all population tables.
		LogUtil.progress(log, "Loading population files:");
		
        simulatedPopulations = newArrayQuadTree(numberOfFiles);
        double[] simPopIntensity = new double[ numberOfFiles];
        
		int num2Collect = 1;
        int numEvents = events.getNumberPointsIncludingSubNodes();
        for(int i=0; i<populationFiles.size(); i++) {
            File popfile = populationFiles.get(i);
            LogUtil.progress(log, "  " + popfile);
            
			CSVTable test = new CSVTable(popfile.getPath());
			simulatedPopulations[i] = GISPointQuadTree.load( test, "x", "y", null, negPointSymbol, 10);
			simulatedPopulations[i].setRandomSource(new Random(random.nextLong()));
            test = null;
            
			double numberInSimulated = simulatedPopulations[ i].getNumberPointsIncludingSubNodes();
			simPopIntensity[ i] = ((double) numEvents) / numberInSimulated;

            // Insert events into loaded point set by finding points closest to
            // events, and moving them to precisely the same position. Moved
            // points are considered "conditioned", and if while processing an
            // event all the closest points are conditioned, then the next
            // closest point is selected instead.
			for(GISPoint eventPoint : events) {
				if( num2Collect == 1) {
					GISPoint closestPoint = simulatedPopulations[ i].closestPoint( eventPoint);
					if( closestPoint.getSymbol() == posPointSymbol) {
						num2Collect += 2;
						GISPoint[] closePoints = 
							simulatedPopulations[ i].closestNPoints( eventPoint, num2Collect);
						if( closePoints == null) {
						    throw new DataException( "closestNPoints() returned as null");
						}
						if( closePoints.length < 2) {
						    throw new DataException( "closestNPoints() returned only one point that had already been conditioned");
						}
						boolean done = false;
						while( done == false) {
							int j = num2Collect - 2;
							done = true;
							while( j < closePoints.length) {
								if( closePoints[ j].getSymbol() != posPointSymbol) {
									simulatedPopulations[ i].removePoint( closePoints[ j]);
									closePoints[ j].setEasting( eventPoint.getEasting());
									closePoints[ j].setNorthing( eventPoint.getNorthing());
									simulatedPopulations[ i].addPoint( closePoints[ j]);
									closePoints[ j].setSymbol( posPointSymbol);
									j = closePoints.length + 2;
								} else {
									j++;
								}
							}
							if( j == closePoints.length) {
								if( closePoints.length < num2Collect) {
								    throw new DataException( "not enough stochastic points: they have all been conditioned.");
								}
								done = false;
								num2Collect +=2;
								closePoints = 
									simulatedPopulations[ i].closestNPoints( 
										eventPoint, num2Collect);
							}
						}
					} else {
						simulatedPopulations[ i].removePoint( closestPoint);
						closestPoint.setEasting( eventPoint.getEasting());
						closestPoint.setNorthing( eventPoint.getNorthing());
						simulatedPopulations[ i].addPoint( closestPoint);
						closestPoint.setSymbol( posPointSymbol);
					}
				} else {
						GISPoint[] closePoints = 
							simulatedPopulations[ i].closestNPoints( eventPoint, num2Collect);
						if( closePoints == null) {
						    throw new DataException("closestNPoints() returned as null");
						}
						if( closePoints.length < 2) {
						    throw new DataException("closestNPoints() returned only one point that had already been conditioned");
						}
						boolean done = false;
						int startJ = 0;
						while( done == false) {
							int j = startJ;
							done = true;
							while( j < closePoints.length) {
								if( closePoints[ j].getSymbol() != posPointSymbol) {
									simulatedPopulations[ i].removePoint( closePoints[ j]);
									closePoints[ j].setEasting( eventPoint.getEasting());
									closePoints[ j].setNorthing( eventPoint.getNorthing());
									simulatedPopulations[ i].addPoint( closePoints[ j]);
									closePoints[ j].setSymbol( posPointSymbol);
									j = closePoints.length + 2;
								} else {
									j++;
								}
							}
							if( j == closePoints.length) {
								if( closePoints.length < num2Collect) {
								    throw new DataException("not enough stochastic points: they have all been conditioned.");
								}
								done = false;
								startJ = j;
								num2Collect +=2;
								closePoints = 
									simulatedPopulations[ i].closestNPoints( 
										eventPoint, num2Collect);
							}
						}
				}
			}

            for(GISPoint simPoint : simulatedPopulations[i]) {
                if( random.nextDouble() < simPopIntensity[ i]) {
                    if( simPoint.getSymbol() == negPointSymbol) {
                        simPoint.setSymbol( falsePosPointSymbol);
                    }
                } else {
                    if( simPoint.getSymbol() == posPointSymbol) {
                        simPoint.setSymbol( falseNegPointSymbol);
                    }
                }
            }
	
            // Save conditioned data to new file if requested.
			if( saveRealizations == true) {
				PrintWriter out = null;
				out = new PrintWriter(
					new BufferedWriter( new FileWriter( "conditioned" + i + ".txt")));
				try {
    				out.println( "x,y,status");
    				for(GISPoint p : simulatedPopulations[i]) {
                        out.print( p.getEasting() + "," + p.getNorthing());
                        if( p.getSymbol() == posPointSymbol) {
                            out.println( ",2");
                        } else if( p.getSymbol() == negPointSymbol) {
                            out.println( ",1");
                        } else if( p.getSymbol() == falseNegPointSymbol) {
                            out.println( ",-2");
                        } else {
                            // the following must be true: if( p.getSymbol() == falsePosPointSymbol) {
                            out.println( ",-1");
                        }
    				}
				}
				finally {
    				out.close();
				}
			}
		}
		
		TimeTracker.finished("Data preperation");
		
        LogUtil.progress(log, "Processing grid");
		
		double[] eventDensity = new double[ numberOfFiles];
		int cells = grid2check.getNumberRows() * grid2check.getNumberColumns();
        ProgressToy progress = new ProgressToy(log, 60, cells, "Completed");
		int cellsDone = 0;
		for( int r = grid2check.getNumberRows() - 1; r >= 0; r--) {
			double northing = grid2check.getCellCenterNorthing( r, 0);
			for( int c = grid2check.getNumberColumns() - 1; c >= 0; c--) {
				double easting = grid2check.getCellCenterEasting( r, c);
				GISPoint centerTargetCell = new GISPoint( easting, northing);
				int bestNumberMoreExtreme = -1; // key for best
				double bestDensityMean = Double.POSITIVE_INFINITY;
				double bestMaxLikeValue = Double.NEGATIVE_INFINITY;
				double bestDensitySD = 0.0;
				double bestDistance = -1.0;
				double popCount = 0.0;
				double eventCount = 0.0;
				boolean populationInFilter = false;

				for( double d = maximumClusterDistance; d > 0; d -= incrementDistance) {
					int countMoreExtreme = numberOfFiles;
					double totalSimulatedEventCount = 0.0;
					for( int i = numberOfFiles - 1; i >= 0; i--) {
						double simulatedEventCount = 0.0;
						popCount = 0.0;
						eventCount = 0.0;
						
                        LinkedList<GISPoint> closePoints = simulatedPopulations[ i].getPoints( centerTargetCell, d);
						for(GISPoint p : closePoints) {
						    // Record that we found someone in this cell.
						    populationInFilter = true;
							// do the thing
							double distanceP = RGISData.distance( p, easting, northing);
							double weight = td.getDouble( distanceP / d);
							popCount += weight;
							GISPointSymbol symbol = p.getSymbol();
							// if symbol == negPointSymbol, do nothing
							if( symbol == falseNegPointSymbol) {
								eventCount += weight;
							} else if( symbol == falsePosPointSymbol) {
								simulatedEventCount += weight;
							} else if( symbol == posPointSymbol) {
								simulatedEventCount += weight;
								eventCount += weight;
							} else if( symbol == negPointSymbol) {
							} else {
								System.out.println( "point symbol bad");
							}
						}
						
						if( eventCount > 0.0) {
							// no?  simulatedEventCount /= popCount;
							totalSimulatedEventCount += simulatedEventCount;
							eventDensity[ i] = eventCount / popCount;
							if( searchHighRate == true) {
								if( eventCount > simulatedEventCount) {
									countMoreExtreme--;
								}
							} else {
								if( eventCount < simulatedEventCount) {
									countMoreExtreme--;
								}
							}
						} else {	// this logic assumes events are hard data.
						    if(i != numberOfFiles-1)
						        log.warning(String.format("i != numberOfFiles-1: i=%d, nOF=%d", i, numberOfFiles));
						    // if this file dosn't have events close by, none will
							i = -1;
							// if distance 'd' fails, all smaller distances will fail
							d = -1.0;
						}
					}
					
                    double averageSimulatedEventCount = totalSimulatedEventCount / numberOfFiles;
					
					
//                    if( d >= 0.0) {
                    if( populationInFilter == true) { // ZZZ Only "no data" when no population in filter
                        
                        double thisMaxLikeValue;
                        if( averageSimulatedEventCount > 0.0) {
    						// ml = (c/n)^c * ([C-c]/[C-n])^(C-c)
    						// c = eventCount
    						// C = numEvents
    						// n = averageSimulatedEventCount
    						double partA = Math.pow( 
    									eventCount / averageSimulatedEventCount, 
    									eventCount);
    						double partB = Math.pow( 
    							  (numEvents - eventCount) / (numEvents - averageSimulatedEventCount), 
    							  numEvents - eventCount);
    						thisMaxLikeValue = partA * partB;

                            if (searchHighRate == true) {
                                if (eventCount <= averageSimulatedEventCount) {
                                    thisMaxLikeValue = 0.0;
                                }
                            } else {
                                if (eventCount >= averageSimulatedEventCount) {
                                    thisMaxLikeValue = 0.0;
                                }
                            }
                        }
                        else {
                            thisMaxLikeValue = -1.0;
                        }
						
						if( bestMaxLikeValue < thisMaxLikeValue) {
							bestMaxLikeValue = thisMaxLikeValue;
							bestNumberMoreExtreme = countMoreExtreme;
							bestDistance = d;
							bestDensityMean = 0.0;
							for( int j = 0; j <= numberOfFiles - 1; j++) {
								bestDensityMean += eventDensity[ j];
							}
							bestDensityMean /= numberOfFiles;
							bestDensitySD = 0.0;
							for( int j = 0; j <= numberOfFiles - 1; j++) {
								bestDensitySD += (eventDensity[ j] - bestDensityMean) * (eventDensity[ j] - bestDensityMean);
							}
							bestDensitySD = Math.sqrt( bestDensitySD);
						}
					}
				}
				
				if( bestMaxLikeValue > Double.NEGATIVE_INFINITY) {
					eventClusterDensityMean.setCellValue( r, c, bestDensityMean);
					eventClusterDensitySD.setCellValue( r, c, bestDensitySD);
					eventClusterDistance.setCellValue( r, c, bestDistance);
					maximumLikelihood.setCellValue( r, c, bestMaxLikeValue);
					double pScoreValue = (bestNumberMoreExtreme + 1.0) / (1.0 + numberOfFiles);
					pScore.setCellValue( r, c, pScoreValue);
				}
				cellsDone++;
			}
            progress.printProgress(cellsDone);
		}
		
		eventClusterDensityMean.writeAsciiEsri( "0densityMean");
		eventClusterDensitySD.writeAsciiEsri( "0densitySD");
		eventClusterDistance.writeAsciiEsri( "0ClusterDistance");
		maximumLikelihood.writeAsciiEsri( "0maximumLikelihood");
		pScore.writeAsciiEsri( "0pScore");
		
        TimeTracker.finished("Processing grid");
        TimeTracker.total();
	}

    /**
     * Type-safe constructor for parameterized type. There is NO clean way to do
     * this in Java; all methods either generate warnings, or required the
     * construction of temporary objects to carry type info around the compiler.
     * 
     * @param length
     *            size of new array
     * 
     * @return new array
     */
    @SuppressWarnings("unchecked")
    protected GISPointQuadTree<GISPoint>[] newArrayQuadTree(int length) {
        return new GISPointQuadTree[length];
    }
    
    /**
     * Run program from the command line.
     * 
     * @param argv command-line args
     * 
     * @throws IOException on any file error
     */
    public static void main( String argv[]) throws IOException {
        // Enumerate every argument we require and their defaults.
        File eventFile;
        File gridFile;
        ArrayList<File> popfiles;

        double maxDist;
        double incDist;
        long   randomSeed = 0;
        boolean userSeed = false;
        double distDecay = 0.5;
        boolean searchHighRate = true;
        boolean saveRealizations = false;

        
	    OptionParser parser = new OptionParser();
        parser.accepts("e",        String.format("decay exponent (default=%s)", distDecay))
            .withRequiredArg().ofType(Double.class);
        parser.accepts("r", "random seed (default=time-dependent random)")
            .withRequiredArg().ofType(Long.class);
        parser.accepts("highrate", "search mode: high-rate clusters" + (searchHighRate ? " (default)" : ""));
        parser.accepts("lowrate",  "search mode: low-rate clusters" + (!searchHighRate ? " (default)" : ""));
        parser.accepts("save",     "save conditioned version of each input file (not default)");

        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");
        
        // [event] [grid] [households...]
        // -d=maxDistance,increment

        // Build argument parser.
        OptionSet opts = parser.parse(argv);
        if(opts.has("h") || opts.has("help") || argv.length == 0) {
            System.out.format("Usage:\n  %s [options] %s\n",
                ConditionalPointClusterDetector.class.getSimpleName(),
                "eventTableFile gridFile dist,incr populationFiles...");
            parser.printHelpOn(System.out);
            
            System.out.println();
            System.out.println( "Arguments\n---------");
            System.out.println( "eventTableFile   The event file.  Each row is an event location.  Must be a CSV\n" +
            		"                 file with two columns:\n" +
            		"                     x: event easting\n" +
            		"                     y: event northing");
            System.out.println( "gridFile         an empty ESRI ASCII raster that defines the specific locations");
            System.out.println( "                 at which we will test for clustering");
            System.out.println( "dist             maximum cluster size");
            System.out.println( "incr             search increment");
            System.out.println( "populationFiles  A list of one (or more) files to load and test for clustering.\n" +
                    "                 Each row defines a location and the number of people at that\n" +
            		"                 location which meet the clustering condition.  Must be a CSV\n" +
            		"                 with three columns:\n" +
            		"                     x:     household easting\n" +
            		"                     y:     household northing\n" +
            		"                     count: number of population meeting criteria");

            System.out.println();
            System.out.println( "Notes\n-----");
            System.out.println( " * Event file lists known real cases (ie black/female/50-64/cancer).");
            System.out.println( " * Decay exponent defines strength of relationship as distance increases.");
            System.out.println( "   As distance ranges from 0 to maximum (defined by -d), relation function");
            System.out.println( "   returns 1 at distance 0, and 0 at maximum.  The decay exponent defines");
            System.out.println( "   the curve between these points:");
            System.out.println( "   0=no decay; 1=linear; 2=squared; etc.");
            
            return;
        }

        int reqdArgs = 4;
        if(opts.nonOptionArguments().size() < reqdArgs) {
            System.err.println("Required args are missing; there should be at least "+reqdArgs+".");
            System.exit(5);
        }

        
        // Parse optional arguments.
        if(opts.has("r")) {
            randomSeed = ((Long)opts.valueOf("r")).longValue();
            userSeed = true;
        }
        if(opts.has("e")) {
            // Decay exponent.
            distDecay = (Double) opts.valueOf("e");
            if(distDecay < 0)
                throw new DataException("decay exponent must be >= 0");
        }
        if(opts.has("highrate")) {
            // High-rate clusters search mode.
            searchHighRate = true;
            if(opts.has("lowrate"))
                throw new DataException("--highrate and --lowrate cannot both be requested for the same run");
        }
        if(opts.has("lowrate")) {
            // Low-rate clusters search mode.
            searchHighRate = false;
        }
        if(opts.has("save")) {
            // Save conditioned input files.
            saveRealizations = true;
        }

        
        //
        // Parse mandatory arguments.
        //
        List<String> args = opts.nonOptionArguments();
        int c=0;
        eventFile = new File(args.get(c++));
        gridFile = new File(args.get(c++));
        
        // maxDistance,increment
        String spec = args.get(c++);
        String[] parts = spec.trim().split(",");
        if(parts.length == 1) {
            maxDist = Double.parseDouble(parts[0]);
            incDist = maxDist / 8;
        }
        if(parts.length == 2) {
            maxDist = Double.parseDouble(parts[0]);
            incDist = Double.parseDouble(parts[1]);
        }
        else
            throw new DataException("dist,incr parameter is badly formatted: "+spec);
        
        // population files
        popfiles = new ArrayList<File>();
        for(String s : args.subList(c, args.size()))
            popfiles.add(new File(s));

        
        // Run!
        LogUtil.getRootLogger().setLevel(Level.INFO);
        LogUtil.quietConsole();
        LogUtil.cleanFormat();

        LogUtil.progress(log,
            "Starting "+ConditionalPointClusterDetector.class.getSimpleName()
                +"\n  Version "+SVN_REV+" dated "+SVN_DATE
                +"\n  in directory "+new File(".").getCanonicalPath()
                +"\n  with arguements "+Arrays.toString(argv));
        LogUtil.cr(log);

        GISGrid grid = GISGrid.loadEsriAscii(gridFile);

        ConditionalPointClusterDetector cd = new ConditionalPointClusterDetector(eventFile, maxDist, incDist, 
            grid, distDecay, searchHighRate, saveRealizations, popfiles);
        
        if(userSeed) {
            cd.setRandomSource(new Random(randomSeed));
        }
        
        cd.go();
	}
}
