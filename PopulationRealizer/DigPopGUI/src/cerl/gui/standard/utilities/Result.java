/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 * Result Class
 * Stores information to handle result information.
 * 
 * Handles if a result was a Success or Failure. 
 * 
 * Holds a return objects. 
 * 
 * Holds error messages. 
 * 
 * @author ajohnson
 */
public class Result {
    
    /**
     * Variable for success.
     * True: if function was a success.
     * False: if function was a failure.
     */
    private boolean success;
    
    /**
     * Variable for the return value of function
     */
    private Object value;
    
    /**
     * Variable for the error message
     */
    private String errorMessage;

    /**
     * Constructor
     */
    public Result() {
        this.success = false;
        this.value = null;
        this.errorMessage = "";
    }

    /**
     * Constructor
     * @param success 
     */
    public Result(boolean success) {
        this.success = success;
    }

    /**
     * Constructor
     * @param value 
     */
    public Result(Object value) {
        this.value = value;
    }

    /**
     * Constructor
     * @param errorMessage 
     */
    public Result(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor
     * @param success
     * @param value 
     */
    public Result(boolean success, Object value) {
        this.success = success;
        this.value = value;
    }

    /**
     * Constructor
     * @param success
     * @param errorMessage 
     */
    public Result(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor
     * @param value
     * @param errorMessage 
     */
    public Result(Object value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor
     * @param success
     * @param value
     * @param errorMessage 
     */
    public Result(boolean success, Object value, String errorMessage) {
        this.success = success;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    /**
     * Getter for the variable: Success
     * 
     * @return 
     * True: if function was a success.
     * False: if function was a failure.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter for the variable: Success
     * @param success 
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter for the variable: value
     * @return 
     */
    public Object getValue() {
        return value;
    }

    /**
     * Setter for the variable: value
     * @param value 
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Getter for the variable: errorMessage
     * @return 
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Setter for the variable: errorMessage
     * Appends a new error message to the existing error message. 
     * Starts on new line if this.errorMessage 
     * already contains an error message.
     * @param errorMessage 
     */
    public void setErrorMessage(String errorMessage) {
        if(this.errorMessage != null
                && !this.errorMessage.isEmpty())
        {
            this.errorMessage = String.format(
                    "%s\n%s",
                    this.errorMessage,
                    errorMessage);
        }
        else
        {
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * Setter for the variable: errorMessage
     * Uses both the error function name and an error message.
     * Appends a new error message to the existing error message. 
     * Starts on new line if this.errorMessage 
     * already contains an error message.
     * Uses both the error function name and an error message.
     * @param errorFunction
     * @param errorMessage 
     */
    public void setErrorMessage(String errorFunction, String errorMessage) {
        if(this.errorMessage != null
                && !this.errorMessage.isEmpty())
        {
            this.errorMessage = String.format(
                    "%s\n%s: %s",
                    this.errorMessage,
                    errorFunction,
                    errorMessage);
        }
        else
        {
            this.errorMessage = String.format(
                "%s: %s",
                errorFunction, 
                errorMessage);
        }
    }
}
