/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Used as the screens for the Help File information
 * Contains a Name, Description, and ArrayList of instructions
 * @author ajohnson
 */
public class Screen {

    private String name;
    private String description; 
    private ArrayList<Instruction> instruction;

    /**
     * Gets the instructions for the current Screen
     * @return ArrayList of Instructions
     */
    public ArrayList<Instruction> getInstruction() {
        return instruction;
    }

    /**
     * Sets the Instruction for the current Screen
     * @param instruction - an ArrayList of values to set as the instructions
     */
    public void setInstruction(ArrayList<Instruction> instruction) {
        this.instruction = instruction;
    }

    /**
     * Gets the Description for the current Screen
     * @return string of the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Description for the current Screen
     * @param description - the new String value of the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the name of the current Screen
     * @return the String value of the Screen name
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name of the current Screen
     * @param name - the string value to set as the new name
     */
    @XmlElement
    public void setName(String name) {
    this.name = name;
    }
    
    /**
     * Provides the screen name as a string
     * @return string of the name
     */
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Gets the display text for the Screen
     * @return - a string with the bold name and description on the next line
     */
    public String getDisplayText() {
        String displayText = String.format("<b>%s</b><br>%s", name, description);
        
        for(Instruction displayInstruction : instruction){
            displayText = String.format(
                    "%s<br><br>%s", 
                    displayText, 
                    displayInstruction.getDisplayText());
        }
        
        return displayText;
    }
    
    /**
     * Gets the instructions by instruction name
     * @param instructionName - the instruction name to show the instructions for
     * @return the Instruction matching the instructionName
     */
    public Instruction getSelectedInstructionByName(String instructionName)
    {
        Instruction foundInstruction = new Instruction();
        
        foundInstruction = instruction.stream()
                .filter(i -> i.getInstructionName().equals(instructionName))
                .findFirst()
                .orElse(new Instruction());
        
        return foundInstruction;
    }
}
