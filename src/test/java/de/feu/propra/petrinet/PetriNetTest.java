package de.feu.propra.petrinet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.feu.propra.controller.ReachabilityGraphChangeListener;
import de.feu.propra.reachability.Marking;
import de.feu.propra.util.DuplicateElementException;

class PetriNetTest {
  PetriNet net;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    net = new PetriNet();
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
  void throwsOnWrongMarkingLength() {
    assertThrows(IllegalArgumentException.class, () -> {
      net.setMarking(new Marking(1));
    });
  }

  @Test
  void notifiesReachabilityGraphOnTransition() {
    var mockGraphListener = new ReachabilityGraphChangeListener() {
      public boolean edgeVisitedWasCalled = false;
      public boolean edgeAddedWasCalled = false;
      public boolean activeMarkingChangedWasCalled = false;

      @Override
      public void reachabilityGraphInitialized(Marking initialMarking) {
      }

      @Override
      public void edgeVisited(Marking oldMarking, String edgeId) {
        edgeVisitedWasCalled = true;
      }

      @Override
      public void edgeAdded(String id, String label, Marking source, Marking target) {
        edgeAddedWasCalled = true;
      }

      @Override
      public void activeMarkingChanged(Marking newActiveMarking) {
        activeMarkingChangedWasCalled = true;
      }
    };

    net.addTransition("t1");
    net.getReachabilityGraph().addChangeListener(mockGraphListener);
    net.triggerTransition("t1");

    assertTrue(mockGraphListener.edgeAddedWasCalled);
    assertTrue(mockGraphListener.edgeVisitedWasCalled);
    assertTrue(mockGraphListener.activeMarkingChangedWasCalled);
  }

  @Test
  void notifiesReachabilityGraphOnMarkingSet() {
    var mockGraphListener = new ReachabilityGraphChangeListener() {
      public boolean edgeVisitedWasCalled = false;
      public boolean edgeAddedWasCalled = false;
      public boolean activeMarkingChangedWasCalled = false;

      @Override
      public void reachabilityGraphInitialized(Marking initialMarking) {
      }

      @Override
      public void edgeVisited(Marking oldMarking, String edgeId) {
        edgeVisitedWasCalled = true;
      }

      @Override
      public void edgeAdded(String id, String label, Marking source, Marking target) {
        edgeAddedWasCalled = true;
      }

      @Override
      public void activeMarkingChanged(Marking newActiveMarking) {
        activeMarkingChangedWasCalled = true;
      }
    };

    net.addPlace("p1");
    net.addTransition("t1");
    net.addArc("a1", "t1", "p1");
    net.triggerTransition("t1");
    net.resetPlaces();

    net.getReachabilityGraph().addChangeListener(mockGraphListener);
    net.setMarking(new Marking(1));

    assertFalse(mockGraphListener.edgeAddedWasCalled); // no new edge was added
    assertFalse(mockGraphListener.edgeVisitedWasCalled); // no edge was visited
    assertTrue(mockGraphListener.activeMarkingChangedWasCalled);
  }

  @Test
  void notifiesReachabilityGraphMarkingChangeViaExistingTransition() {
    var mockGraphListener = new ReachabilityGraphChangeListener() {
      public boolean edgeVisitedWasCalled = false;
      public boolean edgeAddedWasCalled = false;
      public boolean activeMarkingChangedWasCalled = false;

      @Override
      public void reachabilityGraphInitialized(Marking initialMarking) {
      }

      @Override
      public void edgeVisited(Marking oldMarking, String edgeId) {
        edgeVisitedWasCalled = true;
      }

      @Override
      public void edgeAdded(String id, String label, Marking source, Marking target) {
        edgeAddedWasCalled = true;
      }

      @Override
      public void activeMarkingChanged(Marking newActiveMarking) {
        activeMarkingChangedWasCalled = true;
      }
    };

    net.addPlace("p1");
    net.addTransition("t1");
    net.addArc("a1", "t1", "p1");
    net.triggerTransition("t1");
    net.resetPlaces();

    net.getReachabilityGraph().addChangeListener(mockGraphListener);
    net.triggerTransition("t1");

    assertFalse(mockGraphListener.edgeAddedWasCalled); // no new edge was added
    assertTrue(mockGraphListener.edgeVisitedWasCalled); // existing edge was visited
    assertTrue(mockGraphListener.activeMarkingChangedWasCalled);
  }
}
