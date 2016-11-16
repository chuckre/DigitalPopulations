/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author mrivera
 */
public class customTableCellRenderer extends DefaultTableCellRenderer {
    Color backgroundColor = getBackground();
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        MarkovTableModel tableModel = (MarkovTableModel) table.getModel();
        
        if(!tableModel.isCellEditable(row,column)){
            c.setBackground(Color.LIGHT_GRAY);
        } else if(tableModel.isCellCalculated(row,column)){
            c.setBackground(Color.ORANGE);
        } else if(tableModel.isErrorInCell(row, row)){
            c.setBackground(Color.RED);
        } else{
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}
