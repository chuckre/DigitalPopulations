package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.ConflatePumsQueryWithTracts;
import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;



/**
 * Stores points in a tree of squares that allows for efficient proximity
 * searching.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISPointQuadTree<T extends GISPoint> extends GISData implements Iterable<T>, Serializable {
    protected T                           array[];
    protected int                         numPoints, maxPoints;
    protected double                      centerE, centerN;
    protected GISPointQuadTree<T>         nw, ne, sw, se;
    protected double                      leafSizeMultiplier;
    protected GISPointQuadTreeInformation info;
    protected Random random = new Random();

    protected GISPointQuadTree() {
        // forbidden except for deserialization
    }
    
    /**
     * Create a new empty quad tree.
     * 
     * @param west   minimum easting of range
     * @param north  maximum northing of range
     * @param east   maximum easting of range
     * @param south  minimum northing of range
     * @param maxPointsPerNode  maximum number of points per tree node.
     *      Beyond this, new sub-nodes will be created.
     */
    public GISPointQuadTree(double west, double north, double east,
            double south, int maxPointsPerNode) {
        super(west, north, east, south);
        if (maxPointsPerNode < 1)
            maxPoints = 1;
        else
            maxPoints = maxPointsPerNode;
        deleteAllPoints();
        centerE = getWestEdge() + (getEastEdge() - getWestEdge()) / 2.0;
        centerN = getSouthEdge() + (getNorthEdge() - getSouthEdge()) / 2.0;
        leafSizeMultiplier = 1.000001;
        info = new GISPointQuadTreeInformation();
        info.setQTRootNode(this);
    }

    /**
     * Create a new empty tree to cover the same area as another map.
     * 
     * @param region bounds of area to cover
     * @param maxPointsPerNode  maximum number of points per tree node.
     *      Beyond this, new sub-nodes will be created.
     */
    public GISPointQuadTree(GISData region, int maxPointsPerNode) {
        this(region.getWestEdge(), region.getNorthEdge(), region.getEastEdge(),
             region.getSouthEdge(), maxPointsPerNode);
    }

    /**
     * Create a new empty quad tree.
     * 
     * @param west   minimum easting of range
     * @param north  maximum northing of range
     * @param east   maximum easting of range
     * @param south  minimum northing of range
     * @param maxPointsPerNode  maximum number of points per tree node.
     *      Beyond this, new sub-nodes will be created.
     * @param information  initial statistics
     */
    public GISPointQuadTree(double west, double north, double east,
            double south, int maxPointsPerNode,
            GISPointQuadTreeInformation information) {
        super(west, north, east, south);
        if (maxPointsPerNode < 1)
            maxPoints = 1;
        else
            maxPoints = maxPointsPerNode;
        deleteAllPoints();
        centerE = getWestEdge() + (getEastEdge() - getWestEdge()) / 2.0;
        centerN = getSouthEdge() + (getNorthEdge() - getSouthEdge()) / 2.0;
        leafSizeMultiplier = 1.000001;
        info = information;
    }

    /**
     * Create a new quad tree, loading points from a CSV table,
     * and automatically setting bounds.
     * 
     * @param pointTable     CSV table from which points will be loaded
     * @param eastingColumn  name of column which contains the easting value of each point
     * @param northingColumn name of column which contains the northing value of each point
     * @param labelColumn    name of column which contains an identifier for each point
     * @param ptSymbol       icon to use for all points
     * @param maxPointsPerNode  maximum number of points per tree node.
     *      Beyond this, new sub-nodes will be created.
     */
    public static GISPointQuadTree<GISPoint> load( CSVTable pointTable,
            String eastingColumn, String northingColumn, 
			String labelColumn, GISPointSymbol ptSymbol, int maxPointsPerNode) {
		int rows = pointTable.getRowCount();
		if( rows <= 0)
		    throw new IllegalArgumentException("no points in pointTable");
		
		int eastCol = pointTable.findColumn( eastingColumn);	
		int northCol = pointTable.findColumn( northingColumn);	
		int labelCol = -1;
		if(labelColumn != null)
		    labelCol = pointTable.findColumn( labelColumn);

		// Calculate bounds of points.
        double minEasting = Double.POSITIVE_INFINITY;
        double maxEasting = Double.NEGATIVE_INFINITY;
        double minNorthing = Double.POSITIVE_INFINITY;
        double maxNorthing = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < rows; r++) {
			double e = Double.parseDouble( pointTable.getStringAt( r, eastCol));
			if( e < minEasting)
				minEasting = e;
			if( e > maxEasting)
				maxEasting = e;
			double n = Double.parseDouble( pointTable.getStringAt( r, northCol));
			if( n < minNorthing)
				minNorthing = n;
			if( n > maxNorthing)
				maxNorthing = n;
		}

		// Set our bounds 10% larger on all sides.
        GISData range = new GISData();
        range.setNorthEdge( maxNorthing + (maxNorthing - minNorthing) * .1);
        range.setSouthEdge( minNorthing - (maxNorthing - minNorthing) * .1);
        range.setEastEdge( maxEasting + (maxEasting - minEasting) * .1);
        range.setWestEdge( minEasting - (maxEasting - minEasting) * .1);
        
        GISPointQuadTree<GISPoint> tree = new GISPointQuadTree<GISPoint>(range, maxPointsPerNode);
        
		for( int r = 0; r < rows; r++) {
			double e = Double.parseDouble( pointTable.getStringAt( r, eastCol));
			double n = Double.parseDouble( pointTable.getStringAt( r, northCol));
			GISPoint pt = new GISPoint( e, n);
			pt.setSymbol( ptSymbol);
			if( labelCol >= 0) {
				String label = pointTable.getStringAt( r, labelCol);
				pt.setLabel( label);
			}
			tree.addPoint( pt);
		}	
		
		return tree;
	}

    /**
     * Create a new quad tree, loading points from arrays,
     * and automatically setting bounds.
     * 
     * @param xArray  array containing the easting value of each point
     * @param yArray  array containing the northing value of each point
     * @param attributes  array of arrays of objects to attach as attributes
     * @param maxPointsPerNode  maximum number of points per tree node.
     *      Beyond this, new sub-nodes will be created.
     */
    public static GISPointQuadTree<GISPoint> load(double[] xArray, double[] yArray, Object[][] attributes, int maxPointsPerNode) {
		assert xArray != null;
		assert yArray != null;
		assert xArray.length == yArray.length;
		assert attributes != null;
		assert attributes.length == xArray.length;

		// Calculate bounds of point list.
        double north = Double.NEGATIVE_INFINITY;
        double east = Double.NEGATIVE_INFINITY;
        double south = Double.POSITIVE_INFINITY;
        double west = Double.POSITIVE_INFINITY;
        for( int i = 0; i < xArray.length; i++) {
            if( north < yArray[ i]) {
                north = yArray[ i];
            }
            if( east < xArray[ i]) {
                east = xArray[ i];
            }
            if( south > yArray[ i]) {
                south = yArray[ i];
            }
            if( west > xArray[ i]) {
                west = xArray[ i];
            }
        }
        
        GISPointQuadTree<GISPoint> tree = new GISPointQuadTree<GISPoint>(west, north, east, south, maxPointsPerNode);
		for( int i = 0; i < xArray.length; i++) {
			if( attributes == null) {
			    tree.addPoint( new GISPoint( xArray[ i], yArray[ i]));
			} else {
			    tree.addPoint( new GISPoint( xArray[ i], yArray[ i], attributes[ i]));
			}
		}
		
		return tree;
	}

    /**
     * Change our source of random numbers.
     * Random numbers are use to re-balance the tree.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        this.random  = source;
    }

    /**
	 * Fetch all child nodes.
	 * @return array of child nodes, or null if none.
	 */
    public GISPointQuadTree<T>[] getSubQTNodes() {
        int count = 0;
        if( nw != null) count++;
        if( sw != null) count++;
        if( se != null) count++;
        if( ne != null) count++;
        if( count == 0) return null;
        
        @SuppressWarnings("unchecked")
        GISPointQuadTree<T>[] subArray = (GISPointQuadTree<T>[]) new GISPointQuadTree<?>[count];

        count = 0;
        if( nw != null) subArray[ count++] = nw;
        if( sw != null) subArray[ count++] = sw;
        if( se != null) subArray[ count++] = se;
        if( ne != null) subArray[ count++] = ne;
        return subArray;
    }

	public void setLeafSizeMultiplier( double sizeMultiplier) {
		leafSizeMultiplier = sizeMultiplier;
	}

	public double getLeafSizeMultiplier() {
		return leafSizeMultiplier;
	}

    /**
     * "Height" of tree from this node to deepest child. Each node, including
     * this one, counts as one.
     */
	public int depth() {
        int d = 0;
        GISPointQuadTree<T>[] kids = getSubQTNodes();
        if (kids != null)
            for (int i = 0; i < kids.length; i++)
                d = Math.max(d, kids[i].depth());
        return d + 1;
    }

	/** this method moves the random fields at the end key frame to the start key frame
	 *  and creates new random fields at the end key frame.
	 */
	public void makeRealizations() {
		//System.out.println( "GISPointQuadTree making new realizations");
		LatticeRandomField[] northRFs = new LatticeRandomField[ getNumberRealizations()];
		LatticeRandomField[] eastRFs = new LatticeRandomField[ getNumberRealizations()];
		for( int i = 0; i < getNumberRealizations(); i++) {
			northRFs[ i] = new LatticeRandomField( info.getRandomFieldTemplate( i));
			eastRFs[ i] = new LatticeRandomField( info.getRandomFieldTemplate( i));
		}
		setLocationNorth( northRFs);
		setLocationEast( eastRFs);
		//setCurrentNumberRealizations( getNumberRealizations());
	}

    /**
     * Move an existing point object to a new location. The exact instance of
     * the object plus all its attributes will be preserved; only its geographic
     * location will change. The point's location in the quad tree will be
     * updated to suit the new geographic location.
     * 
     * @param point
     *            object to relocate
     * @param newEasting
     *            easting value of new location
     * @param newNorthing
     *            northing value of new location
     * 
     * @throws IllegalArgumentException
     *             if new location is not within the bounds of this quad tree.
     *             Tree and point not be modified.
     */
    public void movePoint(T point, double newEasting, double newNorthing) {
        if(!onMap(newEasting, newNorthing))
            throw new IllegalArgumentException("New location is not within the bounds of this quad tree.");
        this.removePoint(point);
        point.setEasting(newEasting);
        point.setNorthing(newNorthing);
        this.addPoint(point);
    }

	/**
	 * Draw all points onto a target.
	 * @param g  object onto which point icons will be painted
	 */
	public void paint( Graphics g) {
		for(int p = 0; p < numPoints; p++)  {
			array[ p].paint( g);	
		}
		if( sw != null)  {
			sw.paint( g);
		}
		if( se != null) {
			se.paint( g);
		}
		if( nw != null)  {
			nw.paint( g);
		}
		if( ne != null)  {
			ne.paint( g);
		}
	}

	public boolean isRealizable() {
		return info.isRealizable();
	}

	/**
	 * Remove all points from this container, but retain all other metadata.
	 */
	@SuppressWarnings("unchecked")
    public void deleteAllPoints() {
	    array = (T[]) new GISPoint[maxPoints];
		numPoints = 0;
		sw = null;
		se = null;
		nw = null;
		ne = null;
	}

    /**
     * Add a point to this container, placing it in the appropriate
     * child node.
     * 
     * @param point  point to add
     * @param fastInsertLessBalanced  true will more quickly insert points, false will more quickly access points
     */
    public void addPoint(T point, boolean fastInsertLessBalanced) {
        point.setGISPointQuadTree( this);
        if( numPoints < maxPoints) {
            array[ numPoints++] = point;
        } else {
            // balance # of points in each sub-quad
            int nePs = 0;
            int nwPs = 0;
            int sePs = 0;
            int swPs = 0;
            if( fastInsertLessBalanced == false) {
                if( point.getEasting() < centerE) { // point west of center
                    if( point.getNorthing() < centerN) { // point south of center
                        swPs++;
                    } else { // point north of center
                        nwPs++;
                    }
                } else { // point east of center
                    if( point.getNorthing() < centerN) { // point south of center
                        sePs++;
                    } else { // point north of center
                        nePs++;
                    }
                }
                int maxInQ = 1;
                for( int i = 0; i < maxPoints; i++) {
                    if( array[i].getEasting() < centerE) { // point west of center
                        if( array[i].getNorthing() < centerN) { // point south of center
                            swPs++;
                            if( maxInQ < swPs) {
                                maxInQ++;
                                T temp = array[ i];
                                array[ i] = point;
                                point = temp;
                            }
                        } else { // point north of center
                            nwPs++;
                            if( maxInQ < nwPs) {
                                maxInQ++;
                                T temp = array[ i];
                                array[ i] = point;
                                point = temp;
                            }
                        }
                    } else { // point east of center
                        if( array[i].getNorthing() < centerN) { // point south of center
                            sePs++;
                            if( maxInQ < sePs) {
                                maxInQ++;
                                T temp = array[ i];
                                array[ i] = point;
                                point = temp;
                            }
                        } else { // point north of center
                            nePs++;
                            if( maxInQ < nePs) {
                                maxInQ++;
                                T temp = array[ i];
                                array[ i] = point;
                                point = temp;
                            }
                        }
                    }
                }
            }
            if( point.getEasting() < centerE) { // point west of center
                if( point.getNorthing() < centerN) { // point south of center
                    if( sw == null) {
                        sw = new GISPointQuadTree<T>( getWestEdge(), centerN, centerE, 
                            getSouthEdge(), (int) (maxPoints * leafSizeMultiplier), info);
                    }
                    sw.addPoint( point, fastInsertLessBalanced);
                } else { // point north of center
                    if( nw == null) {
                        nw = new GISPointQuadTree<T>( getWestEdge(), getNorthEdge(), centerE, 
                            centerN, (int) (maxPoints * leafSizeMultiplier), info);
                    }
                    nw.addPoint( point, fastInsertLessBalanced);
                }
            } else { // point east of center
                if( point.getNorthing() < centerN) { // point south of center
                    if( se == null) {
                        se = new GISPointQuadTree<T>( centerE, centerN, getEastEdge(), 
                            getSouthEdge(), (int) (maxPoints * leafSizeMultiplier), info);
                    }
                    se.addPoint( point, fastInsertLessBalanced);
                } else { // point north of center
                    if( ne == null) {
                        //System.out.println( "making northeast");
                        ne = new GISPointQuadTree<T>( centerE, getNorthEdge(), 
                            getEastEdge(), centerN, (int) (maxPoints * leafSizeMultiplier), info);
                    }
                    ne.addPoint( point, fastInsertLessBalanced);
                }
            }
        }
    }

	/**
	 * Add a point to this container, placing it in the appropriate
	 * child node. Will insert point using balanced tree method (fastInsertLessBalanced == false)
	 * 
	 * @param point  point to add
	 */
	public void addPoint(T point) {
		//Edited by Yizhao
		//addPoint( point, false);
		addPoint( point, true);
		/* previously working code before fastInsertLessBalanced code added
		point.setGISPointQuadTree( this);

		if( numPoints < maxPoints) {
			array[ numPoints++] = point;
		} else {
			// balance # of points in each sub-quad
			int nePs = 0;
			int nwPs = 0;
			int sePs = 0;
			int swPs = 0;
			if( point.getEasting() < centerE) { // point west of center
				if( point.getNorthing() < centerN) { // point south of center
					swPs++;
				} else { // point north of center
					nwPs++;
				}
			} else { // point east of center
				if( point.getNorthing() < centerN) { // point south of center
					sePs++;
				} else { // point north of center
					nePs++;
				}
			}
			int maxInQ = 1;
			for( int i = 0; i < maxPoints; i++) {
				if( array[i].getEasting() < centerE) { // point west of center
					if( array[i].getNorthing() < centerN) { // point south of center
						swPs++;
						if( maxInQ < swPs) {
							maxInQ++;
							GISPoint temp = array[ i];
							array[ i] = point;
							point = temp;
						}
					} else { // point north of center
						nwPs++;
						if( maxInQ < nwPs) {
							maxInQ++;
							GISPoint temp = array[ i];
							array[ i] = point;
							point = temp;
						}
					}
				} else { // point east of center
					if( array[i].getNorthing() < centerN) { // point south of center
						sePs++;
						if( maxInQ < sePs) {
							maxInQ++;
							GISPoint temp = array[ i];
							array[ i] = point;
							point = temp;
						}
					} else { // point north of center
						nePs++;
						if( maxInQ < nePs) {
							maxInQ++;
							GISPoint temp = array[ i];
							array[ i] = point;
							point = temp;
						}
					}
				}
			}
			if( point.getEasting() < centerE) { // point west of center
				if( point.getNorthing() < centerN) { // point south of center
					if( sw == null) {
						sw = new GISPointQuadTree( getWestEdge(), centerN, centerE, 
							getSouthEdge(), (int) (maxPoints * leafSizeMultiplier), info);
					}
					sw.addPoint( point);
				} else { // point north of center
					if( nw == null) {
						nw = new GISPointQuadTree( getWestEdge(), getNorthEdge(), centerE, 
							centerN, (int) (maxPoints * leafSizeMultiplier), info);
					}
					nw.addPoint( point);
				}
			} else { // point east of center
				if( point.getNorthing() < centerN) { // point south of center
					if( se == null) {
						se = new GISPointQuadTree( centerE, centerN, getEastEdge(), 
							getSouthEdge(), (int) (maxPoints * leafSizeMultiplier), info);
					}
					se.addPoint( point);
				} else { // point north of center
					if( ne == null) {
						//System.out.println( "making northeast");
						ne = new GISPointQuadTree( centerE, getNorthEdge(), 
							getEastEdge(), centerN, (int) (maxPoints * leafSizeMultiplier), info);
					}
					ne.addPoint( point);
				}
			}
		}*/
	}

	/**
     * Find the closest point to a given point.
	 * 
	 * @param goal the point around which we'll search
	 * @return the closest point, or null if this container is empty
	 */
	public GISPoint closestPoint( GISPoint goal) {
        GISPoint[] points = closestNPoints(goal, 1);
        GISPoint best = null;
        if(points.length == 1)
            best = points[0];
        return best;
	}

    /**
     * Find all of the points closest to a given point.
     * 
     * @param goal
     *            the point around which we'll search
     * @param numberPoints2Find
     *            the maximum number of points to find
     * @return an array containing the closest points. The array size will be
     *         exactly what's required to hold the found points.  Returns an
     *         array of zero elements if tree is empty.
     */
	public GISPoint[] closestNPoints( GISPoint goal, int numberPoints2Find) {
		if( numberPoints2Find < 1) {
			throw new IllegalArgumentException("numberPoints2Find < 1");
		}
		ClosestNPointObject o = closestNPoints( goal, 
			new ClosestNPointObject( null, null), numberPoints2Find);
		GISPoint[] bestPoints = o.pts;
		return bestPoints;
	}

	protected ClosestNPointObject closestNPoints( GISPoint goal, ClosestNPointObject o, 
			int numberPoints2Find) {
        BubbleSort bs = new BubbleSort( false);
		GISPoint[] bestSoFar = o.pts; 
		double[] dist = o.dist;
		int foundPoints = 0;
		if( bestSoFar != null) {
			foundPoints = bestSoFar.length;
		}
		
		int newLength = numPoints + foundPoints;
		GISPoint[] newBSF = new GISPoint[ newLength];
		double[] newDist = new double[ newLength];
		
		// Copy over best-so-far points
		for( int i = 0; i < foundPoints; i++) {
			newBSF[ i] = bestSoFar[ i];
			newDist[ i] = dist[ i];
		}
		
		// Append all of this node's points
		int j = 0;
		for( int i = foundPoints; i < newBSF.length; i++) {
			newBSF[ i] = array[ j];
			newDist[ i] = distance( array[ j], goal);
			j++;
		}
		
		// Sort by distance
        int[] order = new int[ newLength];
		bs.sort( order, newDist);
		
		// Keep only the closest
		int bestLength = (int) Math.min( numberPoints2Find, newBSF.length);
		bestSoFar = new GISPoint[ bestLength];
		dist = new double[ bestLength];
		for( int i = 0; i < bestLength; i++) {
			bestSoFar[ i] = newBSF[order[ i]];
			dist[ i] = newDist[ order[ i]];
		}

		// Collect stats
		boolean needMore = bestLength < numberPoints2Find;
		GISPoint farthestBest = bestSoFar[bestLength - 1];
		double bestDistance = distance(goal, farthestBest);
		
		// Process sub-quads
		GISPointQuadTree<T>[] subQuads = getSubQTNodes();
		if(subQuads != null) {
		    // Compute distance
            double[] subDist = new double[subQuads.length];
    		for(int i=0; i<subQuads.length; i++) {
    		    subDist[i] = subQuads[i].distance(goal);
    		}
    
    		// Sort by distance
    		int[] subOrder = new int[subQuads.length];
    		bs.sort(subOrder, subDist);
    		
    		// Search each for more points, in order of distance
    		for(int i=0; i<subQuads.length; i++) {
    		    GISPointQuadTree<?> subquad = subQuads[subOrder[i]];
    		    double subdist = subDist[subOrder[i]];
    		    // Shortcut: If we need more points, then always search subquad.
    		    // But if not, and subquad is farther than our farthest best
    		    // point, then skip, as every point in it is too far to make list.
    		    if(needMore || subdist < bestDistance) {
                    ClosestNPointObject newO = subquad.closestNPoints( goal, 
                              new ClosestNPointObject( bestSoFar, dist), 
                              numberPoints2Find);
                    bestSoFar = newO.pts;
                    dist = newO.dist;
    		    }
    		}
		}		
		
		return( new ClosestNPointObject( bestSoFar, dist));
	}

    /**
     * Remove a point from the tree. The object must be the exact object stored
     * in the tree. If you only have the coordinates, use {@link #closestPoint(GISPoint)}
     * to find the object.
     * 
     * @param pt
     *            the object to remove from the tree
     * @return true of object was found and removed, or false if not found
     */
	public boolean removePoint( T pt) {
		return( removePoint( pt, this, this));
	}

    /**
     * Perform the actual work of removing a point and rebalancing the tree.
     * 
     * @param pt
     *            the object to remove from the tree
     * @param parent
     *            the tree node above this node
     * @param root
     *            the top node of the entire tree
     * @return true of object was found and removed, or false if not found
     */
	protected boolean removePoint(T pt, GISPointQuadTree<T> parent, GISPointQuadTree<T> root) {
		if( numPoints > 0) {
			boolean did_it = false;
			if( array[ numPoints - 1] == pt) {
				array[ numPoints - 1] = null;
				pt.setGISPointQuadTree(null);
				numPoints--;
				did_it = true;
			} else {
				for( int p = 0; p < numPoints - 1; p++) {
					if( array[ p] == pt) {
						array[ p] = array[ numPoints - 1];
						array[ numPoints - 1] = null;
		                pt.setGISPointQuadTree(null);
						numPoints--;
						did_it = true;
					}
				}
			}
			if( did_it == true) {
				if( numPoints == maxPoints - 1) {
					parent = null;
					GISPointQuadTree<T> todo = this;
					GISPointQuadTree<T>[] subs = todo.getSubQTNodes();
					if( subs != null) {
						while( subs != null) {
							int doSub = (int) Math.floor( 
								random.nextDouble() * subs.length);
							parent = todo;
							todo = subs[ doSub];
							subs = todo.getSubQTNodes();
						}
						array[ numPoints] = todo.array[ todo.numPoints - 1];
						array[ numPoints].setGISPointQuadTree(this);
						todo.array[ todo.numPoints - 1] = null;
						todo.numPoints--;
						numPoints++;
						if( todo.numPoints == 0 && parent != null) {
							if( parent.sw == todo) {
								parent.sw = null;
							} else if( parent.se == todo) {
								parent.se = null;
							} else if( parent.nw == todo) {
								parent.nw = null;
							} else if( parent.ne == todo) {
								parent.ne = null;
							} else {
							    throw new RuntimeException("aaa1 shouldn't happen");
							}
						}
					}
				} else if( numPoints == 0) {
					// delete this node from QT unless root node.
					if( this != root) {
						if( parent.sw == this) {
							parent.sw = null;
						} else if( parent.se == this) {
							parent.se = null;
						} else if( parent.nw == this) {
							parent.nw = null;
						} else if( parent.ne == this) {
							parent.ne = null;
						}
					}
				} else if( sw != null || ne != null || nw != null || se != null) {
				    throw new RuntimeException("There should be no empty QT nodes.");
				}
				return true;
			}
		}
		if( pt.getEasting() < centerE) {
			if( pt.getNorthing() < centerN) {
				if( sw != null) {
					return( sw.removePoint( pt, this, root));
				}
			} else {
				if( nw != null) {
					return( nw.removePoint( pt, this, root));
				}
			}
		} else {
			if( pt.getNorthing() < centerN) {
				if( se != null) {
					return( se.removePoint( pt, this, root));
				}
			} else {
				if( ne != null) {
					return( ne.removePoint( pt, this, root));
				}
			}
		}
		return false;
	}

	/**
	 * Count all points strictly inside a circle.
	 * Points on the perimeter will be ignored.
	 * 
	 * @param centerPoint  center of circle to search
	 * @param radiusPoint  example point on perimeter of circle
	 * @return number of points inside given circle
	 */
	public int pointsBetween( GISPoint centerPoint, GISPoint radiusPoint) {
		double maxDist = RGISData.distance( centerPoint, radiusPoint);
		int points = 0;
		double cN = centerPoint.getNorthing();
		double cE = centerPoint.getEasting();
		for( int p = 0; p < numPoints; p++) {
			double pDistance = distance( cE, cN, 
				array[ p].getEasting(), array[ p].getNorthing());
			if( pDistance < maxDist) {
				points++;
			}
		}
		if( cE <= (centerE + maxDist) &&
				cN >= (centerN - maxDist) &&
				cE >= (getWestEdge() - maxDist) &&
				cN <= (getNorthEdge() + maxDist) &&
				nw != null) {
			points += nw.pointsBetween( centerPoint, radiusPoint);
		}
		if( cE <= (centerE + maxDist) &&
				cN <= (centerN + maxDist) &&
				cE >= (getWestEdge() - maxDist) &&
				cN >= (getSouthEdge() - maxDist) &&
				sw != null) {
			points += sw.pointsBetween( centerPoint, radiusPoint);
		}
		if( cE >= (centerE - maxDist) &&
				cN >= (centerN - maxDist) &&
				cE <= (getEastEdge() + maxDist) &&
				cN <= (getNorthEdge() + maxDist) &&
				ne != null) {
			points += ne.pointsBetween( centerPoint, radiusPoint);
		}
		if( cE >= (centerE - maxDist) &&
				cN <= (centerN + maxDist) &&
				cE <= (getEastEdge() + maxDist) &&
				cN >= (getSouthEdge() - maxDist) &&
				se != null) {
			points += se.pointsBetween( centerPoint, radiusPoint);
		}
		return points;
	}

	/**
     * Finds all points within a distance of a point, and returns them
     * in a new linked list.
     * 
     * @param goal center point.
     * @param distance radius of area to scan.
	 */
	public LinkedList<T> getPoints( GISPoint goal, double distance) {
		LinkedList<T> list = new LinkedList<T>();
		getPoints( goal, distance, list);
		return list;
	}

	/**
	 * Finds all points within a distance of a point, and adds them
	 * to given linked list.
	 * 
	 * @param goal center point.
	 * @param distance radius of area to scan.
	 * @param list LinkedList to receive all points in scanned area.
	 */
	protected void getPoints( GISPoint goal, double distance, LinkedList<T> list) {
		for( int p = 0; p < numPoints; p++) {
			double pDistance = distance( goal.getEasting(), goal.getNorthing(),
					array[ p].getEasting(), array[ p].getNorthing());
			if( pDistance <= distance) {
				list.add( array[ p]);
			}
		}
		double goalN = goal.getNorthing();
		double goalE = goal.getEasting();
		if( goalE <= (centerE + distance) &&
				goalN >= (centerN - distance) &&
				goalE >= (getWestEdge() - distance) &&
				goalN <= (getNorthEdge() + distance) &&
				nw != null) {
			nw.getPoints( goal, distance, list);
		}
		if( goalE <= (centerE + distance) &&
				goalN <= (centerN + distance) &&
				goalE >= (getWestEdge() - distance) &&
				goalN >= (getSouthEdge() - distance) &&
				sw != null) {
			sw.getPoints( goal, distance, list);
		}
		if( goalE >= (centerE - distance) &&
				goalN >= (centerN - distance) &&
				goalE <= (getEastEdge() + distance) &&
				goalN <= (getNorthEdge() + distance) &&
				ne != null) {
			ne.getPoints( goal, distance, list);
		}
		if( goalE >= (centerE - distance) &&
				goalN <= (centerN + distance) &&
				goalE <= (getEastEdge() + distance) &&
				goalN >= (getSouthEdge() - distance) &&
				se != null) {
			se.getPoints( goal, distance, list);
		}
	}

    /**
     * Build simple iterator for list. Iterator.remove() is not supported, as it
     * makes iteration erratic.  Iteration is in breadth-first order.
     * 
     * @return result of toListByBreadth() as an Iterator
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            GISPointQuadTreeBreadthFirstIterator<T> bfi = new GISPointQuadTreeBreadthFirstIterator<T>(GISPointQuadTree.this);
            T prev = null;
            T next = (T) bfi.next();
            
            public boolean hasNext() {
                return next != null;
            }

            public T next() {
                if(next == null)
                    throw new NoSuchElementException();
                else {
                    prev = next;
                    next = (T) bfi.next();
                    return prev;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Return all the points in and below this node in a list. Changes to the
     * list itself will have no effect on the tree, though changes to the points
     * will be preserved.
     * <p>
     * WARNING: Each point will continue to have a pointer to the tree node from
     * which it came, under the assumption that the tree continues to exist. You
     * will need to null that pointer if you want to delete the tree.
     * 
     * @return List<T> of the contents in this tree, in the order
     *         dictated by GISPointQuadTreeBreadthFirstIterator.
     * @see GISPointQuadTreeBreadthFirstIterator#toList(GISPointQuadTree)
     */
	public List<T> toListByBreadth() {
	    return GISPointQuadTreeBreadthFirstIterator.toList(this);
	}
	
	public String toString() {
		String s = "west edge: " + Double.toString( getWestEdge()) + 
		", south edge: " + Double.toString( getSouthEdge()) + 
		", east edge: " + Double.toString( getEastEdge()) + 
		", north edge: " + Double.toString( getNorthEdge()) + ", number points: " + numPoints +
		", max points: " + maxPoints + ", Points: ";
		for( int i = 0; i < maxPoints; i++) {
			if( array[ i] == null) { // shouldn't happen
				s = s + "null ";
			} else {
				s = s + array[ i].toString() + "; ";
			}
		}
		if( nw == null) s = s + "nw=null ";
		else		s = s + "nw=is ";
		if( ne == null) s = s + "ne=null ";
		else		s = s + "ne=is ";
		if( sw == null) s = s + "sw=null ";
		else		s = s + "sw=is ";
		if( se == null) s = s + "se=null";
		else		s = s + "se=is";
		return( s);
	}

	/** this method creates all realizations with the same amount of horizontal error: A 
	 *  dangerous and/or foolish assumption in uncertainty analysis.
	 */
	public void setDefaultStandardDeviationError( double value) {
		info.setDefaultStandardDeviationError( value);
	}

	public void setDefaultStandardDeviationError( double[] values) {
		info.setDefaultStandardDeviationError( values);
	}

	/** this method creates all realizations with the same random field parameters: A 
	 *  dangerous and/or foolish assumption in uncertainty analysis.
	 */
	public void setRandomFieldTemplates( LatticeRandomFieldTemplate templet) {
		info.setRandomFieldTemplates( templet);
	}

	public void setRandomFieldTemplates( LatticeRandomFieldTemplate[] templates) {
		info.setRandomFieldTemplates( templates);
	}

	public void setLocationNorth( LatticeRandomField[] lattices) {
		info.setLocationNorth( lattices);
	}

	public void setLocationEast( LatticeRandomField[] lattices) {
		info.setLocationEast( lattices);
	}

	public double getDefaultStandardDeviationError( int mapNumber) {
		return info.getDefaultStandardDeviationError( mapNumber);
	}

	public LatticeRandomFieldTemplate getRandomFieldTemplate( int mapNumber) {
		return info.getRandomFieldTemplate( mapNumber);
	}

	public LatticeRandomField getLocationNorth( int mapNumber)  {
		return info.getLocationNorth( mapNumber);
	}

	public LatticeRandomField getLocationEast( int mapNumber)  {
		return info.getLocationEast( mapNumber);
	}

	public T[] getPointArray() {
		return array;
	}

	/** in alpha testing 
	 * @throws IOException */
	public void writeAsciiEsri( String fileName) throws IOException {
		PrintWriter out = new PrintWriter(
				new BufferedWriter( new FileWriter( getFileName( fileName))));
		out.println( "north         " + getNorthEdge());
		out.println( "south         " + getSouthEdge());
		out.println( "east          " + getEastEdge());
		out.println( "west          " + getWestEdge());
		writePoints( out);
		out.close();
	}

	/**
	 * @return number of objects stored in this tree node
	 */
	public int getNumberPointsInNode() { 
		return numPoints;
	}

	/**
	 * @return number of objects stored in entire tree starting at this node
	 */
	public int getNumberPointsIncludingSubNodes() { 
		int n = numPoints;
		if( nw != null) {
			n += nw.getNumberPointsIncludingSubNodes();
		}
		if( ne != null) {
			n += ne.getNumberPointsIncludingSubNodes();
		}
		if( sw != null) {
			n += sw.getNumberPointsIncludingSubNodes();
		}
		if( se != null) {
			n += se.getNumberPointsIncludingSubNodes();
		}
		return n;
	}

	/**
     * This function checks whether the quadtree is properly formed. Used for
     * debugging purposes only.
     */
	public void checkQT() {
		for( int i = 0; i < numPoints; i++) {
			if( array[ i] == null) {
			    throw new DataException("i[" + i + "] is null. numP: " + numPoints +
					", maxP: " + maxPoints);
			}
			else {
			    if(array[i].getGISPointQuadTree() != this)
			        throw new DataException("point has bad parent");
			}
		}
		if( numPoints < maxPoints) {
			if( se != null) {
			    throw new DataException("se != null. numP: " + numPoints +
					", maxP: " + maxPoints);
			}
			if( sw != null) {
			    throw new DataException("sw != null. numP: " + numPoints +
					", maxP: " + maxPoints);
			}
			if( ne != null) {
			    throw new DataException("ne != null. numP: " + numPoints +
					", maxP: " + maxPoints);
			}
			if( nw != null) {
			    throw new DataException("nw != null. numP: " + numPoints +
					", maxP: " + maxPoints);
			}
		} else {
			if( se != null) {
				se.checkQT();
			}
			if( sw != null) {
				sw.checkQT();
			}
			if( ne != null) {
				ne.checkQT();
			}
			if( nw != null) {
				nw.checkQT();
			}
		}
	}

	protected void writePoints( PrintWriter out) {
		if( numPoints > 0) {
			for( int i = 0; i < numPoints; i++) {
				out.println( array[ i].toString());
			}
		}
		if( nw != null) {
			nw.writePoints( out);
		}
		if( ne != null) {
			ne.writePoints( out);
		}
		if( sw != null) {
			sw.writePoints( out);
		}
		if( se != null) {
			se.writePoints( out);
		}
	}

	protected String getFileName(String fileName) {
        return (fileName + ".pqt");
    }

    /**
     * Custom de-serializer to rebuild the GISPoint-QuadTree link. Serializing a
     * QT is not normally a problem, as they're not usually very deep, and
     * though each GISPoint has a pointer back to a tree node, it's always its
     * own container. So serializing a node doesn't need to recurse very far.
     * <p>
     * The problem is with sub-classes of GISPoint, particularly
     * {@link PumsHouseholdRealization}. PHR has a relationship with
     * {@link PumsHousehold} which roughly mimics the class/instance
     * relationship. PHR has pointer to its PH parent, and PH keeps an array of
     * all PHRs that use it. This results in two completely independent graphs:
     * one between QT and PHR, and one between PHR and PH. Serializing a QT can
     * cause recursion into PHR, which can cause recursion into a different QT
     * node, which can cause recursion into a different PHR, etc.
     * <p>
     * When trying to serialize {@link ConflatePumsQueryWithTracts}, this
     * cross-recursion actually causes serialization to overflow Java's stack. A
     * simple solution is to break some of the links on export to prevent
     * recursion. In GISPoint, <code>tree</code> has been flagged transient to
     * break one such link. This method repairs those links on import.
     * <p>
     * This is sufficient to eliminate the problem for now. But if it reoccurs,
     * the next step might be to break the PH/PHR link the same way.
     */
	private void readObject(java.io.ObjectInputStream stream)
	        throws IOException, ClassNotFoundException {
	    // Use default deserializer
	    stream.defaultReadObject();
	    // .. then fixup tree pointers.
	    for(int i=0; i<numPoints; i++)
	        array[i].setGISPointQuadTree(this);
	}
	
	//Added by Yizhao Gao (ygao29@illinois.edu)
	public void swapPoints(T point1, T point2)
	{
		if(!replacePointPointer(point1, point2))
		{
			T temPoint = (T)this.closestPoint(new GISPoint(point1.getEasting(), point1.getNorthing()));
			
			System.out.println("Target point ("+point1.getEasting()+","+point1.getNorthing()+")");
			System.out.println("Nearest point ("+temPoint.getEasting()+","+temPoint.getNorthing()+")");
			if(temPoint == point1)
			{
				System.out.println("Two points are the same");
			}
			else 
			{
				System.out.println("Two points are different");
			}
			
			throw new DataException("Can't find target household in the quadtree");
		}
		if(!replacePointPointer(point2, point1))
		{
			T temPoint = (T)this.closestPoint(new GISPoint(point2.getEasting(), point2.getNorthing()));
			
			System.out.println("Target point ("+point2.getEasting()+","+point2.getNorthing()+")");
			System.out.println("Nearest point ("+temPoint.getEasting()+","+temPoint.getNorthing()+")");
			if(temPoint == point2)
			{
				System.out.println("Two points are the same");
			}
			else 
			{
				System.out.println("Two points are different");
			}
			
			throw new DataException("Can't find target household in the quadtree");
		}
		
		double x = point1.getEasting();
		double y = point1.getNorthing();
		
		point1.setEasting(point2.getEasting());
		point1.setNorthing(point2.getNorthing());
		
		point2.setEasting(x);
		point2.setNorthing(y);
	}
	
	//Added by Yizhao Gao (ygao29@illinois.edu)
	public boolean replacePointPointer(T from, T to)
	{
		boolean replaced = false;
		for(int i = 0; i < this.numPoints; i++)
		{
			if(this.array[i] == from)
			{
				this.array[i] = to;
				return true;
			}
		}
		
		double x = from.getEasting();
		double y = from.getNorthing();
		
		if(x < centerE)
		{
			if(y < centerN)
			{
				if(this.sw != null)
				{
					replaced = this.sw.replacePointPointer(from, to);
				}
			}
			else
			{
				if(this.nw != null)
				{
					replaced = this.nw.replacePointPointer(from, to);
				}
			}
		}
		else
		{
			if(y < centerN)
			{
				if(this.se != null)
				{
					replaced = this.se.replacePointPointer(from, to);
				}
			}
			else
			{
				if(this.ne != null)
				{
					replaced = this.ne.replacePointPointer(from, to);
				}
			}
		}
		
		return replaced;
	}
}

class ClosestNPointObject {
	public GISPoint[] pts;
	public double[] dist;

	public ClosestNPointObject( GISPoint[] pts, double[] dist) {
		this.pts = pts;
		this.dist = dist;
	}
}
