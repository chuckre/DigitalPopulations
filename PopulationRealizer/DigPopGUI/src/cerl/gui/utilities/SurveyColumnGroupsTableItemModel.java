/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.utilities;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * The custom table model for grouping survey column information together
 * @author ajohnson
 */
public class SurveyColumnGroupsTableItemModel extends AbstractTableModel {

    private final String[] columnNames = new String[]{"Survey Data Column Values", "User Defined Description"};
    private final List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings;

    /**
     * Creates a new table model from the list of survey column groupings
     * @param surveyColumnValuesGroupings - the List of survey column groupings to create the table model
     */
    public SurveyColumnGroupsTableItemModel(List<SurveyColumnValuesGrouping> surveyColumnValuesGroupings) {
        this.surveyColumnValuesGroupings = surveyColumnValuesGroupings;
    }

    /**
     * The list of all survey column groupings in the table model
     * @return the List of Survey Column groups
     */
    public List<SurveyColumnValuesGrouping> getSurveyColumnValuesGroupings() {
        return surveyColumnValuesGroupings;
    }
    
    /**
     * Gets the number of rows in the list of survey column groups
     * @return 
     */
    @Override
    public int getRowCount() {
        return surveyColumnValuesGroupings.size();
    }
    
    /**
     * Gets the number of columns, as the length of column names
     * @return the number of columns in the table
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets the value in the table for a specific cell by row/column index
     * @param rowIndex - the row to find the cell
     * @param columnIndex - the column of the cell
     * @return the Object value of the cell in the table
     */
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
    
    /**
     * Checks the editable flag for the cell in the table at the given row/column
     * @param rowId - the row index of the cell to check
     * @param columnId - the column index of the cell to check
     * @return - true if the cell is editable, else false
     */
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
    
    /**
     * Sets the value in the table for the given row/column
     * @param value - the new value for the cell
     * @param row - the row of the cell to change
     * @param col - the column of the cell to change
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof String) {
            SurveyColumnValuesGrouping editedSurveyColumnValuesGrouping = surveyColumnValuesGroupings.get(row);
            
            editedSurveyColumnValuesGrouping.setUserDefinedDescription((String) value);
        }
    }
    
    /**
     * Gets the name of the column at the given index
     * @param columnIndex - the index of the column to get the name of
     * @return - the column name at the specified index
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Gets the Abstract Table Model's table listeners
     * @return 
     */
    @Override
    public TableModelListener[] getTableModelListeners() {
        return super.getTableModelListeners(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
