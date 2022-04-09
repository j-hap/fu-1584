package de.feu.propra.controller;

import java.util.EventListener;

/**
 * The listener interface for receiving {@link ActiveFileChangeEvent}s The class
 * that is interested in processing an {@link ActiveFileChangeEvent} implements
 * this interface, and the object created with that class is registered with a
 * component, using the component's {@code addActiveFileChangeListener} method.
 * When the action event occurs, that object's {@code fileChanged} method is
 * invoked.
 *
 * @see ActiveFileChangeEvent
 *
 * @author j-hap 
 */
public interface ActiveFileChangeListener extends EventListener {
  /**
   * Invoked when an active file changes.
   * 
   * @param e the event to be processed
   */
  void fileChanged(ActiveFileChangeEvent e);
}
