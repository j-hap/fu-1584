package de.feu.propra.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

public class ClearableTextPane extends JTextPane {
  private static final long serialVersionUID = 1L;
  private final JPopupMenu rightClickMenu = new JPopupMenu();

  public ClearableTextPane() {
    initRightClickMenu();
		setEditable(false);
    setAutoscrolls(true);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // according to oracle, the popuptrigger must be checked on both press and
        // release. At least on windows it's always false in a mouseClicked event
        mouseReleased(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
  }

  private void initRightClickMenu() {
    var item = rightClickMenu.add("Clear");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setText(null);
      }
    });
  }

}
