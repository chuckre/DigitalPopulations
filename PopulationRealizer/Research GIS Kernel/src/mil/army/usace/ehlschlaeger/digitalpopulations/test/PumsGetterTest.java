package mil.army.usace.ehlschlaeger.digitalpopulations.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.DataPreparer;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.PumsTotalGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.PumsTraitGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.FileRelationship;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.TransformAttributes2double;

import org.junit.Before;
import org.junit.Test;



/**
 * Test PumsTraitGetter and PumsTotalGetter classes.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PumsGetterTest {
    protected static FileRelationship rel;

    protected CSVTableNoSwing                households;
    protected CSVTableNoSwing                population;
    protected List<PumsHousehold>     pumsHouses;
    protected ArrayList<PumsTrait>        traits;

    /**
     * Load our test files.
     * 
     * @throws Exception
     *             on any file error
     */
    @Before
    public void setUp() throws Exception {
        File file = new File("test-files/miniproj/relationship.dprxml");
        rel = FileRelationship.loadFile(file);
        DataPreparer dp = new DataPreparer(rel, file.getParentFile());
        
        pumsHouses = dp.loadHouseholds();
        
        households = dp.getHouseholdSchema();
        population = dp.getPopulationSchema();
    }

    /**
     * Test PumsTraitGetter and PumsTotalGetter for each trait in the
     * relationship file.  See miniproj\relationship.dprxml for how
     * to write tests for this function.
     */
    @Test
    public void testMake() {
        TransformAttributes2double numer;
        TransformAttributes2double denom;

        // Test every trait
        for (Trait t : rel.traits) {
            PumsTrait trait = (PumsTrait)t;  //crash if not PumsTrait; I don't have a test for the other types yet.
            // Parse coded ID attribute.
            String[] codes = trait.id.split("=");
            
            //String code = codes[0]; --unused

            int expectedNumer = Integer.parseInt(codes[1]);

            int expectedDenom = 0;
            boolean expectedDenomNull;
            try {
                expectedDenom = Integer.parseInt(codes[2]);
                expectedDenomNull = false;
            } catch (NumberFormatException e) {
                expectedDenomNull = true;
            }

            // NOTE:
            // We only test the first household.  Other records are for other tests.
            
            // Test trait (numerator) getter
            numer = new PumsTraitGetter(trait, households, population);
            assertEquals(trait.id + " trait", expectedNumer,
                         numer.getDouble(pumsHouses.get(0)), 0);

            // Test total (denominator) getter
            denom = PumsTotalGetter.make(trait, households, population);
            if (denom == null)
                assertTrue(trait.id + " total", expectedDenomNull);
            else {
                assertFalse(trait.id + " total", expectedDenomNull);
                assertEquals(trait.id + " total", expectedDenom,
                             denom.getDouble(pumsHouses.get(0)), 0);
            }
        }
    }
}
