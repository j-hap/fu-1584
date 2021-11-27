package de.feu.propra.petrinet.reachability;

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
 * An implementation of the {@link ReachabilityGraph} interface. The
 * {@code ReachabilityGraphImpl} tracks the history of a {@code PetriNet} by
 * storing all visited {@code Marking}s in a graph. It allows manipulating the
 * current marking of a {@code PetriNet} by switching to an already visited
 * marking. Through tracking the visited marking, it is able to determine if the
 * observed {@code PetriNet} is unbounded.
 * 
 * @author j-hap 
 *
 */
public class ReachabilityGraphImpl implements ReachabilityGraph {
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
  public ReachabilityGraphImpl(PetriNet net) {
    this.net = net;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
   * {@inheritDoc} When a new edge is added, is also checks the boundedness of the
   * parent {@code PetriNet} by looking at all predecessor {@code Marking}s of the
   * active one and checking the m &lt;-&gt; m' relation.
   */
  @Override
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
    listeners.forEach(l -> l.edgeVisited(oldMarking, edgeId));
    listeners.forEach(l -> l.activeMarkingChanged(newMarking));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Marking getActiveMarking() {
    return net.getMarking();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActiveMarking(Marking newMarking) {
    checkValid(newMarking);
    net.setMarking(newMarking);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markingChanged(Marking newMarking) {
    checkValid(newMarking);
    listeners.forEach(l -> l.activeMarkingChanged(newMarking));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNodeCount() {
    return nodes.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getEdgeCount() {
    return nodes.values().stream().mapToInt(n -> n.getSuccessorCount()).sum();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addChangeListener(ReachabilityGraphChangeListener l) {
    listeners.add(l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeChangeListener(ReachabilityGraphChangeListener l) {
    listeners.remove(l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isBounded() {
    return isBounded;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
