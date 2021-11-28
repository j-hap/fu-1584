package de.feu.propra.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.feu.propra.controller.PetriNetController;
import de.feu.propra.ui.Settings;

/**
 * Utility class for formatting a {@code List} of
 * {@code BoundednessSolverResult} into a printable table.
 * 
 * @author j-hap 
 *
 */
public class ResultsFormatter {
  private final int nHeaderLines = 2;
  private Appendable buffer;
  private List<List<String>> columns;
  private static final Logger logger = Logger.getLogger(PetriNetController.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  /**
   * Constructs a {@code ResultsFormatter} with a default {@code String} buffer.
   */
  public ResultsFormatter() {
    this(new StringBuilder());
  }

  /**
   * Constructs a {@code ResultsFormatter} with a given {@code String} buffer.
   * 
   * @param buffer The {@code Appendable} used a a String buffer.
   */
  public ResultsFormatter(Appendable buffer) {
    this.buffer = buffer;
  }

  /**
   * Formats the given {@code List} of {@code BoundednessSolverResult} into a
   * printable table.
   * 
   * @param results The {@code List} of {@code BoundednessSolverResult} to format.
   * @return A reference to the {@code ResultsFormatter}.
   */
  public ResultsFormatter format(List<BoundednessSolverResult> results) {
    initColumns(results.size());

    // first two columns are handled the same, no matter what the result is
    results.forEach(r -> columns.get(0).add(r.file.getName()));
    results.forEach(r -> columns.get(1).add(boolean2String(r.isBounded)));

    buildThirdColumn(results);
    prependHeader(columns);
    for (var c : columns) {
      padToMaxLength(c);
    }

    try {
      putColumnsIntoBuffer();
    } catch (IOException e) {
      logger.severe("Failed to append to String buffer.");
    }
    return this;
  }

  private void initColumns(int resultCount) {
    columns = new ArrayList<>(3);
    for (int i = 0; i < 3; ++i) {
      columns.add(new ArrayList<>(resultCount));
    }
  }

  private void buildThirdColumn(List<BoundednessSolverResult> results) {
    var fmt = getFormatStrings(results);
    var col = columns.get(2);
    for (var r : results) {
      if (r.isBounded) {
        col.add(String.format(fmt[0], r.nodeCount, r.edgeCount));
      } else {
        var pathLength = r.problemPath.size();
        var path = "(" + r.problemPath.stream().collect(Collectors.joining(",")) + "); ";
        col.add(String.format(fmt[1], pathLength, path));//
      }
    }
    // quite ugly appending the necessary pieces, but the desired format
    // is quite ugly...
    padToMaxLength(col);
    appendWithLambda(col, results, r -> r.problemMarkings.get(0).toString() + ", ");
    padToMaxLength(col);
    appendWithLambda(col, results, r -> r.problemMarkings.get(1).toString());
  }

  private String[] getFormatStrings(List<BoundednessSolverResult> results) {
    int maxFirstNumber = 0;
    int maxEdgeCount = 0;
    for (var r : results) {
      if (r.isBounded) {
        maxFirstNumber = Math.max(maxFirstNumber, r.nodeCount);
        maxEdgeCount = Math.max(maxEdgeCount, r.edgeCount);
      } else {
        maxFirstNumber = Math.max(maxFirstNumber, r.problemPath.size());
      }
    }

    int firstNumberWidth = String.valueOf(maxFirstNumber).length();
    int edgeCountWidth = String.valueOf(maxEdgeCount).length();

    var fmtBounded = "%" + firstNumberWidth + "d / %" + edgeCountWidth + "d";
    var fmtUnbounded = "%" + firstNumberWidth + "d:%s";
    return new String[] { fmtBounded, fmtUnbounded };
  }

  private void putColumnsIntoBuffer() throws IOException {
    final String SEPARATOR = " | ";
    var nRows = columns.get(0).size();
    for (int iRow = 0; iRow < nRows; ++iRow) {
      if (iRow == nHeaderLines) {
        buffer.append(getSeparationLine());
        buffer.append("\n");
      }
      var row = new ArrayList<String>(3);
      for (var c : columns) {
        row.add(c.get(iRow));
      }
      buffer.append(row.stream().collect(Collectors.joining(SEPARATOR)));
      buffer.append("\n");
    }
  }

  private String getSeparationLine() {
    var row = new ArrayList<String>(3);
    for (var col : columns) {
      row.add("-".repeat(col.get(0).length()));
    }
    return row.stream().collect(Collectors.joining("-|-"));
  }

  /**
   * Returns the formattes results in the buffer as a String.
   * 
   * @return The buffered String.
   */
  public String toString() {
    return buffer.toString();
  }

  private static void appendWithLambda(List<String> list, List<BoundednessSolverResult> res,
      Function<BoundednessSolverResult, String> op) {
    var iRow = list.size();
    for (int iRes = res.size(); iRes-- > 0;) {
      --iRow;
      var r = res.get(iRes);
      if (!r.isBounded) {
        list.set(iRow, list.get(iRow) + op.apply(r));
      }
    }
  }

  private static void padToMaxLength(List<String> list) {
    var padTo = list.stream().mapToInt(String::length).max().getAsInt();
    var fmt = "%-" + padTo + "s";
    list.replaceAll(s -> String.format(fmt, s));
  }

  private static String boolean2String(boolean b) {
    return (b ? bundle.getString("yes") : bundle.getString("no"));
  }

  private static void prependHeader(List<List<String>> columns) {
    columns.get(0).addAll(0, Arrays.asList(new String[] { "", bundle.getString("Filename") }));
    columns.get(1).addAll(0, Arrays.asList(new String[] { "", bundle.getString("bounded") }));
    columns.get(2).addAll(0,
        Arrays.asList(new String[] {
            bundle.getString("Nodes") + " / " + bundle.getString("Edges") + " " + bundle.getString("resp") + ".",
            bundle.getString("Pathlength") + ":" + bundle.getString("Path") + "; m, m'" }));
  }

}
