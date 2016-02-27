package mil.army.usace.ehlschlaeger.rgik.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.RGISData;

public class GISLatticeView extends Component {
    /** The thing to draw. */
    protected GISLattice lattice;
    
    // how to draw it
    protected boolean drawAsContours = true;
    protected Color   contourIndexColor = Color.BLACK;

    
    public GISLatticeView(GISLattice lattice) {
        this.lattice = lattice;
    }
    
    public void setDrawAsContours( boolean value) {
        drawAsContours = value;
    }

    public void paint( Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if( lattice.isColorLookupTable() == false) {
            //ColorSpace csInstance = ColorSpace.getInstance( ColorSpace.CS_CIEXYZ);
            ColorSpace csInstance = ColorSpace.getInstance( ColorSpace.CS_sRGB);
            float[] aArray = new float[ csInstance.getNumComponents()];
            float[] bArray = new float[ csInstance.getNumComponents()];
            float[] cArray = new float[ csInstance.getNumComponents()];

            aArray = Color.BLUE.getColorComponents( csInstance, aArray);
            float aa = Color.BLUE.getAlpha() / 255.0f;
            bArray = Color.CYAN.getColorComponents( csInstance, bArray);
            float bb = Color.CYAN.getAlpha() / 255.0f;
            for( int i = cArray.length - 1; i >= 0; i--) {
                cArray[ i] = aArray[ i] * .5f + bArray[i] * .5f;
            }
            float cc = aa * .5f + bb * .5f;
            Color bc = new Color( csInstance, cArray, cc);

            aArray = Color.CYAN.getColorComponents( csInstance, aArray);
            aa = Color.CYAN.getAlpha() / 255.0f;
            bArray = Color.GREEN.getColorComponents( csInstance, bArray);
            bb = Color.GREEN.getAlpha() / 255.0f;
            for( int i = cArray.length - 1; i >= 0; i--) {
                cArray[ i] = aArray[ i] * .5f + bArray[i] * .5f;
            }
            cc = aa * .5f + bb * .5f;
            Color cg = new Color( csInstance, cArray, cc);

            aArray = Color.GREEN.getColorComponents( csInstance, aArray);
            aa = Color.GREEN.getAlpha() / 255.0f;
            bArray = Color.YELLOW.getColorComponents( csInstance, bArray);
            bb = Color.YELLOW.getAlpha() / 255.0f;
            for( int i = cArray.length - 1; i >= 0; i--) {
                cArray[ i] = aArray[ i] * .5f + bArray[i] * .5f;
            }
            cc = aa * .5f + bb * .5f;
            Color gy = new Color( csInstance, cArray, cc);

            aArray = Color.YELLOW.getColorComponents( csInstance, aArray);
            aa = Color.YELLOW.getAlpha() / 255.0f;
            bArray = Color.ORANGE.getColorComponents( csInstance, bArray);
            bb = Color.ORANGE.getAlpha() / 255.0f;
            for( int i = cArray.length - 1; i >= 0; i--) {
                cArray[ i] = aArray[ i] * .5f + bArray[i] * .5f;
            }
            cc = aa * .5f + bb * .5f;
            Color yo = new Color( csInstance, cArray, cc);

            aArray = Color.ORANGE.getColorComponents( csInstance, aArray);
            aa = Color.ORANGE.getAlpha() / 255.0f;
            bArray = Color.RED.getColorComponents( csInstance, bArray);
            bb = Color.RED.getAlpha() / 255.0f;
            for( int i = cArray.length - 1; i >= 0; i--) {
                cArray[ i] = aArray[ i] * .5f + bArray[i] * .5f;
            }
            cc = aa * .5f + bb * .5f;
            Color or = new Color( csInstance, cArray, cc);

            Color[] colors = { Color.BLUE, bc, Color.CYAN, cg, Color.GREEN, gy, Color.YELLOW, yo,
                Color.ORANGE, or, Color.RED };
            double[] values = new double[ colors.length];
            if( lattice.getMinRealizationValue() != Double.POSITIVE_INFINITY) {
                values[ 0] = lattice.getMinRealizationValue();
                values[ values.length - 1] = lattice.getMaxRealizationValue();
            } else {
                values[ 0] = lattice.getMinimumValue();
                values[ values.length - 1] = lattice.getMaximumValue();
            }
            for( int i = 1; i <= values.length - 2; i++) {
                values[ i] = (values[ 0] * (values.length - i - 1) + 
                    values[ values.length - 1] * i) / (values.length - 1);
            }
            //System.out.println("");
            String thisName = getName();
            if( thisName != null) {
                //System.out.println( "ColorTable for GISLattice [" + thisName + "]:");
            } else {
                //System.out.println( "ColorTable for GISLattice:");
            }
                aArray = colors[0].getColorComponents( csInstance, aArray);
                /*
                System.out.println( "Values: " + values[ 0] + " to " +
                    (values[ 0] + values[ 1])*.5 + ":");
                */
                //System.out.println( "X: " + aArray[0] + ", Y: " + aArray[1] + ", Z: " + aArray[2]);
                //System.out.println( "R: " + aArray[0] + ", G: " + aArray[1] + ", B: " + aArray[2]);
            for( int i = 1; i < values.length - 1; i++) {
                aArray = colors[i].getColorComponents( csInstance, aArray);
                /*
                System.out.println( "Values: " + (values[ i-1] + values[ i])*.5 + " to " +
                    (values[ i] + values[ i+1])*.5 + ":");
                */
                //System.out.println( "X: " + aArray[0] + ", Y: " + aArray[1] + ", Z: " + aArray[2]);
                //System.out.println( "R: " + aArray[0] + ", G: " + aArray[1] + ", B: " + aArray[2]);
            }
                aArray = colors[values.length - 1].getColorComponents( csInstance, aArray);
                /*
                System.out.println( "Values: " + 
                (values[ values.length - 2] + values[ values.length - 1])*.5 + " to " + 
                values[ values.length - 1] + ":");
                */
                //System.out.println( "X: " + aArray[0] + ", Y: " + aArray[1] + ", Z: " + aArray[2]);
                //System.out.println( "R: " + aArray[0] + ", G: " + aArray[1] + ", B: " + aArray[2]);
                //System.out.println( "");
            lattice.setColorLookupTable( new ColorLookupTable( values, colors));
        }
        if( lattice.isRealizable() == true) {
            int earlyRealization = lattice.getCurrentRealizationNumber();
            int nextRealization = (earlyRealization + 1) % lattice.getNumberRealizations();
            double i = lattice.getCurrentRatioToNextRealization();
            //System.out.println( earlyRealization + " " + nextRealization + " " + i);
            if( earlyRealization > lattice.getRealizations().length ||
                    nextRealization > lattice.getRealizations().length) {
                paintThisGISLattice( g2);
            } else {
                paintContourInterpolation( g2, earlyRealization, nextRealization, i);
            }
        } else {
            paintThisGISLattice( g2);
        }
    }

