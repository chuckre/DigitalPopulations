package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.AttributeMap;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Regions;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTableNoSwing;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Wrapper for a region map and its table, plus the spec for what we're intended
 * to do with them.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class RegionData {
    /** File names and interesting columns. */
    public Regions  spec;
    
    /** Contents of region map identified by spec. */
    public GISClass map;
    
    /** Contents of region table identified by spec. */
    public CSVTableNoSwing table;
    
    
    /**
     * Full number of households present in region table. The households PUMS
     * table will only contain a sampling of these.
     */
    public int      aggregateHouseholds;

    /**
     * Total number of people present in region table. The population PUMS table
     * will only contain a sampling of these.
     */
    public int      aggregatePopulation;


    /**
     * Construct blank instance.
     */
    public RegionData() {
    }

    /**
     * Init this object with data described by 'spec'. It's a good idea to call
     * validate() after this returns.
     * <P>
     * Loads map, loads table, and renumbers map.
     * 
     * @param dataDir
     *            base directory of input files
     * @param regionSpec
     *            input files and info
     * @param errors
     *            receiver of problems we've discovered
     * 
     * @throws IOException
     *             on any file error
     */
    protected void loadThis(File dataDir, Regions regionSpec, List<String> errors) throws IOException {
        this.spec = regionSpec;

        // Load map file.
        String tractsMapName = FileUtil.resolve(dataDir, regionSpec.map).getPath();
        this.map = GISClass.loadEsriAscii(tractsMapName);

        // Load and process table, if requested.
        if(ObjectUtil.isBlank(regionSpec.table)) {
            this.table = null;
        }
        else {
            String tractsTableName = FileUtil.resolve(dataDir, regionSpec.table).getPath();
            table = new CSVTableNoSwing(tractsTableName);

            int keyCol = this.table.findColumn(regionSpec.key);
            
            this.regionIDConversion(keyCol);
            
            // Count households if we have such a column.
            aggregateHouseholds  = 0;
            if(! ObjectUtil.isBlank(regionSpec.households)) {
                int col = table.findColumn(regionSpec.households);
                for( int i = table.getRowCount() - 1; i >= 0; i--) {
                    String hiar = table.getStringAt( i, col);
                    aggregateHouseholds += Integer.parseInt(hiar);
                }
            }
            
            // Count people
            aggregatePopulation = 0;
            if(! ObjectUtil.isBlank(regionSpec.population)) {
                int col = table.findColumn(regionSpec.population);
                for( int i = table.getRowCount() - 1; i >= 0; i--) {
                    String np = table.getStringAt( i, col);
                    try {
                        aggregatePopulation += Integer.parseInt(np);
                    } catch (NumberFormatException e) {
                        errors.add(String.format("%s in row %s in region file %s", 
                            ObjectUtil.getMessage(e),
                            table.getStringAt(i, keyCol), tractsTableName));
                    }
                }
            }
        }
    }

    /**
     * Perform some consistency checks on the loaded region data: total houses
     * vs. vacant houses vs. number of occupants.  Call this after load().
     * 
     * @param errors
     *            receiver of problems we've discovered
     */
    protected void validate(ArrayList<String> errs) {
        // We tolerate hoh or pop col name being blank.
        boolean hoh_ok = ! ObjectUtil.isBlank(spec.households);
        boolean pop_ok = ! ObjectUtil.isBlank(spec.population);
        int hohcol=-1, vaccol=-1, popcol=-1;

        if(hoh_ok) {
            hohcol = table.findColumn(spec.households);
            vaccol = table.findColumn(spec.vacancies);
        }
        if(pop_ok) {
            popcol = table.findColumn(spec.population);
        }
        
        if(table != null) {
            int keycol = table.findColumn(spec.key);
            
            // Verify table contains a record for every value in map.
            HashSet<Integer> tableKeys = new HashSet<Integer>();
            for(String cell : table.getColumn(keycol))
                tableKeys.add(new Integer(cell));
            Set<Integer> mapKeys = map.makeInventory();
            
            if(!tableKeys.equals(mapKeys)) {
                // If tableKeys > mapKeys, we don't care.
                // If mapKeys > tableKeys, crash.
                HashSet<Integer> tmp = new HashSet<Integer>(mapKeys);
                tmp.removeAll(tableKeys);
                if(!tmp.isEmpty())
                    errs.add(String.format(
                        "Records for %s %s are missing from table %s, but needed by map %s.",
                        spec.key, tmp, spec.table, spec.map));
            }
            
            // Verify num-hoh vs num-vac vs num-peops is reasonable.
            for(int r=0; r<table.getRowCount(); r++) {
                int nh=0;  //value in number-of-households cell
                int nv=0;  //value in number-of-vacancies cell
                int np=0;  //value in number-of-people cell
                
                // Get row key.
                String sk = table.getStringAt(r, keycol);
                
                // Get hoh data.
                if(hoh_ok) {
                    String sh = table.getStringAt(r, hohcol);
                    String sv = table.getStringAt(r, vaccol);
                    nh = ObjectUtil.parseInt(sh, 0);
                    nv = ObjectUtil.parseInt(sv, 0);
                }
                
                // Get pop data.
                if(pop_ok) {
                    String sp = table.getStringAt(r, popcol);
                    np = ObjectUtil.parseInt(sp, 0);
                }
    
                // Compare what we can.
                if(hoh_ok) {
                    // More vacant households than total households.
                    if(nv > nh)
                        errs.add(String.format("Region %s has more vacancies than households.", sk));
                    
                    if(pop_ok) {
                        // People live here, but all houses are vacant.
                        if(np>0 && nh<=nv)
                            errs.add(String.format("Region %s contains people, but also reports all households as vacant.", sk));
                        
                        // No people live here, yet some houses are not vacant.
                        if(np==0 && nh-nv>0)
                            errs.add(String.format("Region %s is vacant, but not all households are vacant.", sk));
                    }
                }
            }
        }
    }

    /**
     * Helper to load a map and its table, process and validate them, and throw
     * an exception if anything breaks.
     * 
     * @param spec
     *            file to load and how to connect them
     * @param dataDir
     *            base directory, relative to which file names will be resolved
     * @return populated object
     * 
     * @throws DataException
     *             on any logic error
     */
    public static RegionData load(Regions spec, File dataDir) throws IOException {
        ArrayList<String> errors = new ArrayList<String>();

        RegionData data = new RegionData();
        data.loadThis(dataDir, spec, errors);
        data.validate(errors);
        
        if(errors.size() > 0) {
            String msg = "Errors while loading attributes:\n  ";
            msg += ObjectUtil.join(errors, "\n  ");
            throw new DataException(msg);
        }
        
        return data;
    }

    /**
     * Helper to load a map and its table, process and validate them, and throw
     * an exception if anything breaks.
     * 
     * @param spec
     *            file to load and how to connect them
     * @param dataDir
     *            base directory, relative to which file names will be resolved
     * @return populated object
     * 
     * @throws IOException
     *             on any file error
     */
    public static RegionData load(AttributeMap spec, File dataDir) throws IOException {
        ArrayList<String> errors = new ArrayList<String>();

        Regions rspec = new Regions(spec.id, spec.map, spec.table, spec.key);
        RegionData data = new RegionData();
        data.loadThis(dataDir, rspec, errors);
        data.validate(errors);
        
        if(errors.size() > 0) {
            String msg = "Errors while loading attributes:\n  ";
            msg += ObjectUtil.join(errors, "\n  ");
            throw new DataException(msg);
        }

        return data;
    }
    /**
     * Convert region ID's to 1, 2, 3, ... which will increase the performance in later steps
     * Added by Yizhao Gao (ygao29@illinois.edu)
     */
    private void regionIDConversion(int keycol) {
    	GISClass newMap = new GISClass(this.map);
    	newMap.setNoDataValue(this.map.getNoDataValue());
    	
    	Vector<String> idVector = this.table.getColumn(keycol);   	
    	String [] idString = idVector.toArray(new String[idVector.size()]);
    	
    	HashMap<Integer, Integer> idMap = new HashMap<>();
    	
    	int regionIDNew = 1;
		int regionIDOld;
		for(int i = 0; i < idString.length; i++)
		{
			regionIDOld = Integer.parseInt(idString[i]);
			this.table.setValueAt(regionIDNew, i, keycol);
			
			idMap.put(regionIDOld, regionIDNew);
			
			regionIDNew ++;
		}
		
		for( int r = 0; r < newMap.getNumberRows(); r++){
			for( int c = 0; c < newMap.getNumberColumns(); c++){
				if( this.map.isNoData(r, c) == false) {
					regionIDOld = this.map.getCellValue(r, c);
					
					if(idMap.containsKey(regionIDOld))
					{
						regionIDNew = idMap.get(regionIDOld);
						newMap.setCellValue(r, c, regionIDNew);
					}
					else
					{
						System.err.println("Region ID " + regionIDOld + " exists in the ASC file, but not in the Region table");
					}
				}
			}
		}
		
		this.map = newMap;
	}
}
