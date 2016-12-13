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
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.RunFile;
import cerl.gui.utilities.StepSevenInstructionNames;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author ajohnson
 */
public class StepSeven extends javax.swing.JFrame {
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_SEVEN_HELP_FILE_NAME.toString();
    private final String DEFAULT_NEW_FILE_NAME = "last-run.properties";
    private final FileType DEFAULT_NEW_FILE_TYPE = FileType.TXT;
    private final String[] TRUE_FALSE_VALUES = { "", "TRUE", "FALSE" };
    private final DigPopGUIInformation digPopGUIInformation;
    private RunFile RunProperties;
    
    /**
     * Creates new form Step 7 - Run File form
     */
    public StepSeven() {
        initComponents();
        this.digPopGUIInformation = new DigPopGUIInformation();
        this.RunProperties = new RunFile();
    }
    
    /**
     * Creates new Step 7 - Run file form with existing data
     * @param digPopGUIInformation - The digPop Log
     */
    public StepSeven(DigPopGUIInformation digPopGUIInformation) {
        initComponents();
        this.digPopGUIInformation = digPopGUIInformation;
        
        if(this.digPopGUIInformation.getRunFile() != null){
            this.RunProperties = this.digPopGUIInformation.getRunFile();
            setDataOnForm();
        } else{
            this.RunProperties = new RunFile();
            populateData();
        }
    }

    /**
     * If an existing run file exists, populate from the existing data
     */
    private void populateData(){
        String saveFilePath = this.digPopGUIInformation.getFilePath();
        
        if(saveFilePath.contains(".XML")){
            saveFilePath = saveFilePath.substring(0, saveFilePath.lastIndexOf("\\")+1);
        }
        
        //create new run file
        File newRunFile = new File(String.format("%s\\%s", saveFilePath, DEFAULT_NEW_FILE_NAME));
        
        //check if existing file exists
        Result result = FileUtility.VerifySecondaryFileExists(newRunFile, DEFAULT_NEW_FILE_TYPE);
        
        //If yes - populate data from this file
        if(result.isSuccess()){
            result = FileUtility.ReadTextFile(newRunFile.getPath());
            //result = FileUtility.ParseXMLFileIntoSpecifiedObject(newRunFile.getPath(), RunFile.class);
            this.RunProperties = new RunFile(result.getValue().toString());
            setDataOnForm();
        }
        this.jLabel_Errors.setText("All data fields are required");
    }
    
    /**
     * Sets all the data fields on the form from the digPop object
     */
    private void setDataOnForm(){
        //used for the phase 1 time limit, phase 2 random %, phase 3/4 save, phase 3 time, & phase 4 time limit
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        //used for the phase 2 skip tracts & phase 2 prob delta, 
        DecimalFormat twoDecimals = new DecimalFormat("#.##");
        oneDecimal.setRoundingMode(RoundingMode.HALF_UP);
        twoDecimals.setRoundingMode(RoundingMode.HALF_UP);

        this.jTextField_NameOfRun.setText(this.RunProperties.getRunName() != null ? this.RunProperties.getRunName() : "");
        this.jComboBox_LogPhase1Results.setSelectedIndex(this.RunProperties.getDo_dump_number_archtypes() != null ? getTrueFalseIndexValue(this.RunProperties.getDo_dump_number_archtypes()) : 0);
        this.jComboBox_LogQualityEval.setSelectedIndex(this.RunProperties.getDo_dump_statistics() != null ? getTrueFalseIndexValue(this.RunProperties.getDo_dump_statistics()) : 0);
        this.jComboBox_HouseholdArchetype.setSelectedIndex(this.RunProperties.getDo_write_all_hoh_fields() != null ? getTrueFalseIndexValue(this.RunProperties.getDo_write_all_hoh_fields()) : 0);
        this.jComboBox_PopulationArchetype.setSelectedIndex(this.RunProperties.getDo_write_all_pop_fields() != null ? getTrueFalseIndexValue(this.RunProperties.getDo_write_all_pop_fields()) : 0);
        this.jTextField_FinalRealizationIndex.setText(this.RunProperties.getFinal_rzn_num() != null ? this.RunProperties.getFinal_rzn_num().toString() : "");
        this.jTextField_FirstRealizationIndex.setText(this.RunProperties.getFirst_rzn_num() != null ? this.RunProperties.getFirst_rzn_num().toString() : "");
        this.jTextField_RandomNumberSeed.setText(this.RunProperties.getInitial_seed() != null ? this.RunProperties.getInitial_seed().toString() : "");
        this.jComboBox_FirstCensusTract.setSelectedIndex(this.RunProperties.getOnly_one_region() != null ? getTrueFalseIndexValue(this.RunProperties.getOnly_one_region()) : 0);
        this.jTextField_OutputDirectory.setText(this.RunProperties.getOutput_dir() != null ? this.RunProperties.getOutput_dir() : "");
        this.jTextField_ParallelThreads.setText(this.RunProperties.getParallel_threads() != null ? this.RunProperties.getParallel_threads().toString() : "");
        this.jTextField_Phase1TimeLimit.setText(this.RunProperties.getPhase1_time_limit() != null ? oneDecimal.format(this.RunProperties.getPhase1_time_limit()) : "");
        this.jTextField_Phase2RandomPlacement.setText(this.RunProperties.getPhase2_random_tract_prob() != null ? oneDecimal.format(this.RunProperties.getPhase2_random_tract_prob()) : "");
        this.jTextField_Phase2SkipTractsProb.setText(this.RunProperties.getPhase2_tract_skip_prob_init() != null ? twoDecimals.format(this.RunProperties.getPhase2_tract_skip_prob_init()) : "");
        this.jTextField_Phase2SkipTractsDelta.setText(this.RunProperties.getPhase2_tract_skip_prob_delta() != null ? twoDecimals.format(this.RunProperties.getPhase2_tract_skip_prob_delta()) : "");
        this.jTextField_Phase34SaveInterval.setText(this.RunProperties.getPhase3_save_intermediate() != null ? oneDecimal.format(this.RunProperties.getPhase3_save_intermediate()) : "");
        this.jComboBox_Phase3Skip.setSelectedIndex(this.RunProperties.getPhase3_skip() != null ? getTrueFalseIndexValue(this.RunProperties.getPhase3_skip()) : 0);
        this.jTextField_Phase3TimeLimit.setText(this.RunProperties.getPhase3_time_limit() != null ? oneDecimal.format(this.RunProperties.getPhase3_time_limit()) : "");
        this.jTextField_Phase4_Lags.setText(this.RunProperties.getPhase4_num_lags() != null ? this.RunProperties.getPhase4_num_lags().toString() : "");
        this.jComboBox_Phase4Save.setSelectedIndex(this.RunProperties.getPhase4_save_both_ends() != null ? getTrueFalseIndexValue(this.RunProperties.getPhase4_save_both_ends()) : 0);
        this.jComboBox_Phase4Skip.setSelectedIndex(this.RunProperties.getPhase4_skip() != null ? getTrueFalseIndexValue(this.RunProperties.getPhase4_skip()) : 0);
        this.jTextField_Phase4TimeLimit.setText(this.RunProperties.getPhase4_time_limit() != null ? oneDecimal.format(this.RunProperties.getPhase4_time_limit()) : "");
    }
    
