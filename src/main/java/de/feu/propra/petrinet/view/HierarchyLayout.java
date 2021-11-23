package de.feu.propra.petrinet.view;

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

public class HierarchyLayout extends SourceBase implements Layout {
  private Map<String, Integer> nodeLevel = new HashMap<>();
  private List<Set<String>> levelList = new ArrayList<>();
  protected int nodeMoveCount = 0;
  private boolean needPositionUpdate = false;
  private static final int levelDistance = -30;
  private static final int nodeDistance = 50;

  @Override
  public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
    // TODO Auto-generated method stub

  }

  @Override
  public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
      Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
    // TODO Auto-generated method stub

  }

  @Override
  public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
    // TODO Auto-generated method stub
  }

  @Override
  public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
      Object newValue) {
    // TODO Auto-generated method stub
  }

  @Override
  public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
    // TODO Auto-generated method stub
  }

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

  @Override
  public void nodeRemoved(String sourceId, long timeId, String nodeId) {
    // never happens in this application
  }

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

  @Override
  public void edgeRemoved(String sourceId, long timeId, String edgeId) {
    // never happens in this application
  }

  @Override
  public void graphCleared(String sourceId, long timeId) {
    clear();
    sendGraphCleared(sourceId, timeId);
  }

  @Override
  public void stepBegins(String sourceId, long timeId, double step) {
    // TODO Auto-generated method stub
  }

  // --------------------------------------------------------------------

  @Override
  public String getLayoutAlgorithmName() {
    return "Hierarchy";
  }

  @Override
  public int getNodeMovedCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getStabilization() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getStabilizationLimit() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Point3 getLowPoint() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Point3 getHiPoint() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSteps() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getLastStepTime() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getQuality() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getForce() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void clear() {
    nodeLevel.clear();
    levelList.clear();
    nodeMoveCount = 0;
    needPositionUpdate = true;
  }

  @Override
  public void setForce(double value) {
  }

  @Override
  public void setStabilizationLimit(double value) {
    // non iterative, so not necessary
  }

  @Override
  public void setQuality(double qualityLevel) {
    // always the best quality :)
  }

  @Override
  public void setSendNodeInfos(boolean send) {
    // not needed in this application
  }

  @Override
  public void shake() {
    // not needed in this application
  }

  @Override
  public void moveNode(String id, double x, double y, double z) {
    // not needed in this application
  }

  @Override
  public void freezeNode(String id, boolean frozen) {
    // not needed in this application
  }

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
