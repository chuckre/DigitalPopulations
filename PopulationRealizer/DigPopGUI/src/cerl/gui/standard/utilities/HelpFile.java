/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author ajohnson
 */
@XmlRootElement(name="help")
public class HelpFile {
    private ArrayList<Screen> screen;

    public ArrayList<Screen> getScreen() {
        return screen;
    }

    public void setScreen(ArrayList<Screen> screen) {
        this.screen = screen;
    }
    
}
