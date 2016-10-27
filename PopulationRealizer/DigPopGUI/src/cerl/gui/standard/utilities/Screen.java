/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author ajohnson
 */
public class Screen {

    private String name;
    private String description; 
    private ArrayList<Instruction> instruction;

    public ArrayList<Instruction> getInstruction() {
        return instruction;
    }

    public void setInstruction(ArrayList<Instruction> instruction) {
        this.instruction = instruction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
	return name;
    }

    @XmlElement
    public void setName(String name) {
    this.name = name;
    }
}
