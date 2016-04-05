package mil.army.usace.ehlschlaeger.rgik.gene;

import java.io.IOException;
import java.util.Date;

import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;



/**
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GeneticAlgorithm {
	private Date startTime;
	private FitnessFunction ff;
	private Chromosome bestFit;
	private Chromosome[] population;
	private Chromosome[] bestPopulation;
	private boolean initialized,
			printAllFits,
			printAllBestFits,
			upHC;
	private int	printAverage,
			printAverageCount,
			numPopulation, 
			num2Find,
			numGeneration, 
			generationsSinceNewBest,
			maxGenerationsWithoutImprovement,
			newChromosomes,
			populationHC,
			parameterHC;
	private double probCross, 
			probMutation, 
			deltaMutation, 
			probHillClimb, 
			deltaHillClimb, 
			mutationStressRatio,
			maxMutation,
			maxHillClimb,
			fitnessScalingRatio,
			bestFitValue,
			bestAverageFit,
			lowRawFitValue,
			hihRawFitValue,
			lowFitValue,
			hihFitValue,
			difRawFitValue,
			difAdjust,
			sumRawFitness,
			sumFitness,
			difSurvRate,
			maxMilliSeconds,
			maxHillClimbingSeconds,
			shareFactor,
			distDivider,
			exponent,
			dimensionExponent,
			lowSurvRate,
			hihSurvRate;
	private double[][] parameterLocation;
	private double[][] distMatrix;
	private double[] shareValue;

	public GeneticAlgorithm() { 
		ff = null;
		bestFit = null;
		population = null;
		numPopulation = 100; 
		numGeneration = 0; 
		upHC = false;
		populationHC = parameterHC = 0;
		generationsSinceNewBest = 0;
		maxGenerationsWithoutImprovement = Integer.MIN_VALUE;
		maxMilliSeconds = Double.NEGATIVE_INFINITY;
		maxHillClimbingSeconds = -1.0;
		probCross = 1.0; 
		probMutation = 0.0005;
		deltaMutation = 0.00001; 
		probHillClimb = 0.05;
		deltaHillClimb = 0.001;
		mutationStressRatio = 12.5;
		maxMutation = 0.001;
		maxHillClimb = 0.1;
		fitnessScalingRatio = 20.0;
		initialized = false;
		printAllBestFits = printAllFits = false;
		printAverage = -1;
		printAverageCount = 0;
		shareFactor = -1.0;
		dimensionExponent = 2.0;
		exponent = 0.5;
		num2Find = 1;
		lowSurvRate = 0.01;
		hihSurvRate = 0.50;
	}

	private boolean checkBest( Chromosome c) {
		double fit = c.getFitness();
		if( fit > bestFitValue) {
			bestFitValue = fit;
			bestFit = c;
			bestFit.setNumberMade( newChromosomes);
			generationsSinceNewBest = 0;
			if( printAllBestFits == true) {
				for( int i = 0; i < c.getNumberParameters(); i++) {
					System.out.print( c.getParameterValue( i) + ",");
				}
				System.out.println( fit);
			}
		}
		return stillTime();			
	}

	public double getAverageFitness() {
		int num = num2Find;
		if( num > population.length)
			num = population.length;
		double[] bestValues = new double[ num];
		bestValues[ 0] = Double.NEGATIVE_INFINITY;
		for( int p = population.length - 1; p >= 0; p--) {
			double fit = getFitnessValue( p);
			int i = 0;
			while( i < num && fit < bestValues[ i]) {
				i++;
			}
			while( i < num) {
				double tmp = bestValues[ i];
				bestValues[ i] = fit;
				if( tmp == Double.NEGATIVE_INFINITY) {
					if( i < num -1) {
						bestValues[ i + 1] = Double.NEGATIVE_INFINITY;
					}
					i = num;
				} else {
					i++;
					fit = tmp;
				}
			}
		}
		double sum = 0.0;
//System.out.println(" bestValues:");
		for( int j = num - 1; j >= 0; j--) {
//System.out.print( bestValues[j] + " ");
			sum += bestValues[ j];
		}
//System.out.println("");
		return( sum / num);
	}

	public double getAverageRawFitness() {
		return( sumRawFitness / population.length);
	}

	public Chromosome[] getBestPopulation() {
		int num = num2Find;
		if( num > population.length)
			num = population.length;
		Chromosome[] bp = new Chromosome[ num2Find];
		double[] bestValues = new double[ num];
		bestValues[ 0] = Double.NEGATIVE_INFINITY;
		for( int p = population.length - 1; p >= 0; p--) {
			double fit = getFitnessValue( p);
			Chromosome ch = population[ p];
			int i = 0;
			while( i < num && fit < bestValues[ i]) {
				i++;
			}
			while( i < num) {
				double tmp = bestValues[ i];
				Chromosome tmpC = bp[ i];
				bestValues[ i] = fit;
				bp[ i] = ch;
				if( tmp == Double.NEGATIVE_INFINITY) {
					if( i < num -1) {
						bestValues[ i + 1] = Double.NEGATIVE_INFINITY;
					}
					i = num;
				} else {
					i++;
					fit = tmp;
					ch = tmpC;
				}
			}
		}
		return bp;
	}

	private double getFitnessValue( int chromosome) {
		//System.out.println( "gFV start");
		if( difRawFitValue == 0.0) {
			return 1.0;
		}
		//System.out.println( "pop.length: " + population.length + ", ch: " + chromosome);
		double value = hihRawFitValue - difAdjust * 
			(hihRawFitValue - population[ chromosome].getFitness()) / difRawFitValue; 
		if( shareFactor > 0.0) {
			//System.out.println( "gFV() shouldn't happen");
			return( getSharedFitnessValue( chromosome, value));
		}
		//System.out.println( "gFV end");
		return value;
	}

	public int getGenerationsRun() {
		return numGeneration;
	}

	private double getMutationValue( int parentA, int parentB) {
		double value = probMutation + deltaMutation * numGeneration;
		if( hihFitValue == lowFitValue) {
			if( value > maxMutation)
				return maxMutation;
			return value;
		}
		if( Math.random() < 0.5) {
			value *= mutationStressRatio * 
				((getFitnessValue( parentA) - lowFitValue) / 
				 (hihFitValue - lowFitValue));
		} else {
			value *= mutationStressRatio * 
				((getFitnessValue( parentB) - lowFitValue) / 
				 (hihFitValue - lowFitValue));
		}
		if( value > maxMutation) {
			return maxMutation;
		}
		return value;
	}

	private double getMutationHillClimbValue( int parentA, int parentB) {
		double value = probHillClimb + deltaHillClimb * numGeneration;
		if( hihFitValue == lowFitValue) {
			if( value > maxHillClimb)
				return maxHillClimb;
			return value;
		}
		if( Math.random() < 0.5) {
			value *= mutationStressRatio * 
				((getFitnessValue( parentA) - lowFitValue) / 
				 (hihFitValue - lowFitValue));
		} else {
			value *= mutationStressRatio * 
				((getFitnessValue( parentB) - lowFitValue) / 
				 (hihFitValue - lowFitValue));
		}
		if( value > maxHillClimb) {
			return maxHillClimb;
		}
		return value;
	}

	private int getParent() {
		//System.out.println( "GA.gP start");
		double parentValue = Math.random() * sumFitness;
		int value = 0;
		double linearFitnessValue = getFitnessValue( 0);
		while( parentValue > linearFitnessValue) {
			parentValue -= linearFitnessValue;
			value++;
			linearFitnessValue = getFitnessValue( value);
		}
		//System.out.println( "GA.gP end");
		return value;
	}

	public Chromosome[] getPopulation() {
		return population;
	}

	private double getSharedFitnessValue( int chromosome, double value) {
		//System.out.println( "gSFV sf: " + shareFactor + ", v: " + value + ", sV[ch]: " + shareValue[ chromosome]);
		double shareInfluence = (shareFactor * value) / shareValue[ chromosome];
		value *= 1.0 - shareFactor;
		value += shareInfluence;
		//System.out.println( "gSFV value: " + value);
		return value;
	}

	private double getSurvivalRate( int chromosome) {
		if( difSurvRate == 0.0) {
			return hihSurvRate;
		}
		double ratio = (hihFitValue - getFitnessValue( chromosome)) / difSurvRate;
		double value = hihSurvRate - ratio * difSurvRate;
		return value;
	}

	private Chromosome hillClimb() {
System.out.println( "bestAverageFit: " + bestAverageFit);
System.out.println( "Start HillClimb");
		startTime = new Date();
		maxMilliSeconds = maxHillClimbingSeconds * 1000;
		population = bestPopulation;
		for( int p = population.length - 1; p >= 0; p--) {
			ff.calculateFitnessAbsolute( population[ p]);
			if( stillTime() == false) {
System.out.println( "End HillClimb");
System.out.println( "bestAverageFit: " + bestAverageFit);
				return bestFit;
			}
		}
		while( stillTime() == true) {
			doHillClimb();
		}
System.out.println( "End HillClimb");
System.out.println( "bestAverageFit: " + bestAverageFit);
		return bestFit;
	}

	private void doHillClimb() {
System.out.print( "upHC: " + upHC + ", populationHC: " + populationHC + ", parameterHC: " + parameterHC);
		if( upHC) {
			if( bestPopulation[ populationHC].isMaximumValue( parameterHC)) {
				incrementHC();
System.out.println( " no do max");
				return;
			}
		} else {
			if( bestPopulation[ populationHC].isMinimumValue( parameterHC)) {
				incrementHC();
System.out.println( " no do min");
				return;
			}
		}
		population = new Chromosome[ bestPopulation.length];
		for( int p = population.length - 1; p >= 0; p--) {
			population[ p] = bestPopulation[ p].copy();
			if( populationHC == p) {
				if( upHC) {
					population[ p].incrementParameter( parameterHC);
				} else {
					population[ p].decrementParameter( parameterHC);
				}
				newChromosomes++;
				ff.calculateFitnessAbsolute( population[ p]);
				checkBest( population[ p]);
			}
		}
		setSumLowAndHihFitness();
		double averageFit = getAverageFitness();
System.out.print( ", old: " + bestAverageFit + ", new: " + averageFit);
		if( averageFit > bestAverageFit) {
			bestAverageFit = averageFit;
			bestPopulation = population;
			System.out.println( " 1");
		} else {
			System.out.println( " 0");
		}
		if( printAverage > 0) {
			printAverageCount++;
			if( printAverageCount >= printAverage) {
				printAverageCount = 0;
				System.out.println("");
				System.out.println( newChromosomes + " " + lowRawFitValue + " " + 
					getAverageRawFitness() + " " + hihRawFitValue + " " + 
					averageFit + " " + bestAverageFit);
			}
		}
		incrementHC();
	}

	private void incrementHC() {
		parameterHC++;
		if( parameterHC == bestFit.getNumberParameters()) {
			parameterHC = 0;
			populationHC++;
			if( populationHC == bestPopulation.length) {
				if( upHC == true) {
					upHC = false;
				} else {
					upHC = true;
				}
				populationHC = 0;
			}
		}
	}

	public void initialize( FitnessFunction f) {
		if( maxGenerationsWithoutImprovement == Integer.MIN_VALUE &&
			maxMilliSeconds == Double.NEGATIVE_INFINITY) {
			System.out.println( "GeneticAlgorithm.initialize ERROR: ");
			System.out.println( "  either maximum time or minimum number of generations must be set.");
			System.exit( -1);
		}
		startTime = new Date();
		initialized = true;
		ff = f;
		population = new Chromosome[ numPopulation];
		if( population == null) {
				RAMError( "initialize");
		}
		population[ 0] = ff.getChromosome();
		if( shareFactor > 0.0) { 
			shareValue = new double[ numPopulation];
			if( shareValue == null) {
					RAMError( "initialize");
			}
			parameterLocation = new double[ population.length][ population[ 0].getNumberParameters()];
			if( parameterLocation == null) {
					RAMError( "initialize");
			}
			distMatrix = new double[ population.length][ population.length];
			if( distMatrix  == null) {
					RAMError( "initialize");
			}
			for( int p = population.length - 1; p >= 0; p--) {
				distMatrix[ p][ p] = 0.0;
			}
			distDivider = Math.sqrt( ((double) population[ 0].getNumberParameters()));
			//distDivider = (double) population[ 0].getNumberParameters();
			System.out.println( "Theory of Niche implemented");
		}
		if( printAverage > 0) {
			System.out.println( 
			"#Gen lowFitValue        getAverageRawFitness()	hihFitValue        averageFit        bestAverageFit");
		}
		if( printAllFits) {
			ff.printFitness( population[ 0]);
		} else {
			ff.calculateFitness( population[ 0]);
		}
		bestFit = population[ 0];
		bestFitValue = bestFit.getFitness();
		newChromosomes = 1;
		if( stillTime() == false)
			return;
		for( int p = 1; p < numPopulation; p++) {
			newChromosomes++;
			population[ p] = new Chromosome( population[ 0]);
			if( printAllFits) {
				ff.printFitness( population[ p]);
			} else {
				ff.calculateFitness( population[ p]);
			}
			if( false == checkBest( population[ p]))
				return;
		}
		if( lowSurvRate < hihSurvRate) {
			difSurvRate = hihSurvRate - lowSurvRate;
		} else {
			difSurvRate = 0.0;
		}
		setSumLowAndHihFitness();
		bestAverageFit = getAverageFitness();
		bestPopulation = population;
		if( printAverage > 0) {
			System.out.println( numGeneration + " " + lowRawFitValue + " " + 
				getAverageRawFitness() + " " + hihRawFitValue + " " + 
				bestAverageFit + " " + bestAverageFit);
		}
	}

	private void RAMError( String methodName) {
		System.out.println( 
		"Chromosome." + methodName + " ERROR: array not initialized,");
		System.out.println( 
		"  program needs more RAM to run or population must be smaller.");
		System.exit( -1);
	}

	public Chromosome run() {
		return run( Integer.MAX_VALUE);
	}

	public Chromosome run( int maximumGenerations) {
System.out.println( "start run");
		int currentRunGeneration = 0;
		if( stillTime() == false) {
			bestFit.setNumberMade( newChromosomes);
			//System.out.println( "run1");
			return hillClimb();
		}
		while( maxGenerationsWithoutImprovement > generationsSinceNewBest &&
				currentRunGeneration++ < maximumGenerations) {
			//System.out.println( "Generation [" + numGeneration + "] done");
			numGeneration++;
			generationsSinceNewBest++;
			Chromosome[] newPopulation = new Chromosome[ numPopulation];
			int babiesNeeded = 0;
			for( int p = numPopulation - 1; p >= 0; p--) {
				double sRate = getSurvivalRate( p);
				if( Math.random() < sRate) {
					newPopulation[ p] = population[ p].copy();
					if( ff.isFitnessFunctionAbsolute() == false) {
						if( false == checkBest( newPopulation[ p])) {
							bestFit.setNumberMade( newChromosomes);
							//System.out.println( "run2");
							return hillClimb();
						}
					}
				} else {
					babiesNeeded++;
				}
			}
			if( babiesNeeded != (babiesNeeded / 2) * 2) {
				double bestFitness = Double.NEGATIVE_INFINITY;
				int survivor = -1;
				for( int p = numPopulation - 1; p >= 0; p--) {
					if( newPopulation[ p] == null) {
						double fit = getFitnessValue( p);
						if( fit > bestFitness) {
							bestFitness = fit;
							survivor = p;
						}
					}
				}
				newPopulation[ survivor] = population[ survivor].copy();
				babiesNeeded--;
			}
			int i = 0;
			while( babiesNeeded > 0)  {
				int parentA = getParent();
				int parentB = getParent();
				while( parentA == parentB) {
					parentB = getParent();
				}
				while( newPopulation[ i] != null) {
					i++;
				}
				int childA = i;
				i++;
				while( newPopulation[ i] != null) {
					i++;
				}
				int childB = i;
				i++;
				newPopulation[ childA] = population[ parentA].copy();
				newPopulation[ childB] = population[ parentB].copy();
				double thisCrossValue = probCross - Math.random();
				while( thisCrossValue > 0.0) {
					newPopulation[ childA].cross( newPopulation[ childB]);
					thisCrossValue -= 1.0;
				}
				double mutationValue = getMutationValue( parentA, parentB);
				if( 0 == newPopulation[ childA].mutateAnyAllele( mutationValue)) {
					newPopulation[ childA].mutate( getMutationHillClimbValue( parentA, parentB));
				}
				if( 0 == newPopulation[ childB].mutateAnyAllele( mutationValue)) {
					newPopulation[ childB].mutate( getMutationHillClimbValue( parentA, parentB));;
				}
				newChromosomes += 2;
				if( printAllFits) {
					ff.printFitness( newPopulation[ childA]);
					if( false == checkBest( newPopulation[ childA])) {
						bestFit.setNumberMade( newChromosomes);
						//System.out.println( "run3");
						return hillClimb();
					}
					ff.printFitness( newPopulation[ childB]);
					if( false == checkBest( newPopulation[ childB])) {
						bestFit.setNumberMade( newChromosomes);	
						//System.out.println( "run4");
						return hillClimb();
					}
				} else {
					ff.calculateFitness( newPopulation[ childA]);
					if( false == checkBest( newPopulation[ childA])) {
						bestFit.setNumberMade( newChromosomes);	
						//System.out.println( "run5");
						return hillClimb();
					}
					ff.calculateFitness( newPopulation[ childB]);
					if( false == checkBest( newPopulation[ childB])) {
						bestFit.setNumberMade( newChromosomes);	
						//System.out.println( "run6");
						return hillClimb();
					}
				}
				babiesNeeded -= 2;
			} 
			population = newPopulation;
			setSumLowAndHihFitness();
			double averageFit = getAverageFitness();
			if( averageFit > bestAverageFit) {
				bestAverageFit = averageFit;
				bestPopulation = population;
			}
			if( printAverage > 0) {
				printAverageCount++;
				if( printAverageCount >= printAverage) {
					printAverageCount = 0;
					System.out.println( numGeneration + " " + lowRawFitValue + " " + 
						getAverageRawFitness() + " " + hihRawFitValue + " " + 
						averageFit + " " + bestAverageFit);
				}
			}
		}
		bestFit.setNumberMade( newChromosomes);
System.out.println( "maxGenerationsWithoutImprovement: " + maxGenerationsWithoutImprovement); 
System.out.println( "generationsSinceNewBest: " + generationsSinceNewBest); 
System.out.println( "currentRunGeneration: " + currentRunGeneration); 
System.out.println( "maximumGenerations: " + maximumGenerations);
		return hillClimb();
	}

	private void setSharingDistanceDecayExponent( double value) {
		if( value <= 0.0) {
			System.out.println( "GeneticAlgorithm.setSharingDistanceDecayExponent ERROR:");
			System.out.println( "   value must be greater than 0.0");
			System.exit( -1);
		}
		exponent = value;
	}

	public void setHillClimbingSeconds( double value) {
		if( value <= 0.0) {
			System.out.println( "GeneticAlgorithm.setHillClimbingSeconds ERROR:");
			System.out.println( "   value must be greater than 0.0");
			System.exit( -1);
		}
		maxHillClimbingSeconds = value;
	}

	private void setSharingDimensionExponent( double value) {
		if( value <= 0.0) {
			System.out.println( "GeneticAlgorithm.setSharingDimensionExponent ERROR:");
			System.out.println( "   value must be greater than 0.0");
			System.exit( -1);
		}
		dimensionExponent = value;
	}

	public void setLowFitnessSurvivalRate( double value) {
		if( value < 0.0 || value >= 1.0) {
			System.out.println( "GeneticAlgorithm.setLowFitnessSurvivalRate ERROR:");
			System.out.println( "   value must be 0.0 or greater and less than 1.0");
			System.exit( -1);
		}
		lowSurvRate = value;
	}

	public void setHighFitnessSurvivalRate( double value) {
		if( value < 0.0 || value >= 1.0) {
			System.out.println( "GeneticAlgorithm.setLowFitnessSurvivalRate ERROR:");
			System.out.println( "   value must be 0.0 or greater and less than 1.0");
			System.exit( -1);
		}
		hihSurvRate = value;
	}

	public void setNumberOfBest( int value) {
		if( value < 1) {
			System.out.println( "GeneticAlgorithm.setNumberOfBest ERROR: value must be 1 or greater");
			System.exit( -1);
		}
		num2Find = value;
	}

	public void setShareFactor( double value) {
		if( value > 1.0) {
			System.out.println( "GeneticAlgorithm.setShareFactor ERROR: value must be 1.0 or less");
			System.exit( -1);
		}
		shareFactor = value;
	}

	private void setSumLowAndHihFitness() {
		if( shareFactor > 0.0) {
			//System.out.println( "sSLAHF() shouldn't happen");
			setSharedFitnessInfo();
		}
		sumRawFitness = lowRawFitValue = hihRawFitValue = population[ 0].getFitness();
		for( int j = 1; j < numPopulation; j++) {
			double fit = population[ j].getFitness();
			if( fit < lowRawFitValue) {
				lowRawFitValue = fit;
			}
			if( fit > hihRawFitValue) {
				hihRawFitValue = fit;
			}
			sumRawFitness += fit;
		}
		difAdjust = hihRawFitValue - hihRawFitValue / fitnessScalingRatio;
		difRawFitValue = hihRawFitValue - lowRawFitValue;
		lowFitValue = hihFitValue = sumFitness = getFitnessValue( 0);
		for( int i = 1; i < numPopulation; i++) {
			double fit = getFitnessValue( i);
			if( fit < lowFitValue) {
				lowFitValue = fit;
			}
			if( fit > hihFitValue) {
				hihFitValue = fit;
			}
			sumFitness += fit;
		}
	}

	private void setSharedFitnessInfo() {
		for( int p = population.length - 1; p >= 0; p--) {
			for( int a = population[ 0].getNumberParameters() - 1; a >= 0; a--) {
				parameterLocation[ p][ a] = population[ p].getParameterLocation( a);
				//System.out.print( parameterLocation[ p][ a] + " ");
			}
			//System.out.println( "");
		}
		//double avedistMatrix = 0.0;
		for( int p = population.length - 1; p >= 0; p--) {
			for( int pp = p - 1; pp >= 0; pp--) {
				distMatrix[ p][ pp] = 0.0;
				for( int a = population[ 0].getNumberParameters() - 1; a >= 0; a--) {
					double pV = parameterLocation[ p][ a];
					double ppV = parameterLocation[ pp][ a];
					double pValue = pV - ppV;
					if( pValue < 0.0)
						pValue *= 1.0;
					distMatrix[ p][ pp] += Math.pow( pValue, dimensionExponent);
					//distMatrix[ p][ pp] += (pV - ppV) * (pV - ppV);
					//System.out.print( pV + "," + ppV + "," + distMatrix[ p][pp] + " ");
					//avedistMatrix += distMatrix[ pp][ p] + distMatrix[ pp][ p];
				}
				distMatrix[ p][ pp] = Math.pow( distMatrix[ p][ pp], exponent);
				//System.out.print( distMatrix[ p][ pp] + " ");
			}
			//System.out.println( " ");
		}
		//avedistMatrix /= population.length * population.length;
		for( int p = population.length - 1; p >= 0; p--) {
			shareValue[ p] = 0.0;
			for( int pp = population.length - 1; pp >= 0; pp--) {
				shareValue[ p] += 1.0 - Math.sqrt( distMatrix[ p][ pp] / distDivider);
			}
			//System.out.println( "newSV: " + shareValue[ p]);
		}
	}

	public void setNumberPopulation( int value) {
		if( value < 2) {
			System.out.println( 
			"GeneticAlgorithm.setNumberPopulation ERROR: value must be 2 or greater");
			System.exit( -1);
		}
		if( initialized == true) {
			System.out.println( "GeneticAlgorithm.setNumberPopulation ERROR: ");
			System.out.println( "  cannot change population # after initialize()");
			System.exit( -1);
		}
		numPopulation = value; 
	}

	public void setProbabilityCrossover( double value) {
		if( value < 0.0) {
			System.out.println( 
			"GeneticAlgorithm.setProbabilityCrossover ERROR: value must be 0.0 or greater");
			System.exit( -1);
		}
		probCross = value; 
	}

	public void setProbabilityMutation( double value) {
		if( value < 0.0 || value > 1.0) {
			System.out.println( 
			"GeneticAlgorithm.setProbabilityMutation ERROR: value must be 0.0 or greater");
			System.out.println( 
			"  and value must be 1.0 or smaller");
			System.exit( -1);
		}
		probMutation = value; 
	}

	public void setDeltaMutation( double value) {
		if( value < 0.0) {
			System.out.println( 
			"GeneticAlgorithm.setDeltaMutation ERROR: value must be 0.0 or greater");
			System.exit( -1);
		}
		deltaMutation = value; 
	}

	public void setProbabilityHillClimb( double value) {
		if( value < 0.0 || value > 1.0) {
			System.out.println( 
			"GeneticAlgorithm.setProbabilityMutation ERROR: value must be 0.0 or greater");
			System.out.println( 
			"  and value must be 1.0 or smaller");
			System.exit( -1);
		}
		probHillClimb = value; 
	}

	public void setDeltaHillClimb( double value) {
		if( value < 0.0) {
			System.out.println( 
			"GeneticAlgorithm.setDeltaMutation ERROR: value must be 0.0 or greater");
			System.exit( -1);
		}
		deltaHillClimb = value; 
	}

	public void setMaximumGenerationsWithoutImprovement( int value) {
		if( value < 1) {
			System.out.println( 
			"GeneticAlgorithm.setMaximumGenerationsWithoutImprovement ERROR: ");
			System.out.println( "  value must be 1 or greater");
			System.exit( -1);
		}
		maxGenerationsWithoutImprovement = value;
		maxMilliSeconds = Double.MAX_VALUE; 
	}

	public void setMaximumRunTime( double seconds) {
		if( seconds <= 0.0) {
			System.out.println( 
			"GeneticAlgorithm.setMaximumRunTime ERROR: ");
			System.out.println( "  seconds must be greater than 0.0");
			System.exit( -1);
		}
		maxGenerationsWithoutImprovement = Integer.MAX_VALUE;
		maxMilliSeconds = seconds * 1000.0; 
	}

	public void setMutationStressRatio( double value) {
		mutationStressRatio = value; 
	}

	public void setMaximumMutationValue( double value) {
		if( value < 0.0 || value > 1.0) {
			System.out.println( 
			"GeneticAlgorithm.setMaximumMutationValue ERROR: value must be 0.0 or greater");
			System.out.println( 
			"  and value must be 1.0 or smaller");
			System.exit( -1);
		}
		maxMutation = value; 
	}

	public void setFitnessScalingRatio( double value) {
		if( value <= 1.0) {
			System.out.println( 
			"GeneticAlgorithm.setFitnessScalingRatio ERROR: value must be greater than 1.0");
			System.exit( -1);
		}
		fitnessScalingRatio = value; 
	}

	public void setPrintAllBestFitness() {
		printAllBestFits = true;
	}

	public void setPrintAllFitnessResults() {
		printAllFits = true;
	}

	public void setPrintAverageFitness( int generations) {
		printAverage = generations;
	}

	private boolean stillTime() {
		Date nowTime = new Date();
		if( nowTime.getTime() - startTime.getTime() < maxMilliSeconds ) {
			return true;
		}
		return false;
	}

// 245900 easting for transect
// col 669

	/** This main tests the sharing function. 
	 * @throws IOException */
	public static void main( String argv[]) throws IOException {
	  GISLattice g = GISLattice.loadEsriAscii("study_dem");
	  int totalCells = 0;
	  for( int r = 0; r < g.getNumberRows(); r++) {
		for( int c = 0; c < g.getNumberColumns(); c++) {
			if( g.isNoData( r, c) == false)
				totalCells++;
		}
	  }
	  GISLattice d = GISLattice.loadEsriAscii("study_dted");
		int ct = 0;
		double ave = 0.0;
		for( int r = 0; r < g.getNumberRows(); r++) {
			for( int c = 0; c < g.getNumberColumns(); c++) {
				if( g.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - d.getCellValue( r, c);
					ct++;
					ave += value;
				}
			}
		}
		ave /= ct;
		double sd = 0.0;
		for( int r = 0; r < g.getNumberRows(); r++) {
			for( int c = 0; c < g.getNumberColumns(); c++) {
				if( g.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - d.getCellValue( r, c);
					sd += (value - ave) * (value - ave);
				}
			}
		}
		sd = Math.sqrt( sd / (ct - 1));
		System.out.println( "Average: " + ave + ", SD: " + sd);
		System.out.println( "");
		System.out.println( "************************************************");
	  	int cells = 100;
		RubberSheet f = new RubberSheet();
		f.setMaximumHorizontalShift( 0.001);
		f.setMaximumVerticalShift( 40.0);
		f.setCellsToCheck( cells);
		f.setGoal( g);
		f.setData( d);
		f.setResolution( -1.0);
		System.out.println( "Cells Tested: " + cells);
		Date startTime = new Date();
		System.out.println( "Start Time: " + startTime);
		Chromosome ch = f.run();
		Date endTime = new Date();
		System.out.println( "  End Time: " + endTime);
		Chromosome[] bestPop = f.getBestPopulation();
		System.out.println( "Fitness before checking all cells: " + ch.getFitness());
		f.calculateFitnessAbsolute( ch);
		System.out.println( "       Fitness checking all cells: " + ch.getFitness());
		ch.print();
		System.out.println( "Number of Chromosomes checked: " + ch.getNumberMade());

		GISLattice results = f.makeMap( ch);
		//GISLattice dif = new GISLattice( results);
		int count = 0;
		double sumSq = 0.0;
		int oriCount = 0;
		double oriSumSq = 0.0;
		double oriAbsSum = 0.0;
		double absSum = 0.0;
		double sum = 0.0;
		double minError = Double.POSITIVE_INFINITY;
		double maxError = Double.NEGATIVE_INFINITY;
		for( int r = 0; r < results.getNumberRows(); r++) {
			for( int c = 0; c < results.getNumberColumns(); c++) {
				if( results.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - results.getCellValue( r, c);
					//dif.setCellValue( r, c, value);
					count++;
					sumSq += value * value;
					if( value >= 0.0)
						absSum += value;
					else	absSum -= value;
					if( minError > value)
						minError = value;
					if( maxError < value)
						maxError = value;
				}
				if( d.isNoData( r, c) == false) {
					double value = g.getCellValue( r, c) - d.getCellValue( r, c);
					oriCount++;
					sum += value;
					oriSumSq += value * value;
					if( value >= 0.0)
						oriAbsSum += value;
					else	oriAbsSum -= value;
				}
			}
		}
		System.out.println( "Original Squared Error: " + (oriSumSq / oriCount) + 
			", Original Average Absolute Error: " + (oriAbsSum / oriCount));
		System.out.println( "Average Squared Error: " + (sumSq / count) + 
			", Absolute Average Error: " + (absSum / count));
		System.out.println( "Min Error: " + minError + ", Max Error: " + maxError);
		System.out.println( "");
		for( int cc = 0; cc < bestPop[ 0].getNumberParameters(); cc++) {
			System.out.print( bestPop[ 0].getParameterName( cc));
			for( int m = 0; m < bestPop.length; m++) {
				System.out.print( "," + bestPop[ m].getParameterValue( cc));
			}
			System.out.println( "");
		}
		System.out.println( "");
		System.out.println( "");
		GISLattice best[] = new GISLattice[ bestPop.length];
		System.out.print( "attribute");
		System.out.print( ",DTED");
		System.out.print( ",DEM");
		for( int m = 0; m < bestPop.length; m++) {
			System.out.print( ",result" + m);
		}
		System.out.println("");
		System.out.print( "AbsMeanError");
		System.out.print( "," + (oriAbsSum / oriCount));
		System.out.print( ",0.0");
		double dtedAveError = sum / oriCount;
		double dtedSDError = sd;
		for( int m = 0; m < bestPop.length; m++) {
			best[ m] = f.makeMap( bestPop[ m]);
			count = 0;
			sumSq = 0.0;
			absSum = 0.0;
			for( int r = 0; r < best[ m].getNumberRows(); r++) {
				for( int c = 0; c < best[ m].getNumberColumns(); c++) {
					if( best[ m].isNoData( r, c) == false) {
						double value = g.getCellValue( r, c) - best[ m].getCellValue( r, c);
						//dif.setCellValue( r, c, value);
						count++;
						sumSq += value * value;
						if( value >= 0.0)
							absSum += value;
						else	absSum -= value;
					}
				}
			}
			System.out.print( "," + (absSum / count));
		}
		System.out.println( "");
		System.out.print( "ave");
		System.out.print( "," + dtedAveError);
		System.out.print( ",0.0");
		for( int m = 0; m < bestPop.length; m++) {
			count = 0;
			sumSq = 0.0;
			ave = 0.0;
			for( int r = 0; r < best[ m].getNumberRows(); r++) {
				for( int c = 0; c < best[ m].getNumberColumns(); c++) {
					if( best[ m].isNoData( r, c) == false) {
						double value = g.getCellValue( r, c) - best[ m].getCellValue( r, c);
						count++;
						ave += value;
					}
				}
			}
			ave /= count;
			System.out.print( "," + ave);
		}
		System.out.println( "");
		System.out.print( "SDError");
		System.out.print( "," + dtedSDError);
		System.out.print( ",0.0");
		double overallAve = 0.0;
		double overallAveSD = 0.0;
		for( int m = 0; m < bestPop.length; m++) {
			count = 0;
			ave = 0.0;
			for( int r = 0; r < best[ m].getNumberRows(); r++) {
				for( int c = 0; c < best[ m].getNumberColumns(); c++) {
					if( results.isNoData( r, c) == false) {
						double value = g.getCellValue( r, c) - best[ m].getCellValue( r, c);
						count++;
						ave += value;
					}
				}
			}
			ave /= count;
			overallAve += ave;
			sd = 0.0;
			for( int r = 0; r < best[ m].getNumberRows(); r++) {
				for( int c = 0; c < best[ m].getNumberColumns(); c++) {
					if( best[ m].isNoData( r, c) == false) {
						double value = g.getCellValue( r, c) - best[ m].getCellValue( r, c);
						sd += (value - ave) * (value - ave);
					}
				}
			}
			sd = Math.sqrt( sd / (count - 1));
			overallAveSD += sd;
			System.out.print( "," + sd);
		}
		System.out.println( "");

		for( int r = 0; r < 570; r += 7) {
			System.out.print( g.getCellCenterNorthing( r, 669));
			System.out.print( "," + d.getCellValue( r, 669));
			System.out.print( "," + g.getCellValue( r, 669));
			for( int m = 0; m < bestPop.length; m++) {
				if( best[ m].isNoData( r, 669)) {
					System.out.print( ",0.0");
				} else {
					System.out.print( "," + best[ m].getCellValue( r, 669));
				}
			}
			System.out.println("");
		}
		System.out.println( "************************************************");
		System.out.println( "overall average: " + (overallAve / bestPop.length) + 
			", average SD: " + (overallAveSD / bestPop.length));
		System.out.println( "Easting is " + g.getCellCenterEasting( 0, 669) + "   *****end of printing******");
		for( int m = 0; m < bestPop.length; m++) {
			results.writeAsciiEsri( ("RubberSheetTest" + Integer.toString( m)));
		}
	}
}
