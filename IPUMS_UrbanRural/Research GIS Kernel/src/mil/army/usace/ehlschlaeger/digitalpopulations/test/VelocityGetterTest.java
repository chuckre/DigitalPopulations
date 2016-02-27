package mil.army.usace.ehlschlaeger.digitalpopulations.test;

import static org.junit.Assert.assertEquals;

import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.VelocityGetter;

import org.junit.Test;

public class VelocityGetterTest {
    @Test
    public void testMakeSafeID() {
        assertEquals("_data_column_", VelocityGetter.makeSafeID("$data$_$column$"));
        assertEquals("_you", VelocityGetter.makeSafeID("#@$% you"));
    }
}
