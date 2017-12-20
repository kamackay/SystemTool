package com.keithmackay.systemtool.clipboard;

import com.keithmackay.systemtool.SizedStack;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class ClipboardManager {
	private Clipboard clipboard;
	private SizedStack<String> history;

	public ClipboardManager(ClipboardChangeListener listener) {
		this.history = new SizedStack<>(100);
		this.history.push(null);
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				String cont = ClipboardManager.this.getContents();
				if (cont != null && !cont.equals(getMostRecent())) {
					//Clipboard Changed
					history.push(cont);
					listener.onClipboardChange(cont);
				}
			}
		}, 0, 5);

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
