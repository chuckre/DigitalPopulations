package mil.army.usace.ehlschlaeger.digitalpopulations.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;



/**
 * Load a CSV file into a set of household and population objects. We only
 * support integer attributes, so any attributes expressed as doubles will be
 * probabilistically rounded using the given random-number generator.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PumsLoader {
    private Random random;
    private CSVTableNoSwing householdSchema;
    private CSVTableNoSwing populationSchema;

    public PumsLoader() {
        random = new Random();
    }
    
    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        this.random = source;
    }
    
    public CSVTableNoSwing getHouseholdSchema() {
        return householdSchema;
    }
    
    public CSVTableNoSwing getPopulationSchema() {
        return populationSchema;
    }

    /**
     * Load and parse PUMS household table directly into PumsHousehold objects.
     * We don't want to load the full table into a CSVTable as it's too big.
     * Since attributes must be integers within Digital Populations, any floats
     * found in the file will be rounded probabilistically.
     * 
     * @param hohFile
     *            path and name of file to load
     * @param keyCol
     *            (optional) name of column holding a unique ID for each row
     * 
     * @return list of {@link PumsHousehold} objects, one for each row in the
     *         file
     * @throws IOException
     *             on any file error
     */
    public List<PumsHousehold> loadPumsHouseholds(File hohFile, String keyCol) throws IOException {
        DataException.verifyNotBlank(hohFile, "Households table is required for DP to run.");

        householdSchema = CSVTableNoSwing.loadSchemaTable(hohFile);
        
        ICsvListReader reader = new CsvListReader(new FileReader(hohFile), 
                                                  CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            reader.getCSVHeader(true);

            //  hoh file need not have uid column.
            boolean has_key = ! ObjectUtil.isBlank(keyCol);
            long auto_key = 1;
            int key_idx = -1;
            if(has_key)
                key_idx = householdSchema.findColumn(keyCol);
            
            // Load all fields from all rows.
            ArrayList<PumsHousehold> houses = new ArrayList<PumsHousehold>();
            for(;;) {
                List<String> line = reader.read();
                if(line == null)
                    break;

                // Parse cells as integers.
                //  * Blank cells become zero.
                //  * Non-numbers or values outside the range of int are replaced
                //    with NODATA.
                int[] attribs = new int[line.size()];
                for (int v = 0; v < line.size(); v++) {
                    attribs[v] = parseInt(line.get(v));
                }

                // Build unique ID.
                String key;
                if(has_key) {
                    key = line.get(key_idx);
                }
                else {
                    key = Long.toString(auto_key);
                    auto_key += 1;
                }
                
                // Build hoh.
                PumsHousehold pumsHouse = new PumsHousehold(householdSchema, attribs, key);
                houses.add(pumsHouse);
            }
            
            return houses;
        }
        finally {
            reader.close();
        }
    }

    /**
     * Load and parse a table of household realizations. Similar to
     * loadPumsHouseholds(), except for the additional easting and northing
     * columns. Since attributes must be integers within Digital Populations,
     * any floats found in the file will be rounded probabilistically.
     * <P>
     * Creates a {@link PumsHouseholdRealization} for each record to hold the
     * location, and a {@link PumsHousehold} to hold the attributes.
     * 
     * @param hohFile
     *            path and name of file to load
     * @param rznNum
     *            index of system realization being loaded. Used if multiple
     *            solutions are loaded into memory simultaneously. If only one
     *            is loaded, the value zero is most reasonable.
     * @param xCol
     *            name of column holding easting values
     * @param yCol
     *            name of column holding northing values
     * @param keyCol
     *            (optional) name of column holding a unique ID for each row
     * 
     * @return list of {@link PumsHouseholdRealization} objects, one for each
     *         row in the file
     * @throws IOException
     *             on any file error
     */
    public ArrayList<PumsHouseholdRealization> loadPumsHouseholdRzns(File hohFile,
        int rznNum,
        String xCol, String yCol, String keyCol) throws IOException {
        DataException.verifyNotBlank(hohFile, "Households table is required for DP to run.");

        householdSchema = CSVTableNoSwing.loadSchemaTable(hohFile);
        
        ICsvListReader reader = new CsvListReader(new FileReader(hohFile), 
                                                  CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            reader.getCSVHeader(true);

            // Verify x/y cols.
            int xIdx = householdSchema.findColumn(xCol);
            int yIdx = householdSchema.findColumn(yCol);
            
            //  hoh file need not have uid column.
            boolean has_key = ! ObjectUtil.isBlank(keyCol);
            long auto_key = 1;
            int key_idx = -1;
            if(has_key)
                key_idx = householdSchema.findColumn(keyCol);
            
            // Load all fields from all rows.
            ArrayList<PumsHouseholdRealization> houses = new ArrayList<PumsHouseholdRealization>();
            for(;;) {
                List<String> line = reader.read();
                if(line == null)
                    break;

                // Parse cells as integers.
                //  * Blank cells become zero.
                //  * Non-numbers or values outside the range of int are replaced
                //    with NODATA.
                int[] attribs = new int[line.size()];
                for (int v = 0; v < line.size(); v++) {
                    attribs[v] = parseInt(line.get(v));
                }

                // Build unique ID.
                String key;
                if(has_key) {
                    key = line.get(key_idx);
                }
                else {
                    key = Long.toString(auto_key);
                    auto_key += 1;
                }
                
                // Parse location.
                double easting = Double.parseDouble(line.get(xIdx));
                double northing = Double.parseDouble(line.get(yIdx));
                
                // Build hoh.
                PumsHousehold hohArch = new PumsHousehold(householdSchema, attribs, key);
                PumsHouseholdRealization hohRzn = new PumsHouseholdRealization(hohArch, rznNum, easting, northing);
                houses.add(hohRzn);
            }
            
            return houses;
        }
        finally {
            reader.close();
        } 
    }
    
    /**
     * Load and parse PUMS population table directly into PumsPopulation objects.
     * We don't want to load the full table into a CSVTable as it's too big.
     * 
     * @return list of PumsPopulation objects, one for each row in the file
     * @throws IOException on any file error
     */
    public List<PumsPopulation> loadPumsPopulation(File popFile, String hohCol) throws IOException {
        ArrayList<PumsPopulation> peops = new ArrayList<PumsPopulation>();
        
        populationSchema = CSVTableNoSwing.loadSchemaTable(popFile);

        ICsvListReader reader = new CsvListReader(new FileReader(popFile), 
                                                  CsvPreference.STANDARD_PREFERENCE);
        try {
            // Load header line with column names.
            reader.getCSVHeader(true);

            int hohidx = populationSchema.findColumn(hohCol);
            
            // Load all fields from all rows.
            for(;;) {
                List<String> line = reader.read();
                if(line == null)
                    break;

                String hid = line.get(hohidx);
                
                PumsPopulation pumsPerson = newPumsPopulation(hid, populationSchema, line);
                peops.add(pumsPerson);
            }
            
            return peops;
        }
        finally {
            reader.close();
        } 
    }

    /**
     * Sort people from PUMS data into their households.
     * 
     * @param houses
     *            objects to receive people
     * @param persons
     *            people to move into houses
     * @param hohMemberCol
     *            name of column in households that identifies the number of
     *            members inside. Optional; column will be updated by this
     *            method if defined.
     */
    public void populateHouseholds(List<PumsHousehold> houses, List<PumsPopulation> persons, String hohMemberCol) {
        if(ObjectUtil.isBlank(hohMemberCol))
            hohMemberCol = null;
        
        // Create empty list for each household.
        HashMap<String, ArrayList<PumsPopulation>> houseMap = new HashMap<String, ArrayList<PumsPopulation>>();
        for (PumsHousehold house : houses) {
            houseMap.put(house.getID(), new ArrayList<PumsPopulation>());
        }
        
        // Move each person into the appropriate list.
        for (PumsPopulation person : persons) {
            ArrayList<PumsPopulation> popList = houseMap.get(person.getHohID());
            
            if(popList == null)
                throw new DataException("Can't find household with ID: "+person);
            popList.add(person);
        }

        // Trim lists to exact size, install into houses.
        for (PumsHousehold house : houses) {
            ArrayList<PumsPopulation> peops = houseMap.get(house.getID());
            if(peops.size() == 0) {
                house.setMembersOfHousehold(null);
            }
            else {
                PumsPopulation[] ary = new PumsPopulation[peops.size()];
                ary = peops.toArray(ary);
                house.setMembersOfHousehold(ary);
            }
            // Reset members attribute to correct value.
            if(hohMemberCol != null)
                house.setAttributeValue(hohMemberCol, peops.size());
        }
    }

    /**
     * Sort people from PUMS data into their households.
     * 
     * @param houses
     *            objects to receive people
     * @param persons
     *            people to move into houses
     * @param hohMemberCol
     *            name of column in households that identifies the number of
     *            members inside. Optional; column will be updated by this
     *            method if defined.
     */
    public void populateHouseholdRzns(List<PumsHouseholdRealization> houses, List<PumsPopulation> persons, String hohMemberCol) {
        if(ObjectUtil.isBlank(hohMemberCol))
            hohMemberCol = null;
        
        // Create empty list for each household.
        HashMap<String, ArrayList<PumsPopulation>> houseMap = new HashMap<String, ArrayList<PumsPopulation>>();
        for (PumsHouseholdRealization house : houses) {
            houseMap.put(house.getParentHousehold().getID(), new ArrayList<PumsPopulation>());
        }
        
        // Move each person into the appropriate list.
        for (PumsPopulation person : persons) {
            ArrayList<PumsPopulation> popList = houseMap.get(person.getHohID());
            
            if(popList == null)
                throw new DataException("Can't find household with ID: "+person);
            popList.add(person);
        }

        // Trim lists to exact size, install into houses.
        for (PumsHouseholdRealization rzn : houses) {
            PumsHousehold house = rzn.getParentHousehold();
            ArrayList<PumsPopulation> peops = houseMap.get(house.getID());
            if(peops.size() == 0) {
                house.setMembersOfHousehold(null);
            }
            else {
                PumsPopulation[] ary = new PumsPopulation[peops.size()];
                ary = peops.toArray(ary);
                house.setMembersOfHousehold(ary);
            }
            // Reset members attribute to correct value.
            if(hohMemberCol != null)
                house.setAttributeValue(hohMemberCol, peops.size());
        }
    }
    
    /**
     * Convert string to int: if blank or invalid, becomes NODATA. If float, is
     * rounded probabilistically (i.e. if string is "8.2", there's an 80%
     * chance that 8 will be returned, and a 20% chance of 9. Likewise, "-8.2"
     * can yield -8 or -9.)
     * 
     * @param sval
     *            string form of value
     * @return integer form of value
     */
    public int parseInt(String sval) {
        // DEV NOTE:
        // Chuck thinks switching all attribs to float would cause trouble, so
        // instead of that, we'll stick with int, but use this weird
        // random-rounding trick.
        int ival = PumsHousehold.NODATA_VALUE;
        
        if(! ObjectUtil.isBlank(sval)) {
            try {
                double dval = Double.parseDouble(sval);
                int ipart = (int) dval;
                double fpart = dval-ipart;

                if(fpart == 0)
                    ival = (int) dval;
                else
                    ival = ipart + (random.nextDouble() >= Math.abs(fpart) ? 0 : (int)Math.signum(fpart));
            } catch (NumberFormatException e) {
                // ignore; not a number at all
            }
        }
        return ival;
    }

    /**
     * Helper to make a PumsPopulation by running string attributes through parseInt().
     * 
     * @param hohID
     * @param schema
     * @param attributeValues
     * @return
     */
    protected PumsPopulation newPumsPopulation(String hohID, CSVTableNoSwing schema, List<String> attributeValues) {
        int[] values = new int[attributeValues.size()];
        
        for (int v = 0; v < attributeValues.size(); v++) {
            values[v] = parseInt(attributeValues.get(v));
        }
        
        return new PumsPopulation(hohID, schema, values);
    }
}
