/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * The utility used for resizing images in the DigPop GUI
 * @author ajohnson
 */
public class ImageUtility {
    /**
     * Scales the provided image to the new height/width
     * @param filePath - The image to resize
     * @param width - The new width to scale to
     * @param height - The new height to scale to
     * @return ImageIcon - the newly resized image
     */
    public static ImageIcon CreateSizedImageIconScaledSmooth(
            URL filePath, 
            int width, 
            int height){
        
        ImageIcon newImageIcon = new ImageIcon(filePath);
        Image image = newImageIcon.getImage();
        image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        newImageIcon = new ImageIcon(image);
        
        return newImageIcon;
    }
    
}
