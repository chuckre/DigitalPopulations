package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import mil.army.usace.ehlschlaeger.rgik.core.GISPoint;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTree;
import mil.army.usace.ehlschlaeger.rgik.core.GISPointQuadTreeBreadthFirstIterator;

import org.junit.Before;
import org.junit.Test;

public class GISPointQuadTree_BreadthFirstIteratorTest {
    static int NUM_POINTS = 20;
    static int PTS_PER_NODE = 3;
    // Object to test.
    GISPointQuadTree<GISPoint> qt;
    // Raw copy of all points added to qt for later verification.
    Set<GISPoint> master;
    
    /**
     * Runs before each test*() method.
     * Creates a QuadTree to test plus the backup arrays for verification.
     * @throws Exception whenever it wants
     */
    @Before
    public void setUp() throws Exception {
        Random rng = new Random();
        
        qt = new GISPointQuadTree<GISPoint>(0.0, 1.0, 1.0, 0.0, PTS_PER_NODE);
        master = new HashSet<GISPoint>();
        
        for (int i = 0; i < NUM_POINTS; i++) {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            GISPoint pt = new GISPoint(x, y);
            // file away a copy for testing
            master.add(pt);
            // add point to test structure
            qt.addPoint(pt);
            qt.checkQT();
        }
    }

    @Test
    public void testNext() {
        GISPointQuadTreeBreadthFirstIterator<GISPoint> to = new GISPointQuadTreeBreadthFirstIterator<GISPoint>(qt);

        // Follow iterator,
        // verify each point in backup array,
        // then remove from backup array.
        while(true) {
            GISPoint point = to.next();
            if(point == null)
                break;
            boolean found = master.remove(point);
            assertTrue("Quad tree contains a point we didn't insert: "+point, found);
        }

        // Verify that nothing remains.
        assertTrue("Quad tree was missing points: "+master, master.isEmpty());
    }
    
    @Test
    public void testToList() {
        List<GISPoint> allpoints = GISPointQuadTreeBreadthFirstIterator.toList(qt);
        for (GISPoint point : allpoints) {
            boolean found = master.remove(point);
            assertTrue("Quad tree contains a point we didn't insert: "+point, found);
        }
        
        // Verify that nothing remains.
        assertTrue("Quad tree was missing points: "+master, master.isEmpty());
    }
}
