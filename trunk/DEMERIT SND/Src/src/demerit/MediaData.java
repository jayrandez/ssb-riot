package demerit;
import java.util.ArrayList;

/**
 * A descriptor class holding all possible information about a piece of media.
 */
public class MediaData {
	public static String[] typeValues = {
		"Literature",
		"Music",
		"Cinema"
	};
	public static String[] formatValues = {
		"Paperback",
		"Magazine",
		"Periodical",
		"CD",
		"Tape",
		"DVD",
		"VHS",
		"BluRay",
		"Hardcover"
	};
	public static String[] genreValues = {
		"Fiction",
		"Non-Fiction",
		"Reference",
		"Poetry",
		"Drama",
		"Essay",
		"Biography",
		"Journalism"
	};
	public Integer id;
	public Integer type;
	public Integer format;
	public Integer genre;
	public Integer library;
	public String title;
	public String length;
	public String callNumber;
	public String barcode;
	public String copyright;
	public String description;
	public String edition;
	public String isbn;
	public ArrayList<String> producers;
	public ArrayList<String> actors;
	public ArrayList<String> authors;
	public ArrayList<String> categories;
	public Boolean selected;
	public String status;
}
