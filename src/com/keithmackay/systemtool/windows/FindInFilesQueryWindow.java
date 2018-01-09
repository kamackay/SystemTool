package com.keithmackay.systemtool.windows;

import com.keithmackay.systemtool.utils.SpringUtilities;
import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class FindInFilesQueryWindow extends SystemToolWindow {

	public FindInFilesQueryWindow() {
		super();

		JTextField searchFor = new JTextField(), path = new JTextField(), match = new JTextField();
		match.setText(".*\\..*");

		this.centerOnScreen(new Dimension(this.screenSize.width / 3, 250));
		this.getContentPane().setLayout(new BorderLayout());
		String[] labels = {"Search For: ", "In Path: ", "Files Matching: "};

		//Create and populate the panel.
		JPanel p = new JPanel(new SpringLayout());
		for (int x = 0; x < labels.length; x++) {
			String label = labels[x];
			JLabel l = new JLabel(label, JLabel.TRAILING);
			p.add(l);
			JTextField textField;
			switch (x) {
				default:
				case 0:
					textField = searchFor;
					break;
				case 1:
					textField = path;
					break;
				case 2:
					textField = match;
			}
			l.setLabelFor(textField);
			p.add(textField);

		}


		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p, labels.length, 2, 6, 6, 6, 6);

		p.setOpaque(true);
		this.add(p, BorderLayout.PAGE_START);
		JButton okayButton = new JButton("OK");
		okayButton.setPreferredSize(new Dimension(100, 75));
		this.add(okayButton, BorderLayout.PAGE_END);
		ActionListener submit = e -> {
			Logger.info("Search for \"{}\" in \"{}\". Files matching \"{}\"", searchFor.getText(), path.getText(), match.getText());
			new FindInFilesProgressWindow(searchFor.getText(), path.getText(), match.getText());
			this.close();
		};
		okayButton.addActionListener(submit);
		new ArrayList<>(Arrays.asList(searchFor, path, match)).forEach(field -> field.addActionListener(submit));
	}
}
