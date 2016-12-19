/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author ajohnson
 */
public class NewCensusColumnDetails {
    private String newColumnHeader;
    private Integer oldColumnNumber;
    private Double min;
    private Double max;
    private Double randomPercentage;

    public NewCensusColumnDetails() {
        this.min = 0.0;
        this.max = 0.0;
        this.randomPercentage = 0.0;
    }

    public NewCensusColumnDetails(String newColumnHeader, Integer oldColumnNumber, Double min, Double max, Double randomPercentage) {
        this.newColumnHeader = newColumnHeader;
        this.oldColumnNumber = oldColumnNumber;
        this.min = min;
        this.max = max;
        this.randomPercentage = randomPercentage;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public String getNewColumnHeader() {
        return newColumnHeader;
    }

    public void setNewColumnHeader(String newColumnHeader) {
        this.newColumnHeader = newColumnHeader;
    }

    public Integer getOldColumnNumber() {
        return oldColumnNumber;
    }

    public void setOldColumnNumber(Integer oldColumnNumber) {
        this.oldColumnNumber = oldColumnNumber;
    }

    public Double getRandomPercentage() {
        return randomPercentage;
    }
    
    public void calculateNewRandomPercentage() {
        if(this.min.equals(this.max)){
            this.randomPercentage = this.max;
        }else {
            this.randomPercentage = ThreadLocalRandom.current().nextDouble(min, max);
            this.randomPercentage =Math.round(this.randomPercentage  * 100.0) / 100.0;
        }
    }
}
