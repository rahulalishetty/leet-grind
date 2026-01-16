class Propagation {
  public List<Integer> fallingSquares(int[][] positions) {
    int n = positions.length;
    int[] h = new int[n];
    List<Integer> ans = new ArrayList<>(n);
    int runningMax = 0;

    for (int i = 0; i < n; i++) {
      int L = positions[i][0], S = positions[i][1], R = L + S;
      int base = 0;
      for (int k = 0; k < i; k++) { // check overlaps with prior
        int L2 = positions[k][0], R2 = L2 + positions[k][1];
        if (L < R2 && L2 < R)
          base = Math.max(base, h[k]);
      }
      h[i] = base + S; // final height for square i
      runningMax = Math.max(runningMax, h[i]); // tallest so far
      ans.add(runningMax);
    }
    return ans;
  }
}
