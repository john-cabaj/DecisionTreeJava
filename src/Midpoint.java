
//midpoints class
public class Midpoint 
{
	private double midpoint = 0;
	private Midpoint next = null;
	private Midpoint prev = null;
	
	public Midpoint(double midpoint)
	{
		this.midpoint = midpoint;
	}
	
	public Midpoint GetNext()
	{
		return next;
	}
	
	public void SetNext(Midpoint next)
	{
		this.next = next;
	}
	
	public Midpoint GetPrev()
	{
		return prev;
	}
	
	public void SetPrev(Midpoint prev)
	{
		this.prev = prev;
	}
	
	public double Midpoint()
	{
		return midpoint;
	}
}
