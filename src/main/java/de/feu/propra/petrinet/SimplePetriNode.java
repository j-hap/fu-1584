package de.feu.propra.petrinet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class SimplePetriNode extends SimplePetriElement implements PetriNode {
	private int xpos;
	private int ypos;
	String name;
	private final NodeType type;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void setPosition(int x, int y) {
		xpos = x;
		ypos = y;
	}

	public void setName(String n) {
		var oldName = name;
		var oldLabel = getLabel();
		name = n;
		pcs.firePropertyChange("Label", oldLabel, getLabel());
		pcs.firePropertyChange("Name", oldName, name);
	}
	
	public String getName() {
	  return name;
	}

	public SimplePetriNode(String id, NodeType t) {
		super(id);
		type = t;
	}

	@Override
	public boolean isPlace() {
		return type == NodeType.PLACE;
	}

	@Override
	public boolean isTransition() {
		return type == NodeType.TRANSITION;
	}

	@Override
	public NodeType getType() {
		return type;
	}

	@Override
	public int getXPos() {
		return xpos;
	}

	@Override
	public int getYPos() {
		return ypos;
	}

	@Override
	public String getLabel() {
		return super.getLabel() + " " + name;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	 public void removePropertyChangeListener(PropertyChangeListener listener) {
	    pcs.removePropertyChangeListener(listener);
	  }
}
