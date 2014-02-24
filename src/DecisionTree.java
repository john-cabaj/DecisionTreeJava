import java.io.*;

//decision tree class
public class DecisionTree 
{

	//main method
	public static void main(String[] args) 
	{
		File one = new File("heart_train.arff");
		File two = new File("heart_test.arff");
		if(one.exists() && !one.isDirectory() && two.exists() && !two.isDirectory())
		{
//			try
//			{
				//int m = Integer.parseInt(args[2]);
				ARFF train_parser = new ARFF("heart_train.arff", ARFF.Type.TRAINING);
				ARFF test_parser = new ARFF("heart_test.arff", ARFF.Type.TESTING);
				train_parser.ParseFile();
				test_parser.ParseFile();
				
				Examples train_examples = train_parser.GetExamples();
				Attributes train_attributes = train_parser.GetAttributes();
				Examples test_examples = test_parser.GetExamples();
				String first_class_value = train_parser.GetFirstClassValue();
				String second_class_value = train_parser.GetSecondClassValue();
				
				if(train_examples.GetFirstClassCount() == train_examples.GetExamplesCount())
				{
					TreeNode root = new TreeNode(0);
					root.type = TreeNode.Type.CLASS_VALUE;
					root.SetFirstClassValue(train_examples.GetFirstClassCount());
					root.SetSecondClassValue(train_examples.GetSecondClassCount());
					root.SetClassValue(first_class_value);
				}
				else if(train_examples.GetSecondClassCount() == train_examples.GetExamplesCount())
				{
					TreeNode root = new TreeNode(0);
					root.type = TreeNode.Type.CLASS_VALUE;
					root.SetSecondClassValue(train_examples.GetSecondClassCount());
					root.SetFirstClassValue(train_examples.GetFirstClassCount());
					root.SetClassValue(second_class_value);
				}
				else if(train_attributes.GetAttributesCount() == 0)
				{
					TreeNode root = new TreeNode(0);
					
					if(train_examples.GetFirstClassCount() >= train_examples.GetSecondClassCount())
					{
						root.type = TreeNode.Type.CLASS_VALUE;
						root.SetFirstClassValue(train_examples.GetFirstClassCount());
						root.SetSecondClassValue(train_examples.GetSecondClassCount());
						root.SetClassValue(first_class_value);
					}
					else
					{
						root.type = TreeNode.Type.CLASS_VALUE;
						root.SetSecondClassValue(train_examples.GetSecondClassCount());
						root.SetFirstClassValue(train_examples.GetFirstClassCount());
						root.SetClassValue(second_class_value);
					}
				}
				else
				{
					TreeNode root = null;
					root = BuildTree(train_attributes, train_examples, first_class_value, second_class_value, 4);
					PrintTree(root, 0);
					Evaluate(root, test_examples);
				}
//			}
//			catch(NumberFormatException nfe)
//			{
//				System.out.println("Input number for m");
//			}
		}
		else
		{
			System.out.println("Input proper file location");
		}
	}
	
