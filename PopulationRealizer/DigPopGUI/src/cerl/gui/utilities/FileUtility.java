/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author ajohnson
 */
public class FileUtility {
    
    /**
     * Creates a new text file for the given file path. 
     * The new file will contain the sent in string. 
     * @param newFilePath The new file path of the newly created file. 
     * @param newFileText Text for the new file.
     * @throws IOException 
     */
    public static void WriteNewTextFile(
            String newFilePath, 
            String newFileText)throws IOException {  
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFilePath);
            byte[] output = newFileText.getBytes();
            out.write(output);
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    /**
     * File Utility class that will parse a given XML file into a specified 
     * object.
     * 
     * Example: HelpFile.xml will be parsed into the HelpFile object. 
     * 
     * @param filePath The file path of the parsed XML file.
     * @param classType The Object type that the XML file will be parsed into.
     * @return Object loaded with the data from the given XML file.
     */
    public static Object ParseXMLFileIntoObject(String filePath, Class classType) 
    {
        try {
            File file = new File(filePath);  
            JAXBContext jaxbContext = JAXBContext.newInstance(classType);  
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
            return jaxbUnmarshaller.unmarshal(file);  
        } catch (JAXBException e) {  
            e.printStackTrace();  
            return null;
        }  
    }
}
