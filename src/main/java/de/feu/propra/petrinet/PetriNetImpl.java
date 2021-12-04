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
import de.feu.propra.reachability.ReachabilityGraphImpl;
import de.feu.propra.ui.Settings;
import de.feu.propra.util.DuplicateElementException;
import de.feu.propra.util.SimplePnmlParser;

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
  private HashSet<String> ids = new HashSet<>(); // for checking uniqueness
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
    places = new TreeMap<>();
    transitions = new HashMap<>();
    arcs = new HashMap<>();
    rGraph = new ReachabilityGraphImpl(this);
    rGraph.init();
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
  public void addPlace(String id) {
    checkValidId(id);
    var p = new Place(id);
    places.put(id, p);
    rGraph.init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTransition(String id) {
    checkValidId(id);
    var t = new Transition(id);
    transitions.put(id, t);
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
   * {@inheritDoc}
   */
  @Override
  public void setNodeName(String id, String name) {
    getNode(id).setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNodePosition(String id, int x, int y) {
    getNode(id).setPosition(x, y);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInitialTokens(String id, int nTokens) {
    var p = getNode(id);
    if (!p.isPlace()) {
      return;
    }
    places.get(id).setInitialTokenCount(nTokens);
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
    if (m.size() != places.size()) {
      throw new IllegalArgumentException();
    }  
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
      logger.info(String.format(bundle.getString("transition_inactive_info"), id));
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
   * {@inheritDoc}
   */
  @Override
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
