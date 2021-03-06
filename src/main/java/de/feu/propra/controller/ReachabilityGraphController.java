package de.feu.propra.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.InteractiveElement;

import de.feu.propra.reachability.Marking;
import de.feu.propra.reachability.ReachabilityGraph;
import de.feu.propra.ui.HierarchyLayout;
import de.feu.propra.ui.Settings;
import de.feu.propra.ui.ZoomController;

/**
 * Controller for Reachability Graph model interactions. Handles communication
 * between a {@code PetriNet} and its {@code View}.
 * 
 * @author j-hap 
 *
 */
public class ReachabilityGraphController extends MouseAdapter implements ReachabilityGraphChangeListener {
  private Viewer viewer;
  private View view;
  private Graph viewModel;
  private ReachabilityGraph model;
  private SpriteManager spriteManager;
  // used for associating marking nodes in the view with its original marking
  private Map<String, Marking> markingsMap;
  private String currentLayout;
  private static final String STYLESHEET = "url("
      + ReachabilityGraphController.class.getResource("/styles/reachability.css") + ")";

  /**
   * Constructor for a Controller for the given {@code ReachabilityGraph}
   * 
   * @param rGraph The {@code ReachabilityGraph} to control.
   */
  public ReachabilityGraphController(ReachabilityGraph rGraph) {
    model = rGraph;
    model.addChangeListener(this);
    var m = rGraph.getActiveMarking();
    createViewModel();
    initViewModel(m);
    createView();
    initMarkingsMap(m);
  }

  /**
   * Reinitializes the controlled {@code ReachabilityGraph} and resets the view
   */
  public void clearModel() {
    model.init();
    view.getCamera().resetView();
  }

  private void initMarkingsMap(Marking initialMarking) {
    markingsMap = new HashMap<>();
    markingsMap.put(initialMarking.toString(), initialMarking);
  }

  /**
   * Exposes the constructed view to a caller. Intended to be used to place that
   * {@code View} in a parent UI container.
   * 
   * @return The GraphStream View Object that displays the underlying
   *         {@code ReachabilityGraph} model.
   */
  public View getView() {
    return view;
  }

  private void selectMarking(String id) {
    viewModel.nodes().forEach(e -> e.removeAttribute("ui.selected"));
    viewModel.getNode(id).setAttribute("ui.selected");
  }

  private void createViewModel() {
    viewModel = new MultiGraph(UUID.randomUUID().toString(), false, true); // reachability graph can have more than one
                                                                           // edge between nodes
  }

  private void initViewModel(Marking initialMarking) {
    viewModel.setAttribute("ui.stylesheet", STYLESHEET);
    viewModel.setAttribute("ui.quality"); // more effort into nice visuals
    viewModel.setAttribute("ui.antialias"); // activates anti aliasing
    var s = initialMarking.toString();
    var node = viewModel.addNode(s);
    node.setAttribute("ui.label", s);
    node.setAttribute("ui.class", "initial");
    node.setAttribute("ui.selected");
    // when the graph was cleared, all the sprites are deleted as well
    // and the sprite manager now holds invalid ids, so the manager is
    // simply recreated
    spriteManager = new SpriteManager(viewModel);
  }

  private void createView() {
    viewer = new SwingViewer(viewModel, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    setLayout();
    viewer.enableXYZfeedback(false);
    view = viewer.addDefaultView(false);
    var panel = (JPanel) view;
    panel.addMouseWheelListener(new ZoomController());
    panel.addMouseListener(this);
    disableSelectionRectangle();
  }

  private void setLayout() {
    var newLayout = Settings.getReachabilityGraphLayoutMode();
    if (newLayout.equals(currentLayout)) {
      return;
    }
    viewer.disableAutoLayout();
    switch (newLayout) {
    case "Default":
      viewer.enableAutoLayout();
      break;
    case "Hierarchy":
      viewer.enableAutoLayout(new HierarchyLayout());
      break;
    }
    currentLayout = newLayout;
  }

  private void disableSelectionRectangle() {
    // disables all node selection, but keeps ability to drag nodes
    // around. enables right click drag.
    view.setMouseManager(new DefaultMouseManager() {
      private MouseEvent lastRmbPress;

      @Override
      public void mousePressed(MouseEvent event) {
        view.requireFocus();
        if (event.getButton() == MouseEvent.BUTTON3) {
          // stores last position to handle drag
          lastRmbPress = event;
          return;
        }
        lastRmbPress = null;
        var types = EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE);
        curElement = view.findGraphicElementAt(types, event.getX(), event.getY());
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        curElement = null;
      }

      @Override
      public void mouseDragged(MouseEvent event) {
        // let's user drag nodes but not select.
        if (curElement != null) {
          elementMoving(curElement, event);
        } else if (lastRmbPress != null) {
          var cam = view.getCamera();
          var oldCenter = cam.getViewCenter();
          var viewCenterInPixels = cam.transformGuToPx(oldCenter.x, oldCenter.y, 0);
          var dx = lastRmbPress.getX() - event.getX();
          var dy = lastRmbPress.getY() - event.getY();
          viewCenterInPixels.move(dx, dy);
          var newCenter = cam.transformPxToGu(viewCenterInPixels.x, viewCenterInPixels.y);
          cam.setViewCenter(newCenter.x, newCenter.y, 0);
          lastRmbPress = event;
        }
      }
    });
  }

