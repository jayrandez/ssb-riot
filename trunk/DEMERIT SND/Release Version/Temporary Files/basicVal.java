
public class basicVal {
	public static String validateFilled(String s)
	{
		s = s.trim();
		
		if (s.equals(""))
			return "Fill the field.";
		else
			return null;
	}
}
