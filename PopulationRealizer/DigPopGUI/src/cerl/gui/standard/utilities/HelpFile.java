/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Help File object used throughout for Contextual Help and Help Menus
 * @author ajohnson
 */
@XmlRootElement(name="help")
public class HelpFile {
    private ArrayList<Screen> screens;
    private String introduction;
    private String name;

    /**
     * Get the ArrayList screens (name/description/instruction)
     * @return ArrayList of screens
     */
    public ArrayList<Screen> getScreen() {
        return screens;
    }

    /**
     * Sets the screen (name/description/instruction)
     * @param screens The screens to set
     */
    public void setScreen(ArrayList<Screen> screens) {
        this.screens = screens;
    }
    
    /**
     * Gets the introduction information for all help files
     * @return String value of the introduction
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * Sets the introduction information
     * @param introduction String value to display as an intro
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * Gets the overall name of the HelpFile
     * @return name as a string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the overall name of the HelpFile
     * @param name as a string
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the Screen object by name
     * @param screenName The screen to find
     * @return Screen for provided screenName
     */
    public Screen getSelectedScreenByName(String screenName)
    {
        Screen foundScreen = new Screen();
        
        foundScreen = screens.stream()
                .filter(s -> s.getName().equals(screenName))
                .findFirst()
                .orElse(new Screen());
        
        return foundScreen;
    }

    @Override
    public String toString() {
        return name + " Help Information";
    }
    
    /**
     * Gets the overall introduction text
     * @return String of introduction text
     */
    public String getDisplayText()
    {
        return introduction;
    }
    
    private DefaultMutableTreeNode root;

    /**
     * Gets the root value for the Help File tree
     * @return root node
     */
    public DefaultMutableTreeNode getRoot() {
        return root;
    }
    
    /**
     * Gets the default tree nodes, starting at root
     * @return The DefaultTreeModel starting at the root node
     */
    public DefaultTreeModel getAsDefaultTreeModel()
    {
        root = new DefaultMutableTreeNode(this);
        
        for(Screen screen : screens)
        {
            DefaultMutableTreeNode screenNode = new DefaultMutableTreeNode(screen);

            root.add(screenNode);
            
            for(Instruction instruction : screen.getInstruction())
            {
                screenNode.add(new DefaultMutableTreeNode(instruction));
            }
        }
        
        DefaultTreeModel model = new DefaultTreeModel(root);
        
        return model;
    }
    
}
