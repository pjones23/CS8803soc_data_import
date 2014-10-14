package edu.cs8803soc.process;
import java.io.File;
import java.io.FileNotFoundException;
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
						//String[] names = JSONObject.getNames(js);

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
								/*
								System.out.println("BUSINESS EXAMPLE " + Integer.toString(i+1));
								for(String n : names)
								{
									System.out.print(n + ": ");
									System.out.println(js.get(n));
								}
								System.out.println("\n");
								*/
								
								// i++;
							}
						}
						else if(js.getString("type").contains("review") && m<10){
							if(this.reviews.size() == 0)
								System.out.println("Collecting reviews...");
							/*
							System.out.println("REVIEW EXAMPLE " + Integer.toString(m+1));
							for(String n : names)
							{
								System.out.print(n + ": ");
								System.out.println(js.get(n));
							}
							System.out.println("\n");
							*/
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
						}
						else if(js.getString("type").contains("checkin") && k<10){
							if(this.checkins.size() == 0)
								System.out.println("Collecting checkins...");
							/*
							System.out.println("CHECK-IN EXAMPLE " + Integer.toString(k+1));
							for(String n : names)
							{
								System.out.print(n + ": ");
								System.out.println(js.get(n));
							}
							System.out.println("\n");
							*/
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
							/*
							System.out.println("TIP EXAMPLE " + Integer.toString(l+1));
							for(String n : names)
							{
								System.out.print(n + ": ");
								System.out.println(js.get(n));
							}
							System.out.println("\n");
							*/
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
		
		System.out.println("Inserting businesses...");
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
		mysql.close();
	}
	
	public void importReviews(){
		boolean successfulInsert = false;
		int numReviewEntries = this.reviews.size();
		int reviewCounter = 0;
		double percent = 0;
		
		MySQLAccess mysql = new MySQLAccess();
		
		System.out.println("Inserting reviews...");
		for(Review review : this.reviews){
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
	
	public void importTips(){
		boolean successfulInsert = false;
		int numTipEntries = this.tips.size();
		int tipCounter = 0;
		double percent = 0;

		MySQLAccess mysql = new MySQLAccess();
		
		System.out.println("Inserting tips...");
		for(Tip tip : this.tips){
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
		
		System.out.println("Inserting checkins...");
		for(Checkin checkin : this.checkins){
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
