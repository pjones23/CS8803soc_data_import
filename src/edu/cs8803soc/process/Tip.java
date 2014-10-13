package edu.cs8803soc.process;

import edu.cs8803soc.dal.MySQLAccess;

public class Tip implements IRecord{
	public String business_id;
	public String user_id;
	public String text;
	public int likes;
	public String type;
	public String date;
	
	public Tip(String business_id, String user_id, String text, int likes,
			String type, String date){
		this.business_id = business_id;
		this.user_id = user_id;
		this.text = text;
		this.likes = likes;
		this.type = type;
		this.date = date;
	}

	@Override
	public boolean insertRecord(MySQLAccess mysql) {
		return mysql.executeInsertTip(this);
	}
	
}
