package de.feu.propra.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.feu.propra.ui.MainViewAction;
import de.feu.propra.ui.UiIcon;

// inspired by
// https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TabComponentsDemo
public class TabWithCloseButton extends JPanel {
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

  public TabWithCloseButton(String name, JTabbedPane parentContainer) {
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
        // then trigger action to close active tab
        var iTab = tabPane.indexOfTabComponent(TabWithCloseButton.this);
        tabPane.setSelectedIndex(iTab);
        MainViewAction.CLOSE_FILE.action.actionPerformed(e);
      }
    });
    add(closeButton);
  }
}
