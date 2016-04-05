package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.IOException;
import java.util.Date;
import java.util.Random;


//mean 0.0, std dev 1.0
//d dist at which points are fully independent
//e=1 linear; e>1 curve down; e<0 curve up
//f dist where all points have equal weight

/**
 * Generate a random lattice with optional spatial dependence. Values d, e, and
 * f determine the spatial dependence: all points within distance 'f' will have
 * equal weighting; points beyond 'd' will have no dependence; and the weights
 * between distance 'f' and 'd' is governed by a curve with exponent 'e'.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class LatticeRandomField extends GISLattice {
    private double         dis, exp, fla;
    private boolean        normed;
    private static boolean combineReference = false;
    private static Date    previousDate;

    /**
     * Generate a lattice with given parameters.
     * 
     * @param toDo
     *            grid for which lattice will be generated
     * @param distance
     *            maximum distance of spatial dependence
     * @param exponent
     *            dependence decay exponent
     * @param flat
     *            flat weight distance
     */
	public LatticeRandomField( GISGrid toDo, double distance, double exponent, double flat) {
		super( toDo);
		Date seedDate = new Date();
		if( previousDate != null) {
			while( previousDate.getTime() == seedDate.getTime())
				seedDate = new Date();
		}
		previousDate = seedDate;
		this.make2( toDo, distance, exponent, flat, seedDate.getTime());
	}

	public LatticeRandomField( GISGrid toDo, double distance, double exponent, double flat, long seed) {
		super( toDo);
		this.make2( toDo, distance, exponent, flat, seed);
	}

	public LatticeRandomField( LatticeRandomFieldTemplate template) {
		super( template.getGISGrid());
		Date seedDate = new Date();
		if( previousDate != null) {
			while( previousDate.getTime() == seedDate.getTime()) {
				//System.out.print( "x");
				seedDate = new Date();
			}
		}
		previousDate = seedDate;
		this.make2( template.getGISGrid(), template.getSpatialDependence(), 
			template.getDistanceDecayExponent(), template.getFlatParameter(), seedDate.getTime());
	}

	public LatticeRandomField( LatticeRandomFieldTemplate template, long seed) {
		super(  template.getGISGrid());
		this.make2( template.getGISGrid(), template.getSpatialDependence(), 
			template.getDistanceDecayExponent(), template.getFlatParameter(), seed);
	}
 
	public double getSpatialDependence() {
		return dis;
	}

	public double getDistanceDecayExponent() {
		return exp;
	}

	public double getFlatParameter() {
		return fla;
	}

	public void combine( LatticeRandomField otherRandomField, double otherRandomFieldWeight) {
		if( combineReference == false) {
			combineReference = true;
			System.out.println( "");
			System.out.println( "LatticeRandomField.combine() interpolates two random fields");
			System.out.println( "retaining RF properties and is described in:");
			System.out.println( "Ehlschlaeger, C. R., A. M. Shortridge, M. F. Goodchild, 1997,");
			System.out.println( "Visualizing Spatial Data Uncertainty using Animation:");
			System.out.println( "Computers & Geosciences 23(4): 387-395.");
			System.out.println( "WWW: <http://www.geo.hunter.cuny.edu/~chuck/CGFinal/paper.htm>");
			System.out.println( "");
		}
		if( otherRandomFieldWeight < 0.0 || otherRandomFieldWeight > 1.0) {
		    throw new IllegalArgumentException("otherRandomFieldWeight < 0.0 or > 1.0");
		}
		/*
		System.out.println( "thisRF: ");
		this.print();
		System.out.println( "otherRF: ");
		otherRandomField.print();
		*/
		for( int r = getNumberRows() - 1; r >= 0; r--) {
			for( int c = getNumberColumns() - 1; c >= 0; c--) {
				double easting = getCellCenterEasting( r, c);
				double northing = getCellCenterNorthing( r, c); 
				if( otherRandomField.isNoData( easting, northing) == true) { 
					setNoData( r, c, true);
				} else if( isNoData( r, c) == false){
					double val = getCellValue( r, c);
					double valWeight = Math.cos( otherRandomFieldWeight * Math.PI / 2.0);
					double otherval = otherRandomField.getValue( easting, northing);
					double otherValWeight = Math.sin( otherRandomFieldWeight * Math.PI / 2.0);
					setCellValue( r, c, val * valWeight + otherval * otherValWeight);
				} else {
					setNoData( r, c, true);
				}
			}
		} 
	}

	private void make2( GISGrid toDo, double distance, double exponent, double flat, long seed) {
		normed = false;
		dis = distance;
		exp = exponent;
		fla = flat;
		Random ran = new Random( seed);
		int Rs = toDo.getNumberRows();
		int Cs = toDo.getNumberColumns();
		int plusRows = (int) Math.floor( dis / toDo.getNSResolution());
		int plusCols = (int) Math.floor( dis / toDo.getEWResolution());
		int noiseRows = 2 * plusRows + Rs;
		int noiseCols = 2 * plusCols + Cs;
		GISLattice noise = new GISLattice( toDo.getWestEdge() - plusCols * toDo.getEWResolution(),
			toDo.getNorthEdge() + plusRows * toDo.getNSResolution(),
			toDo.getEWResolution(), toDo.getNSResolution(), noiseRows, noiseCols);
		for( int r = noiseRows - 1; r >= 0; r--) {
			for( int c = noiseCols - 1; c >= 0; c--) {
				noise.setCellValue( r, c, (double) ran.nextGaussian());
			}
		}
		double sum = getSum( noise, 0, 0, plusRows, plusCols);
		// if( seed == 0) System.out.print( sum + ",");
		for( int r = Rs - 1; r >= 0; r--) {
			for( int c = Cs - 1; c >= 0; c--) {
				if( toDo.isNoData( r, c) == false) {
					doCell( noise, r, c, plusRows, plusCols, sum);
				}
			}
		}
		noise = null;
	}

	private void doCell( GISLattice noise, int r, int c, int plusRows, int plusCols, double sum) {
		double v = 0.0f;
		int rootR = r + plusRows;
		int rootC = c + plusCols;
		double weight = 1.0f;
		for( int cc = rootC; cc <= rootC + plusCols; cc++) {
			double dist = noise.distance( rootR, rootC, rootR, cc);
			if( dist > dis) {
				cc = rootC + plusCols + 1;
			} else {
				weight = getWeight( dist);
				v = getV( v, weight, noise.getCellValue( rootR, cc));
			}
		}
		for( int cc = rootC - 1; cc >= rootC - plusCols; cc--) {
			double dist = noise.distance( rootR, rootC, rootR, cc);
			if( dist > dis) {
				cc = rootC - plusCols - 1;
			} else {
				weight = getWeight( dist);
				v = getV( v, weight, noise.getCellValue( rootR, cc));
			}
		}
		int rrd = rootR;
		for( int rr = rootR + 1; rr <= rootR + plusRows; rr++) {
			rrd--;
			if( noise.distance( rootR, rootC, rr, rootC) > dis) {
				rr = rootR + plusRows + 1;
			} else {
				for( int cc = rootC; cc <= rootC + plusCols; cc++) {
					double dist = noise.distance( rootR, rootC, rr, cc);
					if( dist > dis) {
						cc = rootC + plusCols + 1;
					} else {
						weight = getWeight( dist);
						v = getV( v, weight, noise.getCellValue( rr, cc));
						v = getV( v, weight, noise.getCellValue( rrd, cc));
					}
				}
				for( int cc = rootC - 1; cc >= rootC - plusCols; cc--) {
					double dist = noise.distance( rootR, rootC, rr, cc);
					if( dist > dis) {
						cc = rootC - plusCols - 1;
					} else {
						weight = getWeight( dist);
						v = getV( v, weight, noise.getCellValue( rr, cc));
						v = getV( v, weight, noise.getCellValue( rrd, cc));
					}
				}
			}
		}
		v /= sum;
		this.setCellValue( r, c, v);	
	}

	private double getSum( GISLattice noise, int r, int c, int plusRows, int plusCols) {
		double sum = 0.0f;
		int rootR = r + plusRows;
		int rootC = c + plusCols;
		double weight = 1.0f;
		for( int cc = rootC; cc <= rootC + plusCols; cc++) {
			double dist = noise.distance( rootR, rootC, rootR, cc);
			if( dist > dis) {
				cc = rootC + plusCols + 1;
			} else {
				weight = getWeight( dist);
				// w2 sum += weight * weight;
				sum += weight;
			}
		}
		for( int cc = rootC - 1; cc >= rootC - plusCols; cc--) {
			double dist = noise.distance( rootR, rootC, rootR, cc);
			if( dist > dis) {
				cc = rootC - plusCols - 1;
			} else {
				weight = getWeight( dist);
				sum += weight * weight;
			}
		}
		for( int rr = rootR + 1; rr <= rootR + plusRows; rr++) {
			if( noise.distance( rootR, rootC, rr, rootC) > dis) {
				rr = rootR + plusRows + 1;
			} else {
				for( int cc = rootC; cc <= rootC + plusCols; cc++) {
					double dist = noise.distance( rootR, rootC, rr, cc);
					if( dist > dis) {
						cc = rootC + plusCols + 1;
					} else {
						weight = getWeight( dist);
						sum += 2 * weight * weight;
					}
				}
				for( int cc = rootC - 1; cc >= rootC - plusCols; cc--) {
					double dist = noise.distance( rootR, rootC, rr, cc);
					if( dist > dis) {
						cc = rootC - plusCols - 1;
					} else {
						weight = getWeight( dist);
						sum += 2 * weight * weight;
					}
				}
			}
		}

		sum = (double) Math.sqrt( sum);
		return( sum);	
	}

	private double getWeight( double distance) {
		double weight = 1.0f;
		if( distance > fla)
			weight -= Math.pow( ((distance - fla) / (dis - fla)), exp);
		return( weight);
	}

	private double getV( double v, double weight, double noise) {
		double dv = weight * noise;
		return( v + dv);
	}

