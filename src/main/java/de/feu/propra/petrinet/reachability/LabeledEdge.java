package de.feu.propra.petrinet.reachability;

public class LabeledEdge extends GenericLabeledEdge<Marking, Marking> {

	public LabeledEdge(Marking from, Marking to, String l) {
		super(from, to, l);
	}

}
