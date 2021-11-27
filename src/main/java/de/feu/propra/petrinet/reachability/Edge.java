package de.feu.propra.petrinet.reachability;

import java.util.Objects;

/**
 * A graph edge with a generic target and a label.
 * 
 * @author j-hap 
 *
 * @param <T> The type of the target of the edge.
 */
public class Edge<T> {
  private final String label;
  private final T target;

  /**
   * Constructs an {@code Edge} with the given label and target element.
   * 
   * @param label  The label of the constructed {@code Edge}.
   * @param target The target element of the constructed {@code Edge}.
   */
  public Edge(String label, T target) {
    this.label = label;
    this.target = target;
  }

  /**
   * @return The label of this {@code Edge}.
   */
  public String getLabel() {
    return label;
  }

  /**
   * @return The target element of this {@code Edge}.
   */
  public T getTarget() {
    return target;
  }

  /**
   * Two edges with the same label and the same target have the same hash code.
   * 
   * @return The hashCode as int.
   */
  @Override
  public int hashCode() {
    return Objects.hash(label, target);
  }

  /**
   * Two edges with the same label and the same target are equal.
   * 
   * @return True if the two objects are equal.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Edge))
      return false;
    var other = (Edge<?>) obj;
    return Objects.equals(label, other.label) && Objects.equals(target, other.target);
  }
}