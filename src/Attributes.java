
//list of attributes
public class Attributes 
{
	private Attribute attributes_head = null;
	private Attribute attributes_tail = null;
	private int attributes_count = 0;
	
	public void AddAttribute(Attribute attribute)
	{
		if(attributes_head == null)
		{
			attributes_head = attribute;
			attributes_tail = attribute;
		}
		else
		{
			attributes_tail.SetNext(attribute);
			attribute.SetPrev(attributes_tail);
			attributes_tail = attribute;
		}
		
		attributes_count++;
	}
	
	public int GetAttributesCount()
	{
		return attributes_count;
	}
	
	public Attribute GetAttributesHead()
	{
		return attributes_head;
	}
	
	public Attribute GetAttributesTail()
	{
		return attributes_tail;
	}
	
	public void RemoveTail()
	{
		attributes_tail.GetPrev().SetNext(null);
		attributes_tail = attributes_tail.GetPrev();
		attributes_tail.SetPrev(null);
		attributes_count--;
	}
}
