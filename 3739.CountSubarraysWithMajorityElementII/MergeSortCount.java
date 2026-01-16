public class MergeSortCount {
  public long countSubarrays(int[] nums, int target) {
    int n = nums.length;

    long[] pref = new long[n + 1];
    for (int i = 1; i <= n; i++) {
      pref[i] = pref[i - 1] + (nums[i - 1] == target ? 1 : -1);
    }

    long[] tmp = new long[n + 1];
    return sortAndCount(pref, tmp, 0, n);
  }

  // sorts pref[l..r] and returns #pairs (i<j) with pref[i] < pref[j] within that
  // segment
  private long sortAndCount(long[] a, long[] tmp, int l, int r) {
    if (l >= r)
      return 0;
    int mid = (l + r) >>> 1;

    long ans = 0;
    ans += sortAndCount(a, tmp, l, mid);
    ans += sortAndCount(a, tmp, mid + 1, r);

    // a[l..mid] and a[mid+1..r] are sorted now.
    // Count cross pairs: i in left, j in right, with a[i] < a[j].
    int i = l;
    for (int j = mid + 1; j <= r; j++) {
      while (i <= mid && a[i] < a[j])
        i++;
      // i is first index in left where a[i] >= a[j]
      // so count of left elements < a[j] is (i - l)
      ans += (i - l);
    }

    // merge
    int p = l, q = mid + 1, t = l;
    while (p <= mid && q <= r) {
      if (a[p] <= a[q])
        tmp[t++] = a[p++];
      else
        tmp[t++] = a[q++];
    }
    while (p <= mid)
      tmp[t++] = a[p++];
    while (q <= r)
      tmp[t++] = a[q++];

    for (int k = l; k <= r; k++)
      a[k] = tmp[k];
    return ans;
  }
}
