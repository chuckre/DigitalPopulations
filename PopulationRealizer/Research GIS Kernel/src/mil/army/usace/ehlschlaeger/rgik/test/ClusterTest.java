package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.*;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.Cluster;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISClassReadRowByRow;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.WoodTopographicVariables;

import org.junit.Test;

public class ClusterTest {
    @Test
    public void testGetCluster() throws IOException {
        GISLattice dem = GISLattice.loadEsriAscii("test-files/TestDEM");
        WoodTopographicVariables wtv;
        
        assertEquals(70, dem.getNumberColumns());
        assertEquals(94, dem.getNumberRows());

        wtv = new WoodTopographicVariables( dem, 100.0, 1.0);
        GISLattice pr = wtv.getProfileCurvatureMap();
        wtv = new WoodTopographicVariables( dem, 300.0, 1.0);
        GISLattice pr2 = wtv.getPlanCurvatureMap();
        wtv = new WoodTopographicVariables( dem, 150.0, 1.0);
        GISLattice pr3 = wtv.getPlanCurvatureMap();
        wtv = new WoodTopographicVariables( dem, 70.0, 1.0);
        GISLattice pr4 = wtv.getPlanCurvatureMap();
        wtv = new WoodTopographicVariables( dem, 45.0, 1.0);
        GISLattice pr5 = wtv.getPlanCurvatureMap();
        
        GISLattice[] maps2do = new GISLattice[ 5];
        maps2do[ 0] = pr;
        maps2do[ 1] = pr2;
        maps2do[ 2] = pr3;
        maps2do[ 3] = pr4;
        maps2do[ 4] = pr5;
        
        Cluster c = new Cluster( maps2do);
        GISClass clusters = c.getCluster( 3);
        
        // Use this to generate file to test against, if you trust it:
        //clusters.writeAsciiEsri( "TestDEMclusters");

        // quick probe of result
        assertEquals(70, clusters.getNumberColumns());
        assertEquals(94, clusters.getNumberRows());
        assertTrue(clusters.isNoData(0, 0));
        assertEquals(0, clusters.getCellValue(10, 10));
        assertEquals(1, clusters.getCellValue(13, 10));
        assertEquals(2, clusters.getCellValue(10, 11));
        
        // test entire object
        GISClass expectedClusters = new GISClassReadRowByRow("test-files/TestDEMclusters");
        assertTrue(clusters.equalsClass(expectedClusters));
    }
}
