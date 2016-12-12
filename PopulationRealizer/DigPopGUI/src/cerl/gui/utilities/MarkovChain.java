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
public class MarkovChain {
    private String mackovName;
    private ArrayList<cerl.gui.utilities.Class> censusClasses;
    private cerl.gui.utilities.Class selectSurveyClass;
    private int id;
    private long allCensusTotal = 0;

    public MarkovChain() {
        this.censusClasses = censusClasses = new ArrayList<cerl.gui.utilities.Class>();
        this.selectSurveyClass = new cerl.gui.utilities.Class();
    }
    
    public MarkovChain(String mackovName, ArrayList<Class> censusClasses, Class selectSurveyClass, int id) {
        this.mackovName = mackovName;
        this.censusClasses = censusClasses;
        this.selectSurveyClass = selectSurveyClass;
        this.id = id;
    }

    public long getAllCensusTotal() {
        this.censusClasses.stream().forEach((c) -> {
            this.allCensusTotal += c.getClassTotal();
        });
        
        return allCensusTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getMackovName() {
        return mackovName;
    }

    public void setMackovName(String mackovName) {
        this.mackovName = mackovName;
    }

    public ArrayList<Class> getCensusClasses() {
        return censusClasses;
    }

    public void setCensusClasses(ArrayList<Class> censusClasses) {
        this.censusClasses = censusClasses;
    }

    public Class getSelectSurveyClass() {
        return selectSurveyClass;
    }

    public void setSelectSurveyClass(Class selectSurveyClass) {
        this.selectSurveyClass = selectSurveyClass;
    }
    
    public ArrayList<String> getAllSelectedCensusClassesUserDefinedNames() {
        ArrayList<String> userDefinedNames =  new ArrayList<>();
        
        censusClasses.stream().forEach((c) -> {
                userDefinedNames.add(c.getUserDefinedDescription());
        });
        
        return userDefinedNames;
    }
}
