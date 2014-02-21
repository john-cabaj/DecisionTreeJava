
//tree node class
public class TreeNode 
{
	private TreeNode[] successors = null;
	private Attribute attribute = null;
	private int first_class_value = 0;
	private int second_class_value = 0;
	private String class_value = null;
	public Type type;
	
	public enum Type
	{
		ATTRIBUTE, FEATURE, CLASS_VALUE
	}
	
	public TreeNode(int successors)
	{
		this.successors = new TreeNode[successors];
	}
	
	public TreeNode GetSuccessor(int index)
	{
		return successors[index];
	}
	
	public void SetSuccessor(TreeNode successor, int index)
	{
		successors[index] = successor;
	}
	
	public int GetFirstClassValue()
	{
		return first_class_value;
	}
	
	public void SetFirstClassValue(int first_class_value)
	{
		this.first_class_value = first_class_value;
	}
	
	public int GetSecondClassValue()
	{
		return second_class_value;
	}
	
	public void SetSecondClassValue(int second_class_value)
	{
		this.second_class_value = second_class_value;
	}
	
	public String GetClassValue()
	{
		return class_value;
	}
	
	public void SetClassValue(String class_value)
	{
		this.class_value = class_value;
	}
	
	public Attribute GetAttribute()
	{
		return attribute;
	}
	
	public void SetAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}
}
