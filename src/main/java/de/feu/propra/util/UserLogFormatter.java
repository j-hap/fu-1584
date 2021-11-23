package de.feu.propra.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Print a only the message of the {@code LogRecord}.
 *
 * <p>
 * The {@code UserLogFormatter} can be used in a context where only the log
 * message text is of interest, e.g. messages to the user. It is recommended to
 * color code the log level, so the severety of the log message is deducable.
 *
 * @see java.util.Formatter
 */

public class UserLogFormatter extends Formatter {

  /**
   * Format the given LogRecord.
   * <p>
   * This method is even simpler than {@link SimpleFormatter#format} since there
   * is no configuration possible. Also no additional information except the log
   * message is returned. It is localized by the {@link Formatter#formatMessage}
   * convenience method.
   * 
   * @param record the log record to be formatted.
   * @return a formatted log record
   */
  @Override
  public String format(LogRecord record) {
    return formatMessage(record) + "\n";
  }
}
