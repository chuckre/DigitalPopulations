/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * The population tag for the goal relationship file
 * @author mrivera
 */
public class Population {
    private String table;
    private String household;

    /**
     * Creates a new blank population object
     */
    public Population() {
    }

    /**
     * Creates a new population tag for the goal relationship file
     * @param table - the file name of the population.csv file
     * @param household - the column name of the household identifier
     */
    public Population(String table, String household) {
        this.table = table;
        this.household = household;
    }

    /**
     * Gets the table as the file name of the population.csv file
     * @return 
     */
    @XmlAttribute(name="table")
    public String getTable() {
        return table;
    }

    /**
     * Sets the table as the file name of the population.csv file
     * @param table 
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Gets the household as the column name from the population.csv file
     * @return 
     */
    @XmlAttribute(name="household")
    public String getHousehold() {
        return household;
    }

    /**
     * Sets the household as the column name from the population.csv file
     * @param household 
     */
    public void setHousehold(String household) {
        this.household = household;
    }

    /**
     * An XML like string for the population tag used in the goal relationship file
     * @return 
     */
    @Override
    public String toString() {
        return "<population " + "table='" + table + "' household='" + household + "'/>";
    }    
}
