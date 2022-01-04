package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An abstract base class for all nodes of a {@code PetriNet}.
 * 
 * @author j-hap 
 *
 */
public abstract class SimplePetriNode extends SimplePetriElement implements PetriNode {
  private double xpos;
  private double ypos;
  private String name;
  private final NodeType type;
  /**
   * utility class to emit property change events.
   */
  protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  /**
   * Constructs a {@code SimplePetriNode} of the given {@code NodeType}.
   * 
   * @param id   ID of the constructed node.
   * @param type {@code NodeType} of the constructed node.
   */
  public SimplePetriNode(String id, NodeType type) {
    super(id);
    this.type = type;
  }

  /**
   * Stores x/y coordinates to be used when visualizing this
   * {@code SimplePetriNode}.
   * 
   * @param x New x-coordinate of the {@code SimplePetriNode}.
   * @param y New y-coordinate of the {@code SimplePetriNode}.
   */
  public void setPosition(double x, double y) {
    // old value 0 because changes in the view aren't propagated, so the event
    // has to be forced;
    xpos = x;
    ypos = y;
    pcs.firePropertyChange("XPos", 0, x);
    pcs.firePropertyChange("YPos", 0, y);
  }

  /**
   * Renames the {@code SimplePetriNode} and notifies any listeners about the
   * change.
   * 
   * @param newName The new Name of the {@code SimplePetriNode}.
   */
  public void setName(String newName) {
    var oldName = name;
    var oldLabel = getLabel();
    name = newName;
    pcs.firePropertyChange("Label", oldLabel, getLabel());
    pcs.firePropertyChange("Name", oldName, name);
  }

  /**
   * @return The name of this {@code SimplePetriNode}.
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPlace() {
    return type == NodeType.PLACE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTransition() {
    return type == NodeType.TRANSITION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NodeType getType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getXPos() {
    return xpos;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getYPos() {
    return ypos;
  }

  /**
   * {@inheritDoc} The name is appended to the ID.
   */
  @Override
  public String getLabel() {
    return super.getLabel() + " " + name;
  }

  /**
   * @param listener The {@code PropertyChangeListener} to add.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  /**
   * @param listener The {@code PropertyChangeListener} to remove.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }
}
