package de.feu.propra.petrinet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransitionTest {
  Transition transition;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    transition = new Transition("");
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void isActiveByDefault() {
    assertTrue(transition.isActive());
  }

  @Test
  void isNotActiveWithEmptyPredecessor() {
    transition.addPredecessor(new Place(""));
    assertFalse(transition.isActive());
  }

  @Test
  void isActiveWithNonEmptyPredecessor() {
    var p = new Place("");
    p.setInitialTokenCount(1);
    transition.addPredecessor(p);
    assertTrue(transition.isActive());
  }

  @Test
  void givesSingleTokenToSuccessors() {
    var p = new Place("");
    transition.addSuccessor(p);
    transition.trigger();
    assertEquals(p.getTokenCount(), 1);
  }

  @Test
  void doesNothingIfInactive() {
    var pred = new Place("pred");
    var succ = new Place("succ");
    transition.addPredecessor(pred);
    transition.addSuccessor(succ);
    transition.trigger();
    assertEquals(pred.getTokenCount(), 0);
    assertEquals(succ.getTokenCount(), 0);
  }

}
