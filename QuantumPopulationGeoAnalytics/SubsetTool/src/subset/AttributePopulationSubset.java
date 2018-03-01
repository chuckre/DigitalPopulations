/**
 * This program create subsets of households and population files based on the user provided logic for population (which could include household attributes)
 * Inputs: Series of input digpopRzn files, series of output digpopRzn files, popFieldName, starting Rzn ID, number of Rzns to be processed, logic.
 * Outputs: People will be split into two sets: above and below the probability. Households may be in both sets if some members are above the probability and others below. 
 * The number of people per household column will be replace by the number of persons in that household that is chosen (unchosen). A new colume indicating the original number of persons in that household is created. 
 * @author Yizhao Gao
 */

package subset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import logicUtil.AttCondition;

public class AttributePopulationSubset {

	public static void main(String[] args) throws Exception 
	{
		if(args.length != 8)
		{
			System.err.println("AttributeHouseholdSubset inputRealizetionFileBeforeRznID outputChosenRealizetionFileBeforeRznID outputUnchosenRealizetionFileBeforeRznID popFieldName startingRznID nRzns logicCondition OriginalPersonPerHHFieldName");
			return;
		}
		
		String rznInFileName = args[0]; 
		String rznOutChosenFileName = args[1];
		String rznOutUnchosenFileName = args[2];
		String popFieldName = args[3];
		
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
		
		int startingRznID = Integer.parseInt(args[4]);
		int nRzn = Integer.parseInt(args[5]);
		
		String attCondtionString = args[6];
		
		String oriPerson = args[7];
		
		AttCondition attCondition = new AttCondition(attCondtionString);
		
		for(int i = 0; i < nRzn; i++)
		{
			AttributePopulationSubset attributePopulationSubset = new AttributePopulationSubset(rznInFileName + String.format("%03d", startingRznID + i), rznOutChosenFileName + String.format("%03d", startingRznID + i), rznOutUnchosenFileName + String.format("%03d", startingRznID + i), popFieldName, oriPerson, attCondition);
			attributePopulationSubset.generateSubset();
			attributePopulationSubset.updateFile();
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
	
	String populationFieldName = "PERSONS";
	String oriPop = "OriPERSONS";
	
	AttCondition condition;
	
	/* Not used
	public AttributePopulationSubset(String input, String output, String popField, String oriPerson, AttCondition attCondition)
	{
		this.inputHHFile = new File(input + "-households.csv");
		this.outputHHInFile = new File(output + "-households-In.csv");
		this.outputHHOutFile = new File(output + "-households-Out.csv");
		
		this.inputPopFile = new File(input + "-population.csv");
		this.outputPopInFile = new File(output + "-population-In.csv");
		this.outputPopOutFile = new File(output + "-population-Out.csv");
		
		this.populationFieldName = popField;
		this.oriPop = oriPerson;
		
		this.condition = attCondition;
	}
	*/
	
	public AttributePopulationSubset(String input, String outputChosen, String outputUnchosen, String popField, String oriPerson, AttCondition attCondition)
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
		
		this.populationFieldName = popField;
		this.oriPop = oriPerson;
		
		this.condition = attCondition;
	}
	
	public void generateSubset() throws Exception
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
        String[] hhLines;
        String[] popLines;
        
        int persons;
        int personsChosen;
        int personColID = -1;
        
        hhLine = bReaderHH.readLine();
        bWriterHHIn.write(hhLine + "," + this.oriPop);
        bWriterHHOut.write(hhLine + "," + this.oriPop);
        
        //Find the column representing the # of persons in each household
        hhLines = hhLine.split(",");
        for(int i = 0; i < hhLines.length; i++)
        {
        	if(this.populationFieldName.equals(hhLines[i]))
        	{
        		personColID = i;
        		break;
        	}
        }
        if(personColID == -1)
        {
        	System.err.println("Incorrect persons-per-household fieldName: " + this.populationFieldName);
        	
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
        
        popLines = popLine.split(",");
        
        //Process the conditions (e.g. Match column name, Find columnID)
        this.condition.processHeader(hhLines, popLines);
        
        while(true)
        {
        	hhLine = bReaderHH.readLine();
        	if(hhLine == null || hhLine.isEmpty())
        	{
        		break;
        	}
        	
        	hhLines = hhLine.split(",");
        	
        	persons = Integer.parseInt(hhLines[personColID]);
        	personsChosen = 0;
        	
        	for(int i = 0; i < persons; i++)
        	{
        		popLine = bReaderPop.readLine();
        		popLines = popLine.split(",");
        		
        		if(this.condition.meetCondition(hhLines, popLines))
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
        			bWriterHHIn.write(hhLines[i] + ",");
        		}
        		bWriterHHIn.write(personsChosen + ",");
        		for(int i = personColID + 1; i < hhLines.length; i++)
        		{
        			bWriterHHIn.write(hhLines[i] + ",");
        		}
        		
        		bWriterHHIn.write(Integer.toString(persons));
        	}
        	if(personsChosen < persons)
        	{
        		bWriterHHOut.newLine();
        		for(int i = 0; i < personColID; i++)
        		{
        			bWriterHHOut.write(hhLines[i] + ",");
        		}
        		bWriterHHOut.write((persons - personsChosen) + ",");
        		for(int i = personColID + 1; i < hhLines.length; i++)
        		{
        			bWriterHHOut.write(hhLines[i] + ",");
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
