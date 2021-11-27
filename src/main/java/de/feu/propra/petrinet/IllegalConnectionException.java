package de.feu.propra.petrinet;

/**
 * Exception to be thrown when a connection between elements cannot be created.
 * 
 * @author j-hap 
 *
 */
public class IllegalConnectionException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message The detail message. The detail message is saved for later
   *                retrieval by the {@link #getMessage()} method.
   */
  public IllegalConnectionException(String message) {
    super(message);
  }
}
