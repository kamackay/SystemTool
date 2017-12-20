package com.keithmackay.systemtool.clipboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Timer;
import java.util.TimerTask;

public class ClipboardManager {
	private ClipboardChangeListener listener;
	private String contents = null, oldContents = null;
	private Clipboard clipboard;

	public ClipboardManager(ClipboardChangeListener listener) {
		this.listener = listener;
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				ClipboardManager.this.getContents();
				if (contents != null && !contents.equals(oldContents)) {
					//Clipboard Changed
					listener.onClipboardChange(ClipboardManager.this.contents);
				}
			}
		}, 0, 5);

	}

	String getContents() {
		try {
			this.oldContents = this.contents;
			this.contents = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			// Logger.error(e, "Error getting clipboard data");
			// Ignore for now
		}
		return this.contents;
	}


}
