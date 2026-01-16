class GreedyStartingTime {
  public int findLongestChain(int[][] pairs) {
    // Sort by start (first element)
    Arrays.sort(pairs, (a, b) -> Integer.compare(a[0], b[0]));

    int n = pairs.length;
    if (n == 0)
      return 0;

    int count = 1;
    int[] prev = pairs[0];

    for (int i = 1; i < n; i++) {
      int[] cur = pairs[i];

      if (cur[0] > prev[1]) {
        // No conflict: we can safely add this pair to the chain
        count++;
        prev = cur;
      } else {
        // Conflict: choose the pair that ends earlier
        if (cur[1] < prev[1]) {
          prev = cur;
        }
      }
    }

    return count;
  }
}
