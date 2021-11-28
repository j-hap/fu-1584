package de.feu.propra.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * Class of composend Swing Components for displaying status information in a
 * JFrame,
 * 
 * @author j-hap 
 *
 */
public class StatusBar {
  private String message = "";
  private boolean markAsModified = false;
  private JPanel panel = new JPanel();
  private JLabel textLabel = new JLabel(message, SwingConstants.LEFT);

  /**
   * Constructs a {@code StatusBar} with a
   */
  public StatusBar() {
    super();
    panel.setPreferredSize(new Dimension(100, 22));
    panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(textLabel);
  }

  /**
   * Exposes the {@code JPanel} to embed into a view.
   * 
   * @return The child {@code JPanel} that displays the message.
   */
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

  /**
   * Sets the displayed message.
   * 
   * @param m The new message to dislay.
   */
  public void setMessage(String m) {
    message = m;
    updateMessage();
  }

  /**
   * Shows / hides the modified postfix in the {@code JPanel}
   * 
   * @param isMod Determines if the suffix is appended.
   */
  public void setModifiedMarker(boolean isMod) {
    markAsModified = isMod;
    updateMessage();
  }

}
