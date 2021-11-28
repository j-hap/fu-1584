package de.feu.propra.util;

/**
 * Formatter for a {@code Place}s token count.
 * 
 * @author j-hap 
 *
 */
public class TokenFormatter {
  /**
   * Formats the given token count into a String determined by the range in which
   * the number lies.
   * 
   * @param nTokens The number to format.
   * @return "" for input 0, ">9" for inputs >9 or the String equivalent of the
   *         given integer.
   */
  public static String format(int nTokens) {
    if (nTokens == 0) {
      return "";
    } else if (nTokens > 9) {
      return ">9";
    } else {
      return String.valueOf(nTokens);
    }
  }
}
