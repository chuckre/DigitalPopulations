/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.forms;

import cerl.gui.standard.utilities.FileType;
import cerl.gui.standard.utilities.FileUtility;
import cerl.gui.standard.utilities.Result;
import cerl.gui.utilities.DigPopGUIInformation;
import cerl.gui.utilities.DigPopGUIUtilityClass;
import cerl.gui.utilities.HelpFileScreenNames;
import cerl.gui.utilities.StepZeroUtilityClass;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The initial step to create the setup/save file
 * @author ajohnson
 */
public class StepZero extends javax.swing.JFrame {
    //Variables
    private final String DEFAULT_NEW_FILE_NAME = "DigPop_GUI";
    private final FileType DEFAULT_NEW_FILE_TYPE = FileType.XML;
    private final String NEW_TAB_NAME = "New";
    private final String OPEN_TAB_NAME = "Open";
    private final String DUPLICATE_TAB_NAME = "Duplicate";
    private final String SCREEN_NAME = HelpFileScreenNames.STEP_ONE_HELP_FILE_NAME.toString();
    
    private final FileNameExtensionFilter XML_FILTER = 
            new FileNameExtensionFilter(
                    "xml files (*.XML)", "XML");
    
    private DigPopGUIInformation digPopGUIInformation;

    /**
     * gets the current DigPopGUIInformation
     * @return DigPopGUIInformation object
     */
    public DigPopGUIInformation getDigPopGUIInformation() {
        return digPopGUIInformation;
    }

