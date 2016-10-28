/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cerl.gui.standard.utilities.Screen;
import cerl.gui.standard.utilities.HelpFile;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.Result;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author ajohnson
 */
public class FileUtilityJUnitTest {
    
    public FileUtilityJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test 
    public void readInHelpFileTest()
    {
//        HelpFile helpFile = (HelpFile)FileUtility.ParseXMLFileIntoObject("C:\\Projects\\CERL GUI\\HelpFileTest2.xml", HelpFile.class);
//        ArrayList<Screen> screens = helpFile.getScreen();
//        
//        assertEquals(2, screens.size());
        
        Result result = FileUtility.getFileExtension("Testerhjhjkh.hjkhk", '.');
        
        System.out.println(result.getValue());
        
    }
    
    @Test 
    public void createTextFileFromString() throws IOException
    {
        File temp = File.createTempFile("temp-file-name", ".txt");
        String newFilePath = temp.getPath();
        System.out.println(newFilePath);
        
        System.out.println(temp.getPath());
        System.out.println(temp.getAbsoluteFile());
        System.out.println(temp.getParent());
        System.out.println(temp.getName());
        String newText = "Test Document";
        
        FileUtility.WriteNewTextFile(newFilePath, newText);
    }
    
}