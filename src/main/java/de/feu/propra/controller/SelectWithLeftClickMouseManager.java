package de.feu.propra.controller;

import java.awt.event.MouseEvent;
import java.util.EnumSet;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.util.InteractiveElement;

public class SelectWithLeftClickMouseManager extends DefaultMouseManager {
  // can't use types from parent, cause it's private
  private final EnumSet<InteractiveElement> typesToSelect = EnumSet.of(InteractiveElement.NODE);

  private void deselectAll() {
    // taken from DefaultMouseManager::mouseButtonPress();
    graph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
    graph.sprites().filter(s -> s.hasAttribute("ui.selected")).forEach(s -> s.removeAttribute("ui.selected"));
    graph.edges().filter(e -> e.hasAttribute("ui.selected")).forEach(e -> e.removeAttribute("ui.selected"));
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    view.requireFocus();
    if (!event.isShiftDown()) {
      deselectAll();
    }
    if (event.getButton() == 1) {
      curElement = view.findGraphicElementAt(typesToSelect, event.getX(), event.getY());
      if (curElement != null) {
        // TODO not propagated to graph, why? probably something with pipe
        curElement.setAttribute("ui.selected");        
      }
    }
  }

  @Override
  protected void elementMoving(GraphicElement element, MouseEvent event) {
    // removes clicked attribute (set by mouseButtonPressOnElement)
    // when element is being dragged
    element.removeAttribute("ui.clicked");
    super.elementMoving(element, event);
  }
}