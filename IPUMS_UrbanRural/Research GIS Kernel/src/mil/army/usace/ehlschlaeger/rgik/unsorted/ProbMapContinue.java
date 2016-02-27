package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.io.IOException;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
/**
 * ProbMapContinue class creates probability qualitative thematic maps for conflation.
 *  This is an unfinished work.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class ProbMapContinue {
	ProbClassMaps original, studyArea; 

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		Runtime rt = Runtime.getRuntime();
		rt.traceMethodCalls( false);
		rt.traceInstructions( false);
		if( argv.length != 5 && argv.length != 6) {
			System.out.println( 
				"Run this program with the following form:");
			System.out.println( 
				"java -ms96m -mx128m ProbMapContinue ...");
			System.out.println( 
				"name of directory with data");
			System.out.println(
				"name of application quality map layer");
			System.out.println(
				"name of generalized map layer");
			System.out.println(
				"the number of realizations, and the extent of the densogram");
			System.out.println(
				"If there is a sixth argument, then the pre-swap maps will be uncorrelated noise.");
			System.out.println(
				"FOR EXAMPLE:");
			System.out.println( 
				"java -ms96m -mx128m ProbMapContinue dataG geology40 geology160 35 200.0");
			System.out.println( 
				"OR");
			System.out.println( 
				"java -ms96m -mx128m ProbMapContinue dataG geology40 geology160 35 200.0 noise");
		}
		System.out.println( "Running ProbMap with the following arguments:");
		for( int i = 0; i < argv.length; i++)
			System.out.println( argv[i]);
		System.out.println( "");
		String directory = argv[0];
		String qualityMapName = argv[1];
		String generalizedMapName = argv[2];
		int numMaps = new Integer( argv[3].trim()).intValue();
		double maxDist = new Double( argv[4].trim()).doubleValue();
		System.out.println( "total memory: " + rt.totalMemory());
		System.out.println( "available memory: " + rt.freeMemory());
		GISClass go = GISClass.loadEsriAscii(directory + "/" + qualityMapName);
		double minRes = (double) Math.min( go.getEWResolution(), go.getNSResolution());
		RandomMaps rM = new RandomMaps( directory + "/" + qualityMapName, 
			directory + "/" + qualityMapName + "_" + generalizedMapName + "_r",
				minRes * 1.01f, maxDist, numMaps);
		rt.gc();	
		rM.swapCells(  0, 0, directory + "/" + qualityMapName + "_" + 
			generalizedMapName + "_r");
		Date nowDate = new Date();
		System.out.println("now: " + nowDate);
		System.out.println("");
	}

	/** in alpha testing 
	 * @throws IOException */
	public ProbMapContinue( GISClass goal, GISClass existing, String probPrefix) throws IOException {
		original = new ProbClassMaps( goal, existing);
		original.save( probPrefix);
	}

	/** in alpha testing 
	 * @throws IOException */
	public ProbMapContinue( GISClass goal, GISClass existing, GISClass study, String probPrefix) throws IOException {
		original = new ProbClassMaps( goal, existing);
		studyArea = new ProbClassMaps( original, study);
		studyArea.save( probPrefix);
	}
}
