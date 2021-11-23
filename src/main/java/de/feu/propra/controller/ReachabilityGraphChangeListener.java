package de.feu.propra.controller;

import de.feu.propra.petrinet.reachability.Marking;

/**
 * The listener interface for receiving events on a {@code ReachabilityGraph}
 * Controllers that monitor changes on a model and propagate them to a view
 * should implement this interface and be registered using the model's
 * {@code addChangeListener} method.
 *
 * @see ReachabilityGraph
 *
 * @author j-hap 
 */
public interface ReachabilityGraphChangeListener {
  /**
   * Is called whenever the active marking of the calling
   * {@code ReachabilityGraph} model is changed, either by triggering a transition
   * in the parent {@code PetriNet} or by setting the active marking
   * programatically.
   * 
   * @param newActiveMarking The active marking in the calling
   *                         {@code ReachabilityGraph} model.
   */
  void activeMarkingChanged(Marking newActiveMarking);

  /**
   * Is called whenever a new edge is added to the {@code ReachabilityGraph}
   * model.
   * 
   * @param id     Unique of the added edge.
   * @param label  Label of the added edge.
   * @param source Source node of the added edge.
   * @param target Target node of the added edge.
   */
  void edgeAdded(String id, String label, Marking source, Marking target);

  /**
   * Is called whenever the calling {@code ReachabilityGraph} is initialized.
   * 
   * @param initialMarking The one and only marking present in the
   *                       {@code ReachabilityGraph} model after initilization
   */
  void reachabilityGraphInitialized(Marking initialMarking);

  /**
   * Is called whenever the calling {@code ReachabilityGraph} changes its active
   * marking due to a transition edge.
   * 
   * @param oldMarking Active Marking before the transition happened.
   * @param edgeId     The unique id of the edge that connects the oldMarking and
   *                   the new active marking.
   */
  void edgeVisited(Marking oldMarking, String edgeId);
}
