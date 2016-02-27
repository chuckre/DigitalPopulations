package mil.army.usace.ehlschlaeger.rgik.util;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *  <http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class LinearDecay extends TransformDouble {

	public LinearDecay() {
		super();
	}

	/** distanceProportion should be a value between 0.0 (no distance) and 1.0 (maximum distance
	 *  of filter function).
	 */
	public double getDouble( double distanceProportion) {
		double inv = 1.0 - distanceProportion;
		return( inv);
	}


	public static void main( String argv[]) {
		LinearDecay ld = new LinearDecay();
		for( double dist = 0.0; dist <= 1.0; dist += .2) {
			System.out.println( "     dist: " + dist + ", value: " + ld.getDouble( dist));
		}
		System.exit( 0);
	}
}