package de.feu.propra.controller;

import java.io.File;
import java.io.FileNotFoundException;

import org.graphstream.ui.view.View;

import de.feu.propra.ui.SwingTab;

/**
 * Controller interface for multi-document UIs. This interface shall be
 * implemented by a controller class for a specific UI framework.
 * 
 * @author j-hap 
 *
 */
public interface TabManager {

  /**
   * Adds a new Tab with a log pane but without any {@code View}s. If a default
   * tab is open but does not contain any information, it is replaced by the new
   * tab.
   * 
   * @param file      The file to which the new tab is associated
   * @return The created {@code SwingTab}
   */
  SwingTab addTab(File file);

  /**
   * Adds a new Tab with a log pane, which displays the two given {@code View}s.
   * If a default tab is open but does not contain any information, it is replaced
   * by the new tab.
   * 
   * @param file      The file to which the new tab is associated
   * @param leftView  The top left {@code View} to display.
   * @param rightView the top right {@code View} to display.
   * @return The created {@code SwingTab}
   */
  SwingTab addTab(File file, View leftView, View rightView);

  /**
   * Deletes the current Tab and the associated objects. If the last tab is
   * closed, a default tab is created.
   */
  void closeCurrentTab();

  /**
   * Switches to the tab assiciated to the given file. Throws
   * FileNotFoundException when the TabManager has no tab for the given
   * {@code File}.
   * 
   * @param file The {@code File}, whose a associated tab shall be shown.
   * @throws FileNotFoundException If the tab, associated with {@code File} does
   *                               not exist.
   */
  void switchToTab(File file) throws FileNotFoundException;

  /**
   * Adds a listener, that is notified when the active tab and therefore active
   * file changes.
   * 
   * @param listener The {@code ActiveFileChangeListener} that reacts to change of
   *                 the active file.
   */
  void addActiveFileChangeListener(ActiveFileChangeListener listener);
}
