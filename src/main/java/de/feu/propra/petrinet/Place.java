package de.feu.propra.petrinet;

/**
 * A {@code Place} is a node of a {@code PetriNet} that holds on to a
 * non-negative number of tokens. It may be connected to {@code Transition}s via
 * {@code Arc}s. It has also an initial token count, to which the current token
 * count can be resetted to.
 * 
 * @author j-hap 
 *
 */
public class Place extends SimplePetriNode {
  private int nTokens = 0;
  private int nInitialTokens = 0;

  /**
   * Constructor for a {@code Place}.
   * 
   * @param id ID of the constructed {@code Place}
   */
  public Place(String id) {
    super(id, NodeType.PLACE);
  }

  /**
   * Removes a token. Throws an exeption instead, if the token count would become
   * negative. Any listener is notified of this change.
   * 
   * @throws IllegalStateException If the number of tokens would become negative
   *                               if another token was removed.
   */
  void removeToken() {
    if (nTokens == 0) {
      throw new IllegalStateException();
    }
    tokensChanged(nTokens, --nTokens);
  }

  /**
   * Adds a token. Any listener if notified of this change.
   */
  void addToken() {
    var old = nTokens;
    // to detect integer overflow
    nTokens = Math.addExact(nTokens, 1);
    tokensChanged(old, nTokens);
  }

  /**
   * Overrides the initial token count with the current token count.
   */
  void setCurrentTokensAsInitial() {
    nInitialTokens = nTokens;
  }

  /**
   * @return True it the current number of tokens is positive. False otherwise.
   */
  public boolean hasTokens() {
    return nTokens > 0;
  }

  /**
   * @return The number of tokens the {@code Place} currently holds.
   */
  public int getTokenCount() {
    return nTokens;
  }

  /**
   * Sets the number of tokens the {@code Place} currently holds.
   * 
   * @param n The new token count.
   */
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
   * @param tokenCount The new token count.
   * @throws IllegalStateException when given token count is negative.
   */
  void setInitialTokenCount(int tokenCount) {
    if (tokenCount < 0) {
      throw new IllegalStateException();
    }
    var old = nInitialTokens;
    nTokens = tokenCount;
    nInitialTokens = tokenCount;
    tokensChanged(old, tokenCount);
  }

  /**
   * Resets the current token count to the initial token count.
   */
  public void reset() {
    tokensChanged(nTokens, nTokens = nInitialTokens);
  }

  /**
   * {@inheritDoc} Appends the current token count in &lt;&gt;.
   */
  @Override
  public String getLabel() {
    return super.getLabel() + " <" + String.valueOf(nTokens) + ">";
  }
}
