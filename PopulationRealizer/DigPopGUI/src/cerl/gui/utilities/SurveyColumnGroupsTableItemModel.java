/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ajohnson
 */
public class SurveyColumnGroupsTableItemModel extends AbstractTableModel {

    private String[] columnNames = new String[]{"Survey Data Column Values", "User Defined Description"};
    
    private List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings;

    public SurveyColumnGroupsTableItemModel(List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings) {
        this.surveyColumnValuesGroupings = surveyColumnValuesGroupings;
    }

    public List<SurveyColumnValuesGrouping> getSurveyColumnValuesGroupings() {
        return surveyColumnValuesGroupings;
    }
    
    @Override
    public int getRowCount() {
        return surveyColumnValuesGroupings.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object value = "";
        SurveyColumnValuesGrouping selectedSurveyColumnValuesGrouping = surveyColumnValuesGroupings.get(rowIndex);
        switch (columnIndex) {
            case 0:
                value = selectedSurveyColumnValuesGrouping.getAllRowIdsAsString();
                break;
            case 1:
                value = selectedSurveyColumnValuesGrouping.getUserDefinedDescription();
                break;
        }
        return value;
    }
    
    @Override
    public boolean isCellEditable(int rowId, int columnId) {
        
        boolean value = false;
        
        switch (columnId) {
            case 0:
                value = false;
                break;
            case 1:
                value = true;
                break;
        }
        
        return value;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            SurveyColumnValuesGrouping editedSurveyColumnValuesGrouping = surveyColumnValuesGroupings.get(row);
            
            editedSurveyColumnValuesGrouping.setUserDefinedDescription((String) value);
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
