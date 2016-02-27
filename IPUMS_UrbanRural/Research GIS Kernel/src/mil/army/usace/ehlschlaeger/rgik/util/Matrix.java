package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;

/**
 * Two-dimensional algebraic matrix.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class Matrix {
	protected double[][] data;

	public Matrix() {
		data = null;
	}

	public Matrix(int rows, int cols) {
	    data = new double[rows][cols];
	}
	
	/**
	 * @return a copy of our contents as a new two-dimension array
	 */
	public double[][] getData() {
		double[][] d = new double[ data.length][];
		for( int r = 0; r < d.length; r++) {
			d[ r] = new double[ data[ r].length ];
			for( int c = 0; c < d[ r].length; c++) {
				d[ r][ c] = data[ r][ c];
			}
		}
		return d;
	}

	public Matrix transpose() {
		int cols = getNumberRows();
		int rows = getNumberColumns();
		double tData[][] = new double[ rows][ cols];
		Matrix transposed = new Matrix();
		for( int r = 0; r < rows; r++) {
			for( int c = 0; c < cols; c++) {
				tData[ r][ c] = getCell( c, r);
			}
		}
		transposed.setMatrix( tData);
		return transposed;
	}

    /**
     * inverse() inverts the matrix using gaussian reduction. If the matrix is
     * not square, the program will terminate. If the matrix is nonsingular, a
     * null pointer will be returned. Otherwise, the inverse of the matrix is
     * returned.
     */
	public Matrix inverse() {
		/* The matrix order is n. NOTE: row 0 and column 0 of the 
		 * array d[][] are NOT used. Matrix d[][] is sized as d[n+1][2*n+2], with 
		 * the left half (excluding row 0 and column 0) initialized to the matrix 
		 * to be inverted. Upon completion the left half is reduced to an identity 
		 * matrix and the right half is reduced to the inverse of the original 
		 * matrix. The inversion routine initializes the right half of d[][] to an 
		 * identity matrix.
		 */
		int n = getNumberRows();
		if( n != getNumberColumns()) {
		    throw new DataException("Matrix must be square.");
		}
		int n2 = 2 * n;
		double[][] d = new double[ n + 1][ n2 + 2];
		/* place matrix values in left half of d[][] */
		for( int r = 0; r < n; r++) {
			for( int c = 0; c < n; c++) {
				d[ r + 1][ c + 1] = getCell( r, c);
			}
		}
		/* init the identity matrix on right half of d[][] */
		for( int i = 1; i <= n; i++ ) {
			d[ i][ i + n] = 1.0;
		}
		//printD( d, "initialized");
		/* perform the reductions  */
		for( int r = 1; r <= n; r++ ) {
			double alpha = d[ r][ r];
			/* error - singular matrix */
			if( alpha == 0.0 ) {
			    throw new DataException("singular matrix");
			}
			for( int c = 1; c <= n2; c++ ) {
				d[ r][ c] = d[ r][ c] / alpha;
			}
			//printD( d, "post alpha loop");
			for( int r2 = 1; r2 <= n; r2++ ) {
				if( r2 - r != 0 ) {
					double beta = d[ r2][ r];
					for( int j = 1; j <= n2; j++ ) {
						d[ r2][ j] = d[ r2][ j] - beta * d[ r][ j];
					}
				}
				//printD( d, "post beta loop, k = " + k);
			}
		}
		Matrix D = new Matrix();
		D.data = new double[ n][ n];
		/* place matrix values from right half of d[][] */
		for( int r = 0; r < n; r++) {
			for( int c = 0; c < n; c++) {
				D.data[ r][ c] = d[ r + 1][ c + n + 1];
			}
		}		
		return D;
	}

	public int getNumberRows() {
		return data.length;
	}

	public int getNumberColumns() {
		return getNumberColumns( 0);
	}

	public int getNumberColumns( int row) {
		if( data[ row] == null)
			return 0;
		return data[ row].length;
	}

	public double getCell( int row, int col) {
		return data[ row][ col];
	}

	public void setCell(int row, int col, double value) {
	    data[row][col] = value;
	}
	
	public void setMatrix( double[][] dataValues) {
	    assert dataValues != null;
	    
	    int rows = dataValues.length;
        int cols = dataValues[0].length;
        for(int row=1; row < rows; row++) {
            if(dataValues[row] == null)
                throw new NullPointerException("dataValues["+row+"]");
            if( dataValues[row].length != cols) {
                throw new IllegalArgumentException("dataValues["+row+"] is not the same length as [0]");
            }
        }
		data = dataValues;
	}

	/**
	 * Delete a full row from matrix, shrinking it by one row.
	 * @param row index of row to remove
	 */
	public void removeRow(int row) {
	    data = (double[][]) ArrayUtils.remove(data, row);
	}
	
	/**
	 * Delete one full column from matrix, shrinking it by one column.
	 * @param col index of column to remove
	 */
	public void removeColumn(int col) {
        for( int r = 0; r < data.length; r++) {
            data[r] = ArrayUtils.remove(data[r], col);
        }
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		for( int i = 0; i < data.length; i++) {
			for( int j = 0; j < data[ i].length; j++) {
			    out.append(data[ i][ j]).append(" ");
			}
            out.append("\n");
		}
		return out.toString();
	}
	
    public String toString(String format) {
        StringBuffer out = new StringBuffer();
        for( int i = 0; i < data.length; i++) {
            for( int j = 0; j < data[ i].length; j++) {
                out.append(String.format(format, data[ i][ j])).append(" ");
            }
            out.append("\n");
        }
        return out.toString();
    }

	public void identity( int size) {
		data = new double[ size][ size];
		for( int i = 0; i < size; i++) {
			for( int j = 0; j < size; j++) {
				if( i == j) {
					data[ i][ j] = 1.0;
				} else {
					data[ i][ j] = 0.0;
				}
			}
		}
	}

    public void set2DTranslate( double dx, double dy) {
        identity( 3);
        data[ 2][ 0] = dx;
        data[ 2][ 1] = dy;
    }

	public void set2DRotate( double angle) {
		identity( 3);
		data[ 0][ 0] = Math.cos( angle);
		data[ 0][ 1] = Math.sin( angle);
		data[ 1][ 0] = -1.0 * Math.sin( angle);
		data[ 1][ 1] = Math.cos( angle);
	}

	public void set2DScale( double sx, double sy) {
		identity( 3);
		data[ 0][ 0] = sx;
		data[ 1][ 1] = sy;
	}

	public void set2DVector( double x, double y) {
		data = new double[ 1][ 3];
		data[ 0][ 0] = x;
		data[ 0][ 1] = y;
		data[ 0][ 2] = 1;
	}

	public double get2DVectorX() {
		return data[ 0][ 0];
	}

	public double get2DVectorY() {
		return data[ 0][ 1];
	}

    /**
     * Add another matrix to this one, element by element.
     * @param b second matrix
     */
	public void add(Matrix b) {
	    assert(getNumberRows() == b.getNumberRows());
	    assert(getNumberColumns() == b.getNumberColumns());
	    
        for( int r = getNumberRows() - 1; r >= 0; r--)
            for( int c = getNumberColumns() - 1; c >= 0; c--)
                data[r][c] += b.data[r][c];
	}

	/**
	 * Subtract another matrix from this one, element by element.
	 * @param b second matrix
	 */
	public void subtract(Matrix b) {
        assert(getNumberRows() == b.getNumberRows());
        assert(getNumberColumns() == b.getNumberColumns());
        
        for( int r = getNumberRows() - 1; r >= 0; r--)
            for( int c = getNumberColumns() - 1; c >= 0; c--)
                data[r][c] -= b.data[r][c];
	}
	
	/**
	 * Compute a matrix multiplication, replacing this objects value with the result.
	 * @param a first matrix
	 * @param b second matrix
	 */
    public void multiply( Matrix a, Matrix b) {
        assert a != null;
        assert b != null;
        assert a.getNumberColumns() == b.getNumberRows();

        if( this == a || this == b) {
            double[][] tmp = new double[ a.getNumberRows()][ b.getNumberColumns()];
            for( int i = getNumberRows() - 1; i >= 0; i--) {
                for( int j = getNumberColumns() - 1; j >= 0; j--) {
                    tmp[ i][ j] = 0.0;
                    for( int ii = b.getNumberRows() - 1; ii >= 0; ii--) {
                        tmp[ i][ j] += a.getCell( i, ii) * b.getCell( ii, j);
                    }
                }
            }
            data = tmp;
        } else {
            data = new double[ a.getNumberRows()][ b.getNumberColumns()];
            for( int i = getNumberRows() - 1; i >= 0; i--) {
                for( int j = getNumberColumns() - 1; j >= 0; j--) {
                    data[ i][ j] = 0.0;
                    for( int ii = b.getNumberRows() - 1; ii >= 0; ii--) {
                        data[ i][ j] += a.getCell( i, ii) * b.getCell( ii, j);
                    }
                }
            }
        }
    }
    
    /**
     * Compute a matrix multiplication, returning a new matrix with the result.
     * @param b second matrix
     * @return new matrix with result
     */
    public Matrix multiply(Matrix b) {
        Matrix result = new Matrix();
        result.multiply(this, b);
        return result;
    }

    /**
     * Construct a deep copy of this object.  Changes to the new object will have no
     * effect on this one.
     */
    public Matrix clone() {
        double[][] newd = new double[ data.length][];
        for(int i = 0; i < newd.length; i++) {
            newd[i] = Arrays.copyOf(data[i], data[i].length);
        }
        
        Matrix matrix = new Matrix();
        matrix.setMatrix(newd);
        return matrix;
    }
    
	public static void main( String argv[]) {
		Matrix m = new Matrix(); // done
		double[][] data = { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } };
		System.out.println( "adding data to matrix:");
		m.setMatrix( data); // done
		System.out.println( m.toString()); // done
		double dx = 2.0;
		double dy = 3.0;
		System.out.println( "translating matrix:");
		Matrix t = new Matrix();
		t.set2DTranslate( dx, dy); // done
		System.out.println( t.toString());
		double angle = .70;
		System.out.println( "rotation matrix:");
		Matrix r = new Matrix();
		r.set2DRotate( angle); // done
		System.out.println( r.toString());
		double sx = 2.0;
		double sy = 3.0;
		System.out.println( "scaling matrix:");
		Matrix s = new Matrix();
		s.set2DScale( sx, sy); // done
		System.out.println( s.toString());
		System.out.println( "multiplying matrix:");
		Matrix out = new Matrix();
		out.multiply( t, r); // done
		System.out.println( out.toString());
		Matrix v = new Matrix();
		v.set2DVector( 4.0, 8.0);
		Matrix vPrime = new Matrix();
		vPrime.multiply( v, out);
		System.out.println( "vector fun");
		System.out.println( v.toString());
		System.out.println( "v' x: " + vPrime.get2DVectorX() + ", v' y: " + vPrime.get2DVectorY());

		m = new Matrix(); // done
		double[][] data2 = { { 1.0, -2.0, 4.0 }, { 0.0, -1.0, 2.0 }, { 2.0, 0.0, 3.0 } };
		System.out.println( "");
		m.setMatrix( data2); 
		System.out.println( "matrix to invert:");
		System.out.println( m.toString());
		Matrix inverse = m.inverse();
		if( inverse == null) {
			System.out.println( "inverse == null");
		} else {
			System.out.println( "inverted matrix:");
			System.out.println( inverse.toString());
		}
		double[][] data3 = { { 3.0, 2.0, 1.0 }, { 4.0, 0.0, 1.0 }, { 3.0, 9.0, 2.0 } };
		System.out.println( "");
		m.setMatrix( data3); 
		System.out.println( "matrix to invert:");
		System.out.println( m.toString());
		inverse = m.inverse();
		if( inverse == null) {
			System.out.println( "inverse == null");
		} else {
			System.out.println( "inverted matrix:");
			System.out.println( inverse.toString());
		}
		double[][] data4 = { { 1.0, 0.0, 0.0, 0.0 }, 
						{ 2.0, -1.0, 0.0, 0.0 }, 
						{ 4.0, 6.0, 2.0, 0.0 },
						{ 3.0, 2.0, 4.0, -1.0 } };
		System.out.println( "");
		m.setMatrix( data4); 
		System.out.println( "matrix to invert:");
		System.out.println( m.toString());
		inverse = m.inverse();
		if( inverse == null) {
			System.out.println( "inverse == null");
		} else {
			System.out.println( "inverted matrix:");
			System.out.println( inverse.toString());
		}
		System.out.println( "done");
	}
}

