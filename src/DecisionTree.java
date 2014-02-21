import java.math.*;

//decision tree class
public class DecisionTree 
{

	//main method
	public static void main(String[] args) 
	{
		ARFF parser = new ARFF("heart_train.arff", ARFF.Type.TRAINING);
		parser.ParseFile();
		
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
			TreeNode root = new TreeNode(0);
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetFirstClassValue(count);
			root.SetSecondClassValue(0);
			root.SetClassValue(first_class_value);
		}
		else if(second_class_value_count == count)
		{
			TreeNode root = new TreeNode(0);
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetSecondClassValue(count);
			root.SetFirstClassValue(0);
			root.SetClassValue(second_class_value);
		}
		else if(attributes.GetAttributesCount() == 0)
		{
			TreeNode root = new TreeNode(0);
			
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
			TreeNode root = null;
			root = BuildTree(attributes, examples, first_class_value, second_class_value, 2);
		}
	}
	
	private static TreeNode BuildTree(Attributes attributes, Examples examples, String first_class_value, String second_class_value, int m_threshold)
	{
		Attribute temp = ChooseSplit(attributes, examples, first_class_value, second_class_value);
		TreeNode root = new TreeNode(temp.GetFeatureCount());
		root.type = TreeNode.Type.ATTRIBUTE;
		root.SetAttribute(temp);
		
		Feature feature_walker = root.GetAttribute().GetFeaturesHead();
		Example example_walker = examples.GetExamplesHead();
		Value value_walker = null;
		for(int i = 0; i < root.GetAttribute().GetFeatureCount(); i++)
		{
			Examples examples_subset = new Examples();
			while(example_walker != null)
			{
				value_walker = example_walker.GetValuesHead();
				while(!value_walker.GetAttribute().equals(feature_walker.GetAttribute()))
				{
					value_walker = value_walker.GetNext();
				}
				
				if(value_walker.GetValue().equals(feature_walker.GetFeature()))
				{
					examples_subset.AddExample(example_walker);
				}
				
				example_walker = example_walker.GetNext();
			}
			
			if(examples_subset.GetExamplesCount() < m_threshold)
			{
				TreeNode leaf = new TreeNode(0);
				leaf.type = TreeNode.Type.CLASS_VALUE;
				example_walker = examples.GetExamplesHead();
				int first_class_value_count = 0, second_class_value_count = 0;
				while(example_walker != null)
				{
					if(example_walker.GetClassValue().equals(first_class_value))
						first_class_value_count++;
					else if(example_walker.GetClassValue().equals(second_class_value))
						second_class_value_count++;
					
					example_walker = example_walker.GetNext();
				}
				
				if(first_class_value_count >= second_class_value_count)
					leaf.SetClassValue(first_class_value);
				else
					leaf.SetClassValue(second_class_value);
				
				root.SetSuccessor(leaf, i);
			}
			else
			{
				
			}
		}
		
		
		return root;
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
		
		if(count > 0)
		{
			first_class_prob = first_class_value_count/count;
			second_class_prob = second_class_value_count/count;
		}
		
		entropy = -(first_class_prob)*log2(first_class_prob) - (second_class_prob)*log2(second_class_prob);
		
		Attribute attribute_walker = attributes.GetAttributesHead();
		Feature feature_walker = null;
		Value value_walker = null;
		double left_branch_first_value = 0, left_branch_second_value = 0, right_branch_first_value = 0, right_branch_second_value = 0, left_branch_count = 0, right_branch_count = 0;
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
					if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) <= mid) && example_walker.GetClassValue().equals(first_class_value))
					{
						left_branch_first_value++;
						left_branch_count++;
					}
					else if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) <= mid) && example_walker.GetClassValue().equals(second_class_value))
					{
						left_branch_second_value++;
						left_branch_count++;
					}
					else if((Double.parseDouble(example_walker.GetHeldValue().GetValue()) > mid) && example_walker.GetClassValue().equals(first_class_value))
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

				if(left_branch_count > 0)
				{
					left_branch_first_value_prob = left_branch_first_value/left_branch_count;
					left_branch_second_value_prob = left_branch_second_value/left_branch_count;
				}
				if(right_branch_count > 0)
				{
					right_branch_first_value_prob = right_branch_first_value/right_branch_count;
					right_branch_second_value_prob = right_branch_second_value/right_branch_count;
				}
				
				left_entropy = -(left_branch_first_value_prob)*log2(left_branch_first_value_prob) - (left_branch_second_value_prob)*log2(left_branch_second_value_prob);
				right_entropy = -(right_branch_first_value_prob)*log2(right_branch_first_value_prob) - (right_branch_second_value_prob)*log2(right_branch_second_value_prob);
				
				if(count > 0)
				info_gain = entropy - (left_branch_count/count)*left_entropy - (right_branch_count/count)*right_entropy;
				
				if(info_gain > max)
				{
					max = info_gain;
					max_gain = attribute_walker;
				}
			}
			else
			{
				double[] entropies = new double[attribute_walker.GetFeatureCount()];
				first_class_value_count = 0;
				second_class_value_count = 0;
				double branch_count = 0;
										
				for(int i = 0; i < entropies.length; i++)
				{
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						value_walker = example_walker.GetValuesHead();
						while(!value_walker.GetAttribute().equals(attribute_walker))
						{
							value_walker = value_walker.GetNext();
						}
						
						if(value_walker.GetValue().equals(feature_walker.GetFeature()))
						{
							if(example_walker.GetClassValue().equals(first_class_value))
								first_class_value_count++;
							else if(example_walker.GetClassValue().equals(second_class_value))
								second_class_value_count++;
							
							branch_count++;
						}
						
						example_walker = example_walker.GetNext();
					}
					
					if(branch_count > 0)
					{
						first_class_prob = first_class_value_count/branch_count;
						second_class_prob = second_class_value_count/branch_count;
					}
					
					entropies[i] = (-first_class_prob)*log2(first_class_prob) - (second_class_prob)*log2(second_class_prob);
					
					if(count > 0)
						entropies[i] = (branch_count/count)*entropies[i];
					
					feature_walker = feature_walker.GetNext();
					first_class_value_count = 0;
					second_class_value_count = 0;
					branch_count = 0;
				}
				
				double entr_sum = 0;
				for(double entr : entropies)
				{
					entr_sum += entr;
				}
				
				info_gain = entropy - entr_sum;
				
				if(info_gain > max)
				{
					max = info_gain;
					max_gain = attribute_walker;
				}
			}
			
			attribute_walker = attribute_walker.GetNext();
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
