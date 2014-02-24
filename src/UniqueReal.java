
//unique real class
public class UniqueReal 
{
	private double value = 0;
	private UniqueReal next = null;
	private UniqueReal prev = null;
	
	public UniqueReal(double value)
	{
		this.value = value;
	}
	
	public double GetValue()
	{
		return value;
	}
	
	public UniqueReal GetNext()
	{
		return next;
	}
	
	public void SetNext(UniqueReal next)
	{
		this.next = next;
	}
	
	public UniqueReal GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(UniqueReal prev)
	{
		this.prev = prev;
	}
}
