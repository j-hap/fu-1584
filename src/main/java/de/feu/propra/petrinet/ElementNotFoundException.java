package de.feu.propra.petrinet;

/**
 * Exception to be thrown when a searched object is not found.
 * 
 * @author j-hap 
 *
 */
public class ElementNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ElementNotFoundException(String message) {
    super(message);
  }
}
