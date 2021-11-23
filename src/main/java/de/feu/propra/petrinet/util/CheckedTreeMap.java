package de.feu.propra.petrinet.util;

import java.util.TreeMap;

public class CheckedTreeMap<K, V> extends TreeMap<K, V> {
  private static final long serialVersionUID = 1L;

  @Override
  public V put(K k, V v) {
    if (containsKey(k)) {
      throw new DuplicateElementException("Element already in container.");
    }
    return super.put(k, v);
  }
}
