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
    private int columnNumber;
    private boolean selected;

    public Class() {
        this.className = null;
        this.classTotal = 0;
        this.columnNumber = 0;
        this.selected = false;
    }

    public Class(String className, int columnNumber, boolean selected) {
        this.className = className;
        this.classTotal = 0;
        this.columnNumber = columnNumber;
        this.selected = selected;
    }

    public Class(String className, long classTotal, int columnNumber, boolean selected) {
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

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void addToClassTotal(long addValue){
        this.classTotal += addValue;
    }

    @Override
    public String toString() {
        return className;
    }
}
