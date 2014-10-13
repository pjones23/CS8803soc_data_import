package edu.cs8803soc.process;

import edu.cs8803soc.dal.MySQLAccess;

public class Checkin implements IRecord{
	public String business_id;
	public String checkin_info;
	public String type;
	
	public Checkin(String business_id, String checkin_info, String type){
		this.business_id = business_id;
		this.checkin_info = checkin_info;
		this.type = type;
	}

	@Override
	public boolean insertRecord(MySQLAccess mysql) {
		return mysql.executeInsertCheckin(this);
	}
	
}
