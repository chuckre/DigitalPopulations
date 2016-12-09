/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ajohnson
 */
public class Class implements Cloneable {
    private String className; 
    private long classTotal;
    private int columnNumber;
    private boolean selected;
    private int idForFinding;
    private String userDefinedDescription;
    private List<SurveyColumnValue> allSurveyColumnValues;
    private List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings;
    private DigPopFileTypeEnum digPopFileTypeEnum;

    public Class() {
        this.className = null;
        this.classTotal = 0;
        this.columnNumber = 0;
        this.selected = false;
        this.allSurveyColumnValues = new ArrayList<SurveyColumnValue>();
        this.surveyColumnValuesGroupings = new ArrayList<SurveyColumnValuesGrouping>();
    }

    public Class(String className, int columnNumber, boolean selected, int idForFinding, DigPopFileTypeEnum digPopFileTypeEnum) {
        this.className = className;
        this.classTotal = 0;
        this.columnNumber = columnNumber;
        this.selected = selected;
        this.idForFinding = idForFinding;
        this.userDefinedDescription = "";
        this.allSurveyColumnValues = new ArrayList<SurveyColumnValue>();
        this.surveyColumnValuesGroupings = new ArrayList<SurveyColumnValuesGrouping>();
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    public Class(String className, long classTotal, int columnNumber, boolean selected, int idForFinding, String userDefinedDescription, List<SurveyColumnValue> allSurveyColumnValues, List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings, DigPopFileTypeEnum digPopFileTypeEnum) {
        this.className = className;
        this.classTotal = classTotal;
        this.columnNumber = columnNumber;
        this.selected = selected;
        this.idForFinding = idForFinding;
        this.userDefinedDescription = userDefinedDescription;
        this.allSurveyColumnValues = allSurveyColumnValues;
        this.surveyColumnValuesGroupings = surveyColumnValuesGroupings;
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    public DigPopFileTypeEnum getDigPopFileTypeEnum() {
        return digPopFileTypeEnum;
    }

    public void setDigPopFileTypeEnum(DigPopFileTypeEnum digPopFileTypeEnum) {
        this.digPopFileTypeEnum = digPopFileTypeEnum;
    }

    public List<SurveyColumnValuesGrouping> getSurveyColumnValuesGroupings() {
        return surveyColumnValuesGroupings;
    }

    public void setSurveyColumnValuesGroupings(ArrayList<SurveyColumnValuesGrouping> surveyColumnValuesGroupings) {
        this.surveyColumnValuesGroupings = surveyColumnValuesGroupings;
    }

    public List<SurveyColumnValue> getAllSurveyColumnValues() {
        return allSurveyColumnValues;
    }
    
    public List<SurveyColumnValue> getAllSurveyColumnValuesNotUsed() {
        
        return allSurveyColumnValues.stream().filter(c-> !c.isUsed()).collect(Collectors.toList());
    }

    public void setAllSurveyColumnValues(ArrayList<SurveyColumnValue> allSurveyColumnValues) {
        this.allSurveyColumnValues = allSurveyColumnValues;
    }
    
    public void removeFromAllSurveyColumnValues(ArrayList<SurveyColumnValue> allSurveyColumnValues) {
        this.allSurveyColumnValues.remove(allSurveyColumnValues);
    }

    public String getUserDefinedDescription() {
        return userDefinedDescription;
    }

    public void setUserDefinedDescription(String userDefinedDescription) {
        this.userDefinedDescription = userDefinedDescription;
    }
    
    public int getIdForFinding() {
        return idForFinding;
    }

    public void setIdForFinding(int idForFinding) {
        this.idForFinding = idForFinding;
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
    
    public void addSurveyGrouping(SurveyColumnValuesGrouping surveyColumnValuesGrouping){
        this.surveyColumnValuesGroupings.add(surveyColumnValuesGrouping);
    }

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
    
  
    public Class clone() throws CloneNotSupportedException 
    {
        Class clonedClass = (Class) super.clone();
 
        return clonedClass;
    }
}
