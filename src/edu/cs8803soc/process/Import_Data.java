package edu.cs8803soc.process;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

import edu.cs8803soc.dal.MySQLAccess;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;

public class Import_Data {

	ArrayList<Business> businesses;
	ArrayList<Review> reviews;
	ArrayList<Checkin> checkins;
	ArrayList<Tip> tips;
	ArrayList<String> business_ids;
	
	public Import_Data(){
		this.businesses = new ArrayList<Business>();
		this.reviews = new ArrayList<Review>();
		this.checkins = new ArrayList<Checkin>();
		this.tips = new ArrayList<Tip>();
		this.business_ids = new ArrayList<String>();
	}
	
	public void buildLists() {
		final JFileChooser jfc = new JFileChooser();
		int returnVal = jfc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening: " + file.getName() + ".\n");
            
            try {
				Scanner scan = new Scanner(file);
				String currentLine;
				int i = 0,k=0,l=0,m=0;
				
				JProgressBar progress = new JProgressBar(0, 100);
				progress.setValue(50);
				progress.setVisible(true);
				
				while(scan.hasNextLine() && (i<10 || k<10 || l<10 || m<10)){
					currentLine = scan.nextLine();
					//System.out.println(currentLine);
					try{
						JSONObject js = new JSONObject(currentLine);

						if(js.getString("type").contains("business") && i<10){
							if(this.businesses.size() == 0)
								System.out.println("Collecting businesses...");
							
							JSONArray categories = js.getJSONArray("categories");
							
							boolean isRestaurant = false;
							if(categories != null)
							{
								for(int a = 0; a < js.length(); a++){
									if(!categories.isNull(a) && categories.getString(a).contains("Restaurants")){
										isRestaurant = true;
										break;
									}
								}
							}
							if(isRestaurant)
							{
								String business_id = js.getString("business_id");
								int review_count = js.getInt("review_count");
								String state = js.getString("state");
								String full_address = js.getString("full_address");
								String type = js.getString("type");
								String city = js.getString("city");
								String neighborhoods = js.get("neighborhoods").toString();
								double stars = js.getDouble("stars");
								String name = js.getString("name");
								String business_categories = js.get("categories").toString();
								double longitude = js.getDouble("longitude");
								double latitude = js.getDouble("latitude");
								String attributes = js.get("attributes").toString();
								
								// create object & add to business list
								this.businesses.add(
										new Business(business_id, review_count, state, full_address, type, city,
												neighborhoods, stars, name, business_categories, longitude, latitude,
												attributes));
								this.business_ids.add(business_id);
								
								// i++;
							}
						}
						else if(js.getString("type").contains("review") && m<10){
							if(this.reviews.size() == 0)
								System.out.println("Collecting reviews...");

							String review_id = js.getString("review_id");
							String business_id = js.getString("business_id");
							String user_id = js.getString("user_id");
							String text = js.getString("text");
							double stars = js.getDouble("stars");
							JSONObject votes = js.getJSONObject("votes");
							int useful_votes = votes.getInt("useful");
							String type = js.getString("type");
							String date = js.getString("date");
							
							if(this.business_ids.contains(business_id)){
								this.reviews.add(new Review(review_id, business_id, user_id, text, stars, useful_votes, type, date));
							}
							
							// m++;
						}/*
						else if(js.getString("type").contains("checkin") && k<10){
							if(this.checkins.size() == 0)
								System.out.println("Collecting checkins...");
							
							String business_id = js.getString("business_id");
							String checkin_info = js.get("checkin_info").toString();
							String type = js.getString("type");
							
							if(this.business_ids.contains(business_id)){
								this.checkins.add(new Checkin(business_id, checkin_info, type));
							}
							
							// k++;
						}
						else if(js.getString("type").contains("tip") && l<10){
							if(this.tips.size() == 0)
								System.out.println("Collecting tips...");
							
							String business_id = js.getString("business_id");
							String user_id = js.getString("user_id");
							String text = js.getString("text");
							int likes = js.getInt("likes");
							String type = js.getString("type");
							String date = js.getString("date");
							
							if(this.business_ids.contains(business_id)){
								this.tips.add(new Tip(business_id, user_id, text, likes, type, date));
							}
							
							// l++;
						}
						*/
					}
					catch(Exception e){
						//System.out.println("Not added due to error: " + e.getMessage());
						continue;
					}
				}
				scan.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        } else
            System.out.println("no file selected");
	}
	
