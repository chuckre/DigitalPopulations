package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomField;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;



/**
 * ProbMap class creates probability qualitative thematic maps for conflation.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class ProbMap extends RGIS implements RGISFunction  {
	private ProbClassMaps original, studyArea; 

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
				"java -ms96m -mx128m ProbMap ...");
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
				"java -ms96m -mx128m ProbMap dataG geology40 geology160 35 200.0");
			System.out.println( 
				"OR");
			System.out.println( 
				"java -ms96m -mx128m ProbMap dataG geology40 geology160 35 200.0 noise");
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
		Date nowDate = new Date();
		System.out.println("Starting " + directory + "/" + qualityMapName +
			" densogram, it is now: " + nowDate);
		double minRes = (double) Math.min( go.getEWResolution(), go.getNSResolution());
		if( maxDist <= minRes * 1.01f)
			maxDist = minRes * 1.011f;
		DensoStat dGoal = new DensoStat( go.getMinimumValue(), go.getMaximumValue(), 
			minRes * 1.01f, maxDist, go);
		dGoal.setDecayExponent( 0.0f);
		dGoal.print();
		rt.gc();
		nowDate = new Date();
		System.out.println(
			"Starting probability maps for: " + generalizedMapName + ", it is now: " + nowDate);
		GISClass ex = GISClass.loadEsriAscii(directory + "/" + generalizedMapName);
		ProbClassMaps test = new ProbClassMaps( go, ex);
		test.save( directory + "/prob_" + qualityMapName + "_" + generalizedMapName + "_cat_");
		nowDate = new Date();
		System.out.println("");
		double d[] = new double[ test.getMaximumClass() - test.getMinimumClass()];
		LatticeRandomField rf[] = new LatticeRandomField[ test.getMaximumClass() - test.getMinimumClass() + 1];
		double addDD = minRes;
		double dd = minRes;
		for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
			d[ i] = dd;
			rf[ i] = new LatticeRandomField( go, d[ i], 1.0f, d[ i] / 3.0f, (long) (i + 1320));
			rf[ i].makeUniform();
		}
		GISClass realization = test.realize( rf);
		DensoStat dTest = new DensoStat( test.getMinimumClass(), test.getMaximumClass(), 
			minRes * 1.01f, maxDist, realization);
		double bestFit = dGoal.spread( dTest);
		System.out.println( "Goal:");
		dGoal.print();
		System.out.println( "Noise:");
		dTest.print();
		nowDate = new Date();
		System.out.println("now: " + nowDate);
		System.out.println("");
		int done = 1;
		if( argv.length == 6)
			done = 0;
		while( done > 0) {
			System.out.print( "Best fit: " + bestFit + ", with:");
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				System.out.print( " " + d[i]);
				rf[i].writeAsciiEsri( directory + "/rf" + i);
			}
			System.out.println( "");
			done = 0;
			dd += addDD;
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				LatticeRandomField previous = rf[ i];
				rf[ i] = new LatticeRandomField( go, dd, 1.0f, dd / 3.0f, (long) (i + 1320));
				rf[i].makeUniform();
				realization = test.realize( rf);
				dTest = new DensoStat( test.getMinimumClass(), test.getMaximumClass(), minRes * 1.01f, maxDist, realization);
				double fit = dGoal.spread( dTest);
				if( fit < bestFit) {
					bestFit = fit;
					d[ i] = dd;
					done++;
				} else { 
					rf[ i] = previous;
				}
				realization = null;
				rt.gc();
			}
			System.out.println( "Goal:");
			dGoal.print();
			System.out.println( "test:");
			dTest.print();
			System.out.println( "Number of changes: " + done);
			nowDate = new Date();
			System.out.println("now: " + nowDate);
			System.out.println("");
		}
		rt.gc();
		System.out.println("Making realizations with random fields");
		for( int m = 0; m < numMaps; m++) {
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				rf[ i] = new LatticeRandomField( go, d[i], 1.0f, 
					(d[i]) / 3.0f, (long) (m + i * 1320));
				rf[i].makeUniform();
			}
			realization = test.realize( rf);
			realization.writeAsciiEsri( directory + "/noise_" + qualityMapName + 
				"_" + generalizedMapName + "_" + m);
			realization = null;
			rt.gc();
		}
		System.out.println("Done making realizations with random fields");
		nowDate = new Date();
		System.out.println("now: " + nowDate);
		System.out.println("");
		RandomMaps rM = new RandomMaps( directory + "/" + qualityMapName, 
			directory + "/noise_" + qualityMapName + "_" + generalizedMapName + "_",
				minRes * 1.01f, maxDist, numMaps);
		rt.gc();	
		rM.swapCells(  0, 0, directory + "/" + qualityMapName + "_" + 
			generalizedMapName + "_r");
		nowDate = new Date();
		System.out.println("now: " + nowDate);
		System.out.println("");
	}

	/** in alpha testing 
	 * @throws IOException */
	public ProbMap( GISClass goal, GISClass existing, String probPrefix) throws IOException {
		super();
		original = new ProbClassMaps( goal, existing);
		original.save( probPrefix);
	}

	/** in alpha testing 
	 * @throws IOException */
	public ProbMap( GISClass goal, GISClass existing, GISClass study, String probPrefix) throws IOException {
		super();
		original = new ProbClassMaps( goal, existing);
		studyArea = new ProbClassMaps( original, study);
		studyArea.save( probPrefix);
	}
}
