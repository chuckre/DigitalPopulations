/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import java.util.*;
import cerl.gui.standard.utilities.MarkovTableModel;
import cerl.gui.standard.utilities.Result;
import cerl.gui.standard.utilities.customTableCellEditor;
import cerl.gui.standard.utilities.customTableCellRenderer;
import cerl.gui.standard.utilities.jTableButtonMouseListener;
import cerl.gui.standard.utilities.customTableModelListener;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.MarkovChain;
import cerl.gui.utilities.MarkovTableCell;
import cerl.gui.utilities.NewCensusColumnDetails;
import cerl.gui.utilities.SurveyColumnValuesGrouping;
import java.awt.event.ActionEvent;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.table.TableColumn;

/**
 * Allows the user to set up a new Markov Chain. 
 * Depends on the MarkovTableModel and MarkovTable Cell
 * @author mrivera
 */
public class MarkovChainMatrix extends javax.swing.JFrame {
    private final String markovName;
    private final MarkovTableModel myTable;
    private final int START_EDITABLE_ROW = 1;
    private final int START_EDITABLE_COL = 2;        
    private final int END_EDITABLE_ROW;
    private final int END_EDITABLE_COL;
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_FOUR_HELP_FILE_NAME.toString();
    private final DigPopGUIInformation digPopGUIInformation;
    private final MarkovChain currentMarkovChain;
    private final int currentMarkovChainId;

    /**
     * Initializes the Markov Chain Matrix
     * @param digPopGUIInformation - The current run's DigPop object
     * @param currentMarkovChainId - The unique ID for the currently selected Markov Chain
     */
    MarkovChainMatrix(DigPopGUIInformation digPopGUIInformation, int currentMarkovChainId) {
        this.digPopGUIInformation = digPopGUIInformation;
        this.currentMarkovChainId = currentMarkovChainId;
        
        this.currentMarkovChain = this.digPopGUIInformation.getCensusSurveyClasses().getMarkovChainByID(this.currentMarkovChainId);
        this.markovName = this.currentMarkovChain.getMarkovName();
        
        List<SurveyColumnValuesGrouping> surveyGroups = this.currentMarkovChain.getSelectSurveyClass().getSurveyColumnValuesGroupings();
        END_EDITABLE_ROW = surveyGroups.size();
        END_EDITABLE_COL = 1 + this.currentMarkovChain.getCensusClasses().size();
        
        //load table
        myTable = populateMarkovTableModel(surveyGroups);
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

    /**
     * Populates the Markov Chain table with existing information
     * @param surveyGroups - The column values for grouped survey data
     * @return MarkovTableModel populated with data
     */
    private MarkovTableModel populateExistingMarkovTableModel(List<SurveyColumnValuesGrouping> surveyGroups){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("","Value"));
        columnNames.addAll(this.currentMarkovChain.getAllSelectedCensusClassesUserDefinedNames());
        columnNames.addAll(Arrays.asList("Amount Left",""));
        
        int[][] cells = new int[2][Math.max(END_EDITABLE_COL-START_EDITABLE_COL, END_EDITABLE_ROW-START_EDITABLE_ROW)+1];
        
        int numRows = END_EDITABLE_ROW - START_EDITABLE_ROW + 1;
        int numCols = END_EDITABLE_COL - START_EDITABLE_COL + 1;
        
        //create table with custom MarkovTableModel
        MarkovTableModel mtmTable = new MarkovTableModel(this.currentMarkovChain.getColumnNames()
                , this.currentMarkovChain.getGenericTableCells(),this.currentMarkovChain.getEmptyCells()
                , numRows, numCols);
        
        return mtmTable;
    }
    
    /**
     * Populates the Markov Chain table, checks if data already exists, or if a new model needs to be created
     * @param surveyGroups - The grouped survey data to populate the table with
     * @return MarkovTableModel - the populated table model
     */
    private MarkovTableModel populateMarkovTableModel(List<SurveyColumnValuesGrouping> surveyGroups){
        MarkovTableModel myModel;
                
        if(this.currentMarkovChain.getMarkovChainTableCells().size() > 0){
            myModel = populateExistingMarkovTableModel(surveyGroups);
        } else{
            myModel = populateNewMarkovTableModel(surveyGroups);
        }
        return myModel;
    }
    
