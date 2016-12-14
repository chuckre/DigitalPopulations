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
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.MarkovChain;
import cerl.gui.utilities.MarkovTableCell;
import cerl.gui.utilities.SurveyColumnValuesGrouping;
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
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_FOUR_HELP_FILE_NAME.toString();
    private final DigPopGUIInformation digPopGUIInformation;
    private MarkovChain currentMarkovChain;
    private int currentMarkovChainId;
    
    /**
     * Creates new form MarkovChainMatrix
     * 
     * TO DO: Pull from Survey/Census data files, Write out to CSV files
     */
//    public MarkovChainMatrix() {
//        this.digPopGUIInformation = new DigPopGUIInformation();
//        //load table
//        myTable = populateMarkovTableModel();
//        myTable.handleTableChange(-1,-1); //calculate the amount left
//        
//        initComponents();
//        
//        this.txtMarkovChainName.setText(this.markovName);
//        
//        //sets up columns with new renderer, and clear buttons for the rows/columns
//        for(int i=0; i<myTable.getColumnCount(); i++){
//            TableColumn tableCol = jTable_MarkovMatrix.getColumnModel().getColumn(i);
//            tableCol.setCellRenderer(new customTableCellRenderer());
//            tableCol.setCellEditor(new customTableCellEditor());
//        }
//        //adds the mouse listener for the buttons to work in the jTable
//        jTable_MarkovMatrix.addMouseListener(new jTableButtonMouseListener(jTable_MarkovMatrix));
//        
//        //adds the listener for the cell calculations
//        jTable_MarkovMatrix.getModel().addTableModelListener(new customTableModelListener(jTable_MarkovMatrix));
//        //hide error messages until needed
//        jLabel_ErrorMessages.setVisible(false);
//        
//        pack();
//    }

    MarkovChainMatrix(DigPopGUIInformation digPopGUIInformation, int currentMarkovChainId) {
        this.digPopGUIInformation = digPopGUIInformation;
        this.currentMarkovChainId = currentMarkovChainId;
        //load table
        myTable = populateMarkovTableModel();
        myTable.handleTableChange(-1,-1); //calculate the amount left
        
        initComponents();
        
        this.txtMarkovChainName.setText(this.markovName);
        
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
        
        this.currentMarkovChain = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(this.currentMarkovChainId);
        this.markovName = this.currentMarkovChain.getMackovName();
        
        List<SurveyColumnValuesGrouping> surveyGroups = this.currentMarkovChain.getSelectSurveyClass().getSurveyColumnValuesGroupings();
        
        
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
     //   columnNames.addAll(Arrays.asList("","Value","Yes Electricity","No Electricity","N/A","Amount Left",""));
     columnNames.addAll(Arrays.asList("","Value"));
        columnNames.addAll(this.currentMarkovChain.getAllSelectedCensusClassesUserDefinedNames());
        columnNames.addAll(Arrays.asList("Amount Left",""));

        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>(); //MarkovTableCell[6][7];
        
        //based on the number of rows/columns, set the limits of editable cells
        START_EDITABLE_ROW = 1;
        START_EDITABLE_COL = 2;
        END_EDITABLE_ROW = surveyGroups.size();//3;
        END_EDITABLE_COL = 1 + this.currentMarkovChain.getCensusClasses().size();//4;
        int[][] cells = new int[2][Math.max(END_EDITABLE_COL-START_EDITABLE_COL, END_EDITABLE_ROW-START_EDITABLE_ROW)+1];
        
        //Set up rows and columns
        int numberOfNeededRows = 3 + surveyGroups.size();
        int numberOfNeededColumn = columnNames.size();
        for(int r = 0; r<numberOfNeededRows; r++){
            cellValues.add(r, new ArrayList<>());
            for(int c=0;c<numberOfNeededColumn;c++){
                cellValues.get(r).add(c, new MarkovTableCell(r, c, "", false, false, false, true));
            }
        }
        
        //MarkovTableCell(int row, int column, Object value, boolean calculated, boolean userEntered, boolean error, boolean editable)
        cellValues.get(0).add(0, new MarkovTableCell(0, 0, "Value", false, false, false, false));
        cellValues.get(numberOfNeededRows - 2).add(0, new MarkovTableCell(numberOfNeededRows - 2, 0, "Amount Left", false, false, false, false));
        cellValues.get(numberOfNeededRows - 1).add(0, new MarkovTableCell(numberOfNeededRows - 1, 0, "", false, false, false, false));
        cellValues.get(0).add(1, new MarkovTableCell(0, 1, "Proportion", false, false, false, false));
        
              
        //Survey Values
//        cellValues.get(1).add(0, new MarkovTableCell(1, 1, "Yes Electricity", false, false, false, false));
//        cellValues.get(2).add(0, new MarkovTableCell(2, 1, "No Electricity", false, false, false, false));
//        cellValues.get(3).add(0, new MarkovTableCell(3, 1, "N/A", false, false, false, false));
        
        int otherCounter = 0;
        for(int counter = 1; counter <= surveyGroups.size(); counter++){
            SurveyColumnValuesGrouping selected = surveyGroups.get(otherCounter);
            
            cellValues.get(counter).add(
                    0, 
                    new MarkovTableCell(
                            counter, 
                            1, 
                            selected.toString(), 
                            false, false, false, false));
            
            otherCounter++;
        }
        
        
        
        
        //load proportions
        //census - proportion min/max start the same
//        cellValues.get(0).add(2, new MarkovTableCell(0, 2, 0.59, 0.59, 0.59, false, false, false, false));
//        cellValues.get(0).add(3, new MarkovTableCell(0, 3, 0.30, 0.30, 0.30, false, false, false, false));
//        cellValues.get(0).add(4, new MarkovTableCell(0, 4, 0.10, 0.10, 0.10, false, false, false, false));

        long allSurveyGroupsTotal = this.currentMarkovChain.getSelectSurveyClass().getAllSurveyGroupsTotal();
        long allCensusTotal = this.currentMarkovChain.getAllCensusTotal();
        
        otherCounter = 2;
        for(int counter = 0; counter < this.currentMarkovChain.getCensusClasses().size(); counter++){
            cerl.gui.utilities.Class selected = this.currentMarkovChain.getCensusClasses().get(counter);
            
            double proportions = (double)selected.getClassTotal() / allCensusTotal;
            proportions =Math.round(proportions * 100.0) / 100.0;
            
            cellValues.get(0).add(otherCounter, new MarkovTableCell(0, otherCounter, proportions, proportions, proportions, false, false, false, false));
            otherCounter++;
        }
        
        otherCounter = 1;
        for(int counter = 0; counter < surveyGroups.size(); counter++){
            SurveyColumnValuesGrouping selected = surveyGroups.get(counter);
            
            double proportions = (double)selected.getGroupingTotal() / allSurveyGroupsTotal;
            proportions =Math.round(proportions * 100.0) / 100.0;
            
            cellValues.get(otherCounter).add(1, new MarkovTableCell(otherCounter, 1, proportions, proportions, proportions, false, false, false, false));
            otherCounter++;
        }
        
        //non-editable corner cells
        cellValues.get(0).add(numberOfNeededColumn -2, new MarkovTableCell(0, numberOfNeededColumn-2, "Range Min:    Range Max:", false, false, false, false));
        cellValues.get(0).add(numberOfNeededColumn - 1, new MarkovTableCell(0, numberOfNeededColumn -1, "", false, false, false, false));
        cellValues.get(numberOfNeededRows - 2).add(1, new MarkovTableCell(numberOfNeededRows - 2, 1, "Range Min:    Range Max:", false, false, false, false));
        cellValues.get(numberOfNeededRows - 1).add(1, new MarkovTableCell(numberOfNeededRows - 1, 1, "", false, false, false, false));
        cellValues.get(numberOfNeededRows - 1).add(numberOfNeededColumn - 1, new MarkovTableCell(numberOfNeededRows -1, numberOfNeededColumn-1, "", false, false, false, false));
        cellValues.get(numberOfNeededRows - 2).add(numberOfNeededColumn - 2, new MarkovTableCell(numberOfNeededRows -2, numberOfNeededColumn-2, "", false, false, false, false));
        
        //survey
//        cellValues.get(1).add(1, new MarkovTableCell(1, 1, 0.40, 0.40, 0.40, false, false, false, false));
//        cellValues.get(2).add(1, new MarkovTableCell(2, 1, 0.39, 0.39, 0.39, false, false, false, false));
//        cellValues.get(3).add(1, new MarkovTableCell(3, 1, 0.20, 0.20, 0.20, false, false, false, false));
        
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

        jLabel_MarkovName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_MarkovMatrix = new javax.swing.JTable();
        jButton_Back = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Save = new javax.swing.JButton();
        jButton_Clear = new javax.swing.JButton();
        jLabel_ErrorMessages = new javax.swing.JLabel();
        txtMarkovChainName = new javax.swing.JTextField();
        jMenuBar_FileMenu = new javax.swing.JMenuBar();
        jMenu_FileTab = new javax.swing.JMenu();
        jMenu_EditTab = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Four");
        setPreferredSize(new java.awt.Dimension(840, 500));

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

        jMenu_EditTab.setText("Help");
        jMenu_EditTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_EditTabMouseClicked(evt);
            }
        });
        jMenuBar_FileMenu.add(jMenu_EditTab);

        jMenu_About.setText("About");
        jMenu_About.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_AboutMouseClicked(evt);
            }
        });
        jMenuBar_FileMenu.add(jMenu_About);

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
                        .addGap(0, 179, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_MarkovName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMarkovChainName)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_MarkovName)
                    .addComponent(txtMarkovChainName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        new StepThree(this.digPopGUIInformation).setVisible(true);
        dispose();
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
     * Saves the current Markov chain
     * @param evt 
     */
    private void jButton_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveActionPerformed
        // TODO add your handling code here:
        // TODO: save markovName to XML with full populated table object.
        new FittingCriteria(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton_SaveActionPerformed

    private void jMenu_EditTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_EditTabMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_EditTabMouseClicked

    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MarkovChainMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//                        
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MarkovChainMatrix().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Clear;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JLabel jLabel_ErrorMessages;
    private javax.swing.JLabel jLabel_MarkovName;
    private javax.swing.JMenuBar jMenuBar_FileMenu;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_EditTab;
    private javax.swing.JMenu jMenu_FileTab;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_MarkovMatrix;
    private javax.swing.JTextField txtMarkovChainName;
    // End of variables declaration//GEN-END:variables

}
