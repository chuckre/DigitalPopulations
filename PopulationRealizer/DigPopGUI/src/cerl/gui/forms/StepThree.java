/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.Result;
import cerl.gui.utilities.CensusSurveyClasses;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.SurveyColumnValue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 *
 * @author ajohnson
 */
public class StepThree extends javax.swing.JFrame {

    public CensusSurveyClasses censusSurveyClasses = new CensusSurveyClasses();
    public cerl.gui.utilities.Class selectSurveyClass = new cerl.gui.utilities.Class();

    private DefaultListModel censusAllListModel = new DefaultListModel();
    private JList censusAllList = new JList(censusAllListModel);

    private DefaultListModel censusSelectedListModel = new DefaultListModel();
    private JList censusSelectedList = new JList(censusSelectedListModel);

    private DefaultListModel surveyAllListModel = new DefaultListModel();
    private JList surveyAllList = new JList(surveyAllListModel);
    
    private DefaultListModel surveyGroupsListModel = new DefaultListModel();
    private JList surveyGroupsList = new JList(surveyGroupsListModel);
    
    private String FILE_PATH_CENSUS = "P:\\CERL\\md_sample-data\\md_census_enumerations.csv";
    private String FILE_PATH_POPULATION = "P:\\CERL\\md_sample-data\\md_survey_microdata_people.csv";
    private String FILE_PATH_HOUSEHOLD = "P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv";

    private final DigPopGUIInformation digPopGUIInformation;
    
