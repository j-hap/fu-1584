package de.feu.propra.petrinet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockListener implements PropertyChangeListener {
  public boolean wasCalled = false;

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    wasCalled = true;
  }
}

class PlaceTest {
  Place place;
  MockListener listener;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    place = new Place("0");
    listener = new MockListener();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void constructsEmptyPlace() {
    assertEquals(0, place.getTokenCount());
  }

  @Test
  void tellsIfItHasTokens() {
    assertEquals(false, place.hasTokens());
    place.addToken();
    assertEquals(true, place.hasTokens());
  }

  @Test
  void doesNotAllowNegativeTokenCount() {
    assertThrows(IllegalStateException.class, () -> {
      place.removeToken();
    });
  }
  
  @Test
  void doesNotAllowInitialNegativeTokenCount() {
    assertThrows(IllegalStateException.class, () -> {
      place.setInitialTokenCount(-1);
    });
  }

  @Test
  void doesNotOverflow() {
    place.setTokenCount(Integer.MAX_VALUE);
    assertThrows(ArithmeticException.class, () -> {
      place.addToken();
    });
  }

  @Test
  void addTokenAddsOneToken() {
    place.addToken();
    assertEquals(1, place.getTokenCount());
  }

  @Test
  void removeTokenRemovesOneToken() {
    place.addToken();
    place.removeToken();
    assertEquals(0, place.getTokenCount());
  }

  @Test
  void changeOfInitialTokensAlsoSetsCurrentTokens() {
    place.setInitialTokenCount(10);
    assertEquals(10, place.getTokenCount());
  }

  @Test
  void firesPropertyChangeOnTokenAdd() {
    place.addPropertyChangeListener(listener);
    place.addToken();
    assertTrue(listener.wasCalled);
  }

  @Test
  void firesPropertyChangeOnTokenRemove() {
    place.addToken();
    place.addPropertyChangeListener(listener);
    place.addToken();
    assertTrue(listener.wasCalled);
  }

  @Test
  void firesPropertyChangeOnInitialTokenSet() {
    place.addPropertyChangeListener(listener);
    place.setInitialTokenCount(1);
    assertTrue(listener.wasCalled);
  }

  @Test
  void firesPropertyChangeOnTokenSet() {
    place.addPropertyChangeListener(listener);
    place.setTokenCount(1);
    assertTrue(listener.wasCalled);
  }

  @Test
  void returnsExpectedLabel() {
    place.setName("BLA");
    place.setTokenCount(5);
    assertEquals("[0] BLA <5>", place.getLabel());
  }

  @Test
  void constructorThrowsOnIdNull() {
    assertThrows(NullPointerException.class, () -> {
      new Place(null);
    });
  }
}
