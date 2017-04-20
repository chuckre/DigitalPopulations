/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author mrivera
 */
public class PopulationDensityMap {
    private String map;

    public PopulationDensityMap() {
        this.map = null;
    }
    
    public PopulationDensityMap(String map) {
        this.map = map;
    }

    @XmlAttribute(name="map")
    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
