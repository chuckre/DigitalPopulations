package mil.army.usace.ehlschlaeger.rgik.gui;
// RGISAnimatedView.java
/** 
 *  in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 212-772-5321, fax: 212-772-5268,
 *  <http://www.geo.hunter.cuny.edu/~chuck/>
 *  Version 0.4, last modified 08/28/2002.
 *  This software is freely usable for research and educational purposes.
 *  Contact C. R. Ehlschlaeger for permission for other purposes.
 *  Use of this software requires appropriate citation in all published
 *  and unpublished documentation.
 */

/** in alpha testing */
/**
 * RGISAnimatedView class creates a Frame containing GISData objects. RGISAnimatedView
 * is designed to allow other RGIS components to graphically display and manipulate
 * GIS data.
 */
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointSymbol;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointSymbolSquare;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomFieldTemplate;

public class RGISAnimatedView extends AnimationComponent
		implements MouseListener {
	private GISData 		data[];
	private int 		extentMap;
	private long 		frameTime, startTime;
	private boolean 		instanciated;
	private RGISAVControlFrame 	controlFrame;
	private double		northExtent, southExtent, eastExtent, westExtent;
	private double[]		windowUnits;
	private double		zoomInFactor, zoomOutFactor;
	private Vector<GISPoint>		locations;
	private GISPointSymbol	locationSymbol;
	private int			verticalButtonBarSize, horizontalButtonBarSize;
	private LongHolder	secondsBetweenKeyFrames, keyFrameOne, ratioKeyFrames, numberRealizations;
	private JSlider		keyFrameSlider;
	private JProgressBar	interpolationSlider;
	private int			interpolationMultiplier = 100;
	private double		ratioExponent, ratioBetweenKeyFrames;
	private long		timeCurrentFrame;
	public final static int	NORTH = 0;
	public final static int	SOUTH = 1;
	public final static int	EAST = 3;
	public final static int	WEST = 2;

	public double[] getWindowUnits() {
		return windowUnits;
	}

	public double getRatioBetweenKeyFrames() {
		return ratioBetweenKeyFrames;
	}

	public void setRatioBetweenKeyFrames( double value) {
		ratioBetweenKeyFrames = value;
		setRatioKeyFrames( (int) (value * interpolationMultiplier));
	}

	public long getTimeCurrentFrame() {
		return timeCurrentFrame;
	}

	public void setTimeCurrentFrame( long value) {
		timeCurrentFrame = value;
	}
 
	public long getStartTime() {
		return startTime;
	}

	public long getFrameTime() {
		return frameTime;
	}

	public void setStartTime( long time) {
		startTime = time;
	}

	public void setFrameTime( long time) {
		frameTime = time;
	}

	public void setCurrentRealization( int value) {
		keyFrameOne.setValue( value);
		keyFrameSlider.setValue( value);
	}

	public int getCurrentRealization() {
		return( (int) keyFrameOne.getValue());
	}

	public RGISAnimatedView( int horizontalFrameSize, int verticalFrameSize, 
			int secondsBetweenRealizations, int numRealizations, double ratioExponent) {
		super( horizontalFrameSize, verticalFrameSize);
		initRGISAV( horizontalFrameSize, verticalFrameSize, 
			secondsBetweenRealizations, numRealizations, ratioExponent, false);
	}

	public RGISAnimatedView( int horizontalFrameSize, int verticalFrameSize, 
			int secondsBetweenRealizations, int numRealizations, double ratioExponent,
			boolean timeSeries) {
		super( horizontalFrameSize, verticalFrameSize);
		initRGISAV( horizontalFrameSize, verticalFrameSize, 
			secondsBetweenRealizations, numRealizations, ratioExponent, timeSeries);
	}

	private void initRGISAV( int horizontalFrameSize, int verticalFrameSize, 
			int secondsBetweenRealizations, int numRealizations, double ratioExponent,
			boolean timeSeries) {
		windowUnits = new double[ 4];
		for( int i = 0; i < 4; i++) {
			windowUnits[ i] = (1 + i) * 999999999.0;
		}
		startTime = (new Date()).getTime();
		setTimeCurrentFrame( startTime);
		frameTime = (new Date()).getTime();
		this.ratioExponent = ratioExponent;
		secondsBetweenKeyFrames = new LongHolder( secondsBetweenRealizations);
		keyFrameOne = new LongHolder( 0);
		ratioKeyFrames = new LongHolder( 0);
		numberRealizations = new LongHolder( numRealizations);
		verticalButtonBarSize = 240;
		horizontalButtonBarSize = 680;
		instanciated = false; 
		keyFrameSlider = new JSlider( 0, numRealizations, 0);
		int tickSpacing = 1;
		if( numRealizations > 7) {
			tickSpacing = 2;
			if( numRealizations > 19) {
				tickSpacing = 5;
				if( numRealizations > 39) {
					tickSpacing = 10;
					if( numRealizations > 69) {
						tickSpacing = 1 + numRealizations / 5;
					}
				}
			}
		}
		keyFrameSlider.setMajorTickSpacing( tickSpacing);
		keyFrameSlider.setMinorTickSpacing( 1);
		interpolationSlider = new JProgressBar( 0, interpolationMultiplier);
		interpolationSlider.setValue( 0);
		controlFrame = new RGISAVControlFrame( verticalButtonBarSize, this, secondsBetweenKeyFrames, 
			keyFrameOne, ratioKeyFrames, keyFrameSlider, interpolationSlider, timeSeries, windowUnits);
		controlFrame.setSize( horizontalButtonBarSize, verticalButtonBarSize);
		controlFrame.setLocation( 0, 0);
		controlFrame.setVisible( true);
		zoomInFactor = 0.5;
		zoomOutFactor = 2.0;
		locations = new Vector<GISPoint>();
		locationSymbol = null;
		addMouseListener( this);
	}

	public void clearChosenLocations() {
		locations.removeAllElements();
	}

	/** returns pt in the locationNumber position of the selected locations.
	 *  The position number is the order at which the user has selected new 
	 *  locations. For example, location 1 is the 1st point and location
	 *  getNumberChosenLocations() is the last chosen point. This method
	 *  returns null if locationNumber is greater than actual selected points.
	 */
	public GISPoint getChosenLocation( int locationNumber) {
		int e = locations.size();
		if( e < locationNumber) {
			return null;
		}
		return( (GISPoint) locations.elementAt( locationNumber - 1));
	}

	/** returns all selected locations. 
	 */
	public Vector<GISPoint> getChosenLocations() {
		return( locations);
	}

	public int getControlFrameVerticalSize() {
		return( verticalButtonBarSize);
	}

	public GISPoint getLastChosenLocation() {
		int e = locations.size();
		if( e == 0) {
			return null;
		}
		return( (GISPoint) locations.elementAt( e - 1));
	}

	public int getNumberChosenLocations() {
		return( locations.size());
	}

	public int getNumberRealizations() {
		return( (int) numberRealizations.getValue());
	}

	public double getRatioExponent() {
		return ratioExponent;
	}

	public int getSecondsBetweenKeyFrame() {
		return( (int) secondsBetweenKeyFrames.getValue());
	}

	public int getFirstKeyFrame() {
		return( (int) keyFrameOne.getValue());
	}

	public int getRatioKeyFrames() {
		return( (int) ratioKeyFrames.getValue());
	}

	public void mouseClicked( MouseEvent event) {
		//System.out.println( "Clicked at [ " + event.getX() + ", " + event.getY() + "]");
	}

	public void mousePressed( MouseEvent event) {
		//System.out.println( "Pressed at [ " + event.getX() + ", " + event.getY() + "]");
		Dimension frameSize = getSize();
		int pixelsHorizontal = frameSize.width;
		int pixelsVertical = frameSize.height;
		double dataEastWestExtent = (eastExtent - westExtent); 
		double dataNorthSouthExtent = (northExtent - southExtent);
		int eastPixel = event.getX();
		int southPixel = event.getY();
		int pixelsEastFromCenter = eastPixel - (pixelsHorizontal / 2);
		int pixelsSouthFromCenter = southPixel - (pixelsVertical / 2);
		double locationEast = (eastExtent + westExtent) / 2.0;
		double locationNorth = (northExtent + southExtent) / 2.0;
		switch( controlFrame.getPressedButton()) {
			case RGISAVControlFrame.ZOOM_IN:
				//System.out.println( "ZOOM IN at [ " + event.getX() + ", " +	event.getY() + "]");
				calcExtents( pixelsSouthFromCenter, pixelsEastFromCenter, pixelsVertical,
					pixelsHorizontal, dataNorthSouthExtent, dataEastWestExtent);
				double nsIChange = ((zoomInFactor * dataNorthSouthExtent) - dataNorthSouthExtent) / 2.0;
				northExtent += nsIChange;
				southExtent -= nsIChange;
				double ewIChange = ((zoomInFactor * dataEastWestExtent) - dataEastWestExtent) / 2.0;
				eastExtent += ewIChange;
				westExtent -= ewIChange;
				controlFrame.resetWindowExtents();
				break;
			case RGISAVControlFrame.ZOOM_OUT:
				//System.out.println( "ZOOM OUT at [ " + event.getX() + ", " +	event.getY() + "]");
				calcExtents( pixelsSouthFromCenter, pixelsEastFromCenter, pixelsVertical,
					pixelsHorizontal, dataNorthSouthExtent, dataEastWestExtent);
				double nsOChange = ((zoomOutFactor * dataNorthSouthExtent) - dataNorthSouthExtent) / 2.0;
				northExtent += nsOChange;
				southExtent -= nsOChange;
				double ewOChange = ((zoomOutFactor * dataEastWestExtent) - dataEastWestExtent) / 2.0;
				eastExtent += ewOChange;
				westExtent -= ewOChange;
				controlFrame.resetWindowExtents();
				break;
			case RGISAVControlFrame.PAN:
				calcExtents( pixelsSouthFromCenter, pixelsEastFromCenter, pixelsVertical,
					pixelsHorizontal, dataNorthSouthExtent, dataEastWestExtent);
				controlFrame.resetWindowExtents();
				break;
			case RGISAVControlFrame.SET_LOCATION:
				if( locationSymbol == null) {
					double area = dataEastWestExtent * dataNorthSouthExtent * 0.0003;
					setLocationSymbol( (GISPointSymbol) (new GISPointSymbolSquare( area, null, Color.black)));
				}
				double eFactor = pixelsHorizontal / dataEastWestExtent;
				double nFactor = pixelsVertical / dataNorthSouthExtent;
				if( nFactor < eFactor) {
					locationNorth -= (1.0 * pixelsSouthFromCenter / pixelsVertical) * dataNorthSouthExtent;
					locationEast += (1.0 * pixelsEastFromCenter / pixelsVertical) * dataNorthSouthExtent;
				} else {
					locationNorth -= (1.0 * pixelsSouthFromCenter / pixelsHorizontal) * dataEastWestExtent;
					locationEast += (1.0 * pixelsEastFromCenter / pixelsHorizontal) * dataEastWestExtent;
				}
				//System.out.println( "e: " + locationEast + ", n: " + locationNorth);
				GISPoint locationPoint = new GISPoint( locationEast, locationNorth);
				int e = locations.size();
				String eString = "" + (e + 1);
				locationPoint.setLabel( eString);
				locationPoint.setSymbol( locationSymbol);
				locations.add( locationPoint);
				break;
		}
	}

	public void mouseReleased( MouseEvent event) {
		//System.out.println( "Released at [ " + event.getX() + ", " + event.getY() + "]");
	}

	public void mouseEntered( MouseEvent event) {
		// System.out.println( "Entered at [ " + event.getX() + ", " + event.getY() + "]");
	}

	public void mouseExited( MouseEvent event) {
		//System.out.println( "Exited at [ " + event.getX() + ", " + event.getY() + "]");
	}

	public void paint (Graphics g) {
		super.paint( g);
		if( instanciated == true) {
			Graphics2D g2 = (Graphics2D) g;
			int buttonDown = controlFrame.getPressedButton();
			if( buttonDown == RGISAVControlFrame.ORIGINAL_EXTENT) {
				northExtent = data[ extentMap].getNorthEdge();
				southExtent = data[ extentMap].getSouthEdge();
				eastExtent = data[ extentMap].getEastEdge();
				westExtent = data[ extentMap].getWestEdge();
				controlFrame.resetWindowExtents();
			}
			Dimension frameSize = getSize();
			AffineTransform at = view2Extents( frameSize, northExtent, southExtent, eastExtent, westExtent);
			g2.transform( at);
			int pixelsHorizontal = frameSize.width;
			int pixelsVertical = frameSize.height;
			// FOLLOWING CODE LOGIC IDENTICAL TO LOGIC IN view2Extents(). If buggy, fix both.
			double dataEastExtent = (eastExtent - westExtent); 
			double dataNorthExtent = (northExtent - southExtent);
			// scale factor
			double eFactor = pixelsHorizontal / dataEastExtent;  //divide to know how much to scale be
			double nFactor = pixelsVertical / dataNorthExtent;
			if (eFactor < nFactor)  {
				nFactor = eFactor;
			} 
			double unitsHalfWide = 0.5 * frameSize.width / nFactor; 
			double unitsHalfTall = 0.5 * frameSize.height / nFactor;
//System.out.println( "west: " + westExtent  + ", east: " + eastExtent + ", north: " + northExtent + ", south: " + southExtent + ", 1/2w: " + unitsHalfWide); 

			//double centerXWindow = (pixelsHorizontal / 2);
			//double centerYWindow = (pixelsVertical / 2);
			double centerXData = ((eastExtent - westExtent) / 2.0) + westExtent;
			double centerYData = ((northExtent - southExtent) / 2.0) + southExtent; // ZZZ
			/* old
			double wWindowUnits = centerXData - unitsHalfWide;
			double eWindowUnits = centerXData + unitsHalfWide;
			double sWindowUnits = centerYData - unitsHalfTall;
			double nWindowUnits = centerYData + unitsHalfTall;
			System.out.println( "wW: " + wWindowUnits  + ", eW: " + eWindowUnits + ", nW: " + nWindowUnits + ", sW: " + sWindowUnits);
			*/

			windowUnits[ WEST] = centerXData - unitsHalfWide;
			windowUnits[ EAST] = centerXData + unitsHalfWide;
			windowUnits[ SOUTH] = centerYData - unitsHalfTall;
			windowUnits[ NORTH] = centerYData + unitsHalfTall;
			// End of identical logic

			data[ 0].setRealization( new Date());
			for( int i = 0; i < data.length; i++) {
				if( data[ i] != null) {
					//System.out.println( "RGISAnimatedView.paint() " + i);
					data[ i].paint( g2);
				}
			}
			//System.out.println( "locations.size(): " + locations.size());
			for( int i = locations.size() - 1; i >=0; i--) {
				//System.out.println( "i: " + i);
				GISPoint pt = (GISPoint) locations.elementAt( i);
				pt.paint( g2);
			}
		}
	}

	public void setData( GISData dataObjects[]) {
		data = dataObjects;
		int numDataObjects = data.length;
		//extentMap = -1;
		//activeMap = -1;
		boolean noData = true;
		for( int i = 0; i < numDataObjects; i++) {
			if( data[ i] != null) {
				noData = false;
				break;
			}
		}
		if(noData) {
		    throw new DataException("RGISAnimatedView was constructed without any GISData objects");
		}
		for( int i = 0; i < numDataObjects; i++) {
			if( data[ i] != null) {
				data[ i].setRGISAnimatedView( this);
				if( data[ i].getNumberRealizations() == 0) {
					data[ i].makeRealizations();
				}
			}
		}
		instanciated = true;
		controlFrame.changeMapList( data);
	}

	public void setLocationSymbol( GISPointSymbol symbol) {
		locationSymbol = symbol;
	}

	public void setSecondsBetweenKeyFrame( int value) {
		secondsBetweenKeyFrames.setValue( value);
	}

	public void setNumberRealizations( int value) {
		numberRealizations.setValue( value);
		//keyFrameSlider.setMajorTickSpacing( 10);
		keyFrameSlider.setMaximum( value - 1);
		//keyFrameSlider.setMajorTickSpacing( 10);
	}

	public void setFirstKeyFrame( int value) {
		keyFrameOne.setValue( value);
		keyFrameSlider.setValue( value - 1);
	}

	public void setRatioKeyFrames( int value) {
		ratioKeyFrames.setValue( value);
		interpolationSlider.setValue( value);
	}

	public void timeStep() {
		//System.out.print( " ts");
		//data[ 0].setCurrentFrameNumber( new Date());
	}

	private AffineTransform view2Extents( Dimension frameSize, double n, double s, double e, double w) {
		int pixelsHorizontal = frameSize.width;
		int pixelsVertical = frameSize.height;
		double dataEastExtent = (e - w); 
		double dataNorthExtent = (n - s);		
		//set up for translation factor
		double centerXWindow = (pixelsHorizontal / 2);
		double centerYWindow = (pixelsVertical / 2);
		double centerXData = ((e - w) / 2.0) + w;
		double centerYData = ((-n + s) / 2.0) - s;
		AffineTransform at = AffineTransform.getTranslateInstance( - centerXData, - centerYData);
		// scale factor
		double eFactor = pixelsHorizontal / dataEastExtent;  //divide to know how much to scale be
		double nFactor = pixelsVertical / dataNorthExtent;
		//figure out which factor is smaller, use that one so all data will be included
		//make the y coordinate negative b/c geographic space is opposite of computer space
		AffineTransform scaleAT = null;
		if (eFactor < nFactor)  {
			scaleAT = AffineTransform.getScaleInstance( eFactor, eFactor);
			scaleAT.concatenate( at);
		} else  {
			scaleAT = AffineTransform.getScaleInstance( nFactor, nFactor);
			scaleAT.concatenate( at);
		}
		// second translate
		AffineTransform secondTransAT = AffineTransform.getTranslateInstance( centerXWindow, centerYWindow);
		secondTransAT.concatenate( scaleAT);
		return secondTransAT;
	}

	private void calcExtents( int pixelsSouthFromCenter, int pixelsEastFromCenter, 
			int pixelsVertical, int pixelsHorizontal, double dataNorthSouthExtent,
			double dataEastWestExtent) {
		// scale factor
		double eFactor = pixelsHorizontal / dataEastWestExtent;
		double nFactor = pixelsVertical / dataNorthSouthExtent;
		if( nFactor < eFactor) {
			northExtent -= (1.0 * pixelsSouthFromCenter / pixelsVertical) * dataNorthSouthExtent;
			southExtent -= (1.0 * pixelsSouthFromCenter / pixelsVertical) * dataNorthSouthExtent;
			eastExtent += (1.0 * pixelsEastFromCenter / pixelsVertical) * dataNorthSouthExtent;
			westExtent += (1.0 * pixelsEastFromCenter / pixelsVertical) * dataNorthSouthExtent;
		} else {
			northExtent -= (1.0 * pixelsSouthFromCenter / pixelsHorizontal) * dataEastWestExtent;
			southExtent -= (1.0 * pixelsSouthFromCenter / pixelsHorizontal) * dataEastWestExtent;
			eastExtent += (1.0 * pixelsEastFromCenter / pixelsHorizontal) * dataEastWestExtent;
			westExtent += (1.0 * pixelsEastFromCenter / pixelsHorizontal) * dataEastWestExtent;
		}
	}

	public static void main( String[] args) {
		int		HORZ_FRAME = 1200;
		int		VERT_FRAME = 900;
		//int		HORZ_BUTTON = 800;
		// beginning of code to set up (point) data layer.
		double  w = 1000.0;		//extent of data
		double n = 4500.0;
		double e = 4000.0;
		double s = 1500.0;
		double insetRatio = 0.05;
		double rfCellSize = 100.0;
		//double topClear = 500.0;
		int numPoints = 50;
		int numReal = 10;
		int numMaps = 3;
		if( args.length > 0) {
			numMaps = Integer.parseInt( args[ 0]);
		}
		GISLattice t = new GISLattice( w, n, rfCellSize, rfCellSize, 
			(int) ((n - s) / rfCellSize + 1), (int) ((e - w) / rfCellSize + 1));
		t.setValueAll( 1.0f);  //making a lattice 
		// make a quad tree with 20 points in each node
		GISData gisObject[] = new GISData[ numMaps];
		Random ran = new Random();
		Color[] fill = { Color.red, Color.blue, null, Color.black };
		Color[] outline = { Color.black, Color.red, Color.black, Color.blue };
		for( int m = 0; m < numMaps; m++) {
			GISPointQuadTree myTree = new GISPointQuadTree( w, n, e, s, 20);
			myTree.setName ( "Nameless Map[" + m + "]");
			myTree.setDefaultStandardDeviationError( 200.10);
			myTree.setNumberDataRealizations( numReal);
			LatticeRandomFieldTemplate[] allTemplates = new LatticeRandomFieldTemplate[ numReal];
			for( int i = 0; i < numReal; i++) {
				LatticeRandomFieldTemplate templet = new LatticeRandomFieldTemplate();
				templet.setGISGrid( (GISGrid) t);
				templet.setSpatialDependence( 500.0);
				templet.setDistanceDecayExponent( 1.0);
				templet.setFlatParameter( 0.0);
				allTemplates[ i] = templet;
			}
			myTree.setRandomFieldTemplates( allTemplates);
			GISPointSymbol ps;
			if( m % 2 == 0) {
				ps = (GISPointSymbol) 
					new GISPointSymbolSquare( 900., fill[ m % fill.length], outline[ m % outline.length]);
			} else {
				ps = new GISPointSymbol( 900., fill[ m % fill.length], outline[ m % outline.length]);
			}
			for( int i = 1; i < numPoints; i++) {
				double x = w + (e - w) * (insetRatio * 0.5 + (1.0 - insetRatio) * ran.nextDouble());
				double y = s + (n - s) * (insetRatio * 0.5 + (1.0 - insetRatio) * ran.nextDouble());
				//System.out.println("point " + i + " to be added");
				GISPoint pt = new GISPoint( x, y);
				pt.setSymbol( ps);
				pt.setLabel( "m" + m + "p" + i);
				myTree.addPoint( pt);
			}
			// end of code to set up (point) data layer.

			// beginning of code to set up RGISAnimatedView object
			gisObject[ m] = (GISData) myTree;
		}
		int secondsBetweenRealizations = 2;
		RGISAnimatedView view = new RGISAnimatedView( HORZ_FRAME, VERT_FRAME, 
			secondsBetweenRealizations, numReal, 2.0);
		view.setData( gisObject);
		//view.setSize( );
		int maxPixels = 600;
		int horzPixels = maxPixels;
		int vertPixels = maxPixels;
		double minWest = Double.POSITIVE_INFINITY;
		double maxEast = Double.NEGATIVE_INFINITY;
		double minSouth = Double.POSITIVE_INFINITY;
		double maxNorth = Double.NEGATIVE_INFINITY;
		for( int m = 0; m < gisObject.length; m++) {
			if( minWest > gisObject[ m].getWestEdge())
				minWest = gisObject[ m].getWestEdge();
			if( minSouth > gisObject[ m].getSouthEdge())
				minSouth = gisObject[ m].getSouthEdge();
			if( maxEast > gisObject[ m].getEastEdge())
				maxEast = gisObject[ m].getEastEdge();
			if( maxNorth > gisObject[ m].getNorthEdge())
				maxNorth = gisObject[ m].getNorthEdge();
		}
		if( maxNorth - minSouth < maxEast - minWest) {
			vertPixels /= (maxEast - minWest) / (maxNorth - minSouth);
		} else {
			horzPixels /= (maxNorth - minSouth) / (maxEast - minWest);
		}
		//Frame f = new AnimationFrame( view, horzPixels, vertPixels, true);
		Frame f = new AnimationFrame( view, 700, 700, true);
		f.setVisible( true);
		// end of code to set up RGISAnimatedView object
	}
}

