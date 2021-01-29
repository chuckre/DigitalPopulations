/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.DropDownListItem;
import cerl.gui.standard.utilities.DropDownListRenderer;
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
import cerl.gui.utilities.WeightWrapper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    
    /**
     * Creates new Step 6 form GenerateTraitClusters
     */
    public GenerateTraitClusters() {
        this.digPopGUIInformation = new DigPopGUIInformation();
        //load table
        myTable = populateTableModel(new MarkovChain());
        initComponents();
        setupCustomTable();
    }
    
    /**
     * Creates new Step 6 form GenerateTraitClusters with existing data
     * @param digPopGUIInformation - the object holding all information for this run
     */
    public GenerateTraitClusters(DigPopGUIInformation digPopGUIInformation) {
        this.digPopGUIInformation = digPopGUIInformation;
        ArrayList<String> columnNames = populateColumnNames();
        ArrayList<ArrayList<Object>> cellValues = populateCellValues(columnNames);
                
        //load table
        myTable = new customTableModel(columnNames, cellValues);
        initComponents();
        setupCustomTable();
        
        jTable_TraitInformation.addMouseListener(new MouseAdapter() {
           /**
            * The user can double click a Trait from the table 
            * and will be given the option to delete it.  
           */
           public void mousePressed(MouseEvent me) {
               if (me.getClickCount() == 2) {
                   int row = jTable_TraitInformation.getSelectedRow();

                    int answer = JOptionPane.showConfirmDialog(
                       null,
                       "Are you sure you want to delete this cluster: " + myTable.getValueAt(row, 1) + "?",
                       "Delete?",
                       JOptionPane.YES_NO_OPTION);

                   if(answer == 0){
                       myTable.removeRow(row);
                       myTable.fireTableDataChanged();
                   }
               }
           }
        });
    }
    
    private ArrayList<String> populateColumnNames(){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("Trait ID", "Trait Description","Reduction","Distance"));

        return columnNames;
    }
    
    private ArrayList<ArrayList<Object>> populateCellValues(ArrayList<String> columnNames){
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
                    case "Trait Description": //string
                        cellValues.get(r).add(c, new customTableCell(getTraitDescByID(thisCluster.getId()), false, "String", false));
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
        else if(this.digPopGUIInformation.getTraitClusters() != null){
            cellValues = this.digPopGUIInformation.getTraitClusters();
        }
        return cellValues;
    }
    
    /**
     * Gets the description of a cluster by the Cluster's trait id
     * @param ClusterId - the trait id selected
     * @return - the description of the cluster/trait
     */
    private String getTraitDescByID(int ClusterId){
        ArrayList<Traits> fitTraits = this.digPopGUIInformation.getFittingTraits();
                
        String retVal = fitTraits.stream().filter(c -> c.getId() == ClusterId).findFirst().get().getDesc();
        
        return retVal;
    }
    
    /**
     * Populates the custom table with data, for a new markov chain
     * @return 
     */
    private customTableModel populateTableModel(MarkovChain thisMarkovChain){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("Trait ID","Trait Description","Reduction","Distance"));

        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>();

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
        jLabel_TraitClusters.setText("Click the button to add a new cluster, or double click a trait cluster in the table below to remove it.");

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
                    .addComponent(jScrollPane1)
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
                    
        if(this.digPopGUIInformation.getFittingTraits() != null){
            ArrayList<Traits> fitTraits = this.digPopGUIInformation.getFittingTraits();
            ArrayList<DropDownListItem> comboValues = new ArrayList<>();

            for(int i=0;i<fitTraits.size();i++){
                boolean alreadyUsed = false;

                if(myTable.getRowCount() > 0){
                    for(ArrayList<customTableCell> cellArray : myTable.getCustomTableCells()){
                        for(customTableCell cell : cellArray){
                            if(cell.toString().equals(fitTraits.get(i).getDesc())){
                                alreadyUsed = true;
                                break;
                            }
                        }
                    }
                }
                if(!alreadyUsed){
                    comboValues.add(new DropDownListItem(fitTraits.get(i).getId(), fitTraits.get(i).getDesc()));                                                            
                }
            }
            trait.setModel(new DefaultComboBoxModel(comboValues.toArray()));
        }
        else if(this.digPopGUIInformation.getTraitList() != null){
            trait.setModel(new DefaultComboBoxModel(this.digPopGUIInformation.getTraitList().toArray()));
        } else {
            trait.setModel(new DefaultComboBoxModel(new ArrayList<>().toArray()));
        }     
        
        trait.setRenderer(new DropDownListRenderer());
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
            cellValues.add(0, new customTableCell(((DropDownListItem)trait.getSelectedItem()).getId(), false, "Integer", false));
            cellValues.add(1, new customTableCell(((DropDownListItem)trait.getSelectedItem()).getDescription(), false, "String", false));
            cellValues.add(2, new customTableCell(reduction.getText(), true, "Integer", false));
            cellValues.add(3, new customTableCell(distance.getText(), true, "Integer", false));
            
            myTable.addRow(cellValues);            
        } else if(result == JOptionPane.CANCEL_OPTION){
            //do nothing
            //return null;
        }
    }//GEN-LAST:event_jButton_NewClusterActionPerformed

    /**
     * Handles the Help Menu Item selection, displays information for the current screen
     * @param evt 
     */
    private void jMenu_HelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_HelpMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_HelpMouseClicked

    /**
     * Handles the About menu item selection, displays the About pop-up
     * @param evt 
     */
    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    /**
     * Handles the Next button selection, saves information and moves to Step 3
     * @param evt 
     */
    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        saveData();
        createFittingCriteriaFile();
        
        StepSeven stepSeven = new StepSeven(this.digPopGUIInformation);
        stepSeven.setVisible(true);
        stepSeven.setLocationRelativeTo(this);
        
        dispose();
    }//GEN-LAST:event_btnNextStepActionPerformed

    /**
     * Handles the Previous button selection, saves the information and moves to the Fitting Criteria step.
     * @param evt 
     */
    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        saveData();
        new FittingCriteria(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    /**
     * Saves the data entered on the page to the currently selected Markov chain
     */
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
                /*case "Trait Description": //string
                    newCluster.setDescription(tableCell);
                    break;*/
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
        
        String fileName = FITTING_FILE_NAME + FITTING_FILE_EXT;
        
        //create new Fitting Criteria file
        File newFittingFile = new File(String.format("%s\\%s", saveFileDirectory, fileName));
                
        //write to file
        Result result = FileUtility.VerifyFileType(DEFAULT_NEW_FILE_TYPE, newFittingFile);

        if(result.isSuccess()){
            try {
                FittingCriteriaInformation fitInfo = new FittingCriteriaInformation();
                
                fitInfo.setRelationshipFile("goal_relationship.dprxml");
                fitInfo.setTraits(this.digPopGUIInformation.getFittingTraits());
                fitInfo.setWeights(new WeightWrapper(this.digPopGUIInformation.getTraitWeights()));
                fitInfo.setPositionRules(this.digPopGUIInformation.getTraitPositionClusters());
                
                //Need to create the file as empty version of the object
                result = FileUtility.ParseObjectToXML(fitInfo, newFittingFile.getPath(), fitInfo.getClass());

                //If successully created object - go to Next Step
                if(result.isSuccess()){
                    System.out.println("successfully updated file");
                    //ArrayList<String> fittingValues = (ArrayList<String>)result.getValue();
                }else {
                    System.out.println("unable to update Fitting Criteria file" + result.getErrorMessage());
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

    /**
     * Sets up the current custom table with the new table model
     */
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
