package mil.army.usace.ehlschlaeger.rgik.gene;



// Chromosome.java
/** @author Chuck Ehlschlaeger
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class Chromosome {
	private boolean[] allele;
	private int parameters, totalAllele;
	private int[] startParameter;
	private int[] endParameter;
	private double[] minParameter;
	private double[] spreadParameter;
	private long[] maxAlleleValue;
	private String[] names;
	private double fitness;
	private int numMade;

	public Chromosome() {
		parameters = 0; 
		numMade = -1;
	}

	public String getParameterName( int parameter) {
		if( parameter < 0 || parameter >= parameters) {
		    throw new IndexOutOfBoundsException("Chromosome.getParameterName ERROR: parameter value [" +
		                                        parameter + "] not within range of 0-" + (parameters - 1));
		}
		return( names[parameter]);
	}

	public int getNumberAllele() {
		return totalAllele;
	}

	public boolean getAllele( int numAllele) {
		return allele[ numAllele];
	}

	public void setNumberMade( int value) {
		numMade = value;
	}

	public int getNumberMade() {
		return numMade;
	}

	public Chromosome( Chromosome existing) {
		parameters = existing.parameters;
		startParameter = existing.startParameter;
		endParameter = existing.endParameter;
		minParameter = existing.minParameter;
		spreadParameter = existing.spreadParameter;
		maxAlleleValue = existing.maxAlleleValue;
		totalAllele = existing.totalAllele;
		names = existing.names;
		allele = new boolean[ totalAllele];
		RandomizeChromosome();
		fitness = Double.NEGATIVE_INFINITY;
	}

	public Chromosome copy() {
		Chromosome c = new Chromosome();
		c.parameters = parameters;
		c.startParameter = startParameter;
		c.endParameter = endParameter;
		c.minParameter = minParameter;
		c.spreadParameter = spreadParameter;
		c.maxAlleleValue = maxAlleleValue;
		c.totalAllele = totalAllele;
		c.names = names;
		c.allele = new boolean[ totalAllele];
		for( int i = 0; i < totalAllele; i++) {
			c.allele[ i] = allele[ i];
		}
		c.fitness = fitness;
		return c;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness( double value) {
		fitness = value;
	}

	public void setNumberParameters( int value) {
		if( value < 1) {
		    throw new IllegalArgumentException("Chromosome.setNumberParameters ERROR: value must be greater than 0");
		}
		startParameter = new int[ value];
		startParameter[ 0] = 0;
		endParameter = new int[ value];
		endParameter[ value - 1] = -1;
		minParameter = new double[ value];
		spreadParameter = new double[ value];
		maxAlleleValue = new long[ value];
		names = new String[ value];
		for( int i = 0; i < value; i++) {
			names[ i] = "parameter" + (Integer.toString( i));
		}
	}

	public int getNumberParameters() {
		return parameters;
	}

	public void setParameter( int numAllele, double minValue, double maxValue, String name) {
		if( parameters == startParameter.length) {
		    throw new IllegalStateException("Chromosome.setParameter ERROR: all parameters have already been set.");
		}
		names[ parameters] = name;
		setParameter( numAllele, minValue, maxValue);
	}

	public void setParameter( int numAllele, double minValue, double maxValue) {
		if( numAllele < 1 || numAllele >= 64) {
		    throw new IllegalArgumentException("numAllele must be greater than 0 and must be less than 64");
		}
		if( parameters == startParameter.length) {
            throw new IllegalStateException("All parameters have already been set.");
		}
		totalAllele += numAllele;
		endParameter[ parameters] = startParameter[ parameters] + numAllele - 1;
		maxAlleleValue[ parameters] = 0;
		for( int i = startParameter[ parameters]; i <= endParameter[ parameters]; i++) {
			maxAlleleValue[ parameters] = (maxAlleleValue[ parameters] << 1) + 1;
		}
		minParameter[ parameters] = minValue;
		spreadParameter[ parameters] = maxValue - minValue;
		parameters++;
		if( parameters < spreadParameter.length) {
			startParameter[ parameters] = endParameter[ parameters - 1] + 1;
		} else {
			allele = new boolean[ totalAllele];
			RandomizeChromosome();
			fitness = Double.NEGATIVE_INFINITY;
		}
	}

	private void RandomizeChromosome() {
		for( int i = 0; i < allele.length; i++) { 
			if( Math.random() < 0.5) {
				allele[ i] = false;
			} else {
				allele[ i] = true;
			}
		}
	}

	public double getParameterValue( int parameter) {
		double value = minParameter[ parameter] +
		  getParameterLocation( parameter) * spreadParameter[ parameter];
		return value;
	}

	public boolean isMaximumValue( int parameter) {
		for( int i = startParameter[ parameter]; i <= endParameter[ parameter]; i++) {
			if( allele[ i] == false) {
				return false;
			} 
		}
		return true;
	}

	public boolean isMinimumValue( int parameter) {
		for( int i = startParameter[ parameter]; i <= endParameter[ parameter]; i++) {
			if( allele[ i] == true) {
				return false;
			} 
		}
		return true;
	}

	public void incrementParameter( int parameter) {
		for( int i = endParameter[ parameter]; i >= startParameter[ parameter]; i--) {
			if( allele[ i] == true) {
				allele[ i] = false;
			} else {
				allele[ i] = true;
				i = -1;
			}
		}
	}

	public void decrementParameter( int parameter) {
		for( int i = endParameter[ parameter]; i >= startParameter[ parameter]; i--) {
			if( allele[ i] == false) {
				allele[ i] = true;
			} else {
				allele[ i] = false;
				i = -1;
			}
		}
	}

	public double getParameterLocation( int parameter) {
		long alleleSum = 0;
		for( int i = startParameter[ parameter]; i <= endParameter[ parameter]; i++) {
			if( allele[ i]) {
				alleleSum = (alleleSum << 1) + 1;
			} else {
				alleleSum = alleleSum << 1;
			}
		}
		return( (1.0 * alleleSum) / maxAlleleValue[ parameter]);
	}

	/**
	 * Swaps a random subset of alleles between chromosomes.
	 * @param other
	 * @return index-1 of first allele swapped
	 */
	public int cross( Chromosome other) {
		int crossLocation = (int) (Math.random() * (totalAllele - 1));
		//System.out.println( "crossLocation: " + crossLocation);
		for( int i = crossLocation + 1; i < totalAllele; i++) {
			boolean tmp = other.allele[ i];
			other.allele[ i] = allele[ i];
			allele[ i] = tmp;
		}
		return crossLocation;
	}

	public int mutate( double mutationProbability) {
		int numberMutations = 0;
		for( int i = 0; i < parameters; i++) {
			if( Math.random() < mutationProbability) {
				numberMutations++;
				if( Math.random() < 0.5) {
					incrementParameter( i);
				} else {
					decrementParameter( i);
				}
			}
		}
		return numberMutations;
	}

	public int mutateAnyAllele( double mutationProbability) {
		int numberMutations = 0;
		for( int i = 0; i < totalAllele; i++) {
			if( Math.random() < mutationProbability) {
				numberMutations++;
				if( allele[ i] == true) {
					allele[ i] = false;
				} else {
					allele[ i] = true;
				}
			}
		}
		return numberMutations;
	}

	public void printHeader() {
		for( int i = 0; i < parameters; i++) {
			System.out.print( names[ i] + ",");
		}
		System.out.println( "fitness");
	}

	public void printAllele() {
		for( int parameter = 0; parameter < parameters; parameter++) {
			for( int i = startParameter[ parameter]; i <= endParameter[ parameter]; i++) {
				if( allele[ i]) {
					System.out.print( "1");
				} else {
					System.out.print( "0");
				} // end of if
			} // end of i loop
			System.out.println( "");
		} // end of parameter loop
	}

	public void print() {
		for( int i = 0; i < parameters; i++) {
			System.out.print( i + "," + (endParameter[i] - startParameter[i] + 1));
			System.out.print( "," + getParameterValue( i) + ",");
			System.out.println( names[ i]);
		}
	}
}
