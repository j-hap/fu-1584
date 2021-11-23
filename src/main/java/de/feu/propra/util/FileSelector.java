package de.feu.propra.util;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.feu.propra.controller.ActiveFileChangeEvent;
import de.feu.propra.controller.ActiveFileChangeListener;

// controller for all file interactions
public class FileSelector implements ActiveFileChangeListener {
  private File currentFile;
  private File[] filesInCurrentDir;
  private JFileChooser fileChooser;
  private FileFilter filter = new FileNameExtensionFilter("Petri Net Markup Language (PNML)", "pnml");

  public FileSelector(String initialDirectory) {
    fileChooser = new JFileChooser(initialDirectory);
    fileChooser.setFileFilter(filter);
    fileChooser.setAcceptAllFileFilterUsed(false);
  }
  
  public FileSelector(File initialDirectory) {
    this(initialDirectory.getAbsolutePath());
  }

  public File getUserSelection() throws FileSelectionAbortedException {
    fileChooser.setMultiSelectionEnabled(false);
    int answer = fileChooser.showOpenDialog(null);
    if (answer == JFileChooser.APPROVE_OPTION) {
      currentFile = fileChooser.getSelectedFile();
      return currentFile;
    } else {
      throw new FileSelectionAbortedException();
    }
  }

  public File[] getUserSelectionMulti() {
    fileChooser.setMultiSelectionEnabled(true);
    int answer = fileChooser.showOpenDialog(null);
    if (answer == JFileChooser.APPROVE_OPTION) {
      var files = fileChooser.getSelectedFiles();
      // Should work everywhere like on windows
      Arrays.sort(files);
      // at least one file is selected, otherwise the JFileChooser does not allow
      // click on okay
      currentFile = files[0];
      return files;
    } else {
      return new File[] {};
    }
  }

  public File getNext() {
    setFileAtRelativePosAsCurrent(1);
    return getCurrent();
  }

  public File getPrevious() {
    setFileAtRelativePosAsCurrent(-1);
    return getCurrent();
  }

  public File getCurrent() {
    return currentFile;
  }

  private int getCurrentFileIndex() {
    return Arrays.asList(filesInCurrentDir).indexOf(currentFile);
  }

  private void setFileAtRelativePosAsCurrent(int offset) {
    // in case the files in the current directory changed
    updateFileList();
    int iSelected;
    if (currentFile == null) {
      iSelected = (offset + 1) / 2 - 1;
    } else {
      iSelected = (getCurrentFileIndex() + offset) % filesInCurrentDir.length;
    }
    if (iSelected < 0) {
      iSelected += filesInCurrentDir.length;
    }
    currentFile = filesInCurrentDir[iSelected];
  }

  private void updateFileList() {
    filesInCurrentDir = fileChooser.getCurrentDirectory().listFiles(file -> filter.accept(file));
  }

  @Override
  public void fileChanged(ActiveFileChangeEvent e) {
    // must tell the file selector the current file, so that the previous and next
    // file buttons work as expected
    currentFile = e.getFile();
  }
}
