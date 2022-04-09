package de.feu.propra.util;

import java.io.File;
import java.util.ArrayList;
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
  private boolean skipElementCreation = false; // flag that determines if elements are created
  private boolean skipProperties = false; // flag to skip properties if there is an error during element creation
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
      } catch (IllegalConnectionException | DuplicateElementException | ElementNotFoundException e) {
        var msg = bundle.getString("arc_error_prefix") + " " + e.getMessage();
        logger.severe(msg);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadFile() {
    // in case the last element previously created triggered to skip properties of
    // nodes AND the parser is reused
    skipProperties = false;
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
    skipProperties = false;
    if (skipElementCreation) {
      return;
    }
    try {
      net.addTransition(id);
    } catch (DuplicateElementException e) {
      var msg = bundle.getString("transition_error_prefix") + " " + e.getMessage();
      logger.severe(msg);
      skipProperties = true;
    }
  }

  /**
   * Creates a new {@code Place}.
   * 
   * @param id The ID of the created {@code Place}.
   */
  @Override
  public void newPlace(String id) {
    skipProperties = false;
    if (skipElementCreation) {
      return;
    }
    try {
      net.addPlace(id);
    } catch (DuplicateElementException e) {
      var msg = bundle.getString("place_error_prefix") + " " + e.getMessage();
      logger.severe(msg);
      skipProperties = true;
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
    if (skipElementCreation) {
      return;
    }
    arcPlans.add(new ArcPlan(id, sourceId, targetId));
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
    if (skipProperties) {
      return;
    }
    // according to https://www.pnml.org/papers/pnnl76.pdf
    // the position values are "decimal", so can be non-integer
    // Graphstream can't handle BigDecimal like org.apache.xmlbeans.XmlDecimal uses,
    // so double must do.
    var xPos = Double.valueOf(x);
    var yPos = Double.valueOf(y);
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
    if (skipProperties) {
      return;
    }
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
    if (skipProperties) {
      return;
    }
    try {
      net.setInitialTokens(id, Integer.valueOf(tokens));
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

}
