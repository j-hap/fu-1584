package de.feu.propra.controller;

import java.util.EventListener;

public interface ActiveFileChangeListener extends EventListener {
  void fileChanged(ActiveFileChangeEvent e);
}
