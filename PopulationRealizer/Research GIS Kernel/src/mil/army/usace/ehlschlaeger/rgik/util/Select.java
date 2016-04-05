package mil.army.usace.ehlschlaeger.rgik.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * Creates a new CSV file by extracting the named columns from another file.
 * Class is named after the SQL function that does the same thing.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Select {
    /**
     * Column name that means insert all cols here. "*" would make more sense,
     * but Microsoft Windows replaces * with a list of files in the current
     * directory.
     */
    public static final String ALL_COLS_MARKER = "##";
    
    protected List<String> columns;
    protected Random random = null;
    protected float skipFactor = 0;

    /**
     * Construct instance to copy named columns.
     * @param columns
     */
    public Select(List<String> columns) {
        this.columns = columns;
    }

    /**
     * Configure to output a random subset of the rows.
     * 
     * @param random
     * @param skipPercent
     */
    public void setRowSkip(Random random, float skipPercent) {
        this.random = random;
        this.skipFactor = (float) (skipPercent/100.0);
    }
    
    /**
     * Perform the configured operation with the given streams.
     * 
     * @param output
     * @param inputFile
     * 
     * @throws IOException
     */
    public void process(PrintStream output, File inputFile) throws IOException {
        ICsvListReader reader = new CsvListReader(new FileReader(inputFile), CsvPreference.STANDARD_PREFERENCE);
        ICsvListWriter writer = new CsvListWriter(new OutputStreamWriter(output), CsvPreference.STANDARD_PREFERENCE);
        
        try {
            // Load header line, find indices of columns to keep.
            List<String> head = Arrays.asList(reader.getCSVHeader(true));

            if(columns == null || columns.size() == 0) {
                // No col names were provided, so just dump the ones in the file.
                System.out.println("Available columns in "+inputFile.getName()+":");
                System.out.println(head);
            }
            
            else {
                ArrayIntList keeps = new ArrayIntList();
                for (String name : columns) {
                    if(ALL_COLS_MARKER.equals(name)) {
                        // Insert all cols here.
                        for(int i=0; i<head.size(); i++)
                            keeps.add(i);
                    }
                    else {
                        // Insert named col here.
                        int p = head.indexOf(name);
                        if (p < 0)
                            throw new IllegalArgumentException(String.format(
                                "Column \"%s\" not found in table.", name));
                        else
                            keeps.add(p);
                    }
                }                
                
                // Write header for new table.
                String[] row = new String[keeps.size()];
                for (int i = 0; i < keeps.size(); i++) {
                    row[i] = head.get(keeps.get(i));
                }
                writer.writeHeader(row);                
                
                // Process all rows.
                for(;;) {
                    List<String> line = reader.read();
                    if(line == null)
                        break;
                    
                    if(random != null)
                        if(random.nextFloat() < skipFactor)
                            continue;
                        
                    for (int i = 0; i < keeps.size(); i++) {
                        int p = keeps.get(i);
                        if(p >= line.size())
                            row[i] = "";
                        else
                            row[i] = line.get(p);
                    }
                    writer.write(row);
                }
            }
        }
        finally {
            writer.close();
            reader.close();
        } 
    }

    /**
     * Command-line interface to tool.  Results are written to standard output.
     * 
     * @param args path and name of input file, followed by list of column names
     * @throws IOException on any file error
     */
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        parser.accepts("r", "random seed (default=time-dependent random)").withRequiredArg().ofType(Long.class);
        parser.accepts("s", "skip factor (drop this percentage of rows; default=0%)").withRequiredArg().ofType(Float.class);
        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");
        
        OptionSet opts = parser.parse(args);
        if(opts.has("h") || opts.has("help") || args.length == 0) {
            System.out.println("Purpose: Create a new CSV file by extracting the named columns from another file.");
            System.out.println("Usage:\n  "+Select.class.getSimpleName()+" [options] <input file> <column 1> ...\n");
            System.out.println("    <input file> is path an name of comma-seperated file to read");
            System.out.println("    <column 1> ... is a list of names of columns to copy; "+ALL_COLS_MARKER+" will copy all columns");
            System.out.println("    The modified result will be written to stdout.\n");
            parser.printHelpOn(System.out);
            return;
        }

        int reqdArgs = 1;
        if(opts.nonOptionArguments().size() < reqdArgs) {
            System.err.println("Required args are missing; there should be at least "+reqdArgs+".");
            System.exit(5);
        }
            
        List<String> cols = new ArrayList<String>(opts.nonOptionArguments());
        String inName = cols.remove(0);
        File inFile = new File(inName).getAbsoluteFile();
        if(!inFile.exists())
            throw new IOException(inFile+" does not exist");
        
        Select select = new Select(cols);
        
        Random random = new Random();
        if(opts.has("r")) {
            long seed = ((Long)opts.valueOf("r")).longValue();
            random = new Random(seed);
        }
        
        if(opts.has("s")) {
            float skip = ((Float)opts.valueOf("s")).floatValue();
            select.setRowSkip(random, skip);
        }
        
        PrintStream out = System.out;
        select.process(out, inFile);
    }
}
