/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author ajohnson
 */

public class Instruction {
    private String instructionName;
    private String helpText; 

    @XmlElement
    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    public String getInstructionName() {
        return instructionName;
    }
    
    @XmlElement
    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    @Override
    public String toString() {
        return instructionName;
    }
    
    public String getDisplayText() {
        return String.format("<b>%s</b><br>%s", instructionName, helpText);
    }
}
