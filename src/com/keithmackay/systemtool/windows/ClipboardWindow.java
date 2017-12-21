package com.keithmackay.systemtool.windows;

import com.keithmackay.systemtool.SizedStack;
import com.keithmackay.systemtool.SystemTool;
import com.keithmackay.systemtool.clipboard.ClipboardManager;
import org.pmw.tinylog.Logger;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

public class ClipboardWindow extends Frame {
	private SizedStack<String> clipboardHistory;

	public ClipboardWindow(ClipboardManager manager) {
		this.clipboardHistory = manager.getHistory();
		Dimension screenSize = this.getScreenSize();
		int width = 150 + (int) (screenSize.height * .25);
		Dimension windowSize = new Dimension(width, screenSize.height - 100);
		this.setSize(windowSize);
		this.setLocation((screenSize.width - width) / 2, this.getLocation().y);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				super.windowClosing(e);
			}
		});
		try {
			this.setIconImage(SystemTool.getIcon());
		} catch (Exception e) {
			Logger.error(e, "Error initializing window");
		}
		List listView = new List(clipboardHistory.size());
		listView.setSize(windowSize);
		clipboardHistory.forEach(historyItem -> {
			if (historyItem != null) {
				listView.add(historyItem);
			}
		});
		listView.setMultipleMode(false);
		listView.addActionListener(e -> {
			StringSelection selection = new StringSelection(e.getActionCommand());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			Logger.info("Set Clipboard Contents to \"{}\"", e.getActionCommand());
			dispose();
		});
		this.add(listView);
		this.setTitle(String.format(Locale.getDefault(), "Clipboard Manager - %d items", this.clipboardHistory.size()));
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}


}
