package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.CustomMessageVEH;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.xml.sax.SAXException;



/**
 * Describes the files that can be used by ConflatePumsQueryWithTracts to build
 * an artificial census. This structure describes how the files relate to each
 * other; a FittingCriteria structure is used to describe an actual run.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
@XmlRootElement(name = "FileRelationship")
public class FileRelationship {
    //
    // DEV NOTE:
    // The Java tool "schemagen" should be used to generate a schema for these
    // classes. Using a schema in loadFile allows JAXB to catch some errors it
    // would otherwise ignore (like extra text or elements inside another
    // element, or markers that aren't enforced in code (like
    // @XmlElement(required)). Note that the classes are considered the master;
    // if changes need to be made, make them in the code, then run schemagen to
    // create the xsd file.
    //
    
    /** Land-use map. Is optional if popdensity is provided. */
    @XmlElement(name="landuse", required=false)
    public Landuse     landuse;
    
    /** Custom population density. */
    @XmlElement(name="popdensity", required=false)
    public    PopDensity  popdensity;
    
    /** Tract map and its attribute table. */
    @XmlElement(name="regions", required=true)
    public List<Regions> regions;

    @XmlElement(name="attribute")
    public List<AttributeMap> attributeMaps;
    
    /** Sample households (i.e. PUMS) table. OPTIONAL: only required if traits need it. */
    @XmlElement(name="households")
    public Households  households;
    
    /** Sample population (i.e. PUMS) table. */
    @XmlElement(name="population")
    public Population  population;

    /**
     * Matching records specify how to relate specific households and people to
     * the larger aggregate picture. Households will be randomly placed so that
     * their individual characteristics, when totaled per-region, resemble the
     * region characteristics in the aggregate table.
     */
    @XmlElementWrapper(name = "traits", required=true)
    @XmlElements({
        @XmlElement(name="trait", type=PumsTrait.class),
        @XmlElement(name="forbid", type=Forbid.class)})
    public List<Trait> traits;

    // ----------
    /** If this object was loaded from a file, this is the file. */
    @XmlTransient
    public File        sourceFile;
    /** Time stamp on source File when we loaded it. */
    @XmlTransient
    public Date        sourceFileTime;
    // ----------

    /**
     * Construct a new blank instance.
     * Use neu() instead for an instance filled with sensible defaults.
     */
    public FileRelationship() {
    }

    /**
     * Create instance, partially populated for convenience.
     * @return
     */
    public static FileRelationship neu() {
        FileRelationship inst = new FileRelationship();
        inst.landuse = new Landuse();
        inst.regions = new ArrayList<Regions>();
        inst.attributeMaps = new ArrayList<AttributeMap>();
        inst.households = new Households();
        inst.traits = new ArrayList<Trait>();
        return inst;
    }
    
    /**
     * Helper to set population density algorithm to mode 2: land-use
     * map plus class density table.  'null' will reset to mode 1:
     * auto-calculated from land-use map.
     *
     * @param classDensityTable file containing relative density for
     *     each land-use class, or null to clear
     */
    public void setPopDensityLanduse(String classDensityTable) {
        if(ObjectUtil.isBlank(classDensityTable))
            this.popdensity = null;
        else {
            this.popdensity = new PopDensity();
            this.popdensity.setLanduse(classDensityTable);
        }
    }

    /**
     * Helper to set population density algorithm to mode 3: explicit
     * density map.  'null' will reset to mode 1:
     * auto-calculated from land-use map.
     * 
     * @param mapFile ESRI ASCII map file containing relative density for
     *     every cell in area of interest, or null to clear
     */
    public void setPopDensityMap(String mapFile) {
        if(ObjectUtil.isBlank(mapFile))
            this.popdensity = null;
        else {
            this.popdensity = new PopDensity();
            this.popdensity.setMap(mapFile);
        }
    }

    /**
     * Find a project file, relative to our file's location.
     * 
     * @param path relative path to file to find
     * @return absolute path to file
     * @throws NullPointerException if we don't have a file location
     */
    public File findFile(String path) {
        return FileUtil.resolve(sourceFile.getParentFile(), path);
    }
    
    /**
     * Check for logic errors.  Rules:
     */
    public void validate() {
        ArrayList<String> msgs = new ArrayList<String>();

        try {
            if(popdensity == null && landuse == null)
                throw new DataException("<landuse> or <popdensity> must be provided");
            if(landuse != null)
                landuse.validate();
        } catch(DataException e) {
            msgs.add(ObjectUtil.getMessage(e));
        }
        
        try {
            DataException.verifyNotEmpty(regions, "'regions' element is mandatory");

            // ID is required only if >1 element present.  All IDs must be unique.
            if(regions.size() > 1) {
                HashMap<String, Regions> ids = new HashMap<String, Regions>();
                boolean foundMain = false;
                for(Regions r : regions) {
                    if(ObjectUtil.isBlank(r.id))
                        msgs.add("Element is missing 'id' attribute: "+r.toString());
                    if(ids.containsKey(r.id))
                        msgs.add(String.format("ID %s has already been defined by %s", r.id, ids.get(r.id)));
                    else
                        ids.put(r.id, r);
                    
                    if(!ObjectUtil.isBlank(r.vacancies) || !ObjectUtil.isBlank(r.households) || !ObjectUtil.isBlank(r.population)) {
                        if(foundMain)
                            throw new DataException("Only one <regions> element can specify vacancies/households/population.");
                        else
                            foundMain = true;
                    }
                }
            }
        } catch(DataException e) {
            msgs.add(ObjectUtil.getMessage(e));
        }

        if(attributeMaps != null && !attributeMaps.isEmpty()) {
            for(AttributeMap att : attributeMaps) {
                try {
                    att.validate();
                } catch(DataException e) {
                    msgs.add(ObjectUtil.getMessage(e));
                }
            }
        }
        
        try {
            DataException.verifyNotNull(households, "'households' element is mandatory");
        } catch(DataException e) {
            msgs.add(ObjectUtil.getMessage(e));
        }
        
        try {
            if(population != null)
                population.validate();
        } catch(DataException e) {
            msgs.add(ObjectUtil.getMessage(e));
        }

        for(Trait traitel : traits) {
            if(traitel instanceof PumsTrait) {
                PumsTrait trait = (PumsTrait)traitel;
                try {
                    trait.validate();
                    
                    if(regions != null && regions.size() > 1)
                        if(ObjectUtil.isBlank(trait.regionTable))
                            msgs.add("Attribute 'regionTable' is required when multiple region maps are available: "+trait);
                 } catch(DataException e) {
                     msgs.add(ObjectUtil.getMessage(e));
                 }
            }
        }
        
        if(!msgs.isEmpty()) {
            String msg = "Errors in " + toString() + ":\n  ";
            msg += ObjectUtil.join(msgs, "\n  ");
            throw new DataException(msg);
        }
    }

    /**
     * Clean up in preperation for saving.
     */
    protected void preSave() {
        if(population != null && population.isBlank())
            population = null;
        
        // Provide an ID for all traits missing one.
        //  - Find the largest number contained anywhere in current trait IDs.
        //  - Assign IDs to traits missing them, starting one higher.
        Pattern matcher = Pattern.compile("[0-9]+");
        long id = 0;
        for(Trait trait : traits) {
            if(!ObjectUtil.isBlank(trait.id)) {
                try {
                    // Extract every number we can find.
                    Matcher m = matcher.matcher(trait.id);
                    while(m.find()) {
                        // Keep the largest one.
                        long v = Long.parseLong(m.group());
                        id = Math.max(id, v);
                    }
                }
                catch(Exception e) {
                    // ignore; id need not contain a number
                }
            }
        }
        for(Trait trait : traits) {
            if(ObjectUtil.isBlank(trait.id))
                trait.id = Long.toString(++id);
        }

        // Clean invidual traits.
        for(Trait trait : traits) {
            trait.preSave();
        }
    }

    
