package mil.army.usace.ehlschlaeger.digitalpopulations.tabletools;

import java.util.List;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Extract a field from an array, polishing string on the fly. Excess white
 * space is trimmed from ends, and empty strings are converted to null.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class CleanGetter implements ColumnGetter {
    protected int colIdx;

    public CleanGetter(int columnIndex) {
        if(columnIndex < 0)
            throw new IllegalArgumentException("Column "+columnIndex+" does not exist.");
        this.colIdx = columnIndex;
    }

    public String get(String[] row) {
        String field = row[colIdx];
        if (ObjectUtil.isBlank(field))
            field = null;
        else
            field = field.trim();
        return field;
    }

    public String get(List<String> row) {
        String field = row.get(colIdx);
        if (ObjectUtil.isBlank(field))
            field = null;
        else
            field = field.trim();
        return field;
    }

    public String get(CSVTable table, int row) {
        String field = table.getStringAt(row, colIdx);
        if (ObjectUtil.isBlank(field))
            field = null;
        else
            field = field.trim();
        return field;
    }
}
