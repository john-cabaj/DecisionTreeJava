
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
	
	//constructor sets feature name and attribute
	public Feature(String feature, Attribute attribute)
	{
		this.feature = feature;
		this.attribute = attribute;
	}
	
	//get feature
	public String GetFeature()
	{
		return feature;
	}
	
	//get next
	public Feature GetNext()
	{
		return next;
	}
	 
	//set next
	public void SetNext(Feature feature)
	{
		next = feature;
	}
	
	//get previous
	public Feature GetPrev()
	{
		return prev;
	}
	
	//set previous
	public void SetPrev(Feature feature)
	{
		prev = feature;
	}
	
	//get attribute
	public Attribute GetAttribute()
	{
		return attribute;
	}
	
	//set attribute
	public void SetAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}
	
	//initialize real features list
	public void InitializeRealFeatures(int count)
	{
		real_features = new RealFeature[count];
	}
	
	//add a real feature
	public void AddRealFeature(RealFeature real_feature, int index)
	{
		real_features[index] = real_feature;
		real_features_count++;
	}
	
	//get number of midpoints
	public int GetMidpointsCount()
	{
		return midpoints_count;
	}
	
	//get midpoint
	public Midpoint GetMidpoint()
	{
		return midpoint;
	}
	
	public void SetMidpoint(Midpoint midpoint)
	{
		this.midpoint = midpoint;
	}
	
	//add midpoint
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
	
	//add unique real
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
	
	//get the midpoints
	public Midpoint GetMidpoints(String first_class_value, String second_class_value)
	{
		boolean swap = false;
		midpoints_head = null;
		midpoints_tail = null;
		midpoints_count = 0;
		unique_reals_head = null;
		unique_reals_tail = null;
		unique_reals_count = 0;
		
		//sort midpoints
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
				
		//get unique sorted values
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
		
		//iterate over range to find value changes
		UniqueReal unique_reals_walker = unique_reals_head;
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
		
		//return midpoints
		return midpoints_head;
	}
}
