package de.feu.propra.util;

import java.util.TreeMap;

/**
 * A {@link TreeMap} that throws an exception when trying put a key that already
 * exists.
 * 
 * @author j-hap 
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class CheckedTreeMap<K, V> extends TreeMap<K, V> {
  private static final long serialVersionUID = 1L;

  /**
   * Associates the specified value with the specified key in this map. If the map
   * previously contained a mapping for the key, it throws a
   * {@code DuplicateElementException}.
   *
   * @param k Key with which the specified value is to be associated.
   * @param v Value to be associated with the specified key.
   *
   * @return The value associated with {@code key}.
   * @throws ClassCastException        If the specified key cannot be compared
   *                                   with the keys currently in the map.
   * @throws NullPointerException      If the specified key is null and this map
   *                                   uses natural ordering, or its comparator
   *                                   does not permit null keys.
   * @throws DuplicateElementException If the specified key is already contained
   *                                   in the map.
   */
  @Override
  public V put(K k, V v) {
    if (containsKey(k)) {
      throw new DuplicateElementException("Element already in container.");
    }
    return super.put(k, v);
  }
}