//I wanna do this, but I just don't trust it enough to enable.
//If user complains, then we'll start playing with it.
//Maybe each model object needs a preSave(dir) method.  -WRZ
//
//    protected void relativize(File dir) throws IOException {
//        if(! dir.isAbsolute())
//            dir = dir.getAbsoluteFile();
//        landuse.map = relativize(dir, landuse.map);
//        
//        if(popdensity.getLanduse() != null)
//            popdensity.setLanduse(relativize(dir, popdensity.getLanduse()));
//        else
//            popdensity.setMap(relativize(dir, popdensity.getMap()));
//        
//        regions.map = relativize(dir, regions.map);
//        regions.table = relativize(dir, regions.table);
//        
//        households.table = relativize(dir, households.table);
//        population.table = relativize(dir, population.table);
//    }
//    
//    protected String relativize(File dir, String path) throws IOException {
//        if(path == null)
//            return null;
//        File f = new File(path);
//        if(f.isAbsolute()) {
//            // Path is absolute; relativize against given dir.
//            return FileUtil.relativize(dir, f);
//        }
//        else if(sourceFile != null) {
//            // Path is relative; resolve against old project location, then
//            // relativize against new.
//            return FileUtil.relativize(dir, FileUtil.resolve(sourceFile, path));
//        }
//        else
//            // Path is relative but we don't have a location to resolve against,
//            // so return unmodified.
//            return path;
//    }
    
    /**
     * Write all data into a new file. If file exists, it will be overwritten.
     * 
     * @param file
     * @throws JAXBException
     * @throws IOException 
     */
    public void saveFile(File file) throws JAXBException, IOException {
        preSave();
//        relativize(file);

        sourceFile = file;
        JAXBContext context = JAXBContext.newInstance(FileRelationship.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, file);

        sourceFileTime = new Date(file.lastModified());
    }

    /**
     * Create a new instance from the contents of a file.
     * <P>
     * Be sure to call validate() afterwards to test the result. You can then
     * report problems to the user or dispose of the result, as appropriate.
     * 
     * @param file
     *            path and name of file to load
     * @return new object
     * @throws JAXBException
     *             on any XML error
     * @throws IOException
     *             on any file error
     * @throws SAXException
     *             on any error reading schema or validating input
     */
    public static FileRelationship loadFile(File file) throws JAXBException, IOException, SAXException {
        file = file.getAbsoluteFile();

        JAXBContext context = JAXBContext.newInstance(FileRelationship.class);
        Unmarshaller um = context.createUnmarshaller();
        ValidationEventHandler errors = new CustomMessageVEH(file.toString());
        um.setEventHandler(errors);

        try {
            String s = "FileRelationship.xsd";
            URL f = FileRelationship.class.getResource(s);
            if(f == null)
                throw new FileNotFoundException("Resource "+s);
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(f);
            um.setSchema(schema);
            
            FileRelationship rel = (FileRelationship) um.unmarshal(new FileReader(file));
            rel.sourceFile = file;
            rel.sourceFileTime = new Date(file.lastModified());

            return rel;
        } catch(UnmarshalException e) {
            if(e.getCause() instanceof SAXException)
                throw (SAXException)e.getCause();
            else
                throw e;
        }
    }

    /** Simple exerciser for debugging. */
    public static void main(String[] args) throws Exception {
        FileRelationship rel = new FileRelationship();
        rel.landuse = new Landuse();
        rel.landuse.map = "ri_nlcd120.asc";
        rel.landuse.comment = "NLCD 2001";
        rel.landuse.vacant = new LanduseVacant();
        rel.landuse.vacant.desc = "Open Water";
        rel.landuse.vacant.classes = "11";
        rel.landuse.combos.add(new LanduseCombination("Wetland", 9, "90-99"));
        rel.landuse.combos.add(new LanduseCombination("Farm/Pasture", 8, "81-89"));
        
        Regions r = new Regions();
        r.id = "rgn1";
        r.map = "ri_tracts120.asc";
        r.table = "ri_tracts.csv";
        r.key = "CNTY_TRACT_FIPS";
        r.households = "HSE_UNITS";
        r.population = "POP2000";
        r.vacancies = "VACANT";
        rel.regions.add(r);
        
        r = new Regions();
        r.id = "rgn2";
        r.map = "xtra.asc";
        r.table = "xtra.csv";
        r.key = "rgn";
        rel.regions.add(r);

        rel.attributeMaps.add(new AttributeMap("urban", "urban.asc"));
        
        rel.households = new Households("SS01HRI.CSV", "SERIALNO", "NP");
        rel.population = new Population("SS01PRI.CSV", "SERIALNO");
        
        PumsTrait t = new PumsTrait();
        t.id = "1";
        t.regionTable = "rgn1";
        t.regionTrait = "SQUIDS";
        t.regionTotal = "POP2000";
        t.pumsTraitTable = Type.POPULATION;
        t.pumsTraitField = "TENTACLES";
        t.pumsTraitSelect = "8-16";
        t.desc = "Octo/Hexadecapuses";
        rel.traits.add(t);

//        File f = new File("test.xml").getAbsoluteFile();
//        rel.saveFile(f);
//        System.out.println(f);

        rel.validate();
        
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(FileRelationship.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(rel, System.out);

        FileRelationship rel2 = FileRelationship.loadFile(new File(
            "projects/RhodeIslandDigitalPopulations/data/relationship.dprxml"));
        m.marshal(rel2, System.out);
    }
}
