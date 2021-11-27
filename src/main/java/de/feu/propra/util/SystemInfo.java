package de.feu.propra.util;

/**
 * Utility class for some information about the machine and environment running
 * this class' methods
 * 
 * @author j-hap 
 *
 */
public class SystemInfo {
  public static String getJavaVersion() {
    return System.getProperty("java.version");
  }

  public static String getJavaVersionInfoString() {
    return getInfoString("Java Version:", getJavaVersion());
  }

  private static String getInfoString(String label, String value) {
    String fmt = "%-28s%s";
    return String.format(fmt, label, value);
  }

  public static String getCurrentWorkingDirectory() {
    return System.getProperty("user.dir");
  }

  public static String getCurrentWorkingDirectoryInfoString() {
    return getInfoString("Current Working Directory:", getCurrentWorkingDirectory());
  }
}
