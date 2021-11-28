package de.feu.propra.ui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.graphstream.ui.view.View;

/**
 * Util class to handle MouseWheel zoom on a GraphStream graph {@code View}.
 * 
 * @author j-hap 
 *
 */
public class ZoomController implements MouseWheelListener {
  private static final double zoomIncrement = 0.1;

  /**
   * Increases zoom on mousewheel up rotation, decreases zoom on mousewheel down
   * rotation.
   */
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
