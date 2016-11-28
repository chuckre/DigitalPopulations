/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

/**
 *
 * @author ajohnson
 */
public class SurveyMicroDataHouseHoldClass {
    private Object className;
    private Object value;

    public SurveyMicroDataHouseHoldClass() {
    }

    public SurveyMicroDataHouseHoldClass(Object className, Object value) {
        this.className = className;
        this.value = value;
    }

    public Object getClassName() {
        return className;
    }

    public void setClassName(Object className) {
        this.className = className;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    
}
