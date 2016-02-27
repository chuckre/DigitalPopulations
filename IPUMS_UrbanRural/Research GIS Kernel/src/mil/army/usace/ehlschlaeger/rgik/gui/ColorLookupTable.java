package mil.army.usace.ehlschlaeger.rgik.gui;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.IOException;
import java.io.Serializable;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;



/**
 * Map values to a set of colors. No interpolation is performed; this will only
 * return one of the tabled colors for any given value.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class ColorLookupTable extends RGIS implements Serializable {
    private double[]   values;
    private Color[]    colors;
    private ColorSpace csInstance;

	public double getMinValue() {
		return values[ 0];
	}

	public double getMaxValue() {
		return values[ values.length - 1];
	}

	public double[] getValues() {
		return values;
	}

	public void displayColorLookupTable( String title, int decimalPlaces) {
		new ColorLookupTableWindow( this, decimalPlaces);
	}

	public ColorLookupTable( double[] values, Color[] colors) {
		if( values == null || colors == null) {
		    throw new DataException( "At least one array was null");
		}
		if( values.length != colors.length) {
		    throw new DataException("The values array and colors array must be the same length."
		                            + "  values.length == " + values.length + ", colors.length == "
		                            + colors.length);
		}
		double previousValue = Double.NEGATIVE_INFINITY;
		for( int i = 0; i < values.length; i++) {
			//System.out.println( values[i]);
			if( values[ i] < previousValue) {
			    throw new DataException("Each value must be greater than the previous value.");
			}
			previousValue = values[i];
		}
		this.colors = new Color[ colors.length];
		this.values = new double[ values.length];
		for( int i = 0; i < values.length; i++) {
			this.colors[ i] = new Color( colors[i].getRed(), colors[i].getGreen(), 
				colors[i].getBlue(), colors[i].getAlpha());
			this.values[i] = values[ i];
		}
		// System.out.println( "***************************************************************************");
		displayColorLookupTable("test", 2);
	}

	public ColorLookupTable( String cltTable) throws IOException {
		super();
		CSVTable table = new CSVTable( cltTable);
		int rows = table.getRowCount();
		int valueColumn = table.findColumn( "value");
		int redColumn = table.findColumn( "red");
		int greenColumn = table.findColumn( "green");
		int blueColumn = table.findColumn( "blue");
		
		int alphaColumn;
        try {
            alphaColumn = table.findColumn( "alpha");
        } catch (DataException e) {
            alphaColumn = -1;
        }
        
		if( rows == 0) {
		    throw new DataException( 
			"ColorLookupTable.ColorLookupTable ERROR: There must be at least one row in color lookup table");
		}
		values = new double[ rows];
		colors = new Color[ rows];
		double previousValue = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < rows; r++) {
			String redS = table.getStringAt( r, redColumn);
			String greenS = table.getStringAt( r, greenColumn);
			String blueS = table.getStringAt( r, blueColumn);
			String valueS = table.getStringAt( r, valueColumn);
			double valueV = Double.parseDouble( valueS);
			if( valueV < previousValue) {
				System.out.println( "ColorLookupTable.ColorLookupTable ERROR: Each value must be greater than the previous value");
			}
			previousValue = valueV;
			values[ r] = valueV;
			float redV = Float.parseFloat( redS);
			if( redV < 0.0f)
				redV = 0.0f;
			else if( redV > 1.0f) 
				redV = 1.0f;
			float greenV = Float.parseFloat( greenS);
			if( greenV < 0.0f)
				greenV = 0.0f;
			else if( greenV > 1.0f) 
				greenV = 1.0f;
			float blueV = Float.parseFloat( blueS);
			if( blueV < 0.0f)
				blueV = 0.0f;
			else if( blueV > 1.0f) 
				blueV = 1.0f;
			if( alphaColumn >= 0) {
				String alphaS = table.getStringAt( r, alphaColumn);
				float alphaV = Float.parseFloat( alphaS);
				if( alphaV < 0.0f)
					alphaV = 0.0f;
				else if( alphaV > 1.0f) 
					alphaV = 1.0f;
				colors[ r] = new Color( redV, greenV, blueV, alphaV);
			} else {
				colors[ r] = new Color( redV, greenV, blueV, 1.0f);
			}
		}
	}

	public Color getColor( double value) {
		int index = 0;
		while( index < values.length && value > values[ index]) {
			index++;
		}
		if( index == 0)
			return( colors[ 0]);
		if( index > values.length - 1)
			return( colors[ values.length - 1]);
		double dif = values[ index] - values[ index - 1];
		if( dif == 0.0) {
			return( colors[ index - 1]);
		}
		float proportionLow = (float) ((values[ index] - value) / dif);
		if( proportionLow > .5)
			return( colors[ index - 1]);
		return( colors[ index]);
		//return( interpolateColor( colors[ index - 1], colors[ index], proportionLow));
	}

	public Color interpolateColor( Color a, Color b, float proportionA) {
		float[] aArray = a.getColorComponents( csInstance, null);
		float aa = a.getAlpha() / 255.0f;
		float[] bArray = b.getColorComponents( csInstance, null);
		float bb = b.getAlpha() / 255.0f;
		float[] cArray = new float[ aArray.length];
		for( int i = cArray.length - 1; i >= 0; i--) {
			cArray[ i] = aArray[ i] * proportionA + bArray[i] * (1.0f - proportionA);
		}
		float cc = aa * proportionA + bb * (1.0f - proportionA);
		return( new Color( csInstance, cArray, cc));
	}
}
