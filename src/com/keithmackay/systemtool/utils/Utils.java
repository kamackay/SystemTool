package com.keithmackay.systemtool.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

	public static List<File> getAllFiles(File root) {
		List<File> files = new ArrayList<>();
		File[] list = root.listFiles();
		if (list != null) for (File f : list) {
			if (f.isDirectory()) files.addAll(getAllFiles(f));
			else files.add(f);
		}
		return files;
	}
}