/* UNFINISHED cofactor code
	public double determinant() {
		if( data.length != data[0].length) {
			System.out.println( "Matrix.determinant() ERROR: Matrix not square");
			System.exit( -1);
		}
		double d = 0.0;
		int negOne = 1;
		for( int i = 0; i < data.length; i++) {
			d += cofactor( 0, i) * negOne * data[ 0][ i];
			negOne *= -1;
		}
		return d;
	}

	private double cofactor( int row, int col) {
		int n = data.length;
		if( n == 2) {
			return( data[ 0][ 0] * data[ 1][ 1] - data[ 1][ 0] * data[ 0][ 1]);
		}
		double[] cfMatrixValue = new double[ n];
		int numZeros = -1;
		int bestRow = -1;
		for( int r = 0; r < n; r++) {
			int zerosInRow = 0;
			for( int c = 0; c < n; c++) {
				if( data[ r][ c] == 0.0) {
					zerosInRow++;
				}
			}
			if( numZeros < zerosInRow) {
				numZeros = zerosInRow;
				bestRow = r;
			}
		}
		double sumCofactor = 0.0;
		for( int c = 0; c < n; c++) {
			//cfValue[ i] = data[ bestRow][ i];
			Matrix cfM = new Matrix();
			cfM.setRows( n - 1);
			cfM.setColumns( n - 1);
			int newRow = 0;
			for( int r = 0; r < n; r++) {
				if( r != bestRow) {
					int newCol = 0;
					for( int cc = 0; cc < n; cc++) {
						if( cc != c) {
							cfM.setCell( newRow, newCol, getCell( r, cc));
							newCol++;
						}
					}
					newRow++;
				}
			}
			sumCofactor += data[ bestRow][ c] * cofactor( 
		}
	}
*/