    // TableRowSorter<ClassTableItemModel> sorter = new TableRowSorter<ClassTableItemModel>(censusClassDefinitionTableItemModel);
    /**
     * Creates new form StepThree
     */
    public StepThree() {
        this.digPopGUIInformation = new DigPopGUIInformation();
        Result result = DigPopGUIUtilityClass.getLoadedCensusSurveyClasses(
                FILE_PATH_CENSUS,
                FILE_PATH_POPULATION,
                FILE_PATH_HOUSEHOLD);
        censusSurveyClasses = (CensusSurveyClasses) result.getValue();

        initComponents();
        
        censusSurveyClasses.getCensusClasses().stream().forEach((c) -> {
            censusAllListModel.addElement(c);
        });
        censusSurveyClasses.getHouseholdMicroDataClasses().stream().forEach((c) -> {
            surveyAllListModel.addElement(c);
        });
        censusSurveyClasses.getPopulationMicroDataClasses().stream().forEach((c) -> {
            surveyAllListModel.addElement(c);
        });
        
        selectSurveyClass.getSurveyColumnValuesGroupings().stream().forEach((c) -> {
            surveyGroupsListModel.addElement(c);
        });

        jScrollPaneCensusAll.setViewportView(censusAllList);
        jScrollPaneCensusSelected.setViewportView(censusSelectedList);
        
        surveyAllList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneSurveyAll.setViewportView(surveyAllList);
        
        jScrollPaneSurveyDataGroups.setViewportView(surveyGroupsList);
        
        surveyAllList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if(surveyAllList.getSelectedValue().getClass() == cerl.gui.utilities.Class.class){
                    btnSurveyDataGroups.setEnabled(true);
                }else {
                    btnSurveyDataGroups.setEnabled(false);
                }
            }
        });

        pack();
    }
    
    public StepThree(DigPopGUIInformation digPopGUIInformation) {
        this.digPopGUIInformation = digPopGUIInformation;
        
        if(this.digPopGUIInformation.getValidCensusEnumerationsFilePath()
                && this.digPopGUIInformation.getValidPopulationMicroDataFilePath()
                && this.digPopGUIInformation.getValidHouseholdMicroDataFilePath()){
            Result result = DigPopGUIUtilityClass.getLoadedCensusSurveyClasses(
                this.digPopGUIInformation.getCensusEnumerationsFilePath(),
                this.digPopGUIInformation.getPopulationMicroDataFilePath(),
                this.digPopGUIInformation.getHouseholdMicroDataFilePath());
        censusSurveyClasses = (CensusSurveyClasses) result.getValue();
        }
        else{
        Result result = DigPopGUIUtilityClass.getLoadedCensusSurveyClasses(
                FILE_PATH_CENSUS,
                FILE_PATH_POPULATION,
                FILE_PATH_HOUSEHOLD);
        censusSurveyClasses = (CensusSurveyClasses) result.getValue();
        }

        initComponents();
        
        censusSurveyClasses.getCensusClasses().stream().forEach((c) -> {
            censusAllListModel.addElement(c);
        });
        censusSurveyClasses.getHouseholdMicroDataClasses().stream().forEach((c) -> {
            surveyAllListModel.addElement(c);
        });
        censusSurveyClasses.getPopulationMicroDataClasses().stream().forEach((c) -> {
            surveyAllListModel.addElement(c);
        });
        
        selectSurveyClass.getSurveyColumnValuesGroupings().stream().forEach((c) -> {
            surveyGroupsListModel.addElement(c);
        });

        jScrollPaneCensusAll.setViewportView(censusAllList);
        jScrollPaneCensusSelected.setViewportView(censusSelectedList);
        
        surveyAllList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneSurveyAll.setViewportView(surveyAllList);
        
        jScrollPaneSurveyDataGroups.setViewportView(surveyGroupsList);
        
        surveyAllList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if(surveyAllList.getSelectedValue().getClass() == cerl.gui.utilities.Class.class){
                    btnSurveyDataGroups.setEnabled(true);
                }else {
                    btnSurveyDataGroups.setEnabled(false);
                }
            }
        });

        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        surveyJPanel = new javax.swing.JPanel();
        jScrollPaneSurveyAll = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        btnSurveyDataGroups = new javax.swing.JButton();
        jScrollPaneSurveyDataGroups = new javax.swing.JScrollPane();
        censusJPanel = new javax.swing.JPanel();
        jScrollPaneCensusAll = new javax.swing.JScrollPane();
        btnAddCensusClass = new javax.swing.JButton();
        jScrollPaneCensusSelected = new javax.swing.JScrollPane();
        btnRemoveCensusClass = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnEditSelectedCensusDataDescriptions = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnPreviousStep = new javax.swing.JButton();
        btnNextStep = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step 3");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        surveyJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        surveyJPanel.setMinimumSize(new java.awt.Dimension(0, 0));

        jScrollPaneSurveyAll.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Available Survey Data(Select Only One):");

        btnSurveyDataGroups.setText("Add/Edit Survey Data Groups");
        btnSurveyDataGroups.setEnabled(false);
        btnSurveyDataGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSurveyDataGroupsActionPerformed(evt);
            }
        });

        jScrollPaneSurveyDataGroups.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout surveyJPanelLayout = new javax.swing.GroupLayout(surveyJPanel);
        surveyJPanel.setLayout(surveyJPanelLayout);
        surveyJPanelLayout.setHorizontalGroup(
            surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(surveyJPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPaneSurveyAll, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(surveyJPanelLayout.createSequentialGroup()
                                .addComponent(btnSurveyDataGroups)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPaneSurveyDataGroups)))
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        surveyJPanelLayout.setVerticalGroup(
            surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSurveyAll, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                    .addGroup(surveyJPanelLayout.createSequentialGroup()
                        .addComponent(btnSurveyDataGroups)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneSurveyDataGroups)))
                .addContainerGap())
        );

        censusJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPaneCensusAll.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAddCensusClass.setText("Add");
        btnAddCensusClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCensusClassActionPerformed(evt);
            }
        });

        jScrollPaneCensusSelected.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnRemoveCensusClass.setText("Remove");
        btnRemoveCensusClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveCensusClassActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Available Census Data:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Selected Census Data:");

        btnEditSelectedCensusDataDescriptions.setText("Edit Selected Census Data Descriptions");
        btnEditSelectedCensusDataDescriptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSelectedCensusDataDescriptionsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout censusJPanelLayout = new javax.swing.GroupLayout(censusJPanel);
        censusJPanel.setLayout(censusJPanelLayout);
        censusJPanelLayout.setHorizontalGroup(
            censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(censusJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(censusJPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPaneCensusAll, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddCensusClass, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemoveCensusClass)))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPaneCensusSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditSelectedCensusDataDescriptions))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        censusJPanelLayout.setVerticalGroup(
            censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(censusJPanelLayout.createSequentialGroup()
                .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, censusJPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(censusJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneCensusAll)
                            .addGroup(censusJPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPaneCensusSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditSelectedCensusDataDescriptions))))
                    .addGroup(censusJPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(btnAddCensusClass)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveCensusClass)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Select The Information for The New Markov Chain");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(censusJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(surveyJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(censusJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(surveyJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnPreviousStep.setText("Previous Step");
        btnPreviousStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousStepActionPerformed(evt);
            }
        });

        btnNextStep.setText("Next Step");
        btnNextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextStepActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("About");
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu3MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreviousStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNextStep)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviousStep)
                    .addComponent(btnNextStep))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddCensusClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCensusClassActionPerformed

        List<cerl.gui.utilities.Class> selected = censusAllList.getSelectedValuesList();

        for (cerl.gui.utilities.Class c : selected) {
            c.setSelected(true);
            censusAllListModel.removeElement(c);
            censusSelectedListModel.addElement(c);
        }

    }//GEN-LAST:event_btnAddCensusClassActionPerformed

    private void btnRemoveCensusClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveCensusClassActionPerformed

        List<cerl.gui.utilities.Class> selected = censusSelectedList.getSelectedValuesList();

        for (cerl.gui.utilities.Class c : selected) {
            c.setSelected(false);
            censusSelectedListModel.removeElement(c);
            censusAllListModel.addElement(c);
        }

    }//GEN-LAST:event_btnRemoveCensusClassActionPerformed

    private void btnEditSelectedCensusDataDescriptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSelectedCensusDataDescriptionsActionPerformed
        CensusClassUserDefinitions censusClassUserDefinitionsForm = new CensusClassUserDefinitions(this);

        censusClassUserDefinitionsForm.setVisible(true);

        censusClassUserDefinitionsForm.setAlwaysOnTop(true);

        this.setVisible(false);
    }//GEN-LAST:event_btnEditSelectedCensusDataDescriptionsActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened

    private void btnSurveyDataGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSurveyDataGroupsActionPerformed
        
        this.selectSurveyClass = (cerl.gui.utilities.Class)surveyAllList.getSelectedValue();
        
        if(this.selectSurveyClass.getAllSurveyColumnValues().isEmpty()){
        
            Result result = DigPopGUIUtilityClass.getSurveyDataColumnValues(
                    FILE_PATH_HOUSEHOLD, 
                    this.selectSurveyClass.getColumnNumber());
            ArrayList<SurveyColumnValue> columnValues = (ArrayList<SurveyColumnValue>)result.getValue();
            this.selectSurveyClass.setAllSurveyColumnValues(columnValues);
        }
        
        SelectSurveyDataColumnGroupings selectSurveyDataColumnGroupings = new SelectSurveyDataColumnGroupings(this.selectSurveyClass, this);
        selectSurveyDataColumnGroupings.setVisible(true);
        selectSurveyDataColumnGroupings.setAlwaysOnTop(true);
        
    }//GEN-LAST:event_btnSurveyDataGroupsActionPerformed

    private void jMenu3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu3MouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu3MouseClicked

    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        new StepTwo(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        new MarkovChainMatrix(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnNextStepActionPerformed

    public void updateSurveyGroupsListModel(){
        surveyGroupsListModel.removeAllElements();
        selectSurveyClass.getSurveyColumnValuesGroupings().stream().forEach((c) -> {
            surveyGroupsListModel.addElement(c);
        });
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
            java.util.logging.Logger.getLogger(StepThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StepThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StepThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StepThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StepThree().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCensusClass;
    private javax.swing.JButton btnEditSelectedCensusDataDescriptions;
    private javax.swing.JButton btnNextStep;
    private javax.swing.JButton btnPreviousStep;
    private javax.swing.JButton btnRemoveCensusClass;
    private javax.swing.JButton btnSurveyDataGroups;
    private javax.swing.JPanel censusJPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPaneCensusAll;
    private javax.swing.JScrollPane jScrollPaneCensusSelected;
    private javax.swing.JScrollPane jScrollPaneSurveyAll;
    private javax.swing.JScrollPane jScrollPaneSurveyDataGroups;
    private javax.swing.JPanel surveyJPanel;
    // End of variables declaration//GEN-END:variables
}
