class SegmentTree {

  static class SegTree {
    final int n;
    final int[] mn, mx, lazy;

    SegTree(int n) {
      this.n = n;
      mn = new int[4 * n];
      mx = new int[4 * n];
      lazy = new int[4 * n];
      // D(l)=0 initially for all l => mn/mx default 0
    }

    private void apply(int node, int delta) {
      mn[node] += delta;
      mx[node] += delta;
      lazy[node] += delta;
    }

    private void push(int node) {
      int lz = lazy[node];
      if (lz != 0) {
        apply(node * 2, lz);
        apply(node * 2 + 1, lz);
        lazy[node] = 0;
      }
    }

    private void pull(int node) {
      mn[node] = Math.min(mn[node * 2], mn[node * 2 + 1]);
      mx[node] = Math.max(mx[node * 2], mx[node * 2 + 1]);
    }

    void rangeAdd(int l, int r, int delta) {
      if (l > r)
        return;
      rangeAdd(1, 0, n - 1, l, r, delta);
    }

    private void rangeAdd(int node, int tl, int tr, int l, int r, int delta) {
      if (l > tr || r < tl)
        return;
      if (l <= tl && tr <= r) {
        apply(node, delta);
        return;
      }
      push(node);
      int tm = (tl + tr) >>> 1;
      rangeAdd(node * 2, tl, tm, l, r, delta);
      rangeAdd(node * 2 + 1, tm + 1, tr, l, r, delta);
      pull(node);
    }

    // Return smallest i in [ql..qr] s.t. D(i) == 0, else -1.
    int findFirstZero(int ql, int qr) {
      if (ql > qr)
        return -1;
      return findFirstZero(1, 0, n - 1, ql, qr);
    }

    private int findFirstZero(int node, int tl, int tr, int ql, int qr) {
      if (ql > tr || qr < tl)
        return -1;

      // If segment cannot contain 0, prune.
      if (mn[node] > 0 || mx[node] < 0)
        return -1;

      if (tl == tr)
        return (mn[node] == 0) ? tl : -1;

      push(node);
      int tm = (tl + tr) >>> 1;

      int left = findFirstZero(node * 2, tl, tm, ql, qr);
      if (left != -1)
        return left;

      return findFirstZero(node * 2 + 1, tm + 1, tr, ql, qr);
    }
  }

  public int longestBalancedSubarray(int[] nums) {
    int n = nums.length;
    if (n == 0)
      return 0;

    // Map values -> id for last occurrence storage
    HashMap<Integer, Integer> idMap = new HashMap<>(n * 2);
    int m = 0;
    for (int v : nums) {
      if (!idMap.containsKey(v))
        idMap.put(v, m++);
    }

    int[] last = new int[m];
    Arrays.fill(last, -1);

    SegTree st = new SegTree(n);
    int ans = 0;

    for (int r = 0; r < n; r++) {
      int v = nums[r];
      int id = idMap.get(v);
      int p = last[id];

      int delta = ((v & 1) == 0) ? +1 : -1;
      // For all starts l in [p+1..r], v becomes newly distinct in [l..r]
      st.rangeAdd(p + 1, r, delta);

      last[id] = r;

      int l = st.findFirstZero(0, r);
      if (l != -1)
        ans = Math.max(ans, r - l + 1);
    }

    return ans;
  }
}
