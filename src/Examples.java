
//examples class
public class Examples 
{
	private Example examples_head = null;
	private Example examples_tail = null;
	private int examples_count = 0;
	
	public void AddExample(Example example)
	{
		if(examples_head == null)
		{
			examples_head = example;
			examples_tail = example;
		}
		else
		{
			examples_tail.SetNext(example);
			example.SetPrev(examples_tail);
			examples_tail = example;
		}
		
		examples_count++;
	}
	
	public int GetExamplesCount()
	{
		return examples_count;
	}
	
	public Example GetExamplesHead()
	{
		return examples_head;
	}
	
	public Example GetExamplesTail()
	{
		return examples_tail;
	}
}
