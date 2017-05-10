/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.Result;
import cerl.gui.utilities.CensusSurveyClasses;
import cerl.gui.utilities.ConstraintMap;
import cerl.gui.utilities.ConstraintMapsTableItemModel;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.Households;
import cerl.gui.utilities.LandUseCombinationTableItemModel;
import cerl.gui.utilities.Population;
import cerl.gui.utilities.Regions;
import cerl.gui.utilities.StepTwoInstructionNames;
import cerl.gui.utilities.VacantClass;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * The second step in the DigPop GUI
 * @author ajohnson
 */
public class StepTwo extends javax.swing.JFrame {
    
    private final ArrayList<String> errors;
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_TWO_HELP_FILE_NAME.toString();
    private final LandUseCombinationTableItemModel landUseCombinationTableItemModel;
    private final ConstraintMapsTableItemModel constraintMapsTableItemModel;
    private final DigPopGUIInformation digPopGUIInformation;
    
    private CensusSurveyClasses censusSurveyClasses;
    private Result landUseLoadClassesResult = new Result();
    
    /**
     * Creates new form StepOne
     * @param digPopGUIInformation
     */
    public StepTwo(DigPopGUIInformation digPopGUIInformation) {
        this.digPopGUIInformation = digPopGUIInformation;
        this.landUseCombinationTableItemModel = new LandUseCombinationTableItemModel(this.digPopGUIInformation.getLandUseMapInformation().getLandUseMapClassCombinations());
        
        ArrayList<ConstraintMap> constraintMaps = new ArrayList<ConstraintMap>();
        if(this.digPopGUIInformation.getConstraintMaps() != null
                && this.digPopGUIInformation.getConstraintMaps().size() > 0){
            constraintMaps = this.digPopGUIInformation.getConstraintMaps();
        }
        this.constraintMapsTableItemModel = new ConstraintMapsTableItemModel(constraintMaps);
        
        //Pull in census Survey Classes for use in dropdowns
        this.censusSurveyClasses = this.digPopGUIInformation.getCensusSurveyClasses(); 
        
        if(this.censusSurveyClasses.getCensusClasses().isEmpty()){
            Result result = DigPopGUIUtilityClass.getLoadedCensusSurveyClasses(
                this.digPopGUIInformation.getCensusEnumerationsFilePath(),
                this.digPopGUIInformation.getPopulationMicroDataFilePath(),
                this.digPopGUIInformation.getHouseholdMicroDataFilePath());
            this.censusSurveyClasses = (CensusSurveyClasses) result.getValue();
        }
        
        initComponents();
        errors = new ArrayList<>();
        populateDataFieldsFromFile();
        
        /**
         * Mouse Listener for the MarkovChains display table.
         * The user can double click a MarkovChain from the table 
         * and will be given the option to view or delete.  
         */
        tblConstraintMaps.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    ConstraintMap constraintMap = constraintMapsTableItemModel.getConstraintMapAt(tblConstraintMaps.getSelectedRow(), tblConstraintMaps.getSelectedColumn());
                    
                    openOrDeleteExistingConstraintMap(constraintMap);
                }
            }
        });
        
        
        pack();
    }
    
    public void openOrDeleteExistingConstraintMap(ConstraintMap constraintMap){
        Object[] options = {
                        "Open",
                        "Delete"};
        int selectedOption = JOptionPane.showOptionDialog(this,
            "Would you like to open or delete the Constraint Map: " + constraintMap.getFilePath(),
            "Question",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if(selectedOption == 0){
            ConstraintMapDetails constraintMapDetails =new ConstraintMapDetails(this.digPopGUIInformation, constraintMap);
            constraintMapDetails.setVisible(true);
            constraintMapDetails.setLocationRelativeTo(this);

            dispose();
        } else if(selectedOption == 1){
            int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete Constraint Map: " + constraintMap.getFilePath() + "?",
                "Delete?",
                JOptionPane.YES_NO_OPTION);
            
            if(answer == 0){
                this.digPopGUIInformation.getConstraintMaps().remove(constraintMap);
                this.constraintMapsTableItemModel.fireTableDataChanged();
            }
        }
                    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogLoadingFile = new javax.swing.JDialog();
        jLabel5 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanelStepTwo = new javax.swing.JPanel();
        jPanelRegionMapCensusEnum = new javax.swing.JPanel();
        txtRegionMap = new javax.swing.JTextField();
        txtCensusEnumerations = new javax.swing.JTextField();
        lblRegionMap = new javax.swing.JLabel();
        lblCensusEnumerations = new javax.swing.JLabel();
        censusEnumerationsInfoIcon = new javax.swing.JLabel();
        regionMapInfoIcon = new javax.swing.JLabel();
        jLabel_KeyColumn = new javax.swing.JLabel();
        KeyColumnInfoIcon = new javax.swing.JLabel();
        jComboBox_KeyColumn = new javax.swing.JComboBox<>();
        jLabel_PopulationColumnName = new javax.swing.JLabel();
        PopulationInfoIcon = new javax.swing.JLabel();
        jComboBox_Population = new javax.swing.JComboBox<>();
        jLabel_VacanciesColumn = new javax.swing.JLabel();
        VacanciesColumnInfoIcon = new javax.swing.JLabel();
        jComboBox_Vacancies = new javax.swing.JComboBox<>();
        jLabel_HouseholdRegionTag = new javax.swing.JLabel();
        HouseholdRegionTagInfoIcon = new javax.swing.JLabel();
        jComboBox_HouseholdsRegionTag = new javax.swing.JComboBox<>();
        jPanelHouseholdDensityMap = new javax.swing.JPanel();
        txtHouseholdDensityMap = new javax.swing.JTextField();
        lblHouseholdDensityMap = new javax.swing.JLabel();
        householdDensityMapInfoIcon = new javax.swing.JLabel();
        jPanelHouseholdMicroData = new javax.swing.JPanel();
        txtHouseholdMicroData = new javax.swing.JTextField();
        lblHouseholdMicroData = new javax.swing.JLabel();
        householdMicroDataInfoIcon = new javax.swing.JLabel();
        jLabel_Members = new javax.swing.JLabel();
        MembersInfoIcon = new javax.swing.JLabel();
        jComboBox_Members = new javax.swing.JComboBox<>();
        jLabel_Household_KeyColumnName = new javax.swing.JLabel();
        HouseholdKeyInfoIcon = new javax.swing.JLabel();
        jComboBox_HouseholdKey = new javax.swing.JComboBox<>();
        jPanelPopulationMicroData = new javax.swing.JPanel();
        txtPopulationMicroData = new javax.swing.JTextField();
        lblPopulationMicroData = new javax.swing.JLabel();
        populationMicroDataInfoIcon = new javax.swing.JLabel();
        jLabel_Pop_HouseholdColumnName = new javax.swing.JLabel();
        Pop_HouseholdColumnInfoIcon = new javax.swing.JLabel();
        jComboBox_Pop_HouseholdColumnName = new javax.swing.JComboBox<>();
        jPanelLandUseHouseholdMap = new javax.swing.JPanel();
        lblLanduseMap = new javax.swing.JLabel();
        txtLandUseHouseholdMap = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVacantClasses = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtVacantClassDescription = new javax.swing.JTextField();
        btnLandUseAddCombinationClass = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLandUseClassCombinations = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtLandUseComment = new javax.swing.JTextField();
        landuseMapInfoIcon = new javax.swing.JLabel();
        landuseMapCommentInfoIcon = new javax.swing.JLabel();
        landuseMapVacentClassesInfoIcon = new javax.swing.JLabel();
        landuseMapVacentClassesDescriptionInfoIcon = new javax.swing.JLabel();
        landuseMapCombinationClassesInfoIcon = new javax.swing.JLabel();
        jPanelConstraintMap = new javax.swing.JPanel();
        lblConstraintMap = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblConstraintMaps = new javax.swing.JTable();
        constraintMapInfoIcon = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnPreviousStep = new javax.swing.JButton();
        lblErrorMessages = new javax.swing.JLabel();
        btnNextStep = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemExitApplication = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        jDialogLoadingFile.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialogLoadingFile.setAlwaysOnTop(true);
        jDialogLoadingFile.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        jDialogLoadingFile.setModal(true);
        jDialogLoadingFile.setResizable(false);
        jDialogLoadingFile.setType(java.awt.Window.Type.UTILITY);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Please Wait While File is Loading.....");

        jProgressBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString("");

        javax.swing.GroupLayout jDialogLoadingFileLayout = new javax.swing.GroupLayout(jDialogLoadingFile.getContentPane());
        jDialogLoadingFile.getContentPane().setLayout(jDialogLoadingFileLayout);
        jDialogLoadingFileLayout.setHorizontalGroup(
            jDialogLoadingFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogLoadingFileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogLoadingFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialogLoadingFileLayout.setVerticalGroup(
            jDialogLoadingFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogLoadingFileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Two");

        jPanelStepTwo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanelRegionMapCensusEnum.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtRegionMap.setEditable(false);

        txtCensusEnumerations.setEditable(false);

        lblRegionMap.setText("Region Map:");

        lblCensusEnumerations.setText("Census Enumerations:");

        censusEnumerationsInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        censusEnumerationsInfoIcon.setToolTipText("Help Infomation for Region Map");
        censusEnumerationsInfoIcon.setIconTextGap(0);
        censusEnumerationsInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                censusEnumerationsInfoIconMouseClicked(evt);
            }
        });

        regionMapInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        regionMapInfoIcon.setToolTipText("Help Infomation for Region Map");
        regionMapInfoIcon.setIconTextGap(0);
        regionMapInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regionMapInfoIconMouseClicked(evt);
            }
        });

        jLabel_KeyColumn.setText("Key Column:");
        jLabel_KeyColumn.setPreferredSize(new java.awt.Dimension(250, 14));

        KeyColumnInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        KeyColumnInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        KeyColumnInfoIcon.setIconTextGap(0);
        KeyColumnInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                KeyColumnInfoIconMouseClicked(evt);
            }
        });

        jComboBox_KeyColumn.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getCensusClasses()));
        jComboBox_KeyColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_KeyColumnActionPerformed(evt);
            }
        });

        jLabel_PopulationColumnName.setText("Population Column:");
        jLabel_PopulationColumnName.setPreferredSize(new java.awt.Dimension(250, 14));

        PopulationInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        PopulationInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        PopulationInfoIcon.setIconTextGap(0);
        PopulationInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PopulationInfoIconMouseClicked(evt);
            }
        });

        jComboBox_Population.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getCensusClasses()));
        jComboBox_Population.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_PopulationActionPerformed(evt);
            }
        });

        jLabel_VacanciesColumn.setText("Vacancies Column:");
        jLabel_VacanciesColumn.setPreferredSize(new java.awt.Dimension(250, 14));

        VacanciesColumnInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        VacanciesColumnInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        VacanciesColumnInfoIcon.setIconTextGap(0);
        VacanciesColumnInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VacanciesColumnInfoIconMouseClicked(evt);
            }
        });

        jComboBox_Vacancies.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getCensusClasses()));
        jComboBox_Vacancies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_VacanciesActionPerformed(evt);
            }
        });

        jLabel_HouseholdRegionTag.setText("Household Column:");
        jLabel_HouseholdRegionTag.setPreferredSize(new java.awt.Dimension(250, 14));

        HouseholdRegionTagInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        HouseholdRegionTagInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        HouseholdRegionTagInfoIcon.setIconTextGap(0);
        HouseholdRegionTagInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HouseholdRegionTagInfoIconMouseClicked(evt);
            }
        });

        jComboBox_HouseholdsRegionTag.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getCensusClasses()));
        jComboBox_HouseholdsRegionTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_HouseholdsRegionTagActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRegionMapCensusEnumLayout = new javax.swing.GroupLayout(jPanelRegionMapCensusEnum);
        jPanelRegionMapCensusEnum.setLayout(jPanelRegionMapCensusEnumLayout);
        jPanelRegionMapCensusEnumLayout.setHorizontalGroup(
            jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                        .addComponent(jLabel_VacanciesColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(VacanciesColumnInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_Vacancies, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_HouseholdRegionTag, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                        .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                                .addComponent(lblRegionMap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regionMapInfoIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRegionMap, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                                .addComponent(jLabel_KeyColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(KeyColumnInfoIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox_KeyColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCensusEnumerations)
                            .addComponent(jLabel_PopulationColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                        .addComponent(censusEnumerationsInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCensusEnumerations))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                        .addComponent(PopulationInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_Population, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                        .addComponent(HouseholdRegionTagInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_HouseholdsRegionTag, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelRegionMapCensusEnumLayout.setVerticalGroup(
            jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegionMapCensusEnumLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCensusEnumerations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCensusEnumerations))
                        .addComponent(censusEnumerationsInfoIcon))
                    .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRegionMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRegionMap))
                    .addComponent(regionMapInfoIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(KeyColumnInfoIcon)
                    .addComponent(jComboBox_KeyColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_KeyColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PopulationInfoIcon)
                    .addComponent(jComboBox_Population, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_PopulationColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(VacanciesColumnInfoIcon)
                    .addGroup(jPanelRegionMapCensusEnumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox_Vacancies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_HouseholdRegionTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel_VacanciesColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HouseholdRegionTagInfoIcon)
                    .addComponent(jComboBox_HouseholdsRegionTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelHouseholdDensityMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtHouseholdDensityMap.setEditable(false);

        lblHouseholdDensityMap.setText("Selected Household Density Map:");

        householdDensityMapInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        householdDensityMapInfoIcon.setToolTipText("Help Infomation for Region Map");
        householdDensityMapInfoIcon.setIconTextGap(0);
        householdDensityMapInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                householdDensityMapInfoIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelHouseholdDensityMapLayout = new javax.swing.GroupLayout(jPanelHouseholdDensityMap);
        jPanelHouseholdDensityMap.setLayout(jPanelHouseholdDensityMapLayout);
        jPanelHouseholdDensityMapLayout.setHorizontalGroup(
            jPanelHouseholdDensityMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHouseholdDensityMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHouseholdDensityMap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(householdDensityMapInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHouseholdDensityMap)
                .addContainerGap())
        );
        jPanelHouseholdDensityMapLayout.setVerticalGroup(
            jPanelHouseholdDensityMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHouseholdDensityMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHouseholdDensityMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(householdDensityMapInfoIcon)
                    .addGroup(jPanelHouseholdDensityMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtHouseholdDensityMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblHouseholdDensityMap)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelHouseholdMicroData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelHouseholdMicroData.setMinimumSize(new java.awt.Dimension(100, 100));

        txtHouseholdMicroData.setEditable(false);

        lblHouseholdMicroData.setText("Household Micro-data Table:");

        householdMicroDataInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        householdMicroDataInfoIcon.setToolTipText("Help Infomation for Region Map");
        householdMicroDataInfoIcon.setIconTextGap(0);
        householdMicroDataInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                householdMicroDataInfoIconMouseClicked(evt);
            }
        });

        jLabel_Members.setText("Members Column");
        jLabel_Members.setPreferredSize(new java.awt.Dimension(250, 14));

        MembersInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        MembersInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        MembersInfoIcon.setIconTextGap(0);
        MembersInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MembersInfoIconMouseClicked(evt);
            }
        });

        jComboBox_Members.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getHouseholdMicroDataClasses()));
        jComboBox_Members.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_MembersActionPerformed(evt);
            }
        });

        jLabel_Household_KeyColumnName.setText("Key Column");
        jLabel_Household_KeyColumnName.setPreferredSize(new java.awt.Dimension(250, 14));

        HouseholdKeyInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        HouseholdKeyInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        HouseholdKeyInfoIcon.setIconTextGap(0);
        HouseholdKeyInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HouseholdKeyInfoIconMouseClicked(evt);
            }
        });

        jComboBox_HouseholdKey.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getHouseholdMicroDataClasses()));
        jComboBox_HouseholdKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_HouseholdKeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHouseholdMicroDataLayout = new javax.swing.GroupLayout(jPanelHouseholdMicroData);
        jPanelHouseholdMicroData.setLayout(jPanelHouseholdMicroDataLayout);
        jPanelHouseholdMicroDataLayout.setHorizontalGroup(
            jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHouseholdMicroDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHouseholdMicroDataLayout.createSequentialGroup()
                        .addComponent(lblHouseholdMicroData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(householdMicroDataInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHouseholdMicroData))
                    .addGroup(jPanelHouseholdMicroDataLayout.createSequentialGroup()
                        .addComponent(jLabel_Members, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(MembersInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_Members, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jLabel_Household_KeyColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HouseholdKeyInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_HouseholdKey, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelHouseholdMicroDataLayout.setVerticalGroup(
            jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHouseholdMicroDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(householdMicroDataInfoIcon)
                    .addGroup(jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHouseholdMicroData)
                        .addComponent(txtHouseholdMicroData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelHouseholdMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MembersInfoIcon)
                    .addComponent(jComboBox_Members, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_Members, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HouseholdKeyInfoIcon)
                    .addComponent(jComboBox_HouseholdKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_Household_KeyColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPopulationMicroData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPopulationMicroData.setMinimumSize(new java.awt.Dimension(100, 100));

        txtPopulationMicroData.setEditable(false);

        lblPopulationMicroData.setText("Population Micro-data Table:");

        populationMicroDataInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        populationMicroDataInfoIcon.setToolTipText("Help Infomation for Region Map");
        populationMicroDataInfoIcon.setIconTextGap(0);
        populationMicroDataInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                populationMicroDataInfoIconMouseClicked(evt);
            }
        });

        jLabel_Pop_HouseholdColumnName.setText("Household Column");
        jLabel_Pop_HouseholdColumnName.setPreferredSize(new java.awt.Dimension(250, 14));

        Pop_HouseholdColumnInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Pop_HouseholdColumnInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        Pop_HouseholdColumnInfoIcon.setIconTextGap(0);
        Pop_HouseholdColumnInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Pop_HouseholdColumnInfoIconMouseClicked(evt);
            }
        });

        jComboBox_Pop_HouseholdColumnName.setModel(DigPopGUIUtilityClass.getNewDefaultComboBoxModel(this.censusSurveyClasses.getPopulationMicroDataClasses()));
        jComboBox_Pop_HouseholdColumnName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Pop_HouseholdColumnNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPopulationMicroDataLayout = new javax.swing.GroupLayout(jPanelPopulationMicroData);
        jPanelPopulationMicroData.setLayout(jPanelPopulationMicroDataLayout);
        jPanelPopulationMicroDataLayout.setHorizontalGroup(
            jPanelPopulationMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPopulationMicroDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPopulationMicroData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(populationMicroDataInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPopulationMicroData, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_Pop_HouseholdColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Pop_HouseholdColumnInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox_Pop_HouseholdColumnName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPopulationMicroDataLayout.setVerticalGroup(
            jPanelPopulationMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPopulationMicroDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPopulationMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Pop_HouseholdColumnInfoIcon)
                    .addComponent(jComboBox_Pop_HouseholdColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_Pop_HouseholdColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(populationMicroDataInfoIcon)
                    .addGroup(jPanelPopulationMicroDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPopulationMicroData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPopulationMicroData)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Pop_HouseholdColumnInfoIcon.getAccessibleContext().setAccessibleDescription("Help Infomation for Population Household tag");

        jPanelLandUseHouseholdMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelLandUseHouseholdMap.setMinimumSize(new java.awt.Dimension(100, 100));

        lblLanduseMap.setText("Selected Land Use Map:");

        txtLandUseHouseholdMap.setEditable(false);

        jLabel2.setText("Vacant Classes: ");

        jLabel3.setText("Vacant Class Description: ");

        btnLandUseAddCombinationClass.setText("Add Custom Combination of Classes");
        btnLandUseAddCombinationClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLandUseAddCombinationClassActionPerformed(evt);
            }
        });

        jTableLandUseClassCombinations.setModel(landUseCombinationTableItemModel);
        jScrollPane1.setViewportView(jTableLandUseClassCombinations);

        jLabel4.setText("Comment: ");

        landuseMapInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        landuseMapInfoIcon.setToolTipText("Help Infomation for Region Map");
        landuseMapInfoIcon.setIconTextGap(0);
        landuseMapInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                landuseMapInfoIconMouseClicked(evt);
            }
        });

        landuseMapCommentInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        landuseMapCommentInfoIcon.setToolTipText("Help Infomation for Region Map");
        landuseMapCommentInfoIcon.setIconTextGap(0);
        landuseMapCommentInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                landuseMapCommentInfoIconMouseClicked(evt);
            }
        });

        landuseMapVacentClassesInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        landuseMapVacentClassesInfoIcon.setToolTipText("Help Infomation for Region Map");
        landuseMapVacentClassesInfoIcon.setIconTextGap(0);
        landuseMapVacentClassesInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                landuseMapVacentClassesInfoIconMouseClicked(evt);
            }
        });

        landuseMapVacentClassesDescriptionInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        landuseMapVacentClassesDescriptionInfoIcon.setToolTipText("Help Infomation for Region Map");
        landuseMapVacentClassesDescriptionInfoIcon.setIconTextGap(0);
        landuseMapVacentClassesDescriptionInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                landuseMapVacentClassesDescriptionInfoIconMouseClicked(evt);
            }
        });

        landuseMapCombinationClassesInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        landuseMapCombinationClassesInfoIcon.setToolTipText("Help Infomation for Region Map");
        landuseMapCombinationClassesInfoIcon.setIconTextGap(0);
        landuseMapCombinationClassesInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                landuseMapCombinationClassesInfoIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelLandUseHouseholdMapLayout = new javax.swing.GroupLayout(jPanelLandUseHouseholdMap);
        jPanelLandUseHouseholdMap.setLayout(jPanelLandUseHouseholdMapLayout);
        jPanelLandUseHouseholdMapLayout.setHorizontalGroup(
            jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                        .addComponent(lblLanduseMap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(landuseMapInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLandUseHouseholdMap))
                    .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                        .addComponent(btnLandUseAddCombinationClass)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(landuseMapCombinationClassesInfoIcon)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(landuseMapCommentInfoIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLandUseComment))
                            .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(landuseMapVacentClassesDescriptionInfoIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVacantClassDescription))
                            .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(landuseMapVacentClassesInfoIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVacantClasses)))))
                .addContainerGap())
        );
        jPanelLandUseHouseholdMapLayout.setVerticalGroup(
            jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLandUseHouseholdMapLayout.createSequentialGroup()
                        .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblLanduseMap)
                                .addComponent(txtLandUseHouseholdMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(landuseMapInfoIcon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(txtLandUseComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(landuseMapCommentInfoIcon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtVacantClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(landuseMapVacentClassesInfoIcon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtVacantClassDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(landuseMapVacentClassesDescriptionInfoIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLandUseHouseholdMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLandUseAddCombinationClass)
                    .addComponent(landuseMapCombinationClassesInfoIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelConstraintMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelConstraintMap.setMinimumSize(new java.awt.Dimension(100, 100));

        lblConstraintMap.setText("Selected Constraint Maps :");

        tblConstraintMaps.setModel(constraintMapsTableItemModel);
        jScrollPane3.setViewportView(tblConstraintMaps);

        constraintMapInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        constraintMapInfoIcon.setToolTipText("Help Infomation for Region Map");
        constraintMapInfoIcon.setIconTextGap(0);
        constraintMapInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                constraintMapInfoIconMouseClicked(evt);
            }
        });

        jLabel7.setText("To Open or Delete Existing Constraint Map: Please Double Click on Specified Constraint Map in Table.");

        javax.swing.GroupLayout jPanelConstraintMapLayout = new javax.swing.GroupLayout(jPanelConstraintMap);
        jPanelConstraintMap.setLayout(jPanelConstraintMapLayout);
        jPanelConstraintMapLayout.setHorizontalGroup(
            jPanelConstraintMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConstraintMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConstraintMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                    .addGroup(jPanelConstraintMapLayout.createSequentialGroup()
                        .addGroup(jPanelConstraintMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelConstraintMapLayout.createSequentialGroup()
                                .addComponent(lblConstraintMap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(constraintMapInfoIcon))
                            .addComponent(jLabel7))
                        .addGap(0, 303, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelConstraintMapLayout.setVerticalGroup(
            jPanelConstraintMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConstraintMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConstraintMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblConstraintMap)
                    .addComponent(constraintMapInfoIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelStepTwoLayout = new javax.swing.GroupLayout(jPanelStepTwo);
        jPanelStepTwo.setLayout(jPanelStepTwoLayout);
        jPanelStepTwoLayout.setHorizontalGroup(
            jPanelStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStepTwoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelHouseholdDensityMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPopulationMicroData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRegionMapCensusEnum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelHouseholdMicroData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelLandUseHouseholdMap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanelStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelStepTwoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelConstraintMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanelStepTwoLayout.setVerticalGroup(
            jPanelStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStepTwoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelRegionMapCensusEnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelPopulationMicroData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHouseholdMicroData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHouseholdDensityMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelLandUseHouseholdMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
            .addGroup(jPanelStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStepTwoLayout.createSequentialGroup()
                    .addContainerGap(511, Short.MAX_VALUE)
                    .addComponent(jPanelConstraintMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        btnPreviousStep.setText("Previous Step");
        btnPreviousStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousStepActionPerformed(evt);
            }
        });

        lblErrorMessages.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblErrorMessages.setForeground(new java.awt.Color(255, 0, 0));

        btnNextStep.setText("Next Step");
        btnNextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextStepActionPerformed(evt);
            }
        });

        menuFile.setText("File");

        menuItemExitApplication.setText("Exit Application");
        menuFile.add(menuItemExitApplication);

        jMenuBar.add(menuFile);

        menuHelp.setText("Help");
        menuHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuHelpMouseClicked(evt);
            }
        });
        jMenuBar.add(menuHelp);

        jMenu_About.setText("About");
        jMenu_About.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_AboutMouseClicked(evt);
            }
        });
        jMenuBar.add(jMenu_About);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreviousStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNextStep))
                    .addComponent(jPanelStepTwo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblErrorMessages)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelStepTwo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviousStep)
                    .addComponent(btnNextStep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblErrorMessages)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles about menu item, opens About pop-up
     * @param evt 
     */
    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    /**
     * Handles help menu item, opens the Help information for the current screen
     * @param evt 
     */
    private void menuHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuHelpMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_menuHelpMouseClicked

    /**
     * Saves the data, and takes the user back to Step 1
     * @param evt 
     */
    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        save();
        
        new StepOne(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed

    /**
     * Saves the data and takes the user to Step 3
     * @param evt 
     */
    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        save();
        
        new StepThree(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnNextStepActionPerformed

    /**
     * Handles the custom adding of a new land use combo class
     * @param evt 
     */
    private void btnLandUseAddCombinationClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLandUseAddCombinationClassActionPerformed

        if(this.digPopGUIInformation.getLandUseMapInformation().getAllClasses() == null
                || this.digPopGUIInformation.getLandUseMapInformation().getAllClasses().isEmpty() ){
            SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>()
            {
                @Override
                protected Void doInBackground()
                {
                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    Date dateobj = new Date();
                    System.out.println(df.format(dateobj));
                    try {
                        landUseLoadClassesResult = DigPopGUIUtilityClass.getClassesFromLandUseASCFile(digPopGUIInformation.getLandUseMapFilePath());
                    } catch (IOException ex) {
                        landUseLoadClassesResult.setErrorMessage("DigPopGUIUtilityClass.getClassesFromLandUseASCFile", ex.getMessage());
                        landUseLoadClassesResult.setSuccess(false);
                    }

                    dateobj = new Date();
                    System.out.println(df.format(dateobj));
                    return null;
                }

                @Override
                protected void done()
                {
                    if(landUseLoadClassesResult.isSuccess()){
                        ArrayList<String> classes = (ArrayList<String>)landUseLoadClassesResult.getValue();

                        digPopGUIInformation.getLandUseMapInformation().setAllClasses(classes);
                    }
                    jDialogLoadingFile.dispose();
                }
            };

            worker.execute();

            jDialogLoadingFile.pack();
            jDialogLoadingFile.setLocationRelativeTo(this);
            jDialogLoadingFile.setVisible(true);
        }
        else
        {
            landUseLoadClassesResult.setSuccess(true);
        }

        if(landUseLoadClassesResult.isSuccess()){
            LandUseDefineCombinationClasses landUseDefineCombinationClasses = new LandUseDefineCombinationClasses(this.digPopGUIInformation, this);
            landUseDefineCombinationClasses.setVisible(true);
            landUseDefineCombinationClasses.setAlwaysOnTop(true);
            landUseDefineCombinationClasses.setLocationRelativeTo(this);
            this.setVisible(false);
            this.setAlwaysOnTop(false);
        }

        landUseLoadClassesResult = new Result();
    }//GEN-LAST:event_btnLandUseAddCombinationClassActionPerformed

    private void censusEnumerationsInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_censusEnumerationsInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Census_File.toString());
    }//GEN-LAST:event_censusEnumerationsInfoIconMouseClicked

    private void regionMapInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regionMapInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Region_Map.toString());
    }//GEN-LAST:event_regionMapInfoIconMouseClicked

    private void populationMicroDataInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_populationMicroDataInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Population_File.toString());
    }//GEN-LAST:event_populationMicroDataInfoIconMouseClicked

    private void householdMicroDataInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_householdMicroDataInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Household_File.toString());
    }//GEN-LAST:event_householdMicroDataInfoIconMouseClicked

    private void householdDensityMapInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_householdDensityMapInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Household_Density_Map_File.toString());
    }//GEN-LAST:event_householdDensityMapInfoIconMouseClicked

    private void landuseMapInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_landuseMapInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Land_Use_Map_File.toString());
    }//GEN-LAST:event_landuseMapInfoIconMouseClicked

    private void landuseMapCommentInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_landuseMapCommentInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Land_Use_Map_File.toString());
    }//GEN-LAST:event_landuseMapCommentInfoIconMouseClicked

    private void landuseMapVacentClassesInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_landuseMapVacentClassesInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Vacant_Classes.toString());
    }//GEN-LAST:event_landuseMapVacentClassesInfoIconMouseClicked

    private void landuseMapVacentClassesDescriptionInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_landuseMapVacentClassesDescriptionInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Vacant_Class_Description.toString());
    }//GEN-LAST:event_landuseMapVacentClassesDescriptionInfoIconMouseClicked

    private void constraintMapInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_constraintMapInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Constraint_Map.toString());
    }//GEN-LAST:event_constraintMapInfoIconMouseClicked

    private void landuseMapCombinationClassesInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_landuseMapCombinationClassesInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Combination_Classes.toString());
    }//GEN-LAST:event_landuseMapCombinationClassesInfoIconMouseClicked

    private void Pop_HouseholdColumnInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Pop_HouseholdColumnInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Population_Tag.toString());
    }//GEN-LAST:event_Pop_HouseholdColumnInfoIconMouseClicked

    private void jComboBox_Pop_HouseholdColumnNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Pop_HouseholdColumnNameActionPerformed
        //TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_Pop_HouseholdColumnNameActionPerformed

    private void MembersInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MembersInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Household_Tag.toString());
    }//GEN-LAST:event_MembersInfoIconMouseClicked

    private void jComboBox_MembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_MembersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_MembersActionPerformed

    private void HouseholdKeyInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HouseholdKeyInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Household_Tag.toString());
    }//GEN-LAST:event_HouseholdKeyInfoIconMouseClicked

    private void jComboBox_HouseholdKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_HouseholdKeyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_HouseholdKeyActionPerformed

    private void KeyColumnInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_KeyColumnInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Region_Tag.toString());
    }//GEN-LAST:event_KeyColumnInfoIconMouseClicked

    private void jComboBox_KeyColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_KeyColumnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_KeyColumnActionPerformed

    private void PopulationInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PopulationInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Region_Tag.toString());
    }//GEN-LAST:event_PopulationInfoIconMouseClicked

    private void jComboBox_PopulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_PopulationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_PopulationActionPerformed

    private void VacanciesColumnInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VacanciesColumnInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Region_Tag.toString());
    }//GEN-LAST:event_VacanciesColumnInfoIconMouseClicked

    private void jComboBox_VacanciesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_VacanciesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_VacanciesActionPerformed

    private void HouseholdRegionTagInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HouseholdRegionTagInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepTwoInstructionNames.Region_Tag.toString());
    }//GEN-LAST:event_HouseholdRegionTagInfoIconMouseClicked

    private void jComboBox_HouseholdsRegionTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_HouseholdsRegionTagActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_HouseholdsRegionTagActionPerformed
    
    /**
     * Updates the error messages
     */
    private void setErrorMessage()
    {
        String errorMessageText = "<html>";
        
        for(String error : errors)
        {
            if(errorMessageText.length()>6)
            {
                errorMessageText = String.format(
                        "%s<br>%s", 
                        errorMessageText, 
                        error);
            }else{
                errorMessageText = errorMessageText + error;
            }
        }
        
        lblErrorMessages.setText(errorMessageText);
        ((JFrame)lblErrorMessages.getTopLevelAncestor()).pack();
    }
    
    /**
     * Populates the text fields from an existing save file
     */
    private void populateDataFieldsFromFile(){
        if(this.digPopGUIInformation == null){
            return;
        }
        
        if((this.digPopGUIInformation.getLandUseMapFilePath() != null) && !this.digPopGUIInformation.getLandUseMapFilePath().equals("")){
            txtLandUseHouseholdMap.setText(this.digPopGUIInformation.getLandUseMapFilePath());
            jPanelLandUseHouseholdMap.setVisible(true);
            jPanelHouseholdDensityMap.setVisible(false);
            
            UpdateLandUseClassCombinationTable();
            if((this.digPopGUIInformation.getLandUseMapInformation() != null) 
                    && (this.digPopGUIInformation.getLandUseMapInformation().getVacantClasses() != null))
            {
                this.txtVacantClassDescription.setText(this.digPopGUIInformation.getLandUseMapInformation().getVacantClasses().getDescription());
                this.txtVacantClasses.setText(this.digPopGUIInformation.getLandUseMapInformation().getVacantClasses().getClasses());
            }
            this.txtLandUseComment.setText(this.digPopGUIInformation.getLandUseMapInformation().getComment());
            
        } else{
            txtHouseholdDensityMap.setText(this.digPopGUIInformation.getHouseholdDensityMapFilePath());
            jPanelLandUseHouseholdMap.setVisible(false);
            jPanelHouseholdDensityMap.setVisible(true);
        }
                
        //Region map fields for goal relationship file <regions> tag parameters:
        Regions thisRegion = this.digPopGUIInformation.getGoalRelationshipFile().getRegions();
        
        txtRegionMap.setText(thisRegion.getMap() != null ? thisRegion.getMap() : this.digPopGUIInformation.getRegionMapFilePath()); //map
        txtCensusEnumerations.setText(thisRegion.getTable() != null ? thisRegion.getTable() : this.digPopGUIInformation.getCensusEnumerationsFilePath()); //table
        this.jComboBox_KeyColumn.setSelectedIndex(getIndexFromValue(thisRegion.getKey(), jComboBox_KeyColumn));
        this.jComboBox_Vacancies.setSelectedIndex(getIndexFromValue(thisRegion.getVacancies(), jComboBox_Vacancies));
        this.jComboBox_Population.setSelectedIndex(getIndexFromValue(thisRegion.getPopulation(),jComboBox_Population));
        this.jComboBox_HouseholdsRegionTag.setSelectedIndex(getIndexFromValue(thisRegion.getHouseholds(), jComboBox_HouseholdsRegionTag));
        
        //Household tag fields for goal relationship file:
        Households thisHousehold = this.digPopGUIInformation.getGoalRelationshipFile().getHouseholds();
        
        txtHouseholdMicroData.setText(thisHousehold.getTable() != null ? thisHousehold.getTable() : this.digPopGUIInformation.getHouseholdMicroDataFilePath()); //table
        this.jComboBox_Members.setSelectedIndex(getIndexFromValue(thisHousehold.getMembers(), jComboBox_Members));
        this.jComboBox_HouseholdKey.setSelectedIndex(getIndexFromValue(thisHousehold.getKey(),jComboBox_HouseholdKey));
        
        //Population tag fields for goal relationship file
        Population thisPopulation = this.digPopGUIInformation.getGoalRelationshipFile().getPopulation();
        
        txtPopulationMicroData.setText(thisPopulation.getTable() != null ? thisPopulation.getTable() : this.digPopGUIInformation.getPopulationMicroDataFilePath()); //table
        if(thisPopulation.getHousehold() != null){
            this.jComboBox_Pop_HouseholdColumnName.setSelectedIndex(getIndexFromValue(thisPopulation.getHousehold(),jComboBox_Pop_HouseholdColumnName));
        }
        pack();
    }
    
    /**
     * Finds the matching index of the value in lookuplist
     * @param value - the value to find
     * @param lookupList - the lookup list to search
     * @return - the index of the located item, or 0 if not found
     */
    private Integer getIndexFromValue(String value, JComboBox lookupList){
        for(int i = 0; i<lookupList.getItemCount(); i++){
            if(lookupList.getItemAt(i).toString().equals(value)){
                return i;
            }
        }
        return 0;
    }
    
    /**
     * Updates the list of combination classes for land use maps
     */
    public void UpdateLandUseClassCombinationTable(){
        if(this.digPopGUIInformation.getLandUseMapInformation().getLandUseMapClassCombinations().size()>0){
            this.landUseCombinationTableItemModel.fireTableDataChanged();
            pack();
        }
    }
    
    /**
     * Saves the information provided
     */
    private void save(){
        VacantClass vacant = new VacantClass(this.txtVacantClassDescription.getText(), this.txtVacantClasses.getText());
        this.digPopGUIInformation.getLandUseMapInformation().setVacantClasses(vacant);
        this.digPopGUIInformation.getLandUseMapInformation().setComment(this.txtLandUseComment.getText());
        
        //Save Region map fields for goal relationship file <regions> tag parameters:
        this.digPopGUIInformation.getGoalRelationshipFile()
                .setRegions(new Regions(txtRegionMap.getText()
                        , txtCensusEnumerations.getText()
                        , this.jComboBox_KeyColumn.getSelectedItem().toString()
                        , this.jComboBox_Vacancies.getSelectedItem().toString()
                        , this.jComboBox_Population.getSelectedItem().toString()
                        , this.jComboBox_HouseholdsRegionTag.getSelectedItem().toString()));
                
        //Save Household tag fields for goal relationship file:
        this.digPopGUIInformation.getGoalRelationshipFile()
                .setHouseholds(new Households(txtHouseholdMicroData.getText()
                        , this.jComboBox_Members.getSelectedItem().toString()
                        , this.jComboBox_HouseholdKey.getSelectedItem().toString()));
                
        //Save Population tag fields for goal relationship file
        if(this.jComboBox_Pop_HouseholdColumnName.getSelectedItem() != null){
            this.digPopGUIInformation.getGoalRelationshipFile()
                .setPopulation(new Population(txtPopulationMicroData.getText()
                        ,this.jComboBox_Pop_HouseholdColumnName.getSelectedItem().toString()));
        }
        saveToFile();
    }
    
    /**
     * Saves the information to the DigPop object
     */
    private void saveToFile(){
        //Save to file
        Result    result = DigPopGUIUtilityClass.saveDigPopGUIInformationSaveFile(
                    this.digPopGUIInformation,
                    this.digPopGUIInformation.getFilePath());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HouseholdKeyInfoIcon;
    private javax.swing.JLabel HouseholdRegionTagInfoIcon;
    private javax.swing.JLabel KeyColumnInfoIcon;
    private javax.swing.JLabel MembersInfoIcon;
    private javax.swing.JLabel Pop_HouseholdColumnInfoIcon;
    private javax.swing.JLabel PopulationInfoIcon;
    private javax.swing.JLabel VacanciesColumnInfoIcon;
    private javax.swing.JButton btnLandUseAddCombinationClass;
    private javax.swing.JButton btnNextStep;
    private javax.swing.JButton btnPreviousStep;
    private javax.swing.JLabel censusEnumerationsInfoIcon;
    private javax.swing.JLabel constraintMapInfoIcon;
    private javax.swing.JLabel householdDensityMapInfoIcon;
    private javax.swing.JLabel householdMicroDataInfoIcon;
    private javax.swing.JComboBox<String> jComboBox_HouseholdKey;
    private javax.swing.JComboBox<String> jComboBox_HouseholdsRegionTag;
    private javax.swing.JComboBox<String> jComboBox_KeyColumn;
    private javax.swing.JComboBox<String> jComboBox_Members;
    private javax.swing.JComboBox<String> jComboBox_Pop_HouseholdColumnName;
    private javax.swing.JComboBox<String> jComboBox_Population;
    private javax.swing.JComboBox<String> jComboBox_Vacancies;
    private javax.swing.JDialog jDialogLoadingFile;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel_HouseholdRegionTag;
    private javax.swing.JLabel jLabel_Household_KeyColumnName;
    private javax.swing.JLabel jLabel_KeyColumn;
    private javax.swing.JLabel jLabel_Members;
    private javax.swing.JLabel jLabel_Pop_HouseholdColumnName;
    private javax.swing.JLabel jLabel_PopulationColumnName;
    private javax.swing.JLabel jLabel_VacanciesColumn;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JPanel jPanelConstraintMap;
    private javax.swing.JPanel jPanelHouseholdDensityMap;
    private javax.swing.JPanel jPanelHouseholdMicroData;
    private javax.swing.JPanel jPanelLandUseHouseholdMap;
    private javax.swing.JPanel jPanelPopulationMicroData;
    private javax.swing.JPanel jPanelRegionMapCensusEnum;
    private javax.swing.JPanel jPanelStepTwo;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableLandUseClassCombinations;
    private javax.swing.JLabel landuseMapCombinationClassesInfoIcon;
    private javax.swing.JLabel landuseMapCommentInfoIcon;
    private javax.swing.JLabel landuseMapInfoIcon;
    private javax.swing.JLabel landuseMapVacentClassesDescriptionInfoIcon;
    private javax.swing.JLabel landuseMapVacentClassesInfoIcon;
    private javax.swing.JLabel lblCensusEnumerations;
    private javax.swing.JLabel lblConstraintMap;
    private javax.swing.JLabel lblErrorMessages;
    private javax.swing.JLabel lblHouseholdDensityMap;
    private javax.swing.JLabel lblHouseholdMicroData;
    private javax.swing.JLabel lblLanduseMap;
    private javax.swing.JLabel lblPopulationMicroData;
    private javax.swing.JLabel lblRegionMap;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemExitApplication;
    private javax.swing.JLabel populationMicroDataInfoIcon;
    private javax.swing.JLabel regionMapInfoIcon;
    private javax.swing.JTable tblConstraintMaps;
    private javax.swing.JTextField txtCensusEnumerations;
    private javax.swing.JTextField txtHouseholdDensityMap;
    private javax.swing.JTextField txtHouseholdMicroData;
    private javax.swing.JTextField txtLandUseComment;
    private javax.swing.JTextField txtLandUseHouseholdMap;
    private javax.swing.JTextField txtPopulationMicroData;
    private javax.swing.JTextField txtRegionMap;
    private javax.swing.JTextField txtVacantClassDescription;
    private javax.swing.JTextField txtVacantClasses;
    // End of variables declaration//GEN-END:variables
}
