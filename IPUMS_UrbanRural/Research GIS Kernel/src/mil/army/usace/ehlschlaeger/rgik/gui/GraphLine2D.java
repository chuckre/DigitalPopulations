package mil.army.usace.ehlschlaeger.rgik.gui;

import graph.Axis;
import graph.DataSet;
import graph.Graph2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;

import javax.swing.JFrame;



/**
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GraphLine2D extends JFrame {
    private Graph2D   graph;
    private Label     dates;
    private DataSet[] data;
    private Axis      xaxis_bottom, yaxis_right;

    public GraphLine2D(String titleHead, double[] x, double[][] y,
            Color[] color, int xSize, int ySize) {
        super(titleHead);
        setSize(xSize, ySize);
        // Create the Graph instance and modify the default behaviour
        graph = new Graph2D();
        graph.zerocolor = new Color(0, 0, 0);
        graph.borderTop = 15;
        graph.borderBottom = 5;
        dates = new Label("Starting", Label.CENTER);
        dates.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        dates.setBackground(new Color(255, 255, 255));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", graph);
        getContentPane().add("South", dates);
        // Modify the default Data behaviour
        data = new DataSet[y.length];
        System.out.println("color.length: " + color.length);
        if (color.length != y[0].length) {
            throw new IllegalArgumentException(
                                               "there must be as many colors as lines");
        }
        // Setup the Axis. Attach it to the Graph2D instance, and attach the
        // data to it.
        yaxis_right = graph.createAxis(Axis.RIGHT);
        yaxis_right.setLabelFont(new Font("TimesRoman", Font.PLAIN, 10));
        xaxis_bottom = graph.createAxis(Axis.BOTTOM);
        xaxis_bottom.setLabelFont(new Font("TimesRoman", Font.PLAIN, 10));
        xaxis_bottom.setTitleText("Lag Distance");
        yaxis_right.setTitleText("Moment of Inertia");

        graph.gridcolor = new Color(100, 100, 100);
        graph.setGraphBackground(new Color(255, 255, 255));
        for (int i = 0; i < y[0].length; i++) {
            data[i] = new DataSet();
            data[i].linecolor = color[i];
            data[i].marker = 1;
            data[i].markercolor = color[i];
            yaxis_right.attachDataSet(data[i]);
            xaxis_bottom.attachDataSet(data[i]);
            graph.attachDataSet(data[i]);
        }
        int count = y.length;
        double[] thisData = new double[count * 2];
        for (int d = 0; d < y[0].length; d++) {
            for (int i = 0; i < count; i++) {
                thisData[2 * i] = x[i];
                thisData[2 * i + 1] = y[i][d];
            }
            data[d].append(thisData, count);
            dates.setText(titleHead);
        }
        double yMin = graph.getYmin();
        double yMax = graph.getYmax();
        // System.out.println( yMin + " " + yMax);
        data[0].yaxis.maximum = yMax;
        data[0].yaxis.minimum = Math.min(0.0, yMin);
    }
}
