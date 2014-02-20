
//tree node class
public class TreeNode 
{
	private TreeNode left = null;
	private TreeNode right = null;
	private int positive = 0;
	private int negative = 0;
	public Type type;
	
	public enum Type
	{
		ATTRIBUTE, FEATURE, CLASS_VALUE
	}
	
	public TreeNode GetLeft()
	{
		return left;
	}
	
	public void SetLeft(TreeNode node)
	{
		left = node;
	}
	
	public TreeNode GetRight()
	{
		return right;
	}
	
	public void SetRight(TreeNode node)
	{
		right = node;
	}
	
	public int GetPostive()
	{
		return positive;
	}
	
	public void SetPositive(int positive)
	{
		this.positive = positive;
	}
	
	public int GetNegative()
	{
		return negative;
	}
	
	public void SetNegative(int negative)
	{
		this.negative = negative;
	}
}
