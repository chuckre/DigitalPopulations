package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.Serializable;


/** GISPointQuadTreeInformation class provides animation information for quad tree.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GISPointQuadTreeInformation implements Serializable {
	private double defaultStandardDeviationError[];
	private LatticeRandomFieldTemplate templates[];
	private LatticeRandomField locationN[], locationE[];
	private GISPointQuadTree<?> root;

	public GISPointQuadTreeInformation() {
	}

	public boolean isRealizable() {
		if( templates == null)
			return false;
		if( locationN == null)
			return false;
		if( locationE == null)
			return false;
		if( defaultStandardDeviationError == null)
			return false;
		return true;
	}

	public void setQTRootNode( GISPointQuadTree<?> rootNode) {
		root = rootNode;
	}

	public GISPointQuadTree<?> getQTRootNode() {
		return root;
	}

	/** this method creates all realizations with the same amount of horizontal error: A 
	 *  dangerous and/or foolish assumption in uncertainty analysis.
	 */
	public void setDefaultStandardDeviationError( double value) {
		defaultStandardDeviationError = new double[ 1];
		defaultStandardDeviationError[ 0] = value;
	}

	public void setDefaultStandardDeviationError( double[] values) {
		if( values.length != root.getNumberDataRealizations()) {
		    throw new DataException("array size must match number of realizations");
		}
		defaultStandardDeviationError = values;
	}

	public double getDefaultStandardDeviationError( int mapNumber) {
		if( defaultStandardDeviationError.length == 1) {			
			return defaultStandardDeviationError[ 0];
		}
		return defaultStandardDeviationError[ mapNumber];
	}

	/** this method creates all realizations with the same random field parameters: A 
	 *  dangerous and/or foolish assumption in uncertainty analysis.
	 */
	public void setRandomFieldTemplates( LatticeRandomFieldTemplate templet) {
		this.templates = new LatticeRandomFieldTemplate[ 1];
		templates[ 0] = templet;
	}

	public void setRandomFieldTemplates( LatticeRandomFieldTemplate[] templates) {
		if( templates.length != root.getNumberDataRealizations()) {
            throw new DataException("array size must match number of realizations");
		}
		this.templates = templates;
	}

	public LatticeRandomFieldTemplate getRandomFieldTemplate( int mapNumber) {
		if( templates.length == 1) {			
			return templates[ 0];
		}
		return templates[ mapNumber];
	}

	public void setLocationNorth( LatticeRandomField[] lattices) {
		locationN = lattices;
	}

	public LatticeRandomField getLocationNorth( int mapNumber)  {
		return locationN[ mapNumber];
	}

	public void setLocationEast( LatticeRandomField[] lattices) {
		locationE = lattices;
	}

	public LatticeRandomField getLocationEast( int mapNumber)  {
		return locationE[ mapNumber];
	}
}