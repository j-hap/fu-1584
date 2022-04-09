package de.feu.propra.solver;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import de.feu.propra.petrinet.PetriNet;
import de.feu.propra.reachability.Marking;
import de.feu.propra.reachability.ReachabilityGraph;

/**
 * The {@code BoundednessSolver} is a utility class to run a boundedness check
 * on an existing PetriNet.
 * 
 * @author j-hap 
 *
 */
public class BoundednessSolver {
  private PetriNet net;
  private ReachabilityGraph rGraph;
  private Set<Marking> visitedMarkings = new HashSet<>();;
  private BoundednessSolverResult result;
  private Stack<String> currentPath = new Stack<>();

  /**
   * Creates a {@code BoundednessSolver}, that operates on the given model.
   * 
   * @param petriNet The {@code PetriNet} model on which the solver operates.
   */
  public BoundednessSolver(PetriNet petriNet) {
    net = petriNet;
    rGraph = net.getReachabilityGraph();
    net.resetPlaces();
    rGraph.init();
    result = new BoundednessSolverResult();
  }

  /**
   * Runs the Boundedness check on the associated {@code PetriNet} model.
   * 
   * @return The {@code BoundednessSolverResult} object with the solver results.
   */
  public BoundednessSolverResult solve() {
    result.isBounded = solveRec();
    if (!result.isBounded) {
      result.problemPath = currentPath;
      result.problemMarkings = rGraph.getUnboundedMarkings();
    }
    result.nodeCount = rGraph.getNodeCount();
    result.edgeCount = rGraph.getEdgeCount();
    return result;
  }

  // recursive depth first search
  private boolean solveRec() {
    var oldMarking = net.getMarking();
    if (visitedMarkings.contains(oldMarking)) {
      return true;
    }
    visitedMarkings.add(oldMarking);
    for (var transitionId : net.getActiveTransitionIds()) {
      net.triggerTransition(transitionId);
      currentPath.add(transitionId);
      if (!(net.isBounded() && solveRec())) {
        return false;
      }
      net.setMarking(oldMarking);
      currentPath.pop();
    }
    return true;
  }
}
