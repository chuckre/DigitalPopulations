/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ajohnson
 */
public class Step3MarkovChainTableItemModel extends AbstractTableModel {

    private String[] columnNames = new String[]{"Markov Chain Name"};
    
    private ArrayList<MarkovChain> markovChains;
    
    private int tableRowCounter = 0;
    
    public Step3MarkovChainTableItemModel(ArrayList<MarkovChain> markovChains) {
        this.markovChains = markovChains;
    }

    public ArrayList<MarkovChain> getMarkovChains() {
        return markovChains;
    }
    
    public MarkovChain getMarkovAt(int rowIndex, int columnIndex) {
        
        Object value = "??";
        MarkovChain selectedMarkovChain = markovChains.get(rowIndex);
        return selectedMarkovChain;
    }
    
    @Override
    public int getRowCount() {
        return markovChains.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object value = "??";
        MarkovChain selectedMarkovChain = markovChains.get(rowIndex);
        switch (columnIndex) {
            case 0:
                value = selectedMarkovChain.getMarkovName();
                break;
        }
        return value;
    }
    
    @Override
    public boolean isCellEditable(int rowId, int columnId) {
        
        boolean value = false;
        
        switch (columnId) {
            case 0:
                value = false;
                break;
        }
        
        return value;
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            MarkovChain markovChain = markovChains.get(row);
            
            
        }
    }
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
