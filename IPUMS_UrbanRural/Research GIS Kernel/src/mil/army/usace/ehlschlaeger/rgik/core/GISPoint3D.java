package mil.army.usace.ehlschlaeger.rgik.core;

/** @author Chuck Ehlschlaeger, Dept of Geography, Hunter College
 *  @author work: 212-772-5321, fax: 212-772-5268
 *  @author http://www.geo.hunter.cuny.edu/~chuck/
 *  @version 0.2
 */

	/** in alpha testing */
public class GISPoint3D {
	private double e, n, value;

	public GISPoint3D( double e, double n, double value) {
		this.e = e;
		this.n = n;
		this.value = value;
	}

	public double getEasting() {
		return e;
	}

	public double getNorthing() {
		return n;
	}

	public double getValue() {
		return value;
	}

	public String toString() {
		return( "(" + e + ", " + n + ", " + value + ")");
	}
}