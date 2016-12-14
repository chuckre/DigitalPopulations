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

    public Forbid(){}
    
    public Forbid(String id, String desc, String map, String mapSelect, String pumsTraitTable, String pumsTraitField, String pumsTraitSelect) {
        this.id = id;
        this.desc = desc;
        this.map = map;
        this.mapSelect = mapSelect;
        this.pumsTraitTable = pumsTraitTable;
        this.pumsTraitField = pumsTraitField;
        this.pumsTraitSelect = pumsTraitSelect;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    @XmlAttribute(name="desc")
    public String getDesc() {
        return desc;
    }

    @XmlAttribute(name="map")
    public String getMap() {
        return map;
    }

    @XmlAttribute(name="mapSelect")
    public String getMapSelect() {
        return mapSelect;
    }

    @XmlAttribute(name="pumsTraitTable")
    public String getPumsTraitTable() {
        return pumsTraitTable;
    }

    @XmlAttribute(name="pumsTraitField")
    public String getPumsTraitField() {
        return pumsTraitField;
    }

    @XmlAttribute(name="pumsTraitSelect")
    public String getPumsTraitSelect() {
        return pumsTraitSelect;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setMapSelect(String mapSelect) {
        this.mapSelect = mapSelect;
    }

    public void setPumsTraitTable(String pumsTraitTable) {
        this.pumsTraitTable = pumsTraitTable;
    }

    public void setPumsTraitField(String pumsTraitField) {
        this.pumsTraitField = pumsTraitField;
    }

    public void setPumsTraitSelect(String pumsTraitSelect) {
        this.pumsTraitSelect = pumsTraitSelect;
    }
}
