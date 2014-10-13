package edu.cs8803soc.process;

import edu.cs8803soc.dal.MySQLAccess;

public class Business implements IRecord {

	public String business_id;
	public int review_count;
	public String state;
	public String full_address;
	public String type;
	public String city;
	public String neighborhoods;
	public double stars;
	public String name;
	public String categories;
	public double longitude;
	public double latitude;
	public String attributes;
	
	public Business(String business_id, int review_count, String state,	String full_address, 
			String type, String city, String neighborhoods, double stars, String name, String categories,
			double longitude, double latitude, String attributes)
	{
		this.business_id = business_id;
		this.review_count = review_count;
		this.state =  state;
		this.full_address = full_address;
		this.type = type;
		this.city = city;
		this.neighborhoods = neighborhoods;
		this.stars = stars;
		this.name = name;
		this.categories = categories;
		this.longitude = longitude;
		this.latitude = latitude;
		this.attributes = attributes;
	}

	@Override
	public boolean insertRecord(MySQLAccess mysql) {
		return mysql.executeInsertBusiness(this);
	}
	
}
