
//tree node class
public class TreeNode 
{
	private TreeNode[] successors = null;
	private TreeNode parent = null;
	private Attribute attribute = null;
	private Feature feature = null;
	private double midpoint = 0;
	private int first_class_value = 0;
	private int second_class_value = 0;
	private String class_value = null;
	public Type type;
	
	//type enumeration
	public enum Type
	{
		ATTRIBUTE, FEATURE, CLASS_VALUE
	}
	
	//constructor sets number of succesors
	public TreeNode(int successors)
	{
		this.successors = new TreeNode[successors];
	}
	
	//get successor
	public TreeNode GetSuccessor(int index)
	{
		return successors[index];
	}
	
	//set successor
	public void SetSuccessor(TreeNode successor, int index)
	{
		successors[index] = successor;
	}
	
	//get first class value
	public int GetFirstClassValue()
	{
		return first_class_value;
	}

	//get set first class value
	public void SetFirstClassValue(int first_class_value)
	{
		this.first_class_value = first_class_value;
	}

	//get second class value
	public int GetSecondClassValue()
	{
		return second_class_value;
	}

	//get set second class value
	public void SetSecondClassValue(int second_class_value)
	{
		this.second_class_value = second_class_value;
	}
	
	//get leaf node class value
	public String GetClassValue()
	{
		return class_value;
	}

	//set leaf node class value
	public void SetClassValue(String class_value)
	{
		this.class_value = class_value;
	}
	
	//get attribute
	public Attribute GetAttribute()
	{
		return attribute;
	}
	
	//set attribute
	public void SetAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}
	
	//get feature
	public Feature GetFeature()
	{
		return feature;
	}
	
	//set feature
	public void SetFeature(Feature feature)
	{
		this.feature = feature;
	}
	
	//get parent node
	public TreeNode GetParent()
	{
		return parent;
	}
	
	//set parent node
	public void SetParent(TreeNode parent)
	{
		this.parent = parent;
	}
	
	//get all successors
	public TreeNode[] GetSuccessors()
	{
		return successors;
	}
	
	//get midpoint
	public double GetMidpoint()
	{
		return midpoint;
	}
	
	//set midpoint
	public void SetMidpoint(double midpoint)
	{
		this.midpoint = midpoint;
	}
}