  /**
   * Handles clicks in the {@code View}. Used to activate markings the the
   * controlled {@code ReachabilityGraph}.
   *
   */
  @Override
  public void mouseClicked(MouseEvent me) {
    // we only care for pressed / released in the same spot -> clicked
    // but the graphstream ViewerListener only supports press and release
    // events
    if (me.getButton() != MouseEvent.BUTTON1) {
      return;
    }
    var types = EnumSet.of(InteractiveElement.NODE);
    var curElement = view.findGraphicElementAt(types, me.getX(), me.getY());
    if (curElement != null) {
      markingClicked(curElement.getId());
    }
  }

  private void markingClicked(String id) {
    deselectAllEdges();
    model.setActiveMarking(markingsMap.get(id));
  }

  /**
   * Visually highlights the path described by the edge ids in the
   * {@code ReachabilityGraph}'s {@code View}.
   * 
   * @param transitionIds List of transition ids whose edges to highlight. List
   *                      must start with a transition at the initial marking.
   */
  public void highlightPath(List<String> transitionIds) {
    var currentNode = viewModel.getNode(0);
    for (var id : transitionIds) {
      // we know that the edges have ids like "MarkingTransitionID", but if we only
      // match the end, then an unfortunate "MarkingTransitionIDTransitionID" would
      // also be matched, so we strip the node id, which is the marking string, from
      // the edge label and do an equals comparison
      var nodeId = currentNode.getId();
      var edges = currentNode.leavingEdges().filter(e -> e.getId().replace(nodeId, "").equals(id))
          .collect(Collectors.toList());
      if (edges.size() != 1) {
        throw new RuntimeException("Cannot find path in Graph View.");
      }
      var e = edges.get(0);
      highlightEdge(e.getId());
      currentNode = e.getTargetNode();
    }
  }

  private void highlightEdge(String id) {
    viewModel.getEdge(id).setAttribute("ui.class", "highlight");
  }

  /**
   * Generates a unique edge id and inserts a new edge with that id into the
   * {@code View} between the given marking nodes. The label of the edge is set to
   * the given {@code edgeLabel}. {@inheritDoc}
   */
  @Override
  public void edgeAdded(String edgeId, String edgeLabel, Marking source, Marking target) {
    var graphicId = source.toString() + edgeId;
    var edge = viewModel.addEdge(graphicId, source.toString(), target.toString(), true);

    // does not position well when edge gets curved
    // edge.setAttribute("ui.label", edgeLabel);

    var sprite = spriteManager.addSprite(graphicId);
    sprite.setAttribute("ui.label", edgeLabel);
    sprite.attachToEdge(graphicId);
    sprite.setPosition(0.4);

    // target node may have been auto created, so set label
    edge.getTargetNode().setAttribute("ui.label", target.toString());
    // could set active node, but that means knowledge that the model
    // now sets the target node as active node, so we do that in a separate callback
    markingsMap.put(target.toString(), target);
  }

  /**
   * Selects the new active marking in the view. {@inheritDoc}
   */
  @Override
  public void activeMarkingChanged(Marking newActiveMarking) {
    deselectAllEdges();
    selectMarking(newActiveMarking.toString());
  }

  /**
   * Initializes the view to show the structure of the displayed
   * {@code ReachabilityGraph}
   */
  @Override
  public void reachabilityGraphInitialized(Marking initialMarking) {
    viewModel.clear();
    setLayout();
    initViewModel(initialMarking);
    initMarkingsMap(initialMarking);
  }

  /**
   * Highlights the last visited edge in the {@code View} as selected. Uses the
   * "edge:selected" from the style CSS of the {@code View}. {@inheritDoc}
   */
  @Override
  public void edgeVisited(Marking source, String edgeId) {
    var graphicId = source.toString() + edgeId;
    var edge = viewModel.getEdge(graphicId);
    deselectAllEdges();
    edge.setAttribute("ui.selected");
  }

  /**
   * Highlights the given marking in the {@code View} as problematic. Uses the
   * "edge.problem" class from the style CSS of the {@code View}.
   * 
   * @param marking The {@code Marking} that shall be highlighted in the
   *                {@code View}.
   */
  public void markProblem(Marking marking) {
    viewModel.getNode(marking.toString()).setAttribute("ui.class", "problem");
  }

  /**
   * Removes "selected" attribute from all edges to remove highlighting.
   */
  public void deselectAllEdges() {
    viewModel.edges().forEach(e -> e.removeAttribute("ui.selected"));
  }
}
