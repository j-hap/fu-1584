package de.feu.propra.petrinet.reachability;

public class NetUnboundedException extends Exception {
  private static final long serialVersionUID = 1L;
  public Marking problemMarking1;
  public Marking problemMarking2;

  public NetUnboundedException(Marking m1, Marking m2) {
    problemMarking1 = m1;
    problemMarking2 = m2;
  }
}
