class SegmentTree {

  private final int[] arr;
  private final int n;

  // Segment tree arrays: majority candidate value and its "net count"
  // (Boyer–Moore style)
  private final int[] segVal;
  private final int[] segCnt;

  // Value -> sorted positions (for fast counting in [L, R])
  private final Map<Integer, int[]> posMap;

  public MajorityChecker(int[] arr) {
    this.arr = arr;
    this.n = arr.length;

    // 1) Build positions map
    Map<Integer, List<Integer>> tmp = new HashMap<>();
    for (int i = 0; i < n; i++) {
      tmp.computeIfAbsent(arr[i], k -> new ArrayList<>()).add(i);
    }
    posMap = new HashMap<>();
    for (Map.Entry<Integer, List<Integer>> e : tmp.entrySet()) {
      List<Integer> list = e.getValue();
      int[] a = new int[list.size()];
      for (int i = 0; i < a.length; i++)
        a[i] = list.get(i);
      posMap.put(e.getKey(), a);
    }

    // 2) Build segment tree of Boyer–Moore candidates
    segVal = new int[4 * n];
    segCnt = new int[4 * n];
    if (n > 0)
      build(1, 0, n - 1);
  }

  public int query(int left, int right, int threshold) {
    if (n == 0)
      return -1;

    // A) Get candidate on [left, right]
    Pair p = queryRange(1, 0, n - 1, left, right);
    int cand = p.val;

    // B) Verify real count via binary search over positions
    int count = countInRange(cand, left, right);
    return count >= threshold ? cand : -1;
  }

  // ----- Segment tree build/query -----

  private void build(int idx, int l, int r) {
    if (l == r) {
      segVal[idx] = arr[l];
      segCnt[idx] = 1;
      return;
    }
    int mid = (l + r) >>> 1;
    build(idx << 1, l, mid);
    build(idx << 1 | 1, mid + 1, r);
    pull(idx);
  }

  private void pull(int idx) {
    int lv = segVal[idx << 1], lc = segCnt[idx << 1];
    int rv = segVal[idx << 1 | 1], rc = segCnt[idx << 1 | 1];
    Pair merged = merge(lv, lc, rv, rc);
    segVal[idx] = merged.val;
    segCnt[idx] = merged.cnt;
  }

  private Pair queryRange(int idx, int l, int r, int ql, int qr) {
    if (qr < l || r < ql)
      return new Pair(0, 0); // neutral
    if (ql <= l && r <= qr)
      return new Pair(segVal[idx], segCnt[idx]);
    int mid = (l + r) >>> 1;
    Pair L = queryRange(idx << 1, l, mid, ql, qr);
    Pair R = queryRange(idx << 1 | 1, mid + 1, r, ql, qr);
    return merge(L.val, L.cnt, R.val, R.cnt);
  }

  // Boyer–Moore merge of two segments
  private Pair merge(int v1, int c1, int v2, int c2) {
    if (c1 == 0)
      return new Pair(v2, c2);
    if (c2 == 0)
      return new Pair(v1, c1);
    if (v1 == v2)
      return new Pair(v1, c1 + c2);
    if (c1 > c2)
      return new Pair(v1, c1 - c2);
    return new Pair(v2, c2 - c1);
  }

  // ----- Count occurrences of value in [L, R] via positions -----

  private int countInRange(int value, int L, int R) {
    int[] pos = posMap.get(value);
    if (pos == null)
      return 0;
    int leftIdx = lowerBound(pos, L);
    int rightIdx = upperBound(pos, R);
    return rightIdx - leftIdx;
  }

  private int lowerBound(int[] a, int x) {
    int lo = 0, hi = a.length; // first idx >= x
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      if (a[mid] >= x)
        hi = mid;
      else
        lo = mid + 1;
    }
    return lo;
  }

  private int upperBound(int[] a, int x) {
    int lo = 0, hi = a.length; // first idx > x
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      if (a[mid] > x)
        hi = mid;
      else
        lo = mid + 1;
    }
    return lo;
  }

  private static class Pair {
    final int val;
    final int cnt;

    Pair(int v, int c) {
      val = v;
      cnt = c;
    }
  }
}

/**
 * Your MajorityChecker object will be instantiated and called as such:
 * MajorityChecker obj = new MajorityChecker(arr);
 * int param_1 = obj.query(left,right,threshold);
 */
