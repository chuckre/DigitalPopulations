package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.VelocityGetter;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;



/**
 * Wrapper for the sorts of things required by Dp2Kml. Methods are reletively
 * generic, so it shouldn't be too hard to swap in a different database engine.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class H2Wrapper {
    public static final String DRIVER = "org.h2.Driver";

    protected Logger           log    = Logger.getLogger(H2Wrapper.class.getPackage().getName());
    /** Connection to database. */
    private Connection         conn;
    /** Our default statement for simple queries. */
    private Statement          stmt;
    /** Timestamp on database file before we opened it. */
    private long               lastModified;
    
    public H2Wrapper(File dbname) throws SQLException {
        this.lastModified = lastModified(dbname);
        try {
            Class.forName(DRIVER).newInstance();
            // File will be created if missing.
            this.conn = DriverManager.getConnection("jdbc:h2:"+dbname.getAbsolutePath());
            this.stmt = conn.createStatement();
            
            // Allow database to use 75% of available memory.
            //   -maxMemory returns bytes
            //   -CACHE_SIZE accepts KiB
//            int cache = (int) Math.floor(Runtime.getRuntime().maxMemory() * 0.75f / 1024);
            
            // 16MB is default; more is SLOWER!
            int cache = 128*1024;
            executeUpdate("SET CACHE_SIZE "+cache);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        } catch (InstantiationException e) {
            throw new SQLException(e);
        }
    }

    /**
     * @return timestamp on database file before we opened it. If we had to
     *         create a new file, returns -1.
     */
    public long lastModified() {
        return lastModified;
    }
    
    public Connection getConnection() {
        return conn;
    }

    /**
     * Flush all modified data to disk.
     * @throws SQLException 
     */
    public void sync() throws SQLException {
        executeUpdate("CHECKPOINT SYNC");
    }
    
    public void shutdown() throws SQLException {
        // H2 has no SHUTDOWN command; it shuts down automatically.
        conn.close();
        conn = null;
    }

    public int executeUpdate(String sql) throws SQLException {
        return stmt.executeUpdate(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    /**
     * Delete named table if it exists.
     * 
     * @param tblName name of table to delete
     * 
     * @throws SQLException on any error deleting table
     */
    public void dropTable(String tblName) throws SQLException {
        executeUpdate("DROP TABLE IF EXISTS "+tblName);
    }

    /**
     * Fetch all indicated records from database.
     * All columns must be strings.
     * 
     * @param stmt PreparedStatement with a parameter for each key
     * @param keys values to insert into parameters
     * 
     * @return all records, as a List<List<String>>
     * 
     * @throws SQLException
     */
    public List<List<String>> getRecords(PreparedStatement stmt, String... keys) throws SQLException {
        for (int i = 0; i < keys.length; i++)
            stmt.setString(i+1, keys[i]);
        ResultSet rs = stmt.executeQuery();
        
        List<List<String>> rows = new ArrayList<List<String>>();
        while(rs.next()) {
            ArrayList<String> row = new ArrayList<String>();
            for(int c=1; c <= rs.getMetaData().getColumnCount(); c++)
                row.add(rs.getString(c));
            rows.add(row);
        }
        return rows;
    }

    /**
     * Load a CSV file into a table. It table already exists, it will be deleted
     * first. All columns will be plain strings.
     * 
     * @param csvFile file to load
     * @param tblName name of table to create
     * @param popTable false if households; true if members (population)
     * 
     * @throws SQLException on any error building table
     * @throws IOException on any error loading file
     */
    public void loadTable(de.schlichtherle.io.File csvFile, String tblName, boolean popTable) throws SQLException, IOException {
        ICsvListReader reader = new CsvListReader(new de.schlichtherle.io.FileReader(csvFile),
            CsvPreference.STANDARD_PREFERENCE);
        
        try {
            LogUtil.progress(log, "Loading %s into database", csvFile.getName());
            ProgressToy prog = new ProgressToy(log, 5, 0);

            // Process header line.
            String[] schema = reader.getCSVHeader(true);
            
            StringBuffer buf = new StringBuffer();
            buf.append("CREATE TABLE ").append(tblName).append(" (");
            
            // Create table.
            //  - Must do it this way; parameters don't work.
            for (int i = 0; i < schema.length; i++) {
                if(i > 0)
                    buf.append(",");
                buf.append(VelocityGetter.makeSafeID(schema[i])).append(" VARCHAR(1000)");
            }
            buf.append(")");

            // Delete old table, create new.
            dropTable(tblName);
            executeUpdate(buf.toString());

            // Create index for DP keys.
            String sql;
            if(popTable)
                sql = String.format(
                    "CREATE INDEX %1$s_index ON %1$s (household)",
                    tblName);
            else
                sql = String.format(
                    "CREATE INDEX %1$s_index ON %1$s (uid)",
                    tblName);
            executeUpdate(sql);

            // Build append query with params.
            buf = new StringBuffer();
            buf.append("insert into ").append(tblName).append(" values(");
            for (int i = 0; i < schema.length; i++) {
                if (i > 0)
                    buf.append(",");
                buf.append("?");
            }
            buf.append(")");
            
            PreparedStatement prep = prepareStatement(buf.toString());
            
            // Append all rows.
            List<String> line;
            while ((line = reader.read()) != null) {
                for (int i = 0; i < line.size(); i++)
                    prep.setString(i+1, line.get(i));
                prep.execute();            
                
                prog.printProgress(reader.getLineNumber());
            }
            
            sync();
        }
        catch(SQLException e) {
            throw e;
        }
        catch(IOException e) {
            throw e;
        }
        catch(Exception e) {
            String msg = String.format("Error reading line %d from file %s:", reader.getLineNumber(), csvFile);
            throw new IOException(msg, e);
        }
        finally {
            reader.close();
        }
    }

    /**
     * @return the time stamp on the database file, or -1 if file doesn't exist
     */
    public static long lastModified(File dbname) {
        dbname = dbname.getAbsoluteFile();
        File probe = new File(dbname.getParentFile(), dbname.getName()+".h2.db");
        if(probe.exists())
            return probe.lastModified();
        else
            return -1;
    }
}
