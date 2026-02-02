import java.util.*;

class SolutionRMQBinarySearch {
  public long countValidSubarrays(int[] nums) {
    int n = nums.length;
    SparseMin st = new SparseMin(nums);
    long ans = 0;

    for (int l = 0; l < n; l++) {
      int base = nums[l];

      int lo = l, hi = n - 1, firstBad = n;
      while (lo <= hi) {
        int mid = (lo + hi) >>> 1;
        int mn = st.queryMin(l, mid);
        if (mn < base) {
          firstBad = mid;
          hi = mid - 1;
        } else {
          lo = mid + 1;
        }
      }
      // valid r are l..firstBad-1
      ans += (long) (firstBad - l);
    }
    return ans;
  }

  // Sparse table for range minimum query: O(1) query, O(n log n) build
  static final class SparseMin {
    private final int[][] st;
    private final int[] log2;

    SparseMin(int[] a) {
      int n = a.length;
      log2 = new int[n + 1];
      for (int i = 2; i <= n; i++)
        log2[i] = log2[i / 2] + 1;

      int K = log2[n] + 1;
      st = new int[K][n];
      System.arraycopy(a, 0, st[0], 0, n);

      for (int k = 1; k < K; k++) {
        int len = 1 << k;
        int half = len >>> 1;
        for (int i = 0; i + len <= n; i++) {
          st[k][i] = Math.min(st[k - 1][i], st[k - 1][i + half]);
        }
      }
    }

    int queryMin(int l, int r) {
      int len = r - l + 1;
      int k = log2[len];
      int rightStart = r - (1 << k) + 1;
      return Math.min(st[k][l], st[k][rightStart]);
    }
  }
}
