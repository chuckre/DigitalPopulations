/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used for the Land Use Combination Table Model in Step 2
 * @author ajohnson
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="combination")
public class LandUseMapClassCombination {
    @XmlAttribute(name="classes")
    private ArrayList<String> classes;
    
    @XmlAttribute(name="target")
    private String target;
    
    @XmlAttribute(name="desc")
    private String classCombinationDescription;

    /**
     * Creates a new empty land use map combination class
     */
    public LandUseMapClassCombination() {
        this.classes = new ArrayList<>();
    }

    /**
     * Creates a new land use map class combination 
     * @param classes - the list of classes to include
     * @param target - the target string
     * @param classCombinationDescription - the description of the combination class
     */
    public LandUseMapClassCombination(ArrayList<String> classes, String target, String classCombinationDescription) {
        this.classes = classes;
        this.target = target;
        this.classCombinationDescription = classCombinationDescription;
    }

    /**
     * Gets the list of classes
     * @return 
     */
    public ArrayList<String> getClasses() {
        return classes;
    }

    /**
     * Sets the list of classes
     * @param classes - new list of classes
     */
    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    /**
     * Gets the target value
     * @return 
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target value
     * @param target - the new target value
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Gets the description of the class combination
     * @return - the description
     */
    public String getClassCombinationDescription() {
        return classCombinationDescription;
    }

    /**
     * Sets the description for the current class combination
     * @param classCombinationDescription - the new description
     */
    public void setClassCombinationDescription(String classCombinationDescription) {
        this.classCombinationDescription = classCombinationDescription;
    }
}
