package de.feu.propra.petrinet.solver;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.feu.propra.petrinet.reachability.Marking;
import de.feu.propra.ui.Settings;

/**
 * Collection of Solver result properties that are collected during a
 * {@link BoundednessSolver#solve()} run.
 * 
 * @author j-hap 
 *
 */
public class BoundednessSolverResult {
  /**
   * The {@code File} on which the {@code BoundednessSolver} was executed.
   */
  public File file;
  /**
   * The boundedness state.
   */
  public boolean isBounded = true;
  /**
   * The number of nodes in the reachability graph when the
   * {@code BoundednessSolver} terminated.
   */
  public int nodeCount;
  /**
   * The number of edges in the reachability graph, i.e. visited transitions of
   * the {@code PetriNet} when the {@code BoundednessSolver} terminated.
   */
  public int edgeCount;
  /**
   * The {@code Marking}s that violate the m &lt;-&gt; m' relation. Is null if the
   * {@code PetriNet} is bounded.
   */
  public List<Marking> problemMarkings;
  /**
   * The list of visited {@code Transition}s in the {@code PetriNet} until a pair
   * of m &lt;-&gt; m' {@code Markings} was encountered. Is null if the
   * {@code PetriNet} is bounded.
   */
  public List<String> problemPath;
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Converts the solver results into a printable list.
   * 
   * @return A formatted {@code String} for user information.
   */
  public String toString() {
    var sb = new StringBuilder();
    sb.append(bundle.getString("Result")).append(":\n");
    sb.append("\t").append(bundle.getString("bounded")).append(": ");
    if (isBounded) {
      sb.append(bundle.getString("yes")).append("\n");
      sb.append("\t").append(bundle.getString("Nodes")).append(": ").append(nodeCount).append("\n");
      sb.append("\t").append(bundle.getString("Edges")).append(": ").append(edgeCount).append("\n");
    } else {
      sb.append(bundle.getString("no")).append("\n");
      sb.append("\t").append(bundle.getString("Pathlength")).append(": ").append(problemPath.size()).append("\n");
      sb.append("\t").append(bundle.getString("Path")).append(": ").append("(")
          .append(problemPath.stream().collect(Collectors.joining(","))).append(")").append("\n");
      sb.append("\t").append("m").append(":  ").append(problemMarkings.get(0)).append("\n");
      sb.append("\t").append("m'").append(": ").append(problemMarkings.get(1)).append("\n");
    }
    return sb.toString();
  }
}
