package de.feu.propra.ui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.graphstream.ui.view.View;

public class ZoomController implements MouseWheelListener {
  private static final double zoomIncrement = 0.1;

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    var view = (View) e.getComponent();
    var direction = e.getWheelRotation();
    var zoomLevel = view.getCamera().getViewPercent();
    zoomLevel = Math.max(0, zoomLevel + zoomIncrement * direction);
    view.getCamera().setViewPercent(zoomLevel);
    // TODO workaround for arrow tip position bug
  }
}
