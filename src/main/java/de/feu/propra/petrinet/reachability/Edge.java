package de.feu.propra.petrinet.reachability;

import java.util.Objects;

public class Edge<T> {
  private final String label;
  private final T target;

  public Edge(String l, T t) {
    label = l;
    target = t;
  }

  public String getLabel() {
    return label;
  }

  public T getTarget() {
    return target;
  }

  public String toString() {
    return label + target.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, target);
  }

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