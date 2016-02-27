package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
 * @author Yizhao Gao, Charles R. Ehlschlaeger
 */
public class CSVTableNoSwing implements Serializable {
    // File we were loaded from.
    protected File              sourceFile = null;
    protected Vector    dataVector;
    protected Vector    columnIdentifiers;
    
    /**
     * Create new empty table.
     */
    public CSVTableNoSwing() {
    	this(0, 0);
    }

    /**
     * Load a table from a file.
     * 
     * @param csvFile
     * @throws IOException
     */
    public CSVTableNoSwing(String csvFile) throws IOException {
    	this(0, 0);
        this.sourceFile = findFile(csvFile);
        loadCSV(this, sourceFile);
    }

    public CSVTableNoSwing(String csvFile, Set<String> keepCols)
            throws IOException {
    	this(0, 0);
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
     * Returns a column given its name.
     * Implementation is naive so this should be overridden if
     * this method is to be called often. This method is not
     * in the <code>TableModel</code> interface and is not used by the
     * <code>JTable</code>.
     *
     * @param columnName string containing name of column to be located
     * @return the column with <code>columnName</code>, or -1 if not found
     */
    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) {
                return i;
            }
        }
        return -1;
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
            addRowV((Vector)null);
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
    public static CSVTableNoSwing loadSchemaTable(File file) throws IOException {
        CSVTableNoSwing tab = new CSVTableNoSwing();
        tab.sourceFile = file;
        for(String key : loadSchema(file))
            tab.addColumn(key);
        return tab;
    }

    protected static void loadCSV(CSVTableNoSwing dtm, File csvFile) throws IOException {
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
    
    protected static void loadCSV(CSVTableNoSwing dtm, File csvFile,
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
    public static CSVTableNoSwing createEmpty(CSVTableNoSwing schema) {
        CSVTableNoSwing table = new CSVTableNoSwing();
        for(int c=0; c < schema.getColumnCount(); c++)
            table.addColumn(schema.getColumnName(c));
        return table;
    }
    
    public void addColumn(Object columnName) {
    	columnIdentifiers.addElement(columnName);
    	justifyRows(0, getRowCount());
    }
    
    private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        dataVector.setSize(getRowCount());

        for (int i = from; i < to; i++) {
            if (dataVector.elementAt(i) == null) {
                dataVector.setElementAt(new Vector(), i);
            }
            ((Vector)dataVector.elementAt(i)).setSize(getColumnCount());
        }
    }
    
    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return dataVector.size();
    }
    
    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnIdentifiers.size();
    }
    
    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.  <code>aValue</code> is the new value.  This method
     * will generate a <code>tableChanged</code> notification.
     *
     * @param   aValue          the new value; this can be null
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public void setValueAt(Object aValue, int row, int column) {
    	Vector rowVector = (Vector)dataVector.elementAt(row);
        rowVector.setElementAt(aValue, column);
    }
    
    /**
     * Returns a vector that contains the same objects as the array.
     * @param anArray  the array to be converted
     * @return  the new vector; if <code>anArray</code> is <code>null</code>,
     *                          returns <code>null</code>
     */
    protected static Vector convertToVector(Object[] anArray) {
        if (anArray == null) {
            return null;
        }
        Vector<Object> v = new Vector<Object>(anArray.length);
        for (Object o : anArray) {
            v.addElement(o);
        }
        return v;
    }
    
    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(Object[] rowData) {
        addRowV(convertToVector(rowData));
    }
    
    /**
     * Returns the column name.
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> does not have an entry
     * for this index, returns the default
     * name provided by the superclass.
     */
    public String getColumnName(int column) {
        Object id = null;
        // This test is to cover the case when
        // getColumnCount has been subclassed by mistake ...
        if (column < columnIdentifiers.size() && (column >= 0)) {
            id = columnIdentifiers.elementAt(column);
        }
        
        String result = "";
        for (; column >= 0; column = column / 26 - 1) {
            result = (char)((char)(column%26)+'A') + result;
        }
        
        return (id == null) ? result : id.toString();
    }
     
    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public Object getValueAt(int row, int column) {
        Vector rowVector = (Vector)dataVector.elementAt(row);
        return rowVector.elementAt(column);
    }
    
    public void addRowV(Vector rowData) {
        insertRow(getRowCount(), rowData);
    }
    
    public void insertRow(int row, Vector rowData) {
        dataVector.insertElementAt(rowData, row);
        justifyRows(row, row+1);
    }
    

    private static Vector newVector(int size) {
        Vector v = new Vector(size);
        v.setSize(size);
        return v;
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with
     *  <code>rowCount</code> and <code>columnCount</code> of
     *  <code>null</code> object values.
     *
     * @param rowCount           the number of rows the table holds
     * @param columnCount        the number of columns the table holds
     *
     * @see #setValueAt
     */
    public CSVTableNoSwing(int rowCount, int columnCount) {
        this(newVector(columnCount), rowCount);
    }
    
    public CSVTableNoSwing(Vector columnNames, int rowCount) {
        setDataVector(newVector(rowCount), columnNames);
    }
    
    public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
        this.dataVector = nonNullVector(dataVector);
        this.columnIdentifiers = nonNullVector(columnIdentifiers);
        justifyRows(0, getRowCount());
    }
    
    private static Vector nonNullVector(Vector v) {
        return (v != null) ? v : new Vector();
    }
    
    public void writeToFile(String fileName) throws Exception
    {
    	FileWriter fWriter = new FileWriter(fileName);
    	BufferedWriter bWriter = new BufferedWriter(fWriter);
    	
    	String[] rowElements = (String [])this.columnIdentifiers.toArray(new String[this.columnIdentifiers.size()]);
    	StringBuilder rowBuilder = new StringBuilder();
    	
    	
 //   	System.out.println(rowBuilder.toString());
    	bWriter.write(this.getRowString(rowElements));
 
    	
    	for(int i = 0; i < this.dataVector.size(); i++)
    	{
    		List<String> row = this.getRow(i);
    		rowElements = row.toArray(rowElements);
        	bWriter.newLine();
        	bWriter.write(this.getRowString(rowElements));
    		
    	}
    	
    	
    	
    	bWriter.close();
    	fWriter.close();
    }
    
    private String getRowString(String[] rowElements)
    {
    	StringBuilder rowBuilder = new StringBuilder();
    	
    	for(int i = 0; i < rowElements.length; i++)
    	{
    		if(i == 0)
    		{
    			rowBuilder.append(rowElements[0]);
    		}
    		else {
				rowBuilder.append("," + rowElements[i]);
			}
    	}
    	
    	return rowBuilder.toString();
    }
}
