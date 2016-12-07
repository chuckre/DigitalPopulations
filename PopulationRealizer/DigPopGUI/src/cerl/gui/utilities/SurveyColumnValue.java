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
public class SurveyColumnValue  implements Cloneable {
    private int rowId;
    private int value;
    private boolean used;

    public SurveyColumnValue() {
        used = false;
    }

    public SurveyColumnValue(int rowId, int value, boolean used) {
        this.rowId = rowId;
        this.value = value;
        this.used = used;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "Row ID: " + rowId + " Value: " + value;
    }
    
    public SurveyColumnValue clone() throws CloneNotSupportedException 
    {
        SurveyColumnValue clonedSurveyColumnValue = (SurveyColumnValue) super.clone();
 
        return clonedSurveyColumnValue;
    }
}
