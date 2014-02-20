
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
		
		
		
	}

}
