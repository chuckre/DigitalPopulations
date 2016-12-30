/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.xml.bind.annotation.XmlElement;

/**
 * Provides the instruction name and help text for a given instruction
 * @author ajohnson
 */
public class Instruction {
    private String instructionName;
    private String helpText; 

    /**
     * Gets the help text
     * @return String of help text
     */
    @XmlElement
    public String getHelpText() {
        return helpText;
    }

    /**
     * Sets the help text
     * @param helpText - the new text to save
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    
    /**
     * Gets the name of the instruction
     * @return String value of the instruction name
     */
    public String getInstructionName() {
        return instructionName;
    }
    
    /**
     * Sets the instruction name
     * @param instructionName - string value to set as the new name
     */
    @XmlElement
    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    /**
     * Returns the instruction name
     * @return string of the instruction name
     */
    @Override
    public String toString() {
        return instructionName;
    }
    
    /**
     * Gets the display text
     * Displays a bold header for the instruction name, and the help text on the next line
     * @return HTML string value of the instructions and help text
     */
    public String getDisplayText() {
        return String.format("<b>%s</b><br>%s", instructionName, helpText);
    }
}
