class TopologicalSort {
  public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
    int n = nums.length;

    // adjacency as Set to avoid duplicate edges
    List<Set<Integer>> g = new ArrayList<>();
    for (int i = 0; i <= n; i++)
      g.add(new HashSet<>());

    int[] indeg = new int[n + 1];
    boolean[] seen = new boolean[n + 1];

    // Build graph from consecutive pairs
    for (List<Integer> seq : sequences) {
      for (int x : seq) {
        seen[x] = true;
      }
      for (int i = 0; i + 1 < seq.size(); i++) {
        int u = seq.get(i), v = seq.get(i + 1);
        if (g.get(u).add(v)) { // only if new edge
          indeg[v]++;
        }
      }
    }

    // If some number in nums never appears in sequences, you cannot uniquely
    // reconstruct
    for (int x : nums) {
      if (!seen[x])
        return false;
    }

    // Start with all indegree-0 nodes
    Deque<Integer> q = new ArrayDeque<>();
    for (int i = 1; i <= n; i++) {
      if (indeg[i] == 0)
        q.add(i);
    }

    int idx = 0;
    while (!q.isEmpty()) {
      // uniqueness check
      if (q.size() != 1)
        return false;

      int cur = q.poll();
      if (cur != nums[idx])
        return false;
      idx++;

      for (int nei : g.get(cur)) {
        if (--indeg[nei] == 0)
          q.add(nei);
      }
    }

    return idx == n;
  }
}
