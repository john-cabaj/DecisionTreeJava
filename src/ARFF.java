import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

//class to parse ARFF file
public class ARFF 
{
	private String filename = null;
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
			String line = null;
			
			while(scan.hasNextLine())
			{
				line = scan.nextLine();
				
				if(line.contains("@relative"))
				{
					
				}
			}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("File not found");
		}
	}
}
