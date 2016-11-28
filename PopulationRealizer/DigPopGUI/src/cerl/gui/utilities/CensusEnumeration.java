/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;

/**
 *
 * @author ajohnson
 */
public class CensusEnumeration {
    private Object GISJOIN;
    private Object UNIQUE_ID;
    private Object YEAR;
    private Object STATEA;
    private Object COUNTY;
    private Object COUNTYA;
    private Object TRACTA;
    private Object BLKGRPA;
    private Object NAME_E;
    private ArrayList<CensusEnumerationClass> censusEnumerationClasses;

    public CensusEnumeration() {
        this.censusEnumerationClasses =new ArrayList<CensusEnumerationClass>();
    }

    public CensusEnumeration(Object GISJOIN, Object UNIQUE_ID, Object YEAR, Object STATEA, Object COUNTY, Object COUNTYA, Object TRACTA, Object BLKGRPA, Object NAME_E, ArrayList<CensusEnumerationClass> censusEnumerationClasses) {
        this.GISJOIN = GISJOIN;
        this.UNIQUE_ID = UNIQUE_ID;
        this.YEAR = YEAR;
        this.STATEA = STATEA;
        this.COUNTY = COUNTY;
        this.COUNTYA = COUNTYA;
        this.TRACTA = TRACTA;
        this.BLKGRPA = BLKGRPA;
        this.NAME_E = NAME_E;
        this.censusEnumerationClasses = censusEnumerationClasses;
    }

    

    public Object getGISJOIN() {
        return GISJOIN;
    }

    public void setGISJOIN(Object GISJOIN) {
        this.GISJOIN = GISJOIN;
    }

    public Object getUNIQUE_ID() {
        return UNIQUE_ID;
    }

    public void setUNIQUE_ID(Object UNIQUE_ID) {
        this.UNIQUE_ID = UNIQUE_ID;
    }

    public Object getYEAR() {
        return YEAR;
    }

    public void setYEAR(Object YEAR) {
        this.YEAR = YEAR;
    }

    public Object getSTATEA() {
        return STATEA;
    }

    public void setSTATEA(Object STATEA) {
        this.STATEA = STATEA;
    }

    public Object getCOUNTY() {
        return COUNTY;
    }

    public void setCOUNTY(Object COUNTY) {
        this.COUNTY = COUNTY;
    }

    public Object getCOUNTYA() {
        return COUNTYA;
    }

    public void setCOUNTYA(Object COUNTYA) {
        this.COUNTYA = COUNTYA;
    }

    public Object getTRACTA() {
        return TRACTA;
    }

    public void setTRACTA(Object TRACTA) {
        this.TRACTA = TRACTA;
    }

    public Object getBLKGRPA() {
        return BLKGRPA;
    }

    public void setBLKGRPA(Object BLKGRPA) {
        this.BLKGRPA = BLKGRPA;
    }

    public Object getNAME_E() {
        return NAME_E;
    }

    public void setNAME_E(Object NAME_E) {
        this.NAME_E = NAME_E;
    }

    public ArrayList<CensusEnumerationClass> getExtraColumnValues() {
        return censusEnumerationClasses;
    }

    public void setExtraColumnValues(ArrayList<CensusEnumerationClass> censusEnumerationClasses) {
        this.censusEnumerationClasses = censusEnumerationClasses;
    }

    public void addCensusEnumerationClass(CensusEnumerationClass censusEnumerationClass){
        this.censusEnumerationClasses.add(censusEnumerationClass);
    }
    
}
