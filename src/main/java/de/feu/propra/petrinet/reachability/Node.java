package de.feu.propra.petrinet.reachability;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Node<T> {
  private Set<Edge<Node<T>>> inEdges = new HashSet<>();
  private Set<Edge<Node<T>>> outEdges = new HashSet<>();
  public final T value;

  public Node(T v) {
    value = v;
  }

  public void addOutEdge(Edge<Node<T>> e) {
    outEdges.add(e);
  }

  public Set<Node<T>> getPrecedessors() {
    return inEdges.stream().map(e -> e.getTarget()).collect(Collectors.toSet());
  }

  public int getSuccessorCount() {
    return outEdges.size();
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Node<?>))
      return false;
    Node<?> other = (Node<?>) obj;
    return Objects.equals(value, other.value);
  }

  public void addInEdge(Edge<Node<T>> e) {
    inEdges.add(e);
  }

  public boolean hasOutEdge(Edge<Node<T>> e) {
    return outEdges.contains(e);
  }
}
