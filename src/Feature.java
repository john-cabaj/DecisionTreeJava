
//feature class
public class Feature 
{
	private String feature = null;
	private Feature next = null;
	private Feature prev = null;
	private Attribute attribute = null;
	private RealFeature[] real_features = null;
	private Midpoint midpoints_head = null;
	private Midpoint midpoints_tail = null;
	private int midpoints_count = 0;
	private int real_features_count = 0;
	private Midpoint midpoint = null;
	
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
	
	public void InitializeRealFeatures(int count)
	{
		real_features = new RealFeature[count];
	}
	
	public void AddRealFeature(RealFeature real_feature, int index)
	{
		real_features[index] = real_feature;
		real_features_count++;
	}
	
	public int GetMidpointsCount()
	{
		return midpoints_count;
	}
	
	public Midpoint GetMidpoint()
	{
		return midpoint;
	}
	
	public void SetMidpoint(Midpoint midpoint)
	{
		this.midpoint = midpoint;
	}
	
	public void AddMidpoint(Midpoint midpoint)
	{
		if(midpoints_head == null)
		{
			midpoints_head = midpoint;
			midpoints_tail = midpoint;
		}
		else
		{
			midpoints_tail.SetNext(midpoint);
			midpoint.SetPrev(midpoints_tail);
			midpoints_tail = midpoint;
		}
		
		midpoints_count++;
	}
	
	public Midpoint GetMidpoints()
	{
		boolean swap = false;
		midpoints_head = null;
		midpoints_tail = null;
		midpoints_count = 0;
		
		do
		{
			swap = false;
			for(int i = 1; i < real_features.length; i++)
			{
				if(real_features[i-1].GetValue() > real_features[i].GetValue())
				{
					RealFeature temp = real_features[i-1];
					real_features[i-1] = real_features[i];
					real_features[i] = temp;
					swap = true;
				}
			}
			
		}while(swap);
		
		
//		for(int j = 0; j < real_features.length - 1; j++)
//		{
//			if(!real_features[j].GetClassValue().equals(real_features[j+1].GetClassValue()))
//			{
//				if(real_features[j].GetValue() != real_features[j+1].GetValue())
//				{
//					Midpoint temp = new Midpoint((real_features[j].GetValue()+real_features[j+1].GetValue())/2);
//					AddMidpoint(temp);
//				}
//			}
//		}
		boolean found = false;
		
		for (int j = 0; j < real_features.length - 1; j++)
		{
			if(!real_features[j].GetClassValue().equals(real_features[j+1].GetClassValue()))
			{
				for(int k = j; k >= 0 && !found; k--)
				{
					if(real_features[j+1].GetValue() > real_features[k].GetValue())
					{
						Midpoint temp = new Midpoint((real_features[j+1].GetValue()+real_features[k].GetValue())/2);
						AddMidpoint(temp);
						found = true;
					}
				}
			}
			
			found = false;
		}
		
		return midpoints_head;
	}
}
