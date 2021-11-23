package de.feu.propra.ui;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import org.graphstream.ui.view.View;

public class SwingTab extends JSplitPane {
  private static final long serialVersionUID = 1L;
  JSplitPane netAndGraphPane;
  JTextPane logPane;

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
    // when resizing, additional space shall be divided equally between two two top
    // panes. the text pane shall grow less that the top pane.
    // this also gives a nice initial distribution
    netAndGraphPane.setResizeWeight(0.5);
    if (netView == null && graphView == null) {
      setResizeWeight(0.3);
    } else {
      setResizeWeight(0.8);
    }
  }

  public void setNetView(View netView) {
    if (netView == null) {
      netAndGraphPane.setLeftComponent(new JPanel());
    } else {
      netAndGraphPane.setLeftComponent((JPanel) netView);
    }
  }

  public void setGraphView(View graphView) {
    if (graphView == null) {
      netAndGraphPane.setRightComponent(new JPanel());
    } else {
      netAndGraphPane.setRightComponent((JPanel) graphView);
    }
  }
  
  public JTextPane getLogPane() {
    return logPane;
  }
  
  public boolean logIsEmpty() {
    return logPane.getText().isEmpty();
  }
}
