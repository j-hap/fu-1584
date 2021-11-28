package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.feu.propra.petrinet.reachability.Marking;
import de.feu.propra.petrinet.reachability.ReachabilityGraph;
import de.feu.propra.petrinet.reachability.ReachabilityGraphImpl;
import de.feu.propra.petrinet.util.CheckedHashMap;
import de.feu.propra.petrinet.util.CheckedTreeMap;
import de.feu.propra.petrinet.util.DuplicateElementException;
import de.feu.propra.petrinet.util.SimplePnmlParser;
import de.feu.propra.ui.Settings;

/**
 * A concrete implementation of the {@code PetriNet} interface. It creates its
 * own {@code ReachabilityGraph} to track the visited markings. A
 * {@code PetriNetImpl} may be constructed from a PNML file.
 * 
 * @author j-hap 
 *
 */
public class PetriNetImpl implements PetriNet {
  // sorted map, because a marking contains tokens in order of place id
  private SortedMap<String, Place> places;
  private Map<String, Transition> transitions;
  private Map<String, Arc> arcs;
  private ReachabilityGraph rGraph;
  private boolean isInInitialState = true;
  private File file;
  private static final Logger logger = Logger.getLogger((PetriNetImpl.class.getName()));
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());
  private boolean unboundedWarningWasShown = false;

  /**
   * Constructor to start a new {@code PetriNetImpl} from scratch.
   */
  public PetriNetImpl() {
    places = new CheckedTreeMap<>();
    transitions = new CheckedHashMap<>();
    arcs = new CheckedHashMap<>();
    rGraph = new ReachabilityGraphImpl(this);
  }

  /**
   * Constructor to create a {@code PetriNetImpl} from a PNML file.
   * 
   * @param file A PNML file from which the {@code PetriNetImpl} shall be loaded.
   */
  public PetriNetImpl(File file) {
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
   * {@inheritDoc}
   */
  @Override
  public void addPlace(String id) throws DuplicateElementException {
    var p = new Place(id);
    places.put(id, p);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTransition(String id) throws DuplicateElementException {
    var t = new Transition(id);
    transitions.put(id, t);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addArc(String id, String source, String target)
      throws IllegalConnectionException, DuplicateElementException {
    SimplePetriNode sourceNode;
    SimplePetriNode targetNode;
    try {
      sourceNode = getNode(source);
      targetNode = getNode(target);
    } catch (ElementNotFoundException e) {
      throw new IllegalConnectionException("Cannot create Arc, source or target does not exist.");
    }
    if (sourceNode.getType() == targetNode.getType()) {
      throw new IllegalConnectionException("Cannot create Arc, source and target must be different node types.");
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
   * {@inheritDoc}
   */
  @Override
  public void setNodeName(String id, String name) throws ElementNotFoundException {
    getNode(id).setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNodePosition(String id, int x, int y) throws ElementNotFoundException {
    getNode(id).setPosition(x, y);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInitialTokens(String id, int nTokens) throws ElementNotFoundException {
    if (places.containsKey(id)) {
      places.get(id).setInitialTokenCount(nTokens);
    } else {
      throw new ElementNotFoundException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Marking getMarking() {
    var tokenCount = places.values().stream().map(Place::getTokenCount).toArray(Integer[]::new);
    return new Marking(tokenCount);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMarking(Marking m) {
    if (m.equals(getMarking())) {
      return;
    }
    int iPlace = 0;
    for (var p : places.values()) {
      p.setTokenCount(m.getTokenCount(iPlace));
      ++iPlace;
    }
    rGraph.markingChanged(m);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void triggerTransition(String id) {
    if (!isTransition(id)) {
      return;
    }
    var oldMarking = getMarking();
    var t = transitions.get(id);
    if (!t.isActive()) {
      return;
    }
    t.trigger();
    rGraph.addMarking(id, t.getLabel(), oldMarking, getMarking());
    if (Settings.isContinouusBoundednessCheckActive() && !unboundedWarningWasShown && !isBounded()) {
      unboundedWarningWasShown = true;
      logger.warning(bundle.getString("unbounded_info"));
    }
    isInInitialState = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isBounded() {
    return rGraph.isBounded();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<PetriNode> nodes() {
    var nodes = Stream.concat(places.values().stream(), transitions.values().stream()).collect(Collectors.toList());
    return Collections.unmodifiableCollection(nodes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Arc> arcs() {
    return Collections.unmodifiableCollection(arcs.values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addToken(String id) {
    if (!places.containsKey(id)) {
      return false;
    }
    places.get(id).addToken();
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
   * {@inheritDoc}
   */
  @Override
  public void setCurrentMarkingAsInitial() {
    places.values().forEach(Place::setCurrentTokensAsInitial);
    isInInitialState = true;
    rGraph.init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addNodePropertyChangeListener(PropertyChangeListener listener) {
    places.values().forEach(p -> p.addPropertyChangeListener(listener));
    transitions.values().forEach(t -> t.addPropertyChangeListener(listener));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeNodePropertyChangeListener(PropertyChangeListener listener) {
    places.values().forEach(p -> p.removePropertyChangeListener(listener));
    transitions.values().forEach(t -> t.removePropertyChangeListener(listener));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resetPlaces() {
    if (isInInitialState) {
      return;
    }
    places.values().forEach(Place::reset);
    isInInitialState = true;
    rGraph.markingChanged(getMarking());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reload() {
    if (file == null) {
      logger.warning(bundle.getString("not_reloadable_warning"));
      return;
    }
    // must reset all tokens to zero, because omittion in file
    // means 0 tokens
    places.values().forEach(p -> p.setInitialTokenCount(0));
    unboundedWarningWasShown = false;
    loadFromFile(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<String> getActiveTransitionIds() {
    return transitions.values().stream().filter(Transition::isActive).map(Transition::getId)
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTransition(String id) {
    return transitions.containsKey(id);
  }

  private SimplePetriNode getNode(String id) throws ElementNotFoundException {
    if (places.containsKey(id)) {
      return places.get(id);
    } else if (transitions.containsKey(id)) {
      return transitions.get(id);
    } else {
      throw new ElementNotFoundException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReachabilityGraph getReachabilityGraph() {
    return rGraph;
  }
}
