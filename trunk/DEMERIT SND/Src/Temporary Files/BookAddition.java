import java.util.*;
import java.sql.*;
import java.io.*;

import demerit.ConnectionData;
import demerit.models.FrickModel;

public class BookAddition {

	public static void main(String[] args)
	{
		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> author = new ArrayList<String>();
		String temp = "";
		String type = "";
		
		String pathname = "Temporary Files/Book List.txt";
		File file = new File(pathname);
		Scanner input = null;
		try
		{
			input = new Scanner(file);
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("Could not open file");
			System.exit(1);
		}
		
		ConnectionData cData = new ConnectionData();
		cData.dbname = "bpa2010";
		cData.driver = "mysql";
		cData.host = "devmonger.com";
		cData.pass = "1q2w3e4r5t";
		cData.port = "3306";
		cData.user = "bpa2010-general";
		
		FrickModel model = new FrickModel(cData);
		if(model.isSane()) {
			System.out.println("System sane. Start adding.");
			while (input.hasNext())
			{
				temp = input.nextLine();
				if (temp.contains("/"))
				{
					temp = temp.replace("/", "");
					type = temp.trim();
				}
				else if (temp.contains(" by "))
				{
				title.add(temp.substring(0, temp.indexOf(" by ")));
				//System.out.println(temp.substring(0, temp.indexOf(" by ")));
				
				author.add(temp.substring(temp.indexOf(" by ")+4));
				//System.out.println(temp.substring(temp.indexOf(" by ")+4));
				}	
			}
			for(int i = 0; i < title.size(); i++) {
				//System.out.println("Inserting: " + title.get(i) + " " + author.get(i));
				try {
					if(author.get(i) != null)
						model.insert(title.get(i), author.get(i));
					else
						System.out.println("null author");
				}
				catch(SQLException ex) {
					System.out.println("FAILED");
				}
			}
		}
	}
}
