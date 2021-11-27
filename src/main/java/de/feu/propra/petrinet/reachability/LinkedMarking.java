package de.feu.propra.petrinet.reachability;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@code LinkedMarking} is a {@code Marking} that is connected to other
 * {@code LinkedMarking}s through {@code Edge}s.
 * 
 * @author j-hap 
 *
 */
public class LinkedMarking extends Marking {
  private Set<Edge<LinkedMarking>> inEdges = new HashSet<>();
  private Set<Edge<LinkedMarking>> outEdges = new HashSet<>();

  /**
   * Constructs a {@code LinkedMarking} with the given token count array.
   * 
   * @param tokenArray An array of integers that repsresents the token count of a
   *                   set of {@code Place}s.
   */
  public LinkedMarking(Integer[] tokenArray) {
    super(tokenArray);
  }

  /**
   * Copy constructor.
   * 
   * @param marking The {@code Marking} on which the new {@code LinkedMarking} is
   *                based.
   */
  public LinkedMarking(Marking marking) {
    super(marking);
  }

  /**
   * Adds an outgoing {@code Edge} to this {@code Marking}.
   * 
   * @param edge The {@code Edge} to add.
   */
  public void addOutEdge(Edge<LinkedMarking> edge) {
    outEdges.add(edge);
  }

  /**
   * Adds an outgoing {@code Edge} to this {@code Marking}. The {@code Edge} is
   * constructed from the given ID and target {@code LinkedMarking}.
   *
   * @param label  The label of the constructed {@code Edge}.
   * @param target The target element of the constructed {@code Edge}.
   */
  public void addOutEdge(String id, LinkedMarking target) {
    addOutEdge(new Edge<LinkedMarking>(id, target));
  }

  /**
   * Adds an incoming {@code Edge} to this {@code Marking}.
   * 
   * @param edge The {@code Edge} to add.
   */
  public void addInEdge(Edge<LinkedMarking> e) {
    inEdges.add(e);
  }

  /**
   * Adds an incoming {@code Edge} to this {@code Marking}. The {@code Edge} is
   * constructed from the given ID and target {@code LinkedMarking}.
   *
   * @param label  The label of the constructed {@code Edge}.
   * @param target The target element of the constructed {@code Edge}.
   */
  public void addInEdge(String id, LinkedMarking source) {
    addInEdge(new Edge<LinkedMarking>(id, source));
  }

  /**
   * Checks if an {@code Edge} is already an outgoing edge of this
   * {@code LinkedMarking}.
   * 
   * @param edge The {@code Edge} to check.
   * @return True if given {@code Edge} is already an outgoing edge. False
   *         otherwise.
   */
  public boolean hasOutEdge(Edge<LinkedMarking> edge) {
    return outEdges.contains(edge);
  }

  /**
   * Checks if an {@code Edge} is already an outgoing edge of this
   * {@code LinkedMarking}. The {@code Edge} is constructed from the given ID and
   * target {@code LinkedMarking}.
   * 
   * @param edge The {@code Edge} to check.
   * @return True if given {@code Edge} is already an outgoing edge. False
   *         otherwise.
   */
  public boolean hasOutEdge(String id, LinkedMarking source) {
    return hasOutEdge(new Edge<LinkedMarking>(id, source));
  }

  /**
   * @return A Set of all {@code LinkedMarking}s, that have an outgoing edge
   *         towards this {@code LinkedMarking}.
   */
  public Set<LinkedMarking> getPrecedessors() {
    return inEdges.stream().map(e -> e.getTarget()).collect(Collectors.toSet());
  }

  /**
   * Counts the number of outgoing {@code Edge}s.
   * 
   * @return The number of outgoing {@code Edge}s.
   */
  public int getSuccessorCount() {
    return outEdges.size();
  }
}
