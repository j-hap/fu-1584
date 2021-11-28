package de.feu.propra.util;

/**
 * Base Interface for PNML Parser implementations
 * 
 * @author j-hap 
 *
 */
public interface PnmlParser {

  /**
   * Loads the file, that was specified in the class constructor.
   */
  void loadFile();

  /**
   * Loads the file, but does not create any components. Intended to be used when
   * the file was loaded once and shall be reset to the properties defined in the
   * file.
   */
  void reloadFile();
}
