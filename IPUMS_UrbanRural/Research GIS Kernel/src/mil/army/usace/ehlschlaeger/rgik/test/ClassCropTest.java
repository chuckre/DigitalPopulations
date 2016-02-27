package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mil.army.usace.ehlschlaeger.rgik.core.ClassCrop;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;

import org.junit.Test;

public class ClassCropTest {

    @Test
    public void test1() {
        int[][] init = {
                        {1,2,3,4,5},
                        {2,3,4,5,6},
                        {3,4,5,6,7},
                        {4,5,6,7,8}
        };
        int[][] expected = {
                            {0,0,3,4,5},
                            {0,3,4,5,6},
                            {3,4,5,6,0},
                            {4,5,6,0,0}
        };
        doTest(init, 3,6, expected);
    }

    @Test
    public void test2() {
        int[][] init = {
                        {1,1,1,1,1},
                        {1,2,3,4,1},
                        {1,1,2,3,1},
                        {1,1,1,2,1}
        };
        int[][] expected = {
                            {2,3,4},
                            {0,2,3},
                            {0,0,2}
        };
        doTest(init, 2,4, expected);
    }

    @Test
    public void test3() {
        int[][] init = {
                        {2,2,2,3,3,3,4,4},
                        {2,2,2,3,3,3,4,4},
                        {1,2,2,3,3,3,4,4},
                        {1,2,5,5,5,5,5,4},
                        {1,2,5,5,5,5,5,5},
                        {7,2,2,5,5,5,6,6},
                        {7,7,7,1,6,6,6,6},
                        {1,7,1,1,6,6,6,1},
        };
        int[][] expected2 = {
                             {2,2,2},
                             {2,2,2},
                             {0,2,2},
                             {0,2,0},
                             {0,2,0},
                             {0,2,2},
        };
        doTest(init, 2,2, expected2);
        int[][] expected3 = {
                             {3,3,3},
                             {3,3,3},
                             {3,3,3},
        };
        doTest(init, 3,3, expected3);
        int[][] expected5 = {
                             {5,5,5,5,5,0},
                             {5,5,5,5,5,5},
                             {0,5,5,5,0,0},
        };
        doTest(init, 5,5, expected5);
    }

    /**
     * Perform one test with given data. expected[r][c] == 0 means cell should
     * be no-data. This means zero cannot be used in a test.
     * 
     * @param init matrix of values to crop
     * @param min minimum value to keep
     * @param max maximum value to keep
     * @param expected the matrix that ClassCrop should produce
     */
    public void doTest(int[][] init, int min, int max, int[][] expected) {
        GISClass cls = new GISClass(1,2, 1,1, init.length,init[0].length);
        for(int r = 0; r<init.length; r++) {
            for(int c = 0; c<init[0].length; c++) {
                cls.setCellValue(r, c, init[r][c]);
            }
        }
        ClassCrop cropper = new ClassCrop(min,max);
        GISClass result = cropper.crop(cls);
        
        if(expected == null)
            assertEquals(null, result);
        assertEquals(expected.length, result.getNumberRows());
        assertEquals(expected[0].length, result.getNumberColumns());
        
        for(int r = 0; r<expected.length; r++) {
            for(int c = 0; c<expected[0].length; c++) {
                if(expected[r][c] == 0)
                    assertTrue(result.isNoData(r, c));
                else {
                    assertFalse(result.isNoData(r, c));
                    assertEquals(expected[r][c], result.getCellValue(r, c));
                }
            }
        }
    }
}
