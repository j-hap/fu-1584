package de.feu.propra.petrinet.view;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.SpriteManager;

import de.feu.propra.petrinet.Arc;
import de.feu.propra.petrinet.PetriNet;
import de.feu.propra.petrinet.PetriNode;
import de.feu.propra.petrinet.Place;
import de.feu.propra.petrinet.Transition;
import de.feu.propra.petrinet.util.TokenFormatter;

public final class GraphFactory {
  static final String STYLESHEET_NET = "url(" + GraphFactory.class.getResource("/styles/net.css") + ")";
  static final String STYLESHEET_REACHABILITY = "url(" + GraphFactory.class.getResource("/styles/reachability.css") + ")";
  private static Graph graph;
//	private static SpriteManager spriteManager;

  private GraphFactory() {
    // prevents instantiation;
  }

  private static void initBlankGraph() {
    graph = new MultiGraph("1");
    var spriteManager = new SpriteManager(graph);
    graph.setAttribute("ui.stylesheet", STYLESHEET_NET);
    graph.setAttribute("ui.quality"); // more effort into nice visuals
    graph.setAttribute("ui.antialias"); // activates anti aliasing
    graph.setAttribute("spritemanager", spriteManager); // to quickly get the spritemanager from the graph
  }

  private static void addNode(PetriNode n) {
    var spriteManager = (SpriteManager) graph.getAttribute("spritemanager");
    var node = graph.addNode(n.getId());
    node.setAttribute("xy", n.getXPos(), -n.getYPos());
    node.setAttribute("ui.class", n.getType().toString().toLowerCase());
    
    // TODO this is code duplication from properyChange in controller
    if (n.isTransition()) {
      node.setAttribute("ui.color", ((Transition) n).isActive() ? 1f : 0f);
    } else if (n.isPlace()) {
      node.setAttribute("ui.label", TokenFormatter.format(((Place) n).getTokenCount()));      
    }
    
    var sprite = spriteManager.addSprite(n.getId());
    sprite.setAttribute("ui.label", n.getLabel());
    sprite.attachToNode(node.getId());

//	n.addPropertyChangeListener(node);
//  n.addPropertyChangeListener(sprite);
  }

  private static void addEdge(Arc a) {
    var id = a.getId();
    var edge = graph.addEdge(id, a.getSourceId(), a.getTargetId(), true);
    edge.setAttribute("ui.label", a.getLabel());
  }

  public static Graph create(PetriNet net) {
    initBlankGraph();
    for (var n : net.nodes()) {
      addNode(n);
    }
    for (var a : net.arcs()) {
      addEdge(a);
    }
    return graph;
  }

//  public static Graph create(ReachabilityGraph m) {
//    graph = new MultiGraph("2", false, true); // reachability graph can have more than one edge between nodes
//    var spriteManager = new SpriteManager(graph);
//    graph.setAttribute("spritemanager", spriteManager); // to quickly get the spritemanager from the graph
//    graph.setAttribute("ui.stylesheet", STYLESHEET_REACHABILITY);
//    graph.setAttribute("ui.quality"); // more effort into nice visuals
//    graph.setAttribute("ui.antialias"); // activates anti aliasing
//    var s = m.getActiveMarking().toString();
//    var node = graph.addNode(s);
//    node.setAttribute("ui.label", s);
//    node.setAttribute("ui.class", "initial");
//    node.setAttribute("ui.selected");    
//    return graph;
//  }

}
