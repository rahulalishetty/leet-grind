class GreedyEndingTime {
  public int findLongestChain(int[][] pairs) {
    // Sort by end (second element)
    Arrays.sort(pairs, (a, b) -> Integer.compare(a[1], b[1]));

    int count = 0;
    int currentEnd = Integer.MIN_VALUE;

    for (int[] p : pairs) {
      if (p[0] > currentEnd) {
        // Can extend chain
        count++;
        currentEnd = p[1];
      }
    }

    return count;
  }
}
