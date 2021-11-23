package de.feu.propra.petrinet;

public class ArcPlan {
  public final String id;
  public final String sourceId;
  public final String targetId;

  public ArcPlan(String id, String sourceId, String targetId) {
    this.id = id;
    this.sourceId = sourceId;
    this.targetId = targetId;
  }
}
