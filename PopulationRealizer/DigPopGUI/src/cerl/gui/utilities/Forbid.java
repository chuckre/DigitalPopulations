/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Used in the Goal Relationship File - forbid tag
 * @author mrivera
 */
public class Forbid {
    private String id;
    private String desc;
    private String map;
    private String mapSelect;
    private String pumsTraitTable;
    private String pumsTraitField;
    private String pumsTraitSelect;

    /**
     * Creates a new, empty, Forbid object
     */
    public Forbid(){}
    
    /**
     * Creates a new Forbid object
     * @param id - Unique identifier that is used in the fitting criteria file
     * @param desc - description for user's reference.
     * @param map - Raster map that provides cell attributes.  Value is a
     *              path and name of an ESRI ASCII file, relative to this
     *              fitting criteria file
     * @param mapSelect - Map cell values to match.  Selected households will NOT
     *                      be placed in any cells where given map has this value.
     *                      Format is a comma-separated list of numbers and ranges
     * @param pumsTraitTable - Which type of object we're analyzing: HOUSEHOLDS or POPULATION.t
     * @param pumsTraitField - Which field contains trait data for each object.
     * @param pumsTraitSelect - Only households where pumsTraitField has these
     *                          values will be constrained. Format is a comma-
     *                          separated list of numbers and ranges
     */
    public Forbid(String id, String desc, String map, String mapSelect, String pumsTraitTable, String pumsTraitField, String pumsTraitSelect) {
        this.id = id;
        this.desc = desc;
        this.map = map;
        this.mapSelect = mapSelect;
        this.pumsTraitTable = pumsTraitTable;
        this.pumsTraitField = pumsTraitField;
        this.pumsTraitSelect = pumsTraitSelect;
    }

    /**
     * Get the id String
     * @return String id
     */
    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    /**
     * Get the desc String
     * @return String desc
     */
    @XmlAttribute(name="desc")
    public String getDesc() {
        return desc;
    }

    /**
     * Get the map String
     * @return String map
     */
    @XmlAttribute(name="map")
    public String getMap() {
        return map;
    }

    /**
     * Get the mapSelect String
     * @return String mapSelect
     */
    @XmlAttribute(name="mapSelect")
    public String getMapSelect() {
        return mapSelect;
    }

    /**
     * Get the pumsTraitTable String
     * @return String pumsTraitTable
     */
    @XmlAttribute(name="pumsTraitTable")
    public String getPumsTraitTable() {
        return pumsTraitTable;
    }

    /**
     * Get the pumsTraitField String
     * @return String pumsTraitField 
     */
    @XmlAttribute(name="pumsTraitField")
    public String getPumsTraitField() {
        return pumsTraitField;
    }

    /**
     * Get the pumsTraitSelect String
     * @return String pumsTraitSelect 
     */
    @XmlAttribute(name="pumsTraitSelect")
    public String getPumsTraitSelect() {
        return pumsTraitSelect;
    }

    /**
     * Set the id String
     * @param id String id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the desc String
     * @param desc String desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Set the map String
     * @param map String map
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Set the mapSelect String
     * @param mapSelect String mapSelect
     */
    public void setMapSelect(String mapSelect) {
        this.mapSelect = mapSelect;
    }

    /**
     * Set the pumsTraitTable String
     * @param pumsTraitTable String pumsTraitTable
     */
    public void setPumsTraitTable(String pumsTraitTable) {
        this.pumsTraitTable = pumsTraitTable;
    }

    /**
     * Set the pumsTraitField String
     * @param pumsTraitField String pumsTraitField
     */
    public void setPumsTraitField(String pumsTraitField) {
        this.pumsTraitField = pumsTraitField;
    }

    /**
     * Set the pumsTraitSelect String
     * @param pumsTraitSelect String pumsTraitSelect
     */
    public void setPumsTraitSelect(String pumsTraitSelect) {
        this.pumsTraitSelect = pumsTraitSelect;
    }
}
