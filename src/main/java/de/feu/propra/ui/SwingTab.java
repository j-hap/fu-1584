package de.feu.propra.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import org.graphstream.ui.view.View;

/**
 * A {@code JComponent} with the default layout to display two
 * {@link org.graphstream.ui.view.View} and an associated log area.
 * 
 * @author j-hap 
 */
public class SwingTab extends JSplitPane {
  private static final long serialVersionUID = 1L;
  private JSplitPane netAndGraphPane;
  private JTextPane logPane;
  private static final JLabel netPlaceholder;
  private static final JLabel graphPlaceholder;
  private static final double defaultVerticalSplitRatio = 0.3;

  static {
    var f = new Font("SansSerif", Font.BOLD, 20);

    netPlaceholder = new JLabel("Petri Net", JLabel.CENTER);
    netPlaceholder.setFont(f);
    netPlaceholder.setForeground(Color.GRAY);

    graphPlaceholder = new JLabel("Reachability Graph", JLabel.CENTER);
    graphPlaceholder.setFont(f);
    graphPlaceholder.setForeground(Color.GRAY);
  }

  /**
   * Creates an empty SwingTab with only a log pane.
   * 
   * @param netView   The GraphStream view to display in the top left area.
   * @param graphView The GraphStream view to display in the top right area.
   */
  public SwingTab() {
    super(JSplitPane.VERTICAL_SPLIT);
    netAndGraphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    logPane = new ClearableTextPane();
    logPane.setFont(new Font("monospaced", Font.PLAIN, 14));
    var scrollLogPane = new JScrollPane(logPane);
    setTopComponent(netAndGraphPane);
    setBottomComponent(scrollLogPane);
    netAndGraphPane.setLeftComponent(netPlaceholder);
    netAndGraphPane.setRightComponent(graphPlaceholder);
    // when resizing, additional space shall be divided equally between the two top
    // panes. the text pane shall grow less that the top pane.
    // this also gives a nice initial distribution
    netAndGraphPane.setResizeWeight(0.5);
    setResizeWeight(defaultVerticalSplitRatio);
  }

  /**
   * Creates a SwingTab that displays the given {@code GraphStream} {@code View}s.
   * 
   * @param netView   The GraphStream view to display in the top left area.
   * @param graphView The GraphStream view to display in the top right area.
   */
  public SwingTab(View netView, View graphView) {
    this();
    setNetView(netView);
    setGraphView(graphView);
  }

  /**
   * Changes the top left view of the tab.
   * 
   * @param netView The GraphStream view to display.
   */
  public void setNetView(View netView) {
    if (netView == null) {
      // adds an empty panel
      netAndGraphPane.setLeftComponent(new JPanel());
    } else {
      netAndGraphPane.setLeftComponent((JPanel) netView);
    }
    if (getResizeWeight() == defaultVerticalSplitRatio) {
      setResizeWeight(0.8);
    }
  }

  /**
   * Changes the top right view of the tab.
   * 
   * @param graphView The GraphStream view to display.
   */
  public void setGraphView(View graphView) {
    if (graphView == null) {
      // adds an empty panel
      netAndGraphPane.setRightComponent(new JPanel());
    } else {
      netAndGraphPane.setRightComponent((JPanel) graphView);
    }
    if (getResizeWeight() == 0.0) {
      setResizeWeight(0.3);
    }
  }

  /**
   * Exposes the log pane to use with a log handler.
   * 
   * @return The log pane of this tab.
   */
  public JTextPane getLogPane() {
    return logPane;
  }

  /**
   * Checks if the log pane of this tab contains any message.
   * 
   * @return True if the log pane is empty, false otherwise.
   */
  public boolean logIsEmpty() {
    return logPane.getText().isEmpty();
  }
}
