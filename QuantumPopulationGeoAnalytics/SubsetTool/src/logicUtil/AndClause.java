/** 
 * Class for "And Clause" - simple conditions connected through "AND" logic
 * @author Yizhao Gao
 */

package logicUtil;

public class AndClause 
{
	public SimpleCondition[] sConditions;
	
	public AndClause(String aConditionString)
	{
		if(aConditionString.contains("&"))
		{
			String[] sep = aConditionString.split("&");
			this.sConditions = new SimpleCondition[sep.length];
			for(int i = 0; i < sep.length; i++)
			{
				this.sConditions[i] = new SimpleCondition(sep[i]);
			}
		}
		else 
		{
			this.sConditions = new SimpleCondition[1];
			this.sConditions[0] = new SimpleCondition(aConditionString);
		}
	}
	
	public void processHeader(String[] hhHeader) throws Exception
	{
		for(int i = 0; i < this.sConditions.length; i++)
		{
			this.sConditions[i].processHeader(hhHeader);
		}
	}
	
	public void processHeader(String[] hhHeader, String[] popHeader) throws Exception
	{
		for(int i = 0; i < this.sConditions.length; i++)
		{
			this.sConditions[i].processHeader(hhHeader, popHeader);
		}
	}
	
	// Check whether a household meet the condition
	public boolean meetCondition(String[] household) 
	{
		for(int i = 0; i < this.sConditions.length; i++)
		{
			if(!this.sConditions[i].meetCondition(household))
			{
				return false;
			}
		}
		return true;
	}

	// Check whether a person meet the condition
	public boolean meetCondition(String[] household, String[] person) 
	{
		for(int i = 0; i < this.sConditions.length; i++)
		{
			if(!this.sConditions[i].meetCondition(household, person))
			{
				return false;
			}
		}
		return true;
	}
	
}
