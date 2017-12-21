package com.keithmackay.systemtool.windows;

import org.pmw.tinylog.Logger;

import javax.swing.*;


public abstract class UserQueryWindow {

	public static String show(String question, String title) {
		try {
			return JOptionPane.showInputDialog(question, title);
		} catch (Exception e){
			Logger.error(e, "Could not show popup");
		}
		return null;
	}
}
