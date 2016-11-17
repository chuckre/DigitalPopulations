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
 *
 * @author mrivera
 */
public class markovTableModelListener implements TableModelListener {
    private final JTable markovTable;
    
    public markovTableModelListener(JTable table){
        this.markovTable = table;
    }
    
    /**
     * Overrides the tableChanged event to calculate the Markov data
     * @param e
     */
    @Override
    public void tableChanged(TableModelEvent e){
        int row = e.getFirstRow();
        int column = e.getColumn();
        MarkovTableModel model = (MarkovTableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        
        //handle data
        model.calculateAmountLeft();
        System.out.println("this was edited: row - " + row + " and col - " + column);
    }
}
