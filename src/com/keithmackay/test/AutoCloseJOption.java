package com.keithmackay.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class AutoCloseJOption {
	public static void show(String message, String title, final int TIME_VISIBLE) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(null, title);
		dialog.setModal(false);
		dialog.setVisible(true);

		new Timer(TIME_VISIBLE, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		}).start();
	}

	public static void show(String message, final int TIME_VISIBLE) {
		show(message, "SystemTool", TIME_VISIBLE);
	}
}