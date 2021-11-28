package de.feu.propra.ui;

import javax.swing.ImageIcon;

/**
 * A enumeration of icons to be used on buttons and menu actions. Each
 * enumeration object contains a small {@code ImageIcon} for a menu action and a
 * larger {@code ImageIcon} for button actions.
 * 
 * @author j-hap 
 *
 */
public enum UiIcon {
  /**
   * Opened folder icon.
   */
  OPEN_FILE("/icons/icons8-opened-folder-24.png", "/icons/icons8-opened-folder-12.png"), //
  /**
   * Gear Wheels icon so symbolize an automated batch process.
   */
  BATCH("/icons/icons8-services-24.png", "/icons/icons8-services-12.png"), //
  /**
   * Left arrow icon.
   */
  PREVIOUS_FILE("/icons/icons8-arrow-pointing-left-24.png", "/icons/icons8-arrow-pointing-left-12.png"), //
  /**
   * Right arrow icon.
   */
  NEXT_FILE("/icons/icons8-arrow-24.png", "/icons/icons8-arrow-12.png"), //
  /**
   * Circle arrow icon.
   */
  RELOAD_FILE("/icons/icons8-restart-24.png", "/icons/icons8-restart-12.png"), //
  /**
   * Circled minus icon.
   */
  MINUS("/icons/icons8-minus-24.png", "/icons/icons8-minus-12.png"), //
  /**
   * Circled plus icon.
   */
  PLUS("/icons/icons8-plus-24.png", "/icons/icons8-plus-12.png"), //
  /**
   * Red cross icon.
   */
  DELETE("/icons/icons8-delete-24.png", "/icons/icons8-delete-12.png"), //
  /**
   * Left arrow with a stop bar icon.
   */
  RESET("/icons/icons8-skip-to-start-24.png", "/icons/icons8-skip-to-start-12.png"), //
  /**
   * Four arrows pointing to the corners of an invisible square.
   */
  FIT("/icons/icons8-fit-to-width-24.png", "/icons/icons8-fit-to-width-12.png"), //
  /**
   * Three dots with a checkmark icon.
   */
  VALIDATION("/icons/icons8-validation-24.png", "/icons/icons8-validation-12.png"), //
  /**
   * Map pin icon.
   */
  MARK("/icons/icons8-marker-24.png", "/icons/icons8-marker-12.png"), //
  /**
   * Checkmark with two circling arrows.
   */
  CONTINUOUS_CHECK("/icons/icons8-continuous-check-24.png", null);

  /**
   * The larger {@code ImageIcon} for buttons.
   */
  public final ImageIcon button;
  /**
   * The smaller {@code ImageIcon} for menus.
   */
  public final ImageIcon menu;

  /**
   * Util function to create an {@code ImageIcon} from a resource path string.
   * 
   * @param path Resource path to load.
   * @return An ImageIcon created from the resource path. null if the resource
   *         could not be loaded.
   */
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