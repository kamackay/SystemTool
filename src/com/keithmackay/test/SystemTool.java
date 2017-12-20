package com.keithmackay.test;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pmw.tinylog.Logger;

import com.keithmackay.test.processes.BackgroundProcess;
import com.keithmackay.test.utils.Time;
import static com.keithmackay.test.utils.Utils.truncate;

public class SystemTool {

	protected static final VersionData VERSION = new VersionData(1, 0, 0);
	protected static WorkstationLockListener workstationListener;
	protected static List<BackgroundProcess> backgroundProcesses;

	public static void main(String[] args) throws MalformedURLException {
		try {
			Logger.info("Start Initialization");
			// Check the SystemTray is supported
			if (!SystemTray.isSupported()) {
				Logger.error("SystemTray is not supported");
				return;
			}
			final TrayIcon trayIcon = new TrayIcon(
					Toolkit.getDefaultToolkit().getImage(new URL("http://keithmackay.com/images/settings.png")),
					"System Tools");
			trayIcon.setImageAutoSize(true);
			final SystemTray tray = SystemTray.getSystemTray();

			backgroundProcesses = new ArrayList<BackgroundProcess>(
					Arrays.asList(new BackgroundProcess("Clean Downloads", Time.minutes(5)) {

						@Override
						public void doWork() {
							File downloadsFolder = new File(
									Paths.get(System.getProperty("user.home"), "Downloads").toString());
							List<String> filesDeleted = cleanupPath(downloadsFolder, Time.days(1));
							if (filesDeleted.size() > 0) {
								StringBuilder sb = new StringBuilder();
								filesDeleted.forEach(f -> sb.append(f + "\n"));
								AutoCloseJOption.show(truncate(sb.toString(), 1000), "Files Deleted", 5000);
							}
						}
					}, new BackgroundProcess("Health Check", Time.seconds(30)) {

						@Override
						public void doWork() {
							// TODO something
						}
					}));

			// Create a pop-up menu components
			final PopupMenu popup = createPopupMenu();
			trayIcon.setPopupMenu(popup);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						// Left Button Click
						showMessage("Hello");
					} else if (SwingUtilities.isMiddleMouseButton(e)) {
						// Scrollbar click
						if (query("Are you sure you want to exit?") == 0) {
							System.exit(0);
						}
					} else if (SwingUtilities.isRightMouseButton(e)) {
						// Right Button Click
					}
				}
			});
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				String errorMessage = "TrayIcon could not be added";
				Logger.error(errorMessage);
				showMessage(errorMessage);
			}

			// Start Background Processes
			backgroundProcesses.forEach(process -> process.start());

			// AutoCloseJOption.show("System Tools Started", 3000);
			workstationListener = new WorkstationLockListener() {

				@Override
				protected void onMachineLocked(int sessionId) {
					Logger.info("Machine Unlocked");
				}

				@Override
				protected void onMachineUnlocked(int sessionId) {
					AutoCloseJOption.show("Welcome Back", 3000);
				}
			};
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Delete anything in the given path older than the given time (ms)
	 */
	private static List<String> cleanupPath(File path, long time) {
		List<String> deletedFiles = new ArrayList<String>();
		long now = System.currentTimeMillis();
		Arrays.asList(path.listFiles()).forEach(file -> {
			if (file.isDirectory()) {
				deletedFiles.addAll(cleanupPath(file, time));
			}
			// If the file is older than the limit or is an empty folder
			if (now - time > file.lastModified() || (file.isDirectory() && file.list().length == 0)) {
				try {
					if ((file.isDirectory() && file.list().length == 0) || !file.isDirectory()) {
						file.delete();
						deletedFiles.add(file.getAbsolutePath());
					}
				} catch (Exception e) {
					Logger.error(e, "Error Deleting file");
				}
			}
		});
		return deletedFiles;
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
			AutoCloseJOption.show(String.format(Locale.getDefault(), "Version: %s\nWritten by: Keith MacKay\n2017",
					VERSION.toString()), 3000);
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(event -> System.exit(0));

		Menu processesItem = new Menu("Processes");
		backgroundProcesses.forEach(process -> {
			MenuItem subItem = new MenuItem("Pause " + process.toString());
			processesItem.add(subItem);
			subItem.addActionListener(event -> {
				if (process.isRunning()) {
					process.pause();
					subItem.setLabel("Start " + process.toString());
				} else {
					process.run();
					subItem.setLabel("Pause " + process.toString());
				}
			});
		});

		// Add components to pop-up menu
		popup.add(processesItem);
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
