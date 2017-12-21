package com.keithmackay.systemtool.clipboard;

import com.keithmackay.systemtool.SizedStack;
import com.keithmackay.systemtool.settings.SettingsManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Timer;
import java.util.TimerTask;

public class ClipboardManager {
	private Clipboard clipboard;
	private SizedStack<String> history;
	private SettingsManager settingsManager;

	public ClipboardManager(ClipboardChangeListener listener) {
		this.history = new SizedStack<>(100);
		this.history.push(null);
		this.settingsManager = SettingsManager.getInstance();
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				String cont = getContents();
				if (cont != null && !cont.equals(getMostRecent())) {
					//Clipboard Changed
					history.push(cont);
					listener.onClipboardChange(cont);
				}
			}
		}, 0, 100);

	}

	private String getMostRecent() {
		return this.history.lastElement();
	}

	private String getContents() {
		try {
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			// Logger.error(e, "Error getting clipboard data");
			// Ignore for now
		}
		return this.getMostRecent();
	}

	public SizedStack<String> getHistory() {
		return this.history;
	}

}
