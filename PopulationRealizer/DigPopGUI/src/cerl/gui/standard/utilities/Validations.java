/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 * Utility for validating various number formats
 * @author mrivera
 */
public class Validations {
    /**
     * Checks if a string value is a valid Double
     * @param value - the value to check
     * @return true if it is a valid double, false if a NumberFormatException would be thrown
     */
    public static Boolean validateAndReturnDouble(String value){
        if(!value.equals("")){
            try{
                double d = Double.parseDouble(value);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }
        return false;
    }
    
    /**
     * Checks if a String value is a valid integer
     * @param value the String to check
     * @return true if it is a valid integer, false if not or if a NumberFormatException would be thrown
     */
    public static Boolean validateAndReturnInteger(String value){
        if(!value.equals("")){
            try{
                Integer i = Integer.parseInt(value);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }
        return false;
    }
    
    /**
     * Checks if a String value is a valid long
     * @param value the String to check
     * @return true if it is a valid Long, false if not or if a NumberFormatException would be thrown
     */
    public static Boolean validateAndReturnLong(String value){
        if(!value.equals("")){
            try{
                Long l = Long.parseLong(value);
                return true;
            }catch(NumberFormatException e){
                return false;
            }
        }
        return false;
    }
}
