package de.feu.propra.controller;

import java.io.File;
import java.util.EventObject;

/**
 * A semantic event which indicates a change of the current file in a multi
 * document interface.
 * <p>
 * The object that implements the {@code ActiveFileChangeListener} interface
 * gets this {@code ActiveFileChangeEvent} when the event occurs. The listener
 * need not care about the source of this event, but now knows that the current
 * file changed.
 *
 * @see ActiveFileChangeListener
 *
 * @author j-hap 
 */
public class ActiveFileChangeEvent extends EventObject {
  private static final long serialVersionUID = 1L;
  private File newFile;

  /**
   * Constructs an Event that contains the new active file.
   * 
   * @param source The source of this event
   * @param file   The new active File
   */
  public ActiveFileChangeEvent(Object source, File file) {
    super(source);
    newFile = file;
  }

  /**
   * Get the {@code newFile}.
   * 
   * @return The new active file.
   */
  public File getFile() {
    return newFile;
  }
}
