package de.feu.propra.ui;

import javax.swing.ImageIcon;

public enum UiIcon {
  OPEN_FILE("/icons/icons8-opened-folder-24.png", "/icons/icons8-opened-folder-12.png"), //
  BATCH("/icons/icons8-services-24.png", "/icons/icons8-services-12.png"), //
  PREVIOUS_FILE("/icons/icons8-arrow-pointing-left-24.png", "/icons/icons8-arrow-pointing-left-12.png"), //
  NEXT_FILE("/icons/icons8-arrow-24.png", "/icons/icons8-arrow-12.png"), //
  RELOAD_FILE("/icons/icons8-restart-24.png", "/icons/icons8-restart-12.png"), //
  MINUS("/icons/icons8-minus-24.png", "/icons/icons8-minus-12.png"), //
//  PLUS("/icons/icons8-minus-24.png", "/icons/icons8-minus-12.png"), //
  PLUS("/icons/icons8-plus-24.png", "/icons/icons8-plus-12.png"), //
  DELETE("/icons/icons8-delete-24.png", "/icons/icons8-delete-12.png"), //
  RESET("/icons/icons8-skip-to-start-24.png", "/icons/icons8-skip-to-start-12.png"), //
  FIT("/icons/icons8-fit-to-width-24.png", "/icons/icons8-fit-to-width-12.png"), //
  VALIDATION("/icons/icons8-validation-24.png", "/icons/icons8-validation-12.png"), //
  MARK("/icons/icons8-marker-24.png", "/icons/icons8-marker-12.png"), //
  CONTINUOUS_CHECK("/icons/icons8-continuous-check-24.png", null);

  public final ImageIcon button;
  public final ImageIcon menu;

  public static ImageIcon iconFromFile(String path) {    
    var resource = UiIcon.class.getResource(path);
    if (resource == null) {
      return null;
    }
    return new ImageIcon(resource, "");
  }

  private UiIcon(String largeIconPath, String smallIconPath) {
    if (largeIconPath != null) {
      button = iconFromFile(largeIconPath);
    } else {
      button = null;
    }
    if (smallIconPath != null) {
      menu = iconFromFile(smallIconPath);
    } else {
      menu = null;
    }
  };
}