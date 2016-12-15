/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.FileType;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.Result;
import cerl.gui.standard.utilities.customInputVerifier;
import cerl.gui.standard.utilities.customTableCell;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.customTableModel;
import cerl.gui.standard.utilities.customTableModelListener;
import cerl.gui.utilities.Cluster;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.FittingCriteriaInformation;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.MarkovChain;
import cerl.gui.utilities.Traits;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;

/**
 * The 6th Step in the DigPop GUI
 * @author mrivera
 */
public class GenerateTraitClusters extends javax.swing.JFrame {
    private final customTableModel myTable;
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_SIX_HELP_FILE_NAME.toString();
    private final String FITTING_FILE_NAME = "FittingCriteria";
    private final String FITTING_FILE_EXT = ".dprxml";
    private final FileType DEFAULT_NEW_FILE_TYPE = FileType.XML;
    private final DigPopGUIInformation digPopGUIInformation;
    private int currentMarkovChainId;
    private MarkovChain markovChain;
    
    /**
     * Creates new Step 6 form GenerateTraitClusters
     */
    public GenerateTraitClusters() {
        this.digPopGUIInformation = new DigPopGUIInformation();
        //load table
        myTable = populateTableModel();
        initComponents();
        
        setupCustomTable();
    }
    
    /**
     * Creates new Step 6 form GenerateTraitClusters with existing data
     */
    public GenerateTraitClusters(DigPopGUIInformation digPopGUIInformation, int currentMarkovChainId) {
        this.currentMarkovChainId = currentMarkovChainId;
        
        this.digPopGUIInformation = digPopGUIInformation;
        
        this.markovChain = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(currentMarkovChainId);
        
        //load table
        myTable = populateTableModel();
        initComponents();
        
        setupCustomTable();
    }

    private customTableModel populateTableModel(){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("Trait ID","Reduction","Distance"));

        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>();
        
