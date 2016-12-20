/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 * The Valid File Types allowed throughout the DigPop GUI
 * @author ajohnson
 */
public enum FileType {
    CVS("CSV"),
    ASC("ASC"),
    PRJ("PRJ"),
    TXT("TXT"),
    XML("XML");
    
    private String fileTypeName = ""; 
    
    /**
     * Creates a new FileType object with a name
     * @param name - String value of the name
     */
    private FileType(String name)
    {
        this.fileTypeName = name;
    }

    @Override
    public String toString() {
        return fileTypeName;
    }
}