/*
	private void make( GISGrid toDo, double distance, double exponent, double flat, long seed) {
		//System.out.println( "Start make");
		normed = false;
		dis = distance;
		exp = exponent;
		fla = flat;
		Random ran = new Random( seed);
		//System.out.println( "pre QT distance: " + distance);
		GISPointQuadTree qt = new GISPointQuadTree( getWestEdge() - distance, 
			getNorthEdge() + distance, getEastEdge() + distance, getSouthEdge() - distance, 8);
		//System.out.println( "QT: " + qt.toString());
		double startE = getCellCenterEasting( 0, 0);
		double startN = getCellCenterNorthing( 0, 0);
		for( double doN = startN; doN <= getNorthEdge() + distance; doN += getNSResolution()) {
			doRow( ran, qt, startE, doN);
		}
		for( double doN = startN - getNSResolution(); doN >= getSouthEdge() - distance;
				doN -= getNSResolution()) {
			doRow( ran, qt, startE, doN);
		}
		double disLessFla = dis - fla;
		GISPoint source = new GISPoint( getCellCenterEasting( 0, 0), getCellCenterNorthing( 0, 0));
		LinkedList<GISPoint> pList = qt.getPoints( source, distance);
		double sum = 0.0f;
		GISPoint p;
		while( (p = (GISPoint) pList.remove()) != null) {
			double pd = RGISData.distance( source, p);
			double weight = 1.0f;
			if( pd > fla) {
				weight -= Math.pow( ((pd - fla) / disLessFla), exp);
			}
			sum += weight * weight;
		}
		//System.out.println( "sum: " + sum);
		sum = (double) Math.sqrt( sum);
		//System.out.println( "sum: " + sum);
		for( int r = 0; r < getNumberRows(); r++) {
			for( int c = 0; c < getNumberColumns(); c++) {
				if( toDo.isNoData( r, c) == false) {
					source = new GISPoint( getCellCenterEasting( r, c), getCellCenterNorthing( r, c));
					pList = qt.getPoints( source, distance);
					double v = 0.0f;
					while( (p = (GISPoint) pList.remove()) != null) {
						double pd = RGISData.distance( source, p);
						double weight = 1.0f;
						if( pd > fla) {
							weight -= Math.pow( ((pd - fla) / disLessFla), exp);
						}
						Double[] attributes = (Double[]) p.getAttributes();
						double modify = weight * attributes[ 0].doubleValue();
						if( modify > 0.0f) {
							v += Math.pow( modify, 2.0f);
						} else {
							v -= Math.pow( modify, 2.0f);
						}
					}
					if( v >= 0.0f) {
						v = (double) Math.pow( v / sum, 0.5f);
					} else {
						v = -1.0f * (double) Math.pow( -1.0 * v / sum, 0.5f);
					}
					this.setCellValue( r, c, v);
				}
			}
		}
	}
*/
	
	/** makeUniform() converts gaussian RF to uniform RF w/ values between 0.0-1.0 */
	public void makeUniform() {
        if( normed != true) {
            normed = true;
            final int MIN_MAX_INTERVAL = 10;
            double DT = 1.0 / 100.0;
            int SOD = (int) (2 * MIN_MAX_INTERVAL / DT);
            double norm[] = new double[ SOD];
            double sqr = 1.0 / Math.pow( 2.0 * 3.141592653589793116, 0.5);
            double cc = 0.0;
            for( int i = 0; i < SOD; i++) {
                double t = (i - SOD / 2) * DT;
                double b = Math.exp( -1.0 * t * t / 2.0) * sqr * DT;
                cc += b;
                norm[ i] = cc;
            }
            norm[ SOD - 1] = 1.0;
            for( int r = getNumberRows() - 1; r >= 0; r--) {
                for( int c = getNumberColumns() - 1; c >= 0; c--) {
                    if( ! isNoData( r, c)) {
                        double value = getCellValue( r, c);
                        double ratio = (value + MIN_MAX_INTERVAL) / (MIN_MAX_INTERVAL * 2.0);
                        double indexValue = ratio * (SOD - 1);
                        int indexFloor = (int) Math.floor( indexValue);
                        int indexCeil = (int) Math.ceil( indexValue);
                        if( indexFloor == indexCeil) {
                            setCellValue( r, c, (double) norm[ indexFloor]);
                        } else {
                            double normValue = norm[ indexCeil] * (indexValue - indexFloor) +
                                norm[ indexFloor] * (indexCeil - indexValue);
                            setCellValue( r, c, (double) normValue);
                        }
                    }
                }
            }
        }
	}

	public boolean isUniform() {
		return normed;
	}

	/** @deprecated this method is replaced by makeUniform(), which converts RF to uniform values between 0.0f and 1.0f */
	public void normalize() {
	    makeUniform();
	}

