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

// fallback is default JLabel behavior
// inspired by https://wiki.byte-welt.net/wiki/HyperlinkLabel_-_Hyperlinks_in_GUI-Komponenten
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

  public void setUrl(String url) {
    try {
      setUrl(new URL(url));
    } catch (MalformedURLException e) {
      disableMouseOver();
      setText(url);
    }
  }

  public void setUrl(URL url) {
    this.url = url;
    setText(url2html(url));
    enableMouseOver();
  }

  public HyperlinkLabel(URL url, int horizontalAlignment) {
    super("", horizontalAlignment);
    setUrl(url);
  }

  public HyperlinkLabel(String url, int horizontalAlignment) {
    super("", horizontalAlignment);
    setUrl(url);
  }

  public HyperlinkLabel(String url) {
    this(url, LEADING);
  }

  public HyperlinkLabel(URL url) {
    this(url, LEADING);
  }

  public URL getURL() {
    return url;
  }

  /**
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
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseEntered(MouseEvent e) {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  /**
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseExited(MouseEvent e) {
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

}
