
//real features class
public class RealFeature 
{
	private double value = 0;
	private String class_value = null;
	
	public RealFeature(double value, String class_value)
	{
		this.value = value;
		this.class_value = class_value;
	}
	
	public double GetValue()
	{
		return value;
	}
	
	public String GetClassValue()
	{
		return class_value;
	}
}
