package de.feu.propra.petrinet.reachability;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LinkedMarking extends Marking {
  private Set<Edge<LinkedMarking>> inEdges = new HashSet<>();
  private Set<Edge<LinkedMarking>> outEdges = new HashSet<>();

  public LinkedMarking(Integer[] t) {
    super(t);
  }

  public LinkedMarking(Marking m) {
    super(m);
  }

  public void addOutEdge(Edge<LinkedMarking> e) {
    // TODO System.out.println("added " + this.toString() + " -" + e.getLabel() + "-> " + e.getTarget().toString());
    outEdges.add(e);
  }
  
  public void addOutEdge(String id, LinkedMarking target) {
    addOutEdge(new Edge<LinkedMarking>(id, target));
  }

  public void addInEdge(Edge<LinkedMarking> e) {
    inEdges.add(e);
  }

  public void addInEdge(String id, LinkedMarking source) {
    addInEdge(new Edge<LinkedMarking>(id, source));
  }

  public boolean hasOutEdge(Edge<LinkedMarking> e) {
    return outEdges.contains(e);
  }
  
  public boolean hasOutEdge(String id, LinkedMarking source) {
    return hasOutEdge(new Edge<LinkedMarking>(id, source));
  }
  
  public Set<LinkedMarking> getPrecedessors() {
    return inEdges.stream().map(e -> e.getTarget()).collect(Collectors.toSet());
  }

  public int getSuccessorCount() {
    return outEdges.size();
  }
}
