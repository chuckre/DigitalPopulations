package mil.army.usace.ehlschlaeger.rgik.gene;


/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public abstract class FitnessFunction {
	GeneticAlgorithm ga;

	public FitnessFunction() { 
	}

	public abstract void calculateFitness( Chromosome ch);

	public abstract Chromosome getChromosome();

	public abstract boolean isFitnessFunctionAbsolute();

	public abstract void calculateFitnessAbsolute( Chromosome ch);

	public abstract void initializeGAParameters( GeneticAlgorithm ga);

	public void printFitness( Chromosome ch) {
		for( int i = 0; i < ch.getNumberParameters(); i++) {
			System.out.print( ch.getParameterValue( i) + ",");
		}
		calculateFitness( ch);
		double fitness = ch.getFitness();
		System.out.println( fitness);
	}

	public Chromosome run() {
		ga = new GeneticAlgorithm();
		initializeGAParameters( ga);
		ga.initialize( this);
		Chromosome c = ga.run();
		return c;
	}

	public Chromosome[] getPopulation() {
		return ga.getPopulation();
	}

	public Chromosome[] getBestPopulation() {
		return ga.getBestPopulation();
	}

	
	/**
	 * 
	 * @param maxSecondsFitnessFunctionRun how many seconds to run each FitnessFunction
	 * @param minHoursOfAnalysis
	 * @param minGenerations
	 * @param numPopulation
	 * @return
	 */
	public Chromosome getBestGAParameters( double maxSecondsFitnessFunctionRun,
			double minHoursOfAnalysis, int minGenerations, int numPopulation) {
		if( maxSecondsFitnessFunctionRun <= 0.0) {
		    throw new IllegalArgumentException("maxSecondsFitnessFunctionRun must be greater than 0.0");
		}
		if( minGenerations < 1) {
		    throw new IllegalArgumentException("minGenerations must be greater than 0");
		}
		if( numPopulation <= 1) {
		    throw new IllegalArgumentException("numPopulation must be greater than 1");
		}
		GAParameters gap = new GAParameters();
		gap.setFitnessFunction( this, maxSecondsFitnessFunctionRun, minHoursOfAnalysis,
			minGenerations, numPopulation);
		Chromosome ch = gap.run();  
		System.out.println( "Number of Chromosomes made: " + ch.getNumberMade());
		ch.print();
		ch.printAllele();
		System.out.println( "Final Fitness: " + ch.getFitness());
		return ch;
	}
}