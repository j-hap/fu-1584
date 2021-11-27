package de.feu.propra.util;

/**
 * The class {@code FileSelectionAbortedException} is intended to be used
 * when a file selection by a user is expected, but the user 
 * cancels the selection and there is no default handling in the file selector
 * class possible.
 *
 * @author j-hap 
 */
public class FileSelectionAbortedException extends Exception {
  private static final long serialVersionUID = 1L;
}
