import java.util.*;

class Solution {
  private List<List<Integer>> adj;

  private int farthestNode;
  private int farthestDist;

  private void dfsFarthest(int node, int parent, int dist, int[] parentArr) {
    if (dist > farthestDist) {
      farthestDist = dist;
      farthestNode = node;
    }

    if (parentArr != null)
      parentArr[node] = parent;

    for (int nei : adj.get(node)) {
      if (nei == parent)
        continue;
      dfsFarthest(nei, node, dist + 1, parentArr);
    }
  }

  public List<Integer> findMinHeightTrees(int n, int[][] edges) {
    if (n == 1)
      return Arrays.asList(0);

    adj = new ArrayList<>(n);
    for (int i = 0; i < n; i++)
      adj.add(new ArrayList<>());

    for (int[] e : edges) {
      int u = e[0], v = e[1];
      adj.get(u).add(v);
      adj.get(v).add(u);
    }

    // 1) DFS from 0 to find one endpoint A
    farthestDist = -1;
    dfsFarthest(0, -1, 0, null);
    int A = farthestNode;

    // 2) DFS from A to find the other endpoint B; also store parents to rebuild
    // path
    int[] parent = new int[n];
    Arrays.fill(parent, -1);

    farthestDist = -1;
    dfsFarthest(A, -1, 0, parent);
    int B = farthestNode;

    // 3) Reconstruct diameter path from B back to A using parent[]
    List<Integer> path = new ArrayList<>();
    int cur = B;
    while (cur != -1) {
      path.add(cur);
      if (cur == A)
        break;
      cur = parent[cur];
    }
    // path is B -> ... -> A (reverse direction is fine for picking centers)

    // 4) Pick center(s)
    int m = path.size();
    if (m % 2 == 1) {
      return Arrays.asList(path.get(m / 2));
    } else {
      return Arrays.asList(path.get(m / 2 - 1), path.get(m / 2));
    }
  }
}
