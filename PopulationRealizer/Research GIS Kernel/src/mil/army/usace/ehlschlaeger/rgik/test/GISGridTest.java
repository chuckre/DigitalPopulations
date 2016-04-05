package mil.army.usace.ehlschlaeger.rgik.test;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;

import org.junit.Test;
import static org.junit.Assert.*;



public class GISGridTest {
    @Test
    public void testLoadESRI() throws IOException {
        double EPSILON = 0.0000005;
        GISGrid grid = GISGrid.loadEsriAscii("test-files/GISGridTest");
        
        assertEquals(216237.071907, grid.getWestEdge(), EPSILON);
        assertEquals(216537.071907, grid.getEastEdge(), EPSILON);
        
        assertEquals(3971419.987776, grid.getSouthEdge(), EPSILON);
        assertEquals(3971659.987776, grid.getNorthEdge(), EPSILON);
        
        assertEquals(60., grid.getNSResolution(), EPSILON);
        assertEquals(60., grid.getEWResolution(), EPSILON);
        
        assertEquals(4, grid.getNumberRows());
        assertEquals(5, grid.getNumberColumns());

/* test for segmentGrid()
        System.out.println( "New Grid. w: 1000.0, n: 1000.0, ew_res: 1.0, nsRes: 1.0, rows: 500, cols: 500");
        GISGrid newGrid = new GISGrid( 1000.0, 1000.0, 1.0, 1.0, 500, 500);
        System.out.println( " 100x100 cell Segments:");
        GISGrid[] seg = newGrid.segmentGrid( 100);
        for( int i = 0; i < seg.length; i++) {
            System.out.println( i);
            seg[ i].printGrid();
        }
        System.out.println( " 100x100 cell Segments with 10 cell overlap:");
        seg = newGrid.segmentGrid( 100, 100, 10);
        for( int i = 0; i < seg.length; i++) {
            System.out.println( i);
            seg[ i].printGrid();
        }
end of segmentGrid() test */
    }
}
