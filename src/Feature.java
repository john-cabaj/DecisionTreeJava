
//feature class
public class Feature 
{
	private String feature = null;
	private Feature next = null;
	private Feature prev = null;
	
	public Feature(String feature)
	{
		this.feature = feature;
	}
	
	public String GetFeature()
	{
		return feature;
	}
	
	public Feature GetNext()
	{
		return next;
	}
	
	public void SetNext(Feature feature)
	{
		next = feature;
	}
	
	public Feature GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(Feature feature)
	{
		prev = feature;
	}
}
