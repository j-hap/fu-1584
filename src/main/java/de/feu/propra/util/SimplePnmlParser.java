package de.feu.propra.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.feu.propra.petrinet.ArcPlan;
import de.feu.propra.petrinet.ElementNotFoundException;
import de.feu.propra.petrinet.IllegalConnectionException;
import de.feu.propra.petrinet.PetriNet;
import de.feu.propra.ui.Settings;
import propra.pnml.PNMLWopedParser;

/**
 * The {@code SimplePnmlParser} parses an PNML file and handles the creation of
 * {@code Place}s, {@code Transition}s and {@code Arc}s as well as positions,
 * IDs and names of nodes and the initial token count of {@code Place}s.
 * 
 * @see <a href= "https://en.wikipedia.org/wiki/Petri_Net_Markup_Language">Petri
 *      Net Markup Language</a>
 * 
 * @author j-hap 
 *
 */
public class SimplePnmlParser extends PNMLWopedParser implements PnmlParser {
  private List<ArcPlan> arcPlans = new ArrayList<>();
  private PetriNet net;
  private HashSet<String> ids = new HashSet<>(); // for checking uniqueness
  private boolean skipElementCreation = false; // flag that determines if elements are created
  private static final Logger logger = Logger.getLogger(SimplePnmlParser.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Constructs a parser for the given file and a PetriNet that gets filled by
   * this parser.
   * 
   * @param pnmlFile The {@code File} to be parsed.
   * @param petriNet The {@code PetriNet} to be filled with components / whose
   *                 properties shall be updated.
   */
  public SimplePnmlParser(File pnmlFile, PetriNet petriNet) {
    super(pnmlFile);
    net = petriNet;
    initParser();
  }

  private void addArcsToNet() {
    // after all nodes are processed, the arcs are added to the model
    for (var s : arcPlans) {
      try {
        net.addArc(s.id, s.sourceId, s.targetId);
      } catch (IllegalConnectionException e) {
        logger.warning(String.format(bundle.getString("illegal_connection"), s.sourceId, s.targetId));
      } catch (DuplicateElementException e) {
        logger.warning(String.format(bundle.getString("arc_exists_warning"), s.sourceId, s.targetId));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadFile() {
    ids.clear();
    arcPlans.clear();
    parse();
    addArcsToNet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reloadFile() {
    skipElementCreation = true;
    loadFile();
    skipElementCreation = false;
  }

  /**
   * Creates a new {@code Transition}.
   * 
   * @param id The ID of the created {@code Transition}.
   */
  @Override
  public void newTransition(String id) {
    if (skipElementCreation || !isValidId(id)) {
      return;
    }
    try {
      net.addTransition(id);
    } catch (DuplicateElementException e) {
      logger.warning(String.format(bundle.getString("transition_id_exists_warning"), id));
    }
  }

  /**
   * Creates a new {@code Place}.
   * 
   * @param id The ID of the created {@code Place}.
   */
  @Override
  public void newPlace(String id) {
    if (skipElementCreation || !isValidId(id)) {
      return;
    }
    try {
      net.addPlace(id);
    } catch (DuplicateElementException e) {
      logger.warning(String.format(bundle.getString("place_id_exists_warning"), id));
    }
  }

  /**
   * Adds the {@code Arc} definition to a list of {@code ArcPlan}s. The list of
   * {@code ArcPlan}s must be handled after parsing the whole file to make sure
   * that all the {@code Place}s and {@code Transition}s are already created.
   * 
   * @param id       The ID of the {@code Arc} to create.
   * @param sourceId The ID of the source node.
   * @param targetId The ID of the target node.
   */
  @Override
  public void newArc(String id, String sourceId, String targetId) {
    if (skipElementCreation || !isValidId(id)) {
      return;
    }
    arcPlans.add(new ArcPlan(id, sourceId, targetId));
  }

  private boolean isValidId(String id) {
    if (ids.contains(id)) {
      logger.severe(String.format(bundle.getString("id_in_use_warning"), id));      
      return false;
    }
    ids.add(id);
    return true;
  }

  /**
   * Sets the position of the {@code PetriNode} with the given ID.
   * 
   * @param id The ID of the {@code PetriNode} whose position shall be modified.
   * @param x  The x-coordinate of the {@code PetriNode}.
   * @param y  The y-coordinate of the {@code PetriNode}.
   */
  @Override
  public void setPosition(String id, String x, String y) {
    int xPos = Integer.valueOf(x);
    int yPos = Integer.valueOf(y);
    try {
      net.setNodePosition(id, xPos, yPos);
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

  /**
   * Sets the name of the {@code PetriNode} with the given ID.
   * 
   * @param id   The ID of the {@code PetriNode} whose name shall be modified.
   * @param name The new name of the {@code PetriNode}.
   */
  @Override
  public void setName(String id, String name) {
    try {
      net.setNodeName(id, name);
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

  /**
   * Sets the initial token count of the {@code Place} with the given ID.
   * 
   * @param id     The ID of the {@code Place} whose initial token count shall be
   *               modified.
   * @param tokens The initial number of tokens (as String).
   */
  @Override
  public void setTokens(String id, String tokens) {
    try {
      net.setInitialTokens(id, Integer.valueOf(tokens));
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

}
