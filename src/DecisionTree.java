//import libraries
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//decision tree class
public class DecisionTree 
{
	//performing learning curve
	private static boolean learning_curve = false;
	//printing to file
	private static boolean to_file = false;
	//file to print to
	private static String file_name = null;
	//writer
	private static BufferedWriter writer = null;
	//only print tree
	private static boolean only_tree = false;
	//performing cross-validation
	private static boolean cross_validation = false;
	//number of cross-validation folds
	private static int folds = 0;

	//main method
	public static void main(String[] args) 
	{
		//try-catch for empty arguments
		try
		{
			//stopping criteria argument
			int m = 0;

			//if only printing tree
			if(args[1].toLowerCase().equals("tree"))
			{
				only_tree = true;
				m = Integer.parseInt(args[2]);
			}
			//if generating learning curves
			else if(args[2].toLowerCase().equals("lc"))
			{
				learning_curve = true;
				m = Integer.parseInt(args[3]);
			}
			//if performing cross-validation
			else if(args[1].toLowerCase().equals("cv"))
			{
				cross_validation = true;
				folds = Integer.parseInt(args[2]);
				m = Integer.parseInt(args[3]);
			}

			//loop through arguments
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].toLowerCase().equals("-o"))
				{
					if(i < args.length - 1)
					{
						to_file = true;
						file_name = args[i+1];
					}
					else
						System.out.println("Must include file name for output");
				}
			}

			//initialize two files to check that training and test set files valid
			File one = new File(args[0]);
			File two = null;
			//if using testing set
			if(!only_tree && !cross_validation)
				two = new File(args[1]);

			//check that training set valid
			if(one.exists() && !one.isDirectory())
			{
				//check that test set valid
				if(only_tree || cross_validation || two.exists() && !two.isDirectory())
				{
					//initialize two ARFF parsers and parse training and test sets
					ARFF train_parser = new ARFF(args[0], ARFF.Type.TRAINING);
					ARFF test_parser = new ARFF(args[1], ARFF.Type.TESTING);
					train_parser.ParseFile();
					//if not only printing tree
					if(!only_tree && !cross_validation)
						test_parser.ParseFile();
					//get list of attributes
					Attributes train_attributes = train_parser.GetAttributes();

					//get training and test examples
					Examples train_examples = train_parser.GetExamples();
					Examples test_examples = test_parser.GetExamples();

					//get class values
					String first_class_value = train_parser.GetFirstClassValue();
					String second_class_value = train_parser.GetSecondClassValue();

					//if the learning curves statified sampling argument is given, perform the learning curve
					if(learning_curve)
					{
						//output to file
						if(to_file)
						{
							//try-catch for IOExceptions
							try
							{
								File file = new File(file_name);
								if(!file.exists())
									file.createNewFile();
								FileWriter fw = new FileWriter(file.getAbsoluteFile());
								writer = new BufferedWriter(fw);

								//perform the learning curve and print to file
								LearningCurveFile(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
								writer.close();
							}
							//catch IOException
							catch(IOException ioe)
							{
								System.out.println("IO excpetion");
							}
						}
						//output to console
						else
							LearningCurve(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
					}
					//if the cross_validation argument is given, perform cross-validation
					else if(cross_validation)
					{
						//output to file
						if(to_file)
						{
							//try-catch for IOExceptions
							try
							{
								File file = new File(file_name);
								if(!file.exists())
									file.createNewFile();
								FileWriter fw = new FileWriter(file.getAbsoluteFile());
								writer = new BufferedWriter(fw);

								//perform the cross-validation and print to file
								CrossValidationFile(train_attributes, train_examples, first_class_value, second_class_value, m, folds);	
								writer.close();
							}
							//catch IOException
							catch(IOException ioe)
							{
								System.out.println("IO excpetion");
							}
						}
						//output to console
						else
							CrossValidation(train_attributes, train_examples, first_class_value, second_class_value, m, folds);	
					}
					//if the output to file option and output file name arguments are given
					else if(to_file)
					{
						//try-catch for IOExceptions
						try
						{
							File file = new File(file_name);
							if(!file.exists())
								file.createNewFile();
							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							writer = new BufferedWriter(fw);

							//build the tree as normal
							BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
							writer.close();
						}
						//catch IOException
						catch(IOException ioe)
						{
							System.out.println("IO exception");
						}

					}
					//no special arguments
					else
					{
						//build the true as normal
						BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
					}
				}
				//testing file or print tree argument incorrect
				else
				{
					//incorrect input
					if(!two.exists()  && ! two.isDirectory() && !only_tree && !cross_validation)
						System.out.println("Usage: dt-learn train-set-file {test-set-file [lc]|tree|cv #folds} m [-o] [output file]");
					//testing set file must not exist
					else
						System.out.println("Testing set file doesn't exist");
				}
			}
			//training set doesn't exist
			else
			{
				System.out.println("Training set file doesn't exist");
			}
		}
		//input missing
		catch(ArrayIndexOutOfBoundsException oob)
		{
			System.out.println("Usage: dt-learn train-set-file {test-set-file [lc]|tree|cv #folds} m [-o] [output file]");
		}
		//catch incorrect numerical value
		catch(NumberFormatException nfe)
		{
			System.out.println("Usage: dt-learn train-set-file {test-set-file [lc]|tree|cv #folds} m [-o] [output file]");
		}
	}

	//method builds the tree and returns the root node
	private static TreeNode BuildTree(Attributes attributes, Examples examples, String first_class_value, String second_class_value, int m_threshold)
	{
		//choose the next attribute
		Attribute temp = ChooseSplit(attributes, examples, first_class_value, second_class_value);
		TreeNode root = null;

		//if there was no information gain or no data to split on
		if(temp == null)
		{
			//create leaf node of the majority class value
			TreeNode leaf = new TreeNode(0);
			leaf.type = TreeNode.Type.CLASS_VALUE;
			if(examples.GetFirstClassCount() >= examples.GetSecondClassCount())
				leaf.SetClassValue(first_class_value);
			else
				leaf.SetClassValue(second_class_value);
			return root;
		}

		//if the attribute contains a real value, it must have 2 successors
		if(temp.GetFeaturesHead().GetFeature().equals("real") || temp.GetFeaturesHead().GetFeature().equals("numeric"))
			root = new TreeNode(2);
		//if the feature is nominal
		else
			root = new TreeNode(temp.GetFeatureCount());

		//set the node type to attribute and link to its attribute
		root.type = TreeNode.Type.ATTRIBUTE;
		root.SetAttribute(temp);

		//initialize variables to walk the features list, exmaples list, and values list
		Feature feature_walker = root.GetAttribute().GetFeaturesHead();
		Example example_walker = examples.GetExamplesHead();
		Value value_walker = null;

		//initialize a subset of the examples not including the one used
		Attributes attributes_subset = new Attributes();
		Attribute attributes_walker = attributes.GetAttributesHead();

		//include real valued attributes in the subset
		if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real") || root.GetAttribute().GetFeaturesHead().GetFeature().equals("numeric"))
		{
			attributes_subset = attributes;
		}
		//remove nominal attributes from subset for next split
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

		//if attribute value is real, must use midpoints
		if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real") || root.GetAttribute().GetFeaturesHead().GetFeature().equals("numeric"))
		{
			//initialize midpoint value
			double midpoint = root.GetAttribute().GetFeaturesHead().GetMidpoint().MidpointValue();

			//cycle through successors
			for(int i = 0; i < 2; i++)
			{
				//create a feature value node to aid in tree printing
				TreeNode feature = new TreeNode(1);
				feature.type = TreeNode.Type.FEATURE;
				feature.SetFeature(feature_walker);
				root.SetSuccessor(feature, i);
				feature.SetParent(root);

				//walk the examples list
				Examples examples_subset = new Examples(first_class_value, second_class_value);
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					//find the value corresponding to the current attribute
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals((feature_walker.GetAttribute().AttributeName())))
					{
						value_walker = value_walker.GetNext();
					}

					//if checking <= comparison
					if(i == 0)
					{
						//if value is <= midpoint, add it to the examples subset
						if(Double.parseDouble(value_walker.GetValue()) <= midpoint)
						{
							Example ex = new Example();
							ex.CopyExample(example_walker);
							examples_subset.AddExample(ex);
						}
					}
					//checking > comparison
					else if(i == 1)
					{
						//if value is > midpoint, add it to the examples subset
						if(Double.parseDouble(value_walker.GetValue()) > midpoint)
						{
							Example ex = new Example();
							ex.CopyExample(example_walker);
							examples_subset.AddExample(ex);
						}
					}

					example_walker = example_walker.GetNext();
				}
				//set the total first class and second class value counts for the feature value
				feature.SetFirstClassValue(examples_subset.GetFirstClassCount());
				feature.SetSecondClassValue(examples_subset.GetSecondClassCount());

				//save the midpoint
				feature.SetMidpoint(midpoint);

				//if all first class examples, create a leaf node
				if(examples_subset.GetFirstClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(first_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				//if all second class examples, create a leaf node
				else if(examples_subset.GetSecondClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(second_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				//if we're below stopping criteria threshold, create a majority leaf node
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

				//otherwise, recursively build the tree
				else
				{
					feature.SetSuccessor(BuildTree(attributes_subset, examples_subset, first_class_value, second_class_value, m_threshold), 0);
				}
			}
		}
		//attribute value is nominal
		else
		{
			//cycle through feature values
			for(int j = 0; j < root.GetAttribute().GetFeatureCount(); j++)
			{
				//create feature value node
				TreeNode feature = new TreeNode(1);
				feature.type = TreeNode.Type.FEATURE;
				feature.SetFeature(feature_walker);
				root.SetSuccessor(feature, j);
				feature.SetParent(root);

				//walk the examples list
				Examples examples_subset = new Examples(first_class_value, second_class_value);
				example_walker = examples.GetExamplesHead();
				while(example_walker != null)
				{
					//find the value corresponding to the current attribute
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals(feature_walker.GetAttribute().AttributeName()))
					{
						value_walker = value_walker.GetNext();
					}

					//if the value equals the feature value, add it to the examples subset
					if(value_walker.GetValue().equals(feature_walker.GetFeature()))
					{
						Example ex = new Example();
						ex.CopyExample(example_walker);
						examples_subset.AddExample(ex);
					}

					example_walker = example_walker.GetNext();
				}

				//set the total first class and second class value counts for the feature value
				feature.SetFirstClassValue(examples_subset.GetFirstClassCount());
				feature.SetSecondClassValue(examples_subset.GetSecondClassCount());

				//if all examples first class, create leaf node
				if(examples_subset.GetFirstClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(first_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				//if all examples second class, create leaf node
				else if(examples_subset.GetSecondClassCount() == examples_subset.GetExamplesCount())
				{
					TreeNode leaf = new TreeNode(0);
					leaf.type = TreeNode.Type.CLASS_VALUE;
					leaf.SetClassValue(second_class_value);
					feature.SetSuccessor(leaf, 0);
					leaf.SetParent(feature);
				}

				//if we're below stopping criteria threshold, create a majority leaf node
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

				//otherwise, recursively build tree
				else
				{
					feature.SetSuccessor(BuildTree(attributes_subset, examples_subset, first_class_value, second_class_value, m_threshold), 0);
				}

				feature_walker = feature_walker.GetNext();
			}
		}

		//return the root
		return root;
	}

	//method chooses the next attribute to split on based on information gain
	private static Attribute ChooseSplit(Attributes attributes, Examples examples, String first_class_value, String second_class_value)
	{
		Attribute max_gain = null;
		double entropy = 0;
		double first_class_prob = 0;
		double second_class_prob = 0;
		double max = 0;

		//check for divide by 0
		if(examples.GetExamplesCount() > 0)
		{
			//initialize first and second class probabilities for top node
			first_class_prob = (double)examples.GetFirstClassCount()/(double)examples.GetExamplesCount();
			second_class_prob = (double)examples.GetSecondClassCount()/(double)examples.GetExamplesCount();
		}

		//calculate base entropy
		entropy = -(first_class_prob)*log2(first_class_prob) - (second_class_prob)*log2(second_class_prob);

		//find the entropies of the left and right branch
		Attribute attribute_walker = attributes.GetAttributesHead();
		Feature feature_walker = null;
		Value value_walker = null;
		double left_branch_first_value = 0, left_branch_second_value = 0, right_branch_first_value = 0, right_branch_second_value = 0, left_branch_count = 0, right_branch_count = 0;
		double left_branch_first_value_prob = 0, left_branch_second_value_prob = 0, right_branch_first_value_prob = 0, right_branch_second_value_prob = 0;
		double left_entropy = 0, right_entropy = 0;
		double info_gain = 0;
		Example example_walker = examples.GetExamplesHead();

		//iterate through all possible attributes
		while(attribute_walker != null)
		{
			feature_walker = attribute_walker.GetFeaturesHead();

			//if attribute is real valued
			if(feature_walker.GetFeature().equals("real") || feature_walker.GetFeature().equals("numeric"))
			{
				//walk through all examples
				example_walker = examples.GetExamplesHead();
				feature_walker.InitializeRealFeatures(examples.GetExamplesCount());
				for(int i = 0; i < examples.GetExamplesCount(); i++)
				{
					//find value that equals the attribute
					value_walker = example_walker.GetValuesHead();
					while(!value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
					{
						value_walker = value_walker.GetNext();
					}

					//create a RealFeature the contains the real value and add it to the feature value
					RealFeature temp = new RealFeature(Double.parseDouble(value_walker.GetValue()), example_walker.GetClassValue());
					feature_walker.AddRealFeature(temp, i);

					example_walker = example_walker.GetNext();
				}

				//calculate the midpoint values
				Midpoint midpoints = feature_walker.GetMidpoints(first_class_value, second_class_value);

				//iterate through midpoints to find one that yields maximum information gain
				Midpoint midpoints_walker = midpoints;
				while(midpoints_walker != null)
				{
					//iterate through examples
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						//find value that equals the attribute
						value_walker = example_walker.GetValuesHead();
						while(!value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
						{
							value_walker = value_walker.GetNext();
						}

						//count number of first and second class values go to the left and right branch
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

					//find left branch first and second value probabilities
					if(left_branch_count > 0)
					{
						left_branch_first_value_prob = left_branch_first_value/left_branch_count;
						left_branch_second_value_prob = left_branch_second_value/left_branch_count;
					}
					//find right branch first and second value probabilities
					if(right_branch_count > 0)
					{
						right_branch_first_value_prob = right_branch_first_value/right_branch_count;
						right_branch_second_value_prob = right_branch_second_value/right_branch_count;
					}

					//calculate left and right value entropy
					left_entropy = -(left_branch_first_value_prob)*log2(left_branch_first_value_prob) - (left_branch_second_value_prob)*log2(left_branch_second_value_prob);
					right_entropy = -(right_branch_first_value_prob)*log2(right_branch_first_value_prob) - (right_branch_second_value_prob)*log2(right_branch_second_value_prob);

					//calculate information gain
					if(examples.GetExamplesCount() > 0)
						info_gain = entropy - (left_branch_count/examples.GetExamplesCount())*left_entropy - (right_branch_count/examples.GetExamplesCount())*right_entropy;

					//if the information gain is greater than the max save it
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
			//attribute value is nominal
			else
			{
				double[] entropies = new double[attribute_walker.GetFeatureCount()];
				double first_class_value_count = 0, second_class_value_count = 0;
				double branch_count = 0;

				//cycle through branches of attribute values
				for(int i = 0; i < entropies.length; i++)
				{
					//walk through examples list
					example_walker = examples.GetExamplesHead();
					while(example_walker != null)
					{
						//find value equal to attribute
						value_walker = example_walker.GetValuesHead();
						while(value_walker != null && !value_walker.GetAttribute().equals(attribute_walker.AttributeName()))
						{
							value_walker = value_walker.GetNext();
						}

						//get first class and second class value counts
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

					//find first class and second class value probabilities
					if(branch_count > 0)
					{
						first_class_prob = first_class_value_count/branch_count;
						second_class_prob = second_class_value_count/branch_count;
					}

					//calculate entropy for each possible value of the attribute
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

				//find information gain
				info_gain = entropy - entr_sum;

				//if information gain is max, save the attribute
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

	//computes log2 of a value
	private static double log2(double input)
	{
		if(input == 0)
			return 0;
		else
			return (Math.log10(input)/Math.log10(2));
	}

	//prints the tree given a root and level for formatting
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
				if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real") || root.GetAttribute().GetFeaturesHead().GetFeature().equals("numeric"))
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
			if(root.GetFeature().GetFeature().equals("real") || root.GetFeature().GetFeature().equals("numeric"))
			{
				DecimalFormat formatter = new DecimalFormat("0.000000");
				String output = formatter.format(root.GetMidpoint());
				System.out.print(output);
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

	//print all evaluations of test set
	public static void PrintAllEvaluate(TreeNode root, Examples examples)
	{
		int correct = 0;

		//cycle through test set and evaluate the tree printing the predicted and actual value and correct and total counts
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

	//prints the tree given a root and level for formatting to file
	private static void PrintTreeFile(TreeNode root, int level)
	{
		try
		{
			if(root.type == TreeNode.Type.ATTRIBUTE)
			{
				writer.newLine();
				for(int i = 0; i < root.GetSuccessors().length; i++)
				{
					for(int j = 0; j < level; j++)
					{
						writer.write("|\t");
					}
					writer.write(root.GetAttribute().AttributeName());
					if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real") || root.GetAttribute().GetFeaturesHead().GetFeature().equals("numeric"))
					{
						if(i == 0)
						{
							writer.write(" <= ");
						}
						else if(i == 1)
						{
							writer.write(" > ");
						}
					}
					else
					{
						writer.write(" = ");
					}

					PrintTreeFile(root.GetSuccessor(i), level);
				}
			}
			else if(root.type == TreeNode.Type.FEATURE)
			{
				if(root.GetFeature().GetFeature().equals("real") || root.GetFeature().GetFeature().equals("numeric"))
				{
					DecimalFormat formatter = new DecimalFormat("0.000000");
					String output = formatter.format(root.GetMidpoint());
					writer.write(output);
				}
				else
				{
					writer.write(root.GetFeature().GetFeature());
				}

				writer.write(" [" + root.GetFirstClassValue() + " " + root.GetSecondClassValue() + "]");
				PrintTreeFile(root.GetSuccessor(0), level+=1);
			}
			else if(root.type == TreeNode.Type.CLASS_VALUE)
			{
				writer.write(": " + root.GetClassValue());
				writer.newLine();
			}
		}
		catch(IOException ioe)
		{
			System.out.println("IO exception");
		}
	}

	//print all evaluations of test set to file
	public static void PrintAllEvaluateFile(TreeNode root, Examples examples)
	{
		try
		{
			int correct = 0;

			//cycle through test set and evaluate the tree printing the predicted and actual value and correct and total counts
			Example example_walker = examples.GetExamplesHead();
			while(example_walker != null)
			{		
				String class_value = GetClassValue(root, example_walker);
				if(class_value.equals(example_walker.GetClassValue()))
					correct++;
				writer.write(class_value + " " + example_walker.GetClassValue());
				writer.newLine();
				example_walker = example_walker.GetNext();
			}
			writer.write(correct + " " + examples.GetExamplesCount());
			writer.newLine();
		}
		catch(IOException ioe)
		{
			System.out.println("IO exception");
		}
	}

	//evaluates the tree for learning curve generation
	public static int Evaluate(TreeNode root, Examples examples)
	{
		int correct = 0;

		//cycle through test set and add up correctly classified count
		Example example_walker = examples.GetExamplesHead();
		while(example_walker != null)
		{		
			String class_value = GetClassValue(root, example_walker);
			if(class_value.equals(example_walker.GetClassValue()))
				correct++;
			example_walker = example_walker.GetNext();
		}
		return correct;
	}

	//cycles through the tree given an example to classify and returns the prediction
	public static String GetClassValue(TreeNode root, Example example)
	{
		String class_value = null;

		//if the root is an attribute
		if(root.type == TreeNode.Type.ATTRIBUTE)
		{
			//find the attribute we're looking for
			Value value_walker = example.GetValuesHead();
			while(!value_walker.GetAttribute().equals(root.GetAttribute().AttributeName()))
			{
				value_walker = value_walker.GetNext();
			}

			//iterate through successors
			for(int i = 0; i < root.GetSuccessors().length && class_value == null; i++)
			{
				//if attribute real valued
				if(root.GetAttribute().GetFeaturesHead().GetFeature().equals("real") || root.GetAttribute().GetFeaturesHead().GetFeature().equals("numeric"))
				{
					//if <= comparison is used
					if(i == 0)
					{
						if(Double.parseDouble(value_walker.GetValue()) <= root.GetSuccessor(i).GetMidpoint())
							class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
					//if > comparison is used
					else if(i == 1)
					{
						if(Double.parseDouble(value_walker.GetValue()) > root.GetSuccessor(i).GetMidpoint())
							class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
				}
				//attribute is nominal feature
				else
				{
					//if the value equals the feature value, recursively traverse tree
					if(value_walker.GetValue().equals(root.GetSuccessor(i).GetFeature().GetFeature()))
					{
						class_value = GetClassValue(root.GetSuccessor(i).GetSuccessor(0), example);
					}
				}
			}
		}

		//if at a class value node, return the prediction
		else if(root.type == TreeNode.Type.CLASS_VALUE)
		{
			return root.GetClassValue();
		}

		return class_value;
	}

	//builds a decision tree
	public static int BuildDecisionTree(Attributes train_attributes, Examples train_examples, Examples test_examples, String first_class_value, String second_class_value, int m)
	{		
		int correct = 0;

		//if all first class examples, create leaf node
		if(train_examples.GetFirstClassCount() == train_examples.GetExamplesCount())
		{
			TreeNode root = new TreeNode(0);
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetFirstClassValue(train_examples.GetFirstClassCount());
			root.SetSecondClassValue(train_examples.GetSecondClassCount());
			root.SetClassValue(first_class_value);
		}
		//if all second class examples, create leaf node
		else if(train_examples.GetSecondClassCount() == train_examples.GetExamplesCount())
		{
			TreeNode root = new TreeNode(0);
			root.type = TreeNode.Type.CLASS_VALUE;
			root.SetSecondClassValue(train_examples.GetSecondClassCount());
			root.SetFirstClassValue(train_examples.GetFirstClassCount());
			root.SetClassValue(second_class_value);
		}
		//no features left, create majority leaf node
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
		//build the tree
		else
		{
			TreeNode root = null;
			root = BuildTree(train_attributes, train_examples, first_class_value, second_class_value, m);

			//we aren't generating learning curve
			if(!learning_curve && !cross_validation)
			{
				//if we're not outputting to file
				if(!to_file)
				{
					PrintTree(root, 0);
					if(!only_tree)
						PrintAllEvaluate(root, test_examples);
				}
				//outputting to file
				else
				{
					PrintTreeFile(root, 0);
					if(!only_tree)
						PrintAllEvaluateFile(root, test_examples);
				}
			}
			//generating learning curve correctly classified
			else if(learning_curve)
			{
				correct = Evaluate(root, test_examples);
			}
			//generating cross-validation correctly classified
			else if(cross_validation)
			{
				//if we're not outputting to file
				if(!to_file)
					PrintTree(root, 0);
				//outputting to file
				else
					PrintTreeFile(root, 0);

				correct = Evaluate(root, test_examples);
			}
		}

		return correct;
	}

	//print off learning curve information
	public static void LearningCurve(Attributes train_attributes, Examples train_examples, Examples test_examples, String first_class_value, String second_class_value, int m)
	{
		double average = 0, accuracy = 0, min = 1, max = 0;
		double sum = 0, count = 0;
		int eval_val = 0;
		DecimalFormat formatter = new DecimalFormat("0.000000");
		int size = 0;

		//initialize training sizes to 1/8 of the training data
		size = (int)Math.floor(train_examples.GetExamplesCount() * .125);

		//training size trees of 1/8 the training data
		for(int i = 0; i < 10; i++)
		{
			//sample the training set
			Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

			//get an evaluation of the tree
			eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

			//get accuracy and min/max of tree evaluation
			accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
			sum+=accuracy;
			count++;
			min = Min(min, accuracy);
			max = Max(max, accuracy);
		}
		//find average of all 10 tree accuracies
		average = (double)sum/(double)count;

		//print information
		System.out.println("*************************TRAINING SIZE: " + size + "*************************");
		System.out.println("AVG: " + formatter.format(average));
		System.out.println("MIN: " + formatter.format(min));
		System.out.println("MAX: " + formatter.format(max));
		accuracy = 0;
		average = 0;
		min = 1;
		max = 0;
		sum = 0;
		count = 0;

		//initialize training sizes to 1/4 of the training data
		size = (int)Math.floor(train_examples.GetExamplesCount() * .25);

		//training size trees of 1/4 the training data
		for(int i = 0; i < 10; i++)
		{
			//sample the training set
			Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

			//get an evaluation of the tree
			eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

			//get accuracy and min/max of tree evaluation
			accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
			sum+=accuracy;
			count++;
			min = Min(min, accuracy);
			max = Max(max, accuracy);
		}
		//find average of all 10 tree accuracies
		average = (double)sum/(double)count;

		//print information
		System.out.println("*************************TRAINING SIZE: " + size + "*************************");
		System.out.println("AVG: " + formatter.format(average));
		System.out.println("MIN: " + formatter.format(min));
		System.out.println("MAX: " + formatter.format(max));
		accuracy = 0;
		average = 0;
		min = 1;
		max = 0;
		sum = 0;
		count = 0;

		//initialize training sizes to 1/2 of the training data
		size = (int)Math.floor(train_examples.GetExamplesCount() * .5);

		//training size trees of 1/2 the training data
		for(int i = 0; i < 10; i++)
		{
			//sample the training set
			Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

			//get an evaluation of the tree
			eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

			//get accuracy and min/max of tree evaluation
			accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
			sum+=accuracy;
			count++;
			min = Min(min, accuracy);
			max = Max(max, accuracy);
		}
		//find average of all 10 tree accuracies
		average = (double)sum/(double)count;

		//print information
		System.out.println("*************************TRAINING SIZE: " + size + "*************************");
		System.out.println("AVG: " + formatter.format(average));
		System.out.println("MIN: " + formatter.format(min));
		System.out.println("MAX: " + formatter.format(max));
		accuracy = 0;
		average = 0;
		min = 1;
		max = 0;
		sum = 0;
		count = 0;


		//training size tree of full training set
		//get an evaluation of the tree
		eval_val = BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);

		//get accuracy and min/max of tree evaluation
		accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();

		//print information
		System.out.println("*************************TRAINING SIZE: " + train_examples.GetExamplesCount() + "*************************");
		System.out.println("AVG: " + formatter.format(accuracy));
		System.out.println("MIN: " + formatter.format(accuracy));
		System.out.println("MAX: " + formatter.format(accuracy));
	}

	//print off learning curve information to file
	public static void LearningCurveFile(Attributes train_attributes, Examples train_examples, Examples test_examples, String first_class_value, String second_class_value, int m)
	{
		try
		{
			double average = 0, accuracy = 0, min = 1, max = 0;
			double sum = 0, count = 0;
			int eval_val = 0;
			DecimalFormat formatter = new DecimalFormat("0.000000");
			int size = 0;

			//initialize training sizes to 1/8 of the training data
			size = (int)Math.floor(train_examples.GetExamplesCount() * .125);

			//training size trees of 1/8 the training data
			for(int i = 0; i < 10; i++)
			{
				//sample the training set
				Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

				//get an evaluation of the tree
				eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

				//get accuracy and min/max of tree evaluation
				accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
				sum+=accuracy;
				count++;
				min = Min(min, accuracy);
				max = Max(max, accuracy);
			}
			//find average of all 10 tree accuracies
			average = (double)sum/(double)count;

			//print information
			writer.write("*************************TRAINING SIZE: " + size + "*************************");
			writer.newLine();
			writer.write("AVG: " + formatter.format(average));
			writer.newLine();
			writer.write("MIN: " + formatter.format(min));
			writer.newLine();
			writer.write("MAX: " + formatter.format(max));
			writer.newLine();
			accuracy = 0;
			average = 0;
			min = 1;
			max = 0;
			sum = 0;
			count = 0;

			//initialize training sizes to 1/4 of the training data
			size = (int)Math.floor(train_examples.GetExamplesCount() * .25);

			//training size trees of 1/4 the training data
			for(int i = 0; i < 10; i++)
			{
				//sample the training set
				Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

				//get an evaluation of the tree
				eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

				//get accuracy and min/max of tree evaluation
				accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
				sum+=accuracy;
				count++;
				min = Min(min, accuracy);
				max = Max(max, accuracy);
			}
			//find average of all 10 tree accuracies
			average = (double)sum/(double)count;

			//print information
			writer.write("*************************TRAINING SIZE: " + size + "*************************");
			writer.newLine();
			writer.write("AVG: " + formatter.format(average));
			writer.newLine();
			writer.write("MIN: " + formatter.format(min));
			writer.newLine();
			writer.write("MAX: " + formatter.format(max));
			writer.newLine();
			accuracy = 0;
			average = 0;
			min = 1;
			max = 0;
			sum = 0;
			count = 0;

			//initialize training sizes to 1/2 of the training data
			size = (int)Math.floor(train_examples.GetExamplesCount() * .5);

			//training size trees of 1/2 the training data
			for(int i = 0; i < 10; i++)
			{
				//sample the training set
				Examples temp = StratifiedSampling(train_examples, size, first_class_value, second_class_value);

				//get an evaluation of the tree
				eval_val = BuildDecisionTree(train_attributes, temp, test_examples, first_class_value, second_class_value, m);

				//get accuracy and min/max of tree evaluation
				accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
				sum+=accuracy;
				count++;
				min = Min(min, accuracy);
				max = Max(max, accuracy);
			}
			//find average of all 10 tree accuracies
			average = (double)sum/(double)count;

			//print information
			writer.write("*************************TRAINING SIZE: " + size + "*************************");
			writer.newLine();
			writer.write("AVG: " + formatter.format(average));
			writer.newLine();
			writer.write("MIN: " + formatter.format(min));
			writer.newLine();
			writer.write("MAX: " + formatter.format(max));
			writer.newLine();
			accuracy = 0;
			average = 0;
			min = 1;
			max = 0;
			sum = 0;
			count = 0;


			//training size tree of full training set
			//get an evaluation of the tree
			eval_val = BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);

			//get accuracy and min/max of tree evaluation
			accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();

			//print information
			writer.write("*************************TRAINING SIZE: " + train_examples.GetExamplesCount() + "*************************");
			writer.newLine();
			writer.write("AVG: " + formatter.format(accuracy));
			writer.newLine();
			writer.write("MIN: " + formatter.format(accuracy));
			writer.newLine();
			writer.write("MAX: " + formatter.format(accuracy));
			writer.newLine();
		}
		catch(IOException ioe)
		{
			System.out.println("IO exception");
		}
	}

	//perform statified sampling
	public static Examples StratifiedSampling(Examples examples, int number, String first_class_value, String second_class_value)
	{
		Examples examples_subset = new Examples(first_class_value, second_class_value);
		Random rand = new Random();

		int extra = rand.nextInt(100);
		double first_class_number = ((double)examples.GetFirstClassCount()/(double)examples.GetExamplesCount()) * (double)number;
		double second_class_number = ((double)examples.GetSecondClassCount()/(double)examples.GetExamplesCount()) * (double)number;
		int first_class_pick = 0;
		int second_class_pick = 0;

		//if number of first class examples to be used is closer to its ceiling
		if(first_class_number % Math.floor(first_class_number) > second_class_number % Math.floor(second_class_number))
		{
			//set number to choose from first and second class
			first_class_pick = (int)Math.ceil(first_class_number);
			second_class_pick = (int)Math.floor(second_class_number);
		}
		//if number of second class examples to be used is closer to its ceiling
		else if(second_class_number % Math.floor(second_class_number) > first_class_number % Math.floor(first_class_number))
		{
			//set number to choose from first and second class
			second_class_pick = (int)Math.ceil(second_class_number);
			first_class_pick = (int)Math.floor(first_class_number);
		}
		//both are same distance to their ceiling, randomly choose which gets its ceiling
		else
		{
			//first class gets ceiling
			if(extra < 50)
			{
				//set number to choose from first and second class
				first_class_pick = (int)Math.ceil(first_class_number);
				second_class_pick = (int)Math.floor(second_class_number);
			}
			//second class gets ceiling
			else
			{
				//set number to choose from first and second class
				second_class_pick = (int)Math.ceil(second_class_number);
				first_class_pick = (int)Math.floor(first_class_number);
			}
		}

		//create array list of first and second class examples
		ArrayList<Example> first_class_examples = new ArrayList<Example>();
		ArrayList<Example> second_class_examples = new ArrayList<Example>();
		Example example_walker = examples.GetExamplesHead();
		while(example_walker != null)
		{
			if(example_walker.GetClassValue().equals(first_class_value))
				first_class_examples.add(example_walker);
			else if(example_walker.GetClassValue().equals(second_class_value))
				second_class_examples.add(example_walker);

			example_walker = example_walker.GetNext();
		}

		//randomly shuffle first and second class examples
		Collections.shuffle(first_class_examples);
		Collections.shuffle(second_class_examples);

		//choose first class values
		for(int i = 0; i < first_class_pick; i++)
		{
			Example ex = new Example();
			ex.CopyExample(first_class_examples.get(i));
			examples_subset.AddExample(ex);
		}
		//choose second class values
		for(int j = 0; j < second_class_pick; j++)
		{
			Example ex = new Example();
			ex.CopyExample(second_class_examples.get(j));
			examples_subset.AddExample(ex);
		}

		//return statified sample
		return examples_subset;
	}

	//calculate minimum of numbers
	public static double Min(double current_min, double candidate)
	{
		if(candidate < current_min)
			return candidate;
		else
			return current_min;
	}

	//calculate maximum of numbers
	public static double Max(double current_max, double candidate)
	{
		if(candidate > current_max)
			return candidate;
		else
			return current_max;
	}

	//perform cross validation
	public static void CrossValidation(Attributes train_attributes, Examples examples, String first_class_value, String second_class_value, int m, int k)
	{
		Random rand = new Random();
		int samples = (int)Math.floor(examples.GetExamplesCount()/k);
		Example[][] folds = new Example[k][samples];
		double accuracy = 0, total_accuracy = 0;
		int eval_val;
		DecimalFormat formatter = new DecimalFormat("0.000000");

		for(int i = 0; i < k; i++)
		{
			Example[] temp = new Example[samples];

			for(int j = 0; j < samples; j++)
			{
				temp[j] = examples.RemoveExample(rand.nextInt(examples.GetExamplesCount()));
			}

			folds[i] = temp;
		}

		//perform k rounds of testing
		for(int i = 0; i < k; i++)
		{
			Examples train_examples = new Examples(first_class_value, second_class_value), test_examples = new Examples(first_class_value, second_class_value);

			for(int j = 0; j < k; j++)
			{
				//create testing set
				if(j == i)
				{
					for(int l = 0; l < folds[i].length; l++)
					{
						Example temp = new Example();
						temp.CopyExample(folds[j][l]);
						test_examples.AddExample(temp);
					}
				}
				//create training set
				else
				{
					for(int l = 0; l < folds[i].length; l++)
					{
						Example temp = new Example();
						temp.CopyExample(folds[j][l]);
						train_examples.AddExample(temp);
					}
				}
			}

			//get an evaluation of the tree
			eval_val = BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
			accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
			total_accuracy += accuracy;
			System.out.println("Fold: " + (i+1) + " Accuracy: " + formatter.format(accuracy));
		}
		System.out.println("Total: " + formatter.format(total_accuracy/k));
	}

	//perform cross validation
	public static void CrossValidationFile(Attributes train_attributes, Examples examples, String first_class_value, String second_class_value, int m, int k)
	{
		try
		{
			Random rand = new Random();
			int samples = (int)Math.floor(examples.GetExamplesCount()/k);
			Example[][] folds = new Example[k][samples];
			double accuracy = 0, total_accuracy = 0;
			int eval_val;
			DecimalFormat formatter = new DecimalFormat("0.000000");

			for(int i = 0; i < k; i++)
			{
				Example[] temp = new Example[samples];

				for(int j = 0; j < samples; j++)
				{
					temp[j] = examples.RemoveExample(rand.nextInt(examples.GetExamplesCount()));
				}

				folds[i] = temp;
			}

			//perform k rounds of testing
			for(int i = 0; i < k; i++)
			{
				Examples train_examples = new Examples(first_class_value, second_class_value), test_examples = new Examples(first_class_value, second_class_value);

				for(int j = 0; j < k; j++)
				{
					//create testing set
					if(j == i)
					{
						for(int l = 0; l < folds[i].length; l++)
						{
							Example temp = new Example();
							temp.CopyExample(folds[j][l]);
							test_examples.AddExample(temp);
						}
					}
					//create training set
					else
					{
						for(int l = 0; l < folds[i].length; l++)
						{
							Example temp = new Example();
							temp.CopyExample(folds[j][l]);
							train_examples.AddExample(temp);
						}
					}
				}

				//get an evaluation of the tree
				eval_val = BuildDecisionTree(train_attributes, train_examples, test_examples, first_class_value, second_class_value, m);
				accuracy = (double)eval_val/(double)test_examples.GetExamplesCount();
				total_accuracy += accuracy;
				writer.write("Fold: " + (i+1) + " Accuracy: " + formatter.format(accuracy));
				writer.newLine();
			}
			writer.write("Total: " + formatter.format(total_accuracy/k));
		}
		catch(IOException ioe)
		{
			System.out.println("IO exception");
		}
	}
}