class LongHolder {
	private long value;

	public LongHolder( long value) {
		this.value = value;
	}

	public void setValue( long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}
}

class RGISAVControlFrame extends JFrame {
	private GISData[]		data;
	private JComboBox		mapListComboBox;
	private JRadioButton	originalExtentButton;
	private JRadioButton	zoomInButton;
	private JRadioButton	zoomOutButton;
	private JRadioButton	panButton;
	private JRadioButton	locationButton;
	private JButton		exitButton;
	private ButtonGroup	radioGroup;
	private int			buttonPressed;
	private int			activeMap;
	private int			extentMap;
	private int			verticalButtonBarSize;
	private static int 	numControlFrames = 1;
	private JSlider		secondsBetweenRealizationsSlider;
	private ChangeListener	listener, listener2;
	private double[]		windowUnits;
	private JTextField	tc0, tc1, tc2, tc3, tc4;

	public final static int ORIGINAL_EXTENT = 0;
	public final static int ZOOM_IN = 1;
	public final static int ZOOM_OUT = 2;
	public final static int PAN = 3;
	public final static int SET_LOCATION = 4;

	public RGISAVControlFrame( int verticalButtonBarSize, RGISAnimatedView view, 
			LongHolder secondsBetweenKeyFrames, LongHolder keyFrameOne, 
			LongHolder ratioKeyFrames, JSlider keyFrameSlider, 
			JProgressBar interpolationSlider, boolean timeSeries, double[] windowUnitsArray) {
		super( "RGISAnimatedView #" + numControlFrames + " Control Frame");
		this.verticalButtonBarSize = verticalButtonBarSize;
//		this.keyFrameOne = keyFrameOne;
//		this.ratioKeyFrames = ratioKeyFrames;
//		this.keyFrameSlider = keyFrameSlider;
//		this.interpolationSlider = interpolationSlider;
		windowUnits = windowUnitsArray;
		activeMap = -1;
		extentMap = -1;
		buttonPressed = ORIGINAL_EXTENT;
		ActionEventHandler handler = new ActionEventHandler();
		Container container = getContentPane();
		container.setLayout( new FlowLayout());

		String[] names = { "ALL MAPS" };
		mapListComboBox = new JComboBox( names);
		mapListComboBox.setToolTipText( "Choose active map layer");
		mapListComboBox.addItemListener( 
			new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					if( event.getStateChange() == ItemEvent.SELECTED) {
						int index = mapListComboBox.getSelectedIndex();
						if( index == 0) {
						} else {
							index--;
						}
						int mapNumber = 0;
						while( index >= 0) {
							if( data[ mapNumber] != null) {
								if( index == 0) {
									break;
								}
								mapNumber++;
								index--;
							}
						}
					}
				}
			}
		);
		container.add( mapListComboBox);

		originalExtentButton = new JRadioButton( "Original Extent", true);
		originalExtentButton.setToolTipText( "Set RGISAnimatedView to extents of active map layer");
		originalExtentButton.addActionListener( handler);
		container.add( originalExtentButton);

		zoomInButton = new JRadioButton( "Zoom In", false);
		zoomInButton.setToolTipText( "Zoom in centered on next location choosen in RGISAnimatedView");
		zoomInButton.addActionListener( handler);
		container.add( zoomInButton);

		zoomOutButton = new JRadioButton( "Zoom Out", false);
		zoomOutButton.setToolTipText( "Zoom out centered on next location choosen in RGISAnimatedView");
		zoomOutButton.addActionListener( handler);
		container.add( zoomOutButton);

		panButton = new JRadioButton( "Pan", false);
		panButton.setToolTipText( "Pan to next location choosen in RGISAnimatedView");
		panButton.addActionListener( handler);
		container.add( panButton);
		
		locationButton = new JRadioButton( "Set Location", false);
		locationButton.setToolTipText( "Selects locations in RGISAnimatedView with mouse clicks");
		locationButton.addActionListener( handler);
		container.add( locationButton);

		exitButton = new JButton( "Exit");
		exitButton.setToolTipText( "Exits program without warning");
		exitButton.addActionListener( handler);
		container.add( exitButton);

		JLabel k = new JLabel( "Realization:");
		k.setToolTipText( "Displays which realizations are being shown");
		if( timeSeries == true) {
			k = new JLabel( "Time series map:");
			k.setToolTipText( "Where in time series animation currently is (each tick is a map)");
		}
		container.add( k);

		listener2 = new RealizationListener( keyFrameOne, view);
		keyFrameSlider.setPaintTicks( true);
		keyFrameSlider.setPaintLabels( true);
		keyFrameSlider.setSnapToTicks( true);
		if( timeSeries == true) {
			keyFrameSlider.setToolTipText( "Where in time series animation currently is (each tick is 8 days)");
		} else {
			keyFrameSlider.setToolTipText( "Selects which realizations to be shown");
		}
		keyFrameSlider.addChangeListener( listener2);
		container.add( keyFrameSlider);

		JLabel l = new JLabel( "Interpolation Ratio:");
		l.setToolTipText( "Location of interpolation between realizations");
		if( timeSeries == true) {
			l = new JLabel( "Ratio to next time step:");
			l.setToolTipText( "Location of interpolation between time steps (each step is 8 days)");
		}
		container.add( l);

