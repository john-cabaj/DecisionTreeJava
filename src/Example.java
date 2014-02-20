
//example class
public class Example 
{
	private Value values_head = null;
	private Value values_tail = null;
	private Example next = null;
	private Example prev = null;
	private String class_value = null;
	private int values_count = 0;
	
	public void AddValues(String value)
	{
		Value temp = new Value(value);
		if(values_head == null)
		{
			values_head = temp;
			values_tail = temp;
		}
		else
		{
			values_tail.SetNext(temp);
			temp.SetPrev(values_tail);
			values_tail = temp;
		}
		
		values_count++;
	}
	
	public int GetValuesCount()
	{
		return values_count;
	}
	
	public Value GetValuesHead()
	{
		return values_head;
	}
	
	public Value GetValuesTail()
	{
		return values_tail;
	}
	
	public Example GetNext()
	{
		return next;
	}
	
	public void SetNext(Example example)
	{
		next = example;
	}
	
	public Example GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(Example example)
	{
		prev = example;
	}
	
	public String GetClassValue()
	{
		return class_value;
	}
	
	public void SetClassValue(String class_value)
	{
		this.class_value = class_value;
	}
}
