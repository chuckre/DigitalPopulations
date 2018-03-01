/**
 * This program create subsets of households and population files based on the proportion map of population.
 * Inputs: ESRI ASCII Grids of individual probability and household autocorrelation, series of input digpopRzn files, series of output digpopRzn files, popFieldName, starting Rzn ID, number of Rzns to be processed, newField"PopulationChosen", a random seed (optional).
 * Outputs: Persons file will be split into two sets: above and below the probability. Households may be in both sets if some members are above the probability and others below. 
 * The number of people per household column will be replace by the number of persons in that household that is chosen (unchosen). A new colume indicating the original number of persons in that household is created. 
 * @author Yizhao Gao
 */

package subset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import fileUtil.AsciiFile;

public class AreaPopulationSubset {

	public static void main(String[] args) throws Exception 
	{
		if(args.length < 8 || args.length > 9)
		{
			System.err.println("AreaHouseholdSubset inputProbabilityGrid inputRealizetionFileBeforeRznID outputChosenRealizetionFileBeforeRznID outputUnchosenRealizetionFileBeforeRznID personPerHHFieldName startingRznID nRzns OriginalPersonPerHHFieldName <randomSeed>");
			return;
		}
		
		AsciiFile pMap;
		pMap = new AsciiFile(new File(args[0]));
		
		String rznInFileName = args[1]; 
		String rznOutChosenFileName = args[2];
		String rznOutUnchosenFileName = args[3];
		
		File tempFile = new File(rznOutChosenFileName);
		if(!(tempFile.getParentFile().exists() && tempFile.getParentFile().isDirectory()))
		{
			tempFile.getParentFile().mkdirs();
		}
		tempFile = new File(rznOutUnchosenFileName);
		if(!(tempFile.getParentFile().exists() && tempFile.getParentFile().isDirectory()))
		{
			tempFile.getParentFile().mkdirs();
		}
		
		String popFieldName = args[4];
		
		int startingRznID = Integer.parseInt(args[5]);
		int nRzn = Integer.parseInt(args[6]);
		
		String oriPerson = args[7];
		
		Random generator;
		if(args.length == 9)
		{
			Long seed = Long.parseLong(args[8]);
			generator = new Random(seed);
		}
		else 
		{
			generator = new Random();
		}

		for(int i = 0; i < nRzn; i++)
		{
			AreaPopulationSubset areaPopulationSubset = new AreaPopulationSubset(rznInFileName + String.format("%03d", startingRznID + i), rznOutChosenFileName + String.format("%03d", startingRznID + i), rznOutUnchosenFileName + String.format("%03d", startingRznID + i), popFieldName, oriPerson, pMap);
			areaPopulationSubset.generateSubset(generator);
			areaPopulationSubset.updateFile();
		}		
	}
	
	File inputHHFile;
	File outputHHInFile;
	File outputHHOutFile;
	
	File inputPopFile;
	File outputPopInFile;
	File outputPopOutFile;
	
	File outputHHInFile_temp;
	File outputHHOutFile_temp;
	File outputPopInFile_temp;
	File outputPopOutFile_temp;
	
	AsciiFile proportionMap;
	
	String popFieldName = "PERSONS";
	String oriPop = "OriPERSONS";
	
	/* Not used 
	public AreaPopulationSubset(String input, String output, String popField, String oriPerson, AsciiFile proportion)
	{
		this.inputHHFile = new File(input + "-households.csv");
		this.outputHHInFile = new File(output + "-households-In.csv");
		this.outputHHOutFile = new File(output + "-households-Out.csv");
		
		this.inputPopFile = new File(input + "-population.csv");
		this.outputPopInFile = new File(output + "-population-In.csv");
		this.outputPopOutFile = new File(output + "-population-Out.csv");
		
		this.popFieldName = popField;
		this.oriPop = oriPerson;
				
		this.proportionMap = proportion;
	}
	*/
	
	public AreaPopulationSubset(String input, String outputChosen, String outputUnchosen, String popField, String oriPerson, AsciiFile proportion)
	{
		this.inputHHFile = new File(input + "-households.csv");
		this.outputHHInFile = new File(outputChosen + "-households.csv");
		this.outputHHOutFile = new File(outputUnchosen + "-households.csv");
		
		this.inputPopFile = new File(input + "-population.csv");
		this.outputPopInFile = new File(outputChosen + "-population.csv");
		this.outputPopOutFile = new File(outputUnchosen + "-population.csv");
		
		this.outputHHInFile_temp = new File(outputChosen + "-households.csv.tmp");;
		this.outputHHOutFile_temp = new File(outputUnchosen + "-households.csv.tmp");
		this.outputPopInFile_temp = new File(outputChosen + "-population.csv.tmp");
		this.outputPopOutFile_temp = new File(outputUnchosen + "-population.csv.tmp");
		
		this.popFieldName = popField;
		this.oriPop = oriPerson;
				
		this.proportionMap = proportion;
	}
	
