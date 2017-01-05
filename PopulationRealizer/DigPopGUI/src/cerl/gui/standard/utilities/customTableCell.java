/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creates a basic, custom table cell object for use in the custom table model
 * @author mrivera
 */
@XmlRootElement(name="customTableCell")
public class customTableCell {
    private Object value;
    private String allowedDataType;
    private boolean editable;
    private boolean error;

    /**
     * Creates a blank new table cell
     */
    public customTableCell(){
    }
    
    /**
     * Creates a new custom Table Cell
     * @param value - the value of the cell
     * @param editable - if the cell is editable
     * @param allowedDataType - the data type allowed
     * @param error - if the cell has an error in it
     */
    public customTableCell(Object value, boolean editable, String allowedDataType, boolean error) {
        this.value = value;
        this.editable = editable;
        this.allowedDataType = allowedDataType;
        this.error = error;
    }

    /**
     * Gets the value of the current cell
     * @return Object value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the true/false value if the cell is editable
     * @return true if editable, false if not
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Gets the string value of the allowed data type for the cell
     * @return string value of the allowed data type
     */
    public String getAllowedDataType() {
        return allowedDataType;
    }

    /**
     * Checks if the error flag is set on the current cell
     * @return true if there is an error, false if not
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets the value of the current cell
     * @param value - the object for the new value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Sets the editable flag for the current cell
     * @param editable - true if it should be editable, false if not
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Sets the allowed data type for the cell
     * @param allowedDataType - A string value for the type allowed (e.g. "Double")
     */
    public void setAllowedDataType(String allowedDataType) {
        this.allowedDataType = allowedDataType;
    }

    /**
     * Sets the error flag for the current cell
     * @param error true if an error is in the cell, false if not
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Returns a string representation of the current value, or empty string if it's null
     * @return string of value or empty string if null
     */
    @Override
    public String toString() {
        if(value != null){
            return value.toString();
        } else{
            return "";
        }
    }
}
