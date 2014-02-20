import java.math.*;

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
			
			example_walker = example_walker.GetNext();
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
			Attribute temp = ChooseSplit(attributes, examples, first_class_value, second_class_value);
		}
	}
	
	private static Attribute ChooseSplit(Attributes attributes, Examples examples, String first_class_value, String second_class_value)
	{
		Attribute max_gain = null;
		double entropy = 0;
		double first_class_prob = 0;
		double second_class_prob = 0;
		double max = 0;
		
		Example example_walker = examples.GetExamplesHead();
		double first_class_value_count = 0, second_class_value_count = 0, count = 0;
		
		while(example_walker != null)
		{
			if(example_walker.GetClassValue().equals(first_class_value))
				first_class_value_count++;
			else if(example_walker.GetClassValue().equals(second_class_value))
				second_class_value_count++;
			
			count++;
			
			example_walker = example_walker.GetNext();
		}
		
		first_class_prob = first_class_value_count/count;
		second_class_prob = second_class_value_count/count;
		
		entropy = -(first_class_prob)*log2(first_class_prob) - (second_class_prob)*log2(second_class_prob);
		
		Attribute attribute_walker = attributes.GetAttributesHead();
		Feature feature_walker = null;
		Value value_walker = null;
		int left_branch_first_value = 0, left_branch_second_value = 0, right_branch_first_value = 0, right_branch_second_value = 0, left_branch_count = 0, right_branch_count = 0;
		double left_branch_first_value_prob = 0, left_branch_second_value_prob = 0, right_branch_first_value_prob = 0, right_branch_second_value_prob = 0;
		double left_entropy = 0, right_entropy = 0;
		double info_gain = 0;
		double mid_sum = 0, mid_count = 0, mid = 0;
		
		while(attribute_walker != null)
		{
			feature_walker = attribute_walker.GetFeaturesHead();
			
			if(feature_walker.GetFeature().equals("real"))
			{
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals(attribute_walker))
					{
						value_walker = value_walker.GetNext();
					}
					
					mid_sum += Double.parseDouble(value_walker.GetValue());
					mid_count++;
					example_walker.SetHeldValue(value_walker);
					
					example_walker = example_walker.GetNext();
				}
				
				mid = midpoint(mid_sum, mid_count);
				
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) <= mid) && example_walker.getClass().equals(first_class_value))
					{
						left_branch_first_value++;
						left_branch_count++;
					}
					else if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) <= mid) && example_walker.getClass().equals(second_class_value))
					{
						left_branch_second_value++;
						left_branch_count++;
					}
					else if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) > mid) && example_walker.getClass().equals(first_class_value))
					{
						right_branch_first_value++;
						right_branch_count++;
					}
					else
					{
						right_branch_second_value++;
						right_branch_count++;
					}
																			
					example_walker = example_walker.GetNext();
				}

				left_branch_first_value_prob = left_branch_first_value/left_branch_count;
				left_branch_second_value_prob = left_branch_second_value/left_branch_count;
				right_branch_first_value_prob = right_branch_first_value/right_branch_count;
				right_branch_second_value_prob = right_branch_second_value/right_branch_count;
				
				left_entropy = -(left_branch_first_value_prob)*log2(left_branch_first_value_prob) - (left_branch_second_value_prob)*log2(left_branch_second_value_prob);
				right_entropy = -(right_branch_first_value_prob)*log2(right_branch_first_value_prob) - (right_branch_second_value_prob)*log2(right_branch_second_value_prob);
				
				info_gain = entropy - left_entropy - right_entropy;
				if(info_gain > max)
				{
					max = info_gain;
					max_gain = attribute_walker;
				}
			}
			else
			{
				while(feature_walker != null)
				{
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						value_walker = example_walker.GetValuesHead();
						while(value_walker != null)
						{
							if(feature_walker.GetFeature().equals(value_walker.GetValue()) && example_walker.GetClassValue().equals(first_class_value))
								first_class_value_count++;
							else if(feature_walker.GetFeature().equals(value_walker.GetValue()) && example_walker.GetClassValue().equals(second_class_value))
								second_class_value_count++;
						}
					}
					
					feature_walker = feature_walker.GetNext();
				}
				
				attribute_walker = attribute_walker.GetNext();
			}
		}
		
		return max_gain;
	}
	
	private static double log2(double input)
	{
		return (Math.log10(input)/Math.log10(2));
	}
	
	private static double midpoint(double sum, double count)
	{
		return sum/count;
	}

}
