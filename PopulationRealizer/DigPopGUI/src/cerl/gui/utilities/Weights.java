/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Creates a new Weight object to be used for weighting traits in the Fitting Criteria File
 * @author mrivera
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="traitWeights")
public class Weights {
    @XmlAttribute(name="id")
    private Integer id;
    
    @XmlAttribute(name="weight")
    private Double weight;

    /**
     * Creates a new, empty weight object
     */
    public Weights(){
    }
    
    /**
     * Creates a new weight object with the provided parameters
     * @param id - the Trait ID to be weighted
     * @param weight - the new weight of the provided trait
     */
    public Weights(Integer id, Double weight) {
        this.id = id;
        this.weight = weight;
    }

    /**
     * Gets the ID of the weighted trait
     * @return - the trait ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the weight of the current trait
     * @return - the trait weight
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the Trait ID to be weighted
     * @param id - the trait ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the weight of a trait
     * @param weight - the new trait weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * An "XML"-like string of the trait weights as used in the Fitting Criteria file
     * @return 
     */
    @Override
    public String toString() {
        return "<trait " + "id=" + id + " weight=" + weight + "/>";
    }
}
