package edu.cs8803soc.main;

import edu.cs8803soc.process.Import_Data;

public class Main {

	public static void main(String[] args) {

		Import_Data id = new Import_Data();
		id.buildLists();
		id.importLists();
		
	}

}
