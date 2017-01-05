/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.io.File;

/**
 *
 * @author ajohnson
 */
public class ConstraintMap {
    private String filePath;
    private Forbid forbid;
    private String id;

    public ConstraintMap() {
        this.filePath = "";
        this.forbid = new Forbid();
    }

    public ConstraintMap(String filePath, Forbid forbid, String id) {
        this.filePath = filePath;
        this.forbid = forbid;
        this.id = id;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Forbid getForbid() {
        return forbid;
    }

    public void setForbid(Forbid forbid) {
        this.forbid = forbid;
    }
    
    public void addNewForbid(String desc, String mapSelect, String pumsTraitTable, String pumsTraitField, String pumsTraitSelect) {
        String fileName = (new File(this.filePath)).getName();
        Forbid newForbid = new Forbid(this.id,desc,fileName,mapSelect,pumsTraitTable,pumsTraitField,pumsTraitSelect);
        setForbid(newForbid);
    }

    @Override
    public String toString() {
       // if(forbid.equals(new Forbid())){
            return filePath;
//        }else
//        {
//            return "ConstraintMap: " + filePath + ", forbid=" + forbid + '}';
//        }
    }
}
