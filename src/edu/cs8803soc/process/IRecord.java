package edu.cs8803soc.process;

import edu.cs8803soc.dal.MySQLAccess;

public interface IRecord {
	public boolean insertRecord(MySQLAccess mysql);
}
