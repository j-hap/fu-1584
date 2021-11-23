package de.feu.propra.petrinet.reachability;

import java.util.List;

import de.feu.propra.controller.ReachabilityGraphChangeListener;

public interface ReachabilityGraph {
  void init();

  void addMarking(String edgeId, String edgeLabel, Marking oldMarking, Marking newMarking);

  Marking getActiveMarking();

  void setActiveMarking(Marking newMarking);

  void markingChanged(Marking newMarking);
  
  int getNodeCount();

  int getEdgeCount();

  void addChangeListener(ReachabilityGraphChangeListener l);
  
  public void removeChangeListener(ReachabilityGraphChangeListener l);

  boolean isBounded();

  List<Marking> getUnboundedMarkings();  
}
