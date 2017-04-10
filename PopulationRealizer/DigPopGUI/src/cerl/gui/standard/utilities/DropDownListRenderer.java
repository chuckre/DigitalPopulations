/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * Used to render custom id/value pairs for dropdownlists
 * @author mrivera
 */
public class DropDownListRenderer extends BasicComboBoxRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null)
        {
            DropDownListItem item = (DropDownListItem)value;
            setText(item.getDescription());
        }

        return this;
    }  
}
