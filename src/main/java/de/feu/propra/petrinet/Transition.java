package de.feu.propra.petrinet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@code Transition} is a node of a {@code PetriNet} that should be connected
 * to {@code Places}s via {@code Arc}s. When triggered, it removes a token from
 * all predecessor {@code Place}s and adds a token to all successor
 * {@code Place}s. It may only be triggered when all predecessor {@code Place}s
 * have a least one token.
 * 
 * @author j-hap 
 *
 */
public class Transition extends SimplePetriNode implements PropertyChangeListener {
  private Set<Place> predecessors = new HashSet<>();
  private Set<Place> successors = new HashSet<>();
  private boolean isActive = true; // active if no precedessors
  private boolean reactToPropertyChange = true;

  /**
   * Constructor for a {@code Transition}.
   * 
   * @param id ID of the constructed {@code Transition}
   */
  public Transition(String id) {
    super(id, NodeType.TRANSITION);
  }

  /**
   * Connects this {@code Transition} to a predecesor {@code Place}.
   * 
   * @param place The {@code Place} that is added to the predecessors.
   */
  public void addPredecessor(Place place) {
    predecessors.add(place);
    isActive &= place.hasTokens();
  }

  /**
   * Disconnects this {@code Transition} from a predecesor {@code Place}.
   * 
   * @param place The {@code Place} that is removed from the predecessors.
   */
  public void removePredecessor(Place place) {
    predecessors.remove(place);
    checkActive();
  }

  /**
   * Connects this {@code Transition} to a successor {@code Place}.
   * 
   * @param place The {@code Place} that is added to the successors.
   */
  public void addSuccessor(Place place) {
    successors.add(place);
  }

  /**
   * Disconnects this {@code Transition} from a successor {@code Place}.
   * 
   * @param place The {@code Place} that is removed from the successors.
   */
  public void removeSuccessor(Place place) {
    successors.remove(place);
  }

  private void returnTokens(Place last) {
    var placeIter = predecessors.iterator();
    Place currentPlace;
    while (placeIter.hasNext() && (currentPlace = placeIter.next()) != last) {
      currentPlace.addToken();
    }
  }

  private void takeTokenFromPredecessors() {
    for (var place : predecessors) {
      try {
        place.removeToken();
      } catch (OutOfTokensException e) {
        // roll back
        returnTokens(place);
        break;
      }
    }
  }

  private void giveTokenToSuccessors() {
    for (var place : successors) {
      place.addToken();
    }
  }

  /**
   * Takes a token from all predecessors and gives a token to all successors. Does
   * nothing then {@code Transition} is not active.
   * 
   * @see Transition#isActive
   */
  public void trigger() {
    if (!isActive) {
      return;
    }
    reactToPropertyChange = false; // prevents property change action on every token retrieval
    takeTokenFromPredecessors();
    giveTokenToSuccessors();
    reactToPropertyChange = true;
    checkActive();
  }

  private void checkActive() {
    var before = isActive;
    isActive = predecessors.stream().allMatch(Place::hasTokens);
    pcs.firePropertyChange("ActiveState", before, isActive);
  }

  /**
   * Checks if a {@code Transition} can be triggered.
   * 
   * @return True if all predecessor {@code Place}s have at least one token or if
   *         there are no predecessors. False otherwise.
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * Handles changes of properies of predecessors {@code Place}s, that may change
   * the activity state of this {@code Transition}. Is is deactivate during the
   * execution of a trigger event.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (reactToPropertyChange) {
      checkActive();
    }
  }
}
