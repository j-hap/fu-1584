package de.feu.propra.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graphstream.stream.SourceBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

/**
 * Layout for hierarchical graphs with a single root node.
 * 
 * @author j-hap 
 *
 */
public class HierarchyLayout extends SourceBase implements Layout {
  private Map<String, Integer> nodeLevel = new HashMap<>();
  private List<Set<String>> levelList = new ArrayList<>();
  private boolean needPositionUpdate = false;
  private static final int levelDistance = -30;
  private static final int nodeDistance = 50;

  /**
   * {@inheritDoc}
   */
  @Override
  public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue, Object newValue) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
      Object newValue) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
      Object newValue) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
    // not needed in this application
  }

  /**
   * Adds a new node into the level model on the default level. The default level
   * is 0 for the first node and -1 for any other. The level is reset when an
   * incoming edge is added. {@inheritDoc}
   */
  @Override
  public void nodeAdded(String sourceId, long timeId, String nodeId) {
    int level = -1;
    if (nodeLevel.isEmpty()) {
      // special treatment of root node
      level = 0;
      levelList.add(new LinkedHashSet<>());
      levelList.get(level).add(nodeId);
      needPositionUpdate = true;
    }
    nodeLevel.put(nodeId, level);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nodeRemoved(String sourceId, long timeId, String nodeId) {
    // never happens in this application
  }

  /**
   * A new edge was added into the graph. The target node is moved to a level
   * closer the root node if possible. {@inheritDoc}
   */
  @Override
  public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
      boolean directed) {
    // target node already exists
    var currentLevel = nodeLevel.get(toNodeId);
    var levelFromEdge = nodeLevel.get(fromNodeId) + 1;
    if (currentLevel == -1 || levelFromEdge < currentLevel) {
      // node is either not leveled yet or moves closer to the root
      if (currentLevel > 0) {
        levelList.get(currentLevel).remove(toNodeId);
      }
      if (levelList.size() <= levelFromEdge) {
        // inserts a new level if necessary
        levelList.add(new HashSet<String>());
      }
      levelList.get(levelFromEdge).add(toNodeId);
      nodeLevel.put(toNodeId, levelFromEdge);
    }
    needPositionUpdate = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void edgeRemoved(String sourceId, long timeId, String edgeId) {
    // never happens in this application
  }

  /**
   * The level hierarchy gets cleared. {@inheritDoc}
   */
  @Override
  public void graphCleared(String sourceId, long timeId) {
    clear();
    sendGraphCleared(sourceId, timeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stepBegins(String sourceId, long timeId, double step) {
    // not needed in this application
  }

  // --------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLayoutAlgorithmName() {
    return "Hierarchy";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNodeMovedCount() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getStabilization() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getStabilizationLimit() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Point3 getLowPoint() {
    // not needed in this application
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Point3 getHiPoint() {
    // not needed in this application
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSteps() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getLastStepTime() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getQuality() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getForce() {
    // not needed in this application
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    nodeLevel.clear();
    levelList.clear();
    needPositionUpdate = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setForce(double value) {
    // non iterative, so not necessary
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStabilizationLimit(double value) {
    // non iterative, so not necessary
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setQuality(double qualityLevel) {
    // always the best quality :)
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSendNodeInfos(boolean send) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shake() {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void moveNode(String id, double x, double y, double z) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void freezeNode(String id, boolean frozen) {
    // not needed in this application
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void compute() {
    if (needPositionUpdate) {
      updatePositions();
      needPositionUpdate = false;
    }
  }

  private void updatePositions() {
    for (var nodeId : nodeLevel.keySet()) {
      var newY = (double) (nodeLevel.get(nodeId) * levelDistance);
      sendNodeAttributeChanged(sourceId, nodeId, "y", null, newY);
    }
    for (var group : levelList) {
      var nNodes = group.size();
      var newX = (double) -nodeDistance * (double) nNodes / 2;
      for (var nodeId : group) {
        sendNodeAttributeChanged(sourceId, nodeId, "x", null, newX);
        newX += nodeDistance;
      }
    }
  }
}
