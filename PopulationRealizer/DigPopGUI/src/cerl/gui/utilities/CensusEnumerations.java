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
public class CensusEnumerations {
    private ArrayList<CensusEnumeration> censusEnumerations;

    public CensusEnumerations() {
        this.censusEnumerations = new ArrayList<CensusEnumeration>();
    }

    public CensusEnumerations(ArrayList<CensusEnumeration> censusEnumerations) {
        this.censusEnumerations = censusEnumerations;
    }

    public ArrayList<CensusEnumeration> getCensusEnumerations() {
        return censusEnumerations;
    }

    public void setCensusEnumerations(ArrayList<CensusEnumeration> censusEnumerations) {
        this.censusEnumerations = censusEnumerations;
    }
    
    public void addCensusEnumerations(CensusEnumeration censusEnumeration) {
        this.censusEnumerations.add(censusEnumeration);
    }
    
    
}
