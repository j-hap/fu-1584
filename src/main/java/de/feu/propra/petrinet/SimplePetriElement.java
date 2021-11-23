package de.feu.propra.petrinet;

import java.util.Objects;

public abstract class SimplePetriElement implements PetriElement {
  private final String id;

  public SimplePetriElement(String id) {
    Objects.requireNonNull(id, "ID must not be null!");
    this.id = id;
  }
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getLabel() {
    return "[" + getId() + "]";
  }
}
