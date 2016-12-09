/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 *
 * @author mrivera
 */
public class Validations {
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