//		listener3 = new RealizationListener( ratioKeyFrames, view);
		//interpolationSlider.setPaintTicks( false);
		//interpolationSlider.setPaintLabels( false);
		//interpolationSlider.setSnapToTicks( false);
		if( timeSeries == true) {
			interpolationSlider.setToolTipText( "Location of interpolation between time steps (each step is 8 days)");
		} else {
			interpolationSlider.setToolTipText( "Location of interpolation between realizations");
		}
		//interpolationSlider.setToolTipText( "Location of interpolation between realizations");
		//interpolationSlider.addChangeListener( listener3);
		container.add( interpolationSlider);

		JLabel j = new JLabel( "Seconds between Realizations:");
		j.setToolTipText( "Selects seconds between realizations in uncertainty animation");
		if( timeSeries == true) {
			j = new JLabel( "Seconds between time steps:");
			j.setToolTipText( "Selects seconds between time steps (each step is 8 days)");
		}
		container.add( j);

		listener = new SliderListener( secondsBetweenKeyFrames, view);
		secondsBetweenRealizationsSlider = new JSlider( 1, 10, (int) secondsBetweenKeyFrames.getValue());
		secondsBetweenRealizationsSlider.setValueIsAdjusting( true);
		secondsBetweenRealizationsSlider.setMajorTickSpacing( 3);
		secondsBetweenRealizationsSlider.setMinorTickSpacing( 1);
		secondsBetweenRealizationsSlider.setPaintTicks( true);
		secondsBetweenRealizationsSlider.setPaintLabels( true);
		secondsBetweenRealizationsSlider.setSnapToTicks( true);
		secondsBetweenRealizationsSlider.setToolTipText( "Selects seconds between realizations in uncertainty animation");
		secondsBetweenRealizationsSlider.addChangeListener( listener);
		container.add( secondsBetweenRealizationsSlider);

		radioGroup = new ButtonGroup();
		radioGroup.add( originalExtentButton);
		radioGroup.add( zoomInButton);
		radioGroup.add( zoomOutButton);
		radioGroup.add( panButton);
		radioGroup.add( locationButton);

		JLabel k0 = new JLabel( "East Window Edge:");
		container.add( k0);
		tc0 = new JTextField( new Double( windowUnits[ RGISAnimatedView.EAST]).toString());
		container.add( tc0);

		JLabel k1 = new JLabel( "West Window Edge:");
		container.add( k1);
		tc1 = new JTextField( new Double( windowUnits[ RGISAnimatedView.WEST]).toString());
		container.add( tc1);

		JLabel k2 = new JLabel( "North Window Edge:");
		container.add( k2);
		tc2 = new JTextField( new Double( windowUnits[ RGISAnimatedView.NORTH]).toString());
		container.add( tc2);

		JLabel k3 = new JLabel( "South Window Edge:");
		container.add( k3);
		tc3 = new JTextField( new Double( windowUnits[ RGISAnimatedView.SOUTH]).toString());
		container.add( tc3);

		JLabel k4 = new JLabel( "Scale: Window is ");
		container.add( k4);
		tc4 = new JTextField( 
			new Double( windowUnits[ RGISAnimatedView.EAST] - windowUnits[ RGISAnimatedView.WEST]).toString());
		container.add( tc4);

		JLabel k5 = new JLabel( "units wide.");
		container.add( k5);
	}

	public void resetWindowExtents() {
		tc0.setText( new Double( windowUnits[ RGISAnimatedView.EAST]).toString());
		tc1.setText( new Double( windowUnits[ RGISAnimatedView.WEST]).toString());
		tc2.setText( new Double( windowUnits[ RGISAnimatedView.NORTH]).toString());
		tc3.setText( new Double( windowUnits[ RGISAnimatedView.SOUTH]).toString());
		tc4.setText( new Double( 
			windowUnits[ RGISAnimatedView.EAST] - windowUnits[ RGISAnimatedView.WEST]).toString());
	}

	public int getActiveMap() {
		return activeMap;
	}

	public void setActiveMap( int mapNumber) {
		activeMap = mapNumber;
	}

	public int getExtentMap() {
		return extentMap;
	}

	public int getVerticalSize() {
		return( verticalButtonBarSize);
	}

	public void setExtentMap( int mapNumber) {
		extentMap = mapNumber;
	}

	public void changeMapList( GISData[] data) {
		this.data = data;
		int count = mapListComboBox.getItemCount();
		while( count > 0) {
			mapListComboBox.removeItemAt( 0);
			count--;
		}
		mapListComboBox.addItem( "ALL MAPS");
		for( int i = 0; i < data.length; i++) {
			if( data[ i] != null) {
				String name = data[ i].getName();
				if( name != null) {
					mapListComboBox.addItem( name);
				} else {
					mapListComboBox.addItem( "nameless map");
				}
			}
		}
	}

	public int getPressedButton() {
		return buttonPressed;
	}

	private class ActionEventHandler implements ActionListener {
		public void actionPerformed( ActionEvent event) {
			if( event.getSource() == exitButton) {
				//System.out.println( "exitButton");
				System.exit( 0);
			} else if( event.getSource() == zoomInButton) {
				//System.out.println( "zoomInButton");
				buttonPressed = ZOOM_IN;
			} else if( event.getSource() == zoomOutButton) {
				//System.out.println( "zoomOutButton");
				buttonPressed = ZOOM_OUT;
			} else if( event.getSource() == panButton) {
				//System.out.println( "panButton");
				buttonPressed = PAN;
			} else if( event.getSource() == locationButton) {
				//System.out.println( "locationButton");
				buttonPressed = SET_LOCATION;
			} else if( event.getSource() == originalExtentButton) {
				//System.out.println( "originalExtentButton");
				buttonPressed = ORIGINAL_EXTENT;
			}
		}
	}

