package mil.army.usace.ehlschlaeger.digitalpopulations.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.ConflatePumsQueryWithTracts;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

/**
 * Write a collection of {@link PumsHouseholdRealization} to CSV files, one for
 * households and one for population. Generally used to output the results of
 * the phases.
 * 
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class HohRznWriter {
    protected static Logger log = Logger.getLogger(ConflatePumsQueryWithTracts.class.getPackage().getName());
    private File outputDir;
    

    public HohRznWriter(File outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Write all required output files.
     * 
     * @param realizationNum
     *            number of realization currently being generated. Copied into
     *            output file names.
     * @param nameNote
     *            which version of file, as a note to the user. Will be embedded
     *            in file names. Suggestions: "preliminary", "intermediate",
     *            "phase3", etc. Use null if no note is desired (i.e. is final
     *            version of file.)
     * @param houses
     *            list of households and attached members (location and
     *            metadata) to write
     * @param flushOlder
     *            'true' to delete older versions of these files, or 'false' to
     *            leave them. This should normally be 'true'; only use 'false'
     *            for testing and debugging.
     * @param writePop
     *            true to write population file; false to skip
     * @param writeAllHohFields
     *            true to write all household attributes; false to only write
     *            location and uid
     * @param writeAllPopFields
     *            true to write all population attributes; false to only write
     *            location and hoh id
     * 
     * @throws IOException
     *             on any error creating files. If there's a problem renaming
     *             files, it will only appear in the log.
     */
    public void writeFileSet(
            int realizationNum, String nameNote,
            Iterator<PumsHouseholdRealization> houses,
            boolean flushOlder,
            boolean writePop,
            boolean writeAllHohFields, boolean writeAllPopFields,
            String hohKeyField, String popHohField)
            throws IOException {
        if(nameNote == null)
            nameNote = "";
        else if(nameNote.length() > 0)
            nameNote = String.format("(%s)", nameNote);
        
        // Build file names.
        String prefix = String.format("rzn%03d", realizationNum);
        File hohFile = new File(outputDir,
            String.format("%s-households%s.csv", prefix, nameNote));
        File popFile = null;

        // Save results to temp files to protect existing files in case of crash,
        // and protect the new files from the purge below.
        File hohTmp = new File(outputDir,
            String.format("%s-households%s.tmp", prefix, nameNote));
        File popTmp = null;

        // Create pop file only if a pop file was loaded.
        if(writePop) {
            popFile = new File(outputDir,
                String.format("%s-population%s.csv", prefix, nameNote));
            popTmp = new File(outputDir,
                String.format("%s-population%s.tmp", prefix, nameNote));
        }

        // Write houses and peoples.
        writeRealizations(hohTmp, popTmp, houses,
            writeAllHohFields, writeAllPopFields,
            hohKeyField, popHohField);

        // Purge all old versions of files, where possible.
        if(flushOlder) {
            File[] files = outputDir.listFiles();
            for(File file:files) {
                if(file.isFile()) {
                    String n = file.getName().toLowerCase();
                    if(n.startsWith(prefix) && n.endsWith(".csv"))
                        file.delete();
                }
            }
        }
        
        // Rename temp files.
        FileUtil.replaceFile(log, hohTmp, hohFile);
        if(popTmp != null)
            FileUtil.replaceFile(log, popTmp, popFile);
    }

    /**
     * Write a table of realizations to a file. Household and population files
     * are written simultaneously to ensure easting/northing values are in sync.
     * 
     * @param hohFile
     *            households file to create, or null to skip. If exists, will be
     *            overwritten.
     * @param popFile
     *            population file to create, or null to skip. If exists, will be
     *            overwritten.
     * @param houses
     *            data to write to files
     * @param allHohFields
     *            true to write all household data fields to output file, false
     *            to only write easting/northing/household id/realization id.
     * @param allPopFields
     *            true to write all population data fields to output file, false
     *            to only write easting/northing/household id/realization id.
     * 
     * @throws IOException
     *             on any file error
     */
    public void writeRealizations(File hohFile, File popFile,
            Iterator<PumsHouseholdRealization> houses,
            boolean allHohFields, boolean allPopFields,
            String hohKeyField, String popHohField) throws IOException {
        PrintWriter hout = null, pout = null;
        int hohKeyIdx = -1;
        int popHohIdx = -1;
        
        CSVTableNoSwing householdSchema = null;
        CSVTableNoSwing populationSchema = null;
        
        if(hohFile != null) {
            hout = new PrintWriter(
                new BufferedWriter( new FileWriter(hohFile)));
        }
        if(popFile != null) {
            pout = new PrintWriter(
                new BufferedWriter( new FileWriter(popFile)));
        }
        
        while(houses.hasNext()) {
            PumsHouseholdRealization house = houses.next();
            PumsHousehold arch = house.getParentHousehold();
            
            String serID = arch.getID();
            int rznID = house.getRealizationNumber();

            // Output household if requested
            if(hout != null) {
                // Can't write header til we find first valid household.
                if(householdSchema == null) {
                    householdSchema = arch.getSchema();
                    
                    hout.print( "x,y,uid");
                    if(allHohFields) {
                        for (int i = 0; i < householdSchema.getColumnCount(); i++) {
                            hout.format(",%s", householdSchema.getColumnName(i));
                        }
                    }
                    hout.println();
                    
                    // Reverse of what DataPreparer.loadHouseholds did:
                    // Attribs are all numeric except for uid column which can be string.
                    // This lets us slips the archtype ID back into the appropriate column.
                    if(! ObjectUtil.isBlank(hohKeyField))
                        hohKeyIdx = householdSchema.findColumn(hohKeyField);
                }

                // Formatting double with %s prints sufficient precision.
                hout.format("%s,%s,%s:%s", house.getEasting(), house.getNorthing(), serID, rznID);
    
                if(allHohFields) {
                    for(int v = 0; v < arch.getNumberAttributes(); v++) {
                        hout.print(",");
                        if(v == hohKeyIdx)
                            // Sneak archtype ID string into appropriate col.
                            hout.print(arch.getID());
                        else
                            hout.print(arch.getAttributeValue(v));
                    }
                }
                hout.println();
            }
            
            // Output people if requested
            if(pout != null) {
                PumsPopulation[] peops = arch.getMembersOfHousehold();

                if(peops != null && peops.length > 0) {
                    // Can't write header til we find first valid peop.
                    if(populationSchema == null) {
                        populationSchema = peops[0].getSchema();
                        
                        pout.print( "x,y,household");
                        if(allPopFields) {
                            for (int i = 0; i < populationSchema.getColumnCount(); i++) {
                                pout.format(",%s", populationSchema.getColumnName(i));
                            }
                        }
                        pout.println();
                        
                        // Same trick for pop->hoh link column.
                        if(! ObjectUtil.isBlank(popHohField))
                            popHohIdx = populationSchema.findColumn(popHohField);
                    }
                    
                    for (PumsPopulation person : peops) {
                        // Formatting double with %s prints sufficient precision.
                        pout.format("%s,%s,%s:%s", house.getEasting(), house.getNorthing(), serID, rznID);
                        if(allPopFields) {
                            for(int v = 0; v < person.getNumberAttributes(); v++) {
                                pout.print(",");
                                if(v == popHohIdx)
                                    // Sneak ID string into appropriate col.
                                    pout.print(person.getHohID());
                                else
                                    pout.print(person.getAttributeValue(v));
                            }
                        }
                        pout.println();
                    }
                }
            }
        }
            
        if(hout != null)
            hout.close();
        if(pout != null)
            pout.close();
    }
}
