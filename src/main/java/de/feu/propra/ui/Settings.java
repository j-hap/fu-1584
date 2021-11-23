package de.feu.propra.ui;

import java.awt.Frame;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class Settings {
  private static Preferences prefs = Preferences.userRoot().node("fernuni").node("propra").node("hapke");
  private static SettingsDialog settingsDialog;

  // prevents instances
  private Settings() {
  }

  public static void showDialog(Frame parent) {
    if (settingsDialog == null) {
      settingsDialog = new SettingsDialog(null);
    }
    settingsDialog.setVisible(true);
  }

  public static Locale[] getAvailableLanguages() {
    var resource = Settings.class.getResource("/langs/");
    var files = new File(resource.getPath()).list();
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

  public static String[] getReachabilityGraphLayoutModeOptions() {
    return new String[] { "Default", "Hierarchy" };
  }

  public static Locale getLocale() {
    var localeString = prefs.get("Language", Locale.getDefault().toLanguageTag());
    return Locale.forLanguageTag(localeString);
  }

  static void setLocale(Locale newLang) {
    prefs.put("Language", newLang.toLanguageTag());
  }

  public static boolean isContinouusBoundednessCheckActive() {
    return prefs.getBoolean("ContinuousBoundednessCheck", true);
  }

  public static void setContinouusBoundednessCheckActive(boolean status) {
    prefs.putBoolean("ContinuousBoundednessCheck", status);
  }

  public static String getReachabilityGraphLayoutMode() {
    return prefs.get("LayoutMode", "Hierarchy");
  }

  static void setReachabilityGraphLayoutMode(String mode) {
    prefs.put("LayoutMode", mode);
  }

}
