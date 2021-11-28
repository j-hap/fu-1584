package de.feu.propra.petrinet.reachability;

import java.util.List;

import de.feu.propra.controller.ReachabilityGraphChangeListener;

/**
 * Interface for a Reachability Graph model that allows iterative construction
 * as well as listening to changes.
 * 
 * @author j-hap 
 *
 */
public interface ReachabilityGraph {
  /**
   * Initializes the {@code ReachabilityGraph} to start a new iterative
   * construction. It notifies listeners about the initialization, in case a
   * visualization has to be updated.
   */
  void init();

  /**
   * Adds a marking to the graph and creates the necessary connections.
   * 
   * @param edgeId     ID of the edge that lead to the new {@code Marking}.
   * @param edgeLabel  Label of the edge that lead to the new {@code Marking}.
   * @param oldMarking Marking that was active before the newly added one.
   * @param newMarking Marking to be added to the graph.
   */
  void addMarking(String edgeId, String edgeLabel, Marking oldMarking, Marking newMarking);

  /**
   * @return The currently active {@code Marking}.
   */
  Marking getActiveMarking();

  /**
   * Sets an already existing {@code Marking} as active. Throws
   * IllegalStateException when the given {@code Marking} is not present in the
   * {@code ReachabilityGraph}.
   * 
   * @param newMarking The {@code Marking} to set active.
   * @throws IllegalStateException If the Marking is not present in the
   *                               {@code ReachabilityGraph}.
   */
  void setActiveMarking(Marking newMarking);

  /**
   * Method to be called when the parent {@code PetriNet} was set to another
   * marking by any other way than triggering a transition.
   * 
   * @param newMarking The {@code Marking} to set active.
   */
  void markingChanged(Marking newMarking);

  /**
   * Counts the number of nodes.
   * 
   * @return The number of {@code Marking}s currently present in the
   *         {@code ReachabilityGraph}.
   */
  int getNodeCount();

  /**
   * Counts the number of edges.
   * 
   * @return The number of {@code Edge}s currently present in the
   *         {@code ReachabilityGraph}.
   */
  int getEdgeCount();

  /**
   * Adds a {@code ReachabilityGraphChangeListener} to the
   * {@code ReachabilityGraph}.
   * 
   * @param listener The {@code ReachabilityGraphChangeListener} to be added.
   */
  void addChangeListener(ReachabilityGraphChangeListener listener);

  /**
   * Removes a {@code ReachabilityGraphChangeListener} from the
   * {@code ReachabilityGraph}.
   * 
   * @param listener The {@code ReachabilityGraphChangeListener} to be removed.
   */
  void removeChangeListener(ReachabilityGraphChangeListener listener);

  /**
   * @return True if the currently present {@code Marking}s do not indicate
   *         unboundedness. False otherwise.
   */
  boolean isBounded();

  /**
   * @return A List of Markings that match the m &lt;-&gt; m' relation.
   * @see Marking#compareTo(Marking)
   */
  List<Marking> getUnboundedMarkings();
}