/*
setStartTime
secondsBetweenKeyFrames, keyFrameOne, ratioKeyFrames, numberRealizations
*/

	private class RealizationListener implements ChangeListener {
		LongHolder ih;
		RGISAnimatedView view;

		public RealizationListener( LongHolder i, RGISAnimatedView view) {
			ih = i;
			this.view = view;
		}

		public void stateChanged( ChangeEvent e) {
			long frameTime = view.getTimeCurrentFrame();
			int secondsBetweenKeyFrames = view.getSecondsBetweenKeyFrame();
			int currentRealization = view.getFirstKeyFrame();

			JSlider s1 = (JSlider) e.getSource();
			int value = s1.getValue();
			ih.setValue( value);
			int newCurrentRealization = view.getFirstKeyFrame();
			if( newCurrentRealization != currentRealization) {
				long newStartTime = (long) (frameTime -
					(1000 * secondsBetweenKeyFrames * (newCurrentRealization)));
				view.setStartTime( newStartTime);
			}
		}
	}

	private class SliderListener implements ChangeListener {
		LongHolder ih;
		RGISAnimatedView view;

		public SliderListener( LongHolder i, RGISAnimatedView view) {
			ih = i;
			this.view = view;
		}

		public void stateChanged( ChangeEvent e) {
			int currentRealization = view.getFirstKeyFrame();

			JSlider s1 = (JSlider) e.getSource();
			int maxValue = s1.getMaximum();
			int value = s1.getValue();
			ih.setValue( value);
			//System.out.println( "new time: " + ih.getValue());
			if( value == maxValue) {
				if( value < 40)
					s1.setMaximum( value * 2);
			}
			int newSecondsBetweenKeyFrames = view.getSecondsBetweenKeyFrame();
			Date newTime = new Date();
			long newStartTime = (long) (newTime.getTime() -
				(1000 * newSecondsBetweenKeyFrames * (currentRealization)));
			view.setStartTime( newStartTime);
		}
	}
}