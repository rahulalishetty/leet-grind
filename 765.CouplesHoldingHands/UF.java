class Solution {
  public int minSwapsCouples(int[] row) {
    int N = row.length / 2;
    DSU dsu = new DSU(N);

    for (int i = 0; i < N; i++) {
      int a = row[2 * i] / 2;
      int b = row[2 * i + 1] / 2;
      dsu.union(a, b);
    }

    // If a component has size k, it needs k-1 swaps.
    int ans = 0;
    for (int root = 0; root < N; root++) {
      if (dsu.find(root) == root) {
        ans += dsu.size[root] - 1;
      }
    }
    return ans;
  }

  static class DSU {
    int[] parent, size;

    DSU(int n) {
      parent = new int[n];
      size = new int[n];
      for (int i = 0; i < n; i++) {
        parent[i] = i;
        size[i] = 1;
      }
    }

    int find(int x) {
      if (parent[x] != x)
        parent[x] = find(parent[x]);
      return parent[x];
    }

    void union(int a, int b) {
      a = find(a);
      b = find(b);
      if (a == b)
        return;
      if (size[a] < size[b]) {
        int t = a;
        a = b;
        b = t;
      }
      parent[b] = a;
      size[a] += size[b];
    }
  }
}
