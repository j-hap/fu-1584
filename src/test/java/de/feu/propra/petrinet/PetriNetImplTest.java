package de.feu.propra.petrinet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.feu.propra.util.DuplicateElementException;

class PetriNetImplTest {
  PetriNetImpl net;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    net = new PetriNetImpl();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void throwsOnDuplicateId() {
    net.addTransition("id");    
    assertThrows(DuplicateElementException.class, () -> {
      net.addTransition("id");
    });
  }
  
  @Test
  void throwsOnUnknownId() {
    assertThrows(ElementNotFoundException.class, () -> {
      net.setNodeName("id", "");
    });
  }
  
  @Test
  void throwsOnArcBetweenPlaces() {
    net.addPlace("p1");
    net.addPlace("p2");
    assertThrows(IllegalConnectionException.class, () -> {
      net.addArc("arc", "p1", "p2");
    });
  }
  
  @Test
  void throwsOnArcBetweenTransitions() {
    net.addTransition("t1");
    net.addTransition("t2");
    assertThrows(IllegalConnectionException.class, () -> {
      net.addArc("arc", "t1", "t2");
    });
  }
  
  @Test
  void notifiesReachabilityGraphOnTransition() {
    fail("Not yet implemented");
  }
  
  @Test
  void notifiesReachabilityGraphOnMarkingSet() {
    fail("Not yet implemented");
  }
  
  @Test
  void test() {
    fail("Not yet implemented");
  }

}
