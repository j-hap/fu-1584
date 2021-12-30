package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.feu.propra.reachability.Marking;
import de.feu.propra.reachability.ReachabilityGraph;
import de.feu.propra.ui.Settings;
import de.feu.propra.util.DuplicateElementException;
import de.feu.propra.util.SimplePnmlParser;

/**
 * A Petri Net representation that allows creation and interaction as well as
 * listening to changes. It creates its own {@code ReachabilityGraph} to track
 * the visited markings. A {@code PetriNet} may be constructed from a PNML file.
 * 
 * @author j-hap 
 *
 */
public class PetriNet {
  // sorted map, because a marking contains tokens in order of place id
  private SortedMap<String, Place> places;
  private Map<String, Transition> transitions;
  private Map<String, Arc> arcs;
  private HashSet<String> ids = new HashSet<>(); // for checking uniqueness
  private ReachabilityGraph rGraph;
  private boolean isInInitialState = true;
  private File file;
  private static final Logger logger = Logger.getLogger((PetriNet.class.getName()));
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());  

  /**
   * Constructor to start a new {@code PetriNetImpl} from scratch.
   */
  public PetriNet() {
    places = new TreeMap<>();
    transitions = new HashMap<>();
    arcs = new HashMap<>();
    rGraph = new ReachabilityGraph(this);
    rGraph.init();
  }

  /**
   * Constructor to create a {@code PetriNetImpl} from a PNML file.
   * 
   * @param file A PNML file from which the {@code PetriNetImpl} shall be loaded.
   */
  public PetriNet(File file) {
    this();
    // parser swallows file not found exception, so we test for it here
    // to give better feedback
    if (!file.exists()) {
      logger.warning("File " + file.getAbsolutePath() + " not found.");
      return;
    }
    this.file = file;
    loadFromFile(true);
  }

  private void loadFromFile(boolean createElements) {
    var parser = new SimplePnmlParser(file, this);
    if (createElements) {
      parser.loadFile();
    } else {
      parser.reloadFile();
    }
    isInInitialState = true;
    rGraph.init();
  }

  /**
   * Adds a {@code Place} to the net.
   * 
   * @param id Unique ID of the {@code Place} to be added.
   */
  public void addPlace(String id) {
    checkValidId(id);
    var p = new Place(id);
    places.put(id, p);
    rGraph.init();
  }

  /**
   * Adds a {@code Transition} to the net.
   * 
   * @param id Unique ID of the {@code Transition} to be added.
   */
  public void addTransition(String id) {
    checkValidId(id);
    var t = new Transition(id);
    transitions.put(id, t);
  }

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
  public void addArc(String id, String source, String target) {
    checkValidId(id);
    SimplePetriNode sourceNode;
    SimplePetriNode targetNode;
    sourceNode = getNode(source);
    targetNode = getNode(target);

    if (sourceNode.getType() == targetNode.getType()) {
      var msg = String.format(bundle.getString("illegal_connection"), source, target);
      throw new IllegalConnectionException(msg);
    }

    if (sourceNode.isTransition()) {
      var transition = (Transition) sourceNode;
      var place = (Place) targetNode;
      transition.addSuccessor(place);
    } else {
      var transition = (Transition) targetNode;
      var place = (Place) sourceNode;
      place.addPropertyChangeListener(transition);
      transition.addPredecessor(place);
    }
    arcs.put(id, new Arc(id, sourceNode, targetNode));
  }

  /**
   * Renames a {@code PetriNode}.
   * 
   * @param id   Unique ID of the {@code PetriNode} to be renamed.
   * @param name New Name of the {@code PetriNode}.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  public void setNodeName(String id, String name) {
    getNode(id).setName(name);
  }

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
  public void setNodePosition(String id, int x, int y) {
    getNode(id).setPosition(x, y);
  }

  /**
   * Sets the number of initial tokens an a {@code PetriNode}.
   * 
   * @param id      Unique ID of the {@code PetriNode} to be modified.
   * @param nTokens Number of Tokens that {@code PetriNode} shall have as its
   *                initial token count.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  public void setInitialTokens(String id, int nTokens) {
    var p = getNode(id);
    if (!p.isPlace()) {
      return;
    }
    places.get(id).setInitialTokenCount(nTokens);
  }

  /**
   * Collects the current token count of all places and returns it as a
   * {@code Marking} object.
   * 
   * @return The current {@code Marking} of the {@code PetriNet}.
   * @see de.feu.propra.reachability.Marking
   */
  public Marking getMarking() {
    var tokenCount = places.values().stream().map(Place::getTokenCount).toArray(Integer[]::new);
    return new Marking(tokenCount);
  }

  /**
   * Sets the current marking of the {@code PetriNet}. Is is not checked if that
   * marking is possible to reach from the current initial marking. The tokens in
   * the {@code Marking} are ordered alphabetically by ID of the {@code Place}s.
   * 
   * @param marking The new marking of the {@code PetriNet}.
   * @throws IllegalArgumentException If the number of token counts in the given
   *                                  {@code Marking} does not match the number of
   *                                  {@code Place}s in the {@code PetriNet}.
   */
  public void setMarking(Marking marking) {
    if (marking.size() != places.size()) {
      throw new IllegalArgumentException();
    }
    if (marking.equals(getMarking())) {
      return;
    }
    int iPlace = 0;
    for (var p : places.values()) {
      p.setTokenCount(marking.getTokenCount(iPlace));
      ++iPlace;
    }
    rGraph.markingChanged(marking);
  }

  /**
   * Triggers a {@code Transition} to redistribute tokens. If the given id is not
   * a {@code Transition}, the method does nothing.
   * 
   * @param id Unique ID of the {@code Transition} to be triggered.
   * @throws ElementNotFoundException If a {@code PetriNode} with the given ID is
   *                                  not found.
   */
  public void triggerTransition(String id) {
    if (!isTransition(id)) {
      return;
    }
    var oldMarking = getMarking();
    var t = transitions.get(id);
    if (!t.isActive()) {
      logger.info(String.format(bundle.getString("transition_inactive_info"), id));
      return;
    }
    t.trigger();
    rGraph.addMarking(id, t.getLabel(), oldMarking, getMarking());
    isInInitialState = false;
  }

  /**
   * Tells if the the represented Petri Net is known to have an unlimited number
   * of states it can reach. The return values only relies on already visited
   * states. This method does not trigger an analysis.
   * 
   * @return True if none of the already visited markings indicate unboundedness.
   *         False otherwise.
   */
  public boolean isBounded() {
    return rGraph.isBounded();
  }

  /**
   * @return An unmodifiable collection of all {@code PetriNode}s of the
   *         {@code PetriNet}.
   */
  public Collection<PetriNode> nodes() {
    var nodes = Stream.concat(places.values().stream(), transitions.values().stream()).collect(Collectors.toList());
    return Collections.unmodifiableCollection(nodes);
  }

  /**
   * @return An unmodifiable collection of all {@code Arc}s of the
   *         {@code PetriNet}.
   */
  public Collection<Arc> arcs() {
    return Collections.unmodifiableCollection(arcs.values());
  }

  /**
   * Increases the initial token count of a {@code Place} by one. Does nothing if
   * a {@code Place} with the given ID is not present in the {@code PetriNet}
   * 
   * @param id Unique ID of the {@code PetriNode} to be modified.
   * @return True of success, false if not.
   */
  public boolean addToken(String id) {
    if (!places.containsKey(id)) {
      return false;
    }
    places.get(id).addToken();
    return true;
  }

  /**
   * Decreases the initial token count of a {@code Place} by one. Does nothing if
   * a {@code Place} with the given ID is not present in the {@code PetriNet}
   * 
   * @param id Unique ID of the {@code PetriNode} to be modified.
   * @return True of success, false if not.
   */
  public boolean removeToken(String id) {
    if (!places.containsKey(id)) {
      return false;
    }
    var p = places.get(id);
    if (p.hasTokens()) {
      p.removeToken();
      return true;
    } else {
      logger.warning(bundle.getString("Place") + " " + id + " " + bundle.getString("out_of_tokens") + ".");
      return false;
    }
  }

  /**
   * Freezes the current marking and sets the current token count as the initial
   * token count of each {@code Place}.
   */
  public void setCurrentMarkingAsInitial() {
    places.values().forEach(Place::setCurrentTokensAsInitial);
    isInInitialState = true;
    rGraph.init();
  }

  /**
   * Adds a {@code PropertyChangeListener} to each {@code PetriNode} of the
   * {@code PetriNet}.
   * 
   * @param listener The listener to add to each {@code PetriNode}.
   */
  public void addNodePropertyChangeListener(PropertyChangeListener listener) {
    places.values().forEach(p -> p.addPropertyChangeListener(listener));
    transitions.values().forEach(t -> t.addPropertyChangeListener(listener));
  }

  /**
   * Removes a {@code PropertyChangeListener} from each {@code PetriNode} of the
   * {@code PetriNet}.
   * 
   * @param listener The listener to remove from each {@code PetriNode}.
   */
  public void removeNodePropertyChangeListener(PropertyChangeListener listener) {
    places.values().forEach(p -> p.removePropertyChangeListener(listener));
    transitions.values().forEach(t -> t.removePropertyChangeListener(listener));
  }

  /**
   * Sets the current token count of each {@code Place} to its initial token
   * count.
   */
  public void resetPlaces() {
    if (isInInitialState) {
      return;
    }
    places.values().forEach(Place::reset);
    isInInitialState = true;
    rGraph.markingChanged(getMarking());
  }

  /**
   * Reloads the properties (name, position, token count) of each
   * {@code PetriNode} from the file, from which the {@code PetriNet} was created.
   */
  public void reload() {
    if (file == null) {
      logger.warning(bundle.getString("not_reloadable_warning"));
      return;
    }
    // must reset all tokens to zero, because omission in file
    // means 0 tokens
    places.values().forEach(p -> p.setInitialTokenCount(0));
    loadFromFile(false);
  }

  /**
   * @return The IDs of all active {@code Transition}s in the {@code PetriNet}.
   */
  public Collection<String> getActiveTransitionIds() {
    return transitions.values().stream().filter(Transition::isActive).map(Transition::getId)
        .collect(Collectors.toList());
  }

  /**
   * @param id Unique ID of the {@code PetriNode} to be checked.
   * @return True if the given ID belongs to a {@code Transition}, false
   *         otherwise.
   * @throws ElementNotFoundException If there is no Element with the given ID.
   */
  public boolean isTransition(String id) {
    return getNode(id).isTransition();
  }

  private SimplePetriNode getNode(String id) {
    if (places.containsKey(id)) {
      return places.get(id);
    } else if (transitions.containsKey(id)) {
      return transitions.get(id);
    } else {
      var msg = String.format(bundle.getString("id_does_not_exist_warning"), id);
      throw new ElementNotFoundException(msg);
    }
  }

  /**
   * @return The child {@code ReachabilityGraph} that tracks all visited markings.
   */
  public ReachabilityGraph getReachabilityGraph() {
    return rGraph;
  }

  private void checkValidId(String id) {
    if (ids.contains(id)) {
      throw new DuplicateElementException(String.format(bundle.getString("id_in_use_warning"), id));
    }
    ids.add(id);
  }
}
