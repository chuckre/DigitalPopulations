package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.LatticeRandomField;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
/**
 * TestProbMap class creates probability maps for multinominal conflation.
 */
public class TestProbMap {
	ProbClassMaps original, studyArea;

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		Runtime rt = Runtime.getRuntime();
		rt.traceMethodCalls( false);
		rt.traceInstructions( false);
		GISClass go = GISClass.loadEsriAscii("dataProbMap/depthBedrock160");
		rt.gc();
		GISClass ex = GISClass.loadEsriAscii("dataProbMap/depthBedrock320");
		ProbClassMaps test = new ProbClassMaps( go, ex);
		test.save( "dataProbMap/prob320_160cat");
		int numMaps = 10;
		int done = 1;
		float d[] = new float[ test.getMaximumClass() - test.getMinimumClass()];
		DensoStat dGoal = new DensoStat( test.getMinimumClass(), test.getMaximumClass(), 200.0f, 500.0f, go);
		dGoal.setDecayExponent( 0.0f);
		dGoal.print( "goal");
		LatticeRandomField rf[] = new LatticeRandomField[ test.getMaximumClass() - test.getMinimumClass() + 1];
		for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
			d[ i] = 100.0f;
			rf[ i] = new LatticeRandomField( go, d[ i], 1.0f, d[ i] / 3.0f, (long) (i + 320));
			rf[i].makeUniform();
		}
		GISClass realization = test.realize( rf);
System.out.println( "test.getMinimumClass(), test.getMaximumClass()" + test.getMinimumClass() + " " + test.getMaximumClass());
		DensoStat dTest = new DensoStat( test.getMinimumClass(), test.getMaximumClass(),
			200.0f, 500.0f, realization);
		float dd = 100.f;
		double bestFit = dGoal.spread( dTest);
		System.out.println( "Goal:");
		dGoal.print();
		System.out.println( "Noise:");
		dTest.print();
		System.out.println("");
		while( done > 0) {
			System.out.print( "Best fit: " + bestFit + ", with:");
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				System.out.print( " " + d[i]);
				rf[i].writeAsciiEsri( "dataProbMap/rf" + i);
			}
			System.out.println( "");
			done = 0;
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				LatticeRandomField previous = rf[ i];
				rf[ i] = new LatticeRandomField( go, d[i] + dd, 1.0f,
					(d[i] + dd) / 3.0f, (long) (i + 320));
				rf[i].makeUniform();
				realization = test.realize( rf);
				dTest = new DensoStat( test.getMinimumClass(), test.getMaximumClass(),
					200.0f, 500.0f, realization);
				double fit = dGoal.spread( dTest);
				if( fit < bestFit) {
					bestFit = fit;
					d[ i] += dd;
					//done++;
				} else {
					rf[ i] = previous;
				}
				realization.writeAsciiEsri( "dataProbMap/realization");
				realization = null;
				rt.gc();
			}
			System.out.println( "Goal:");
			dGoal.print();
			System.out.println( "test:");
			dTest.print();
			System.out.println("");
			System.out.println( "Number of changes: " + done);
		}
		rt.gc();
		for( int m = 0; m < numMaps; m++) {
			for( int i = 0; i < test.getMaximumClass() - test.getMinimumClass(); i++) {
				rf[ i] = new LatticeRandomField( go, d[i], 1.0f, (d[i]) / 3.0f, (long) (m + i * 320));
				rf[i].makeUniform();
			}
			realization = test.realize( rf);
			realization.writeAsciiEsri( "dataProbMap/noise" + m);
			realization = null;
			rt.gc();
		}
		RandomMaps rM = new RandomMaps( "dataProbMap/depthBedrock160", "dataProbMap/noise",
			200.0f, 500.0f, numMaps);
		rt.gc();
		rM.swapCells(  0, 0, "dataProbMap/bD320_160r");
	}

	/** in alpha testing 
	 * @throws IOException */
	public TestProbMap( GISClass goal, GISClass existing, String probPrefix) throws IOException {
		original = new ProbClassMaps( goal, existing);
		original.save( probPrefix);
	}

	/** in alpha testing 
	 * @throws IOException */
	public TestProbMap( GISClass goal, GISClass existing, GISClass study, String probPrefix) throws IOException {
		original = new ProbClassMaps( goal, existing);
		studyArea = new ProbClassMaps( original, study);
		studyArea.save( probPrefix);
	}
}
