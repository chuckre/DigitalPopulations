package mil.army.usace.ehlschlaeger.digitalpopulations.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.FormatGetter;

import org.junit.Test;

public class FormatGetterTest {
    List<String> schema = Arrays.asList("a","b","c","d","e");
    String[] row1 = {"1","2","3","4","5"};
    List<String> row2 = Arrays.asList("11","12","13","14","15");

    @Test
    public void test1() {
        FormatGetter g = new FormatGetter("abc%{d}def%{a}", schema);
        assertEquals("abc4def1", g.get(row1));
        assertEquals("abc14def11", g.get(row2));
    }
    
    @Test
    public void test2() {
        FormatGetter g = new FormatGetter("%{b}abc%{c}def", schema);
        assertEquals("2abc3def", g.get(row1));
    }
    
    @Test
    public void test3() {
        FormatGetter g = new FormatGetter("plain text", schema);
        assertEquals("plain text", g.get(row1));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test4() {
        new FormatGetter("%{bad}", schema);
    }
}
