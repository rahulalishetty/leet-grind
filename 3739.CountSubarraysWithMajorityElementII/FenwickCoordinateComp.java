public class FenwickCoordinateComp {
  static final class Fenwick {
    private final long[] bit;

    Fenwick(int n) {
      bit = new long[n + 1];
    }

    void add(int i, long delta) {
      for (int n = bit.length - 1; i <= n; i += i & -i)
        bit[i] += delta;
    }

    long sum(int i) {
      long s = 0;
      for (; i > 0; i -= i & -i)
        s += bit[i];
      return s;
    }
  }

  public long countSubarrays(int[] nums, int target) {
    int n = nums.length;

    int[] pref = new int[n + 1];
    for (int i = 1; i <= n; i++) {
      pref[i] = pref[i - 1] + (nums[i - 1] == target ? 1 : -1);
    }

    int[] sorted = pref.clone();
    Arrays.sort(sorted);

    int m = 0;
    for (int i = 0; i < sorted.length; i++) {
      if (i == 0 || sorted[i] != sorted[i - 1])
        sorted[m++] = sorted[i];
    }

    Fenwick fw = new Fenwick(m);
    long ans = 0;

    for (int x : pref) {
      int r = rank(sorted, m, x); // 1..m
      ans += fw.sum(r - 1); // strictly less
      fw.add(r, 1);
    }

    return ans;
  }

  private int rank(int[] uniqSorted, int m, int x) {
    int idx = Arrays.binarySearch(uniqSorted, 0, m, x);
    return idx + 1; // 1-based for Fenwick
  }
}
