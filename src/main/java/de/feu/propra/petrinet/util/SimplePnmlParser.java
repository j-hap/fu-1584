package de.feu.propra.petrinet.util;

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

public class SimplePnmlParser extends PNMLWopedParser implements PnmlParser {
  private List<ArcPlan> arcPlans = new ArrayList<>();
  private PetriNet petriNet;
  private boolean noCreate = false; // flag that determines if elements are created
  private static final Logger logger = Logger.getLogger(SimplePnmlParser.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  public SimplePnmlParser(File pnml, PetriNet pn) {
    super(pnml);
    petriNet = pn;
    initParser();
  }

  private void addArcsToNet() {
    // after all nodes are processed, the arcs are added to the model
    for (var s : arcPlans) {
      try {
        petriNet.addArc(s.id, s.sourceId, s.targetId);
      } catch (IllegalConnectionException e) {
        logger.warning(String.format(bundle.getString("illegal_connection"), s.sourceId, s.targetId));
      } catch (DuplicateElementException e) {
        logger.warning(String.format(bundle.getString("arc_exists_warning"), s.sourceId, s.targetId));
      }
    }
  }

  @Override
  public void loadFile() {
    parse();
    addArcsToNet();
  }

  // skips creation of transitions, places and arcs and only reads properties from
  // file
  @Override
  public void reloadFile() {
    noCreate = true;
    parse();
    noCreate = false;
  }

  @Override
  public void newTransition(String id) {
    if (noCreate) {
      return;
    }
    try {
      petriNet.addTransition(id);
    } catch (DuplicateElementException e) {
      logger.warning(String.format(bundle.getString("transition_id_exists_warning"), id));
    }
  }

  @Override
  public void newPlace(String id) {
    if (noCreate) {
      return;
    }
    try {
      petriNet.addPlace(id);
    } catch (DuplicateElementException e) {
      logger.warning(String.format(bundle.getString("place_id_exists_warning"), id));
    }
  }

  @Override
  public void newArc(String id, String source, String target) {
    if (noCreate) {
      return;
    }
    // there is no guarantee that the referenced IDs are already available as
    // objects, so we must collect all definitions first
    arcPlans.add(new ArcPlan(id, source, target));
  }

  @Override
  public void setPosition(String id, String x, String y) {
    int xPos = Integer.valueOf(x);
    int yPos = Integer.valueOf(y);
    try {
      petriNet.setNodePosition(id, xPos, yPos);
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

  @Override
  public void setName(String id, String name) {
    try {
      petriNet.setNodeName(id, name);
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

  @Override
  public void setTokens(String id, String tokens) {
    try {
      petriNet.setInitialTokens(id, Integer.valueOf(tokens));
    } catch (ElementNotFoundException e) {
      logger.warning(String.format(bundle.getString("id_does_not_exist_warning"), id));
    }
  }

}
