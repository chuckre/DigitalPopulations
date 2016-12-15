/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

    public customTableCell(Object value, boolean editable, String allowedDataType, boolean error) {
        this.value = value;
        this.editable = editable;
        this.allowedDataType = allowedDataType;
        this.error = error;
    }

    @XmlAttribute(name="value")
    public Object getValue() {
        return value;
    }

    @XmlAttribute(name="editable")
    public boolean isEditable() {
        return editable;
    }

    @XmlAttribute(name="dataType")
    public String getAllowedDataType() {
        return allowedDataType;
    }

    @XmlAttribute(name="isError")
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
        if(value != null){
            return value.toString();
        } else{
            return "";
        }
    }
}
