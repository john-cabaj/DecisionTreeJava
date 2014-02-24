
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
	private UniqueReal unique_reals_head = null;
	private UniqueReal unique_reals_tail = null;
	private int unique_reals_count = 0;
	
	public Feature(String feature, Attribute attribute)
	{
		this.feature = feature;
		this.attribute = attribute;
	}
	
	public String Feature()
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
	
	public void AddUniqueReal(UniqueReal unique_real)
	{
		if(unique_reals_head == null)
		{
			unique_reals_head = unique_real;
			unique_reals_tail = unique_real;
		}
		else
		{
			unique_reals_tail.SetNext(unique_real);
			unique_real.SetPrev(unique_reals_tail);
			unique_reals_tail = unique_real;
		}
		
		unique_reals_count++;
	}
	
	public Midpoint GetMidpoints(String first_class_value, String second_class_value)
	{
		boolean swap = false;
		midpoints_head = null;
		midpoints_tail = null;
		midpoints_count = 0;
		unique_reals_head = null;
		unique_reals_tail = null;
		unique_reals_count = 0;
		
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
		
//		System.out.println(this.feature);
//		
//		for(int x = 0; x < real_features.length; x++)
//		{
//			System.out.println(real_features[x].GetClassValue() + " " + real_features[x].GetValue());
//		}
		
		for(RealFeature rf : real_features)
		{
			if(unique_reals_tail != null)
			{
				if(unique_reals_tail.GetValue() != rf.GetValue())
				{
					UniqueReal temp =  new UniqueReal(rf.GetValue());
					AddUniqueReal(temp);
				}
			}
			else
			{
				UniqueReal temp =  new UniqueReal(rf.GetValue());
				AddUniqueReal(temp);
			}
		}
		
		UniqueReal unique_reals_walker = unique_reals_head;
		boolean label_change = false;
		boolean found = false;
		int[] lower_bound_count = new int[2];
		int[] upper_bound_count = new int[2];
		int next_index = 0;
		
		while(unique_reals_walker.GetNext() != null)
		{
			double lower_bound = 0, upper_bound = 0;
			lower_bound = unique_reals_walker.GetValue();
			upper_bound = unique_reals_walker.GetNext().GetValue();
			
			for(int j = next_index; j < real_features.length && !found; j++)
			{
				if(real_features[j].GetValue() > upper_bound)
				{
					found = true;
					next_index = j - 1;
					if(next_index < 0)
						System.out.println("HERE");
				}
				if(real_features[j].GetValue() == lower_bound)
				{
					if(real_features[j].GetClassValue().equals(first_class_value))
						lower_bound_count[0]++;
					else if(real_features[j].GetClassValue().equals(second_class_value))
						lower_bound_count[1]++;
				}
				else if(real_features[j].GetValue() == upper_bound)
				{
					if(real_features[j].GetClassValue().equals(first_class_value))
						upper_bound_count[0]++;
					else if(real_features[j].GetClassValue().equals(second_class_value))
						upper_bound_count[1]++;
				}
			}
			
			if(lower_bound_count[0] > 0 && upper_bound_count[1] > 0)
			{					
				Midpoint temp = new Midpoint((lower_bound+upper_bound)/2);
				AddMidpoint(temp);
			}
			else if(lower_bound_count[1] > 0 && upper_bound_count[0] > 0)
			{
				Midpoint temp = new Midpoint((lower_bound+upper_bound)/2);
				AddMidpoint(temp);
			}
			
			found = false;
			unique_reals_walker = unique_reals_walker.GetNext();
		}
		
		//boolean found = false;
		
		
		
		
//		for (int j = 0; j < real_features.length - 1; j++)
//		{
//			if(real_features[j].GetValue() < real_features[j+1].GetValue())
//			{
//				for(int k = j; k >= 0 && !found; k--)
//				{
//					if(!real_features[j+1].GetClassValue().equals(real_features[k].GetClassValue()))
//					{
//						Midpoint temp = new Midpoint((real_features[j+1].GetValue()+real_features[k].GetValue())/2);
//						AddMidpoint(temp);
//						found = true;
//					}
//				}
//			}
//			
//			found = false;
//		}
		
		
		
//		for (int j = 0; j < real_features.length - 1; j++)
//		{
//			if(!real_features[j].GetClassValue().equals(real_features[j+1].GetClassValue()))
//			{
//				for(int k = j; k >= 0 && !found; k--)
//				{
//					if(real_features[j+1].GetValue() > real_features[k].GetValue())
//					{
//						Midpoint temp = new Midpoint((real_features[j+1].GetValue()+real_features[k].GetValue())/2);
//						AddMidpoint(temp);
//						found = true;
//					}
//				}
//			}
//			
//			found = false;
//		}
		
		return midpoints_head;
	}
}
