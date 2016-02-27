package mil.army.usace.ehlschlaeger.rgik.gui;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;

public class RGISView extends Frame {
	GISData data[];
	int numDataObjects;
	int extentMap;

      
	public static void main( String[] args) {
		System.out.println ("main start");
		double  w = 1000.0;	//extent of data
		double n = 4500.0;
		double e = 4000.0;
		double s = 1500.0;
		double insetRatio = 0.05;
		GISPointQuadTree myTree = new GISPointQuadTree(w,n,e,s,20);
		Random ran = new Random( );
		for( int i = 1; i < 200; i++) {
			double x = w + (e - w) * (insetRatio * 0.5 + (1.0 - insetRatio) * ran.nextDouble());
			double y = s + (n - s) * (insetRatio * 0.5 + (1.0 - insetRatio) * ran.nextDouble());
			//System.out.println("point " + i + " to be added");
			GISPoint pt = new GISPoint( x, y);
			myTree.addPoint( pt);
		}


		GISData gisObject[] = new GISData[1];
		gisObject[ 0] = (GISData) myTree;
		RGISView view = new RGISView( gisObject);
		view.setSize( 800, 700);
		view.setVisible(true);
	    System.out.println ("main end");
	}

	public RGISView( GISData dataObjects[]) {
		super( "RGISView");
		data = dataObjects;
		numDataObjects = data.length;
		extentMap = -1;
		for( int i = 0; i < numDataObjects; i++) {
			if( data[ i] != null) {
				extentMap = i;
				i = numDataObjects;
			}
		}
		if( extentMap == -1) {
		    throw new IllegalArgumentException("RGISView.RGISView was constructed without any GISData objects");
		}
		addWindowListener (new WindowAdapter()  {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public void paint (Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Dimension frameSize = getSize();
		AffineTransform at = view2Extents( data[ extentMap], frameSize.width-20, frameSize.height-100);
		g2.transform(at);
		for( int i = 0; i < numDataObjects; i++) {
			if( data[ i] != null) {
				data[ i].paint( g2);
			}
		}
	}

	public AffineTransform view2Extents( GISData region, int pixelsHorizontal, int pixelsVertical) {
		//System.out.println("beginning getTransform");
		//System.out.println("pixHorizontal = " + pixelsHorizontal);
		//System.out.println("pixVertical = " + pixelsVertical);
		double e = region.getEastEdge();
		double w = region.getWestEdge();
		double s = region.getSouthEdge();
		double n = region.getNorthEdge();
		//System.out.println("east extent of data = " + e);
		//System.out.println("west extent of data = " + w);
		//System.out.println("south extent of data = " + s);
		//System.out.println("north extent of data = " + n);
		double dataEastExtent = (e - w); //get extent of data
		double dataNorthExtent = (-n + s);
		//System.out.println("dataEastExtent = " + dataEastExtent);
		//System.out.println("dataNorthExtent = " + dataNorthExtent);
				
		//set up for translation factor
		double centerXWindow = (pixelsHorizontal/2);
		//System.out.println("centerXWindow = " + centerXWindow);
		double centerYWindow = (pixelsVertical/2);
		//System.out.println("centerYWindow = " + centerYWindow);
		double centerXData = ( ((e-w)/2)+ w);
		//System.out.println("centerXData = " + centerXData);
		double centerYData = (((-n+s)/2)-s);
		//System.out.println("centerYData = " + centerYData);
				
		double translateX = -(centerXData);
		//System.out.println("translateX = " + translateX);
		double translateY = -(centerYData);
		//System.out.println("translateY = " + translateY);
		AffineTransform at = AffineTransform.getTranslateInstance( translateX, translateY);
		//System.out.println("firstTranslate = " + at);
		//System.out.println("after first translation");
		double eFactor = pixelsHorizontal/dataEastExtent;  //divide to know how much to scale be
		//System.out.println("eFactor = " + eFactor);
		double nFactor = pixelsVertical/dataNorthExtent;
		//System.out.println("nFactor = " + nFactor);
		//figure out which factor is smaller, use that one so all data will be included
		//make the y coordinate negative b/c geographic space is opposite of computer space
		AffineTransform scaleAT = null;
		if (eFactor < nFactor)  {
			scaleAT = AffineTransform.getScaleInstance(eFactor, -eFactor);
			//System.out.println("scale factor = " + scaleAT);
			scaleAT.concatenate(at);
		} else  {
			scaleAT = AffineTransform.getScaleInstance(nFactor, -nFactor);
			//System.out.println("scale factor = " + scaleAT);
			scaleAT.concatenate(at);
		}
		//System.out.println("firstTranslate after concatenate w/scale = " + scaleAT);
		//System.out.println("after scaling");
		translateX = centerXWindow; // defines how much to transform during second translate
		translateY = centerYWindow + 50; // defines how much to transform during second translate
		AffineTransform secondTransAT = AffineTransform.getTranslateInstance( translateX, translateY);
		//System.out.println("translation2 = " + secondTransAT);
		secondTransAT.concatenate(scaleAT);
		//System.out.println("after 2nd concatenate, at = " + secondTransAT);
		return secondTransAT;
	}
}
  
