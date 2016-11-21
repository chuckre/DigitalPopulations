/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author mrivera
 */
public class customTableCellEditor extends DefaultCellEditor implements TableCellEditor {
    
    public customTableCellEditor() {
        super(new JTextField());
    }
    
    public String setupDialog(){
        String returnVal = "";
        JPanel myPanel = new JPanel();
        JTextField min = new JTextField(10);
        JTextField max = new JTextField(10);

        myPanel.add(new JLabel("Min:"));
        myPanel.add(min);
        myPanel.add(new JLabel("Max:"));
        myPanel.add(max);

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter min and max values", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            returnVal = min.getText() + " - " + max.getText();
            System.out.println(min.getText() + " - " + max.getText());
        } else if(result == JOptionPane.CANCEL_OPTION){
            //do nothing
            return null;
        }
                
        return returnVal;
    }

    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int i, int i1) {
        String cellValue = setupDialog();
        JTextField cellEditor = (JTextField)super.getTableCellEditorComponent(jtable, o, bln, i, i1);
        
        if((cellValue != null) && (o!=null)){
            cellEditor.setText(cellValue);
        }
        
        return cellEditor;
    }

}