    private void paintThisGISLattice( Graphics2D g2) {
        if( drawAsContours == true) {
            for( double r = lattice.getNumberRows() - 1.5; r >= 0.0; r -= 1.0) {
                for( double c = lattice.getNumberColumns() - 1.5; c >= 0.0; c -= 1.0) {
                    paintContour( g2, r, c);
                }
            }
        } else {
            double ewRes = lattice.getEWResolution();
            double nsRes = lattice.getNSResolution();
            for( int r = lattice.getNumberRows() - 1; r >= 0; r--) {
                for( int c = lattice.getNumberColumns() - 1; c >= 0; c--) {
                    drawCell( g2, r, c, ewRes, nsRes);
                }
            }
        }
    }

    private void paintContourInterpolation( Graphics2D g2, int earlyRealization, 
            int nextRealization, double i) {
        for( double r = lattice.getNumberRows() - 1.5; r >= 0.0; r -= 1.0) {
            for( double c = lattice.getNumberColumns() - 1.5; c >= 0.0; c -= 1.0) {
                paintContourInterpolation( g2, r, c, earlyRealization, nextRealization, i);
            }
        }
    }

    private void paintContourInterpolation( Graphics2D g, double rC, double cC, 
            int earlyRealization, int nextRealization, double i) {
        int n = (int) rC;
        int s = (int) Math.ceil( rC);
        int e = (int) Math.ceil( cC);
        int w = (int) cC;
        if( lattice.getRealizations()[ earlyRealization].isNoData( n, e) == false && 
            lattice.getRealizations()[ earlyRealization].isNoData( n, w) == false && 
            lattice.getRealizations()[ earlyRealization].isNoData( s, e) == false && 
            lattice.getRealizations()[ earlyRealization].isNoData( s, w) == false &&
            lattice.getRealizations()[ nextRealization].isNoData( n, e) == false && 
            lattice.getRealizations()[ nextRealization].isNoData( n, w) == false && 
            lattice.getRealizations()[ nextRealization].isNoData( s, e) == false && 
            lattice.getRealizations()[ nextRealization].isNoData( s, w) == false) {
            paintContourInterpolation( g, n, s, e, w, earlyRealization, nextRealization, i);
        } else {
            //System.out.println( "GISLattice: emptyBox");
        }
    }

