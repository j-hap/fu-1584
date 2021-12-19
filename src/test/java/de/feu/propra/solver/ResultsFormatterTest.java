package de.feu.propra.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.feu.propra.reachability.Marking;

class ResultsFormatterTest {
  List<BoundednessSolverResult> results = new ArrayList<>();
  List<BoundednessSolverResult> emptyResultsList = new ArrayList<>();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    addBoundedResults();
    addUnboundedResults();
  }

  void addBoundedResults() {
    var r = new BoundednessSolverResult();
    r.isBounded = true;
    r.nodeCount = 100;
    r.edgeCount = 1;
    r.file = new File("firstName");
    results.add(r);
  }

  void addUnboundedResults() {
    var r = new BoundednessSolverResult();
    r.isBounded = false;
    r.problemMarkings.add(new Marking(1, 2, 3));
    r.problemMarkings.add(new Marking(1, 3, 3));
    r.problemPath = new ArrayList<>();
    r.problemPath.add("t1");
    r.file = new File("secondName");
    results.add(r);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void returnsHeaderOnlyOnEmptyInputEnglish() {
    var expected = """
                 |         | Nodes / Edges resp.\s\s\s
        Filename | bounded | Pathlength:Path; m, m'
        ---------|---------|-----------------------
        """;
    var s = new ResultsFormatter(Locale.US).format(emptyResultsList).toString();
    assertEquals(expected, s);
    s = new ResultsFormatter().format(Locale.US, emptyResultsList).toString();
    assertEquals(expected, s);
  }

  @Test
  void returnsHeaderOnlyOnEmptyInputGerman() {
    var expected = """
                  |            | Knoten / Kanten bzw.\s
        Dateiname | beschränkt | Pfadlänge:Pfad; m, m'
        ----------|------------|----------------------
        """;
    var s = new ResultsFormatter(Locale.GERMANY).format(emptyResultsList).toString();
    assertEquals(expected, s);
    s = new ResultsFormatter().format(Locale.GERMANY, emptyResultsList).toString();
    assertEquals(expected, s);
  }

  @Test
  void hasCorrectOutput() {
    var s = new ResultsFormatter().format(Locale.US, results).toString();
    // formatter outputs trailing whitespace
    var expected = """
                   |         | Nodes / Edges resp.\s\s\s\s\s\s\s
        Filename   | bounded | Pathlength:Path; m, m'\s\s\s\s
        -----------|---------|---------------------------
        firstName  | yes     | 100 / 1\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s
        secondName | no      |   1:(t1); (1|2|3), (1|3|3)
        """;
    System.out.println(expected);
    System.out.println(s);
    assertEquals(expected, s);
  }

}
