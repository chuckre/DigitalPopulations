package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Graphics;
import java.io.IOException;
import java.io.Serializable;



/**
 * Represents axis-aligned bounds of a geographic region. Serves as parent for
 * all classes that represent a rectangular geographic area in UTM coordinates.
 * <p>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GISData extends RGISData implements Serializable {
    // bounds of area
    private double w, n, e, s;
    // not used here, but available to subclasses
    private String name;

    /**
     * Construct blank instance.
     */
    protected GISData() {
    }
    
    /**
     * Construct instance for a specific rectangle.
     * 
     * @param westEdge  easting of west edge of area
     * @param northEdge northing of north edge of area
     * @param eastEdge  easting of east edge of area
     * @param southEdge northing of south edge of area
     */
	public GISData( double westEdge, double northEdge, double eastEdge, double southEdge) {
		w = westEdge; n = northEdge; e = eastEdge; s = southEdge;
	}

	/**
	 * Construct a copy from another GISData object.
	 * 
	 * @param data object to copy
	 */
	public GISData(GISData data) {
	    this(data.w, data.n, data.e, data.s);
	    this.name = data.name;
	}
	
	public boolean equalsBounds(GISData otherData) {
	    return (this == otherData || (w == otherData.w && n == otherData.n
                && e == otherData.e && s == otherData.s));
    }
	
	public double getEastEdge() {
		return( e);
	}

	public double getNorthEdge() {
		return( n);
	}

	public String getName() {
		return name;
	}

	public double getSouthEdge() {
		return( s);
	}

	public double getWestEdge() {
		return( w);
	}

	/**
	 * Determine if given point is within our bounds.
	 * 
	 * @param easting   easting value of point to test
	 * @param northing  northing value of point to test
	 * @return true if point is inside bounds or
	 *     on the edge, or false if outside.
	 */
	public boolean onMap( double easting, double northing) {
		if( easting < getWestEdge() || 
			easting > getEastEdge() || 
			northing < getSouthEdge() || 
			northing > getNorthEdge())
			return( false);
		else	return( true);
	}

    /**
     * Determine if given point is within our bounds.
     * 
     * @param point location of point to test
     * @return true if point is inside bounds or
     *     on the edge, or false if outside.
     */
	public boolean onMap( GISPoint point) {
		double easting = point.getEasting();
		double northing = point.getNorthing();
		if( easting < getWestEdge() || 
			easting > getEastEdge() || 
			northing < getSouthEdge() || 
			northing > getNorthEdge())
			return( false);
		else	return( true);
	}

	public String toString() {
	    return "[GISData|n: " + getNorthEdge() + ", s: " + getSouthEdge() + ", e: " + 
			getEastEdge() + ", w: " + getWestEdge() + "]";
	}

	public void setEastEdge( double east) {
		e = east;
	}

	public void setName( String name) {
		this.name = name;
	}

	public void setNorthEdge( double north) {
		n = north;
	}

	public void setSouthEdge( double south) {
		s = south;
	}

    public void setWestEdge( double west) {
        w = west;
    }

	/**
     * Compute distance from point to closest part of our bounding box. If there
     * is a line that intersects an edge of our bounding box, is perpendicular
     * to that edge, and passes through pt, then distance is the length of that
     * line. If not, distance is from pt to the closest corner of bounds. If
     * point lies entirely within bounds, distance is 0.
     * 
     * @param pt
     *            point to test
     * @return distance to closest point on bounding box from outside, or 0 if
     *         pt is inside bounds or on edge
     */
    public double distance(GISPoint pt) {
        double easting = pt.getEasting();
        double northing = pt.getNorthing();
        double w = getWestEdge();
        double e = getEastEdge();
        double s = getSouthEdge();
        double n = getNorthEdge();

        // Think of map divided into nine squares by the edges
        // of our bounding box:
        //
        //    |   |
        // ---+-n-+---
        //    |   |
        //    w   e
        //    |   |
        // ---+-s-+---
        //    |   |
        //
        // We need to test all nine cases to choose a formula
        // for computing distance.  Since the lines are always
        // aligned with axes, the pt-line distance is simply
        // the distance along an axis.
        if(easting < w) {
            if(northing < s)
                return distance(pt, w,s);
            else if(northing > n)
                return distance(pt, w,n);
            else
                return w-easting;
        }
        else if(easting > e) {
            if(northing < s)
                return distance(pt, e,s);
            else if(northing > n)
                return distance(pt, e,n);
            else
                return easting-e;
        }
        else {
            if(northing < s)
                return s-northing;
            else if(northing > n)
                return northing-n;
            else
                return 0;
        }
    }
	
    /**
     * Draw a visual representation of the contents of this object.
     * <p>
     * GISData draws nothing.
     * 
     * @param g graphics controller with which to draw
     */
	public void paint( Graphics g) {
	    // Draw nothing.
	}

	/**
	 * Construct a set of realizations.
	 * <p>
	 * GISData throws RuntimeException always - realizations are not supported.
	 */
	public void makeRealizations() {
	    throw new RuntimeException("GISData does not support realizations.");
	}

	/**
     * Report whether this object supports realizations.
     * <p>
     * GISData returns false always - realizations are not supported.
	 */
	public boolean isRealizable() {
	    return false;
	}

    /**
     * Create an ASCII-formatted ESRI file from the contents of this object.
     * <p>
     * GISData throws RuntimeException always - it doesn't contain enough data
     * to write a file.
     */
	public void writeAsciiEsri( String fileName) throws IOException {
	    throw new RuntimeException("GISData is insufficient to create an ESRI file.");
	}
}