	private static TreeNode BuildTree(Attributes attributes, Examples examples, String first_class_value, String second_class_value, int m_threshold)
	{
		Attribute temp = ChooseSplit(attributes, examples, first_class_value, second_class_value);
		TreeNode root = null;
		if(temp.GetFeaturesHead().GetFeature().equals("real"))
			root = new TreeNode(2);
		else
			root = new TreeNode(temp.GetFeatureCount());
		root.type = TreeNode.Type.ATTRIBUTE;
		root.SetAttribute(temp);
		
		Feature feature_walker = root.GetAttribute().GetFeaturesHead();
		Example example_walker = examples.GetExamplesHead();
		Value value_walker = null;

		Attributes attributes_subset = new Attributes();
		Attribute attributes_walker = attributes.GetAttributesHead();
		
		if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real"))
		{
			attributes_subset = attributes;
		}
		else
		{
			while(attributes_walker != null)
			{
				if(!attributes_walker.AttributeName().equals(root.GetAttribute().AttributeName()))
				{
					Attribute at = new Attribute(attributes_walker.AttributeName());
					at.CopyAttribute(attributes_walker);
					attributes_subset.AddAttribute(at);
				}
				
				attributes_walker = attributes_walker.GetNext();
			}
		}
		
		if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real"))
		{
			double midpoint = root.GetAttribute().GetFeaturesHead().GetMidpoint().MidpointValue();
			
			for(int i = 0; i < 2; i++)
			{
				TreeNode feature = new TreeNode(1);
				feature.type = TreeNode.Type.FEATURE;
				feature.SetFeature(feature_walker);
				root.SetSuccessor(feature, i);
				feature.SetParent(root);
				
				Examples examples_subset = new Examples(first_class_value, second_class_value);
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals((feature_walker.GetAttribute().AttributeName())))
					{
						value_walker = value_walker.GetNext();
					}
					
					if(i == 0)
					{
						if(Double.parseDouble(value_walker.GetValue()) <= midpoint)
						{
							Example ex = new Example();
							ex.CopyExample(example_walker);
							examples_subset.AddExample(ex);
						}
					}
					else if(i == 1)
					{
						if(Double.parseDouble(value_walker.GetValue()) > midpoint)
						{
							Example ex = new Example();
							ex.CopyExample(example_walker);
							examples_subset.AddExample(ex);
						}
					}
					
					example_walker = example_walker.GetNext();
				}
				feature.SetFirstClassValue(examples_subset.GetFirstClassCount());
				feature.SetSecondClassValue(examples_subset.GetSecondClassCount());
				feature.SetMidpoint(midpoint);
				
				if(examples_subset.GetFirstClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(first_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				else if(examples_subset.GetSecondClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(second_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}
				
				else if(examples_subset.GetExamplesCount() < m_threshold)
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					
					if(examples_subset.GetFirstClassCount() >= examples_subset.GetSecondClassCount())
						leaf.SetClassValue(first_class_value);
					else
						leaf.SetClassValue(second_class_value);
					
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				else
				{
					feature.SetSuccessor(BuildTree(attributes_subset, examples_subset, first_class_value, second_class_value, m_threshold), 0);
				}
			}
		}
		else
		{
			for(int j = 0; j < root.GetAttribute().GetFeatureCount(); j++)
			{
				TreeNode feature = new TreeNode(1);
				feature.type = TreeNode.Type.FEATURE;
				feature.SetFeature(feature_walker);
				root.SetSuccessor(feature, j);
				feature.SetParent(root);
				
				Examples examples_subset = new Examples(first_class_value, second_class_value);
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals(feature_walker.GetAttribute().AttributeName()))
					{
						value_walker = value_walker.GetNext();
					}
					
					if(value_walker.GetValue().equals(feature_walker.GetFeature()))
					{
						Example ex = new Example();
						ex.CopyExample(example_walker);
						examples_subset.AddExample(ex);
					}
					
					example_walker = example_walker.GetNext();
				}
				feature.SetFirstClassValue(examples_subset.GetFirstClassCount());
				feature.SetSecondClassValue(examples_subset.GetSecondClassCount());
					

				if(examples_subset.GetFirstClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(first_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				else if(examples_subset.GetSecondClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(second_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}
				
				else if(examples_subset.GetExamplesCount() < m_threshold)
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					
					if(examples_subset.GetFirstClassCount() >= examples_subset.GetSecondClassCount())
						leaf.SetClassValue(first_class_value);
					else
						leaf.SetClassValue(second_class_value);
					
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}
				else
				{
					feature.SetSuccessor(BuildTree(attributes_subset, examples_subset, first_class_value, second_class_value, m_threshold), 0);
				}
				
				feature_walker = feature_walker.GetNext();
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
				
		if(examples.GetExamplesCount() > 0)
		{
			first_class_prob = (double)examples.GetFirstClassCount()/(double)examples.GetExamplesCount();
			second_class_prob = (double)examples.GetSecondClassCount()/(double)examples.GetExamplesCount();
		}
		
		entropy = -(first_class_prob)*log2(first_class_prob) - (second_class_prob)*log2(second_class_prob);
		
		Attribute attribute_walker = attributes.GetAttributesHead();
		Feature feature_walker = null;
		Value value_walker = null;
		double left_branch_first_value = 0, left_branch_second_value = 0, right_branch_first_value = 0, right_branch_second_value = 0, left_branch_count = 0, right_branch_count = 0;
		double left_branch_first_value_prob = 0, left_branch_second_value_prob = 0, right_branch_first_value_prob = 0, right_branch_second_value_prob = 0;
		double left_entropy = 0, right_entropy = 0;
		double info_gain = 0;
		Example example_walker = examples.GetExamplesHead();
		
		while(attribute_walker != null)
		{
			feature_walker = attribute_walker.GetFeaturesHead();
			
			if(feature_walker.GetFeature().equals("real"))
			{
//				if(attribute_walker.Attribute().equals("thalach"))
//					System.out.println("HERE");
				example_walker = examples.GetExamplesHead();
				feature_walker.InitializeRealFeatures(examples.GetExamplesCount());
				for(int i = 0; i < examples.GetExamplesCount(); i++)
				{
					value_walker = example_walker.GetValuesHead();

					while(!value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
					{
						value_walker = value_walker.GetNext();
					}
					
					RealFeature temp = new RealFeature(Double.parseDouble(value_walker.GetValue()), example_walker.GetClassValue());
					feature_walker.AddRealFeature(temp, i);
					example_walker.SetHeldValue(value_walker);
					
					example_walker = example_walker.GetNext();
				}
				
				Midpoint midpoints = feature_walker.GetMidpoints(first_class_value, second_class_value);
				
				Midpoint midpoints_walker = midpoints;
				while(midpoints_walker != null)
				{
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						value_walker = example_walker.GetValuesHead();
						while(!value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
						{
							value_walker = value_walker.GetNext();
						}
						
						if(Double.parseDouble(value_walker.GetValue()) <= midpoints_walker.MidpointValue() && example_walker.GetClassValue().equals(first_class_value))
						{
							left_branch_first_value++;
							left_branch_count++;
						}
						else if(Double.parseDouble(value_walker.GetValue()) <= midpoints_walker.MidpointValue() && example_walker.GetClassValue().equals(second_class_value))
						{
							left_branch_second_value++;
							left_branch_count++;
						}
						else if(Double.parseDouble(value_walker.GetValue()) > midpoints_walker.MidpointValue() && example_walker.GetClassValue().equals(first_class_value))
						{
							right_branch_first_value++;
							right_branch_count++;
						}
						else if(Double.parseDouble(value_walker.GetValue()) > midpoints_walker.MidpointValue() && example_walker.GetClassValue().equals(second_class_value))
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
					
					if(examples.GetExamplesCount() > 0)
						info_gain = entropy - (left_branch_count/examples.GetExamplesCount())*left_entropy - (right_branch_count/examples.GetExamplesCount())*right_entropy;
				
					if(info_gain > max)
					{
						max = info_gain;
						max_gain = attribute_walker;
						feature_walker.SetMidpoint(midpoints_walker);
					}
					
					left_branch_first_value = 0;
					left_branch_second_value = 0;
					right_branch_first_value = 0;
					right_branch_second_value = 0;
					left_branch_count = 0;
					right_branch_count = 0;
					
					midpoints_walker = midpoints_walker.GetNext();
				}
			}
			else
			{
				double[] entropies = new double[attribute_walker.GetFeatureCount()];
				double first_class_value_count = 0, second_class_value_count = 0;
				double branch_count = 0;
										
				for(int i = 0; i < entropies.length; i++)
				{
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						value_walker = example_walker.GetValuesHead();
						while(value_walker != null && !value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
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
					
					if(examples.GetExamplesCount() > 0)
						entropies[i] = (branch_count/examples.GetExamplesCount())*entropies[i];
					
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
		if(input == 0)
			return 0;
		else
			return (Math.log10(input)/Math.log10(2));
	}
	
	private static void PrintTree(TreeNode root, int level)
	{
		if(root.type == TreeNode.Type.ATTRIBUTE)
		{
			System.out.println();
			for(int i = 0; i < root.GetSuccessors().length; i++)
			{
				for(int j = 0; j < level; j++)
				{
					System.out.print("|\t");
				}
				System.out.print(root.GetAttribute().AttributeName());
				if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real"))
				{
					if(i == 0)
					{
						System.out.print(" <= ");
					}
					else if(i == 1)
					{
						System.out.print(" > ");
					}
				}
				else
				{
					System.out.print(" = ");
				}
				
				PrintTree(root.GetSuccessor(i), level);
			}
		}
		else if(root.type == TreeNode.Type.FEATURE)
		{
			if(root.GetFeature().GetFeature().equals("real"))
			{
				System.out.print(root.GetMidpoint());
			}
			else
			{
				System.out.print(root.GetFeature().GetFeature());
			}
			
			System.out.print(" [" + root.GetFirstClassValue() + " " + root.GetSecondClassValue() + "]");
			PrintTree(root.GetSuccessor(0), level+=1);
		}
		else if(root.type == TreeNode.Type.CLASS_VALUE)
		{
			System.out.println(": " + root.GetClassValue());
		}
	}
	
	public static void Evaluate(TreeNode root, Examples examples)
	{
		int correct = 0;
		Example example_walker = examples.GetExamplesHead();
		while(example_walker != null)
		{		
			String class_value = GetClassValue(root, example_walker);
			if(class_value.equals(example_walker.GetClassValue()))
				correct++;
			System.out.println(class_value + " " + example_walker.GetClassValue());
			example_walker = example_walker.GetNext();
		}
		System.out.println(correct + " " + examples.GetExamplesCount());
	}
	
	public static String GetClassValue(TreeNode root, Example example)
	{
		String class_value = null;
		
		if(root.type == TreeNode.Type.ATTRIBUTE)
		{
			Value value_walker = example.GetValuesHead();
			
			while(!value_walker.GetAttribute().equals(root.GetAttribute().AttributeName()))
			{
				value_walker = value_walker.GetNext();
			}
			
			for(int i = 0; i < root.GetSuccessors().length && class_value == null; i++)
			{
				if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real"))
				{
					if(i == 0)
					{
						if(Double.parseDouble(value_walker.GetValue()) <= root.GetSuccessor(i).GetMidpoint())
							class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
					else if(i == 1)
					{
						if(Double.parseDouble(value_walker.GetValue()) > root.GetSuccessor(i).GetMidpoint())
							class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
				}
				else
				{
					if(value_walker.GetValue().equals(root.GetSuccessor(i).GetFeature().GetFeature()))
					{
						class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
				}
			}
		}
		else if(root.type == TreeNode.Type.CLASS_VALUE)
		{
			return root.GetClassValue();
		}
		
		return class_value;
	}

}
