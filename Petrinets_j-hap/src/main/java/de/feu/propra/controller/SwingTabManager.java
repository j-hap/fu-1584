package de.feu.propra.controller;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.ui.view.View;

import de.feu.propra.ui.MainViewAction;
import de.feu.propra.ui.SwingTab;
import de.feu.propra.util.UserLogHandler;

/**
 * Controller for a tabbed multi-document interface. Handles and propagates tab
 * changes.
 * 
 * @author j-hap 
 *
 */
public class SwingTabManager implements TabManager {
  private JTabbedPane tabContainer = new JTabbedPane(JTabbedPane.BOTTOM);
  private ArrayList<ActiveFileChangeListener> listeners = new ArrayList<>();
  // I'm too lazy to implement a full blown HashBiMap
  private Map<JComponent, String> tabToFileMap = new HashMap<>();
  private Map<String, JComponent> fileToTabMap = new HashMap<>();
  private Map<JComponent, Handler> logHandlers = new HashMap<>();
  private SwingTab lastActiveTab = null;

  /**
   * Constructor for the Manager. Creates the Tab Pane and creates a default tab.
   */
  public SwingTabManager() {
    createTabPane();
    addDefaultTab();
  }

  /**
   * @return The JComponent that containes all the tabs. Intended to be used to
   *         place it in a parent UI component.
   */
  public JComponent getTabContainer() {
    return tabContainer;
  }

  private void createTabPane() {
    tabContainer = new JTabbedPane(JTabbedPane.BOTTOM);
    tabContainer.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    tabContainer.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        var evt = new ActiveFileChangeEvent(this, getActiveFile());
        listeners.forEach(l -> l.fileChanged(evt));
        swapLogHandler();
        lastActiveTab = (SwingTab) tabContainer.getSelectedComponent();
      }
    });
  }

  private void swapLogHandler() {
    var oldHandler = logHandlers.get(lastActiveTab);
    Logger.getLogger("").removeHandler(oldHandler);
    var currentTab = tabContainer.getSelectedComponent();
    if (currentTab != null) {
      var newHandler = logHandlers.get(currentTab);
      Logger.getLogger("").addHandler(newHandler);
    }
  }

  private boolean onlyDefaulTabIsOpen() {
    return lastActiveTab != null && lastActiveTab.getName() == "" && fileToTabMap.size() == 1;
  }

  private void addDefaultTab() {
    addTab(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SwingTab addTab(File file) {
    boolean removeDefaultTab = onlyDefaulTabIsOpen() && lastActiveTab.logIsEmpty();
    if (removeDefaultTab) {
      closeTab(fileToTabMap.get(""));
    }
    var tab = new SwingTab();
    // uses filename as key, because the hashCode of a File includes
    // file modification time
    String filename = "";
    String tabName = "";
    if (file != null) {
      filename = file.getAbsolutePath();
      tabName = file.getName();
    }
    tab.setName(tabName);

    // creates a logger for this tab
    var logHandler = new UserLogHandler(tab.getLogPane());
    logHandlers.put(tab, logHandler);

    var tabComponent = new TabWithXButton(tabName, tabContainer, MainViewAction.CLOSE_FILE.action);

    tabToFileMap.put(tab, filename);
    fileToTabMap.put(filename, tab);

    tabContainer.add(tab);
    var iTab = tabContainer.getTabCount() - 1;
    tabContainer.setTabComponentAt(iTab, tabComponent);

    // swing does not automatically switch to new tabs
    tabContainer.setSelectedIndex(iTab);
    lastActiveTab = tab;

    return tab;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SwingTab addTab(File file, View leftView, View rightView) {
    var tab = addTab(file);
    tab.setNetView(leftView);
    tab.setGraphView(rightView);
    return tab;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void closeCurrentTab() {
    var tab = tabContainer.getSelectedComponent();
    closeTab(tab);
    // at least one tab is needed
    if (tabContainer.getTabCount() == 0) {
      addDefaultTab();
    }
  }

  private void closeTab(Component tab) {
    var fileToClose = tabToFileMap.get(tab);
    tabContainer.remove(tab);
    tabToFileMap.remove(tab);
    logHandlers.remove(tab);
    fileToTabMap.remove(fileToClose);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void switchToTab(File file) throws FileNotFoundException {
    var comp = fileToTabMap.get(file.getAbsolutePath());
    if (comp == null) {
      throw new FileNotFoundException();
    }
    tabContainer.setSelectedComponent(comp);
  }

  /**
   * Determines if the {@code TabManager} contains a tab assiciated to the given
   * {@code File}.
   * 
   * @param file The {@code File} whose tab is of interest
   * @return true if there is a tab associated with that {@code File}. False
   *         otherwise.
   */
  public boolean hasTab(File file) {
    return fileToTabMap.containsKey(file.getAbsolutePath());
  }

  private File getActiveFile() {
    // all other classes shall ask the FileSelector
    var comp = tabContainer.getSelectedComponent();
    var filename = tabToFileMap.get(comp);
    if (filename == null || filename.equals("")) {
      // special case, default tab is selected
      return null;
    } else {
      return new File(filename);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addActiveFileChangeListener(ActiveFileChangeListener l) {
    listeners.add(l);
  }
}
