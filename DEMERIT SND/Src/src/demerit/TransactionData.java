package demerit;

/**
 * Descriptor class containing all information about a media transaction
 * (checkout/checkin/renew)
 */
public class TransactionData {
	
	public Integer patronId;
	public Integer mediaId;
	public Integer libraryId;
	public String type;
	public java.util.Date checkout;
	public java.util.Date transaction;
	public java.util.Date due;
}
