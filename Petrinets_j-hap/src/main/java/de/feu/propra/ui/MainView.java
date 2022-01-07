package de.feu.propra.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * The Main Window of the application as composition of Swing components.
 * 
 * @author j-hap 
 */
public class MainView {
  private JFrame frame;
  private InfoDialog infoDialog;
  private StatusBar statusBar;
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.commands_and_tooltips",
      Settings.getLocale());

  /**
   * Creates the {@code MainView} with the specified title. By default its
   * position is centered on the screen and occupies 2 3rds of the screensize
   * 
   * @param title The title of the main window frame.
   */
  public MainView(String title) {
    frame = new JFrame(title);
    frame.setIconImage(getImage("/icons/icons8-blockchain-technology-24.png"));
    // close on click on X
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setDefaultSize();
    centerOnScreen();
    createComponents();
  }

  private static Image getImage(String path) {
    var resource = MainView.class.getResource(path);
    try {
      return ImageIO.read(resource);
    } catch (IOException e) {
      return null;
    }
  }

  private void setDefaultSize() {
    // robust for multiple monitor environment
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    var w = gd.getDisplayMode().getWidth();
    var h = gd.getDisplayMode().getHeight();
    frame.setSize(2 * w / 3, 2 * h / 3);
  }

  private void centerOnScreen() {
    frame.setLocationRelativeTo(null);
  }

  private void createComponents() {
    createMenuBar();
    createToolBar();
    createStatusBar();
  }

  private void createMenuBar() {
    var menuBar = new JMenuBar();
    var fileMenu = new JMenu(bundle.getString("menu.file"));
    fileMenu.add(MainViewAction.OPEN_FILE.action);
    fileMenu.add(MainViewAction.PREVIOUS_FILE.action);
    fileMenu.add(MainViewAction.NEXT_FILE.action);
    fileMenu.add(MainViewAction.RELOAD_FILE.action);
    var closeMenuItem = fileMenu.add(MainViewAction.CLOSE_FILE.action);
    var keyStrokeToClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
    closeMenuItem.setAccelerator(keyStrokeToClose);

    closeMenuItem.setMnemonic(KeyEvent.VK_W);
    fileMenu.addSeparator();
    fileMenu.add(MainViewAction.BATCH.action);
    fileMenu.addSeparator();
    var exitItem = new JMenuItem(bundle.getString("command.exit"));
    // emulates click on [X] in title bar
    var exitEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
    exitItem.addActionListener((ActionEvent e) -> frame.dispatchEvent(exitEvent));
    fileMenu.add(exitItem);

    var toolsMenu = new JMenu(bundle.getString("menu.tools"));
    toolsMenu.add(MainViewAction.REMOVE_TOKEN.action);
    toolsMenu.add(MainViewAction.ADD_TOKEN.action);
    toolsMenu.add(MainViewAction.FREEZE_TOKENS.action);
    toolsMenu.add(MainViewAction.RESET_NET.action);
    toolsMenu.add(MainViewAction.DELETE_GRAPH.action);
    toolsMenu.add(MainViewAction.BOUNDS_CHECK.action);
    toolsMenu.addSeparator();
    var prefItem = new JMenuItem(bundle.getString("menu.preferences"));
    prefItem.addActionListener(e -> Settings.showDialog(frame));
    toolsMenu.add(prefItem);

    var helpMenu = new JMenu(bundle.getString("menu.help"));
    var infoItem = new JMenuItem(bundle.getString("menu.info"));
    infoItem.addActionListener((ActionEvent e) -> showInfoDialog());
    helpMenu.add(infoItem);

    menuBar.add(fileMenu);
    menuBar.add(toolsMenu);
    menuBar.add(helpMenu);

    frame.setJMenuBar(menuBar);
  }

  private void createToolBar() {
    var toolBar = new JToolBar();
    toolBar.setFloatable(false);

    toolBar.add(MainViewAction.OPEN_FILE.action);
    toolBar.add(MainViewAction.PREVIOUS_FILE.action);
    toolBar.add(MainViewAction.NEXT_FILE.action);
    toolBar.add(MainViewAction.RELOAD_FILE.action);
    toolBar.addSeparator();

    toolBar.add(MainViewAction.FIT_TO_VIEW.action);
    toolBar.add(MainViewAction.REMOVE_TOKEN.action);
    toolBar.add(MainViewAction.FREEZE_TOKENS.action);
    toolBar.add(MainViewAction.ADD_TOKEN.action);
    toolBar.add(MainViewAction.RESET_NET.action);

    toolBar.addSeparator();

    toolBar.add(MainViewAction.DELETE_GRAPH.action);
    toolBar.add(MainViewAction.BOUNDS_CHECK.action);

    frame.add(toolBar, BorderLayout.PAGE_START);
  }

  private void createStatusBar() {
    statusBar = new StatusBar();
    frame.add(statusBar.getPanel(), BorderLayout.PAGE_END);
  }

  private void showInfoDialog() {
    if (infoDialog == null) {
      // we could also use JOptionPane.showMessageDialog, but that would reconstruct
      // the pane and the dialog every time. instead we create the modal dialog once
      // and show/hide it when necessary
      infoDialog = new InfoDialog(frame);
    }
    infoDialog.setVisible(true);
  }

  /**
   * Sets the message displayed in the status bar.
   * 
   * @param msg The message to be shown.
   */
  public void setStatusMessage(String msg) {
    statusBar.setMessage(msg);
  }

  /**
   * If the modified marker is true, the statusbar contains a specific postfix.
   * Used to tell the user, that the state of the current file differs from the
   * state on disk.
   * 
   * @param status The modification status of the current file.
   */
  public void setModifiedMarker(boolean status) {
    statusBar.setModifiedMarker(status);
  }

  /**
   * Displays a popup message over the main window.
   * 
   * @param message The message to be shown in a popup.
   */
  public void showPopup(String message) {
    JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Changes curser to the OS default wait cursor.
   */
  public void showWaitCursor() {
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  /**
   * Resets curser to the OS default cursor.
   */
  public void resetCursor() {
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Changes visibility state of the main window frame.
   * 
   * @param state New visibility state.s
   */
  public void setVisible(boolean state) {
    frame.setVisible(state);
  }

  /**
   * Embeds the given {@code JComponent} as the center pane of the main window
   * frame.
   * 
   * @param component The {@code JComponent} to embed. Replaces any previously
   *                  embedded component.
   */
  public void setCenterPane(JComponent component) {
    frame.add(component, BorderLayout.CENTER);
  }
}