/*
	private void doRow( Random ran, GISPointQuadTree qt, double startE, double startN) {
		//int pts = 0;
		for( double doE = startE; doE <= getEastEdge() + dis; 
				doE += getEWResolution()) {
			//pts++;
			Double[] att = new Double[ 1];
			att[ 0] = new Double( (double) ran.nextGaussian());
			GISPoint p = new GISPoint( doE, startN, att);
			//System.out.println( "adding point: " + p.toString());
			qt.addPoint( p);
		}
		for( double doE = startE - getEWResolution(); doE >= getWestEdge() - dis; 
				doE -= getEWResolution()) {
			//pts++;
			Double[] att = new Double[ 1];
			att[ 0] = new Double( (double) ran.nextGaussian());
			GISPoint p = new GISPoint( doE, startN, att);
			//System.out.println( "adding point: " + p.toString());
			qt.addPoint( p);
		}
		//System.out.print( "    row: " + startN + ", pts: " + pts);
	}
*/

    public static void main( String argv[]) throws IOException {
        double w = 500.0f, 
        n = 1000.0f, 
        ewRes = 40.0f,
        nsRes = 40.0f;
        int r = 2, c = 8;
 
        GISLattice template = new GISLattice( w, n, ewRes, nsRes, r, c);
        template.setValueAll( 1.0f);
        template.writeAsciiEsri( "template");
        double e = 1.0, f = 5.0;
        System.out.println( "Sum,D,A,V");
        for( double d = 40.0f; d <= 10000.0f; d += d / 3.0f) {
            long seed = 0;
            f = d / 2.0;
            int numTest = 200;
            LatticeRandomField rf = null;
            double testCell[] = new double[ numTest];
            double ave = 0.0f;
            for( int m = 0; m < numTest; m++) {
                rf = new LatticeRandomField( template, d, e, f, seed++);
                testCell[ m] = rf.getCellValue( 0, 0);
                ave += testCell[ m];
            }
            double var = 0.0;
            ave /= numTest;
            for( int m = 0; m < numTest; m++) {
                var += (double) Math.pow( testCell[ m] - ave, 2.0f);
            }
            var /= numTest;
            System.out.println( d + "," + ave + "," + var);
        }
    }
}
