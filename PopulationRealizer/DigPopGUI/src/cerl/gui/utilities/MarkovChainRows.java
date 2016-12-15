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

    public MarkovChainRows(){
        this.tableCells = new ArrayList<>();
    }
    
    public MarkovChainRows(ArrayList<MarkovTableCell> tableCells) {
        this.tableCells = tableCells;
    }

    @XmlElement(name="customTableCells")
    public ArrayList<MarkovTableCell> getTableCells() {
        return tableCells;
    }

    public void setTableCells(ArrayList<MarkovTableCell> tableCells) {
        this.tableCells = tableCells;
    }

    @Override
    public String toString() {
        return "MarkovChainRows{" + "tableCells=" + tableCells + '}';
    }
    
    @XmlElement(name="genericRow")
    public ArrayList<Object> getGenericTableCells(){
        ArrayList<Object> genericTableCells = new ArrayList<>();
        
        this.tableCells.stream().forEach((c)-> {genericTableCells.add((Object)c);});
        
        return genericTableCells;
    }
    
    public void setTableCellsFromGeneric(ArrayList<Object> genericTableCells){
        ArrayList<MarkovTableCell> newTableCells = new ArrayList<>();
        genericTableCells.stream().forEach((c)-> {newTableCells.add((MarkovTableCell)c);});
        
        this.tableCells = newTableCells;
    }
}
