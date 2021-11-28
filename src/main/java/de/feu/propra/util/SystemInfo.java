package de.feu.propra.util;

/**
 * Utility class for some information about the machine and environment running
 * this class' methods
 * 
 * @author j-hap 
 *
 */
public class SystemInfo {
  /**
   * @return The java version of the JVM that runs the application.
   */
  public static String getJavaVersion() {
    return System.getProperty("java.version");
  }

  /**
   * @return A formatted string to display the java version of the JVM that runs
   *         the application.
   */
  public static String getJavaVersionInfoString() {
    return getInfoString("Java Version", getJavaVersion());
  }

  /**
   * Formats a label / value String pair for aligned display.
   * 
   * @param label Label String (left of the colon)
   * @param value Value String (right of the colon)
   * @return
   */
  private static String getInfoString(String label, String value) {
    String fmt = "%-28s:%s";
    return String.format(fmt, label, value);
  }

  /**
   * @return The current working the directory of the JVM. Does not change over
   *         runtime.
   */
  public static String getCurrentWorkingDirectory() {
    return System.getProperty("user.dir");
  }

  /**
   * @return A formatted string to display the working directory of the JVM that
   *         runs the application.
   */
  public static String getCurrentWorkingDirectoryInfoString() {
    return getInfoString("Current Working Directory", getCurrentWorkingDirectory());
  }
}
