/** 
 * Class for Ascii Grid File reading and accessing
 * @author Yizhao Gao
 */

package fileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class AsciiFile {
	
	File fileName;
	public int nCols;
	public int nRows;
	public double xMin;
	public double yMin;
	public double cellSize;
	public double noDataValue;
	public double[] cellValue;
	
	public AsciiFile(File inputFile) throws Exception
	{
		this.fileName = inputFile;
		this.readFile();
	}
	
	public double getCellValue(int x, int y)
	{
		if(x < 0 || x >= this.nCols || y < 0 || y >= nRows)
		{
			return Double.NaN;
		}
		else 
		{
			return this.cellValue[y * nCols + x];
		}
	}
	
	private void readFile() throws Exception 
	{
		FileReader fReader = new FileReader(this.fileName);
        BufferedReader bReader = new BufferedReader(fReader);
		
		String line;
		String[] lines;
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.nCols = Integer.parseInt(lines[1]);
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.nRows = Integer.parseInt(lines[1]);
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.xMin = Double.parseDouble(lines[1]);
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.yMin = Double.parseDouble(lines[1]);
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.cellSize = Double.parseDouble(lines[1]);
		
		line = bReader.readLine();
		lines = line.split("( |\t)+");
		this.noDataValue = Double.parseDouble(lines[1]);
		
		cellValue = new double[nRows * nCols];
		
		for(int i = 0; i < this.nRows; i++)
		{
			line = bReader.readLine();
			lines = line.split("( |\t)+");
			
			//Remove empty strings at the beginning
            int k;
            for(k = 0; k < lines.length; k++)
            {
                if((!lines[k].equals("")) && (!lines[k].equals(" ")) && (!lines[k].equals("\t")))
                    break;
            }
            for(int j = 0; j < this.nCols; j++)
            {
            	cellValue[i * this.nCols + j] = Double.parseDouble(lines[k]);
            	if(Math.abs(cellValue[i * this.nCols + j] - noDataValue) < 0.00001)
            	{
            		cellValue[i * this.nCols + j] = Double.NaN;
            	}
            	k++;
            }
		}		
		
		bReader.close();
		fReader.close();
	}

}
