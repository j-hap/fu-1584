package de.feu.propra.controller;

import java.awt.Component;
import java.io.File;
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

import de.feu.propra.ui.SwingTab;
import de.feu.propra.util.UserLogHandler;

public class SwingTabManager implements TabManager {
  private JTabbedPane tabContainer = new JTabbedPane(JTabbedPane.BOTTOM);
  private ArrayList<ActiveFileChangeListener> listeners = new ArrayList<>();
  // I'm too lazy to implement a full blown HashBiMap
  private Map<JComponent, String> tabToFileMap = new HashMap<>();
  private Map<String, JComponent> fileToTabMap = new HashMap<>();
  private Map<JComponent, Handler> logHandlers = new HashMap<>();
  private SwingTab lastActiveTab = null;

  public SwingTabManager() {
    createTabPane();
    addDefaultTab();
  }

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

  private void addDefaultTab() {
    addTab(null, null, null);
  }

  private boolean onlyDefaulTabIsOpen() {
    return lastActiveTab != null && lastActiveTab.getName() == "" && fileToTabMap.size() == 1;
  }

  @Override
  public void addTab(File file, View netView, View graphView) {
    boolean removeDefaultTab = onlyDefaulTabIsOpen() && lastActiveTab.logIsEmpty();

    var tab = new SwingTab(netView, graphView);
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
//    var logHandler = new UserLogHandler(tab.getLogArea());
    var logHandler = new UserLogHandler(tab.getLogPane());
    logHandlers.put(tab, logHandler);    

    var tabComponent = new TabWithCloseButton(tabName, tabContainer);

    tabToFileMap.put(tab, filename);
    fileToTabMap.put(filename, tab);

    tabContainer.add(tab);
    var iTab = tabContainer.getTabCount() - 1;
    tabContainer.setTabComponentAt(iTab, tabComponent);
    // swing does not automatically switch to new tabs
    tabContainer.setSelectedIndex(iTab);
    lastActiveTab = tab;

    if (removeDefaultTab) {
      closeTab(fileToTabMap.get(""));
    }
  }

  @Override
  public void closeCurrentTab() {
    var tab = tabContainer.getSelectedComponent();
    closeTab(tab);
  }

  private void closeTab(Component tab) {
    var fileToClose = tabToFileMap.get(tab);
    tabContainer.remove(tab);
    tabToFileMap.remove(tab);
    logHandlers.remove(tab);
    fileToTabMap.remove(fileToClose);
    // at least one tab is needed
    if (tabContainer.getTabCount() == 0) {
      addDefaultTab();
    }
  }

  @Override
  public void switchToTab(File file) {
    tabContainer.setSelectedComponent(fileToTabMap.get(file.getAbsolutePath()));
  }

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

  @Override
  public void addActiveFileChangeListener(ActiveFileChangeListener l) {
    listeners.add(l);
  }
}
