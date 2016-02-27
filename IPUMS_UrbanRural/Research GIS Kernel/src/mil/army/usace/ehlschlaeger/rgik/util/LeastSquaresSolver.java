package mil.army.usace.ehlschlaeger.rgik.util;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISPoint3D;

/**
 * This object has not been tested.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class LeastSquaresSolver {
	private Matrix X, y, b, e;

	public LeastSquaresSolver() {
		b = null;
		e = null;
		X = null;
		y = null;
	}

    /**
     * the Matrix X should be in the form specified by Montgomery and Peck's
     * "Introduction to Linear Regression Analysis, 2nd Ed" Chapter four (4.2.1)
     */
	public void setX( Matrix X) {
		this.X = X;
		ifReadyDo();
	}

	public void sety( Matrix y) {
		this.y = y;
		ifReadyDo();
	}

	private void ifReadyDo() {
		if( X == null || y == null) {
			return;
		}
		if( X.getNumberRows() != y.getNumberRows()) {
		    throw new DataException("Matrices X and y should have same number of rows.");
		}
		b = getBeta( y, X);
	}

	public double getEstimatedValue( double[] variableValues) {
	    assert variableValues != null;
	    assert variableValues.length == b.getNumberRows() - 1;
        if( b == null)
            throw new DataException("beta not calculated yet.");

		double value = b.getCell( 0, 0);
		for( int v = 1; v < b.getNumberRows(); v++) {
			value += b.getCell( v, 0) * variableValues[ v - 1];
		}
		return( value);
	}

	public double[] getBetas() {
		if( b == null)
		    throw new DataException("beta not calculated yet.");

		double[] bArray = new double[ b.getNumberRows()];
		for( int v = 0; v < b.getNumberRows(); v++) {
			bArray[ v] = b.getCell( v, 0);
		}
		return( bArray);
	}

	public double[] getErrors() {
        if( b == null)
            throw new DataException("beta not calculated yet.");

        e = getEpsilon( y, X, b);
		double[] eArray = new double[ e.getNumberRows()];
		for( int v = 0; v < e.getNumberRows(); v++) {
			eArray[ v] = e.getCell( v, 0);
		}
		return( eArray);
	}

    /**
     * getBeta() estimates the regression coefficients using least squares for a
     * linear multiple regression. See Montgomery and Peck's
     * "Introduction to Linear Regression Analysis, 2nd Ed" Chapter four (4.2.1)
     * for definitions. Equation: beta estimate = (X' X)^-1 X' y
     */
    public static Matrix getBeta( Matrix y, Matrix X) { 
        Matrix xTranspose = X.transpose();
        Matrix beta = xTranspose.multiply(X).inverse().multiply(xTranspose).multiply(y);
        return beta;
    }
    
    /**
     * getEpsilon() determines the errors of the data points for a set of
     * regression coefficients. See Montgomery and Peck's
     * "Introduction to Linear Regression Analysis, 2nd Ed" Chapter four (4.2.1)
     * for definitions. Equation: y = X beta + epsilon
     */
    public static Matrix getEpsilon( Matrix y, Matrix X, Matrix beta) {
        Matrix epsilon = y.clone();
        epsilon.subtract(X.multiply(beta));
        return epsilon;
    }

	public static void main( String argv[]) {
		GISPoint3D a = new GISPoint3D( 0.0, 10.0, 100.0);
		GISPoint3D b = new GISPoint3D( 0.0, 20.0, 100.0);
		GISPoint3D c = new GISPoint3D( 10.0, 20.0, 110.0);
		GISPoint3D[] pts = new GISPoint3D[ 3];
		pts[ 0] = a;
		pts[ 1] = b;
		pts[ 2] = c;
		Matrix aMatrix = new Matrix();
		double[][] aData = new double[ 3][ 3];
		for( int i = 0; i < 3; i++) {
			aData[ i][ 0] = 1.0;
			aData[ i][ 1] = pts[ i].getEasting();
			aData[ i][ 2] = pts[ i].getNorthing();
		}
		aMatrix.setMatrix( aData);
		LeastSquaresSolver f = new LeastSquaresSolver();
		f.setX( aMatrix);
		Matrix zVector = new Matrix();
		double zData[][] = new double[ 3][ 1];
		for( int i = 0; i < 3; i++) {
			zData[ i][ 0] = pts[ i].getValue();
		}
		zVector.setMatrix( zData);
		f.sety( zVector);
		double[] check = new double[ 2];
		check[ 0] = 5.0;
		check[ 1] = 5.0;
		System.out.println( "x: 5.0, y: 5.0, z: " + f.getEstimatedValue( check));
		double[] betas = f.getBetas();
		for( int bb = 0; bb < betas.length; bb++) {
			System.out.println( "beta[ " + bb + "]: " + betas[ bb]);
		}
		double[] errors = f.getErrors();
		for( int bb = 0; bb < errors.length; bb++) {
			System.out.println( "error[ " + bb + "]: " + errors[ bb]);
		}
	}
}