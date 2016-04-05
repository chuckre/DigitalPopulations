package mil.army.usace.ehlschlaeger.digitalpopulations;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mil.army.usace.ehlschlaeger.digitalpopulations.io.PumsLoader;
import mil.army.usace.ehlschlaeger.digitalpopulations.pqfile.PumsQueryFile;
import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;



/**
 * Boolean expression for the filtering of households based on their attributes
 * and their members.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class PumsQuery implements Serializable {
    protected int[][]     vars;
    protected int[][]     minV;
    protected int[][]     maxV;
    protected boolean[][] popV;

	public PumsQuery() {
	}

    /**
     * Adds a set of conditions, all of which must be true to match a household.
     * If this method is called repeatedly, only one of the sets of conditions
     * must be met to consider a household a match. In other words, the
     * conditions specified in one call are ANDed together, while the disparate
     * calls are ORed.
     * <P>
     * One condition is defined by the corresponding elements in the given set
     * of arrays. The variable indicated by isPopulations[n] and variables[n]
     * must be greater than or equal to minValues[n] and less than or equal to
     * maxValues[n] to consider the condition met.
     * 
     * @param isPopulations[i]
     *            if true, then variables[i] refers to the PUMS populataion
     *            table. If false, then variables[i] refers to the PUMS
     *            households table.
     * @param variables[i]
     *            index of the variable to examine, as would be needed by
     *            getVariableValue
     * @param minValues[i]
     *            is the smallest acceptable value for variables[i]
     * @param maxValues[i]
     *            is the largest acceptable value for variables[i]
     */
	public void addAndQuery( boolean[] isPopulations, int[] variables, int[] minValues, int[] maxValues) {
        assert isPopulations != null;
        assert variables != null;
        assert minValues != null;
        assert maxValues != null;
        assert maxValues.length == minValues.length;
        assert maxValues.length == variables.length;
        assert maxValues.length == isPopulations.length;
	    
		if( vars == null) {
			vars = new int[ 1][ variables.length];
			minV = new int[ 1][ variables.length];
			maxV = new int[ 1][ variables.length];
			popV = new boolean[ 1][ variables.length];
			for( int i = 0; i < variables.length; i++) {
				vars[ 0][ i] = variables[ i];
				minV[ 0][ i] = minValues[ i];
				maxV[ 0][ i] = maxValues[ i];
				popV[ 0][ i] = isPopulations[ i];
			}
		} else {
			int old = vars.length;
			int[][] n_v = new int[ old + 1][];
			int[][] n_minV = new int[ old + 1][];
			int[][] n_maxV = new int[ old + 1][];
			boolean[][] n_popV = new boolean[ old + 1][];
			for( int i = 0; i < old; i++) {
				n_v[ i] = vars[ i];
				n_minV[ i] = minV[ i];
				n_maxV[ i] = maxV[ i];
				n_popV[ i] = popV[ i];
			}
			n_v[ old] = new int[ variables.length];
			n_minV[ old] = new int[ variables.length];
			n_maxV[ old] = new int[ variables.length];
			n_popV[ old] = new boolean[ variables.length];
			for( int i = 0; i < variables.length; i++) {
				n_v[ old][ i] = variables[ i];
				n_minV[ old][ i] = minValues[ i];
				n_maxV[ old][ i] = maxValues[ i];
				n_popV[ old][ i] = isPopulations[ i];
			}
			vars = n_v;
			minV = n_minV;
			maxV = n_maxV;
			popV = n_popV;
		}
	}

    /**
     * Count number of household members who meet conditions. Will return
     * population of household if all criteria are based on household
     * parameters.
     * 
     * @return -1 if we contain no queries, else number of people who match
     */
	public int numberTrue(PumsHousehold house) {
		if( vars == null)
			return -1;
		
        // Population table is mandatory. We need to return the number of
        // *people* that match, but we have no other source for that number.
        PumsPopulation[] members = house.getMembersOfHousehold();
        assert members != null && members.length > 0;
        
		int membersLength = 1;
	    membersLength = members.length;
		
        int count = 0;
		for( int i = 0; i < membersLength; i++) {
            boolean match = true;
            
			// j is an OR query
			for(int j=0; j<vars.length; j++) {
				// k is an AND query.
				for( int k = 0; k < popV[ j].length; k++) {
					if( popV[ j][ k] == true) {
						int pVarValue = members[ i].getAttributeValue( vars[ j][ k]);
						if( pVarValue < minV[ j][ k] || pVarValue > maxV[ j][ k]) {
						    // short circuit: one false means AND is false
							match = false;
							break;
						}
					} else {
						int hVarValue = house.getAttributeValue( vars[ j][ k]);
						if( hVarValue < minV[ j][ k] || hVarValue > maxV[ j][ k]) {
                            // short circuit: one false means AND is false
							match = false;
							break;
						} 
					}
				}
				
                // short circuit: one true means OR is true
				if(match)
				    break;
			}

            // TODO: another optimization: if the j/k loop above never sees a
            // pop variable (i.e. popV is always false) then we can skip the
            // rest of the i loop, as the answer will always be the same.
			
			// Member matched, count'em.
			if(match)
			    count++;
		}
		
		return count;
	}

    /**
     * numberTrue() returns -1 if PumsQuery object contains no parameters.
     * Parameters must be added with addAndQuery() function. Otherwise,
     * numberTrue() returns number of population members that fit criteria.
     * numberTrue() will return population of household even if all criteria are
     * based on household parameters.
     */
	public int numberTrue( PumsHousehold parent, int realization) {
	    // All realizations evaluate the same.
		return( numberTrue(parent));
	}

    /**
     * Construct a filter iterator that produces only households that pass this
     * query (i.e. numberTrue > 0).
     * 
     * @param houses
     *            iterator that produces households to test
     * 
     * @return iterator that produces only households with at least one matching
     *         member
     */
    public Iterator<PumsHousehold> iterateArch(final Iterator<PumsHousehold> houses) {
        return new Iterator<PumsHousehold>() {
            // next valid element to return
            private PumsHousehold next;
            // iterator for the elements following 'next'
            private Iterator<PumsHousehold> iter;

            /*constructor*/ {
                // find first valid element
                iter = houses;
                findNext();
            }
            
            /** @return true if there are more elements */
            public boolean hasNext() {
                return next != null;
            }

            /** @return next valid element */
            public PumsHousehold next() {
                PumsHousehold reeturn = next;
                findNext();
                return reeturn;
            }

            /** Not implemented. */ 
            public void remove() {
                // findNext() means iter.remote() will NOT work.
                throw new UnsupportedOperationException();
            }
            
            /** find the next element that pumsQuery approves of */
            protected void findNext() {
                while(iter.hasNext()) {
                    next = iter.next();
                    if(numberTrue(next) > 0)
                        break;
                    else
                        // Flag end-of-list in case we can't find a valid element.
                        next = null;
                }
            }
        };
    }
	
    /**
     * Construct a filter iterator that produces only households that pass this
     * query (i.e. numberTrue > 0).
     * 
     * @param houses
     *            iterator that produces households to test
     * 
     * @return iterator that produces only households with at least one matching
     *         member
     */
	public Iterator<PumsHouseholdRealization> iterateRzn(final Iterator<PumsHouseholdRealization> houses) {
	    return new Iterator<PumsHouseholdRealization>() {
	        // next valid element to return
	        private PumsHouseholdRealization next;
	        // iterator for the elements following 'next'
	        private Iterator<PumsHouseholdRealization> iter;

	        /*constructor*/ {
	            // find first valid element
	            iter = houses;
	            findNext();
	        }
	        
	        /** @return true if there are more elements */
	        public boolean hasNext() {
	            return next != null;
	        }

	        /** @return next valid element */
	        public PumsHouseholdRealization next() {
	            PumsHouseholdRealization reeturn = next;
	            findNext();
	            return reeturn;
	        }

	        /** Not implemented. */ 
	        public void remove() {
	            // findNext() means iter.remote() will NOT work.
	            throw new UnsupportedOperationException();
	        }
            
            /** find the next element that pumsQuery approves of */
            protected void findNext() {
                while(iter.hasNext()) {
                    next = iter.next();
                    if(numberTrue(next.getParentHousehold()) > 0)
                        break;
                    else
                        // Flag end-of-list in case we can't find a valid element.
                        next = null;
                }
            }
	    };
	}
	
	
	public static void main(String[] argv) throws IOException {
        //    PumsQuery -o file/dir query-file input-file...
	    
	    // hohFiles = select input-file like "rzn%d-households.*"
	    // popFile = select input-file like "rzn%d-population.*"
	    // warn about non-matching files
	    // for each hohFile
	    //   check dir for pop file, load and attach if found
	    //   if found pop file in popFiles, remove it
	    //   for each hoh
	    //      n = numberTrue(hoh)
	    //      if n>0
	    //        println(hoh.x, hoh.y, n);
	    // if popFiles not empty, warn

        OptionParser parser = new OptionParser();
        parser.accepts("o", "output file or dir").withRequiredArg().ofType(String.class);
        parser.acceptsAll(Arrays.asList("h", "help"), "print this help");
	    
        OptionSet opts = parser.parse(argv);
        if(opts.has("h") || opts.has("help") || argv.length == 0) {
            System.out.format("Usage:\n  %s [options] %s\n",
                PumsQuery.class.getSimpleName(),
                "query-file input-file...");
            parser.printHelpOn(System.out);
            
            System.out.println();
            System.out.println( "Argument\n--------");
            System.out.println( "query-file  PUMS query file");
            System.out.println( "input-file  household and population files to process");
            
            System.out.println();
            System.out.println( "Notes\n-----");
            System.out.println( " * Household files must be formatted as from Digital Populations.");
            System.out.println( " * Population files need not be listed; they will be auto-loaded");
            System.out.println( "   for each household file.");
            System.out.println( " * Any population files that are listed but not used by this");
            System.out.println( "   program will produce warnings.");
            
            return;
        }

        int reqdArgs = 2;
        if(opts.nonOptionArguments().size() < reqdArgs) {
            System.err.println("Required args are missing; there should be at least "+reqdArgs+".");
            System.exit(5);
        }
            
        // Enumerate the args we will need.
        File userDest = null;
        boolean userDestDir = false;  //true if userDest is a dir; false if plain file
        ArrayList<File> hohFiles = new ArrayList<File>();
        ArrayList<File> oddFiles = new ArrayList<File>();
        
        // Capture query file path.
        File queryFile = new File(argv[0]);
        if(!queryFile.exists())
            throw new IOException("Query file not found.");
        
        // Collect file names from command line, sort into household files and "other" files.
        Pattern hohPattern = Pattern.compile("^rzn([0-9]+)-households(.*)\\.csv$", Pattern.CASE_INSENSITIVE);
        {
            ArrayList<File> allFiles = new ArrayList<File>();

            // opts[0] is the query file
            for(int i=1; i<opts.nonOptionArguments().size(); i++)
                allFiles.add(new File(opts.nonOptionArguments().get(i)));
    
            for(File f : allFiles) {
                Matcher m = hohPattern.matcher(f.getName());
                if(m.matches())
                    hohFiles.add(f.getAbsoluteFile());
                else
                    oddFiles.add(f.getAbsoluteFile());
            }
        }

        // Decipher the -o option.
        if(opts.has("o")) {
            userDest = new File((String) opts.valueOf("o")).getAbsoluteFile();
            // cases:
            //   hohfiles=1, dest=file      OK  file=arg
            //   hohfiles=1, dest=dir       OK  file=arg/default.csv
            //   hohfiles=1, dest=absent    OK  file=arg
            //   hohfiles>1, dest=file      FAIL
            //   hohfiles>1, dest=dir       OK  file=arg/default.csv
            //   hohfiles>1, dest=absent    OK  file=arg/default.csv  (create dir)
            if(hohFiles.size() > 1 && userDest.isFile()) {
                throw new IOException("-o cannot specify plain file if more than 1 input file is given");
            }
            if(hohFiles.size() > 1 && !userDest.exists()) {
                if(! userDest.mkdirs())
                    throw new IOException("Unable to create output dir " + userDest);
            }
            
            // Tell system what to do with userDest.
            if(userDest.exists())
                userDestDir = userDest.isDirectory();
            else
                // If should be dir, then dir was created above. If doesn't
                // exist, we can assume should be file.
                userDestDir = false;
        }
        
        // Now process each household file.
        for(File hohFile : hohFiles) {
            // Extract rzn number and file note.
            Matcher m = hohPattern.matcher(hohFile.getName());
            m.matches();
            String rzn = m.group(1);
            String note = m.group(2);
            
            // Compute matching population file.
            File p = hohFile.getParentFile();
            String n = String.format("rzn%s-population%s.csv", rzn, note);
            File popFile = new File(p, n);

            // Compute appropriate output file.
            File destFile;
            p = hohFile.getParentFile();
            n = String.format("rzn%s-pumsquery%s.csv", rzn, note);
            if(userDest != null) {
                if(userDestDir)
                    p = userDest;
                else
                    n = userDest.getPath();
            }
            destFile = new File(p, n);
            
            // Remove from list of unrecognized files.
            oddFiles.remove(popFile);
            oddFiles.remove(destFile);
            
            if(!popFile.exists()) {
                System.err.println("WARNING:  Population file not found for " + hohFile.getName());
                popFile = null;
            }

            // Load query and compile for current set of files.
            List<String> hohSchema = CSVTable.loadSchema(hohFile);
            List<String> popSchema = CSVTable.loadSchema(popFile);
            PumsQuery query = PumsQueryFile.loadQuery(queryFile, hohSchema, popSchema);
            
            // Load, process, save.
            PrintStream out = null;
            try {
                out = new PrintStream(destFile);
                out.println("x,y,count");
                query.process(out, hohFile, popFile);
            }
            finally {
                if(out != null)
                    out.close();
            }
        }
        
        
        // Complain if any files left over.
        if(oddFiles.size() > 0) {
            StringBuffer msg = new StringBuffer();
            msg.append("WARNING:  Could not find a use for these files:\n");
            for(File f : oddFiles)
                msg.append("  ").append(f).append('\n');
            System.err.println(msg);
        }
    }

    /**
     * Load files, attach members to households, run query, and append results
     * to given file. Result is written as one household per line, format
     * "x,y,count" with location of household and number of its members matching
     * query.
     * 
     * @param out
     * @param hohFile
     * @param popFile
     * @throws IOException
     */
    protected void process(PrintStream out, File hohFile, File popFile) throws IOException {
        System.out.print("Loading " + hohFile.getName() + " ...");
        
        PumsLoader pl = new PumsLoader();
        ArrayList<PumsHouseholdRealization> hoh = pl.loadPumsHouseholdRzns(hohFile, 0, "x", "y", "uid");
        
        if(popFile != null) {
            List<PumsPopulation> pop = pl.loadPumsPopulation(popFile, "household");
            pl.populateHouseholdRzns(hoh, pop, null);
        }
        
        System.out.print(" Processing ...");
        process(out, hoh);
        System.out.println("Done.");
    }

    protected void process(PrintStream out, ArrayList<PumsHouseholdRealization> hoh) {
        for(PumsHouseholdRealization h : hoh) {
            int n = numberTrue(h.getParentHousehold());
            if(n > 0)
                out.format("%s, %s, %d\n", h.getEasting(), h.getNorthing(), n);
        }
    }
}
