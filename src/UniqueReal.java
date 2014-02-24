
//unique real class
public class UniqueReal 
{
	private double value = 0;
	private UniqueReal next = null;
	private UniqueReal prev = null;
	
	//constructor initializes unique value
	public UniqueReal(double value)
	{
		this.value = value;
	}
	
	//get value
	public double GetValue()
	{
		return value;
	}
	
	//get next
	public UniqueReal GetNext()
	{
		return next;
	}
	
	//set next
	public void SetNext(UniqueReal next)
	{
		this.next = next;
	}
	
	//get previous
	public UniqueReal GetPrev()
	{
		return prev;
	}
	
	//set previous
	public void SetPrev(UniqueReal prev)
	{
		this.prev = prev;
	}
}
