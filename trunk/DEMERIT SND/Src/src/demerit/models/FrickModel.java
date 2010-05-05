package demerit.models;

import java.util.ArrayList;

import demerit.ConnectionData;
import demerit.JAutoCompleteScroll;
import demerit.MediaData;
import demerit.Model;
import java.util.Random;
import java.sql.SQLException;

/**
 * TEMPORARY MODEL
 * Used to insert large quantities of data into the database during production.
 */
public class FrickModel extends Model {

	MediaModel mediaModel;
	PersonModel personModel;
	CategoryModel categoryModel;
	
	public FrickModel(ConnectionData connectionData) {
		super(connectionData);
		mediaModel = new MediaModel(connectionData);
		categoryModel = new CategoryModel(connectionData);
		personModel = new PersonModel(connectionData);
	}
	
	public void insert(String book, String author) throws SQLException {
		MediaData data = new MediaData();
		data.callNumber = generateCallNumber(author);
		data.barcode = generateBarcode();
		data.type = 0;
		data.format = 0;
		data.genre = (int)(Math.random()*MediaData.genreValues.length);
		data.copyright = "2006";
		data.description = "Lorem ipsum dolor sit amet.";
		data.edition = "1";
		data.isbn = generateISBN();
		data.length = new Integer((int)(Math.random()*1000)).toString();
		data.library = 1;
		data.title = book;
		
		Integer mediaId = mediaModel.insertMedia(data, 1);
		System.out.println("MediaID: " + mediaId);
		
		ArrayList<String> categories = categoryModel.listCategories();
		int numCategories = (int)(Math.random()*3);
		for(int i = 0; i < numCategories; i++) {
			int categoryId = (int)(Math.random()*(categories.size()-1));
			System.out.println("Category: " + categoryId);
			categoryModel.categoryToMedia(categories.get(categoryId), mediaId);
		}
		
		personModel.authorToMedia(author, mediaId);
	}
	
	private String generateISBN() {
		String barcode = "";
		for(int i = 0; i < 13; i++) {
			Integer digit = (int)(Math.random()*9.0);
			barcode += digit.toString();
		}
		return barcode;
	}
	
	private String generateCallNumber(String author) {
		String callnumber = "980.";
		for(int i = 0; i < 4; i++) {
			Integer digit = (int)(Math.random()*9.0);
			callnumber += digit.toString();
		}
		int substr = 3;
		if(author.length() < 3)
				substr = author.length();
		callnumber += " " + author.substring(0, substr);
		return callnumber;
	}
	
	private String generateBarcode() {
		String[] array = new String[14];
		int r = 0;
		int sum = 0;
		Random random = new Random();
		String barcode = "";

		//get the entity code
		array[0] = "1";
		
		//enter library code
		array[1] = "1";
		array[2] = "3";
		array[3] = "3";
		array[4] = "7";
		
		//get random number
		for (int x = 5; x <= 12; x++){
			r = random.nextInt(9);
			array[x] = (new Integer(r).toString());
		}
		
		//odd add and mult 3, even just add
		for (int x = 0; x<= 12; x++){
			if ((x+1)==0){
				sum += Integer.parseInt(array[x])*3;
			}
			else if(x%2==0){
				sum += Integer.parseInt(array[x]);
			}
			else {
				sum += Integer.parseInt(array[x])*3;
			}
		}
		
		//get check sum number
		int y = 10-(sum % 10);
		
		array[13] = (new Integer(y).toString());
		
		for (int x = 0; x <= 12; x++){
			barcode += array[x];
		}
		
		return barcode;
	}
}
