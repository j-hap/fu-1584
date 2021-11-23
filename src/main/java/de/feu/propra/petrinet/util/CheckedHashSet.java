package de.feu.propra.petrinet.util;

import java.util.HashSet;
import java.util.Set;

public class CheckedHashSet<E> extends HashSet<E> implements Set<E> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(E s) {
		if (contains(s)) {
			throw new DuplicateElementException("Element already in container.");
		}
		return super.add(s);		
	}
}
