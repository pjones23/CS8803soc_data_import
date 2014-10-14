package edu.cs8803soc.dal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.cs8803soc.process.Business;
import edu.cs8803soc.process.Checkin;
import edu.cs8803soc.process.Review;
import edu.cs8803soc.process.Tip;

public class MySQLAccess {

	private Connection connect = null;
	
	public MySQLAccess() {

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			String connectionURL = "jdbc:mysql://helpyelp.me/helpyelpdb?useInformationSchema=true&user=helpyelp14&password=CS8803soc";
			this.connect = DriverManager.getConnection(connectionURL);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			if(this.connect != null && !this.connect.isClosed())
				this.connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean executeInsertBusiness(Business business){
		CallableStatement cs;
		try {
			cs = this.connect.prepareCall("{call insertBusiness(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			// 14 parameters
			cs.setString(1, business.business_id); // 1 - business_id (VARCHAR(100))
			cs.setInt(2, business.review_count); // 2 - review_count (INT)
			cs.setString(3, business.state); // 3 - state (VARCHAR(50))
			cs.setString(4, business.full_address); // 4 - full address (VARCHAR (500))
			cs.setString(5, business.type); // 5 - type (VARCHAR (10))
			cs.setString(6, business.city); // 6 - city (VARCHAR (50))
			cs.setString(7, business.neighborhoods); // 7 - neighborhoods (VARCHAR (1000))
			cs.setDouble(8, business.stars); // 8 - stars (DECIMAL)
			cs.setString(9, business.name); // 9 - name (VARCHAR (100))
			cs.setString(10, business.categories); // 10 - categories (VARCHAR (1000))
			cs.setDouble(11, business.longitude); // 11 - longitude (DECIMAL)
			cs.setDouble(12, business.latitude); // 12 - latitude (DECIMAL)
			cs.setString(13, business.attributes); // 13 - attributes (VARCHAR (1000))
			cs.registerOutParameter(14, java.sql.Types.INTEGER); // 14 (OUT) - result (INT) 
		
			cs.executeQuery();
			int result = cs.getInt(14);
			if(result == 1)
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean executeInsertReview(Review review){
		CallableStatement cs;
		try {
			cs = this.connect.prepareCall("{call insertReview(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			// 9 parameters
			cs.setString(1, review.review_id); // 1 - review_id (VARCHAR (100))
			cs.setString(2, review.business_id); // 2 - business_id (VARCHAR (100))
			cs.setString(3, review.user_id); // 3 - user_id (VARCHAR (100))
			if(review.text.length() > 2000)
				review.text = review.text.substring(0, 2000);
			cs.setString(4, review.text); // 4 - review_text (VARCHAR (2000))
			cs.setDouble(5, review.stars); // 5 - stars (DECIMAL)
			cs.setInt(6, review.useful_votes); // 6 - useful_votes (INT)
			cs.setString(7, review.type); // 7 - type (VARCHAR(10))
			cs.setString(8, review.date); // 8 - review_date (VARCHAR (20))
			cs.registerOutParameter(9, java.sql.Types.INTEGER); // 9 (OUT) - result (INT) 
			
			cs.executeQuery();
			int result = cs.getInt(9);
			if(result == 1)
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean executeInsertCheckin(Checkin checkin){
		CallableStatement cs;
		try {
			cs = this.connect.prepareCall("{call insertCheckin(?, ?, ?, ?)}");
			// 4 parameters
			cs.setString(1, checkin.business_id); // 1 - business_id (VARCHAR (100))
			cs.setString(2, checkin.checkin_info); // 2 - checkin_info (VARCHAR (1000))
			cs.setString(3,  checkin.type); // 3 - type (VARCHAR (10))
			cs.registerOutParameter(4, java.sql.Types.INTEGER); // 4 (OUT) - result (INT) 
			
			cs.executeQuery();
			int result = cs.getInt(4);
			if(result == 1)
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean executeInsertTip(Tip tip){
		CallableStatement cs;
		try {
			cs = this.connect.prepareCall("{call insertTip(?, ?, ?, ?, ?, ?, ?)}");
			// 7 parameters
			cs.setString(1, tip.business_id); // 1 - business_id (VARCHAR (100))
			cs.setString(2, tip.user_id); // 2 - user_id (VARCHAR (100))
			if(tip.text.length() > 2000)
				tip.text = tip.text.substring(0, 2000);
			cs.setString(3, tip.text); // 3 - tip_text (VARCHAR (2000))
			cs.setInt(4, tip.likes); // 4 - likes (INT)
			cs.setString(5, tip.type); // 5 - type (VARCHAR (10))
			cs.setString(6, tip.date); // 6 - tip_date (VARCHAR (20))
			cs.registerOutParameter(7, java.sql.Types.INTEGER); // 7 (OUT) - result (INT) 
			
			cs.executeQuery();
			int result = cs.getInt(7);
			if(result == 1)
				return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	  
}
