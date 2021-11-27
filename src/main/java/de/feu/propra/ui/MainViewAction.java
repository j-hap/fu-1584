package de.feu.propra.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * A collection of {@code Action}s to be used in the main window.
 * 
 * @author j-hap 
 */
public enum MainViewAction {
  OPEN_FILE("command.open_file", "tooltip.open_file", UiIcon.OPEN_FILE), //
  PREVIOUS_FILE("command.previous_file", "tooltip.previous_file", UiIcon.PREVIOUS_FILE), //
  NEXT_FILE("command.next_file", "tooltip.next_file", UiIcon.NEXT_FILE), //
  RELOAD_FILE("command.reload_file", "tooltip.reload_file", UiIcon.RELOAD_FILE), //
  CLOSE_FILE("command.close_file", "tooltip.close_file"), //
  BATCH("command.batch", "tooltip.batch"), //
  EXIT("command.exit", "tooltip.exit", null), //
  FIT_TO_VIEW("command.fit_to_view", "tooltip.fit_to_view", UiIcon.FIT), //
  REMOVE_TOKEN("command.remove_token", "tooltip.remove_token", UiIcon.MINUS), //
  FREEZE_TOKENS("command.freeze_tokens", "tooltip.freeze_tokens", UiIcon.MARK), //
  ADD_TOKEN("command.add_token", "tooltip.add_token", UiIcon.PLUS), //
  DELETE_GRAPH("command.delete_graph", "tooltip.delete_graph", UiIcon.DELETE), //
  RESET_NET("command.reset_net", "tooltip.reset_net", UiIcon.RESET), //
  BOUNDS_CHECK("command.bounds_check", "tooltip.bounds_check", UiIcon.VALIDATION);

  public final AbstractAction action;
  private static ActionListener listener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      // does nothing but exists by default
    }
  };

  /**
   * Changes enabled state of the underlying {@code Action} of the enum object.
   * 
   * @param state New enabled state of the {@code Action}.
   */
  public void setEnabled(boolean state) {
    action.setEnabled(state);
  }

  /**
   * Sets the mediator that has to handle all {@code MainViewAction}s.
   * 
   * @param listener
   */
  public static void setActionListener(ActionListener listener) {
    MainViewAction.listener = listener;
  }

  /**
   * @return A subset of the defined {@code MainViewAction} that operate on an
   *         opened {@code PetriNet}.
   */
  private static EnumSet<MainViewAction> petriNetActions() {
    return EnumSet.of(RELOAD_FILE, FIT_TO_VIEW, REMOVE_TOKEN, FREEZE_TOKENS, ADD_TOKEN, RESET_NET, BOUNDS_CHECK);
  }

  /**
   * @return A subset of the defined {@code MainViewAction} that operate on a
   *         displayed {@code ReachabilityGraph}.
   */
  private static EnumSet<MainViewAction> reachabilityGraphActions() {
    return EnumSet.of(DELETE_GRAPH);
  }

  private static void setGraphActionsEnabled(boolean state) {
    MainViewAction.petriNetActions().forEach((MainViewAction a) -> a.setEnabled(state));
    MainViewAction.reachabilityGraphActions().forEach((MainViewAction a) -> a.setEnabled(state));
  }

  /**
   * Disables all {@code MainViewAction}s that operate on a displayed graph.
   */
  public static void disableGraphActions() {
    setGraphActionsEnabled(false);
  }

  /**
   * Enables all {@code MainViewAction}s that operate on a displayed graph.
   */
  public static void enableGraphActions() {
    setGraphActionsEnabled(true);
  }

  // for actions without icon
  private MainViewAction(String name, String tooltip) {
    this(name, tooltip, null);
  }

  private MainViewAction(String nameId, String tooltipId, UiIcon icon) {
    var bundle = ResourceBundle.getBundle("langs.commands_and_tooltips", Settings.getLocale());
    action = new AbstractAction(bundle.getString(nameId)) {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        listener.actionPerformed(new ActionEvent(MainViewAction.this, MainViewAction.this.ordinal(), null));
      }
    };
    if (icon != null) {
      action.putValue(Action.SMALL_ICON, icon.menu);
      action.putValue(Action.LARGE_ICON_KEY, icon.button);
    }

    var tooltip = bundle.getString(tooltipId);
    action.putValue(Action.SHORT_DESCRIPTION, tooltip);
  }
}
