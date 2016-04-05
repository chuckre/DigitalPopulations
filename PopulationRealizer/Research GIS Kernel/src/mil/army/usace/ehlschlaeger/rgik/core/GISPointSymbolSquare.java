package mil.army.usace.ehlschlaeger.rgik.core;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * GISPointSymbolSquare class defines a point in the GIS.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GISPointSymbolSquare extends GISPointSymbol  {
	public GISPointSymbolSquare( double areaSquare, Color colorFill, Color colorOutline) {
		super( areaSquare, colorFill, colorOutline);
	}

	public void drawPoint( Graphics g, GISPoint point) {
		double area = getArea();
		if( area > 0.0) {
			double size = Math.sqrt( area);
			Graphics2D g2D = (Graphics2D) g;
			Rectangle2D r = new Rectangle2D.Double( point.getEasting() - (size / 2.0), 
				-(point.getNorthing() + (size / 2.0)), size, size);
			Color c = getColorFill( point);
			if( c != null) {
				g2D.setPaint( c);
				g2D.fill( r);
			}
			c = getColorOutline();
			if( c != null) {
				g2D.setPaint( c);
				g2D.draw( r);
			}
			String label = point.getLabel();
			if( label != null) {	
				FontRenderContext fRC = g2D.getFontRenderContext();
				Font fnt = new Font( "LucidaSansRegular", Font.ITALIC, (int) (size + 0.5));
				GlyphVector gV = fnt.createGlyphVector( fRC, label);
				g2D.drawGlyphVector( gV, (float) (point.getEasting() + size), 
					(float) (0.5 * size - point.getNorthing()));
			}
		}
	}
}