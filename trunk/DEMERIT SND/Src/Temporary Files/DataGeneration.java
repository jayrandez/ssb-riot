import java.util.*;
public class DataGeneration {
	
	
	public static void main(String[] args)
	{
		
		long seed = System.currentTimeMillis();
		Random gen = new Random(seed);
		
		int randomOut = 0;
		int randomChoice = 0;
		
		ArrayList<Integer> checkedIn = new ArrayList<Integer>();
		ArrayList<Integer> checkedOut = new ArrayList<Integer>();
		
		Scanner in = new Scanner(System.in);
		
		int mediaId = 0;
		int libraryId = 1;
		String type = "";
		
		int year = 0;
		int month = 0;
		int day = 0;
		int yearF = 0;
		int monthF = 0;
		int dayF = 0;
		
		System.out.print("Enter the start year: ");
		year = in.nextInt();
		System.out.print("Enter the start month: ");
		month = in.nextInt();
		System.out.print("Enter the start day: ");
		day = in.nextInt();
		
		System.out.print("Enter the final year: ");
		yearF = in.nextInt();
		System.out.print("Enter the final month: ");
		monthF = in.nextInt();
		System.out.print("Enter the final day: ");
		dayF = in.nextInt();
				
		Calendar start = new GregorianCalendar(year, month, day);
		Calendar end = new GregorianCalendar(yearF, monthF, dayF+1);
		
		//add to the checked in and out array lists
		
		while (!start.equals(end))
		{
			//check out
			
			//generate random number of checkouts
			randomOut = 10 + 34 * gen.nextInt();
			for(int x = 0; x < randomOut; x++)
			{
				randomChoice = gen.nextInt() * (checkedIn.size() - 1);
				
				//check out sql
				
			}
			
			
			//check in
			for(int i = 0;i<checkedOut.size();i++)
			{
				//if date checkedout = 5 day ago or more...
				
				//random num
				//0-15 check in
				//16-20 renew
				//21-99 do nothing
			}
			
			//increment to next day
			start.add(Calendar.DATE, 1);
		}
	}
}