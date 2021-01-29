/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * The wrapper element for the Fitting Criteria, <weights> tag
 * @author mrivera
 */
public class WeightWrapper {
    private double location;
    private ArrayList<Weights> weights;

    /**
     * Generates a new blank weight wrapper, with location set to the default value
     */
    public WeightWrapper() {
        location = 1.0; //default value
    }

    /**
     * Creates a new weight wrapper with the set of weights provided, and default location
     * @param weights - the weights to include in the fitting file
     */
    public WeightWrapper(ArrayList<Weights> weights) {
        this.location = 1.0;
        this.weights = weights;
    }
    
    /**
     * Generates a new weight wrapper wtih the location and set of weights provided
     * @param location - the double location value
     * @param weights - the arraylist of weights
     */
    public WeightWrapper(double location, ArrayList<Weights> weights) {
        this.location = location;
        this.weights = weights;
    }

    /**
     * Gets the location field
     * @return a double of the location, default is 1.0
     */
    @XmlAttribute(name="location")
    public double getLocation() {
        return location;
    }

    /**
     * Sets the location to the provided value
     * @param location - the value to use as the new location
     */
    public void setLocation(double location) {
        this.location = location;
    }

    /**
     * Gets the ArrayList of weights 
     * @return an ArrayList of weights
     */
    @XmlElement(name="trait")
    public ArrayList<Weights> getWeights() {
        return weights;
    }

    /**
     * Sets the weights to the provided ArrayList
     * @param weights the ArrayList of weights
     */
    public void setWeights(ArrayList<Weights> weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        return "<weights location='" + location + "'> <trait weights=" + weights + "/>";
    }    
}
