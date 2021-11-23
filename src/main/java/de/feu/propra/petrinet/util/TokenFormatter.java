package de.feu.propra.petrinet.util;

public class TokenFormatter {
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
