/** 
 * Class for attribute conditions - "AndClause" connected through "OR" operator
 * @author Yizhao Gao
 */


package logicUtil;

public class AttCondition 
{
	public AndClause[] andClause;
	
	public AttCondition(String attCondtionString)
	{
		if(attCondtionString.contains("|"))
		{
			String[] sep = attCondtionString.split("\\|");
			this.andClause = new AndClause[sep.length];
			for(int i = 0; i < sep.length; i++)
			{
				this.andClause[i] = new AndClause(sep[i]);
			}
		}
		else 
		{
			this.andClause = new AndClause[1];
			this.andClause[0] = new AndClause(attCondtionString);
		}
	}
	
	//Process hh condition
	public void processHeader(String[] hhHeader) throws Exception
	{
		for(int i = 0; i < this.andClause.length; i++)
		{
			this.andClause[i].processHeader(hhHeader);
		}
	}
	
	//Process pop condition
	public void processHeader(String[] hhHeader, String[] popHeader) throws Exception
	{
		for(int i = 0; i < this.andClause.length; i++)
		{
			this.andClause[i].processHeader(hhHeader, popHeader);
		}
	}
	
	//Check whether a household meet the condition
	public boolean meetCondition(String[] household)
	{
		for(int i = 0; i < this.andClause.length; i++)
		{
			if(this.andClause[i].meetCondition(household))
			{
				return true;
			}
		}	
		return false;
	}
	
	
	//Check whether a person meet the condition
	public boolean meetCondition(String[] household, String[] person)
	{
		for(int i = 0; i < this.andClause.length; i++)
		{
			if(this.andClause[i].meetCondition(household, person))
			{
				return true;
			}
		}
		return false;
	}
}
