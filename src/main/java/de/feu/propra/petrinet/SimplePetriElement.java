package de.feu.propra.petrinet;

import java.util.Objects;

/**
 * An abstract base class for all elements of a {@code PetriNet}.
 * 
 * @author j-hap 
 *
 */
public abstract class SimplePetriElement implements PetriElement {
  private final String id;

  /**
   * Constructs a {@code SimplePetriElement}.
   * 
   * @param id The ID of this {@code SimplePetriElement}.
   */
  public SimplePetriElement(String id) {
    Objects.requireNonNull(id, "ID must not be null!");
    this.id = id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Constructs a label string and returns it. The label string contains the ID of
   * the element inside []. {@inheritDoc}
   */
  @Override
  public String getLabel() {
    return "[" + getId() + "]";
  }
}
