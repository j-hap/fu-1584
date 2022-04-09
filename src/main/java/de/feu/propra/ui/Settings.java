package de.feu.propra.ui;

import java.awt.Frame;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import de.feu.propra.util.ResourceFinder;

/**
 * Utility class that manages application settings.
 * 
 * @author j-hap 
 *
 */
public class Settings {
  private static Preferences prefs = Preferences.userRoot().node("fernuni").node("propra").node("hapke");
  private static SettingsDialog settingsDialog;

  // prevents instances
  private Settings() {
  }

  /**
   * Displays the settings dialog to modify available application settings.
   * 
   * @param parent The parent {@code Frame} over which the dialog shall be
   *               displayed.
   */
  public static void showDialog(Frame parent) {
    if (settingsDialog == null) {
      settingsDialog = new SettingsDialog(null);
    }
    settingsDialog.setVisible(true);
  }

  /**
   * Can be used to get the available translations for this application.
   * 
   * @return A list of available {@code Locale}s for which translation resources
   *         are defined.
   */
  public static Locale[] getAvailableLanguages() {
    var files = ResourceFinder.getResourcesBelow("langs");
    var pattern = Pattern.compile("_[a-z]{2}(_[A-Z]{2})?\\.");
    // LinkedHashSet to alphabetic sorting
    Set<Locale> langs = new LinkedHashSet<Locale>();
    for (var f : files) {
      var matcher = pattern.matcher(f);
      if (matcher.find()) {
        // omits leading underscore, that is matched by regex
        var match = matcher.group();
        match = match.substring(1, match.length() - 1);
        var parts = match.split("_");
        if (parts.length == 2) {
          langs.add(new Locale(parts[0], parts[1]));
        } else {
          langs.add(new Locale(match));
        }
      }
    }
    return langs.toArray(Locale[]::new);
  }

  /**
   * Determines the available layout algorithms for GraphStream views.
   * 
   * @return A list of available GraphStream graph layouts.
   */
  public static String[] getReachabilityGraphLayoutModeOptions() {
    return new String[] { "Default", "Hierarchy" };
  }

  /**
   * If no locale setting is present, the method tries to configure the default
   * locale. If a resource bundle tries to use that locale and there are not
   * property files, the fallback language is en_US.
   * 
   * @return The currently defined locale
   */
  public static Locale getLocale() {
    var defaultLocale = Locale.getDefault();
    var localeString = prefs.get("Language", defaultLocale.toLanguageTag());
    return Locale.forLanguageTag(localeString);
  }

  /**
   * Changes the display language of the application. Requires restart to take
   * effect on GUI elements.
   * 
   * @param newLang The new display language.
   */
  static void setLocale(Locale newLang) {
    prefs.put("Language", newLang.toLanguageTag());
  }

  /**
   * Determines of the continuous boundedness check is active
   * 
   * @return The current state of the continuous boundedness check.
   */
  public static boolean isContinouusBoundednessCheckActive() {
    return prefs.getBoolean("ContinuousBoundednessCheck", true);
  }

  /**
   * Disables / enables the continuous boundedness check.
   * 
   * @param status The new state of the continuous boundedness check.
   */
  public static void setContinouusBoundednessCheckActive(boolean status) {
    prefs.putBoolean("ContinuousBoundednessCheck", status);
  }

  /**
   * @return The current graph layout mode.
   */
  public static String getReachabilityGraphLayoutMode() {
    return prefs.get("LayoutMode", "Hierarchy");
  }

  /**
   * Changes the used layout mode for the displayed {@code ReachabilityGraph}.
   * 
   * @param mode The new graph layout mode.
   */
  static void setReachabilityGraphLayoutMode(String mode) {
    prefs.put("LayoutMode", mode);
  }

}
