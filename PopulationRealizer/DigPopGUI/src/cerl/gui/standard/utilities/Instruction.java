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
    private String instructionname;
    private String helpText; 

    @XmlElement
    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    public String getInstructionname() {
        return instructionname;
    }
    
    @XmlElement
    public void setInstructionname(String instructionname) {
        this.instructionname = instructionname;
    }
}
