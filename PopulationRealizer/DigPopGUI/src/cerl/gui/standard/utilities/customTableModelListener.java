/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * A custom Table Model Listener to handle changes to the table
 * Used to handle the custom Markov Chain table calculations
 * Calls the customTableModel handleTableChange function
 * @author mrivera
 */
public class customTableModelListener implements TableModelListener {
    private final JTable table;
    
    /**
     * Creates a new customTableModelListener for the provided JTable
     * @param table - the JTable to set the new listener on
     */
    public customTableModelListener(JTable table){
        this.table = table;
    }
    
    /**
     * Overrides the tableChanged event to calculate the Markov data
     * @param e
     */
    @Override
    public void tableChanged(TableModelEvent e){
        int row = e.getFirstRow();
        int column = e.getColumn();
        customTableModel model = (customTableModel)e.getSource();
        //String columnName = model.getColumnName(column);
        //Object data = model.getValueAt(row, column);
        
        //handle data
        model.handleTableChange(row, column);
    }
}
