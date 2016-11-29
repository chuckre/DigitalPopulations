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
public class Class {
    private String className; 
    private long classTotal;
    private long columnNumber;
    private boolean selected;

    public Class() {
        this.className = null;
        this.classTotal = 0;
        this.columnNumber = 0;
        this.selected = false;
    }

    public Class(String className, long columnNumber, boolean selected) {
        this.className = className;
        this.classTotal = 0;
        this.columnNumber = columnNumber;
        this.selected = selected;
    }

    public Class(String className, long classTotal, long columnNumber, boolean selected) {
        this.className = className;
        this.classTotal = classTotal;
        this.columnNumber = columnNumber;
        this.selected = selected;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getClassTotal() {
        return classTotal;
    }

    public void setClassTotal(long classTotal) {
        this.classTotal = classTotal;
    }

    public long getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(long columnNumber) {
        this.columnNumber = columnNumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void addToClassTotal(long addValue){
        this.classTotal = this.classTotal + addValue;
    }
}
