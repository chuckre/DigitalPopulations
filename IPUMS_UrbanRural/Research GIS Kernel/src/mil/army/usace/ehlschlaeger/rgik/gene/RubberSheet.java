package mil.army.usace.ehlschlaeger.rgik.gene;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class RubberSheet extends FitnessFunction {
	private GISLattice dx, dy, dz;
	private int numRows, numCols, numAlleleXY, numAlleleZ, checkCells;
	private GISLattice goal, data;
	private double[] x;
	private double[] y;
	private double[] z;
	private boolean made;
	private double res, maxSpread, minSpread, maximumX, maximumZ;

	public RubberSheet() { 
		super();
		res = -1.0;
		made = false;
		numAlleleXY = 12;
		numAlleleZ = 12;
		maxSpread = Double.NEGATIVE_INFINITY;
		minSpread = Double.POSITIVE_INFINITY;
		maximumX = Double.NEGATIVE_INFINITY;
		maximumZ = Double.POSITIVE_INFINITY;
		checkCells = 100;
	}

	public boolean isFitnessFunctionAbsolute() {
		if( checkCells > 0)
			return false;
		return true;
	}

	public void setCellsToCheck( int value) {
		if( value >= 0 && value < 4) {
			System.out.println( 
			"RubberSheet.setCellsToCheck ERROR:");
			System.out.println( 
			"  value must be less than 0 (DO ALL CELLS) or greater than 4");
			System.exit( -1);
		}
		checkCells = value;
	}

	public void setMaximumHorizontalShift( double value) {
		if( value < 0.0) {
			System.out.println( 
			"RubberSheet.setMaximumHorizontalShift ERROR: value must be 0.0 or greater");
			System.exit( -1);
		}
		maximumX = value;
	}

	public void setMaximumVerticalShift( double value) {
		if( value < 0.0) {
			System.out.println( 
			"RubberSheet.setMaximumHorizontalShift ERROR: value must be 0.0 or greater");
			System.exit( -1);
		}
		maximumZ = value;
	}

	public void calculateFitness( Chromosome ch) {
		//System.out.println( "cf");
		int paraNum = 0;
		for( int r = 0; r < dx.getNumberRows(); r++) {
			for( int c = 0; c < dx.getNumberColumns(); c++) {
				dx.setCellValue( r, c, ch.getParameterValue( paraNum++));
				dy.setCellValue( r, c, ch.getParameterValue( paraNum++));
				dz.setCellValue( r, c, ch.getParameterValue( paraNum++));
			}
		}
		if( checkCells < 0 || checkCells >= x.length) {
			calculateFitnessAbsolute( ch);
			return;
		}
		int count = 0;
		double sum = 0.0;
		while( count < checkCells) {
			int cell = (int) (Math.random() * x.length);
			double e = x[ cell];
			double n = y[ cell];
			if( dx.onMap( e, n) == true) { 
				double newX = e + dx.getValue( e, n);
				double newY = n + dy.getValue( e, n);
				if( data.isNoData4Corners( newX, newY) == false) {
					double value = z[ cell] + dz.getValue( e, n) - data.getValue( newX, newY);
					sum += value * value;
					count++;
				}
			}
		}
		if( count == 0) {
			ch.setFitness( 0.0);
		} else {
			sum /= count;
			sum = Math.sqrt( sum);
			ch.setFitness( 1.0 / sum);
		}
	}

	public void calculateFitnessAbsolute( Chromosome ch) {
		//System.out.println( "cfac");
		int count = 0;
		double sum = 0.0;
		for( int cell = x.length - 1; cell >= 0; cell--) {
			double e = x[ cell];
			double n = y[ cell];
			if( dx.onMap( e, n) == true) { 
				double newX = e + dx.getValue( e, n);
				double newY = n + dy.getValue( e, n);
				if( data.isNoData4Corners( newX, newY) == false) {
					double value = z[ cell] + dz.getValue( e, n) - data.getValue( newX, newY);
					sum += value * value;
					count++;
				}
			}
		}
		if( count == 0) {
System.out.println( "count==0 in calculateFitnessAbsolute()");
			ch.setFitness( 0.0);
		} else {
			sum /= count;
			sum = Math.sqrt( sum);
			ch.setFitness( 1.0 / sum);
		}
	}

	public GISLattice makeMap( Chromosome ch) {
		//System.out.println( "mm");
		int paraNum = 0;
		for( int r = 0; r < dx.getNumberRows(); r++) {
			for( int c = 0; c < dx.getNumberColumns(); c++) {
				dx.setCellValue( r, c, ch.getParameterValue( paraNum++));
				dy.setCellValue( r, c, ch.getParameterValue( paraNum++));
				dz.setCellValue( r, c, ch.getParameterValue( paraNum++));
			}
		}
		return( makeMap());
	}

	public GISLattice makeMap() {
		//System.out.println( "mm2");
		GISLattice newMap = new GISLattice( goal);
		for( int r = 0; r < newMap.getNumberRows(); r++) {
			for( int c = 0; c < newMap.getNumberColumns(); c++) {
				double e = newMap.getCellCenterEasting( r, c);
				double n = newMap.getCellCenterNorthing( r, c);
				if( dx.onMap( e, n) == true) {
					double newX = e + dx.getValue( e, n);
					double newY = n + dy.getValue( e, n);
					if( data.isNoData4Corners( newX, newY) == false) {
						newMap.setCellValue( r, c, 
							data.getValue( newX, newY) - dz.getValue( e, n));
					}
				}
			}
		}
		return newMap;
	}

	public Chromosome getChromosome() {
		Chromosome ch = new Chromosome();
		ch.setNumberParameters( 3 * dx.getNumberRows() * dx.getNumberColumns());
		double minX = res * -0.49;
		double maxX = res * 0.49;
		if( maximumX >= 0.0 ) {
			minX = maximumX * -1.0;
			maxX = maximumX;
		}
		if( maxSpread > maximumZ) {
			maxSpread = maximumZ;
			minSpread = maximumZ * -1.0;
		}
		for( int r = 0; r < dx.getNumberRows(); r++) {
			for( int c = 0; c < dx.getNumberColumns(); c++) {
				ch.setParameter( numAlleleXY, minX, maxX, 
					"dx[" + Integer.toString( r) + "][" + Integer.toString( c) + "]");
				ch.setParameter( numAlleleXY, minX, maxX, 
					"dy[" + Integer.toString( r) + "][" + Integer.toString( c) + "]");
				ch.setParameter( numAlleleZ, minSpread, maxSpread, 
					"dz[" + Integer.toString( r) + "][" + Integer.toString( c) + "]");
			}
		}
		return ch;
	}

	private void makeRubberSheet() {
		//System.out.println( "mrs");
	    if( made == false) {
		double s = goal.getSouthEdge();
		if( s < data.getSouthEdge()) {
			s = data.getSouthEdge();
		}
		double n = goal.getNorthEdge();
		if( n > data.getNorthEdge()) {
			n = data.getNorthEdge();
		}
		double e = goal.getEastEdge();
		if( e > data.getEastEdge()) {
			e = data.getEastEdge();
		}
		double w = goal.getWestEdge();
		if( w < data.getWestEdge()) {
			w = data.getWestEdge();
		}
		numRows = (int) (((n - s) / res) + 2.0);
		numCols = (int) (((e - w) / res) + 2.0);
		dx = new GISLattice( w, n, res, res, numRows, numCols);
		dy = new GISLattice( w, n, res, res, numRows, numCols);
		dz = new GISLattice( w, n, res, res, numRows, numCols);
		if( dx == null || dy == null || dz == null) {
			System.out.println( "RubberSheet.makeRubberSheet ERROR: not enough RAM to run program");
			System.exit( -1);
		}
		int count = 0;
		for( int r = 0; r < goal.getNumberRows(); r++) {
			for( int c = 0; c < goal.getNumberColumns(); c++) {
				if( goal.isNoData( r, c) == false) {
					e = goal.getCellCenterEasting( r, c);
					n = goal.getCellCenterNorthing( r, c);
					if( data.isNoData4Corners( e, n) == false) {
						count++;
					}
				}
			}
		}
		x = new double[ count];
		y = new double[ count];
		z = new double[ count];
		if( x == null || y == null || z == null) {
			System.out.println( "RubberSheet.makeRubberSheet ERROR: not enough RAM to run program");
			System.exit( -1);
		}
		count = 0;
		for( int r = 0; r < goal.getNumberRows(); r++) {
			for( int c = 0; c < goal.getNumberColumns(); c++) {
				if( goal.isNoData( r, c) == false) {
					e = goal.getCellCenterEasting( r, c);
					n = goal.getCellCenterNorthing( r, c);
					if( data.isNoData4Corners( e, n) == false) {
						x[ count] = e;
						y[ count] = n;
						z[ count] = goal.getValue( r, c);
						double spread = z[ count] - data.getValue( e, n);
						if( spread < minSpread) {
							minSpread = spread;
						}
						if( spread > maxSpread) {
							maxSpread = spread;
						}
						count++;
					}
				}
			}
		}
		if( minSpread == Double.MAX_VALUE) {
			System.out.println( "RubberSheet.makeRubberSheet ERROR: no points to compare values");
			System.exit( -1);
		}
		made = true;
	    }
	}

	public void setGoal( GISLattice map) {
		goal = map;
		if( map == null) {
			System.out.println( "RubberSheet.setGoal ERROR: map == null");
			System.exit( -1);
		}
		if( goal != null && data != null && res > 0.0) {
			makeRubberSheet();
		}
	}

	public void setData( GISLattice map) {
		data = map;
		if( data == null) {
			System.out.println( "RubberSheet.setGoal ERROR: data == null");
			System.exit( -1);
		}
		if( goal != null && data != null && res > 0.0) {
			makeRubberSheet();
		}
	}

	public void setResolution( double value) {
		if( value <= 0.0) {
			if( goal == null || data == null) {
				System.out.println( "RubberSheet.setGoal ERROR: value must be greater than 0.0");
				System.exit( -1);
			}
			double minNorth = goal.getNorthEdge();
			double t = data.getNorthEdge();
			if( t < minNorth) {
				minNorth = t;
			}
			double maxSouth = goal.getSouthEdge();
			t = data.getSouthEdge();
			if( t > maxSouth) {
				maxSouth = t;
			}
			res = minNorth - maxSouth;
			double minEast = goal.getEastEdge();
			t = data.getEastEdge();
			if( t < minEast) {
				minEast = t;
			}
			double maxWest = goal.getWestEdge();
			t = data.getWestEdge();
			if( t > maxWest) {
				maxWest = t;
			}
			if( res > 0.0 && res < minEast - maxWest) {
				res = minEast - maxWest;
			}
			if( res <= 0.0) {
				System.out.println( 
				"RubberSheet.setGoal ERROR: computed resolution must be greater than 0.0");
				System.exit( -1);
			}
			res *= 1.000001;
			makeRubberSheet();
		} else {
			res = value;
			if( goal != null && data != null && res > 0.0) {
				makeRubberSheet();
			}
		}
	}

	public void setDX( GISLattice dxMap) {
		dx = dxMap;
		if( dx != null && dy != null && dz != null)
			made = true;
	}

	public void setDY( GISLattice dyMap) {
		dy = dyMap;
		if( dx != null && dy != null && dz != null)
			made = true;
	}

	public void setDZ( GISLattice dzMap) {
		dz = dzMap;
		if( dx != null && dy != null && dz != null)
			made = true;
	}

	public boolean isDX( double easting, double northing) {
		boolean nodata = dx.isNoData( easting, northing);
		if( nodata)
			return false;
		return true;
	}

	public double getDX( double easting, double northing) {
		return dx.getValue( easting, northing);
	}

	public boolean isDY( double easting, double northing) {
		boolean nodata = dy.isNoData( easting, northing);
		if( nodata)
			return false;
		return true;
	}

	public double getDY( double easting, double northing) {
		return dy.getValue( easting, northing);
	}

	public boolean isDZ( double easting, double northing) {
		boolean nodata = dz.isNoData( easting, northing);
		if( nodata)
			return false;
		return true;
	}

	public double getDZ( double easting, double northing) {
		return dz.getValue( easting, northing);
	}

	public void initializeGAParameters( GeneticAlgorithm ga) {
		ga.setNumberPopulation( 50); 
		ga.setProbabilityCrossover( 1.0);
		ga.setProbabilityMutation( 0.0001);
		ga.setDeltaMutation( 0.00005);
		ga.setMaximumRunTime( 60.0 * 60.0 * 12.0); // 60 sec * 60 min * 12 hours
		ga.setMutationStressRatio( 75.0);
		ga.setMaximumMutationValue( 0.001);
		ga.setFitnessScalingRatio( 65.0);
		ga.setPrintAverageFitness( 5000);
		ga.setNumberOfBest( 30);
		ga.setShareFactor( 1.0);
		//ga.setHillClimbingSeconds( 60.0 * 60.0 * 2.0); // 60 sec * 60 min * 2 hours
	}

	public static void main( String argv[]) throws IOException {
	  GISLattice g = GISLattice.loadEsriAscii("study_dem");
	  int totalCells = 0;
	  for( int r = 0; r < g.getNumberRows(); r++) {
		for( int c = 0; c < g.getNumberColumns(); c++) {
			if( g.isNoData( r, c) == false)
				totalCells++;
		}
	  }
	  GISLattice d = GISLattice.loadEsriAscii("study_dted");
	  //for( int cells = 4; cells <= totalCells * 1.4; cells *= 1.3) {
	  for( int cells = 100; cells <= 100; cells += 101) {
		RubberSheet f = new RubberSheet();
		double maxHorz = 0.001;
		f.setMaximumHorizontalShift( maxHorz);
		System.out.println( "Max Horizontal shift: " + maxHorz);
		f.setMaximumVerticalShift( 40.0);
		f.setCellsToCheck( cells);
		f.setGoal( g);
		f.setData( d);
		f.setResolution( -1.0);
		System.out.println( "Cells Tested: " + cells);
		Chromosome ch = f.run();
		Chromosome[] bestPop = f.getBestPopulation();
		System.out.println( "Fitness before checking all cells: " + ch.getFitness());
		f.calculateFitnessAbsolute( ch);
		System.out.println( "       Fitness checking all cells: " + ch.getFitness());
		ch.print();
		System.out.println( "Number of Chromosomes made: " + ch.getNumberMade());
		GISLattice results = f.makeMap( ch);
		//GISLattice dif = new GISLattice( results);
		int count = 0;
		double sumSq = 0.0;
		int oriCount = 0;
		double oriSumSq = 0.0;
		double oriAbsSum = 0.0;
		double absSum = 0.0;
		double minError = Double.POSITIVE_INFINITY;
		double maxError = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < results.getNumberRows(); r++) {
			for( int c = 0; c < results.getNumberColumns(); c++) {
				if( results.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - results.getCellValue( r, c);
					//dif.setCellValue( r, c, value);
					count++;
					sumSq += value * value;
					if( value >= 0.0)
						absSum += value;
					else	absSum -= value;
					if( minError > value)
						minError = value;
					if( maxError < value)
						maxError = value;
				}
				if( d.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - d.getCellValue( r, c);
					oriCount++;
					oriSumSq += value * value;
					if( value >= 0.0)
						oriAbsSum += value;
					else	oriAbsSum -= value;
				}
			}
		}
		System.out.println( "Original Squared Error: " + (oriSumSq / oriCount) + 
			", Original Average Absolute Error: " + (oriAbsSum / oriCount));
		System.out.println( "Average Squared Error: " + (sumSq / count) + 
			", Absolute Average Error: " + (absSum / count));
		System.out.println( "Min Error: " + minError + ", Max Error: " + maxError);
		results.writeAsciiEsri( "RubberSheetTest");
		System.out.println( "");
		System.out.println( "************************************************");
		for( int cc = 0; cc < bestPop[ 0].getNumberParameters(); cc++) {
			System.out.print( bestPop[ 0].getParameterName( cc));
			for( int m = 0; m < bestPop.length; m++) {
				System.out.print( "," + bestPop[ m].getParameterValue( cc));
			}
			System.out.println( "");
		}
		System.out.print( "AbsMeanError");
		for( int m = 0; m < bestPop.length; m++) {
			results = f.makeMap( bestPop[ m]);
			GISLattice dif = new GISLattice( results);
			count = 0;
			sumSq = 0.0;
			absSum = 0.0;
			for( int r = 0; r < results.getNumberRows(); r++) {
				for( int c = 0; c < results.getNumberColumns(); c++) {
					if( results.isNoData( r, c) == false) {
						double value = g.getCellValue( r, c) - results.getCellValue( r, c);
						dif.setCellValue( r, c, value);
						count++;
						sumSq += value * value;
						if( value >= 0.0)
							absSum += value;
						else	absSum -= value;
					}
				}
			}
			System.out.print( "," + (absSum / count));
		}
		System.out.println( "");
	  }
	}
}
