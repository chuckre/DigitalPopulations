package mil.army.usace.ehlschlaeger.rgik.gene;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;

/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GAParameters extends FitnessFunction {
	private int numPop, minGens, numSamples;
	private double totalTime, ffRunTime;

	private FitnessFunction ff;

	public GAParameters() { 
		super();
		numSamples = 5;
	}

	public void setFitnessFunction( FitnessFunction testingFunction, 
			double maxSecondsFitnessFunctionRun, double minHoursOfAnalysis, 
			int minGenerations, int numPopulation) {
		numPop = numPopulation;
		minGens = minGenerations;
		ffRunTime = maxSecondsFitnessFunctionRun;
		ff = testingFunction;
		totalTime = maxSecondsFitnessFunctionRun * (numSamples * numPop * minGens + 2);
		if( totalTime / (60.0 * 60.0) < minHoursOfAnalysis) {
			totalTime = minHoursOfAnalysis * 60.0 * 60.0;
		}
	}

	public boolean isFitnessFunctionAbsolute() {
		return true;
	}

	public void initializeGAParameters( GeneticAlgorithm ga) {
		ga.setNumberPopulation( 80); 
		ga.setProbabilityCrossover( 2.50);
		ga.setProbabilityMutation( 0.01);
		//ga.setDeltaMutation( 0.00005);
		//ga.setMaximumGenerationsWithoutImprovement( Integer.MAX_VALUE);
		ga.setMaximumRunTime( totalTime);
		//ga.setMutationStressRatio( 15.0);
		ga.setMaximumMutationValue( 0.01);
		ga.setFitnessScalingRatio( 80.0);
		ga.setPrintAllBestFitness();
		ga.setPrintAllFitnessResults();
	}

	public void calculateFitnessAbsolute( Chromosome ch) {
		calculateFitness( ch);
	}

	public void calculateFitness( Chromosome c) {
		double worstFit = Double.NEGATIVE_INFINITY;
		double secondWorstFit = Double.NEGATIVE_INFINITY;
		double aveFit = 0;
		for( int i = 0; i < numSamples; i++) {
			GeneticAlgorithm ga = new GeneticAlgorithm();
			ga.setNumberPopulation( ((int) c.getParameterValue( 6))); //6
			ga.setProbabilityCrossover( c.getParameterValue( 5)); //5
			ga.setProbabilityMutation( c.getParameterValue( 2)); //2
			ga.setDeltaMutation( c.getParameterValue( 0)); //0
			ga.setMaximumRunTime( ffRunTime);
			ga.setMutationStressRatio( c.getParameterValue( 3)); //3
			ga.setMaximumMutationValue( c.getParameterValue( 1)); //1
			ga.setFitnessScalingRatio( c.getParameterValue( 4)); //4
			ga.setLowFitnessSurvivalRate( c.getParameterValue( 7)); //7
			ga.setHighFitnessSurvivalRate( c.getParameterValue( 8)); //8
			ga.initialize( ff);
			Chromosome fitC = ga.run();
			double fit = fitC.getFitness();
			//System.out.print( ga.getGenerationsRun() + "," + fit + ",");
			if( fit > secondWorstFit) {
				worstFit = secondWorstFit;
				secondWorstFit = fit;
			} else if( fit > worstFit) {
				worstFit = fit;
			}
			aveFit += fit;
		}
		aveFit /= numSamples;
		//System.out.println("");
		c.setFitness( secondWorstFit + aveFit);
	}

	public Chromosome getChromosome() {
		Chromosome c = new Chromosome();
		c.setNumberParameters( 9);
		c.setParameter( 10, 0.0, 0.001, "deltaMutation"); //0
		c.setParameter( 10, 0.0, 0.2, "maxMutationValue"); //1
		c.setParameter( 10, 0.0, 0.1, "probMutation"); //2
		c.setParameter( 16, 1.0, 100.0, "mutationStressRatio"); //3
		c.setParameter( 16, 1.0, 100.0, "fitnessScalingRatio"); //4
		c.setParameter( 10, 0.5, 4.0, "probCrossover"); //5
		c.setParameter( 20, 10.0, 400.0, "numPopulation"); //6
		c.setParameter( 10, 0.0, 0.5, "lowSurvRate"); //7
		c.setParameter( 10, 0.0, 0.9, "hihSurvRate"); //8
		c.printHeader();
		return c;
	}

	/** in alpha testing 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
		RubberSheet f = new RubberSheet ();
		f.setMaximumHorizontalShift( 800.0);
		f.setMaximumVerticalShift( 40.0);
		GISLattice g = GISLattice.loadEsriAscii("study_dem");
		f.setGoal( g);
		GISLattice d = GISLattice.loadEsriAscii("study_dted");
		f.setData( d);
		f.setResolution( -1.0);
		Chromosome c = f.getBestGAParameters( 60.0 * 2.0, 8.0, 10, 40);
		c.print();
		c.printAllele();
		System.out.println( "Final Fitness: " + c.getFitness());
	}
}
