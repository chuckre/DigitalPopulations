package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.digitalpopulations.LandcoverPopulationDensity;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulation;
import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.FileRelationship;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Forbid;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.LanduseCombination;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Regions;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.FittingCriteria;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.MatchSpec;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria.TraitRefElement;
import mil.army.usace.ehlschlaeger.digitalpopulations.io.PumsLoader;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.TransformAttributes2double;
import mil.army.usace.ehlschlaeger.rgik.statistics.Count;
import mil.army.usace.ehlschlaeger.rgik.statistics.ForbidConstraint;
import mil.army.usace.ehlschlaeger.rgik.statistics.Match;
import mil.army.usace.ehlschlaeger.rgik.statistics.PointConstraint;
import mil.army.usace.ehlschlaeger.rgik.statistics.Proportion;
import mil.army.usace.ehlschlaeger.rgik.statistics.SpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.statistics.TractSpatialStatistic;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;
import mil.army.usace.ehlschlaeger.rgik.util.TimeTracker;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntList;



/**
 * Generates the pieces of the census synthesizer from a relationship file.
 * Encapsulates the "prepareData" phase of {@link ConflatePumsQueryWithTracts}.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class DataPreparer {
    // cheat and use master logger directly
    protected static Logger log = ConflatePumsQueryWithTracts.log;
    
    // Option switches
    /**
     * If true, we'll trim all unused columns from populations table to save
     * memory. Note that output files will be similarly truncated.
     */
    public boolean             trimPopulationColumns = false;

    // Control structures
    protected FittingCriteria  crit;
    protected FileRelationship rel;
    protected File             dataDir;

    // Land-use data
    protected GISClass         landuseMap;
    protected Map<Integer,String> landuseClasses;

    // Region (aggregate) data
    protected HashMap<String, RegionData> regionDatas;
    // Primary region provides counts of hoh/pop/vac
    protected RegionData primaryRegion;

    /** Single-attribute goal maps. */
    protected HashMap<String, RegionData> attributeMaps;
    
    //PUMS tables are too large to keep in memory
    
    /** Column names from household table. */
    protected CSVTableNoSwing householdSchema;
    /** Column names from population table. */
    protected CSVTableNoSwing populationSchema;

    /** Our source of random numbers. */
    protected Random random = new Random();

    
    
    /**
     * Create helper from fitting criteria and its linked relationship spec. All
     * required files will be loaded relative to the dir containing the fitting
     * criteria file.
     * 
     * @param crit fitting criteria spec
     * 
     * @throws IOException on any error loading data files
     */
    public DataPreparer(FittingCriteria crit) throws IOException {
        this.crit = crit;
        this.rel = crit.relationship;
        this.dataDir = crit.sourceFile.getAbsoluteFile().getParentFile();
        preLoad();
    }
    
    /**
     * Create helper from fitting criteria and its linked relationship spec.
     * 
     * @param crit fitting criteria spec
     * @param dataDir directory where all input files can be found
     * 
     * @throws IOException on any error loading data files
     */
    public DataPreparer(FittingCriteria crit, File dataDir) throws IOException {
        this.crit = crit;
        this.rel = crit.relationship;
        this.dataDir = dataDir;
        preLoad();
    }

    /**
     * Create helper from a relationship spec.  Functions that require
     * fitting criteria spec won't work.
     * 
     * @param rel file relationship spec
     * @param dataDir directory where all input files can be found
     * 
     * @throws IOException on any error loading data files
     */
    public DataPreparer(FileRelationship rel, File dataDir) throws IOException {
        this.crit = null;
        this.rel = rel;
        this.dataDir = dataDir;
        preLoad();
    }

    /**
     * Load basic useful stuff that the other methods will need.
     * 
     * @throws IOException
     */
    protected void preLoad() throws IOException {
        if(rel.landuse != null && !ObjectUtil.isBlank(rel.landuse.map)) {
            loadLanduse();
            TimeTracker.finished("Loading land-use maps");
        }

        if(rel.regions != null) {
            loadRegions();
            loadAttributeMaps();
            TimeTracker.finished("Loading goal maps");
        }
    }
    
    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        random = source;
    }

    public FittingCriteria getFittingCriteria() {
        return crit;
    }

    /** @return primary region from relationship file */
    public RegionData getPrimaryRegion() {
        return primaryRegion;
    }
    
    public GISClass getLanduseMap() {
        return landuseMap;
    }

    /**
     * Returns the schema (list of column names) for the last household table we
     * loaded. WARNING: Not valid until after loadPumsHouseholds() has been
     * called.
     * 
     * @return households table column names
     */
    public CSVTableNoSwing getHouseholdSchema() {
        return householdSchema;
    }

    /**
     * Returns the schema (list of column names) for the last population table
     * we loaded. WARNING: Not valid until after loadPumsPopulation() has been
     * called.
     * 
     * @return population table column names
     */
    public CSVTableNoSwing getPopulationSchema() {
        return populationSchema;
    }

    /** @return list of single-attribute goal maps from fitting criteria */
    public HashMap<String,RegionData> getAttributeMaps() {
        return attributeMaps;
    }
    
    /**
     * Load and pre-process the land-use map. The file is as specified in the
     * relationship file, and the cell values are remapped as specified.
     * 
     * @throws IOException on any file error
     */
    protected void loadLanduse() throws IOException {
        String file = FileUtil.resolve(dataDir, rel.landuse.map).getPath();
        landuseMap = GISClass.loadEsriAscii(file);

        // Construct an attribute "table" to go with the new landuseMap.
        landuseClasses = new HashMap<Integer, String>();
        
        // Track classes that have been mapped to something.
        BitSet mapped = new BitSet();

        // For speed, build a map from sourceClass to targetClass
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (LanduseCombination combo : rel.landuse.combos) {
            for (Integer source : LanduseCombination.makeIntSet(combo.classes)) {
                if(mapped.get(source))
                    throw new DataException(String.format("Land-use class %d has already been mapped to %d.", source, map.get(source)));
                map.put(source, combo.target);
                mapped.set(source);
            }
            landuseClasses.put(combo.target, combo.desc);
        }

        // Also flag all classes marked 'vacant'
        Set<Integer> vacancies;
        if(rel.landuse.vacant != null && rel.landuse.vacant.classes != null) {
            vacancies = LanduseCombination.makeIntSet(rel.landuse.vacant.classes);
            for(Integer source : vacancies) {
                mapped.set(source);
            }
        }
        else {
            // no vacant classes -> empty set
            vacancies = new HashSet<Integer>();
        }

        // Convert the values in landuseMap according to the above mapping.
        // Also collect values of all the classes we found in the loaded map.
        BitSet used = new BitSet();
        for (int r = landuseMap.getNumberRows() - 1; r >= 0; r--) {
            for (int c = landuseMap.getNumberColumns() - 1; c >= 0; c--) {
                if (landuseMap.isNoData(r, c) == false) {
                    int klass = landuseMap.getCellValue(r, c);
                    used.set(klass);

                    Integer target = map.get(klass);
                    if(target == null) {
                        // If class is 'vacant', set it to no-data.
                        if(vacancies.contains(klass)) {
                            landuseMap.setNoData(r, c, true);
                        }
                        //else unmapped source -> we'll catch those below
                    }
                    
                    // Class value is mapped to different value, update cell.
                    else {
                        landuseMap.setCellValue(r, c, target.intValue());
                    }
                }
            }
        }
        
        // Verify that all classes in map have been addressed by the <landuse>
        // descriptor.  Doing it down here means we can print a single message that
        // contains all of the problem values.
        used.andNot(mapped);  //subtract mapped from used
        if(used.cardinality() > 0)
            throw new DataException("Land-use map contains classes that have not been remapped: " + used);
    }
    
    /**
     * Load region map and table sets, and validate everything.
     * 
     * @throws IOException on any file error
     * @throws DataException
     *             on any logic error
     */
    protected void loadRegions() throws IOException {
        regionDatas = new HashMap<String, RegionData>();
        primaryRegion = null;
        
        for(Regions spec : rel.regions) {
            RegionData data = RegionData.load(spec, dataDir);
            regionDatas.put(spec.id, data);
            
            if(!ObjectUtil.isBlank(spec.vacancies) || !ObjectUtil.isBlank(spec.households) || !ObjectUtil.isBlank(spec.population)) {
                if(primaryRegion != null)
                    throw new DataException("Only one <regions> element can specify vacancies/households/population.");
                else
                    primaryRegion = data;
            }
        }
        
        if(primaryRegion == null)
            throw new DataException("One <regions> element must specify vacancies/households/population.");
    }

    /**
     * Load extra attribute maps.
     * 
     * @throws IOException on any file error
     * @throws DataException
     *             on any logic error
     */
    protected void loadAttributeMaps() throws IOException {
        attributeMaps = new HashMap<String, RegionData>();
        if(rel.attributeMaps != null && rel.attributeMaps.size() > 0) {
            throw new DataException("Attribute maps are disabled for now.  Syntax is confusing and they don't currently work.  Please use <forbid> instead.");
//            for(AttributeMap spec : rel.attributeMaps) {
//                RegionData data = RegionData.load(spec, dataDir);
//                attributeMaps.put(spec.id, data);
//            }
        }
    }

    /**
     * Load a map from our data dir, and cache result in case it's needed again.
     * 
     * @param relName
     *            path/name of file, relative to our data dir
     * 
     * @return GISClass object with file's content
     * 
     * @throws IOException
     *             if file can't be found or read
     */
    protected RegionData getMap(String relName) throws IOException {
        File file = FileUtil.resolve(dataDir, relName);
        
        // abuse attributeMaps to cache our map files.
        // key is full absolute path to file, which user is unlikely to ever use
        // as an id themselves.
        String key = file.getPath();
        RegionData rd = attributeMaps.get(key);
        if(rd == null) {
            rd = new RegionData();
            rd.spec = new Regions(key, relName, null, null);
            rd.map = GISClass.loadEsriAscii(file);
            attributeMaps.put(key, rd);
        }
        
        return rd;
    }

    /**
     * @return the file we will load PUMS household data from
     */
    public File getPumsHouseholdsFile() {
        return FileUtil.resolve(dataDir, rel.households.table);
    }

    /**
     * Load household and population archtypes, and install the population into
     * their corresponding households.
     * 
     * @return list of household archtype objects with members installed
     * 
     * @throws IOException on any error accessing input tables
     */
    public List<PumsHousehold> loadHouseholds() throws IOException {
      PumsLoader pl = new PumsLoader();
      pl.setRandomSource(random);
      
      List<PumsHousehold> pumsHouses = pl.loadPumsHouseholds(getPumsHouseholdsFile(), rel.households.key);
      this.householdSchema = pl.getHouseholdSchema();
      
      // If no population file, bail.
      File popFile = getPumsPopulationFile();
      if(popFile != null) {
          List<PumsPopulation> pumsPeople = pl.loadPumsPopulation(getPumsPopulationFile(), rel.population.household);
          pl.populateHouseholds(pumsHouses, pumsPeople, rel.households.members);
          this.populationSchema = pl.getPopulationSchema();
      }
      
      return pumsHouses;
    }


    /**
     * @return the file we will load PUMS population data from, or null if none provided
     */
    public File getPumsPopulationFile() {
        if(rel.population != null && !ObjectUtil.isBlank(rel.population.table))
            return FileUtil.resolve(dataDir, rel.population.table);
        else
            return null;
    }
    
    /**
     * Load PUMS households table directly into a CSVTable without processing.
     * Not recommended; memory consumption can be large.
     */
    public CSVTableNoSwing loadPumsHouseholdCSV() throws IOException {
        String file = getPumsHouseholdsFile().getPath();
        CSVTableNoSwing householdsTable = new CSVTableNoSwing(file);
        householdSchema = CSVTableNoSwing.createEmpty(householdsTable);
        return householdsTable;
    }

    /**
     * Load PUMS population table directly into a CSVTable without processing.
     * Not recommended; memory consumption can be large.
     */
    public CSVTableNoSwing loadPumsPopulationCSV() throws IOException {
        String file = getPumsPopulationFile().getPath();
        CSVTableNoSwing populationTable;
        if(trimPopulationColumns && crit != null) {
            Set<String> cols = crit.getRefdPopCols();
            populationTable = new CSVTableNoSwing(file, cols);
        }
        else
            populationTable = new CSVTableNoSwing(file);
        populationSchema = CSVTableNoSwing.createEmpty(populationTable);
        return populationTable;
    }

    /**
     * Build or load PDF as requested by crit file. Call getPDF() for map
     * corresponding to primaryRegion, or call this to build alternate maps.
     * 
     * @param region
     *            spec for region map for which a PDF will be built
     * 
     * @return population density map
     * @throws IOException
     *             on any error loading data files
     */
    public GISLattice makePDF(RegionData region) throws IOException {
        if(rel.popdensity != null) {
            // Build PDF directly from user data.
            if(rel.popdensity.getMap() != null) {
                // Finished PDF is provided.
                File f = crit.findFile(rel.popdensity.getMap());
                LogUtil.detail(log, "Loading population density from "+f);
                // Load user's pdf.
                GISLattice pdf = GISLattice.loadEsriAscii(f);
                // Resample user's pdf to match regionMap.
                pdf = LandcoverPopulationDensity.createPDF(region.map, pdf);
                return pdf;
            }
            else if(rel.popdensity.getLanduse() != null) {
                // Per-land-use-code densities provided.
                File f = crit.findFile(rel.popdensity.getLanduse());
                LogUtil.detail(log, "Building population density from "+f);
                CSVTableNoSwing densities = new CSVTableNoSwing(f.getAbsolutePath());
                
                int klassCol = densities.findColumn(LandcoverPopulationDensity.TABLE_LANDUSE_CLASS);
                int densCol = densities.findColumn(LandcoverPopulationDensity.TABLE_LANDUSE_DENSITY);
                Map<Integer, Double> valueMap = new HashMap<Integer, Double>();

                // Load code table.
                for(int r=0; r<densities.getRowCount(); r++) {
                    // Simple comment support: if line (i.e. col 0) starts with #, skip.
                    if(densities.getStringAt(r, 0).startsWith("#"))
                        continue;
                    
                    int k = Integer.parseInt(densities.getStringAt(r, klassCol));
                    double v = Double.parseDouble(densities.getStringAt(r, densCol));
                    valueMap.put(k, v);
                }
                
                // Build PDF from land-use map and code table.
                GISLattice pdf = LandcoverPopulationDensity.createPDF(landuseMap, valueMap);
                return pdf;
            }
            else
                throw new DataException("<popdensity> doesn't specify any file.");
        }
        else {
            // Compute PDF from input maps.
            DataException.verifyNotNull(landuseMap, "Land-use map is required to compute population density.");
            DataException.verifyNotNull(region.map,  "Region map is required to compute population density.");
            DataException.verifyNotNull(region.table, "Region attribute table is required to compute population density.");
            DataException.verifyNotBlank(region.spec.key, "<regions key> is required to compute population density.");
            DataException.verifyNotBlank(region.spec.households, "<regions key> is required to compute population density.");
            
            LandcoverPopulationDensity lpd = new LandcoverPopulationDensity( 
                landuseMap, landuseClasses,
                region.map, region.table,
                region.spec.key, region.spec.households);
            GISLattice pdf = lpd.createPDF();

            
            // Write inputs and outputs from LPD into files in case user wants to customize.
            File f = null;
            LogUtil.cr(log);
            
            f = new File(RGIS.getOutputFolder(), "LandcoverPopulationDensity-coverage.csv").getCanonicalFile();
            try {
                lpd.writeLandPercentTable(f);
                LogUtil.result(log, "NOTE: Wrote LPD land-percent table to %s", f);
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't create LPD data file "+f, e);
            }

            f = new File(RGIS.getOutputFolder(), "LandcoverPopulationDensity-input.csv").getCanonicalFile();
            try {
                lpd.writeSourceTable(f);
                LogUtil.result(log, "NOTE: Wrote LPD input table to %s", f);
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't create LPD data file "+f, e);
            }
                
            f = new File(RGIS.getOutputFolder(), "LandcoverPopulationDensity-landuse.csv").getCanonicalFile();
            try {
                lpd.writeSolutionTable(f);
                LogUtil.result(log, "NOTE: Wrote LPD land-use table to %s", f);
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't create LPD data file "+f, e);
            }
                
            f = new File(RGIS.getOutputFolder(), "LandcoverPopulationDensity-map.asc").getCanonicalFile();
            try {
                pdf.writeAsciiEsri(f);
                LogUtil.result(log, "NOTE: Wrote LPD map to %s", f);
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't create LPD data file "+f, e);
            }
                
            f = new File(RGIS.getOutputFolder(), "LandcoverPopulationDensity-population.csv").getCanonicalFile();
            try {
                lpd.writePopulationTable(f);
                LogUtil.result(log, "NOTE: Wrote LPD class-population table to %s", f);
            } catch (IOException e) {
                log.log(Level.WARNING, "Can't create LPD data file "+f, e);
            }
            
            return pdf;
        }
    }
    
    /**
     * Build a few traits to manage the raw and relative populations of the map.
     * 
     * @param region map of regions with population data to model
     * 
     * @return list of traits
     */
    protected PumsTrait[] makeAutomaticTraits(RegionData region) {
        DataException.verifyNotBlank(region.spec.households, "<regions households> is mandatory.");

        List<PumsTrait> mantra = new ArrayList<PumsTrait>();
        boolean hoh_ok = ! ObjectUtil.isBlank(region.spec.vacancies);
        boolean pop_ok = ! ObjectUtil.isBlank(region.spec.population);

        // Automatic Trait #1: Number of Households per Region
        PumsTrait trait1 = new PumsTrait();
        trait1.id = "Auto-1";
        trait1.desc = "Absolute Households (AUTO)";
        // Goal: absolute number of households per region
        trait1.regionTable = region.spec.id;
        trait1.regionTrait = region.spec.households;
        trait1.regionTotal = "1";
        // Stats: absolute number of households
        trait1.pumsTraitTable = Type.HOUSEHOLDS;
        trait1.pumsTraitField = "1";
        trait1.pumsTotalTable = null;

        mantra.add(trait1);
        
        // Automatic Trait #2: Vacant Households %
        if(hoh_ok && pop_ok) {
            DataException.verifyNotBlank(rel.households.members, "<regions vacancies> was provided, so <households members> is required as well.");

            PumsTrait trait2 = new PumsTrait();
            trait2.id = "Auto-2";
            trait2.desc = "Vacant Households (AUTO)";
            // Goal: % of households that are vacant
            trait2.regionTable = region.spec.id;
            trait2.regionTrait = region.spec.vacancies;
            trait2.regionTotal = region.spec.households;
            // Stats: % of households with zero members
            trait2.pumsTraitTable = Type.HOUSEHOLDS;
            trait2.pumsTraitField = rel.households.members;
            trait2.pumsTraitSelect = "0";
            trait2.pumsTotalTable = Type.HOUSEHOLDS;
            trait2.pumsTotalField = "1";
            
            mantra.add(trait2);
        }
        
        // Automatic Trait #3: Absolute Population per Region
        if(pop_ok) {
            PumsTrait trait3 = new PumsTrait();
            trait3.id = "Auto-3";
            trait3.desc = "Absolute Population (AUTO)";
            // Goal: # people
            trait3.regionTable = region.spec.id;
            trait3.regionTrait = region.spec.population;
            trait3.regionTotal = "1";
            // Stats: # people
            trait3.pumsTraitTable = Type.HOUSEHOLDS;
            trait3.pumsTraitField = rel.households.members;
            trait3.pumsTotalTable = null;

            mantra.add(trait3);
        }
 
        return mantra.toArray(new PumsTrait[mantra.size()]);
    }

    /**
     * Sift through fitting criteria file and extract all stats that can be
     * considered to be <i>constraints</i>, i.e. conditions that <b>must not</b>
     * be violated.
     * <P>
     * Constraints are created by defining special traits, then listing them in
     * the 'position-rules' section of the fitting criteria file. Traits must
     * either use an attribute map, or be the special 'forbid' type. Trait
     * references within 'position-rules' can only use 'match' to refer to such
     * a trait.
     * 
     * @return list of constraint evaluators
     * @throws IOException on any error loading map files
     */
    public ArrayList<PointConstraint> makeConstraints() throws IOException {
        LinkedHashMap<Trait, TraitRefElement> criteria = getFittingCriteria().traitCluster;
        ArrayList<PointConstraint> stats = new ArrayList<PointConstraint>();
        
        if(criteria != null) {
            for (Trait trait : criteria.keySet()) {
                TraitRefElement tre = criteria.get(trait);

                if(tre instanceof MatchSpec) {
                    if(trait instanceof PumsTrait) {
                        // target of ref is an attribute-map trait
                        PumsTrait pt = (PumsTrait)trait;
                        RegionData goalMap = attributeMaps.get(pt.attribute);
                        Match stat = Match.createStat(
                            goalMap.map, pt.attributeSelect,
                            pt.pumsTraitTable == Type.POPULATION, pt.pumsTraitField, pt.pumsTraitSelect);
                        if(! ObjectUtil.isBlank(trait.desc))
                            stat.setLabel(trait.desc);
                        stats.add(stat);
                    }
                    else if(trait instanceof Forbid) {
                        // target of ref is a <forbid>
                        Forbid ft = (Forbid)trait;
                        RegionData goalMap = getMap(ft.map);
                        ForbidConstraint stat = ForbidConstraint.createStat(
                            ft.pumsTraitTable == Type.POPULATION, ft.pumsTraitField, ft.pumsTraitSelect,
                            goalMap.map, ft.mapSelect);
                        if(! ObjectUtil.isBlank(trait.desc))
                            stat.setLabel(trait.desc);
                        stats.add(stat);
                    }
                }
            }
        }
        
        return stats;
    }
    
    /**
     * Find best region or attribute map for a trait. Examines attribute
     * 'regionTable' or 'attribute' as needed.
     * 
     * @param trait
     *            object to examine
     * 
     * @return region data requested by trait
     * 
     * @throws DataException
     *             if trait is bad or confusing
     */
    protected RegionData findGoalMap(PumsTrait trait) {
        RegionData rgn;

        if(trait.attribute == null) {
            // This is a region goal.
            if(regionDatas.size() == 0)
                throw new DataException("No region maps are available.");
            else if(trait.regionTable == null) {
                if(regionDatas.size() == 1)
                    // There's only one, return it.
                    rgn = regionDatas.values().iterator().next();
                else
                    // There's more than one, we're confused.
                    throw new DataException(String.format(
                        "Project has %d region maps; trait must specify which to use: %s",
                        regionDatas.size(), trait));
            }
            else {
                // Trait specs a table name, we want to look it up even if there's only one.
                rgn = regionDatas.get(trait.regionTable);
            }
        }
        else {
            // This is an attribute map goal.
            rgn = attributeMaps.get(trait.attribute);
        }

        if(rgn == null)
            throw new DataException("Can't find goal map for "+trait);
        
        return rgn;
    }

    /**
     * Build statistics goal object from a trait. Scans current region map and
     * table, and computes target values for each region. Can be used to analyze
     * the quality of an accumulation object.
     * <P>
     * Note this method must be synchronized with makeAccumStat() so the two
     * methods make matched pairs of objects.
     * 
     * @param trait
     *            specification for goals
     * 
     * @return an object with goal values prepared
     */
    public SpatialStatistic makeGoalStat(PumsTrait trait) {
        
        if(! ObjectUtil.isBlank(trait.regionTrait)) {
            RegionData rgn = findGoalMap(trait);

            // Validate region map spec.
            DataException.verifyNotBlank(rgn.spec.key, "Attribute \"key\" is missing from <regions> element.");
            DataException.verifyNotBlank(trait.regionTotal, "regionTotal is missing in "+trait);
            
            TractSpatialStatistic goal;
            try {
                double fixedTot = Double.parseDouble(trait.regionTotal);
                // exception above kicks us to the catch below
                if(fixedTot != 1.0)
                    throw new DataException("The only constant regionTotal supports is 1.0: "+trait);
                
                Count c = Count.createGoal(rgn.map, rgn.table, rgn.spec.key, trait.regionTrait);
                c.setLabel(trait.desc);
                goal = c;
            } catch (NumberFormatException e) {
                Proportion p = Proportion.createGoal(rgn.map, rgn.table, rgn.spec.key,
                    trait.regionTrait, trait.regionTotal);
                p.setLabel(trait.desc);
                goal = p;
            }

            return goal;
        }
        else if(! ObjectUtil.isBlank(trait.attribute)) {
            Match m = Match.createGoal();
            if(! ObjectUtil.isBlank(trait.desc))
                m.setLabel(trait.desc);
            return m;
        }
        else {
            throw new DataException("No goal specified in trait: "+trait);
        }
    }

    /**
     * Build statistics accumulation object from a trait. Computes values for an
     * arrangement of households, and can be compared to a goal object to
     * determine quality of arrangement.
     * <P>
     * Note this method must be synchronized with makeGoalStat() so the two
     * methods make matched pairs of objects.
     * 
     * @param trait
     *            specification for statistics
     * 
     * @return an object that computes and maintains statistics for sets of
     *         PumsHousehold objects: Count, Proportion, or Match
     */
    public SpatialStatistic makeAccumStat(PumsTrait trait) {
        if(! ObjectUtil.isBlank(trait.regionTrait)) {
            TransformAttributes2double numer, denom;
            
            numer = new PumsTraitGetter(trait, householdSchema, populationSchema);
            denom = PumsTotalGetter.make(trait, householdSchema, populationSchema);
            
            RegionData rgn = findGoalMap(trait);
            TractSpatialStatistic stat;
            if(denom == null) {
                Count c = Count.createStat(rgn.map, numer);
                c.setLabel(trait.desc);
                stat = c;
            }
            else {
                Proportion p = Proportion.createStat(rgn.map, numer, denom);
                p.setLabel(trait.desc);
                stat = p;
            }
            return stat;
        }
        else if(! ObjectUtil.isBlank(trait.attribute)) {
            RegionData map = findGoalMap(trait);
            boolean isPop = (trait.pumsTraitTable == Type.POPULATION);
            Match m = Match.createStat(map.map, trait.attributeSelect,
                isPop, trait.pumsTraitField, trait.pumsTraitSelect);
            if(! ObjectUtil.isBlank(trait.desc))
                m.setLabel(trait.desc);
            return m;
        }
        else {
            throw new DataException("No goal specified in trait: "+trait);
        }
    }

    /**
     * Build query that selects all people and households fitting the weighting
     * criteria.
     * 
     * @param crit fitting criteria from which query will be built
     * @return new query object
     */
    public PumsQuery makePumsQuery() {
        // Build from all traits listed in fitting criteria.
        Set<PumsTrait> keyTraits = crit.traitWeights.keySet();
        BitSet  isPops = new BitSet();
        IntList attributes = new ArrayIntList();
        IntList minValues = new ArrayIntList();
        IntList maxValues = new ArrayIntList();

        for (PumsTrait trait : keyTraits) {
            // Parse table and column
            boolean isPop = (trait.pumsTraitTable == Type.POPULATION);
            int attribute;
            if (isPop) {
                attribute = populationSchema.findColumn(trait.pumsTraitField);
            } else {
                attribute = householdSchema.findColumn(trait.pumsTraitField);
            }

            // Parse range string
            String selector = null;
            if(trait.pumsTraitSelect != null)
                selector = trait.pumsTraitSelect;
            else
                selector = trait.pumsTraitContinuous;
            
            if(selector == null)
                selector = "";
            else
                selector = selector.trim();
            
            // - For each number or range listed, add the current attribute and the range values.
            if(selector.length() > 0) {
                String[] ranges = selector.split("\\s|,");
                for (String range : ranges) {
                    if(range.length() > 0) {
                        int p = range.indexOf('-');
                        try {
                            if(p < 0) {
                                // lone int
                                isPops.set(attributes.size(), isPop);
                                attributes.add(attribute);
                                
                                int first = Integer.parseInt(range);
                                minValues.add(first);
                                maxValues.add(first);
                            }
                            else {
                                // range "7-9"
                                isPops.set(attributes.size(), isPop);
                                attributes.add(attribute);
                                
                                int first = Integer.parseInt(range.substring(0, p));
                                int last = Integer.parseInt(range.substring(p + 1));
                                minValues.add(first);
                                maxValues.add(last);
                            }
                        }
                        catch(NumberFormatException e) {
                            throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                        }
                        catch(IndexOutOfBoundsException e) {
                            throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                        }
                    }
                }
            }
            else {
                // Attribute was asked for, but no range was given.
                isPops.set(attributes.size(), isPop);
                attributes.add(attribute);
                
                minValues.add(Integer.MIN_VALUE);
                maxValues.add(Integer.MAX_VALUE);
            }
        }

        PumsQuery pq = new PumsQuery();
        pq.addAndQuery(
            ObjectUtil.toArray(isPops, attributes.size()), attributes.toArray(),
            minValues.toArray(), maxValues.toArray());
        return pq;
    }
}
