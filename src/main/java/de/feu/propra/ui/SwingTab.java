package de.feu.propra.ui;

import java.awt.Font;

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
  JSplitPane netAndGraphPane;
  JTextPane logPane;

  /**
   * Creates a SwingTab that displays the given {@code GraphStream} {@code View}s.
   * 
   * @param netView   The GraphStream view to display in the top left area.
   * @param graphView The GraphStream view to display in the top right area.
   */
  public SwingTab(View netView, View graphView) {
    super(JSplitPane.VERTICAL_SPLIT);
    netAndGraphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    setNetView(netView);
    setGraphView(graphView);
    logPane = new ClearableTextPane();
    logPane.setFont(new Font("monospaced", Font.PLAIN, 14));
    var scrollLogPane = new JScrollPane(logPane);
    setTopComponent(netAndGraphPane);
    setBottomComponent(scrollLogPane);
    // when resizing, additional space shall be divided equally between the two top
    // panes. the text pane shall grow less that the top pane.
    // this also gives a nice initial distribution
    netAndGraphPane.setResizeWeight(0.5);
    if (netView == null && graphView == null) {
      setResizeWeight(0.3);
    } else {
      setResizeWeight(0.8);
    }
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
