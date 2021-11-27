package de.feu.propra.ui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JLabel;

/**
 * The {@code HyperlinkLabel} is a {@code JLabel} that properly formats the text
 * as a hyperlink. The label is clickable. A click on the label opens a browser
 * and visits the linked url if possible.
 * 
 * @author j-hap 
 * @see <a href=
 *      "https://wiki.byte-welt.net/wiki/HyperlinkLabel_-_Hyperlinks_in_GUI-Komponenten">Inspiration</a>
 */
public final class HyperlinkLabel extends JLabel implements MouseListener {
  private static final long serialVersionUID = 1L;
  private URL url = null;
  private static final Logger logger = Logger.getLogger(HyperlinkLabel.class.getName());
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  private static String url2html(URL url) {
    // because of html this component unfortunately stretches to the max available
    // width, has to be dealt with in parent container
    return "<html><a href=\"" + url.toString() + "\">" + url.toString() + "</a></html>";
  }

  private void disableMouseOver() {
    removeMouseListener(this);
  }

  private void enableMouseOver() {
    addMouseListener(this);
  }

  /**
   * Sets the clickable URL of this {@code HyperlinkLabel}. If the given URL is
   * not properly formatted, the {@code HyperlinkLabel} behaves just like a
   * {@code JLabel}
   * 
   * @param url The URL as a String to which the {@code HyperlinkLabel} shall
   *            point.
   */
  public void setUrl(String url) {
    try {
      setUrl(new URL(url));
    } catch (MalformedURLException e) {
      disableMouseOver();
      setText(url);
    }
  }

  /**
   * Sets the clickable URL of this {@code HyperlinkLabel}.
   * 
   * @param url The {@code URL} to which the {@code HyperlinkLabel} shall point.
   */
  public void setUrl(URL url) {
    this.url = url;
    setText(url2html(url));
    enableMouseOver();
  }

  /**
   * Constructs a {@code HyperlinkLabel} to the given {@code URL}.
   * 
   * @param url                 The {@code URL} to which the
   *                            {@code HyperlinkLabel} shall point.
   * @param horizontalAlignment One of the following constants defined in
   *                            <code>SwingConstants</code>: <code>LEFT</code>,
   *                            <code>CENTER</code>, <code>RIGHT</code>,
   *                            <code>LEADING</code> or <code>TRAILING</code>.
   */
  public HyperlinkLabel(URL url, int horizontalAlignment) {
    super("", horizontalAlignment);
    setUrl(url);
  }

  /**
   * Constructs a {@code HyperlinkLabel} to the given {@code URL}.
   * 
   * @param url                 The {@code URL} to which the
   *                            {@code HyperlinkLabel} shall point.
   * @param horizontalAlignment One of the following constants defined in
   *                            <code>SwingConstants</code>: <code>LEFT</code>,
   *                            <code>CENTER</code>, <code>RIGHT</code>,
   *                            <code>LEADING</code> or <code>TRAILING</code>.
   */
  public HyperlinkLabel(String url, int horizontalAlignment) {
    super("", horizontalAlignment);
    setUrl(url);
  }

  /**
   * Constructs a {@code HyperlinkLabel} to the given {@code URL}. The label is
   * aligned against the leading edge of its display area, and centered
   * vertically.
   * 
   * @param url The URL (as String) to which the {@code HyperlinkLabel} shall
   *            point.
   */
  public HyperlinkLabel(String url) {
    this(url, LEADING);
  }

  /**
   * Constructs a {@code HyperlinkLabel} to the given {@code URL}. The label is
   * aligned against the leading edge of its display area, and centered
   * vertically.
   * 
   * @param url The {@code URL} to which the {@code HyperlinkLabel} shall point.
   */
  public HyperlinkLabel(URL url) {
    this(url, LEADING);
  }

  /**
   * @return The URL to which the label points.
   */
  public URL getURL() {
    return url;
  }

  /**
   * Opens the Browser and shows the website the URL points to.
   * 
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(url.toURI());
      } catch (URISyntaxException | IOException ex) {
        logger.warning(bundle.getString("url_open_warning"));
      }
    }
  }

  /**
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  @Override
  public void mousePressed(MouseEvent e) {
    // nothing to do
  }

  /**
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    // nothing to do
  }

  /**
   * Changes the Cursor to a hand symbol.
   * 
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseEntered(MouseEvent e) {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  /**
   * Changes the Cursor back to the default cursor.
   * 
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseExited(MouseEvent e) {
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

}