	public void importLists(){
		int numBusinessEntries = this.businesses.size();
		int numReviewEntries = this.reviews.size();
		int numCheckinEntries = this.checkins.size();
		int numTipEntries = this.tips.size();
		
		int totalEntries = numBusinessEntries + numReviewEntries + numCheckinEntries + numTipEntries;
		int businessCounter = 0;
		int reviewCounter = 0;
		int checkinCounter = 0;
		int tipCounter = 0;
		double percent = 0;
		
		MySQLAccess mysql = new MySQLAccess();
		boolean successfulInsert = false;
		
		System.out.println("Inserting businesses...");
		System.out.println("Total Progress: 0% (0/"	+ Integer.toString(totalEntries) + ")");
		for(Business business : this.businesses){
			successfulInsert = business.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert business. (Name: " + business.name 
						+ " | ID: " + business.business_id + ")");
			businessCounter++;
			
			percent = (double)businessCounter/(double)numBusinessEntries*100;
			System.out.println("Business Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter) + "/"
					+ Integer.toString(numBusinessEntries) + ")");
		}
		
		System.out.println("Inserting reviews...");
		percent = (double)businessCounter/(double)totalEntries*100;
		System.out.println("Total Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter) + "/"
				+ Integer.toString(totalEntries) + ")");
		
		for(Review review : this.reviews){
			successfulInsert = review.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert review. (ID: " + review.review_id + ")");
			reviewCounter++;
			
			percent = (double)reviewCounter/(double)numReviewEntries*100;
			System.out.println("Review Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(reviewCounter) + "/"
					+ Integer.toString(numReviewEntries) + ")");
		}
		
		System.out.println("Inserting tips...");
		percent = (double)(businessCounter + reviewCounter)/(double)totalEntries*100;
		System.out.println("Total Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter + reviewCounter) + "/"
				+ Integer.toString(totalEntries) + ")");
		
		for(Tip tip : this.tips){
			successfulInsert = tip.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert tip for business ID: " + tip.business_id + ")");
			tipCounter++;
			
			percent = (double)tipCounter/(double)numTipEntries*100;
			System.out.println("Tip Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(tipCounter) + "/"
					+ Integer.toString(numTipEntries) + ")");
		}
		
		System.out.println("Inserting checkins...");
		percent = (double)(businessCounter + tipCounter + reviewCounter)/(double)totalEntries*100;
		System.out.println("Total Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter + tipCounter + reviewCounter) + "/"
				+ Integer.toString(totalEntries) + ")");
		
		for(Checkin checkin : this.checkins){
			successfulInsert = checkin.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert checkin for business ID: " + checkin.business_id + ")");
			checkinCounter++;
			
