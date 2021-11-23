package de.feu.propra.petrinet.solver;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import de.feu.propra.petrinet.PetriNet;
import de.feu.propra.petrinet.reachability.Marking;
import de.feu.propra.petrinet.reachability.ReachabilityGraph;

public class BoundednessSolver {
  private PetriNet petriNet;
  private ReachabilityGraph reachabilityModel;
  private Set<Marking> visitedMarkings = new HashSet<>();;
  private BoundednessSolverResult result;
  private Stack<String> currentPath = new Stack<>();

  public BoundednessSolver(PetriNet n, ReachabilityGraph g) {
    petriNet = n;
    reachabilityModel = g;
    petriNet.resetPlaces();
    g.init();
    result = new BoundednessSolverResult();
    petriNet = n;
    reachabilityModel = g;
  }

  public void solve() {
    result.isBounded = solveRec();
    if (!result.isBounded) {
      result.problemPath = currentPath;
      result.problemMarkings = reachabilityModel.getUnboundedMarkings();
    }
    result.nodeCount = reachabilityModel.getNodeCount();
    result.edgeCount = reachabilityModel.getEdgeCount();    
  }

  // recursive depth first search
  private boolean solveRec() {
    var oldMarking = petriNet.getMarking();
    if (visitedMarkings.contains(oldMarking)) {
      return true;
    }
    visitedMarkings.add(oldMarking);
    for (var transitionId : petriNet.getActiveTransitionIds()) {
      petriNet.triggerTransition(transitionId);
      currentPath.add(transitionId);
      if (!(petriNet.isBounded() && solveRec())) {
        return false;
      }
      petriNet.setMarking(oldMarking);
      currentPath.pop();
    }
    return true;
  }

  public BoundednessSolverResult getResult() {
    return result;
  }
}
