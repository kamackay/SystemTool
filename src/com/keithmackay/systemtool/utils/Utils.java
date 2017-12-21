package com.keithmackay.systemtool.utils;

public class Utils {
	public static String truncate(String s, int length) {
		int newLen = Math.min(s.length(), length - 3);
		boolean shortened = newLen != s.length();
		return s.substring(0, newLen) + (shortened ? "..." : ""); // Append ellipsis if truncated
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
