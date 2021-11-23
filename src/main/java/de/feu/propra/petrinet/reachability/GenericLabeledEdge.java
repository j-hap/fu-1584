package de.feu.propra.petrinet.reachability;

import java.util.Objects;

public class GenericLabeledEdge<F,T> {
	public final F source;
	public final T target;
	public final String label;

	public GenericLabeledEdge(F from, T to, String l) {
		source = from;
		target = to;
		label = l;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(label, source, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GenericLabeledEdge))
			return false;
		GenericLabeledEdge<?, ?> other = (GenericLabeledEdge<?, ?>) obj;
		return Objects.equals(label, other.label) && Objects.equals(source, other.source)
				&& Objects.equals(target, other.target);
	}

	public String getLabel() {
		return label;
	}

}
