package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import mil.army.usace.ehlschlaeger.rgik.util.HashCodeUtil;



/**
 * A point in two-dimensional space.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISPoint extends RGISData implements Serializable {
    /*
    ** *****
    **
    ** WARNING!
    ** This method has custom equals() and hashCode(). If any
    ** changes are made to this class's fields, those methods
    ** will need to be updated.
    **
    ** *****
    */
	private double e, n, value;
	private Object[] att;
	private double standardDeviationError;
	private double originalN, originalE;
	private GISPointSymbol sym;
	private String label;
	// tree must be transient: see GISPointQuadTree.readObject for explanation.
	private transient GISPointQuadTree<? extends GISPoint> tree;
	
	public GISPoint( double easting, double northing) {
		super();
		e = easting;
		n = northing;
		att = null;
		originalN = northing;
		originalE = easting;
		standardDeviationError = -1.0;
		tree = null;
		label = null;
		value = Double.NEGATIVE_INFINITY;
	}

	public GISPoint( double easting, double northing, Object[] attributes) {
		super();
		e = easting;
		n = northing;
		att = attributes;
		tree = null;
	}

	/**
	 * Create new instance as partial copy of given point.
	 * This new instance will copy the location from the original,
	 * and share the attribute array.  Nothing else is copied.
	 * 
	 * @param point object to copy
	 */
    public GISPoint( GISPoint point) {
        this( point.getEasting(), point.getNorthing(), point.getAttributes());
    }

    /**
     * Determine if this point contains the same values as another point.
     * Compares everything except for sym and tree.
     */
    @Override
    public boolean equals(Object obj) {
        GISPoint p1 = this;
        GISPoint p2 = (GISPoint) obj;
        
        boolean eq = true;
        
        // simple stuff
        eq &= p1.e==p2.e && p1.n==p2.n && p1.value==p2.value;
        eq &= p1.originalE==p2.originalE && p1.originalN==p2.originalN;
        eq &= p1.standardDeviationError==p2.standardDeviationError;
        
        // label string
        if(eq) {
            if(p1.label==null || p2.label==null)
                // equal only if both null
                eq &= p1.label==p2.label;
            else
                eq &= p1.label.equals(p2.label);
        }
        
        // att array
        if(eq) {
            if(p1.att==null || p2.att==null)
                // equal only if both null
                eq &= p1.att==p2.att;
            else
                for(int i=0; i<att.length; i++)
                    eq &= p1.att[i]==p2.att[i];
        }
        
        // sym:
        //   Punt.  It doesn't have an equals(), and
        //   == doesn't work after deserialization.
        
        // tree:
        //   Skip.  Not relevant.
        
        return eq;
    }
    
    /**
     * Compute a hash code for this point.
     */
    @Override
    public int hashCode() {
        int h = HashCodeUtil.start();
        // Don't need to hash everything,
        // just the major values.
        h = HashCodeUtil.hash(h, e);
        h = HashCodeUtil.hash(h, n);
        h = HashCodeUtil.hash(h, value);
        h = HashCodeUtil.hash(h, originalE);
        h = HashCodeUtil.hash(h, originalN);
        h = HashCodeUtil.hash(h, standardDeviationError);
        return h;
    }
    
	public void setValue( double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setLabel( String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public GISPointSymbol getSymbol() {
		return sym;
	}

	public void setSymbol( GISPointSymbol symbol) {
		sym = symbol;
	}

	public void setStandardDeviationError( double value) {
		standardDeviationError = value;
	}

	public double getStandardDeviationError( int mapNumber) {
		if( standardDeviationError < 0.0 && tree != null)
			return tree.getDefaultStandardDeviationError( mapNumber);
		return standardDeviationError;
	}

	public GISPointQuadTree<? extends GISPoint> getGISPointQuadTree() {
		return tree;
	}

	public void setGISPointQuadTree( GISPointQuadTree<? extends GISPoint> tree) {
		this.tree = tree;
	}

    /**
     * Compute the distance to another point.
     * 
     * @param point origin of distance measurement
     * @return distance, in meters
     */
	public double distance(GISPoint point) {
	    return distance(point, this);
	}
	
	/** not yet fully implemented
	 * This method determines the current location of the point
	 * based on the time within the animation. Currently, the calculation
	 * only determines the point locations at the start key frame.
	 */
	public void calculateCurrentLocation() {
		int earlyRealization = tree.getCurrentRealizationNumber();
		//System.out.print( "eR:" + earlyRealization + " ");
		GISLattice rf = tree.getLocationEast( earlyRealization);
		double sd = getStandardDeviationError( earlyRealization);
		double rfV = rf.getValue(originalE, originalN);
		//System.out.print( "sd: " + sd + ", rfV: " + rfV);
		double changeE = sd * rfV / sqrtTwo;
		rf = tree.getLocationNorth( earlyRealization);
		double changeN = sd * rf.getValue( originalE, originalN) / sqrtTwo;
		//System.out.println( "earlyRealization: " + earlyRealization);
		int nextRealization = (earlyRealization + 1) % getNumberRealizations(); //error message here 3/22/2003
		// int nextRealization = (earlyRealization + 1) % tree.getNumberRealizations(); possible fix: no
		rf = tree.getLocationEast( nextRealization);
		double endEChange = getStandardDeviationError( nextRealization) *
			rf.getValue(originalE, originalN) / sqrtTwo;
		rf = tree.getLocationNorth( nextRealization);
		double endNChange = getStandardDeviationError( nextRealization) * 
			rf.getValue( originalE, originalN) / sqrtTwo;
		double i = getCurrentRatioToNextRealization();
		setEasting( originalE + changeE * Math.cos( i * 3.1415927 / 2.0) + 
			endEChange * Math.sin( i * 3.1415927 / 2.0));
		setNorthing( originalN + changeN * Math.cos( i * 3.1415927 / 2.0) + 
			endNChange * Math.sin( i * 3.1415927 / 2.0));
		//System.out.println("Easting intermediate= " + getEasting());
		//System.out.println("Easting = " + getEasting());
	}

	/** not fully implemented yet */
	public void paint( Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		if( tree != null) {
			if( tree.isRealizable())
				calculateCurrentLocation();
			if( sym == null) {
				Rectangle2D r = new Rectangle2D.Double( getEasting() - 15, -(getNorthing() + 15), 30, 30);
				g2D.fill(r);
			} else {
				sym.drawPoint( g, this);
			}
		} else {
			if( sym == null) {
				Rectangle2D r = new Rectangle2D.Double( getEasting() - 5, -(getNorthing() + 5), 10, 10);
				g2D.fill(r);
			} else {
				sym.drawPoint( g, this);
			}
		}
	}

	public Object[] getAttributes() {
		return( att);
	}

	public void setAttributes( Object[] attributes) {
		att = attributes;
	}

	public double getEasting() {
		return( e);
	}

	public double getNorthing() {
		return( n);
	}

	public void setEasting( double easting) {
		e = easting;
	}

	public void setNorthing( double northing) {
		n = northing;
	}

	public String toString() {
		String s = "(" + Double.toString( e) + ", " + Double.toString( n) + ")";
		return( s);
	}
}
