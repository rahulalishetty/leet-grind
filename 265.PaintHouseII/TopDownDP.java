class TopDownDp {

  private int n;
  private int k;
  private int[][] costs;
  private Map<String, Integer> memo;

  public int minCostII(int[][] costs) {
    if (costs.length == 0)
      return 0;
    this.k = costs[0].length;
    this.n = costs.length;
    this.costs = costs;
    this.memo = new HashMap<>();
    int minCost = Integer.MAX_VALUE;
    for (int color = 0; color < k; color++) {
      minCost = Math.min(minCost, memoSolve(0, color));
    }
    return minCost;
  }

  private int memoSolve(int houseNumber, int color) {

    // Base case: There are no more houses after this one.
    if (houseNumber == n - 1) {
      return costs[houseNumber][color];
    }

    // Memoization lookup case: Have we already solved this subproblem?
    if (memo.containsKey(getKey(houseNumber, color))) {
      return memo.get(getKey(houseNumber, color));
    }

    // Recursive case: Determine the minimum cost for the remainder.
    int minRemainingCost = Integer.MAX_VALUE;
    for (int nextColor = 0; nextColor < k; nextColor++) {
      if (color == nextColor)
        continue;
      int currentRemainingCost = memoSolve(houseNumber + 1, nextColor);
      minRemainingCost = Math.min(currentRemainingCost, minRemainingCost);
    }
    int totalCost = costs[houseNumber][color] + minRemainingCost;
    memo.put(getKey(houseNumber, color), totalCost);
    return totalCost;
  }

  // Convert a house number and color into a simple string key for the memo.
  private String getKey(int n, int color) {
    return String.valueOf(n) + " " + String.valueOf(color);
  }
}
