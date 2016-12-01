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
    private String allowedDataType;
    private boolean editable;
    private boolean error;

    public customTableCell(Object value, boolean editable, String allowedDataType, boolean error) {
        this.value = value;
        this.editable = editable;
        this.allowedDataType = allowedDataType;
        this.error = error;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEditable() {
        return editable;
    }

    public String getAllowedDataType() {
        return allowedDataType;
    }

    public boolean isError() {
        return error;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setAllowedDataType(String allowedDataType) {
        this.allowedDataType = allowedDataType;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
