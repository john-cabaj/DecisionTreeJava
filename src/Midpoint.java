
//midpoints class
public class Midpoint 
{
	private double midpoint = 0;
	private Midpoint next = null;
	private Midpoint prev = null;
	
	//constructor initializes midpoint value
	public Midpoint(double midpoint)
	{
		this.midpoint = midpoint;
	}
	
	//get next
	public Midpoint GetNext()
	{
		return next;
	}
	
	//set next
	public void SetNext(Midpoint next)
	{
		this.next = next;
	}
	
	//get previous
	public Midpoint GetPrev()
	{
		return prev;
	}
	
	//set previous
	public void SetPrev(Midpoint prev)
	{
		this.prev = prev;
	}
	
	//get midpoint value
	public double MidpointValue()
	{
		return midpoint;
	}
}
