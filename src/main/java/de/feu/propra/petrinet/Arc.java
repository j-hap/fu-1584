package de.feu.propra.petrinet;

public class Arc extends SimplePetriElement {
  public final PetriNode source;
  public final PetriNode target;

  public Arc(String id, PetriNode s, PetriNode t) {
    super(id);
    source = s;
    target = t;
  }

  public String getSourceId() {
    return source.getId();
  }

  public String getTargetId() {
    return target.getId();
  }
}
