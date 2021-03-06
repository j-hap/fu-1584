package de.feu.propra.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphFactory;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.InteractiveElement;

import de.feu.propra.petrinet.PetriNet;
import de.feu.propra.petrinet.PetriNode;
import de.feu.propra.petrinet.Place;
import de.feu.propra.petrinet.Transition;
import de.feu.propra.ui.Settings;
import de.feu.propra.ui.ZoomController;
import de.feu.propra.util.TokenFormatter;

/**
 * Controller for Petri Net model interactions. Handles communication between a
 * {@code PetriNet} and its {@code View}.
 * 
 * @author j-hap 
 *
 */
public class PetriNetController extends MouseAdapter implements PropertyChangeListener {
  private PetriNet net; // the data model
  private Graph graph; // the view model (data model for the view)
  private View view; // the view
  private Viewer viewer;
  private SpriteManager spriteManager;
  private boolean initialMarkingIsModified = false;
  private boolean unboundedWarningWasShown = false;
  private static final Logger logger = Logger.getLogger(PetriNetController.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());
  private static final String STYLESHEET = "url(" + GraphFactory.class.getResource("/styles/net.css") + ")";

  /**
   * Constructor for a Controller for the given {@code PetriNet}
   * 
   * @param net The {@code PetriNet} to control.
   */
  public PetriNetController(PetriNet net) {
    this.net = net;
    net.addNodePropertyChangeListener(this);
    initGraph();
    initView();
  }

  private void initGraph() {
    graph = new MultiGraph(UUID.randomUUID().toString());
    graph.setAttribute("ui.stylesheet", STYLESHEET);
    graph.setAttribute("ui.quality"); // more effort into nice visuals
    graph.setAttribute("ui.antialias"); // activates anti aliasing

    spriteManager = new SpriteManager(graph);
    for (var petriNode : net.nodes()) {
      var graphNode = graph.addNode(petriNode.getId());
      formatNode(graphNode, petriNode);

      var sprite = spriteManager.addSprite(petriNode.getId());
      sprite.setAttribute("ui.label", petriNode.getLabel());
      sprite.attachToNode(petriNode.getId());
    }

    for (var a : net.arcs()) {
      var edge = graph.addEdge(a.getId(), a.getSourceId(), a.getTargetId(), true);
      edge.setAttribute("ui.label", a.getLabel());
    }
  }

  private void formatNode(Node graphNode, PetriNode petriNode) {
    graphNode.setAttribute("xy", petriNode.getXPos(), -petriNode.getYPos());
    graphNode.setAttribute("ui.class", petriNode.getType().toString().toLowerCase());

    if (petriNode.isTransition()) {
      var t = (Transition) petriNode;
      formatTransition(graphNode, t.isActive());
    } else if (petriNode.isPlace()) {
      var p = (Place) petriNode;
      formatPlace(graphNode, p.getTokenCount());
    }
  }

  private void formatPlace(Node node, int tokenCount) {
    node.setAttribute("ui.label", TokenFormatter.format(tokenCount));
  }

  private void formatTransition(Node node, boolean active) {
    node.setAttribute("ui.color", active ? 1f : 0f);
  }

  private void initView() {
    viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    view = viewer.addDefaultView(false);
    var panel = (JPanel) view;
    panel.addMouseWheelListener(new ZoomController());
    panel.addMouseListener(this);
    panel.addMouseMotionListener(this);
    enableRightMouseButtonDrag();
  }

  private void enableRightMouseButtonDrag() {
    // disabled all default right mouse button actions. enables right click drag.
    view.setMouseManager(new DefaultMouseManager() {
      private MouseEvent lastRmbPress;

      @Override
      public void mousePressed(MouseEvent event) {
        // masks RMB press
        if (event.getButton() != MouseEvent.BUTTON3) {
          super.mousePressed(event);
          lastRmbPress = null;
        } else {
          lastRmbPress = event;
        }
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        // masks RMB release
        if (event.getButton() != MouseEvent.BUTTON3) {
          super.mouseReleased(event);
          lastRmbPress = event;
        }
      }

      @Override
      public void mouseDragged(MouseEvent event) {
        // Moves view center of {@code View} according to mouse drag distance.
        if (lastRmbPress != null) {
          var cam = view.getCamera();
          var oldCenter = cam.getViewCenter();
          var viewCenterInPixels = cam.transformGuToPx(oldCenter.x, oldCenter.y, 0);
          var dx = lastRmbPress.getX() - event.getX();
          var dy = lastRmbPress.getY() - event.getY();
          viewCenterInPixels.move(dx, dy);
          var newCenter = cam.transformPxToGu(viewCenterInPixels.x, viewCenterInPixels.y);
          cam.setViewCenter(newCenter.x, newCenter.y, 0);
          lastRmbPress = event;
        } else {
          // removed clicked status when beginning to drag
          if (curElement!= null) {
            curElement.removeAttribute("ui.clicked");
          }
          super.mouseDragged(event);
        }
      }
    });
  }

  /**
   * Exposes the constructed view to a caller. Intended to be used to place that
   * {@code View} in a parent UI container.
   * 
   * @return The GraphStream View Object that displays the underlying
   *         {@code PetriNet} model.
   */
  View getView() {
    return view;
  }

