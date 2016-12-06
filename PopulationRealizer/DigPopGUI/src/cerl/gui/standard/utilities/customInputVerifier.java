/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Creates a custom input verifier for the min and max values
 * @author mrivera
 */
public class customInputVerifier extends InputVerifier{
    private final String typeNeeded;
    private JLabel validationMessage;
    private double maximum;
    private double minimum;
        
    /**
     * Creates a new verifier for the supplied data type
     * TO DO: Handle more than just doubles
     * @param type - The class type to be verified
     */
    public customInputVerifier(String type){
        this.typeNeeded = type;
        
        if(type.equals("Integer")){
            this.minimum = Integer.MIN_VALUE;
            this.maximum = Integer.MAX_VALUE;
        } else if(type.equals("Double")){
            this.minimum = Double.MIN_VALUE;
            this.maximum = Double.MAX_VALUE;
        }
    }
    
    /**
     * Creates a new verifier for the supplied data type
     * TO DO: Handle more than just doubles
     * @param type - The class type to be verified
     * @param errorField - The label to be used to output error messages
     */
    public customInputVerifier(String type, JLabel errorField){
        this.typeNeeded = type;
        this.validationMessage = errorField;
        
        if(type.equals("Integer")){
            this.minimum = Integer.MIN_VALUE;
            this.maximum = Integer.MAX_VALUE;
        } else if(type.equals("Double")){
            this.minimum = Double.MIN_VALUE;
            this.maximum = Double.MAX_VALUE;
        }
    }
    
    /**
     * Creates a new verifier for the supplied data type
     * TO DO: Handle more than just doubles
     * @param type - The class type to be verified
     * @param maximum - The minimum value allowed
     * @param minimum  - The maximum value allowed
     */
    public customInputVerifier(String type, double maximum, double minimum){
        this.typeNeeded = type;
        this.maximum = maximum;
        this.minimum = minimum;
    }
        
    /***
     * Sets up the validation for the custom table cell popup
     * Rules - both min and max must be provided
     *       - Values must be between 0 and 0.99
     *       - Values must be valid doubles
     * @param input
     * @return 
     */
    @Override
    public boolean verify(JComponent input){
        JTextField text = (JTextField)input;

        try{
            double num = Double.parseDouble(text.getText());
            if((num > maximum) || (num < minimum)){
                validationMessage.setText("Value must be a valid " + typeNeeded + " between " + minimum + " and " + maximum);
                return false;
            }
            validationMessage.setText(" ");
            return true;
        }
        catch(NullPointerException n){
            validationMessage.setText("Value must be provided.");
            return false;
        }
        catch(NumberFormatException e){
            validationMessage.setText("Value must be a valid " + typeNeeded);
            return false;
        }
    }

    public String getValidationMessage() {
        return validationMessage.getText();
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }
}
