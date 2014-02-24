
//individual attribute
public class Attribute 
{
	private Attribute next = null;
	private Attribute prev = null;
	private String attribute = null;
	private Feature features_head = null;
	private Feature features_tail = null;
	private int feature_count = 0;
	
	public Attribute(String attribute)
	{
		this.attribute = attribute;
	}
	
	public void AddFeatures(String feature)
	{
		Feature temp = new Feature(feature, this);
		if(features_head == null)
		{
			features_head = temp;
			features_tail = temp;
		}
		else
		{
			features_tail.SetNext(temp);
			temp.SetPrev(features_tail);
			features_tail = temp;
		}
		
		feature_count++;
	}
	
	public String AttributeName()
	{
		return attribute;
	}
	
	public int GetFeatureCount()
	{
		return feature_count;
	}
	
	public Attribute GetNext()
	{
		return next;
	}
	
	public void SetNext(Attribute attribute)
	{
		next = attribute;
	}
	
	public Attribute GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(Attribute attribute)
	{
		prev = attribute;
	}
	
	public Feature GetFeaturesHead()
	{
		return features_head;
	}
	
	public Feature GetFeaturesTail()
	{
		return features_tail;
	}
	
	public void CopyAttribute(Attribute attribute)
	{
		features_head = attribute.features_head;
		features_tail = attribute.features_tail;
		feature_count = attribute.feature_count;
	}
}
