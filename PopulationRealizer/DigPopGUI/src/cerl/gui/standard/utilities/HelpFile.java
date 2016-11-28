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
 *
 * @author ajohnson
 */
@XmlRootElement(name="help")
public class HelpFile {
    private ArrayList<Screen> screens;
    private String introduction;
    private String name;

    public ArrayList<Screen> getScreen() {
        return screens;
    }

    public void setScreen(ArrayList<Screen> screens) {
        this.screens = screens;
    }
    
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
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
    
    public String getDisplayText()
    {
        return introduction;
    }
    
    private DefaultMutableTreeNode root;

    public DefaultMutableTreeNode getRoot() {
        return root;
    }
    
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
