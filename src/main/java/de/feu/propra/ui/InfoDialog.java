package de.feu.propra.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.feu.propra.util.SystemInfo;

/**
 * A dialog to display some information about the application and the runtime
 * environment.
 * 
 * @param parent
 */
public class InfoDialog {
  private JDialog dialog;
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Creates an {@code InfoDialog} over the given {@code Frame}.
   * 
   * @param parent The {@code Frame} over which the Frame shall be displayed.
   */
  public InfoDialog(Frame parent) {
    var labels = Box.createVerticalBox();
    labels.add(new JLabel(bundle.getString("cwd") + ":"));
    labels.add(new JLabel(bundle.getString("Used") + " Java Version:"));
    labels.add(new JLabel("Icons " + bundle.getString("by") + ":"));

    var values = Box.createVerticalBox();
    values.add(new JLabel(SystemInfo.getCurrentWorkingDirectory()));
    values.add(new JLabel(SystemInfo.getJavaVersion()));

    var hyperlink = new HyperlinkLabel("https://icons8.com");
    // workaround to prevent the HyperlinkLabel to strech the clickable region
    // due to the html formatting
    var hyperlinkPanel = new JPanel(new BorderLayout());
    hyperlinkPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    hyperlinkPanel.add(hyperlink, BorderLayout.WEST);
    values.add(hyperlinkPanel);

    var panel = Box.createHorizontalBox();
    panel.add(labels);
    panel.add(Box.createRigidArea(new Dimension(5, 0)));
    panel.add(values);

    var icon = UiIcon.iconFromFile("/icons/icons8-blockchain-technology-48.png");
    var pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, icon);

    // all the listener stuff is taken from the JOpionPane.showMessageDialog
    // and its initialization of the parent JDialog. But instead of using that
    // we create a JDialog only once.

    dialog = new JDialog(parent, bundle.getString("Info"), true);
    pane.addPropertyChangeListener(new PropertyChangeListener() {
      // hides the dialog when the VALUE_PROPERTY on the JOptionPane changed, i.e.
      // user clicked a button
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (dialog.isVisible() && evt.getSource() == pane && (evt.getPropertyName().equals(JOptionPane.VALUE_PROPERTY))
            && evt.getNewValue() != null && evt.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
          var exitEvent = new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING);
          dialog.dispatchEvent(exitEvent);
        }
      }
    });

    dialog.addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent ce) {
        // reset value to ensure closing works properly
        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
      }
    });

    dialog.setContentPane(pane);
    dialog.pack();
    dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(parent);
  }

  /**
   * Shows or hides this {@code Dialog} depending on the value of parameter
   * {@code status}.
   * 
   * @param status If {@code true}, makes the {@code Dialog} visible, otherwise
   *               hides the {@code Dialog}.
   */
  public void setVisible(boolean status) {
    dialog.setVisible(status);
  }
}
