/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.FileType;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.Result;
import cerl.gui.standard.utilities.Validations;
import cerl.gui.standard.utilities.customTableModel;
import cerl.gui.standard.utilities.customTableCell;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.customTableModelListener;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.GoalRelationshipFile;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.MarkovChain;
import cerl.gui.utilities.SurveyColumnValuesGrouping;
import cerl.gui.utilities.Traits;
import cerl.gui.utilities.Weights;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

/**
 * Step 5 in the DigPop GUI
 * @author mrivera
 */
public class FittingCriteria extends javax.swing.JFrame {
    private final customTableModel myTable;
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_FIVE_HELP_FILE_NAME.toString();
    private final String RELATIONSHIP_FILE_NAME = "goal_relationship";
    private final String RELATIONSHIP_FILE_EXT = ".dprxml";
    private final FileType RELATIONSHIP_FILE_TYPE = FileType.XML;
    private final DigPopGUIInformation digPopGUIInformation;
    private int currentMarkovChainId;
    private String currentMarkovChainName;
    private MarkovChain markovChain;
    
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
    public FittingCriteria(DigPopGUIInformation digPopGUIInformation, int currentMarkovChainId) {
        this.currentMarkovChainId = currentMarkovChainId;
        this.digPopGUIInformation = digPopGUIInformation;
        
        //get name of Markov for saving
        MarkovChain mc = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(this.currentMarkovChainId);
        this.currentMarkovChainName = mc.getMarkovName();
        this.markovChain = mc;
        
        //load table
        myTable = populateTableModel();
        initComponents();
        setupCustomTable();
    }

