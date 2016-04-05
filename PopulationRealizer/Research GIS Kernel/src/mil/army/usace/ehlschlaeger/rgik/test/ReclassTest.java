package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Vector;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.Reclass;

import org.junit.Test;



public class ReclassTest {

    @Test
    public void testRun() throws IOException {
        GISClass map = new GISClass(100, 100, 10, 10, 2, 2);
        map.setCellValue(0, 0, 1);
        map.setCellValue(0, 1, 2);
        map.setCellValue(1, 0, 3);
        map.setCellValue(1, 1, 4);

        String[][] testData = {
                { "col1", "1", "2", "3", "4" },
                { "col2", "5", "6", "7", "8" }
            };

        CSVTable table = new CSVTable();
        for (String[] col : testData) {
            Vector<String> vcol = new Vector<String>();
            for (int r = 1; r < col.length; r++)
                vcol.add(col[r]);
            table.addColumn(col[0], vcol);
        }

        String keyColumn = "col1";
        String newColumn = "col2";

        GISClass newmap = Reclass.reclass(map, table, keyColumn, newColumn);

        assertEquals("0,0", 5, newmap.getCellValue(0, 0));
        assertEquals("0,1", 6, newmap.getCellValue(0, 1));
        assertEquals("1,0", 7, newmap.getCellValue(1, 0));
        assertEquals("1,1", 8, newmap.getCellValue(1, 1));
    }
}
