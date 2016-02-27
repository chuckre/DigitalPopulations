package mil.army.usace.ehlschlaeger.rgik.util;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class RowRLE {
	private char[] values;
	private short[] maxColumnIndex;
	private boolean isRLE, autoOptimizeAfterSetCellValue;
	private int numColumns;
	private int lastLocation;

	public RowRLE( int numberColumns) {
		isRLE = autoOptimizeAfterSetCellValue = false;
		values = new char[ numberColumns];
		for( int c = numberColumns - 1; c >= 0; c--) {
			values[ c] = (char) 0;
		}
		maxColumnIndex = null;
		numColumns = numberColumns;
		lastLocation = 0;
	}

	public boolean isRunLengthEncoded() {
		return isRLE;
	}

	public int getNumberColumns() {
		return numColumns;
	}

	public int getLengthRow() {
		return values.length;
	}

	public void setCellValue( int columnIndex, int cellValue) {
		if(!isRLE) {
			values[ columnIndex] = (char) cellValue;
		} else {
			flattenRow();
			values[ columnIndex] = (char) cellValue;
			if( autoOptimizeAfterSetCellValue == true) {
				optimizeRow();
			}
		}
	}

	public int getCellValue( int columnIndex) {
		if( isRLE == false) {
			return values[ columnIndex];
		}
		if( columnIndex == 0) {
			lastLocation = 0;
			return values[ 0];
		}
		if( columnIndex == numColumns - 1) {
			lastLocation = maxColumnIndex.length - 1;
			return values[ lastLocation];
		}
		if( maxColumnIndex[ lastLocation] >= columnIndex) {
			while( true) {
				if( lastLocation == 0 || maxColumnIndex[ lastLocation - 1] < columnIndex) {
					return values[ lastLocation];
				}
				lastLocation--;
			}			
		}
		while( lastLocation != maxColumnIndex.length - 1 && 
			maxColumnIndex[ lastLocation] < columnIndex) {
				lastLocation++;
		}
		return values[ lastLocation];
	}

	public void setAutomaticOptimizeAfterSetCellValue( boolean value) {
		autoOptimizeAfterSetCellValue = value;
	}

	public boolean optimizeRow() {
		if(isRLE) {
			return true;
		} else {
			int ci = 1;
			int currentValue = values[ 0];
			for( int i = 1; i < numColumns; i++) {
				if( values[ i] != currentValue) {
					ci++;
					currentValue = values[ i];
				}
			}
			if( ci * 3 < numColumns) {
				encodeRow( ci);
				return true;
			}
		}
		return false;
	}

	public void encodeRow() {
		if(isRLE)
		    throw new DataException("row is already run length encoded");

		int ci = 1;
		int currentValue = values[ 0];
		for( int i = 1; i < numColumns; i++) {
			if( values[ i] != currentValue) {
				ci++;
				currentValue = values[ i];
			}
		}
		encodeRow( ci);
	}

	private void encodeRow( int sizeIndex) {
        if(isRLE)
            throw new DataException("row is already run length encoded");

        //System.out.println( "was [" + values.length + "], will be [" + sizeIndex + "]");
		char[] newValues = new char[ sizeIndex];
		maxColumnIndex = new short[ sizeIndex];
		sizeIndex = 0;
		int i = 1;
		newValues[ 0] = values[ 0];
		while( i < numColumns) {
			if( values[ i] != newValues[ sizeIndex]) {
				maxColumnIndex[ sizeIndex++] = (short) (i - 1);
				newValues[ sizeIndex] = (char) values[ i];
			}
			i++;
		}
		maxColumnIndex[ sizeIndex] = (short) (numColumns - 1);
		if( sizeIndex != newValues.length - 1) {
		    throw new DataException("sizeIndex [" + 
				sizeIndex + "] doesn't equal length-1 [" + (newValues.length - 1) + "]");
		}
		values = newValues;
		isRLE = true;
		lastLocation = 0;
	}

	public void flattenRow() {
        if(!isRLE)
            throw new DataException("row is already flat");
		char[] newValues = new char[ numColumns];
		int i = 0;
		for( int ci = 0; ci < maxColumnIndex.length; ci++) {
			int mi = maxColumnIndex[ ci];
			while( i <= mi) {
				newValues[ i] = values[ ci];
				i++;
			}
			if( i >= numColumns)
				ci = maxColumnIndex.length;
		}
		values = newValues;
		isRLE = false;
		maxColumnIndex = null;
	}
}