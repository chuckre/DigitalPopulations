/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;

/**
 * From http://www.cordinc.com/blog/2010/01/jbuttons-in-a-jtable.html
 * 
 * Used for handling the button click events in the JTable
 * @author mrivera
 */
public class jTableButtonMouseListener extends MouseAdapter {
    private final JTable table;
    
    public jTableButtonMouseListener(JTable table){
        this.table = table;
    }
    
    @Override
    public void mouseClicked(MouseEvent e){
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row = e.getY()/table.getRowHeight();
        
        Object value = table.getValueAt(row,column);
        if(value instanceof JButton){
            ((JButton)value).doClick();
        }
    }
}
