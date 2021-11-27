package de.feu.propra.petrinet;

/**
 * Connects two {@code PetriNode}s. Indented to be used to connect a
 * {@code Place} to a {@code Transition} or vice versa.
 * 
 * @author j-hap 
 *
 */
public class Arc extends SimplePetriElement {
  /**
   * The source node of the {@code Arc}.
   */
  public final PetriNode source;
  /**
   * The target node of the {@code Arc}.
   */
  public final PetriNode target;

  /**
   * Constructs an {@code Arc} between the given {@code PetriNode}s.
   * 
   * @param id         The unique ID of this {@code Arc}
   * @param sourceNode The {@code PetriNode} where the {@code Arc} originates.
   * @param targetNode The {@code PetriNode} where the {@code Arc} ends.
   */
  public Arc(String id, PetriNode sourceNode, PetriNode targetNode) {
    super(id);
    source = sourceNode;
    target = targetNode;
  }

  /**
   * @return The ID of the source {@code PetriNode}.
   */
  public String getSourceId() {
    return source.getId();
  }

  /**
   * @return The ID of the target {@code PetriNode}.
   */
  public String getTargetId() {
    return target.getId();
  }
}
