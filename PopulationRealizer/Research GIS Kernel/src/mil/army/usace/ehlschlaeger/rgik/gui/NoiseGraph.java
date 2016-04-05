package mil.army.usace.ehlschlaeger.rgik.gui;
import graph.Axis;
import graph.DataSet;
import graph.Graph2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
/*************************************************************************
**
**    NoiseGraph, by Dr. Charles R. Ehlschlaeger
**					Hunter College
**					chuckre@comcast.net
**
**    This program is free software; you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation; either version 2 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program; if not, write to the Free Software
**    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
**
*************************************************************************/

public class NoiseGraph extends JFrame {
      private Graph2D graph;
      private Label dates;
      private DataSet data1;
      private CalculateData cd1;
      private Axis	yaxis_right;
      private int seed;
	
	// In milliseconds how often do we want to add a new data point.
      private int period = 50;

	// Maximum number of points to display before scrolling the data
      private int maximum = 1000;

      URL markersURL, dataURL;

      public NoiseGraph( String titleHead, Vector<Calendar> timeInformation, Vector<Double> noiseInformation,
			int xSize, int ySize) {
		super( titleHead + " graph");
		setSize( xSize, ySize);
		seed = 1;
		// Create the Graph instance and modify the default behaviour
		graph = new Graph2D();
		graph.zerocolor = new Color( 0, 0, 0);
		graph.borderTop    = 5;
		graph.borderBottom = 5;
		dates = new Label( "Starting", Label.CENTER);
		dates.setFont( new Font( "TimesRoman", Font.PLAIN, 12));
		dates.setBackground( new Color( 255, 255, 255));
		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add("Center", graph);
		getContentPane().add( "South", dates);
		// Modify the default Data behaviour
		data1 = new DataSet();
		data1.linecolor   = new Color( 255, 0, 0);
		data1.marker      = 1;
		data1.markercolor = new Color( 100, 100, 255);
		// Setup the Axis. Attach it to the Graph2D instance, and attach the data to it.
		yaxis_right = graph.createAxis( Axis.RIGHT);
		yaxis_right.attachDataSet( data1);
		yaxis_right.setLabelFont( new Font( "Helvetica", Font.PLAIN, 8));
		//graph.framecolor = new Color( 255, 255, 255);
		graph.gridcolor = new Color( 155, 155, 155);
		graph.setGraphBackground( new Color( 255, 255, 255));
		graph.attachDataSet( data1);
		if( timeInformation == null) {
			// Calculate the data asynchronously using a new Thread.
			cd1 = new CalculateData( data1, graph, period, maximum, seed, dates);
			cd1.start();
		} else {
			if( noiseInformation == null) {
				throw new IllegalArgumentException( "noiseInformation == null");
			}
			if( noiseInformation.size() != timeInformation.size()) {
			    throw new IllegalArgumentException("noiseInformation.size() != timeInformation.size()");
			}
			double[] thisData = new double[ noiseInformation.size() * 2];
			int count = noiseInformation.size();
			for( int i = 0; i < count; i++) {
				Calendar cObject = timeInformation.remove( 0);
				double timeD = (double) cObject.getTimeInMillis();
				thisData[ 2 * i] = timeD;
				Double dObject = noiseInformation.remove( 0);
				thisData[ 2 * i + 1] = dObject.doubleValue();
			}
			data1.append( thisData, count);
			double xMin = data1.getXmin();
			double xMax = data1.getXmax();
			Calendar minDate = Calendar.getInstance();
			minDate.setTimeInMillis( (long) xMin);
			Calendar maxDate = Calendar.getInstance();
			maxDate.setTimeInMillis( (long) xMax);
			dates.setText( minDate.getTime().toString() + " - " + maxDate.getTime().toString());
			data1.yaxis.maximum = 160.0;
			data1.yaxis.minimum = 0.0;
		}
		//setVisible( true);
      }

	public static void main( String[] argv) {
		NoiseGraph ng = new NoiseGraph( "testGraph", null, null, 400, 200);
		ng.setVisible( true);
	}
}


/*
** Here is the Thread class to calculate the data and append it to the existing data set.
** This class is easily modified to get the data from a server or the local machine. 
*/

class CalculateData extends Thread {
	// local copy of the update period in milliseconds
	int p = 100;
	DataSet dataSetObj;
	Graph2D graphObj;
	int maxPointsOnGraph, seed;
	String titleHead;
	Label dates;
       
	public CalculateData( DataSet ds, Graph2D g, int p, int maxPointsOnGraph, 
			int seed, Label dates) {	
            dataSetObj = ds;
            graphObj = g;
            if( p > 0) {
			this.p = p;
		}
            if( maxPointsOnGraph > 0) {
			this.maxPointsOnGraph = maxPointsOnGraph;
		}
		this.seed = seed;
		this.dates = dates;
	}

	public void run() {
		//System.out.println( "run start");
		double data[] = new double[ 2];
 		if( dataSetObj == null) 
			return;
		setPriority( Thread.MIN_PRIORITY);
		Random ran = new Random();
		ran.setSeed( (long) seed);
		data[ 1] = ran.nextGaussian();
		data[ 1] *= data[ 1];
		data[ 1] *= 24.0;
		data[ 1] += 30.0;
		if( data[ 1] < 30.0) {
			data[ 1] = 30.0 + ran.nextGaussian() * 3.0;
		}
		Date nowTime = new Date();
		data[0] = nowTime.getTime();
		try {
			dataSetObj.append( data, 1);
	      } catch (Exception e) {
			System.out.println("Error appending Data!");
		}
		try {    
			sleep( p); 
		} catch( Exception e) { 
		}
		while( true) {
			if( dataSetObj.dataPoints() >= maxPointsOnGraph) { /*count >= maxPointsOnGraph*/
				dataSetObj.delete( 0,0);
			}
			data[ 1] = ran.nextGaussian();
			data[ 1] *= data[ 1];
			data[ 1] *= 22.0;
			data[ 1] += 30.0;
			if( data[ 1] < 30.0) {
				data[ 1] = 30.0 + ran.nextGaussian() * 3.0;
			}
			nowTime = new Date();
			data[0] = nowTime.getTime();
			try {
				dataSetObj.append( data, 1);
		      } catch (Exception e) {
                        System.out.println("Error appending Data!");
			}
			double xMin = dataSetObj.getXmin();
			double xMax = dataSetObj.getXmax();
			Date minDate = new Date( (long) xMin);
			Date maxDate = new Date( (long) xMax);
			dates.setText( minDate.toString() + "--" + maxDate.toString());
			dataSetObj.yaxis.maximum = 160.0;
			dataSetObj.yaxis.minimum = 0.0;

			// To avoid flashing try and repaint halfway through the sleep
			graphObj.repaint( p / 2);
			try {    
				sleep( p); 
			} catch( Exception e) { 
			}
		}
	}
}


