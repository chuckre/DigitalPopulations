/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.customTableModel;
import cerl.gui.standard.utilities.customTableCell;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.customTableModelListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.table.TableColumn;

/**
 *
 * @author mrivera
 */
public class FittingCriteria extends javax.swing.JFrame {
    private final customTableModel myTable;
    
    /**
     * Creates new form FittingCriteria
     */
    public FittingCriteria() {
        //load table
        myTable = populateTableModel();
        initComponents();
                
        //sets up columns with new renderer, and clear buttons for the rows/columns
        for(int i=0; i<myTable.getColumnCount(); i++){
            TableColumn tableCol = jTable_TraitInformation.getColumnModel().getColumn(i);
            tableCol.setCellRenderer(new customTableCellRenderer());
        }
        //adds the listener for the cell calculations/validations
        jTable_TraitInformation.getModel().addTableModelListener(new customTableModelListener(jTable_TraitInformation));
    }

    private customTableModel populateTableModel(){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("ID","Census Region Trait"
                ,"Census Region Total","Survey Trait Table"
                ,"Survey Trait Select","Survey Trait Field"
                ,"Survey Total Table", "Survey Total Field"
                , "User Entered Description", "Trait Weight"));

        //columns must be rows+1 because the header row is the -1th row.
        Object[][] cellValues = new Object[10][10];
        
        //ID's
        cellValues[0][0] = new customTableCell("1", false, "Integer", false);
        cellValues[1][0] = new customTableCell("2", false, "Integer", false);
        cellValues[2][0] = new customTableCell("3", false, "Integer", false);
        
        //Census Region Traits
        cellValues[0][1] = new customTableCell("ABA2E006", false, "String", false);
        cellValues[1][1] = new customTableCell("ABA2E006", false, "String", false);
        cellValues[2][1] = new customTableCell("ABA2E006", false, "String", false);
        
        //Census Region Total
        cellValues[0][2] = new customTableCell("Toilet - MC - Total", false, "String", false);
        cellValues[1][2] = new customTableCell("Toilet - MC - Total", false, "String", false);
        cellValues[2][2] = new customTableCell("Toilet - MC - Total", false, "String", false);
        
        //Survey Trait Table
        cellValues[0][3] = new customTableCell("HOUSEHOLDS", false, "String", false);
        cellValues[1][3] = new customTableCell("HOUSEHOLDS", false, "String", false);
        cellValues[2][3] = new customTableCell("HOUSEHOLDS", false, "String", false);
        
        //Survey Trait Select
        cellValues[0][4] = new customTableCell("0", false, "Integer", false);
        cellValues[1][4] = new customTableCell("1", false, "Integer", false);
        cellValues[2][4] = new customTableCell("2", false, "Integer", false);
                
        //Survey Trait Field
        cellValues[0][5] = new customTableCell("JWMNP", false, "String", false);
        cellValues[1][5] = new customTableCell("JWMNP", false, "String", false);
        cellValues[2][5] = new customTableCell("JWMNP", false, "String", false);

        //Survey Total Table
        cellValues[0][6] = new customTableCell("HOUSEHOLDS", false, "String", false);
        cellValues[1][6] = new customTableCell("HOUSEHOLDS", false, "String", false);
        cellValues[2][6] = new customTableCell("HOUSEHOLDS", false, "String", false);

        //Survey Total Field
        cellValues[0][7] = new customTableCell("1", false, "Integer", false);
        cellValues[1][7] = new customTableCell("1", false, "Integer", false);
        cellValues[2][7] = new customTableCell("1", false, "Integer", false);

        //User Entered Description
        cellValues[0][8] = new customTableCell("Flush to piped sewer", false, "String", false);
        cellValues[1][8] = new customTableCell("Flush to septic tank", false, "String", false);
        cellValues[2][8] = new customTableCell("Flush to pit latrine", false, "String", false);

        //Trait Weight
        cellValues[0][9] = new customTableCell("", true, "Double", false);
        cellValues[1][9] = new customTableCell("", true, "Double", false);
        cellValues[2][9] = new customTableCell("", true, "Double", false);

        //create table with custom MarkovTableModel
        customTableModel myTableModel = new customTableModel(columnNames, cellValues);
        
        return myTableModel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel_Header = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_TraitInformation = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 300));

        jLabel_Header.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_Header.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Header.setText("Generate Fitting Criteria File");
        jLabel_Header.setName("Header"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Trait Information");

        jTable_TraitInformation.setModel(myTable);
        jTable_TraitInformation.setMinimumSize(new java.awt.Dimension(100, 300));
        jTable_TraitInformation.setName("Trait Information"); // NOI18N
        jScrollPane1.setViewportView(jTable_TraitInformation);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_Header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_Header)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel_Header.getAccessibleContext().setAccessibleName("Header");
        jLabel_Header.getAccessibleContext().setAccessibleDescription("Generate the Fitting Criteria File");

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(FittingCriteria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FittingCriteria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FittingCriteria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FittingCriteria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FittingCriteria().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_Header;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_TraitInformation;
    // End of variables declaration//GEN-END:variables
}
