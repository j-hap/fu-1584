package de.feu.propra.petrinet.solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.feu.propra.petrinet.PetriNetImpl;
import de.feu.propra.ui.Settings;

/**
 * The {@code BatchSolver} is a utility class for running a boundedness check on
 * an arbitraty number of PNML files.
 * 
 * @author j-hap 
 *
 */
public class BatchSolver {
  private List<BoundednessSolverResult> results = new ArrayList<>();
  private File[] files;
  private static final Logger logger = Logger.getLogger(BatchSolver.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Constructor for a BatchSolver for the given {@code File}s.
   * 
   * @param fileList The {@code File}s to analyze.
   */
  public BatchSolver(File[] fileList) {
    files = fileList;
  }

  /**
   * Runs {@code BoundednessSolver} check on all files.
   */
  public void solve() {
    for (var f : files) {
      checkSingleFile(f);
    }
  }

  /**
   * Prints formatted results to the active Logger.
   */
  public void printResults() {
    logger.info(new ResultsFormatter().format(results).toString());
  }

  private void checkSingleFile(File f) {
    logger.info(bundle.getString("Checking") + " " + f.getName());
    var net = new PetriNetImpl(f);
    var solver = new BoundednessSolver(net);
    var res = solver.solve();
    res.file = f;
    results.add(res);
    logger.info(res.toString());
  }
}
