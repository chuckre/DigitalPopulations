package mil.army.usace.ehlschlaeger.rgik.core;


/**
 * Holds spatial dependence, decay exponent, flatness, and a 'is-normalized'
 * flag for a region of a grid.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class LatticeRandomFieldTemplate extends RGIS {
	private double d, e, f;
	private GISGrid region;
	private boolean normed;

	public LatticeRandomFieldTemplate() {
		super();
		d = 0.0;
		e = 1.0;
		f = 0.0;
		normed = false;
		region = null;
	}

	public LatticeRandomFieldTemplate( LatticeRandomField lattice) {
		super();
		d = lattice.getSpatialDependence();
		e = lattice.getDistanceDecayExponent();
		f = lattice.getFlatParameter();
		normed = lattice.isUniform();
		region = (GISGrid) lattice;
	}

	public LatticeRandomFieldTemplate( LatticeRandomFieldTemplate templet) {
		super();
		d = templet.getSpatialDependence();
		e = templet.getDistanceDecayExponent();
		f = templet.getFlatParameter();
		normed = templet.isUniform();
		region = templet.getGISGrid();
	}

	public GISGrid getGISGrid() {
		return region;
	}

	public double getSpatialDependence() {
		return d;
	}

	public double getDistanceDecayExponent() {
		return e;
	}

	public double getFlatParameter() {
		return f;
	}

	public boolean isUniform() {
		return normed;
	}

	public void setGISGrid( GISGrid region) {
		this.region = region;
	}

	public void setSpatialDependence( double value) {
		d = value;
	}

	public void setDistanceDecayExponent( double value) {
		e = value;
	}

	public void setFlatParameter( double value) {
		f = value;
	}

	public void setUniform( boolean value) {
		normed = value;
	}
}