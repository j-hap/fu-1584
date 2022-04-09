package de.feu.propra.petrinet;

/**
 * Exception to be thrown when a searched object is not found.
 * 
 * @author j-hap 
 *
 */
public class ElementNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message The detail message. The detail message is saved for later
   *                retrieval by the {@link #getMessage()} method.
   */
  public ElementNotFoundException(String message) {
    super(message);
  }
}
