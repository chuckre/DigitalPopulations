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
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ajohnson
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="combination")
public class LandUseMapClassCombination {
    @XmlAttribute(name="classes")
    private ArrayList<String> classes;
    
    @XmlAttribute(name="target")
    private String target;
    
    @XmlAttribute(name="desc")
    private String classCombinationDescription;

    public LandUseMapClassCombination() {
        this.classes = new ArrayList<>();
    }

    public LandUseMapClassCombination(ArrayList<String> classes, String target, String classCombinationDescription) {
        this.classes = classes;
        this.target = target;
        this.classCombinationDescription = classCombinationDescription;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getClassCombinationDescription() {
        return classCombinationDescription;
    }

    public void setClassCombinationDescription(String classCombinationDescription) {
        this.classCombinationDescription = classCombinationDescription;
    }
}
