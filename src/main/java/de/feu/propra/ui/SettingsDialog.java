package de.feu.propra.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class SettingsDialog implements ActionListener, ItemListener {
  private JDialog dialog;
  private JComboBox<Locale> langBox;
  private JCheckBox boundednessCheck;
  private JButton applyButton;
  private String selectedLayout = Settings.getReachabilityGraphLayoutMode();
  private static final ResourceBundle bundle = ResourceBundle.getBundle("langs.labels", Settings.getLocale());

  public SettingsDialog(Frame parent) {
    dialog = new JDialog(parent, bundle.getString("Settings"), false);

    var mainPane = Box.createVerticalBox();
    mainPane.add(createLanguageOption());
    mainPane.add(createBoundednessCheckOption());
    mainPane.add(createLayoutOption());

    var okButton = new JButton(bundle.getString("OK"));
    var cancelButton = new JButton(bundle.getString("Cancel"));
    applyButton = new JButton(bundle.getString("Apply"));
    var buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(okButton);
    buttonBox.add(cancelButton);
    buttonBox.add(applyButton);
    buttonBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

    var listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        var src = e.getSource();
        if (src == okButton || src == applyButton) {
          apply();
        }
        if (src == okButton || src == cancelButton) {
          var exitEvent = new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING);
          dialog.dispatchEvent(exitEvent);
        }
      }
    };

    okButton.addActionListener(listener);
    cancelButton.addActionListener(listener);
    applyButton.addActionListener(listener);
    mainPane.add(buttonBox);

    dialog.setContentPane(mainPane);
    dialog.pack();
    dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(parent);
  }

  private Container createLanguageOption() {    
    
    
    var langs = Settings.getAvailableLanguages();    
    langBox = new JComboBox<Locale>(langs);
    langBox.setSelectedItem(Settings.getLocale());
    langBox.addActionListener(this);
    langBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    
    var container = Box.createHorizontalBox();
    container.add(langBox);
    container.add(Box.createHorizontalGlue());
    var warning = new JLabel( "(" + bundle.getString("requires_restart") + ")");
    warning.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    container.add(warning);
    container.setBorder(BorderFactory.createTitledBorder(bundle.getString("Language")));
    container.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    return container;
  }

  private Container createBoundednessCheckOption() {
    boundednessCheck = new JCheckBox(bundle.getString("continuous"));
    boundednessCheck.setSelected(Settings.isContinouusBoundednessCheckActive());
    boundednessCheck.addActionListener(this);
    boundednessCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    var container = Box.createHorizontalBox();
    container.setBorder(BorderFactory.createTitledBorder(bundle.getString("boundedness_check")));
    container.add(boundednessCheck);
    container.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    container.add(Box.createHorizontalGlue());
    return container;
  }

  private Container createLayoutOption() {
    var options = Settings.getReachabilityGraphLayoutModeOptions();
    var buttons = new HashMap<String, Component>();
    var layoutRadioGroup = new ButtonGroup();
    var currentOption = Settings.getReachabilityGraphLayoutMode();
    var vBox = Box.createVerticalBox();
    for (var o : options) {
      var b = new JRadioButton(o);
      b.setName(o);
      buttons.put(o, b);
      layoutRadioGroup.add(b);      
      if (o.equals(currentOption)) {
        b.setSelected(true);
      }      
      b.addItemListener(this);
      vBox.add(b);
    }    
    vBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    var container = Box.createHorizontalBox();
    container.add(vBox);
    container.add(Box.createHorizontalGlue());
    container.setBorder(BorderFactory.createTitledBorder(bundle.getString("rgraph_layout")));
    container.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    return container;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    applyButton.setEnabled(true);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    applyButton.setEnabled(true);
    selectedLayout = ((JRadioButton)e.getSource()).getName();
  }

  public void setVisible(boolean status) {
    applyButton.setEnabled(false);
    dialog.setVisible(status);
  }

  private void apply() {
    Settings.setLocale((Locale) langBox.getSelectedItem());
    Settings.setContinouusBoundednessCheckActive(boundednessCheck.isSelected());
    Settings.setReachabilityGraphLayoutMode(selectedLayout);
  }
}
