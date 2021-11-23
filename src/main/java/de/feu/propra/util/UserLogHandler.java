package de.feu.propra.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * A {@code UserLogHandler} object takes log messages from a {@code Logger} and
 * exports them to a {@link JEditorPane}. By default it writes a color coded
 * message of severity >= {@link Level#INFO} to its output pane. You may change
 * the colors with {@link UserLogHandler#setLevelColor}. The default colors are
 * listed {@link UserLogHandler#restoreDefaultColors}
 */

public class UserLogHandler extends Handler {
  private JEditorPane outputPane;
  private static final Formatter formatter = new UserLogFormatter();
  private static final Map<Level, Color> levelColors = new HashMap<>();

  /**
   * Constructor. The resulting {@code Handler} has a log level of
   * {@code Level.WARNING}, a {@code UserLogFormatter}, and no {@code Filter}.
   * 
   * @param outPane the output pane to display log messages
   */
  public UserLogHandler(JEditorPane outPane) {
    setOutPane(outPane);
    setLevel(Level.WARNING);
    setFormatter(formatter);
    restoreDefaultColors();
  }

  /**
   * Redirect Handler output to a different {@link JEditorPane}
   * 
   * @param outPane Output JEditorPane to which this handler writes
   */
  public void setOutPane(JEditorPane outPane) {
    outputPane = outPane;
  }

  /**
   * Restore the default colors of this handler.
   * <p>
   * The default colors are
   * <ul>
   * <li>{@link Level#SEVERE}: {@link Color#BLACK}
   * <li>{@link Level#WARNING}: Orange
   * <li>{@link Level#INFO}: {@link Color#BLACK}
   * <li>{@link Level#CONFIG}: {@link Color#BLACK}
   * <li>{@link Level#FINE}: {@link Color#DARK_GRAY}
   * <li>{@link Level#FINER}: {@link Color#GRAY}
   * <li>{@link Level#FINEST}: {@link Color#LIGHT_GRAY}
   * <li>{@link Level#ALL}: {@link Color#BLACK}
   * </ul>
   */
  public void restoreDefaultColors() {
    setLevelColor(Level.SEVERE, Color.RED);
    setLevelColor(Level.WARNING, new Color(255, 204, 0));
    setLevelColor(Level.INFO, Color.BLACK);
    setLevelColor(Level.CONFIG, Color.BLACK);
    setLevelColor(Level.FINE, Color.DARK_GRAY);
    setLevelColor(Level.FINER, Color.GRAY);
    setLevelColor(Level.FINEST, Color.LIGHT_GRAY);
    setLevelColor(Level.ALL, Color.BLACK);
  }

  /**
   * Set Color for given Log Level.
   * 
   * @param level Level which color shall be modified
   * @param color new Color of given level
   */
  public void setLevelColor(Level level, Color color) {
    levelColors.put(level, color);
  }

  /**
   * Format and publish a {@code LogRecord}.
   * <p>
   * The {@code UserLogHandler} checks if the given {@code LogRecord} has at least
   * the required log level. If not it silently returns. Otherwise it calls its
   * {@code Formatter} to format the record and then writes it color coded the
   * result to the current output pane.
   *
   * @param record description of the log event. A null record is silently ignored
   *               and is not published
   */
  @Override
  public void publish(LogRecord record) {
    if (!isLoggable(record)) {
      return;
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(attributes, levelColors.get(record.getLevel()));
        var s = getFormatter().format(record);
        var doc = outputPane.getDocument();
        try {
          doc.insertString(doc.getLength(), s, attributes);
        } catch (BadLocationException e) {
          // if the logger fails, who notifies the user...
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Not implemented for this handler, because no buffering is happening.
   */
  @Override
  public void flush() {
  }

  /**
   * Disables all logging on this handler.
   */
  @Override
  public void close() throws SecurityException {
    setLevel(Level.OFF);
  }
}
