package de.feu.propra.petrinet;

/**
 * Interface for a node of a {@code PetriNet} with read-only methods.
 * 
 * @author j-hap 
 *
 */
public interface PetriNode extends PetriElement {

  /**
   * @return The x-coordinate of the {@code PetriNode}
   */
  double getXPos();

  /**
   * @return The y-coordinate of the {@code PetriNode}
   */
  double getYPos();

  /**
   * @return The type of the {@code PetriNode}
   */
  NodeType getType();

  /**
   * @return True if the {@code PetriNode} is a {@code Place} False otherwise.
   */
  boolean isPlace();

  /**
   * @return True if the {@code PetriNode} is a {@code Transition} False
   *         otherwise.
   */
  boolean isTransition();
}
