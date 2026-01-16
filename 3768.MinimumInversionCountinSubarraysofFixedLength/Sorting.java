import java.util.*;

class Solution {
  public long minInversionCount(int[] nums, int k) {
    int n = nums.length;
    if (k <= 1)
      return 0;
    long ans = Long.MAX_VALUE;

    for (int l = 0; l + k <= n; l++) {
      int[] win = new int[k];
      for (int i = 0; i < k; i++)
        win[i] = nums[l + i];
      long inv = countInv(win);
      ans = Math.min(ans, inv);
    }
    return ans;
  }

  private long countInv(int[] a) {
    int[] tmp = new int[a.length];
    return mergeCount(a, tmp, 0, a.length - 1);
  }

  private long mergeCount(int[] a, int[] tmp, int lo, int hi) {
    if (lo >= hi)
      return 0;
    int mid = (lo + hi) >>> 1;
    long inv = 0;
    inv += mergeCount(a, tmp, lo, mid);
    inv += mergeCount(a, tmp, mid + 1, hi);

    int i = lo, j = mid + 1, t = lo;
    while (i <= mid && j <= hi) {
      if (a[i] <= a[j])
        tmp[t++] = a[i++];
      else {
        tmp[t++] = a[j++];
        inv += (mid - i + 1); // all remaining left elements invert with a[j-1]
      }
    }
    while (i <= mid)
      tmp[t++] = a[i++];
    while (j <= hi)
      tmp[t++] = a[j++];
    for (int p = lo; p <= hi; p++)
      a[p] = tmp[p];
    return inv;
  }
}
