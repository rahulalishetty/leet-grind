class AdjacentPairing {
  public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
    int n = nums.length;
    int[] pos = new int[n + 1];

    for (int i = 0; i < n; i++)
      pos[nums[i]] = i;

    boolean[] seen = new boolean[n + 1];
    boolean[] adjConfirmed = new boolean[n]; // adjConfirmed[i] means nums[i] -> nums[i+1] confirmed
    int confirmedCount = 0;

    for (List<Integer> seq : sequences) {
      for (int x : seq) {
        if (x < 1 || x > n)
          return false;
        seen[x] = true;
      }

      for (int i = 0; i + 1 < seq.size(); i++) {
        int u = seq.get(i), v = seq.get(i + 1);
        if (pos[u] >= pos[v])
          return false; // contradicts nums ordering

        // If this edge matches an adjacent pair in nums, mark it
        if (pos[u] + 1 == pos[v] && !adjConfirmed[pos[u]]) {
          adjConfirmed[pos[u]] = true;
          confirmedCount++;
        }
      }
    }

    for (int x : nums) {
      if (!seen[x])
        return false;
    }

    // Must confirm all n-1 adjacent relations
    return confirmedCount == n - 1;
  }
}
