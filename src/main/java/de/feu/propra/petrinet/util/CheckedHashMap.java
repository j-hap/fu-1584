package de.feu.propra.petrinet.util;

import java.util.HashMap;

/**
 * A {@link HashMap} that throws an exception when trying put a key that already
 * exists.
 * 
 * @author j-hap 
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class CheckedHashMap<K, V> extends HashMap<K, V> {
  private static final long serialVersionUID = 1L;

  /**
   * Associates the specified value with the specified key in this map. If the map
   * previously contained a mapping for the key, it throws a
   * {@code DuplicateElementException}.
   *
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   *
   * @return the value associated with {@code key}.
   * @throws ClassCastException        if the specified key cannot be compared
   *                                   with the keys currently in the map
   * @throws NullPointerException      if the specified key is null and this map
   *                                   uses natural ordering, or its comparator
   *                                   does not permit null keys
   * @throws DuplicateElementException if the specified key is already contained
   *                                   in the map
   */
  @Override
  public V put(K k, V v) {
    if (containsKey(k)) {
      throw new DuplicateElementException("Element already in container.");
    }
    return super.put(k, v);
  }
}
