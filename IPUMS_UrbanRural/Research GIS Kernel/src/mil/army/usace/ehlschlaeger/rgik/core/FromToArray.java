package mil.army.usace.ehlschlaeger.rgik.core;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class FromToArray extends RGIS  {
	private FromTo head;

	public FromToArray( ) {
		super();
		head = new FromTo( -1, -1, -1, -1, (double) -1.0);
	}

	public void addFT( int fromRow, int fromColumn, int toRow, int toColumn, double distance) {
		FromTo ptr = head;
		while( ptr.getNext() != null && ptr.getNext().getDistance() < distance) {
			ptr = ptr.getNext();
		}
		FromTo newFT = new FromTo( fromRow, fromColumn, toRow, toColumn, distance, ptr.getNext());
		ptr.setNext( newFT);
	}

	public FromTo getNextFT() {
		if( head.getNext() == null)
			return( null);
		FromTo it = head.getNext();
		/* old
		head.putNext( head.getNext());
		head.putNext( head.getNext());
		*/
		head.setNext( it.getNext()); // new
		return( it);
	}
}
 
