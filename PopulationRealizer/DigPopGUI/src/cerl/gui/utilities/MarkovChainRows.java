/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * For handling the Markov table rows, holds all cells in a row
 * @author mrivera
 */
@XmlRootElement(name="markovChainRows")
public class MarkovChainRows {
    private ArrayList<MarkovTableCell> tableCells;

    /**
     * Creates a new, empty Markov Chain Row
     */
    public MarkovChainRows(){
        this.tableCells = new ArrayList<>();
    }
    
    /**
     * Creates a new Markov Chain Row from the provided ArrayList
     * @param tableCells - the ArrayList of MarkovTableCells for the row
     */
    public MarkovChainRows(ArrayList<MarkovTableCell> tableCells) {
        this.tableCells = tableCells;
    }

    /**
     * Gets the ArrayList of all MarkovTableCells for the row
     * @return the ArrayList of MarkovTableCells
     */
    @XmlElement(name="customTableCells")
    public ArrayList<MarkovTableCell> getTableCells() {
        return tableCells;
    }

    /**
     * Sets the table cells to the new list of MarkovTableCells
     * @param tableCells - the new ArrayList of MarkovTable Cells for the row
     */
    public void setTableCells(ArrayList<MarkovTableCell> tableCells) {
        this.tableCells = tableCells;
    }

    /**
     * Returns a string representation of the current row
     * @return 
     */
    @Override
    public String toString() {
        return "MarkovChainRows{" + "tableCells=" + tableCells + '}';
    }
    
    /**
     * Returns the ArrayList of table cells cast as a generic object
     * Needed for handling the XML outputs
     * @return the ArrayList of cells cast as Objects
     */
    @XmlElement(name="genericRow")
    public ArrayList<Object> getGenericTableCells(){
        ArrayList<Object> genericTableCells = new ArrayList<>();
        
        this.tableCells.stream().forEach((c)-> {genericTableCells.add((Object)c);});
        
        return genericTableCells;
    }
    
    /**
     * Sets the ArrayList of MarkovTableCells by recasting the generic Object cells back to MarkovTableCells
     * Needed for the XML reading/writing
     * @param genericTableCells 
     */
    public void setTableCellsFromGeneric(ArrayList<Object> genericTableCells){
        ArrayList<MarkovTableCell> newTableCells = new ArrayList<>();
        genericTableCells.stream().forEach((c)-> {newTableCells.add((MarkovTableCell)c);});
        
        this.tableCells = newTableCells;
    }
}