	public void generateSubset(Random generator) throws Exception
	{
		FileReader fReaderHH = new FileReader(this.inputHHFile);
		FileWriter fWriterHHIn = new FileWriter(this.outputHHInFile_temp);
		FileWriter fWriterHHOut = new FileWriter(this.outputHHOutFile_temp);
		BufferedReader bReaderHH = new BufferedReader(fReaderHH);
        BufferedWriter bWriterHHIn = new BufferedWriter(fWriterHHIn);
        BufferedWriter bWriterHHOut = new BufferedWriter(fWriterHHOut);
        
        FileReader fReaderPop = new FileReader(this.inputPopFile);
		FileWriter fWriterPopIn = new FileWriter(this.outputPopInFile_temp);
		FileWriter fWriterPopOut = new FileWriter(this.outputPopOutFile_temp);
		BufferedReader bReaderPop = new BufferedReader(fReaderPop);
        BufferedWriter bWriterPopIn = new BufferedWriter(fWriterPopIn);
        BufferedWriter bWriterPopOut = new BufferedWriter(fWriterPopOut);
        
        String hhLine;
        String popLine;
        String[] lines;
        
        int persons;
        int personsChosen;
        int personColID = -1;
        
        hhLine = bReaderHH.readLine();
        bWriterHHIn.write(hhLine + "," + this.oriPop);
        bWriterHHOut.write(hhLine + "," + this.oriPop);
        
        //Find the column representing the # of persons in each household
        lines = hhLine.split(",");
        for(int i = 0; i < lines.length; i++)
        {
        	if(this.popFieldName.equals(lines[i]))
        	{
        		personColID = i;
        		break;
        	}
        }
        if(personColID == -1)
        {
        	System.err.println("Incorrect persons-per-household fieldName: " + this.popFieldName);
        	
        	bReaderHH.close();
            bWriterHHIn.close();
            bWriterHHOut.close();
            fReaderHH.close();
            fWriterHHIn.close();
            fWriterHHOut.close();
            
            bReaderPop.close();
            bWriterPopIn.close();
            bWriterPopOut.close();
            fReaderPop.close();
            fWriterPopIn.close();
            fWriterPopOut.close();
            
        	return;
        }
        
        popLine = bReaderPop.readLine();
        bWriterPopIn.write(popLine);
        bWriterPopOut.write(popLine);
        
        double x;
        double y;
        int col;
        int row;
        double xMin = this.proportionMap.xMin;
        double yMin = this.proportionMap.yMin;
        double cellSize = this.proportionMap.cellSize;
        
        while(true)
        {
        	hhLine = bReaderHH.readLine();
        	if(hhLine == null || hhLine.isEmpty())
        	{
        		break;
        	}
        	
        	lines = hhLine.split(",");
        	
        	x = Double.parseDouble(lines[0]);
        	y = Double.parseDouble(lines[1]);
        	
        	col = (int)((x - xMin) / cellSize);
        	row = (int)((y - yMin) / cellSize);
        	
        	persons = Integer.parseInt(lines[personColID]);
        	personsChosen = 0;
        	
        	for(int i = 0; i < persons; i++)
        	{
        		popLine = bReaderPop.readLine();
        		if(generator.nextDouble() < this.proportionMap.getCellValue(col, row))
        		{
        			personsChosen ++;
        			bWriterPopIn.newLine();
        			bWriterPopIn.write(popLine);
        		}
        		else
        		{
        			bWriterPopOut.newLine();
        			bWriterPopOut.write(popLine);
        		}
        	}
        	
        	if(personsChosen > 0)
        	{
        		bWriterHHIn.newLine();
        		for(int i = 0; i < personColID; i++)
        		{
        			bWriterHHIn.write(lines[i] + ",");
        		}
        		bWriterHHIn.write(personsChosen + ",");
        		for(int i = personColID + 1; i < lines.length; i++)
        		{
        			bWriterHHIn.write(lines[i] + ",");
        		}
        		
        		bWriterHHIn.write(Integer.toString(persons));
        	}
        	if(personsChosen < persons)
        	{
        		bWriterHHOut.newLine();
        		for(int i = 0; i < personColID; i++)
        		{
        			bWriterHHOut.write(lines[i] + ",");
        		}
        		bWriterHHOut.write((persons - personsChosen) + ",");
        		for(int i = personColID + 1; i < lines.length; i++)
        		{
        			bWriterHHOut.write(lines[i] + ",");
        		}
        		
        		bWriterHHOut.write(Integer.toString(persons));
        		
        	}
        }
        
        
        
        bReaderHH.close();
        bWriterHHIn.close();
        bWriterHHOut.close();
        fReaderHH.close();
        fWriterHHIn.close();
        fWriterHHOut.close();
        
        bReaderPop.close();
        bWriterPopIn.close();
        bWriterPopOut.close();
        fReaderPop.close();
        fWriterPopIn.close();
        fWriterPopOut.close();
	}
	
	public void updateFile() 
	{
		if(this.outputHHInFile.exists())
			this.outputHHInFile.delete();
		if(this.outputHHOutFile.exists())
			this.outputHHOutFile.delete();
		if(this.outputPopInFile.exists())
			this.outputPopInFile.delete();
		if(this.outputPopOutFile.exists())
			this.outputPopOutFile.delete();

		if(!this.outputHHInFile_temp.renameTo(this.outputHHInFile))
			System.err.println("Can't generate output file: " + outputHHInFile.getAbsolutePath() + "\n Probabily the file is in use.");
		if(!this.outputHHOutFile_temp.renameTo(this.outputHHOutFile))
			System.err.println("Can't generate output file: " + outputHHOutFile.getAbsolutePath() + "\n Probabily the file is in use.");
		if(!this.outputPopInFile_temp.renameTo(this.outputPopInFile))
			System.err.println("Can't generate output file: " + outputPopInFile.getAbsolutePath() + "\n Probabily the file is in use.");
		if(!this.outputPopOutFile_temp.renameTo(this.outputPopOutFile))
			System.err.println("Can't generate output file: " + outputPopOutFile.getAbsolutePath() + "\n Probabily the file is in use.");
	}

}
