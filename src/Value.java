
//value class
public class Value 
{
	private Attribute attribute = null;
	private String value = null;
	private Value next = null;
	private Value prev = null;
	
	public Value(String value, Attribute attribute)
	{
		this.value = value;
		this.attribute = attribute;
	}
	
	public String GetValue()
	{
		return value;
	}
	
	public Value GetNext()
	{
		return next;
	}
	
	public void SetNext(Value next)
	{
		this.next = next;
	}
	
	public Value GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(Value prev)
	{
		this.prev = prev;
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
