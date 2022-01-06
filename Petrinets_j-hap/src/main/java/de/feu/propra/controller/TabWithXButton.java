package de.feu.propra.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.feu.propra.ui.UiIcon;

/**
 * A swing component to be used in a JTabbedPane to identify a single tab. It
 * contains a button marked with an X, that executes an action, that whas
 * injected into the constructor after activating the tab on which the button
 * lies.
 * 
 * @author j-hap 
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TabComponentsDemo">Tutorial</a>
 *
 */
public class TabWithXButton extends JPanel {
  private static final long serialVersionUID = 1L;
  private static final int buttonSize = 18;
  private JTabbedPane tabPane;

  private static final MouseListener ml = new MouseAdapter() {
    @Override
    public void mouseEntered(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(true);
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(false);
      }
    }
  };

  /**
   * Constructs a JPanel to be used as a TabComponent of a {@code JTabbedPane}.
   * 
   * @param name            The content of the label on the tab.
   * @param parentContainer The parent JTabbedPane to trigger tab switching.
   * @param action          The {@code Action} to be performed when the button is
   *                        clicked.
   */
  public TabWithXButton(String name, JTabbedPane parentContainer, Action action) {
    tabPane = parentContainer;
    setOpaque(false);

    var label = new JLabel(name);
    add(label);

    var closeButton = new JButton(UiIcon.DELETE.menu);
    closeButton.setBorderPainted(false);
    closeButton.setOpaque(false);
    closeButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
    closeButton.addMouseListener(ml);
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // must activate tab in which it lies
        // then trigger the injected action
        var iTab = tabPane.indexOfTabComponent(TabWithXButton.this);
        tabPane.setSelectedIndex(iTab);
        action.actionPerformed(e);
      }
    });
    add(closeButton);
  }
}
