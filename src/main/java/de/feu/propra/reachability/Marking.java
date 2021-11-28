package de.feu.propra.reachability;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A wrapper class for an int array that represents the token count of a number
 * of {@code Place} objects.
 * 
 * @author j-hap 
 *
 */
public class Marking implements Comparable<Marking> {
  /**
   * The token count at each {@code Place}, sorted alphabetically by the
   * {@code Place}s ID.
   */
  protected final List<Integer> nTokens;

  /**
   * Copy constructor.
   * 
   * @param marking The base {@code Marking}.
   */
  protected Marking(Marking marking) {
    nTokens = marking.nTokens;
  }

  /**
   * Constructs a {@code Marking} with the given token count array.
   * 
   * @param tokenArray An array of integers that repsresents the token count of a
   *                   set of {@code Place}s.
   */
  public Marking(Integer... tokenArray) {
    // List.of returns unmodifiable list, so a Marking is final after creation
    nTokens = List.of(tokenArray);
  }

  /**
   * Two {@code Marking}s with the same token count yield the same hash code.
   * 
   * @return The hashCode as int.
   */
  @Override
  public int hashCode() {
    return Objects.hash(nTokens);
  }

  /**
   * Two {@code Marking}s with the same token count are equal.
   * 
   * @return True if the two objects are equal.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Marking))
      return false;
    Marking other = (Marking) obj;
    return Objects.equals(nTokens, other.nTokens);
  }

  /**
   * Implements the m &lt;-&gt; m' comparison of two {@code Marking}s. A {@code Marking}
   * is &gt; than another if at least one token count if bigger that another and all
   * others are equal. Does not check for equal size of the underlying token count
   * array and might throw an IndexOutOfBoundsException when used with wrong
   * inputs. This cannot happen when all {@code Markings} come from the same
   * {@code PetriNet}
   * 
   * @return 0 if the {@code Marking}s are equal, 1 if this {@code Marking} has at
   *         least one token count if bigger that another and all others are equal
   *         or -1 if at least one token count is smaller that the other.
   * @see ReachabilityGraph#isBounded()
   */
  @Override
  public int compareTo(Marking other) {
    if (this.equals(other)) {
      return 0;
    }
    for (int i = 0; i < nTokens.size(); ++i) {
      if (this.nTokens.get(i) < other.nTokens.get(i)) {
        return -1;
      }
    }
    return 1;
  }

  /**
   * String representation of the {@code Marking} to be used then printing.
   */
  public String toString() {
    var s = nTokens.stream().map(String::valueOf).collect(Collectors.joining("|"));
    return "(" + s + ")";
  }

  /**
   * Returns the token count at the ith position.
   * 
   * @param i Index to query.
   * @return Token count at the ith position.
   */
  public int getTokenCount(int i) {
    return nTokens.get(i);
  }
}