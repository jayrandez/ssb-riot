package demerit;

/**
 * NEEDS WORK
 * Simple class used to validate fields upon database insertion.
 */
public class Validation {
	
	public static String validateFilled(String s)
	{
		s = s.trim();
		if (s.equals(""))
			return "Field can't be empty.";
		else
			return null;
	}
	
	public static String validateZip(String z)
	{
		String val = "";
		if(z.contains("-") && z.length()==10) {
			if(z.indexOf("-")==5) {
				z = z.replace("-", "");
				try {
					Integer.parseInt(z);
					return null;
				}
				catch (NumberFormatException e) {
					val +="Invalid numerical entry with dash. ";
				}
			}
			else {
				val+="Invalid zip format. (dash not right). ";
			}
		}
		else if (z.length()==5) {
			try {
				Integer.parseInt(z);
				return null;
			}
			catch (NumberFormatException e) {
				val+="Invalid numerical entry (no dash). ";
			}
		}	
		val+="Invalid Zip length. ";
		return val;
	}
	
	public static String validatePhone(String number)
	{
		String er = "";
		try {
			Integer.parseInt(number);
		}
		catch(NumberFormatException e) {
			er.concat("Numeric characters only ");
		}
		if(number.length() != 10)
			er.concat("Phone number must be 10 characters ");
		if(number.startsWith("1"))
			er.concat("Phone number must start with 1");
		if(er.equals(""))
			return null;
		else
			return er;
	}
	
	public static String validateStreet(String street)
	{
		String er = null;
		if(!street.startsWith("\\d"));
			er = "Streets address must begin with a number ";
		return er;
	}
	
	public static String validateISBN(String bookNum)
	{
		String er = "";
		try {
			Integer.parseInt(bookNum);
		}
		catch(NumberFormatException e) {
			er.concat("Numeric characters only ");
		}
		if(bookNum.length() != 13)
			er.concat("ISBN must be 13 characters ");
		if(er.equals(""))
			return null;
		else
			return er;
	}

}
