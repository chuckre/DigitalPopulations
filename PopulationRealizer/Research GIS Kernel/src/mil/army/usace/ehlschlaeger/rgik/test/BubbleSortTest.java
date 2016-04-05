package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.*;

import mil.army.usace.ehlschlaeger.rgik.util.BubbleSort;

import org.junit.Test;

public class BubbleSortTest {

    @Test
    public void testSort() {
        BubbleSort bs = new BubbleSort( false);
        int[] old = new int[5];
        double[] v = new double[5];
        v[0] = 0.1;
        v[1] = 0.5;
        v[2] = 0.4;
        v[3] = 0.4;
        v[4] = 0.2;
        bs.sort( old, v);
        
        int[] expected = {0,4,2,3,1};
        assertArrayEquals(expected, old);
        
        bs = new BubbleSort( true);
        bs.sort( old, v);
        int[] trueExpected = { 1, 2, 3, 4, 0};

        assertArrayEquals( trueExpected, old);
    }
}
