class BinarySearchTree {
  private int depthFirstSearch(int n, int k, int val) {
    if (n == 1)
      return val;

    int totalNodes = (int) Math.pow(2, n - 1);

    if (k > totalNodes / 2) {
      int nextVal = val ^ 1;
      return depthFirstSearch(n - 1, k - (totalNodes / 2), nextVal);
    }

    return depthFirstSearch(n - 1, k, val);
  }

  public int kthGrammar(int n, int k) {
    return depthFirstSearch(n, k, 0);
  }
}
