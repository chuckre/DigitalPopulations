package mil.army.usace.ehlschlaeger.rgik.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.Serializable;



/**
 * Manages the visual appearance of a GISPoint.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISPointSymbol extends RGIS implements Serializable {
    private double area;
    private Color  colourFill, colourOutline;

	public GISPointSymbol( double area, Color colorFill, Color colorOutline) {
		super();
		this.area = area;
		colourFill = colorFill;
		colourOutline = colorOutline;
	}

	public void drawPoint( Graphics g, GISPoint point) {
		if( area > 0.0) {
			double size = Math.sqrt( area);
			Graphics2D g2D = (Graphics2D) g;
			int[] x = { (int) (.5 + point.getEasting() - (size / 2.0)), 
				(int) (.5 + point.getEasting()), 
				(int) (.5 + point.getEasting() + (size / 2.0)) };
			int[] y = { (int) (.5 -(point.getNorthing() - (size / 2.0))),
				(int) (.5 -(point.getNorthing() + (size / 2.0))),
				(int) (.5 -(point.getNorthing() - (size / 2.0))) };
			Polygon r = new Polygon( x, y, 3);
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

	public void setColor( Color colorFill, Color colorOutline)  {
		colourFill = colorFill;
		colourOutline = colorOutline;
	}

	public Color getColorFill( GISPoint point) {
//		GISPointQuadTree tree = point.getGISPointQuadTree();
//		if( tree != null && tree.isColorLookupTable() == true) {
//			return( tree.getColor( point.getValue()));
//		}
		return colourFill;
	}

	public Color getColorOutline() {
		return colourOutline;
	}

	public void setArea( double area) {
		this.area = area;
	}

	public double getArea() {
		return area;
	}
}