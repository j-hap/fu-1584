package de.feu.propra.controller;

import java.io.File;
import java.io.FileNotFoundException;

import org.graphstream.ui.view.View;

/**
 * Controller interface for multi-document UIs. This interface shall be
 * implemented by a controller class for a specific UI framework.
 * 
 * @author j-hap 
 *
 */
public interface TabManager {

  /**
   * Adds a new Tab to display the given two {@code View}s. And an assiciated log
   * pane.
   * 
   * @param file The file to which the new tab is associated
   * @param leftView The top left {@code View} to display.
   * @param rightView the top right {@code View} to display.
   */
  void addTab(File file, View leftView, View rightView);

  /**
   * Deletes the current Tab and the associated objects.
   */
  void closeCurrentTab();

  /**
   * Switches to the tab assiciated to the given file. Throws
   * FileNotFoundException when the TabManager has no tab for the given
   * {@code File}.
   * 
   * @param file
   * @throws FileNotFoundException
   */
  void switchToTab(File file) throws FileNotFoundException;

  /**
   * Adds a listener, that is notified when the active tab and therefore active
   * file changes.
   * 
   * @param listener
   */
  void addActiveFileChangeListener(ActiveFileChangeListener listener);
}
