package com.keithmackay.test.processes;

public class Time {

	/**
	 * Convert minutes to milliseconds
	 * 
	 * @param minutes
	 *            - Number of minutes to convert
	 * @return Milliseconds
	 */
	public static long minutes(long minutes) {
		return minutes * 60000;
	}

	/**
	 * Convert seconds to milliseconds
	 * 
	 * @param seconds
	 *            - Number of seconds to convert
	 * @return Milliseconds
	 */
	public static long seconds(long seconds) {
		return seconds * 1000;
	}

}
