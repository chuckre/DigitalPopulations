package mil.army.usace.ehlschlaeger.digitalpopulations.csvplusmap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

import org.apache.commons.io.FilenameUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;



/**
 * This utility adds a column to a CSV table, where values are read from a map
 * based on coordinates encoded in each row of the table.
 * <P>
 * Includes main() method; run to see usage instructions.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class CsvPlusMap {
    /** Name of column that holds x/easting/latitude. */
    private String xcol   = "x";
    /** Name of column that holds y/northing/longitude. */
    private String ycol   = "y";
    /** Name of column to append to table. */
    private String newcol = null;
    /** Input table to process. */
    private File   csvFile;
    /** Input map that will supply new values. */
    private File   mapFile;
    /** Output table to create with copy of csvFile plus new column. */
    private File   outFile;
    /** Formatter for values in new column. */
    private NumberFormat numfmt;

    /**
     * Construct default instance.  Call set*() methods to configure, then call go().
     */
    public CsvPlusMap() {
        numfmt = NumberFormat.getNumberInstance();
        numfmt.setMinimumFractionDigits(0);
        numfmt.setMaximumFractionDigits(80);
        numfmt.setMinimumIntegerDigits(1);
        numfmt.setMaximumIntegerDigits(80);
    }

    public String getXcol() {
        return xcol;
    }

    /**
     * @param xcol
     *            name of X/longitude/easting column in CSV file. Case
     *            sensitive.
     */
    public void setXcol(String xcol) {
        this.xcol = xcol;
    }

    public String getYcol() {
        return ycol;
    }

    /**
     * @param ycol
     *            name of Y/latitude/northing column in CSV file. Case
     *            sensitive.
     */
    public void setYcol(String ycol) {
        this.ycol = ycol;
    }

    public String getNewcol() {
        return newcol;
    }

    /**
     * @param newcol
     *            name of column to append to new CSV file. Defaults to base
     *            name of map file when setMapFile() is called.
     */
    public void setNewcol(String newcol) {
        this.newcol = newcol;
    }

    public File getCsvFile() {
        return csvFile;
    }

    /**
     * @param csvFile
     *            path and name of CSV file to read. File doesn't consume
     *            memory; rows will be read and written on the fly.
     */
    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;

        File dir = csvFile.getParentFile();
        String name = FilenameUtils.getBaseName(csvFile.getName());
        String ext = FilenameUtils.getExtension(csvFile.getName());
        this.outFile = new File(dir, name + ".plusmap." + ext);
    }

    public File getMapFile() {
        return mapFile;
    }

    /**
     * @param mapFile
     *            path and name of map file from which values will be read. Must
     *            be ESRI ASCII. Will be loaded entirely into memory before
     *            process starts.
     */
    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;

        String col = FilenameUtils.getBaseName(mapFile.getName());
        newcol = col;
    }

    public File getOutFile() {
        return outFile;
    }

    /**
     * @param outFile
     *            path and name of file to create. Will be overwritten if
     *            exists. Defaults to csvFile+".plusmap.csv" when setCsvFile()
     *            is called.
     */
    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    /**
     * Run the process as currently configured.
     * 
     * @throws IOException on any file error
     */
    public void go() throws IOException {
        if (csvFile == null)
            throw new DataException("csv-file was not specified");
        if (mapFile == null)
            throw new DataException("map-file was not specified");

        ICsvListReader reader = new CsvListReader(new FileReader(csvFile),
            CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            List<String> head = new ArrayList<String>(Arrays.asList(reader.getCSVHeader(true)));
            int xidx = head.indexOf(xcol);
            if (xidx < 0)
                throw new DataException("Column '" + xcol + "' not found in table header");
            int yidx = head.indexOf(ycol);
            if (yidx < 0)
                throw new DataException("Column '" + ycol + "' not found in table header");

            // Load map down here so error checks above are done first.
            System.out.println("Loading map " + mapFile.getName());
            GISLattice map = GISLattice.loadEsriAscii(mapFile);
            
            System.out.println("Writing file " + outFile.getName());
            ICsvListWriter writer = new CsvListWriter(new FileWriter(outFile),
                CsvPreference.STANDARD_PREFERENCE);
            try {
                head.add(newcol);
                writer.writeHeader(head.toArray(new String[head.size()]));

                // Process all rows.
                List<String> line;
                while ((line = reader.read()) != null) {
                    double x = Double.parseDouble(line.get(xidx));
                    double y = Double.parseDouble(line.get(yidx));
                    if(map.isNoData(x, y)) {
                        // Outside map or NODATA yields blank cell.
                        line.add("");
                    }
                    else {
                        double val = map.getCellValue(x, y);
                        line.add(numfmt.format(val));
                        writer.write(line);
                    }
                }

                System.out.println("Done.");
            } finally {
                writer.close();
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Command-line interface to program.  Run for usage instructions.
     * 
     * @param args command-line arguments
     * @throws IOException on any file error
     */
    public static void main(String[] args) throws IOException {
        // CsvPlusMap -x x -y y -n new-col -o out-file <csv> <map>
        OptionParser parser = new OptionParser();
        parser.accepts("x", "name of X/longitude/easting column (default is \"x\")").withRequiredArg().ofType(
            String.class);
        parser.accepts("y", "name of Y/latitude/northing column (default is \"y\")").withRequiredArg().ofType(
            String.class);
        parser.accepts("n", "name of new column (default is base name of map file)").withRequiredArg().ofType(
            String.class);
        parser.accepts("o", "output file (default is <csv-file>.plusmap.csv)").withRequiredArg().ofType(
            File.class);
        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");

        CsvPlusMap cpm = new CsvPlusMap();

        OptionSet opts = parser.parse(args);
        if(opts.has("h") || opts.has("help") || args.length == 0) {
            System.out.println("Usage: " + cpm.getClass().getSimpleName()
                    + " [options] <csv-file> <map-file>");
            System.out.println("  <csv-file> is csv to extend");
            System.out.println("  <map-file> is ESRI ASCII map file that provides values");
            System.out.println();
            parser.printHelpOn(System.out);
            System.exit(5);
        }

        int reqdArgs = 2;
        if(opts.nonOptionArguments().size() != reqdArgs) {
            System.err.println("Required args are missing; there should be "+reqdArgs+".");
            System.exit(5);
        }
        
        cpm.setCsvFile(new File(opts.nonOptionArguments().get(0)));
        cpm.setMapFile(new File(opts.nonOptionArguments().get(1)));
        if (opts.has("x"))
            cpm.setXcol((String) opts.valueOf("x"));
        if (opts.has("y"))
            cpm.setYcol((String) opts.valueOf("y"));
        if (opts.has("n"))
            cpm.setNewcol((String) opts.valueOf("n"));
        if (opts.has("o"))
            cpm.setOutFile((File) opts.valueOf("o"));

        cpm.go();
    }
}
