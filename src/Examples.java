
//examples class
public class Examples 
{
	private Example examples_head = null;
	private Example examples_tail = null;
	private String first_value = null;
	private String second_value = null;
	private int first_value_count = 0;
	private int second_value_count = 0;
	private int examples_count = 0;
	
	//constructor initializes first and second class value
	public Examples(String first_value, String second_value)
	{
		this.first_value = first_value;
		this.second_value = second_value;
	}
	
	//add example
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
		
		if(example.GetClassValue().equals(first_value))
			first_value_count++;
		else if(example.GetClassValue().equals(second_value))
			second_value_count++;
		
		examples_count++;
	}
	
	//remove example
	public Example RemoveExample(int index)
	{
		boolean found = false;
		//store halfway point of examples
		int middle  = (int)Math.floor((examples_count-1)/2);
		Example remove = null;
		
		if(index == 0)
		{
			remove = examples_head;
			
			if(examples_count != 1)
			{
				examples_head = examples_head.GetNext();
				examples_head.GetPrev().SetNext(null);
				examples_head.SetPrev(null);
			}
		}
		else if(index == examples_count-1)
		{
			remove = examples_tail;
			examples_tail = examples_tail.GetPrev();
			examples_tail.GetNext().SetPrev(null);
			examples_tail.SetNext(null);
		}
		else
		{
			//pursue from head
			if(index <= middle)
			{
				Example example_walker = examples_head;
				
				for(int i = 0; i <= middle && !found; i++)
				{
					if(i != index)
						example_walker = example_walker.GetNext();
					else
					{
						found = true;
						remove = example_walker;
					}
				}
			}
			//pursue from tail
			else
			{
				Example example_walker = examples_tail;
				
				for(int j = examples_count-1; j > middle && !found; j--)
				{
					if(j != index)
						example_walker = example_walker.GetPrev();
					else
					{
						found = true;
						remove = example_walker;
					}
				}
			}
			
			remove.GetPrev().SetNext(remove.GetNext());
			remove.GetNext().SetPrev(remove.GetPrev());
			remove.SetNext(null);
			remove.SetPrev(null);
		}

		examples_count--;
		return remove;
	}
	
	//get examples count
	public int GetExamplesCount()
	{
		return examples_count;
	}
	
	//get first example
	public Example GetExamplesHead()
	{
		return examples_head;
	}
	
	//get last example
	public Example GetExamplesTail()
	{
		return examples_tail;
	}
	
	//get first class value count
	public int GetFirstClassCount()
	{
		return first_value_count;
	}
	
	//get second class value count
	public int GetSecondClassCount()
	{
		return second_value_count;
	}
}
