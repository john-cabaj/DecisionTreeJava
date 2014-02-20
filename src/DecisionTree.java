
//decision tree class
public class DecisionTree 
{

	//main method
	public static void main(String[] args) 
	{
		ARFF parser = new ARFF("heart_train.arff", ARFF.Type.TRAINING);
		parser.ParseFile();
	}

}
