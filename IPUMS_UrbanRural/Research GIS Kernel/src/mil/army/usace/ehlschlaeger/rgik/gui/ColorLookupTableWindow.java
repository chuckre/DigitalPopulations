package mil.army.usace.ehlschlaeger.rgik.gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class ColorLookupTableWindow extends GenericApplicationFrame {
	private ColorLookupTable clt;
	Font fnt;
	int dP;

	public ColorLookupTableWindow( ColorLookupTable colorLookupTable, int decimalPlaces) {
		super( "Color Lookup Table", 250, 300);
		clt = colorLookupTable;
		fnt = new Font( "LucidaSansRegular", Font.ITALIC, 24);
		dP = decimalPlaces;
		setVisible( true);
	}
   

	public void paint( Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int minCol = getMinDrawPixelWidth();
		int maxCol = getMaxDrawPixelWidth();
		int minRow = getMinDrawPixelHeight();
		int maxRow = getMaxDrawPixelHeight();
		Rectangle r = new Rectangle( minCol, minRow, maxCol, maxRow);
		g2.setPaint( Color.white);
		g2.fill( r);
		minCol += 2;
		maxCol -= 2;
		minRow += 20;
		maxRow -= 10;
		double minValue = clt.getMinValue();
		double dValue = clt.getMaxValue() - minValue;
		double rows = maxRow - minRow;
		for( int i = minRow; i <= maxRow; i++) {
			double drawValue = minValue + dValue * (i - minRow) / rows;
			g2.setPaint( clt.getColor( drawValue));
			g2.draw( new Line2D.Float( minCol, i, minCol + 50, i));
		}
		double[] values = clt.getValues();
		g2.setFont( fnt);
		g2.setPaint( Color.black);
		for( int j = values.length - 1; j >= 0; j--) {
			double valueRow = (values[ j] - minValue) / dValue;
			valueRow = minRow + valueRow * ( maxRow - minRow);
			g2.draw( new Line2D.Double( minCol + 50, valueRow, minCol + 60, valueRow));
			if( dP > 0) {
				double mult = Math.pow( 10, dP);
				double v = values[ j] * mult + .5;
				v = ((int) v) / mult;
				String s = "" + v;
				g2.drawString( s, (float) (minCol + 65), (float) (valueRow + 8));
			}
		}
	}
}