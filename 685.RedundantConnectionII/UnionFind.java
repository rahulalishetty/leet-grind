import java.util.*;

class Solution {
  static class DSU {
    int[] parent, size;

    DSU(int n) {
      parent = new int[n + 1];
      size = new int[n + 1];
      for (int i = 1; i <= n; i++) {
        parent[i] = i;
        size[i] = 1;
      }
    }

    int find(int x) {
      if (parent[x] == x)
        return x;
      parent[x] = find(parent[x]);
      return parent[x];
    }

    // returns false if union would create a cycle (same set)
    boolean union(int a, int b) {
      int ra = find(a), rb = find(b);
      if (ra == rb)
        return false;
      if (size[ra] < size[rb]) {
        parent[ra] = rb;
        size[rb] += size[ra];
      } else {
        parent[rb] = ra;
        size[ra] += size[rb];
      }
      return true;
    }
  }

  public int[] findRedundantDirectedConnection(int[][] edges) {
    int n = edges.length;

    int[] parent = new int[n + 1]; // parent[v] = u
    Arrays.fill(parent, 0);

    int[] cand1 = null; // earlier edge to a node with 2 parents
    int[] cand2 = null; // later edge to a node with 2 parents

    // Step 1: detect a node with two parents
    for (int[] e : edges) {
      int u = e[0], v = e[1];
      if (parent[v] == 0) {
        parent[v] = u;
      } else {
        // v already has a parent: two-parent case
        cand1 = new int[] { parent[v], v };
        cand2 = new int[] { u, v };
        // mark cand2 as "to potentially skip" later
      }
    }

    // Step 2: DSU to detect cycle (optionally skipping cand2)
    DSU dsu = new DSU(n);

    for (int[] e : edges) {
      int u = e[0], v = e[1];

      // if two-parent situation exists, skip the later edge cand2 first
      if (cand2 != null && u == cand2[0] && v == cand2[1]) {
        continue;
      }

      // if union fails, we found a cycle
      if (!dsu.union(u, v)) {
        // If no two-parent issue, this edge is the answer
        if (cand1 == null)
          return e;

        // If two-parent issue exists, cycle means cand1 must be removed
        return cand1;
      }
    }

    // If we got here, no cycle when skipping cand2 => remove cand2
    return cand2;
  }
}
