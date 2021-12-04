package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import de.feu.propra.reachability.Marking;
import de.feu.propra.reachability.ReachabilityGraph;

/**
 * Interface for a Petri Net model that allows creation and interaction as well
 * as listening to changes.
 * 
 * @author j-hap 
 *
 */
public interface PetriNet {
  /**
   * Adds a {@code Place} to the net.
   * 
   * @param id Unique ID of the {@code Place} to be added.
   */
  void addPlace(String id);

  /**
   * Adds a {@code Transition} to the net.
   * 
   * @param id Unique ID of the {@code Transition} to be added.
   */
  void addTransition(String id);

  /**
   * Tries to add an {@code Arc} to the net.
   * 
   * @param id     Unique ID of the {@code Arc} to be added.
   * @param source Unique ID of the source {@code PetriNode}.
   * @param target Unique ID of the target {@code PetriNode}.
   * @throws IllegalConnectionException When the types of the {@code PetriNode}s
   *                                    to be connected are equal.
   * @throws ElementNotFoundException   When either source or target
   *                                    {@code PetriNode} does not exist.
   */
  void addArc(String id, String source, String target);

  /**
   * Renames a {@code PetriNode}.
   * 
   * @param id   Unique ID of the {@code PetriNode} to be renamed.
   * @param name New Name of the {@code PetriNode}.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  void setNodeName(String id, String name);

  /**
   * Sets position of a {@code PetriNode} to be used in a {@code View} of the
   * {@code PetriNet}
   * 
   * @param id Unique ID of the {@code PetriNode} to be repositioned.
   * @param x  x-coordinate of the {@code PetriNode}.
   * @param y  y-coordinate of the {@code PetriNode}.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  void setNodePosition(String id, int x, int y);

  /**
   * Sets the number of initial tokens an a {@code PetriNode}.
   * 
   * @param id      Unique ID of the {@code PetriNode} to be modified.
   * @param nTokens Number of Tokens that {@code PetriNode} shall have as its
   *                initial token count.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  void setInitialTokens(String id, int nTokens);

  /**
   * Collects the current token count of all places and returns it as a
   * {@code Marking} object.
   * 
   * @return The current {@code Marking} of the {@code PetriNet}.
   * @see de.feu.propra.reachability.Marking
   */
  public Marking getMarking();

  /**
   * Sets the current marking of the {@code PetriNet}. Is is not checked if that
   * marking is possible to reach from the current initial marking. The tokens in
   * the {@code Marking} are ordered alphabetically by ID of the {@code Place}s.
   * 
   * @param marking The new marking of the {@code PetriNet}.
   */
  void setMarking(Marking marking);

  /**
   * Triggers a {@code Transition} to redistribute tokens. If the given id is not
   * a {@code Transition}, the method does nothing.
   * 
   * @param id Unique ID of the {@code Transition} to be triggered.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  void triggerTransition(String id);

  /**
   * @return An unmodifiable collection of all {@code PetriNode}s of the
   *         {@code PetriNet}.
   */
  Collection<PetriNode> nodes();

  /**
   * @return An unmodifiable collection of all {@code Arc}s of the
   *         {@code PetriNet}.
   */
  Collection<Arc> arcs();

  /**
   * Increases the initial token count of a {@code Place} by one. Does nothing if
   * a {@code Place} with the given ID is not present in the {@code PetriNet}
   * 
   * @param id Unique ID of the {@code PetriNode} to be modified.
   * @return True of success, false if not.
   */
  boolean addToken(String id);

  /**
   * Decreases the initial token count of a {@code Place} by one. Does nothing if
   * a {@code Place} with the given ID is not present in the {@code PetriNet}
   * 
   * @param id Unique ID of the {@code PetriNode} to be modified.
   * @return True of success, false if not.
   */
  boolean removeToken(String id);

  /**
   * Freezes the current marking and sets the current token count as the initial
   * token count of each {@code Place}.
   */
  void setCurrentMarkingAsInitial();

  /**
   * Adds a {@code PropertyChangeListener} to each {@code PetriNode} of the
   * {@code PetriNet}.
   * 
   * @param listener The listener to add to each {@code PetriNode}.
   */
  void addNodePropertyChangeListener(PropertyChangeListener listener);

  /**
   * Removes a {@code PropertyChangeListener} from each {@code PetriNode} of the
   * {@code PetriNet}.
   * 
   * @param listener The listener to remove from each {@code PetriNode}.
   */
  void removeNodePropertyChangeListener(PropertyChangeListener listener);

  /**
   * Sets the current token count of each {@code Place} to its initial token
   * count.
   */
  void resetPlaces();

  /**
   * Reloads the properties (name, position, token count) of each
   * {@code PetriNode} from the file, from which the {@code PetriNet} was created.
   */
  void reload();

  /**
   * @return The IDs of all active {@code Transition}s in the {@code PetriNet}.
   */
  Collection<String> getActiveTransitionIds();

  /**
   * @param id Unique ID of the {@code PetriNode} to be checked.
   * @return True if the given ID belongs to a {@code Transition}, false
   *         otherwise.
   * @throws ElementNotFoundException If there is no Element with the given ID.
   */
  boolean isTransition(String id);

  /**
   * @return The child {@code ReachabilityGraph} that tracks all visited markings.
   */
  ReachabilityGraph getReachabilityGraph();

  /**
   * @return True if none of the already visited markings indicate unboundedness.
   *         False otherwise.
   */
  boolean isBounded();
}
