/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import java.util.*;
import cerl.gui.standard.utilities.MarkovTableModel;
import cerl.gui.standard.utilities.customTableCellEditor;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.jTableButtonMouseListener;
import cerl.gui.standard.utilities.customTableModelListener;
import cerl.gui.utilities.MarkovTableCell;
import javax.swing.table.TableColumn;

/**
 * Allows the user to set up a new Markov Chain. 
 * Depends on the MarkovTableModel and MarkovTable Cell
 * @author mrivera
 */
public class MarkovChainMatrix extends javax.swing.JFrame {
    private String markovName;
    private final MarkovTableModel myTable;
    private int START_EDITABLE_ROW;
    private int START_EDITABLE_COL;
    private int END_EDITABLE_ROW;
    private int END_EDITABLE_COL;
            
    /**
     * Creates new form MarkovChainMatrix
     * 
     * TO DO: Pull from Survey/Census data files, Write out to CSV files
     */
    public MarkovChainMatrix() {
        //load table
        myTable = populateMarkovTableModel();
        myTable.handleTableChange(-1,-1); //calculate the amount left
        
        initComponents();
        
        //sets up columns with new renderer, and clear buttons for the rows/columns
        for(int i=0; i<myTable.getColumnCount(); i++){
            TableColumn tableCol = jTable_MarkovMatrix.getColumnModel().getColumn(i);
            tableCol.setCellRenderer(new customTableCellRenderer());
            tableCol.setCellEditor(new customTableCellEditor());
        }
        //adds the mouse listener for the buttons to work in the jTable
        jTable_MarkovMatrix.addMouseListener(new jTableButtonMouseListener(jTable_MarkovMatrix));
        
        //adds the listener for the cell calculations
        jTable_MarkovMatrix.getModel().addTableModelListener(new customTableModelListener(jTable_MarkovMatrix));
        //hide error messages until needed
        jLabel_ErrorMessages.setVisible(false);
    }

    private MarkovTableModel populateMarkovTableModel(){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("","Value","Yes Electricity","No Electricity","N/A","Amount Left",""));

        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>(); //MarkovTableCell[6][7];
        
        //based on the number of rows/columns, set the limits of editable cells
        START_EDITABLE_ROW = 1;
        START_EDITABLE_COL = 2;
        END_EDITABLE_ROW = 3;
        END_EDITABLE_COL = 4;
        int[][] cells = new int[2][Math.max(END_EDITABLE_COL-START_EDITABLE_COL, END_EDITABLE_ROW-START_EDITABLE_ROW)+1];
        
        //Set up rows and columns
        for(int r = 0; r<6; r++){
            cellValues.add(r, new ArrayList<>());
            for(int c=0;c<7;c++){
                cellValues.get(r).add(c, new MarkovTableCell(r, c, "", false, false, false, true));
            }
        }
        
        //MarkovTableCell(int row, int column, Object value, boolean calculated, boolean userEntered, boolean error, boolean editable)
        cellValues.get(0).add(0, new MarkovTableCell(0, 0, "Value", false, false, false, false));
        cellValues.get(4).add(0, new MarkovTableCell(4, 0, "Amount Left", false, false, false, false));
        cellValues.get(5).add(0, new MarkovTableCell(5, 0, "", false, false, false, false));
        cellValues.get(0).add(1, new MarkovTableCell(0, 1, "Proportion", false, false, false, false));
        //non-editable corner cells
        cellValues.get(0).add(5, new MarkovTableCell(0, 5, "Range Min:    Range Max:", false, false, false, false));
        cellValues.get(0).add(6, new MarkovTableCell(0, 6, "", false, false, false, false));
        cellValues.get(4).add(1, new MarkovTableCell(4, 1, "Range Min:    Range Max:", false, false, false, false));
        cellValues.get(5).add(1, new MarkovTableCell(5, 1, "", false, false, false, false));
        cellValues.get(5).add(6, new MarkovTableCell(5, 6, "", false, false, false, false));
              
        //Survey Values
        cellValues.get(1).add(0, new MarkovTableCell(1, 1, "Yes Electricity", false, false, false, false));
        cellValues.get(2).add(0, new MarkovTableCell(2, 1, "No Electricity", false, false, false, false));
        cellValues.get(3).add(0, new MarkovTableCell(3, 1, "N/A", false, false, false, false));
        
        //load proportions
        //census - proportion min/max start the same
        cellValues.get(0).add(2, new MarkovTableCell(0, 2, 0.59, 0.59, 0.59, false, false, false, false));
        cellValues.get(0).add(3, new MarkovTableCell(0, 3, 0.30, 0.30, 0.30, false, false, false, false));
        cellValues.get(0).add(4, new MarkovTableCell(0, 4, 0.10, 0.10, 0.10, false, false, false, false));
        //survey
        cellValues.get(1).add(1, new MarkovTableCell(1, 1, 0.40, 0.40, 0.40, false, false, false, false));
        cellValues.get(2).add(1, new MarkovTableCell(2, 1, 0.39, 0.39, 0.39, false, false, false, false));
        cellValues.get(3).add(1, new MarkovTableCell(3, 1, 0.20, 0.20, 0.20, false, false, false, false));
        
