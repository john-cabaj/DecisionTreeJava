
//feature class
public class Feature 
{
	private String feature = null;
	private Feature next = null;
	private Feature prev = null;
	private Attribute attribute = null;
	
	public Feature(String feature, Attribute attribute)
	{
		this.feature = feature;
		this.attribute = attribute;
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
	
	public Attribute GetAttribute()
	{
		return attribute;
	}
	
	public void SetAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}
}
