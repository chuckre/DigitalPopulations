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
 * The object used for the Trait value in the Goal Relationship and Fitting Criteria XML files
 * Additional information can be found at: http://digitalpopulations.pbworks.com/w/page/26621761/schema%3Adprxml
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
    
    /**
     * Creates a new, empty Trait object
     */
    public Traits(){
    }
    
    /**
     * Creates a new Trait object with the provided parameters
     * @param id - the unique identifier used in the fitting criteria file
     * @param regionTrait - the field in the region table that gives the number of items in the region with the trait. 
     * @param regionTotal - the field in the region table that gives the total number of candidates for trait. Use "1" if already in the target population
     * @param pumsTraitTable - the type we're analyzing, "HOUSEHOLDS" or "POPULATION"
     * @param pumsTraitField - the field that contains the trait data for each object
     * @param pumsTraitSelect - Used in "flag" mode
     * @param pumsTotalTable - The type of object to draw totals from, "HOUSEHOLDS" or "POPULATION"
     * @param pumsTotalField - the field that contains the total values for each object
     * @param desc - the description of the trait for the user's reference
     */
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

    /**
     * Gets the unique ID of the trait
     * @return the unique ID of the trait
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the field in the region table that gives the number of items in the region with the trait
     * @return the Region Trait
     */
    public String getRegionTrait() {
        return regionTrait;
    }

    /**
     * Gets the fields in the region table that gives the total number of candidates
     * @return the string Region Total
     */
    public String getRegionTotal() {
        return regionTotal;
    }

    /**
     * The type of object being analyzed
     * @return "HOUSEHOLDS" or "POPULATION"
     */
    public String getPumsTraitTable() {
        return pumsTraitTable;
    }

    /**
     * The field that contains the trait data for each object
     * @return the field with the trait data
     */
    public String getPumsTraitField() {
        return pumsTraitField;
    }

    /**
     * Switches to "Flag" mode
     * @return 
     */
    public String getPumsTraitSelect() {
        return pumsTraitSelect;
    }

    /**
     * Gets the type of object to draw totals from
     * @return "HOUSEHOLDS" or "POPULATION"
     */
    public String getPumsTotalTable() {
        return pumsTotalTable;
    }

    /**
     * Gets the field that contains the total values for each object
     * @return the field with the total values
     */
    public int getPumsTotalField() {
        return pumsTotalField;
    }

    /**
     * Gets the user defined description
     * @return the description of the Trait
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the unique ID for the trait
     * @param id - the new unique ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the field in the region table that gives the number of items in region with trait
     * @param regionTrait - the new field
     */
    public void setRegionTrait(String regionTrait) {
        this.regionTrait = regionTrait;
    }

    /**
     * Sets the field in the region table that gives the total number of candidates for trait
     * @param regionTotal - the new field
     */
    public void setRegionTotal(String regionTotal) {
        this.regionTotal = regionTotal;
    }

    /**
     * Sets the type of object being analyzed
     * @param pumsTraitTable as "HOUSEHOLDS" or "POPULATION"
     */
    public void setPumsTraitTable(String pumsTraitTable) {
        this.pumsTraitTable = pumsTraitTable;
    }

    /**
     * Sets the field that contains the trait data for each object
     * @param pumsTraitField - the new field
     */
    public void setPumsTraitField(String pumsTraitField) {
        this.pumsTraitField = pumsTraitField;
    }

    /**
     * Sets value for "Flag" mode
     * @param pumsTraitSelect - the new value
     */
    public void setPumsTraitSelect(String pumsTraitSelect) {
        this.pumsTraitSelect = pumsTraitSelect;
    }

    /**
     * Sets the type of object to draw totals from
     * @param pumsTotalTable - "HOUSEHOLDS" or "POPULATION"
     */
    public void setPumsTotalTable(String pumsTotalTable) {
        this.pumsTotalTable = pumsTotalTable;
    }

    /**
     * The field that contains the total values for each object
     * @param pumsTotalField - the new field
     */
    public void setPumsTotalField(int pumsTotalField) {
        this.pumsTotalField = pumsTotalField;
    }

    /**
     * Sets the custom description for the trait
     * @param desc - the new description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    /**
     * Provides an "XML"-like string for the trait information
     * @return 
     */
    @Override
    public String toString() {
        return "<trait " + "id=" + id 
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
