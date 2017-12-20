package com.keithmackay.systemtool.utils;

public class Utils {
	public static String truncate(String s, int length) {
		int newLen = Math.min(s.length(), length - 3);
		boolean shortened = newLen != s.length();
		return s.substring(0, newLen) + (shortened ? "..." : ""); // Append ellipsis if truncated
	}
}