    /**
     * Used to set the values from a previous run.
     * @param value
     * @return 
     */
    private Integer getTrueFalseIndexValue(Boolean value){
        //Must change to string otherwise "true" always true in if statement
        switch(value.toString()){
            case "true":
                return 1;
            case "false":
                return 2;
            default:
                return 0;
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

        jPanel_Phase2 = new javax.swing.JPanel();
        jLabel_Phase2RandomPlacement = new javax.swing.JLabel();
        jLabel_Phase2SkipTracts = new javax.swing.JLabel();
        jLabel_Phase2SkippedTracts = new javax.swing.JLabel();
        Phase2RandomPercentInfoIcon = new javax.swing.JLabel();
        jTextField_Phase2RandomPlacement = new javax.swing.JTextField();
        jTextField_Phase2SkipTractsProb = new javax.swing.JTextField();
        Phase2SkipTractsInfoIcon = new javax.swing.JLabel();
        jTextField_Phase2SkipTractsDelta = new javax.swing.JTextField();
        Phase2SkippedProbabilityInfoIcon = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel_CreateRunFile = new javax.swing.JLabel();
        jLabel_Errors = new javax.swing.JLabel();
        jPanel_Run = new javax.swing.JPanel();
        jLabel_NameOfRun = new javax.swing.JLabel();
        nameOfRunInfoIcon = new javax.swing.JLabel();
        jTextField_NameOfRun = new javax.swing.JTextField();
        jPanel_LogResults = new javax.swing.JPanel();
        LogQualityInfoIcon = new javax.swing.JLabel();
        Phase1ResultsLogInfoIcon = new javax.swing.JLabel();
        jLabel_LogPhase1Results = new javax.swing.JLabel();
        jLabel_LogQualityEval = new javax.swing.JLabel();
        jComboBox_LogPhase1Results = new javax.swing.JComboBox<>();
        jComboBox_LogQualityEval = new javax.swing.JComboBox<>();
        jPanel_Archetype = new javax.swing.JPanel();
        jLabel_HouseholdArchetype = new javax.swing.JLabel();
        HouseholdArchetypeInfoIcon = new javax.swing.JLabel();
        PopulationAchetypeInfoIcon = new javax.swing.JLabel();
        jLabel_PopulationArchetype = new javax.swing.JLabel();
        jComboBox_HouseholdArchetype = new javax.swing.JComboBox<>();
        jComboBox_PopulationArchetype = new javax.swing.JComboBox<>();
        jPanel_RealizationIndex = new javax.swing.JPanel();
        jTextField_FinalRealizationIndex = new javax.swing.JTextField();
        FinalRealizationIndexInfoIcon = new javax.swing.JLabel();
        jLabel_FinalRealizationIndex = new javax.swing.JLabel();
        jComboBox_FirstCensusTract = new javax.swing.JComboBox<>();
        FirstCensusTractInfoIcon = new javax.swing.JLabel();
        jLabel_FirstCensusTract = new javax.swing.JLabel();
        jPanel_Seeds = new javax.swing.JPanel();
        jLabel_OutputDirectory = new javax.swing.JLabel();
        OutputDirectoryInfoIcon = new javax.swing.JLabel();
        jTextField_OutputDirectory = new javax.swing.JTextField();
        jTextField_ParallelThreads = new javax.swing.JTextField();
        ParallelThreadsInfoIcon = new javax.swing.JLabel();
        jLabel_ParallelThreads = new javax.swing.JLabel();
        jTextField_Phase1TimeLimit = new javax.swing.JTextField();
        Phase1TimeLimitInfoIcon = new javax.swing.JLabel();
        jLabel_Phase1TimeLimit = new javax.swing.JLabel();
        jPanel_Phase4 = new javax.swing.JPanel();
        jLabel_Phase4_Lags = new javax.swing.JLabel();
        phase4LagsInfoIcon = new javax.swing.JLabel();
        jTextField_Phase4_Lags = new javax.swing.JTextField();
        Phase4SaveInfoIcon = new javax.swing.JLabel();
        jLabel_Phase4Save = new javax.swing.JLabel();
        jLabel_Phase4Skip = new javax.swing.JLabel();
        SkipPhase4InfoIcon = new javax.swing.JLabel();
        jTextField_Phase4TimeLimit = new javax.swing.JTextField();
        Phase4TimeLimitInfoIcon = new javax.swing.JLabel();
        jLabel_Phase4_TimeLimit = new javax.swing.JLabel();
        jComboBox_Phase4Save = new javax.swing.JComboBox<>();
        jComboBox_Phase4Skip = new javax.swing.JComboBox<>();
        jPanel_Phase3and4 = new javax.swing.JPanel();
        jLabel_Phase34SaveInterval = new javax.swing.JLabel();
        Phase34SaveInfoIcon = new javax.swing.JLabel();
        jTextField_Phase34SaveInterval = new javax.swing.JTextField();
        jPanel_Phase3 = new javax.swing.JPanel();
        jLabel_Phase3Skip = new javax.swing.JLabel();
        SkipPhase3InfoIcon = new javax.swing.JLabel();
        jTextField_Phase3TimeLimit = new javax.swing.JTextField();
        phase3TimeLimitInfoIcon = new javax.swing.JLabel();
        jLabel_Phase3TimeLimit = new javax.swing.JLabel();
        jComboBox_Phase3Skip = new javax.swing.JComboBox<>();
        btn_Save = new javax.swing.JButton();
        btnPreviousStep = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel_FirstRealizationIndex = new javax.swing.JLabel();
        FirstRealizationIndexInfoIcon = new javax.swing.JLabel();
        jTextField_FirstRealizationIndex = new javax.swing.JTextField();
        jTextField_RandomNumberSeed = new javax.swing.JTextField();
        randomNumberSeedInfoIcon = new javax.swing.JLabel();
        jLabel_RandomNumberSeed = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenu_File = new javax.swing.JMenu();
        jMenu_Help = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Step Seven");

        jPanel_Phase2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_Phase2RandomPlacement.setText("Phase 2 - Random Placement Percentage");

        jLabel_Phase2SkipTracts.setText("Phase 2 - Skip Tracts Probability");

        jLabel_Phase2SkippedTracts.setText("Phase 2 - Skipped Tracts Probability Delta");

        Phase2RandomPercentInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase2RandomPercentInfoIcon.setToolTipText("Help Infomation for Phase 2 Random Placement Percentage");
        Phase2RandomPercentInfoIcon.setIconTextGap(0);
        Phase2RandomPercentInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase2RandomPercentInfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase2RandomPlacement.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase2RandomPlacementFocusLost(evt);
            }
        });

        jTextField_Phase2SkipTractsProb.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase2SkipTractsProbFocusLost(evt);
            }
        });

        Phase2SkipTractsInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase2SkipTractsInfoIcon.setToolTipText("Help Infomation for Phase 2 Skip Tracts Probability");
        Phase2SkipTractsInfoIcon.setIconTextGap(0);
        Phase2SkipTractsInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase2SkipTractsInfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase2SkipTractsDelta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase2SkipTractsDeltaFocusLost(evt);
            }
        });

        Phase2SkippedProbabilityInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase2SkippedProbabilityInfoIcon.setToolTipText("Help Infomation for Phase 2 Skipped Tracts Probability Delta");
        Phase2SkippedProbabilityInfoIcon.setIconTextGap(0);
        Phase2SkippedProbabilityInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase2SkippedProbabilityInfoIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Phase2Layout = new javax.swing.GroupLayout(jPanel_Phase2);
        jPanel_Phase2.setLayout(jPanel_Phase2Layout);
        jPanel_Phase2Layout.setHorizontalGroup(
            jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_Phase2Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase2RandomPlacement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Phase2RandomPercentInfoIcon))
                    .addGroup(jPanel_Phase2Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase2SkipTracts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Phase2SkipTractsInfoIcon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField_Phase2SkipTractsProb)
                    .addComponent(jTextField_Phase2RandomPlacement, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel_Phase2SkippedTracts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Phase2SkippedProbabilityInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Phase2SkipTractsDelta)
                .addContainerGap())
        );
        jPanel_Phase2Layout.setVerticalGroup(
            jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Phase2SkippedProbabilityInfoIcon)
                    .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_Phase2RandomPlacement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Phase2SkippedTracts))
                    .addComponent(jTextField_Phase2SkipTractsDelta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Phase2RandomPercentInfoIcon)
                        .addComponent(jLabel_Phase2RandomPlacement)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Phase2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_Phase2SkipTractsProb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase2Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase2SkipTracts)
                        .addGap(6, 6, 6))
                    .addComponent(Phase2SkipTractsInfoIcon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel_CreateRunFile.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel_CreateRunFile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_CreateRunFile.setText("Create Run File");

        jLabel_Errors.setForeground(java.awt.Color.red);
        jLabel_Errors.setText("All Values are Required");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_CreateRunFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel_Errors)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel_CreateRunFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_Errors))
        );

        jPanel_Run.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_NameOfRun.setText("Name of Run");

        nameOfRunInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        nameOfRunInfoIcon.setToolTipText("Help Infomation for the Name of the Run");
        nameOfRunInfoIcon.setIconTextGap(0);
        nameOfRunInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nameOfRunInfoIconMouseClicked(evt);
            }
        });

        jTextField_NameOfRun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_NameOfRunFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel_RunLayout = new javax.swing.GroupLayout(jPanel_Run);
        jPanel_Run.setLayout(jPanel_RunLayout);
        jPanel_RunLayout.setHorizontalGroup(
            jPanel_RunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_RunLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_NameOfRun)
                .addGap(4, 4, 4)
                .addComponent(nameOfRunInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_NameOfRun)
                .addContainerGap())
        );
        jPanel_RunLayout.setVerticalGroup(
            jPanel_RunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_RunLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_RunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_NameOfRun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_RunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nameOfRunInfoIcon)
                        .addComponent(jLabel_NameOfRun)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_LogResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        LogQualityInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        LogQualityInfoIcon.setToolTipText("Help Infomation for logging the quality evaluation reports");
        LogQualityInfoIcon.setIconTextGap(0);
        LogQualityInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogQualityInfoIconMouseClicked(evt);
            }
        });

        Phase1ResultsLogInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase1ResultsLogInfoIcon.setToolTipText("Help Infomation for Logging the results of Phase 1");
        Phase1ResultsLogInfoIcon.setIconTextGap(0);
        Phase1ResultsLogInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase1ResultsLogInfoIconMouseClicked(evt);
            }
        });

        jLabel_LogPhase1Results.setText("Log the results of phase 1?");
        jLabel_LogPhase1Results.setPreferredSize(new java.awt.Dimension(250, 14));

        jLabel_LogQualityEval.setText("Log the quality evaluation reports between phases?");
        jLabel_LogQualityEval.setPreferredSize(new java.awt.Dimension(250, 14));

        jComboBox_LogPhase1Results.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_LogPhase1Results.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_LogPhase1ResultsActionPerformed(evt);
            }
        });

        jComboBox_LogQualityEval.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_LogQualityEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_LogQualityEvalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_LogResultsLayout = new javax.swing.GroupLayout(jPanel_LogResults);
        jPanel_LogResults.setLayout(jPanel_LogResultsLayout);
        jPanel_LogResultsLayout.setHorizontalGroup(
            jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_LogResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_LogQualityEval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_LogResultsLayout.createSequentialGroup()
                        .addComponent(jLabel_LogPhase1Results, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_LogResultsLayout.createSequentialGroup()
                        .addComponent(LogQualityInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox_LogQualityEval, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_LogResultsLayout.createSequentialGroup()
                        .addComponent(Phase1ResultsLogInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_LogPhase1Results, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel_LogResultsLayout.setVerticalGroup(
            jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_LogResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Phase1ResultsLogInfoIcon)
                    .addComponent(jComboBox_LogPhase1Results, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_LogPhase1Results, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_LogQualityEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_LogResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBox_LogQualityEval, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LogQualityInfoIcon, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Archetype.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_HouseholdArchetype.setText("Does each Household record contain a full copy of the archetype record?");

        HouseholdArchetypeInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        HouseholdArchetypeInfoIcon.setToolTipText("Help Infomation for Household archetype record");
        HouseholdArchetypeInfoIcon.setIconTextGap(0);
        HouseholdArchetypeInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HouseholdArchetypeInfoIconMouseClicked(evt);
            }
        });

        PopulationAchetypeInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        PopulationAchetypeInfoIcon.setToolTipText("Help Infomation for the Population archetype");
        PopulationAchetypeInfoIcon.setIconTextGap(0);
        PopulationAchetypeInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PopulationAchetypeInfoIconMouseClicked(evt);
            }
        });

        jLabel_PopulationArchetype.setText("Does each Population record contain a full copy of the archetype record?");

        jComboBox_HouseholdArchetype.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_HouseholdArchetype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_HouseholdArchetypeActionPerformed(evt);
            }
        });

        jComboBox_PopulationArchetype.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_PopulationArchetype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_PopulationArchetypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_ArchetypeLayout = new javax.swing.GroupLayout(jPanel_Archetype);
        jPanel_Archetype.setLayout(jPanel_ArchetypeLayout);
        jPanel_ArchetypeLayout.setHorizontalGroup(
            jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ArchetypeLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ArchetypeLayout.createSequentialGroup()
                        .addComponent(jLabel_PopulationArchetype)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(PopulationAchetypeInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_PopulationArchetype, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ArchetypeLayout.createSequentialGroup()
                        .addComponent(jLabel_HouseholdArchetype)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(HouseholdArchetypeInfoIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_HouseholdArchetype, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_ArchetypeLayout.setVerticalGroup(
            jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ArchetypeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox_HouseholdArchetype, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HouseholdArchetypeInfoIcon, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel_HouseholdArchetype))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ArchetypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(PopulationAchetypeInfoIcon)
                        .addComponent(jComboBox_PopulationArchetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel_PopulationArchetype))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_RealizationIndex.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextField_FinalRealizationIndex.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_FinalRealizationIndexFocusLost(evt);
            }
        });

        FinalRealizationIndexInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        FinalRealizationIndexInfoIcon.setToolTipText("Help Infomation for Final Realization Index");
        FinalRealizationIndexInfoIcon.setIconTextGap(0);
        FinalRealizationIndexInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FinalRealizationIndexInfoIconMouseClicked(evt);
            }
        });

        jLabel_FinalRealizationIndex.setText("Final Realization Index");

        jComboBox_FirstCensusTract.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_FirstCensusTract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_FirstCensusTractActionPerformed(evt);
            }
        });

        FirstCensusTractInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        FirstCensusTractInfoIcon.setToolTipText("Help Infomation for First Census Tract");
        FirstCensusTractInfoIcon.setIconTextGap(0);
        FirstCensusTractInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FirstCensusTractInfoIconMouseClicked(evt);
            }
        });

        jLabel_FirstCensusTract.setText("Use only the first census tract?");

        javax.swing.GroupLayout jPanel_RealizationIndexLayout = new javax.swing.GroupLayout(jPanel_RealizationIndex);
        jPanel_RealizationIndex.setLayout(jPanel_RealizationIndexLayout);
        jPanel_RealizationIndexLayout.setHorizontalGroup(
            jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_RealizationIndexLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_RealizationIndexLayout.createSequentialGroup()
                        .addComponent(jLabel_FirstCensusTract)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(FirstCensusTractInfoIcon))
                    .addGroup(jPanel_RealizationIndexLayout.createSequentialGroup()
                        .addComponent(jLabel_FinalRealizationIndex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FinalRealizationIndexInfoIcon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField_FinalRealizationIndex)
                    .addComponent(jComboBox_FirstCensusTract, 0, 75, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_RealizationIndexLayout.setVerticalGroup(
            jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_RealizationIndexLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_FinalRealizationIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FinalRealizationIndexInfoIcon)
                    .addComponent(jLabel_FinalRealizationIndex, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_RealizationIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox_FirstCensusTract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FirstCensusTractInfoIcon)
                    .addComponent(jLabel_FirstCensusTract))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Seeds.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_OutputDirectory.setText("Output Directory");
        jLabel_OutputDirectory.setPreferredSize(new java.awt.Dimension(127, 14));

        OutputDirectoryInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        OutputDirectoryInfoIcon.setToolTipText("Help Infomation for Output Directory");
        OutputDirectoryInfoIcon.setIconTextGap(0);
        OutputDirectoryInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OutputDirectoryInfoIconMouseClicked(evt);
            }
        });

        jTextField_OutputDirectory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_OutputDirectoryFocusLost(evt);
            }
        });

        jTextField_ParallelThreads.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_ParallelThreadsFocusLost(evt);
            }
        });

        ParallelThreadsInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        ParallelThreadsInfoIcon.setToolTipText("Help Infomation for Parallel Threads");
        ParallelThreadsInfoIcon.setIconTextGap(0);
        ParallelThreadsInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ParallelThreadsInfoIconMouseClicked(evt);
            }
        });

        jLabel_ParallelThreads.setText("Number of parallel threads");

        jTextField_Phase1TimeLimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase1TimeLimitFocusLost(evt);
            }
        });

        Phase1TimeLimitInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase1TimeLimitInfoIcon.setToolTipText("Help Infomation for Phase 1 Time Limit");
        Phase1TimeLimitInfoIcon.setIconTextGap(0);
        Phase1TimeLimitInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase1TimeLimitInfoIconMouseClicked(evt);
            }
        });

        jLabel_Phase1TimeLimit.setText("Phase 1 Time limit");
        jLabel_Phase1TimeLimit.setPreferredSize(new java.awt.Dimension(127, 14));

        javax.swing.GroupLayout jPanel_SeedsLayout = new javax.swing.GroupLayout(jPanel_Seeds);
        jPanel_Seeds.setLayout(jPanel_SeedsLayout);
        jPanel_SeedsLayout.setHorizontalGroup(
            jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SeedsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_SeedsLayout.createSequentialGroup()
                        .addComponent(jLabel_ParallelThreads)
                        .addGap(18, 18, 18)
                        .addComponent(ParallelThreadsInfoIcon))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_SeedsLayout.createSequentialGroup()
                        .addComponent(jLabel_OutputDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(OutputDirectoryInfoIcon))
                    .addGroup(jPanel_SeedsLayout.createSequentialGroup()
                        .addComponent(jLabel_Phase1TimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Phase1TimeLimitInfoIcon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_Phase1TimeLimit)
                    .addComponent(jTextField_OutputDirectory)
                    .addComponent(jTextField_ParallelThreads))
                .addContainerGap())
        );
        jPanel_SeedsLayout.setVerticalGroup(
            jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SeedsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_SeedsLayout.createSequentialGroup()
                        .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField_OutputDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_SeedsLayout.createSequentialGroup()
                                .addComponent(jLabel_OutputDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addComponent(OutputDirectoryInfoIcon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_ParallelThreads)
                            .addComponent(ParallelThreadsInfoIcon, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jTextField_ParallelThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SeedsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Phase1TimeLimitInfoIcon)
                    .addComponent(jLabel_Phase1TimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_Phase1TimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Phase4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_Phase4_Lags.setText("Phase 4 - Number of Lags");

        phase4LagsInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        phase4LagsInfoIcon.setToolTipText("Help Infomation for Phase 4 Number of Lags");
        phase4LagsInfoIcon.setIconTextGap(0);
        phase4LagsInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                phase4LagsInfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase4_Lags.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase4_LagsFocusLost(evt);
            }
        });

        Phase4SaveInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase4SaveInfoIcon.setToolTipText("Help Infomation for Phase 4 Save Both Ends");
        Phase4SaveInfoIcon.setIconTextGap(0);
        Phase4SaveInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase4SaveInfoIconMouseClicked(evt);
            }
        });

        jLabel_Phase4Save.setText("Phase 4 - Save both ends?");

        jLabel_Phase4Skip.setText("Skip Phase 4?");

        SkipPhase4InfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        SkipPhase4InfoIcon.setToolTipText("Help Infomation for Skip Phase 4");
        SkipPhase4InfoIcon.setIconTextGap(0);
        SkipPhase4InfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SkipPhase4InfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase4TimeLimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase4TimeLimitFocusLost(evt);
            }
        });

        Phase4TimeLimitInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase4TimeLimitInfoIcon.setToolTipText("Help Infomation for Phase 4 Time Limit");
        Phase4TimeLimitInfoIcon.setIconTextGap(0);
        Phase4TimeLimitInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase4TimeLimitInfoIconMouseClicked(evt);
            }
        });

        jLabel_Phase4_TimeLimit.setText("Phase 4 Time Limit");

        jComboBox_Phase4Save.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_Phase4Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Phase4SaveActionPerformed(evt);
            }
        });

        jComboBox_Phase4Skip.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_Phase4Skip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Phase4SkipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Phase4Layout = new javax.swing.GroupLayout(jPanel_Phase4);
        jPanel_Phase4.setLayout(jPanel_Phase4Layout);
        jPanel_Phase4Layout.setHorizontalGroup(
            jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase4Save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Phase4SaveInfoIcon))
                    .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase4_Lags)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(phase4LagsInfoIcon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField_Phase4_Lags)
                    .addComponent(jComboBox_Phase4Save, 0, 75, Short.MAX_VALUE))
                .addGap(155, 155, 155)
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase4_TimeLimit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Phase4TimeLimitInfoIcon))
                    .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                        .addComponent(jLabel_Phase4Skip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SkipPhase4InfoIcon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                        .addComponent(jComboBox_Phase4Skip, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextField_Phase4TimeLimit, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel_Phase4Layout.setVerticalGroup(
            jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_Phase4_Lags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField_Phase4TimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Phase4TimeLimitInfoIcon)
                        .addComponent(jLabel_Phase4_TimeLimit))
                    .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(phase4LagsInfoIcon)
                        .addComponent(jLabel_Phase4_Lags)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Phase4SaveInfoIcon)
                    .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Phase4Save)
                        .addComponent(jComboBox_Phase4Save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBox_Phase4Skip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(SkipPhase4InfoIcon)
                        .addComponent(jLabel_Phase4Skip)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Phase3and4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_Phase34SaveInterval.setText("Phase 3 and 4 Intermediate Save Interval");

        Phase34SaveInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        Phase34SaveInfoIcon.setToolTipText("Help Infomation for Phase 3 and 4 Intermediate Save Interval");
        Phase34SaveInfoIcon.setIconTextGap(0);
        Phase34SaveInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Phase34SaveInfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase34SaveInterval.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase34SaveIntervalFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Phase3and4Layout = new javax.swing.GroupLayout(jPanel_Phase3and4);
        jPanel_Phase3and4.setLayout(jPanel_Phase3and4Layout);
        jPanel_Phase3and4Layout.setHorizontalGroup(
            jPanel_Phase3and4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase3and4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_Phase34SaveInterval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Phase34SaveInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Phase34SaveInterval)
                .addContainerGap())
        );
        jPanel_Phase3and4Layout.setVerticalGroup(
            jPanel_Phase3and4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase3and4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase3and4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_Phase34SaveInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase3and4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Phase34SaveInfoIcon)
                        .addComponent(jLabel_Phase34SaveInterval)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Phase3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_Phase3Skip.setText("Skip Phase 3?");
        jLabel_Phase3Skip.setMaximumSize(new java.awt.Dimension(128, 14));
        jLabel_Phase3Skip.setMinimumSize(new java.awt.Dimension(128, 14));
        jLabel_Phase3Skip.setPreferredSize(new java.awt.Dimension(127, 14));

        SkipPhase3InfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        SkipPhase3InfoIcon.setToolTipText("Help Infomation for Skip Phase 3");
        SkipPhase3InfoIcon.setIconTextGap(0);
        SkipPhase3InfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SkipPhase3InfoIconMouseClicked(evt);
            }
        });

        jTextField_Phase3TimeLimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_Phase3TimeLimitFocusLost(evt);
            }
        });

        phase3TimeLimitInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        phase3TimeLimitInfoIcon.setToolTipText("Help Infomation for Phase 3 Time Limit");
        phase3TimeLimitInfoIcon.setIconTextGap(0);
        phase3TimeLimitInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                phase3TimeLimitInfoIconMouseClicked(evt);
            }
        });

        jLabel_Phase3TimeLimit.setText("Phase 3 Time limit");

        jComboBox_Phase3Skip.setModel(new DefaultComboBoxModel<>(TRUE_FALSE_VALUES));
        jComboBox_Phase3Skip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Phase3SkipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Phase3Layout = new javax.swing.GroupLayout(jPanel_Phase3);
        jPanel_Phase3.setLayout(jPanel_Phase3Layout);
        jPanel_Phase3Layout.setHorizontalGroup(
            jPanel_Phase3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Phase3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_Phase3Skip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SkipPhase3InfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox_Phase3Skip, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(331, 331, 331)
                .addComponent(jLabel_Phase3TimeLimit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phase3TimeLimitInfoIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Phase3TimeLimit)
                .addContainerGap())
        );
        jPanel_Phase3Layout.setVerticalGroup(
            jPanel_Phase3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Phase3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Phase3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_Phase3TimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_Phase3Skip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Phase3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(SkipPhase3InfoIcon)
                        .addComponent(jLabel_Phase3Skip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_Phase3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(phase3TimeLimitInfoIcon)
                        .addComponent(jLabel_Phase3TimeLimit)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_Save.setText("Save Run File");
        btn_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveActionPerformed(evt);
            }
        });

        btnPreviousStep.setText("Previous Step");
        btnPreviousStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousStepActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel_FirstRealizationIndex.setText("First Realization Index");
        jLabel_FirstRealizationIndex.setPreferredSize(new java.awt.Dimension(127, 14));

        FirstRealizationIndexInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        FirstRealizationIndexInfoIcon.setToolTipText("Help Infomation for First Realization Index");
        FirstRealizationIndexInfoIcon.setIconTextGap(0);
        FirstRealizationIndexInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FirstRealizationIndexInfoIconMouseClicked(evt);
            }
        });

        jTextField_FirstRealizationIndex.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_FirstRealizationIndexFocusLost(evt);
            }
        });

        jTextField_RandomNumberSeed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_RandomNumberSeedFocusLost(evt);
            }
        });

        randomNumberSeedInfoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cerl/gui/resources/info.png"))); // NOI18N
        randomNumberSeedInfoIcon.setToolTipText("Help Infomation for Random Number Seed");
        randomNumberSeedInfoIcon.setIconTextGap(0);
        randomNumberSeedInfoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                randomNumberSeedInfoIconMouseClicked(evt);
            }
        });

        jLabel_RandomNumberSeed.setText("Random Number seed");
        jLabel_RandomNumberSeed.setPreferredSize(new java.awt.Dimension(127, 14));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel_FirstRealizationIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FirstRealizationIndexInfoIcon))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel_RandomNumberSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(randomNumberSeedInfoIcon)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_RandomNumberSeed)
                    .addComponent(jTextField_FirstRealizationIndex))
                .addGap(8, 8, 8))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(FirstRealizationIndexInfoIcon)
                    .addComponent(jTextField_FirstRealizationIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel_FirstRealizationIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_RandomNumberSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(randomNumberSeedInfoIcon)
                    .addComponent(jLabel_RandomNumberSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu_File.setText("File");
        jMenuBar.add(jMenu_File);

        jMenu_Help.setText("Help");
        jMenu_Help.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu_HelpMouseClicked(evt);
            }
        });
        jMenuBar.add(jMenu_Help);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreviousStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Save))
                    .addComponent(jPanel_Phase4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Phase3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Run, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Phase3and4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Phase2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_LogResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel_Archetype, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_RealizationIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel_Seeds, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Run, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_LogResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Archetype, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_RealizationIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Seeds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Phase2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Phase3and4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Phase3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Phase4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Save)
                    .addComponent(btnPreviousStep))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameOfRunInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameOfRunInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Name_Of_Run.toString());
    }//GEN-LAST:event_nameOfRunInfoIconMouseClicked

    private void Phase1ResultsLogInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase1ResultsLogInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase1_Log_Results.toString());
    }//GEN-LAST:event_Phase1ResultsLogInfoIconMouseClicked

    private void LogQualityInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogQualityInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Log_Quality_Evaluation.toString());
    }//GEN-LAST:event_LogQualityInfoIconMouseClicked

    private void Phase2SkipTractsInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase2SkipTractsInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase2_Skip_Tracts.toString());
    }//GEN-LAST:event_Phase2SkipTractsInfoIconMouseClicked

    private void Phase2RandomPercentInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase2RandomPercentInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase2_Random_Placement.toString());
    }//GEN-LAST:event_Phase2RandomPercentInfoIconMouseClicked

    private void Phase1TimeLimitInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase1TimeLimitInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase1_Time_Limit.toString());
    }//GEN-LAST:event_Phase1TimeLimitInfoIconMouseClicked

    private void ParallelThreadsInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ParallelThreadsInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Parallel_Threads.toString());
    }//GEN-LAST:event_ParallelThreadsInfoIconMouseClicked

    private void OutputDirectoryInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_OutputDirectoryInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Output_Directory.toString());
    }//GEN-LAST:event_OutputDirectoryInfoIconMouseClicked

    private void FirstCensusTractInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FirstCensusTractInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.First_Census_Tract.toString());
    }//GEN-LAST:event_FirstCensusTractInfoIconMouseClicked

    private void randomNumberSeedInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_randomNumberSeedInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Random_Number_Seed.toString());
    }//GEN-LAST:event_randomNumberSeedInfoIconMouseClicked

    private void FinalRealizationIndexInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FinalRealizationIndexInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Final_Realization_Index.toString());
    }//GEN-LAST:event_FinalRealizationIndexInfoIconMouseClicked

    private void FirstRealizationIndexInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FirstRealizationIndexInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.First_Realization_Index.toString());
    }//GEN-LAST:event_FirstRealizationIndexInfoIconMouseClicked

    private void PopulationAchetypeInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PopulationAchetypeInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Population_archetype.toString());
    }//GEN-LAST:event_PopulationAchetypeInfoIconMouseClicked

    private void HouseholdArchetypeInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HouseholdArchetypeInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Household_archetype.toString());
    }//GEN-LAST:event_HouseholdArchetypeInfoIconMouseClicked

    private void Phase2SkippedProbabilityInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase2SkippedProbabilityInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase2_Skip_Tracts_Probability.toString());
    }//GEN-LAST:event_Phase2SkippedProbabilityInfoIconMouseClicked

    private void phase4LagsInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_phase4LagsInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase4_Lags.toString());
    }//GEN-LAST:event_phase4LagsInfoIconMouseClicked

    private void Phase4SaveInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase4SaveInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase4_Save.toString());
    }//GEN-LAST:event_Phase4SaveInfoIconMouseClicked

    private void SkipPhase4InfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SkipPhase4InfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase4_Skip.toString());
    }//GEN-LAST:event_SkipPhase4InfoIconMouseClicked

    private void Phase4TimeLimitInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase4TimeLimitInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase4_Time_Limit.toString());
    }//GEN-LAST:event_Phase4TimeLimitInfoIconMouseClicked

    private void Phase34SaveInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Phase34SaveInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase34_Save_Interval.toString());
    }//GEN-LAST:event_Phase34SaveInfoIconMouseClicked

    private void SkipPhase3InfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SkipPhase3InfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase3_Skip.toString());
    }//GEN-LAST:event_SkipPhase3InfoIconMouseClicked

    private void phase3TimeLimitInfoIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_phase3TimeLimitInfoIconMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenInstructionName(SCREEN_NAME, StepSevenInstructionNames.Phase3_Time_Limit.toString());
    }//GEN-LAST:event_phase3TimeLimitInfoIconMouseClicked

    /**
     * Checks if all required values were provided, and updates error text as needed
     * @return - True if all fields were provided, false if missing information
     */
    private boolean validateData(){
        Boolean isValid = true; //valid until proven otherwise
        String errorText = "<html>The following data fields must be provided missing: ";
        
        //Check all true/false combo boxes:
        if(jComboBox_LogPhase1Results.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_LogPhase1Results.getText();
            isValid = false;
        }
        
        if(jComboBox_LogQualityEval.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_LogQualityEval.getText();
            isValid = false;
        }
        
        if(jComboBox_HouseholdArchetype.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_HouseholdArchetype.getText();
            isValid = false;
        }
        
        if(jComboBox_PopulationArchetype.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_PopulationArchetype.getText();
            isValid = false;
        }
        
        if(jComboBox_FirstCensusTract.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_FirstCensusTract.getText();
            isValid = false;
        }
        
        if(jComboBox_Phase3Skip.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_Phase3Skip.getText();
            isValid = false;
        }
        
        if(jComboBox_Phase4Save.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_Phase4Save.getText();
            isValid = false;
        }
        
        if(jComboBox_Phase4Skip.getSelectedIndex() <= 0){
            errorText += "<br /> - " + this.jLabel_Phase4Skip.getText();
            isValid = false;
        }
        
        //Check all text fields
        if(jTextField_NameOfRun.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_NameOfRun.getText();
            isValid = false;
        }
        if(jTextField_FinalRealizationIndex.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_FinalRealizationIndex.getText();
            isValid = false;
        }
        if(jTextField_FirstRealizationIndex.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_FirstRealizationIndex.getText();
            isValid = false;
        }
        if(jTextField_OutputDirectory.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_OutputDirectory.getText();
            isValid = false;
        }
        if(jTextField_ParallelThreads.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_ParallelThreads.getText();
            isValid = false;
        }
        if(jTextField_Phase1TimeLimit.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase1TimeLimit.getText();
            isValid = false;
        }
        if(jTextField_Phase2RandomPlacement.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase2RandomPlacement.getText();
            isValid = false;
        }
        if(jTextField_Phase2SkipTractsProb.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase2SkipTracts.getText();
            isValid = false;
        }
        if(jTextField_Phase2SkipTractsDelta.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase2SkippedTracts.getText();
            isValid = false;
        }
        if(jTextField_Phase3TimeLimit.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase3TimeLimit.getText();
            isValid = false;
        }
        if(jTextField_Phase34SaveInterval.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase34SaveInterval.getText();
            isValid = false;
        }
        if(jTextField_Phase4TimeLimit.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase4_TimeLimit.getText();
            isValid = false;
        }
        if(jTextField_Phase4_Lags.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_Phase4_Lags.getText();
            isValid = false;
        }
        if(jTextField_RandomNumberSeed.getText().equals("")){
            errorText += "<br /> - " + this.jLabel_RandomNumberSeed.getText();
            isValid = false;
        }
        errorText += "</html>";
        
        jLabel_Errors.setText(errorText);
        
        return isValid;
    }
    
    /**
     * Checks true/false combo boxes for their values & translates to a boolean
     * @param picklist - the true/false combo box
     * @return True if the user selected "True", "False" if false or null
     */
    private Boolean returnTrueFalseValue(javax.swing.JComboBox<String> picklist){
        switch (picklist.getSelectedIndex()){
            case 1:
                return true;
            case 2:
                return false;
            default:
                break;
        }
        return false;
    }
    
    /**
     * Handles the save button click, validates data first
     * Then saves to the last-run.properties file
     * @param evt 
     */
    private void btn_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveActionPerformed
        Boolean isValid = validateData();
        this.digPopGUIInformation.setRunFile(this.RunProperties);
        
        String saveFilePath = this.digPopGUIInformation.getFilePath();
        
        this.RunProperties.setDateOfRun(Calendar.getInstance().getTime());
        
        if(saveFilePath.contains(".XML")){
            saveFilePath = saveFilePath.substring(0, saveFilePath.lastIndexOf("\\")+1);
        }
        
        this.RunProperties.setCriteria_file(String.format("%s\\%s", saveFilePath, "FittingCriteria.dprxml"));
        
        //create new run file
        File newRunFile = new File(String.format("%s\\%s", saveFilePath, DEFAULT_NEW_FILE_NAME));
                
        //write to file
        Result result = FileUtility.WriteNewTextFile(newRunFile.getPath(), this.RunProperties.toString());
        
        //provide message to user that the file has been created
        if(isValid){
            this.jLabel_Errors.setText("The Run file was saved");
        }
    }//GEN-LAST:event_btn_SaveActionPerformed

    private void jComboBox_LogPhase1ResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_LogPhase1ResultsActionPerformed
        if(jComboBox_LogPhase1Results.getSelectedIndex() > 0){
            this.RunProperties.setDo_dump_number_archtypes(returnTrueFalseValue(jComboBox_LogPhase1Results));
        } else{
            this.jLabel_Errors.setText(this.jLabel_LogPhase1Results.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_LogPhase1ResultsActionPerformed

    private void jComboBox_LogQualityEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_LogQualityEvalActionPerformed
        if(jComboBox_LogQualityEval.getSelectedIndex() > 0){
            this.RunProperties.setDo_dump_statistics(returnTrueFalseValue(jComboBox_LogQualityEval));
        } else{
            this.jLabel_Errors.setText(this.jLabel_LogQualityEval.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_LogQualityEvalActionPerformed

    private void jComboBox_HouseholdArchetypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_HouseholdArchetypeActionPerformed
        if(jComboBox_HouseholdArchetype.getSelectedIndex() > 0){
            this.RunProperties.setDo_write_all_hoh_fields(returnTrueFalseValue(jComboBox_HouseholdArchetype));
        } else{
            this.jLabel_Errors.setText(this.jLabel_HouseholdArchetype.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_HouseholdArchetypeActionPerformed

    private void jComboBox_PopulationArchetypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_PopulationArchetypeActionPerformed
        if(jComboBox_PopulationArchetype.getSelectedIndex() > 0){
            this.RunProperties.setDo_write_all_pop_fields(returnTrueFalseValue(jComboBox_PopulationArchetype));
        } else{
            this.jLabel_Errors.setText(this.jLabel_PopulationArchetype.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_PopulationArchetypeActionPerformed

    private void jComboBox_FirstCensusTractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_FirstCensusTractActionPerformed
        //Use only the first census tract
        if(jComboBox_FirstCensusTract.getSelectedIndex() > 0){
            this.RunProperties.setOnly_one_region(returnTrueFalseValue(jComboBox_FirstCensusTract));
        } else{
            this.jLabel_Errors.setText(this.jLabel_FirstCensusTract.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_FirstCensusTractActionPerformed

    private void jComboBox_Phase3SkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Phase3SkipActionPerformed
        if(jComboBox_Phase3Skip.getSelectedIndex() > 0){
            this.RunProperties.setPhase3_skip(returnTrueFalseValue(jComboBox_Phase3Skip));
        } else{
            this.jLabel_Errors.setText(this.jLabel_Phase3Skip.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_Phase3SkipActionPerformed

    private void jComboBox_Phase4SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Phase4SaveActionPerformed
        if(jComboBox_Phase4Save.getSelectedIndex() > 0){
            this.RunProperties.setPhase4_save_both_ends(returnTrueFalseValue(jComboBox_Phase4Save));
        } else{
            this.jLabel_Errors.setText(this.jLabel_Phase4Save.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_Phase4SaveActionPerformed

    private void jComboBox_Phase4SkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Phase4SkipActionPerformed
        if(jComboBox_Phase4Skip.getSelectedIndex() > 0){
            this.RunProperties.setPhase4_skip(returnTrueFalseValue(jComboBox_Phase4Skip));
        } else{
            this.jLabel_Errors.setText(this.jLabel_Phase4Skip.getText() +  " is required");
        }
    }//GEN-LAST:event_jComboBox_Phase4SkipActionPerformed

    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    private void jMenu_HelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_HelpMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_HelpMouseClicked

    private void btnPreviousStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStepActionPerformed
        new GenerateTraitClusters(this.digPopGUIInformation).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreviousStepActionPerformed
    
    private void jTextField_FirstRealizationIndexFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_FirstRealizationIndexFocusLost
        String value = jTextField_FirstRealizationIndex.getText();
        
        if(Validations.validateAndReturnInteger(value)){
            int frn = Integer.parseInt(value);
            if(frn > 0){
                this.RunProperties.setFirst_rzn_num(frn);                
                jLabel_Errors.setText(" ");
            }
            else{
                jLabel_Errors.setText("The First Realization Index Must be Greater than 0");
            }
        } else {
            jLabel_Errors.setText("The First Realization Index Must be a Valid Integer");
        }
    }//GEN-LAST:event_jTextField_FirstRealizationIndexFocusLost

    private void jTextField_Phase1TimeLimitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase1TimeLimitFocusLost
        String value = jTextField_Phase1TimeLimit.getText();
        if(Validations.validateAndReturnDouble(value)){
            
            double d = Double.parseDouble(value);
            if(d > 1){
                this.RunProperties.setPhase1_time_limit(d);
                jLabel_Errors.setText(" ");
            }
            else{
                jLabel_Errors.setText("The Phase 1 Time Limit Must be Greater than 1");
            }
        } else {
            jLabel_Errors.setText("The Phase 1 Time Limit Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase1TimeLimitFocusLost

    private void jTextField_NameOfRunFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_NameOfRunFocusLost
        this.RunProperties.setRunName(jTextField_NameOfRun.getText());
    }//GEN-LAST:event_jTextField_NameOfRunFocusLost

    private void jTextField_RandomNumberSeedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_RandomNumberSeedFocusLost
        String value = jTextField_RandomNumberSeed.getText();
        
        if(Validations.validateAndReturnLong(value)){
            this.RunProperties.setInitial_seed(Long.parseLong(value));
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Random Number Seed Must be a Valid Long");
        }
    }//GEN-LAST:event_jTextField_RandomNumberSeedFocusLost

    private void jTextField_FinalRealizationIndexFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_FinalRealizationIndexFocusLost
        String value = jTextField_FinalRealizationIndex.getText();
        
        if(Validations.validateAndReturnInteger(value)){
            int frn = Integer.parseInt(value);
            if(frn > 0){
                this.RunProperties.setFinal_rzn_num(frn);
                jLabel_Errors.setText(" ");
            }
            else{
                jLabel_Errors.setText("The Final Realization Index Must be Greater than 0");
            }
        } else {
            jLabel_Errors.setText("The Final Realization Index Must be a Valid Integer");
        }
    }//GEN-LAST:event_jTextField_FinalRealizationIndexFocusLost

    private void jTextField_OutputDirectoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_OutputDirectoryFocusLost
        this.RunProperties.setOutput_dir(jTextField_OutputDirectory.getText());
    }//GEN-LAST:event_jTextField_OutputDirectoryFocusLost

    private void jTextField_ParallelThreadsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_ParallelThreadsFocusLost
        String value = jTextField_ParallelThreads.getText();
        
        if(Validations.validateAndReturnInteger(value)){
            int i = Integer.parseInt(value);
            if(i > 1){
                this.RunProperties.setParallel_threads(i);
                jLabel_Errors.setText(" ");
            }
            else{
                jLabel_Errors.setText("The Parallel Threads Must be Greater than 1");
            }
        } else {
            jLabel_Errors.setText("The Parallel Threads Must be a Valid Integer");
        }
    }//GEN-LAST:event_jTextField_ParallelThreadsFocusLost

    private void jTextField_Phase2RandomPlacementFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase2RandomPlacementFocusLost
        String value = jTextField_Phase2RandomPlacement.getText();
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase2_random_tract_prob(Double.parseDouble(value));   
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Phase 2 Random Placement Percentage Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase2RandomPlacementFocusLost

    private void jTextField_Phase2SkipTractsDeltaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase2SkipTractsDeltaFocusLost
        String value = jTextField_Phase2SkipTractsDelta.getText();
        
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase2_tract_skip_prob_delta(Double.parseDouble(value));
            jLabel_Errors.setText(" ");
        }else {
            jLabel_Errors.setText("The Phase 2 Skip Tracts Probability Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase2SkipTractsDeltaFocusLost

    private void jTextField_Phase2SkipTractsProbFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase2SkipTractsProbFocusLost
        String value = jTextField_Phase2SkipTractsProb.getText();
        
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase2_tract_skip_prob_init(Double.parseDouble(value));
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Phase 2 Skip Tracts Probability Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase2SkipTractsProbFocusLost

    private void jTextField_Phase34SaveIntervalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase34SaveIntervalFocusLost
        String value = jTextField_Phase34SaveInterval.getText();
        
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase3_save_intermediate(Double.parseDouble(value));   
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Phase 3 and 4 Intermediate save Interval Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase34SaveIntervalFocusLost

    private void jTextField_Phase3TimeLimitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase3TimeLimitFocusLost
        String value = jTextField_Phase3TimeLimit.getText();
        
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase3_time_limit(Double.parseDouble(value));  
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Phase 3 Time Limit Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase3TimeLimitFocusLost

    private void jTextField_Phase4_LagsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase4_LagsFocusLost
        String value = jTextField_Phase4_Lags.getText();
        
        if(Validations.validateAndReturnInteger(value)){
            int i = Integer.parseInt(value);
            if(i > 1){
                this.RunProperties.setPhase4_num_lags(i);
                jLabel_Errors.setText(" ");
            }
            else{
                jLabel_Errors.setText("The Number of Lags Must be Greater than 1");
            }
        } else {
            jLabel_Errors.setText("The Number of Lags Must be a Valid Integer");
        }
    }//GEN-LAST:event_jTextField_Phase4_LagsFocusLost

    private void jTextField_Phase4TimeLimitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_Phase4TimeLimitFocusLost
        String value = jTextField_Phase4TimeLimit.getText();
        
        if(Validations.validateAndReturnDouble(value)){
            this.RunProperties.setPhase4_time_limit(Double.parseDouble(value));   
            jLabel_Errors.setText(" ");
        } else {
            jLabel_Errors.setText("The Phase 4 Time Limit Must be a Valid Double");
        }
    }//GEN-LAST:event_jTextField_Phase4TimeLimitFocusLost
    
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
            java.util.logging.Logger.getLogger(StepSeven.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StepSeven.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StepSeven.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StepSeven.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StepSeven().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel FinalRealizationIndexInfoIcon;
    private javax.swing.JLabel FirstCensusTractInfoIcon;
    private javax.swing.JLabel FirstRealizationIndexInfoIcon;
    private javax.swing.JLabel HouseholdArchetypeInfoIcon;
    private javax.swing.JLabel LogQualityInfoIcon;
    private javax.swing.JLabel OutputDirectoryInfoIcon;
    private javax.swing.JLabel ParallelThreadsInfoIcon;
    private javax.swing.JLabel Phase1ResultsLogInfoIcon;
    private javax.swing.JLabel Phase1TimeLimitInfoIcon;
    private javax.swing.JLabel Phase2RandomPercentInfoIcon;
    private javax.swing.JLabel Phase2SkipTractsInfoIcon;
    private javax.swing.JLabel Phase2SkippedProbabilityInfoIcon;
    private javax.swing.JLabel Phase34SaveInfoIcon;
    private javax.swing.JLabel Phase4SaveInfoIcon;
    private javax.swing.JLabel Phase4TimeLimitInfoIcon;
    private javax.swing.JLabel PopulationAchetypeInfoIcon;
    private javax.swing.JLabel SkipPhase3InfoIcon;
    private javax.swing.JLabel SkipPhase4InfoIcon;
    private javax.swing.JButton btnPreviousStep;
    private javax.swing.JButton btn_Save;
    private javax.swing.JComboBox<String> jComboBox_FirstCensusTract;
    private javax.swing.JComboBox<String> jComboBox_HouseholdArchetype;
    private javax.swing.JComboBox<String> jComboBox_LogPhase1Results;
    private javax.swing.JComboBox<String> jComboBox_LogQualityEval;
    private javax.swing.JComboBox<String> jComboBox_Phase3Skip;
    private javax.swing.JComboBox<String> jComboBox_Phase4Save;
    private javax.swing.JComboBox<String> jComboBox_Phase4Skip;
    private javax.swing.JComboBox<String> jComboBox_PopulationArchetype;
    private javax.swing.JLabel jLabel_CreateRunFile;
    private javax.swing.JLabel jLabel_Errors;
    private javax.swing.JLabel jLabel_FinalRealizationIndex;
    private javax.swing.JLabel jLabel_FirstCensusTract;
    private javax.swing.JLabel jLabel_FirstRealizationIndex;
    private javax.swing.JLabel jLabel_HouseholdArchetype;
    private javax.swing.JLabel jLabel_LogPhase1Results;
    private javax.swing.JLabel jLabel_LogQualityEval;
    private javax.swing.JLabel jLabel_NameOfRun;
    private javax.swing.JLabel jLabel_OutputDirectory;
    private javax.swing.JLabel jLabel_ParallelThreads;
    private javax.swing.JLabel jLabel_Phase1TimeLimit;
    private javax.swing.JLabel jLabel_Phase2RandomPlacement;
    private javax.swing.JLabel jLabel_Phase2SkipTracts;
    private javax.swing.JLabel jLabel_Phase2SkippedTracts;
    private javax.swing.JLabel jLabel_Phase34SaveInterval;
    private javax.swing.JLabel jLabel_Phase3Skip;
    private javax.swing.JLabel jLabel_Phase3TimeLimit;
    private javax.swing.JLabel jLabel_Phase4Save;
    private javax.swing.JLabel jLabel_Phase4Skip;
    private javax.swing.JLabel jLabel_Phase4_Lags;
    private javax.swing.JLabel jLabel_Phase4_TimeLimit;
    private javax.swing.JLabel jLabel_PopulationArchetype;
    private javax.swing.JLabel jLabel_RandomNumberSeed;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenu jMenu_File;
    private javax.swing.JMenu jMenu_Help;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Archetype;
    private javax.swing.JPanel jPanel_LogResults;
    private javax.swing.JPanel jPanel_Phase2;
    private javax.swing.JPanel jPanel_Phase3;
    private javax.swing.JPanel jPanel_Phase3and4;
    private javax.swing.JPanel jPanel_Phase4;
    private javax.swing.JPanel jPanel_RealizationIndex;
    private javax.swing.JPanel jPanel_Run;
    private javax.swing.JPanel jPanel_Seeds;
    private javax.swing.JTextField jTextField_FinalRealizationIndex;
    private javax.swing.JTextField jTextField_FirstRealizationIndex;
    private javax.swing.JTextField jTextField_NameOfRun;
    private javax.swing.JTextField jTextField_OutputDirectory;
    private javax.swing.JTextField jTextField_ParallelThreads;
    private javax.swing.JTextField jTextField_Phase1TimeLimit;
    private javax.swing.JTextField jTextField_Phase2RandomPlacement;
    private javax.swing.JTextField jTextField_Phase2SkipTractsDelta;
    private javax.swing.JTextField jTextField_Phase2SkipTractsProb;
    private javax.swing.JTextField jTextField_Phase34SaveInterval;
    private javax.swing.JTextField jTextField_Phase3TimeLimit;
    private javax.swing.JTextField jTextField_Phase4TimeLimit;
    private javax.swing.JTextField jTextField_Phase4_Lags;
    private javax.swing.JTextField jTextField_RandomNumberSeed;
    private javax.swing.JLabel nameOfRunInfoIcon;
    private javax.swing.JLabel phase3TimeLimitInfoIcon;
    private javax.swing.JLabel phase4LagsInfoIcon;
    private javax.swing.JLabel randomNumberSeedInfoIcon;
    // End of variables declaration//GEN-END:variables
}
