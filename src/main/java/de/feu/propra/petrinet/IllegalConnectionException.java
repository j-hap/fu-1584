package de.feu.propra.petrinet;

/**
 * Exception to be thrown when a connection between elements cannot be created.
 * 
 * @author j-hap 
 *
 */
public class IllegalConnectionException extends Exception {
  private static final long serialVersionUID = 1L;

  public IllegalConnectionException(String message) {
    super(message);
  }
}
