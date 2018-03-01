/** 
 * Class for simple  condition that includes only one attributes
 * @author Yizhao Gao
 */


package logicUtil;

public class SimpleCondition 
{
	public boolean householdAtt;
	public int attributeID;
	public String attributeName;
	public boolean negative;
	public int min;
	public int max;
	
	public SimpleCondition(String sConditionString)
	{
		if(sConditionString.contains("!"))
		{
			this.negative = true;
			sConditionString = sConditionString.split("!")[1];
		}
		else 
		{
			this.negative = false;
		}
	//	System.out.println(sConditionString);
		String[] sep = sConditionString.split(":");
		this.attributeName = sep[0];
		this.min = Integer.parseInt(sep[1]);
		this.max = Integer.parseInt(sep[2]);
		
		this.attributeID = -1;
		
//		System.out.println(sConditionString + "\tMax:" + this.max + "\tMin:" + this.min);
	}
	
	public void processHeader(String[] hhHeader) throws Exception
	{
		this.attributeID = -1;
		this.householdAtt = true;
		for(int i = 0; i < hhHeader.length; i++)
		{
			if(hhHeader[i].equals(this.attributeName)) 
			{
				this.attributeID = i;
				break;
			}
		}
		if(this.attributeID == -1)
		{
			throw new Exception("Can't find the attribute name that is specified: " + this.attributeName);
		}
		
//		System.out.println("Name:" + this.attributeName + "\tID:" + this.attributeID + "\tMax:" + this.max + "\tMin:" + this.min);
	}
	
	public void processHeader(String[] hhHeader, String[] popHeader) throws Exception
	{
		this.attributeID = -1;
		for(int i = 0; i < hhHeader.length; i++)
		{
			if(hhHeader[i].equals(this.attributeName))
			{
				this.attributeID = i;
				this.householdAtt = true;
				break;
			}
		}
		if(this.attributeID == -1)
		{
			for(int i = 0; i < popHeader.length; i++)
			{
				if(popHeader[i].equals(this.attributeName))
				{
					this.attributeID = i;
					this.householdAtt = false;
					break;
				}
			}
			if(this.attributeID == -1)
			{
				throw new Exception("Can't find the attribute name that is specified: " + this.attributeName);
			}
		}	
	}
	
	// Check whether a household meet the condition
	public boolean meetCondition(String[] household) 
	{
		int value = Integer.parseInt(household[attributeID]);
		return (value >= this.min && value <= this.max);
	}

	// Check whether a person meet the condition
	public boolean meetCondition(String[] household, String[] person) 
	{
		int value;
		if(this.householdAtt)
		{
			value = Integer.parseInt(household[attributeID]);
		}
		else 
		{
			value = Integer.parseInt(person[attributeID]);
		}

		return (value >= this.min && value <= this.max);
	}
}