        //create table with custom MarkovTableModel
        MarkovTableModel mtmTable = new MarkovTableModel(columnNames, cellValues,cells);
        
        return mtmTable;
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField_MarkovName = new javax.swing.JTextField();
        jLabel_MarkovName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_MarkovMatrix = new javax.swing.JTable();
        jButton_Back = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Save = new javax.swing.JButton();
        jButton_Clear = new javax.swing.JButton();
        jLabel_ErrorMessages = new javax.swing.JLabel();
        jMenuBar_FileMenu = new javax.swing.JMenuBar();
        jMenu_FileTab = new javax.swing.JMenu();
        jMenu_EditTab = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(840, 500));

        jTextField_MarkovName.setToolTipText("Please enter the name of the Markov Chain for saving and reuse later.");
        jTextField_MarkovName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_MarkovNameActionPerformed(evt);
            }
        });

        jLabel_MarkovName.setText("Markov Chain Name:");

        jTable_MarkovMatrix.setModel(myTable);
        jTable_MarkovMatrix.setAlignmentY(0.0F);
        jTable_MarkovMatrix.setCellEditor(jTable_MarkovMatrix.getCellEditor());
        jTable_MarkovMatrix.setColumnSelectionAllowed(true);
        jTable_MarkovMatrix.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable_MarkovMatrix.setFillsViewportHeight(true);
        jTable_MarkovMatrix.setMinimumSize(new java.awt.Dimension(100, 300));
        jTable_MarkovMatrix.setName("Markov Chain Matrix"); // NOI18N
        jTable_MarkovMatrix.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable_MarkovMatrix);
        jTable_MarkovMatrix.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jButton_Back.setText("Back to Definitions");
        jButton_Back.setToolTipText("Go back to the previous step");
        jButton_Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_BackActionPerformed(evt);
            }
        });

        jButton_Cancel.setText("Cancel Creation of New Markov Chain Matrix");
        jButton_Cancel.setToolTipText("Cancel the creation of a new Markov Chain matrix");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        jButton_Save.setText("Save New Markov Chain");
        jButton_Save.setToolTipText("Saves the current matrix as a new Markov Chain");
        jButton_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SaveActionPerformed(evt);
            }
        });

        jButton_Clear.setText("Clear All Inputs");
        jButton_Clear.setName("ClearButton"); // NOI18N
        jButton_Clear.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton_Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ClearActionPerformed(evt);
            }
        });

        jLabel_ErrorMessages.setForeground(java.awt.Color.red);
        jLabel_ErrorMessages.setText("jLabel1");

        jMenuBar_FileMenu.setName("Markov Chain Matrix"); // NOI18N

        jMenu_FileTab.setText("File");
        jMenuBar_FileMenu.add(jMenu_FileTab);

        jMenu_EditTab.setText("Edit");
        jMenuBar_FileMenu.add(jMenu_EditTab);

        setJMenuBar(jMenuBar_FileMenu);
        jMenuBar_FileMenu.getAccessibleContext().setAccessibleName("Markov Chain Matrix");
        jMenuBar_FileMenu.getAccessibleContext().setAccessibleDescription("Add a new Markov Chain Matrix");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel_MarkovName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_MarkovName, javax.swing.GroupLayout.PREFERRED_SIZE, 673, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jButton_Back)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Cancel)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Save))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton_Clear)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel_ErrorMessages)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_MarkovName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_MarkovName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton_Clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel_ErrorMessages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Back)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Save))
                .addContainerGap())
        );

        jButton_Clear.getAccessibleContext().setAccessibleDescription("Clear all user entered data from the table.");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Sends the user back to the previous step
     * @param evt 
     */
    private void jButton_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_BackActionPerformed

    /**
     * Cancels creation of the current Markov chain.
     * @param evt 
     */
    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_CancelActionPerformed

    /**
     * Clears the data entered by the user
     * @param evt 
     */
    private void jButton_ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ClearActionPerformed
        //Clear all user entered inputs from the grid.
        myTable.clear(START_EDITABLE_ROW, END_EDITABLE_ROW, START_EDITABLE_COL, END_EDITABLE_COL);
    }//GEN-LAST:event_jButton_ClearActionPerformed

    /**
     * Sets the name of the Markov when the user updates the name
     * @param evt 
     */
    private void jTextField_MarkovNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_MarkovNameActionPerformed
        //Save name of Markov
        markovName = jTextField_MarkovName.getText();
    }//GEN-LAST:event_jTextField_MarkovNameActionPerformed

    /**
     * Saves the current Markov chain
     * @param evt 
     */
    private void jButton_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveActionPerformed
        // TODO add your handling code here:
        // TODO: save markovName to XML with full populated table object.
    }//GEN-LAST:event_jButton_SaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
                        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MarkovChainMatrix().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Clear;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JLabel jLabel_ErrorMessages;
    private javax.swing.JLabel jLabel_MarkovName;
    private javax.swing.JMenuBar jMenuBar_FileMenu;
    private javax.swing.JMenu jMenu_EditTab;
    private javax.swing.JMenu jMenu_FileTab;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_MarkovMatrix;
    private javax.swing.JTextField jTextField_MarkovName;
    // End of variables declaration//GEN-END:variables

}