    private void paintContour( Graphics2D g, double rC, double cC) {
        int n = (int) rC;
        int s = (int) Math.ceil( rC);
        int e = (int) Math.ceil( cC);
        int w = (int) cC;
        if( lattice.isNoData( n, e) == false && lattice.isNoData( n, w) == false && 
            lattice.isNoData( s, e) == false && lattice.isNoData( s, w) == false) {
            paintContour( g, n, s, e, w);
        } else {
            //System.out.println( "GISLattice: emptyBox");
        }
    }

    private void paintContourInterpolation( Graphics2D g, int n, int s, int e, int w, 
            int earlyRealization, int nextRealization, double i) {
        double nw = lattice.interpolateValues( earlyRealization, nextRealization, n, w, i);
        int nwIndex = (int) ((nw - lattice.getContourBase()) / lattice.getContourInterval());
        int lowIndex = nwIndex;
        int hihIndex = nwIndex;
        double low = nw;
        double ne = lattice.interpolateValues( earlyRealization, nextRealization, n, e, i);
        int neIndex = (int) ((ne - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > neIndex)
            lowIndex = neIndex;
        if( hihIndex < neIndex)
            hihIndex = neIndex;
        if( low > ne) {
            low = ne;
        }
        double se = lattice.interpolateValues( earlyRealization, nextRealization, s, e, i);
        int seIndex = (int) ((se - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > seIndex)
            lowIndex = seIndex;
        if( hihIndex < seIndex)
            hihIndex = seIndex;
        if( low > se) {
            low = se;
        }
        double sw = lattice.interpolateValues( earlyRealization, nextRealization, s, w, i);
        int swIndex = (int) ((sw - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > swIndex)
            lowIndex = swIndex;
        if( hihIndex < swIndex)
            hihIndex = swIndex;
        if( low > sw) {
            low = sw;
        }
        if( lowIndex < hihIndex) {
            paintContour( g, n, s, e, w, ne, se, nw, sw, 
                neIndex, seIndex, nwIndex, swIndex, lowIndex, hihIndex);
        }
    }

    private void paintContour( Graphics2D g, int n, int s, int e, int w) {
        double nw = lattice.getCellValue( n, w);
        int nwIndex = (int) ((nw - lattice.getContourBase()) / lattice.getContourInterval());
        int lowIndex = nwIndex;
        int hihIndex = nwIndex;
        double low = nw;
        double ne = lattice.getCellValue( n, e);
        int neIndex = (int) ((ne - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > neIndex)
            lowIndex = neIndex;
        if( hihIndex < neIndex)
            hihIndex = neIndex;
        if( low > ne) {
            low = ne;
        }
        double se = lattice.getCellValue( s, e);
        int seIndex = (int) ((se - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > seIndex)
            lowIndex = seIndex;
        if( hihIndex < seIndex)
            hihIndex = seIndex;
        if( low > se) {
            low = se;
        }
        double sw = lattice.getCellValue( s, w);
        int swIndex = (int) ((sw - lattice.getContourBase()) / lattice.getContourInterval());
        if( lowIndex > swIndex)
            lowIndex = swIndex;
        if( hihIndex < swIndex)
            hihIndex = swIndex;
        if( low > sw) {
            low = sw;
        }
        if( lowIndex < hihIndex) {
            paintContour( g, n, s, e, w, ne, se, nw, sw, 
                neIndex, seIndex, nwIndex, swIndex, lowIndex, hihIndex);
        }
    }

    private void paintContourInterpolation( Graphics2D g, int n, int s, int e, int w, double ne, 
            double se, double nw, double sw, int neIndex, int seIndex, 
            int nwIndex, int swIndex, int lowIndex, int hihIndex) {
        double[] easting = new double[ 4];
        double[] northing = new double[ 4];
        boolean isIndex = true;
        for( int contour = lowIndex + 1; contour <= hihIndex; contour++) {
            double contourValue = contour * lattice.getContourInterval() + lattice.getContourBase();
            int numPts = 0;
            if( contour % lattice.getContourIndexRate() == 0) {
                isIndex = true;
            } else {
                isIndex = false;
            }
            if( neIndex < contour) {
                if( nwIndex >= contour) {
                    double ratio = lattice.ratioEdge( ne, nw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, w);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, e) * ratio +
                        lattice.getCellCenterEasting( n, w) * (1.0 - ratio);
                }
                if( seIndex >= contour) {
                    double ratio = lattice.ratioEdge( ne, se, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, e) * ratio +
                        lattice.getCellCenterNorthing( s, e) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, e);
                }
            }
            if( seIndex < contour) {
                if( swIndex >= contour) {
                    double ratio = lattice.ratioEdge( se, sw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, w);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, e) * ratio +
                        lattice.getCellCenterEasting( s, w) * (1.0 - ratio);
                }
                if( neIndex >= contour) {
                    double ratio = lattice.ratioEdge( se, ne, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, e) * ratio +
                        lattice.getCellCenterNorthing( n, e) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, e);
                }
            }
            if( swIndex < contour) {
                if( seIndex >= contour) {
                    double ratio = lattice.ratioEdge( sw, se, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, e);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, w) * ratio +
                        lattice.getCellCenterEasting( s, e) * (1.0 - ratio);
                }
                if( nwIndex >= contour) {
                    double ratio = lattice.ratioEdge( sw, nw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, w) * ratio +
                        lattice.getCellCenterNorthing( n, w) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, w);
                }
            }
            if( nwIndex < contour) {
                if( neIndex >= contour) {
                    double ratio = lattice.ratioEdge( nw, ne, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, e);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, w) * ratio +
                        lattice.getCellCenterEasting( n, e) * (1.0 - ratio);
                }
                if( swIndex >= contour) {
                    double ratio = lattice.ratioEdge( nw, sw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, w) * ratio +
                        lattice.getCellCenterNorthing( s, w) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, w);
                }
            }
            if( numPts == 2) {
                paintCL( g, easting[ 0], northing[ 0], easting[ 1], northing[ 1], contourValue, isIndex);
            } else if( numPts != 4) {
                System.out.println( "numPts: " + numPts + ", SHOULD NEVER HAPPEN");
            } else {
                double firstDist = RGISData.distance( easting[ 0], northing[ 0], easting[ 1], northing[ 1]);
                firstDist += RGISData.distance( easting[ 2], northing[ 2], easting[ 3], northing[ 3]);
                double secondDist = RGISData.distance( easting[ 1], northing[ 1], easting[ 2], northing[ 2]);
                secondDist += RGISData.distance( easting[ 0], northing[ 0], easting[ 3], northing[ 3]);
                if( firstDist < secondDist) {
                    paintCL( g, easting[ 0], northing[ 0], easting[ 1], northing[ 1], contourValue, isIndex);
                    paintCL( g, easting[ 2], northing[ 2], easting[ 3], northing[ 3], contourValue, isIndex);
                } else {
                    paintCL( g, easting[ 1], northing[ 1], easting[ 2], northing[ 2], contourValue, isIndex);
                    paintCL( g, easting[ 0], northing[ 0], easting[ 3], northing[ 3], contourValue, isIndex);
                }
            }
        }
    }

    private void paintContour( Graphics2D g, int n, int s, int e, int w, double ne, 
            double se, double nw, double sw, int neIndex, int seIndex, 
            int nwIndex, int swIndex, int lowIndex, int hihIndex) {
        double[] easting = new double[ 4];
        double[] northing = new double[ 4];
        boolean isIndex = true;
        for( int contour = lowIndex + 1; contour <= hihIndex; contour++) {
            double contourValue = contour * lattice.getContourInterval() + lattice.getContourBase();
            int numPts = 0;
            if( contour % lattice.getContourIndexRate() == 0) {
                isIndex = true;
            } else {
                isIndex = false;
            }
            if( neIndex < contour) {
                if( nwIndex >= contour) {
                    double ratio = lattice.ratioEdge( ne, nw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, w);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, e) * ratio +
                        lattice.getCellCenterEasting( n, w) * (1.0 - ratio);
                }
                if( seIndex >= contour) {
                    double ratio = lattice.ratioEdge( ne, se, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, e) * ratio +
                        lattice.getCellCenterNorthing( s, e) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, e);
                }
            }
            if( seIndex < contour) {
                if( swIndex >= contour) {
                    double ratio = lattice.ratioEdge( se, sw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, w);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, e) * ratio +
                        lattice.getCellCenterEasting( s, w) * (1.0 - ratio);
                }
                if( neIndex >= contour) {
                    double ratio = lattice.ratioEdge( se, ne, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, e) * ratio +
                        lattice.getCellCenterNorthing( n, e) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, e);
                }
            }
            if( swIndex < contour) {
                if( seIndex >= contour) {
                    double ratio = lattice.ratioEdge( sw, se, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, e);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, w) * ratio +
                        lattice.getCellCenterEasting( s, e) * (1.0 - ratio);
                }
                if( nwIndex >= contour) {
                    double ratio = lattice.ratioEdge( sw, nw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( s, w) * ratio +
                        lattice.getCellCenterNorthing( n, w) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, w);
                }
            }
            if( nwIndex < contour) {
                if( neIndex >= contour) {
                    double ratio = lattice.ratioEdge( nw, ne, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, e);
                    easting[ numPts++] = lattice.getCellCenterEasting( n, w) * ratio +
                        lattice.getCellCenterEasting( n, e) * (1.0 - ratio);
                }
                if( swIndex >= contour) {
                    double ratio = lattice.ratioEdge( nw, sw, contour);
                    northing[ numPts] = lattice.getCellCenterNorthing( n, w) * ratio +
                        lattice.getCellCenterNorthing( s, w) * (1.0 - ratio);
                    easting[ numPts++] = lattice.getCellCenterEasting( s, w);
                }
            }
            if( numPts == 2) {
                paintCL( g, easting[ 0], northing[ 0], easting[ 1], northing[ 1], contourValue, isIndex);
            } else if( numPts != 4) {
                System.out.println( "numPts: " + numPts + ", SHOULD NEVER HAPPEN");
            } else {
                double firstDist = RGISData.distance( easting[ 0], northing[ 0], easting[ 1], northing[ 1]);
                firstDist += RGISData.distance( easting[ 2], northing[ 2], easting[ 3], northing[ 3]);
                double secondDist = RGISData.distance( easting[ 1], northing[ 1], easting[ 2], northing[ 2]);
                secondDist += RGISData.distance( easting[ 0], northing[ 0], easting[ 3], northing[ 3]);
                if( firstDist < secondDist) {
                    paintCL( g, easting[ 0], northing[ 0], easting[ 1], northing[ 1], contourValue, isIndex);
                    paintCL( g, easting[ 2], northing[ 2], easting[ 3], northing[ 3], contourValue, isIndex);
                } else {
                    paintCL( g, easting[ 1], northing[ 1], easting[ 2], northing[ 2], contourValue, isIndex);
                    paintCL( g, easting[ 0], northing[ 0], easting[ 3], northing[ 3], contourValue, isIndex);
                }
            }
        }
    }

    private void paintCL( Graphics2D g2, double eA, double nA, double eB, double nB, double value, boolean index) {
        Color c = lattice.getColor( value);
        g2.setPaint( c);
        if( index == true) {
            g2.setColor( contourIndexColor);
        } else {
            //g2.setColor( contourColor);
        }
        if( Math.abs( eA - eB) <= lattice.getEWResolution() * 1.0001 && Math.abs( nA - nB) <= lattice.getNSResolution() * 1.0001) {
        //if( Double.NaN != eA && Double.NaN != eB && Double.NaN != nA && Double.NaN != nB) {
            Line2D line = new Line2D.Double( eA, - nA, eB, - nB);
            g2.draw( line);
        } //else System.out.println( "eA: " + eA + ", eB: " + eB + ", ewRes: " + getEWResolution() + ", nA: " + nA + ", nB: " + nB + ", nsRes: " + getNSResolution());
    }

    private void drawCell( Graphics2D g, int row, int col, double ewRes, double nsRes) {
        if( lattice.isNoData( row, col) == false) {
            double minE = lattice.getCellCenterEasting( row, col) - ewRes * 0.5;
            double minN = lattice.getCellCenterNorthing( row, col) + nsRes * 0.5;
            double value = lattice.getCellValue( row, col);
            Color c = lattice.getColor( value);
            Rectangle2D r = new Rectangle2D.Double( minE, -(minN), ewRes, nsRes);
            g.setPaint( c);
            g.fill( r);
        }
    }

    public static void show(GISLattice lattice) {
        JFrame f = new JFrame();
        f.add(new GISLatticeView(lattice));
        f.pack();
        f.setVisible(true);
    }
}
