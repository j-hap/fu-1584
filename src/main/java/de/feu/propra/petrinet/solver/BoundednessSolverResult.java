package de.feu.propra.petrinet.solver;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.feu.propra.petrinet.reachability.Marking;
import de.feu.propra.ui.Settings;

public class BoundednessSolverResult {
  public File file;
  public boolean isBounded = true;
  public int nodeCount;
  public int edgeCount;
  public List<Marking> problemMarkings;
  public List<String> problemPath;
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

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
