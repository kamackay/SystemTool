package com.keithmackay.systemtool.windows;

import com.keithmackay.systemtool.SizedStack;
import com.keithmackay.systemtool.clipboard.ClipboardManager;
import org.pmw.tinylog.Logger;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Locale;

import static com.keithmackay.systemtool.utils.Reversed.reversed;

public class ClipboardWindow extends SystemToolWindow {
	private SizedStack<String> clipboardHistory;

	public ClipboardWindow(ClipboardManager manager) {
		super();
		this.clipboardHistory = manager.getHistory();
		int width = 150 + (int) (this.screenSize.height * .25);
		this.centerOnScreen(new Dimension(width, this.screenSize.height - 100));

		List listView = new List(clipboardHistory.size());
		listView.setSize(this.getSize());
		reversed(clipboardHistory).forEach(historyItem -> {
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

}
