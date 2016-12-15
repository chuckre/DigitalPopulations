/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Used in the Goal Relationship File - landuse vacant classes
 * @author mrivera
 */
public class VacantClass {
    private String Description;
    private String Classes;

    public VacantClass(){}
    
    public VacantClass(String Description, String Classes) {
        this.Description = Description;
        this.Classes = Classes;
    }

    @XmlAttribute(name="desc")
    public String getDescription() {
        return Description;
    }

    @XmlAttribute(name="classes")
    public String getClasses() {
        return Classes;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setClasses(String Classes) {
        this.Classes = Classes;
    }
}
