/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * The household tag for the goal relationship file
 * @author mrivera
 */
@XmlType(propOrder={"table","members","key"})
public class Households {
    private String table;
    private String members;
    private String key;

    /**
     * Creates a blank household object
     */
    public Households() {
    }

    /**
     * Creates a new household tag for the goal relationship file
     * @param table - the file name of the household.csv file
     * @param members - a user selected column name from the household.csv file
     * @param key - a user selected column name from the household.csv file
     */
    public Households(String table, String members, String key) {
        this.table = table;
        this.members = members;
        this.key = key;
    }

    /**
     * Gets the table as the file name from the household.csv file
     * @return 
     */
    @XmlAttribute(name="table")
    public String getTable() {
        return table;
    }

    /**
     * Sets the table as the file name from the household.csv file
     * @param table 
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Gets the members as the user selected column name from the household.csv file
     * @return 
     */
    @XmlAttribute(name="members")
    public String getMembers() {
        return members;
    }

    /**
     * Sets the members as the user selected column name from the household.csv file
     * @param members 
     */
    public void setMembers(String members) {
        this.members = members;
    }

    /**
     * Gets the key as the user selected column name from the household.csv file
     * @return 
     */
    @XmlAttribute(name="key")
    public String getKey() {
        return key;
    }

    /**
     * Sets the key as the user selected column name from the household.csv file
     * @param key 
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Generates an XML like string of the household values
     * @return 
     */
    @Override
    public String toString() {
        return "<households " + "table='" + table + "' members='" + members + "' key='" + key + "'/>";
    }
}
