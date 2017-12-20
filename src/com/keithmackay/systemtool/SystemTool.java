package com.keithmackay.systemtool;

import com.keithmackay.systemtool.processes.BackgroundProcess;
import com.keithmackay.systemtool.settings.Settings;
import com.keithmackay.systemtool.settings.SettingsManager;
import com.keithmackay.systemtool.utils.Time;
import com.keithmackay.systemtool.utils.VersionData;
import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static com.keithmackay.systemtool.utils.Utils.truncate;

public class SystemTool {

	private static final VersionData VERSION = new VersionData(1, 0, 0);
	private static List<BackgroundProcess> backgroundProcesses;
	private static SettingsManager settingsManager;

	public static void main(String[] args) {
		try {
			Logger.info("Start Initialization");
			// Check the SystemTray is supported
			if (!SystemTray.isSupported()) {
				Logger.error("SystemTray is not supported");
				return;
			}
			final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(new URL("http://keithmackay.com/images/settings.png")), "System Tools");
			trayIcon.setImageAutoSize(true);
			final SystemTray tray = SystemTray.getSystemTray();

			settingsManager = SettingsManager.getInstance();
			settingsManager.setString(Settings.VERSION, VERSION.toString());

			backgroundProcesses = new ArrayList<>(Arrays.asList(new BackgroundProcess("Clean Downloads", Time.minutes(5)) {

				@Override
				public void doWork() {
					File downloadsFolder = new File(Paths.get(System.getProperty("user.home"), "Downloads").toString());
					List<String> filesDeleted = cleanupPath(downloadsFolder, Time.days(14));
					if (filesDeleted.size() > 0) {
						StringBuilder sb = new StringBuilder();
						filesDeleted.forEach(f -> sb.append(f).append("\n"));
						AutoCloseJOption.show(truncate(sb.toString(), 1000), "Files Deleted", 5000);
					} else {
						Logger.info("No files in Downloads folder to delete");
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
			backgroundProcesses.forEach(BackgroundProcess::start);

			// AutoCloseJOption.show("System Tools Started", 3000);
			new WorkstationLockListener() {

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
		List<String> deletedFiles = new ArrayList<>();
		long now = System.currentTimeMillis();
		Arrays.asList(Objects.requireNonNull(path.listFiles())).forEach(file -> {
			if (file.isDirectory()) {
				deletedFiles.addAll(cleanupPath(file, time));
			}
			// If the file is older than the limit or is an empty folder
			if (now - time > file.lastModified() || (file.isDirectory() && Objects.requireNonNull(file.list()).length == 0)) {
				try {
					if (!file.isDirectory() || Objects.requireNonNull(file.list()).length == 0) {
						boolean deleted = file.delete();
						if (deleted) {
							deletedFiles.add(file.getAbsolutePath());
							Logger.info("Deleted File {}", file.getAbsolutePath());
						}
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
	private static PopupMenu createPopupMenu() {
		final PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.addActionListener(event -> AutoCloseJOption.show(String.format(Locale.getDefault(), "Version: %s\nWritten by: Keith MacKay\n2017", VERSION.toString()), 3000));
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(event -> System.exit(0));

		Menu processesItem = new Menu("Processes");
		backgroundProcesses.forEach(process -> {
			MenuItem subItem = new MenuItem((process.isRunning() ? "Pause " : "Start ") + process.toString());
			processesItem.add(subItem);
			subItem.addActionListener(event -> {
				if (process.isRunning()) {
					process.pause();
					subItem.setLabel("Start " + process.toString());
					Logger.info("Started process {}", process.toString());
				} else {
					process.run();
					subItem.setLabel("Pause " + process.toString());
					Logger.info("Paused process {}", process.toString());
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
	 * @param message - The message to show
	 * @param title   - Title to show
	 * @param type    - The dialog type
	 */
	private static void showMessage(String message, String title, int type) {
		JOptionPane.showMessageDialog(null, message, title, type);
	}

	/**
	 * Show a message
	 *
	 * @param message - The message to show
	 * @param title   - Title to show
	 */
	protected static void showMessage(String message, String title) {
		showMessage(message, title, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Show a message
	 *
	 * @param message - The message to show
	 */
	private static void showMessage(String message) {
		showMessage(message, "System Tool", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Query a user
	 *
	 * @param message - The message to show
	 * @param title   - Title to show
	 * @return The user's response
	 */
	private static int query(String message, String title) {
		return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
	}

	/**
	 * Query a user
	 *
	 * @param message - The message to show
	 * @return the user's response
	 */
	private static int query(String message) {
		return query(message, "System Tool");
	}
}
