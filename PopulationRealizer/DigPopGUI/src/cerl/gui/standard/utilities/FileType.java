/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 *
 * @author ajohnson
 */
public enum FileType {
    CVS("CSV"),
    ACS("ACS"),
    PRJ("PRJ");
    private String fileTypeName = ""; 
    private FileType(String name)
    {
        this.fileTypeName = name;
    }

    @Override
    public String toString() {
        return fileTypeName;
    }
}