package de.feu.propra.petrinet;

public class Place extends SimplePetriNode {
  private int nTokens = 0;
  private int nInitialTokens = 0;

  public Place(String id) {
    super(id, NodeType.PLACE);
  }

  // package private
  void removeToken() throws IllegalStateException {
    if (nTokens == 0) {
      throw new IllegalStateException("Token count must not be negative!");
    }
    tokensChanged(nTokens, --nTokens);
  }

  // package private
  void addToken() {
    var old = nTokens;
    // to detect integer overflow
    nTokens = Math.addExact(nTokens, 1);
    tokensChanged(old, nTokens);
  }

  // package private
  void setCurrentTokensAsInitial() {
    nInitialTokens = nTokens;
  }

  public boolean hasTokens() {
    return nTokens > 0;
  }

  public int getTokenCount() {
    return nTokens;
  }

  // package private
  void setTokenCount(int n) {
    tokensChanged(nTokens, nTokens = n);
  }

  private void tokensChanged(int oldCount, int newCount) {
    if (oldCount != newCount) {
      // if old and new are equal is also checken in the pcs, but
      // its more trouble to get the old label than just check outselfs
      pcs.firePropertyChange("TokenCount", null, newCount);
      pcs.firePropertyChange("Label", null, getLabel());
    }
  }

  /**
   * Sets the initial token count to given number. Also resets the current token
   * count to the new initial token count.
   * 
   * @param n the new token count
   */
  void setInitialTokenCount(int n) {
    var old = nInitialTokens;
    nTokens = n;
    nInitialTokens = n;
    tokensChanged(old, n);
  }

  /**
   * Resets the current token count to the initial token count.
   */
  public void reset() {
    tokensChanged(nTokens, nTokens = nInitialTokens);
  }

  @Override
  public String getLabel() {
    return super.getLabel() + " <" + String.valueOf(nTokens) + ">";
  }
}
