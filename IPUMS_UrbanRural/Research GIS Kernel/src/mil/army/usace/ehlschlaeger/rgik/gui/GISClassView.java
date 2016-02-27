package mil.army.usace.ehlschlaeger.rgik.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISio;

/**
 * Misc printout and GUI stuff for GISClass.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GISClassView {
    protected GISio   gisIO;
    protected GISClass cls;
   
    public GISClassView(GISClass klass) {
        this.gisIO = new GISio( 12);
        this.cls = klass;
    }
    
    /**
     * This constructor opens a JFileChooser object for the user to pick a ESRI
     * ASCII Grid file.
     * 
     * @throws IOException
     */
    public static GISClass loadGUI() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY);
        chooser.setDialogTitle( "GISClass File Chooser");
        chooser.setMultiSelectionEnabled( false);
        FileExtensionChooser filter = new FileExtensionChooser();
        filter.addExtension( "asc");
        filter.setDescription( "Integer ASCII Grid File");
        chooser.setFileFilter( filter);
        while(true) {
            int returnVal = chooser.showOpenDialog( null);
            if( returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
                try {
                    GISClass cls = GISClass.loadEsriAscii(chooser.getSelectedFile().getAbsolutePath());
                    return cls;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Can't Open File", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //System.out.println( "GISClass.GISClass() ERROR: You must choose a file");
                return null;
            }
        }
    }

    public void paint( Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if( cls.isColorLookupTable() == false) {
            int minValue = cls.getMinimumValue();
            int[] categoryCount = cls.makeHistogram();
            int numCategories = 0;
            for( int i = categoryCount.length - 1; i >= 0; i--) {
                if( categoryCount[ i] > 0)
                    numCategories++;
            }
            if( numCategories > 0) {
                float[] red = new float[ numCategories];
                float[] green = new float[ numCategories];
                float[] blue = new float[ numCategories];
                red[ 0] = (float) Math.random();
                green[ 0] = (float) Math.random();
                blue[ 0] = (float) Math.random();
                for( int i = 1; i < numCategories; i++) {
                    float redTry = red[ i] = (float) Math.random();
                    float greenTry = green[ i] = (float) Math.random();
                    float blueTry = blue[ i] = (float) Math.random();
                    double bestDiff = (redTry - red[ 0]) * (redTry - red[ 0]) +
                            (greenTry - green[ 0]) * (greenTry - green[ 0]) + 
                            (blueTry - blue[ 0]) * (blueTry - blue[ 0]);
                    for( int j = 1; j < i; j++) {
                        double thisDiff = (redTry - red[ j]) * (redTry - red[ j]) +
                            (greenTry - green[ j]) * (greenTry - green[ j]) + 
                            (blueTry - blue[ j]) * (blueTry - blue[ j]);
                        if( thisDiff < bestDiff) {
                            bestDiff = thisDiff;
                        }
                    }
                    for( int k = (int) ( 2 + Math.sqrt( numCategories)); k >= 0; k--) {
                        redTry = (float) Math.random();
                        greenTry = (float) Math.random();
                        blueTry = (float) Math.random();
                        double diff = (redTry - red[ 0]) * (redTry - red[ 0]) +
                            (greenTry - green[ 0]) * (greenTry - green[ 0]) + 
                            (blueTry - blue[ 0]) * (blueTry - blue[ 0]);
                        for( int j = 1; j < i; j++) {
                            double thisDiff = (redTry - red[ j]) * (redTry - red[ j]) +
                                (greenTry - green[ j]) * (greenTry - green[ j]) + 
                                (blueTry - blue[ j]) * (blueTry - blue[ j]);
                            if( thisDiff < diff) {
                                diff = thisDiff;
                            }
                        }
                        if( diff > bestDiff) {
                            red[ i] = redTry;
                            green[ i] = greenTry;
                            blue[ i] = blueTry;
                            bestDiff = diff;
                        }
                    }
                }
                Color[] colors = new Color[ numCategories];
                for( int i = 0; i < numCategories; i++) {
                    colors[ i] = new Color( red[ i], green[ i], blue[ i], 1.0f);
                }
                red = null;
                green = null;
                blue = null;
                double[] values = new double[ numCategories];
                int j = 0;
                for( int i = 0; i < categoryCount.length; i++) {
                    if( categoryCount[ i] > 0)
                        values[ j++] = i + minValue;
                }
                cls.setColorLookupTable( new ColorLookupTable( values, colors));
            }
        } else {
            double ewRes = cls.getEWResolution();
            double nsRes = cls.getNSResolution();
            int nCols = cls.getNumberColumns();
            for( int r = cls.getNumberRows() - 1; r >= 0; r--) {
                int lowCol = 0;
                while( lowCol < nCols && cls.isNoData( r, lowCol) == true) {
                    lowCol++;
                }
                while( lowCol < nCols) {
                    int value = cls.getCellValue( r, lowCol);
                    int hihCol = lowCol + 1;
                    while( hihCol < nCols && 
                            cls.isNoData( r, hihCol) == false &&
                            value == cls.getCellValue( r, hihCol)) {
                        hihCol++;
                    }
                    hihCol--;
                    drawCells( g2, r, lowCol, hihCol, value, ewRes, nsRes);
                    lowCol = hihCol + 1;
                    while( lowCol < nCols && cls.isNoData( r, lowCol) == true) {
                        lowCol++;
                    }
                }
            }
        }
    }

    private void drawCells( Graphics2D g, int row, int lowCol, int hihCol, int value, double ewRes, double nsRes) {
        double minE = cls.getCellCenterEasting( row, lowCol) - ewRes * 0.5;
        double minN = cls.getCellCenterNorthing( row, lowCol) - nsRes * 0.5;
        Color c = cls.getColor( value);
        Rectangle2D r = new Rectangle2D.Double( minE, -(minN), ewRes * (1 + hihCol - lowCol), nsRes);
        g.setPaint( c);
        g.fill( r);
    }


    /** in alpha testing. */
    public void printCategoryCount() {
        int minValue = cls.getMinimumValue();
        int[] categoryCount = cls.makeHistogram();
        int totalCells = cls.getNumberRows() * cls.getNumberColumns();
        int nonNullCells = 0;
        for( int i = 0; i < categoryCount.length; i++) {
            nonNullCells += categoryCount[ i];
        }
        gisIO.printBuffered( "Category");
        System.out.print( " ");
        gisIO.printBuffered( "Count");
        System.out.print( " ");
        gisIO.printBuffered( "Ratio Grid");
        System.out.print( " ");
        gisIO.printBuffered( "Ratio Data");
        System.out.println( "");
        if( totalCells - nonNullCells > 0) {
                gisIO.printBuffered( "no data");
                System.out.print( " ");
                gisIO.printBuffered( gisIO.getIntegerForm().format( (totalCells - nonNullCells)));
                System.out.print( " ");
                gisIO.printBuffered( gisIO.getDecimalForm().format( ((totalCells - nonNullCells) * 1.0 / totalCells)));
                System.out.println( "");
        }
        for( int i = 0; i < categoryCount.length; i++) {
            if( categoryCount[ i] > 0) {
                gisIO.printBuffered( gisIO.getIntegerForm().format( (minValue + i)));
                System.out.print( " ");
                gisIO.printBuffered( gisIO.getIntegerForm().format( categoryCount[ i]));
                System.out.print( " ");
                gisIO.printBuffered( gisIO.getDecimalForm().format( (categoryCount[ i] * 1.0 / totalCells)));
                System.out.print( " ");
                gisIO.printBuffered( gisIO.getDecimalForm().format( (categoryCount[ i] * 1.0 / nonNullCells)));
                System.out.println( "");
            }
        }
    }

    /** in alpha testing. 
     * @throws IOException */
    public void printCategoryCount( String fileName) throws IOException {
        PrintWriter out = new PrintWriter(
                        new BufferedWriter( new FileWriter( cls.categoryCountFileName( fileName))));
        int minValue = cls.getMinimumValue();
        int[] categoryCount = cls.makeHistogram();
        int totalCells = cls.getNumberRows() * cls.getNumberColumns();
        int nonNullCells = 0;
        for( int i = 0; i < categoryCount.length; i++) {
            nonNullCells += categoryCount[ i];
        }
        gisIO.printBuffered( out, "Category");
        out.print( " ");
        gisIO.printBuffered( out, "Count");
        out.print( " ");
        gisIO.printBuffered( out, "Ratio Grid");
        out.print( " ");
        gisIO.printBuffered( out, "Ratio Data");
        out.println( "");
        if( totalCells - nonNullCells > 0) {
                gisIO.printBuffered( out, "no data");
                out.print( " ");
                gisIO.printBuffered( out, gisIO.getIntegerForm().format( (totalCells - nonNullCells)));
                out.print( " ");
                gisIO.printBuffered( out, gisIO.getDecimalForm().format( ((totalCells - nonNullCells) * 1.0 / totalCells)));
                out.println( "");
        }
        for( int i = 0; i < categoryCount.length; i++) {
            if( categoryCount[ i] > 0) {
                gisIO.printBuffered( out, gisIO.getIntegerForm().format( (minValue + i)));
                out.print( " ");
                gisIO.printBuffered( out, gisIO.getIntegerForm().format( categoryCount[ i]));
                out.print( " ");
                gisIO.printBuffered( out, gisIO.getDecimalForm().format( (categoryCount[ i] * 1.0 / totalCells)));
                out.print( " ");
                gisIO.printBuffered( out, gisIO.getDecimalForm().format( (categoryCount[ i] * 1.0 / nonNullCells)));
                out.println( "");
            }
        }
        out.close();
    }

    /** in alpha testing. 
     * @throws IOException */
    public void printSignatureCount( String fileName) throws IOException {
        PrintWriter out = new PrintWriter(
                        new BufferedWriter( new FileWriter( signatureFileName( fileName))));
        int minValue = cls.getMinimumValue();
        int[] categoryCount = cls.makeHistogram();
        for( int i = 0; i < categoryCount.length; i++) {
            if( categoryCount[ i] > 0) {
                out.println( (minValue + i) + " " + (minValue + i));
            }
        }
        out.close();
    }

    /** the extension for class files informing idrisi which categories should be
     *  turned into signature files is .cls
     */
    public String signatureFileName( String fileName) {
        return( fileName + ".cls");
    }

    /** in alpha testing 
     * @throws IOException
     */
    public static void main( String argv[]) throws IOException {
        if( argv == null) {
            System.out.println( "GISClass.main ERROR: must have ESRI ASCII grid name (without .asc extension)");
            System.out.println( "as a parameter. Example:");
            System.out.println( "java -mx128m GISClass geology40");
            System.exit( -1);
        }
        GISClass t = GISClass.loadEsriAscii(argv[ 0]);
        t.setName(argv[0]);
        
        // beginning of code to set up RGISAnimatedView object
        GISData gisObject[] = new GISData[ 1];
        gisObject[ 0] = (GISData) t;
        RGISAnimatedView view = new RGISAnimatedView( 1000, 1000, 2, 30, 1.0);
        view.setData( gisObject);
        //view.setSize( 1000, 1000);
        Frame f = new AnimationFrame( view, 1000, 1000, false);
        f.setVisible( true);
        // end of code to set up RGISAnimatedView object
    }
}
