/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Provides a custom cell renderer to update the colored styles of cells
 * Based on the MarkovTableModel attributes
 * @author mrivera
 */
public class customTableCellRenderer extends DefaultTableCellRenderer {
    Color backgroundColor = getBackground();
    
    /**
     * Sets the cell styling, based on the MarkovTableModel
     * @param table - The JTable to apply styles
     * @param value - The value of the cell to be rendered
     * @param isSelected - true if the cell is in the same row as the selected cell
     * @param hasFocus - true if it is the selected cell
     * @param row - the index of the row currently selected
     * @param column - the index of the column currently selected
     * @return 
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        //Add button to table in first row and column.
        System.out.println("table cell renderer called: row - " + row + " and col - " + column);
        if((value != null) && (value.getClass() == JButton.class)){
            JButton button = (JButton)value;
            return button;
        }

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        MarkovTableModel tableModel = (MarkovTableModel) table.getModel();
        
        //set the cell colors based on it's status
        if(tableModel.isCellCalculated(row,column)){ //calculated
            c.setBackground(Color.ORANGE);
        } else if(tableModel.isErrorInCell(row, row)){ //errors
            c.setBackground(Color.RED);
        } else if(!tableModel.isCellEditable(row,column)){ //editable
            c.setBackground(Color.LIGHT_GRAY);
        } else{ //normal
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
            if(isSelected){ //when selecting a cell, highlights the row
                c.setBackground(Color.GREEN);
            }
        }
        return c;
    }
}
