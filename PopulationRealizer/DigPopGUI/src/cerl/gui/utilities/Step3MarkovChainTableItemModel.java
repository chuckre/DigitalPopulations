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
 * Used in Step 3 as the custom table model for the list of Markov Chains
 * @author ajohnson
 */
public class Step3MarkovChainTableItemModel extends AbstractTableModel {

    private final String[] columnNames = new String[]{"Markov Chain Name"};
    private final ArrayList<MarkovChain> markovChains;
    
    /**
     * Creates a new Table Model from an ArrayList of Markov chains
     * @param markovChains - the list of Markov Chains to display
     */
    public Step3MarkovChainTableItemModel(ArrayList<MarkovChain> markovChains) {
        this.markovChains = markovChains;
    }

    /**
     * Gets the list of Markov Chains in the table
     * @return an ArrayList of all MarkovChain objects
     */
    public ArrayList<MarkovChain> getMarkovChains() {
        return markovChains;
    }
    
    /**
     * Gets the specific Markov Chain by row/column in the table
     * @param rowIndex - the row the Markov Chain is in
     * @param columnIndex - the column the Markov Chain is in
     * @return the Markov Chain in the specified cell
     */
    public MarkovChain getMarkovAt(int rowIndex, int columnIndex) {
        MarkovChain selectedMarkovChain = markovChains.get(rowIndex);
        return selectedMarkovChain;
    }
    
    /**
     * Gets the number of rows in the table, as the size of the MarkovChain ArrayList
     * @return the number of rows in the table
     */
    @Override
    public int getRowCount() {
        return markovChains.size();
    }
    
    /**
     * Gets the number of columns in the table, as the length of the column Name ArrayList
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets the Markov Chain name as a generic Object value from a specific cell in the table
     * @param rowIndex - the row of the cell to obtain
     * @param columnIndex - the column of the cell to obtain
     * @return - the Markov Chain Name at the specified row/column index, or "??" if unknown
     */
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
    
    /**
     * Returns the status of the editable flag for a specific cell
     * @param rowId - the row of the cell to check
     * @param columnId - the column of the cell to check
     * @return - true if the cell is editable, false if not
     */
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
    
    /**
     * Sets the value at a specific row/column value in the table, if the value is a string
     * @param value - the new Object value to set
     * @param row - the row of the cell to set
     * @param col - the column of the cell to set
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            MarkovChain markovChain = markovChains.get(row);
        }
    }
    
    /**
     * Gets the column name for the specified column index
     * @param columnIndex - the column to obtain the name from
     * @return the column name as a string
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Gets the AbstractTableModel listener associated with the table
     * @return the Abstract Table Model's table model listener
     */
    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
}
