package mil.army.usace.ehlschlaeger.rgik.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;



/**
 * Simple two-dimensional array of booleans.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class BooleanGrid implements Serializable {
	private int	rs, cs;
	protected BitSet[] bits;

	/**
	 * Create a new, pre-initialized grid.
	 * 
	 * @param nRows
	 * @param nCols
	 * @param startValue
	 */
	public BooleanGrid( int nRows, int nCols, boolean startValue) {
		super();
		rs = nRows;
		cs = nCols;
		
		bits = new BitSet[nRows];
		for (int i = 0; i < bits.length; i++)
            bits[i] = new BitSet(nCols);
		
		if(startValue)
		    setAll( startValue);
	}

	/**
	 * Create a copy of another grid.
	 * @param old
	 */
	public BooleanGrid( BooleanGrid old) {
	    this(old.getRows(), old.getColumns(), false);
		copy( old);
	}

	/**
	 * Set all bits to the given value.
	 * @param value
	 */
	public void setAll( boolean value) {
		for( int r = 0; r < rs; r++) {
		    bits[r].set(0, cs, value);
		}
	}

	/**
	 * Copy all the bits from given grid into this grid.
	 * @param old
	 */
	public void copy( BooleanGrid old) {
		for( int r = 0; r < rs; r++) {
		    bits[r] = (BitSet) old.bits[r].clone();
		}
	}

	/**
	 * Flip the state of every bit in the grid.
	 */
	public void reverse() {
		for( int r = 0; r < rs; r++) {
		    bits[r].flip(0, cs);
		}
	}

	/**
	 * Set one bit in the grid.
	 * 
	 * @param row
	 * @param col
	 * @param value
	 */
	public void setBoolean( int row, int col, boolean value) {
	    bits[row].set(col, value);
	}

	/**
	 * Fetch one bit from the grid.
	 * 
	 * @param row
	 * @param col
	 * @return value at given row/col
	 */
	public boolean getBoolean( int row, int col) {
	    return bits[row].get(col);
	}

	/**
	 * @return number of rows in grid
	 */
	public int getRows() {
		return( rs);
	}

	/**
	 * @return number of columns in grid
	 */
	public int getColumns() {
		return( cs);
	}

	/**
	 * @return number of bits in entire array set to 'true'
	 */
	public long cardinality() {
	    long sum = 0;
        for( int r = 0; r < rs; r++) {
            sum += bits[r].cardinality();
        }
        return sum;
	}
	
	@Override
	public boolean equals(Object obj) {
	    BooleanGrid g1 = this;
	    BooleanGrid g2 = (BooleanGrid) obj;
	    return(g1.rs==g2.rs && g1.cs==g2.cs && Arrays.deepEquals(g1.bits, g2.bits));
	}
}
