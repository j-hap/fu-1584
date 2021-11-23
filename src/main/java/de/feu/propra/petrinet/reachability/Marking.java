package de.feu.propra.petrinet.reachability;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Marking implements Comparable<Marking> {
  protected final List<Integer> nTokens;
  
  protected Marking(Marking m) {
    nTokens = m.nTokens;
  }
  
  public Marking(Integer... t) {
    // List.of returns unmodifiable list, so a Marking is final after creation
    nTokens = List.of(t);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nTokens);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Marking))
      return false;
    Marking other = (Marking) obj;
    return Objects.equals(nTokens, other.nTokens);
  }

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

  public String toString() {
    var s = nTokens.stream().map(String::valueOf).collect(Collectors.joining("|"));
    return "(" + s + ")";
  }

  public int getTokenCount(int i) {
    return nTokens.get(i);
  }
}