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
@XmlType(name="trait")
public class Traits {
    @XmlAttribute(name="id")
    private Integer id;
    
    @XmlAttribute(name="regionTrait")
    private String regionTrait;
    
    @XmlAttribute(name="regionTotal")
    private String regionTotal;
    
    @XmlAttribute(name="pumsTraitTable")
    private String pumsTraitTable;
    
    @XmlAttribute(name="pumsTraitField")
    private String pumsTraitField;
    
    @XmlAttribute(name="pumsTraitSelect")
    private String pumsTraitSelect;
    
    @XmlAttribute(name="pumsTotalTable")
    private String pumsTotalTable;
    
    @XmlAttribute(name="pumsTotalField")
    private Integer pumsTotalField;
    
    @XmlAttribute(name="desc")
    private String desc;
    
    public Traits(){
    }
    
    public Traits(int id, String regionTrait, String regionTotal, String pumsTraitTable, String pumsTraitField, String pumsTraitSelect, String pumsTotalTable, int pumsTotalField, String desc) {
        this.id = id;
        this.regionTrait = regionTrait;
        this.regionTotal = regionTotal;
        this.pumsTraitTable = pumsTraitTable;
        this.pumsTraitField = pumsTraitField;
        this.pumsTraitSelect = pumsTraitSelect;
        this.pumsTotalTable = pumsTotalTable;
        this.pumsTotalField = pumsTotalField;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getRegionTrait() {
        return regionTrait;
    }

    public String getRegionTotal() {
        return regionTotal;
    }

    public String getPumsTraitTable() {
        return pumsTraitTable;
    }

    public String getPumsTraitField() {
        return pumsTraitField;
    }

    public String getPumsTraitSelect() {
        return pumsTraitSelect;
    }

    public String getPumsTotalTable() {
        return pumsTotalTable;
    }

    public int getPumsTotalField() {
        return pumsTotalField;
    }

    public String getDesc() {
        return desc;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRegionTrait(String regionTrait) {
        this.regionTrait = regionTrait;
    }

    public void setRegionTotal(String regionTotal) {
        this.regionTotal = regionTotal;
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

    public void setPumsTotalTable(String pumsTotalTable) {
        this.pumsTotalTable = pumsTotalTable;
    }

    public void setPumsTotalField(int pumsTotalField) {
        this.pumsTotalField = pumsTotalField;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return "<traits " + "id=" + id 
                + " regionTrait=" + regionTrait 
                + " regionTotal=" + regionTotal 
                + " pumsTraitTable=" + pumsTraitTable 
                + " pumsTraitField=" + pumsTraitField 
                + " pumsTraitSelect=" + pumsTraitSelect 
                + " pumsTotalTable=" + pumsTotalTable 
                + " pumsTotalField=" + pumsTotalField 
                + " desc=" + desc + "/>";
    }
}