  private void modifyInitialTokensOfSelectedPlaces(Function<? super String, Boolean> action) {
    boolean success = applyToSelectedPlaces(action);
    if (success) {
      setCurrentMarkingAsInitial();
    }
  }

  /**
   * Freezes the current marking of the {@code PetriNet} as the initial marking.
   */
  public void setCurrentMarkingAsInitial() {
    if (net.isInInitialState()) {
      logger.info(bundle.getString("current_marking_is_initial"));
      return;
    }
    net.setCurrentMarkingAsInitial();
    initialMarkingIsModified = true;
    unboundedWarningWasShown = false;
  }

  /**
   * Removes a token from all places, that are currently selected in the
   * {@code View}.
   */
  public void removeInitialTokenFromSelectedPlaces() {
    modifyInitialTokensOfSelectedPlaces(net::removeToken);
  }

  /**
   * Adds a token to all places, that are currently selected in the {@code View}.
   */
  public void addInitialTokenToSelectedPlaces() {
    modifyInitialTokensOfSelectedPlaces(net::addToken);
  }

  private boolean applyToSelectedPlaces(Function<? super String, Boolean> action) {
    var selectedPlaces = getIdsOfSelectedPlaces();
    if (selectedPlaces.isEmpty()) {
      logger.warning(bundle.getString("no_places_selected"));
      return false;
    }
    // can't use anyMatch() because that short circuits on first match and
    // does not execute subsequent calls
    return selectedPlaces.stream().map(s -> action.apply(s))
        .collect(Collectors.reducing(Boolean.FALSE, Boolean::logicalOr));
  }

  private Set<String> getIdsOfSelectedPlaces() {
    return viewer.getGraphicGraph().nodes().filter(n -> n.hasAttribute("ui.selected"))
        .filter(n -> ((String) n.getAttribute("ui.class")).equals("place")).map(Node::getId)
        .collect(Collectors.toSet());
  }

  /**
   * Resets all {@code Place}s in the {@code PetriNet} to their initial marking.
   */
  public void resetModel() {
    net.resetPlaces();
  }

  /**
   * Listener Method to handle changes in the controlled {@code PetriNet}.
   * Propagates those changes to the {@code View}.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    var node = (PetriNode) evt.getSource();
    switch (evt.getPropertyName()) {
    case "TokenCount":
      formatPlace(graph.getNode(node.getId()), (Integer) evt.getNewValue());
      break;
    case "ActiveState":
      formatTransition(graph.getNode(node.getId()), (Boolean) evt.getNewValue());
      break;
    case "Label":
      spriteManager.getSprite(node.getId()).setAttribute("label", (String) evt.getNewValue());
      break;
    case "XPos":
      graph.getNode(node.getId()).setAttribute("x", (Double) evt.getNewValue());
      break;
    case "YPos":
      graph.getNode(node.getId()).setAttribute("y", -(Double) evt.getNewValue());
      break;
    }
  }

  /**
   * Exposes the model to a caller. Intended to be used when the model is needed
   * in non-interactive calculations.
   * 
   * @return The controlled {@code PetriNet} model
   */
  public PetriNet getModel() {
    return net;
  }

  private void deselectAll() {
    // taken from DefaultMouseManager::mouseButtonPress();
    graph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
    graph.edges().filter(e -> e.hasAttribute("ui.selected")).forEach(e -> e.removeAttribute("ui.selected"));
  }

  /**
   * Handles clicks in the {@code View}. Used to forward trigger actions of
   * {@code Transition}s and handle selection of {@code Place}s. {@inheritDoc}
   */
  @Override
  public void mouseClicked(MouseEvent me) {
    // only care for pressed / released in the same spot -> clicked
    // but the graphstream ViewerListener only supports press and release
    // events
    if (me.getButton() != MouseEvent.BUTTON1) {
      return;
    }
    var types = EnumSet.of(InteractiveElement.NODE);
    var curElement = view.findGraphicElementAt(types, me.getX(), me.getY());
    if (curElement != null) {
      var id = curElement.getId();
      net.triggerTransition(id);
      if (Settings.isContinouusBoundednessCheckActive() && !unboundedWarningWasShown && !net.isBounded()) {
        unboundedWarningWasShown = true;
        logger.warning(bundle.getString("unbounded_info"));
      }
      if (!me.isShiftDown()) {
        deselectAll();
      }
      graph.getNode(id).setAttribute("ui.selected");
    }
  }

  /**
   * Tells caller about the current modification state of the underlying
   * {@code PetriNet} model.
   * 
   * @return True if the initial marking of the underlying model was changed after
   *         import.
   */
  public boolean initialMarkingIsModified() {
    return initialMarkingIsModified;
  }

  /**
   * Triggers a reload of the underlying {@code PetriNet} model from its original
   * file if there is one.
   */
  public void reloadModel() {
    net.reload();
    initialMarkingIsModified = false;
    unboundedWarningWasShown = false;
  }

  /**
   * Convenience function for users who don't know the Shift+R shortcut of
   * graphstream to fit the current {@code View}.
   */
  public void fitView() {
    view.getCamera().resetView();
  }
}
