package demerit;

import java.util.Date;

/**
 * A descriptor class containing all possible information for a patron.
 */
public class PatronData {
	public Integer id;
	public Integer addressId;
	public String name;
	public String phone;
	public String email;
	public String barcode;
	public Date birthday;
	public Date creation;
}