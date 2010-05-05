import java.util.Scanner;


public class Zip {
	
	//return null if good
	//if bad-string why bad
	
	public static String validateZip(String z)
	{
		String val = null;
		if(z.contains("-") && z.length()==10)
		{
			if(z.indexOf("-")==5)
			{
				z = z.replace("-", "");
				
				try
				{
					Integer.parseInt(z);
					return null;
				}
				catch (NumberFormatException e)
				{
					val +="Invalid numerical entry with dash.";
				}
			}
			else
			{
				val+="Invalid zip format. (dash not right).";
			}
			
		}
		else if (z.length()==5)
		{
			try
			{
				Integer.parseInt(z);
				return null;
			}
			catch (NumberFormatException e)
			{
				val+="Invalid numerical entry (no dash).";
			}
		}	
		
		val+="Invalid Zip length.";
		
		return val;
	}
	
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		String zip = "";
		
		while (zip != "x")
		{
		System.out.print("Enter zip: ");
		zip = in.next();
		System.out.println(validateZip(zip));
		}
	}

}
