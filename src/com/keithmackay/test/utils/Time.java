package com.keithmackay.test.utils;

public class Time {

	/**
	 * Convert seconds to milliseconds
	 *
	 * @param seconds - Number of seconds to convert
	 * @return Milliseconds
	 */
	public static long seconds(long seconds) {
		return seconds * 1000;
	}

	/**
	 * Convert minutes to milliseconds
	 *
	 * @param minutes - Number of minutes to convert
	 * @return Milliseconds
	 */
	public static long minutes(long minutes) {
		return seconds(minutes * 60);
	}

	public static long hours(long hours) {
		return minutes(hours * 60);
	}

	public static long days(long days) {
		return days * hours(days * 24);
	}
}
