/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 * Creates a basic, custom table cell object for use in the custom table model
 * @author mrivera
 */
public class customTableCell {
    private Object value;
    private boolean editable;

    public customTableCell(Object value, boolean editable) {
        this.value = value;
        this.editable = editable;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
