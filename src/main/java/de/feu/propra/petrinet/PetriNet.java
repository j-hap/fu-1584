package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import de.feu.propra.petrinet.reachability.Marking;
import de.feu.propra.petrinet.reachability.ReachabilityGraph;

public interface PetriNet {
  void addPlace(String id);

  void addTransition(String id);

  void addArc(String id, String source, String target) throws IllegalConnectionException;

  void setNodeName(String id, String name) throws ElementNotFoundException;

  void setNodePosition(String id, int x, int y) throws ElementNotFoundException;

  void setInitialTokens(String id, int nTokens) throws ElementNotFoundException;

  public Marking getMarking();

  void setMarking(Marking m);

  void triggerTransition(String id);

  Collection<PetriNode> nodes();

  Collection<Arc> arcs();

  boolean addToken(String id);

  boolean removeToken(String id);

  void setCurrentMarkingAsInitial();

  void addNodePropertyChangeListener(PropertyChangeListener listener);

  void removeNodePropertyChangeListener(PropertyChangeListener listener);

  void resetPlaces();

  void reload();

  Collection<String> getActiveTransitionIds();

  boolean isTransition(String id);

  ReachabilityGraph getReachabilityGraph();

  boolean isBounded();
}
