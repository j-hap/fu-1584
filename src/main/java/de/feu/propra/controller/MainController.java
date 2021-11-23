package de.feu.propra.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.feu.propra.petrinet.PetriNetImpl;
import de.feu.propra.petrinet.solver.BatchSolver;
import de.feu.propra.petrinet.solver.BoundednessSolver;
import de.feu.propra.ui.MainView;
import de.feu.propra.ui.MainViewAction;
import de.feu.propra.ui.Settings;
import de.feu.propra.util.FileSelector;

/**
 * The {@code MainController} handles all Actions triggered in the UI and
 * delegates work to other controllers.
 * 
 * @see MainViewAction
 * 
 * @author j-hap 
 *
 */
public class MainController implements ActionListener, ActiveFileChangeListener {
  private Map<String, PetriNetController> netControllers = new HashMap<>();
  private Map<String, ReachabilityGraphController> graphControllers = new HashMap<>();
  private FileSelector fileSelector;
  private SwingTabManager tabManager = new SwingTabManager();
  MainView mainView;
  private static final Logger logger = Logger.getLogger(MainController.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Constructs a {@code MainController} that listens to UI Events and sends
   * feedback to the given {@code MainView}
   * 
   * @param view The {@code MainView} whose Actions are processed
   * @see MainViewAction
   */
  public MainController(MainView view) {
    mainView = view;
    initFileSelector();
    mainView.setCenterPane(tabManager.getTabContainer());
    tabManager.addActiveFileChangeListener(fileSelector);
    tabManager.addActiveFileChangeListener(this);
  }

  /**
   * Constructs a {@code FileSelector} with the default start directory.
   */
  private void initFileSelector() {
    var startDir = new File("../ProPra-WS21-Basis/Beispiele");
    if (!startDir.exists()) {
      startDir = new File("");
    }
    fileSelector = new FileSelector(startDir);
  }

  /**
   * Handles all {@code MainViewAction}
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    var source = event.getSource();
    if (!(source instanceof MainViewAction)) {
      return;
    }
    delegateAction((MainViewAction) source);
  }

  /**
   * Determines necessary steps to take, depending on the {@code caller}.
   * 
   * @param caller The MainViewAction item that triggered the event.
   */
  private void delegateAction(MainViewAction caller) {
    switch (caller) {
    case OPEN_FILE -> openMultipleFiles();
    case PREVIOUS_FILE -> openSingleFile(fileSelector.getPrevious());
    case NEXT_FILE -> openSingleFile(fileSelector.getNext());
    case RELOAD_FILE -> reloadFile();
    case CLOSE_FILE -> closeFile();
    case BATCH -> solveBatch();
    case REMOVE_TOKEN -> {
      getActiveNetController().removeInitialTokenFromSelectedPlaces();
      mainView.setModifiedMarker(getActiveNetController().initialMarkingIsModified());
    }
    case FREEZE_TOKENS -> {
      getActiveNetController().setCurrentMarkingAsInitial();
      mainView.setModifiedMarker(getActiveNetController().initialMarkingIsModified());
    }
    case ADD_TOKEN -> {
      getActiveNetController().addInitialTokenToSelectedPlaces();
      mainView.setModifiedMarker(getActiveNetController().initialMarkingIsModified());
    }
    case DELETE_GRAPH -> deleteGraph();
    case RESET_NET -> getActiveNetController().resetModel();
    case BOUNDS_CHECK -> runBoundsCheck();
    case FIT_TO_VIEW -> getActiveNetController().fitView();
    default -> logger.warning("Missing implementation.");
    }
  }

  private void openMultipleFiles() {
    var filesToOpen = fileSelector.getUserSelectionMulti();
    for (var file : filesToOpen) {
      openSingleFile(file);
    }
  }

  private void openSingleFile(File file) {
    if (tabManager.hasTab(file)) {
      tabManager.switchToTab(file);
      mainView.setModifiedMarker(getActiveNetController().initialMarkingIsModified());
      return;
    }
    loadFile(file);
  }

  private void loadFile(File file) {
    var filename = file.getAbsolutePath();
    var net = new PetriNetImpl(file);
    var rGraph = net.getReachabilityGraph();
    var netController = new PetriNetController(net);
    var graphController = new ReachabilityGraphController(rGraph);

    netControllers.put(filename, netController);
    graphControllers.put(filename, graphController);
    tabManager.addTab(file, netController.getView(), graphController.getView());
    MainViewAction.enableGraphActions();
  }

  private void reloadFile() {
    getActiveNetController().reloadModel();
    mainView.setModifiedMarker(false);
  }

  private PetriNetController getActiveNetController() {
    return netControllers.get(getCurrentFilename());
  }

  private ReachabilityGraphController getActiveGraphController() {
    return graphControllers.get(getCurrentFilename());
  }

  private String getCurrentFilename() {
    var f = fileSelector.getCurrent();
    if (f == null) {
      return "";
    } else {
      return f.getAbsolutePath();
    }
  }

  private void closeFile() {
    var filename = getCurrentFilename();
    netControllers.remove(filename);
    graphControllers.remove(filename);
    tabManager.closeCurrentTab();
    if (netControllers.isEmpty()) {
      MainViewAction.disableGraphActions();
    }
  }

  private void solveBatch() {
    var files = fileSelector.getUserSelectionMulti();
    var solver = new BatchSolver(files);
    mainView.showWaitCursor();
    solver.solve();
    mainView.resetCursor();
    solver.printResults();
  }

  private void deleteGraph() {
    var graphController = getActiveGraphController();
    graphController.clearModel();
  }

  private void runBoundsCheck() {
    deleteGraph();
    var solver = new BoundednessSolver(getActiveNetController().getModel(), getActiveGraphController().getModel());
    mainView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    solver.solve();
    mainView.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    var result = solver.getResult();
    logger.info(result.toString());
    if (result.isBounded) {
      mainView.showPopup(bundle.getString("bounded_info"));
    } else {
      var controller = getActiveGraphController();
      controller.highlightPath(result.problemPath);
      for (var m : result.problemMarkings) {
        controller.markProblem(m);
      }
      controller.deselectAllEdges();
      mainView.showPopup(bundle.getString("unbounded_info"));
    }
  }

  /**
   * {@inheritDoc}
   * Sends necessary information to {@code MainView} for user feedback. 
   */
  @Override
  public void fileChanged(ActiveFileChangeEvent e) {
    if (e.getFile() == null) {
      mainView.setStatusMessage("");
      mainView.setModifiedMarker(false);
    } else {
      var filename = e.getFile().getAbsolutePath();
      var netController = netControllers.get(filename);
      mainView.setModifiedMarker(netController.initialMarkingIsModified());
      mainView.setStatusMessage(filename);
    }
  }
}
