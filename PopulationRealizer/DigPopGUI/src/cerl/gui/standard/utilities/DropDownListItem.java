/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

/**
 * New item used for dropdown lists as id/name pair
 * @author mrivera
 */
public class DropDownListItem {
    private final int id;
    private final String description;
 
    public DropDownListItem(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
