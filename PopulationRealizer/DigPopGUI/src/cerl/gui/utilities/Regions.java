/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Used in the Goal Relationship file - Regions tag
 * @author mrivera
 */
@XmlType(propOrder={"id","map","table","key","vacancies","population","households"})
public class Regions {
    private String id;
    private String map;
    private String table;
    private String key;
    private String vacancies;
    private String population;
    private String households;

    /**
     * Creates a new regions object
     */
    public Regions() {
    }

    /**
     * Creates a new regions object for the goal relationship file
     * @param map - the region map file provided from Step 1
     * @param table - the census enumeration file from Step 1
     * @param key - the column the user selected from the census enumeration.csv file
     * @param vacancies - the column the user selected from the census enumeration.csv file
     * @param population - the column the user selected from the census enumeration.csv file
     * @param households  - the column the user selected from the census enumeration.csv file
     */
    public Regions(String map, String table, String key, String vacancies, String population, String households) {
        this.map = map;
        this.table = table;
        this.key = key;
        this.vacancies = vacancies;
        this.population = population;
        this.households = households;
    }

    /**
     * Gets the id of the region - unused at the moment
     * @return 
     */
    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    /**
     * Sets the id for the region - unused at the moment
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the region map file name for the Region from Step 1
     * @return 
     */
    @XmlAttribute(name="map")
    public String getMap() {
        return map;
    }

    /**
     * Sets the region map file name for the Region from Step 1
     * @param map 
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Gets the table name as the census enumeration file provided in Step 1
     * @return 
     */
    @XmlAttribute(name="table")
    public String getTable() {
        return table;
    }

    /**
     * Sets the table name as the census enumeration file provided in Step 1
     * @param table 
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Gets the key as the user selected column name from the census enumeration file
     * @return 
     */
    @XmlAttribute(name="key")
    public String getKey() {
        return key;
    }

    /**
     * Sets the key as the user selected column name from the census enumeration file
     * @param key 
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the vacancies as the user selected column name from the census enumeration file
     * @return 
     */
    @XmlAttribute(name="vacancies")
    public String getVacancies() {
        return vacancies;
    }

    /**
     * Sets the vacancies as the user selected column name from the census enumeration file
     * @param vacancies 
     */
    public void setVacancies(String vacancies) {
        this.vacancies = vacancies;
    }

    /**
     * Gets the population as the user selected column name from the census enumeration file
     * @return 
     */
    @XmlAttribute(name="population")
    public String getPopulation() {
        return population;
    }

    /**
     * Sets the population as the user selected column name from the census enumeration file
     * @param population 
     */
    public void setPopulation(String population) {
        this.population = population;
    }

    /**
     * Gets the households as the user selected column name from the census enumeration file
     * @return 
     */
    @XmlAttribute(name="households")
    public String getHouseholds() {
        return households;
    }

    /**
     * Sets the households as the user selected column name from the census enumeration file
     * @param households 
     */
    public void setHouseholds(String households) {
        this.households = households;
    }

    /**
     * Provides an XML like string of the values provided
     * @return 
     */
    @Override
    public String toString() {
        return "<regions " + "id='" + id + "' map='" + map + "' table='" + table + "' key='" + key + "' vacancies='" + vacancies + "' population='" + population + "' households='" + households + "'/>";
    }
}
