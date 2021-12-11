package de.feu.propra.reachability;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.feu.propra.controller.ReachabilityGraphChangeListener;
import de.feu.propra.petrinet.PetriNet;

/**
 * A Reachability Graph representation that allows iterative construction as
 * well as listening to changes. The {@code ReachabilityGraph} tracks the
 * history of a {@code PetriNet} by storing all visited {@code Marking}s in a
 * graph. It allows manipulating the current marking of a {@code PetriNet} by
 * switching to an already visited marking. Through tracking the visited
 * marking, it is able to determine if the observed {@code PetriNet} is
 * unbounded.
 * 
 * @author j-hap 
 *
 */
public class ReachabilityGraph {
  private PetriNet net;
  private Map<Marking, LinkedMarking> nodes;
  private List<Marking> unboundedMarkings;
  private boolean isBounded = true;

  private List<ReachabilityGraphChangeListener> listeners = new ArrayList<>();

  /**
   * Constructs a new {@code ReachabilityGraph} that propagates changes of the
   * active {@code Marking} to the given {@code PetriNet}.
   * 
   * @param net The {@code PetriNet} to which changes in the active Marking are
   *            propagated.
   */
  public ReachabilityGraph(PetriNet net) {
    this.net = net;
  }

  /**
   * Initializes the {@code ReachabilityGraph} to start a new iterative
   * construction. It notifies listeners about the initialization, in case a
   * visualization has to be updated.
   */
  public void init() {
    net.resetPlaces();
    nodes = new HashMap<>();
    unboundedMarkings = new ArrayList<>(2);
    isBounded = true;
    var m = net.getMarking();
    nodes.put(m, new LinkedMarking(m));
    listeners.forEach(l -> l.reachabilityGraphInitialized(m));
  }

  /**
   * Adds a marking to the graph and creates the necessary connections.
   * 
   * @param edgeId     ID of the edge that lead to the new {@code Marking}.
   * @param edgeLabel  Label of the edge that lead to the new {@code Marking}.
   * @param oldMarking Marking that was active before the newly added one.
   * @param newMarking Marking to be added to the graph.
   */
  public void addMarking(String edgeId, String edgeLabel, Marking oldMarking, Marking newMarking) {
    LinkedMarking target;
    if (nodes.containsKey(newMarking)) {
      target = nodes.get(newMarking);
    } else {
      target = new LinkedMarking(newMarking);
      nodes.put(newMarking, target);
    }
    var source = nodes.get(oldMarking);
    var e = new Edge<LinkedMarking>(edgeId, target);
    if (!source.hasOutEdge(e)) {
      source.addOutEdge(edgeId, target);
      target.addInEdge(edgeId, nodes.get(oldMarking));
      listeners.forEach(l -> l.edgeAdded(edgeId, edgeLabel, oldMarking, newMarking));
      checkUnbound(newMarking);
    }
    listeners.forEach(l -> l.activeMarkingChanged(newMarking));
    listeners.forEach(l -> l.edgeVisited(oldMarking, edgeId));
  }

  /**
   * @return The currently active {@code Marking}.
   */
  public Marking getActiveMarking() {
    return net.getMarking();
  }

  /**
   * Sets an already existing {@code Marking} as active. Throws
   * IllegalStateException when the given {@code Marking} is not present in the
   * {@code ReachabilityGraph}.
   * 
   * @param newMarking The {@code Marking} to set active.
   * @throws IllegalStateException If the Marking is not present in the
   *                               {@code ReachabilityGraph}.
   */
  public void setActiveMarking(Marking newMarking) {
    checkValid(newMarking);
    net.setMarking(newMarking);
  }

  /**
   * Method to be called when the parent {@code PetriNet} was set to another
   * marking by any other way than triggering a transition.
   * 
   * @param newMarking The {@code Marking} to set active.
   */
  public void markingChanged(Marking newMarking) {
    checkValid(newMarking);
    listeners.forEach(l -> l.activeMarkingChanged(newMarking));
  }

  /**
   * Counts the number of nodes.
   * 
   * @return The number of {@code Marking}s currently present in the
   *         {@code ReachabilityGraph}.
   */
  public int getNodeCount() {
    return nodes.size();
  }

  /**
   * Counts the number of edges.
   * 
   * @return The number of {@code Edge}s currently present in the
   *         {@code ReachabilityGraph}.
   */
  public int getEdgeCount() {
    return nodes.values().stream().mapToInt(n -> n.getSuccessorCount()).sum();
  }

  /**
   * Adds a {@code ReachabilityGraphChangeListener} to the
   * {@code ReachabilityGraph}.
   * 
   * @param listener The {@code ReachabilityGraphChangeListener} to be added.
   */
  public void addChangeListener(ReachabilityGraphChangeListener l) {
    listeners.add(l);
  }

  /**
   * Removes a {@code ReachabilityGraphChangeListener} from the
   * {@code ReachabilityGraph}.
   * 
   * @param listener The {@code ReachabilityGraphChangeListener} to be removed.
   */
  public void removeChangeListener(ReachabilityGraphChangeListener l) {
    listeners.remove(l);
  }

  /**
   * @return True if the currently present {@code Marking}s do not indicate
   *         unboundedness. False otherwise.
   */
  public boolean isBounded() {
    return isBounded;
  }

  /**
   * @return A List of Markings that match the m &lt;-&gt; m' relation.
   * @see Marking#compareTo(Marking)
   */
  public List<Marking> getUnboundedMarkings() {
    return Collections.unmodifiableList(unboundedMarkings);
  }

  private void checkValid(Marking newMarking) {
    if (!nodes.containsKey(newMarking)) {
      throw new IllegalStateException("Reachability Graph and Petri Net are out of synch.");
    }
  }

  private void checkUnbound(Marking startMarking) {
    // walks backwards from start marking through the reachability graph and
    // compare with predecessor markings
    Set<Marking> visited = new HashSet<>();
    visited.add(startMarking);
    Deque<LinkedMarking> q = new ArrayDeque<>();
    var startNode = nodes.get(startMarking);
    q.addAll(startNode.getPrecedessors());
    while (!q.isEmpty()) {
      var other = q.removeFirst();
      if (visited.contains(other)) {
        continue;
      }
      if (startNode.compareTo(other) > 0) {
        isBounded = false;
        unboundedMarkings.add(other);
        unboundedMarkings.add(startNode);
        return;
      }
      q.addAll(other.getPrecedessors());
      visited.add(other);
    }
  }
}
