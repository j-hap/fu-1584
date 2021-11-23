package de.feu.propra.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class StatusBar {
	// composition over inheritance
	private String message = "Ready";
	private boolean markAsModified = false;
	private JPanel panel = new JPanel();
	private JLabel textLabel = new JLabel(message, SwingConstants.LEFT);

	public StatusBar() {
		super();
		panel.setPreferredSize(new Dimension(100, 22));
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(textLabel);
	}

	public JPanel getPanel() {
		return panel;
	}

	private void updateMessage() {
		if (markAsModified) {
			textLabel.setText(message + " - modified");
		} else {
			textLabel.setText(message);
		}
	}

	public void setMessage(String m) {
		message = m;
		updateMessage();
	}

	public void setModifiedMarker(boolean isMod) {
		markAsModified = isMod;
		updateMessage();
	}

}
