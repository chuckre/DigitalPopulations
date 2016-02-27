package mil.army.usace.ehlschlaeger.digitalpopulations.popgen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.ColumnGetter;
import mil.army.usace.ehlschlaeger.digitalpopulations.tabletools.RangeGetter;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;
import mil.army.usace.ehlschlaeger.rgik.util.NormalizedDoubleRangeIndex;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.ArrayIntList;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;



/**
 * This class takes a household table as output by Digital Populations and
 * constructs a population table. Age and gender are generated randomly.
 */
public class PopulationMaker {
    //
    // ** These commented-out fields are from Chuck's original sketch. They will
    // ** be removed as we figure out and implement the functionality they
    // ** represent.
    //
    // ** column name containing number of males
    // public String numberMalesColumn;
    //
    // ** minimum value in numberMalesColumn that is acceptable
    // public int minimumMvalue;
    //
    // ** maximum value in numberMalesColumn that is acceptable. other values will
    // ** will be considered `no data'. `no data' should be given a value based on
    // ** the distribution of the rest of the household CVS file.
    // public int maximumMvalue;
    //
    // ** column name containing number of females. if males and female columns are
    // ** null, assume proportion based on population pyramid.
    // public String numberFemalesColumn;
    //
    // ** minimum value in numberFemalesColumn that is acceptable
    // public int minimumFvalue;
    //
    // ** minimum value in numberFemalesColumn that is acceptable.. other values
    // ** will will be considered `no data'. `no data' should be given a value
    // ** based on the distribution of the rest of the household CVS file.
    // public int maximumFvalue;
    //
    // ** column names containing different age groups
    // public String[] ageColumns;
    //
    // ** minimum age of specified column
    // public int[] columnMinimumAge;
    //
    // ** maximum age of specified column
    // public int[] columnMaximumAge;
    //
    // ** true value indicates that column contains number of people in that age
    // ** group for that household. false value indicates that column is boolean
    // ** representation of whether people in that age group exists in the
    // ** household or not.
    // public boolean[] isEnumeration;
    //

    
    //
    // Maybe also:
    //   setGenderField(String label, String maleValue, String femaleValue);
    //
    
    // Households table and its first row.
    protected File                       householdsFile;
    protected List<String>               householdSchema;

    // Population distribution of area.
    // http://en.wikipedia.org/wiki/Population_pyramid
    protected NormalizedDoubleRangeIndex age_probs;
    protected ArrayList<PopSpec>         age_specs;

    // Column names for new columns.
    protected String                     genderTitle = "isMale";
    protected String                     ageTitle    = "age";

    protected ColumnGetter               npGetter;
    protected ArrayList<String>          copyColNam  = new ArrayList<String>();
    protected ArrayIntList               copyColIdx  = new ArrayIntList();

    // Our source of randomness.
    protected Random                     random      = new Random();

    /**
     * Create instance to process given file. File must be provided here, as
     * schema (column names) will be loaded and referenced during setup.
     * 
     * @param householdsFile
     *            CSV table containing households to populate
     * @throws IOException
     *             on any file error
     */
    public PopulationMaker(File householdsFile) throws IOException {
        this.householdsFile = householdsFile;
        this.householdSchema = CSVTable.loadSchema(householdsFile);
    }

    /**
     * @return name of column that will hold the is-male value
     */
    public String getGenderTitle() {
        return genderTitle;
    }

    /**
     * Change name of column that will hold the is-male value.
     * 
     * @param genderTitle
     */
    public void setGenderTitle(String genderTitle) {
        this.genderTitle = genderTitle;
    }

    /**
     * @return name of column that will hold the age value
     */
    public String getAgeTitle() {
        return ageTitle;
    }

    /**
     * Change name of column that will hold the age value.
     */
    public void setAgeTitle(String ageTitle) {
        this.ageTitle = ageTitle;
    }

    /**
     * Set list of columns that will be copied intact into output file.
     * 
     * @param names
     *            names of columns to copy
     */
    public void setCopyColumns(String... names) {
        copyColNam = new ArrayList<String>();
        copyColIdx = new ArrayIntList();

        for (int i = 0; i < names.length; i++) {
            copyColNam.add(names[i]);
            copyColIdx.add(householdSchema.indexOf(names[i]));
        }
    }

    /**
     * Return copy of setCopyColumns(). New container is created; it can be
     * modified without affecting this class.
     * 
     * @return list of columns that will be copied intact into output file
     */
    public List<String> getCopyColumns() {
        return new ArrayList<String>(copyColNam);
    }

    /**
     * Define column in household table that specifies the number of members in
     * each house. Households with values outside the given range will receive
     * no rows in the generated population table.
     * 
     * @param colName
     *            name of column to access
     * @param minimum
     *            smallest legal value
     * @param maximum
     *            largest legal value
     */
    public void setNumPeopleCol(String colName, double minimum, double maximum) {
        npGetter = new RangeGetter(householdSchema.indexOf(colName), minimum, maximum);
    }

