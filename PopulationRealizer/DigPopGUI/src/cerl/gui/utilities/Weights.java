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
 *
 * @author mrivera
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="traitWeights")
public class Weights {
    @XmlAttribute(name="id")
    private Integer id;
    
    @XmlAttribute(name="weight")
    private Double weight;

    public Weights(){
    }
    
    public Weights(Integer id, Double weight) {
        this.id = id;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "<trait " + "id=" + id + " weight=" + weight + "/>";
    }
}
