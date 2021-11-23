package de.feu.propra.controller;

import java.io.File;

import org.graphstream.ui.view.View;

public interface TabManager {

  void addTab(File file, View netView, View graphView);

  void closeCurrentTab();

  void switchToTab(File file);

  void addActiveFileChangeListener(ActiveFileChangeListener l);
}
