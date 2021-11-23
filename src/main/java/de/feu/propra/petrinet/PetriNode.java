package de.feu.propra.petrinet;

public interface PetriNode extends PetriElement {

	int getXPos();

	int getYPos();

	NodeType getType();

	boolean isPlace();

	boolean isTransition();
}
