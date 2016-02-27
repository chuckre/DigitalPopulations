package mil.army.usace.ehlschlaeger.rgik.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.WoodTopographicVariables;

import org.junit.Test;



public class WoodTopographicVariablesTest {
    GISLattice dem;
    WoodTopographicVariables wtv;
    
    public WoodTopographicVariablesTest() throws IOException {
        dem = GISLattice.loadEsriAscii("test-files/TestDEM");
        wtv = new WoodTopographicVariables( dem, 100.0, 1.0);
    }

    @Test
    public void testWoodTopographicVariables() {
        assertEquals(70, dem.getNumberColumns());
        assertEquals(94, dem.getNumberRows());
        fail("incomplete");
    }

    @Test
    public void testGetSlopeMap() {
        //GISLattice s = wtv.getSlopeMap();
        //s.writeAsciiEsri( "TestDEMslope");
        fail("incomplete");
    }

    @Test
    public void testGetAspectMap() {
        //GISLattice a = wtv.getAspectMap();
        //a.writeAsciiEsri( "TestDEMaspect");
        fail("incomplete");
    }

    @Test
    public void testGetProfileCurvatureMap() {
        //GISLattice pr = wtv.getProfileCurvatureMap();
        //pr.writeAsciiEsri( "TestDEMproCurve100");
        fail("incomplete");
    }

    @Test
    public void testGetPlanCurvatureMap() {
        //GISLattice pl = wtv.getPlanCurvatureMap();
        //pl.writeAsciiEsri( "TestDEMplanCurve");
        fail("incomplete");
    }
}
