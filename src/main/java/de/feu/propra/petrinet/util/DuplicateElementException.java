package de.feu.propra.petrinet.util;

/**
 * Exception to be thrown when trying to add an Element into a container that
 * may only contain unique elements.
 * 
 * @author j-hap 
 */
public class DuplicateElementException extends IllegalArgumentException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message The detail message. The detail message is saved for later
   *                retrieval by the {@link #getMessage()} method.
   */
  public DuplicateElementException(String message) {
    super(message);
  }

}
