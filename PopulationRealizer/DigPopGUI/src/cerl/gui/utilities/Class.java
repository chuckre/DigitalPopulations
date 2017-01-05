/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;

/**
 * The census and survey classes
 * @author ajohnson
 */
public class Class implements Cloneable {
    private String className; 
    private long classTotal;
    private int columnNumber;
    private boolean selected;
    private int idForFinding;
    private String userDefinedDescription;
    private ArrayList<SurveyColumnValue> allSurveyColumnValues;
    private ArrayList<SurveyColumnValuesGrouping> surveyColumnValuesGroupings;
    private DigPopFileTypeEnum digPopFileTypeEnum;

    /**
     * Creates a new empty class
     */
    public Class() {
        this.className = null;
        this.classTotal = 0;
        this.columnNumber = 0;
        this.selected = false;
        this.allSurveyColumnValues = new ArrayList<>();
        this.surveyColumnValuesGroupings = new ArrayList<>();
    }

    /**
     * Creates a new Class
     * @param className - the string name of the new class
     * @param columnNumber - the integer value of the number of columns
     * @param selected - true if the Class is selected, false if not
     * @param idForFinding - The unique integer value for the ID to reference
     * @param digPopFileTypeEnum - the type of file (e.g. Land_Use_Map, Region_Map)
     */
    public Class(String className, int columnNumber, boolean selected, int idForFinding, DigPopFileTypeEnum digPopFileTypeEnum) {
        this.className = className;
        this.classTotal = 0;
        this.columnNumber = columnNumber;
        this.selected = selected;
        this.idForFinding = idForFinding;
        this.userDefinedDescription = "";
        this.allSurveyColumnValues = new ArrayList<>();
        this.surveyColumnValuesGroupings = new ArrayList<>();
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    /**
     * Creates a new Class
     * @param className - the string name of the new class
     * @param classTotal - the long value for class totals
     * @param columnNumber - the integer value of the number of columns
     * @param selected - true if the Class is selected, false if not
     * @param idForFinding - The unique integer value for the ID to reference
     * @param userDefinedDescription - the string value for the user description
     * @param allSurveyColumnValues - the List of survey column values
     * @param surveyColumnValuesGroupings - the List of survey column groupings
     * @param digPopFileTypeEnum - the type of file (e.g. Land_Use_Map, Region_Map)
     */
    public Class(String className, long classTotal, int columnNumber, boolean selected, int idForFinding, String userDefinedDescription, List<SurveyColumnValue> allSurveyColumnValues, List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings, DigPopFileTypeEnum digPopFileTypeEnum) {
        this.className = className;
        this.classTotal = classTotal;
        this.columnNumber = columnNumber;
        this.selected = selected;
        this.idForFinding = idForFinding;
        this.userDefinedDescription = userDefinedDescription;
        this.allSurveyColumnValues = new ArrayList<>(allSurveyColumnValues);
        this.surveyColumnValuesGroupings = new ArrayList<>(surveyColumnValuesGroupings);
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    /**
     * Gets the DigPop File Type
     * @return the type of File (e.g. Land_Use_Map, Household_Density_Map)
     */
    public DigPopFileTypeEnum getDigPopFileTypeEnum() {
        return digPopFileTypeEnum;
    }

    /**
     * Sets the DigPop File Type
     * @param digPopFileTypeEnum - the new type of the Class 
     */
    public void setDigPopFileTypeEnum(DigPopFileTypeEnum digPopFileTypeEnum) {
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    /**
     * Gets the List of all survey column value groupings created in Step 3
     * @return 
     */
    @XmlElement
    public List<SurveyColumnValuesGrouping> getSurveyColumnValuesGroupings() {
        return surveyColumnValuesGroupings;
    }

    /**
     * Sets the list of survey column value groupings from Step 3
     * @param surveyColumnValuesGroupings - the new ArrayList of groups
     */
    public void setSurveyColumnValuesGroupings(ArrayList<SurveyColumnValuesGrouping> surveyColumnValuesGroupings) {
        this.surveyColumnValuesGroupings = surveyColumnValuesGroupings;
    }

    /**
     * Gets all survey column values, whether they are used or not
     * @return the List of all SurveyColumnValues
     */
    public List<SurveyColumnValue> getAllSurveyColumnValues() {
        return allSurveyColumnValues;
    }
    
    /**
     * Gets all the survey column values that have not yet been used
     * @return - the List of all unused SurveyColumnValues
     */
    public List<SurveyColumnValue> getAllSurveyColumnValuesNotUsed() {
        
        return allSurveyColumnValues.stream().filter(c-> !c.isUsed()).collect(Collectors.toList());
    }

    /**
     * Sets the list of all survey column values
     * @param allSurveyColumnValues  - the ArrayList of all survey columns
     */
    public void setAllSurveyColumnValues(ArrayList<SurveyColumnValue> allSurveyColumnValues) {
        this.allSurveyColumnValues = allSurveyColumnValues;
    }
    
    /**
     * Clears the ArrayList of survey Column values
     * @param allSurveyColumnValues - the list to remove
     */
    public void removeFromAllSurveyColumnValues(ArrayList<SurveyColumnValue> allSurveyColumnValues) {
        this.allSurveyColumnValues.removeAll(allSurveyColumnValues);//.remove(allSurveyColumnValues);
    }

    /**
     * Gets the user defined description of the current Class
     * @return string description of the class
     */
    public String getUserDefinedDescription() {
        return userDefinedDescription;
    }

    /**
     * Sets the string description for the current class
     * @param userDefinedDescription - the string value of the class
     */
    public void setUserDefinedDescription(String userDefinedDescription) {
        this.userDefinedDescription = userDefinedDescription;
    }
    
    /**
     * Gets the unique ID of the current class
     * @return - ID of the class to find
     */
    public int getIdForFinding() {
        return idForFinding;
    }

    /**
     * Sets the unique ID for the current class
     * @param idForFinding - the unique ID for finding later
     */
    public void setIdForFinding(int idForFinding) {
        this.idForFinding = idForFinding;
    }

    /**
     * Gets the string value for the current class name
     * @return string of the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the string value of the current class name
     * @param className - the new name to set as the class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the long value of the current class total
     * @return a long value of the class total
     */
    public long getClassTotal() {
        return classTotal;
    }

    /**
     * Sets the class total as a long value
     * @param classTotal the long value to set as the new class total
     */
    public void setClassTotal(long classTotal) {
        this.classTotal = classTotal;
    }

    /**
     * Gets the integer value of the column number
     * @return the column number as an int
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Sets the column number
     * @param columnNumber the int value for the new column Number
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * returns if the class is selected
     * @return true if selected, false if not
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets if the Class is selected
     * @param selected true if selected, false if not
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * Adds the provided value to the running class total
     * @param addValue - adds the long value to the current ClassTotal
     */
    public void addToClassTotal(long addValue){
        this.classTotal += addValue;
    }
    
    /**
     * Adds a new group to the Survey Column Groupings
     * @param surveyColumnValuesGrouping - the new survey group to add
     */
    public void addSurveyGrouping(SurveyColumnValuesGrouping surveyColumnValuesGrouping){
        this.surveyColumnValuesGroupings.add(surveyColumnValuesGrouping);
    }
    
    /**
     * Gets the current total of all survey column groups
     * @return the class total for all survey column groups
     */
    public long getAllSurveyGroupsTotal(){
        this.surveyColumnValuesGroupings.stream().forEach((s) -> {
           addToClassTotal(s.calculateGroupingTotal());
        });
        
        return this.classTotal;
    }

    /**
     * Provides a string value of the class name and the user defined description
     * @return className - custom description if provided, else just class name
     */
    @Override
    public String toString() {
        if(this.userDefinedDescription != null && !this.userDefinedDescription.equals(""))
        {
            return this.className + " - " + this.userDefinedDescription;
        }
        else {
            return this.className;
        }
    }
    
    /**
     * Clones the current class
     * @return a clone of the current class
     * @throws CloneNotSupportedException thrown if the class can't be cloned
     */
    public Class clone() throws CloneNotSupportedException 
    {
        Class clonedClass = (Class) super.clone();
 
        return clonedClass;
    }
}
