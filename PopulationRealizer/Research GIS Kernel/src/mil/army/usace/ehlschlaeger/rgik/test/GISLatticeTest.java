package mil.army.usace.ehlschlaeger.rgik.test;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

import org.junit.Assert;
import org.junit.Test;



public class GISLatticeTest {
    static final double EPSILON = TestTools.EPSILON;

    @Test
    public void testLoadESRIFile() throws IOException {
        GISLattice lattice = GISLattice.loadEsriAscii("test-files/GISGridTest");

        Assert.assertEquals(216237.071907, lattice.getWestEdge(), EPSILON);
        Assert.assertEquals(216537.071907, lattice.getEastEdge(), EPSILON);
        
        Assert.assertEquals(3971419.987776, lattice.getSouthEdge(), EPSILON);
        Assert.assertEquals(3971659.987776, lattice.getNorthEdge(), EPSILON);
        
        Assert.assertEquals(60., lattice.getNSResolution(), EPSILON);
        Assert.assertEquals(60., lattice.getEWResolution(), EPSILON);

        Assert.assertEquals(4, lattice.getNumberRows());
        Assert.assertEquals(5, lattice.getNumberColumns());
        
        double[][] expected = {
               {0, 0, 0, 0, 0},
               {3, 3, 3, 0, 0},
               {1, 3, 3, 1, 0},
               {6, 6, 6, 4, 0},
        };

        TestTools.assertEquals(expected, lattice);
    }
    
    @Test
    public void testResampleDataToGrid1() {
        double[][] init = {
                           {0,0,0,0,0},
                           {0,2,2,2,0},
                           {0,1,1,1,0},
                           {0,2,2,2,0},
                           {0,0,0,0,0}
        };
        double[][] expected = {
                               {0,0,0},
                               {0,1,0},
                               {0,0,0}
        };
        GISLattice lattice = TestTools.newLattice(init);
        GISLattice newlat = new GISLattice(1,2, 1,1, 3,3);
        newlat.resampleDataToGrid(lattice);
        
        TestTools.assertEquals(expected, newlat);
    }
    
    @Test
    public void testResampleDataToGrid2() {
        double[][] init = {
                        {2,2,2,3,3,3,4,4},
                        {2,2,2,3,3,3,4,4},
                        {1,2,2,3,3,3,4,4},
                        {1,2,5,5,5,5,5,4},
                        {1,2,5,5,5,5,5,5},
                        {7,2,2,5,5,5,6,6},
                        {7,7,7,1,6,6,6,6},
                        {1,7,1,1,6,6,6,1},
        };
        double[][] expected = {
                               {5,5,5,5,5,4},
                               {5,5,5,5,5,5},
                               {2,5,5,5,6,6}
        };

        GISLattice lattice = new GISLattice(1,2, 1,1, init.length, init[0].length);
        TestTools.setValues(lattice, init);
        GISLattice newlat = new GISLattice(3,-1, 1,1, 3,6);
        newlat.resampleDataToGrid(lattice);
        TestTools.assertEquals(expected, newlat);
    }    
    
    @Test
    public void testExtract1() {
        double[][] init = {
                           {2,2,2,3,3,3,4,4},
                           {2,2,2,3,3,3,4,4},
                           {1,2,2,3,3,3,4,4},
                           {1,2,5,5,5,5,5,4},
                           {1,2,5,5,5,5,5,5},
                           {7,2,2,5,5,5,6,6},
                           {7,7,7,1,6,6,6,6},
                           {1,7,1,1,6,6,6,1},
        };
        double[][] expected1 = {
                                {5,5,5,5,5,4},
                                {5,5,5,5,5,5},
                                {2,5,5,5,6,6}
        };

        GISLattice lattice = new GISLattice(1,2, 1,1, init.length, init[0].length);
        TestTools.setValues(lattice, init);
        
        GISGrid range1 = new GISGrid(3,-1, 1,1, expected1.length, expected1[0].length);
        GISLattice newlat1 = lattice.extract(range1);
        TestTools.assertEquals(expected1, newlat1);

        GISGrid range2 = new GISGrid(1,2, 1,1, init.length, init[0].length);
        GISLattice newlat2 = lattice.extract(range2);
        TestTools.assertEquals(init, newlat2);
    }

    @Test(expected=DataException.class)
    public void testExtract2() {
        double[][] init = {
                           {2,2,2,3,3,3,4,4},
                           {2,2,2,3,3,3,4,4},
                           {1,2,2,3,3,3,4,4},
                           {1,2,5,5,5,5,5,4},
                           {1,2,5,5,5,5,5,5},
                           {7,2,2,5,5,5,6,6},
                           {7,7,7,1,6,6,6,6},
                           {1,7,1,1,6,6,6,1},
        };

        GISLattice lattice = new GISLattice(1,2, 1,1, init.length, init[0].length);
        TestTools.setValues(lattice, init);

        GISGrid range1 = new GISGrid(3,-1, 2,2, 1,1);
        lattice.extract(range1);
        // crash: bad resolution
    }

    @Test
    public void testExtractWhere1() {
        double[][] init = {
                           {2,2,2,3,3,3,4,4},
                           {2,2,2,3,3,3,4,4},
                           {1,2,2,3,3,3,4,4},
                           {1,2,5,5,5,5,5,4},
                           {1,2,5,5,5,5,5,5},
                           {7,2,2,5,5,5,6,6},
                           {7,7,7,1,6,6,6,6},
                           {1,7,1,1,6,6,6,1},
        };
        double[][] where1 = {
                             {1,1,1,1,1,0},
                             {1,1,1,1,1,1},
                             {0,1,1,1,0,0}
        };
        double[][] expected1 = {
                                {5,5,5,5,5,0},
                                {5,5,5,5,5,5},
                                {0,5,5,5,0,0}
        };

        GISLattice lattice = TestTools.newLattice(1,2, init);
        
        GISLattice latWhere1 = TestTools.newLattice(3,-1, where1);
        GISGrid range1 = new GISGrid(3,-1, 1,1, expected1.length, expected1[0].length);
        GISLattice newlat1 = lattice.extractWhere(range1, latWhere1);
        TestTools.assertEquals(expected1, newlat1);
    }
}
