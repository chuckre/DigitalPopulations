package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class GISPointQuadTreeTest {
    static int NUM_POINTS = 20;
    static int PTS_PER_NODE = 3;
    // Object to test.
    GISPointQuadTree<GISPoint> qt;
    // Raw copy of all points added to qt for later verification.
    double[] xary, yary;
    
    /**
     * Runs before each test*() method.
     * Creates a QuadTree to test plus the backup arrays for verification.
     * @throws Exception whenever it wants
     */
    @Before
    public void setUp() throws Exception {
        Random rng = new Random();
        
        qt = new GISPointQuadTree<GISPoint>(0.0, 1.0, 1.0, 0.0, PTS_PER_NODE);
        xary = new double[NUM_POINTS];
        yary = new double[NUM_POINTS];
        
        for (int i = 0; i < NUM_POINTS; i++) {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            // file away a copy for testing
            xary[i] = x;
            yary[i] = y;
            // add point to test structure
            GISPoint pt = new GISPoint(x, y);
            qt.addPoint(pt);
            qt.checkQT();
        }
    }

    /**
     * Runs after each test*() method.
     * @throws Exception whenever it wants
     */
    @After
    public void tearDown() throws Exception {
        qt = null;
        xary = null;
        yary = null;
    }

    @Test
    public void testDeleteAllPoints() {
        assertEquals(NUM_POINTS, qt.getNumberPointsIncludingSubNodes());
        assertEquals(PTS_PER_NODE, qt.getNumberPointsInNode());
        qt.deleteAllPoints();
        qt.checkQT();
        assertEquals(0, qt.getNumberPointsIncludingSubNodes());
        assertEquals(0, qt.getNumberPointsInNode());
    }

    @Test
    public void testAddPoint() {
        // Fetch all points in qt
        GISPoint center = new GISPoint(0.5, 0.5);
        LinkedList<GISPoint> allpts = qt.getPoints(center, Double.MAX_VALUE);
        // Scan qt,
        // verify each point in backup array,
        // then remove from backup array.
        for (Iterator<GISPoint> iterator = allpts.iterator(); iterator.hasNext();) {
            GISPoint point = iterator.next();
            boolean found = nukePoint(point.getEasting(), point.getNorthing());
            assertTrue("Quad tree contains a point we didn't insert: "+point, found);
        }
        // Verify that nothing remains.
        for (int i = 0; i < NUM_POINTS; i++) {
            boolean nuked = (xary[i]==-1 && yary[i]==-1);
            assertTrue("Point was added but not found: ("+xary[i]+", "+yary[i]+")", nuked);
        }
    }

    boolean nukePoint(double x, double y) {
        for (int i = 0; i < NUM_POINTS; i++) {
            if(xary[i] == x && yary[i] == y) {
                // damage point to prevent matching it again.
                // nextDouble only generates 0..1.
                xary[i] = -1;
                yary[i] = -1;
                // found
                return true;
            }
        }
        // not found
        return false;
    }
    
    @Test
    public void testClosestPoint() {
        // Verify each point is its own closest.
        for (int i = 0; i < NUM_POINTS; i++) {
            GISPoint test = new GISPoint(xary[i], yary[i]);
            GISPoint found = qt.closestPoint(test);
            assertTrue("Point is not its own closest", xary[i]==found.getEasting() && yary[i]==found.getNorthing());
        }
    }

    @Test
    public void testClosestNPoints() {
        // Bit of a gimmick here:
        // First we request every point in qt using method under test:
        // (we ask for 2*NUM to test length of returned array)
        GISPoint[] points = qt.closestNPoints(new GISPoint(0,0), NUM_POINTS);
        assertEquals(NUM_POINTS, points.length);
        // Then we overwrite our backup arrays with the points found:
        xary = new double[NUM_POINTS];
        yary = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xary[i] = points[i].getEasting();
            yary[i] = points[i].getNorthing();
        }
        // .. so that this method will do the verification for us:
        testAddPoint();
    }

    @Test
    public void testRemovePoint() {
        // Verify setUp
        assertEquals(NUM_POINTS, qt.getNumberPointsIncludingSubNodes());
        // Test failed remove
        assertFalse(qt.removePoint(new GISPoint(2,2)));
        assertEquals(NUM_POINTS, qt.getNumberPointsIncludingSubNodes());
        
        // Scan backup arrays,
        // verify each point is present in qt,
        // then remove.
        for (int i = 0; i < NUM_POINTS; i++) {
            GISPoint test = new GISPoint(xary[i], yary[i]);
            LinkedList<GISPoint> found = qt.getPoints(test, 0);
            switch(found.size()) {
                case 0:
                    fail("Point not found in QuadTree: "+test);
                case 1:
                    // Found one: remove it!
                    assertTrue(qt.removePoint(found.getFirst()));
                    qt.checkQT();
                    break;
                default:
                    fail("Too many points ("+found.size()+") at test location: "+test);
            }
        }
        // Verify that nothing remains.
        assertEquals(0, qt.getNumberPointsIncludingSubNodes());
        
        // One more just for fun: Remove a point from and empty QT.
        GISPointQuadTree<GISPoint> mt = new GISPointQuadTree<GISPoint>(0.0, 1.0, 1.0, 0.0, PTS_PER_NODE);
        assertFalse(mt.removePoint(new GISPoint(0.5,0.5)));
    }

    @Test
    public void testPointsBetween() {
        GISPoint center = new GISPoint(0.5, 0.5);
        GISPoint edge = new GISPoint(1, 1);
        GISPoint edge2 = new GISPoint(2, 2);
        assertEquals(NUM_POINTS, qt.pointsBetween(center, edge));
        assertEquals(0, qt.pointsBetween(edge2, edge2));
    }

    @Test
    public void testGetPoints() {
        GISPoint center = new GISPoint(0.5, 0.5);
        assertEquals(NUM_POINTS, qt.getPoints(center, 1.0).size());
        assertEquals(NUM_POINTS, qt.getPoints(center, Double.MAX_VALUE).size());
        GISPoint far = new GISPoint(2, 2);
        assertEquals(0, qt.getPoints(far, 0).size());
        assertEquals(NUM_POINTS, qt.getPoints(far, 3).size());
        assertEquals(NUM_POINTS, qt.getPoints(far, Double.MAX_VALUE).size());
    }
    
    /**
     * High memory requirement!  -Xmx512m recommended.
     * Test file is created under build/tests.
     * It is overwritten on every run, and not deleted.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSerialize() throws IOException, ClassNotFoundException {
        System.out.println("GISPointQuadTreeTest.testSerialize:");
        System.out.println("  Building ...");

        File dir = new File("build/tests");
        dir.mkdirs();
        File outputFile = FileUtil.resolve(dir, "GISPointQuadTreeTest.state");
        
        // abusively high to match actual projects (i.e. Rhode Island)
        int numpoints = 500000;
        // same as in ConflatePums*
        int pts_per_node = 10;
        
        Random rng = new Random();
        GISPointQuadTree<GISPoint> qt1 = new GISPointQuadTree<GISPoint>(0.0, 1.0, 1.0, 0.0, pts_per_node);
        GISPointQuadTree<GISPoint> qt2;
        
        for (int i = 0; i < numpoints; i++) {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            // add point to test structure
            GISPoint pt = new GISPoint(x, y);
            qt1.addPoint(pt);
        }
        qt1.checkQT();
        
        System.out.println("  Saving ...");
        
        ObjectOutputStream out = 
            new ObjectOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream(outputFile)));
        try {
            out.writeObject(qt1);
        }
        finally {
            out.close();
        }

        System.out.println("  Loading ...");

        ObjectInputStream in = 
            new ObjectInputStream(
                new BufferedInputStream(
                    new FileInputStream(outputFile)));
        try {
            qt2 = (GISPointQuadTree<GISPoint>) in.readObject();
            qt2.checkQT();
        }
        finally {
            in.close();
        }

        System.out.println("  Testing ...");

        HashSet<GISPoint> set1 = new HashSet<GISPoint>(qt1.toListByBreadth());
        HashSet<GISPoint> set2 = new HashSet<GISPoint>(qt2.toListByBreadth());
        
        assertEquals(set1, set2);
//        for (GISPoint point : set2) {
//            assertNotNull(point.getGISPointQuadTree());
//        }
        
        System.out.println("  Done.");
    }
}
