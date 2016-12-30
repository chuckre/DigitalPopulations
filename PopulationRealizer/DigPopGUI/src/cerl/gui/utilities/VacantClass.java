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

    /**
     * Creates a new, empty vacant class object
     */
    public VacantClass(){}
    
    /**
     * Creates a new Vacant class object with the provided details
     * @param Description - the description of the class
     * @param Classes - the classes that to be grouped as vacant
     */
    public VacantClass(String Description, String Classes) {
        this.Description = Description;
        this.Classes = Classes;
    }

    /**
     * Gets the description of the vacant classes
     * @return - the user provided description
     */
    @XmlAttribute(name="desc")
    public String getDescription() {
        return Description;
    }

    /**
     * Gets the classes included in the vacant class group
     * @return the vacant classes
     */
    @XmlAttribute(name="classes")
    public String getClasses() {
        return Classes;
    }

    /**
     * Sets the user provided description of the vacant class
     * @param Description - the new description
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * Sets the classes value in the vacant class object
     * @param Classes - the included classes
     */
    public void setClasses(String Classes) {
        this.Classes = Classes;
    }
}
