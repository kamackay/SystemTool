package com.keithmackay.systemtool.windows;

import org.apache.commons.io.FileUtils;
import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.keithmackay.systemtool.utils.Utils.getAllFiles;

class FindInFilesProgressWindow extends SystemToolWindow {

	private JLabel topLabel, bottomLabel;
	private JProgressBar progressBar;
	private boolean open = true;
	private long fileCount = 0;
	private AtomicLong filesChecked = new AtomicLong();

	FindInFilesProgressWindow(final String text, final String path, final String match) {
		super();
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.centerOnScreen(new Dimension(this.screenSize.width / 3, 250));

		topLabel = new JLabel("Counting Files");
		bottomLabel = new JLabel();
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		new Thread(() -> {
			File root = new File(path);
			if (root.exists()) {
				List<File> fileList = getAllFiles(root).stream().filter(file -> file.getAbsolutePath().matches(match)).collect(Collectors.toList());
				runOnUiThread(() -> setFileCount(fileList.size()));
				fileList.stream().parallel().forEach(file -> {
					try {
						if (FileUtils.readFileToString(file).contains(text)) {
							Logger.info("Found! - {}", file.getAbsolutePath());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					filesChecked.getAndIncrement();
				});
			}
		}).start();

		new Thread(() -> {
			while (open) {
				try {
					Thread.sleep(100);
					if (fileCount != 0) setProgress(filesChecked.get(), fileCount);
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		}).start();
		topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(topLabel);

		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(progressBar);

		bottomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(bottomLabel);

		setProgress(0, 0);

		this.setOnClose(() -> open = false);
	}

	private void setFileCount(long fileCount) {
		this.topLabel.setText(String.format("Scanning %d files", fileCount));
		this.fileCount = fileCount;
	}

	private void setProgress(double progress, long total) {
		if (total == 0) return;
		this.runOnUiThread(() -> {
			progressBar.setValue((int) (progress / total * 100));
			progressBar.setString(String.format("%.4f%% Done", progress / total * 100));
			bottomLabel.setText(String.format("%d files checked", filesChecked.get()));
		});
	}

}
