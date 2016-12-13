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
@XmlType(name="cluster")
public class Cluster {
    @XmlAttribute(name="id")
    private Integer id;
    
    @XmlAttribute(name="reduction")
    private Integer reduction;
    
    @XmlAttribute(name="distance")
    private Integer distance;

    public Cluster(){
    }
    
    public Cluster(Integer id, Integer reduction, Integer distance) {
        this.id = id;
        this.reduction = reduction;
        this.distance = distance;
    }

    public Integer getId() {
        return id;
    }

    public Integer getReduction() {
        return reduction;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setReduction(Integer reduction) {
        this.reduction = reduction;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "<cluster " + "id=" + id + " reduction=" + reduction + " distance=" + distance + "/>";
    }
}
