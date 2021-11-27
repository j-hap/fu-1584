package de.feu.propra.petrinet;

/**
 * POD class to temporarily store information about an {@code Arc} defined in a
 * PNML file.
 * 
 * @author j-hap 
 *
 */
public class ArcPlan {
  public final String id;
  public final String sourceId;
  public final String targetId;

  /**
   * @param id       Unique ID of the {@code Arc} to create.
   * @param sourceId Unique ID of the source {@code PetriNode}.
   * @param targetId Unique ID of the target {@code PetriNode}.
   */
  public ArcPlan(String id, String sourceId, String targetId) {
    this.id = id;
    this.sourceId = sourceId;
    this.targetId = targetId;
  }
}
