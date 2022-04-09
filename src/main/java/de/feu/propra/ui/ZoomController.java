package de.feu.propra.ui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.graphstream.ui.view.View;

/**
 * Util class to handle MouseWheel zoom on a GraphStream graph {@code View}.
 * Increases / Decreases Zoom by 10% in every scroll increment. The zoom center
 * is moved so that the mouse position stays the same in respect to the graph.
 * 
 * @see <a href=
 *      "https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view">https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view</a>
 * 
 * @author j-hap 
 */
public class ZoomController implements MouseWheelListener {
  private static final double baseFactor = 1.1;

  /**
   * Increases zoom on mousewheel up rotation, decreases zoom on mousewheel down
   * rotation.
   */
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    var view = (View) e.getComponent();
    var direction = e.getWheelRotation();
    var zoomFactor = Math.pow(baseFactor, direction);
    var cam = view.getCamera();
    var zoomLevel = cam.getViewPercent() * zoomFactor;
    var mousePositionInGraphUnits = cam.transformPxToGu(e.getX(), e.getY());
    var newCenter = mousePositionInGraphUnits.interpolate(cam.getViewCenter(), zoomFactor);
    cam.setViewCenter(newCenter.x, newCenter.y, newCenter.z);
    cam.setViewPercent(zoomLevel);
  }
}
