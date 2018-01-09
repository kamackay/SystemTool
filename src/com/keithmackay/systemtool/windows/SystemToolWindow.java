package com.keithmackay.systemtool.windows;

import com.keithmackay.systemtool.SystemTool;
import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

abstract class SystemToolWindow extends JFrame {

	Dimension screenSize;
	private OnCloseHandler onClose = null;

	SystemToolWindow() {
		screenSize = this.getScreenSize();

		try {
			this.setIconImage(SystemTool.getIcon());
		} catch (Exception e) {
			Logger.error(e, "Error initializing window");
		}
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (onClose != null) onClose.run();
				dispose();
				super.windowClosing(e);
			}
		});
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	final void centerOnScreen(Dimension windowSize) {
		this.setSize(windowSize);
		this.setLocation((this.screenSize.width - windowSize.width) / 2, (this.screenSize.height - windowSize.height) / 2);
	}

	void setOnClose(OnCloseHandler onClose) {
		this.onClose = onClose;
	}

	public interface OnCloseHandler {
		void run();
	}

	void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	void runOnUiThread(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}
}
