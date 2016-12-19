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
 * Creates a new Cluster type object with an id, reduction, and distance
 * Used for the Position Rules in the Fitting Criteria File
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

    /**
     * Creates a new, empty cluster object
     */
    public Cluster(){
    }
    
    /**
     * Creates a new cluster object with the given parameters
     * @param id - the trait ID to be clustered. Trait must be a normal tract-based trait (i.e. one with 'regionTrait' and 'regionTotal'.)
     * @param reduction - the percentage reduction in "inertia," which is a measure of the randomness of an arrangement of households.  A reduction of inertia is an increase of clustering, and is achieved by moving households closer together. Goal will be this much lower than the "inertia" values calculated when phase 4 begins.
     * @param distance - the Maximum size of clusters to build. Only houses within this radius will be considered for clustering.  1000 meters or 0.01 degrees is a good starting point.  This values is in the same units as the input maps.
     */
    public Cluster(Integer id, Integer reduction, Integer distance) {
        this.id = id;
        this.reduction = reduction;
        this.distance = distance;
    }

    /**
     * Gets the Trait ID of the current cluster
     * @return the Trait ID that is clustered
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the percentage reduction value for the current cluster
     * @return the reduction value for the current cluster
     */
    public Integer getReduction() {
        return reduction;
    }

    /**
     * Gets the Maximum size of clusters to build
     * @return the distance of the current cluster
     */
    public Integer getDistance() {
        return distance;
    }

    /**
     * Sets the current trait ID for the cluster
     * @param id - the trait ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the reduction percentage for the current cluster
     * @param reduction - the percentage of reduction
     */
    public void setReduction(Integer reduction) {
        this.reduction = reduction;
    }

    /**
     * Sets the distance for the current cluster
     * @param distance - the maximum size of cluster to build
     */
    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    /**
     * Provides an "XML" like string for the cluster
     * Formatted as used in the Fitting Criteria File
     * @return string with the cluster, id, reduction, and distance
     */
    @Override
    public String toString() {
        return "<cluster " + "id=" + id + " reduction=" + reduction + " distance=" + distance + "/>";
    }
}