    /**
     * Creates a new Markov Table Model and populates it with the survey/census data
     * @param surveyGroups - The groupings of survey data to populate the table
     * @return MarkovTableModel - populated with data
     */
    private MarkovTableModel populateNewMarkovTableModel(List<SurveyColumnValuesGrouping> surveyGroups){
        ArrayList<String> columnNames = new ArrayList<>();
        //Census Value Names
        columnNames.addAll(Arrays.asList("","Value"));
        columnNames.addAll(this.currentMarkovChain.getAllSelectedCensusClassesUserDefinedNames());
        columnNames.addAll(Arrays.asList("Amount Left",""));

        //columns must be rows+1 because the header row is the -1th row.
        ArrayList<ArrayList<Object>> cellValues = new ArrayList<>(); //MarkovTableCell[6][7];
        
        //based on the number of rows/columns, set the limits of editable cells
        int[][] cells = new int[2][Math.max(END_EDITABLE_COL-START_EDITABLE_COL, END_EDITABLE_ROW-START_EDITABLE_ROW)+1];
        
        int numRows = END_EDITABLE_ROW - START_EDITABLE_ROW + 1;
        int numCols = END_EDITABLE_COL - START_EDITABLE_COL + 1;
        
        for(int r=0; r<numRows; r++){
            cells[0][r] = numCols; //in a row, there are numCols cells
        }
        for(int c=0; c<numCols; c++){
            cells[1][c] = numRows; //in a column, there are numRows cells
        }
        
        //Set up rows and columns
        int numberOfNeededRows = 3 + surveyGroups.size();
        int numberOfNeededColumn = columnNames.size();
        for(int r = 0; r<numberOfNeededRows; r++){
            cellValues.add(r, new ArrayList<>());
            for(int c=0;c<numberOfNeededColumn;c++){
                cellValues.get(r).add(c, new MarkovTableCell(r, c, "", false, false, false, true));
            }
        }
        
        //Set All Values takes: (Object value, boolean calculated, boolean userEntered, boolean error, boolean editable)
        ((MarkovTableCell)cellValues.get(0).get(0)).setAllValues("Value", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 2).get(0)).setAllValues("Amount Left", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 1).get(0)).setAllValues("", false, false, false, false);
        ((MarkovTableCell)cellValues.get(0).get(1)).setAllValues("Proportion", false, false, false, false);
              
        //Survey Values
        int otherCounter = 0;
        for(int counter = 1; counter <= surveyGroups.size(); counter++){
            SurveyColumnValuesGrouping selected = surveyGroups.get(otherCounter);
            
            ((MarkovTableCell)cellValues.get(counter).get(0)).setAllValues(selected.toString(), false, false, false, false);
            
            otherCounter++;
        }
        
        //load proportions
        long allSurveyGroupsTotal = this.currentMarkovChain.getSelectSurveyClass().getAllSurveyGroupsTotal();
        long allCensusTotal = this.currentMarkovChain.getAllCensusTotal();
        //census proportions
        otherCounter = 2;
        for(int counter = 0; counter < this.currentMarkovChain.getCensusClasses().size(); counter++){
            cerl.gui.utilities.Class selected = this.currentMarkovChain.getCensusClasses().get(counter);
            
            double proportions = (double)selected.getClassTotal() / allCensusTotal;
            proportions =Math.round(proportions * 100.0) / 100.0;
            
            ((MarkovTableCell)cellValues.get(0).get(otherCounter)).setAllValues(proportions, false, false, false, false);
            ((MarkovTableCell)cellValues.get(0).get(otherCounter)).setMin(proportions);
            ((MarkovTableCell)cellValues.get(0).get(otherCounter)).setMax(proportions);
            otherCounter++;
        }
        //survey proportions
        otherCounter = 1;
        for(int counter = 0; counter < surveyGroups.size(); counter++){
            SurveyColumnValuesGrouping selected = surveyGroups.get(counter);
            
            double proportions = (double)selected.getGroupingTotal() / allSurveyGroupsTotal;
            proportions =Math.round(proportions * 100.0) / 100.0;
            
            ((MarkovTableCell)cellValues.get(otherCounter).get(1)).setAllValues(proportions, false, false, false, false);
            ((MarkovTableCell)cellValues.get(otherCounter).get(1)).setMin(proportions);
            ((MarkovTableCell)cellValues.get(otherCounter).get(1)).setMax(proportions);
            otherCounter++;
        }
        
        //Update non-editable corner cells
        ((MarkovTableCell)cellValues.get(0).get(numberOfNeededColumn -2)).setAllValues("Range Min:    Range Max:", false, false, false, false);
        ((MarkovTableCell)cellValues.get(0).get(numberOfNeededColumn -1)).setAllValues("", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 2).get(1)).setAllValues("Range Min:    Range Max:", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 1).get(1)).setAllValues("", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 1).get(numberOfNeededColumn - 1)).setAllValues("", false, false, false, false);
        ((MarkovTableCell)cellValues.get(numberOfNeededRows - 2).get(numberOfNeededColumn - 2)).setAllValues("", false, false, false, false);
        
        //create table with custom MarkovTableModel
        MarkovTableModel mtmTable = new MarkovTableModel(columnNames, cellValues, cells, numRows, numCols);
        this.currentMarkovChain.setEmptyCells(cells);
        //this.currentMarkovChain.setMarkovChainTableCells(cellValues);
        this.currentMarkovChain.setMarkovTableCellsFromGeneric(cellValues);
        this.currentMarkovChain.setColumnNames(columnNames);
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
        jMenu_HelpTab = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Four");

        jLabel_MarkovName.setText("Markov Chain Name:");

        jTable_MarkovMatrix.setModel(myTable);
        jTable_MarkovMatrix.setAlignmentY(0.0F);
        jTable_MarkovMatrix.setCellEditor(jTable_MarkovMatrix.getCellEditor());
        jTable_MarkovMatrix.setColumnSelectionAllowed(true);
        jTable_MarkovMatrix.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable_MarkovMatrix.setFillsViewportHeight(true);
        jTable_MarkovMatrix.setMinimumSize(new java.awt.Dimension(100, 300));
        jTable_MarkovMatrix.setName("Markov Chain Matrix"); // NOI18N
        jTable_MarkovMatrix.setRowHeight(24);
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

        jMenu_HelpTab.setText("Help");
        jMenu_HelpTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_HelpTabMouseClicked(evt);
            }
        });
        jMenuBar_FileMenu.add(jMenu_HelpTab);

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
                        .addComponent(jLabel_MarkovName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMarkovChainName))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton_Clear)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel_ErrorMessages))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jButton_Back)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Cancel)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Save)))
                        .addGap(0, 179, Short.MAX_VALUE)))
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
        saveToFile();
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
        saveToFile();
        new FittingCriteria(this.digPopGUIInformation, this.currentMarkovChainId).setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton_SaveActionPerformed

    /**
     * Handles the Help menu, opens a new Help screen with the current screen's information
     * @param evt 
     */
    private void jMenu_HelpTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_HelpTabMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_HelpTabMouseClicked

    /**
     * Handles the About menu, opens the About pop-up
     * @param evt 
     */
    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    /**
     * Saves the current Markov Chain to the DigPop object
     */
    private void saveToFile(){
        this.currentMarkovChain.setMarkovName(this.txtMarkovChainName.getText());
        saveMarkovToCSVFileInformation();
        
        //Save to file
        Result    result = DigPopGUIUtilityClass.saveDigPopGUIInformationSaveFile(
                    this.digPopGUIInformation,
                    this.digPopGUIInformation.getFilePath());
    }
    
    /**
     * Saves the Markov Chain details to the NewCensusColumnDetails Array List. 
     * This information is used to create the new Census Enumeration CSV file
     * after the user selects the number of runs to be ran.
     */
    private void saveMarkovToCSVFileInformation(){
        /**
         * Clear out the current NewCensusColumnDetails before saving.
         */
        this.currentMarkovChain.setNewCensusColumnDetails(new ArrayList<NewCensusColumnDetails>());
        
        ArrayList<NewCensusColumnDetails> newCensusColumnDetails = new ArrayList<>();
            
        ArrayList<cerl.gui.utilities.Class> censusClasses = this.currentMarkovChain.getCensusClasses();
        List<SurveyColumnValuesGrouping> surveyGroupings =  this.currentMarkovChain.getSelectSurveyClass().getSurveyColumnValuesGroupings();

        /**
         * These will be used to find the min and max values stored in the grid. 
         * 
         * Row data starts at index 1.
         * Column data starts at index 2.
         */
        int rowToStartAt = START_EDITABLE_ROW;
        int currentColumnNumber = START_EDITABLE_COL;

            for(int surveyCounter = 0; surveyCounter < surveyGroupings.size(); surveyCounter++){
                SurveyColumnValuesGrouping surveyGrouping = surveyGroupings.get(surveyCounter);
                
                double newTotalRandomNumber = 0;
                ArrayList<Integer> oldValueLookUpColumns = new ArrayList<Integer>();
                
                for(int censusCounter = 0; censusCounter < censusClasses.size(); censusCounter++){
                    cerl.gui.utilities.Class censusClass = censusClasses.get(censusCounter);
                    oldValueLookUpColumns.add(censusClass.getColumnNumber());
                    
                    double[] minMaxValues = this.myTable.getMinMaxObject(rowToStartAt + surveyCounter, currentColumnNumber);
                    
                    double foundMin = minMaxValues[0];
                    double foundMax = minMaxValues[1];
                    
                    double foundRandomNumber = 0.0;
                    if(foundMin == foundMax){
                        foundRandomNumber = foundMax;
                    }else {
                        foundRandomNumber = ThreadLocalRandom.current().nextDouble(foundMin, foundMax);
                        foundRandomNumber =Math.round(foundRandomNumber  * 100.0) / 100.0;
                    }
                    
                    newTotalRandomNumber += foundRandomNumber;
                    if(currentColumnNumber < END_EDITABLE_COL){
                        currentColumnNumber++;
                    }
                }

                //set min and max numbers
                //New column header that will appear in the new csv file
                NewCensusColumnDetails details = new NewCensusColumnDetails(
                        surveyGrouping.toString() + "_" + newTotalRandomNumber,
                        newTotalRandomNumber,
                        oldValueLookUpColumns
                );

                newCensusColumnDetails.add(details);
            }
        /**
         * Add the new NewCensusColumnDetails to the current MarkovChain object
         */
        this.currentMarkovChain.setNewCensusColumnDetails(newCensusColumnDetails);
            
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Clear;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JLabel jLabel_ErrorMessages;
    private javax.swing.JLabel jLabel_MarkovName;
    private javax.swing.JMenuBar jMenuBar_FileMenu;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_FileTab;
    private javax.swing.JMenu jMenu_HelpTab;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_MarkovMatrix;
    private javax.swing.JTextField txtMarkovChainName;
    // End of variables declaration//GEN-END:variables

}
