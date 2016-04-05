package mil.army.usace.ehlschlaeger.rgik.core;

import java.util.Random;



/**
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class NoiseEvent extends RGIS {
    private double[] x, y, t, distance, dxUnit, dyUnit;
    private double   temperatureCelcius;
    private double   solX, solY, solFitness, nExtent, sExtent, eExtent,
            wExtent;
    private int      numberAngles;

	public boolean possibleNoiseByDistance() {
		for( int i = 0; i < x.length - 1; i++) {
			double iSensorTime = t[i];
			for( int j = i + 1; j < x.length; j++) {
				double jSensorTime = t[j];
				double timeDif = Math.abs( iSensorTime - jSensorTime);
				double dij = Math.sqrt( (x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j]));
				double timeBetweenSensors = dij / getSpeedOfSound();
				if( timeBetweenSensors < timeDif) {
					return false;
				}
			}
		}
		return true;
	}

/*
	public void setMaximumDistance( double distance) {
		maxDistance = distance;
	}
*/
	
	public double getSolutionX() {
		return solX;
	}

	public double getSolutionY() {
		return solY;
	}

	public double getSolutionFitness() {
		return solFitness;
	}

	public double getSolutionTime() {
		double tt = 0;
		for( int i = 0; i < x.length; i++) {
			double dist = Math.sqrt( 
				(x[i] - solX) * (x[i] - solX) + (y[i] - solY) * (y[i] - solY));
			tt += t[i] - dist / getSpeedOfSound();
//System.out.println( "   [" + i + "] time " + t[i] + ", distance: " + dist);
		}
		tt /= x.length;
		return tt;
	}

	public double getSpeedOfSound() {
		return getSpeedOfSound( temperatureCelcius);
	}

	public double getSpeedOfSound( double temperatureCelcius) {
		return( 331.4 + 0.6 * temperatureCelcius);
	}

	public NoiseEvent( double[] x, double[] y, double[] t, double temperatureCelcius) {
		super();
		assert x != null;
		assert y != null;
		assert t != null;
        assert x.length == y.length;
        assert x.length == t.length;
        
		this.x = x;
		this.y = y;
		this.t = t;
		numberAngles = 16;
		//maxDistance = Double.POSITIVE_INFINITY;
		solFitness = Double.POSITIVE_INFINITY;
		this.x = new double[ x.length];
		this.y = new double[ x.length];
		this.t = new double[ x.length];
//System.out.println( "data:");
		for( int i = 0; i < x.length; i++) {
			this.x[i] = x[i];
			this.y[i] = y[i];
			this.t[i] = t[i];
//System.out.println( x[i] + "," + y[i] + "," + t[i]);
		}
		distance = new double[ x.length];
		this.temperatureCelcius = temperatureCelcius;
	}

	public void solve() {
		solve( temperatureCelcius);
	}

	public double getMinimumTimeDifference() {
		double minTime = Double.POSITIVE_INFINITY;
		for( int i = 0; i < t.length; i++) {
			if( minTime > t[i])
				minTime = t[i];
		}
		return( minTime);
	}

	public double getSensorsCentroidX() {
		double aveX = 0.0;
		for( int i = 0; i < x.length; i++) {
			aveX += x[ i];
		}
		return( aveX / x.length);
	}

	public double getSensorsCentroidY() {
		double aveY = 0.0;
		for( int i = 0; i < x.length; i++) {
			aveY += y[ i];
		}
		return( aveY / y.length);
	}

	public void solve( double temperature) {
		double extrapolationMultiplier = 10.0; // .2 was working
		double sos = getSpeedOfSound( temperature);
		dxUnit = new double[ numberAngles];
		dyUnit = new double[ numberAngles];
		for( int a = 0; a < numberAngles; a++) {
			dxUnit[ a] = Math.cos( 2.0 * Math.PI * (numberAngles - a) / numberAngles);
			dyUnit[ a] = Math.sin( 2.0 * Math.PI * (numberAngles - a) / numberAngles);
			//System.out.println( dxUnit[a] + ", " + dyUnit[a]);
		}
		solX = getSensorsCentroidX();
		solY = getSensorsCentroidY();
		/*
		nExtent = solY + maxDistance;
		sExtent = solY - maxDistance;
		eExtent = solX + maxDistance;
		wExtent = solX - maxDistance;
		*/
		nExtent = sExtent = y[0];
		for( int i = 1; i < y.length; i++) {
			if( nExtent < y[i])
				nExtent = y[i];
			if( sExtent > y[i])
				sExtent = y[i];
		}
		eExtent = wExtent = y[0];
		for( int i = 1; i < x.length; i++) {
			if( eExtent < x[i])
				eExtent = x[i];
			if( wExtent > x[i])
				wExtent = x[i];
		}
		double dif = Math.max( nExtent - sExtent, eExtent - wExtent);
		nExtent += dif * extrapolationMultiplier;
		sExtent -= dif * extrapolationMultiplier;
		eExtent += dif * extrapolationMultiplier;
		wExtent -= dif * extrapolationMultiplier;
		//System.out.println( "x,y,d,sol");
		solFitness = fitness( solX, solY);
		double checkDistance = 1000.0;
		double diagDistance = checkDistance * Math.sin( Math.PI / 4.0);
		/*
		while( checkDistance >= .001) for NoiseSet4sq_b.txt (8 angles)
		while( checkDistance >= .0001) for NoiseSet4sq_c.txt (8 angles)
		*/
		while( checkDistance >= .0001) {
			//System.out.println( "NoiseEvent.solve(): (x,y,sol): " + solX + "," + solY + "," + solFitness);
			if( checkNearby( sos, checkDistance, diagDistance) == false) {
				/*
				checkDistance *= .5; for NoiseSet4sq_b.txt (8 angles)
				*/
				checkDistance *= .5;
				diagDistance = checkDistance * Math.sin( Math.PI / 4.0);
			} else {
				/*
				checkDistance *= 1.5; for NoiseSet4sq_b.txt (8 angles)
				*/
				checkDistance *= 1.5;
			}
		}
	}

	private boolean checkNearby( double speedOfSound, double checkDistance, double diagDistance) {
	  double bestSol = solFitness;
	  double bestX = solX;
	  double bestY = solY;
	  double newSol = 0.0;
	  for( int a = 0; a < numberAngles; a++) {
		double checkX = solX + checkDistance * dxUnit[ a];
		if( checkX >= wExtent && checkX <= eExtent) {
			double checkY = solY + checkDistance * dyUnit[ a];
			if( checkY >= sExtent && checkY <= nExtent) {
				newSol = fitness( checkX, checkY);
				if( newSol < bestSol) {
					bestX = checkX;
					bestY = checkY;
					bestSol = newSol;
				}
			}
		}
	  }
	  if( bestSol < solFitness) {
		solX = bestX;
		solY = bestY;
		solFitness = bestSol;
		return true;
	  }
	  return false;
	}

	public double distanceFitness( double[] errorBySensor) {
		if( errorBySensor.length != x.length) {
		    throw new IllegalArgumentException("errorBySensor length not same length as x");
		}
		double aveError = 0.0;
		double solTime = getSolutionTime();
		for( int i = 0; i < x.length; i++) {
			double di = Math.sqrt( (solX - x[ i]) * (solX - x[ i]) + (solY - y[ i]) * (solY - y[ i])); 
			double dD = (t[i] - solTime) * getSpeedOfSound( temperatureCelcius);
			if( di > dD) {
				errorBySensor[i] = di - dD;
			} else {
				errorBySensor[i] = dD - di;
			}
			aveError += errorBySensor[i];
		}
		aveError /= x.length;
		return aveError;
	}

	public double fitness( double easting, double northing) {
		for( int i = 0; i < x.length; i++) {
			distance[ i] = Math.sqrt( (easting - x[ i]) * (easting - x[ i]) +
				(northing - y[ i]) * (northing - y[ i])); 
		}
		double fitnessValue = 0.0;
		for( int i = 0; i < x.length; i++) {
			double eventTime = t[ i] - distance[ i] / getSpeedOfSound( temperatureCelcius);
			for( int j = 0; j < x.length; j++) {
				if( i != j) {
					double error = eventTime - (t[ j] - distance[ j] / getSpeedOfSound( temperatureCelcius));
					fitnessValue += error * error;
				}
			}
		}
//System.out.println( easting + "," + northing + "," + fitnessValue);
		return fitnessValue;
	}

	public static void main( String argv[]) {
		System.out.println( "NoiseEvent.main START");
		//double[] xs = { 100., 100., 10000., 10000., 5000., 2000. };
		//double[] ys = { 100., 10000., 100., 10000., 5000., 2000. };
		//double[] xs = { 100., 100., 10000., 10000., 5000. };
		//double[] ys = { 100., 10000., 100., 10000., 5000. };
		//double[] xs = { 100., 100., 10000., 10000. };
		//double[] ys = { 100., 10000., 100., 10000. };
		double[] xs = { 100., 100., 10000. };
		double[] ys = { 100., 10000., 100. };
		//double[] ts = new double[ 5];
		double[] ts = new double[ 3];
		double[] noiseX = new double[ 3];
		double[] noiseY = new double[ 3];
		double[] noiseT = new double[ 3];
		
		double temperatureCelcius = 10.0;
		// speed of sound based on http://www.measure.demon.co.uk/Acoustics_Software/speed.html
		// assuming temp is 10oC & humitity of 30%RH
		//double speedSound = 337.66;  // m/s (+- 0.1)
		double speedSound = 331.4 + 0.6 * temperatureCelcius;
		Random ran = new Random( 1);
		for( int j = 0; j < 1; j++) {
			double nx = ran.nextDouble() * 10000.0 + 1000.0;
			nx = 8162.364511057306;
			noiseX[ j] = nx;
			double ny = ran.nextDouble() * 10000.0 + 1000.0;
			ny = 443.859375038691;
			noiseY[ j] = ny;
			double nt = ran.nextDouble() * 100.0;
			noiseT[ j] = nt;
			//for( int i = 0; i < 5; i++) {
			//for( int i = 0; i < 4; i++) {
			for( int i = 0; i < 3; i++) {
				ts[ i] = nt + (Math.sqrt( (xs[i] - nx) * (xs[i] - nx) + 
					(ys[i] - ny) * (ys[i] - ny)) / speedSound);
				/* System.out.println( "i:" + i + ", x:" + xs[i] + ", y:" + ys[i] + 
					", time: " + ts[i]);
				*/
			} 
			System.out.println( "x,y,att");
			NoiseEvent n = new NoiseEvent( xs, ys, ts, temperatureCelcius); 
			//System.out.println( "x,y,att");
			//System.out.println( nx + "," + ny + ",100");
//            GISLattice tLattice = new GISLattice( nx-20000.0, ny+20000.0, 100., 100., 401, 401);
//			for( int r = 0; r < tLattice.getNumberRows(); r++) {
//				for( int c = 0; c < tLattice.getNumberColumns(); c++) {
//					double easting = tLattice.getCellCenterEast( r, c);
//					double northing = tLattice.getCellCenterNorth( r, c);
//					tLattice.setCellValue( r, c, n.fitness( easting, northing));
//				}
//			}
			//tLattice.writeAsciiEsri( "NoiseEventTest");
			n.solve();
			/*for( int i = 0; i < 5; i++) {
			//for( int i = 0; i < 4; i++) {
			for( int i = 0; i < 3; i++) {
				System.out.println( xs[i] + "," + ys[i] + "," + i);
			}
			*/
			System.out.println( "solX: " + n.getSolutionX() + ", solY: " + 
				n.getSolutionY() + ", time: " + n.getSolutionTime());
			System.out.println( "FITNESS: " + n.getSolutionFitness());

		}
	}
}