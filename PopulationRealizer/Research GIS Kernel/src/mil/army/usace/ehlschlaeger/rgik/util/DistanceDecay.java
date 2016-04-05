package mil.army.usace.ehlschlaeger.rgik.util;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *  <http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class DistanceDecay extends TransformDouble {
	private double dd;

	public DistanceDecay( double distanceDecayExponent) {
		super();
		dd = distanceDecayExponent;
	}

	/** distanceProportion should be a value between 0.0 (no distance) and 1.0 (maximum distance
	 *  of filter function).
	 */
	public double getDouble( double distanceProportion) {
		double inv = 1.0 - distanceProportion;
		double v = Math.pow( inv, dd);
		return( v);
	}


	public static void main( String argv[]) {
		for( double e = 0.0; e < 1.0; e += .20) {
			DistanceDecay dd = new DistanceDecay( e);
			System.out.println( "exp: " + e);
			for( double dist = 0.0; dist <= 1.0; dist += .2) {
				System.out.println( "     dist: " + dist + ", value: " + dd.getDouble( dist));
			}
		}
		for( double e = 1.0; e <= 3.0; e += 1.0) {
			DistanceDecay dd = new DistanceDecay( e);
			System.out.println( "exp: " + e);
			for( double dist = 0.0; dist <= 1.0; dist += .2) {
				System.out.println( "     dist: " + dist + ", value: " + dd.getDouble( dist));
			}
		}
		System.exit( 0);
	}
}