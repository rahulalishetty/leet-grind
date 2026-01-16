public class Solution {
  static class Fenwick {
    long[] bit;
    int n;

    Fenwick(int n) {
      this.n = n;
      this.bit = new long[n + 1];
    }

    void add(int idx, long delta) {
      for (; idx <= n; idx += idx & -idx)
        bit[idx] += delta;
    }

    long sum(int idx) {
      long res = 0;
      for (; idx > 0; idx -= idx & -idx)
        res += bit[idx];
      return res;
    }

    long rangeSum(int l, int r) {
      if (r < l)
        return 0;
      return sum(r) - sum(l - 1);
    }
  }

  public long minInversionCount(int[] nums, int k) {
    int n = nums.length;
    if (k <= 1)
      return 0;

    // Coordinate compression
    int[] sorted = nums.clone();
    Arrays.sort(sorted);
    int m = 0;
    for (int i = 0; i < n; i++) {
      if (i == 0 || sorted[i] != sorted[i - 1])
        sorted[m++] = sorted[i];
    }

    int[] rk = new int[n];
    for (int i = 0; i < n; i++) {
      int pos = Arrays.binarySearch(sorted, 0, m, nums[i]);
      rk[i] = pos + 1; // 1-based ranks for Fenwick
    }

    Fenwick ft = new Fenwick(m);

    long inv = 0;
    // Build first window [0..k-1]
    for (int i = 0; i < k; i++) {
      int x = rk[i];
      long le = ft.sum(x); // <= x
      long sizeSoFar = i; // already inserted count
      long greater = sizeSoFar - le; // > x
      inv += greater;
      ft.add(x, 1);
    }

    long ans = inv;

    // Slide windows
    for (int l = 0; l + k < n; l++) {
      int y = rk[l]; // outgoing
      int x = rk[l + k]; // incoming

      // Remove y (leftmost): subtract pairs (y, z) where z < y
      long less = ft.sum(y - 1);
      inv -= less;
      ft.add(y, -1);

      // Add x (rightmost): add pairs (z, x) where z > x
      long curSize = k - 1; // after removal
      long le = ft.sum(x); // <= x
      long greater = curSize - le; // > x
      inv += greater;
      ft.add(x, 1);

      ans = Math.min(ans, inv);
    }

    return ans;
  }
}
