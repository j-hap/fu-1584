package de.feu.propra.petrinet;

public class IllegalConnectionException extends Exception {	
	private static final long serialVersionUID = 1L;
	public IllegalConnectionException() {
		super("Cannot create Arc, source and target must be different node types");
	}
}
