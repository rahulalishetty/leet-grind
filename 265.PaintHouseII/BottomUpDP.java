class BottomUpDP {
  public int minCostII(int[][] costs) {

    if (costs.length == 0)
      return 0;
    int k = costs[0].length;
    int n = costs.length;

    for (int house = 1; house < n; house++) {
      for (int color = 0; color < k; color++) {
        int min = Integer.MAX_VALUE;
        for (int previousColor = 0; previousColor < k; previousColor++) {
          if (color == previousColor)
            continue;
          min = Math.min(min, costs[house - 1][previousColor]);
        }
        costs[house][color] += min;
      }
    }

    // Find the minimum in the last row.
    int min = Integer.MAX_VALUE;
    for (int c : costs[n - 1]) {
      min = Math.min(min, c);
    }
    return min;
  }
}
