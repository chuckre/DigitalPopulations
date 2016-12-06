/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.Result;
import cerl.gui.utilities.CensusSurveyClasses;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author ajohnson
 */
public class StepThree extends javax.swing.JFrame {

    public CensusSurveyClasses censusSurveyClasses = new CensusSurveyClasses();

    private DefaultListModel censusAllListModel = new DefaultListModel();
    private JList censusAllList = new JList(censusAllListModel);

    private DefaultListModel censusSelectedListModel = new DefaultListModel();
    private JList censusSelectedList = new JList(censusSelectedListModel);

    private DefaultListModel surveyAllListModel = new DefaultListModel();
    private JList surveyAllList = new JList(surveyAllListModel);

    private DefaultListModel surveySelectedListModel = new DefaultListModel();
    private JList surveySelectedList = new JList(surveySelectedListModel);

    // TableRowSorter<ClassTableItemModel> sorter = new TableRowSorter<ClassTableItemModel>(censusClassDefinitionTableItemModel);
    /**
     * Creates new form StepThree
     */
    public StepThree() {

        Result result = DigPopGUIUtilityClass.getLoadedCensusSurveyClasses(
                "P:\\CERL\\md_sample-data\\md_census_enumerations.csv",
                "P:\\CERL\\md_sample-data\\md_survey_microdata_people.csv",
                "P:\\CERL\\md_sample-data\\md_survey_microdata_household.csv");
        censusSurveyClasses = (CensusSurveyClasses) result.getValue();

        initComponents();

        for (cerl.gui.utilities.Class c : censusSurveyClasses.getCensusClasses()) {
            censusAllListModel.addElement(c);
        }
        for (cerl.gui.utilities.Class c : censusSurveyClasses.getHouseholdMicroDataClasses()) {
            surveyAllListModel.addElement(c);
        }
        for (cerl.gui.utilities.Class c : censusSurveyClasses.getPopulationMicroDataClasses()) {
            surveyAllListModel.addElement(c);
        }

        jScrollPaneCensusAll.setViewportView(censusAllList);
        jScrollPaneCensusSelected.setViewportView(censusSelectedList);
        jScrollPaneSurveyAll.setViewportView(surveyAllList);
        jScrollPaneSurveySelected.setViewportView(surveySelectedList);

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
        btnAddSurveyClass = new javax.swing.JButton();
        jScrollPaneSurveySelected = new javax.swing.JScrollPane();
        btnRemoveSurveyClass = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        censusJPanel = new javax.swing.JPanel();
        jScrollPaneCensusAll = new javax.swing.JScrollPane();
        btnAddCensusClass = new javax.swing.JButton();
        jScrollPaneCensusSelected = new javax.swing.JScrollPane();
        btnRemoveCensusClass = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        surveyJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        surveyJPanel.setMinimumSize(new java.awt.Dimension(0, 0));

        jScrollPaneSurveyAll.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAddSurveyClass.setText("Add");
        btnAddSurveyClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSurveyClassActionPerformed(evt);
            }
        });

        jScrollPaneSurveySelected.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnRemoveSurveyClass.setText("Remove");
        btnRemoveSurveyClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSurveyClassActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Available Survey Classes:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Selected Survey Classes:");

        javax.swing.GroupLayout surveyJPanelLayout = new javax.swing.GroupLayout(surveyJPanel);
        surveyJPanel.setLayout(surveyJPanelLayout);
        surveyJPanelLayout.setHorizontalGroup(
            surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(surveyJPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPaneSurveyAll, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddSurveyClass, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemoveSurveyClass)))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPaneSurveySelected, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        surveyJPanelLayout.setVerticalGroup(
            surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(surveyJPanelLayout.createSequentialGroup()
                .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, surveyJPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(surveyJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPaneSurveySelected)
                            .addComponent(jScrollPaneSurveyAll)))
                    .addGroup(surveyJPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(btnAddSurveyClass)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoveSurveyClass)
                        .addGap(0, 92, Short.MAX_VALUE)))
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
        jLabel3.setText("Available Census Classes:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Selected Census Classes:");

        jButton1.setText("Edit Selected Census Data Descriptions");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                    .addComponent(jButton1))
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
                                .addComponent(jButton1))))
                    .addGroup(censusJPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(btnAddCensusClass)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveCensusClass)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(censusJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(surveyJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(censusJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(surveyJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddSurveyClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSurveyClassActionPerformed

        List<cerl.gui.utilities.Class> selected = surveyAllList.getSelectedValuesList();

        for (cerl.gui.utilities.Class c : selected) {
            c.setSelected(true);
            surveyAllListModel.removeElement(c);
            surveySelectedListModel.addElement(c);
        }

    }//GEN-LAST:event_btnAddSurveyClassActionPerformed

    private void btnRemoveSurveyClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSurveyClassActionPerformed

        List<cerl.gui.utilities.Class> selected = surveySelectedList.getSelectedValuesList();

        for (cerl.gui.utilities.Class c : selected) {
            c.setSelected(false);
            surveySelectedListModel.removeElement(c);
            surveyAllListModel.addElement(c);
        }

    }//GEN-LAST:event_btnRemoveSurveyClassActionPerformed

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        CensusClassUserDefinitions censusClassUserDefinitionsForm = new CensusClassUserDefinitions(this.censusSurveyClasses.getCensusClasses(), this);

        censusClassUserDefinitionsForm.setVisible(true);

        censusClassUserDefinitionsForm.setAlwaysOnTop(true);

        this.setVisible(false);

        //  this.censusSurveyClasses.setCensusClasses(censusClassUserDefinitionsForm.classTableItemModel.getClasses());
        //   jDialogCensusClassDefinitions.setAlwaysOnTop(true);
//   RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
//            public boolean include(RowFilter.Entry entry) {
//                cerl.gui.utilities.Class selectedClass = (cerl.gui.utilities.Class)entry.getValue(0);
//                if(selectedClass.isSelected()){
//                    return true;
//                }
//                else{
//                    return false;
//                }
//            }
//          };
//        
//        sorter.setRowFilter(filter);
//        censusClassDefinitionTable.setRowSorter(sorter);
//   jDialogCensusClassDefinitions.setSize(400, 400);
//      jDialogCensusClassDefinitions.setVisible(true);
//      
//      
//        jDialogCensusClassDefinitions.setModal(true);
//      jDialogCensusClassDefinitions.setAlwaysOnTop(true);
        int b = 0;

    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        int test = 0;
    }//GEN-LAST:event_formWindowOpened

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
    private javax.swing.JButton btnAddSurveyClass;
    private javax.swing.JButton btnRemoveCensusClass;
    private javax.swing.JButton btnRemoveSurveyClass;
    private javax.swing.JPanel censusJPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPaneCensusAll;
    private javax.swing.JScrollPane jScrollPaneCensusSelected;
    private javax.swing.JScrollPane jScrollPaneSurveyAll;
    private javax.swing.JScrollPane jScrollPaneSurveySelected;
    private javax.swing.JPanel surveyJPanel;
    // End of variables declaration//GEN-END:variables
}
