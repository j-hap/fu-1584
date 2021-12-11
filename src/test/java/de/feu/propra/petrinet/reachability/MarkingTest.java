package de.feu.propra.petrinet.reachability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.feu.propra.reachability.Marking;

class MarkingTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testToStringEmptyMarking() {
    assertEquals("()", new Marking().toString());
  }

  @Test
  void testToStringSingleEntry() {
    assertEquals("(1)", new Marking(1).toString());
  }

  @Test
  void testToStringTwoEntries() {
    assertEquals("(1|2)", new Marking(1, 2).toString());
  }

  @Test
  void testToStringNegativeEntries() {
    assertEquals("(-1|-2)", new Marking(-1, -2).toString());
  }

  @Test
  void sameTokenCountMeansEqual() {
    assertTrue(new Marking().equals(new Marking()));
    assertTrue(new Marking(1).equals(new Marking(1)));
  }

  @Test
  void sameTokenCountYieldsSameHash() {
    assertTrue(new Marking().hashCode() == new Marking().hashCode());
    assertTrue(new Marking(1).hashCode() == new Marking(1).hashCode());
  }

}
