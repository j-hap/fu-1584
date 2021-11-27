package de.feu.propra.petrinet;

/**
 * Base Interface for all Petri Net elements.
 * 
 * @author j-hap 
 *
 */
public interface PetriElement {
  /**
   * @return The ID of the {@code PetriElement}. 
   */
  String getId();

  /**
   * @return The label of the {@code PetriElement}. 
   */  
  String getLabel();
}
