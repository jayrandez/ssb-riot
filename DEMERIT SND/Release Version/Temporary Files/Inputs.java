

public class Inputs {
 
	public String validatePhone(String number)
	{
		String er = "Error: ";
		try
		{
			Integer.parseInt(number);
		}
		catch(NumberFormatException e)
		{
			er.concat("Numeric characters only ");
		}
		if(number.length() != 10)
			er.concat("Phone number must be 10 characters ");
		if(number.startsWith("1"))
			er.concat("Phone number must start with 1");
		if(er == "Error: ")
			return null;
		else
			return er;
	}
	public String validateStreet(String street)
	{
		String er = "Error: ";
		if(street.startsWith("\\d"));
			er.concat("Streets address must begin with a number ");
		if(er == "Error: ")
			return null;
		else
			return er;
	}
	public String validateISBN(String bookNum)
	{
		String er = "Error: ";
		try
		{
			Integer.parseInt(bookNum);
		}
		catch(NumberFormatException e)
		{
			er.concat("Numeric characters only ");
		}
		if(bookNum.length() != 13)
			er.concat("ISBN must be 13 characters ");
		if(er == "Error: ")
			return null;
		else
			return er;
	}
}
