package edu.cs8803soc.process;

import edu.cs8803soc.dal.MySQLAccess;

public class Review implements IRecord {
	public String review_id;
	public String business_id;
	public String user_id;
	public String text;
	public double stars;
	public int useful_votes;
	public String type;
	public String date;
	
	public Review(String review_id,	String business_id, String user_id,	String text,
			double stars, int useful_votes, String type, String date){
		this.review_id = review_id;
		this.business_id = business_id;
		this.user_id = user_id;
		this.text = text;
		this.stars = stars;
		this.useful_votes = useful_votes;
		this.type = type;
		this.date = date;
	}

	@Override
	public boolean insertRecord(MySQLAccess mysql) {
		return mysql.executeInsertReview(this);
	}
	
}
