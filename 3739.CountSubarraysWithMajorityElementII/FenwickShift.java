public class FenwickShift {
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

    int offset = n + 2;
    int size = 2 * n + 5;

    Fenwick fw = new Fenwick(size);

    long ans = 0;
    int pref = 0;

    // insert pref[0] = 0
    fw.add(pref + offset, 1);

    for (int x : nums) {
      pref += (x == target ? 1 : -1);

      int idx = pref + offset;

      // count of previous pref values < current pref
      ans += fw.sum(idx - 1);

      // insert current pref
      fw.add(idx, 1);
    }

    return ans;
  }
}
