
//real features class
public class RealFeature 
{
	private double value = 0;
	private String class_value = null;
	
	//constructor sets real feature value and class label
	public RealFeature(double value, String class_value)
	{
		this.value = value;
		this.class_value = class_value;
	}
	
	//get value
	public double GetValue()
	{
		return value;
	}
	
	//get class label
	public String GetClassValue()
	{
		return class_value;
	}
}
