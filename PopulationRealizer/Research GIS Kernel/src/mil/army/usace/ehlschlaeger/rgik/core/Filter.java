package mil.army.usace.ehlschlaeger.rgik.core;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class Filter extends RGIS  {
	private double maxDistance;
	private GISGrid filterMap;

	/** in alpha testing */
	public Filter( GISGrid map, double maximumDistance) {
		super();
		filterMap = map;
		maxDistance = maximumDistance;
	}

	public int getMaxCells() {
		int centerR = filterMap.getNumberRows() / 2;
		int centerC = filterMap.getNumberColumns() / 2;
		int count = 0;
		for( int r = getMinRow( centerR); r <= getMaxRow( centerR); r++) {
			for( int c = getMinCol( centerC); c <= getMaxCol( centerC); c++) {
				double dist = filterMap.distance( centerR, centerC, r, c);
				if( dist <= maxDistance) {
					count++;
				}
			}
		}
		return count;
		// old return( (getMaxRows() * 2 + 1) * (getMaxCols() * 2 + 1));
	}

	public int getMaxRows() {
		return( (int) (maxDistance / filterMap.getNSResolution()));
	}

	public int getMinRow( int row) {
		return((int) (Math.max( row - getMaxRows(), 0)));
	}

	public int getMaxRow( int row) {
		return((int) (Math.min( row + getMaxRows(), filterMap.getNumberRows() - 1)));
	}

	public int getMaxCols() {
		return((int) ((int) (maxDistance / filterMap.getEWResolution())));
	}

	public int getMinCol( int col) {
		return((int) (Math.max( col - getMaxCols(), 0)));
	}

	public int getMaxCol( int col) {
		return((int) (Math.min( col + getMaxCols(), filterMap.getNumberColumns() - 1)));
	}
}
