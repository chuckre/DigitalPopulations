package mil.army.usace.ehlschlaeger.rgik.test;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

import org.junit.Assert;



public class TestTools {
    public static final double EPSILON = 0.0000005;

    public static GISLattice newLattice(double[][] values) {
        GISLattice lattice = new GISLattice(1, 2, 1, 1, values.length,
                                            values[0].length);
        setValues(lattice, values);
        return lattice;
    }

    public static GISLattice newLattice(double westEdge, double northEdge, double[][] values) {
        GISLattice lattice = new GISLattice(westEdge, northEdge, 1, 1, values.length,
                                            values[0].length);
        setValues(lattice, values);
        return lattice;
    }

    /**
     * Copy a simple double array into a GISLattice. row[0] is at the
     * <b>north</b> edge of the grid.
     * 
     * @param lattice
     *            container to receive values
     * @param values
     *            array of values to copy into lattice. Also zero values will be
     *            set as no-data.
     */
    public static void setValues(GISLattice lattice, double[][] values) {
        for (int r = 0; r < values.length; r++) {
            for (int c = 0; c < values[0].length; c++) {
                if(values[r][c] == 0.0)
                    lattice.setNoData(r, c, true);
                else
                    lattice.setCellValue(r, c, values[r][c]);
            }
        }
    }

    /**
     * Test the contents of a GISLattice.
     * 
     * @param expected
     *            array of values that should appear in the lattice. row[0] is
     *            at the <b>north</b> edge of the grid.  Zeroes imply no-data.
     * @param actual
     *            lattice containing values to test
     */
    public static void assertEquals(double[][] expected, GISLattice actual) {
        if (expected == null)
            Assert.assertEquals(null, actual);
        Assert.assertEquals(expected.length, actual.getNumberRows());
        Assert.assertEquals(expected[0].length, actual.getNumberColumns());

        for (int row = 0; row < actual.getNumberRows(); row++)
            for (int col = 0; col < actual.getNumberColumns(); col++) {
                double e = expected[row][col];
                double a = actual.getCellValue(row, col);
                if(e == 0)
                    Assert.assertTrue("row " + row + ", col " + col, actual.isNoData(row, col));
                else {
                    Assert.assertFalse("row " + row + ", col " + col, actual.isNoData(row, col));
                    Assert.assertEquals("row " + row + ", col " + col,
                                        e, a, EPSILON);
                }
            }
    }
    
    public static void dump(GISLattice lattice) {
        for (int row = 0; row < lattice.getNumberRows(); row++) {
            for (int col = 0; col < lattice.getNumberColumns(); col++) {
                if(lattice.isNoData(row, col))
                    System.out.print("nd, ");
                else
                    System.out.print(lattice.getCellValue(row, col)+", ");
            }
            System.out.println();
        }
    }
}
