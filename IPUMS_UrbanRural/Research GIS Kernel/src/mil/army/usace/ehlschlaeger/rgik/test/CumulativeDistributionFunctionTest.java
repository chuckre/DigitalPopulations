package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.assertEquals;
import mil.army.usace.ehlschlaeger.rgik.core.CumulativeDistributionFunction;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

import org.junit.Before;
import org.junit.Test;

public class CumulativeDistributionFunctionTest {

    protected CumulativeDistributionFunction cdf;

    /**
     * Build CDF with varying weights so it's obvious which one fails.
     */
    @Before
    public void setUp() {
        GISLattice lattice = new GISLattice(1,2, 1,1, 3,3);
        double[][] values = {
                             {0, 2, 4},
                             {5, 6, 7},
                             {0, 8, 0},
        };
        for(int r = 0; r<values.length; r++) {
            for(int c = 0; c<values[0].length; c++) {
                lattice.setCellValue(r, c, values[r][c]);
            }
        }
        
        cdf = CumulativeDistributionFunction.createNormalized(lattice);
    }

    /**
     * Verify CDF was created properly.
     */
    @Test
    public void testCreateNormalized() {
        double[][] values = {
                             {0.0,     0.0625,  0.1875},
                             {0.34375, 0.53125, 0.75  },
                             {0.75,    1.0,     1.0   },
        };
        for(int r = 0; r<values.length; r++) {
            for(int c = 0; c<values[0].length; c++) {
                assertEquals(values[r][c], cdf.getCellValue(r, c), 0);
            }
        }
    }

    @Test
    public void testGetGridCellID() {
        // easy cases
        assertEquals(1, cdf.getGridCellID(0.04));
        assertEquals(2, cdf.getGridCellID(0.10));
        assertEquals(3, cdf.getGridCellID(0.20));
        assertEquals(4, cdf.getGridCellID(0.34375));
        assertEquals(4, cdf.getGridCellID(0.50));
        assertEquals(5, cdf.getGridCellID(0.60));
        assertEquals(7, cdf.getGridCellID(0.80));
        // cases around 0
        //  - cells with zero probability should never be produced.
        assertEquals(1, cdf.getGridCellID(0.0));
        assertEquals(5, cdf.getGridCellID(0.7499999999999999));
        assertEquals(7, cdf.getGridCellID(0.75));
        assertEquals(7, cdf.getGridCellID(0.7500000000000001));
    }
}
