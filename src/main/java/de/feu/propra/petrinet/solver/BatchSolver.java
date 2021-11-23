package de.feu.propra.petrinet.solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.feu.propra.petrinet.PetriNetImpl;
import de.feu.propra.ui.Settings;

public class BatchSolver {
  private List<BoundednessSolverResult> results = new ArrayList<>();
  private File[] files;
  private static final Logger logger = Logger.getLogger(BatchSolver.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  public BatchSolver(File[] f) {
    files = f;
  }

  public void solve() {
    for (var f : files) {
      checkSingleFile(f);
    }
  }

  public void printResults() {
    logger.info(new ResultsFormatter().format(results).toString());
  }

  private void checkSingleFile(File f) {
    logger.info(bundle.getString("Checking") + " " + f.getName());
    var net = new PetriNetImpl(f);
    var rGraph = net.getReachabilityGraph();
    var solver = new BoundednessSolver(net, rGraph);
    solver.solve();
    var res = solver.getResult();
    res.file = f;
    results.add(res);
    logger.info(res.toString());
  }
}
