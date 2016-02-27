package mil.army.usace.ehlschlaeger.rgik.core;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * GISPolygonOutline class defines a point in the GIS.
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GISPolygonOutline extends GISPointSymbol  {
	private Line2D.Double[] drawLines; // Remember, drawing lines MUST have northings multipied by -1.0.

	public GISPolygonOutline( double[] eastings, double[] northings, Color colorOutline) {
		super( 1.0, null, colorOutline);
		assert eastings != null;
		assert northings != null;
		assert eastings.length == northings.length;
		if( eastings.length < 3) {
		    throw new IllegalArgumentException("must have 3+ points");
		}
		
		drawLines = new Line2D.Double[ eastings.length];
		//System.out.println( "GISPolygonOutline.GISPolygonOutline has [" + eastings.length + "] lines.");
		for( int i = 0; i < eastings.length - 1; i++) {
			drawLines[ i] = new Line2D.Double( eastings[ i], - northings[ i], eastings[ i+1], - northings[ i+1]);
		}
		drawLines[ eastings.length - 1] = new Line2D.Double( eastings[ eastings.length - 1], 
			- northings[ eastings.length - 1], eastings[ 0], - northings[ 0]);
		//bs = new BasicStroke( 20.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	}

	public void drawPoint( Graphics g, GISPoint point) {
		//System.out.println( "GISPolygonOutline.GISPolygonOutline drawPoint called");
		Graphics2D g2D = (Graphics2D) g;
		Color c = getColorOutline();
		if( c != null) {
			//g2D.setStroke( bs);
			g2D.setPaint( c);
			for( int i = drawLines.length - 1; i >= 0; i--) {
				g2D.draw( drawLines[ i]);
				//g2D.fill( drawLines[ i]);
				//System.out.println( "[" + drawLines[i].getX1() + "," + drawLines[i].getY1() + "]");
			}
		} else {
			System.out.println( "GISPolygonOutline.drawPoint null outline color");
		}
		/*
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
		*/
	}
}