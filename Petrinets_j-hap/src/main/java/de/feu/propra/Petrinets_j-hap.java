package de.feu.propra;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.feu.propra.controller.MainController;
import de.feu.propra.ui.MainView;
import de.feu.propra.ui.MainViewAction;
import de.feu.propra.ui.Settings;
import de.feu.propra.util.SystemInfo;

/**
 * Entry class for PetriCheck
 * 
 * @author j-hap 
 */
public final class Petricheck  {

  /**
   * Helper function to displace some system information via the default logger.
   */
  private static void printSystemProperties() {
    Logger.getLogger("").info(SystemInfo.getCurrentWorkingDirectoryInfoString());
    Logger.getLogger("").info(SystemInfo.getJavaVersionInfoString());
  }

  /**
   * Determines current OS and tries to mimic the look and feel of that.
   * 
   * @see <a href=
   *      "https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html">Tutorial</a>
   */
  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      Logger.getLogger("").warning("Failed to detect System Look and Feel. Falling back to default.");
    }
  }

  /**
   * Initializes the default logger to print all messages to sysout
   */
  private static void setupLogging() {
    var defaultHandlers = Logger.getLogger("").getHandlers();
    for (var h : defaultHandlers) {
      h.setLevel(Level.ALL);      
    }
  }

  /**
   * Initialization for necessary classes
   */
  private static void createAndShowGui() {
    // no net loaded by default, so no graph actions
    MainViewAction.disableGraphActions();
    var mainView = new MainView("j-hap ");
    var mainController = new MainController(mainView);
    MainViewAction.setActionListener(mainController);
    mainView.setVisible(true);
  }

  /**
   * Sets up graphic properties, initializes necessary clases and starts GUI
   * thread.
   * 
   * @param args unused
   */
  public static void main(String[] args) {
    // so loggers also use the correct locale
    Locale.setDefault(Settings.getLocale());
    setupLogging();
    setLookAndFeel();
    printSystemProperties();
    // defines swing viewer as default graph viewer for graphstream library
    System.setProperty("org.graphstream.ui", "swing");
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        // makes sure everything gui is handles by EDT
        createAndShowGui();
      }
    });
  }
}
