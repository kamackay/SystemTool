package com.keithmackay.test;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SystemTool {

	protected static final VersionData VERSION = new VersionData(1, 0, 0);
	protected static WorkstationLockListening workstationListener;

	public static void main(String[] args) throws MalformedURLException {
		// Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final TrayIcon trayIcon = new TrayIcon(
				Toolkit.getDefaultToolkit().getImage(new URL("http://keithmackay.com/images/settings.png")),
				"System Tools");
		trayIcon.setImageAutoSize(true);
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		final PopupMenu popup = createPopupMenu();
		trayIcon.setPopupMenu(popup);
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					showMessage("Hello");
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
					if (query("Are you sure you want to exit?") == 0) {
						System.exit(0);
					}
				}
			}
		});
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			String errorMessage = "TrayIcon could not be added";
			System.out.println(errorMessage);
			showMessage(errorMessage);
		}
		workstationListener = new WorkstationLockListening() {

			@Override
			protected void onMachineLocked(int sessionId) {
				System.out.println("Machine Unlocked");
			}

			@Override
			protected void onMachineUnlocked(int sessionId) {
				showMessage("Welcome Back");				
			}
			
		};
		System.out.println("Started SystemTool");
	}

	/**
	 * Create the context menu for the Tray Icon
	 * 
	 * @return The Context Menu
	 */
	protected static PopupMenu createPopupMenu() {
		final PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.addActionListener(event -> {
			showMessage(String.format(Locale.getDefault(), "Version: %s\nWritten by: Keith MacKay\n2017",
					VERSION.toString()));
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(event -> System.exit(0));
		// Add components to pop-up menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(exitItem);
		return popup;
	}

	/**
	 * Show a message
	 * 
	 * @param message
	 * @param title
	 * @param type
	 */
	protected static void showMessage(String message, String title, int type) {
		JOptionPane.showMessageDialog(null, message, title, type);
	}

	/**
	 * Show a message
	 * 
	 * @param message
	 * @param title
	 */
	protected static void showMessage(String message, String title) {
		showMessage(message, title, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Show a message
	 * 
	 * @param message
	 */
	protected static void showMessage(String message) {
		showMessage(message, "System Tool", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Query a user
	 * 
	 * @param message
	 * @param title
	 * @return The user's response
	 */
	protected static int query(String message, String title) {
		int result = JOptionPane.showConfirmDialog((Component) null, message, title, JOptionPane.OK_CANCEL_OPTION);
		return result;
	}

	/**
	 * Query a user
	 * 
	 * @param message
	 * @return the user's response
	 */
	protected static int query(String message) {
		return query(message, "System Tool");
	}
}
