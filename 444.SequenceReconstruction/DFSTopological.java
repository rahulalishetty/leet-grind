class DFSTopological {
  public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
    int n = nums.length;

    List<Set<Integer>> g = new ArrayList<>();
    for (int i = 0; i <= n; i++)
      g.add(new HashSet<>());

    boolean[] seen = new boolean[n + 1];

    for (List<Integer> seq : sequences) {
      for (int x : seq) {
        if (x < 1 || x > n)
          return false;
        seen[x] = true;
      }
      for (int i = 0; i + 1 < seq.size(); i++) {
        int u = seq.get(i), v = seq.get(i + 1);
        g.get(u).add(v);
      }
    }

    for (int x : nums) {
      if (!seen[x])
        return false;
    }

    // 0 = unvisited, 1 = visiting, 2 = done
    int[] state = new int[n + 1];
    ArrayList<Integer> order = new ArrayList<>(n);

    for (int i = 1; i <= n; i++) {
      if (state[i] == 0) {
        if (!dfs(i, g, state, order))
          return false; // cycle
      }
    }

    Collections.reverse(order);

    // Must match nums exactly
    if (order.size() != n)
      return false;
    for (int i = 0; i < n; i++) {
      if (order.get(i) != nums[i])
        return false;
    }

    // Uniqueness: every consecutive pair must have an edge
    for (int i = 0; i + 1 < n; i++) {
      int u = order.get(i), v = order.get(i + 1);
      if (!g.get(u).contains(v))
        return false;
    }

    return true;
  }

  private boolean dfs(int u, List<Set<Integer>> g, int[] state, List<Integer> order) {
    state[u] = 1; // visiting
    for (int v : g.get(u)) {
      if (state[v] == 1)
        return false; // cycle
      if (state[v] == 0 && !dfs(v, g, state, order))
        return false;
    }
    state[u] = 2; // done
    order.add(u);
    return true;
  }
}
