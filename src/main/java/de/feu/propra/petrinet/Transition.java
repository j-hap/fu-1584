package de.feu.propra.petrinet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

public class Transition extends SimplePetriNode implements PropertyChangeListener {
//	private List<Place> predecessors = new ArrayList<>();	
//	private List<Place> successors = new ArrayList<>();
	private Set<Place> predecessors = new HashSet<>();
  private Set<Place> successors = new HashSet<>();
	private boolean isActive = true; // active if no precedessors
	private boolean reactToPropertyChange = true;

	public Transition(String id) {
		super(id, NodeType.TRANSITION);
	}

	public void addPredecessor(Place p) {
		predecessors.add(p);
		isActive &= p.hasTokens();
	}

	public void addSuccessor(Place p) {
		successors.add(p);
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

	public void removeSuccessor(Place p) {
		successors.remove(p);
	}

	public void removePredecessor(Place p) {
		predecessors.remove(p);
		checkActive();
	}
	
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (reactToPropertyChange) {
			checkActive();
		}
	}
}
