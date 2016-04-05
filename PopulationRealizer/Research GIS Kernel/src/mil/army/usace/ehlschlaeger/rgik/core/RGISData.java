package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.gui.ColorLookupTable;
import mil.army.usace.ehlschlaeger.rgik.gui.RGISAnimatedView;



/**
 * Root class for all classes that either represent GIS data or store it
 * somewhere.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public abstract class RGISData extends RGIS implements Serializable {
    public final static double         sqrtTwo         = Math.sqrt(2.0);

    // private static int currentRealizationNumber = 0;
    private int                        numRealizations = 0;
    // private static double secondsBetweenKeyFrame = 1.0;
    // private static double ratioToNextRealization = 0.0;
    // private static double timeElapsed;
    // private static Date startKeyTime;
    private ColorLookupTable           clt;
    private transient RGISAnimatedView view;

    protected RGISData() {
    }

    /**
     * Compute the shortest distance between this object geometry and the given
     * point.
     * 
     * @param point origin of distance measurement
     * @return distance, in meters
     */
    public abstract double distance(GISPoint point);
    
	/**
	 * Compute the planar distance between two points.
	 * 
	 * @param eastingA the first cell's easting,
	 * @param northingA the first cell's northing,
	 * @param eastingB the second cell's easting,
	 * @param northingB the second cell's northing.
	 * @return the distance in a plane between the given points.
	 */
	public static double distance( double eastingA, double northingA,
			double eastingB, double northingB) {
		return( 
			(double) Math.sqrt( 
				(double) distanceSquared(
					eastingA, northingA, eastingB, northingB
				)
			)
		);
	}

    /**
     * Compute the planar distance between two points.
     * 
     * @param point the first cell's location
     * @param easting the second cell's easting,
     * @param northing the second cell's northing.
     * @return the distance in a plane between the given points.
     */
	public static double distance( GISPoint point, double easting, double northing) {
		return( 
			(double) Math.sqrt( 
				(double) distanceSquared(
					point.getEasting(), point.getNorthing(), easting, northing
				)
			)
		);
	}

    /**
     * Compute the planar distance between two points.
     * 
     * @param firstPoint the first cell's location
     * @param secondPoint the second cell's location
     * @return the distance in a plane between the given points.
     */
	public static double distance( GISPoint firstPoint, GISPoint secondPoint) {
		return( 
			(double) Math.sqrt( 
				(double) distanceSquared(
					firstPoint.getEasting(), firstPoint.getNorthing(),
					secondPoint.getEasting(), secondPoint.getNorthing()
				)
			)
		);
	}

    /**
     * Compute the square of the planar distance between two points.
     * This is faster than computing the distance then taking the
     * square root.
     * 
     * @param eastingA the first cell's easting,
     * @param northingA the first cell's northing,
     * @param eastingB the second cell's easting,
     * @param northingB the second cell's northing.
     * @return the square of the distance in a plane between the
     *      given points.
     */
	public static double distanceSquared( double eastingA, double northingA,
			double eastingB, double northingB) {
		return( ( eastingA - eastingB)  * ( eastingA - eastingB) +
			 ( northingA - northingB) * ( northingA - northingB)
		);
	}

	public void setNumberDataRealizations( int value) {
		numRealizations = value;
	}

	public int getNumberDataRealizations() {
		return numRealizations;
	}

	/** in alpha testing */
	public static int endNumber( String s, int startNumber) {
		int i = startNumber;
		int sLen = s.length();
		while( i < sLen - 1 && ! Character.isWhitespace( s.charAt( i)))
			i++;
		// i--;
		return( i);
	}

	public Color getColor( double value) {
		return( clt.getColor( value));
	}

	public double getCurrentRatioToNextRealization()  {
		return( view.getRatioBetweenKeyFrames());
	}

	public int getCurrentRealizationNumber()  {
		return( (int) view.getCurrentRealization());
	}

	public int getNumberRealizations() {
		return( view.getNumberRealizations());
	}

	public long getStartTime() {
		return( view.getStartTime());
	}

	public boolean isColorLookupTable() {
		if( clt == null)
			return false;
		return true;
	}

	/** in alpha testing */
	public static int nextNumber( String s, int presentStart) {
		int i = presentStart;
		while( i < s.length() && ! Character.isWhitespace( s.charAt( i)))
			i++;
		while( i < s.length() && Character.isWhitespace( s.charAt( i)))
			i++;
		return( i);
	}

	public void setColorLookupTable( ColorLookupTable table) {
		clt = table;
	}

	public void setRealization( Date nowTime) {
		if( view == null) {
		    throw new DataException("RGISAnimatedView not initialized");
		}
		long time = nowTime.getTime();
		int secondsBetweenKeyFrame = view.getSecondsBetweenKeyFrame();
		long lastTime = view.getTimeCurrentFrame();
		view.setTimeCurrentFrame( time);
		double timeElapsed = (time - lastTime) / 1000.0;
		//System.out.println( "timeElapsed: " + timeElapsed + ", time: " + time + ", lastTime:" + lastTime);
		if( timeElapsed > secondsBetweenKeyFrame) {
			if( view.getCurrentRealization() == view.getNumberRealizations()) {
				view.setCurrentRealization( 0);
			} else {
				view.setCurrentRealization( view.getCurrentRealization() + 1);
			}
			view.setRatioBetweenKeyFrames( 0.0);
		} else {
			long startTime = view.getStartTime();
			long totalTime = time - startTime;
			long realization = (totalTime / 1000) / secondsBetweenKeyFrame;
			double ratioToNextRealization = (totalTime - realization * secondsBetweenKeyFrame * 1000) / 
				(1000.0 * secondsBetweenKeyFrame);
			realization %= view.getNumberRealizations();
			view.setCurrentRealization( (int) realization);
			if( ratioToNextRealization < 0.5) {
				if( ratioToNextRealization > 0.0)
					ratioToNextRealization = Math.pow( 
						ratioToNextRealization * 2.0, view.getRatioExponent()) / 2.0;
			} else {
				double temp = 1.0 - ratioToNextRealization;
				ratioToNextRealization = 1.0 - Math.pow( temp * 2.0, view.getRatioExponent()) / 2.0;
			}
			view.setRatioBetweenKeyFrames( ratioToNextRealization);
		}
	}

	public void setRGISAnimatedView( RGISAnimatedView view) {
		this.view = view;
	}
}