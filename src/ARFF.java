import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

//class to parse ARFF file
public class ARFF 
{
	private String filename = null;
	private Relation relation = null;
	private Attributes attributes = null;
	private Examples examples = null;
	private String first_class_value = null;
	private String second_class_value = null;
	public Type type;
	
	public enum Type
	{
		TRAINING, TESTING
	}
	
	public ARFF(String filename, Type type)
	{
		this.filename = filename;
		this.type = type;
	}
	
	public String GetFilename()
	{
		return filename;
	}
	
	public void SetFilename(String filename)
	{
		this.filename = filename;
	}
	
	public void ParseFile()
	{
		try
		{
			Scanner scan = new Scanner(new File(filename));
			Attribute attribute_walker = null;
			String line = null;
			attributes = new Attributes();
			boolean data = false;
			
			while(scan.hasNextLine())
			{
				line = scan.nextLine();
				
				if(data)
				{
					String[] data_tokens = line.split("[,]");
					Example ex = new Example();
					attribute_walker = attributes.GetAttributesHead();
					
					for(int i = 0; i < data_tokens.length - 1; i++)
					{
						ex.AddValues(data_tokens[i], attribute_walker.GetAttribute());
						attribute_walker = attribute_walker.GetNext();
					}
					
					ex.SetClassValue(data_tokens[data_tokens.length - 1]);
					examples.AddExample(ex);
				}
				
				if(line.contains("@relation"))
				{
					String[] relation_tokens = line.split("[ ]");
					relation = new Relation(relation_tokens[1]);
				}
				else if(line.contains("@attribute"))
				{
					String[] attribute_tokens = line.split("['{ , , }]+");
					Attribute attr = new Attribute(attribute_tokens[1]);
					
					for(int i = 2; i < attribute_tokens.length; i++)
						attr.AddFeatures(attribute_tokens[i]);
					
					attributes.AddAttribute(attr);
				}
				else if(line.contains("@data"))
				{
					data = true;
					
					first_class_value = attributes.GetAttributesTail().GetFeaturesHead().GetFeature();
					second_class_value = attributes.GetAttributesTail().GetFeaturesTail().GetFeature();
					examples = new Examples(first_class_value, second_class_value);
					
					attributes.RemoveTail();
				}
			}
						
			scan.close();
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("File not found");
		}
	}
	
	public Examples GetExamples()
	{
		return examples;
	}
	
	public Attributes GetAttributes()
	{
		return attributes;
	}
	
	public Relation GetRelation()
	{
		return relation;
	}
	
	public String GetFirstClassValue()
	{
		return first_class_value;
	}
	
	public String GetSecondClassValue()
	{
		return second_class_value;
	}
}
