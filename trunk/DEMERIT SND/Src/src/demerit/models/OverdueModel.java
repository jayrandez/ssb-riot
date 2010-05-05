package demerit.models;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import demerit.ConnectionData;
import demerit.Core;
import demerit.MediaData;
import demerit.Model;
import demerit.PatronData;
import demerit.TransactionData;

/**
 * Model used by the overdue report generating controller.
 */
public class OverdueModel extends Model {
	
	private PatronModel patronModel;
	private MediaModel mediaModel;
	private TransactionModel transactionModel;

	public OverdueModel(ConnectionData connectionData) {
		super(connectionData);
		patronModel = new PatronModel(connectionData);
		mediaModel = new MediaModel(connectionData);
		transactionModel = new TransactionModel(connectionData);
	}
	
	public ArrayList<String[]> getRecords() throws SQLException {
		ArrayList<String[]> records = new ArrayList<String[]>();

		ArrayList<Integer> patronIds = patronModel.listPatronIds();
		for(Integer patronId: patronIds) {
			ArrayList<Integer> bookIds = transactionModel.getPatronBooks(patronId);
			PatronData patronDescriptor = patronModel.getPatron(patronId);
			for(Integer mediaId: bookIds) {
				MediaData descriptor = mediaModel.getMedia(mediaId);
				TransactionData transaction = transactionModel.getLastTransaction(descriptor.id);
				Date due = transaction.due;
				Integer days = new Date().compareTo(due);
				Boolean overdue = false;
				Double fees = 0.0;
				if(days > 0) {
					overdue = true;
					long diff = new Date().getTime() - due.getTime();
					days = (int)diff / (1000 * 60 * 60 * 24);
					fees += (Core.dailyCharge * (double)days);
				}
				if(overdue) {
					String title = descriptor.title;
					if(title.length() > 50)
						title = title.substring(0, 50);
					String author = "";
					if(descriptor.authors.size() > 0)
						author = descriptor.authors.get(0);
					DecimalFormat formatter = new DecimalFormat("\\$0.00");
					String[] record = new String[] {
						title, author, days.toString(), patronDescriptor.name, patronDescriptor.phone, formatter.format(fees)
					};
					records.add(record);
				}
			}
		}
		return records;
	}

}