    /**
     * Creates new form StepZero
     */
    public StepZero() {
        digPopGUIInformation = new DigPopGUIInformation();
        
        initComponents();
        
        enableNewFileTab(false);
        enableOpenFileTab(false);
        enableDuplicateFileTab(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileLocationChooser = new javax.swing.JFileChooser();
        buttonGroupSelectRun = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPanel = new javax.swing.JTabbedPane();
        tabbedPanelNew = new javax.swing.JPanel();
        lblNewFileName = new javax.swing.JLabel();
        lblNewFileLocation = new javax.swing.JLabel();
        btnSelectNewFileLocation = new javax.swing.JButton();
        txtNewFileName = new javax.swing.JTextField();
        txtNewFileLocation = new javax.swing.JTextField();
        tabbedPanel_Open = new javax.swing.JPanel();
        lblOpenFile = new javax.swing.JLabel();
        txtOpenFile = new javax.swing.JTextField();
        btnSelectOpenFile = new javax.swing.JButton();
        tabbedPanel_Duplicate = new javax.swing.JPanel();
        lblDuplicateFile = new javax.swing.JLabel();
        txtDuplicateFile = new javax.swing.JTextField();
        btnSelectDuplicateFile = new javax.swing.JButton();
        lblDuplicateNewFileName = new javax.swing.JLabel();
        txtDuplicateNewFileName = new javax.swing.JTextField();
        txtDuplicateFileLocation = new javax.swing.JTextField();
        btnSelectDuplicateFileLocation = new javax.swing.JButton();
        lblDuplicateFileLocation = new javax.swing.JLabel();
        btnNew = new javax.swing.JRadioButton();
        btnOpen = new javax.swing.JRadioButton();
        btnDuplicate = new javax.swing.JRadioButton();
        lblErrorMessages = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu_ExitMenu = new javax.swing.JMenuItem();
        jMenu_Help = new javax.swing.JMenu();
        jMenu_About = new javax.swing.JMenu();

        fileLocationChooser.setCurrentDirectory(new java.io.File("C:\\"));
            fileLocationChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("DigPop GUI");

            jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel1.setText("Welcome to the DigPop GUI");

            jTabbedPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            tabbedPanelNew.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            lblNewFileName.setText("New File Name:");

            lblNewFileLocation.setText("File Location:");

            btnSelectNewFileLocation.setText("Select Location");
            btnSelectNewFileLocation.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSelectNewFileLocationActionPerformed(evt);
                }
            });

            txtNewFileLocation.setEditable(false);

            javax.swing.GroupLayout tabbedPanelNewLayout = new javax.swing.GroupLayout(tabbedPanelNew);
            tabbedPanelNew.setLayout(tabbedPanelNewLayout);
            tabbedPanelNewLayout.setHorizontalGroup(
                tabbedPanelNewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabbedPanelNewLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(tabbedPanelNewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(tabbedPanelNewLayout.createSequentialGroup()
                            .addComponent(lblNewFileName)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNewFileName))
                        .addGroup(tabbedPanelNewLayout.createSequentialGroup()
                            .addComponent(lblNewFileLocation)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNewFileLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSelectNewFileLocation)))
                    .addContainerGap())
            );
            tabbedPanelNewLayout.setVerticalGroup(
                tabbedPanelNewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabbedPanelNewLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(tabbedPanelNewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNewFileName)
                        .addComponent(txtNewFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(tabbedPanelNewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNewFileLocation)
                        .addComponent(btnSelectNewFileLocation)
                        .addComponent(txtNewFileLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(66, Short.MAX_VALUE))
            );

            jTabbedPanel.addTab("New", tabbedPanelNew);

            tabbedPanel_Open.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            lblOpenFile.setText("File:");

            txtOpenFile.setEditable(false);

            btnSelectOpenFile.setText("Select File");
            btnSelectOpenFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSelectOpenFileActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout tabbedPanel_OpenLayout = new javax.swing.GroupLayout(tabbedPanel_Open);
            tabbedPanel_Open.setLayout(tabbedPanel_OpenLayout);
            tabbedPanel_OpenLayout.setHorizontalGroup(
                tabbedPanel_OpenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabbedPanel_OpenLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblOpenFile)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtOpenFile, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnSelectOpenFile)
                    .addContainerGap())
            );
            tabbedPanel_OpenLayout.setVerticalGroup(
                tabbedPanel_OpenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabbedPanel_OpenLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(tabbedPanel_OpenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSelectOpenFile)
                        .addComponent(txtOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOpenFile))
                    .addContainerGap(97, Short.MAX_VALUE))
            );

            jTabbedPanel.addTab("Open", tabbedPanel_Open);

            tabbedPanel_Duplicate.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            lblDuplicateFile.setText("Old File:");

            txtDuplicateFile.setEditable(false);

            btnSelectDuplicateFile.setText("Select File");
            btnSelectDuplicateFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSelectDuplicateFileActionPerformed(evt);
                }
            });

            lblDuplicateNewFileName.setText("New File Name:");

            txtDuplicateFileLocation.setEditable(false);

            btnSelectDuplicateFileLocation.setText("Select Location");
            btnSelectDuplicateFileLocation.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSelectDuplicateFileLocationActionPerformed(evt);
                }
            });

            lblDuplicateFileLocation.setText("New File Location:");

            javax.swing.GroupLayout tabbedPanel_DuplicateLayout = new javax.swing.GroupLayout(tabbedPanel_Duplicate);
            tabbedPanel_Duplicate.setLayout(tabbedPanel_DuplicateLayout);
            tabbedPanel_DuplicateLayout.setHorizontalGroup(
                tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabbedPanel_DuplicateLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabbedPanel_DuplicateLayout.createSequentialGroup()
                            .addComponent(lblDuplicateFile)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDuplicateFile, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSelectDuplicateFile))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabbedPanel_DuplicateLayout.createSequentialGroup()
                            .addComponent(lblDuplicateNewFileName)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDuplicateNewFileName))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabbedPanel_DuplicateLayout.createSequentialGroup()
                            .addComponent(lblDuplicateFileLocation)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDuplicateFileLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSelectDuplicateFileLocation)))
                    .addContainerGap())
            );
            tabbedPanel_DuplicateLayout.setVerticalGroup(
                tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabbedPanel_DuplicateLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSelectDuplicateFile)
                        .addComponent(txtDuplicateFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDuplicateFile))
                    .addGap(18, 18, 18)
                    .addGroup(tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDuplicateNewFileName)
                        .addComponent(txtDuplicateNewFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(tabbedPanel_DuplicateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDuplicateFileLocation)
                        .addComponent(btnSelectDuplicateFileLocation)
                        .addComponent(txtDuplicateFileLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(18, Short.MAX_VALUE))
            );

            jTabbedPanel.addTab("Duplicate", tabbedPanel_Duplicate);

            buttonGroupSelectRun.add(btnNew);
            btnNew.setText("New");
            btnNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnNewActionPerformed(evt);
                }
            });

            buttonGroupSelectRun.add(btnOpen);
            btnOpen.setText("Open");
            btnOpen.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnOpenActionPerformed(evt);
                }
            });

            buttonGroupSelectRun.add(btnDuplicate);
            btnDuplicate.setText("Duplicate");
            btnDuplicate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnDuplicateActionPerformed(evt);
                }
            });

            lblErrorMessages.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            lblErrorMessages.setForeground(new java.awt.Color(204, 0, 0));

            btnNext.setText("Next");
            btnNext.setEnabled(false);
            btnNext.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnNextActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPanel, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(lblErrorMessages)
                            .addGap(178, 178, 178)
                            .addComponent(btnNew)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnOpen)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDuplicate)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGap(10, 10, 10))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNext)
                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblErrorMessages)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnOpen)
                            .addComponent(btnDuplicate)
                            .addComponent(btnNew)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btnNext)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            jMenu1.setText("File");

            jMenu_ExitMenu.setText("Exit");
            jMenu_ExitMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenu_ExitMenuActionPerformed(evt);
                }
            });
            jMenu1.add(jMenu_ExitMenu);
            jMenu_ExitMenu.getAccessibleContext().setAccessibleDescription("Exit Step");

            jMenuBar1.add(jMenu1);

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
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles the event for selecting the Duplicate option, opens the tab
     * @param evt 
     */
    private void btnDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuplicateActionPerformed
        if(btnDuplicate.isSelected())
        {
            enableNewFileTab(false);
            enableOpenFileTab(false);
            enableDuplicateFileTab(true);
        }
    }//GEN-LAST:event_btnDuplicateActionPerformed

    /**
     * Handles the event for selecting the Open option, opens the tab
     * @param evt 
     */
    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        if(btnOpen.isSelected())
        {
            enableNewFileTab(false);
            enableOpenFileTab(true);
            enableDuplicateFileTab(false);
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    /**
     * Handles the event for selecting the New option, opens the tab
     * @param evt 
     */
    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        if(btnNew.isSelected())
        {
            enableNewFileTab(true);
            enableOpenFileTab(false);
            enableDuplicateFileTab(false);

            txtNewFileName.setText(
                FileUtility.createNewFileName(
                    true,
                    DEFAULT_NEW_FILE_NAME,
                    DEFAULT_NEW_FILE_TYPE));
        }
    }//GEN-LAST:event_btnNewActionPerformed

    /**
     * Handles the Next button click, saves or creates the new file
     * @param evt 
     */
    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed

        Result result = new Result();
        
        switch(jTabbedPanel.getSelectedIndex()){
            case 0: //New File
                String directory = txtNewFileLocation.getText();
                String fileName = txtNewFileName.getText();

                File file = new File(String.format("%s\\%s", directory,fileName));

                result = FileUtility.VerifyFileType(DEFAULT_NEW_FILE_TYPE, file);

                if(result.isSuccess()
                    && (Boolean)result.getValue()
                    && !file.exists()){
                    try {
                        //Need to create the file as empty version of the object
                        result = StepZeroUtilityClass.CreateNewEmptySaveFile(file);
                        
                        //If successully created object - go to Next Step
                        if(result.isSuccess()){
                            this.digPopGUIInformation = (DigPopGUIInformation)result.getValue();
                            this.digPopGUIInformation.setFileDirectory(directory);
                            //Now I need to move too step 2
                            goToNextStep(evt);

                        }else {
                            lblErrorMessages.setText(result.getErrorMessage());
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(StepZero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case 1: //Open Existing File
                File openFile = new File(txtOpenFile.getText());
                
                result = StepZeroUtilityClass.verifyXMLFile(openFile);
            
                if(result.isSuccess()){
                    this.digPopGUIInformation = (DigPopGUIInformation)result.getValue();
                        //Now I need to move too step 2
                        goToNextStep(evt);
                }else {
                    lblErrorMessages.setText(result.getErrorMessage());
                }
                break;
            case 2: //Duplicate Existing File
                String dupDirectory = txtDuplicateFileLocation.getText();
                String dupFileName = txtDuplicateNewFileName.getText();
                
                //setup new copied file
                File newDupFile = new File(String.format("%s\\%s", dupDirectory,dupFileName));
                result = FileUtility.VerifyFileType(DEFAULT_NEW_FILE_TYPE, newDupFile);

                if(result.isSuccess()
                    && (Boolean)result.getValue()
                    && !newDupFile.exists()){
                    try {
                        //Need to create the file as empty version of the object
                        result = StepZeroUtilityClass.CreateNewEmptySaveFile(newDupFile);
                        
                        //If successully created object - go to Next Step
                        if(result.isSuccess()){
                            //pull existing file
                            File existingFile = new File(this.txtDuplicateFile.getText());
                            Result origResult = StepZeroUtilityClass.verifyXMLFile(existingFile);
                            DigPopGUIInformation origObj = (DigPopGUIInformation)origResult.getValue();
                            
                            DigPopGUIInformation newObj = (DigPopGUIInformation)result.getValue();
                            origObj.setFilePath(newObj.getFilePath());
                            
                            //populate object
                            this.digPopGUIInformation = origObj;
                            this.digPopGUIInformation.setFileDirectory(dupDirectory);
                            
                            //Now I need to move too step 2
                            goToNextStep(evt);

                        }else {
                            lblErrorMessages.setText(result.getErrorMessage());
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(StepZero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            default: 
                break;
        }
    }//GEN-LAST:event_btnNextActionPerformed

    /**
     * Opens the next step, step one when click next.
     * @param evt 
     */
    private void goToNextStep(java.awt.event.ActionEvent evt){
        new StepOne(this.digPopGUIInformation).setVisible(true);
        dispose();
    }
    
    /**
     * Handles the selection of Duplicating an existing file
     * @param evt 
     */
    private void btnSelectDuplicateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectDuplicateFileActionPerformed
        File file = getFileFromFileChooser();

        if(file != null
            && file.isFile()){
            txtDuplicateFile.setText(file.getPath());
            
            txtDuplicateNewFileName.setText(FileUtility.createNewFileName(
                    true,
                    DEFAULT_NEW_FILE_NAME,
                    DEFAULT_NEW_FILE_TYPE));
            
            txtDuplicateFileLocation.setText(file.getParent());
            
            btnNext.setEnabled(true);
        }
    }//GEN-LAST:event_btnSelectDuplicateFileActionPerformed

    /**
     * Handles the button event for selecting to open a file
     * @param evt 
     */
    private void btnSelectOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectOpenFileActionPerformed
        File file = getFileFromFileChooser();

        if(file != null
            && file.isFile()){
            txtOpenFile.setText(file.getPath());
            
            btnNext.setEnabled(true);
        }
    }//GEN-LAST:event_btnSelectOpenFileActionPerformed

    /**
     * Handles the button event for selecting a new file location
     * @param evt 
     */
    private void btnSelectNewFileLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectNewFileLocationActionPerformed

        File file = getDirectoryFromFileChooser();

        if(file != null
            && file.isDirectory()){
            txtNewFileLocation.setText(file.getPath());

            btnNext.setEnabled(true);
        }

    }//GEN-LAST:event_btnSelectNewFileLocationActionPerformed

    /**
     * Handles the button event for duplicating a file
     * @param evt 
     */
    private void btnSelectDuplicateFileLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectDuplicateFileLocationActionPerformed
        File file = getDirectoryFromFileChooser();

        if(file != null
            && file.isDirectory()){
            txtDuplicateFileLocation.setText(file.getPath());
        }
    }//GEN-LAST:event_btnSelectDuplicateFileLocationActionPerformed

    /**
     * Handles the About menu item selection
     * @param evt 
     */
    private void jMenu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_AboutMouseClicked
        new About().setVisible(true);
    }//GEN-LAST:event_jMenu_AboutMouseClicked

    /**
     * Handles the Help menu item selection
     * @param evt 
     */
    private void jMenu_HelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu_HelpMouseClicked
        DigPopGUIUtilityClass.loadDefaultHelpGUIByScreenName(SCREEN_NAME);
    }//GEN-LAST:event_jMenu_HelpMouseClicked

    /**
     * Handles the Exit menu item
     * @param evt 
     */
    private void jMenu_ExitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_ExitMenuActionPerformed
        dispose();
    }//GEN-LAST:event_jMenu_ExitMenuActionPerformed

    
    
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
            java.util.logging.Logger.getLogger(StepZero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StepZero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StepZero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StepZero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StepZero().setVisible(true);
            }
        });
    }
    
    /**
     * Shows or hides the new file tab
     * @param enable - true if the New tab should display, false to hide
     */
    private void enableNewFileTab(Boolean enable){
        txtNewFileName.setEnabled(enable);
        txtNewFileLocation.setEnabled(enable);
        btnSelectNewFileLocation.setEnabled(enable);
        lblNewFileName.setEnabled(enable);
        lblNewFileLocation.setEnabled(enable);
        
        int index = jTabbedPanel.indexOfTab(
                            NEW_TAB_NAME);
        
        jTabbedPanel.setEnabledAt(
                    index, 
                    enable);
        
        if(enable){
            jTabbedPanel.setSelectedIndex(index);
        }
    }
    
    /**
     * Shows or hides the Open existing tab
     * @param enable - true to display the Open tab, false to hide
     */
    private void enableOpenFileTab(Boolean enable){
        txtOpenFile.setEnabled(enable);
        btnSelectOpenFile.setEnabled(enable);
        lblOpenFile.setEnabled(enable);
        
        int index = jTabbedPanel.indexOfTab(
                            OPEN_TAB_NAME);
        
        jTabbedPanel.setEnabledAt(
                    index, 
                    enable);
        
        if(enable){
            jTabbedPanel.setSelectedIndex(index);
        }
    }
    
    /**
     * Shows or hides the Duplicate tab
     * Tab allows user to copy from an existing file into a new file
     * @param enable - true to show the Duplicate tab, false to hide
     */
    private void enableDuplicateFileTab(Boolean enable){
        txtDuplicateFile.setEnabled(enable);
        txtDuplicateNewFileName.setEnabled(enable);
        btnSelectDuplicateFile.setEnabled(enable);
        lblDuplicateFile.setEnabled(enable);
        lblDuplicateNewFileName.setEnabled(enable);
        txtDuplicateFileLocation.setEnabled(enable);
        lblDuplicateFileLocation.setEnabled(enable);
        btnSelectDuplicateFileLocation.setEnabled(enable);
        
        int index = jTabbedPanel.indexOfTab(
                            DUPLICATE_TAB_NAME);
        
        jTabbedPanel.setEnabledAt(
                    index, 
                    enable);
        
        if(enable){
            jTabbedPanel.setSelectedIndex(index);
        }
    }
    
    /**
     * Pulls the directory the user selected from the file chooser
     * @return - the selected file
     */
    private File getDirectoryFromFileChooser() {
        File returnFile = null;
        
        int returnVal = fileLocationChooser.showOpenDialog(this);

        /**
         * FileChooser will return APPROVE_OPTION if the user selected a directory
         * from the dialog.
         */
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            returnFile = fileLocationChooser.getSelectedFile();
        }

        return returnFile;
    }
    
    /**
     * Gets the file the user selected from the file chooser
     * @return - the selected file
     */
    private File getFileFromFileChooser() {
        File returnFile = null;

        fileChooser.setFileFilter(XML_FILTER);
        int returnVal = fileChooser.showOpenDialog(this);

        /**
         * FileChooser will return APPROVE_OPTION if the user selected a directory
         * from the dialog.
         */
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            returnFile = fileChooser.getSelectedFile();
        }

        return returnFile;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnDuplicate;
    private javax.swing.JRadioButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JRadioButton btnOpen;
    private javax.swing.JButton btnSelectDuplicateFile;
    private javax.swing.JButton btnSelectDuplicateFileLocation;
    private javax.swing.JButton btnSelectNewFileLocation;
    private javax.swing.JButton btnSelectOpenFile;
    private javax.swing.ButtonGroup buttonGroupSelectRun;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JFileChooser fileLocationChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu_About;
    private javax.swing.JMenuItem jMenu_ExitMenu;
    private javax.swing.JMenu jMenu_Help;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPanel;
    private javax.swing.JLabel lblDuplicateFile;
    private javax.swing.JLabel lblDuplicateFileLocation;
    private javax.swing.JLabel lblDuplicateNewFileName;
    private javax.swing.JLabel lblErrorMessages;
    private javax.swing.JLabel lblNewFileLocation;
    private javax.swing.JLabel lblNewFileName;
    private javax.swing.JLabel lblOpenFile;
    private javax.swing.JPanel tabbedPanelNew;
    private javax.swing.JPanel tabbedPanel_Duplicate;
    private javax.swing.JPanel tabbedPanel_Open;
    private javax.swing.JTextField txtDuplicateFile;
    private javax.swing.JTextField txtDuplicateFileLocation;
    private javax.swing.JTextField txtDuplicateNewFileName;
    private javax.swing.JTextField txtNewFileLocation;
    private javax.swing.JTextField txtNewFileName;
    private javax.swing.JTextField txtOpenFile;
    // End of variables declaration//GEN-END:variables
}
