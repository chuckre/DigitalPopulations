package mil.army.usace.ehlschlaeger.rgik.core;

import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;

/**
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class Sensor extends RGIS {
	private double	x, y;
	private double[]	tEvents;

	public Sensor( double xCoordinate, double yCoordinate, double[] timeEvents) {
		super();
		x = xCoordinate;
		y = yCoordinate;
		tEvents = new double[ timeEvents.length];
		BubbleSort bs = new BubbleSort( false);
		int[] order = new int[ timeEvents.length];
		for( int i = 0; i < timeEvents.length; i++) {
			order[ i] = i;
		}
		bs.sort( order, timeEvents);
		for( int i = 0; i < timeEvents.length; i++) {
			tEvents[ i] = timeEvents[ order[ i]];
		}
	}

	public String toString() {
		String s = "";
		s = s + "x: " + x + ", y: " + y;
		for( int i = 0; i < tEvents.length; i++) {
			s = s + '\n' + i + " " + tEvents[ i];
		}
		return s;
	}

	public int getNumberEvents() {
		return tEvents.length;
	}

	public double getDistance( Sensor otherSensor) {
		double dx = x - otherSensor.getX();
		double dy = y - otherSensor.getY();
		double dist = Math.sqrt( dx * dx + dy * dy);
		return dist;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getEvent( int eventNumber) {
		return tEvents[ eventNumber];
	}

	public double[] getEvents() {
		return tEvents;
	}

	public static void main( String argv[]) {
		double[] times = new double[ 10];
		Random ran = new Random( 1);
		for( int i = 0; i < times.length; i++) {
			times[ i] = ran.nextDouble();
			System.out.print( times[i] + " ");
		}
		System.out.println();
		Sensor s = new Sensor( 7.0, 9.0, times);
		times = s.getEvents();
		for( int i = 0; i < times.length; i++) {
			System.out.print( times[i] + " ");
		}
		System.out.println();

	}
}