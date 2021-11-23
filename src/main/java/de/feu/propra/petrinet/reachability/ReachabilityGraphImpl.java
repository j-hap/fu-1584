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

public class ReachabilityGraphImpl implements ReachabilityGraph {
  private PetriNet net;
  private Map<Marking, LinkedMarking> nodes;
  private List<Marking> unboundedMarkings;
  private boolean isBounded = true;

  private List<ReachabilityGraphChangeListener> listeners = new ArrayList<>();

  public ReachabilityGraphImpl(PetriNet n) {
    net = n;
  }

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

  @Override
  public Marking getActiveMarking() {
    return net.getMarking();
  }

  @Override
  public void setActiveMarking(Marking newMarking) {
    checkValid(newMarking);
    net.setMarking(newMarking);
  }

  @Override
  public void markingChanged(Marking newMarking) {
    checkValid(newMarking);
    listeners.forEach(l -> l.activeMarkingChanged(newMarking));
  }

  private void checkValid(Marking newMarking) {
    if (!nodes.containsKey(newMarking)) {
      throw new IllegalStateException("Reachability Graph and Petri Net are out of synch.");
    }
  }

  @Override
  public int getNodeCount() {
    return nodes.size();
  }

  @Override
  public int getEdgeCount() {
    return nodes.values().stream().mapToInt(n -> n.getSuccessorCount()).sum();
  }

  @Override
  public void addChangeListener(ReachabilityGraphChangeListener l) {
    listeners.add(l);
  }

  @Override
  public void removeChangeListener(ReachabilityGraphChangeListener l) {
    listeners.remove(l);
  }

  @Override
  public boolean isBounded() {
    return isBounded;
  }

  @Override
  public List<Marking> getUnboundedMarkings() {
    return Collections.unmodifiableList(unboundedMarkings);
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