    /**
     * Populates the custom table with initial dataset
     * @return 
     */
    private customTableModel populateTableModel(){
        ArrayList<String> columnNames = new ArrayList<>();
        
        if(this.markovChain.getFittingCriteriaColumnNames() != null){
            columnNames = this.markovChain.getFittingCriteriaColumnNames();
        } else{
        //Census Value Names
            columnNames.addAll(Arrays.asList("ID","Census Region Trait"
                    ,"Census Region Total","Survey Trait Table"
                    ,"Survey Trait Select","Survey Trait Field"
                    ,"Survey Total Table", "Survey Total Field"
                    , "User Entered Description", "Trait Weight"));
            this.markovChain.setFittingCriteriaColumnNames(columnNames);
        }
        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<customTableCell>> cellValues = new ArrayList<>();
        
        if(this.markovChain.getFittingTraits() != null){
            ArrayList<Traits> fitTraits = this.markovChain.getFittingTraits();
            ArrayList<Weights> fitWeights = this.markovChain.getTraitWeights();
            
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
        else { //first time in 
            MarkovChain mc = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(this.currentMarkovChainId);
            this.currentMarkovChainName = mc.getMarkovName();
            ArrayList<String> censusClasses = mc.getAllSelectedCensusClassesUserDefinedNames();
            List<SurveyColumnValuesGrouping> surveyTraits = mc.getSelectSurveyClass().getSurveyColumnValuesGroupings();
            
            //for tables used
            String householdOrPopulation = "";
            
            //to calculate the match up - set all census values to match all survey
            //int censusCounter = 0;
            //int surveyCounter = 0;
            String censusRegionTrait = "";
            
            if(this.digPopGUIInformation.getValidHouseholdMicroDataFilePath()){
                householdOrPopulation = "HOUSEHOLD";
            } else if(this.digPopGUIInformation.getValidPopulationMicroDataFilePath()){
                householdOrPopulation = "POPULATION";
            }
            
            for(int c = 0; c<censusClasses.size(); c++){
                censusRegionTrait += censusClasses.get(c) + " - ";
            }
            if(censusRegionTrait.endsWith(" - ")){
                censusRegionTrait = censusRegionTrait.substring(0, censusRegionTrait.lastIndexOf(" - "));
            }
            
            //Set up rows and columns
            //for(int r = 0; r<censusClasses.size()*surveyTraits.size(); r++){
            for(int r = 0; r<surveyTraits.size(); r++){
                cellValues.add(r, new ArrayList<>());
                
                for(int c=0;c<columnNames.size(); c++){
                    
                    switch(columnNames.get(c)){
                    case "ID": //int
                        cellValues.get(r).add(c, new customTableCell(r, false, "Integer", false));
                        break;
                    case "Census Region Trait": //String
                        //cellValues.get(r).add(c, new customTableCell(censusClasses.get(censusCounter), false, "String", false));
                        cellValues.get(r).add(c, new customTableCell(censusRegionTrait, false, "String", false));
                        break;
                    case "Census Region Total": //String
                        cellValues.get(r).add(c, new customTableCell(this.currentMarkovChainName + "_Total", false, "String", false));
                        break;
                    case "Survey Trait Table": //String
                        cellValues.get(r).add(c, new customTableCell(householdOrPopulation, false, "String", false));
                        break;
                    case "Survey Trait Select": //String
                        //cellValues.get(r).add(c, new customTableCell(surveyCounter, false, "Integer", false));
                        cellValues.get(r).add(c, new customTableCell(r, false, "Integer", false));
                        break;
                    case "Survey Trait Field": //String
                        //cellValues.get(r).add(c, new customTableCell(surveyTraits.get(surveyCounter).getAllRowIdsAsString(), false, "String", false));
                        cellValues.get(r).add(c, new customTableCell(surveyTraits.get(r).getAllRowIdsAsString(), false, "String", false));
                        break;
                    case "Survey Total Table": //String
                        cellValues.get(r).add(c, new customTableCell(householdOrPopulation, false, "String", false));
                        break;
                    case "Survey Total Field": //int
                        cellValues.get(r).add(c, new customTableCell(1, false, "Integer", false));
                        break;
                    case "User Entered Description": //String
                        //cellValues.get(r).add(c, new customTableCell(surveyTraits.get(surveyCounter).getUserDefinedDescription(), false, "String", false));
                        cellValues.get(r).add(c, new customTableCell(surveyTraits.get(r).getUserDefinedDescription(), false, "String", false));
                        break;
                    case "Trait Weight":  //Double
                        cellValues.get(r).add(c, new customTableCell("", true, "Double", false));
                        break;
                    default:
                        break;
                    }   
                }
                
                //match all census values to survey values
                /*if(censusCounter<censusClasses.size()){
                    if(surveyCounter<(surveyTraits.size()-1)){
                        surveyCounter++; //iterate survey values first
                    } else{
                        surveyCounter = 0;
                        censusCounter++; //then iterate census values
                    }
                }*/
            }
            
            this.markovChain.setFittingCriteriaCellValues(cellValues);
        }
        //create table with custom MarkovTableModel
        ArrayList<ArrayList<Object>> val = new ArrayList<>();
        
        for(int r=0;r<cellValues.size(); r++){
            val.add(new ArrayList<>());
            for(int c=0;c<cellValues.get(r).size(); c++){
                val.get(r).add((Object)cellValues.get(r).get(c));
            }
        }
        
        customTableModel myTableModel = new customTableModel(columnNames, val);
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
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu_FileHelpMenu = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Five");
        setPreferredSize(new java.awt.Dimension(1000, 350));

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

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Weights must be values between 0 and 0.99");

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
                        .addComponent(btnNextStep))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextStep)
                    .addComponent(btnPreviousStep))
                .addContainerGap())
        );

        jLabel_Header.getAccessibleContext().setAccessibleName("Header");
        jLabel_Header.getAccessibleContext().setAccessibleDescription("Generate the Fitting Criteria File");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles the Help menu item, displays information for the current screen
     * @param evt 
     */
    private void jMenu_FileHelpMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_FileHelpMenuMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_FileHelpMenuMouseClicked

    /**
     * Handles the About menu item, displays the About pop-up
     * @param evt 
     */
    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    /**
     * Handles the next button c lick, saves information and moves to the next step
     * @param evt 
     */
    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        if(validateFile()){
            saveToFile();
            new GenerateTraitClusters(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
            dispose();
        }
        else{
            JOptionPane.showMessageDialog(null, "All weights are required, and must be between 0 and 1", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNextStepActionPerformed

    /**
     * Handles the previous button click, saves and moves to the previous step
     * @param evt 
     */
    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        if(!validateFile()){
            Object[] options = { "OK", "CANCEL" };
            int result = JOptionPane.showOptionDialog(null, "Not all weights are provided, changes will not be saved, would you like to continue?", "Warning",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
            if(result == 0){
                new MarkovChainMatrix(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
                dispose();
            }
        } else{
            saveToFile();
            new MarkovChainMatrix(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    /**
     * Validates the data entered on the page so far
     * @return true if data entered is valid, false if an error exists
     */
    private Boolean validateFile(){
        ArrayList<ArrayList<Object>> cells = myTable.getTableCells();
        ArrayList<String> columns = myTable.getColumns();
        
        for(int r = 0; r<cells.size(); r++){
            int c = columns.indexOf("Trait Weight");
            if(cells.get(r).get(c).toString().equals("")){
                return false;
            } else if (Validations.validateAndReturnDouble(cells.get(r).get(c).toString())){
                Double d = Double.parseDouble(cells.get(r).get(c).toString());
                if((d>=1) || (d < 0.0)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Saves the user entered information to the FittingTraits and TraitWeights objects
     */
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
        //this.markovChain.setFittingCriteriaCellValues(myTable.getTableCells());
        this.markovChain.setFittingCriteriaCellValues(myTable.getCustomTableCells());
        
        this.markovChain.setFittingTraits(theseTraits);
        this.markovChain.setTraitWeights(traitWeights);
        
        this.markovChain.getGoalRelationshipFile().setLandUseMapInformation(this.digPopGUIInformation.getLandUseMapInformation());
        this.markovChain.addConstraintMaps(this.digPopGUIInformation.getConstraintMaps());
        
        if(this.digPopGUIInformation.getFileDirectory() != null){
            createRelationshipFile();
        }
        
        if(this.digPopGUIInformation.getFilePath() != null){
        //Save to file
        Result result = DigPopGUIUtilityClass.saveDigPopGUIInformationSaveFile(
                    this.digPopGUIInformation,
                this.digPopGUIInformation.getFilePath());
        }
    }
    
    /**
     * Creates the Goal Relationship .dprxml file with the information provided 
     * Saves file into the same folder as the log file selected on the initial step
     */
    private void createRelationshipFile(){
        String saveFileDirectory = this.digPopGUIInformation.getFileDirectory();
        String fileName = RELATIONSHIP_FILE_NAME;
        if(this.currentMarkovChainName.contains(" ")){
            fileName += this.currentMarkovChainName.replace(" ", "_") + RELATIONSHIP_FILE_EXT;
        } else{
            fileName += this.currentMarkovChainName + RELATIONSHIP_FILE_EXT;
        }
         
        //create new Fitting Criteria file
        File newRelationshipFile = new File(String.format("%s\\%s", saveFileDirectory, fileName));
                
        //write to file
        Result result = FileUtility.VerifyFileType(RELATIONSHIP_FILE_TYPE, newRelationshipFile);

        if(result.isSuccess()){
            try {
                //LandUseMapInformation relInfo = this.digPopGUIInformation.getLandUseMapInformation();
                GoalRelationshipFile goalFile = this.markovChain.getGoalRelationshipFile();
                
                //Need to create the file as empty version of the object
                result = FileUtility.ParseObjectToXML(goalFile, newRelationshipFile.getPath(), goalFile.getClass());

                //If successully created object - go to Next Step
                if(result.isSuccess()){
                    System.out.println("Successfully created relationship file");
                }else {
                    //lblErrorMessages.setText(result.getErrorMessage());
                }

            } catch (Exception ex) {
                System.err.print(ex.getMessage());
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_Header;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_FileHelpMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_TraitInformation;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates the initial column model for the custom table
     */
    private void setupCustomTable() {
        //sets up columns with new renderer, and clear buttons for the rows/columns
        for(int i=0; i<myTable.getColumnCount(); i++){
            TableColumn tableCol = jTable_TraitInformation.getColumnModel().getColumn(i);
            tableCol.setCellRenderer(new customTableCellRenderer());
        }
        //adds the listener for the cell calculations/validations
        jTable_TraitInformation.getModel().addTableModelListener(new customTableModelListener(jTable_TraitInformation, true));
    }
}
