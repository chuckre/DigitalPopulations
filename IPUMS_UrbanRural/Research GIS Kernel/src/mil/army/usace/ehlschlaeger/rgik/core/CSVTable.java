package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;



/**
 * Manages a table (rows and columns) of data.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class CSVTable extends DefaultTableModel implements Serializable {
    // File we were loaded from.
    protected File              sourceFile = null;
    
    /**
     * Create new empty table.
     */
    public CSVTable() {
    }

    /**
     * Load a table from a file.
     * 
     * @param csvFile
     * @throws IOException
     */
    public CSVTable(String csvFile) throws IOException {
        this.sourceFile = findFile(csvFile);
        loadCSV(this, sourceFile);
    }

    public CSVTable(String csvFile, Set<String> keepCols)
            throws IOException {
        this.sourceFile = findFile(csvFile);
        if(keepCols == null)
            loadCSV(this, sourceFile);
        else
            loadCSV(this, sourceFile, keepCols);
    }

    /**
     * @return file from which we were loaded, or null
     */
    public File getFile() {
        return sourceFile;
    }

    /**
     * @return a descriptive identifier for this table
     */
    public String getTitle() {
        String t = getFile()==null ? "Table " + toString() : "Table " + getFile().getName();
        return t;
    }
    
    /**
     * Attach file extension if necessary.
     * 
     * @param filename path and name of file to probe
     * @return path and name of file that exists
     * 
     * @throws FileNotFoundException if no suitable file could be found
     */
    public static File findFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if(!file.exists()) {
            file = new File(filename+".csv");
            if(!file.exists())
                throw new FileNotFoundException(filename);
        }
        return file;
    }
    
    /**
     * Add a new column with given name.  If there is data already in that
     * column, its column number will be changed.  Each field will be filled
     * (or overwritten) with its row's index.
     * 
     * @param keyColumnName name of column to create.
     */
    public int addKeyColumn(String keyColumnName) {
        this.addColumn(keyColumnName);
        int columnNumber = this.findColumn(keyColumnName);
        for (int r = this.getRowCount() - 1; r >= 0; r--) {
            this.setValueAt(Integer.toString(r), r, columnNumber);
        }
        return columnNumber;
    }

    /**
     * Add a row to the bottom.
     * 
     * @param values
     */
    public void addRow(List<String> values) {
        this.addRow(values.toArray());
    }

    /**
     * returns value less than 0 if no attribute matches cell in columnNumber
     */
    public int findFirstRowWithAttribute(int columnNumber, String attribute) {
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String possibleID = getStringAt(i, columnNumber);
            // System.out.println( i + ", " + attribute + ", " + possibleID);
            if (possibleID.compareTo(attribute) == 0) {
                return i;
            }
        }
        return (-1);
    }

    /**
     * Find and return the index of every row that has the given value in the
     * given column.
     * 
     * @param columnNumber
     *            index of column to test
     * @param attribute
     *            value to find in that column
     * @return array of indices of every row that matches, or null if none found
     */
    public int[] findAllRowsWithAttribute(int columnNumber, String attribute) {
        int rowCount = getRowCount();
        int matchs = 0;
        int[] m = new int[10];
        for (int i = 0; i < rowCount; i++) {
            String possibleID = getStringAt(i, columnNumber);
            if (possibleID.compareTo(attribute) == 0) {
                m[matchs] = i;
                matchs++;
                if (matchs == m.length) {
                    // ran out of space, double array
                    m = Arrays.copyOf(m, matchs * 2);
                }
            }
        }
        if (matchs == 0)
            return null;
        // trim array to exact requirement
        m = Arrays.copyOf(m, matchs);
        return m;
    }

    /**
     * Find index of named column.
     * @param columnName name of column to find
     * @return index of named column
     * @throws DataException if column isn't found
     */
    public int findColumn(String columnName) {
        int idx = super.findColumn(columnName);
        if (idx < 0) {
            throw new DataException(String.format("Cannot find column \"%s\" in %s.",
                                                  columnName, getTitle()));
        }
        return idx;
    }

    /**
     * Fetch the names of all columns at once.
     * @return an array of column names
     */
    public String[] getColumnNames() {
        String[] n = new String[getColumnCount()];
        for (int i = 0; i < n.length; i++) {
            n[i] = getColumnName(i);
        }
        return n;
    }
    
    /**
     * Extract a column (one cell from every row in the table).
     * 
     * @param colIndex
     *            indes of column to extract
     * @return Vector<String> of result where first element is from first row,
     *         etc.
     */
    public Vector<String> getColumn(int colIndex) {
        Vector<String> dataVector = new Vector<String>();
        for(int row=0; row<getRowCount(); row++)
            dataVector.add(getStringAt(row, colIndex));
        return dataVector;
    }
    
    /**
     * identical to DefaultTableModel method of same name
     * @see DefaultTableModel#getValueAt
     */
    public String getStringAt(int row, int col) {
        return ((String) (this.getValueAt(row, col).toString()));
    }
    
    /**
     * Change the value of a cell.  Adds empty rows as necessary up to given row number.
     * @see DefaultTableModel#setValueAt
     */
    @SuppressWarnings("unchecked")
    public void setStringAt(int row, int col, String value) {
        for(int r=this.getRowCount(); r<=row; r++)
            super.addRow((Vector)null);
        this.setValueAt(value, row, col);
    }

    /**
     * Return a view of a row as a List object. Changes to the list will be saved
     * into this table.
     * 
     * @param row index of row to wrap
     * @return wrapper for the requested row
     */
    public List<String> getRow(final int row) {
        return new AbstractList<String>() {
            @Override
            public String get(int index) {
                return getStringAt(row, index);
            }

            @Override
            public String set(int index, String value) {
                String old = getStringAt(row, index);
                setStringAt(row, index, value);
                return old;
            }
            
            @Override
            public int size() {
                return getColumnCount();
            }
        };
    }
    
    /**
     * Load the schema (first row) from a CSV file.
     * 
     * @param file file to scan
     * 
     * @return headers from first row of file
     * 
     * @throws IOException on any file error
     */
    public static List<String> loadSchema(File file) throws IOException {
        ICsvListReader reader = new CsvListReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            String[] head = reader.getCSVHeader(true);
            return Arrays.asList(head);
        }
        finally {
            reader.close();
        } 
    }

    /**
     * Load the schema (first row) from a CSV file. Result is just like
     * "new CSVTable(file)", except there are no data rows.
     * 
     * @param file
     *            file to scan
     * 
     * @return new CSVTable with named columns
     * 
     * @throws IOException
     *             on any file error
     */
    public static CSVTable loadSchemaTable(File file) throws IOException {
        CSVTable tab = new CSVTable();
        tab.sourceFile = file;
        for(String key : loadSchema(file))
            tab.addColumn(key);
        return tab;
    }

    protected static void loadCSV(DefaultTableModel dtm, File csvFile) throws IOException {
        // Load entire file, build header and contents.
        ICsvListReader reader = new CsvListReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            String[] head = reader.getCSVHeader(true);
            for (String key : head)
                dtm.addColumn(key);

            // Load all fields from all rows.
            List<String> line;
            while( (line = reader.read()) != null ) {
                dtm.addRow(line.toArray());
            }
        }
        finally {
            reader.close();
        } 
    }
    
    protected static void loadCSV(DefaultTableModel dtm, File csvFile,
            Set<String> keepCols) throws IOException {
        // Load entire file, build header and contents.
        ICsvListReader reader = new CsvListReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names,
            // but zero all names not in keep-list.
            // Note: we preserve number of columns, as things like
            // PumsPopulation expects a specific number in specific order.
            String[] head = reader.getCSVHeader(true);
            for(int c=0; c<head.length; c++) {
                if(!keepCols.contains(head[c]))
                    head[c] = null;
                dtm.addColumn(head[c]);
            }

            // Load rows, zeroing the cols we don't need.
            for(;;) {
                List<String> line = reader.read();
                
                if(line == null)
                    break;

                Object[] rowData = line.toArray();
                for(int c=0; c<rowData.length; c++)
                    if(head[c] == null)
                        rowData[c] = null;
                dtm.addRow(rowData);
            }
        }
        finally {
            reader.close();
        } 
    }

    /**
     * Create empty table that copies the schema (column names) from another
     * table.
     * 
     * @param schema
     *            table from which we'll copy column names
     * @return new empty CSVTable
     */
    public static CSVTable createEmpty(CSVTable schema) {
        CSVTable table = new CSVTable();
        for(int c=0; c < schema.getColumnCount(); c++)
            table.addColumn(schema.getColumnName(c));
        return table;
    }
}
