/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * Adds a popup for editable cells
 * Requires users to enter both the min and max before continuing.
 * @author mrivera
 */
public class customTableCellEditor extends DefaultCellEditor implements TableCellEditor  {
    private JLabel validationLabel;
    private double rowProportion;
    private double colProportion;
     JComponent component = new JTextField();
     JComponent component2 = new JTextField();
    /**
     * Creates a new customTableCellEditor
     */
    public customTableCellEditor() {
        super(new JTextField()); 
        delegate = new DefaultCellEditor.EditorDelegate()
        {
            @Override
            public void setValue(Object value)
            {
                super.setValue(value);
            }

            @Override
            public Object getCellEditorValue()
            {
                return super.value;
            }

            @Override
            public void itemStateChanged(ItemEvent ie) {
                super.itemStateChanged(ie); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void actionPerformed(ActionEvent ae) {
                super.actionPerformed(ae); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void cancelCellEditing() {
                super.cancelCellEditing(); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean stopCellEditing() {
                return super.stopCellEditing(); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean startCellEditing(EventObject eo) {
                return super.startCellEditing(eo); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean shouldSelectCell(EventObject eo) {
               stopCellEditing(); 
        
                return false;//super.shouldSelectCell(eo); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isCellEditable(EventObject eo) {
                return super.isCellEditable(eo); //true;To change body of generated methods, choose Tools | Templates.
            }
            
        };
        super.delegate = delegate;
    }
    
    /**
     * Sets up the min/max popup panel and adds data validations
     * @param cellValue - The current string value of the current cell.
     * @return 
     */
    public String setupDialog(String cellValue){
        String returnVal = "";
        JPanel myPanel = new JPanel();
        this.validationLabel = new JLabel();
        
        //Set up custom double formatter for the min/max textboxes
        NumberFormat doubleFormat = NumberFormat.getNumberInstance();
        doubleFormat.setGroupingUsed(false);
        doubleFormat.setMaximumIntegerDigits(1);
        doubleFormat.setMaximumFractionDigits(2);
        doubleFormat.setRoundingMode(RoundingMode.HALF_UP);
        
        //create the min/max textboxes
        JFormattedTextField max = new JFormattedTextField(doubleFormat);
        JFormattedTextField min = new JFormattedTextField(doubleFormat);
        
        //set the dimensions for the min/max textboxes
        Dimension d = new Dimension();
        d.height=30;
        d.width=100;
        max.setPreferredSize(d);
        min.setPreferredSize(d);
        
        //setup validator for values entered 
        customInputVerifier verifyDoubles = new customInputVerifier("Double", validationLabel);
        verifyDoubles.setMaximum(Math.min(rowProportion, colProportion));
        verifyDoubles.setMinimum(0);
        
        //validate data entered into both the min and max textboxes
        min.setInputVerifier(verifyDoubles);
        max.setInputVerifier(verifyDoubles);
        
        //populate with data from the cell, if data exists
        int minValLocation = cellValue.indexOf(" - ");
        if(minValLocation > 0){
            min.setText(cellValue.substring(0, minValLocation));
            max.setText(cellValue.substring(minValLocation+3));
        }

        //Add textboxes and labels to the panel
        myPanel.add(new JLabel("Min:"));
        myPanel.add(min);
        myPanel.add(new JLabel("Max:"));
        myPanel.add(max);
        myPanel.add(validationLabel);
        myPanel.setPreferredSize(new Dimension(300,75));
        
        //create the popup
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter min and max values", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            returnVal = min.getText() + " - " + max.getText();
        } else if(result == JOptionPane.CANCEL_OPTION){
            //do nothing
            return null;
        }
                
        return returnVal;
    }

    /**
     * Gets a new custom table cell editor
     * @param jtable - The table for which to return the editor
     * @param o - The cell value
     * @param bln - If the cell is selected
     * @param i - the row being edited
     * @param i1 - the column being edited
     * @return 
     */
    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int i, int i1) {
        String cellValue = "";
        
        //get column Proportion for validation errors
        if((jtable.getValueAt(0, i1) != null) && (jtable.getValueAt(0, i1).getClass() == Double.class)){
            colProportion = (double)jtable.getValueAt(0, i1);
        } 
        
        //get row Proportion for validation errors
        if((jtable.getValueAt(i, 1) != null) && (jtable.getValueAt(i, 1).getClass() == Double.class)){
            rowProportion = (double)(jtable.getValueAt(i, 1));
        }
        
        if(o.getClass() == String.class){
            cellValue = setupDialog(o.toString());
        }
        JTextField cellEditor = (JTextField)super.getTableCellEditorComponent(jtable, o, bln, i, i1);
             
        if((cellValue != null) && (o!=null)){
            cellEditor.setText(cellValue);
        }
        test = (MarkovTableModel)jtable.getModel();
        currentRow = i;
        currentColumn = i1;
        
        rowNum = jtable.getRowCount() - 1;
        columnNum = jtable.getColumnCount() - 1;

        component =cellEditor;
        jtable1 = jtable;
        cellEditor.transferFocus();
      
        return cellEditor;
    }
    
    int rowNum, columnNum; 
    MarkovTableModel test;
    int currentRow, currentColumn;
    JTable jtable1;
    boolean changed = false;
    @Override
    public Object getCellEditorValue() {
        return ((JTextField) component).getText();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped(); //To change body of generated methods, choose Tools | Templates.
         
        jtable1.requestFocus(true);
        jtable1.editCellAt(rowNum,columnNum);
        test.fireTableCellUpdated(rowNum,columnNum);
        test.fireTableRowsUpdated(0, rowNum);
        test.fireTableDataChanged();  
    }
}