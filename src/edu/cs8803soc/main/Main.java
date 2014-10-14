package edu.cs8803soc.main;

import edu.cs8803soc.process.Import_Data;

public class Main {

	public static void main(String[] args) {

		Import_Data id = new Import_Data();
		id.buildLists();

		Thread businessImportThread = new Thread(new DataOperation(id,
				DataOperation.BUSINESS));
		businessImportThread.start();

		Thread reviewImportThread = new Thread(new DataOperation(id,
				DataOperation.REVIEW));
		reviewImportThread.start();

		Thread tipImportThread = new Thread(new DataOperation(id,
				DataOperation.TIP));
		tipImportThread.start();

		Thread checkingImportThread = new Thread(new DataOperation(id,
				DataOperation.CHECKIN));
		checkingImportThread.start();

	}

	// Display a message, preceded by
	// the name of the current thread
	static void threadMessage(String message) {
		String threadName = Thread.currentThread().getName();
		System.out.format("%s: %s%n", threadName, message);
	}

	private static class DataOperation implements Runnable {

		static final String BUSINESS = "BUSINESS";
		static final String REVIEW = "REVIEW";
		static final String TIP = "TIP";
		static final String CHECKIN = "CHECKIN";

		Import_Data id;
		String mode;

		public DataOperation(Import_Data id, String mode) {
			this.id = id;
			this.mode = mode;
		}

		public void run() {
			if (mode == BUSINESS) {
				threadMessage("Starting business import thread.");
				id.importBusinesses();
			} else if (mode == REVIEW) {
				threadMessage("Starting review import thread.");
				id.importReviews();
			} else if (mode == TIP) {
				threadMessage("Starting tip import thread.");
				id.importTips();
			} else if (mode == CHECKIN) {
				threadMessage("Starting checkin import thread.");
				id.importCheckins();
			}
		}
	}

}
