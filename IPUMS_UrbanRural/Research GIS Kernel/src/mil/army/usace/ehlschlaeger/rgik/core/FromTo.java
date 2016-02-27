package mil.army.usace.ehlschlaeger.rgik.core;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class FromTo extends RGIS  {
	private int fRow, tRow, fCol, tCol;
	private double dist;
	private FromTo next;

	public FromTo( int fromRow, int fromColumn, int toRow, int toColumn, double distance) {
		super();
		fRow = fromRow;
		tRow = toRow;
		fCol = fromColumn;
		tCol = toColumn;
		dist = distance;
		next = null;
	}

	public FromTo( int fromRow, int fromColumn, int toRow, int toColumn, double distance,
			FromTo nextFromTo) {
		super();
		fRow = fromRow;
		tRow = toRow;
		fCol = fromColumn;
		tCol = toColumn;
		dist = distance;
		next = nextFromTo;
	}

	public int getFromRow() {
		return( fRow);
	}
	
	public int getFromColumn() {
		return( fCol);
	}
	
	public int getToRow() {
		return( tRow);
	}
	
	public int getToColumn() {
		return( tCol);
	}
	
	public double getDistance() {
		return( dist);
	}

	public FromTo getNext() {
		return( next);
	}

	public void setFromRow( int fromRow) {
		fRow = fromRow;
	}

	public void setFromColumn( int fromColumn) {
		fCol = fromColumn;
	}

	public void setToRow( int toRow) {
		tRow = toRow;
	}

	public void setToColumn( int toColumn) {
		tCol = toColumn;
	}

	public void setDistance( double distance) {
		dist = distance;
	}

	public void setNext( FromTo nextFromTo) {
		next = nextFromTo;
	}

	public void print() {
		System.out.println( "fR: " + fRow + ", fC: " + fCol + ", tR: " + tRow + 
			", tC: " + tCol + ", dist: " + dist);
	}
}
