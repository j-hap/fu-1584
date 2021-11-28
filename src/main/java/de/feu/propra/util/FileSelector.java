package de.feu.propra.util;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.feu.propra.controller.ActiveFileChangeEvent;
import de.feu.propra.controller.ActiveFileChangeListener;

/**
 * Controller class for all file interactions. Provides utility function for
 * interaction file selection as well as wrap-around methods to get files from
 * the current directory
 * 
 * @author j-hap 
 *
 */
public class FileSelector implements ActiveFileChangeListener {
  private File currentFile;
  private File[] filesInCurrentDir;
  private JFileChooser fileChooser;
  private FileFilter filter = new FileNameExtensionFilter("Petri Net Markup Language (PNML)", "pnml");

  /**
   * Constructor for utility class for file selection interactions.
   * 
   * @param initialDirectory The directory in which the interactive file selection
   *                         dialog is opened on the first call.
   */
  public FileSelector(String initialDirectory) {
    fileChooser = new JFileChooser(initialDirectory);
    fileChooser.setFileFilter(filter);
    fileChooser.setAcceptAllFileFilterUsed(false);
  }

  /**
   * Constructor for utility class for file selection interactions.
   * 
   * @param initialDirectory The directory in which the interactive file selection
   *                         dialog is opened on the first call.
   */
  public FileSelector(File initialDirectory) {
    this(initialDirectory.getAbsolutePath());
  }

  /**
   * Asks user to select a single file.
   * 
   * @return The selected file
   * @throws FileSelectionAbortedException If the user clicks cancel in the
   *                                       {@code JFileChooser}.
   */
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

  /**
   * Asks user to select multiple files.
   * 
   * @return A list of selected files.
   */
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

  /**
   * Starting from the current file steps to the next file in an alphabetically
   * sorted list of files. If these is no current file, it returns the first file.
   * 
   * @return The alphabetically next file in the current directory.
   */
  public File getNext() {
    setFileAtRelativePosAsCurrent(1);
    return getCurrent();
  }

  /**
   * Starting from the current file steps to the previous file in an
   * alphabetically sorted list of files. If these is no current file, it returns
   * the last file.
   * 
   * @return The alphabetically previous file in the current directory.
   */
  public File getPrevious() {
    setFileAtRelativePosAsCurrent(-1);
    return getCurrent();
  }

  /**
   * @return The file currently selected.
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void fileChanged(ActiveFileChangeEvent e) {
    // must tell the file selector the current file, so that the previous and next
    // file buttons work as expected
    currentFile = e.getFile();
  }
}
