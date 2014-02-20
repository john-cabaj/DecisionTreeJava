
//decision tree class
public class DecisionTree 
{

	//main method
	public static void main(String[] args) 
	{
		ARFF parser = new ARFF("heart_train.arff", ARFF.Type.TRAINING);
		parser.ParseFile();
		
		TreeNode root = new TreeNode();
		Examples examples = parser.GetExamples();
		Attributes attributes = parser.GetAttributes();
		String first_class_value = parser.GetFirstClassValue();
		String second_class_value = parser.GetSecondClassValue();
		
		Example example_walker = examples.GetExamplesHead();
		int first_class_value_count = 0, second_class_value_count = 0, count = 0;
		while(example_walker != null)
		{
			if(example_walker.GetClassValue().equals(first_class_value))
				first_class_value_count++;
			else if(example_walker.GetClassValue().equals(second_class_value))
				second_class_value_count++;
			
			count++;
		}
		
		if(first_class_value_count == count)
		{
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetFirstClassValue(count);
			root.SetSecondClassValue(0);
			root.SetClassValue(first_class_value);
		}
		else if(second_class_value_count == count)
		{
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetSecondClassValue(count);
			root.SetFirstClassValue(0);
			root.SetClassValue(second_class_value);
		}
		else if(attributes.GetAttributesCount() == 0)
		{
			if(first_class_value_count >= second_class_value_count)
			{
				root.type = TreeNode.Type.CLASS_VALUE;
				root.SetFirstClassValue(first_class_value_count);
				root.SetSecondClassValue(second_class_value_count);
				root.SetClassValue(first_class_value);
			}
			else
			{
				root.type = TreeNode.Type.CLASS_VALUE;
				root.SetSecondClassValue(second_class_value_count);
				root.SetFirstClassValue(first_class_value_count);
				root.SetClassValue(second_class_value);
			}
		}
		else
		{
			
		}
	}

}