        if(this.digPopGUIInformation.getTraitPositionClusters() != null){
            ArrayList<Cluster> posCluster = this.digPopGUIInformation.getTraitPositionClusters();
            
            for(int r=0; r<posCluster.size(); r++){
                cellValues.add(r, new ArrayList<>());
                Cluster thisCluster = posCluster.get(r);
                
                for(int c=0; c<columnNames.size(); c++){
                    switch(columnNames.get(c)){
                    case "Trait ID": //int
                        cellValues.get(r).add(c, new customTableCell(thisCluster.getId(), false, "Integer", false));
                        break;
                    case "Reduction": //int
                        cellValues.get(r).add(c, new customTableCell(thisCluster.getReduction(), true, "Integer", false));
                        break;
                    case "Distance": //int
                        cellValues.get(r).add(c, new customTableCell(thisCluster.getDistance(), true, "Integer", false));
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        else if(this.markovChain.getFittingTraits() != null){
            ArrayList<Traits> fitTraits = this.markovChain.getFittingTraits();
            
            for(int r=0; r<fitTraits.size(); r++){
                cellValues.add(r, new ArrayList<>());
                Traits thisTrait = fitTraits.get(r);
                
                for(int c=0; c<columnNames.size(); c++){
                    switch(columnNames.get(c)){
                    case "Trait ID": //int
                        cellValues.get(r).add(c, new customTableCell(thisTrait.getId(), false, "Integer", false));
                        break;
                    case "Reduction": //int
                        cellValues.get(r).add(c, new customTableCell("", true, "Integer", false));
                        break;
                    case "Distance": //int
                        cellValues.get(r).add(c, new customTableCell("", true, "Integer", false));
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        else if(this.digPopGUIInformation.getTraitClusters() != null){
            cellValues = this.digPopGUIInformation.getTraitClusters();
        } else {
            //Add rows
            cellValues.add(0,new ArrayList<>());

            //Add Column - Trait ID's
            //cellValues[0][0] = new customTableCell("123", false, "Integer", false);
            cellValues.get(0).add(0, new customTableCell("123", false, "Integer", false));

            //Reduction
            cellValues.get(0).add(1, new customTableCell("", true, "Integer", false));

            //Distance
            cellValues.get(0).add(2, new customTableCell("", true, "Integer", false));
        }
        //create table with customTableModel
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
        jLabel_TraitClusters = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_TraitInformation = new javax.swing.JTable();
        jButton_NewCluster = new javax.swing.JButton();
        btnNextStep = new javax.swing.JButton();
        btnPreviousStep = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu_File = new javax.swing.JMenu();
        jMenu_Help = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Six");

        jLabel_Header.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_Header.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Header.setText("Generate Trait Clusters");
        jLabel_Header.setName("Header"); // NOI18N

        jLabel_TraitClusters.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_TraitClusters.setText("Trait Clusters");

        jTable_TraitInformation.setModel(myTable);
        jTable_TraitInformation.setMinimumSize(new java.awt.Dimension(100, 300));
        jTable_TraitInformation.setName("Trait Information"); // NOI18N
        jScrollPane1.setViewportView(jTable_TraitInformation);

        jButton_NewCluster.setText("Add New Cluster");
        jButton_NewCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NewClusterActionPerformed(evt);
            }
        });

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

        jMenu_File.setText("File");
        jMenuBar1.add(jMenu_File);

        jMenu_Help.setText("Help");
        jMenu_Help.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_HelpMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu_Help);

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
                    .addComponent(jLabel_TraitClusters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton_NewCluster)
                        .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(jLabel_TraitClusters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_NewCluster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextStep)
                    .addComponent(btnPreviousStep))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Creates the pop up window for creating a new trait cluster
     * @param evt 
     */
    private void jButton_NewClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NewClusterActionPerformed
        //Open a new dialog, to have users provide the trait cluster information
        JPanel myPanel = new JPanel();
        
        //Set up custom int formatter for the reduction/distance textboxes
        NumberFormat intFormat = NumberFormat.getNumberInstance();
        intFormat.setGroupingUsed(false);
        intFormat.setMaximumFractionDigits(0);
        
        //create the dropdown selector for Trait ID
        //Trait ID (List with descriptions)
        JComboBox trait = new JComboBox();
        
        if(this.markovChain.getFittingTraits() != null){
            ArrayList<Traits> fitTraits = this.markovChain.getFittingTraits();
            ArrayList<String> comboValues = new ArrayList<>();
            
            for(int i=0;i<fitTraits.size();i++){
                comboValues.add(fitTraits.get(i).getId() +"");
            }
            trait.setModel(new DefaultComboBoxModel(comboValues.toArray()));
        }
        else if(this.digPopGUIInformation.getTraitList() != null){
            trait.setModel(new DefaultComboBoxModel(this.digPopGUIInformation.getTraitList().toArray()));
        } else {
            String[] traitList = {"123", "456", "789"};
            trait.setModel(new DefaultComboBoxModel(traitList));
        }     
        //create the reduction/distance textboxes
        JFormattedTextField reduction = new JFormattedTextField(intFormat);
        JFormattedTextField distance = new JFormattedTextField(intFormat);
        JLabel errorLabel = new JLabel();
        JLabel traitLabel = new JLabel("Trait:");
        JLabel reductionLabel = new JLabel("Reduction:");
        JLabel distanceLabel = new JLabel("Distance:");
        errorLabel.setText(" ");
        errorLabel.setSize(new Dimension(30,100));
                
        //set the dimensions for the min/max textboxes
        Dimension d = new Dimension();
        d.height=30;
        d.width=80;
        trait.setPreferredSize(d);
        reduction.setPreferredSize(d);
        distance.setPreferredSize(d);
        
        //setup validator for values entered 
        customInputVerifier verifyReduction = new customInputVerifier("Integer", errorLabel);
        customInputVerifier verifyDistance = new customInputVerifier("Integer", errorLabel);
        //validate data entered into both the min and max textboxes
        verifyReduction.setMinimum(1);
        verifyDistance.setMinimum(0);
        //set input verifiers to validate data entry
        reduction.setInputVerifier(verifyReduction);
        distance.setInputVerifier(verifyDistance);
        
        //set layout and alignment
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        trait.setAlignmentX(Component.LEFT_ALIGNMENT);
        reduction.setAlignmentX(Component.LEFT_ALIGNMENT);
        distance.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //Add textboxes and labels to the panel
        myPanel.add(errorLabel);
        myPanel.add(traitLabel);
        myPanel.add(trait);
        myPanel.add(reductionLabel);
        myPanel.add(reduction);
        myPanel.add(distanceLabel);
        myPanel.add(distance);
        myPanel.setPreferredSize(new Dimension(350,150));
        
        //create the popup
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Add new Trait Cluster", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            //add to table
            ArrayList<Object> cellValues = new ArrayList<>();
            //populateRow
            cellValues.add(0, new customTableCell(trait.getSelectedItem().toString(), false, "Integer", false));
            cellValues.add(1, new customTableCell(reduction.getText(), true, "Integer", false));
            cellValues.add(2, new customTableCell(distance.getText(), true, "Integer", false));
            
            myTable.addRow(cellValues);            
        } else if(result == JOptionPane.CANCEL_OPTION){
            //do nothing
            //return null;
        }
    }//GEN-LAST:event_jButton_NewClusterActionPerformed

    private void jMenu_HelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_HelpMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_HelpMouseClicked

    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        saveData();
        createFittingCriteriaFile();
        new StepThree(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnNextStepActionPerformed

    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        saveData();
        new FittingCriteria(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    private void saveData(){
        ArrayList<Cluster> clusters = new ArrayList<>();
        ArrayList<ArrayList<Object>> cells = myTable.getTableCells();
        ArrayList<String> columns = myTable.getColumns();
        
        for(int r = 0; r<cells.size(); r++){
            Cluster newCluster = new Cluster();
            for(int c = 0; c<columns.size() && c<cells.get(r).size(); c++){
                String tableCell = cells.get(r).get(c).toString();
                
                try{
                    switch(columns.get(c)){
                case "Trait ID": //int
                    newCluster.setId(Integer.parseInt(tableCell));
                    break;
                case "Reduction": //int
                    newCluster.setReduction(Integer.parseInt(tableCell));
                    break;
                case "Distance": //int
                    newCluster.setDistance(Integer.parseInt(tableCell));
                    break;
                default:
                    break;
                }
                } catch(NumberFormatException ex){
                    System.err.print("NumberFormatException" + ex.getMessage());
                }
            }
            clusters.add(r, newCluster);
        }
        this.digPopGUIInformation.setTraitPositionClusters(clusters);
    }
    
    /**
     * Generates the output Fitting Criteria .dprxml file
     */
    private void createFittingCriteriaFile(){
        String saveFileDirectory = this.digPopGUIInformation.getFileDirectory();
        
        String mcName = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(currentMarkovChainId).getMarkovName();
        String fileName = FITTING_FILE_NAME + mcName.replace(" ", "_") + FITTING_FILE_EXT;
        
        //create new Fitting Criteria file
        File newFittingFile = new File(String.format("%s\\%s", saveFileDirectory, fileName));
                
        //write to file
        Result result = FileUtility.VerifyFileType(DEFAULT_NEW_FILE_TYPE, newFittingFile);

        if(result.isSuccess()){
            try {
                FittingCriteriaInformation fitInfo = new FittingCriteriaInformation();
                
                fitInfo.setRelationshipFile(fileName);
                fitInfo.setTraits(this.markovChain.getFittingTraits());
                fitInfo.setWeights(this.markovChain.getTraitWeights());
                fitInfo.setPositionRules(this.digPopGUIInformation.getTraitPositionClusters());
                
                //Need to create the file as empty version of the object
                result = FileUtility.ParseObjectToXML(fitInfo, newFittingFile.getPath(), fitInfo.getClass());

                //If successully created object - go to Next Step
                if(result.isSuccess()){
                    System.out.println("successfully updated file");
                    //ArrayList<String> fittingValues = (ArrayList<String>)result.getValue();
                }else {
                    //lblErrorMessages.setText(result.getErrorMessage());
                }

            } catch (Exception ex) {
                System.err.print(ex.getMessage());
                //Logger.getLogger(StepZero.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            java.util.logging.Logger.getLogger(GenerateTraitClusters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GenerateTraitClusters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GenerateTraitClusters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GenerateTraitClusters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GenerateTraitClusters().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNextStep;
    private javax.swing.JButton btnPreviousStep;
    private javax.swing.JButton jButton_NewCluster;
    private javax.swing.JLabel jLabel_Header;
    private javax.swing.JLabel jLabel_TraitClusters;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_File;
    private javax.swing.JMenu jMenu_Help;
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
