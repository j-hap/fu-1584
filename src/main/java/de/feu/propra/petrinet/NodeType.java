package de.feu.propra.petrinet;

/**
 * Possible Types of {@code PetriNodes} that allow identification of subtype
 * from base type.
 * 
 * @author j-hap 
 *
 */
public enum NodeType {
  /**
   * A Petri Net {@code Place}, that hold tokens.
   */
  PLACE, //
  /**
   * A Petri Net {@code Transition}, that redistributes tokens.
   */
  TRANSITION;
}
