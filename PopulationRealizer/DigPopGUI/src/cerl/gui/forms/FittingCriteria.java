/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.Result;
import cerl.gui.standard.utilities.customTableModel;
import cerl.gui.standard.utilities.customTableCell;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.customTableModelListener;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.Traits;
import cerl.gui.utilities.Weights;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.table.TableColumn;

/**
 * Step 5 in the DigPop GUI
 * @author mrivera
 */
public class FittingCriteria extends javax.swing.JFrame {
    private final customTableModel myTable;
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_FIVE_HELP_FILE_NAME.toString();
    private final DigPopGUIInformation digPopGUIInformation;
    
    /**
     * Creates new Step 5 form FittingCriteria
     */
    public FittingCriteria() {
        this.digPopGUIInformation = new DigPopGUIInformation();
        //load table
        myTable = populateTableModel();
        initComponents();
        
        setupCustomTable();
    }
    /**
     * Creates new Step 5 form FittingCriteria with existing data
     * @param digPopGUIInformation - the saved log file
     */
    public FittingCriteria(DigPopGUIInformation digPopGUIInformation) {
        this.digPopGUIInformation = digPopGUIInformation;
        //load table
        myTable = populateTableModel();
        initComponents();
        setupCustomTable();
    }

    private customTableModel populateTableModel(){
        ArrayList<String> columnNames = new ArrayList<>();
        
        if(this.digPopGUIInformation.getFittingCriteriaColumnNames() != null){
            columnNames = this.digPopGUIInformation.getFittingCriteriaColumnNames();
        } else{
        //Census Value Names
            columnNames.addAll(Arrays.asList("ID","Census Region Trait"
                    ,"Census Region Total","Survey Trait Table"
                    ,"Survey Trait Select","Survey Trait Field"
                    ,"Survey Total Table", "Survey Total Field"
                    , "User Entered Description", "Trait Weight"));
            this.digPopGUIInformation.setFittingCriteriaColumnNames(columnNames);
        }
        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>();
        
        if(this.digPopGUIInformation.getFittingTraits() != null){
            ArrayList<Traits> fitTraits = this.digPopGUIInformation.getFittingTraits();
            ArrayList<Weights> fitWeights = this.digPopGUIInformation.getTraitWeights();
            
            for(int r=0; r<fitTraits.size(); r++){
                cellValues.add(r, new ArrayList<>());
                Traits thisTrait = fitTraits.get(r);
                Weights thisWeight = fitWeights.get(r);
                
                for(int c=0; c<columnNames.size(); c++){
                    switch(columnNames.get(c)){
                    case "ID": //int
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getId(), false, "Integer", false));
                        break;
                    case "Census Region Trait": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getRegionTrait(), false, "String", false));
                        break;
                    case "Census Region Total": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getRegionTotal(), false, "String", false));
                        break;
                    case "Survey Trait Table": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getPumsTraitTable(), false, "String", false));
                        break;
                    case "Survey Trait Select": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getPumsTraitSelect(), false, "Integer", false));
                        break;
                    case "Survey Trait Field": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getPumsTraitField(), false, "String", false));
                        break;
                    case "Survey Total Table": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getPumsTotalTable(), false, "String", false));
                        break;
                    case "Survey Total Field": //int
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getPumsTotalField(), false, "Integer", false));
                        break;
                    case "User Entered Description": //String
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getDesc(), false, "String", false));
                        break;
                    case "Trait Weight":  //Double
                        cellValues.get(r).add(c, new customTableCell(thisWeight.getWeight(), true, "Double", false));
                        break;
                    default:
                        break;
                }
                }
            }
        }
        else if(this.digPopGUIInformation.getFittingCriteriaCellValues() != null){
            cellValues = this.digPopGUIInformation.getFittingCriteriaCellValues();
        } else {
            //Set up rows and columns
            for(int r = 0; r<1; r++){
                cellValues.add(r, new ArrayList<>());
            }

            //ID's
            cellValues.get(0).add(0, new customTableCell("1", false, "Integer", false));
            //Census Region Traits
            cellValues.get(0).add(1, new customTableCell("ABA2E006", false, "String", false));
            //Census Region Total
            cellValues.get(0).add(2, new customTableCell("Toilet - MC - Total", false, "String", false));
            //Survey Trait Table
            cellValues.get(0).add(3, new customTableCell("HOUSEHOLDS", false, "String", false));
            //Survey Trait Select
            cellValues.get(0).add(4, new customTableCell("0", false, "Integer", false));
            //Survey Trait Field
            cellValues.get(0).add(5, new customTableCell("JWMNP", false, "String", false));
            //Survey Total Table
            cellValues.get(0).add(6, new customTableCell("HOUSEHOLDS", false, "String", false));
            //Survey Total Field
            cellValues.get(0).add(7, new customTableCell("1", false, "Integer", false));
            //User Entered Description
            cellValues.get(0).add(8, new customTableCell("Flush to piped sewer", false, "String", false));
            //Trait Weight
            cellValues.get(0).add(9, new customTableCell("", true, "Double", false));

            this.digPopGUIInformation.setFittingCriteriaCellValues(cellValues);
        }
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
        btnNextStep = new javax.swing.JButton();
        btnPreviousStep = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu_FileHelpMenu = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Five");
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

        btnNextStep.setText("Next Step");
        btnNextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextStepActionPerformed(evt);
            }
        });

        btnPreviousStep.setText("Previous Step");
        btnPreviousStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousStepActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu_FileHelpMenu.setText("Help");
        jMenu_FileHelpMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_FileHelpMenuMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu_FileHelpMenu);

        jMenu_About.setText("About");
        jMenu_About.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_AboutMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu_About);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_Header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnPreviousStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNextStep)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextStep)
                    .addComponent(btnPreviousStep))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel_Header.getAccessibleContext().setAccessibleName("Header");
        jLabel_Header.getAccessibleContext().setAccessibleDescription("Generate the Fitting Criteria File");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu_FileHelpMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_FileHelpMenuMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_FileHelpMenuMouseClicked

    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        saveToFile();
        new GenerateTraitClusters(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnNextStepActionPerformed

    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        saveToFile();
        new MarkovChainMatrix(this.digPopGUIInformation, 1).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    private void saveToFile(){
        ArrayList<Traits> theseTraits = new ArrayList<>();
        ArrayList<Weights> traitWeights = new ArrayList<>();
        ArrayList<ArrayList<Object>> cells = myTable.getTableCells();
        ArrayList<String> columns = myTable.getColumns();
        
        for(int r = 0; r<cells.size(); r++){
            Traits newTrait = new Traits();
            Weights newWeight = new Weights();
            
            for(int c = 0; c<columns.size() && c<cells.get(r).size(); c++){
                String tableCell = cells.get(r).get(c).toString();
                        
                switch(columns.get(c)){
                    case "ID": //int
                        newTrait.setId(Integer.parseInt(tableCell));
                        newWeight.setId(Integer.parseInt(tableCell));
                        break;
                    case "Census Region Trait": //String
                        newTrait.setRegionTrait(tableCell);
                        break;
                    case "Census Region Total": //String
                        newTrait.setRegionTotal(tableCell);
                        break;
                    case "Survey Trait Table": //String
                        newTrait.setPumsTraitTable(tableCell);
                        break;
                    case "Survey Trait Select": //String
                        newTrait.setPumsTraitSelect(tableCell);
                        break;
                    case "Survey Trait Field": //String
                        newTrait.setPumsTraitField(tableCell);
                        break;
                    case "Survey Total Table": //String
                        newTrait.setPumsTotalTable(tableCell);
                        break;
                    case "Survey Total Field": //int
                        newTrait.setPumsTotalField(Integer.parseInt(tableCell));
                        break;
                    case "User Entered Description": //String
                        newTrait.setDesc(tableCell);
                        break;
                    case "Trait Weight":  //Double
                        newWeight.setWeight(Double.parseDouble(tableCell));
                        break;
                    default:
                        break;
                }
            }
            theseTraits.add(r, newTrait);
            traitWeights.add(newWeight);
        }
        
        this.digPopGUIInformation.setFittingTraits(theseTraits);
        this.digPopGUIInformation.setTraitWeights(traitWeights);
        
        if(this.digPopGUIInformation.getFilePath() != null){
        //Save to file
        Result result = DigPopGUIUtilityClass.saveDigPopGUIInformationSaveFile(
                    this.digPopGUIInformation,
                this.digPopGUIInformation.getFilePath());
        }
    }
    
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
    private javax.swing.JButton btnNextStep;
    private javax.swing.JButton btnPreviousStep;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_Header;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_FileHelpMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_TraitInformation;
    // End of variables declaration//GEN-END:variables

    private void setupCustomTable() {
        //sets up columns with new renderer, and clear buttons for the rows/columns
        for(int i=0; i<myTable.getColumnCount(); i++){
            TableColumn tableCol = jTable_TraitInformation.getColumnModel().getColumn(i);
            tableCol.setCellRenderer(new customTableCellRenderer());
        }
        //adds the listener for the cell calculations/validations
        jTable_TraitInformation.getModel().addTableModelListener(new customTableModelListener(jTable_TraitInformation));
    }
}