    /**
     * Initialize the structure that provides relative probabilities of ages and
     * genders.
     * <P>
     * Table must have precisely four columns, named
     * <TT>minAge, maxAge, maleProportion, femaleProportion</TT>. Proportions
     * are relative and can have any units, but all values must use the same
     * scale.
     * 
     * @param table
     * @throws IOException
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Population_pyramid">Population
     *      Pyramid</a>
     */
    public void loadPyramid(CSVTable table) throws IOException {
        ArrayDoubleList probs = new ArrayDoubleList();
        age_specs = new ArrayList<PopSpec>();

        // Load header line with column names,
        int cMin = table.findColumn("minAge");
        int cMax = table.findColumn("maxAge");
        int cMal = table.findColumn("maleProportion");
        int cFem = table.findColumn("femaleProportion");

        // Load rows.
        for (int r = 0; r < table.getRowCount(); r++) {
            int min = Integer.parseInt(table.getStringAt(r, cMin));
            int max = Integer.parseInt(table.getStringAt(r, cMax));
            double mal = Double.parseDouble(table.getStringAt(r, cMal));
            double fem = Double.parseDouble(table.getStringAt(r, cFem));

            // Ages are given as integers. So when the CSV file says
            // 0 4 0.19
            // 5 9 0.18
            // it really means
            // [0,5) 0.19
            // [5,10) 0.18
            // which is exactly what PopSpec does.

            probs.add(mal);
            age_specs.add(new PopSpec(true, min, max));
            probs.add(fem);
            age_specs.add(new PopSpec(false, min, max));
        }

        age_probs = new NormalizedDoubleRangeIndex(probs.toArray());
    }

    /**
     * Generate the population table from current configuration.
     * 
     * @param file
     *            path and name of file to create. Will be overwritten if
     *            exists.
     * @throws SuperCSVException
     *             on any error generating table
     * @throws IOException
     *             on any file error
     */
    @SuppressWarnings("unchecked")
    public void generate(File file) throws SuperCSVException, IOException {
        ICsvListReader reader = new CsvListReader(new FileReader(householdsFile),
            CsvPreference.STANDARD_PREFERENCE);
        ICsvListWriter writer = new CsvListWriter(new FileWriter(file),
            CsvPreference.STANDARD_PREFERENCE);
        try {
            reader.getCSVHeader(true);

            // Build header from copy-cols plus our new cols.
            List<String> head = (List<String>) copyColNam.clone();
            head.add(genderTitle);
            head.add(ageTitle);
            writer.writeHeader(head.toArray(new String[head.size()]));

            for (;;) {
                List<String> line = reader.read();

                if (line == null)
                    break;

                // Copy the cols we're told to copy.
                List<String> newLine = new ArrayList<String>();
                for (int i = 0; i < copyColIdx.size(); i++)
                    newLine.add(line.get(copyColIdx.get(i)));

                int np = Integer.parseInt(npGetter.get(line));

                // Append placeholders for new fields.
                int c = newLine.size();
                newLine.add("");
                newLine.add("");

                // For all required members, insert fake data, and write record.
                for (int pop = 0; pop < np; pop++) {
                    PopSpec spec = age_specs.get(age_probs.get(random));
                    int age = spec.pick(random);
                    boolean ismale = spec.isMale;

                    newLine.set(c, ismale ? "1" : "0");
                    newLine.set(c + 1, Integer.toString(age));
                    writer.write(newLine);
                }
            }
        } finally {
            writer.close();
        }
    }

    public static void main(String[] args) throws IOException {
        File fAge = new File("data/afPopPyr.csv");
        File fHoh = new File("0629 run/rzn001-households.csv");
        File fOut = new File("fakepop.csv");

        PopulationMaker makr = new PopulationMaker(fHoh);
        makr.loadPyramid(new CSVTable(fAge.getAbsolutePath()));
        
        makr.setCopyColumns("x", "y", "uid");

        // makr.setNumPeopleCol("NP", 0, 999); //PUMS
        makr.setNumPeopleCol("NumPeopleHousehold_SC1", 0, 999); // Afghan

        makr.generate(fOut);
    }
}



/**
 * Descriptor for a node from a population pyramid.
 * 
 * @author William R. Zwicky
 */
class PopSpec {
    public boolean isMale;
    public int     min_age;
    public int     max_age;

    /**
     * Default constructor.
     */
    public PopSpec() {
    }

    /**
     * Full constructor.
     */
    public PopSpec(boolean isMale, int minAge, int maxAge) {
        super();
        this.isMale = isMale;
        min_age = minAge;
        max_age = maxAge;
    }

    /**
     * Select an age value at random from within our range.
     * 
     * @param random
     *            random-number generator
     * @return int age value
     */
    public int pick(Random random) {
        // If spec covers ages 5-9, then we need to select from [5,10).
        // Fortunately, nextDouble() selects from [0,1).
        return min_age + (int) (Math.floor(random.nextDouble() * (max_age + 1 - min_age)));
    }
}