			percent = (double)checkinCounter/(double)numCheckinEntries*100;
			System.out.println("Tip Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(checkinCounter) + "/"
					+ Integer.toString(numCheckinEntries) + ")");
		}
		
		percent = (double)(businessCounter + tipCounter + checkinCounter + reviewCounter)/(double)totalEntries*100;
		System.out.println("Total Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter + tipCounter + checkinCounter + reviewCounter) + "/"
				+ Integer.toString(totalEntries) + ")");
		
	}
	
	public void importBusinesses(){
		boolean successfulInsert = false;
		int numBusinessEntries = this.businesses.size();
		int businessCounter = 0;
		double percent = 0;
		
		MySQLAccess mysql = new MySQLAccess();
		
		// Get starting point by checking current number of records in the table
		int startingPoint = Business.getNumberOfRecordsInDatabase(mysql);
		if(startingPoint == -1){
			System.out.println("Error in getting number of records in business table. Returned -1.");
			return;
		}
		else{
			businessCounter = startingPoint;
		}
		
		System.out.println("Inserting businesses...");
		Business business;
		for(int i = startingPoint; i < this.businesses.size(); i++){
			business = this.businesses.get(i);
			successfulInsert = business.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert business. (Name: " + business.name 
						+ " | ID: " + business.business_id + ")");
			businessCounter++;
			
			percent = (double)businessCounter/(double)numBusinessEntries*100;
			System.out.println("Business Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(businessCounter) + "/"
					+ Integer.toString(numBusinessEntries) + ")");
		}
		mysql.close();
	}
	
	public void importReviews(){
		boolean successfulInsert = false;
		int numReviewEntries = this.reviews.size();
		int reviewCounter = 0;
		double percent = 0;
		
		MySQLAccess mysql = new MySQLAccess();
		
		// Get starting point by checking current number of records in the table
		int startingPoint = Review.getNumberOfRecordsInDatabase(mysql);
		if(startingPoint == -1){
			System.out.println("Error in getting number of records in review table. Returned -1.");
			return;
		}
		else{
			reviewCounter = startingPoint;
		}
		
		System.out.println("Inserting reviews...");
		Review review;
		for(int i = startingPoint; i < this.reviews.size(); i++){
			review = this.reviews.get(i);
			successfulInsert = review.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert review. (ID: " + review.review_id + ")");
			reviewCounter++;
			
			percent = (double)reviewCounter/(double)numReviewEntries*100;
			System.out.println("Review Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(reviewCounter) + "/"
					+ Integer.toString(numReviewEntries) + ")");
		}
		mysql.close();
	}
	
	public boolean createReviewSQL() {
		System.out.println("Creating review sql import file...");
	
		boolean successfulCreation = false;
	
		final JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = jfc.showOpenDialog(null);
	
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			// Create multiple files because they have to be separated due to GoDaddy's mysql
			// maximum packet limit size is 32MB. Currently, there are 706646 review records to
			// insert result in a 530467 KB (~518 MB).
			// For sanity check, we will create 18 different sub files which should be below the
			// 32 MB limit (518/32 = ~16). Each of the sub files will have 39258 records (706646/18 = ~ 39258).
			
			int numRecordsPerFile = 39258;
			int fileCounter = 1;
	
			int numReviewEntries = this.reviews.size();
			int reviewCounter = 0;
			double percent = 0;
	
			// For now, lets set starting point to 0;
			int startingPoint = 0;
	
			try {
				
				File newFile = new File(jfc.getSelectedFile(), "review_insert_" + Integer.toString(fileCounter) + ".sql");
				FileWriter writer = new FileWriter(newFile.getAbsolutePath());
	
				writer.append("INSERT INTO `review_test_import` (`review_id`, `business_id`, `user_id`, `text`, `stars`, `useful_votes`, `type`, `date`) VALUES");
	
				Review review;
				String valueDelimiter;
				for (int i = startingPoint; i < this.reviews.size() - 1; i++) {
					// check if a new file is necessary
					if(i >= numRecordsPerFile && i%numRecordsPerFile == 0){
						// finalize the old file
						writer.flush();
						writer.close();
						
						// create a new file to start writing
						fileCounter++;
						newFile = new File(jfc.getSelectedFile(), "review_insert_" + Integer.toString(fileCounter) + ".sql");
						writer = new FileWriter(newFile.getAbsolutePath());
						
						writer.append("INSERT INTO `review_test_import` (`review_id`, `business_id`, `user_id`, `text`, `stars`, `useful_votes`, `type`, `date`) VALUES");
					}
					
					//use correct value delimiter when there is a new file
					if((i+1) >= numRecordsPerFile && (i+1)%numRecordsPerFile == 0)
						valueDelimiter = ";";
					else
						valueDelimiter = ",";
					
					review = this.reviews.get(i);
					if(review.text.length() > 2000)
						review.text = review.text.substring(0, 2000);
					review.text = review.text.replace("\\", "");
					review.text = review.text.replace("'", "");
					
					// ('test', 'test', 'test', 'test', '4.5', 3, 'test', 'test'),
					writer.append("('" + review.review_id + "', '"
							+ review.business_id + "', '" + review.user_id
							+ "', '" + review.text + "', '"
							+ Double.toString(review.stars) + "', "
							+ Integer.toString(review.useful_votes) + ", '"
							+ review.type + "', '" + review.date + "')" + valueDelimiter);
	
					reviewCounter++;
	
					percent = (double) reviewCounter / (double) numReviewEntries
							* 100;
					System.out.println("Review SQL insert Progress: "
							+ Integer.toString((int) percent) + "% ("
							+ Integer.toString(reviewCounter) + "/"
							+ Integer.toString(numReviewEntries) + ")");
				}
				review = this.reviews.get(this.reviews.size() - 1);
				if(review.text.length() > 2000)
					review.text = review.text.substring(0, 2000);
				review.text = review.text.replace("\\", "\\\\");
				review.text = review.text.replace("'", "");
				
				// ('test', 'test', 'test', 'test', '4.5', 3, 'test', 'test');
				// // the last value
				writer.append("('" + review.review_id + "', '"
						+ review.business_id + "', '" + review.user_id + "', '"
						+ review.text + "', '" + Double.toString(review.stars)
						+ "', " + Integer.toString(review.useful_votes) + ", '"
						+ review.type + "', '" + review.date + "');");
	
				reviewCounter++;
	
				percent = (double) reviewCounter / (double) numReviewEntries * 100;
				System.out.println("Review SQL insert Progress: "
						+ Integer.toString((int) percent) + "% ("
						+ Integer.toString(reviewCounter) + "/"
						+ Integer.toString(numReviewEntries) + ")");
	
				// generate whatever data you want
	
				writer.flush();
				writer.close();
				successfulCreation = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return successfulCreation;
	}

	public void importTips(){
		boolean successfulInsert = false;
		int numTipEntries = this.tips.size();
		int tipCounter = 0;
		double percent = 0;

		MySQLAccess mysql = new MySQLAccess();
		
		// Get starting point by checking current number of records in the table
		int startingPoint = Tip.getNumberOfRecordsInDatabase(mysql);
		if(startingPoint == -1){
			System.out.println("Error in getting number of records in tip table. Returned -1.");
			return;
		}
		else{
			tipCounter = startingPoint;
		}
		
		System.out.println("Inserting tips...");
		Tip tip;
		for(int i = startingPoint; i < this.tips.size(); i++){
			tip = this.tips.get(i);
			successfulInsert = tip.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert tip for business ID: " + tip.business_id + ")");
			tipCounter++;
			
			percent = (double)tipCounter/(double)numTipEntries*100;
			System.out.println("Tip Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(tipCounter) + "/"
					+ Integer.toString(numTipEntries) + ")");
		}
		mysql.close();
	}
	
	public void importCheckins(){
		boolean successfulInsert = false;
		int numCheckinEntries = this.checkins.size();
		int checkinCounter = 0;
		double percent = 0;
		
		MySQLAccess mysql = new MySQLAccess();
		
		// Get starting point by checking current number of records in the table
		int startingPoint = Checkin.getNumberOfRecordsInDatabase(mysql);
		if(startingPoint == -1){
			System.out.println("Error in getting number of records in checkin table. Returned -1.");
			return;
		}
		else{
			checkinCounter = startingPoint;
		}
		
		System.out.println("Inserting checkins...");
		Checkin checkin;
		for(int i = startingPoint; i < this.checkins.size(); i++){
			checkin = this.checkins.get(i);
			successfulInsert = checkin.insertRecord(mysql);
			if(!successfulInsert)
				System.out.println("Failed to insert checkin for business ID: " + checkin.business_id + ")");
			checkinCounter++;
			
			percent = (double)checkinCounter/(double)numCheckinEntries*100;
			System.out.println("Checkin Progress: " + Integer.toString((int)percent) + "% (" + Integer.toString(checkinCounter) + "/"
					+ Integer.toString(numCheckinEntries) + ")");
		}
		mysql.close();
	}

}
