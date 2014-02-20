
//value class
public class Value 
{
	private String value = null;
	private Value next = null;
	private Value prev = null;
	
	public Value(String value)
	{
		this.value = value;
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
}
